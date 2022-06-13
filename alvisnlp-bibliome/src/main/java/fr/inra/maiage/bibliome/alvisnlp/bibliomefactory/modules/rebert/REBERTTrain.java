package fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.rebert;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import javax.ws.rs.ProcessingException;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.converters.expression.parser.ExpressionParser;
import fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.DefaultExpressions;
import fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.SectionModule;
import fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.SectionModule.SectionResolvedObjects;
import fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.rebert.REBERTTrain.REBERTTrainResolvedObjects;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Annotation;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Corpus;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.DefaultNames;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Document;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Element;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.ElementVisitor;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Layer;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.NameType;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Relation;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Section;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Tuple;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.EvaluationContext;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.Evaluator;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.Expression;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.LibraryResolver;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.ResolverException;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.VariableLibrary;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.VariableLibrary.Variable;
import fr.inra.maiage.bibliome.alvisnlp.core.module.ModuleException;
import fr.inra.maiage.bibliome.alvisnlp.core.module.NameUsage;
import fr.inra.maiage.bibliome.alvisnlp.core.module.ProcessingContext;
import fr.inra.maiage.bibliome.alvisnlp.core.module.lib.AlvisNLPModule;
import fr.inra.maiage.bibliome.alvisnlp.core.module.lib.Param;
import fr.inra.maiage.bibliome.util.Iterators;
import fr.inra.maiage.bibliome.util.fragments.Fragment;
import fr.inra.maiage.bibliome.util.fragments.FragmentTag;
import fr.inra.maiage.bibliome.util.fragments.FragmentTagIterator;
import fr.inra.maiage.bibliome.util.streams.TargetStream;

@AlvisNLPModule(beta = true)
public class REBERTTrain extends SectionModule<REBERTTrainResolvedObjects> {
	private static final String[] SUBJECT_TAGS = new String[] { "@@", "@@" };
	private static final String[] OBJECT_TAGS = new String[] { "<<", ">>" };

	private Expression subjects;
	private Expression objects;
	private Expression start = DefaultExpressions.ANNOTATION_START;
	private Expression end = DefaultExpressions.ANNOTATION_END;
	private Expression label;
	private String sentenceLayer = DefaultNames.getSentenceLayer();
	private TargetStream dataFile;

	public static class REBERTTrainResolvedObjects extends SectionResolvedObjects {
		private final Evaluator subjects;
		private final Evaluator objects;
		private final Evaluator start;
		private final Evaluator end;
		private final Evaluator label;
		private final Variable subjectVariable;
		private final Variable objectVariable;

		public REBERTTrainResolvedObjects(ProcessingContext<Corpus> ctx, REBERTTrain module) throws ResolverException {
			super(ctx, module);
			this.subjects = module.subjects.resolveExpressions(rootResolver);
			this.objects = module.objects.resolveExpressions(rootResolver);
			this.start = module.start.resolveExpressions(rootResolver);
			this.end = module.end.resolveExpressions(rootResolver);
			VariableLibrary argLib = new VariableLibrary("arg");
			this.subjectVariable = argLib.newVariable("subject");
			this.objectVariable = argLib.newVariable("object");
			LibraryResolver argResolver = argLib.newLibraryResolver(rootResolver);
			this.label = module.label.resolveExpressions(argResolver);
		}

		@Override
		public void collectUsedNames(NameUsage nameUsage, String defaultType) throws ModuleException {
			super.collectUsedNames(nameUsage, defaultType);
			this.subjects.collectUsedNames(nameUsage, defaultType);
			this.objects.collectUsedNames(nameUsage, defaultType);
			this.start.collectUsedNames(nameUsage, defaultType);
			this.end.collectUsedNames(nameUsage, defaultType);
			this.label.collectUsedNames(nameUsage, defaultType);
		}
	}
	
	private Map<Annotation,Integer> getSentencesOrder(Document doc, EvaluationContext ctx) {
		Map<Annotation,Integer> result = new HashMap<>();
		int order = 0;
		for (Section sec : Iterators.loop(sectionIterator(ctx, doc))) {
			Layer sentences = sec.ensureLayer(sentenceLayer);
			for (Annotation sent : sentences) {
				result.put(sent, order);
				order++;
			}
		}
		return result;
	}
	
