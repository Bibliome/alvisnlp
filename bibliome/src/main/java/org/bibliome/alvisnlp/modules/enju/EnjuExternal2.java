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


package org.bibliome.alvisnlp.modules.enju;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bibliome.util.Files;
import org.bibliome.util.files.ExecutableFile;
import org.bibliome.util.files.InputFile;
import org.bibliome.util.files.OutputFile;
import org.bibliome.util.fragments.Fragment;
import org.bibliome.util.fragments.FragmentComparator;
import org.bibliome.util.fragments.SimpleFragment;
import org.bibliome.util.streams.FileSourceStream;
import org.bibliome.util.streams.FileTargetStream;
import org.bibliome.util.streams.SourceStream;
import org.bibliome.util.streams.TargetStream;

import alvisnlp.corpus.Annotation;
import alvisnlp.corpus.Corpus;
import alvisnlp.corpus.Layer;
import alvisnlp.corpus.Relation;
import alvisnlp.corpus.Section;
import alvisnlp.corpus.Tuple;
import alvisnlp.module.Module;
import alvisnlp.module.ModuleException;
import alvisnlp.module.ProcessingException;
import alvisnlp.module.lib.External;
import alvisnlp.module.lib.ModuleBase;

public class EnjuExternal2 implements External<Corpus> {
	private static final Pattern ENJU_LINE_PATTERN = Pattern.compile("(?<start>\\d+)\t(?<end>\\d+)\t(?<kind>[a-z0-9_]+) (?<attr>.*)");
	private static final Pattern ENJU_ATTRIBUTE_PATTERN = Pattern.compile("\\s*(?<key>[a-z0-9_]+)=\"(?<value>.*?)\"\\s*");
	private static final Pattern ENJU_PRED_PATTERN = Pattern.compile("(?<label>[a-z]+)(?<mod>_mod)?_arg\\d*(?<argn>\\d)");

	private final EnjuParser2 enjuParser;
	private final Logger logger;
	private final ExecutableFile scriptFile;
	private final OutputFile enjuIn;
	private final InputFile enjuOut;
	private final int maxSentenceLength;

	// Read state
	private Iterator<Layer> sentenceIt;
	private Layer currentSentence = null;
	private String currentSentenceId = null;
	private int currentPosition = 0;
	private final Map<Fragment,Annotation> wordPositionIndex = new TreeMap<Fragment,Annotation>(new FragmentComparator<Fragment>());
	private final Map<String,Annotation> wordIdIndex = new HashMap<String,Annotation>();
	private final Map<String,String> consHeads = new HashMap<String,String>();
	private final Collection<PendingDependencies> dependencies = new ArrayList<PendingDependencies>();
	private int parseNumber = 0;

	EnjuExternal2(EnjuParser2 enjuParser2, File tempDir, Logger logger, Collection<Layer> sentences) throws IOException {
		enjuParser = enjuParser2;
		this.logger = logger;
		this.scriptFile = createScriptFile(tempDir);
		this.enjuIn = new OutputFile(tempDir, "corpus.txt");
		this.enjuOut = new InputFile(tempDir, "corpus.enju");
		this.maxSentenceLength = writeEnjuInput(sentences);
	}

	private static ExecutableFile createScriptFile(File tempDir) throws IOException {
		ExecutableFile result = new ExecutableFile(tempDir, "enju.sh");
		// same ClassLoader as this class
		InputStream is = EnjuParser.class.getResourceAsStream("enju.sh");
		Files.copy(is, result, 1024, true);
		result.setExecutable(true);
		return result;
	}

	private int writeEnjuInput(Collection<Layer> sentences) throws IOException {
		TargetStream enjuTS = new FileTargetStream(enjuParser.getEnjuEncoding(), enjuIn);
		try (PrintStream ps = enjuTS.getPrintStream()) {
			int result = 0;
			for (Layer sent : sentences) {
				int sentLength = sent.size();
				result = Math.max(result, sentLength);
				writeSentence(ps, sent);
			}
			return result;
		}
	}

