package fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.stanford;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Logger;

import javax.ws.rs.ProcessingException;

import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.CoreDocument;
import edu.stanford.nlp.pipeline.CoreEntityMention;
import edu.stanford.nlp.pipeline.CoreSentence;
import edu.stanford.nlp.semgraph.SemanticGraph;
import edu.stanford.nlp.semgraph.SemanticGraphEdge;
import fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.DependencyParserModule;
import fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.SectionModule;
import fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.SectionModule.SectionResolvedObjects;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Annotation;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Corpus;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.DefaultNames;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Layer;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.NameType;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Relation;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Section;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Tuple;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.creators.AnnotationCreator;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.EvaluationContext;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.ResolverException;
import fr.inra.maiage.bibliome.alvisnlp.core.module.ModuleException;
import fr.inra.maiage.bibliome.alvisnlp.core.module.ProcessingContext;
import fr.inra.maiage.bibliome.alvisnlp.core.module.lib.AlvisNLPModule;
import fr.inra.maiage.bibliome.alvisnlp.core.module.lib.Param;
import fr.inra.maiage.bibliome.alvisnlp.core.module.types.Mapping;
import fr.inra.maiage.bibliome.util.Iterators;
import fr.inra.maiage.bibliome.util.Strings;

@AlvisNLPModule(beta=true)
public abstract class StanfordCoreNLP extends SectionModule<SectionResolvedObjects> implements AnnotationCreator, DependencyParserModule {
	private String wordLayer = DefaultNames.getWordLayer();
	private String sentenceLayer = DefaultNames.getSentenceLayer();
	private String posFeature = DefaultNames.getPosTagFeature();
	private String lemmaFeature = DefaultNames.getCanonicalFormFeature();
	private String namedEntityLayer = DefaultNames.getNamedEntityLayer();
	private String namedEntityTypeFeature = DefaultNames.getNamedEntityTypeFeature();
	private String dependencyRelation= DefaultNames.getDependencyRelationName();
	private String dependencySentenceRole = DefaultNames.getDependencySentenceRole();
	private String headRole = DefaultNames.getDependencyHeadRole();
	private String dependentRole = DefaultNames.getDependencyDependentRole();
	private String dependencyLabelFeature = DefaultNames.getDependencyLabelFeatureName();
	private Boolean ner = false;
	private Boolean parse = false;
	private Boolean pretokenized = false;
	private Mapping pipelineProperties = new Mapping();

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

	private Properties buildCoreNLPProperties() {
		Properties result = new Properties();
	    result.setProperty("annotators", getAnnotators());
	    result.putAll(pipelineProperties);
	    if (pretokenized) {
	    	result.setProperty("tokenize.whitespace", "true");
	    	result.setProperty("ssplit.eolonly", "true");
	    }
	    return result;
	}
	
	private String getAnnotators() {
		List<String> result = new ArrayList<String>();
		result.addAll(Arrays.asList("tokenize", "pos", "lemma"));
		if (ner) {
			result.add("ner");
		}
		if (parse) {
			result.add("depparse");
		}
		return Strings.join(result, ',');
	}

	private CoreDocument buildCoreNLPDocument(Section sec) {
		if (pretokenized) {
			return new CoreDocument(buildPretokenizedText(sec));
		}
		return new CoreDocument(sec.getContents());
	}
	
	private String buildPretokenizedText(Section sec) {
		try {
			Layer sentences = sec.getLayer(sentenceLayer);
			Layer words = sec.ensureLayer(wordLayer);
			StringBuilder text = new StringBuilder();
			for (Annotation aSent : sentences) {
				buildSentenceText(text, words, aSent);
			}
			return text.toString();
		}
		catch (IOException e) {
			throw new ProcessingException(e);
		}
	}

	private void buildSentenceText(StringBuilder text, Layer words, Annotation aSent) throws IOException {
		List<String> tokens = new ArrayList<String>();
		for (Annotation aTok : words.between(aSent)) {
			String form = aTok.getForm();
			String noSpace = Strings.normalizeSpace(form, '_');
			tokens.add(noSpace);
		}
		Strings.join(text, tokens, ' ');
		text.append('\n');
	}

