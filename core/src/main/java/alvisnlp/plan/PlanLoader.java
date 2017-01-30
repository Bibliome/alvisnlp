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


package alvisnlp.plan;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilder;

import org.bibliome.util.Strings;
import org.bibliome.util.service.ServiceException;
import org.bibliome.util.service.UnsupportedServiceException;
import org.bibliome.util.streams.SourceStream;
import org.bibliome.util.streams.StreamFactory;
import org.bibliome.util.xml.XMLUtils;
import org.w3c.dom.Comment;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.Text;
import org.xml.sax.SAXException;

import alvisnlp.converters.ConverterException;
import alvisnlp.converters.ParamConverter;
import alvisnlp.converters.ParamConverterFactory;
import alvisnlp.documentation.ConstantDocumentation;
import alvisnlp.factory.ModuleFactory;
import alvisnlp.module.Annotable;
import alvisnlp.module.Module;
import alvisnlp.module.ModuleException;
import alvisnlp.module.ParamHandler;
import alvisnlp.module.ParameterException;
import alvisnlp.module.Sequence;

/**
 * A plan loader reads plans from XML files.
 * @author rbossy
 *
 * @param <T>
 */
public class PlanLoader<T extends Annotable> {
	/** Tag name for plan (top-level). */
	public static final String PLAN_ELEMENT_NAME = "alvisnlp-plan";
	
	/** Tag name for module instance. */
	public static final String MODULE_ELEMENT_NAME = "module";
	
	/** Tag name for module sequence. */
	public static final String SEQUENCE_ELEMENT_NAME = "sequence";
	
	/** Attribute name for sequence and module instance identifiers. */
	public static final String ID_ATTRIBUTE_NAME = "id";
	
	/** Attribute name for module instance class. */
	public static final String CLASS_ATTRIBUTE_NAME = "class";

	public static final String ACTIVE_PARAM_ELEMENT_NAME = "active";

	public static final String SELECT_PARAM_ELEMENT_NAME = "select";

	public static final String USER_FUNCTIONS_PARAM_ELEMENT_NAME = "userFunctions";
	
	public static final String SHELL_ELEMENT_NAME = "shell";
	
	/** Tag name for module parameter. */
	public static final String PARAM_ELEMENT_NAME = "param";
	
	/** Attribute name for module parameters and sequence parameter alias. */
	public static final String NAME_ATTRIBUTE_NAME = "name";
	
	/** Attribute name for file check inhibition. */
	public static final String INHIBIT_FILE_CHECK_ATTRIBUTE_NAME = "inhibitFileCheck";
	
	/** Tag name for alias target parameter. */
	public static final String ALIAS_ELEMENT_NAME = "alias";
	
	/** Attribute name for alias target parameter module path. */
	public static final String MODULE_PATH_ATTRIBUTE_NAME = "module";
	
	/** Attribute name for alias target parameter name. */
	public static final String PARAM_ATTRIBUTE_NAME = "param";
	
	/** Tag name for external plan import. */
	public static final String IMPORT_ELEMENT_NAME = "import";
	
	/** Attribute name for external plan import path. */
	public static final String SOURCE_ATTRIBUTE_NAME = "file";

	private static final String[] ALTERNATE_SOURCE_ATTRIBUTE_NAMES = { "source", "resource", "href" };

	public static final String LOAD_FILE_ATTRIBUTE_NAME = "load";

	public static final String DOCUMENTATION_ELEMENT_NAME = "alvisnlp-doc";

	private static final String LOCALE_ATTRIBUTE_NAME = "locale";

	private final ModuleFactory<T> moduleFactory;
	private final ParamConverterFactory converterFactory;
	private final Map<String,List<Element>> defaultParamValues;
	private final List<String> inputDirs;
	private final String outputDir;
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
	public PlanLoader(ModuleFactory<T> moduleFactory, ParamConverterFactory converterFactory, Document defaultParamValuesDoc, List<String> inputDirs, String outputDir, DocumentBuilder docBuilder, String creatorNameFeature, Map<String,String> customEntities) throws PlanException {
		super();
		this.moduleFactory = moduleFactory;
		this.converterFactory = converterFactory;
		this.defaultParamValues = buildDefaultParamValues(defaultParamValuesDoc);
		this.inputDirs = inputDirs;
		this.outputDir = outputDir;
		this.docBuilder = docBuilder;
		this.creatorNameFeature = creatorNameFeature;
		AlvisNLPEntityResolver entityResolver = new AlvisNLPEntityResolver(customEntities);
		docBuilder.setEntityResolver(entityResolver);
	}

