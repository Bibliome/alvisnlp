package fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.enju;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.enju.EnjuParser.EnjuParserResolvedObjects;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Annotation;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Corpus;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Layer;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Relation;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Section;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Tuple;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.EvaluationContext;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.Evaluator;
import fr.inra.maiage.bibliome.alvisnlp.core.module.ModuleException;
import fr.inra.maiage.bibliome.alvisnlp.core.module.ProcessingContext;
import fr.inra.maiage.bibliome.alvisnlp.core.module.ProcessingException;
import fr.inra.maiage.bibliome.alvisnlp.core.module.lib.ExternalHandler;
import fr.inra.maiage.bibliome.util.Iterators;
import fr.inra.maiage.bibliome.util.fragments.SimpleFragment;
import fr.inra.maiage.bibliome.util.streams.FileTargetStream;
import fr.inra.maiage.bibliome.util.streams.TargetStream;

class EnjuParserExternalHandler extends ExternalHandler<Corpus,EnjuParser> {
	private final Collection<Layer> sentences = new ArrayList<Layer>();
	private int maxSentenceLength = -1;

	EnjuParserExternalHandler(ProcessingContext<Corpus> processingContext, EnjuParser module, Corpus annotable) {
		super(processingContext, module, annotable);
	}

	@Override
	protected void prepare() throws IOException, ModuleException {
		fillSentences();
		writeEnjuInput();
	}
	
	private void fillSentences() {
		sentences.clear();
		EnjuParser owner = getModule();
		EnjuParserResolvedObjects resObj = owner.getResolvedObjects();
		Evaluator sentenceFilter = resObj.getSentenceFilter();
		EvaluationContext evalCtx = new EvaluationContext(getLogger());
		for (Section sec : Iterators.loop(owner.sectionIterator(evalCtx, getAnnotable())))
			for (Layer sent : sec.getSentences(owner.getWordLayerName(), owner.getSentenceLayerName()))
				if (sentenceFilter.evaluateBoolean(evalCtx, sent.getSentenceAnnotation()))
					sentences.add(sent);
	}

	private void writeEnjuInput() throws IOException {
		TargetStream enjuTS = new FileTargetStream(getModule().getEnjuEncoding(), getInputFile().getAbsolutePath());
		try (PrintStream ps = enjuTS.getPrintStream()) {
			maxSentenceLength = 0;
			for (Layer sent : sentences) {
				int sentLength = sent.size();
				maxSentenceLength = Math.max(maxSentenceLength, sentLength);
				writeSentence(ps, sent);
			}
		}
	}

	private void writeSentence(PrintStream ps, Layer sent) {
		EnjuParser enjuParser = getModule();
		boolean notFirst = false;
		for (Annotation word : sent) {
			if (notFirst) {
				ps.print(' ');
			}
			else {
				notFirst = true;
			}
			writeToken(ps, word.getLastFeature(enjuParser.getWordFormFeatureName()));
			if (word.hasFeature(enjuParser.getPosFeatureName())) {
				ps.print('/');
				writeToken(ps, word.getLastFeature(enjuParser.getPosFeatureName()));
			}
		}
		ps.println();
	}

	private static void writeToken(PrintStream out, String s) {
		for (int i = 0; i < s.length(); ++i) {
			final char c = s.charAt(i);
			out.print(isSpecial(c) ? '_' : c);
		}
	}

	private static boolean isSpecial(char c) {
		return c == '/' || Character.isWhitespace(c);
	}

	@Override
	protected void collect() throws IOException, ModuleException {
		readOutput();
	}
	
