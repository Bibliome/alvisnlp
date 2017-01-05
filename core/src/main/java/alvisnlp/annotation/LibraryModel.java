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

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.ElementFilter;
import javax.tools.FileObject;
import javax.tools.StandardLocation;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.xpath.XPathExpressionException;

import org.bibliome.util.Iterators;
import org.bibliome.util.Strings;
import org.bibliome.util.defaultmap.DefaultArrayListHashMap;
import org.bibliome.util.defaultmap.DefaultMap;
import org.bibliome.util.xml.XMLUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import alvisnlp.corpus.expressions.Function;
import alvisnlp.corpus.expressions.FunctionLibrary;
import alvisnlp.corpus.expressions.Library;

class LibraryModel {
	private final String simpleName;
	private final String fullName;
	private final String packageName;
	private final String baseClassFullName;
	private final boolean implementsResolveFunction;
	private final DefaultMap<String,List<FunctionModel>> functions = new DefaultArrayListHashMap<String,FunctionModel>();
	private final Library annotation;
	
	LibraryModel(ModelContext ctx, TypeElement libraryElement, String prefix) throws ModelException {
		baseClassFullName = libraryElement.getQualifiedName().toString();
		if (!ctx.isFunctionLibrary(libraryElement.asType()))
			throw new ModelException("library must inherit from " + FunctionLibrary.class + ": " + baseClassFullName);
		if (!libraryElement.getTypeParameters().isEmpty())
			throw new ModelException("library cannot be generic: " + baseClassFullName);
		if (!ModelContext.isPublic(libraryElement))
			throw new ModelException("library must be public: " + baseClassFullName);
		annotation = libraryElement.getAnnotation(Library.class);
		if (annotation.targetClass().isEmpty()) {
			packageName = ctx.elementUtils.getPackageOf(libraryElement).getQualifiedName().toString();
			simpleName = prefix + libraryElement.getSimpleName();
			fullName = packageName + '.' + simpleName;
		}
		else {
			fullName = annotation.targetClass();
			int dot = fullName.lastIndexOf('.');
			packageName = fullName.substring(0, dot);
			simpleName = fullName.substring(dot + 1);
		}
		boolean implementsResolveFunction = false;
		int ord = 0;
		for (ExecutableElement method : ElementFilter.methodsIn(ctx.elementUtils.getAllMembers(libraryElement))) {
			Function functionAnnotation = method.getAnnotation(Function.class);
			if (functionAnnotation != null) {
				String callMethod = method.getSimpleName().toString();
				String firstFtor = functionAnnotation.firstFtor().isEmpty() ? callMethod : functionAnnotation.firstFtor();
				FunctionModel fun = new FunctionModel(ctx, method, firstFtor, callMethod, functionAnnotation.ftors(), functionAnnotation.nameTypes(), ++ord);
				addFunctionModel(fun);
			}
			if (ctx.isResolveFunctionImplementation(method, libraryElement))
				implementsResolveFunction = true;
		}
		this.implementsResolveFunction = implementsResolveFunction;
		for (String extSpec : annotation.externalStatic()) {
			int eq = extSpec.indexOf('=');
			String extFullSig = extSpec;
			if (eq != -1)
				extFullSig = extSpec.substring(eq + 1);
			int paren = extFullSig.indexOf('(');
			if (paren == -1)
				throw new ModelException("illegal method signature: " + extSpec);
			String extFullName = extFullSig.substring(0, paren);
			int dot = extFullName.lastIndexOf('.');
			if (dot == -1)
				throw new ModelException("illegal method signature: " + extFullSig);
			String extClassName = extFullName.substring(0, dot);
			String extName = extFullName.substring(dot + 1);
			String extSig = extName + extFullSig.substring(paren);
			TypeElement extClass = ctx.elementUtils.getTypeElement(extClassName);
			if (extClass == null)
				throw new ModelException("could not find class: " + extClassName);
			ExecutableElement extMethod = null;
			for (ExecutableElement m : ElementFilter.methodsIn(extClass.getEnclosedElements())) {
				if (ModelContext.isStatic(m) && extSig.equals(m.toString())) {
					extMethod = m;
					break;
				}
			}
			if (extMethod == null)
				throw new ModelException("could not find external function method " + extFullSig);
			String firstFtor = extName;
			if (eq != -1)
				firstFtor = extSpec.substring(0, eq);
			addFunctionModel(new FunctionModel(ctx, extMethod, firstFtor, extFullName, 0, new String[0], ++ord));
		}
	}
	
	private void addFunctionModel(FunctionModel fun) throws ModelException {
		List<FunctionModel> sameFirstFtor = functions.safeGet(fun.getFirtstFtor());
		for (FunctionModel f : sameFirstFtor) {
			if ((f.numFtors() == fun.numFtors()) && (f.numArgs() == fun.numArgs()))
				throw new ModelException("in library " + getName() + ": several implementations of function " + fun.getFirtstFtor() + " with " + fun.numFtors() + " ftors and " + fun.numArgs() + " arguments");
		}
		sameFirstFtor.add(fun);
	}
	
	public String getName() {
		return annotation.value();
	}
	
