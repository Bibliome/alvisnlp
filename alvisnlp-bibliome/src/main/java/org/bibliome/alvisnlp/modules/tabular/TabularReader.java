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


package org.bibliome.alvisnlp.modules.tabular;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

import org.bibliome.alvisnlp.modules.CorpusModule;
import org.bibliome.alvisnlp.modules.ResolvedObjects;
import org.bibliome.alvisnlp.modules.tabular.TabularReader.TabularReaderResolvedObjects;
import org.bibliome.util.Iterators;
import org.bibliome.util.StringCat;
import org.bibliome.util.filelines.FileLines;
import org.bibliome.util.filelines.InvalidFileLineEntry;
import org.bibliome.util.filelines.TabularFormat;
import org.bibliome.util.streams.SourceStream;

import alvisnlp.corpus.AbstractElement;
import alvisnlp.corpus.Corpus;
import alvisnlp.corpus.Element;
import alvisnlp.corpus.ElementType;
import alvisnlp.corpus.ElementVisitor;
import alvisnlp.corpus.expressions.AbstractIntEvaluator;
import alvisnlp.corpus.expressions.AbstractListEvaluator;
import alvisnlp.corpus.expressions.AbstractStringEvaluator;
import alvisnlp.corpus.expressions.EvaluationContext;
import alvisnlp.corpus.expressions.Evaluator;
import alvisnlp.corpus.expressions.Expression;
import alvisnlp.corpus.expressions.FunctionLibrary;
import alvisnlp.corpus.expressions.LibraryResolver;
import alvisnlp.corpus.expressions.ResolverException;
import alvisnlp.documentation.Documentation;
import alvisnlp.module.ActionInterface;
import alvisnlp.module.ModuleException;
import alvisnlp.module.NameUsage;
import alvisnlp.module.ProcessingContext;
import alvisnlp.module.ProcessingException;
import alvisnlp.module.TimerCategory;
import alvisnlp.module.lib.AlvisNLPModule;
import alvisnlp.module.lib.Param;
import alvisnlp.module.lib.TimeThis;

@AlvisNLPModule(beta=true)
public abstract class TabularReader extends CorpusModule<TabularReaderResolvedObjects> implements ActionInterface {
	private SourceStream source;
	private Expression sourceElement;
	private Expression[] lineActions;
	private Integer checkNumColumns;
	private Boolean trimColumns = true;
	private Boolean skipBlank = false;
	private Boolean commitLines = false;

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
		TabularReaderResolvedObjects resObj = getResolvedObjects();
    	Logger logger = getLogger(ctx);
		EvaluationContext evalCtx = new EvaluationContext(commitLines ? null : logger, this);

		TabularFormat format = new TabularFormat();
		if (checkNumColumns != null) {
			format.setNumColumns(checkNumColumns);
			format.setStrictColumnNumber(true);
		}
		format.setSkipBlank(skipBlank);
		format.setSkipEmpty(skipBlank);
		format.setTrimColumns(trimColumns);
		
