package fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.rebert;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.library.standard.NavigationLibrary;
import fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.rebert.REBERTPredict.REBERTPredictResolvedObjects;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Annotation;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Corpus;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.DownCastElement;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Element;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Layer;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Section;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.EvaluationContext;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.Evaluator;
import fr.inra.maiage.bibliome.alvisnlp.core.module.ModuleException;
import fr.inra.maiage.bibliome.alvisnlp.core.module.ProcessingContext;
import fr.inra.maiage.bibliome.alvisnlp.core.module.lib.ExternalHandler;
import fr.inra.maiage.bibliome.alvisnlp.core.module.types.Mapping;
import fr.inra.maiage.bibliome.util.Iterators;
import fr.inra.maiage.bibliome.util.Pair;
import fr.inra.maiage.bibliome.util.files.InputFile;
import fr.inra.maiage.bibliome.util.fragments.Fragment;
import fr.inra.maiage.bibliome.util.fragments.FragmentTag;
import fr.inra.maiage.bibliome.util.fragments.FragmentTagIterator;
import fr.inra.maiage.bibliome.util.streams.FileSourceStream;
import fr.inra.maiage.bibliome.util.streams.SourceStream;

public class REBERTPredictExternalHandler extends ExternalHandler<Corpus,REBERTPredict> {
	private final int numLabels;
	private final EvaluationContext evalCtx;

	protected REBERTPredictExternalHandler(ProcessingContext<Corpus> processingContext, REBERTPredict module, Corpus annotable) throws ModuleException {
		super(processingContext, module, annotable);
		SourceStream source = new FileSourceStream("UTF-8", new InputFile(getModule().getFinetunedModel(), "id2label.json"));
		try (Reader r = source.getReader()) {
			JSONParser parser = new JSONParser();
			JSONObject jLabels = (JSONObject) parser.parse(r);
			numLabels = jLabels.size();
		}
		catch (ParseException | IOException e) {
			throw new ModuleException(e);
		}
		Logger logger = module.getLogger(processingContext);
		evalCtx = new EvaluationContext(logger);
	}

	@Override
	protected void prepare() throws IOException, ModuleException {
		// CSVFormat format = CSVFormat.MYSQL.withQuote('"').withDelimiter(',');
		CSVFormat format = CSVFormat.MYSQL.builder().setQuote('"').setDelimiter(',').build();
		try (Writer writer = new FileWriter(getRebertInputFile())) {
			try (CSVPrinter printer = new CSVPrinter(writer, format)) {
				printer.printRecord("text", "sentence_1", "sentence_2", "label");
				REBERTPredictResolvedObjects resObj = getModule().getResolvedObjects();
				int nCandidates = 0;
				for (Element cand : Iterators.loop(resObj.getCandidates().evaluateElements(evalCtx, getAnnotable()))) {
					prepare(printer, cand);
					nCandidates++;
				}
				getLogger().info("prepared " + nCandidates + " candidates");
			}
		}
	}
	
	private static final String[] SUBJECT_TAGS = new String[] { "@@", "@@" };
	private static final String[] OBJECT_TAGS = new String[] { "<<", ">>" };

	private void prepare(CSVPrinter printer, Element cand) throws IOException, ModuleException {
		REBERTPredictResolvedObjects resObj = getModule().getResolvedObjects();
		ArgInfo subject = new ArgInfo(SUBJECT_TAGS, cand, resObj.getSubject());
		ArgInfo object = new ArgInfo(OBJECT_TAGS, cand, resObj.getObject());
		Object[] candRec = getCandidateRecord(subject, object);
		printer.printRecord(candRec);
	}

	private Object[] getCandidateRecord(ArgInfo subject, ArgInfo object) {
		if (subject.element == null) {
			if (object.element == null) {
				return new String[] { "", "", "", "" };
			}
			String taggedSentence = TextBuilder.buildText(object);
			return new String[] { taggedSentence, taggedSentence, "", "" };
		}
		if (object.element == null) {
			String taggedSentence = TextBuilder.buildText(subject);
			return new String[] { taggedSentence, taggedSentence, "", "" };
		}
		if (subject.sentence == object.sentence) {
			String taggedSentence = TextBuilder.buildText(subject, object);
			return new String[] { taggedSentence, taggedSentence, "", "" };
		}
		String subjectTaggedSentence = TextBuilder.buildText(subject);
		String objectTaggedSentence = TextBuilder.buildText(object);
		if (subject.sentenceBefore(object)) {
			return new String[] { subjectTaggedSentence + " " + objectTaggedSentence, subjectTaggedSentence, objectTaggedSentence, "" };
		}
		return new String[] { objectTaggedSentence + " " + subjectTaggedSentence, objectTaggedSentence, subjectTaggedSentence, "" };
	}
	
