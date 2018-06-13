package fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.wapiti;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.wapiti.AbstractWapiti.WapitiResolvedObjects;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Annotation;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Corpus;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Layer;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Section;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.EvaluationContext;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.Evaluator;
import fr.inra.maiage.bibliome.alvisnlp.core.module.ModuleException;
import fr.inra.maiage.bibliome.alvisnlp.core.module.ProcessingContext;
import fr.inra.maiage.bibliome.alvisnlp.core.module.lib.AbstractExternal;
import fr.inra.maiage.bibliome.util.Iterators;

abstract class AbstractWapitiExternal<T extends AbstractWapiti> extends AbstractExternal<Corpus,T> {
	private final File inputFile;
	protected final File outputFile;

	protected AbstractWapitiExternal(T owner, ProcessingContext<Corpus> ctx, Corpus corpus, File outputFile) throws IOException {
		super(owner, ctx);
		File tmpDir = owner.getTempDir(ctx);
		this.inputFile = new File(tmpDir, "input.txt");
		writeInput(corpus);
		this.outputFile = outputFile;
	}
	
	private void writeInput(Corpus corpus) throws IOException {
		EvaluationContext evalCtx = new EvaluationContext(getLogger());
		AbstractWapiti owner = getOwner();
		WapitiResolvedObjects resObj = owner.getResolvedObjects();
		try (PrintStream ps = new PrintStream(inputFile)) {
			for (Section sec : Iterators.loop(owner.sectionIterator(evalCtx, corpus))) {
				for (Layer sentence : sec.getSentences(owner.getTokenLayerName(), owner.getSentenceLayerName())) {
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
							ps.print(value);
						}
						ps.println();
					}
					ps.println();
				}
			}
		}
	}
	
	protected abstract String getMode();
	
	protected abstract void fillAdditionalCommandLineArgs(List<String> args);
	
	protected static void addOption(List<String> args, String option, Object value) {
		if (value != null) {
			args.add(option);
			args.add(value.toString());
		}
	}
	
	protected static void addOption(List<String> args, String option, File value) {
		if (value != null) {
			args.add(option);
			args.add(value.getAbsolutePath());
		}
	}

	protected static void addOption(List<String> args, Boolean value, String option) {
		if (value != null && value) {
			args.add(option);
		}
	}

	@Override
	public String[] getCommandLineArgs() throws ModuleException {
		AbstractWapiti owner = getOwner();
		List<String> result = new ArrayList<String>();
		result.add(owner.getWapitiExecutable().getAbsolutePath());
		result.add(getMode());
		fillAdditionalCommandLineArgs(result);
		String[] commandLineOptions = owner.getCommandLineOptions();
		if (commandLineOptions != null) {
			result.addAll(Arrays.asList(commandLineOptions));
		}
		result.add(inputFile.getAbsolutePath());
		result.add(outputFile.getAbsolutePath());
		return result.toArray(new String[result.size()]);
	}

	@Override
	public String[] getEnvironment() throws ModuleException {
		return null;
	}

	@Override
	public File getWorkingDirectory() throws ModuleException {
		return null;
	}
}