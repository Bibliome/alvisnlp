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


package fr.inra.maiage.bibliome.alvisnlp.core.module.lib;

import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import fr.inra.maiage.bibliome.alvisnlp.core.documentation.Documentation;
import fr.inra.maiage.bibliome.alvisnlp.core.documentation.ResourceDocumentation;
import fr.inra.maiage.bibliome.alvisnlp.core.module.Annotable;
import fr.inra.maiage.bibliome.alvisnlp.core.module.Module;
import fr.inra.maiage.bibliome.alvisnlp.core.module.ModuleException;
import fr.inra.maiage.bibliome.alvisnlp.core.module.ModuleVisitor;
import fr.inra.maiage.bibliome.alvisnlp.core.module.ParamHandler;
import fr.inra.maiage.bibliome.alvisnlp.core.module.ProcessingContext;
import fr.inra.maiage.bibliome.alvisnlp.core.module.Sequence;
import fr.inra.maiage.bibliome.alvisnlp.core.module.TimerCategory;
import fr.inra.maiage.bibliome.alvisnlp.core.module.UnexpectedParameterException;
import fr.inra.maiage.bibliome.util.Strings;
import fr.inra.maiage.bibliome.util.Timer;
import fr.inra.maiage.bibliome.util.files.OutputFile;
import fr.inra.maiage.bibliome.util.xml.XMLUtils;

/**
 * Base class for module implementations.
 * 
 * @author rbossy
 */
public abstract class ModuleBase<T extends Annotable> implements Module<T> {
	@SuppressWarnings("serial")
	public static final Level HIGHLIGHT = new Level("HIGHLIGHT", Level.INFO.intValue() + 1) {};
	public static final String UNKNOWN_SOURCE = "<<unknown source>>";

    private final Map<String,ParamHandler<T>> paramHandlers  = new LinkedHashMap<String,ParamHandler<T>>();
    private final String resourceBundleName;
    private Documentation documentation;
    private boolean beta;
    private Class<?>[] useInstead;
    private OutputFile dumpFile = null;
    private String id = null;
    private Sequence<T> sequence = null;
	private String creatorNameFeature;

    /**
     * Instantiates a new module base.
     * This constructor assumes the class or one ancestor class has an annotation of type AlvisNLPModule.
     */
    protected ModuleBase() {
        resourceBundleName = searchResourceBundleName();
        documentation = new ResourceDocumentation(resourceBundleName);
        try {
            searchParams(getClass());
        }
        catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }
    
    private Class<?> searchAlvisNLPModuleAnnotation() {
        for (Class<?> klass = getClass(); klass != null; klass = klass.getSuperclass()) {
            AlvisNLPModule annot = klass.getAnnotation(AlvisNLPModule.class);
            if (annot != null) {
            	beta = annot.beta();
            	useInstead = annot.obsoleteUseInstead();
				return klass;
			}
        }
        return null;
    }
    
    private String searchResourceBundleName() {
    	Class<?> klass = searchAlvisNLPModuleAnnotation();
    	if (klass == null) {
			throw new RuntimeException("class " + getClass().getCanonicalName() + " does not extend a module class");
		}
    	String docResourceBundle = klass.getAnnotation(AlvisNLPModule.class).docResourceBundle();
    	if (docResourceBundle.isEmpty()) {
			return klass.getCanonicalName() + "Doc";
		}
    	return docResourceBundle;
    }

    private void searchParams(Class<?> klass) throws NoSuchMethodException {
        if (klass == null) {
			return;
		}
        for (Method method : klass.getMethods()) {
            Param annot = method.getAnnotation(Param.class);
            if (annot != null) {
                ParamHandler<T> ph = createParamHandler(method, annot);
		String name = ph.getName();
		if (!(paramHandlers.containsKey(name) && method.isBridge())) {
		    paramHandlers.put(ph.getName(), ph);
		}
            }
        }
    }

