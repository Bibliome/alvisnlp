package fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.rebert;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;

import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Corpus;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Element;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Relation;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Tuple;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.EvaluationContext;
import fr.inra.maiage.bibliome.alvisnlp.core.module.ModuleException;
import fr.inra.maiage.bibliome.alvisnlp.core.module.ProcessingContext;
import fr.inra.maiage.bibliome.alvisnlp.core.module.lib.ExternalHandler;
import fr.inra.maiage.bibliome.alvisnlp.core.module.types.Mapping;
import fr.inra.maiage.bibliome.util.Pair;

public class REBERTPredictExternalHandler extends ExternalHandler<Corpus,REBERTPredict> {
	private final String[] labels;
	private final EvaluationContext evalCtx;
	private final Collection<Candidate> candidates;

	protected REBERTPredictExternalHandler(ProcessingContext<Corpus> processingContext, REBERTPredict module, Corpus annotable) throws ModuleException {
		super(processingContext, module, annotable);
		labels = module.readLabels();
		Logger logger = module.getLogger(processingContext);
		evalCtx = new EvaluationContext(logger);
		candidates = module.getResolvedObjects().createCandidates(evalCtx, annotable);
	}
	
	protected boolean hasCandidates() {
		return !candidates.isEmpty();
	}
	
	@Override
	protected void prepare() throws IOException, ModuleException {
		CSVFormat format = CSVFormat.MYSQL.builder().setQuote('"').setDelimiter(',').build();
		try (Writer writer = new FileWriter(getRebertInputFile())) {
			try (CSVPrinter printer = new CSVPrinter(writer, format)) {
				printer.printRecord("text", "sentence_1", "sentence_2", "label");
				for (Candidate cand : candidates) {
					Object[] candRec = cand.getRecord();
					printer.printRecord(candRec);
				}
				getLogger().info("prepared " + candidates.size() + " candidates");
			}
		}
	}

	@Override
	protected void collect() throws IOException, ModuleException {
		REBERTPredict owner = getModule();
		EnsembleAggregator aggregator = owner.getAggregator();
		try (ProbasReader probasReader = new ProbasReader()) {
			int[] catCount = new int[probasReader.categories.length];
			for (Candidate cand : candidates) {
				double[][] ensembleProbas = probasReader.next();
				Pair<Integer,Mapping> pred = aggregator.aggregate(ensembleProbas);
				Element elt = getLabelledElement(cand, pred.first);
				if (elt != null) {
					elt.addFeature(owner.getLabelFeature(), probasReader.categories[pred.first]);
					if (owner.getExplainFeaturePrefix() != null) {
						for (Map.Entry<String,String> e : pred.second.entrySet()) {
							elt.addFeature(owner.getExplainFeaturePrefix() + e.getKey(), e.getValue());
						}
					}
				}
				catCount[pred.first]++;
			}
			Logger logger = getLogger();
			logger.info("predictions for " + candidates.size() + " candidates:");
			for (int nc = 0; nc < catCount.length; ++nc) {
				logger.info(probasReader.categories[nc] + ": " + catCount[nc]);
			}
		}
	}
	
	private Element getLabelledElement(Candidate cand, int cat) {
		REBERTPredict owner = getModule();
		if (cand.isAsserted()) {
			if (owner.getCreateAssertedTuples()) {
				return createTuple(cand, cat);
			}
			return cand.getSupportElement();
		}
		return createTuple(cand, cat);
	}
	
	private Tuple createTuple(Candidate cand, int cat) {
		REBERTPredict owner = getModule();
		if (owner.getCreateNegativeTuples() || (cat != owner.getNegativeCategory())) {
			Relation rel = cand.getSubject().getSection().ensureRelation(owner, owner.getRelationName());
			Tuple t = new Tuple(owner, rel);
			t.setArgument(owner.getSubjectRole(), cand.getSubject().getElement());
			t.setArgument(owner.getObjectRole(), cand.getObject().getElement());
			return t;
		}
		return null;
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
		result.add(Integer.toString(labels.length));
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
