/*
Copyright 2016, 2017 Institut National de la Recherche Agronomique

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

        http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/


package fr.inra.maiage.bibliome.alvisnlp.core.app.cli;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.ServiceLoader;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.xpath.XPathExpressionException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import fr.inra.maiage.bibliome.alvisnlp.core.converters.CompoundParamConverterFactory;
import fr.inra.maiage.bibliome.alvisnlp.core.converters.ConverterException;
import fr.inra.maiage.bibliome.alvisnlp.core.converters.ParamConverter;
import fr.inra.maiage.bibliome.alvisnlp.core.converters.ParamConverterFactory;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.FunctionLibrary;
import fr.inra.maiage.bibliome.alvisnlp.core.documentation.Documentation;
import fr.inra.maiage.bibliome.alvisnlp.core.factory.ModuleFactory;
import fr.inra.maiage.bibliome.alvisnlp.core.module.Annotable;
import fr.inra.maiage.bibliome.alvisnlp.core.module.CheckMandatoryParameters;
import fr.inra.maiage.bibliome.alvisnlp.core.module.CheckParamValueConstraints;
import fr.inra.maiage.bibliome.alvisnlp.core.module.CheckUniquePaths;
import fr.inra.maiage.bibliome.alvisnlp.core.module.CollectModules;
import fr.inra.maiage.bibliome.alvisnlp.core.module.Module;
import fr.inra.maiage.bibliome.alvisnlp.core.module.ModuleException;
import fr.inra.maiage.bibliome.alvisnlp.core.module.ParamHandler;
import fr.inra.maiage.bibliome.alvisnlp.core.module.ParameterException;
import fr.inra.maiage.bibliome.alvisnlp.core.module.Sequence;
import fr.inra.maiage.bibliome.alvisnlp.core.module.TimerCategory;
import fr.inra.maiage.bibliome.alvisnlp.core.module.UnexpectedParameterException;
import fr.inra.maiage.bibliome.alvisnlp.core.plan.PlanException;
import fr.inra.maiage.bibliome.alvisnlp.core.plan.PlanLoader;
import fr.inra.maiage.bibliome.util.Files;
import fr.inra.maiage.bibliome.util.FlushedStreamHandler;
import fr.inra.maiage.bibliome.util.GitInfo;
import fr.inra.maiage.bibliome.util.Pair;
import fr.inra.maiage.bibliome.util.Timer;
import fr.inra.maiage.bibliome.util.clio.CLIOException;
import fr.inra.maiage.bibliome.util.clio.CLIOParser;
import fr.inra.maiage.bibliome.util.clio.CLIOption;
import fr.inra.maiage.bibliome.util.count.Count;
import fr.inra.maiage.bibliome.util.count.CountStats;
import fr.inra.maiage.bibliome.util.count.Stats;
import fr.inra.maiage.bibliome.util.files.OutputFile;
import fr.inra.maiage.bibliome.util.service.AmbiguousAliasException;
import fr.inra.maiage.bibliome.util.service.ServiceException;
import fr.inra.maiage.bibliome.util.service.UnsupportedServiceException;
import fr.inra.maiage.bibliome.util.xml.XMLUtils;

/**
 * Base class for the alvisnlp CLI.
 * @author rbossy
 *
 * @param <A>
 * @param <M>
 * @param <C>
 */
public abstract class AbstractAlvisNLP<A extends Annotable,M extends ModuleFactory<A>,C extends CommandLineProcessingContext<A>> extends CLIOParser {
	private static final String TYPE_ATTRIBUTE_NAME = "type";
	private static final String SHORT_TYPE_ATTRIBUTE_NAME = "short-type";
	
	private final GitInfo gitInfo;
	private final M moduleFactory = getModuleFactory();
	private final ParamConverterFactory converterFactory = getParamConverterFactory();

	/**
	 * Module and other processes timer, the root timer has name "alvisnlp".
	 */
	protected final Timer<TimerCategory> timer = new Timer<TimerCategory>("alvisnlp", TimerCategory.MODULE);
	
	private Level logLevel = Level.FINE;
	private String logPath = null;
	private Map<String,String> logPaths = new LinkedHashMap<String,String>();
	private boolean appendToLog = false;
	private File tmpDir = new File("/tmp");
	private boolean dumps = true;
	private final Map<String,String> dumpModules = new LinkedHashMap<String,String>();
	private File defaultParamValuesFile = null;

	private final List<ModuleParamSetter> params = new ArrayList<ModuleParamSetter>();
	private final List<Pair<String,String>> moreModules = new ArrayList<Pair<String,String>>();

	/**
	 * Name of the file from which to resume the processing.
	 */
	protected File resumeFile = null;
	private Locale locale = Locale.getDefault();
	private ResourceBundle bundle = ResourceBundle.getBundle(DocResourceConstants.RESOURCE, locale);
	private Transformer xmlDocTransformer;
	private String planFile = null;
	private String creatorNameFeature = null;
	protected boolean noProcess = false;
	private boolean writePlan = false;
	protected int exitCode = 0;
	private boolean cleanTmpDir = false;
	private final Map<String,String> customEntities = new LinkedHashMap<String,String>();
	private List<String> inputDirs;
	private List<String> resourceBases;
	private String outputDir;
	private final Map<String,String> baseDirs = new LinkedHashMap<String,String>();
	private boolean noColors = false;
	
	/**
	 * Creates anew CLI instance.
	 * @throws TransformerConfigurationException
	 * @throws IOException 
	 */
	public AbstractAlvisNLP() throws TransformerConfigurationException, IOException {
		super();
		xmlDocTransformer = XMLUtils.transformerFactory.newTransformer();
		gitInfo = new GitInfo("/fr/inra/maiage/bibliome/alvisnlp/core/app/AlvisNLPGit.properties", "https://github.com/Bibliome/alvisnlp.git");
	}

