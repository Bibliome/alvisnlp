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


package alvisnlp.documentation;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import org.bibliome.util.xml.XMLUtils;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

class XMLDocumentationResourceBundleControl extends ResourceBundle.Control {
	static final String FORMAT_DOCUMENTATION = "alvisnlp.documentation";
	static final XMLDocumentationResourceBundleControl INSTANCE = new XMLDocumentationResourceBundleControl();

	@Override
	public List<String> getFormats(String arg0) {
		return Collections.unmodifiableList(Arrays.asList(FORMAT_DOCUMENTATION));
	}
	
	private static InputStream openInputStream(String resName, ClassLoader loader, boolean reload) throws IOException {
		if (reload) {
			URL url = loader.getResource(resName);
			if (url != null) {
				URLConnection urlCx = url.openConnection();
				urlCx.setUseCaches(false);
				return urlCx.getInputStream();
			}
			return null;
		}
		return loader.getResourceAsStream(resName);
	}
	
	private static InputStream safeOpenInputStream(String resName, ClassLoader loader, boolean reload) throws IOException {
		InputStream result = openInputStream(resName, loader, reload);
		if (result != null) {
			return result;
		}
		throw new IOException("could not open " + resName);
	}
	
	@Override
	public ResourceBundle newBundle(String baseName, Locale locale, String format, ClassLoader loader, boolean reload) throws IllegalAccessException, InstantiationException, IOException {
		if (!FORMAT_DOCUMENTATION.equals(format))
			throw new IllegalArgumentException("unknown format: " + format);
		String bundleName = toBundleName(baseName, locale);
		String resName = toResourceName(bundleName, "xml");
		try (InputStream is = safeOpenInputStream(resName, loader, reload)) {
			if (is == null) {
				return null;
			}
			Document document = XMLUtils.docBuilder.parse(is);
			return new XMLDocumentationResourceBundle(document);
		}
		catch (SAXException saxe) {
			throw new IllegalArgumentException(saxe);
		}
	}
}