	private static Map<String,List<Element>> buildDefaultParamValues(Document defaultParamValuesDoc) throws PlanException {
		Map<String,List<Element>> result = new HashMap<String,List<Element>>();
		if (defaultParamValuesDoc == null) {
			return result;
		}
		Element root = defaultParamValuesDoc.getDocumentElement();
		for (Element elt : XMLUtils.childrenElements(root)) {
			String tag = elt.getTagName();
			switch (tag) {
				case MODULE_ELEMENT_NAME: {
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
//	public Sequence<T> loadFile(Logger logger, File source) throws PlanException, SAXException, IOException, ModuleException, ServiceException, ConverterException, URISyntaxException {
//		Document doc = docBuilder.parse(source);
//		return loadDocument(logger, source.getCanonicalPath(), doc);
//	}

	public Sequence<T> loadSource(Logger logger, SourceStream source) throws SAXException, IOException, PlanException, ModuleException, ServiceException, ConverterException, URISyntaxException {
		try (InputStream is = source.getInputStream()) {
			Document doc = docBuilder.parse(is);
			String name = source.getStreamName(is);
			return loadDocument(logger, name, doc);
		}
	}
	
	public Document parseDoc(SourceStream source) throws SAXException, IOException {
		try (InputStream is = source.getInputStream()) {
			return docBuilder.parse(is);
		}
	}

	public Document parseDoc(String source) throws SAXException, IOException, URISyntaxException {
		StreamFactory sf = getStreamFactory();
		SourceStream sourceStream = sf.getSourceStream(source);
		return parseDoc(sourceStream);
	}
	
	public Sequence<T> loadSource(Logger logger, String sourceString) throws SAXException, IOException, PlanException, ModuleException, ServiceException, ConverterException, URISyntaxException {
		StreamFactory sf = getStreamFactory();
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
		logger.config("loading plan from " + source);
		Element elt = doc.getDocumentElement();
		if (!PLAN_ELEMENT_NAME.equals(elt.getTagName()))
			throw new PlanException("expected element " + PLAN_ELEMENT_NAME + ", got " + elt.getTagName());
		Sequence<T> result = loadSequence(logger, doc.getDocumentElement(), true);
		logger.fine("finished loading " + source);
		return result;
	}
	
	private static <T extends Annotable> void setModuleId(Logger logger, Module<T> module, Element elt) throws PlanException {
		String id;
		String tag = elt.getTagName();
		if (tag.equals(MODULE_ELEMENT_NAME) || elt.hasAttribute(ID_ATTRIBUTE_NAME)) {
			id = getAttribute(elt, ID_ATTRIBUTE_NAME);
		}
		else {
			id = tag;
		}
		if (id.isEmpty())
			throw new PlanException("missing id");
		module.setId(id);
		if (elt.hasAttribute("dump")) {
			String dumpPath = elt.getAttribute("dump");
			logger.warning("setting dump file inside the plan is obsolete, use -dumpModule instead");
			module.setDumpFile(new File(dumpPath));
		}
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

	private Sequence<T> loadSequence(Logger logger, Element elt, boolean plan) throws PlanException, ModuleException, ServiceException, ConverterException, SAXException, IOException, URISyntaxException {
		Sequence<T> result = moduleFactory.newSequence();
		ConstantDocumentation documentation = null;
		setModuleId(logger, result, elt);
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
				if (MODULE_ELEMENT_NAME.equals(childName)) {
					Module<T> module = loadModule(logger, childElement);
					result.appendModule(module);
					continue;
				}
				if (SEQUENCE_ELEMENT_NAME.equals(childName)) {
					Sequence<T> sequence = loadSequence(logger, childElement, false);
					result.appendModule(sequence);
					continue;
				}
				if (IMPORT_ELEMENT_NAME.equals(childName)) {
					Module<T> module = importPlan(logger, childElement);
					result.appendModule(module);
					continue;
				}
				if (ACTIVE_PARAM_ELEMENT_NAME.equals(childName)) {
					setParam(childElement, result);
					continue;
				}
				if (SELECT_PARAM_ELEMENT_NAME.equals(childName)) {
					setParam(childElement, result);
					continue;
				}
				if (USER_FUNCTIONS_PARAM_ELEMENT_NAME.equals(childName)) {
					setParam(childElement, result);
					continue;
				}
				if (SHELL_ELEMENT_NAME.equals(childName)) {
					String id = "shell_" + (++nShells);
					childElement.setAttribute("id", id);
					String shellModule;
					shellModule = moduleFactory.getShellModule();
					childElement.setAttribute("class", shellModule);
					Module<T> module = loadModule(logger, childElement);
					result.appendModule(module);
					continue;
				}
				if (plan && PARAM_ELEMENT_NAME.equals(childName)) {
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
				Module<T> module = loadModuleOrSequence(logger, childElement);
				result.appendModule(module);
				continue;
//				throw new PlanException("unexpected element: " + childName + " (plan: " + plan + ")");
			}
			throw new PlanException("unexpected node: " + child);
		}
		for (Element aliasElt : aliasParams)
			setAliasParam(aliasElt, result);
		return result;
	}

	private void setAliasParam(Element elt, Sequence<T> sequence) throws PlanException, ParameterException {
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
	
	private Module<T> loadModuleOrSequence(Logger logger, Element elt) throws PlanException, ConverterException, ServiceException, SAXException, IOException, URISyntaxException, ModuleException {
		if (elt.hasAttribute(CLASS_ATTRIBUTE_NAME)) {
			return loadModule(logger, elt);
		}
		return loadSequence(logger, elt, false);
	}

	private Module<T> loadModule(Logger logger, Element elt) throws PlanException, ParameterException, ConverterException, ServiceException, SAXException, IOException, URISyntaxException {
		String moduleClass = getAttribute(elt, CLASS_ATTRIBUTE_NAME);
		Module<T> result = getModuleInstance(moduleClass);
		setModuleId(logger, result, elt);
		result.setCreatorNameFeature(creatorNameFeature);
		setDefaultParams(result);
		setModuleParams(elt, result);
		return result;
	}
	
	private void setModuleParams(Element parent, Module<T> module) throws PlanException, ParameterException, UnsupportedServiceException, ConverterException, SAXException, IOException, URISyntaxException {
		for (Node child : XMLUtils.childrenNodes(parent)) {
			if (child instanceof Comment)
				continue;
			if (checkEmptyText(child))
				continue;
			if (child instanceof Element) {
				Element childElement = (Element) child;
				setParam(childElement, module);
				continue;
			}
			throw new PlanException("unexpected node: " + child);
		}
	}
	
	private void setDefaultParams(Module<T> module) throws ParameterException, UnsupportedServiceException, PlanException, ConverterException, SAXException, IOException, URISyntaxException {
		String moduleClass = module.getModuleClass();
		if (!defaultParamValues.containsKey(moduleClass)) {
			return;
		}
		for (Element elt : defaultParamValues.get(moduleClass)) {
			setParam(elt, module);
		}
	}

	private static String getAttribute(Element elt, String name) throws PlanException {
		if (elt.hasAttribute(name))
			return elt.getAttribute(name);
		throw new PlanException("missing attribute: " + name);
	}
	
	private Module<T> importPlan(Logger logger, Element elt) throws PlanException, ModuleException, SAXException, IOException, ServiceException, ConverterException, URISyntaxException {
		String sourceString = XMLUtils.attributeOrValue(elt, SOURCE_ATTRIBUTE_NAME, ALTERNATE_SOURCE_ATTRIBUTE_NAMES);
		Module<T> result = loadSource(logger, sourceString);
		if (!XMLUtils.childrenElements(elt).isEmpty()) {
			setModuleParams(elt, result);
		}
		return result;
	}

	private ParamConverter getParamConverterInstance(Class<?> paramType) throws UnsupportedServiceException {
		try {
			ParamConverter result = converterFactory.getService(paramType);
			result.setInputDirs(inputDirs);
			result.setOutputDir(outputDir);
			return result;
		}
		catch (UnsupportedServiceException use) {
			throw new UnsupportedServiceException("could not find converter for type: " + paramType.getCanonicalName());
		}
	}
	
	public StreamFactory getStreamFactory() {
		StreamFactory result = new StreamFactory();
		result.setInputDirs(inputDirs);
		return result;
	}
	
	public void setParam(Element elt, Module<T> module) throws PlanException, ParameterException, ConverterException, UnsupportedServiceException, SAXException, IOException, URISyntaxException {
		String eltName = elt.getTagName();
		String paramName;
		if (eltName.equals(PARAM_ELEMENT_NAME))
			paramName = getAttribute(elt, NAME_ATTRIBUTE_NAME);
		else
			paramName = eltName;
		ParamHandler<T> paramHandler = module.getParamHandler(paramName);
		if (elt.hasAttribute("inhibitCheck")) {
			boolean inhibitCheck = Strings.getBoolean(elt.getAttribute("inhibitCheck"));
			paramHandler.setInhibitCheck(inhibitCheck);
		}
		Class<?> paramType = paramHandler.getType();
		ParamConverter paramConverter = getParamConverterInstance(paramType);
		if (elt.hasAttribute(LOAD_FILE_ATTRIBUTE_NAME)) {
			String sourceString = elt.getAttribute(LOAD_FILE_ATTRIBUTE_NAME);
			StreamFactory sf = getStreamFactory();
			SourceStream source = sf.getSourceStream(sourceString);
			try (InputStream is = source.getInputStream()) {
				Document doc = docBuilder.parse(is);
				elt = doc.getDocumentElement();
			}
		}
		try {
			Object value = paramConverter.convert(elt);
			paramHandler.setValue(value);
		}
		catch (ConverterException e) {
			throw new ConverterException("in module " + module.getPath() + ", parameter " + paramName + ": " + e.getMessage(), e);
		}
	}
}