	/**
	 * CLI option: print version.
	 */
	@CLIOption(value="-version", stop=true)
	public final void version() {
        System.out.println(gitInfo.getBuildVersion());
        if (!gitInfo.isCanonicalRemoteOrigin()) {
        	System.out.format("Remote URL: %s\n", gitInfo.getRemoteOriginURL());
        }
        System.out.format("Commit: %s (%s)\n", gitInfo.getCommitId(), gitInfo.getCommitTime());
        if (!gitInfo.isDefaultBranch()) {
        	System.out.format("Branch: %s\n", gitInfo.getBranch());
        }
        if (gitInfo.isDirty()) {
        	System.out.format("Built: %s (%s)\n", gitInfo.getBuildHost(), gitInfo.getBuildTime());
        }
	}
	
	/**
	 * CLI option: be verbose.
	 */
	@CLIOption("-verbose")
	public final void verbose() { 
		logLevel = Level.ALL;
	}

	/**
	 * CLI option: be quiet.
	 */
	@CLIOption("-quiet")
	public final void quiet() { 
		logLevel = Level.INFO;
	}
	
	/**
	 * CLI option: be silent.
	 */
	@CLIOption("-silent")
	public final void silent() { 
		logLevel = Level.WARNING;
	}

	/**
	 * CLI option: set the log file.
	 * @param logPath
	 */
	@CLIOption("-log")
	public final void setLogPath(String logPath) {
		int colon = logPath.indexOf(':');
		if (colon < 0) {
			this.logPath = logPath;
		}
		else {
			String log = logPath.substring(0, colon);
			String path = logPath.substring(colon + 1);
			logPaths.put(log, path);
		}
	}
	
	/**
	 * CLI option: append log to the log file.
	 */
	@CLIOption("-append")
	public final void appendToLogFile() { 
		appendToLog = true;
	}
	
	/**
	 * CLI option: set the tmp dir.
	 * @param tmpDir
	 */
	@CLIOption("-tmp")
	public final void setRootTempDir(File tmpDir) {
		this.tmpDir = tmpDir;
	}
	
	/**
	 * CLI option: disable dumps.
	 */
	@CLIOption("-nodumps")
	public final void noDumps() { 
		dumps = false;
	}
	
	@CLIOption("-unset")
	public final void unsetParam(String module, String param) {
		params.add(new ModuleParamSetter(new ModulePathSelector(module), new ParamUnsetter(param)));
	}
	
	@CLIOption("-param")
	public final void setParam(String module, String param, String value) { 
		params.add(new ModuleParamSetter(new ModulePathSelector(module), new StringParamSetter(param, value)));
	}
	
	@CLIOption("-xparam")
	public final void setXMLParam(String module, String value) { 
		params.add(new ModuleParamSetter(new ModulePathSelector(module), new XMLParamSetter(value)));
	}
	
	@CLIOption("-alias")
	public final void setAlias(String param, String value) { 
		params.add(new ModuleParamSetter(new PlanSelector<A>(), new StringParamSetter(param, value)));
	}
	
	@CLIOption("-xalias")
	public final void setXMLAlias(String value) { 
		params.add(new ModuleParamSetter(new PlanSelector<A>(), new XMLParamSetter(value)));
	}
	
	@CLIOption("-defaultParamValuesFile")
	public void setDefaultParamValuesFile(File defaultParamValuesFile) {
		this.defaultParamValuesFile = defaultParamValuesFile;
	}
	
	@CLIOption("-module")
	public final void appendModule(String id, String klass) { 
		moreModules.add(new Pair<String,String>(id, klass));
	}
	
	/**
	 * CLI option: set file from which to resume.
	 * @param resumeFile
	 */
	@CLIOption("-resume")
	public final void setResumeFile(File resumeFile) {
		this.resumeFile = resumeFile;
	}
	
	@CLIOption("-cleanTmp")
	public final void setCleanTmpDir() {
		this.cleanTmpDir = true;
	}
	
	@CLIOption("-creator")
	public final void setCreatorNameFeature(String creatorNameFeature) {
		this.creatorNameFeature = creatorNameFeature;
	}
	
	@CLIOption("-entity")
	public final void setCustomEntity(String name, String value) {
		customEntities.put(name, value);
	}
	
	@CLIOption("-inputDir")
	public void addInputDir(String path) {
		if (inputDirs == null) {
			inputDirs = new ArrayList<String>();
		}
		inputDirs.add(path);
	}
	
	@CLIOption("-resourceBase")
	public void addResourceBase(String base) {
		if (resourceBases == null) {
			resourceBases = new ArrayList<String>();
		}
		resourceBases.add(base);
	}
	
	@CLIOption("-outputDir")
	public void setOutputDir(String path) {
		outputDir = path;
	}
	
	@CLIOption("-baseDir")
	public void addBaseDir(String name, String path) throws CLIOException {
		if (baseDirs.containsKey(name)) {
			throw new CLIOException("duplicate base directory named " + name);
		}
		baseDirs.put(name, path);
	}
	
	@CLIOption("--input")
	public void OMTD_setInput(String value) {
		setAlias("input", value);
	}

	@CLIOption("--output")
	public void OMTD_setOutput(String path) {
		setOutputDir(path);
	}
	
	@CLIOption("--param")
	public static void OMTD_setParam() throws CLIOException {
		// this option should not be used actualy, this annotated method forces it to appear in the usage message
		throw new CLIOException("unknow option --param");
	}
	
