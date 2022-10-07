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


package fr.inra.maiage.bibliome.alvisnlp.core.plan;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilder;

import org.w3c.dom.Comment;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.Text;
import org.xml.sax.SAXException;

import fr.inra.maiage.bibliome.alvisnlp.core.converters.ConverterException;
import fr.inra.maiage.bibliome.alvisnlp.core.converters.ParamConverter;
import fr.inra.maiage.bibliome.alvisnlp.core.converters.ParamConverterFactory;
import fr.inra.maiage.bibliome.alvisnlp.core.documentation.ConstantDocumentation;
import fr.inra.maiage.bibliome.alvisnlp.core.factory.ModuleFactory;
import fr.inra.maiage.bibliome.alvisnlp.core.module.Annotable;
import fr.inra.maiage.bibliome.alvisnlp.core.module.Module;
import fr.inra.maiage.bibliome.alvisnlp.core.module.ModuleException;
import fr.inra.maiage.bibliome.alvisnlp.core.module.ParamHandler;
import fr.inra.maiage.bibliome.alvisnlp.core.module.ParameterException;
import fr.inra.maiage.bibliome.alvisnlp.core.module.Sequence;
import fr.inra.maiage.bibliome.util.service.ServiceException;
import fr.inra.maiage.bibliome.util.service.UnsupportedServiceException;
import fr.inra.maiage.bibliome.util.streams.SourceStream;
import fr.inra.maiage.bibliome.util.streams.StreamFactory;
import fr.inra.maiage.bibliome.util.xml.XMLUtils;

/**
 * A plan loader reads plans from XML files.
 * @author rbossy
 *
 * @param <T>
 */
public class PlanLoader<T extends Annotable> {
	/** Tag name for plan (top-level). */
	public static final String PLAN_ELEMENT_NAME = "alvisnlp-plan";
	
	public static final String PLAN_ID_ATTRIBUTE = "id";
	
	/** Attribute name for module instance class. */
	public static final String CLASS_ATTRIBUTE_NAME = "class";

	public static final String ACTIVE_PARAM_ELEMENT_NAME = "active";

	public static final String SELECT_PARAM_ELEMENT_NAME = "select";

	public static final String USER_FUNCTIONS_PARAM_ELEMENT_NAME = "userFunctions";
	
	public static final String SHELL_ELEMENT_NAME = "shell";
	
	public static final String BROWSER_ELEMENT_NAME = "browser";
		
	/** Attribute name for module parameters and sequence parameter alias. */
	public static final String NAME_ATTRIBUTE_NAME = "name";
	
	/** Tag name for alias target parameter. */
	public static final String ALIAS_ELEMENT_NAME = "alias";
	
	/** Attribute name for alias target parameter module path. */
	public static final String MODULE_PATH_ATTRIBUTE_NAME = "module";
	
	/** Attribute name for alias target parameter name. */
	public static final String PARAM_ATTRIBUTE_NAME = "param";

	public static final String[] SOURCE_ATTRIBUTE_NAMES = { "href", "source", "resource", "file" };

	public static final String LOAD_FILE_ATTRIBUTE_NAME = "load";

	public static final String DOCUMENTATION_ELEMENT_NAME = "alvisnlp-doc";

	public static final String LOCALE_ATTRIBUTE_NAME = "locale";

	public static final String BASE_DIR_ATTRIBUTE_NAME = "base-dir";
	
	public static final String OUTPUT_FEED_ATTRIBUTE_NAME = "output-feed";

	public static final String DEFAULT_PARAMS_MODULE_ELEMENT_NAME = "module";

	public static final String PLAN_PARAM_ELEMENT_NAME = "param";

	private final ModuleFactory<T> moduleFactory;
	private final ParamConverterFactory converterFactory;
	private final Map<String,List<Element>> defaultParamValues;
	private final List<String> inputDirs;
	private final List<String> resourceBases;
	private final String outputDir;
	private final Map<String,String> baseDirs;
	private final DocumentBuilder docBuilder;
	private final String creatorNameFeature;
	private int nShells = 0;
	
