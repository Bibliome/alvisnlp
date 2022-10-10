package fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.stanford;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.ws.rs.ProcessingException;

import com.github.pemistahl.lingua.api.Language;

import edu.stanford.nlp.ling.TaggedWord;
import edu.stanford.nlp.parser.nndep.DependencyParser;
import edu.stanford.nlp.trees.GrammaticalStructure;
import edu.stanford.nlp.trees.TypedDependency;
import fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.DependencyParserModule;
import fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.SectionModule;
import fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.SectionModule.SectionResolvedObjects;
import fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.stanford.StanfordParser.StanfordParserResolvedObjects;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Annotation;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Corpus;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.DefaultNames;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Layer;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.NameType;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Relation;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Section;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.ConstantsLibrary;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.EvaluationContext;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.Evaluator;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.Expression;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.ResolverException;
import fr.inra.maiage.bibliome.alvisnlp.core.module.ModuleException;
import fr.inra.maiage.bibliome.alvisnlp.core.module.ProcessingContext;
import fr.inra.maiage.bibliome.alvisnlp.core.module.lib.AlvisNLPModule;
import fr.inra.maiage.bibliome.alvisnlp.core.module.lib.Param;
import fr.inra.maiage.bibliome.util.Checkable;
import fr.inra.maiage.bibliome.util.Iterators;
import fr.inra.maiage.bibliome.util.LoggingUtils;

@AlvisNLPModule
public abstract class StanfordParser extends SectionModule<StanfordParserResolvedObjects> implements DependencyParserModule, Checkable {
	private String sentenceLayer = DefaultNames.getSentenceLayer();
	private String tokenLayer = DefaultNames.getWordLayer();
	private Expression sentenceFilter = ConstantsLibrary.TRUE;
	private String formFeature = Annotation.FORM_FEATURE_NAME;
	private String posTagFeature = DefaultNames.getPosTagFeature();
	private Boolean omitRoot = false;
	private Language language = Language.ENGLISH;

	@Override
	public void process(ProcessingContext<Corpus> ctx, Corpus corpus) throws ModuleException {
        LoggingUtils.configureSilentLog4J();
		Logger logger = getLogger(ctx);
		EvaluationContext evalCtx = new EvaluationContext(logger);
		StanfordParserResolvedObjects resObj = getResolvedObjects();
		logger.info("loading parser model");
		DependencyParser parser = loadParser();
		logger.info("parsing sentences");
		for (Section sec : Iterators.loop(sectionIterator(evalCtx, corpus))) {
			Layer sentences = sec.getLayer(sentenceLayer);
			Layer tokens = sec.getLayer(tokenLayer);
			Relation dependencies = ensureRelation(sec);
			for (Annotation sentence : sentences) {
				if (resObj.sentenceFilter.evaluateBoolean(evalCtx, sentence)) {
					parseSentence(parser, dependencies, tokens, sentence);
				}
			}
		}
	}

	private String getParserPath() {
		switch (language) {
			case ENGLISH: return "edu/stanford/nlp/models/parser/nndep/english_UD.gz";
			case FRENCH: return "edu/stanford/nlp/models/parser/nndep/UD_French.gz";
			case GERMAN: return "edu/stanford/nlp/models/parser/nndep/UD_German.gz";
			case SPANISH: return "edu/stanford/nlp/models/parser/nndep/UD_Spanish.gz";
			case CHINESE: return "edu/stanford/nlp/models/parser/nndep/UD_Chinese.gz";
			default:
				return null;
		}
	}

	private DependencyParser loadParser() {
		String parserPath = getParserPath();
		if (parserPath == null) {
			throw new ProcessingException("language " + language + " is not supported");
		}
		return DependencyParser.loadFromModelFile(getParserPath());
	}

	private void parseSentence(DependencyParser parser, Relation dependencies, Layer tokens, Annotation sentence) {
		Layer sentenceTokens = tokens.between(sentence);
		List<TaggedWord> taggedSentence = convertSentenceTokens(sentenceTokens);
		GrammaticalStructure gs = parser.predict(taggedSentence);
		for (TypedDependency dep : gs.typedDependencies()) {
			createDependencyTuple(dependencies, sentenceTokens, sentence, dep);
		}
	}

	private List<TaggedWord> convertSentenceTokens(Layer sentenceTokens) {
		List<TaggedWord> result = new ArrayList<TaggedWord>(sentenceTokens.size());
		for (Annotation t : sentenceTokens) {
			String form = t.getLastFeature(formFeature);
			String pos = t.getLastFeature(posTagFeature);
			TaggedWord tw = new TaggedWord(form, pos);
			result.add(tw);
		}
		return result;
	}