	private Collection<Element> evaluateArguments(Document doc, EvaluationContext ctx, Evaluator args) {
		Collection<Element> result = new ArrayList<>();
		for (Section sec : Iterators.loop(sectionIterator(ctx, doc))) {
			result.addAll(args.evaluateList(ctx, sec));
		}
		return result;
	}
	
	private static final ElementVisitor<Section,Void> ELEMENT_SECTION = new ElementVisitor<Section,Void>() {
		@Override
		public Section visit(Annotation a, Void param) {
			return a.getSection();
		}

		@Override
		public Section visit(Corpus corpus, Void param) {
			return null;
		}

		@Override
		public Section visit(Document doc, Void param) {
			return null;
		}

		@Override
		public Section visit(Relation rel, Void param) {
			return rel.getSection();
		}

		@Override
		public Section visit(Section sec, Void param) {
			return sec;
		}

		@Override
		public Section visit(Tuple t, Void param) {
			return t.getRelation().accept(this, param);
		}

		@Override
		public Section visit(Element e, Void param) {
			return null;
		}
	};

	private Annotation getSentence(EvaluationContext evalCtx, Element arg, int from, int to) {
		Section sec = arg.accept(ELEMENT_SECTION, null);
		if (sec == null) {
			return null;
		}
		Layer sentences = sec.ensureLayer(sentenceLayer);
		Layer includingSentences = sentences.including(from, to);
		if (includingSentences.isEmpty()) {
			return null;
		}
		return includingSentences.first();
	}
	
	private static class ArgumentInfo implements Fragment {
		private final String opening;
		private final String closing;
		private final Element element;
		private final int start;
		private final int end;
		private final Annotation sentence;
		private final int sentenceOrd;

		private ArgumentInfo(String opening, String closing, Element element, int start, int end, Annotation sentence, int sentenceOrd) {
			super();
			this.opening = opening;
			this.closing = closing;
			this.element = element;
			this.start = start;
			this.end = end;
			this.sentence = sentence;
			this.sentenceOrd = sentenceOrd;
		}

		@Override
		public int getStart() {
			return start;
		}

		@Override
		public int getEnd() {
			return end;
		}
	}
	
	private static class TextBuilder implements FragmentTagIterator<String,ArgumentInfo> {
		private final StringBuilder result = new StringBuilder();

		@Override
		public void handleGap(String text, int from, int to) {
			result.append(text.substring(from, to));
		}

		@Override
		public void handleHead(String text, int to) {
			result.append(text.substring(0, to));
		}

		@Override
		public void handleTag(String text, FragmentTag<ArgumentInfo> tag) {
			ArgumentInfo arg = tag.getFragment();
			space();
			switch (tag.getTagType()) {
				case EMPTY: throw new RuntimeException();
				case OPEN: {
					result.append(arg.opening);
					break;
				}
				case CLOSE: {
					result.append(arg.closing);
					break;
				}
			}
			space();
		}

		@Override
		public void handleTail(String text, int from) {
			result.append(text.substring(from));
		}
		
		public void space() {
			result.append(' ');
		}
		
		public String getText() {
			return result.toString();
		}
		
		private static String buildText(ArgumentInfo... argsInfo) {
			TextBuilder textBuilder = new TextBuilder();
			Annotation sentence = argsInfo[0].sentence;
			FragmentTag.iterateFragments(textBuilder, sentence.getForm(), Arrays.asList(argsInfo), sentence.getStart());
			return textBuilder.getText();
		}
	}
	
	private ArgumentInfo getArgumentInfo(Map<Annotation,Integer> sentencesOrd, EvaluationContext evalCtx, Element element, boolean is_subject) {
		REBERTTrainResolvedObjects resObj = getResolvedObjects();
		int start = resObj.start.evaluateInt(evalCtx, element);
		int end = resObj.end.evaluateInt(evalCtx, element);
		Annotation sentence = getSentence(evalCtx, element, start, end);
		if (sentence == null) {
			return null;
		}
		if (!sentencesOrd.containsKey(sentence)) {
			return null;
		}
		int sentenceOrd = sentencesOrd.get(sentence);
		String[] tags = is_subject ? SUBJECT_TAGS : OBJECT_TAGS;
		return new ArgumentInfo(tags[0], tags[1], element, start, end, sentence, sentenceOrd);
	}
	
	private static class CandidateText {
		private final String text;
		private final String sentence_1;
		private final String sentence_2;
		
		private CandidateText(String text) {
			this.text = text;
			this.sentence_1 = text;
			this.sentence_2 = "";
		}
		
