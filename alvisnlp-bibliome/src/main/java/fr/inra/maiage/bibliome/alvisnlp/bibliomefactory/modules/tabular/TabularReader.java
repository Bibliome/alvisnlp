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
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.CorpusModule;
import fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.ResolvedObjects;
import fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.tabular.TabularReader.TabularReaderResolvedObjects;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.AbstractElement;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Corpus;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Element;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.ElementType;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.ElementVisitor;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.AbstractIntEvaluator;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.AbstractListEvaluator;
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
import fr.inra.maiage.bibliome.alvisnlp.core.module.TimerCategory;
import fr.inra.maiage.bibliome.alvisnlp.core.module.lib.AlvisNLPModule;
import fr.inra.maiage.bibliome.alvisnlp.core.module.lib.Param;
import fr.inra.maiage.bibliome.alvisnlp.core.module.lib.TimeThis;
import fr.inra.maiage.bibliome.util.Iterators;
import fr.inra.maiage.bibliome.util.StringCat;
import fr.inra.maiage.bibliome.util.filelines.FileLines;
import fr.inra.maiage.bibliome.util.filelines.InvalidFileLineEntry;
import fr.inra.maiage.bibliome.util.filelines.TabularFormat;
import fr.inra.maiage.bibliome.util.streams.SourceStream;

@AlvisNLPModule
public abstract class TabularReader extends CorpusModule<TabularReaderResolvedObjects> implements ActionInterface {
	private SourceStream source;
	private Expression sourceElement;
	private Expression[] lineActions;
	private Integer checkNumColumns;
	private Boolean trimColumns = true;
	private Boolean skipBlank = false;
	private Boolean commitLines = false;
	private Character separator = '\t';
	private Boolean trueCSV = false;
	private Boolean header = false;

	static class TabularReaderResolvedObjects extends ResolvedObjects {
		private final EntryLibrary entryLib;
		private final Evaluator sourceElement;
		private final Evaluator[] lineActions;

		private TabularReaderResolvedObjects(ProcessingContext ctx, TabularReader module) throws ResolverException {
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
	protected TabularReaderResolvedObjects createResolvedObjects(ProcessingContext ctx) throws ResolverException {
		return new TabularReaderResolvedObjects(ctx, this);
	}

	@Override
	public void process(ProcessingContext ctx, Corpus corpus) throws ModuleException {
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
		format.setSeparator(separator);

		TabularReaderFileLines trfl = new TabularReaderFileLines(format, getLogger(ctx), resObj.lineActions, evalCtx, commitLines, header);
		if (trueCSV) {
			readLinesCSV(ctx, evalCtx, corpus, trfl);
		}
		else {
			readLines(ctx, evalCtx, corpus, trfl);
		}
		commit(ctx, evalCtx);
	}

	@TimeThis(task="read", category=TimerCategory.LOAD_RESOURCE)
	protected void readLinesCSV(ProcessingContext ctx, EvaluationContext evalCtx, Corpus corpus, TabularReaderFileLines trfl) throws ProcessingException {
		TabularReaderResolvedObjects resObj = getResolvedObjects();
		CSVFormat format = CSVFormat.DEFAULT.builder().setDelimiter(separator).setIgnoreEmptyLines(skipBlank).build();
		try {
			for (BufferedReader r : Iterators.loop(source.getBufferedReaders())) {
				resObj.entryLib.startSource(source.getStreamName(r));
				Iterator<Element> it = resObj.sourceElement.evaluateElements(evalCtx, corpus);
				if (it.hasNext()) {
					trfl.element = it.next();
					CSVParser parser = format.parse(r);
					for (CSVRecord record : parser) {
						List<String> entry = new ArrayList<String>();
						Iterators.fill(record.iterator(), entry);
						trfl.processEntry(resObj.entryLib, (int) record.getRecordNumber(), entry);
					}
				}
				r.close();
			}
		}
		catch (IOException|InvalidFileLineEntry e) {
			throw new ProcessingException(e);
		}
	}

	@TimeThis(task="read", category=TimerCategory.LOAD_RESOURCE)
	protected void readLines(ProcessingContext ctx, EvaluationContext evalCtx, Corpus corpus, TabularReaderFileLines trfl) throws ProcessingException {
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
			throw new ProcessingException(e);
		}
	}

	protected static final class TabularReaderFileLines extends FileLines<EntryLibrary> {
		private final Evaluator[] actions;
		private final EvaluationContext evalCtx;
		private final boolean commitLines;
		private List<String> fieldNames = Collections.emptyList();
		private Element element;
		private boolean header;

		private TabularReaderFileLines(TabularFormat format, Logger logger, Evaluator[] actions, EvaluationContext evalCtx, boolean commitLines, boolean header) {
			super(format, logger);
			this.actions = actions;
			this.evalCtx = evalCtx;
			this.commitLines = commitLines;
			this.header = header;
		}

