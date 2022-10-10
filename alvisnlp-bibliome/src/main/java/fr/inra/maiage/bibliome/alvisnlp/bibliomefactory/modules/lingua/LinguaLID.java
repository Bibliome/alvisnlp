package fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.lingua;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import com.github.pemistahl.lingua.api.Language;
import com.github.pemistahl.lingua.api.LanguageDetector;
import com.github.pemistahl.lingua.api.LanguageDetectorBuilder;
import com.google.common.collect.Lists;

import fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.CorpusModule;
import fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.DefaultExpressions;
import fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.ResolvedObjects;
import fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.lingua.LinguaLID.LinguaLIDResolvedObjects;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Corpus;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Element;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.NameType;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.EvaluationContext;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.Evaluator;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.Expression;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.ResolverException;
import fr.inra.maiage.bibliome.alvisnlp.core.module.ModuleException;
import fr.inra.maiage.bibliome.alvisnlp.core.module.NameUsage;
import fr.inra.maiage.bibliome.alvisnlp.core.module.ProcessingContext;
import fr.inra.maiage.bibliome.alvisnlp.core.module.lib.AlvisNLPModule;
import fr.inra.maiage.bibliome.alvisnlp.core.module.lib.Param;
import fr.inra.maiage.bibliome.util.Iterators;

@AlvisNLPModule(beta=true)
public class LinguaLID extends CorpusModule<LinguaLIDResolvedObjects> {
	private Expression target = DefaultExpressions.CORPUS_SECTIONS;
	private Expression form = DefaultExpressions.SECTION_CONTENTS;
	private String languageFeature = "language";
	private String languageConfidenceFeature = null;
	private Integer languageCandidates = 1;
	private Double confidenceThreshold = 0.0;
	private Language[] includeLanguages = null;

	static class LinguaLIDResolvedObjects extends ResolvedObjects {
		private final Evaluator target;
		private final Evaluator form;

		private LinguaLIDResolvedObjects(ProcessingContext<Corpus> ctx, LinguaLID module) throws ResolverException {
			super(ctx, module);
			this.target = module.target.resolveExpressions(rootResolver);
			this.form = module.form.resolveExpressions(rootResolver);
		}

		@Override
		public void collectUsedNames(NameUsage nameUsage, String defaultType) throws ModuleException {
			super.collectUsedNames(nameUsage, defaultType);
			this.target.collectUsedNames(nameUsage, defaultType);
			this.form.collectUsedNames(nameUsage, defaultType);
		}
	}

	private LanguageDetectorBuilder getLanguageDetectorBuilder() {
		if (includeLanguages == null) {
			return LanguageDetectorBuilder.fromAllSpokenLanguages();
		}
		return LanguageDetectorBuilder.fromLanguages(includeLanguages);
	}

	@Override
	public void process(ProcessingContext<Corpus> ctx, Corpus corpus) throws ModuleException {
		LinguaLIDResolvedObjects resObj = getResolvedObjects();
		Logger logger = getLogger(ctx);
		EvaluationContext evalCtx = new EvaluationContext(logger);
		LanguageDetector detector = getLanguageDetectorBuilder().build();
		int ntarget = 0;
		int nlang = 0;
		for (Element elt : Iterators.loop(resObj.target.evaluateElements(evalCtx, corpus))) {
			ntarget++;
			String form = resObj.form.evaluateString(evalCtx, elt);
			Map<Language,Double> predMap = detector.computeLanguageConfidenceValues(form);
			List<Map.Entry<Language,Double>> predList = new ArrayList<Map.Entry<Language,Double>>(predMap.entrySet());
			Lists.reverse(predList);
			int n = 0;
			for (Map.Entry<Language,Double> e : predList) {
				Language lang = e.getKey();
				double conf = e.getValue();
				if (conf < confidenceThreshold) {
					continue;
				}
				nlang++;
				elt.addFeature(languageFeature, lang.getIsoCode639_1().toString());
				elt.addFeature(languageConfidenceFeature, Double.toString(conf));
				n++;
				if (n == languageCandidates) {
					break;
				}
			}
		}
		logger.info("visited " + ntarget + " targets");
		logger.info("set " + nlang + " features");
	}

	@Override
	protected LinguaLIDResolvedObjects createResolvedObjects(ProcessingContext<Corpus> ctx) throws ResolverException {
		return new LinguaLIDResolvedObjects(ctx, this);
	}

	@Param
	public Expression getTarget() {
		return target;
	}

	@Param
	public Expression getForm() {
		return form;
	}

	@Param(nameType=NameType.FEATURE)
	public String getLanguageFeature() {
		return languageFeature;
	}

	@Param(nameType=NameType.FEATURE, mandatory=false)
	public String getLanguageConfidenceFeature() {
		return languageConfidenceFeature;
	}

	@Param
	public Integer getLanguageCandidates() {
		return languageCandidates;
	}

	@Param
	public Double getConfidenceThreshold() {
		return confidenceThreshold;
	}

	@Param(mandatory=false)
	public Language[] getIncludeLanguages() {
		return includeLanguages;
	}

	public void setIncludeLanguages(Language[] includeLanguages) {
		this.includeLanguages = includeLanguages;
	}

	public void setTarget(Expression target) {
		this.target = target;
	}

	public void setForm(Expression form) {
		this.form = form;
	}

	public void setLanguageFeature(String languageFeature) {
		this.languageFeature = languageFeature;
	}

	public void setLanguageConfidenceFeature(String languageConfidenceFeature) {
		this.languageConfidenceFeature = languageConfidenceFeature;
	}

	public void setLanguageCandidates(Integer languageCandidates) {
		this.languageCandidates = languageCandidates;
	}

	public void setConfidenceThreshold(Double confidenceThreshold) {
		this.confidenceThreshold = confidenceThreshold;
	}
}
