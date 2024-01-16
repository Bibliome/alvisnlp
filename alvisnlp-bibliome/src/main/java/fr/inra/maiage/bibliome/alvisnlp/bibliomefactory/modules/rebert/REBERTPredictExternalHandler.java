package fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.rebert;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.io.FileUtils;

import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Corpus;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Element;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Relation;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Tuple;
import fr.inra.maiage.bibliome.alvisnlp.core.module.ModuleException;
import fr.inra.maiage.bibliome.alvisnlp.core.module.ProcessingContext;
import fr.inra.maiage.bibliome.alvisnlp.core.module.types.Mapping;
import fr.inra.maiage.bibliome.util.Pair;
import fr.inra.maiage.bibliome.util.Strings;

public class REBERTPredictExternalHandler extends REBERTBaseExternalHandler<REBERTPredict> {
	protected REBERTPredictExternalHandler(ProcessingContext processingContext, REBERTPredict module, Corpus annotable) throws ModuleException {
		super(processingContext, module, annotable);
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
			Relation rel = cand.getSubject().getSection().ensureRelation(owner, owner.getRelation());
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
			int[] ensembleModels = getModels();
			//int ensembleNumber = getModule().getEnsembleNumber();
			readers = new BufferedReader[ensembleModels.length];
			parsers = new CSVParser[ensembleModels.length];
			iterators = new Iterator[ensembleModels.length];
			try {
				for (int i = 0; i < ensembleModels.length; ++i) {
					int ne = ensembleModels[i];
					File outFile = new File(getRebertOutputDir(), "probas_ensemble_" + (ne + 1) + ".csv");
					readers[i] = new BufferedReader(new FileReader(outFile));
					parsers[i] = new CSVParser(readers[i], format);
					iterators[i] = parsers[i].iterator();
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
		
		private int[] getModels() {
			REBERTPredict module = getModule();
			Integer[] ensembleModels = module.getEnsembleModels();
			if (ensembleModels != null) {
				int[] result = new int[ensembleModels.length];
				for (int i = 0; i < result.length; ++i) {
					result[i] = ensembleModels[i] - 1;
				}
				return result;
			}
			int[] result = new int[module.getEnsembleNumber()];
			for (int i = 0; i < result.length; ++i) {
				result[i] = i;
			}
			return result;
		}
		
		public boolean hasNext() {
			return iterators[0].hasNext();
		}
		
		public double[][] next() {
			double[][] result = new double[readers.length][];
			CSVRecord[] records = nextRecords();
			for (int i = 0; i < result.length; ++i) {
				double[] probas = new double[categories.length];
				for (int nc = 0; nc < probas.length; ++nc) {
					probas[nc] = Double.parseDouble(records[i].get(nc));
				}
				result[i] = probas;
			}
			return result;
		}
		
		private CSVRecord[] nextRecords() {
			CSVRecord[] result = new CSVRecord[readers.length];
			for (int i = 0; i < result.length; ++i) {
				result[i] = iterators[i].next();
			}
			return result;
		}
		
		public void close() throws IOException {
			for (int i = 0; i < readers.length; ++i) {
				if ((parsers[i] != null) && (!parsers[i].isClosed())) {
					parsers[i].close();
				}
				if (readers[i] != null) {
					readers[i].close();
				}
			}
		}
	}

	void readPredictions() throws IOException, ModuleException {
		REBERTBase owner = getModule();
		FileUtils.copyDirectory(owner.getPredictionsDirectory(), getRebertOutputDir());
		collect();
	}

	@Override
	protected void completeCommandLineMnemonics(Map<String,String> mnemonics, boolean deferred) {
		mnemonics.put("FINETUNED_MODEL", getModule().getFinetunedModel().getAbsolutePath());
	}

	@Override
	protected List<String> getCommandLine(Map<String,String> mnemonics, boolean deferred) {
		List<String> result = new ArrayList<String>();
		REBERTPredict owner = getModule();
		if (owner.getCondaEnvironment() != null) {
			result.add(getMnemonicValue(mnemonics, "CONDA_EXECUTABLE", deferred));
			result.add("run");
			result.add("--name");
			result.add(getMnemonicValue(mnemonics, "CONDA_ENVIRONMENT", deferred));
		}
		result.add(getMnemonicValue(mnemonics, "PYTHON", deferred));
		result.add(getRebertFile(mnemonics, "eval.py", deferred));
		result.add("--data_dir");
		result.add(getMnemonicValue(mnemonics, "DATA_FILE", deferred));
		result.add("--model_type");
		result.add(owner.getModelType());
		result.add("--config_name_or_path");
		result.add(getRebertFile(mnemonics, "config/" + owner.getModelType() + ".json", deferred));
		result.add("--pretrained_model_path");
		result.add(getRebertFile(mnemonics, "pretrained_model", deferred));
		result.add("--finetuned_model_path");
		result.add(getMnemonicValue(mnemonics, "FINETUNED_MODEL", deferred));
		result.add("--num_labels");
		result.add(Integer.toString(labels.length));
		if (owner.getEnsembleNumber() != null) {
			result.add("--num_ensemble");
			result.add(owner.getEnsembleNumber().toString());
		}
		if (owner.getEnsembleModels() != null) {
			result.add("--ensemble_models");
			result.add(Strings.joinStrings(owner.getEnsembleModels(), ','));
		}
		if (deferred) {
			result.add(getMnemonicValue(mnemonics, "FORCE_CPU", deferred));
		}
		else {
			if (!owner.getUseGPU()) {
				result.add("--force_cpu");
			}
		}
		result.add("--output_dir");
		result.add(getMnemonicValue(mnemonics, "OUTPUT_DIR", deferred));
		return result;
	}

	@Override
	protected String getOutputFilename() {
		return null;
	}
}
