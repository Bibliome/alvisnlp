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


package fr.inra.maiage.bibliome.alvisnlp.core.annotation;

import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.ElementFilter;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;
import javax.xml.transform.TransformerFactory;

import fr.inra.maiage.bibliome.alvisnlp.core.converters.lib.Converter;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Corpus;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.EvaluationContext;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.Evaluator;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.Expression;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.FunctionLibrary;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.Library;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.LibraryResolver;
import fr.inra.maiage.bibliome.alvisnlp.core.module.Module;
import fr.inra.maiage.bibliome.alvisnlp.core.module.ProcessingContext;
import fr.inra.maiage.bibliome.alvisnlp.core.module.lib.AlvisNLPModule;
import fr.inra.maiage.bibliome.alvisnlp.core.module.lib.ModuleBase;
import fr.inra.maiage.bibliome.util.GitInfo;

/**
 * A model context holds several constants used by AlvisNLPAnnotationProcessor.
 * @author rbossy
 *
 */
class ModelContext {
	private static final String isoDateFormat = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";
	
	private final Types typeUtils;
	final Elements elementUtils;
	private final Filer filer;
	private final Messager messager;
	private final TypeElement moduleAnnotation;
	private final TypeElement converterAnnotation;
	private final TypeElement libraryAnnotation;
	private ExecutableElement resolveFunction;
	private final TypeMirror moduleBase;
	private final TypeMirror functionLibrary;
	private final TypeMirror corpusModule;
	private final TypeMirror processingContext;
	final TypeMirror element;
	final TypeMirror evaluator;
	final TypeMirror expression;
	final TypeMirror string;
	final TypeMirror charSequence;
	final TypeMirror elementList;
	final TypeMirror elementIterator;
	final TypeMirror evaluationContext;
	final TypeMirror libraryResolver;
	private final TransformerFactory transformerFactory;
	private final Map<String,String> options;
	private final String generatorId;
	private final GitInfo gitInfo;
	private final Date date = new Date();

	/**
	 * Creates a new model context with the specified processing environment for the specified generator.
	 * @param procEnv
	 * @param generatorId
	 * @throws IOException 
	 */
	ModelContext(ProcessingEnvironment procEnv, String generatorId) throws IOException {
		super();
		typeUtils = procEnv.getTypeUtils();
		elementUtils = procEnv.getElementUtils();
        filer = procEnv.getFiler();
        messager = procEnv.getMessager();
        moduleAnnotation = elementUtils.getTypeElement(AlvisNLPModule.class.getCanonicalName());
        converterAnnotation = elementUtils.getTypeElement(Converter.class.getCanonicalName());
        libraryAnnotation = elementUtils.getTypeElement(Library.class.getCanonicalName());
        moduleBase = getModuleBase(typeUtils, elementUtils);
        TypeElement fle = getTypeElement(elementUtils, FunctionLibrary.class);
        for (ExecutableElement m : ElementFilter.methodsIn(fle.getEnclosedElements())) {
        	if (m.getSimpleName().toString().equals("resolveExpression")) {
        		resolveFunction = m;
        		break;
        	}
        }
        functionLibrary = fle.asType();
		corpusModule = getCorpusModule(typeUtils, elementUtils);
		element = getTypeMirror(elementUtils, fr.inra.maiage.bibliome.alvisnlp.core.corpus.Element.class);
		evaluationContext = getTypeMirror(elementUtils, EvaluationContext.class);
		libraryResolver = getTypeMirror(elementUtils, LibraryResolver.class);
		evaluator = getTypeMirror(elementUtils, Evaluator.class);
		expression = getTypeMirror(elementUtils, Expression.class);
		string = getTypeMirror(elementUtils, String.class);
		charSequence = getTypeMirror(elementUtils, CharSequence.class);
		processingContext = typeUtils.erasure(getTypeMirror(elementUtils, ProcessingContext.class));
		elementList = getElementList(typeUtils, elementUtils);
		elementIterator = getElementIterator(typeUtils, elementUtils);
		transformerFactory = TransformerFactory.newInstance();
        options = procEnv.getOptions();
        this.generatorId = generatorId;
        this.gitInfo = new GitInfo("/fr/inra/maiage/bibliome/alvisnlp/core/app/AlvisNLPGit.properties", "https://github.com/Bibliome/alvisnlp.git");
	}
	
	private static final TypeMirror getCorpusModule(Types typeUtils, Elements elementUtils) {
        TypeElement moduleTypeElement = elementUtils.getTypeElement(Module.class.getCanonicalName());
        return typeUtils.getDeclaredType(moduleTypeElement);
	}
    
    private static final TypeMirror getModuleBase(Types typeUtils, Elements elementUtils) {
        TypeElement moduleBaseTypeElement = elementUtils.getTypeElement(ModuleBase.class.getCanonicalName());
        return typeUtils.getDeclaredType(moduleBaseTypeElement);
    }
    