	private class ArgInfo implements Fragment {
		private final String[] tags;
		private final Element element;
		private final int start;
		private final int end;
		private final Section section;
		private final Annotation sentence;
		
		private ArgInfo(String[] tags, Element cand, Evaluator argEval) throws ModuleException {
			this.tags = tags;
			REBERTPredict owner = getModule();
			REBERTPredictResolvedObjects resObj = owner.getResolvedObjects();
			Iterator<Element> argIt = argEval.evaluateElements(evalCtx, cand);
			if (argIt.hasNext()) {
				element = argIt.next();
				start = resObj.getStart().evaluateInt(evalCtx, element);
				end = resObj.getEnd().evaluateInt(evalCtx, element);
				section = DownCastElement.toSection(NavigationLibrary.section(evalCtx, element).next());
				Layer sentenceLayer = section.ensureLayer(owner.getSentenceLayer());
				Layer enclosingSentences = sentenceLayer.including(start, end);
				if (enclosingSentences.size() == 0) {
					throw new ModuleException("could not find enclosing sentence for " + element + " :: " + sentenceLayer);
				}
				sentence = enclosingSentences.get(0);
			}
			else {
				element = null;
				start = 0;
				end = 0;
				section = null;
				sentence = null;
			}
		}

		public boolean sentenceBefore(ArgInfo other) {
			if (section.getDocument() != other.section.getDocument()) {
				return true;
			}
			if (section.getOrder() <= other.section.getOrder()) {
				return true;
			}
			return sentence.getStart() < other.sentence.getStart();
		}

		@Override
		public int getStart() {
			return start;
		}

		@Override
		public int getEnd() {
			return end;
		}
	}
	
	private static class TextBuilder implements FragmentTagIterator<String,ArgInfo> {
		private final StringBuilder result = new StringBuilder();

		@Override
		public void handleGap(String text, int from, int to) {
			result.append(text.substring(from, to));
		}

		@Override
		public void handleHead(String text, int to) {
			result.append(text.substring(0, to));
		}

		@Override
		public void handleTag(String text, FragmentTag<ArgInfo> tag) {
			ArgInfo arg = tag.getFragment();
			space();
			switch (tag.getTagType()) {
				case EMPTY: throw new RuntimeException();
				case OPEN: {
					result.append(arg.tags[0]);
					break;
				}
				case CLOSE: {
					result.append(arg.tags[1]);
					break;
				}
			}
			space();
		}

		@Override
		public void handleTail(String text, int from) {
			result.append(text.substring(from));
		}
		
		public void space() {
			result.append(' ');
		}
		
		public String getText() {
			return result.toString();
		}
		
		private static String buildText(ArgInfo... argsInfo) {
			TextBuilder textBuilder = new TextBuilder();
			Annotation sentence = argsInfo[0].sentence;
			FragmentTag.iterateFragments(textBuilder, sentence.getForm(), Arrays.asList(argsInfo), sentence.getStart());
			return textBuilder.getText();
		}
	}

	@Override
	protected void collect() throws IOException, ModuleException {
		REBERTPredict owner = getModule();
		REBERTPredictResolvedObjects resObj = owner.getResolvedObjects();
		EnsembleAggregator aggregator = owner.getAggregator();
		try (ProbasReader probasReader = new ProbasReader()) {
			int[] catCount = new int[probasReader.categories.length];
			int nCandidates = 0;
			for (Element cand : Iterators.loop(resObj.getCandidates().evaluateElements(evalCtx, getAnnotable()))) {
				double[][] ensembleProbas = probasReader.next();
				Pair<Integer,Mapping> pred = aggregator.aggregate(ensembleProbas);
				cand.addFeature(owner.getLabelFeature(), probasReader.categories[pred.first]);
				if (owner.getExplainFeaturePrefix() != null) {
					for (Map.Entry<String,String> e : pred.second.entrySet()) {
						cand.addFeature(owner.getExplainFeaturePrefix() + e.getKey(), e.getValue());
					}
				}
				catCount[pred.first]++;
				nCandidates++;
			}
			Logger logger = getLogger();
			logger.info("predictions for " + nCandidates + " candidates:");
			for (int nc = 0; nc < catCount.length; ++nc) {
				logger.info(probasReader.categories[nc] + ": " + catCount[nc]);
			}
		}
	}
	
