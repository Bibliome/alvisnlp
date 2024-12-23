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


package fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.shell;

import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

import jline.ConsoleReader;

import fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.CorpusModule;
import fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.ResolvedObjects;

import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Corpus;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.creators.AnnotationCreator;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.creators.DocumentCreator;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.creators.SectionCreator;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.creators.TupleCreator;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.ResolverException;
import fr.inra.maiage.bibliome.alvisnlp.core.module.ModuleException;
import fr.inra.maiage.bibliome.alvisnlp.core.module.ProcessingContext;
import fr.inra.maiage.bibliome.alvisnlp.core.module.ProcessingException;
import fr.inra.maiage.bibliome.alvisnlp.core.module.lib.AlvisNLPModule;
import fr.inra.maiage.bibliome.alvisnlp.core.module.lib.Param;
import fr.inra.maiage.bibliome.alvisnlp.core.module.lib.TimeThis;
import fr.inra.maiage.bibliome.util.files.OutputFile;

@AlvisNLPModule
public abstract class Shell extends CorpusModule<ResolvedObjects> implements DocumentCreator, SectionCreator, AnnotationCreator, TupleCreator {
	private String prompt = "> ";
	private OutputFile historyFile = new OutputFile(System.getProperty("user.home"), ".alvisnlp/shell_history");

	@Override
	protected ResolvedObjects createResolvedObjects(ProcessingContext ctx) throws ResolverException {
		return new ResolvedObjects(ctx, this);
	}

	@Override
	public void process(ProcessingContext ctx, Corpus corpus) throws ModuleException {
		try {
	    	Logger logger = getLogger(ctx);
			ConsoleReader console = getConsoleReader(ctx);
			console.setDefaultPrompt(prompt);
			ShellEnvironment env = new ShellEnvironment(this, logger, getLibraryResolver(ctx), ctx.getLocale(), corpus);
			while (true) {
				String cmdStr = readCommand(ctx, console);
				if (cmdStr == null)
					break;
				if (cmdStr.isEmpty())
					continue;
				execCommand(ctx, env, cmdStr);
			}
		}
		catch (IOException e) {
			throw new ProcessingException(e);
		}
	}

	@TimeThis(task="prompt")
	protected String readCommand(ProcessingContext ctx, ConsoleReader console) throws IOException {
		System.out.print("\u001B[35;1m");
		String result = console.readLine();
		System.out.print("\u001B[0m");
		return result;
	}

	@TimeThis(task="execute")
	protected void execCommand(ProcessingContext ctx, ShellEnvironment env, String cmdStr) {
		env.executeCommand(cmdStr);
	}

	private ConsoleReader getConsoleReader(ProcessingContext ctx) throws IOException {
		ConsoleReader result = new ConsoleReader();
		File tmpDir = getTempDir(ctx);
		File historyFile = this.historyFile == null ? new File(tmpDir, "history") : this.historyFile;
		if (!historyFile.exists()) {
			historyFile.getParentFile().mkdirs();
		}
		result.getHistory().setHistoryFile(historyFile);
		result.setDefaultPrompt(prompt);
		return result;
	}

	@Param
	public String getPrompt() {
		return prompt;
	}

	@Param(mandatory=false)
	public OutputFile getHistoryFile() {
		return historyFile;
	}

	public void setPrompt(String prompt) {
		this.prompt = prompt;
	}

	public void setHistoryFile(OutputFile historyFile) {
		this.historyFile = historyFile;
	}
}
