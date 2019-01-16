package fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.stanford;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

import edu.stanford.nlp.ling.TaggedWord;
import edu.stanford.nlp.parser.nndep.DependencyParser;
import edu.stanford.nlp.trees.GrammaticalStructure;
import edu.stanford.nlp.trees.TypedDependency;
import fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.SectionModule;
import fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.SectionModule.SectionResolvedObjects;
import fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.stanford.StanfordParser.StanfordParserResolvedObjects;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Annotation;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Corpus;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.DefaultNames;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Layer;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Relation;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Section;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Tuple;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.creators.TupleCreator;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.ConstantsLibrary;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.EvaluationContext;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.Evaluator;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.Expression;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.ResolverException;
import fr.inra.maiage.bibliome.alvisnlp.core.module.ModuleException;
import fr.inra.maiage.bibliome.alvisnlp.core.module.ProcessingContext;
import fr.inra.maiage.bibliome.alvisnlp.core.module.lib.AlvisNLPModule;
import fr.inra.maiage.bibliome.util.Iterators;
import fr.inra.maiage.bibliome.util.LoggingUtils;

@AlvisNLPModule(beta=true)
public abstract class StanfordParser extends SectionModule<StanfordParserResolvedObjects> implements TupleCreator {
	private String sentenceLayerName = DefaultNames.getSentenceLayer();
	private String tokenLayerName = DefaultNames.getWordLayer();
	private Expression sentenceFilter = ConstantsLibrary.TRUE;
	private String formFeatureName = Annotation.FORM_FEATURE_NAME;
	private String posTagFeatureName = DefaultNames.getPosTagFeature();
	private String dependencyRelationName = DefaultNames.getDependencyRelationName();
	private String headRole = DefaultNames.getDependencyHeadRole();
	private String dependentRole = DefaultNames.getDependencyDependentRole();
	private String dependencyLabelFeatureName = DefaultNames.getDependencyLabelFeatureName();
	private String dependencySentenceRole = DefaultNames.getDependencySentenceRole();
	
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
			Layer sentences = sec.getLayer(sentenceLayerName);
			Layer tokens = sec.getLayer(tokenLayerName);
			Relation dependencies = sec.ensureRelation(this, dependencyRelationName);
			for (Annotation sentence : sentences) {
				if (resObj.sentenceFilter.evaluateBoolean(evalCtx, sentence)) {
					parseSentence(parser, dependencies, tokens, sentence);
				}
			}
		}
	}
	
	private static DependencyParser loadParser() {
		return DependencyParser.loadFromModelFile(DependencyParser.DEFAULT_MODEL);
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
			String form = t.getLastFeature(formFeatureName);
			String pos = t.getLastFeature(posTagFeatureName);
			TaggedWord tw = new TaggedWord(form, pos);
			result.add(tw);
		}
		return result;
	}
	
	private void createDependencyTuple(Relation dependencies, Layer sentenceTokens, Annotation sentence, TypedDependency dep) {
		int headIndex = dep.gov().index() - 1;
		int modIndex = dep.dep().index() - 1;
		String label = dep.reln().getShortName();
		if ((headIndex == -1) || (modIndex == -1)) {
			// skip root
			return;
		}
		Annotation head = sentenceTokens.get(headIndex);
		Annotation mod = sentenceTokens.get(modIndex);
		Tuple t = new Tuple(this, dependencies);
		t.setArgument(dependencySentenceRole, sentence);
		t.setArgument(headRole, head);
		t.setArgument(dependentRole, mod);
		t.addFeature(dependencyLabelFeatureName, label);
	}

	@Override
	protected String[] addLayersToSectionFilter() {
		return new String[] { sentenceLayerName, tokenLayerName };
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








	public static void main(String[] args) {
        LoggingUtils.configureSilentLog4J();

		List<TaggedWord> taggedSentence = Arrays.asList(
				new TaggedWord("The", "DT"),
				new TaggedWord("fat", "JJ"),
				new TaggedWord("cat", "NN"),
				new TaggedWord("eats", "VVZ"),
				new TaggedWord("the", "DT"),
				new TaggedWord("mouse", "NN"),
				new TaggedWord("in", "IN"),
				new TaggedWord("my", "PP$"),
				new TaggedWord("kitchen", "NN"),
				new TaggedWord(".", ".")
				);

		System.err.println("load model");
		String modelPath = DependencyParser.DEFAULT_MODEL;
		DependencyParser parser = DependencyParser.loadFromModelFile(modelPath);

		System.err.println("parse");
		GrammaticalStructure gs = parser.predict(taggedSentence);
		for (TypedDependency dep : gs.typedDependencies()) {
			int head = dep.gov().index() - 1;
			int mod = dep.dep().index() - 1;
			String label = dep.reln().getShortName();
			if ((head == -1) || (mod == -1)) {
				System.out.format("* %s - %d - %d\n", label, head, mod);
				continue;
			}
			System.out.format("%s - %s - %s\n", label, taggedSentence.get(head).word(), taggedSentence.get(mod).word());
		}
	}
}
