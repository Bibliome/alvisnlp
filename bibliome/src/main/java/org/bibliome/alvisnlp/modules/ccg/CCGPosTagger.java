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


package org.bibliome.alvisnlp.modules.ccg;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.List;
import java.util.logging.Logger;

import org.bibliome.alvisnlp.modules.ccg.CCGBase.CCGResolvedObjects;
import org.bibliome.util.files.ExecutableFile;
import org.bibliome.util.files.InputDirectory;
import org.bibliome.util.files.InputFile;
import org.bibliome.util.files.OutputFile;
import org.bibliome.util.streams.FileSourceStream;
import org.bibliome.util.streams.FileTargetStream;
import org.bibliome.util.streams.SourceStream;
import org.bibliome.util.streams.TargetStream;

import alvisnlp.corpus.Annotation;
import alvisnlp.corpus.Corpus;
import alvisnlp.corpus.Layer;
import alvisnlp.corpus.expressions.EvaluationContext;
import alvisnlp.corpus.expressions.ResolverException;
import alvisnlp.module.Module;
import alvisnlp.module.ModuleException;
import alvisnlp.module.ProcessingContext;
import alvisnlp.module.ProcessingException;
import alvisnlp.module.TimerCategory;
import alvisnlp.module.lib.AlvisNLPModule;
import alvisnlp.module.lib.External;
import alvisnlp.module.lib.Param;
import alvisnlp.module.lib.TimeThis;

@AlvisNLPModule
public class CCGPosTagger extends CCGBase<CCGResolvedObjects> {
	private ExecutableFile executable;
	private InputDirectory model;
	private Boolean silent = false;
	private String internalEncoding = "UTF-8";
	private Boolean keepPreviousPos = false;
	
	@Override
	public void process(ProcessingContext<Corpus> ctx, Corpus corpus) throws ModuleException {
		try {
			Logger logger = getLogger(ctx);
			EvaluationContext evalCtx = new EvaluationContext(logger);
			List<List<Layer>> sentenceRuns = getSentences(logger, evalCtx, corpus);
			for (int run = 0; run < sentenceRuns.size(); ++run) {
				logger.info(String.format("run %d/%d", run+1, sentenceRuns.size())); 
				List<Layer> sentences = sentenceRuns.get(run);
				CCGPosTaggerExternal ext = new CCGPosTaggerExternal(ctx, run, getMaxLength(sentences));
				TargetStream target = new FileTargetStream(internalEncoding, ext.input);
				try (PrintStream out = target.getPrintStream()) {
					printSentences(ctx, out, sentences, false);
				}
				callExternal(ctx, "run-ccg", ext, internalEncoding, "ccg-pos.sh");
				SourceStream source = new FileSourceStream(internalEncoding, ext.output);
				try (BufferedReader r = source.getBufferedReader()) {
					readSentences(ctx, r, sentences);
				}
			}
		}
		catch (IOException e) {
			rethrow(e);
		}
	}

	@Override
	protected CCGResolvedObjects createResolvedObjects(ProcessingContext<Corpus> ctx) throws ResolverException {
		return new CCGResolvedObjects(ctx, this);
	}

	private void readSentence(BufferedReader r, Layer sentence) throws IOException, ProcessingException {
		boolean reachedEOS = false;
		for (Annotation word : sentence) {
			if (word.getLastFeature(getFormFeatureName()).isEmpty())
				continue;
			if (reachedEOS)
				throw new ProcessingException("CCG sentence was too short");
			String pos = r.readLine();
			if (pos == null)
				throw new ProcessingException("CCG was short");
			if (pos.endsWith("\tEOS")) {
				reachedEOS = true;
				pos = pos.substring(0, pos.length() - 4);
			}
			if (keepPreviousPos && word.hasFeature(getPosFeatureName()))
				continue;
			word.addFeature(getPosFeatureName(), pos.intern());
		}
		if (!reachedEOS)
			throw new ProcessingException("CCG sentence was too long: " + sentence.getSentenceAnnotation());
	}
	
	@TimeThis(task="read-ccg-out", category=TimerCategory.COLLECT_DATA)
	protected void readSentences(@SuppressWarnings("unused") ProcessingContext<Corpus> ctx, BufferedReader r, List<Layer> sentences) throws ProcessingException, IOException {
		for (Layer sent : sentences)
			readSentence(r, sent);
	}
	
	private final class CCGPosTaggerExternal implements External<Corpus> {
		private final int maxLength;
		private final OutputFile input;
		private final InputFile output;
		private final ProcessingContext<Corpus> ctx;
		
		private CCGPosTaggerExternal(ProcessingContext<Corpus> ctx, int n, int maxLength) {
			super();
			this.ctx = ctx;
			this.maxLength = maxLength;
			File tmp = getTempDir(ctx);
			String h = String.format("corpus_%8H", n);
			input = new OutputFile(tmp, h + ".txt");
			output = new InputFile(tmp, h + ".pos");
		}

		@Override
		public Module<Corpus> getOwner() {
			return CCGPosTagger.this;
		}

		@Override
		public String[] getCommandLineArgs() throws ModuleException {
			return new String[] {
					executable.getAbsolutePath(),
					"--model",
					model.getAbsolutePath(),
					"--input",
					input.getAbsolutePath(),
					"--output",
					output.getAbsolutePath(),
					"--ofmt",
					"%p\\n\\tEOS\\n",
					"--maxwords",
					Integer.toString(maxLength)
			};
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
	        if (silent) {
				return;
			}
	        Logger logger = getLogger(ctx);
	        try {
	            logger.fine("CCG standard error:");
	            for (String line = err.readLine(); line != null; line = err.readLine()) {
	                logger.fine("    " + line);
	            }
	            logger.fine("end of CCG standard error");
	        }
	        catch (IOException ioe) {
	            logger.warning("could not read CCG standard error: " + ioe.getMessage());
	        }
		}
	}

	@Param
	public ExecutableFile getExecutable() {
		return executable;
	}

	@Param
	public InputDirectory getModel() {
		return model;
	}

	@Param
	public Boolean getSilent() {
		return silent;
	}

	@Param
	public String getInternalEncoding() {
		return internalEncoding;
	}

	@Param
	public Boolean getKeepPreviousPos() {
		return keepPreviousPos;
	}

	public void setKeepPreviousPos(Boolean keepPreviousPos) {
		this.keepPreviousPos = keepPreviousPos;
	}

	public void setExecutable(ExecutableFile executable) {
		this.executable = executable;
	}

	public void setModel(InputDirectory model) {
		this.model = model;
	}

	public void setSilent(Boolean silent) {
		this.silent = silent;
	}

	public void setInternalEncoding(String internalEncoding) {
		this.internalEncoding = internalEncoding;
	}
}
