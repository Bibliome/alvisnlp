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


package org.bibliome.alvisnlp.modules;

import java.io.IOException;
import java.io.PrintStream;
import java.util.Collection;
import java.util.List;
import java.util.logging.Logger;

import org.bibliome.alvisnlp.modules.SectionModule.SectionResolvedObjects;
import org.bibliome.alvisnlp.modules.WhatsWrongExport.WhatsWrongExportResolvedObjects;
import org.bibliome.util.Iterators;
import org.bibliome.util.Strings;
import org.bibliome.util.defaultmap.DefaultArrayListHashMap;
import org.bibliome.util.defaultmap.DefaultMap;
import org.bibliome.util.defaultmap.IndexHashMap;
import org.bibliome.util.streams.TargetStream;

import alvisnlp.corpus.Annotation;
import alvisnlp.corpus.Corpus;
import alvisnlp.corpus.DefaultNames;
import alvisnlp.corpus.DownCastElement;
import alvisnlp.corpus.Element;
import alvisnlp.corpus.Layer;
import alvisnlp.corpus.NameType;
import alvisnlp.corpus.Relation;
import alvisnlp.corpus.Section;
import alvisnlp.corpus.Tuple;
import alvisnlp.corpus.expressions.EvaluationContext;
import alvisnlp.corpus.expressions.Evaluator;
import alvisnlp.corpus.expressions.Expression;
import alvisnlp.corpus.expressions.ResolverException;
import alvisnlp.module.ModuleException;
import alvisnlp.module.NameUsage;
import alvisnlp.module.ProcessingContext;
import alvisnlp.module.lib.AlvisNLPModule;
import alvisnlp.module.lib.Param;

@AlvisNLPModule
public class WhatsWrongExport extends SectionModule<WhatsWrongExportResolvedObjects> {
	private Expression sentences;
	private String words = DefaultNames.getWordLayer();
	private String wordForm = Annotation.FORM_FEATURE_NAME;
	private String[] entities;
	private String entityType;
	private String relationName;
	private String sentence = DefaultNames.getDependencySentenceRole();
	private String head = DefaultNames.getDependencyHeadRole();
	private String dependent = DefaultNames.getDependencyDependentRole();
	private String label = DefaultNames.getDependencyLabelFeatureName();
	private TargetStream outFile;
	
	static class WhatsWrongExportResolvedObjects extends SectionResolvedObjects {
		private final Evaluator sentences;
		
		private WhatsWrongExportResolvedObjects(ProcessingContext<Corpus> ctx, WhatsWrongExport module) throws ResolverException {
			super(ctx, module);
			sentences = module.sentences.resolveExpressions(rootResolver);
		}

		@Override
		public void collectUsedNames(NameUsage nameUsage, String defaultType) throws ModuleException {
			super.collectUsedNames(nameUsage, defaultType);
			sentences.collectUsedNames(nameUsage, defaultType);
		}
	}
	
	@Override
	protected WhatsWrongExportResolvedObjects createResolvedObjects(ProcessingContext<Corpus> ctx) throws ResolverException {
		return new WhatsWrongExportResolvedObjects(ctx, this);
	}

	@Override
	public void process(ProcessingContext<Corpus> ctx, Corpus corpus) throws ModuleException {
		WhatsWrongExportResolvedObjects resObj = getResolvedObjects();
		Logger logger = getLogger(ctx);
		EvaluationContext evalCtx = new EvaluationContext(logger);
		try(PrintStream out = outFile.getPrintStream()) {
			for (Section sec : Iterators.loop(sectionIterator(evalCtx, corpus))) {
				DefaultMap<Annotation,List<Tuple>> dependencies = getDependencies(sec);
				for (Element sent : Iterators.loop(resObj.sentences.evaluateElements(evalCtx, sec))) {
					Annotation sentAnnot = DownCastElement.toAnnotation(sent);
					if (sentAnnot != null)
						processSentence(out, logger, dependencies.safeGet(sentAnnot), sec, sentAnnot);
				}
			}
		}
		catch (IOException ioe) {
			rethrow(ioe);
		}
	}
	
