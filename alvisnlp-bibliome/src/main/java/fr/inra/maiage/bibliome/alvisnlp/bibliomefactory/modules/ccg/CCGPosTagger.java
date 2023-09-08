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


package fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.ccg;

import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

import fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.ccg.CCGBase.CCGResolvedObjects;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Corpus;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Layer;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.EvaluationContext;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.ResolverException;
import fr.inra.maiage.bibliome.alvisnlp.core.module.ModuleException;
import fr.inra.maiage.bibliome.alvisnlp.core.module.ProcessingContext;
import fr.inra.maiage.bibliome.alvisnlp.core.module.ProcessingException;
import fr.inra.maiage.bibliome.alvisnlp.core.module.lib.AlvisNLPModule;
import fr.inra.maiage.bibliome.alvisnlp.core.module.lib.Param;
import fr.inra.maiage.bibliome.util.files.ExecutableFile;
import fr.inra.maiage.bibliome.util.files.InputDirectory;

@AlvisNLPModule
public class CCGPosTagger extends CCGBase<CCGResolvedObjects> {
	private ExecutableFile executable;
	private InputDirectory model;
	private Boolean silent = false;
	private Boolean keepPreviousPos = false;

	@Override
	public void process(ProcessingContext ctx, Corpus corpus) throws ModuleException {
		try {
			Logger logger = getLogger(ctx);
			EvaluationContext evalCtx = new EvaluationContext(logger);
			List<List<Layer>> sentenceRuns = getSentences(logger, evalCtx, corpus);
			for (int run = 0; run < sentenceRuns.size(); ++run) {
				logger.info(String.format("run %d/%d", run+1, sentenceRuns.size()));
				List<Layer> sentences = sentenceRuns.get(run);
				new CCGPosTaggerExternalHandler(ctx, this, corpus, run, sentences).start();
			}
		}
		catch (IOException | InterruptedException e) {
			throw new ProcessingException(e);
		}
	}

	@Override
	protected CCGResolvedObjects createResolvedObjects(ProcessingContext ctx) throws ResolverException {
		return new CCGResolvedObjects(ctx, this);
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
}
