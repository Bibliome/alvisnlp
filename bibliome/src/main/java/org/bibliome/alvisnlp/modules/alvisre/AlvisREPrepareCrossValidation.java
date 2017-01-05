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


package org.bibliome.alvisnlp.modules.alvisre;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.logging.Logger;

import org.bibliome.alvisnlp.modules.alvisre.AbstractAlvisRE.AlvisREResolvedObjects;
import org.bibliome.alvisnlp.modules.alvisre.AlvisREPrepareCrossValidation.AlvisRETrainResolvedObjects;
import org.bibliome.util.Iterators;
import org.bibliome.util.files.OutputDirectory;
import org.bibliome.util.files.OutputFile;
import org.bibliome.util.streams.FileTargetStream;
import org.bibliome.util.streams.TargetStream;
import org.bibliome.util.xml.XMLUtils;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import alvisnlp.corpus.Corpus;
import alvisnlp.corpus.Section;
import alvisnlp.corpus.expressions.EvaluationContext;
import alvisnlp.corpus.expressions.ResolverException;
import alvisnlp.module.ModuleException;
import alvisnlp.module.NameUsage;
import alvisnlp.module.ProcessingContext;
import alvisnlp.module.lib.AlvisNLPModule;
import alvisnlp.module.lib.Param;

@AlvisNLPModule(beta=true)
public abstract class AlvisREPrepareCrossValidation extends AbstractAlvisRE<AlvisRETrainResolvedObjects> {
	private OutputDirectory outDir;
	private String sectionSeparator = "\n";
	private Double cParameter;
	private DocumentFragment schema;
	private DocumentFragment similarityFunction;
	private Integer folds = 10;
	
	private AlvisRETokens sentences = AlvisRETokens.getSentencesAlvisRETokens();
	private AlvisRETokens words = AlvisRETokens.getWordsAlvisRETokens();
	private AlvisRETokens[] terms;
	private AlvisRERelations dependencies = AlvisRERelations.getDependenciesAlvisRERelation();
	private AlvisRERelations[] relations;
	
	@Override
	public void process(ProcessingContext<Corpus> ctx, Corpus corpus) throws ModuleException {
		try {
			if (!outDir.exists() && !outDir.mkdirs()) {
				processingException("could not create " + outDir);
			}
			Logger logger = getLogger(ctx);
			EvaluationContext evalCtx = new EvaluationContext(logger);
			writeConfigurationFile(ctx, outDir);
			File inputDir = new File(outDir, "input");
			@SuppressWarnings("unused")
			SectionsMerger merger = writeInputFiles(logger, evalCtx, corpus, inputDir);
		}
		catch (SAXException|IOException e) {
			rethrow(e);
		}
	}

	public static class AlvisRETrainResolvedObjects extends AlvisREResolvedObjects {
		private final AlvisRETokens.Resolved sentences;
		private final AlvisRETokens.Resolved words;
		private final AlvisRETokens.Resolved[] terms;
		private final AlvisRERelations.Resolved dependencies;
		private final AlvisRERelations.Resolved[] relations;

		private AlvisRETrainResolvedObjects(ProcessingContext<Corpus> ctx, AlvisREPrepareCrossValidation module) throws ResolverException {
			super(ctx, module);
			this.sentences = module.sentences.resolveExpressions(rootResolver);
			this.words = module.words.resolveExpressions(rootResolver);
			this.terms = rootResolver.resolveArray(module.terms, AlvisRETokens.Resolved.class);
			this.dependencies = module.dependencies.resolveExpressions(rootResolver);
			this.relations = rootResolver.resolveArray(module.relations, AlvisRERelations.Resolved.class);
		}

		@Override
		public void collectUsedNames(NameUsage nameUsage, String defaultType) throws ModuleException {
			super.collectUsedNames(nameUsage, defaultType);
			sentences.collectUsedNames(nameUsage, defaultType);
			words.collectUsedNames(nameUsage, defaultType);
			nameUsage.collectUsedNamesArray(terms, defaultType);
			dependencies.collectUsedNames(nameUsage, defaultType);
			nameUsage.collectUsedNamesArray(relations, defaultType);
		}
	}
	
	@Override
	protected String getActionString() {
		return "TrainAndTest";
	}

	@Override
	protected boolean isTrain() {
		return true;
	}

	@Override
	protected AlvisRETrainResolvedObjects createResolvedObjects(ProcessingContext<Corpus> ctx) throws ResolverException {
		return new AlvisRETrainResolvedObjects(ctx, this);
	}

	@Override
	protected void fillConfigParameters(ProcessingContext<Corpus> ctx, Element experimentElt) {
		Document doc = experimentElt.getOwnerDocument();
		experimentElt.appendChild(createWekaElement(doc));
		experimentElt.appendChild(doc.adoptNode(schema));
		experimentElt.appendChild(doc.adoptNode(similarityFunction));
		experimentElt.appendChild(createInputElement(doc));
		XMLUtils.createElement(doc, experimentElt, 0, "evaluation", "Weka");
		XMLUtils.createElement(doc, experimentElt, 0, "folds", folds.toString());
		XMLUtils.createElement(doc, experimentElt, 0, "runs", "1");
		experimentElt.appendChild(createOutputElement(doc));
		File inputDir = new File(outDir, "input");
		XMLUtils.createElement(doc, experimentElt, 0, "inputPath", inputDir.getAbsolutePath());
	}
	