	/**
	 * Creates a plan loader that uses the specified module factory, converter factory and document builder.
	 * @param moduleFactory module factory used to instantiate modules
	 * @param converterFactory parameter converter factory used to convert parameter values
	 * @param docBuilder document builder used to parse XML files
	 * @param customEntities 
	 * @throws PlanException 
	 */
	public PlanLoader(ModuleFactory<T> moduleFactory, ParamConverterFactory converterFactory, Document defaultParamValuesDoc, List<String> inputDirs, String outputDir, Map<String,String> baseDirs, List<String> resourceBases, DocumentBuilder docBuilder, String creatorNameFeature) throws PlanException {
		super();
		this.moduleFactory = moduleFactory;
		this.converterFactory = converterFactory;
		this.defaultParamValues = buildDefaultParamValues(defaultParamValuesDoc);
		this.inputDirs = inputDirs;
		this.outputDir = outputDir;
		this.baseDirs = baseDirs;
		this.resourceBases = resourceBases;
		this.docBuilder = docBuilder;
		this.creatorNameFeature = creatorNameFeature;
	}

	private static Map<String,List<Element>> buildDefaultParamValues(Document defaultParamValuesDoc) throws PlanException {
		Map<String,List<Element>> result = new LinkedHashMap<String,List<Element>>();
		if (defaultParamValuesDoc == null) {
			return result;
		}
		Element root = defaultParamValuesDoc.getDocumentElement();
		for (Element elt : XMLUtils.childrenElements(root)) {
			String tag = elt.getTagName();
			switch (tag) {
				case DEFAULT_PARAMS_MODULE_ELEMENT_NAME: {
					if (!elt.hasAttribute(CLASS_ATTRIBUTE_NAME)) {
						throw new PlanException("missing " + CLASS_ATTRIBUTE_NAME + " attribute in module defaults");
					}
					String moduleClass = elt.getAttribute(CLASS_ATTRIBUTE_NAME);
					if (result.containsKey(moduleClass)) {
						throw new PlanException("duplicate default values for module " + moduleClass);
					}
					List<Element> defaultValues = XMLUtils.childrenElements(elt);
					result.put(moduleClass, defaultValues);
					break;
				}
				default: {
					throw new PlanException("unexpected tag " + tag + " in module defaults");
				}
			}
		}
		return result;
	}

	/**
	 * Reads the specified file containing the XML description of a plan and builds the corresponding module.
	 * @param source
	 * @return the sequence described in the specified file
	 * @throws PlanException
	 * @throws IOException 
	 * @throws SAXException 
	 * @throws ConverterException 
	 * @throws ServiceException 
	 * @throws URISyntaxException 
	 * @throws ParameterException 
	 */
	public Sequence<T> loadSource(Logger logger, SourceStream source) throws SAXException, IOException, PlanException, ModuleException, ServiceException, ConverterException, URISyntaxException {
		try (InputStream is = source.getInputStream()) {
			String name = source.getStreamName(is);
			logger.config("loading plan from " + name);
			Document doc = docBuilder.parse(is);
			return loadDocument(logger, name, doc);
		}
	}
	
	public Document parseDoc(SourceStream source) throws SAXException, IOException {
		try (InputStream is = source.getInputStream()) {
			return docBuilder.parse(is);
		}
	}

	public Document parseDoc(String source) throws SAXException, IOException, URISyntaxException {
		StreamFactory sf = getStreamFactory(null);
		SourceStream sourceStream = sf.getSourceStream(source);
		return parseDoc(sourceStream);
	}
	
	public Sequence<T> loadSource(Logger logger, String sourceString, String baseDir) throws SAXException, IOException, PlanException, ModuleException, ServiceException, ConverterException, URISyntaxException {
		StreamFactory sf = getStreamFactory(baseDir);
		SourceStream source = sf.getSourceStream(sourceString);
		return loadSource(logger, source);
	}
	
	/**
	 * Reads plan description in the specified DOM tree and builds the corresponding module.
	 * @param source
	 * @return the sequence described in the specified DOM tree
	 * @throws PlanException
	 * @throws ConverterException 
	 * @throws ServiceException 
	 * @throws ParameterException 
	 * @throws IOException 
	 * @throws SAXException 
	 * @throws URISyntaxException 
	 */
	public Sequence<T> loadDocument(Logger logger, String source, Document doc) throws PlanException, ModuleException, ServiceException, ConverterException, SAXException, IOException, URISyntaxException {
		Element elt = doc.getDocumentElement();
		if (!PLAN_ELEMENT_NAME.equals(elt.getTagName())) {
			throw new PlanException("expected element " + PLAN_ELEMENT_NAME + ", got " + elt.getTagName());
		}
		if (!elt.hasAttribute(PLAN_ID_ATTRIBUTE)) {
			throw new PlanException("missing " + PLAN_ID_ATTRIBUTE + " attribute");
		}
		String id = elt.getAttribute(PLAN_ID_ATTRIBUTE);
		Sequence<T> result = loadSequence(logger, source, doc.getDocumentElement(), id, true);
		result.setSequenceSourceName(source);
		logger.fine("finished loading " + source);
		return result;
	}
	