    private static final TypeMirror getElementList(Types typeUtils, Elements elementUtils) {
    	TypeElement listTypeElement = getTypeElement(elementUtils, List.class);
    	TypeMirror elementTypeMirror = getTypeMirror(elementUtils, fr.inra.maiage.bibliome.alvisnlp.core.corpus.Element.class);
    	return typeUtils.getDeclaredType(listTypeElement, elementTypeMirror);
    }
    
    private static final TypeMirror getElementIterator(Types typeUtils, Elements elementUtils) {
    	TypeElement listTypeElement = getTypeElement(elementUtils, Iterator.class);
    	TypeMirror elementTypeMirror = getTypeMirror(elementUtils, fr.inra.maiage.bibliome.alvisnlp.core.corpus.Element.class);
    	return typeUtils.getDeclaredType(listTypeElement, elementTypeMirror);
    }

    private static final TypeElement getTypeElement(Elements elementUtils, Class<?> klass) {
    	return elementUtils.getTypeElement(klass.getCanonicalName());
    }
    
    private static final TypeMirror getTypeMirror(Elements elementUtils, Class<?> klass) {
    	return getTypeElement(elementUtils, klass).asType();
    }

    private boolean isCorpusModule(TypeElement typeElement) {
    	return typeUtils.isAssignable(typeElement.asType(), corpusModule);
    }
    
    boolean isEvaluationContext(TypeMirror type) {
    	return typeUtils.isSameType(type, evaluationContext);
    }
    
    boolean isLibraryResolver(TypeMirror type) {
    	return typeUtils.isSameType(type, libraryResolver);
    }
    
    boolean isProcessingContext(TypeMirror type) {
    	return typeUtils.isSameType(typeUtils.erasure(type), processingContext);
    }
    
    boolean isElement(TypeMirror type) {
    	return typeUtils.isSameType(type, element);
    }
    
    boolean isString(TypeMirror type) {
    	return typeUtils.isSameType(type, string);
    }
    
    boolean isCharSequence(TypeMirror type) {
    	return typeUtils.isSameType(type, charSequence);
    }
    
    public boolean isElementIterator(TypeMirror typeMirror) {
    	return typeUtils.isAssignable(typeMirror, elementIterator);
    }
    
    public boolean isElementList(TypeMirror typeMirror) {
    	return typeUtils.isAssignable(typeMirror, elementList);
    }
    
    boolean isEvaluator(TypeMirror typeMirror) {
    	return typeUtils.isSameType(typeMirror, evaluator);
    }
    
    boolean isExpression(TypeMirror typeMirror) {
    	return typeUtils.isSameType(typeMirror, expression);
    }
    
    boolean isResolveFunctionImplementation(ExecutableElement method, TypeElement type) {
    	return elementUtils.overrides(method, resolveFunction, type) && !isAbstract(method);
    }
    
    /**
     * Returns for the specified type element a map from method names to their respective method element.
     * @param moduleElement
     */
    Map<String,ExecutableElement> getMethodsByName(TypeElement moduleElement) {
		Map<String,ExecutableElement> result = new LinkedHashMap<String,ExecutableElement>();
		for (ExecutableElement method : ElementFilter.methodsIn(elementUtils.getAllMembers(moduleElement)))
			result.put(method.getSimpleName().toString(), method);
		return result;
	}
    
    boolean isRightDataClassModule(TypeElement moduleElement) {
        TypeElement moduleTypeElement = elementUtils.getTypeElement(Module.class.getCanonicalName());
        DeclaredType expectedType = typeUtils.getDeclaredType(moduleTypeElement);
    	return typeUtils.isAssignable(moduleElement.asType(), expectedType);    	
    }

    String getModuleDataClass(TypeElement moduleElement) throws ModelException {
    	if (isCorpusModule(moduleElement))
    		return Corpus.class.getCanonicalName();
    	throw new ModelException("cannot determine model for module: " + moduleElement.getQualifiedName());
    }

    /**
     * Returns the target type of the specified type element representing a parameter converter class.
     * @param converterElement
     * @throws ModelException
     */
	String getTargetType(TypeElement converterElement) throws ModelException {
        for (AnnotationMirror am : converterElement.getAnnotationMirrors()) {
            Element ae = typeUtils.asElement(am.getAnnotationType());
            if (ae.equals(converterAnnotation))
                for (Map.Entry<? extends ExecutableElement,? extends AnnotationValue> e : am.getElementValues().entrySet())
                    if ("targetType".equals(e.getKey().getSimpleName().toString()))
                        return e.getValue().toString();
        }
        throw new ModelException("cannot read target type for converter: " + converterElement.getQualifiedName());
	}
	
