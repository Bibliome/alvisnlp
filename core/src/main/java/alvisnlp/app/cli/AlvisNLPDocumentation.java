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


package alvisnlp.app.cli;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.ServiceLoader;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.xpath.XPathExpressionException;

import org.bibliome.util.Pair;
import org.bibliome.util.TeeOutputStream;
import org.bibliome.util.clio.CLIOException;
import org.bibliome.util.clio.CLIOParser;
import org.bibliome.util.clio.CLIOption;
import org.bibliome.util.service.AmbiguousAliasException;
import org.bibliome.util.service.UnsupportedServiceException;
import org.bibliome.util.xml.XMLUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import alvisnlp.converters.CompoundParamConverterFactory;
import alvisnlp.converters.ParamConverter;
import alvisnlp.converters.ParamConverterFactory;
import alvisnlp.corpus.expressions.FunctionLibrary;
import alvisnlp.documentation.Documentation;
import alvisnlp.factory.CompoundCorpusModuleFactory;
import alvisnlp.factory.CorpusModuleFactory;
import alvisnlp.factory.ModuleFactory;
import alvisnlp.module.Annotable;
import alvisnlp.module.Module;
import alvisnlp.module.ParamHandler;
import alvisnlp.module.UnexpectedParameterException;

/**
 * CLI for alvisnlp module and converter documentation
 * @author rbossy
 *
 */
public class AlvisNLPDocumentation extends CLIOParser { 
	private static enum SortBy {
		NONE,
		SIMPLE,
		CANONICAL;
	}
	
	private String outputFilenamePattern = null;
	private Transformer xmlDocTransformer;
	private boolean converters = false;
	private boolean modules = true;
	private boolean libraries = false;
	private Locale locale = Locale.getDefault();
	private SortBy sortBy = SortBy.NONE;
	private boolean list = false;
	private final Map<String,String> params = new HashMap<String,String>();
	private final CompoundParamConverterFactory converterFactory = new CompoundParamConverterFactory();

	/**
	 * Creates an AlvisNLP documentation CLI instance.
	 * @throws TransformerConfigurationException
	 */
	public AlvisNLPDocumentation() throws TransformerConfigurationException {
		super();
		xmlDocTransformer = XMLUtils.transformerFactory.newTransformer();
        converterFactory.loadServiceFactories(ParamConverterFactory.class, null, null, null);
	}

	/**
	 * CLI option: print module and/or converter list.
	 */
	@CLIOption("-list")
	public void setList() {
		list = true;
	}

	/**
	 * CLI option: set string parameter for the XSLT stylesheet.
	 * @param key
	 * @param value
	 */
	@CLIOption("-param")
	public void setParameter(String key, String value) {
		params.put(key, value);
	}

	/**
	 * CLI option: pattern for output file names.
	 * @param outputFilenamePattern
	 */
	@CLIOption("-pattern")
	public void setOutputFilenamePattern(String outputFilenamePattern) {
		this.outputFilenamePattern = outputFilenamePattern;
	}

	/**
	 * CLI option: set the XSLT stylesheet.
	 * @param file
	 * @throws TransformerConfigurationException
	 */
	@CLIOption("-transformer")
	public void setXmlDocTransformer(File file) throws TransformerConfigurationException {
		xmlDocTransformer = XMLUtils.transformerFactory.newTransformer(new StreamSource(file));
	}
	
	/**
	 * CLI option: output documentation for converters.
	 */
	@CLIOption("-converters")
	public void setConverters() {
		converters = true;
	}

	@CLIOption("-libraries")
	public void setLibraries() {
		libraries = true;
	}
	
	/**
	 * CLI option: do not output documentation for modules.
	 */
	@CLIOption("-noModules")
	public void setNoModules() {
		modules = false;
	}
	
	/**
	 * CLI option: sort by simple name.
	 */
	@CLIOption("-simpleSort")
	public void simpleSort() {
		sortBy = SortBy.SIMPLE;
	}
	
	/**
	 * CLI option: sort by fully qualified name.
	 */
	@CLIOption("-canonicalSort")
	public void canonicalSort() {
		sortBy = SortBy.CANONICAL;
	}
	
	/**
	 * CLI option: print help.
	 */
	@CLIOption(value="-help", stop=true)
	public void help() {
		System.out.print(usage());
	}