	private void createDependencyTuple(Relation dependencies, Layer sentenceTokens, Annotation sentence, TypedDependency dep) {
		int headIndex = dep.gov().index() - 1;
		int modIndex = dep.dep().index() - 1;
		String label = dep.reln().getShortName();
		Annotation head;
		if (headIndex == -1) {
			if (omitRoot) {
				return;
			}
			head = null;
		}
		else {
			head = sentenceTokens.get(headIndex);
		}
		Annotation mod = sentenceTokens.get(modIndex);
		createDependency(dependencies, sentence, head, mod, label);
	}

	@Override
	protected String[] addLayersToSectionFilter() {
		return new String[] { sentenceLayer, tokenLayer };
	}

	@Override
	protected String[] addFeaturesToSectionFilter() {
		return null;
	}

	@Override
	protected StanfordParserResolvedObjects createResolvedObjects(ProcessingContext<Corpus> ctx) throws ResolverException {
		return new StanfordParserResolvedObjects(ctx, this);
	}

	public static class StanfordParserResolvedObjects extends SectionResolvedObjects {
		private final Evaluator sentenceFilter;

		public StanfordParserResolvedObjects(ProcessingContext<Corpus> ctx, StanfordParser module) throws ResolverException {
			super(ctx, module);
			this.sentenceFilter = module.sentenceFilter.resolveExpressions(rootResolver);
		}
	}

	@Override
	public boolean check(Logger logger) {
		String parserPath = getParserPath();
		if (parserPath == null) {
			logger.severe("language " + language + " is not supported");
			return false;
		}
		return true;
	}

	@Param(nameType=NameType.LAYER)
	public String getSentenceLayer() {
	    return this.sentenceLayer;
	};

	public void setSentenceLayer(String sentenceLayer) {
	    this.sentenceLayer = sentenceLayer;
	};

	@Deprecated
	@Param(nameType=NameType.LAYER)
	public String getSentenceLayerName() {
		return sentenceLayer;
	}

	@Param(nameType=NameType.LAYER)
	public String getTokenLayer() {
	    return this.tokenLayer;
	};

	public void setTokenLayer(String tokenLayer) {
	    this.tokenLayer = tokenLayer;
	};

	@Deprecated
	@Param(nameType=NameType.LAYER)
	public String getTokenLayerName() {
		return tokenLayer;
	}

	@Param
	public Expression getSentenceFilter() {
		return sentenceFilter;
	}

	@Deprecated
	@Param(nameType=NameType.FEATURE)
	public String getFormFeatureName() {
		return formFeature;
	}

	@Deprecated
	@Param(nameType=NameType.FEATURE)
	public String getPosTagFeatureName() {
		return posTagFeature;
	}

	@Deprecated
	@Param(nameType=NameType.RELATION)
	public String getDependencyRelationName() {
		return getDependencyRelation();
	}

	@Deprecated
	@Param(nameType=NameType.FEATURE)
	public String getDependencyLabelFeatureName() {
		return getDependencyLabelFeature();
	}

	@Param
	public Boolean getOmitRoot() {
		return omitRoot;
	}

	@Param
	public Language getLanguage() {
		return language;
	}

	@Param(nameType=NameType.FEATURE)
	public String getFormFeature() {
		return formFeature;
	}

	@Param(nameType=NameType.FEATURE)
	public String getPosTagFeature() {
		return posTagFeature;
	}

	public void setFormFeature(String formFeature) {
		this.formFeature = formFeature;
	}

	public void setPosTagFeature(String posTagFeature) {
		this.posTagFeature = posTagFeature;
	}

	public void setLanguage(Language language) {
		this.language = language;
	}

	public void setOmitRoot(Boolean omitRoot) {
		this.omitRoot = omitRoot;
	}

	public void setSentenceLayerName(String sentenceLayer) {
		this.sentenceLayer = sentenceLayer;
	}

	public void setTokenLayerName(String tokenLayer) {
		this.tokenLayer = tokenLayer;
	}

	public void setSentenceFilter(Expression sentenceFilter) {
		this.sentenceFilter = sentenceFilter;
	}

	public void setFormFeatureName(String formFeatureName) {
		this.formFeature = formFeatureName;
	}

	public void setPosTagFeatureName(String posTagFeatureName) {
		this.posTagFeature = posTagFeatureName;
	}

	public void setDependencyRelationName(String dependencyRelationName) {
		setDependencyRelation(dependencyRelationName);
	}

	public void setDependencyLabelFeatureName(String dependencyLabelFeatureName) {
		setDependencyLabelFeature(dependencyLabelFeatureName);
	}
}