	private void setModuleId(Logger logger, Module<T> module, Element elt, String id) throws PlanException, UnsupportedServiceException, ConverterException {
		if (id == null) {
			id = elt.getTagName();
		}
		module.setId(id);
	}
	
	private static void setSequenceProperties(Sequence<?> sequence, Element elt) {
		NamedNodeMap attributes = elt.getAttributes();
		for (int i = 0; i < attributes.getLength(); ++i) {
			Node attr = attributes.item(i);
			String name = attr.getNodeName();
			String value = attr.getNodeValue();
			sequence.setProperty(name, value);
		}
	}

	private Sequence<T> loadSequence(Logger logger, String source, Element elt, String id, boolean plan) throws PlanException, ModuleException, ServiceException, ConverterException, SAXException, IOException, URISyntaxException {
		Sequence<T> result = moduleFactory.newSequence();
		if (result == null) {
			throw new ModuleException("could not instanciate sequence");
		}
		ConstantDocumentation documentation = null;
		setModuleId(logger, result, elt, id);
		if (plan) {
			setSequenceProperties(result, elt);
		}
		Collection<Element> aliasParams = new ArrayList<Element>();
		for (Node child : XMLUtils.childrenNodes(elt)) {
			if (child instanceof Comment)
				continue;
			if (checkEmptyText(child))
				continue;
			if (child instanceof Element) {
				Element childElement = (Element) child;
				String childName = childElement.getTagName();
				if (ACTIVE_PARAM_ELEMENT_NAME.equals(childName)) {
					setParam(logger, source, childElement, result);
					continue;
				}
				if (SELECT_PARAM_ELEMENT_NAME.equals(childName)) {
					setParam(logger, source, childElement, result);
					continue;
				}
				if (USER_FUNCTIONS_PARAM_ELEMENT_NAME.equals(childName)) {
					setParam(logger, source, childElement, result);
					continue;
				}
				if (SHELL_ELEMENT_NAME.equals(childName)) {
					String shellId = "shell_" + (++nShells);
					String shellModule = moduleFactory.getShellModule();
					childElement.setAttribute(CLASS_ATTRIBUTE_NAME, shellModule);
					Module<T> module = loadModule(logger, source, childElement, shellId);
					result.appendModule(module);
					continue;
				}
				if (BROWSER_ELEMENT_NAME.equals(childName)) {
					String browserId = "browser_" + (++nShells);
					String browserModule = moduleFactory.getBrowserModule();
					childElement.setAttribute(CLASS_ATTRIBUTE_NAME, browserModule);
					Module<T> module = loadModule(logger, source, childElement, browserId);
					result.appendModule(module);
					continue;
				}
				if (plan && PLAN_PARAM_ELEMENT_NAME.equals(childName)) {
					aliasParams.add(childElement);
					continue;
				}
				if (plan && DOCUMENTATION_ELEMENT_NAME.equals(childName)) {
					if (documentation == null) {
						documentation = new ConstantDocumentation();
						result.setDocumentation(documentation);
					}
					Locale locale;
					if (childElement.hasAttribute(LOCALE_ATTRIBUTE_NAME)) {
						String lang = childElement.getAttribute(LOCALE_ATTRIBUTE_NAME);
						locale = Locale.forLanguageTag(lang);
					}
					else {
						locale = Locale.getDefault();
					}
					Document doc = docBuilder.newDocument();
					Element copyElement = (Element) childElement.cloneNode(true);
					doc.adoptNode(copyElement);
					doc.appendChild(copyElement);
					documentation.setDocument(locale, doc);
					continue;
				}
				Module<T> module = loadModuleOrSequence(logger, source, childElement);
				result.appendModule(module);
				continue;
//				throw new PlanException("unexpected element: " + childName + " (plan: " + plan + ")");
			}
			throw new PlanException("unexpected node: " + child);
		}
		for (Element aliasElt : aliasParams)
			setAliasParam(logger, aliasElt, result);
		return result;
	}

