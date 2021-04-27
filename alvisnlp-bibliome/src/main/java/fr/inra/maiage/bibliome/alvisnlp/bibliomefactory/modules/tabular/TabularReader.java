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


package fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.tabular;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.CorpusModule;
import fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.ResolvedObjects;
import fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.tabular.TabularReader.TabularReaderResolvedObjects;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Corpus;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Element;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.AbstractIntEvaluator;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.AbstractStringEvaluator;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.EvaluationContext;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.Evaluator;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.Expression;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.FunctionLibrary;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.LibraryResolver;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.ResolverException;
import fr.inra.maiage.bibliome.alvisnlp.core.documentation.Documentation;
import fr.inra.maiage.bibliome.alvisnlp.core.module.ActionInterface;
import fr.inra.maiage.bibliome.alvisnlp.core.module.ModuleException;
import fr.inra.maiage.bibliome.alvisnlp.core.module.NameUsage;
import fr.inra.maiage.bibliome.alvisnlp.core.module.ProcessingContext;
import fr.inra.maiage.bibliome.alvisnlp.core.module.ProcessingException;
import fr.inra.maiage.bibliome.alvisnlp.core.module.lib.AlvisNLPModule;
import fr.inra.maiage.bibliome.alvisnlp.core.module.lib.Param;
import fr.inra.maiage.bibliome.util.Iterators;
import fr.inra.maiage.bibliome.util.StringCat;
import fr.inra.maiage.bibliome.util.filelines.InvalidFileLineEntry;
import fr.inra.maiage.bibliome.util.streams.SourceStream;

@AlvisNLPModule(beta=true)
public abstract class TabularReader extends CorpusModule<TabularReaderResolvedObjects> implements ActionInterface {
	private SourceStream source;
	private Expression sourceElement;
	private Expression[] lineActions;
	private Boolean commitLines = false;
	private Character separator = '\t';
	private Character quote = '"';

	static class TabularReaderResolvedObjects extends ResolvedObjects {
		private final EntryLibrary entryLib;
		private final Evaluator sourceElement;
		private final Evaluator[] lineActions;

		private TabularReaderResolvedObjects(ProcessingContext<Corpus> ctx, TabularReader module) throws ResolverException {
			super(ctx, module);
			entryLib = new EntryLibrary();
			rootResolver.addLibrary(entryLib);
			sourceElement = rootResolver.resolveNullable(module.sourceElement);
			lineActions = rootResolver.resolveArray(module.lineActions, Evaluator.class);
		}

		@Override
		public void collectUsedNames(NameUsage nameUsage, String defaultType) throws ModuleException {
			super.collectUsedNames(nameUsage, defaultType);
			nameUsage.collectUsedNamesNullable(sourceElement, defaultType);
			nameUsage.collectUsedNamesArray(lineActions, defaultType);
		}
	}

	@Override
	protected TabularReaderResolvedObjects createResolvedObjects(ProcessingContext<Corpus> ctx) throws ResolverException {
		return new TabularReaderResolvedObjects(ctx, this);
	}

	@Override
	public void process(ProcessingContext<Corpus> ctx, Corpus corpus) throws ModuleException {
    	Logger logger = getLogger(ctx);
		EvaluationContext evalCtx = new EvaluationContext(commitLines ? null : logger, this);
		TabularReaderResolvedObjects resObj = getResolvedObjects();
		CSVFormat format = CSVFormat.MYSQL.withQuote(quote).withDelimiter(separator).withHeader();
		Iterator<Element> it = resObj.sourceElement.evaluateElements(evalCtx, corpus);
		if (it.hasNext()) {
			Element element = it.next();
			try {
				for (BufferedReader r : Iterators.loop(source.getBufferedReaders())) {
					resObj.entryLib.startSource(source.getStreamName(r));
					try (CSVParser parser = new CSVParser(r, format)) {
						int line = 0;
						for (CSVRecord entry : parser) {
							resObj.entryLib.startLine(++line, entry);
							for (Evaluator expr : resObj.lineActions) {
								Iterators.deplete(expr.evaluateElements(evalCtx, element));
							}
							if (commitLines) {
								evalCtx.commit();
							}
						}
					}
					r.close();
				}
			}
			catch (IOException|InvalidFileLineEntry e) {
				throw new ProcessingException(e);
			}
		}
		commit(ctx, evalCtx);
	}
	
