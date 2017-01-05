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

import javax.tools.FileObject;
import javax.tools.StandardLocation;
import javax.xml.transform.TransformerFactoryConfigurationError;

import org.bibliome.util.xml.XMLUtils;
import org.w3c.dom.Document;

import alvisnlp.converters.ParamConverterFactory;

/**
 * Converter factory model.
 * @author rbossy
 *
 */
class ConverterFactoryModel extends AbstractFactoryModel {
	private final Collection<ConverterModel> converters = new ArrayList<ConverterModel>();

	/**
	 * Creates a converter factory model.
	 * @param fullName
	 */
	ConverterFactoryModel(String fullName) {
		super(fullName);
	}
	
	/**
	 * Adds a converter to the target factory.
	 * @param converter
	 */
	void addConverter(ConverterModel converter) {
		converters.add(converter);
	}

	@Override
	public String getServiceClass() {
		return ParamConverterFactory.class.getCanonicalName();
	}

	@Override
	protected void fillDOM(ModelContext ctx, Document doc) {
		org.w3c.dom.Element root = doc.getDocumentElement();
		for (ConverterModel converter : converters)
			root.appendChild(converter.getDOM(doc));
	}

	@Override
	protected InputStream getClassTemplate(ModelContext ctx) throws FileNotFoundException {
		return ctx.getConverterFactoryClassTemplate();
	}

	/**
	 * Generates a template documentation for each converter registered for the target factory.
	 * @param ctx
	 * @throws TransformerFactoryConfigurationError
	 * @throws IOException
	 */
	void generateConvertersDoc(ModelContext ctx) throws TransformerFactoryConfigurationError, IOException {
		for (ConverterModel converter : converters)
			generateConverterDoc(ctx, converter);
	}

	private static void generateConverterDoc(ModelContext ctx, ConverterModel converter) throws TransformerFactoryConfigurationError, IOException {
		String bundleName = converter.getBundleName();
		int dot = bundleName.lastIndexOf('.');
		String packageName = bundleName.substring(0, dot);
		String fileName = bundleName.substring(dot + 1) + ".xml";
		FileObject fo;
		try {
			fo = ctx.getFiler().getResource(StandardLocation.SOURCE_PATH, packageName, fileName);
		} catch (IOException ioe) {
			fo = ctx.getFiler().createResource(StandardLocation.SOURCE_OUTPUT, packageName, fileName);
			ctx.note("generating " + fo.getName());
			PrintWriter out = new PrintWriter(fo.openOutputStream());
			XMLUtils.writeDOMToFile(converter.generateDoc(), null, out);
			out.close();
		}
	}
}
