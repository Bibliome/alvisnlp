package fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.wapiti;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Annotation;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Corpus;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Layer;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Section;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.EvaluationContext;
import fr.inra.maiage.bibliome.alvisnlp.core.module.ProcessingContext;
import fr.inra.maiage.bibliome.alvisnlp.core.module.ProcessingException;
import fr.inra.maiage.bibliome.alvisnlp.core.module.lib.ModuleBase;
import fr.inra.maiage.bibliome.util.Iterators;

class WapitiLabelExternal extends AbstractWapitiExternal<WapitiLabel> {
	public WapitiLabelExternal(WapitiLabel owner, ProcessingContext<Corpus> ctx, Corpus corpus) throws IOException {
		super(owner, ctx, corpus, new File(owner.getTempDir(ctx), "labels.txt"));
	}

	@Override
	protected String getMode() {
		return "label";
	}

	@Override
	protected void fillAdditionalCommandLineArgs(List<String> args) {
		args.add("--label");
		addOption(args, "--model", getOwner().getModelFile());
	}
	
	void readResult(Corpus corpus) throws IOException, ProcessingException {
		try (BufferedReader reader = new BufferedReader(new FileReader(outputFile))) {
			Logger logger = getLogger();
			EvaluationContext evalCtx = new EvaluationContext(logger);
			for (Section sec : Iterators.loop(getOwner().sectionIterator(evalCtx, corpus))) {
				for (Layer sentence : sec.getSentences(getOwner().getTokenLayerName(), getOwner().getSentenceLayerName())) {
					for (Annotation a : sentence) {
						String line = reader.readLine();
						if (line == null) {
							ModuleBase.processingException("wapiti output has too few lines");
						}
						a.addFeature(getOwner().getLabelFeature(), line.trim());
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