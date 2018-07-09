package fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.contes;

import java.io.File;

import org.w3c.dom.Element;

import fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.DefaultExpressions;
import fr.inra.maiage.bibliome.alvisnlp.core.converters.ConverterException;
import fr.inra.maiage.bibliome.alvisnlp.core.converters.lib.AbstractParamConverter;
import fr.inra.maiage.bibliome.alvisnlp.core.converters.lib.Converter;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.Expression;
import fr.inra.maiage.bibliome.util.xml.XMLUtils;

@Converter(targetType=ContesTermClassifier.class)
public class ContesTermClassifierParamConverter extends AbstractParamConverter<ContesTermClassifier> {
	@Override
	protected ContesTermClassifier convertTrimmed(String stringValue) throws ConverterException {
		cannotConvertString(stringValue, "no string conversion for " + ContesTermClassifier.class.getCanonicalName());
		return null;
	}

	@Override
	protected ContesTermClassifier convertXML(Element xmlValue) throws ConverterException {
		Expression documentFilter = DefaultExpressions.TRUE;
		Expression sectionFilter = DefaultExpressions.TRUE;
		String termLayerName = null;
		String conceptFeatureName = null;
		File regressionMatrixFile = null;
		if (xmlValue.hasAttribute("documentFilter")) {
			documentFilter = convertComponent(Expression.class, xmlValue.getAttribute("documentFilter"));
		}
		if (xmlValue.hasAttribute("sectionFilter")) {
			sectionFilter = convertComponent(Expression.class, xmlValue.getAttribute("sectionFilter"));
		}
		if (xmlValue.hasAttribute("termLayerName")) {
			termLayerName = convertComponent(String.class, xmlValue.getAttribute("termLayerName"));
		}
		if (xmlValue.hasAttribute("conceptFeatureName")) {
			conceptFeatureName = convertComponent(String.class, xmlValue.getAttribute("conceptFeatureName"));
		}
		if (xmlValue.hasAttribute("regressionMatrixFile")) {
			regressionMatrixFile = convertComponent(File.class, xmlValue.getAttribute("regressionMatrixFile"));
		}
		for (Element child : XMLUtils.childrenElements(xmlValue)) {
			String tag = child.getTagName();
			String contents = child.getTextContent();
			switch (tag) {
				case "documentFilter": {
					documentFilter = convertComponent(Expression.class, contents);
					break;
				}
				case "sectionFilter": {
					sectionFilter = convertComponent(Expression.class, contents);
					break;
				}
				case "termLayerName": {
					termLayerName = convertComponent(String.class, contents);
					break;
				}
				case "conceptFeatureName": {
					conceptFeatureName = convertComponent(String.class, contents);
					break;
				}
				case "regressionMatrixFile": {
					regressionMatrixFile = convertComponent(File.class, contents);
					break;
				}
				default: {
					cannotConvertXML(xmlValue, "incorrect tag " + tag);
				}
			}
		}
		if (termLayerName == null) {
			cannotConvertXML(xmlValue, "missing termLayerName");
		}
		if (conceptFeatureName == null) {
			cannotConvertXML(xmlValue, "missing conceptFeatureName");
		}
		if (regressionMatrixFile == null) {
			cannotConvertXML(xmlValue, "missing regressionMatrixFile");
		}
		return new ContesTermClassifier(documentFilter, sectionFilter, termLayerName, conceptFeatureName, regressionMatrixFile);
	}
}
