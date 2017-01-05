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

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.w3c.dom.Document;

public class ConstantDocumentation implements Documentation {
	private final Map<Locale,Document> documents = new HashMap<Locale,Document>();

	@Override
	public Document getDocument() {
		Locale locale = Locale.getDefault();
		return getDocument(locale);
	}

	@Override
	public Document getDocument(Locale locale) {
		if (documents.containsKey(locale)) {
			return documents.get(locale);
		}
		throw new RuntimeException("No documentation for locale " + locale);
	}
	
	public void setDocument(Locale locale, Document doc) {
		documents.put(locale, doc);
	}
}
