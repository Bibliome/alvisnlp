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


package alvisnlp.annotation;

import java.util.List;
import java.util.Map;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.ArrayType;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.ErrorType;
import javax.lang.model.type.ExecutableType;
import javax.lang.model.type.NoType;
import javax.lang.model.type.NullType;
import javax.lang.model.type.PrimitiveType;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.type.TypeVariable;
import javax.lang.model.type.TypeVisitor;
import javax.lang.model.type.UnionType;
import javax.lang.model.type.WildcardType;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

import alvisnlp.module.lib.Param;

/**
 * Module parameter model.
 * @author rbossy
 *
 */
class ParamModel {
	private final String name;
	private final String getter;
	private final String setter;
	private final String javaName;
	private final String type;
	private final boolean generate;
	private final Param annotation;
	
	/**
	 * Creates anew module parameter model.
	 * @param moduleMethods map of all methods of the module class to which belongs the parameter
	 * @param getterElement element representing the parameter getter
	 * @param annotation Param annotation associated to the getter
	 * @throws ModelException
	 */
	ParamModel(Map<String,ExecutableElement> moduleMethods, ExecutableElement getterElement, Param annotation) throws ModelException {
		this.annotation = annotation;
		getter = getGetter(getterElement);
        javaName = getter.substring(3, 4).toLowerCase() + getter.substring(4);
        generate = ModelContext.isAbstract(getterElement);
        setter = getSetter(moduleMethods, getter, generate, getterElement.getReturnType());
        String paramName = annotation.publicName();
        name = paramName.isEmpty() ? javaName : paramName;
        type = getType(getterElement);
	}
	
	private static String getType(ExecutableElement getterElement) throws ModelException {
		TypeMirror type = getterElement.getReturnType();
		if (!type.accept(checkGetterTypeVisitor, true))
			throw new ModelException("illegal parameter type: " + type.toString());
		return type.toString();
	}
	
	private static String getGetter(ExecutableElement getterElement) throws ModelException {
        String result = getterElement.getSimpleName().toString();
        if (!result.startsWith("get"))
        	throw new ModelException("parameter should be a method starting with 'get': " + result);
        if (!getterElement.getParameters().isEmpty())
        	throw new ModelException("parameter should be a method without any formal parameters: " + result);
        if (!ModelContext.isPublic(getterElement))
        	throw new ModelException("parameter should be a public method: " + result);
        return result;
	}
	
	private static String getSetter(Map<String,ExecutableElement> moduleMethods, String getter, boolean abstractGetter, TypeMirror type) throws ModelException {
        String result = "set" + getter.substring(3);
		if (!moduleMethods.containsKey(result)) {
			if (abstractGetter)
				return result;
			throw new ModelException("missing parameter setter method: " + result + " (" + getter + ")");
		}
		ExecutableElement setterElement = moduleMethods.get(result);
		if (!ModelContext.isPublic(setterElement))
			throw new ModelException("parameter setter should be public: " + result);
		if (ModelContext.isAbstract(setterElement)) {
			if (!abstractGetter)
				throw new ModelException("parameter setter should not be abstract since getter is implemented: " + result + " (" + getter + ")");
		}
		else {
			if (abstractGetter)
				throw new ModelException("parameter setter should be abstract since the getter is abstract: " + result + " (" + getter + ")");
		}
        List<? extends VariableElement> setterParameterTypes = setterElement.getParameters();
        if (setterParameterTypes.size() != 1)
        	throw new ModelException("parameter setter should be a method with one formal parameter: " + result + " (" + getter + ")");
        TypeMirror setterType = setterParameterTypes.get(0).asType();
        if (!type.equals(setterType))
        	throw new ModelException("parameter setter formal parameter should be the same type as the getter return type: " + result + " (" + getter + ")");
        return result;
	}

	/**
	 * Returns the parameter name.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Returns the name of the parameter getter.
	 */
	public String getGetter() {
		return getter;
	}

	/**
	 * Returns the name of the parameter setter.
	 */
	public String getSetter() {
		return setter;
	}

	/**
	 * Returns the name of the java field holding the parameter value.
	 */
	public String getJavaName() {
		return javaName;
	}

	/**
	 * Returns the fully qualified name of the parameter type.
	 */
	public String getType() {
		return type;
	}

	public String getNameType() {
		return annotation.nameType();
	}

	/**
	 * Returns either the parameter is mandatory.
	 */
	public boolean isMandatory() {
		return annotation.mandatory();
	}

	/**
	 * Returns the default documentation for the parameter.
	 */
	public String getDefaultDoc() {
		return annotation.defaultDoc();
	}

	public String getDefaultValue() {
		return annotation.defaultValue();
	}
	
	/**
	 * Returns either the parameter getter or setter is abstract.
	 */
	public boolean isGenerate() {
		return generate;
	}

	private static final TypeVisitor<Boolean,Boolean> checkGetterTypeVisitor = new TypeVisitor<Boolean,Boolean>() {
		@Override
		public Boolean visit(TypeMirror t) {
			return visit(t, true);
		}

		@Override
		public Boolean visit(TypeMirror t, Boolean p) {
			return false;
		}

		@Override
		public Boolean visitArray(ArrayType t, Boolean p) {
			if (p)
				return t.getComponentType().accept(this, false);
			return false;
		}

		@Override
		public Boolean visitDeclared(DeclaredType t, Boolean p) {
			return t.getTypeArguments().isEmpty();
		}

		@Override
		public Boolean visitError(ErrorType t, Boolean p) {
			return false;
		}

		@Override
		public Boolean visitExecutable(ExecutableType t, Boolean p) {
			return false;
		}

		@Override
		public Boolean visitNoType(NoType t, Boolean p) {
			return false;
		}

		@Override
		public Boolean visitNull(NullType t, Boolean p) {
			return false;
		}

		@Override
		public Boolean visitPrimitive(PrimitiveType t, Boolean p) {
			return false;
		}

		@Override
		public Boolean visitTypeVariable(TypeVariable t, Boolean p) {
			return false;
		}

		@Override
		public Boolean visitUnknown(TypeMirror t, Boolean p) {
			return false;
		}

		@Override
		public Boolean visitWildcard(WildcardType t, Boolean p) {
			return false;
		}

		@Override
		public Boolean visitUnion(UnionType arg0, Boolean arg1) {
			return false;
		}
	};

	Node getDOM(Document doc) {
        org.w3c.dom.Element result = doc.createElement("accessor");
        result.setAttribute("type", type);
        result.setAttribute("java", javaName);
        result.setAttribute("getter", getter);
        result.setAttribute("setter", setter);
        result.setAttribute("public", name);
        result.setAttribute("mandatory", Boolean.toString(isMandatory()));
        result.setAttribute("defaultDoc", getDefaultDoc());
        result.setAttribute("name-type", getNameType());
        result.setAttribute("defaultValue", getDefaultValue());
        return result;
	}
}
