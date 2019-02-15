package fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.contes;

import org.w3c.dom.Element;

import fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.DefaultExpressions;
import fr.inra.maiage.bibliome.alvisnlp.core.converters.ConverterException;
import fr.inra.maiage.bibliome.alvisnlp.core.converters.lib.AbstractParamConverter;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.Expression;
import fr.inra.maiage.bibliome.util.files.AbstractFile;
import fr.inra.maiage.bibliome.util.xml.XMLUtils;

abstract class AbstractContesTermClassifierParamConverter<F extends AbstractFile,T extends ContesTermClassifier<F>> extends AbstractParamConverter<T> {
	@Override
	protected T convertTrimmed(String stringValue) throws ConverterException {
		cannotConvertString(stringValue, "no string conversion for " + ContesTermClassifier.class.getCanonicalName());
		return null;
	}
	
	protected abstract Class<F> getMatrixFileClass();
	
	protected abstract T createContesTermClassifier(Expression documentFilter, Expression sectionFilter, Double factor, String termLayerName, String conceptFeatureName, String similarityFeatureName, F regressionMatrixFile);

	@Override
	protected T convertXML(Element xmlValue) throws ConverterException {
		Expression documentFilter = DefaultExpressions.TRUE;
		Expression sectionFilter = DefaultExpressions.TRUE;
		Double factor = 1.0;
		String termLayerName = null;
		String conceptFeatureName = null;
		String similarityFeatureName = null;
		F regressionMatrixFile = null;
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
		if (xmlValue.hasAttribute("similarityFeatureName")) {
			similarityFeatureName = convertComponent(String.class, xmlValue.getAttribute("similarityFeatureName"));
		}
		if (xmlValue.hasAttribute("regressionMatrixFile")) {
			regressionMatrixFile = convertComponent(getMatrixFileClass(), xmlValue.getAttribute("regressionMatrixFile"));
		}
		if (xmlValue.hasAttribute("factor")) {
			factor = convertComponent(Double.class, xmlValue.getAttribute("factor"));
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
				case "similarityFeatureName": {
					similarityFeatureName = convertComponent(String.class, contents);
					break;
				}
				case "regressionMatrixFile": {
					regressionMatrixFile = convertComponent(getMatrixFileClass(), contents);
					break;
				}
				case "factor": {
					factor = convertComponent(Double.class, contents);
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
		return createContesTermClassifier(documentFilter, sectionFilter, factor, termLayerName, conceptFeatureName, similarityFeatureName, regressionMatrixFile);
	}
}