	/**
	 * Returns either the specified type mirror represents a type assignable to fr.inra.maiage.bibliome.alvisnlp.core.module.lib.ModuleBase.
	 * @param type
	 */
	public boolean isModuleBase(TypeMirror type) {
		return typeUtils.isAssignable(type, moduleBase);
	}

	public boolean isFunctionLibrary(TypeMirror type) {
		return typeUtils.isAssignable(type, functionLibrary);
	}
	
	/**
	 * Returns either the specified element has the public modufier.
	 * @param element
	 */
	static boolean isPublic(Element element) {
		return element.getModifiers().contains(Modifier.PUBLIC);
	}
	
	boolean isPublic(TypeMirror type) {
		return isPublic(typeUtils.asElement(type));
	}
	
	boolean isProtected(TypeMirror type) {
		return isProtected(typeUtils.asElement(type));
	}
	
	static boolean isProtected(Element element) {
		return element.getModifiers().contains(Modifier.PROTECTED);
	}
	
	/**
	 * Returns either the specified element has the abstract modifier.
	 * @param element
	 */
	static boolean isAbstract(Element element) {
		return element.getModifiers().contains(Modifier.ABSTRACT);
	}

	static boolean isStatic(Element element) {
		return element.getModifiers().contains(Modifier.STATIC);
	}

	/**
	 * Returns this context XSLT transformer factory.
	 */
	public TransformerFactory getTransformerFactory() {
		return transformerFactory;
	}
	
	private InputStream getTemplate(String name) {
		// loads from same ClassLoader as this class
		return getClass().getResourceAsStream(name);
	}

	/**
	 * Returns the XSLT stylesheet for module class generation.
	 */
	public InputStream getModuleClassTemplate() {
		return getTemplate("ModuleClass.xslt");
	}
	
	/**
	 * Returns the XSLT stylesheet for module factory class generation.
	 */
	public InputStream getModuleFactoryClassTemplate() {
		return getTemplate("ModuleFactoryClass.xslt");
	}
	
	public InputStream getLibraryClassTemplate() {
		return getTemplate("FunctionLibraryClass.xslt");
	}
	
	/**
	 * Returns the XSLT stylesheet for converter factory class generation.
	 */
	public InputStream getConverterFactoryClassTemplate() {
		return getTemplate("ConverterFactoryClass.xslt");
	}
	
	/**
	 * Returns this context filer (obtained from the processing environment given to the constructor).
	 */
	public Filer getFiler() {
		return filer;
	}

	/**
	 * Returns this context generator identifier (given to the constructor).
	 */
	public String getGeneratorId() {
		return generatorId;
	}

	public GitInfo getGitInfo() {
		return gitInfo;
	}
	
	public String getGitInfoString() {
		StringBuilder sb = new StringBuilder();
        sb.append(String.format("version=%s", gitInfo.getBuildVersion()));
        if (!gitInfo.isCanonicalRemoteOrigin()) {
        	sb.append(String.format(", remote-url=%s", gitInfo.getRemoteOriginURL()));
        }
        sb.append(String.format(", commit=%s, commit-date=%s", gitInfo.getCommitId(), gitInfo.getCommitTime()));
        if (!gitInfo.isDefaultBranch()) {
        	sb.append(String.format(", branch=%s", gitInfo.getBranch()));
        }
        if (gitInfo.isDirty()) {
        	sb.append(String.format(", built-host=%s, build-date=%s", gitInfo.getBuildHost(), gitInfo.getBuildTime()));
        }
        return sb.toString();
	}

	/**
	 * Returns the date at which this context was created.
	 */
	public String getDate() {
		DateFormat dateFormat = new SimpleDateFormat(isoDateFormat);
		return dateFormat.format(date);
	}

	/**
	 * Returns a type element representing the AlvisNLPModule annotation interface.
	 */
	public TypeElement getModuleAnnotation() {
		return moduleAnnotation;
	}

	/**
	 * Returns a type element representing the Converter annotation interface.
	 */
	public TypeElement getConverterAnnotation() {
		return converterAnnotation;
	}
	
	TypeElement getLibraryAnnotation() {
		return libraryAnnotation;
	}
	
	/**
	 * Returns either this context has the specified option.
	 * @param option
	 */
	boolean hasOption(String option) {
		return options.containsKey(option);
	}
	
	/**
	 * Returns the specified option value for this context.
	 * @param option
	 */
	String getOption(String option) {
		return options.get(option);
	}
	
	/**
	 * Issue a note-level message.
	 * @param msg
	 */
	void note(String msg) {
		messager.printMessage(Diagnostic.Kind.NOTE, msg);
	}

	/**
	 * Issue a warning.
	 * @param msg
	 */
	void warning(String msg) {
		messager.printMessage(Diagnostic.Kind.WARNING, msg);
	}

	/**
	 * Issue an error.
	 * @param msg
	 */
	void error(String msg) {
		messager.printMessage(Diagnostic.Kind.ERROR, msg);
	}
}