	private void collectAnnotations(CoreDocument doc, Section sec) {
		Map<CoreLabel,Annotation> tokenMap = collectTokens(doc, sec);
		collectEntities(tokenMap, doc, sec);
		collectSentences(tokenMap, doc, sec);
	}

	private Map<CoreLabel,Annotation> collectTokens(CoreDocument doc, Section sec) {
		Map<CoreLabel,Annotation> result = new HashMap<CoreLabel,Annotation>();
		Layer wordLayer = sec.ensureLayer(this.wordLayer);
		List<CoreLabel> tokens = doc.tokens();
		if (pretokenized) {
			collectPretokenizedTokens(result, tokens, wordLayer);
		}
		else {
			collectTokens(result, tokens, wordLayer);
		}
		return result;
	}

	private void collectPretokenizedTokens(Map<CoreLabel,Annotation> tokenMap, List<CoreLabel> tokens, Layer wordLayer) {
		if (wordLayer.size() != tokens.size()) {
			throw new ProcessingException("wrong token number (expected " + wordLayer.size() + ", got " + tokens.size() + ")");
		}
		for (int i = 0; i < wordLayer.size(); ++i) {
			CoreLabel tok = tokens.get(i);
			Annotation aTok = wordLayer.get(i);
			handleToken(tokenMap, tok, aTok);
		}
	}

	private void collectTokens(Map<CoreLabel,Annotation> tokenMap, List<CoreLabel> tokens, Layer wordLayer) {
		for (CoreLabel tok : tokens) {
			Annotation aTok = new Annotation(this, wordLayer, tok.beginPosition(), tok.endPosition());
			handleToken(tokenMap, tok, aTok);
		}
	}
	
	private void handleToken(Map<CoreLabel,Annotation> tokenMap, CoreLabel tok, Annotation aTok) {
		aTok.addFeature(posFeature, tok.tag());
		aTok.addFeature(lemmaFeature, tok.lemma());
		tokenMap.put(tok, aTok);
	}

	private void collectEntities(Map<CoreLabel,Annotation> tokenMap, CoreDocument doc, Section sec) {
		if (!ner) {
			return;
		}
		Layer entityLayer = sec.ensureLayer(this.namedEntityLayer);
		for (CoreEntityMention ent : doc.entityMentions()) {
			Annotation aEnt = createAnnotation(tokenMap, entityLayer, ent.tokens());
			aEnt.addFeature(namedEntityTypeFeature, ent.entityType());
		}
	}

	private Annotation createAnnotation(Map<CoreLabel,Annotation> tokenMap, Layer layer, List<CoreLabel> tokens) {
		CoreLabel firstToken = tokens.get(0);
		CoreLabel lastToken = tokens.get(tokens.size() - 1);
		Annotation firstAToken = tokenMap.get(firstToken);
		Annotation lastAToken = tokenMap.get(lastToken);
		int start = firstAToken.getStart();
		int end = lastAToken.getEnd();
		return new Annotation(this, layer, start, end);
	}

	private void collectSentences(Map<CoreLabel,Annotation> tokenMap, CoreDocument doc, Section sec) {
		Layer sentLayer = sec.ensureLayer(this.sentenceLayer);
		List<CoreSentence> sentences = doc.sentences();
		if (pretokenized) {
			collectPretokenizedSentences(tokenMap, doc, sec, sentences, sentLayer);
		}
		else {
			collectSentences(tokenMap, doc, sec, sentences, sentLayer);
		}
	}

	private void collectPretokenizedSentences(Map<CoreLabel,Annotation> tokenMap, CoreDocument doc, Section sec, List<CoreSentence> sentences, Layer sentLayer) {
		if (sentLayer.size() != sentences.size()) {
			throw new ProcessingException("wrong sentence number (expected " + sentLayer.size() + ", got " + sentences.size() + ")");
		}
		for (int i = 0; i < sentLayer.size(); ++i) {
			CoreSentence sent = sentences.get(i);
			Annotation aSent = sentLayer.get(i);
			collectDependencies(tokenMap, doc, sec, sent, aSent);
		}
	}

	private void collectSentences(Map<CoreLabel, Annotation> tokenMap, CoreDocument doc, Section sec,
			List<CoreSentence> sentences, Layer sentLayer) {
		for (CoreSentence sent : sentences) {
			Annotation aSent = createAnnotation(tokenMap, sentLayer, sent.tokens());
			collectDependencies(tokenMap, doc, sec, sent, aSent);
		}
	}

