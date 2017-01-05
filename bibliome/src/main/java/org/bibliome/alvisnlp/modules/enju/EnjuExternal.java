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
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bibliome.util.Files;
import org.bibliome.util.Strings;
import org.bibliome.util.files.ExecutableFile;
import org.bibliome.util.files.InputFile;
import org.bibliome.util.files.OutputFile;
import org.bibliome.util.streams.FileSourceStream;
import org.bibliome.util.streams.FileTargetStream;
import org.bibliome.util.streams.TargetStream;

import alvisnlp.corpus.Annotation;
import alvisnlp.corpus.Corpus;
import alvisnlp.corpus.Layer;
import alvisnlp.corpus.Relation;
import alvisnlp.corpus.Tuple;
import alvisnlp.module.Module;
import alvisnlp.module.ModuleException;
import alvisnlp.module.ProcessingContext;
import alvisnlp.module.ProcessingException;
import alvisnlp.module.lib.External;
import alvisnlp.module.lib.ModuleBase;

final class EnjuExternal implements External<Corpus> {
	private final EnjuParser enjuParser;
	private final OutputFile inputFile;
	private final ExecutableFile scriptFile;
	private final InputFile outputFile;
	private final Collection<Layer> sentences;
	private final int maxSentenceLength;
	private final Logger logger;
	private final String parseNumberFeatureName;
	private final String sentenceRole;
	private final String dependencyHeadRole;
	private final String dependencyLabelFeatureName;
	private final String dependenciesRelationName;

	EnjuExternal(EnjuParser enjuParser, ProcessingContext<Corpus> ctx, Collection<Layer> sentences, File tempDir) throws IOException {
		this.enjuParser = enjuParser;
		this.sentences = sentences;
		inputFile = createInputFile(tempDir);
		maxSentenceLength = writeCorpus(enjuParser, sentences, inputFile);
		scriptFile = createScriptFile(tempDir);
		outputFile = createOutputFile(tempDir);
		logger = enjuParser.getLogger(ctx);
		parseNumberFeatureName = enjuParser.getParseNumberFeatureName();
		sentenceRole = enjuParser.getSentenceRole();
		dependencyHeadRole = enjuParser.getDependencyHeadRole();
		dependencyLabelFeatureName = enjuParser.getDependencyLabelFeatureName();
		dependenciesRelationName = enjuParser.getDependenciesRelationName();
	}
	
	private static ExecutableFile createScriptFile(File tempDir) throws IOException {
		ExecutableFile result = new ExecutableFile(tempDir, "enju.sh");
		// same ClassLoader as this class
		InputStream is = EnjuParser.class.getResourceAsStream("enju.sh");
		Files.copy(is, result, 1024, true);
		result.setExecutable(true);
		return result;
	}
	
	private static OutputFile createInputFile(File tempDir) {
		return new OutputFile(tempDir, "corpus.txt");
	}
	
	private static InputFile createOutputFile(File tempDir) {
		return new InputFile(tempDir, "corpus.enju");
	}
	
	private static int writeCorpus(EnjuParser enjuParser, Collection<Layer> sentences, OutputFile inputFile) throws IOException {
		TargetStream enjuTS = new FileTargetStream(enjuParser.getEnjuEncoding(), inputFile);
		String wordFormFeatureName = enjuParser.getWordFormFeatureName();
		String posFeatureName = enjuParser.getPosFeatureName();
		try (PrintStream out = enjuTS.getPrintStream()) {
			int result = 0;
			for (Layer sent : sentences) {
				writeSentence(out, sent, wordFormFeatureName, posFeatureName);
				final int len = sent.size();
				if (len > result)
					result = len;
			}
			return result;
		}
	}
	
	private static void writeSentence(PrintStream out, Collection<Annotation> sentence, String wordFormFeatureName, String posFeatureName) {
		for (Annotation word : sentence) {
			writeWordForm(out, wordFormFeatureName);
			if (word.hasFeature(posFeatureName)) {
				out.print('/');
				out.print(word.getLastFeature(posFeatureName));
			}
			out.print(' ');
		}
		out.println();
	}
	
	private static void writeWordForm(PrintStream out, String s) {
		for (int i = 0; i < s.length(); ++i) {
			final char c = s.charAt(i);
			out.print(isSpecial(c) ? '_' : c);
		}
	}
	
