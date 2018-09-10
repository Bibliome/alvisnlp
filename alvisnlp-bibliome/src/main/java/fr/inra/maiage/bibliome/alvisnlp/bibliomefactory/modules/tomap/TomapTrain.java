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


package fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.tomap;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Logger;

import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.SectionModule.SectionResolvedObjects;
import fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.tomap.TomapTrain.TomapTrainResolvedObjects;
import fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.yatea.AbstractYateaExtractor;
import fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.yatea.TestifiedTerminology;
import fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.yatea.YateaExtractorExternalHandler;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Annotation;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Corpus;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Element;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Layer;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Section;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.EvaluationContext;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.Evaluator;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.Expression;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.ResolverException;
import fr.inra.maiage.bibliome.alvisnlp.core.module.ModuleException;
import fr.inra.maiage.bibliome.alvisnlp.core.module.NameUsage;
import fr.inra.maiage.bibliome.alvisnlp.core.module.ProcessingContext;
import fr.inra.maiage.bibliome.alvisnlp.core.module.ProcessingException;
import fr.inra.maiage.bibliome.alvisnlp.core.module.lib.AlvisNLPModule;
import fr.inra.maiage.bibliome.alvisnlp.core.module.lib.Param;
import fr.inra.maiage.bibliome.util.Iterators;
import fr.inra.maiage.bibliome.util.Strings;
import fr.inra.maiage.bibliome.util.defaultmap.DefaultArrayListHashMap;
import fr.inra.maiage.bibliome.util.defaultmap.DefaultMap;
import fr.inra.maiage.bibliome.util.files.InputFile;
import fr.inra.maiage.bibliome.util.streams.FileSourceStream;
import fr.inra.maiage.bibliome.util.streams.SourceStream;
import fr.inra.maiage.bibliome.util.streams.TargetStream;
import fr.inra.maiage.bibliome.util.tomap.Candidate;
import fr.inra.maiage.bibliome.util.tomap.StringNormalization;
import fr.inra.maiage.bibliome.util.tomap.TokenNormalization;
import fr.inra.maiage.bibliome.util.tomap.readers.YateaCandidateReader;
import fr.inra.maiage.bibliome.util.tomap.readers.YateaCandidateReader.YateaResult;
import fr.inra.maiage.bibliome.util.xml.XMLUtils;

@AlvisNLPModule(beta=true)
public class TomapTrain extends AbstractYateaExtractor<TomapTrainResolvedObjects> {
	private Expression conceptIdentifier;
	private TargetStream outFile;

	public TomapTrain() {
		super();
		setDocumentTokens(false);
		setTestifiedTerminology(TOMAP_TESTIFIED_TERMS);
	}

	public static class TomapTrainResolvedObjects extends SectionResolvedObjects {
		private final Evaluator conceptIdentifier;
		
		private TomapTrainResolvedObjects(ProcessingContext<Corpus> ctx, TomapTrain module) throws ResolverException {
			super(ctx, module);
			conceptIdentifier = module.conceptIdentifier.resolveExpressions(rootResolver);
		}

		@Override
		public void collectUsedNames(NameUsage nameUsage, String defaultType) throws ModuleException {
			super.collectUsedNames(nameUsage, defaultType);
			conceptIdentifier.collectUsedNames(nameUsage, defaultType);
		}
	}
	
	@Override
	protected TomapTrainResolvedObjects createResolvedObjects(ProcessingContext<Corpus> ctx) throws ResolverException {
		return new TomapTrainResolvedObjects(ctx, this);
	}

	@Override
	public void process(ProcessingContext<Corpus> ctx, Corpus corpus) throws ModuleException {
		try {
			YateaExtractorExternalHandler<TomapTrainResolvedObjects> ext = new YateaExtractorExternalHandler<TomapTrainResolvedObjects>(ctx, this, corpus);
			ext.start();
			Logger logger = getLogger(ctx);
			Map<String,List<String>> conceptMap = getConceptMap(ctx, corpus);
			writeOutput(logger, conceptMap, ext);
		}
		catch (IOException|InterruptedException | SAXException | ParserConfigurationException e) {
			throw new ProcessingException(e);
		}
	}

	private static final TestifiedTerminology TOMAP_TESTIFIED_TERMS = new TestifiedTerminology() {
		@Override
		public boolean check(Logger logger) {
			return true;
		}
		
		@Override
		public void collectUsedNames(NameUsage nameUsage, String defaultType) throws ModuleException {
		}
		
		@Override
		public <S extends SectionResolvedObjects> InputFile ensureFile(AbstractYateaExtractor<S> module, ProcessingContext<Corpus> ctx, Corpus corpus) throws ModuleException, IOException {
			File tmpDir = module.getTempDir(ctx);
			return new InputFile(tmpDir, "corpus.ttg"); // the same file as yatea input
		}
	};
	
