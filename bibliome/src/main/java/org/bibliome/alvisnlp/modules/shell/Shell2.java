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


package org.bibliome.alvisnlp.modules.shell;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.Collection;
import java.util.Iterator;
import java.util.logging.Logger;

import jline.ConsoleReader;

import org.bibliome.alvisnlp.converters.expression.parser.ExpressionParser;
import org.bibliome.alvisnlp.converters.expression.parser.ParseException;
import org.bibliome.alvisnlp.modules.CorpusModule;
import org.bibliome.alvisnlp.modules.ResolvedObjects;
import org.bibliome.util.Iterators;
import org.bibliome.util.files.OutputFile;

import alvisnlp.corpus.Corpus;
import alvisnlp.corpus.Element;
import alvisnlp.corpus.creators.AnnotationCreator;
import alvisnlp.corpus.creators.DocumentCreator;
import alvisnlp.corpus.creators.SectionCreator;
import alvisnlp.corpus.creators.TupleCreator;
import alvisnlp.corpus.expressions.EvaluationContext;
import alvisnlp.corpus.expressions.EvaluationType;
import alvisnlp.corpus.expressions.Evaluator;
import alvisnlp.corpus.expressions.Expression;
import alvisnlp.corpus.expressions.LibraryResolver;
import alvisnlp.corpus.expressions.ResolverException;
import alvisnlp.module.ModuleException;
import alvisnlp.module.ProcessingContext;
import alvisnlp.module.lib.AlvisNLPModule;
import alvisnlp.module.lib.TimeThis;

@AlvisNLPModule
public abstract class Shell2 extends CorpusModule<ResolvedObjects> implements DocumentCreator, SectionCreator, AnnotationCreator, TupleCreator {
	private OutputFile historyFile = new OutputFile(System.getProperty("user.home"), ".alvisnlp/shell_history");

	@Override
	protected ResolvedObjects createResolvedObjects(ProcessingContext<Corpus> ctx) throws ResolverException {
		return new ResolvedObjects(ctx, this);
	}

	@Override
	public void process(ProcessingContext<Corpus> ctx, Corpus corpus) throws ModuleException {
		ExpressionParser parser = new ExpressionParser((Reader) null);
		ShellLibrary shellLib = new ShellLibrary(corpus);
		LibraryResolver resolver = shellLib.newLibraryResolver(getLibraryResolver(ctx));
		AllowLibrary allowLib = new AllowLibrary(this, this, this, this, this);
		resolver.addLibrary(allowLib);
		Logger logger = getLogger(ctx);
		EvaluationContext evalCtx = new EvaluationContext(logger);
		try {
			ConsoleReader console = getConsoleReader(ctx);
			while (true) {
				String cmdStr = console.readLine();
				if (cmdStr == null)
					break;
				cmdStr = cmdStr.trim();
				if (cmdStr.isEmpty())
					continue;
				try {
					evaluate(ctx, parser, cmdStr, resolver, shellLib, evalCtx);
					commit(ctx, evalCtx);
				}
				catch (ParseException|ResolverException e) {
					e.printStackTrace();
				}
			}
		}
		catch (IOException ioe) {
			rethrow(ioe);
		}
	}
	
	@SuppressWarnings("static-method")
	@TimeThis(task="evaluate")
	protected void evaluate(@SuppressWarnings("unused") ProcessingContext<Corpus> ctx, ExpressionParser parser, String cmdStr, LibraryResolver resolver, ShellLibrary shellLib, EvaluationContext evalCtx) throws ParseException, ResolverException {
		Reader r = new StringReader(cmdStr);
		parser.ReInit(r);
		Expression expr = parser.expression();
		Evaluator eval = expr.resolveExpressions(resolver);
		Collection<EvaluationType> types = eval.getTypes();
		EvaluationType primaryType = types.iterator().next();
		Element elt = shellLib.getCurrentElement();
		switch (primaryType) {
			case BOOLEAN:
				boolean b = eval.evaluateBoolean(evalCtx, elt);
				System.out.println(b);
				break;
			case INT:
				int i = eval.evaluateInt(evalCtx, elt);
				System.out.println(i);
				break;
			case DOUBLE:
				double d = eval.evaluateDouble(evalCtx, elt);
				System.out.println(d);
				break;
			case STRING:
				String s = eval.evaluateString(evalCtx, elt);
				DumpData.string(System.out, s);
				break;
			case UNDEFINED:
				System.err.println("could not determine the expression type, force one with boolean, int, double, string or elements");
				break;
			default:
				Iterator<Element> it = eval.evaluateElements(evalCtx, elt);
				DumpData dumpData = shellLib.getDumpData();
				for (Element e : Iterators.loop(it))
					dumpData.dump(e);
		}
	}

	private ConsoleReader getConsoleReader(ProcessingContext<Corpus> ctx) throws IOException {
		ConsoleReader result = new ConsoleReader();
		File tmpDir = getTempDir(ctx);
		File historyFile = this.historyFile == null ? new File(tmpDir, "history") : this.historyFile;
		if (!historyFile.exists()) {
			historyFile.getParentFile().mkdirs();
		}
		result.getHistory().setHistoryFile(historyFile);
		result.setDefaultPrompt(getPath() + "> ");
		return result;
	}
}