	private static boolean isSpecial(char c) {
		return c == '/' || Character.isWhitespace(c);
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
				"ENJU_IN=" + inputFile.getAbsolutePath(),
				"ENJU_OUT=" + outputFile.getAbsolutePath(),
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
	
	private String currentSentenceId = null;
	private Layer currentSentence = null;
	private Annotation currentSentenceAnnotation = null;
	private Relation currentSentenceRelation = null;
	private int parseNum = 0;
	private final Collection<Map<String,String>> dependencies = new ArrayList<Map<String,String>>();
	private final Map<String,Map<String,String>> constituents = new HashMap<String,Map<String,String>>();
	private final Map<String,Integer> tokenIndex = new HashMap<String,Integer>();

	void readSentences() throws IOException, ProcessingException {
		try (BufferedReader r = new FileSourceStream(enjuParser.getEnjuEncoding(), outputFile).getBufferedReader()) {
			Iterator<Layer> sentenceIterator = sentences.iterator();
			if (!sentenceIterator.hasNext())
				return;
			while (true) {
				String line = r.readLine();
				if (line == null)
					break;
				line = line.trim();
				if (line.isEmpty())
					continue;
				Map<String,String> attrs = parseLine(line);
				String id = attrs.get("id");
				switch (attrs.get("kind")) {
				case "tok":
					addDependency(id, attrs);
					break;
				case "sentence":
					if (currentSentenceId != null)
						endParse();
					if (!id.equals(currentSentenceId)) {
						startSentence(id, sentenceIterator);
						currentSentenceAnnotation.addFeature(enjuParser.getParseStatusFeature(), attrs.get("parse_status"));
					}
					startParse();
					break;
				case "cons":
					addConstituent(id, attrs);
					break;
				default:
					ModuleBase.processingException("unsupported enju line, unknown '" + attrs.get("kind") + "': " + line);
				}
			}
			endParse();
			if (sentenceIterator.hasNext())
				ModuleBase.processingException("enju is short on sentences, last sentence id: " + currentSentenceId);
		}
	}
	
	private void addDependency(String id, Map<String,String> dependency) {
		dependencies.add(dependency);
		tokenIndex.put(id, tokenIndex.size());
	}
	
	private void addConstituent(String id, Map<String,String> constiuent) {
		constituents.put(id, constiuent);
	}
	
	private void startSentence(String id, Iterator<Layer> sentenceIterator) {
		currentSentenceId = id;
		currentSentence = sentenceIterator.next();
		currentSentenceAnnotation = currentSentence.getSentenceAnnotation();
		currentSentenceRelation = currentSentence.getSection().ensureRelation(enjuParser, dependenciesRelationName);
		parseNum = 0;
	}
	
	private void startParse() {
		dependencies.clear();
		constituents.clear();
		tokenIndex.clear();
	}
	
	private Map<String,String> getConstituentById(String caller, String id) throws ProcessingException {
		if (!constituents.containsKey(id))
			ModuleBase.processingException("unknown constituent " + id + " in " + caller);
		return constituents.get(id);
	}
	
	private String getTokenArgument(String caller, String id) throws ProcessingException {
		if (id.charAt(0) == 't')
			return id;
		Map<String,String> constituent = getConstituentById(caller, id);
		if (constituent.containsKey("tok_head"))
			return constituent.get("tok_head");
		if (!constituent.containsKey("sem_head"))
			ModuleBase.processingException("constiuent " + constituent.get("id") + " does not have a sem_head");
		String result = getTokenArgument(id, constituent.get("sem_head"));
		constituent.put("tok_head", result);
		return result;
	}
	
	private Annotation getTokenAnnotation(String caller, String id) throws ProcessingException {
		if (!tokenIndex.containsKey(id))
			ModuleBase.processingException("unknown token " + id + " in " + caller);
		return currentSentence.get(tokenIndex.get(id));
	}
	
	private Tuple createDependencyTuple(String headId, String label) throws ProcessingException {
		Tuple result = new Tuple(enjuParser, currentSentenceRelation);
		result.addFeature(parseNumberFeatureName, Integer.toString(parseNum));
		result.setArgument(sentenceRole, currentSentenceAnnotation);
		result.setArgument(dependencyHeadRole, getTokenAnnotation(headId, headId));
		result.addFeature(dependencyLabelFeatureName, label);
		return result;
	}
	
	private void processDependencyArgument(Tuple t, Map<String,String> dependency, String headId, int argN) throws ProcessingException {
		String role = "arg" + argN;
		if (!dependency.containsKey(role)) {
			logger.warning("no argument given for " + role + " in " + headId);
			return;
		}
		String argId = dependency.get(role);
		if (argId.equals("unk")) {
			logger.warning("argument " + role + " is 'unk' in " + headId);
			return;
		}
		String tokenId = getTokenArgument(headId, argId);
		t.setArgument(role, getTokenAnnotation(headId, tokenId));
	}
	
	private void endParse() throws ProcessingException {
		for (Map<String,String> d : dependencies) {
			String headId = d.get("id");
			String pred = d.get("pred");
			int under = pred.indexOf('_');
			int nargs = pred.charAt(pred.length() - 1) - '0';
			Tuple t = createDependencyTuple(headId, pred.substring(0, under));
			for (int i = 1; i <= nargs; ++i)
				processDependencyArgument(t, d, headId, i);
		}
		parseNum++;
	}
	
	private static final Pattern ENJU_LINE_ATTRIBUTE = Pattern.compile("\\s*(?<key>[a-z0-9_]+)=\"(?<value>.*?)\"\\s*");
	
	private static Map<String,String> parseLine(String line) throws ProcessingException {
		Map<String,String> result = new HashMap<String,String>();
		List<String> tabCols = Strings.split(line, '\t', -1);
		result.put("start", tabCols.get(0));
		result.put("end", tabCols.get(1));
		int spc = tabCols.get(2).indexOf(' ');
		if (spc == -1) {
			ModuleBase.processingException("unsupported enju line, missing ' ': " + line);
		}
		String kind = tabCols.get(2).substring(0, spc);
		result.put("kind", kind);
		String props = tabCols.get(2).substring(spc);
		Matcher m = ENJU_LINE_ATTRIBUTE.matcher(props);
		while (m.find()) {
			result.put(m.group("key"), m.group("value"));
		}
		if (m.requireEnd()) {
			ModuleBase.processingException("unsupported enju line, junk at the end ' ': " + line);
		}
		return result;
	}
}