	@CLIOption("-environmentEntities")
	public final void setEnvironmentEntities() {
		Map<String,String> env = System.getenv();
		for (Map.Entry<String,String> e : env.entrySet()) {
			String name = e.getKey();
			if (name.matches("[A-Z_a-z][0-9A-Z_a-z]*")) {
				customEntities.put(name, e.getValue());
			}
		}
	}
	
	@CLIOption("-propEntities")
	public final void setPropEntities(File f) throws IOException {
		Properties props = new Properties();
		try (InputStream is = new FileInputStream(f)) {
			props.load(is);
			for (Map.Entry<Object,Object> e : props.entrySet()) {
				customEntities.put(e.getKey().toString(), e.getValue().toString());
			}
		}
	}
	
	@CLIOption("-noProcess")
	public final void setNoProcess() { 
		this.noProcess = true;
	}

	@CLIOption("-writePlan")
	public final void setWritePlan() {
		setNoProcess();
		this.writePlan = true;
	}
	
	@CLIOption("-planDoc")
	public final void planDoc() throws TransformerConfigurationException {
		Source xslt = new StreamSource(getClass().getResourceAsStream(noColors ? "alvisnlp-doc2txt.xslt" : "alvisnlp-doc2ansi.xslt"));
		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		xmlDocTransformer = transformerFactory.newTransformer(xslt);
		xmlDocTransformer.setParameter("name", bundle.getString(DocResourceConstants.MODULE_NAME).toUpperCase(locale));
		xmlDocTransformer.setParameter("synopsis", bundle.getString(DocResourceConstants.SYNOPSIS).toUpperCase(locale));
		xmlDocTransformer.setParameter("description", bundle.getString(DocResourceConstants.MODULE_DESCRIPTION).toUpperCase(locale));
		xmlDocTransformer.setParameter("parameters", bundle.getString(DocResourceConstants.MODULE_PARAMETERS).toUpperCase(locale));
		setNoProcess();
		this.writePlan = true;
	}

	/**
	 * CLI option: print supported modules.
	 */
	@CLIOption(value="-supportedModules", stop=true)
	public final void supportedModules() { 
        List<String> moduleNames = new ArrayList<String>();
        for (Class<? extends Module<A>> mod : moduleFactory.supportedServices())
			moduleNames.add(mod.getCanonicalName());
        Collections.sort(moduleNames);
        for (String name : moduleNames)
			System.out.println(name);		
	}

	@CLIOption(value="-supportedModulesXML", stop=true)
	public final void supportedModulesXML() throws TransformerException {
		Document doc = XMLUtils.docBuilder.newDocument();
		Element root = XMLUtils.createRootElement(doc, "alvisnlp-supported-modules");
        for (Class<? extends Module<A>> mod : moduleFactory.supportedServices()) {
        	Element item = XMLUtils.createElement(doc, root, 1, "module-item");
        	item.setAttribute("target", mod.getCanonicalName());
        	item.setAttribute("short-target", mod.getSimpleName());
        }
		Source source = new DOMSource(doc);
		Result result = new StreamResult(System.out);
		xmlDocTransformer.transform(source, result);
	}
	
	/**
	 * CLI option: print XML documentation for the specified module class.
	 * @param name
	 * @throws TransformerFactoryConfigurationError
	 * @throws TransformerException
	 * @throws UnsupportedServiceException
	 * @throws ServiceInstanciationException 
	 * @throws AmbiguousAliasException
	 * @throws XPathExpressionException
	 */
	@CLIOption(value="-moduleDocXML", stop=true)
	public final void moduleDocXML(String name) throws TransformerFactoryConfigurationError, TransformerException, UnsupportedServiceException, AmbiguousAliasException, XPathExpressionException { 
		Source source = new DOMSource(getModuleDocumentation(name));
		Result result = new StreamResult(System.out);
		xmlDocTransformer.transform(source, result);
	}
	
	public Document getModuleDocumentation(String name) throws UnsupportedServiceException, AmbiguousAliasException, XPathExpressionException {
    	Module<A> mod = moduleFactory.getServiceByAlias(name);
    	Documentation documentation = mod.getDocumentation();
    	Document result = documentation.getDocument(locale);
    	String moduleClass = mod.getModuleClass();
    	supplementModuleDocumentation(mod, result, moduleClass, moduleClass.substring(moduleClass.lastIndexOf('.') + 1));
    	return result;
	}
	
	private void supplementModuleDocumentation(Module<A> mod, Document doc, String target, String shortTarget) throws XPathExpressionException {
    	Element alvisnlpDocElt = XMLUtils.evaluateElement("//alvisnlp-doc", doc);
    	if (alvisnlpDocElt == null) {
    		return;
    	}
    	alvisnlpDocElt.setAttribute("target", target);
    	alvisnlpDocElt.setAttribute("short-target", shortTarget);
    	Element moduleElt = XMLUtils.evaluateElement("module-doc|plan-doc", alvisnlpDocElt);
    	List<Element> paramDocs = XMLUtils.evaluateElements("param-doc", moduleElt);
    	for (ParamHandler<A> ph : mod.getAllParamHandlers()) {
    		List<Element> l = XMLUtils.evaluateElements("param-doc[@name = '" + ph.getName() + "']", moduleElt);
    		if (l.isEmpty()) {
    			Element pe = doc.createElement("param-doc");
    			pe.setAttribute("name", ph.getName());
    			continue;
    		}
    		while (l.size() > 1)
    			moduleElt.removeChild(l.get(0));
    	}
    	for (Element p : paramDocs) {
    		try {
				ParamHandler<A> ph = mod.getParamHandler(p.getAttribute("name"));
				p.setAttribute("mandatory", getParamStatus(ph));
        		Class<?> type = ph.getType();
        		p.setAttribute(TYPE_ATTRIBUTE_NAME, type.getCanonicalName());
        		p.setAttribute(SHORT_TYPE_ATTRIBUTE_NAME, type.getSimpleName());
			}
    		catch (UnexpectedParameterException upe) {
				p.getParentNode().removeChild(p);
			}
    	}
	}
	