	private void readOutput() throws FileNotFoundException, IOException, ProcessingException {
		Iterator<Layer> sentenceIt = sentences.iterator();
		Iterator<Annotation> tokenIt = null;
		Layer currentSentence = null;
		String currentSentenceId = null;
		int currentParse = 0;
		Map<String,EnjuClause> clauseMap = new HashMap<String,EnjuClause>();
		try (BufferedReader r = new BufferedReader(new FileReader(getOutputFile()))) {
			while (true) {
				String line = r.readLine();
				if (line == null) {
					endParse(line, currentSentence.getSentenceAnnotation(), currentParse, tokenIt, clauseMap);
					break;
				}
				if (line.isEmpty()) {
					endParse(line, currentSentence.getSentenceAnnotation(), currentParse, tokenIt, clauseMap);
					continue;
				}
				EnjuClause clause = EnjuClause.readLine(line);
				switch (clause.kind) {
					case "sentence": {
						if ((currentSentence == null) || (!currentSentenceId.equals(clause.id))) {
							if (!sentenceIt.hasNext()) {
								enjuOutError(line, "too many sentences in enju output");
							}
							currentSentence = sentenceIt.next();
							currentSentenceId = clause.id;
							currentParse = 1;
						}
						else {
							currentParse++;
						}
						clauseMap.clear();
						tokenIt = currentSentence.iterator();
						break;
					}
					case "cons": {
						clauseMap.put(clause.id, clause);
						break;
					}
					case "tok": {
						clause.head = skipToken(tokenIt, clause.attributes.get("base").equals("-PERIOD-"));
						if (clause.head == null) {
							enjuOutError(line, "enju analyzis has extra token (" + clause.attributes.get("base") + ")");
						}
						//getLogger().fine("clause base = " + clause.attributes.get("base"));
						//getLogger().fine("token form  = " + clause.annotation.getForm());
						clauseMap.put(clause.id, clause);
						break;
					}
					default: {
						enjuOutError(line, "unknown clause " + clause.kind);
					}
				}
			}
		}
	}
	
	private static Annotation skipToken(Iterator<Annotation> tokenIt, boolean expectPeriod) {
		while (tokenIt.hasNext()) {
			Annotation tok = tokenIt.next();
			String form = tok.getForm();
			if (expectPeriod || !(form.equals(".") || form.equals("?"))) {
				return tok;
			}
		}
		return null;
	}
	
	private void endParse(String line, Annotation sentence, int currentParse, Iterator<Annotation> tokenIt, Map<String,EnjuClause> clauseMap) throws ProcessingException {
		Annotation eos = skipToken(tokenIt, false);
		if (eos != null) {
			enjuOutError(line, "parse did not analyze all tokens (" + eos.getForm() + ")");
		}
		for (EnjuClause clause : clauseMap.values()) {
			if (clause.kind.equals("cons") && (clause.head == null)) {
				clause.head = lookupHead(line, clauseMap, clause.attributes.get("head"));
			}
		}
		EnjuParser owner = getModule();
		for (EnjuClause clause : clauseMap.values()) {
			if (clause.kind.equals("tok")) {
				Section sec = clause.head.getSection();
				Relation rel = sec.ensureRelation(owner, owner.getDependenciesRelationName());
				createDependencies(rel, sentence, Integer.toString(currentParse), clauseMap, clause);
			}
		}
	}
	
	private static final Pattern ENJU_PRED_PATTERN = Pattern.compile("(?:\033\\[\\?1034h)?(?<label>[a-z]+)(?<mod>_mod)?_arg\\d*(?<argn>\\d)");
	private void createDependencies(Relation rel, Annotation sentence, String parseNumber, Map<String,EnjuClause> clauseMap, EnjuClause tok) throws ProcessingException {
		String predString = tok.checkAttribute("pred");
		Matcher m = ENJU_PRED_PATTERN.matcher(predString);
		if (!m.matches()) {
			enjuOutError(tok.line, "malformed pred " + predString);
		}
		EnjuParser owner = getModule();
		String label = m.group("label");
		String argsString = m.group("argn");
		if (!argsString.equals("0")) {
			for (int i = 0; i < argsString.length(); ++i) {
				int argn = argsString.charAt(i) - 48;
				String key = "arg" + argn;
				Tuple t = new Tuple(owner, rel);
				t.addFeature(owner.getDependencyLabelFeatureName(), label);
				t.addFeature(owner.getDependentTypeFeatureName(), key);
				t.addFeature(owner.getParseNumberFeatureName(), parseNumber);
				t.setArgument(owner.getSentenceRole(), sentence);
				t.setArgument(owner.getDependencyHeadRole(), tok.head);
				String dependentId = tok.checkAttribute(key);
				if (!dependentId.equals("unk")) {
					if (!clauseMap.containsKey(dependentId)) {
						enjuOutError(tok.line, "unknown argument " + dependentId);
					}
					EnjuClause dependent = clauseMap.get(dependentId);
					t.setArgument(owner.getDependencyDependentRole(), dependent.head);
				}
			}
		}
	}

