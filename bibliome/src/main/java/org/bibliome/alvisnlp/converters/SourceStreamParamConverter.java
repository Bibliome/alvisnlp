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


package org.bibliome.alvisnlp.converters;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.bibliome.util.streams.CollectionSourceStream;
import org.bibliome.util.streams.CompressionFilter;
import org.bibliome.util.streams.PatternFileFilter;
import org.bibliome.util.streams.SourceStream;
import org.bibliome.util.streams.StreamFactory;
import org.bibliome.util.xml.XMLUtils;
import org.w3c.dom.Element;

import alvisnlp.converters.ConverterException;
import alvisnlp.converters.lib.AbstractParamConverter;
import alvisnlp.converters.lib.Converter;

@Converter(targetType=SourceStream.class)
public class SourceStreamParamConverter extends AbstractParamConverter<SourceStream> {
	@Override
	protected SourceStream convertTrimmed(String stringValue) throws ConverterException {
		try {
			StreamFactory sf = getDefaultStreamFactory();
			return sf.getSourceStream(stringValue);
		}
		catch (IOException | URISyntaxException e) {
			cannotConvertString(stringValue, e.getMessage(), e);
			return null;
		}
	}
	
	private StreamFactory getDefaultStreamFactory() {
		StreamFactory result = new StreamFactory();
		result.setFilter(new PatternFileFilter());
		result.setInputDirs(getInputDirs());
		return result;
	}

	@Override
	protected SourceStream convertXML(Element xmlValue)	throws ConverterException {
		StreamFactory sf = getDefaultStreamFactory();
		List<SourceStream> sourceStreams = getSourceStreams(xmlValue, sf);
		if (sourceStreams.isEmpty())
			cannotConvertXML(xmlValue, "no source specified");
		if (sourceStreams.size() == 1)
			return sourceStreams.get(0);
		return new CollectionSourceStream("UTF-8", sourceStreams);
	}

	private static final String[] valueAttributes = new String[] {
		"value",
		"file",
		"path",
		"url",
		"href",
		"dir",
		"resource"
	};
	
	private Pattern getFilterPattern(Element xmlValue, Pattern defaultPattern) throws ConverterException {
		if (xmlValue.hasAttribute("filter")) {
			String re = xmlValue.getAttribute("filter");
			return convertComponent(Pattern.class, re);
		}
		return defaultPattern;
	}
	
	private CompressionFilter getCompressionFilter(Element xmlValue, CompressionFilter defaultFilter) throws ConverterException {
		if (xmlValue.hasAttribute("compression")) {
			String value = xmlValue.getAttribute("compression");
			switch (value) {
				case "none":
					return CompressionFilter.NONE;
				case "gz":
				case "gzip":
					return CompressionFilter.GZIP;
				default:
					cannotConvertXML(xmlValue, "unsupported compression '" + xmlValue.getAttribute("compression") + '\'');
			}
		}
		return defaultFilter;
	}

	private StreamFactory getStreamFactory(Element xmlValue, StreamFactory sf) throws ConverterException {
		StreamFactory result = new StreamFactory();

		String charset = sf.getCharset();
		charset = getCharset(xmlValue, charset);
		result.setCharset(charset);
		
		CompressionFilter compressionFilter = sf.getCompressionFilter();
		compressionFilter = getCompressionFilter(xmlValue, compressionFilter);
		result.setCompressionFilter(compressionFilter);
		
		boolean recursive = sf.isRecursive();
		recursive = XMLUtils.getBooleanAttribute(xmlValue, "recursive", recursive);
		result.setRecursive(recursive);

		PatternFileFilter filter = (PatternFileFilter) sf.getFilter();
		Pattern filterPattern = filter.getPattern();
		filterPattern = getFilterPattern(xmlValue, filterPattern);
		
		boolean fullNameFilter = filter.isFullNameFilter();
		fullNameFilter = XMLUtils.getBooleanAttribute(xmlValue, "fullNameFilter", fullNameFilter);

		boolean wholeMatch = filter.isWholeMatch();
		wholeMatch = XMLUtils.getBooleanAttribute(xmlValue, "wholeMatch", wholeMatch);
		filter = new PatternFileFilter(filterPattern, fullNameFilter, wholeMatch);
		result.setFilter(filter);
		
		List<String> inputDirs = sf.getInputDirs();
		result.setInputDirs(inputDirs);
		
		return result;
	}

	private List<SourceStream> getSourceStreams(Element xmlValue, StreamFactory sf) throws ConverterException {
		sf = getStreamFactory(xmlValue, sf);
		List<SourceStream> result = new ArrayList<SourceStream>();
		try {
			for (String a : valueAttributes) {
				if (xmlValue.hasAttribute(a)) {
					String value = xmlValue.getAttribute(a);
					SourceStream source = sf.getSourceStream(value);
					result.add(source);
				}
			}
			List<Element> children = XMLUtils.childrenElements(xmlValue);
			if (children.isEmpty()) {
				String s = xmlValue.getTextContent().trim();
				if (!s.isEmpty()) {
					SourceStream source = sf.getSourceStream(s);
					result.add(source);
				}
				return result;
			}
			for (Element child : children) {
				List<SourceStream> sources = getSourceStreams(child, sf);
				result.addAll(sources);
			}
			return result;
		}
		catch (IOException | URISyntaxException e) {
			cannotConvertXML(xmlValue, e.getMessage());
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
