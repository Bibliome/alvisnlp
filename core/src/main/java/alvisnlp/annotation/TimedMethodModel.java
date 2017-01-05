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

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.ArrayType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

import alvisnlp.module.ProcessingContext;
import alvisnlp.module.lib.TimeThis;

class TimedMethodModel {
	private final ExecutableElement method;
	private final Modifier scope;
	private final TimeThis annotation;
	
	TimedMethodModel(ModelContext ctx, ExecutableElement method, TimeThis annotation) throws ModelException {
		this.annotation = annotation;
		this.method = method;
		this.scope = getScope(method);
		String msg = "method " + method.getSimpleName() + " in " + method.getEnclosingElement();
		List<? extends VariableElement> params = method.getParameters();
		if (params.isEmpty()) {
			throw new ModelException(msg + ": expected at least one " + ProcessingContext.class.getCanonicalName() + " parameter");
		}
		boolean first = true;
		for (VariableElement param : params) {
			TypeMirror paramType = param.asType();
			if (first) {
				if (!ctx.isProcessingContext(paramType)) {
					throw new ModelException(msg + ": first parameter must be of type " + ProcessingContext.class.getCanonicalName());
				}
				first = false;
				continue;
			}
			checkPublicType(ctx, paramType, method, false, msg);
		}
		TypeMirror returnType = method.getReturnType();
		checkPublicType(ctx, returnType, method, true, msg);
	}
	
	private void checkPublicType(ModelContext ctx, TypeMirror type, ExecutableElement method, boolean acceptVoid, String msg) throws ModelException {
		TypeKind kind = type.getKind();
		if (kind.isPrimitive()) {
			return;
		}
		switch (kind) {
			case ARRAY:
				checkPublicType(ctx, ((ArrayType) type).getComponentType(), method, false, msg);
				return;
			case DECLARED:
				if (ctx.isPublic(type) || ctx.isProtected(type)) {
					return;
				}
				throw new ModelException(msg + ": " + type + " is not public or protected");
			case VOID:
				if (acceptVoid) {
					return;
				}
				throw new ModelException(msg + ": unsupported type " + type);
			default:
				throw new ModelException(msg + ": unsupported type " + type);
		}
	}
	
	private static Modifier getScope(ExecutableElement method) throws ModelException {
		Modifier result = Modifier.PUBLIC;
		for (Modifier mod : method.getModifiers()) {
			switch (mod) {
				case PROTECTED:
				case PUBLIC:
					result = mod;
					break;
				case PRIVATE:
				case FINAL:
				case STATIC:
					throw new ModelException("timed method " + method.getSimpleName() + "() in " + method.getEnclosingElement() + " is " + mod);
				default:
					break;
			}
		}
		return result;
	}
	
	Node getDOM(Document doc) {
		org.w3c.dom.Element result = doc.createElement("timed-method");
		result.setAttribute("name", method.getSimpleName().toString());
		result.setAttribute("task", annotation.task());
		result.setAttribute("return", method.getReturnType().toString());
		result.setAttribute("category", annotation.category().name());
		result.setAttribute("scope", scope.toString());
		for (VariableElement param : method.getParameters()) {
			org.w3c.dom.Element paramElt = doc.createElement("param");
			paramElt.setTextContent(param.asType().toString());
			result.appendChild(paramElt);
		}
		for (TypeMirror exc : method.getThrownTypes()) {
			org.w3c.dom.Element excElt = doc.createElement("throws");
			excElt.setTextContent(exc.toString());
			result.appendChild(excElt);
		}
		return result;
	}
}