	private String getParamStatus(ParamHandler<A> ph) {
        if (ph.isMandatory()) {
            if (ph.isSet()) {
				try {
                    return bundle.getString(DocResourceConstants.PARAMETER_DEFAULT) + ": " + converterFactory.getService(ph.getType()).getStringValue(ph.getValue());
                }
                catch (Exception e) {
                    return bundle.getString(DocResourceConstants.PARAMETER_DEFAULT) + ": ...";
                }
            }
            return bundle.getString(DocResourceConstants.REQUIRED_PARAMETER);
        }
        return bundle.getString(DocResourceConstants.OPTIONAL_PARAMETER);
	}

	/**
	 * CLI option: print the documentation for the specified module.
	 * @param name
	 * @throws TransformerException
	 * @throws XPathExpressionException
	 * @throws UnsupportedServiceException
	 * @throws ServiceInstanciationException
	 * @throws AmbiguousAliasException
	 */
	@CLIOption(value="-moduleDoc", stop=true)
	public final void moduleDoc(String name) throws TransformerException, XPathExpressionException, UnsupportedServiceException, AmbiguousAliasException { 
		Document doc = getModuleDocumentation(name);
		// same ClassLoader as this class
		Source xslt = new StreamSource(getClass().getResourceAsStream(noColors ? "alvisnlp-doc2txt.xslt" : "alvisnlp-doc2ansi.xslt"));
		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		Transformer transformer = transformerFactory.newTransformer(xslt);
		transformer.setParameter("name", bundle.getString(DocResourceConstants.MODULE_NAME).toUpperCase(locale));
		transformer.setParameter("synopsis", bundle.getString(DocResourceConstants.SYNOPSIS).toUpperCase(locale));
		transformer.setParameter("description", bundle.getString(DocResourceConstants.MODULE_DESCRIPTION).toUpperCase(locale));
		transformer.setParameter("parameters", bundle.getString(DocResourceConstants.MODULE_PARAMETERS).toUpperCase(locale));
		Source source = new DOMSource(doc);
		Result result = new StreamResult(System.out);
		transformer.transform(source, result);
	}
	
	@CLIOption(value="-noColors")
	public void noColors() {
		noColors = true;
	}
	
	@CLIOption(value="-supportedLibraries", stop=true)
	public static void supportedLibraries() {
		List<String> libs = new ArrayList<String>();
//		for (FunctionLibrary lib : ServiceLoader.load(FunctionLibrary.class))
		Class<FunctionLibrary> klass = FunctionLibrary.class;
		for (FunctionLibrary lib : ServiceLoader.load(klass, klass.getClassLoader())) {
			libs.add(lib.getName());
		}
		Collections.sort(libs);
		for (String lib : libs) {
			System.out.println(lib);
		}
	}

	@CLIOption(value="-supportedLibrariesXML", stop=true)
	public final void supportedLibrariesXML() throws TransformerException {
		Document doc = XMLUtils.docBuilder.newDocument();
		Element root = XMLUtils.createRootElement(doc, "alvisnlp-supported-libraries");
		Class<FunctionLibrary> klass = FunctionLibrary.class;
        for (FunctionLibrary lib : ServiceLoader.load(klass, klass.getClassLoader())) {
        	Element item = XMLUtils.createElement(doc, root, 1, "library-item");
        	item.setAttribute("target", lib.getName());
        	item.setAttribute("short-target", lib.getName());
        }
		Source source = new DOMSource(doc);
		Result result = new StreamResult(System.out);
		xmlDocTransformer.transform(source, result);
	}

	@CLIOption(value="-libraryDocXML", stop=true)
	public final void libraryDocXML(String name) throws TransformerFactoryConfigurationError, TransformerException { 
		Source source = new DOMSource(getLibraryDocumentation(name));
		Result result = new StreamResult(System.out);
		xmlDocTransformer.transform(source, result);
	}

	@CLIOption(value="-libraryDoc", stop=true)
	public final void libraryDoc(String name) throws TransformerException { 
		Document doc = getLibraryDocumentation(name);
		// same ClassLoader as this class
		Source xslt = new StreamSource(getClass().getResourceAsStream(noColors ? "alvisnlp-doc2txt.xslt" : "alvisnlp-doc2ansi.xslt"));
		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		Transformer transformer = transformerFactory.newTransformer(xslt);
		transformer.setParameter("name", bundle.getString(DocResourceConstants.MODULE_NAME).toUpperCase(locale));
		transformer.setParameter("synopsis", bundle.getString(DocResourceConstants.SYNOPSIS).toUpperCase(locale));
		transformer.setParameter("functions", bundle.getString(DocResourceConstants.FUNCTIONS).toUpperCase(locale));
		Source source = new DOMSource(doc);
		Result result = new StreamResult(System.out);
		transformer.transform(source, result);
	}
	
	private Document getLibraryDocumentation(String name) {
//		for (FunctionLibrary lib : ServiceLoader.load(FunctionLibrary.class))
		Class<FunctionLibrary> klass = FunctionLibrary.class;
		for (FunctionLibrary lib : ServiceLoader.load(klass, klass.getClassLoader())) {
			if (lib.getName().equals(name)) {
				return lib.getDocumentation().getDocument(locale);
			}
		}
		throw new RuntimeException("library " + name + " is not supported");
	}
	
