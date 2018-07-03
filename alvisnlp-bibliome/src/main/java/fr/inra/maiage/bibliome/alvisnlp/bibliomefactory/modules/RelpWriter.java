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


package fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules;

import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Logger;

import fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.RelpWriter.RelpWriterResolvedObjects;
import fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.SectionModule.SectionResolvedObjects;

import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Annotation;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Corpus;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.DefaultNames;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.DownCastElement;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Layer;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.NameType;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Relation;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Section;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Tuple;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.EvaluationContext;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.Evaluator;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.Expression;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.ResolverException;
import fr.inra.maiage.bibliome.alvisnlp.core.module.ModuleException;
import fr.inra.maiage.bibliome.alvisnlp.core.module.NameUsage;
import fr.inra.maiage.bibliome.alvisnlp.core.module.ProcessingContext;
import fr.inra.maiage.bibliome.alvisnlp.core.module.ProcessingException;
import fr.inra.maiage.bibliome.alvisnlp.core.module.TimerCategory;
import fr.inra.maiage.bibliome.alvisnlp.core.module.lib.AlvisNLPModule;
import fr.inra.maiage.bibliome.alvisnlp.core.module.lib.Param;
import fr.inra.maiage.bibliome.alvisnlp.core.module.lib.TimeThis;
import fr.inra.maiage.bibliome.util.Iterators;
import fr.inra.maiage.bibliome.util.Timer;
import fr.inra.maiage.bibliome.util.defaultmap.DefaultMap;
import fr.inra.maiage.bibliome.util.streams.TargetStream;

@AlvisNLPModule
public class RelpWriter extends SectionModule<RelpWriterResolvedObjects> {
	private TargetStream outFile;
	private String sentenceLayer = DefaultNames.getSentenceLayer();
	private String wordLayer = DefaultNames.getWordLayer();
	private String dependencyRelation = DefaultNames.getDependencyRelationName();
	private String linkageNumberFeature;
	private String sentenceRole = DefaultNames.getDependencySentenceRole();
	private String dependencyLabelFeature = DefaultNames.getDependencyLabelFeatureName();
	private String headRole = DefaultNames.getDependencyHeadRole();
	private String dependentRole = DefaultNames.getDependencyDependentRole();
	private Expression pmid = DefaultExpressions.DOCUMENT_ID;

	private Expression wordForm = DefaultExpressions.ANNOTATION_FORM;
	private Expression lemmaForm = DefaultExpressions.WORD_LEMMA;
	private Expression headForm = DefaultExpressions.ANNOTATION_FORM;
	private Expression dependentForm = DefaultExpressions.ANNOTATION_FORM;

	static class RelpWriterResolvedObjects extends SectionResolvedObjects {
		private final Evaluator pmid;
		private final Evaluator wordForm;
		private final Evaluator lemmaForm;
		private final Evaluator headForm;
		private final Evaluator dependentForm;
		
		private RelpWriterResolvedObjects(ProcessingContext<Corpus> ctx, RelpWriter module) throws ResolverException {
			super(ctx, module);
			pmid = module.pmid.resolveExpressions(rootResolver);
			wordForm = module.wordForm.resolveExpressions(rootResolver);
			lemmaForm = module.lemmaForm.resolveExpressions(rootResolver);
			headForm = module.headForm.resolveExpressions(rootResolver);
			dependentForm = module.dependentForm.resolveExpressions(rootResolver);
		}

		@Override
		public void collectUsedNames(NameUsage nameUsage, String defaultType) throws ModuleException {
			super.collectUsedNames(nameUsage, defaultType);
			pmid.collectUsedNames(nameUsage, defaultType);
			wordForm.collectUsedNames(nameUsage, defaultType);
			lemmaForm.collectUsedNames(nameUsage, defaultType);
			headForm.collectUsedNames(nameUsage, defaultType);
			dependentForm.collectUsedNames(nameUsage, defaultType);
		}
	}
	
	@Override
	protected RelpWriterResolvedObjects createResolvedObjects(ProcessingContext<Corpus> ctx) throws ResolverException {
		return new RelpWriterResolvedObjects(ctx, this);
	}

	@Override
	protected String[] addFeaturesToSectionFilter() {
		return null;
	}

	@Override
	protected String[] addLayersToSectionFilter() {
		return new String[] { sentenceLayer, wordLayer };
	}
	
