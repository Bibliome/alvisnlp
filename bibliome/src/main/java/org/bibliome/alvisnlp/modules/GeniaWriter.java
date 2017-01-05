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
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.bibliome.alvisnlp.modules.GeniaWriter.GeniaWriterResolvedObjects;
import org.bibliome.alvisnlp.modules.SectionModule.SectionResolvedObjects;
import org.bibliome.util.Iterators;
import org.bibliome.util.Pair;
import org.bibliome.util.files.OutputDirectory;
import org.bibliome.util.files.OutputFile;
import org.bibliome.util.streams.FileTargetStream;
import org.bibliome.util.streams.TargetStream;

import alvisnlp.corpus.Annotation;
import alvisnlp.corpus.Corpus;
import alvisnlp.corpus.DefaultNames;
import alvisnlp.corpus.DownCastElement;
import alvisnlp.corpus.Element;
import alvisnlp.corpus.NameType;
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
import alvisnlp.module.types.EvaluatorMapping;
import alvisnlp.module.types.ExpressionMapping;

@AlvisNLPModule(obsoleteUseInstead=TabularExport.class)
public class GeniaWriter extends SectionModule<GeniaWriterResolvedObjects> {
	private ExpressionMapping entities;
	private Expression entityForm = DefaultExpressions.ANNOTATION_FORM;
	private ExpressionMapping events;
	private Expression words;
	private Expression wordForm = DefaultExpressions.ANNOTATION_FORM;
	private Expression sentences;
	private Expression sentenceForm = DefaultExpressions.ANNOTATION_FORM;
	private Expression dependencies;
	private OutputDirectory outputDir;
	private Expression fileName;
	private String labelFeature = DefaultNames.getDependencyLabelFeatureName();
	private Expression eventExtra;
	
	@SuppressWarnings("hiding")
	class GeniaWriterResolvedObjects extends SectionResolvedObjects {
		private final EvaluatorMapping entities;
		private final Evaluator entityForm;
		private final EvaluatorMapping events;
		private final Evaluator words;
		private final Evaluator wordForm;
		private final Evaluator sentences;
		private final Evaluator sentenceForm;
		private final Evaluator dependencies;
		private final Evaluator fileName;
		private final Evaluator eventExtra;
	
		private GeniaWriterResolvedObjects(ProcessingContext<Corpus> ctx) throws ResolverException {
			super(ctx, GeniaWriter.this);
			entities = rootResolver.resolveNullable(GeniaWriter.this.entities);
			entityForm = rootResolver.resolveNullable(GeniaWriter.this.entityForm);
			events = rootResolver.resolveNullable(GeniaWriter.this.events);
			words = rootResolver.resolveNullable(GeniaWriter.this.words);
			wordForm = rootResolver.resolveNullable(GeniaWriter.this.wordForm);
			sentences = rootResolver.resolveNullable(GeniaWriter.this.sentences);
			sentenceForm = rootResolver.resolveNullable(GeniaWriter.this.sentenceForm);
			dependencies = rootResolver.resolveNullable(GeniaWriter.this.dependencies);
			fileName = rootResolver.resolveNullable(GeniaWriter.this.fileName);
			eventExtra = rootResolver.resolveNullable(GeniaWriter.this.eventExtra);
		}

		@Override
		public void collectUsedNames(NameUsage nameUsage, String defaultType) throws ModuleException {
			super.collectUsedNames(nameUsage, defaultType);
			entities.collectUsedNames(nameUsage, defaultType);
			entityForm.collectUsedNames(nameUsage, defaultType);
			events.collectUsedNames(nameUsage, defaultType);
			words.collectUsedNames(nameUsage, defaultType);
			wordForm.collectUsedNames(nameUsage, defaultType);
			sentences.collectUsedNames(nameUsage, defaultType);
			sentenceForm.collectUsedNames(nameUsage, defaultType);
			dependencies.collectUsedNames(nameUsage, defaultType);
			eventExtra.collectUsedNames(nameUsage, defaultType);
		}
	}
	