	private void setAliasParam(Logger logger, Element elt, Sequence<T> sequence) throws PlanException, ParameterException {
		String name = getAttribute(elt, NAME_ATTRIBUTE_NAME);
		Sequence.CompositeParamHandler<T> ph = sequence.createAliasParam(name);
		for (Node child : XMLUtils.childrenNodes(elt)) {
			if (checkEmptyText(child))
				continue;
			if (child instanceof Element) {
				Element childElement = (Element) child;
				String childName = childElement.getTagName();
				if (ALIAS_ELEMENT_NAME.equals(childName)) {
					String modulePath = getAttribute(childElement, MODULE_PATH_ATTRIBUTE_NAME);
					String paramName = getAttribute(childElement, PARAM_ATTRIBUTE_NAME);
					ph.addParamHandler(modulePath, paramName);
					continue;
				}
				throw new PlanException("unexpected element: " + childName);				
			}
			throw new PlanException("unexpected node: " + child);
		}
		for (ParamHandler<T> c : ph.getAllParamHandlers()) {
			if (c.isDeprecated()) {
				Module<T> module = c.getModule();
				String moduleClass = module.getModuleClass();
				String shortModuleClass = moduleClass.substring(moduleClass.lastIndexOf('.') + 1);
				logger.severe("parameter " + c.getName() + " in " + module.getPath() + " (" + shortModuleClass + ") is DEPRECATED (alias " + ph.getName() + ")");
				logger.severe("this parameter might not be supported in future versions");
			}
		}
	}
	
	private static boolean checkEmptyText(Node node) throws PlanException {
		if (node instanceof Text) {
			String text = node.getNodeValue().trim();
			if (text.isEmpty())
				return true;
			throw new PlanException("unexpected text: " + text);
		}
		return false;
	}
	
	private Module<T> getModuleInstance(String moduleClass) throws ServiceException {
		try {
			return moduleFactory.getServiceByAlias(moduleClass);
		}
		catch (UnsupportedServiceException use) {
			throw new UnsupportedServiceException("unknown module class: " + moduleClass);
		}
	}
	
	private Module<T> loadModuleOrSequence(Logger logger, String source, Element elt) throws PlanException, ConverterException, ServiceException, SAXException, IOException, URISyntaxException, ModuleException {
		if (elt.hasAttribute(CLASS_ATTRIBUTE_NAME)) {
			return loadModule(logger, source, elt, null);
		}
		for (String key : SOURCE_ATTRIBUTE_NAMES) {
			if (elt.hasAttribute(key)) {
				return importPlan(logger, elt);
			}
		}
		return loadSequence(logger, source, elt, null, false);
	}

	private Module<T> loadModule(Logger logger, String source, Element elt, String id) throws PlanException, ParameterException, ConverterException, ServiceException, SAXException, IOException, URISyntaxException {
		String moduleClass = getAttribute(elt, CLASS_ATTRIBUTE_NAME);
		Module<T> result = getModuleInstance(moduleClass);
		setModuleId(logger, result, elt, id);
		result.setCreatorNameFeature(creatorNameFeature);
		setDefaultParams(logger, source, result);
		setModuleParams(logger, source, elt, result);
		return result;
	}
	
	private void setModuleParams(Logger logger, String source, Element parent, Module<T> module) throws PlanException, ParameterException, UnsupportedServiceException, ConverterException, SAXException, IOException, URISyntaxException {
		for (Node child : XMLUtils.childrenNodes(parent)) {
			if (child instanceof Comment)
				continue;
			if (checkEmptyText(child))
				continue;
			if (child instanceof Element) {
				Element childElement = (Element) child;
				setParam(logger, source, childElement, module);
				continue;
			}
			throw new PlanException("unexpected node: " + child);
		}
	}
	
	private void setDefaultParams(Logger logger, String source, Module<T> module) throws ParameterException, UnsupportedServiceException, PlanException, ConverterException, SAXException, IOException, URISyntaxException {
		String moduleClass = module.getModuleClass();
		if (!defaultParamValues.containsKey(moduleClass)) {
			return;
		}
		for (Element elt : defaultParamValues.get(moduleClass)) {
			setParam(logger, source, elt, module);
		}
	}

	private static String getAttribute(Element elt, String name) throws PlanException {
		if (elt.hasAttribute(name))
			return elt.getAttribute(name);
		throw new PlanException("missing attribute: " + name);
	}