	private void output(Document doc, OutputStream out) throws TransformerException, IOException {
		Source source = new DOMSource(doc);
		Result result = new StreamResult(out);
//		ResourceBundle bundle = ResourceBundle.getBundle(DocResourceConstants.RESOURCE, locale);
		ResourceBundle bundle = ResourceBundle.getBundle(DocResourceConstants.RESOURCE, locale, AlvisNLPDocumentation.class.getClassLoader());
		xmlDocTransformer.setParameter("synopsis", bundle.getString(DocResourceConstants.SYNOPSIS));
		xmlDocTransformer.setParameter("description", bundle.getString(DocResourceConstants.MODULE_DESCRIPTION));
		xmlDocTransformer.setParameter("parameters", bundle.getString(DocResourceConstants.MODULE_PARAMETERS));
		xmlDocTransformer.setParameter("string-conversion", bundle.getString(DocResourceConstants.STRING_CONVERSION));
		xmlDocTransformer.setParameter("xml-conversion", bundle.getString(DocResourceConstants.XML_CONVERSION));
		xmlDocTransformer.setParameter("modules", bundle.getString(DocResourceConstants.MODULE_LIST));
		xmlDocTransformer.setParameter("converters", bundle.getString(DocResourceConstants.CONVERTER_LIST));
		xmlDocTransformer.setParameter("libraries", bundle.getString(DocResourceConstants.LIBRARY_LIST));
		xmlDocTransformer.setParameter("functions", bundle.getString(DocResourceConstants.FUNCTIONS));
		xmlDocTransformer.setParameter("full-names", bundle.getString(DocResourceConstants.FULL_NAMES));
		xmlDocTransformer.setParameter("short-names", bundle.getString(DocResourceConstants.SHORT_NAMES));
		xmlDocTransformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
		for (Map.Entry<String,String> e : params.entrySet())
			xmlDocTransformer.setParameter(e.getKey(), e.getValue());
		xmlDocTransformer.transform(source, result);
		out.write('\n');
	}
	
	private void outputList(List<Pair<Class<?>,Document>> docs, OutputStream out) throws TransformerException, IOException {
		Document listDoc = XMLUtils.docBuilder.newDocument();
		Element root = listDoc.createElement("alvisnlp-doclist");
		listDoc.appendChild(root);
		for (Pair<Class<?>,Document> doc : docs) {
			Element docElt = doc.second.getDocumentElement();
			listDoc.adoptNode(docElt);
			root.appendChild(docElt);
		}
		output(listDoc, out);
	}
	
	private void outputSingle(Collection<Pair<Class<?>,Document>> docs) throws TransformerException, IOException {
		for (Pair<Class<?>,Document> p : docs)
			output(p.second, System.out);
	}

	private OutputStream getOutputStream(Class<?> klass) throws FileNotFoundException {
		String filename = outputFilenamePattern.replace("%n", klass.getSimpleName()).replace("%f", klass.getCanonicalName());
		if (filename.contains("%b")) {
			String fullFilename = filename.replace("%b", klass.getCanonicalName());
			OutputStream fullOut = new PrintStream(fullFilename);
			String simpleFilename = filename.replace("%b", klass.getSimpleName());
			OutputStream simpleOut = new PrintStream(simpleFilename);
//			System.err.println("  writing to " + fullFilename + " and " + simpleFilename);
			return new TeeOutputStream(fullOut, simpleOut);
		}
		System.err.println("  writing to " + filename);
		return new PrintStream(filename);
	}
	
	private void outputMultiple(Collection<Pair<Class<?>,Document>> docs) throws TransformerException, IOException {
		for (Pair<Class<?>,Document> p : docs) {
			OutputStream out = getOutputStream(p.first);
			output(p.second, out);
			out.close();
		}
	}
	
	private void getConvertersDocumentations(Collection<Pair<Class<?>,Document>> docs) throws UnsupportedServiceException, XPathExpressionException {
		Collection<Class<?>> classes = converterFactory.supportedServices();
        for (Class<?> klass : classes) {
    		ParamConverter converter = converterFactory.getService(klass);
    		Document doc = converter.getDocumentation().getDocument();
    		getDocRootElement(doc, klass);
        	docs.add(new Pair<Class<?>,Document>(klass, doc));
        }
	}
	
	private static void getLibrariesDocumentations(Collection<Pair<Class<?>,Document>> docs) {
//		for (FunctionLibrary lib : ServiceLoader.load(FunctionLibrary.class)) {
		Class<FunctionLibrary> klass = FunctionLibrary.class;
		for (FunctionLibrary lib : ServiceLoader.load(klass, klass.getClassLoader())) {
			docs.add(new Pair<Class<?>,Document>(lib.getClass(), lib.getDocumentation().getDocument()));
		}
	}
	
	private static ModuleFactory<? extends Annotable> getModuleFactory() {
		System.err.println("  loading corpus module factories");
		CompoundCorpusModuleFactory corpusModuleFactory = new CompoundCorpusModuleFactory();
		corpusModuleFactory.loadServiceFactories(CorpusModuleFactory.class, null, null, null);
		return corpusModuleFactory;
	}
	
	private void getModulesDocumentations(Collection<Pair<Class<?>,Document>> docs) throws UnsupportedServiceException, AmbiguousAliasException, XPathExpressionException {
		ModuleFactory<? extends Annotable> moduleFactory = getModuleFactory();
        for (Class<?> klass : moduleFactory.supportedServices()) {
        	String name = klass.getCanonicalName();
//			System.err.println("  loading documentation for " + name);
    		Module<?> module = moduleFactory.getServiceByAlias(name);
    		Document doc = getModuleDocumentation(module, klass);
        	docs.add(new Pair<Class<?>,Document>(klass, doc));
        }
	}
	