	private Element createWekaElement(Document doc) {
		Element result = doc.createElement("Weka");
		XMLUtils.createElement(doc, result, 0, "Algorithm", "Advanced");
		XMLUtils.createElement(doc, result, 0, "Classifier", "LibSVM");
		XMLUtils.createElement(doc, result, 0, "Options", "-K 0 -C " + cParameter + " -W \"10 5 1\"");
		return result;
	}
	
	private Element createInputElement(Document doc) {
		Element result = doc.createElement("inputPath");
		File inputDir = new File(outDir, "input");
		result.setTextContent(inputDir.getAbsolutePath());
		return result;
	}
	
	private Element createOutputElement(Document doc) {
		Element result = doc.createElement("outputPath");
		File outputDir = new File(outDir, "output");
		result.setTextContent(outputDir.getAbsolutePath());
		return result;
	}
	
	private static void writeTxtFile(alvisnlp.corpus.Document doc, File outDir, SectionsMerger merger) throws IOException {
		OutputFile txtFile = new OutputFile(outDir, doc.getId() + ".txt");
		TargetStream txtStream = new FileTargetStream("UTF-8", txtFile);
		try (PrintStream out = txtStream.getPrintStream()) {
			out.print(merger.getContents());
		}
	}

	private void writeAFile(alvisnlp.corpus.Document doc, File outDir, SectionsMerger merger, EvaluationContext ctx) throws IOException {
		OutputFile aFile = new OutputFile(outDir, doc.getId() + ".a");
		TargetStream aStream = new FileTargetStream("UTF-8", aFile);
		try (PrintStream out = aStream.getPrintStream()) {
			do {
				addElements(merger, ctx);
				printElements(out, merger, ctx);
			} while (merger.nextSection());
		}
	}

	private void addElements(SectionsMerger merger, EvaluationContext ctx) {
		AlvisRETrainResolvedObjects resObj = getResolvedObjects();
		Section sec = merger.getSection();
		resObj.sentences.addElements(merger, ctx, sec);
		resObj.words.addElements(merger, ctx, sec);
		for (AlvisRETokens.Resolved t : resObj.terms) {
			t.addElements(merger, ctx, sec);
		}
		resObj.dependencies.addElements(merger, ctx, sec);
		for (AlvisRERelations.Resolved r : resObj.relations) {
			r.addElements(merger, ctx, sec);
		}
	}
	
	private void printElements(PrintStream out, SectionsMerger merger, EvaluationContext ctx) {
		AlvisRETrainResolvedObjects resObj = getResolvedObjects();
		resObj.sentences.printLines(out, merger, ctx, "Sentence");
		resObj.words.printLines(out, merger, ctx, "Word");
		for (AlvisRETokens.Resolved t : resObj.terms) {
			t.printLines(out, merger, ctx, null);
		}
		resObj.dependencies.printLines(out, merger, ctx, "Dependency");
		for (AlvisRERelations.Resolved r : resObj.relations) {
			r.printLines(out, merger, ctx, null);
		}
	}

	private SectionsMerger writeInputFiles(Logger logger, EvaluationContext ctx, Corpus corpus, File outDir) throws IOException {
		logger.info("writing input files in " + outDir.getAbsolutePath());
		SectionsMerger result = new SectionsMerger(this, sectionSeparator);
		for (alvisnlp.corpus.Document doc : Iterators.loop(documentIterator(ctx, corpus))) {
			if (result.setDocument(ctx, doc)) {
				writeAFile(doc, outDir, result, ctx);
				writeTxtFile(doc, outDir, result);
			}
		}
		return result;
	}

	@Param
	public OutputDirectory getOutDir() {
		return outDir;
	}

	@Param
	public String getSectionSeparator() {
		return sectionSeparator;
	}

	@Param
	public Double getcParameter() {
		return cParameter;
	}

	@Param
	public DocumentFragment getSchema() {
		return schema;
	}

	@Param
	public DocumentFragment getSimilarityFunction() {
		return similarityFunction;
	}

	@Param
	public Integer getFolds() {
		return folds;
	}

	@Param
	public AlvisRETokens getSentences() {
		return sentences;
	}

	@Param
	public AlvisRETokens getWords() {
		return words;
	}

	@Param
	public AlvisRETokens[] getTerms() {
		return terms;
	}

	@Param
	public AlvisRERelations getDependencies() {
		return dependencies;
	}

	@Param
	public AlvisRERelations[] getRelations() {
		return relations;
	}

	public void setOutDir(OutputDirectory outDir) {
		this.outDir = outDir;
	}

	public void setSectionSeparator(String sectionSeparator) {
		this.sectionSeparator = sectionSeparator;
	}

	public void setcParameter(Double cParameter) {
		this.cParameter = cParameter;
	}

	public void setSchema(DocumentFragment schema) {
		this.schema = schema;
	}

	public void setSimilarityFunction(DocumentFragment similarityFunction) {
		this.similarityFunction = similarityFunction;
	}

	public void setFolds(Integer folds) {
		this.folds = folds;
	}

	public void setSentences(AlvisRETokens sentences) {
		this.sentences = sentences;
	}

	public void setWords(AlvisRETokens words) {
		this.words = words;
	}

	public void setTerms(AlvisRETokens[] terms) {
		this.terms = terms;
	}

	public void setDependencies(AlvisRERelations dependencies) {
		this.dependencies = dependencies;
	}

	public void setRelations(AlvisRERelations[] relations) {
		this.relations = relations;
	}
}
