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


package org.bibliome.alvisnlp.modules.tomap;

import org.bibliome.util.service.UnsupportedServiceException;
import org.bibliome.util.streams.SourceStream;
import org.bibliome.util.tomap.classifiers.CandidateDistanceFactory;
import org.bibliome.util.tomap.classifiers.StandardCandidateDistanceFactory;
import org.bibliome.util.xml.XMLUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import alvisnlp.converters.ConverterException;
import alvisnlp.converters.ParamConverter;
import alvisnlp.converters.lib.AbstractParamConverter;
import alvisnlp.converters.lib.Converter;

@Converter(targetType=TomapClassifier.class)
public class TomapClassifierParamConverter extends AbstractParamConverter<TomapClassifier> {
	@Override
	protected TomapClassifier convertTrimmed(String stringValue) throws ConverterException {
		SourceStream tomapFile = convertComponent(SourceStream.class, stringValue);
		return new TomapClassifier(false, tomapFile, null, null, StandardCandidateDistanceFactory.JACCARD, null, false, true, true, true);
	}

	@Override
	protected TomapClassifier convertXML(Element xmlValue) throws ConverterException {
		boolean noExactClassifier = XMLUtils.getBooleanAttribute(xmlValue, "no-exact-match", false);
		String tomapPath = XMLUtils.attributeOrValue(xmlValue, "classifier", "tomap");
		SourceStream tomapFile = convertComponent(SourceStream.class, tomapPath);
		SourceStream headGraylistFile = getSourceAttribute(xmlValue, "graylist");
		String defaultConcept = XMLUtils.getAttribute(xmlValue, "default", null);
		CandidateDistanceFactory candidateDistanceFactory = getCandidateDistance(xmlValue);
		SourceStream emptyWordsFile = getSourceAttribute(xmlValue, "empty-words");
		boolean wholeCandidateDistance = XMLUtils.getBooleanAttribute(xmlValue, "whole-candidate-distance", false);
		boolean wholeProxyDistance = XMLUtils.getBooleanAttribute(xmlValue, "whole-proxy-distance", true);
		boolean candidateHeadPriority = XMLUtils.getBooleanAttribute(xmlValue, "candidate-head-priority", true);
		boolean proxyHeadPriority = XMLUtils.getBooleanAttribute(xmlValue, "proxy-head-priority", true);
		return new TomapClassifier(noExactClassifier, tomapFile, headGraylistFile, defaultConcept, candidateDistanceFactory, emptyWordsFile, wholeCandidateDistance, wholeProxyDistance, candidateHeadPriority, proxyHeadPriority);
	}
	
	private SourceStream getSourceAttribute(Element xmlValue, String attribute) throws ConverterException {
		if (xmlValue.hasAttribute(attribute)) {
			SourceStream source = convertComponent(SourceStream.class, xmlValue.getAttribute(attribute));
			return source;
		}
		return null;
	}

	private CandidateDistanceFactory getCandidateDistance(Element xmlValue) throws ConverterException {
		if (xmlValue.hasAttribute("distance")) {
			String dist = xmlValue.getAttribute("distance");
			switch (dist) {
				case "jaccard":
					return StandardCandidateDistanceFactory.JACCARD;
				default:
					cannotConvertXML(xmlValue, "unknown distance: " + dist);
			}
		}
		return StandardCandidateDistanceFactory.JACCARD;
	}

	@Override
	public String getStringValue(Object value) throws ConverterException, UnsupportedServiceException {
		if (!(value instanceof TomapClassifier)) {
			throw new RuntimeException();
		}
		TomapClassifier classifier = (TomapClassifier) value;
		SourceStream file = classifier.getTomapFile();
		ParamConverter conv = getComponentConverter(SourceStream.class);
		return conv.getStringValue(file);
	}

	@Override
	public Element getXMLValue(Document doc, String tagName, Object value) throws ConverterException, UnsupportedServiceException {
		if (!(value instanceof TomapClassifier)) {
			throw new RuntimeException();
		}
		TomapClassifier classifier = (TomapClassifier) value;
		SourceStream file = classifier.getTomapFile();
		ParamConverter ssConv = getComponentConverter(SourceStream.class);
		Element result = ssConv.getXMLValue(doc, tagName, file);
		if (classifier.isNoExactClassifier()) {
			result.setAttribute("no-exact-match", "true");
		}
		SourceStream glf = classifier.getHeadGraylistFile();
		if (glf != null) {
			result.setAttribute("graylist", ssConv.getStringValue(glf));
		}
		String defaultConcept = classifier.getDefaultConcept();
		if (defaultConcept != null) {
			result.setAttribute("default", defaultConcept);
		}
		if (classifier.isWholeCandidateDistance()) {
			result.setAttribute("whole-candidate-distance", "true");
		}
		if (!classifier.isWholeProxyDistance()) {
			result.setAttribute("whole-proxy-distance", "false");
		}
		if (!classifier.isCandidateHeadPriority()) {
			result.setAttribute("candidate-head-priority", "false");
		}
		if (!classifier.isProxyHeadPriority()) {
			result.setAttribute("proxy-head-priority", "false");
		}
		return result;
	}
}