	private void processSentence(PrintStream out, Logger logger, Collection<Tuple> dependencies, Section sec, Annotation sent) {
		out.println(">>");
		out.println(">Word");
		IndexHashMap<Annotation> wordIndex = new IndexHashMap<Annotation>();
		Layer wordLayer = sec.getLayer(words);
		StringBuilder sb = new StringBuilder();
		for (Annotation w : wordLayer.between(sent)) {
			sb.setLength(0);
			Strings.escapeWhitespaces(sb, w.getLastFeature(wordForm));
			out.format("%d\t\"%s\"\n", wordIndex.safeGet(w), sb);
		}
		if (entities != null) {
			for (String name : entities) {
				Layer entityLayer = sec.ensureLayer(name);
				out.println(">Entities");
				for (Annotation e : entityLayer.between(sent)) {
					Layer includedWords = wordLayer.overlapping(e);
					if (includedWords.isEmpty()) {
						logger.warning("entity " + e + " dos not include any word");
						continue;
					}
					if (includedWords.size() == 1) {
						out.format("%d\t\"%s\"\n", wordIndex.safeGet(includedWords.first()), e.getLastFeature(entityType));
					}
					else {
						out.format("%d\t%d\t\"%s\"\n", wordIndex.safeGet(includedWords.first()), wordIndex.safeGet(includedWords.last()), e.getLastFeature(entityType));
					}
				}
			}
		}
		out.println(">Relations");
		for (Tuple t : dependencies) {
			if (!t.hasArgument(head)) {
				logger.warning("dependency without head");
				continue;
			}
			if (!t.hasArgument(dependent)) {
				logger.warning("dependency without dependent");
				continue;
			}
			Annotation headWord = DownCastElement.toAnnotation(t.getArgument(head));
			Annotation dependentWord = DownCastElement.toAnnotation(t.getArgument(dependent));
			if (!wordLayer.contains(headWord)) {
				logger.warning("head not in the word layer");
				continue;
			}
			if (!wordLayer.contains(dependentWord)) {
				logger.warning("dependent not in the word layer");
				continue;
			}
			if (!wordIndex.containsKey(headWord)) {
				logger.warning("head outisde sentence");
				continue;
			}
			if (!wordIndex.containsKey(dependentWord)) {
				logger.warning("dependent outisde sentence");
				continue;
			}
			int h = wordIndex.get(headWord);
			int d = wordIndex.get(dependentWord);
			out.format("%d\t%d\t\"%s\"\n", h, d, t.getLastFeature(label));
		}
	}

	private DefaultMap<Annotation,List<Tuple>> getDependencies(Section sec) {
		DefaultMap<Annotation,List<Tuple>> result = new DefaultArrayListHashMap<Annotation,Tuple>();
		if (sec.hasRelation(relationName)) {
			Relation rel = sec.getRelation(relationName);
			for (Tuple t : rel.getTuples())
				if (t.hasArgument(sentence))
					result.safeGet(DownCastElement.toAnnotation(t.getArgument(sentence))).add(t);
		}
		return result;
	}

	@Override
	protected String[] addLayersToSectionFilter() {
		return new String[] { words };
	}

	@Override
	protected String[] addFeaturesToSectionFilter() {
		return null;
	}

	@Param
	public Expression getSentences() {
		return sentences;
	}

	@Param(nameType=NameType.LAYER)
	public String getWords() {
		return words;
	}

	@Param(nameType=NameType.FEATURE)
	public String getWordForm() {
		return wordForm;
	}

	@Param(nameType=NameType.RELATION)
	public String getRelationName() {
		return relationName;
	}

	@Param(nameType=NameType.RELATION)
	public String getSentence() {
		return sentence;
	}

	@Param(nameType=NameType.ARGUMENT)
	public String getHead() {
		return head;
	}

	@Param(nameType=NameType.ARGUMENT)
	public String getDependent() {
		return dependent;
	}

	@Param(nameType=NameType.FEATURE)
	public String getLabel() {
		return label;
	}

	@Param
	public TargetStream getOutFile() {
		return outFile;
	}

	@Param(mandatory=false, nameType=NameType.FEATURE)
	public String getEntityType() {
		return entityType;
	}

	@Param(mandatory=false, nameType=NameType.LAYER)
	public String[] getEntities() {
		return entities;
	}

	public void setEntities(String[] entities) {
		this.entities = entities;
	}

	public void setEntityType(String entityType) {
		this.entityType = entityType;
	}

	public void setSentences(Expression sentences) {
		this.sentences = sentences;
	}

	public void setWords(String words) {
		this.words = words;
	}

	public void setWordForm(String wordForm) {
		this.wordForm = wordForm;
	}

	public void setRelationName(String relationName) {
		this.relationName = relationName;
	}

	public void setSentence(String sentence) {
		this.sentence = sentence;
	}

	public void setHead(String head) {
		this.head = head;
	}

	public void setDependent(String dependent) {
		this.dependent = dependent;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public void setOutFile(TargetStream outFile) {
		this.outFile = outFile;
	}
}
