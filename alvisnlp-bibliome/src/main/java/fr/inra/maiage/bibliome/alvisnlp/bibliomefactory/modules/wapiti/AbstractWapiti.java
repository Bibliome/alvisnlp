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


package fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.wapiti;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

import fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.SectionModule;
import fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.SectionModule.SectionResolvedObjects;
import fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.wapiti.AbstractWapiti.WapitiResolvedObjects;

import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Annotation;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Corpus;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.DefaultNames;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Layer;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.NameType;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Section;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.EvaluationContext;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.Evaluator;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.Expression;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.ResolverException;
import fr.inra.maiage.bibliome.alvisnlp.core.module.Module;
import fr.inra.maiage.bibliome.alvisnlp.core.module.ModuleException;
import fr.inra.maiage.bibliome.alvisnlp.core.module.NameUsage;
import fr.inra.maiage.bibliome.alvisnlp.core.module.ProcessingContext;
import fr.inra.maiage.bibliome.alvisnlp.core.module.lib.External;
import fr.inra.maiage.bibliome.alvisnlp.core.module.lib.Param;
import fr.inra.maiage.bibliome.util.Iterators;
import fr.inra.maiage.bibliome.util.files.ExecutableFile;

public abstract class AbstractWapiti extends SectionModule<WapitiResolvedObjects> {
	private ExecutableFile wapitiExecutable;
	private String[] commandLineOptions;
	private String sentenceLayerName = DefaultNames.getSentenceLayer();
	private String tokenLayerName = DefaultNames.getWordLayer();
	private Expression[] features;
	
	@Override
	protected String[] addLayersToSectionFilter() {
		return new String[] { tokenLayerName };
	}

	@Override
	protected String[] addFeaturesToSectionFilter() {
		return null;
	}
	
	@Param
	public ExecutableFile getWapitiExecutable() {
		return wapitiExecutable;
	}

	@Param(nameType=NameType.FEATURE)
	public Expression[] getFeatures() {
		return features;
	}

	@Param(mandatory=false)
	public String[] getCommandLineOptions() {
		return commandLineOptions;
	}

	@Param(mandatory=false, nameType=NameType.LAYER)
	public String getSentenceLayerName() {
		return sentenceLayerName;
	}

	@Param(nameType=NameType.LAYER)
	public String getTokenLayerName() {
		return tokenLayerName;
	}

	public void setSentenceLayerName(String sentenceLayerName) {
		this.sentenceLayerName = sentenceLayerName;
	}

	public void setTokenLayerName(String tokenLayerName) {
		this.tokenLayerName = tokenLayerName;
	}

	public void setCommandLineOptions(String[] commandLineOptions) {
		this.commandLineOptions = commandLineOptions;
	}

	public void setWapitiExecutable(ExecutableFile wapitiExecutable) {
		this.wapitiExecutable = wapitiExecutable;
	}

	public void setFeatures(Expression[] features) {
		this.features = features;
	}

	protected static class WapitiResolvedObjects extends SectionResolvedObjects {
		private final Evaluator[] features;
		
		public WapitiResolvedObjects(ProcessingContext<Corpus> ctx, AbstractWapiti module) throws ResolverException {
			super(ctx, module);
			this.features = rootResolver.resolveArray(module.features, Evaluator.class);
		}

		@Override
		public void collectUsedNames(NameUsage nameUsage, String defaultType) throws ModuleException {
			super.collectUsedNames(nameUsage, defaultType);
			nameUsage.collectUsedNamesArray(features, defaultType);
		}
	}
	
	protected abstract class WapitiExternal implements External<Corpus> {
		private final Logger logger;
		private final File inputFile;
		protected final File outputFile;
		
		protected WapitiExternal(ProcessingContext<Corpus> ctx, Corpus corpus, File outputFile) throws IOException {
			super();
			this.logger = getLogger(ctx);
			File tmpDir = getTempDir(ctx);
			this.inputFile = new File(tmpDir, "input.txt");
			writeInput(corpus);
			this.outputFile = outputFile;
		}
		
		private void writeInput(Corpus corpus) throws IOException {
			EvaluationContext evalCtx = new EvaluationContext(logger);
			WapitiResolvedObjects resObj = getResolvedObjects();
			try (PrintStream ps = new PrintStream(inputFile)) {
				for (Section sec : Iterators.loop(sectionIterator(evalCtx, corpus))) {
					for (Layer sentence : sec.getSentences(tokenLayerName, sentenceLayerName)) {
						for (Annotation a : sentence) {
							boolean notFirst = false;
							for (Evaluator feat : resObj.features) {
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
		
		protected void addOption(List<String> args, String option, Object value) {
			if (value != null) {
				args.add(option);
				args.add(value.toString());
			}
		}
		
		protected void addOption(List<String> args, String option, File value) {
			if (value != null) {
				args.add(option);
				args.add(value.getAbsolutePath());
			}
		}
	
		protected void addOption(List<String> args, Boolean value, String option) {
			if (value != null && value) {
				args.add(option);
			}
		}

		@Override
		public Module<Corpus> getOwner() {
			return AbstractWapiti.this;
		}

		@Override
		public String[] getCommandLineArgs() throws ModuleException {
			List<String> result = new ArrayList<String>();
			result.add(wapitiExecutable.getAbsolutePath());
			result.add(getMode());
			fillAdditionalCommandLineArgs(result);
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

		@Override
		public void processOutput(BufferedReader out, BufferedReader err) throws ModuleException {
			try {
				logger.fine("wapiti standard error:");
				for (String line = err.readLine(); line != null; line = err.readLine()) {
					logger.fine("    " + line);
				}
				logger.fine("end of wapiti standard error");
			}
			catch (IOException ioe) {
				logger.warning("could not read wapiti standard error: " + ioe.getMessage());
			}
		}
	}
}
