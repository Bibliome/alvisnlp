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


package org.bibliome.alvisnlp.modules.wapiti;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

import org.bibliome.util.Iterators;
import org.bibliome.util.files.InputFile;

import alvisnlp.corpus.Annotation;
import alvisnlp.corpus.Corpus;
import alvisnlp.corpus.Layer;
import alvisnlp.corpus.NameType;
import alvisnlp.corpus.Section;
import alvisnlp.corpus.expressions.EvaluationContext;
import alvisnlp.corpus.expressions.ResolverException;
import alvisnlp.module.ModuleException;
import alvisnlp.module.ProcessingContext;
import alvisnlp.module.ProcessingException;
import alvisnlp.module.lib.AlvisNLPModule;
import alvisnlp.module.lib.Param;

@AlvisNLPModule(beta=true)
public class WapitiLabel extends AbstractWapiti {
	private String labelFeature;
	private InputFile modelFile;

	@Override
	public void process(ProcessingContext<Corpus> ctx, Corpus corpus) throws ModuleException {
		try {
			WapitiLabelExternal external = new WapitiLabelExternal(ctx, corpus);
			callExternal(ctx, "wapiti", external, "UTF-8", "wapiti.sh");
			external.readResult(ctx, corpus);
		}
		catch (IOException e) {
			rethrow(e);
		}
	}

	@Override
	protected WapitiResolvedObjects createResolvedObjects(ProcessingContext<Corpus> ctx) throws ResolverException {
		return new WapitiResolvedObjects(ctx, this);
	}
	
	@Param(nameType=NameType.FEATURE)
	public String getLabelFeature() {
		return labelFeature;
	}

	@Param
	public InputFile getModelFile() {
		return modelFile;
	}

	public void setLabelFeature(String labelFeature) {
		this.labelFeature = labelFeature;
	}

	public void setModelFile(InputFile modelFile) {
		this.modelFile = modelFile;
	}

	private class WapitiLabelExternal extends WapitiExternal {
		public WapitiLabelExternal(ProcessingContext<Corpus> ctx, Corpus corpus) throws IOException {
			super(ctx, corpus, new File(getTempDir(ctx), "labels.txt"));
		}

		@Override
		protected String getMode() {
			return "label";
		}

		@Override
		protected void fillAdditionalCommandLineArgs(List<String> args) {
			args.add("--label");
			addOption(args, "--model", modelFile);
		}
		
		private void readResult(ProcessingContext<Corpus> ctx, Corpus corpus) throws IOException, ProcessingException {
			try (BufferedReader reader = new BufferedReader(new FileReader(outputFile))) {
				Logger logger = getLogger(ctx);
				EvaluationContext evalCtx = new EvaluationContext(logger);
				for (Section sec : Iterators.loop(sectionIterator(evalCtx, corpus))) {
					for (Layer sentence : sec.getSentences(getTokenLayerName(), getSentenceLayerName())) {
						for (Annotation a : sentence) {
							String line = reader.readLine();
							if (line == null) {
								processingException("wapiti output has too few lines");
							}
							a.addFeature(labelFeature, line.trim());
						}
						String line = reader.readLine();
						if (line == null) {
							processingException("wapiti output has too few lines");
						}
					}
				}
				if (reader.readLine() != null) {
					processingException("wapiti output has too many lines");
				}
			}
		}
	}
}