	private Annotation lookupHead(String line, Map<String,EnjuClause> clauseMap, String id) throws ProcessingException {
		if (!clauseMap.containsKey(id)) {
			enjuOutError(line, "unknown constituent or token " + id);
		}
		EnjuClause clause = clauseMap.get(id);
		if (clause.head == null) {
			clause.head = lookupHead(clause.line, clauseMap, clause.checkAttribute("head"));
		}
		return clause.head;
	}

	private static class EnjuClause extends SimpleFragment {
		private final String line;
		private final String kind;
		private final Map<String,String> attributes;
		private final String id;
		private Annotation head = null;
		
		private EnjuClause(int start, int end, String line, String kind, Map<String,String> attributes) throws ProcessingException {
			super(start, end);
			this.line = line;
			this.kind = kind;
			this.attributes = attributes;
			this.id = checkAttribute("id");
		}
		
		private static final Pattern ENJU_LINE_PATTERN = Pattern.compile("(?<start>\\d+)\t(?<end>\\d+)\t(?<kind>[a-z0-9_]+) (?<attr>.*)");
		private static EnjuClause readLine(String line) throws ProcessingException {
			Matcher m = ENJU_LINE_PATTERN.matcher(line);
			if (!m.matches()) {
				enjuOutError(line, "could not parse line");
			}
			int start = Integer.parseInt(m.group("start"));
			int end = Integer.parseInt(m.group("end"));
			String kind = m.group("kind");
			Map<String,String> attributes = parseAttributes(line, m.group("attr"));
			return new EnjuClause(start, end, line, kind, attributes);
		}
		
		private static final Pattern ENJU_ATTRIBUTE_PATTERN = Pattern.compile("\\s*(?<key>[a-z0-9_]+)=\"(?<value>.*?)\"\\s*");
		private static Map<String,String> parseAttributes(String line, String attrStr) throws ProcessingException {
			Map<String,String> result = new LinkedHashMap<String,String>();
			Matcher attrMatcher = ENJU_ATTRIBUTE_PATTERN.matcher(attrStr);
			while (attrMatcher.find()) {
				String key = attrMatcher.group("key");
				String value = attrMatcher.group("value");
				result.put(key, value);
			}
			if (attrMatcher.requireEnd()) {
				enjuOutError(line, "could not parse attributes");
			}
			return result;
		}
		
		private String checkAttribute(String key) throws ProcessingException {
			if (!attributes.containsKey(key)) {
				enjuOutError(line, "missing attribute " + key);
			}
			return attributes.get(key);
		}
	}

	private static void enjuOutError(String line, String msg) throws ProcessingException {
		throw new ProcessingException("could not parse line (" + msg + "): " + line);
	}

	@Override
	protected String getPrepareTask() {
		return "alvisnlp-to-enju";
	}

	@Override
	protected String getExecTask() {
		return "enju";
	}

	@Override
	protected String getCollectTask() {
		return "enju-to-alvisnlp";
	}

	@Override
	protected List<String> getCommandLine() {
		List<String> result = new ArrayList<String>();
		EnjuParser owner = getModule();
		result.add(owner.getEnjuExecutable().getAbsolutePath());
		result.add("-nt");
		result.add("-so");
		result.add(owner.getBiology() ? "-genia" : "-brown");
		result.add("-W");
		result.add(Integer.toString(maxSentenceLength));
		int nBest = owner.getnBest();
		if (nBest > 1) {
			result.add("-N");
			result.add(Integer.toString(nBest));
		}
		return result;
	}

	@Override
	protected void updateEnvironment(Map<String,String> env) {
	}

	@Override
	protected File getWorkingDirectory() {
		return null;
	}

	@Override
	protected String getInputFileame() {
		return "corpus.txt";
	}

	@Override
	protected String getOutputFilename() {
		return "corpus.enju";
	}
}