	private static Element getDocRootElement(Document doc, Class<?> klass) throws XPathExpressionException {
    	Element docElement = XMLUtils.evaluateElement("/alvisnlp-doc", doc);
    	docElement.setAttribute("short-target", klass.getSimpleName());
    	docElement.setAttribute("target", klass.getCanonicalName());
    	return docElement;
	}

	private Document getModuleDocumentation(Module<?> mod, Class<?> klass) throws XPathExpressionException {
    	Documentation documentation = mod.getDocumentation();
    	Document result = documentation.getDocument(locale);
    	Element docElement = getDocRootElement(result, klass);
    	if (mod.isBeta())
    		docElement.setAttribute("beta", "yes");
    	if (mod.getUseInstead().length != 0)
    		docElement.setAttribute("use-instead", mod.getUseInstead()[0].getCanonicalName());
    	Element moduleElt = XMLUtils.evaluateElement("module-doc", docElement);
    	List<Element> paramDocs = XMLUtils.evaluateElements("param-doc", moduleElt);
    	for (ParamHandler<? extends Annotable> ph : mod.getAllParamHandlers()) {
    		List<Element> l = XMLUtils.evaluateElements("param-doc[@name = '" + ph.getName() + "']", moduleElt);
    		if (l.isEmpty()) {
    			Element pe = result.createElement("param-doc");
    			pe.setAttribute("name", ph.getName());
    			continue;
    		}
    		for (Element ch : l.subList(1, l.size())) {
    			moduleElt.removeChild(ch);
    		}
//    		while (l.size() > 1) {
//    			System.err.println("moduleElt = " + moduleElt);
//    			System.err.println("l.get(0) = " + l.get(0));
//    			moduleElt.removeChild(l.get(0));
//    		}
    	}
    	for (Element p : paramDocs) {
    		try {
				ParamHandler<? extends Annotable> ph = mod.getParamHandler(p.getAttribute("name"));
				String paramStatus = getParamStatus(ph);
				p.setAttribute("mandatory", paramStatus);
				Class<?> type = ph.getType();
				p.setAttribute("type", type.getCanonicalName());
				p.setAttribute("short-type", type.getSimpleName());
			} catch (UnexpectedParameterException upe) {
				p.getParentNode().removeChild(p);
			}
    	}
    	return result;
	}

	private String getParamStatus(ParamHandler<? extends Annotable> ph) {
		ResourceBundle bundle = ResourceBundle.getBundle(DocResourceConstants.RESOURCE, locale);
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
	
	private static final Comparator<Class<?>> canonicalNameComparator = new Comparator<Class<?>>() {
		@Override
		public int compare(Class<?> a, Class<?> b) {
			return a.getCanonicalName().compareTo(b.getCanonicalName());
		}
	};
	
	private static final Comparator<Class<?>> simpleNameComparator = new Comparator<Class<?>>() {
		@Override
		public int compare(Class<?> a, Class<?> b) {
			return a.getSimpleName().compareTo(b.getSimpleName());
		}
	};
	
	public static void main(String[] args) throws UnsupportedServiceException, AmbiguousAliasException, TransformerException, IOException, XPathExpressionException, CLIOException {
		AlvisNLPDocumentation inst = new AlvisNLPDocumentation();
		if (inst.parse(args))
			return;
//		if (inst.list) {
//			Document doc = inst.getDocumentationList();
//			inst.output(doc, System.out);
//			return;
//		}
		List<Pair<Class<?>,Document>> docs = new ArrayList<Pair<Class<?>,Document>>();
		if (inst.converters) {
			System.err.println("reading converters documenation");
			inst.getConvertersDocumentations(docs);
		}
		if (inst.modules) {
			System.err.println("reading modules documenation");
			inst.getModulesDocumentations(docs);
		}
		if (inst.libraries) {
			System.err.println("reading liraries documenation");
			AlvisNLPDocumentation.getLibrariesDocumentations(docs);
		}
		Comparator<Pair<Class<?>,Document>> comp;
		switch (inst.sortBy) {
		case NONE:
			break;
		case CANONICAL:
			comp = Pair.firstComparator(canonicalNameComparator);
			Collections.sort(docs, comp);
			break;
		case SIMPLE:
			comp = Pair.firstComparator(simpleNameComparator);
			Collections.sort(docs, comp);
			break;
		}
		if (inst.list) {
			System.err.println("writing documentation list to standard output");
			inst.outputList(docs, System.out);
			return;
		}
		if (inst.outputFilenamePattern == null) {
			System.err.println("writing documentation to standard output");
			inst.outputSingle(docs);
			return;
		}
		System.err.println("writing documentation to files");
		inst.outputMultiple(docs);
	}

	@Override
	public String getResourceBundleName() {
		return AlvisNLPDocumentation.class.getCanonicalName() + "Help";
	}

	@Override
	protected boolean processArgument(String arg) throws CLIOException {
		throw new CLIOException("loose argument " + arg);
	}
}