	private void writeOutput(Logger logger, Map<String,List<String>> conceptMap, YateaExtractorExternalHandler<TomapTrainResolvedObjects> ext) throws IOException, SAXException, ParserConfigurationException {
		try (Writer writer = outFile.getBufferedWriter()) {
			Document doc = buildOutputDocument(logger, conceptMap, ext);
			XMLUtils.writeDOMToFile(doc, null, writer);
		}
	}
	
	private Document buildOutputDocument(Logger logger, Map<String,List<String>> conceptMap, YateaExtractorExternalHandler<TomapTrainResolvedObjects> ext) throws IOException, SAXException, ParserConfigurationException {
		Document result = XMLUtils.docBuilder.newDocument();
		YateaCandidateReader yateaReader = new YateaCandidateReader(logger, TokenNormalization.FORM, StringNormalization.NONE);
		try (InputStream is = getYateaOutput(ext)) {
			YateaResult yateaResult = yateaReader.parseStream(is);
			org.w3c.dom.Element top = result.createElement("candidates");
			result.appendChild(top);
			for (Candidate cand : yateaResult.getCandidates()) {
				String form = cand.getForm();
				if (conceptMap.containsKey(form)) {
					List<String> concepts = conceptMap.get(form);
					org.w3c.dom.Element ce = cand.toDOM(result, concepts);
					top.appendChild(ce);
				}
			}
		}
		return result;
	}

	private InputStream getYateaOutput(YateaExtractorExternalHandler<TomapTrainResolvedObjects> ext) throws IOException {
		Properties options = ext.getOptions();
		String suffix = removeQuotes(options.getProperty("suffix"));
		String outputPath = options.getProperty("output-path");
		String candPath;
		if (outputPath == null) {
			candPath = getWorkingDir() + "/corpus/" + suffix + "/xml/candidates.xml";
		}
		else {
			outputPath = removeQuotes(outputPath);
			candPath = getWorkingDir() + "/" + outputPath + "/corpus/" + suffix + "/xml/candidates.xml";
		}
		SourceStream stream = new FileSourceStream("UTF-8", candPath);
		return stream.getInputStream();
	}
	
	private static String removeQuotes(String s) {
		int len = s.length();
		if (len < 2) {
			return s;
		}
		int last = len - 1;
		if (s.charAt(0) == '"' && s.charAt(last) == '"') {
			return s.substring(1, last);
		}
		if (s.charAt(0) == '\'' && s.charAt(last) == '\'') {
			return s.substring(1, last);
		}
		return s;
	}
	
	private Map<String,List<String>> getConceptMap(ProcessingContext<Corpus> ctx, Corpus corpus) {
		DefaultMap<String,List<String>> result = new DefaultArrayListHashMap<String,String>();
		TomapTrainResolvedObjects resObj = getResolvedObjects();
		Logger logger = getLogger(ctx);
		EvaluationContext evalCtx = new EvaluationContext(logger);
		String wordLN = getWordLayerName();
		String sentLN = getSentenceLayerName();
		for (Section sec : Iterators.loop(sectionIterator(evalCtx, corpus))) {
			// get word annotations if any
			Layer words = null;
			if(wordLN != null){
				words = sec.getLayer(wordLN);
			}
			for (Layer sent : sec.getSentences(wordLN, sentLN)) {
				Element e;
				String form;
				if (sentLN == null) {
					e = sec;
					form = sec.getContents();
				}
				else {
					Annotation a = sent.getSentenceAnnotation();
					form = a.getForm();
					e = a;
					// build tokenized string if there are word annotations
					// and use it as 'form'
					if(words != null){
						StringBuilder buildTok = new StringBuilder();
						Layer wordsInAnnotation = words.between(a);
						Iterator<Annotation> it = wordsInAnnotation.iterator();
						while(it.hasNext()) {
							Annotation word = it.next();
							buildTok.append(" ");
							buildTok.append(word.getForm());
						}
						form = buildTok.toString().trim();
					}
				}
				String spaceNormForm = Strings.normalizeSpace(form);
				String conceptId = resObj.conceptIdentifier.evaluateString(evalCtx, e);
				result.safeGet(spaceNormForm).add(conceptId);
			}
		}
		return result;
	}
	
	@Override
	public Boolean getDocumentTokens() {
		return super.getDocumentTokens();
	}

	@Override
	public TestifiedTerminology getTestifiedTerminology() {
		return super.getTestifiedTerminology();
	}

	@Param
	public Expression getConceptIdentifier() {
		return conceptIdentifier;
	}

	@Param
	public TargetStream getOutFile() {
		return outFile;
	}

	public void setConceptIdentifier(Expression conceptIdentifier) {
		this.conceptIdentifier= conceptIdentifier;
	}

	public void setOutFile(TargetStream outFile) {
		this.outFile = outFile;
	}
}