	/**
	 * CLI oprion: print all supported converters.
	 */
	@CLIOption(value="-supportedConverters", stop=true)
	public final void supportedConverters() { 
        List<String> converters = new ArrayList<String>();
        for (Class<?> type : converterFactory.supportedServices())
			converters.add(type.getCanonicalName());
        Collections.sort(converters);
        for (String c : converters)
			System.out.println(c);
	}

	@CLIOption(value="-supportedConvertersXML", stop=true)
	public final void supportedConvertersXML() throws TransformerException {
		Document doc = XMLUtils.docBuilder.newDocument();
		Element root = XMLUtils.createRootElement(doc, "alvisnlp-supported-converters");
        for (Class<?> type : converterFactory.supportedServices()) {
        	Element item = XMLUtils.createElement(doc, root, 1, "converter-item");
        	item.setAttribute("target", type.getCanonicalName());
        	item.setAttribute("short-target", type.getSimpleName());
        }
		Source source = new DOMSource(doc);
		Result result = new StreamResult(System.out);
		xmlDocTransformer.transform(source, result);
	}

	/**
	 * CLI option: set the transformer to apply on the XML documentation.
	 * @param file
	 * @throws TransformerConfigurationException
	 */
	@CLIOption("-docTransformer")
	public final void setDocTransformer(File file) throws TransformerConfigurationException {
		xmlDocTransformer = XMLUtils.transformerFactory.newTransformer(new StreamSource(file));
	}
	
	@CLIOption("-xslParam")
	public void setXSLParameter(String name, String value) {
		xmlDocTransformer.setParameter(name, value);
	}
	
	@CLIOption("-dumpModule")
	public final void addDumpModule(String modulePath, String dumpPath) { 
		dumpModules.put(modulePath, dumpPath);
	}
	
	/**
	 * CLI option: print the XML documentation for the specified type converter.
	 * @param name
	 * @throws UnsupportedServiceException
	 * @throws ServiceInstanciationException
	 * @throws AmbiguousAliasException
	 * @throws TransformerFactoryConfigurationError
	 * @throws TransformerException
	 */
	@CLIOption(value="-converterDocXML", stop=true)
	public final void converterDocXML(String name) throws UnsupportedServiceException, AmbiguousAliasException, TransformerFactoryConfigurationError, TransformerException { 
		ParamConverter converter = converterFactory.getServiceByAlias(name);
		Document doc = converter.getDocumentation().getDocument();
		Source source = new DOMSource(doc);
		Result result = new StreamResult(System.out);
		xmlDocTransformer.transform(source, result);
	}
	
	/**
	 * print the documentation for the specified type converter.
	 * @param name
	 * @throws UnsupportedServiceException
	 * @throws ServiceInstanciationException
	 * @throws AmbiguousAliasException
	 * @throws TransformerException
	 */
	@CLIOption(value="-converterDoc", stop=true)
	public final void converterDoc(String name) throws UnsupportedServiceException, AmbiguousAliasException, TransformerException { 
		ParamConverter converter = converterFactory.getServiceByAlias(name);
		Document doc = converter.getDocumentation().getDocument();
		// same ClassLoader as this class
		Source xslt = new StreamSource(getClass().getResourceAsStream(noColors ? "alvisnlp-doc2txt.xslt" : "alvisnlp-doc2ansi.xslt"));
		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		Transformer transformer = transformerFactory.newTransformer(xslt);
		transformer.setParameter("name", bundle.getString(DocResourceConstants.MODULE_NAME));
		transformer.setParameter("synopsis", bundle.getString(DocResourceConstants.SYNOPSIS));
		transformer.setParameter("string-conversion", bundle.getString(DocResourceConstants.STRING_CONVERSION));
		transformer.setParameter("xml-conversion", bundle.getString(DocResourceConstants.XML_CONVERSION));
		Source source = new DOMSource(doc);
		Result result = new StreamResult(System.out);
		transformer.transform(source, result);
	}
	
	/**
	 * CLI option: set documentation locale.
	 * @param locale
	 */
	@CLIOption("-locale")
	public final void setLocale(String locale) {
        String[] ls = locale.split("_");
        switch (ls.length) {
            case 1:
                this.locale = new Locale(ls[0]);
                break;
            case 2:
                this.locale = new Locale(ls[0], ls[1]);
                break;
            case 3:
                this.locale = new Locale(ls[0], ls[1], ls[2]);
                break;
            default:
            	throw new IllegalArgumentException("strange locale: " + locale);
        }
    	bundle = ResourceBundle.getBundle(DocResourceConstants.RESOURCE, this.locale);
	}
	
	@CLIOption("-shell")
	public void shell() {
		appendModule("command-line-shell", moduleFactory.getShellModule());
	}
	
	@CLIOption("-browser")
	public void browser() {
		appendModule("annotation-browser", moduleFactory.getBrowserModule());
	}
	
	private static final Pattern OMTD_PARAM_PATTERN = Pattern.compile("--param:(?<alias>[^=]+)=(?<value>.+)");

	@Override
	public final boolean processArgument(String arg) throws CLIOException {
		Matcher m = OMTD_PARAM_PATTERN.matcher(arg);
		if (m.matches()) {
			setAlias(m.group("alias"), m.group("value"));
			return false;
		}
		if (arg.charAt(0) == '-') {
			throw new CLIOException("unknown option: " + arg);
		}
		setPlanFile(arg);
		return false;
	}
	
	public void setPlanFile(String planFile) throws CLIOException {
		if (this.planFile != null) {
			throw new CLIOException("specified a plan file twice: " + this.planFile + " / " + planFile);
		}
		this.planFile = planFile;
	}

	/**
	 * CLI option: print help.
	 */
	@CLIOption(value="-help", stop=true)
	public void help() { 
		System.out.print(usage());
	}
	
	/**
	 * Returns a new module factory.
	 */
    protected abstract M getModuleFactory();
    
