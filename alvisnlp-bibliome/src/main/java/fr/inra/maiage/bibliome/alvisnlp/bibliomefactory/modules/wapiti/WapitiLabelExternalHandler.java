package fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.wapiti;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Annotation;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Corpus;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Layer;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Section;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.EvaluationContext;
import fr.inra.maiage.bibliome.alvisnlp.core.module.ModuleException;
import fr.inra.maiage.bibliome.alvisnlp.core.module.ProcessingContext;
import fr.inra.maiage.bibliome.alvisnlp.core.module.lib.ModuleBase;
import fr.inra.maiage.bibliome.util.Iterators;

class WapitiLabelExternalHandler extends AbstractWapitiExternalHandler<WapitiLabel> {
	WapitiLabelExternalHandler(ProcessingContext<Corpus> processingContext, WapitiLabel module, Corpus annotable) {
		super(processingContext, module, annotable);
	}

	@Override
	protected void fillAdditionalCommandLineArgs(List<String> args) {
		args.add("--label");
		addOption(args, "--model", getModule().getModelFile().getAbsolutePath());
	}

	@Override
	protected String getMode() {
		return "label";
	}

	@Override
	protected File getWapitiOutputFile() {
		return getTempFile("labels.txt");
	}

	@Override
	protected void collect() throws IOException, ModuleException {
		try (BufferedReader reader = new BufferedReader(new FileReader(getWapitiOutputFile()))) {
			WapitiLabel owner = getModule();
			EvaluationContext evalCtx = new EvaluationContext(getLogger());
			for (Section sec : Iterators.loop(owner.sectionIterator(evalCtx, getAnnotable()))) {
				for (Layer sentence : sec.getSentences(owner.getTokenLayerName(), owner.getSentenceLayerName())) {
					for (Annotation a : sentence) {
						String line = reader.readLine();
						if (line == null) {
							ModuleBase.processingException("wapiti output has too few lines");
						}
						a.addFeature(owner.getLabelFeature(), line.trim());
					}
					String line = reader.readLine();
					if (line == null) {
						ModuleBase.processingException("wapiti output has too few lines");
					}
				}
			}
			if (reader.readLine() != null) {
				ModuleBase.processingException("wapiti output has too many lines");
			}
		}
	}
}
