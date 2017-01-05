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
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import javax.lang.model.element.Element;
import javax.tools.FileObject;
import javax.tools.StandardLocation;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.xpath.XPathExpressionException;

import org.bibliome.util.xml.XMLUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

/**
 * Module factory model.
 * @author rbossy
 *
 */
class ModuleFactoryModel extends AbstractFactoryModel {
	private final String dataClass;
	private final String sequenceImplementationClass;
	private final String factoryInterface;
	private final String prefix;
	private final String shellModule;
	private final Collection<ModuleModel> modules = new ArrayList<ModuleModel>();
	
	/**
	 * Creates a new factory model with the specified name, annotable model and concrete module class prefix.
	 * @param fullName
	 * @param model
	 * @param prefix
	 */
	ModuleFactoryModel(String fullName, String dataClass, String sequenceImplementationClass, String factoryInterface, String prefix, String shellModule) {
		super(fullName);
		this.dataClass = dataClass;
		this.sequenceImplementationClass = sequenceImplementationClass;
		this.factoryInterface = factoryInterface;
		this.prefix = prefix;
		this.shellModule = shellModule;
	}

	/**
	 * Adds a module to the target factory.
	 * @param module
	 */
	void addModule(ModelContext ctx, ModuleModel module) {
//		if (!dataClass.equals(module.getDataClass()))
		if (!ctx.isRightDataClassModule(module.getElement(), dataClass))
			throw new IllegalArgumentException();
		modules.add(module);
		clearDOM();
	}
	
	@Override
	protected void fillDOM(ModelContext ctx, Document doc) {
		org.w3c.dom.Element root = doc.getDocumentElement();
		root.setAttribute("generated-prefix", prefix);
		root.setAttribute("dataClass", dataClass);
		root.setAttribute("sequenceClass", sequenceImplementationClass);
		root.setAttribute("factoryInterface", factoryInterface);
		root.setAttribute("shellModule", shellModule);
		for (ModuleModel module : modules)
			root.appendChild(module.getDOM(doc));
	}

	/**
	 * Generate Java source code for modules registered for the target facotry.
	 * @param ctx
	 * @throws IOException
	 * @throws TransformerException
	 */
	void generateModules(ModelContext ctx) throws IOException, TransformerException {
		for (ModuleModel module : modules)
			if (module.isGenerate())
				generateModule(ctx, module);
	}

	private void generateModule(ModelContext ctx, ModuleModel module) throws IOException, TransformerException {
        Transformer transformer = ctx.getTransformerFactory().newTransformer(new StreamSource(ctx.getModuleClassTemplate()));
        transformer.setParameter("full-name", module.getFullName());
        FileObject fo = ctx.getFiler().createSourceFile(getPackageName() + '.' + prefix + module.getSimpleName(), (Element[])null);
        PrintWriter out = new PrintWriter(fo.openWriter());
        transformer.transform(new DOMSource(getDOM(ctx)), new StreamResult(out));
        out.close();
	}

	/**
	 * Generate template documentation for registered modules.
	 * @param ctx
	 * @throws IOException
	 * @throws TransformerFactoryConfigurationError
	 * @throws XPathExpressionException
	 * @throws SAXException
	 */
	void generateModuleDocs(ModelContext ctx) throws IOException, TransformerFactoryConfigurationError, XPathExpressionException, SAXException {
		for (ModuleModel module : modules)
			generateModuleDoc(ctx, module);
	}
	
	private static void generateModuleDoc(ModelContext ctx, ModuleModel module) throws IOException, TransformerFactoryConfigurationError, SAXException, XPathExpressionException {
		String bundleName = module.getBundleName();
		int dot = bundleName.lastIndexOf('.');
		String packageName = bundleName.substring(0, dot);
		String fileName = bundleName.substring(dot + 1) + ".xml";
		FileObject fo;
		try {
			// XXX broken 
			// with maven build these XML files reside in the resources directory
			// there is no StandardLocation for resources in JDK7...
			fo = ctx.getFiler().getResource(StandardLocation.SOURCE_PATH, packageName, fileName);
		}
		catch (FileNotFoundException fnfe) {
			// XXX broken
			// with maven build these XML files should be generated in the build directory
			// there is no StandardLocation for resources in JDK7...
			fo = ctx.getFiler().createResource(StandardLocation.SOURCE_OUTPUT, packageName, fileName);
			ctx.note("generating " + fo.getName());
			PrintWriter out = new PrintWriter(fo.openOutputStream());
			XMLUtils.writeDOMToFile(module.generateDoc(), null, out);
			out.close();
			return;
		}
		InputStream is = fo.openInputStream();
		Document doc = XMLUtils.docBuilder.parse(is);
		Collection<String> paramNames = new HashSet<String>();
		for (ParamModel param : module.getParams()) {
			String name = param.getName();
			paramNames.add(name);
			List<Node> docs = XMLUtils.evaluateNodes("/alvisnlp-doc/module-doc/param-doc[@name = '" + name + "']", doc);
			if (docs.isEmpty())
				ctx.warning("parameter " + name + " of module " + module.getFullName() + " is not documented");
			if (docs.size() > 1)
				ctx.warning("parameter " + name + " of module " + module.getFullName() + " is documented more than once");
		}
		for (org.w3c.dom.Element paramDoc : XMLUtils.evaluateElements("/alvisnlp-doc/module-doc/param-doc", doc)) {
			String name = paramDoc.getAttribute("name");
			if (!paramNames.contains(name))
				ctx.warning("parameter " + name + " of module " + module.getFullName() + " is documented but does not exist");
		}
		is.close();
	}

	@Override
	public String getServiceClass() {
		return factoryInterface;
	}

	@Override
	protected InputStream getClassTemplate(ModelContext ctx) throws FileNotFoundException {
		return ctx.getModuleFactoryClassTemplate();
	}

	/**
	 * Return the number of registered modules.
	 */
	int moduleCount() {
		return modules.size();
	}
	
	int obsoleteCount() {
		int result = 0;
		for (ModuleModel module : modules) {
			if (module.isObsolete()) {
				result++;
			}
		}
		return result;
	}
	
	int betaCount() {
		int result = 0;
		for (ModuleModel module : modules) {
			if (module.isBeta()) {
				result++;
			}
		}
		return result;
	}
}