	private class ProbasReader implements Closeable, AutoCloseable, Iterator<double[][]> {
		private final BufferedReader[] readers;
		private final CSVParser[] parsers;
		private final Iterator<CSVRecord>[] iterators;
		private final String[] categories;
		
		@SuppressWarnings("unchecked")
		private ProbasReader() throws IOException {
			CSVFormat format = CSVFormat.MYSQL.builder().setQuote('"').setDelimiter(',').build();
			int ensembleNumber = getModule().getEnsembleNumber();
			readers = new BufferedReader[ensembleNumber];
			parsers = new CSVParser[ensembleNumber];
			iterators = new Iterator[ensembleNumber];
			try {
				for (int ne = 0; ne < ensembleNumber; ++ne) {
					File outFile = new File(getRebertOutputDir(), "probas_ensemble_" + (ne + 1) + ".csv");
					readers[ne] = new BufferedReader(new FileReader(outFile));
					parsers[ne] = new CSVParser(readers[ne], format);
					iterators[ne] = parsers[ne].iterator();
				}
			}
			catch (IOException e) {
				close();
				throw new IOException(e);
			}
			CSVRecord header = nextRecords()[0];
			categories = new String[header.size()];
			for (int nc = 0; nc < categories.length; ++nc) {
				categories[nc] = header.get(nc);
			}
		}
		
		public boolean hasNext() {
			return iterators[0].hasNext();
		}
		
		public double[][] next() {
			double[][] result = new double[readers.length][];
			CSVRecord[] records = nextRecords();
			for (int ne = 0; ne < result.length; ++ne) {
				double[] probas = new double[categories.length];
				for (int nc = 0; nc < probas.length; ++nc) {
					probas[nc] = Double.parseDouble(records[ne].get(nc));
				}
				result[ne] = probas;
			}
			return result;
		}
		
		private CSVRecord[] nextRecords() {
			CSVRecord[] result = new CSVRecord[readers.length];
			for (int ne = 0; ne < result.length; ++ne) {
				result[ne] = iterators[ne].next();
			}
			return result;
		}
		
		public void close() throws IOException {
			for (int ne = 0; ne < readers.length; ++ne) {
				if ((parsers[ne] != null) && (!parsers[ne].isClosed())) {
					parsers[ne].close();
				}
				if (readers[ne] != null) {
					readers[ne].close();
				}
			}
			
		}
	}

	@Override
	protected String getPrepareTask() {
		return "rebert-prepare-input";
	}

	@Override
	protected String getExecTask() {
		return "rebert-run";
	}

	@Override
	protected String getCollectTask() {
		return "rebert-collect-results";
	}

	@Override
	protected List<String> getCommandLine() {
		List<String> result = new ArrayList<String>();
		REBERTPredict owner = getModule();
		if (owner.getConda() != null) {
			result.add(owner.getConda().getAbsolutePath());
			result.add("run");
			result.add("--name");
			result.add(owner.getCondaEnvironment());
		}
		if (owner.getPython() == null) {
			result.add("python");
		}
		else {
			result.add(owner.getPython().getAbsolutePath());
		}
		result.add(getRebertFile("eval.py").getAbsolutePath());
		result.add("--data_dir");
		result.add(getRebertInputFile().getAbsolutePath());
		result.add("--model_type");
		result.add(owner.getModelType());
		result.add("--config_name_or_path");
		result.add(getRebertFile("config/" + owner.getModelType() + ".json").getAbsolutePath());
		result.add("--pretrained_model_path");
		result.add(getRebertFile("pretrained_model").getAbsolutePath());
		result.add("--finetuned_model_path");
		result.add(owner.getFinetunedModel().getAbsolutePath());
		result.add("--num_labels");
		result.add(Integer.toString(numLabels));
		result.add("--num_ensemble");
		result.add(owner.getEnsembleNumber().toString());
		if (!owner.getUseGPU()) {
			result.add("--force_cpu");
		}
		result.add("--output_dir");
		result.add(getRebertOutputDir().getAbsolutePath());
		return result;
	}
	
	private File getRebertFile(String fileName) {
		return new File(getModule().getRebertDir(), fileName);
	}
	
	private File getRebertInputFile() {
		return getTempFile("input.txt");
	}
	
	private File getRebertOutputDir() {
		return getTempFile("output");
	}

	@Override
	protected void updateEnvironment(Map<String, String> env) {
		env.put("PYTHONPATH", getModule().getRebertDir().getAbsolutePath());
	}

	@Override
	protected File getWorkingDirectory() {
		return null;
	}

	@Override
	protected String getInputFileame() {
		return null;
	}

	@Override
	protected String getOutputFilename() {
		return null;
	}
}