	@Override
	public void process(ProcessingContext<Corpus> ctx, Corpus corpus) throws ModuleException {
		Logger logger = getLogger(ctx);
		try {
			PrintStream out = outFile.getPrintStream();
			EvaluationContext evalCtx = new EvaluationContext(logger);
			int sentNum = 0;
			Timer<TimerCategory> indexTimer = getTimer(ctx, "relation-index", TimerCategory.PREPARE_DATA, false);
			for (Section sec : Iterators.loop(sectionIterator(evalCtx, corpus))) {
				//logger.fine(sec.toString());
				Relation rel = sec.getRelation(dependencyRelation);
				if (rel == null) {
					logger.fine("no dependencies");
					continue;
				}
				indexTimer.start();
				RelationIndex relIndex = new RelationIndex(rel);
				indexTimer.stop();
				for (Layer sentence : sec.getSentences(wordLayer, sentenceLayer)) {
					Annotation sentenceAnnotation = sentence.getSentenceAnnotation();
					if (!relIndex.containsKey(sentenceAnnotation)) {
						logger.warning("linkage on an unknown sentence: '" + sentenceAnnotation + "'");
						continue;
					}
					printSentenceHeader(ctx, out, evalCtx, sentence, ++sentNum);
					indexTimer.start();
					Map<Annotation,Integer> wordIndex = getWordIndex(sentence);
					indexTimer.stop();
					LinkageIndex linkIndex = relIndex.get(sentence.getSentenceAnnotation());
					for (Map.Entry<Integer,Collection<Tuple>> e : linkIndex.entrySet())
						printLinkage(ctx, out, wordIndex, e.getKey(), e.getValue(), evalCtx);
				}
			}
			out.close();
		}
		catch (IOException e) {
			throw new ProcessingException(e);
		}
	}
	
	private final class RelationIndex extends DefaultMap<Annotation,LinkageIndex> {
		private RelationIndex(Relation rel) {
			super(true, new LinkedHashMap<Annotation,LinkageIndex>());
			for (Tuple t : rel.getTuples()) {
				Annotation sentence = DownCastElement.toAnnotation(t.getArgument(sentenceRole));
				DefaultMap<Integer,Collection<Tuple>> sentLinkages = safeGet(sentence);
				Integer linkage = linkageNumberFeature == null ? 0 : Integer.parseInt(t.getLastFeature(linkageNumberFeature));
				sentLinkages.safeGet(linkage).add(t);
			}
		}

		@Override
		protected LinkageIndex defaultValue(Annotation key) {
			return new LinkageIndex();
		}
	}
	
	private static final class LinkageIndex extends DefaultMap<Integer,Collection<Tuple>> {
		private LinkageIndex() {
			super(true, new TreeMap<Integer,Collection<Tuple>>());
		}

		@Override
		protected Collection<Tuple> defaultValue(Integer key) {
			return new ArrayList<Tuple>();
		}
	}
	
	private static Map<Annotation,Integer> getWordIndex(Layer sentence) {
		Map<Annotation,Integer> result = new LinkedHashMap<Annotation,Integer>();
		for (int i = 0; i < sentence.size(); ++i)
			result.put(sentence.get(i), i);
		return result;
	}

	@TimeThis(task="write-relp", category=TimerCategory.EXPORT)
	protected void printSentenceHeader(@SuppressWarnings("unused") ProcessingContext<Corpus> ctx, PrintStream out, EvaluationContext evalCtx, Layer sentence, int sentNum) {
		RelpWriterResolvedObjects resObj = getResolvedObjects();
		out.println("PMID\t" + resObj.pmid.evaluateString(evalCtx, sentence.getSection().getDocument()));
    	out.println("Sentence " + sentNum);
    	out.println(sentence.getSentenceAnnotation().getForm().replaceAll("\n", ""));
    	out.println("Words");
    	for (Annotation word : sentence) {
    		out.print('(');
    		out.print(resObj.wordForm.evaluateString(evalCtx, word));
    		out.print(')');
    	}
    	out.println();
    	out.println("Lemma");
    	for (Annotation word : sentence) {
    		out.print('(');
    		out.print(resObj.lemmaForm.evaluateString(evalCtx, word));
    		out.print(')');
    	}
    	out.println();
    	out.println();
	}
	