    public static final ParamConverterFactory getParamConverterFactory() {
        CompoundParamConverterFactory result = new CompoundParamConverterFactory();
        result.loadServiceFactories(ParamConverterFactory.class, null, null, null);
        return result;
    }
    
    private void setHandlers(Logger logger, String path) throws FileNotFoundException, UnsupportedServiceException, ConverterException {
    	for (Handler h : logger.getHandlers()) {
    		logger.removeHandler(h);
    	}
        Handler stderrHandler = new FlushedStreamHandler(System.err, noColors ? CommandLineLogFormatter.INSTANCE : CommandLineLogFormatter.COLORS);
        logger.addHandler(stderrHandler);
        if (path == null)
			stderrHandler.setLevel(logLevel);
		else {
			File file = getOutputFile(path);
			File dir = file.getParentFile();
			if (dir != null) {
				dir.mkdirs();
			}
            Handler fileHandler = new FlushedStreamHandler(new FileOutputStream(file, appendToLog), CommandLineLogFormatter.INSTANCE);
            logger.addHandler(fileHandler);
            if (logLevel.intValue() < Level.FINE.intValue()) {
                fileHandler.setLevel(logLevel);
                stderrHandler.setLevel(Level.FINE);
            }
            else {
                fileHandler.setLevel(Level.FINE);
                stderrHandler.setLevel(logLevel);
            }
        }
    }
    
    private OutputFile getOutputFile(String path) throws ConverterException, UnsupportedServiceException {
        ParamConverter converter = converterFactory.getService(OutputFile.class);
        if (outputDir != null) {
        	converter.setOutputDir(outputDir);
        }
    	return (OutputFile) converter.convert(path);
    }
    
    protected Logger getLogger(C ctx) throws FileNotFoundException, IOException, UnsupportedServiceException, ConverterException {
    	Logger result = ctx.getLogger("alvisnlp");
    	result.setLevel(logLevel);
        result.setUseParentHandlers(false);
    	setHandlers(result, logPath);
    	for (Map.Entry<String,String> e : logPaths.entrySet()) {
        	Logger logger = ctx.getLogger("alvisnlp." + e.getKey());
        	logger.setLevel(logLevel);
        	setHandlers(logger, e.getValue());
        }
        return result;
    }
    
    private File buildRootTempDir(Logger logger) throws IOException {
	tmpDir.mkdirs();
        File result = File.createTempFile("alvisnlp", "", tmpDir);
        result.delete();
        result.mkdirs();
        logger.config("temporary directory: " + result.getCanonicalPath());
        return result;
    }
    
    public Document getDefaultParamValuesDoc(DocumentBuilder docBuilder) throws SAXException, IOException {
    	try (InputStream is = getDefaultParamValuesStream()) {
    		if (is == null) {
    			return null;
    		}
    		return docBuilder.parse(is);
    	}
	}
    
    private InputStream getDefaultParamValuesStream() throws FileNotFoundException {
    	if (defaultParamValuesFile == null) {
    		return null;
    	}
    	return new FileInputStream(defaultParamValuesFile);
	}
    
    private class ModuleParamSetter {
    	private final ModuleSelector<A> moduleSelector;
    	private final ParamSetter<A> paramSetter;
		
    	private ModuleParamSetter(ModuleSelector<A> moduleSelector, ParamSetter<A> paramSetter) {
			super();
			this.moduleSelector = moduleSelector;
			this.paramSetter = paramSetter;
		}
    	
    	private void set(Logger logger, PlanLoader<A> planLoader, Sequence<A> plan) throws Exception {
    		Module<A> module = moduleSelector.getModule(logger, plan);
    		if (module != null) {
    			paramSetter.setValue(logger, planLoader, module);
    		}
    	}
    }

    private interface ModuleSelector<A extends Annotable> {
    	Module<A> getModule(Logger logger, Sequence<A> plan);
    }

    private static class PlanSelector<A extends Annotable> implements ModuleSelector<A> {
		@Override
		public Module<A> getModule(Logger logger, Sequence<A> plan) {
			return plan;
		}
    }
    
    private class ModulePathSelector implements ModuleSelector<A> {
    	private final String modulePath;

		private ModulePathSelector(String modulePath) {
			super();
			this.modulePath = modulePath;
		}

		@Override
		public Module<A> getModule(Logger logger, Sequence<A> plan) {
			Module<A> result = plan.getModuleByPath(modulePath);
			if (result == null) {
        		logger.warning("there is no module with path: '" + modulePath + "'");	
			}
			return result;
		}
    }
    
    private List<String> buildResourceBases() {
    	List<String> result = new ArrayList<String>();
    	if (resourceBases != null) {
    		result.addAll(resourceBases);
    	}
    	result.addAll(converterFactory.getResourceBases());
    	return result;
    }
    
    private interface ParamSetter<A extends Annotable> {
    	void setValue(Logger logger, PlanLoader<A> planLoader, Module<A> module) throws Exception;
    }
    
    private class StringParamSetter implements ParamSetter<A> {
    	private final String name;
    	private final String value;
		
    	private StringParamSetter(String name, String value) {
			super();
			this.name = name;
			this.value = value;
		}

		@Override
		public void setValue(Logger logger, PlanLoader<A> planLoader, Module<A> module) throws ParameterException, ConverterException, UnsupportedServiceException {
    		ParamHandler<A> h = module.getParamHandler(name);
			ParamConverter conv = converterFactory.getService(h.getType());
			conv.setInputDirs(inputDirs);
			conv.setOutputDir(outputDir);
			conv.setResourceBases(buildResourceBases());
			logger.config("setting " + h.getName() + " to '" + value + "' in " + module.getPath());
			h.setValue(conv.convert(value));
		}
    }

