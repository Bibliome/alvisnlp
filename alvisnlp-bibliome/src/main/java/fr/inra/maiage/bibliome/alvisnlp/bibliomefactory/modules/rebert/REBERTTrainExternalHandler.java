package fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.rebert;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Corpus;
import fr.inra.maiage.bibliome.alvisnlp.core.module.ModuleException;
import fr.inra.maiage.bibliome.alvisnlp.core.module.ProcessingContext;

public class REBERTTrainExternalHandler extends REBERTBaseExternalHandler<REBERTTrain> {
	public REBERTTrainExternalHandler(ProcessingContext processingContext, REBERTTrain module, Corpus annotable) throws ModuleException {
		super(processingContext, module, annotable);
	}

	@Override
	protected void completeCommandLineMnemonics(Map<String, String> mnemonics, boolean deferred) {
		REBERTTrain owner = getModule();
		mnemonics.put("FINETUNED_MODEL", owner.getFinetunedModel().getAbsolutePath());
		mnemonics.put("TRAIN_DEV_SPLIT", owner.getTrainDevSplit().toString());
		mnemonics.put("BATCH_SIZE", owner.getBatchSize().toString());
	}

	@Override
	protected List<String> getCommandLine(Map<String, String> mnemonics, boolean deferred) {
		List<String> result = new ArrayList<String>();
		REBERTTrain owner = getModule();
		if (owner.getCondaEnvironment() != null) {
			result.add(getMnemonicValue(mnemonics, "CONDA_EXECUTABLE", deferred));
			result.add("run");
			result.add("--name");
			result.add(getMnemonicValue(mnemonics, "CONDA_ENVIRONMENT", deferred));
		}
		result.add(getMnemonicValue(mnemonics, "PYTHON", deferred));
		result.add(getRebertFile(mnemonics, "train.py", deferred));
		result.add("--data_dir");
		result.add(getMnemonicValue(mnemonics, "DATA_FILE", deferred));
		result.add("--model_type");
		result.add(owner.getModelType());
		result.add("--pretrained_model_path");
		result.add(getRebertFile(mnemonics, "pretrained_model", deferred));
		result.add("--config_name_or_path");
		result.add(getRebertFile(mnemonics, "config/" + owner.getModelType() + ".json", deferred));
		result.add("--num_labels");
		result.add(Integer.toString(labels.length));
		result.add("--train_dev_split");
		result.add(getMnemonicValue(mnemonics, "TRAIN_DEV_SPLIT", deferred));
		result.add("--batch_size");
		result.add(getMnemonicValue(mnemonics, "BATCH_SIZE", deferred));
		result.add("--early_stopping");
		result.add("--no_randomness");
		result.add("--num_ensemble");
		result.add(owner.getEnsembleNumber().toString());
		if (deferred) {
			result.add(getMnemonicValue(mnemonics, "FORCE_CPU", deferred));
		}
		else {
			if (!owner.getUseGPU()) {
				result.add("--force_cpu");
			}
		}
		result.add("--finetuned_model_path");
		result.add(getMnemonicValue(mnemonics, "FINETUNED_MODEL", deferred));
		return result;
	}

	@Override
	protected void collect() throws IOException, ModuleException {
	}
}