	protected static final class EntryLibrary extends FunctionLibrary {
		private String source = "";
		private CSVRecord entry = null;
		private int line = 0;

		private void startSource(String source) {
			this.source = source;
			entry = null;
			line = 0;
		}
		
		private void startLine(int line, CSVRecord entry) {
			this.line = line;
			this.entry = entry;
		}
		
		@Override
		public String getName() {
			return "tab";
		}

		@Override
		public Evaluator resolveExpression(LibraryResolver resolver,	List<String> ftors, List<Expression> args) throws ResolverException {
			checkExactFtors(ftors, 1);
			String firstFtor = ftors.get(0);
			switch (firstFtor) {
			case "source":
				checkExactArity(ftors, args, 0);
				return SOURCE;
			case "column":
				checkExactArity(ftors, args, 1);
				return new ColumnEvaluator(args.get(0).resolveExpressions(resolver));
			case "line":
				checkExactArity(ftors, args, 0);
				return LINE;
			case "width":
				checkExactArity(ftors, args, 0);
				return WIDTH;
			}
			return null;
		}

		@Override
		public Documentation getDocumentation() {
			return null;
		}
		
		private final class ColumnEvaluator extends AbstractStringEvaluator {
			private final Evaluator column;
			
			private ColumnEvaluator(Evaluator column) {
				super();
				this.column = column;
			}

			@Override
			public String evaluateString(EvaluationContext ctx, Element elt) {
//				System.err.println("entry = " + entry);
				return entry.get(column.evaluateInt(ctx, elt));
			}
			
			@Override
			public void evaluateString(EvaluationContext ctx, Element elt, StringCat strcat) {
				strcat.append(entry.get(column.evaluateInt(ctx, elt)));
			}

			@Override
			public void collectUsedNames(NameUsage nameUsage, String defaultType) throws ModuleException {
				column.collectUsedNames(nameUsage, defaultType);
			}
		}

		private final Evaluator SOURCE = new AbstractStringEvaluator() {
			@Override
			public String evaluateString(EvaluationContext ctx, Element elt) {
				return source;
			}
			
			@Override
			public void evaluateString(EvaluationContext ctx, Element elt, StringCat strcat) {
				strcat.append(source);
			}

			@Override
			public void collectUsedNames(NameUsage nameUsage, String defaultType) throws ModuleException {
			}
		};
		
		private final Evaluator LINE = new AbstractIntEvaluator() {
			@Override
			public int evaluateInt(EvaluationContext ctx, Element elt) {
				return line;
			}

			@Override
			public void collectUsedNames(NameUsage nameUsage, String defaultType) throws ModuleException {
			}
		};
		
		private final Evaluator WIDTH = new AbstractIntEvaluator() {
			@Override
			public int evaluateInt(EvaluationContext ctx, Element elt) {
				return entry.size();
			}

			@Override
			public void collectUsedNames(NameUsage nameUsage, String defaultType) throws ModuleException {
			}
		};
	}

	@Param
	public SourceStream getSource() {
		return source;
	}

	@Param
	public Expression getSourceElement() {
		return sourceElement;
	}

	@Param
	public Expression[] getLineActions() {
		return lineActions;
	}

	@Param
	public Boolean getCommitLines() {
		return commitLines;
	}

	@Param
	public Character getSeparator() {
		return separator;
	}

	public void setSeparator(Character separator) {
		this.separator = separator;
	}

	public void setCommitLines(Boolean commitLines) {
		this.commitLines = commitLines;
	}

	public void setSource(SourceStream source) {
		this.source = source;
	}

	public void setSourceElement(Expression sourceElement) {
		this.sourceElement = sourceElement;
	}

	public void setLineActions(Expression[] lineActions) {
		this.lineActions = lineActions;
	}
}