    private class ParamUnsetter implements ParamSetter<A> {
    	private final String name;

		private ParamUnsetter(String name) {
			super();
			this.name = name;
		}

		@Override
		public void setValue(Logger logger, PlanLoader<A> planLoader, Module<A> module) throws Exception {
    		ParamHandler<A> h = module.getParamHandler(name);
			logger.config("unsetting " + h.getName() + " in " + module.getPath());
			h.setValue(null);
		}
    }
    
    private class XMLParamSetter implements ParamSetter<A> {
    	private final String xmlValue;

		private XMLParamSetter(String xmlValue) {
			super();
			this.xmlValue = xmlValue;
		}

		@Override
		public void setValue(Logger logger, PlanLoader<A> planLoader, Module<A> module) throws ParameterException, ConverterException, UnsupportedServiceException, SAXException, IOException, PlanException, URISyntaxException {
			logger.config("setting XML value to module " + module.getPath() + ": '" + xmlValue + "'");
    		InputSource is = new InputSource(new StringReader(xmlValue));
    		Document doc = XMLUtils.docBuilder.parse(is);
    		Element elt = doc.getDocumentElement();
    		planLoader.setParam(logger, elt, module);
		}
    }
    
	public Sequence<A> buildMainModule(C ctx) throws Exception {
    	if (planFile == null)
    		throw new PlanException("missing plan file path");
    	Logger logger = ctx.getLogger("alvisnlp");
        Timer<TimerCategory> planTimer = timer.newChild("load-plan", TimerCategory.LOAD_RESOURCE);
        planTimer.start();
        DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
        docBuilderFactory.setNamespaceAware(true);
        docBuilderFactory.setFeature("http://xml.org/sax/features/use-entity-resolver2", true);
        DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
        Document defaultParamValuesDoc = getDefaultParamValuesDoc(docBuilder);
        if (!customEntities.isEmpty()) {
        	logger.severe("custom XML entities is deprecated, support may be discontinued in a future version");
        }
		PlanLoader<A> planLoader = new PlanLoader<A>(moduleFactory, converterFactory, defaultParamValuesDoc, inputDirs, outputDir, baseDirs, buildResourceBases(), docBuilder, creatorNameFeature, customEntities);

		Document doc = planLoader.parseDoc(planFile);
		logger.config("loading plan from " + planFile);
		Sequence<A> result = planLoader.loadDocument(logger, planFile, doc);
        
        for (Pair<String,String> p : moreModules) {
        	Module<A> module = moduleFactory.getServiceByAlias(p.second);
        	module.setId(p.first);
        	result.appendModule(module);
        }
        
        if (CheckUniquePaths.visit(logger, result))
    		throw new PlanException("duplicate module paths");
        
        for (ModuleParamSetter mps : params) {
        	mps.set(logger, planLoader, result);
        }
        
        if (!writePlan) {
        	if (CheckMandatoryParameters.visit(logger, result))
        		throw new PlanException("some mandatory parameters are not set");

        	if (CheckParamValueConstraints.visit(ctx, logger, result))
        		throw new PlanException("some parameter values do not satisfy constraints");

        	result.init(ctx);

        	for (Map.Entry<String,String> e : dumpModules.entrySet()) {
        		String modulePath = e.getKey();
        		Module<A> module = result.getModuleByPath(modulePath);
        		if (module == null) {
        			logger.warning("there is no module with path: '" + modulePath + "'");
        			continue;
        		}
        		String dumpPath = e.getValue();
        		OutputFile dumpFile = getOutputFile(dumpPath);
        		logger.config("setting dump file after " + modulePath + " to " + dumpFile);
        		module.setDumpFile(dumpFile);
        	}
        }
        planTimer.stop();

        if (writePlan) {
        	supplementModuleDocumentation(result, doc, planFile, result.getId());
    		Source xSource = new DOMSource(doc);
    		Result xResult = new StreamResult(System.out);
    		xmlDocTransformer.transform(xSource, xResult);
        	//XXX
        }
        return result;
    }

	/**
     * Returns a new processing context.
     */
    protected abstract C newCommandLineProcessingContext();
    
    protected C getProcessingContext() {
    	C result = newCommandLineProcessingContext();
    	result.setLocale(locale);
    	result.setDumps(dumps);
        result.setResumeMode(resumeFile != null);
    	return result;
    }
        
    private static void logEnvironment(Logger logger) {
    	ResourceBundle res = ResourceBundle.getBundle("fr.inra.maiage.bibliome.alvisnlp.core.app.cli.LogEnvironment");
    	for (String var : res.keySet()) {
    		String value = System.getenv(var);
    		if (value == null)
    			continue;
    		String label = res.getString(var);
    		logger.config(label + ": " + value);
    	}
    	String javaVersion = System.getProperty("java.version");
    	logger.config("java version: " + javaVersion);
    }
    
    private void logVersion(Logger logger) {
    	logger.config("build version: " + gitInfo.getBuildVersion());
        if (!gitInfo.isCanonicalRemoteOrigin()) {
        	logger.config("remote URL: " + gitInfo.getRemoteOriginURL());
        }
        logger.config("commit id: " + gitInfo.getCommitId());
        logger.config("commit time: " + gitInfo.getCommitTime());
        if (!gitInfo.isDefaultBranch()) {
        	logger.config("branch: " + gitInfo.getBranch());
        }
        if (gitInfo.isDirty()) {
        	logger.warning("dirty build");
        	logger.config("build host: " + gitInfo.getBuildHost());
        	logger.config("build time: " + gitInfo.getBuildTime());
        }
    }
    