	private void writeSentence(PrintStream ps, Layer sent) {
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

	void readEnjuOut(Collection<Layer> sentences) throws IOException, ProcessingException {
		sentenceIt = sentences.iterator();
		SourceStream enjuSS = new FileSourceStream(enjuParser.getEnjuEncoding(), enjuOut);
		try (BufferedReader r = enjuSS.getBufferedReader()) {
			while (true) {
				String line = r.readLine();
				if (line == null) {
					break;
				}
				line = line.trim();
				if (line.isEmpty()) {
					continue;
				}
				parseLine(line);
			}
		}
	}

	private void parseLine(String line) throws ProcessingException {
		Matcher lineMatcher = ENJU_LINE_PATTERN.matcher(line);
		if (!lineMatcher.matches()) {
			enjuOutError(line, "malformed line");
		}
		String attrStr = lineMatcher.group("attr");
		Map<String,String> attr = parseAttributes(line, attrStr);
		String kind = lineMatcher.group("kind");
		switch (kind) {
			case "sentence":
				parseSentence(line, attr);
				break;
			case "cons":
				parseCons(line, attr);
				break;
			case "tok":
				parseTok(line, attr, getTokHead(line, lineMatcher, attr));
				break;
			default:
				enjuOutError(line, "unknown kind " + kind);
		}
	}

	private void parseSentence(String line, Map<String,String> attr) throws ProcessingException {
		finishParse();
		checkAttributes(line, attr, "id");
		String id = attr.get("id");
		if (!id.equals(currentSentenceId)) {
			finishSentence();
			startSentence(line, id);
		}
		startParse(line, attr);
	}

	private void finishParse() throws ProcessingException {
		if (currentSentence == null) {
			return;
		}
		Section sec = currentSentence.getSection();
		Relation rel = sec.ensureRelation(enjuParser, enjuParser.getDependenciesRelationName());
		for (PendingDependencies dep : dependencies) {
			for (int i = 0; i < dep.args.length(); ++i) {
				char c = dep.args.charAt(i);
				int argn = c - 48;
				String role = "arg" + argn;
				createDependencyTuple(dep, rel, role);
			}
			if (dep.mod) {
				createDependencyTuple(dep, rel, "mod");
			}
		}
	}

	private void createDependencyTuple(PendingDependencies dep, Relation rel, String role) throws ProcessingException {
		checkAttributes(dep.line, dep.attr, role);
		String dependentId = dep.attr.get(role);
		if (dependentId.equals("unk")) {
			return;
		}
		Tuple t = new Tuple(enjuParser, rel);
		t.setArgument(enjuParser.getSentenceRole(), currentSentence.getSentenceAnnotation());
		t.setArgument(enjuParser.getDependencyHeadRole(), dep.head);
		Annotation dependent = searchDependent(dep.line, dependentId);
		t.setArgument(enjuParser.getDependencyDependentRole(), dependent);
		t.addFeature(enjuParser.getDependentTypeFeatureName(), role);
		t.addFeature(enjuParser.getDependencyLabelFeatureName(), dep.label);
		t.addFeature(enjuParser.getParseNumberFeatureName(), Integer.toString(parseNumber));
		t.addFeatures(dep.attr);
	}

	private Annotation searchDependent(String line, String id) throws ProcessingException {
		while (!wordIdIndex.containsKey(id)) {
			if (!consHeads.containsKey(id)) {
				enjuOutError(line, "no head for " + id); 
			}
			id = consHeads.get(id);
		}
		return wordIdIndex.get(id);
	}

	private void finishSentence() {
	}

	private void startSentence(String line, String id) throws ProcessingException {
		if (!sentenceIt.hasNext()) {
			enjuOutError(line, "too many sentences");
		}
		currentSentence = sentenceIt.next();
		currentSentenceId = id;
		wordPositionIndex.clear();
		parseNumber = 0;
		boolean notFirst = false;
		for (Annotation word : currentSentence) {
			if (notFirst) {
				currentPosition++;
			}
			else {
				notFirst = true;
			}
			String form = word.getLastFeature(enjuParser.getWordFormFeatureName());
			int formLength = form.length();
			int len = formLength;
			if (word.hasFeature(enjuParser.getPosFeatureName())) {
				String pos = word.getLastFeature(enjuParser.getPosFeatureName());
				len += 1 + pos.length();
			}
			Fragment f = new SimpleFragment(currentPosition, currentPosition + formLength);
			wordPositionIndex.put(f, word);
			currentPosition += len;
		}
		currentPosition++;
	}

	private void startParse(String line, Map<String,String> attr) throws ProcessingException {
		checkAttributes(line, attr, "parse_status");
		String parseStatus = attr.get("parse_status");
		Annotation sent = currentSentence.getSentenceAnnotation();
		sent.addFeature(enjuParser.getParseStatusFeatureName(), parseStatus);
		wordIdIndex.clear();
		consHeads.clear();
		dependencies.clear();
		parseNumber++;
	}

	private void parseCons(String line, Map<String,String> attr) throws ProcessingException {
		checkAttributes(line, attr, "id", "head");
		String id = attr.get("id");
		String head = attr.get("head");
		consHeads.put(id, head);
	}

	private void parseTok(String line, Map<String,String> attr, Annotation head) throws ProcessingException {
		checkAttributes(line, attr, "pred");
		String pred = attr.get("pred");
		Matcher predMatcher = ENJU_PRED_PATTERN.matcher(pred);
		if (!predMatcher.matches()) {
			enjuOutError(line, "malformed pred " + pred);
		}
		checkAttributes(line, attr, "id");
		String id = attr.get("id");
		wordIdIndex.put(id, head);
		String args = predMatcher.group("argn");
		if (!args.equals("0")) {
			String label = predMatcher.group("label");
			boolean mod = predMatcher.group("mod") != null;
			PendingDependencies dep = new PendingDependencies(line, head, attr, args, label, mod);
			dependencies.add(dep);
		}
	}

	private Annotation getTokHead(String line, Matcher lineMatcher, Map<String, String> attr) throws ProcessingException {
		int start = Integer.parseInt(lineMatcher.group("start"));
		int end = Integer.parseInt(lineMatcher.group("end"));
		Fragment frag = new SimpleFragment(start, end);
		if (wordPositionIndex.containsKey(frag)) {
			return wordPositionIndex.get(frag);
		}
		// Workaround a bug in enju XXX
		String form = attr.get("base");
		Annotation best = null;
		int minDistance = Integer.MAX_VALUE;
		for (Annotation a : wordPositionIndex.values()) {
			if (!a.getForm().equalsIgnoreCase(form)) {
				continue;
			}
			int d = Math.abs(a.getStart() - start);
			if (d < minDistance) {
				minDistance = d;
				best = a;
			}
		}
		String message = "token alignment (" + frag.getStart() + '-' + frag.getEnd() + ')';
		if (best == null) {
			enjuOutError(line, message);
		}
		logger.warning(message + " fallback: " + best);
		return best;
	}

	private static Map<String,String> parseAttributes(String line, String attrStr) throws ProcessingException {
		Map<String,String> result = new HashMap<String,String>();
		Matcher attrMatcher = ENJU_ATTRIBUTE_PATTERN.matcher(attrStr);
		while (attrMatcher.find()) {
			String key = attrMatcher.group("key");
			String value = attrMatcher.group("value");
			result.put(key, value);
		}
		if (attrMatcher.requireEnd()) {
			enjuOutError(line, "malformed attributes");
		}
		return result;
	}

	private static void enjuOutError(String line, String msg) throws ProcessingException {
		ModuleBase.processingException("could not parse line (" + msg + "): " + line);
	}

	private static void checkAttributes(String line, Map<String,String> attr, String... keys) throws ProcessingException {
		for (String key : keys) {
			if (!attr.containsKey(key)) {
				enjuOutError(line, "missing attribute " + key);
			}
		}
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

	private static class PendingDependencies {
		private final String line;
		private final Annotation head;
		private final Map<String,String> attr;
		private final String args;
		private final String label;
		private final boolean mod;

		private PendingDependencies(String line, Annotation head, Map<String,String> attr, String args, String label, boolean mod) {
			super();
			this.line = line;
			this.head = head;
			this.attr = attr;
			this.args = args;
			this.label = label;
			this.mod = mod;
		}
	}

	@Override
	public Module<Corpus> getOwner() {
		return enjuParser;
	}

	@Override
	public String[] getCommandLineArgs() throws ModuleException {
		return new String[] { scriptFile.getAbsolutePath() };
	}

	@Override
	public String[] getEnvironment() throws ModuleException {
		return new String[] {
				"ENJU_BIN=" + enjuParser.getEnjuExecutable().getAbsolutePath(),
				"ENJU_IN=" + enjuIn.getAbsolutePath(),
				"ENJU_OUT=" + enjuOut.getAbsolutePath(),
				"ENJU_MODEL=" + (enjuParser.getBiology() ? "-genia" : "-brown"),
				"ENJU_LENGTH=" + maxSentenceLength,
				"ENJU_N=" + (enjuParser.getnBest() > 1 ? "-N " + enjuParser.getnBest() : "")
		};
	}

	@Override
	public File getWorkingDirectory() throws ModuleException {
		return null;
	}

	@Override
	public void processOutput(BufferedReader out, BufferedReader err) throws ModuleException {
		try {
			logger.fine("enju standard error:");
			while (true) {
				String line = err.readLine();
				if (line == null)
					break;
				logger.fine("    " + line);
			}
			logger.fine("end of enju standard error");
		}
		catch (IOException e) {
			ModuleBase.rethrow(e);
		}
	}
}