		@Override
		public void processEntry(EntryLibrary data, int lineno, List<String> entry) throws InvalidFileLineEntry {
			if (header) {
				fieldNames = new ArrayList<String>(entry);
				header = false;
				return;
			}
			Map<String,String> fields = getFields(entry);
			data.startLine(lineno, entry, fields);
			for (Evaluator expr : actions) {
				Iterators.deplete(expr.evaluateElements(evalCtx, element));
			}
			if (commitLines) {
				evalCtx.commit();
			}
		}

		private Map<String,String> getFields(List<String> entry) {
			Map<String,String> result = new LinkedHashMap<String,String>();
			Iterator<String> entryIt = entry.iterator();
			for (String key : fieldNames) {
				String value = entryIt.hasNext() ? entryIt.next() : "";
				result.put(key, value);
			}
			return result;
		}
	}

	protected static final class EntryLibrary extends FunctionLibrary {
		private String source = "";
		private List<String> entry = Collections.emptyList();
		private Map<String,String> fields = Collections.emptyMap();

		private int line = 0;

		private void startSource(String source) {
			this.source = source;
			entry = Collections.emptyList();
			fields = Collections.emptyMap();
			line = 0;
		}

		private void startLine(int line, List<String> entry, Map<String,String> fields) {
			this.line = line;
			this.entry = entry;
			this.fields = fields;
		}

		@Override
		public String getName() {
			return "tab";
		}

		@Override
		public Evaluator resolveExpression(LibraryResolver resolver, List<String> ftors, List<Expression> args) throws ResolverException {
			String firstFtor = ftors.get(0);
			switch (firstFtor) {
			case "source":
				checkExactFtors(ftors, 1);
				checkExactArity(ftors, args, 0);
				return SOURCE;
			case "column":
				checkExactFtors(ftors, 1);
				checkExactArity(ftors, args, 1);
				return new ColumnEvaluator(args.get(0).resolveExpressions(resolver));
			case "field": {
				checkMaxFtors(ftors, 2);
				if (ftors.size() == 2) {
					checkExactArity(ftors, args, 0);
					return new ConstantFieldEvaluator(ftors.get(1));
				}
				checkExactArity(ftors, args, 1);
				return new FieldEvaluator(args.get(0).resolveExpressions(resolver));
			}
			case "line":
				checkExactFtors(ftors, 1);
				checkExactArity(ftors, args, 0);
				return LINE;
			case "width":
				checkExactFtors(ftors, 1);
				checkExactArity(ftors, args, 0);
				return WIDTH;
			case "range":
				checkExactFtors(ftors, 1);
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
				//System.err.println("entry = " + entry);
				int index = column.evaluateInt(ctx, elt);
				if ((index < 0) || (index >= entry.size())) {
					return "";
				}
				return entry.get(index);
			}

			@Override
			public void evaluateString(EvaluationContext ctx, Element elt, StringCat strcat) {
				int index = column.evaluateInt(ctx, elt);
				if ((index >= 0) && (index < entry.size())) {
					strcat.append(entry.get(index));
				}
			}

			@Override
			public void collectUsedNames(NameUsage nameUsage, String defaultType) throws ModuleException {
				column.collectUsedNames(nameUsage, defaultType);
			}
		}

		private final class ConstantFieldEvaluator extends AbstractStringEvaluator {
			private final String fieldName;

			private ConstantFieldEvaluator(String field) {
				super();
				this.fieldName = field;
			}

			@Override
			public String evaluateString(EvaluationContext ctx, Element elt) {
				if (fields.containsKey(fieldName)) {
					return fields.get(fieldName);
				}
				return "";
			}

			@Override
			public void evaluateString(EvaluationContext ctx, Element elt, StringCat strcat) {
				if (fields.containsKey(fieldName)) {
					strcat.append(fields.get(fieldName));
				}
			}

			@Override
			public void collectUsedNames(NameUsage nameUsage, String defaultType) throws ModuleException {
			}
		}

		private final class FieldEvaluator extends AbstractStringEvaluator {
			private final Evaluator fieldName;

			private FieldEvaluator(Evaluator field) {
				super();
				this.fieldName = field;
			}

			@Override
			public String evaluateString(EvaluationContext ctx, Element elt) {
				String key = fieldName.evaluateString(ctx, elt);
				if (fields.containsKey(key)) {
					return fields.get(key);
				}
				return "";
			}

			@Override
			public void evaluateString(EvaluationContext ctx, Element elt, StringCat strcat) {
				String key = fieldName.evaluateString(ctx, elt);
				if (fields.containsKey(key)) {
					strcat.append(fields.get(key));
				}
			}

			@Override
			public void collectUsedNames(NameUsage nameUsage, String defaultType) throws ModuleException {
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

	@Param
	public Character getSeparator() {
		return separator;
	}

	@Param
	public Boolean getTrueCSV() {
		return trueCSV;
	}

	@Param
	public Boolean getHeader() {
		return header;
	}

	public void setHeader(Boolean header) {
		this.header = header;
	}

	public void setTrueCSV(Boolean trueCSV) {
		this.trueCSV = trueCSV;
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