		TabularReaderFileLines trfl = new TabularReaderFileLines(format, getLogger(ctx), resObj.lineActions, evalCtx, commitLines);
		readLines(ctx, evalCtx, corpus, trfl);
		commit(ctx, evalCtx);
	}
	
	@TimeThis(task="read", category=TimerCategory.LOAD_RESOURCE)
	protected void readLines(@SuppressWarnings("unused") ProcessingContext<Corpus> ctx, EvaluationContext evalCtx, Corpus corpus, TabularReaderFileLines trfl) throws ProcessingException {
		TabularReaderResolvedObjects resObj = getResolvedObjects();
		try {
			for (BufferedReader r : Iterators.loop(source.getBufferedReaders())) {
				resObj.entryLib.startSource(source.getStreamName(r));
				Iterator<Element> it = resObj.sourceElement.evaluateElements(evalCtx, corpus);
				if (it.hasNext()) {
					trfl.element = it.next();
					trfl.process(r, resObj.entryLib);
				}
				r.close();
			}
		}
		catch (IOException|InvalidFileLineEntry e) {
			rethrow(e);
		}
	}
	
	protected static final class TabularReaderFileLines extends FileLines<EntryLibrary> {
		private final Evaluator[] actions;
		private final EvaluationContext evalCtx;
		private final boolean commitLines;
		private Element element;
		
		
		private TabularReaderFileLines(TabularFormat format, Logger logger, Evaluator[] actions, EvaluationContext evalCtx, boolean commitLines) {
			super(format, logger);
			this.actions = actions;
			this.evalCtx = evalCtx;
			this.commitLines = commitLines;
		}

		@Override
		public void processEntry(EntryLibrary data, int lineno, List<String> entry) throws InvalidFileLineEntry {
			data.startLine(lineno, entry);
			for (Evaluator expr : actions) {
				Iterators.deplete(expr.evaluateElements(evalCtx, element));
			}
			if (commitLines) {
				evalCtx.commit();
			}
		}
	}
	
	protected static final class EntryLibrary extends FunctionLibrary {
		private String source = "";
		private List<String> entry = Collections.emptyList();
		private int line = 0;

		private void startSource(String source) {
			this.source = source;
			entry = Collections.emptyList();
			line = 0;
		}
		
		private void startLine(int line, List<String> entry) {
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
			case "range":
				checkRangeArity(ftors, args, 1, 2);
				switch (args.size()) {
				case 1: return new ColumnRangeEvaluator(args.get(0).resolveExpressions(resolver), null);
				case 2: return new ColumnRangeEvaluator(args.get(0).resolveExpressions(resolver), args.get(1).resolveExpressions(resolver));
				}
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
		
		private final class ColumnRangeEvaluator extends AbstractListEvaluator {
			private final Evaluator from;
			private final Evaluator to;
			
			private ColumnRangeEvaluator(Evaluator from, Evaluator to) {
				super();
				this.from = from;
				this.to = to;
			}

			@Override
			public List<Element> evaluateList(EvaluationContext ctx, Element elt) {
				int from = this.from.evaluateInt(ctx, elt);
				int to = this.to == null ? entry.size() : this.to.evaluateInt(ctx, elt);
				List<Element> result = new ArrayList<Element>(to - from);
				for (String s : entry.subList(from, to)) {
					result.add(new ColumnElement(elt, s));
				}
				return result;
			}

			@Override
			public void collectUsedNames(NameUsage nameUsage, String defaultType) throws ModuleException {
				from.collectUsedNames(nameUsage, defaultType);
				to.collectUsedNames(nameUsage, defaultType);
			}
		}
		
		@SuppressWarnings("serial")
		private static final class ColumnElement extends AbstractElement {
			private final Element parent;
			private final String value;

			private ColumnElement(Element parent, String value) {
				super("value", null);
				this.parent = parent;
				this.value = value;
			}

			@Override
			public String getStaticFeatureValue() {
				return value;
			}

			@Override
			public <R,P> R accept(ElementVisitor<R, P> visitor, P param) {
				return visitor.visit(this, param);
			}

			@Override
			public ElementType getType() {
				return ElementType.OTHER;
			}

			@Override
			public Element getParent() {
				return parent;
			}

			@Override
			public Element getOriginal() {
				return parent;
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

	@Param(mandatory=false)
	public Integer getCheckNumColumns() {
		return checkNumColumns;
	}

	@Param
	public Boolean getTrimColumns() {
		return trimColumns;
	}

	@Param
	public Boolean getSkipBlank() {
		return skipBlank;
	}

	@Param
	public Boolean getCommitLines() {
		return commitLines;
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

	public void setCheckNumColumns(Integer checkNumColumns) {
		this.checkNumColumns = checkNumColumns;
	}

	public void setTrimColumns(Boolean trimColumns) {
		this.trimColumns = trimColumns;
	}

	public void setSkipBlank(Boolean skipBlank) {
		this.skipBlank = skipBlank;
	}
}
