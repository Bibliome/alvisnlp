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

import java.util.Enumeration;
import java.util.Iterator;
import java.util.ResourceBundle;

import org.bibliome.util.Iterators;
import org.w3c.dom.Document;

class XMLDocumentationResourceBundle extends ResourceBundle {
	public static final String DOCUMENTATION = "documentation";

	private final Document document;
	
	XMLDocumentationResourceBundle(Document document) {
		super();
		this.document = document;
	}

	@Override
	public Enumeration<String> getKeys() {
		Iterator<String> it = Iterators.singletonIterator(DOCUMENTATION);
		return Iterators.getEnumeration(it);
	}

	@Override
	protected Object handleGetObject(String key) {
		if (DOCUMENTATION.equals(key))
			return document;
		return null;
	}
}