	private Document getDOM(ModelContext ctx, DocumentBuilder docBuilder) {
		Document result = docBuilder.newDocument();
		Element root = XMLUtils.createRootElement(result, "library");
		root.setAttribute("base-class", baseClassFullName);
		root.setAttribute("simple-name", simpleName);
		root.setAttribute("name", getName());
		root.setAttribute("package-name", packageName);
		root.setAttribute("generator", ctx.getGeneratorId());
		root.setAttribute("date", ctx.getDate());
		root.setAttribute("generator-version", ctx.getVersion().toString());
		root.setAttribute("resource-bundle-name", getDocResourceBundleName());
		root.setAttribute("full-name", fullName);
		root.setAttribute("service-class", annotation.serviceClass());
		root.setAttribute("generate-service", Boolean.toString(annotation.generateService()));
		root.setAttribute("implements-resolve", Boolean.toString(implementsResolveFunction));
		for (Map.Entry<String,List<FunctionModel>> e : functions.entrySet()) {
			Element firstFtor = result.createElement("first-ftor");
			firstFtor.setAttribute("value", e.getKey());
			root.appendChild(firstFtor);
			for (FunctionModel f : e.getValue())
				firstFtor.appendChild(f.toDOM(result));
		}
		return result;
	}

	void generateClass(ModelContext ctx) throws IOException, TransformerException {
        Transformer transformer = ctx.getTransformerFactory().newTransformer(new StreamSource(ctx.getLibraryClassTemplate()));
        FileObject fo = ctx.getFiler().createSourceFile(fullName, (javax.lang.model.element.Element[])null);
        PrintWriter out = new PrintWriter(fo.openWriter());
        transformer.transform(new DOMSource(getDOM(ctx, XMLUtils.docBuilder)), new StreamResult(out));
        out.close();
	}
	
	private Document getDocumentation(DocumentBuilder docBuilder) throws IOException {
		Document result = docBuilder.newDocument();
		Element root = XMLUtils.createRootElement(result, "alvisnlp-doc");
		root.setAttribute("target", fullName);
		root.setAttribute("short-target", getName());
		Element synopsis = XMLUtils.createElement(result, root, 0, "synopsis");
		XMLUtils.createElement(result, synopsis, 1, "p");
		Element lib = XMLUtils.createElement(result, root, 1, "library-doc");
		for (List<FunctionModel> fml : functions.values())
			for (FunctionModel f : fml)
				f.fillDocumentation(result, getName(), lib);
		return result;
	}
	
	public String getDocResourceBundleName() {
		if (annotation.docResourceBundle().isEmpty())
			return baseClassFullName + "Doc";
		return annotation.docResourceBundle();
	}
	
	void generateDocumentation(ModelContext ctx) throws SAXException, IOException, XPathExpressionException, TransformerFactoryConfigurationError {
		String docResourceBundleName = getDocResourceBundleName();
		int dot = docResourceBundleName.lastIndexOf('.');
		String packageName = docResourceBundleName.substring(0, dot);
		String fileName = docResourceBundleName.substring(dot + 1) + ".xml";
		FileObject fo;
		try {
			fo = ctx.getFiler().getResource(StandardLocation.SOURCE_PATH, packageName, fileName);
		}
		catch (FileNotFoundException fnfe) {
			fo = ctx.getFiler().createResource(StandardLocation.SOURCE_OUTPUT, packageName, fileName);
			ctx.note("generating " + fo.getName());
			PrintWriter out = new PrintWriter(fo.openOutputStream());
			XMLUtils.writeDOMToFile(getDocumentation(XMLUtils.docBuilder), null, out);
			out.close();
			return;
		}
		InputStream is = fo.openInputStream();
		Document doc = XMLUtils.docBuilder.parse(is);
		List<Element> funDocs = new ArrayList<Element>(XMLUtils.evaluateElements("/alvisnlp-doc/library-doc/function-doc", doc));
		for (Map.Entry<String,List<FunctionModel>> e : functions.entrySet()) {
			String firstFtor = e.getKey();
			for (FunctionModel f : e.getValue()) {
				int n = 0;
				Iterator<Element> funDocIt = funDocs.iterator();
				for (Element funDoc : Iterators.loop(funDocIt)) {
					if (!firstFtor.equals(funDoc.getAttribute("first-ftor")))
						continue;
					String synopsis = funDoc.getAttribute("synopsis");
					int paren = synopsis.indexOf('(');
					if (paren == -1)
						continue;
					int ftors = Strings.count(synopsis.substring(0, paren), ':') - 1;
					if (ftors != f.numFtors())
						continue;
					int args = synopsis.charAt(paren + 1) == ')' ? 0 : Strings.count(synopsis.substring(paren + 1), ',') + 1;
					if (args != f.numArgs())
						continue;
					n++;
					funDocIt.remove();
				}
				if (n == 0)
					ctx.warning("function " + f.toString(getName()) + " is not documented");
				if (n > 1)
					ctx.warning("function " + f.toString(getName()) + " is documented more than once");
			}
		}
		for (Element funDoc : funDocs)
			ctx.warning("function " + funDoc.getAttribute("synopsis") + " is documented but does not exist");
	}
}
