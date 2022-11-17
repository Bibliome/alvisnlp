package fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.wapiti;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.wapiti.AbstractWapiti.WapitiResolvedObjects;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Annotation;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Corpus;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Layer;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Section;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.EvaluationContext;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.Evaluator;
import fr.inra.maiage.bibliome.alvisnlp.core.module.ModuleException;
import fr.inra.maiage.bibliome.alvisnlp.core.module.ProcessingContext;
import fr.inra.maiage.bibliome.alvisnlp.core.module.lib.ExternalHandler;
import fr.inra.maiage.bibliome.util.Iterators;

abstract class AbstractWapitiExternalHandler<T extends AbstractWapiti> extends ExternalHandler<Corpus,T> {
	protected AbstractWapitiExternalHandler(ProcessingContext<Corpus> processingContext, T module, Corpus annotable) {
		super(processingContext, module, annotable);
	}

	@Override
	protected void prepare() throws IOException, ModuleException {
		EvaluationContext evalCtx = new EvaluationContext(getLogger());
		AbstractWapiti owner = getModule();
		WapitiResolvedObjects resObj = owner.getResolvedObjects();
		try (PrintStream ps = new PrintStream(getWapitiInputFile())) {
			for (Section sec : Iterators.loop(owner.sectionIterator(evalCtx, getAnnotable()))) {
				for (Layer sentence : sec.getSentences(owner.getTokenLayer(), owner.getSentenceLayer())) {
					for (Annotation a : sentence) {
						boolean notFirst = false;
						for (Evaluator feat : resObj.getFeatures()) {
							if (notFirst) {
								ps.print('\t');
							}
							else {
								notFirst = true;
							}
							String value = feat.evaluateString(evalCtx, a);
							ps.print(value.replace(' ', '_'));
						}
						ps.println();
					}
					ps.println();
				}
			}
		}
	}
	
	private File getWapitiInputFile() {
		return getTempFile("input.txt");
	}

	@Override
	protected String getPrepareTask() {
		return "alvisnlp-to-wapiti";
	}

	@Override
	protected String getExecTask() {
		return "wapiti";
	}

	@Override
	protected String getCollectTask() {
		return "wapiti-to-alvisnlp";
	}

	@Override
	protected List<String> getCommandLine() {
		AbstractWapiti owner = getModule();
		List<String> result = new ArrayList<String>();
		result.add(owner.getWapitiExecutable().getAbsolutePath());
		result.add(getMode());
		fillAdditionalCommandLineArgs(result);
		String[] commandLineOptions = owner.getCommandLineOptions();
		if (commandLineOptions != null) {
			result.addAll(Arrays.asList(commandLineOptions));
		}
		result.add(getWapitiInputFile().getAbsolutePath());
		result.add(getWapitiOutputFile().getAbsolutePath());
		return result;
	}
	
	protected abstract void fillAdditionalCommandLineArgs(List<String> args);
	
	protected static void addOption(List<String> args, String option, String value) {
		if (value != null) {
			args.add(option);
			args.add(value.toString());
		}
	}

	protected abstract String getMode();
	
	protected abstract File getWapitiOutputFile();

	@Override
	protected void updateEnvironment(Map<String,String> env) {
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