		private CandidateText(String sentence_1, String sentence_2) {
			this.sentence_1 = sentence_1;
			this.sentence_2 = sentence_2;
			this.text = sentence_1 + " " + sentence_2;
		}
	}
	
	private CandidateText getCandidateText(ArgumentInfo subjectInfo, ArgumentInfo objectInfo) {
		if (subjectInfo.sentence == objectInfo.sentence) {
			return new CandidateText(TextBuilder.buildText(subjectInfo, objectInfo));
		}
		if (subjectInfo.sentenceOrd < objectInfo.sentenceOrd) {
			return new CandidateText(TextBuilder.buildText(subjectInfo), TextBuilder.buildText(objectInfo));
		}
		return new CandidateText(TextBuilder.buildText(objectInfo), TextBuilder.buildText(subjectInfo));
	}

	private void generateDataset(CSVPrinter printer, EvaluationContext evalCtx, Document doc) throws IOException {
		REBERTTrainResolvedObjects resObj = getResolvedObjects();
		Map<Annotation,Integer> sentencesOrd = getSentencesOrder(doc, evalCtx);
		Collection<Element> subjects = evaluateArguments(doc, evalCtx, resObj.subjects);
		Collection<Element> objects = evaluateArguments(doc, evalCtx, resObj.objects);
		for (Element subject : subjects) {
			resObj.subjectVariable.set(subject);
			ArgumentInfo subjectInfo = getArgumentInfo(sentencesOrd, evalCtx, subject, true);
			if (subjectInfo == null) {
				continue;
			}
			for (Element object : objects) {
				resObj.objectVariable.set(object);
				ArgumentInfo objectInfo = getArgumentInfo(sentencesOrd, evalCtx, object, false);
				if (objectInfo == null) {
					continue;
				}
				CandidateText candidateText = getCandidateText(subjectInfo, objectInfo);
				String label = resObj.label.evaluateString(evalCtx, doc);
				printer.printRecord(candidateText.text, candidateText.sentence_1, candidateText.sentence_2, label);
			}
		}
	}

	@Override
	public void process(ProcessingContext<Corpus> ctx, Corpus corpus) throws ModuleException {
		Logger logger = getLogger(ctx);
		EvaluationContext evalCtx = new EvaluationContext(logger);
		CSVFormat format = CSVFormat.MYSQL.withQuote('"').withDelimiter(',');
		try (Writer writer = dataFile.getWriter()) {
			try (CSVPrinter printer = new CSVPrinter(writer, format)) {
				printer.printRecord("text", "sentence_1", "sentence_2", "label");
				for (Document doc : Iterators.loop(documentIterator(evalCtx, corpus))) {
					generateDataset(printer, evalCtx, doc);
				}
			}
		}
		catch (IOException e) {
			throw new ProcessingException(e);
		}
	}

	@Override
	protected String[] addLayersToSectionFilter() {
		return new String[] { sentenceLayer };
	}

	@Override
	protected String[] addFeaturesToSectionFilter() {
		return null;
	}

	@Override
	protected REBERTTrainResolvedObjects createResolvedObjects(ProcessingContext<Corpus> ctx) throws ResolverException {
		return new REBERTTrainResolvedObjects(ctx, this);
	}

	@Param
	public Expression getSubjects() {
		return subjects;
	}

	@Param
	public Expression getObjects() {
		return objects;
	}

	@Param
	public Expression getStart() {
		return start;
	}

	@Param
	public Expression getEnd() {
		return end;
	}

	@Param
	public Expression getLabel() {
		return label;
	}

	@Param(nameType = NameType.LAYER)
	public String getSentenceLayer() {
		return sentenceLayer;
	}

	@Param
	public TargetStream getDataFile() {
		return dataFile;
	}

	public void setSubjects(Expression subjects) {
		this.subjects = subjects;
	}

	public void setObjects(Expression objects) {
		this.objects = objects;
	}

	public void setStart(Expression start) {
		this.start = start;
	}

	public void setEnd(Expression end) {
		this.end = end;
	}

	public void setLabel(Expression label) {
		this.label = label;
	}

	public void setSentenceLayer(String sentenceLayer) {
		this.sentenceLayer = sentenceLayer;
	}

	public void setDataFile(TargetStream dataFile) {
		this.dataFile = dataFile;
	}
}
