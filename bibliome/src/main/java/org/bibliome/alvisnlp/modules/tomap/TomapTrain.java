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


package org.bibliome.alvisnlp.modules.tomap;

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
import javax.xml.transform.TransformerFactoryConfigurationError;

import org.bibliome.alvisnlp.modules.SectionModule.SectionResolvedObjects;
import org.bibliome.alvisnlp.modules.tomap.TomapTrain.TomapTrainResolvedObjects;
import org.bibliome.alvisnlp.modules.yatea.AbstractYateaExtractor;
import org.bibliome.alvisnlp.modules.yatea.TestifiedTerminology;
import org.bibliome.util.Iterators;
import org.bibliome.util.Pair;
import org.bibliome.util.Strings;
import org.bibliome.util.defaultmap.DefaultArrayListHashMap;
import org.bibliome.util.defaultmap.DefaultMap;
import org.bibliome.util.files.InputFile;
import org.bibliome.util.streams.FileSourceStream;
import org.bibliome.util.streams.SourceStream;
import org.bibliome.util.streams.TargetStream;
import org.bibliome.util.tomap.Candidate;
import org.bibliome.util.tomap.StringNormalization;
import org.bibliome.util.tomap.TokenNormalization;
import org.bibliome.util.tomap.readers.YateaCandidateReader;
import org.bibliome.util.tomap.readers.YateaCandidateReader.YateaResult;
import org.bibliome.util.xml.XMLUtils;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import alvisnlp.corpus.Annotation;
import alvisnlp.corpus.Corpus;
import alvisnlp.corpus.Element;
import alvisnlp.corpus.Layer;
import alvisnlp.corpus.Section;
import alvisnlp.corpus.expressions.EvaluationContext;
import alvisnlp.corpus.expressions.Evaluator;
import alvisnlp.corpus.expressions.Expression;
import alvisnlp.corpus.expressions.ResolverException;
import alvisnlp.module.ModuleException;
import alvisnlp.module.NameUsage;
import alvisnlp.module.ProcessingContext;
import alvisnlp.module.ProcessingException;
import alvisnlp.module.lib.AlvisNLPModule;
import alvisnlp.module.lib.Param;

@AlvisNLPModule(beta=true)
public class TomapTrain extends AbstractYateaExtractor<TomapTrainResolvedObjects> {
	private Expression conceptIdentifier;
	private TargetStream outFile;

	public TomapTrain() {
		super();
		setDocumentTokens(false);
		setTestifiedTerminology(TOMAP_TESTIFIED_TERMS);
	}

	static class TomapTrainResolvedObjects extends SectionResolvedObjects {
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
		super.process(ctx, corpus);
		Logger logger = getLogger(ctx);
		Map<String,List<String>> conceptMap = getConceptMap(ctx, corpus);
		writeOutput(logger, conceptMap);
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
	
	private void writeOutput(Logger logger, Map<String,List<String>> conceptMap) throws ProcessingException {
		try (Writer writer = outFile.getBufferedWriter()) {
			Document doc = buildOutputDocument(logger, conceptMap);
			XMLUtils.writeDOMToFile(doc, null, writer);
		}
		catch (TransformerFactoryConfigurationError|IOException|SAXException|ParserConfigurationException e) {
			rethrow(e);
		}
	}
	
	private Document buildOutputDocument(Logger logger, Map<String,List<String>> conceptMap) throws IOException, SAXException, ParserConfigurationException {
		Document result = XMLUtils.docBuilder.newDocument();
		YateaCandidateReader yateaReader = new YateaCandidateReader(logger, TokenNormalization.FORM, StringNormalization.NONE);
		try (InputStream is = getYateaOutput()) {
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
	
	private InputStream getYateaOutput() throws IOException {
		Pair<Properties,Properties> p = createConfig(null);
		String outputPath = removeQuotes(p.second.getProperty("output-path"));
		String suffix = removeQuotes(p.second.getProperty("suffix"));
		String candPath = getWorkingDir() + "/" + outputPath + "/corpus/" + suffix + "/xml/candidates.xml";
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
