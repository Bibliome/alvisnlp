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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import fr.inra.maiage.bibliome.alvisnlp.core.module.lib.AlvisNLPModule;
import fr.inra.maiage.bibliome.alvisnlp.core.module.lib.ModuleBase;
import fr.inra.maiage.bibliome.alvisnlp.core.module.lib.Param;
import fr.inra.maiage.bibliome.alvisnlp.core.module.lib.TimeThis;
import fr.inra.maiage.bibliome.util.xml.XMLUtils;

/**
 * Module class model.
 * @author rbossy
 *
 */
class ModuleModel {
	private final TypeElement element;
	private final String simpleName;
	private final String fullName;
	private final String packageName;
	private final String bundleName;
	private final List<ParamModel> params = new ArrayList<ParamModel>();
	private final List<TimedMethodModel> timedMethods = new ArrayList<TimedMethodModel>();
	private final AlvisNLPModule annotation;
	
	/**
	 * Creates a new module class model.
	 * @param ctx
	 * @param moduleElement type element representing the class of the module.
	 * @throws ModelException
	 */
	ModuleModel(ModelContext ctx, TypeElement moduleElement) throws ModelException {
		if (!ctx.isModuleBase(moduleElement.asType()))
			throw new ModelException("module must inherit from " + ModuleBase.class + ": " + moduleElement.getQualifiedName());
		if (!moduleElement.getTypeParameters().isEmpty())
			throw new ModelException("module cannot be generic: " + moduleElement.getQualifiedName());
		element = moduleElement;
		simpleName = moduleElement.getSimpleName().toString();
		fullName = moduleElement.getQualifiedName().toString();
		packageName = fullName.substring(0, fullName.lastIndexOf('.'));
		annotation = moduleElement.getAnnotation(AlvisNLPModule.class);
		bundleName = fullName + "Doc";
		Map<String,ExecutableElement> methods = ctx.getMethodsByName(moduleElement);
		for (ExecutableElement method : methods.values()) {
			Param paramAnnotation = method.getAnnotation(Param.class);
			if (paramAnnotation != null) {
				params.add(new ParamModel(methods, method, paramAnnotation));
				continue;
			}
			TimeThis timeAnnotation = method.getAnnotation(TimeThis.class);
			if (timeAnnotation != null) {
				timedMethods.add(new TimedMethodModel(ctx, method, timeAnnotation));
			}
		}
	}

	public TypeElement getElement() {
		return element;
	}

	/**
	 * Returns the module class simple name.
	 */
	public String getSimpleName() {
		return simpleName;
	}
	
	/**
	 * Returns the module class fully qualified name.
	 */
	public String getFullName() {
		return fullName;
	}
	
	/**
	 * Returns the documentation resource bundle name for this module class model.
	 */
	public String getBundleName() {
		return bundleName;
	}
	
	/**
	 * Returns the module class package name.
	 */
	public String getPackageName() {
		return packageName;
	}

	/**
	 * Returns the list of all parameters of the module class. 
	 */
	public List<ParamModel> getParams() {
		return Collections.unmodifiableList(params);
	}
	
	/**
	 * Returns either the module class needs to be generated.
	 */
	public boolean isGenerate() {
        for (ParamModel param : params)
        	if (param.isGenerate())
        		return true;
        return !timedMethods.isEmpty();
	}
	
	Node getDOM(Document doc) {
        org.w3c.dom.Element result = doc.createElement("module");
        result.setAttribute("full-name", fullName);
        result.setAttribute("name", simpleName);
        for (ParamModel param : params) {
        	if (param.isGenerate()) {
        		result.appendChild(param.getDOM(doc));
        	}
        }
        for (TimedMethodModel timed : timedMethods) {
        	result.appendChild(timed.getDOM(doc));
        }
        return result;
	}

	Document generateDoc() {
		Document result = XMLUtils.docBuilder.newDocument();

		Element root = XMLUtils.createRootElement(result, "alvisnlp-doc");
		root.setAttribute("target", fullName);
		root.setAttribute("author", "");
		root.setAttribute("date", "");

		Element synopsis = XMLUtils.createElement(result, root, 1, "synopsis");
		XMLUtils.createElement(result, synopsis, 2, "p", "synopsis");
		
		Element module = XMLUtils.createElement(result, root, 1, "module-doc");
		Element description = XMLUtils.createElement(result, module, 2, "description");
		XMLUtils.createElement(result, description, 3, "p", "synopsis");
		
		for (ParamModel param : params) {
			Element paramDoc = XMLUtils.createElement(result, module, 2, "param-doc");
			paramDoc.setAttribute("name", param.getName());
		}
		
		return result;
	}

	@SuppressWarnings("static-method")
	boolean isObsolete() {
		return false; //	annotation.obsoleteUseInstead().length > 1;
	}

	boolean isBeta() {
		return annotation.beta();
	}
}
