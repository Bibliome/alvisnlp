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


package fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.converters;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Element;

import fr.inra.maiage.bibliome.alvisnlp.core.converters.ConverterException;
import fr.inra.maiage.bibliome.alvisnlp.core.converters.lib.AbstractParamConverter;
import fr.inra.maiage.bibliome.alvisnlp.core.converters.lib.Converter;
import fr.inra.maiage.bibliome.util.files.AbstractFile;
import fr.inra.maiage.bibliome.util.files.FileFactory;
import fr.inra.maiage.bibliome.util.files.OutputFile;
import fr.inra.maiage.bibliome.util.streams.CollectionTargetStream;
import fr.inra.maiage.bibliome.util.streams.FileTargetStream;
import fr.inra.maiage.bibliome.util.streams.OutputStreamTargetStream;
import fr.inra.maiage.bibliome.util.streams.TargetStream;
import fr.inra.maiage.bibliome.util.xml.XMLUtils;

@Converter(targetType=TargetStream.class)
public class TargetStreamParamConverter extends AbstractParamConverter<TargetStream> {
	@Override
	protected TargetStream convertTrimmed(String stringValue) throws ConverterException {
		return getTargetStream(stringValue, "UTF-8", false);
	}

	@Override
	protected TargetStream convertXML(Element xmlValue)	throws ConverterException {
		List<TargetStream> targetStreams = getTargetStreams(xmlValue, "UTF-8", false);
		if (targetStreams.isEmpty())
			cannotConvertXML(xmlValue, "no target specified");
		if (targetStreams.size() == 1)
			return targetStreams.get(0);
		return new CollectionTargetStream("UTF-8", targetStreams);
	}

	private static final String[] valueAttributes = new String[] {
		"value",
		"file",
		"path"
	};

	private List<TargetStream> getTargetStreams(Element xmlValue, String charset, boolean append) throws ConverterException {
		List<TargetStream> c = new ArrayList<TargetStream>();
		charset = getCharset(xmlValue, charset);
		append = XMLUtils.getBooleanAttribute(xmlValue, "append", append);
		for (String a : valueAttributes)
			if (xmlValue.hasAttribute(a))
				c.add(getTargetStream(xmlValue.getAttribute(a), charset, append));
		List<Element> children = XMLUtils.childrenElements(xmlValue);
		if (children.isEmpty()) {
			String s = xmlValue.getTextContent().trim();
			if (!s.isEmpty())
				c.add(getTargetStream(s, charset, append));
		}
		else
			for (Element child : children)
				c.addAll(getTargetStreams(child, charset, append));
		return c;
	}
	
	private static final FileFactory<OutputFile> OUTPUT_FILE_FACTORY = new FileFactory<OutputFile>() {
		@Override
		public OutputFile createFile(String path) {
			return new OutputFile(path);
		}

		@Override
		public OutputFile createFile(String parent, String path) {
			return new OutputFile(parent, path);
		}
	};

	private TargetStream getTargetStream(String s, String charset, boolean append) throws ConverterException {
		try {
			URI uri = new URI(s);
			String scheme = uri.getScheme();
			if (scheme == null) {
				OutputFile file = AbstractFile.getOutputFile(OUTPUT_FILE_FACTORY, getOutputDir(), uri.getPath());
				return new FileTargetStream(charset, file, append);
			}
			switch (uri.getScheme()) {
				case "file": {
					OutputFile file = AbstractFile.getOutputFile(OUTPUT_FILE_FACTORY, getOutputDir(), uri.getPath());
					return new FileTargetStream(charset, file, append);
				}
				case "stream":
					String host = uri.getHost();
					if (host == null) {
						cannotConvertString(s, "missing stream");
						break;
					}
					switch (host) {
						case "stdout":
							return new OutputStreamTargetStream(charset, System.out, "<<stdout>>");
						case "stderr":
							return new OutputStreamTargetStream(charset, System.err, "<<stderr>>");
						default:
							cannotConvertString(s, "unsupported stream: " + host);
					}
			}
			cannotConvertString(s, "unsupported scheme: " + uri.getScheme());
			return null;
		}
		catch (URISyntaxException e) {
			cannotConvertString(s, e.getMessage());
			return null;
		}
	}

	private static String getCharset(Element xmlValue, String defaultCharset) {
		if (xmlValue.hasAttribute("encoding"))
			return xmlValue.getAttribute("encoding").trim();
		if (xmlValue.hasAttribute("charset"))
			return xmlValue.getAttribute("charset").trim();
		return defaultCharset;
	}
}
