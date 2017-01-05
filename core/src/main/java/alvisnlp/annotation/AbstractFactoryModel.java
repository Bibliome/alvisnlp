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

import javax.lang.model.element.Element;
import javax.tools.FileObject;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.bibliome.util.xml.XMLUtils;
import org.w3c.dom.Document;

/**
 * Base class for factory models.
 * @author rbossy
 *
 */
abstract class AbstractFactoryModel {
	private final String fullName;
	private final String packageName;
	private final String simpleName;
	private Document dom;
	private ModelContext domCtx;
	
	/**
	 * Create a new factory model with the specified full name.
	 * @param fullName fully qualified Java class name of the target factory
	 */
	AbstractFactoryModel(String fullName) {
		int dot = fullName.lastIndexOf('.');
		if (dot <= 0)
			throw new IllegalArgumentException();
		if (dot == fullName.length() - 1)
			throw new IllegalArgumentException();
		this.fullName = fullName;
		packageName = fullName.substring(0, dot);
		simpleName = fullName.substring(dot + 1);
	}

	/**
	 * Return the target factory full name.
	 */
	public String getFullName() {
		return fullName;
	}

	/**
	 * Return the package part of the target factory name.
	 */
	public String getPackageName() {
		return packageName;
	}

	/**
	 * Return the last part of the target factory name.
	 */
	public String getSimpleName() {
		return simpleName;
	}

	/**
	 * Return the fully qualified name of the interface implemented by the target factory.
	 */
	public abstract String getServiceClass();
	
	/**
	 * Return an XML representation of this model.
	 * The returned value is cached.
	 * @param ctx
	 */
	Document getDOM(ModelContext ctx) {
		if ((dom == null) || (ctx != domCtx)) {
			domCtx = ctx;
			dom = buildDOM(ctx);
		}
		return dom;
	}
	
	private Document buildDOM(ModelContext ctx) {
		Document result = XMLUtils.docBuilder.newDocument();
		org.w3c.dom.Element root = result.createElement("factory");
		result.appendChild(root);
		root.setAttribute("package", getPackageName());
		root.setAttribute("name", getSimpleName());
		root.setAttribute("fullName", getFullName());
		root.setAttribute("generator", ctx.getGeneratorId());
		root.setAttribute("date", ctx.getDate());
		root.setAttribute("generator-version", ctx.getVersion().toString());
		fillDOM(ctx, result);
		return result;
	}
	
	/**
	 * Fills the specified XML document with factory-specific nodes.
	 * @param ctx
	 * @param doc generic factory model XML representation
	 */
	protected abstract void fillDOM(ModelContext ctx, Document doc);
	
	/**
	 * Clears the current XML representation in cache.
	 */
	void clearDOM() {
		dom = null;
	}
	
	/**
	 * Generates the Java source for the target factory.
	 * The XML representation must be in cache.
	 * @param ctx
	 * @throws TransformerException
	 * @throws IOException
	 */
	void generateClass(ModelContext ctx) throws TransformerException, IOException {
        Transformer transformer = ctx.getTransformerFactory().newTransformer(new StreamSource(getClassTemplate(ctx)));
        FileObject fo = ctx.getFiler().createSourceFile(getFullName(), (Element[])null);
        PrintWriter out = new PrintWriter(fo.openWriter());
        transformer.transform(new DOMSource(getDOM(ctx)), new StreamResult(out));
        out.close();
	}

	/**
	 * Returns a stream with the XSLT stylesheet used to generate the target factory code.
	 * @param ctx
	 * @throws FileNotFoundException
	 */
	protected abstract InputStream getClassTemplate(ModelContext ctx) throws FileNotFoundException;
}