	private void collectDependencies(Map<CoreLabel,Annotation> tokenMap, CoreDocument doc, Section sec, CoreSentence sent, Annotation aSent) {
		if (!parse) {
			return;
		}
		Relation rel = sec.ensureRelation(this, dependencyRelation);
		SemanticGraph graph = sent.dependencyParse();
		if (graph == null) {
			return;
		}
		for (SemanticGraphEdge edge : graph.edgeIterable()) {
			Tuple t = new Tuple(this, rel);
			t.setArgument(dependencySentenceRole, aSent);
			t.setArgument(headRole, tokenMap.get(edge.getGovernor().backingLabel()));
			t.setArgument(dependentRole, tokenMap.get(edge.getDependent().backingLabel()));
			t.addFeature(dependencyLabelFeature, edge.getRelation().getShortName());
		}
	}

	@Param(nameType = NameType.LAYER)
	public String getWordLayer() {
		return wordLayer;
	}

	@Param(nameType = NameType.LAYER)
	public String getSentenceLayer() {
		return sentenceLayer;
	}

	@Param(nameType = NameType.FEATURE)
	public String getPosFeature() {
		return posFeature;
	}

	@Param(nameType = NameType.FEATURE)
	public String getLemmaFeature() {
		return lemmaFeature;
	}

	@Param(nameType = NameType.LAYER)
	public String getNamedEntityLayer() {
		return namedEntityLayer;
	}

	@Param(nameType = NameType.FEATURE)
	public String getNamedEntityTypeFeature() {
		return namedEntityTypeFeature;
	}

	@Param(nameType = NameType.RELATION)
	public String getDependencyRelation() {
		return dependencyRelation;
	}

	@Param(nameType = NameType.ARGUMENT)
	public String getDependencySentenceRole() {
		return dependencySentenceRole;
	}

	@Param(nameType = NameType.ARGUMENT)
	public String getHeadRole() {
		return headRole;
	}

	@Param(nameType = NameType.ARGUMENT)
	public String getDependentRole() {
		return dependentRole;
	}

	@Param(nameType = NameType.FEATURE)
	public String getDependencyLabelFeature() {
		return dependencyLabelFeature;
	}

	@Param
	public Boolean getNer() {
		return ner;
	}

	@Param
	public Boolean getParse() {
		return parse;
	}

	@Param
	public Boolean getPretokenized() {
		return pretokenized;
	}

	@Param
	public Mapping getPipelineProperties() {
		return pipelineProperties;
	}

	public void setWordLayer(String wordLayer) {
		this.wordLayer = wordLayer;
	}

	public void setSentenceLayer(String sentenceLayer) {
		this.sentenceLayer = sentenceLayer;
	}

	public void setPosFeature(String posFeature) {
		this.posFeature = posFeature;
	}

	public void setLemmaFeature(String lemmaFeature) {
		this.lemmaFeature = lemmaFeature;
	}

	public void setNamedEntityLayer(String namedEntityLayer) {
		this.namedEntityLayer = namedEntityLayer;
	}

	public void setNamedEntityTypeFeature(String namedEntityTypeFeature) {
		this.namedEntityTypeFeature = namedEntityTypeFeature;
	}

	public void setDependencyRelation(String dependencyRelation) {
		this.dependencyRelation = dependencyRelation;
	}

	public void setDependencySentenceRole(String sentenceRole) {
		this.dependencySentenceRole = sentenceRole;
	}

	public void setHeadRole(String headRole) {
		this.headRole = headRole;
	}

	public void setDependentRole(String dependentRole) {
		this.dependentRole = dependentRole;
	}

	public void setDependencyLabelFeature(String dependencyLabelFeature) {
		this.dependencyLabelFeature = dependencyLabelFeature;
	}

	public void setNer(Boolean ner) {
		this.ner = ner;
	}

	public void setParse(Boolean parse) {
		this.parse = parse;
	}

	public void setPretokenized(Boolean pretokenized) {
		this.pretokenized = pretokenized;
	}

	public void setPipelineProperties(Mapping pipelineProperties) {
		this.pipelineProperties = pipelineProperties;
	}
}