    protected void initProcessingContext(Logger logger, C ctx, Module<A> mainModule) throws IOException, ModuleException{
    	ctx.setRootTempDir(buildRootTempDir(logger));
    	if (!writePlan) {
    		ctx.checkPlan(logger, mainModule);
    	}
    }
    
    /**
     * Returns a new annotable.
     * @param logger
     * @throws Exception
     */
    protected abstract A getCorpus(Logger logger) throws Exception;
    	
    /**
     * Process the plan specified in command line.
     * @throws IOException 
     * @throws ConverterException 
     * @throws UnsupportedServiceException 
     * @throws FileNotFoundException 
     * @throws Exception
     */
    public void process() throws IOException, UnsupportedServiceException, ConverterException {
    	C ctx = getProcessingContext();
    	Logger logger = getLogger(ctx);
    	logVersion(logger);
    	logEnvironment(logger);
		try {
	    	timer.start();
			Module<A> mainModule = buildMainModule(ctx);
			initProcessingContext(logger, ctx, mainModule);
			A corpus = getCorpus(logger);
			if (noProcess) {
				logger.info("skipping process...");
			}
			else {
				logger.info("start");
				ctx.processCorpus(mainModule, corpus);
				logger.info("finished");
			}
	    	logFinished(logger, corpus);
	    	timer.stop();
	    	logTimer(ctx, mainModule);
	    	if (cleanTmpDir) {
	    		File rootTempDir = ctx.getRootTempDir();
				logger.info("deleting temp dir: " + rootTempDir);
				Files.recDelete(rootTempDir);
	    	}
		}
		catch (ModuleException me) {
			error(logger, me, me.getMessage());
		}
		catch (SAXParseException saxpe) {
			error(logger, saxpe, "XML error at " + saxpe.getSystemId() + ":" + saxpe.getLineNumber() + ": " + saxpe.getMessage());
		}
		catch (ConverterException ce) {
			error(logger, ce, "parameter conversion error: " + ce.getMessage());
		}
		catch (PlanException pe) {
			error(logger, pe, "error in plan: " + pe.getMessage());
		}
		catch (ParserConfigurationException|IOException|ServiceException e) {
			error(logger, e, e.getMessage());
		}
		catch (Exception e) {
			error(logger, e, e.getMessage());
		}
    }
    
    private void error(Logger logger, Exception e, String msg) {
    	if (logger.isLoggable(Level.FINEST)) {
    		logger.log(Level.SEVERE, msg, e);
    	}
		else {
	    	logger.severe(msg);
			logger.info("use -verbose option to get debug info");
		}
    	exitCode = 1;
    }

    private static Comparator<Timer<TimerCategory>> TIMER_COMPARATOR = new Comparator<Timer<TimerCategory>>() {
		@Override
		public int compare(Timer<TimerCategory> a, Timer<TimerCategory> b) {
			return new Long(b.getTime()).compareTo(a.getTime());
			//return Long.compare(b.getTime(), a.getTime());
		}
	};
    
    private static void recLogTimer(Logger logger, Timer<TimerCategory> timer, double percent, String indent) {
    	logger.info(String.format("% 8d   %s%6.2f%%   %s", timer.getTime() / 1000000, indent, percent, timer.getName()));
    	long rest = timer.getTime() - timer.getChildrenTime();
    	List<Timer<TimerCategory>> children = new ArrayList<Timer<TimerCategory>>(timer.getChildren());
    	Collections.sort(children, TIMER_COMPARATOR);
    	for (Timer<TimerCategory> t : children)
    		recLogTimer(logger, t, 100.0 * t.getTime() / timer.getTime(), indent + "    ");
    	if (!children.isEmpty())
    		logger.info(String.format("% 8d       %s%6.2f%%   (misc)", rest / 1000000, indent, 100.0 * rest / timer.getTime()));
    }
    
    private static <T> void logTimes(Logger logger, String title, Stats<T,Count> stats) {
    	logger.info(title);
    	long total = stats.sum();
    	for (Map.Entry<T,Count> e : stats.entryList(true)) {
    		long t = e.getValue().get();
    		logger.info(String.format("% 8d   %6.2f%%   %s", t / 1000000, 100.0 * t / total, e.getKey()));
    	}
    }
    
    protected void logTimer(C ctx, Module<A> mainModule) throws ModuleException {
    	Logger logger = ctx	.getLogger("alvisnlp.timer");
    	
    	logger.info("Hierarchical timer summary:");
    	recLogTimer(logger, timer, 100, "");
    	
    	if (!noProcess) {
    		Stats<Module<A>,Count> moduleStats = new CountStats<Module<A>>(new LinkedHashMap<Module<A>,Count>());
    		List<Module<A>> modules = CollectModules.visit(mainModule, false);
    		for (Module<A> m : modules)
    			moduleStats.incr(m, m.getTimer(ctx).getTime());
    		logTimes(logger, "Time spent by effective module:", moduleStats);
    	}

    	Stats<TimerCategory,Count> categoryStats = new CountStats<TimerCategory>(new EnumMap<TimerCategory,Count>(TimerCategory.class));
    	for (Timer<TimerCategory> t : timer.allTimers())
    		categoryStats.incr(t.getCategory(), t.getTime() - t.getChildrenTime());
    	logTimes(logger, "Time spent by task category:", categoryStats);
    }
    
    /**
     * A chance to log something when the processing has finished.
     * @param logger
     * @param corpus
     */
    protected abstract void logFinished(Logger logger, A corpus);
    
    /**
     * Run alvisnlp.
     * @param args
     * @throws IOException 
     * @throws CLIOException 
     * @throws FileNotFoundException 
     */
    public final void run(String[] args) throws Exception {
    	if (!parse(args))
			process();
    }
    
    public int getExitCode() {
    	return exitCode;
    }
}