	private Module<T> importPlan(Logger logger, Element elt) throws PlanException, ModuleException, SAXException, IOException, ServiceException, ConverterException, URISyntaxException {
		String sourceString = XMLUtils.attributeOrValue(elt, " ", SOURCE_ATTRIBUTE_NAMES);
		String baseDir = getBaseDir(elt);
		Module<T> result = loadSource(logger, sourceString, baseDir);
		setModuleId(logger, result, elt, null);
		if (!XMLUtils.childrenElements(elt).isEmpty()) {
			setModuleParams(logger, sourceString, elt, result);
		}
		return result;
	}

	private ParamConverter getParamConverterInstance(String name, Class<?> paramType, boolean outputFeed, String baseDir) throws UnsupportedServiceException, PlanException {
		try {
			ParamConverter result = converterFactory.getService(paramType);
			if (outputFeed) {
				if (baseDir != null) {
					throw new PlanException("incompatible options");
				}
				if (outputDir == null) {
					result.setInputDirs(Collections.emptyList());
				}
				else {
					result.setInputDirs(Collections.singletonList(outputDir));
				}
				result.setOutputDir(outputDir);
			}
			else {
				if (baseDir != null) {
					result.setInputDirs(Collections.singletonList(baseDir));
					result.setOutputDir(baseDir);
				}
				else {
					result.setInputDirs(inputDirs);
					result.setOutputDir(outputDir);
				}
			}
			result.setResourceBases(resourceBases);
			return result;
		}
		catch (UnsupportedServiceException use) {
		    throw new UnsupportedServiceException("could not find converter for param '" + name + "' of type: " + paramType.getCanonicalName(), use);
		}
	}
	
	public StreamFactory getStreamFactory(String baseDir) {
		StreamFactory result = new StreamFactory();
		result.setInputDirs(baseDir == null ? inputDirs : Collections.singletonList(baseDir));
		result.setResourceBases(resourceBases);
		return result;
	}
	
	private String getBaseDir(Element elt) throws PlanException {
		if (elt.hasAttribute(BASE_DIR_ATTRIBUTE_NAME)) {
			String baseDirName = elt.getAttribute(BASE_DIR_ATTRIBUTE_NAME);
			if (!baseDirs.containsKey(baseDirName)) {
				throw new PlanException("undefined base directory named " + baseDirName);
			}
			return baseDirs.get(baseDirName);
		}
		return null;
	}
	
	public void setParam(Logger logger, String sourceName, Element elt, Module<T> module) throws PlanException, ParameterException, ConverterException, UnsupportedServiceException, SAXException, IOException, URISyntaxException {
		String paramName = elt.getTagName();
		ParamHandler<T> paramHandler = module.getParamHandler(paramName);
		if (paramHandler.isDeprecated()) {
			String moduleClass = module.getModuleClass();
			String shortModuleClass = moduleClass.substring(moduleClass.lastIndexOf('.') + 1);
			logger.severe("parameter " + paramName + " in " + module.getPath() + " (" + shortModuleClass + ") is DEPRECATED");
			logger.severe("this parameter might not be supported in future versions");
		}
		boolean outputFeed = XMLUtils.getBooleanAttribute(elt, OUTPUT_FEED_ATTRIBUTE_NAME, false);
		if (outputFeed) {
			paramHandler.setInhibitCheck(true);
		}
		String baseDir = getBaseDir(elt);
		Class<?> paramType = paramHandler.getType();
		ParamConverter paramConverter = getParamConverterInstance(paramName, paramType, outputFeed, baseDir);
		if (elt.hasAttribute(LOAD_FILE_ATTRIBUTE_NAME)) {
			StreamFactory sf = getStreamFactory(baseDir);
			String sourceString = elt.getAttribute(LOAD_FILE_ATTRIBUTE_NAME);
			SourceStream source = sf.getSourceStream(sourceString);
			try (InputStream is = source.getInputStream()) {
				Document doc = docBuilder.parse(is);
				elt = doc.getDocumentElement();
				sourceName = source.getStreamName(is);
			}
		}
		try {
			Object value = paramConverter.convert(elt);
//			System.err.println("paramName = " + paramName);
//			System.err.println("value = " + value);
			paramHandler.setValue(value);
			paramHandler.setParamSourceName(sourceName);
		}
		catch (ConverterException e) {
			throw new ConverterException("in module " + module.getPath() + ", parameter " + paramName + ": " + e.getMessage(), e);
		}
	}
}