	@TimeThis(task="write-relp", category=TimerCategory.EXPORT)
	protected void printLinkage(@SuppressWarnings("unused") ProcessingContext<Corpus> ctx, PrintStream out, Map<Annotation,Integer> wordIndex, int linkNum, Collection<Tuple> tuples, EvaluationContext evalCtx) {
		RelpWriterResolvedObjects resObj = getResolvedObjects();
		out.println("Parse " + linkNum);
		for (Tuple dep : tuples) {
			Annotation head = DownCastElement.toAnnotation(dep.getArgument(headRole));
			Annotation dependent = DownCastElement.toAnnotation(dep.getArgument(dependentRole));
			out.print(dep.getLastFeature(dependencyLabelFeature));
			out.print(" (");
			out.print(resObj.headForm.evaluateString(evalCtx, head));
			out.print(',');
			out.print(resObj.dependentForm.evaluateString(evalCtx, dependent));
			out.print(") (");
			out.print(wordIndex.get(head));
			out.print(',');
			out.print(wordIndex.get(dependent));
			out.println(")");
		}
		out.println();
	}

	@Param(defaultDoc = "File where to write the dependencies.")
	public TargetStream getOutFile() {
		return outFile;
	}

	@Param(nameType=NameType.LAYER, defaultDoc = "Name of the layer containing sentence annotations.")
	public String getSentenceLayer() {
		return sentenceLayer;
	}

	@Param(nameType=NameType.LAYER, defaultDoc = "Name of the layer containing word annotations.")
	public String getWordLayer() {
		return wordLayer;
	}

	@Param(nameType=NameType.RELATION, defaultDoc = "Name of the dependecy relation.")
	public String getDependencyRelation() {
		return dependencyRelation;
	}

	@Param(nameType=NameType.FEATURE, defaultDoc = "Feature containing the linkage number to which a dependency belongs.", mandatory=false)
	public String getLinkageNumberFeature() {
		return linkageNumberFeature;
	}

	@Param(nameType=NameType.ARGUMENT, defaultDoc = "Name of the role of the parsed sentence in the dependency relation.")
	public String getSentenceRole() {
		return sentenceRole;
	}

	@Param(nameType=NameType.FEATURE, defaultDoc = "Feature containing the dependency label.")
	public String getDependencyLabelFeature() {
		return dependencyLabelFeature;
	}

	@Param(nameType=NameType.ARGUMENT, defaultDoc = "Name of the role of the head word.")
	public String getHeadRole() {
		return headRole;
	}

	@Param(nameType=NameType.ARGUMENT, defaultDoc = "Name of the role of the modifier word.")
	public String getDependentRole() {
		return dependentRole;
	}

	@Param
	public Expression getPmid() {
		return pmid;
	}

	@Param
	public Expression getWordForm() {
		return wordForm;
	}

	@Param
	public Expression getLemmaForm() {
		return lemmaForm;
	}

	@Param
	public Expression getDependentForm() {
		return dependentForm;
	}

	@Param
	public Expression getHeadForm() {
		return headForm;
	}

	public void setHeadForm(Expression headForm) {
		this.headForm = headForm;
	}

	public void setDependentForm(Expression dependentForm) {
		this.dependentForm = dependentForm;
	}

	public void setWordForm(Expression wordForm) {
		this.wordForm = wordForm;
	}

	public void setLemmaForm(Expression lemmaForm) {
		this.lemmaForm = lemmaForm;
	}

	public void setPmid(Expression pmid) {
		this.pmid = pmid;
	}

	public void setOutFile(TargetStream outFile) {
		this.outFile = outFile;
	}

	public void setSentenceLayer(String sentenceLayer) {
		this.sentenceLayer = sentenceLayer;
	}

	public void setWordLayer(String wordLayer) {
		this.wordLayer = wordLayer;
	}

	public void setDependencyRelation(String dependencyRelation) {
		this.dependencyRelation = dependencyRelation;
	}

	public void setLinkageNumberFeature(String linkageNumberFeature) {
		this.linkageNumberFeature = linkageNumberFeature;
	}

	public void setSentenceRole(String sentenceRole) {
		this.sentenceRole = sentenceRole;
	}

	public void setDependencyLabelFeature(String dependencyLabelFeature) {
		this.dependencyLabelFeature = dependencyLabelFeature;
	}

	public void setHeadRole(String headRole) {
		this.headRole = headRole;
	}

	public void setDependentRole(String dependentRole) {
		this.dependentRole = dependentRole;
	}
}