	@Override
	protected GeniaWriterResolvedObjects createResolvedObjects(ProcessingContext<Corpus> ctx) throws ResolverException {
		return new GeniaWriterResolvedObjects(ctx);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void process(ProcessingContext<Corpus> ctx, Corpus corpus) throws ModuleException {
		try {
			GeniaWriterResolvedObjects resObj = getResolvedObjects();
			Logger logger = getLogger(ctx);
			EvaluationContext evalCtx = new EvaluationContext(logger);
			outputDir.mkdirs();
			for (Section sec : Iterators.loop(sectionIterator(evalCtx, corpus))) {
				OutputFile outFile = new OutputFile(outputDir, resObj.fileName.evaluateString(evalCtx, sec) + ".a2");
				TargetStream target = new FileTargetStream("US-ASCII", outFile);
				PrintStream out = target.getPrintStream();
				
				Map<Annotation,String> entitiesMap = new HashMap<Annotation,String>();
				if (entities != null)
					for (Map.Entry<String,Evaluator> e : resObj.entities.entrySet())
						printEntities(ctx, evalCtx, out, sec, "T", entitiesMap, e.getKey(), e.getValue(), resObj.entityForm);
				
				Map<Annotation,String> segmentation = new HashMap<Annotation,String>();
				if (words != null)
					printEntities(ctx, evalCtx, out, sec, "W", segmentation, "Word", resObj.words, resObj.wordForm);

				if (sentences != null)
					printEntities(ctx, evalCtx, out, sec, "S", segmentation, "Sentence", resObj.sentences, resObj.sentenceForm);
				
				if ((entities != null) && (events != null)) {
					Map<Tuple,String> eventMap = new HashMap<Tuple,String>();
					List<Pair<String,Tuple>> allEvents = new ArrayList<Pair<String,Tuple>>();
					int n = 0;
					for (Map.Entry<String,Evaluator> e : resObj.events.entrySet()) {
						String type = e.getKey();
						for (Element elt : Iterators.loop(e.getValue().evaluateElements(evalCtx, sec))) {
							Tuple t = DownCastElement.toTuple(elt);
							if (t == null)
								continue;
							allEvents.add(new Pair<String,Tuple>(type, t));
							eventMap.put(t, "E" + Integer.toString(++n));
						}
					}
					for (Pair<String,Tuple> p : allEvents)
						if (eventExtra == null)
							printEvent(ctx, out, p.second, entitiesMap, eventMap, p.first, eventMap.get(p.second));
						else
							printEvent(ctx, out, p.second, entitiesMap, eventMap, p.first, eventMap.get(p.second), resObj.eventExtra.evaluateString(evalCtx, p.second));
				}
				
				if ((words != null) && (dependencies != null) && (sentences != null)) {
					int n = 0;
					for (Element elt : Iterators.loop(resObj.dependencies.evaluateElements(evalCtx, sec)))
						printEvent(ctx, out, elt, segmentation, Collections.EMPTY_MAP, "Dependency", "R" + (++n), "Label:" + elt.getLastFeature(labelFeature));
				}
				
				out.close();
			}
		}
		catch (IOException ioe) {
			rethrow(ioe);
		}
	}
	
	private boolean printEvent(ProcessingContext<Corpus> ctx, PrintStream out, Element elt, Map<Annotation,String> entities, Map<Tuple,String> events, String type, String id, String... more) {
		Tuple t = DownCastElement.toTuple(elt);
		if (t == null) {
			getLogger(ctx).warning("event of type " + type + " is not a tuple: " + t);
			return true;
		}
		out.print(id + '\t' + type);
		for (String role : t.getRoles()) {
			Element arg = t.getArgument(role);
			String ref = entities.containsKey(arg) ? entities.get(arg) : events.get(arg);
			out.print(" " + role + ':' + ref);
		}
		for (String s : more) {
			out.print(' ');
			out.print(s);
		}
		out.println();
		return true;
	}

	private void printEntities(ProcessingContext<Corpus> ctx, EvaluationContext evalCtx, PrintStream out, Section sec, String idPrefix, Map<Annotation,String> entities, String type, Evaluator expr, Evaluator form) {
		for (Element elt : Iterators.loop(expr.evaluateElements(evalCtx, sec))) {
			Annotation a = DownCastElement.toAnnotation(elt);
			if (a == null) {
				getLogger(ctx).warning("entity of type " + type + " is not an annotation: " + a);
				continue;
			}
			if (entities.containsKey(a)) {
				getLogger(ctx).warning("duplicate entity: " + a);
				continue;
			}
			String id = idPrefix + (entities.size() + 1);
			entities.put(a, id);
			out.println(id + '\t' + type + ' ' + a.getStart() + ' ' + a.getEnd() + '\t' + form.evaluateString(evalCtx, a).replace('\n', ' '));
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

	@Param
	public ExpressionMapping getEntities() {
		return entities;
	}

	@Param
	public ExpressionMapping getEvents() {
		return events;
	}

	@Param
	public OutputDirectory getOutputDir() {
		return outputDir;
	}

	@Param
	public Expression getFileName() {
		return fileName;
	}

	@Param(mandatory=false)
	public Expression getWords() {
		return words;
	}

	@Param(mandatory=false)
	public Expression getDependencies() {
		return dependencies;
	}

	@Param(mandatory=false)
	public Expression getSentences() {
		return sentences;
	}

	@Param(nameType=NameType.FEATURE, mandatory=false)
	public String getLabelFeature() {
		return labelFeature;
	}

	@Param
	public Expression getEntityForm() {
		return entityForm;
	}

	@Param
	public Expression getWordForm() {
		return wordForm;
	}

	@Param
	public Expression getSentenceForm() {
		return sentenceForm;
	}

	@Param(mandatory=false)
	public Expression getEventExtra() {
		return eventExtra;
	}

	public void setEventExtra(Expression eventExtra) {
		this.eventExtra = eventExtra;
	}

	public void setEntityForm(Expression entityForm) {
		this.entityForm = entityForm;
	}

	public void setWordForm(Expression wordForm) {
		this.wordForm = wordForm;
	}

	public void setSentenceForm(Expression sentenceForm) {
		this.sentenceForm = sentenceForm;
	}

	public void setLabelFeature(String labelFeature) {
		this.labelFeature = labelFeature;
	}

	public void setSentences(Expression sentences) {
		this.sentences = sentences;
	}

	public void setWords(Expression words) {
		this.words = words;
	}

	public void setDependencies(Expression dependencies) {
		this.dependencies = dependencies;
	}

	public void setEntities(ExpressionMapping entities) {
		this.entities = entities;
	}

	public void setEvents(ExpressionMapping events) {
		this.events = events;
	}

	public void setOutputDir(OutputDirectory outputDir) {
		this.outputDir = outputDir;
	}

	public void setFileName(Expression fileName) {
		this.fileName = fileName;
	}
}