    /**
     * Creates a parameter handler.
     * Override this method to use parameter handlers for a specific parameter type.
     * @param getter
     * @param annot
     * @throws NoSuchMethodException
     */
    private ParamHandler<T> createParamHandler(Method getter, Param annot) throws NoSuchMethodException {
        return new ParamHandlerBase<T>(this, getter, annot);
    }

    @Override
    public void clean() {
    }

    @Override
    public Collection<ParamHandler<T>> getAllParamHandlers() {
        List<ParamHandler<T>> result = new ArrayList<ParamHandler<T>>(paramHandlers.values());
        Collections.sort(result, nameComparator);
        return Collections.unmodifiableCollection(result);
    }
    
    private final static Comparator<ParamHandler<? extends Annotable>> nameComparator = new Comparator<ParamHandler<? extends Annotable>>() {
        @Override
        public int compare(ParamHandler<?> a, ParamHandler<?> b) {
            return a.getName().compareTo(b.getName());
        }
    };

    @Override
    public OutputFile getDumpFile() {
        return dumpFile;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
	public String getModuleClass() {
    	Class<?> klass = getClass();
    	return klass.getCanonicalName();
	}

	@Override
    public Logger getLogger(ProcessingContext<T> ctx) {
    	return ctx.getLogger(Strings.join(getAncestors("alvisnlp"), '.'));
    }

    @Override
    public ParamHandler<T> getParamHandler(String name) throws UnexpectedParameterException {
        if (paramHandlers.containsKey(name)) {
			return paramHandlers.get(name);
		}
        throw new UnexpectedParameterException(this, name);
    }

    @Override
    public String getPath() {
    	return Strings.join(getAncestors(null), '.');
    }

    private List<String> getAncestors(String prefix) {
    	List<String> result = new ArrayList<String>();
    	for (Module<T> m = this; m != null; m = m.getSequence()) {
			result.add(m.getId());
		}
    	if (prefix != null) {
			result.add(prefix);
		}
    	Collections.reverse(result);
    	return Collections.unmodifiableList(result);
    }
    
    @Override
    public Sequence<T> getSequence() {
        return sequence;
    }

	public File getTempDir(ProcessingContext<T> ctx) {
		return ctx.getTempDir(this);
	}

	@Override
    public void setDumpFile(OutputFile file) {
        dumpFile = file;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }

    @Override
    public void setSequence(Sequence<T> sequence) {
        if (this.sequence != null) {
			sequence.removeModule(this);
		}
        this.sequence = sequence;
    }

    /**
     * Returns the name of the resource bundle for the user documentation.
     * By default it is the name of the class with "Doc" appended.
     * This default can be overridden by AlvisNLPModule.docResourceBundle().
     * 
     * @return the resource bundle name
     */
    public String getResourceBundleName() {
        return resourceBundleName;
    }

    @Override
    public String toString() {
    	return getPath();
    }
    
    @Override
    public void init(ProcessingContext<T> ctx) throws ModuleException {
    	Class<?> klass = searchAlvisNLPModuleAnnotation();
    	if (klass == null)
			throw new RuntimeException("class does not extend a module class");
    	AlvisNLPModule annot = klass.getAnnotation(AlvisNLPModule.class);
    	if (annot.beta())
    		getLogger(ctx).warning("this module is EXPERIMENTAL");
    	Class<?>[] better = annot.obsoleteUseInstead();
    	if (better.length == 0)
    		return;
    	Logger logger = getLogger(ctx);
    	logger.severe("this module class is DEPRECATED, use instead: " + Strings.joinStrings(Arrays.asList(better), ", "));
    	logger.severe("support for this module class may be DSCONTINUED WITHOUT NOTICE");
    }

	private static final XPathExpression alvisnlpDocExpression;
	private static final XPathExpression synopsisExpression;
	private static final XPathExpression moduleDocExpression;
	private static final XPathExpression descriptionExpression;
	private static final XPathExpression paramDocExpression;
	static {
		try {
			alvisnlpDocExpression = XMLUtils.xp.compile("alvisnlp-doc");
			synopsisExpression = XMLUtils.xp.compile("synopsis");
			moduleDocExpression = XMLUtils.xp.compile("module-doc|plan-doc");
			descriptionExpression = XMLUtils.xp.compile("description");
			paramDocExpression = XMLUtils.xp.compile("param-doc");
		}
		catch (XPathExpressionException e) {
			throw new RuntimeException(e);
		}
	}
	
    private class ModuleBaseDocumentation implements Documentation {
    	private ModuleBaseDocumentation() {
		}

		private void supplementAlvisNLPDocElement(Document doc) throws XPathExpressionException {
    		Element alvisnlpDocElt = XMLUtils.evaluateElement(alvisnlpDocExpression, doc);
    		if (alvisnlpDocElt == null) {
    			alvisnlpDocElt = XMLUtils.createElement(doc, doc, 0, "alvisnlp-doc");
    		}
    		ensureAttribute(alvisnlpDocElt, "author", "");
    		ensureAttribute(alvisnlpDocElt, "date", "");
    		String klass = getModuleClass();
    		ensureAttribute(alvisnlpDocElt, "target", klass);
    		ensureAttribute(alvisnlpDocElt, "short-target", klass.substring(klass.lastIndexOf('.') + 1));
    		alvisnlpDocElt.setAttribute("beta", Boolean.toString(beta));
    		if (useInstead.length > 0) {
    			alvisnlpDocElt.setAttribute("use-instead", useInstead[0].getCanonicalName());
    		}
    		supplementSynopsisElement(alvisnlpDocElt);
    		supplementModuleDocElement(alvisnlpDocElt);
    	}
    	
    	private void ensureAttribute(Element elt, String name, String value) {
    		if (!elt.hasAttribute(name)) {
    			elt.setAttribute(name, value);
    		}
    	}

    	private void supplementSynopsisElement(Element alvisnlpDocElt) throws XPathExpressionException {
    		Element synopsisElt = XMLUtils.evaluateElement(synopsisExpression, alvisnlpDocElt);
    		if (synopsisElt == null) {
    			Document doc = alvisnlpDocElt.getOwnerDocument();
    			synopsisElt = undocumented(doc, alvisnlpDocElt, "synopsis");
    		}
    	}
    	
    	private void supplementModuleDocElement(Element alvisnlpDocElt) throws XPathExpressionException {
    		Element moduleDocElt = XMLUtils.evaluateElement(moduleDocExpression, alvisnlpDocElt);
    		if (moduleDocElt == null) {
    			Document doc = alvisnlpDocElt.getOwnerDocument();
    			moduleDocElt = XMLUtils.createElement(doc, alvisnlpDocElt, 0, "module-doc");
    		}
    		supplementDescriptionElement(moduleDocElt);
    		supplementParamDocElements(moduleDocElt);
    	}

    	private void supplementDescriptionElement(Element moduleDocElt) throws XPathExpressionException {
    		Element descriptionElt = XMLUtils.evaluateElement(descriptionExpression, moduleDocElt);
    		if (descriptionElt == null) {
    			Document doc = moduleDocElt.getOwnerDocument();
    			descriptionElt = undocumented(doc, moduleDocElt, "description");
    		}
    	}
    	
    	private Element undocumented(Document doc, Element parent, String name) {
    		Element result = XMLUtils.createElement(doc, parent, 0, name);
    		XMLUtils.createElement(doc, result, 0, "p", "UNDOCUMENTED");
    		return result;
    	}

    	private void supplementParamDocElements(Element moduleDocElt) throws XPathExpressionException {
    		Map<String,Element> result = new LinkedHashMap<String,Element>();
    		Collection<Element> toRemove = new ArrayList<Element>();
    		for (Element e : XMLUtils.evaluateElements(paramDocExpression, moduleDocElt)) {
    			if (!e.hasAttribute("name")) {
    				toRemove.add(e);
    				continue;
    			}
    			String name = e.getAttribute("name");
    			if (result.containsKey(name)) {
    				toRemove.add(e);
    				continue;
    			}
    			result.put(name, e);
    		}
    		Document doc = moduleDocElt.getOwnerDocument();
    		for (ParamHandler<T> ph : getAllParamHandlers()) {
    			String name = ph.getName();
    			Element paramDocElt;
    			if (result.containsKey(name)) {
    				paramDocElt = result.remove(name);
    			}
    			else {
    				paramDocElt = undocumented(doc, moduleDocElt, "param-doc");
    				paramDocElt.setAttribute("name", name);
    			}
    			paramDocElt.setAttribute("mandatory", Boolean.toString(ph.isMandatory()));
    			Class<?> type = ph.getType();
    			paramDocElt.setAttribute("type", type.getCanonicalName());
    			paramDocElt.setAttribute("short-type", type.getSimpleName());
    			if (ph.isSet()) {
    				Object value = ph.getValue();
    				String sValue;
    				if (type.isArray()) {
    					sValue = Strings.joinStrings((Object[]) value, ',');
    				}
    				else {
    					sValue = value.toString();
    				}
    				paramDocElt.setAttribute("default-value", sValue);
    			}
    			String nameType = ph.getNameType();
    			if (nameType != null) {
    				paramDocElt.setAttribute("name-type", nameType);
    			}
    		}
    		toRemove.addAll(result.values());
    		for (Element e : toRemove) {
    			moduleDocElt.removeChild(e);
    		}
    	}
    	
		@Override
		public Document getDocument() {
			Document result = documentation.getDocument();
			try {
				supplementAlvisNLPDocElement(result);
			}
			catch (XPathExpressionException e) {
				throw new RuntimeException(e);
			}
			return result;
		}

		@Override
		public Document getDocument(Locale locale) {
			Document result = documentation.getDocument(locale);
			try {
				supplementAlvisNLPDocElement(result);
			}
			catch (XPathExpressionException e) {
				throw new RuntimeException(e);
			}
			return result;
		}
    }

	@Override
	public Documentation getDocumentation() {
		return new ModuleBaseDocumentation();
	}

	protected void setDocumentation(Documentation documentation) {
		this.documentation = documentation;
	}

	@Override
	public Timer<TimerCategory> getTimer(ProcessingContext<T> ctx) {
		Timer<TimerCategory> parentTimer = sequence == null ? ctx.getTimer() : sequence.getTimer(ctx);
		Timer<TimerCategory> result = parentTimer.getChild(id);
		if (result != null)
			return result;
		return parentTimer.newChild(id, TimerCategory.MODULE);
	}

	@Override
	public Timer<TimerCategory> getTimer(ProcessingContext<T> ctx, String task, TimerCategory category, boolean start) {
		Timer<TimerCategory> moduleTimer = getTimer(ctx);
		Timer<TimerCategory> result = moduleTimer.getChild(task);
		if (result == null)
			result = moduleTimer.newChild(task, category);
		else {
			if (category != result.getCategory())
				throw new IllegalArgumentException();
		}
		if (start)
			result.start();
		return result;
	}

	@Override
	public Module<T> getModuleByPath(String modulePath) {
		return null;
	}

	@Override
	public boolean isBeta() {
		return beta;
	}

	@Override
	public Class<?>[] getUseInstead() {
		return useInstead;
	}
	
	@Override
	public String getCreatorNameFeature() {
		return creatorNameFeature;
	}

	@Override
	public void setCreatorNameFeature(String nameFeature) {
		this.creatorNameFeature = nameFeature;
	}
	
	@Override
	public String getCreatorName() {
		return getPath();
	}

	@Override
	public <P> void accept(ModuleVisitor<T,P> visitor, P param) throws ModuleException {
		visitor.visitModule(this, param);
	}

	@Override
	public boolean testProcess(ProcessingContext<T> ctx, T corpus) throws ModuleException {
		if (ctx.isResumeMode() && corpus.wasProcessedBy(getPath())) {
			getLogger(ctx).info("skipping (resume)");
			return false;
		}
		return true;
	}

	@Override
	public String getModuleSourceName() {
		if (sequence == null) {
			return UNKNOWN_SOURCE;
		}
		return sequence.getModuleSourceName();
	}
}
