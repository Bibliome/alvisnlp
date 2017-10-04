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


package alvisnlp.plan;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.Map;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.ext.EntityResolver2;

class AlvisNLPEntityResolver implements EntityResolver2 {
	private final Map<String,String> customEntities;
	
	AlvisNLPEntityResolver(Map<String,String> customEntities) {
		super();
		this.customEntities = customEntities;
	}

	@Override
	public InputSource resolveEntity(String publicId, String systemId) throws SAXException, IOException {
		throw new SAXException("feature http://xml.org/sax/features/use-entity-resolver2 not set");
	}

	@Override
	public InputSource getExternalSubset(String name, String baseURI) throws SAXException, IOException {
		StringBuilder sb = new StringBuilder();
		for (String entityName : customEntities.keySet()) {
			sb.append("<!ENTITY ");
			sb.append(entityName);
			sb.append(" SYSTEM \"\">");
		}
		String dtd = sb.toString();
		Reader r = new StringReader(dtd);
		return new InputSource(r);
	}

	@Override
	public InputSource resolveEntity(String name, String publicId, String baseURI, String systemId) throws SAXException, IOException {
		String value = customEntities.get(name);
		Reader r = new StringReader(value);
		return new InputSource(r);
	}
	
	public void setCustomEntity(String name, String value) {
		customEntities.put(name, value);
	}
}
