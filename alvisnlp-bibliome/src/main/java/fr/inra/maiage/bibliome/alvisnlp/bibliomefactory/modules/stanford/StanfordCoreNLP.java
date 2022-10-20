package fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.stanford;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Logger;

import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.CoreDocument;
import edu.stanford.nlp.pipeline.CoreEntityMention;
import edu.stanford.nlp.pipeline.CoreSentence;
import fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.DependencyParserModule;
import fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.SectionModule;
import fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.SectionModule.SectionResolvedObjects;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Annotation;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Corpus;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.DefaultNames;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Layer;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Section;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.creators.AnnotationCreator;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.EvaluationContext;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.ResolverException;
import fr.inra.maiage.bibliome.alvisnlp.core.module.ModuleException;
import fr.inra.maiage.bibliome.alvisnlp.core.module.ProcessingContext;
import fr.inra.maiage.bibliome.alvisnlp.core.module.lib.AlvisNLPModule;
import fr.inra.maiage.bibliome.util.Iterators;

@AlvisNLPModule(beta=true)
public abstract class StanfordCoreNLP extends SectionModule<SectionResolvedObjects> implements AnnotationCreator, DependencyParserModule {
	private String wordLayer = DefaultNames.getWordLayer();
	private String sentenceLayer = DefaultNames.getSentenceLayer();
	private String posFeature = DefaultNames.getPosTagFeature();
	private String lemmaFeature = DefaultNames.getCanonicalFormFeature();
	private String namedEntityLayer = DefaultNames.getNamedEntityLayer();
	private String namedEntityTypeFeature = DefaultNames.getNamedEntityTypeFeature();

	@Override
	public void process(ProcessingContext<Corpus> ctx, Corpus corpus) throws ModuleException {
		Properties props = buildCoreNLPProperties();
		edu.stanford.nlp.pipeline.StanfordCoreNLP pipeline = new edu.stanford.nlp.pipeline.StanfordCoreNLP(props);
		Logger logger = getLogger(ctx);
		EvaluationContext evalCtx = new EvaluationContext(logger);
		for (Section sec : Iterators.loop(sectionIterator(evalCtx, corpus))) {
			CoreDocument doc = buildCoreNLPDocument(sec);
		    pipeline.annotate(doc);
		    collectAnnotations(doc, sec);
		}
	}
	
	@Override
	protected String[] addLayersToSectionFilter() {
		return null;
	}

	@Override
	protected String[] addFeaturesToSectionFilter() {
		return null;
	}

	@Override
	protected SectionResolvedObjects createResolvedObjects(ProcessingContext<Corpus> ctx) throws ResolverException {
		return new SectionResolvedObjects(ctx, this);
	}

	private Annotation createAnnotation(Layer layer, List<CoreLabel> tokens) {
		int start = tokens.get(0).beginPosition();
		int end = tokens.get(tokens.size() - 1).endPosition();
		return new Annotation(this, layer, start, end);
	}

	private void collectAnnotations(CoreDocument doc, Section sec) {
		Map<CoreLabel,Annotation> tokenMap = collectTokens(doc, sec);
		collectEntities(doc, sec);
		collectSentences(doc, sec);
	}

	private void collectEntities(CoreDocument doc, Section sec) {
		Layer entityLayer = sec.ensureLayer(this.namedEntityLayer);
		for (CoreEntityMention ent : doc.entityMentions()) {
			Annotation aEnt = createAnnotation(entityLayer, ent.tokens());
			aEnt.addFeature(namedEntityTypeFeature, ent.entityType());
		}
	}

	private void collectSentences(CoreDocument doc, Section sec) {
		Layer sentLayer = sec.ensureLayer(this.sentenceLayer);
		for (CoreSentence sent : doc.sentences()) {
			Annotation aSent = createAnnotation(sentLayer, sent.tokens());
		}
	}

	private Map<CoreLabel,Annotation> collectTokens(CoreDocument doc, Section sec) {
		Map<CoreLabel,Annotation> result = new HashMap<CoreLabel,Annotation>();
		Layer wordLayer = sec.ensureLayer(this.wordLayer);
		for (CoreLabel tok : doc.tokens()) {
			Annotation aTok = new Annotation(this, wordLayer, tok.beginPosition(), tok.endPosition());
			aTok.addFeature(posFeature, tok.tag());
			aTok.addFeature(lemmaFeature, tok.lemma());
			result.put(tok, aTok);
		}
		return result;
	}

	private CoreDocument buildCoreNLPDocument(Section sec) {
		return new CoreDocument(sec.getContents());
	}

	private Properties buildCoreNLPProperties() {
		Properties result = new Properties();
		//result.setProperty("annotators", "tokenize,pos,lemma,ner,parse,depparse,coref");
	    result.setProperty("annotators", "tokenize,pos,lemma,ner");
	    return result;
	}

}
