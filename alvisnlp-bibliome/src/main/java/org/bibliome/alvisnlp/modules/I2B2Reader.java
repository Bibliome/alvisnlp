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


package org.bibliome.alvisnlp.modules;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bibliome.util.Strings;
import org.bibliome.util.files.InputDirectory;
import org.bibliome.util.files.InputFile;
import org.bibliome.util.streams.FileSourceStream;
import org.bibliome.util.streams.PatternFileFilter;
import org.bibliome.util.streams.SourceStream;

import alvisnlp.corpus.Annotation;
import alvisnlp.corpus.Corpus;
import alvisnlp.corpus.Document;
import alvisnlp.corpus.Layer;
import alvisnlp.corpus.NameType;
import alvisnlp.corpus.Relation;
import alvisnlp.corpus.Section;
import alvisnlp.corpus.Tuple;
import alvisnlp.corpus.creators.AnnotationCreator;
import alvisnlp.corpus.creators.DocumentCreator;
import alvisnlp.corpus.creators.SectionCreator;
import alvisnlp.corpus.creators.TupleCreator;
import alvisnlp.corpus.expressions.ResolverException;
import alvisnlp.module.ModuleException;
import alvisnlp.module.ProcessingContext;
import alvisnlp.module.ProcessingException;
import alvisnlp.module.lib.AlvisNLPModule;
import alvisnlp.module.lib.Param;

@AlvisNLPModule(beta=true)
public abstract class I2B2Reader extends CorpusModule<ResolvedObjects> implements DocumentCreator, SectionCreator, AnnotationCreator, TupleCreator {
	private static class Patterns {
		private static final Pattern NON_SPACE = Pattern.compile("\\S+");
		private static final Pattern CONCEPT = Pattern.compile("c=\".+\" (?<startlineno>\\d+):(?<starttokenno>\\d+) (?<endlineno>\\d+):(?<endtokenno>\\d+)\\|\\|t=\"(?<type>.+)\"");
		private static final Pattern ASSERTION = Pattern.compile("c=\".+\" (?<startlineno>\\d+):(?<starttokenno>\\d+) (?<endlineno>\\d+):(?<endtokenno>\\d+)\\|\\|t=\"(?<type>.+)\"\\|\\|a=\"(?<ast>.+)\"");
		private static final Pattern RELATION = Pattern.compile("c=\".+\" (?<leftstartlineno>\\d+):(?<leftstarttokenno>\\d+) (?<leftendlineno>\\d+):(?<leftendtokenno>\\d+)\\|\\|r=\"(?<rel>.+)\"\\|\\|c=\".+\" (?<rightstartlineno>\\d+):(?<rightstarttokenno>\\d+) (?<rightendlineno>\\d+):(?<rightendtokenno>\\d+)");
	}

	private InputDirectory textDir;
	private InputDirectory conceptsDir;
	private InputDirectory assertionsDir;
	private InputDirectory relationsDir;
	private String sectionName = "text";
	private String linesLayerName = "lines";
	private String linenoFeature = "lineno";
	private String tokensLayerName = "tokens";
	private String tokenNumberFeature = "tokenno";
	private String conceptsLayerName = "concepts";
	private String conceptTypeFeature = "type";
	private String assertionFeature = "assertion";
	private String leftRole = "left";
	private String rightRole = "right";
	
	@Override
	public void process(ProcessingContext<Corpus> ctx, Corpus corpus) throws ModuleException {
		try {
			FileFilter filter = new PatternFileFilter(Pattern.compile("\\.txt$"), false, false);
			File[] txtFiles = textDir.listFiles(filter);
			for (File f : txtFiles) {
				Map<TokenRef,Annotation> tokens = readText(corpus, f);
				if (conceptsDir != null) {
					readConcepts(corpus, f, tokens);
					if (assertionsDir != null) {
						readAssertions(corpus, f, tokens);
					}
					if (relationsDir != null) {
						readRelations(corpus, f, tokens);
					}
				}
			}
		}
		catch (IOException e) {
			rethrow(e);
		}
	}
	
	@Override
	protected ResolvedObjects createResolvedObjects(ProcessingContext<Corpus> ctx) throws ResolverException {
		return new ResolvedObjects(ctx, this);
	}
	
	private static class TokenRef implements Comparable<TokenRef> {
		private final int lineno;
		private final int tokenno;
		
		private TokenRef(int lineno, int tokenno) {
			super();
			this.lineno = lineno;
			this.tokenno = tokenno;
		}

		@Override
		public int compareTo(TokenRef o) {
			if (lineno == o.lineno) {
				return tokenno - o.tokenno;
			}
			return lineno - o.lineno;
		}

		@Override
		public String toString() {
			return "TokenRef " + lineno + ":" + tokenno;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + lineno;
			result = prime * result + tokenno;
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (!(obj instanceof TokenRef))
				return false;
			TokenRef other = (TokenRef) obj;
			if (lineno != other.lineno)
				return false;
			if (tokenno != other.tokenno)
				return false;
			return true;
		}
	}
	
	private void readRelations(Corpus corpus, File f, Map<TokenRef,Annotation> tokens) throws IOException, ProcessingException {
		String docId = f.getName();
		Document doc = corpus.getDocument(docId);
		Section sec = doc.sectionIterator(sectionName).next();
		Layer conceptLayer = sec.ensureLayer(conceptsLayerName);
		String source = docId.replace(".txt", ".rel");
		InputFile file = new InputFile(relationsDir, source);
		SourceStream sourceStream = new FileSourceStream("UTF-8", file);
		try (BufferedReader br = sourceStream.getBufferedReader()) {
			int lineno = 0;
			while (true) {
				String line = br.readLine();
				if (line == null) {
					break;
				}
				lineno++;
				Matcher m = Patterns.RELATION.matcher(line);
				if (!m.matches()) {
					throw new ProcessingException("malformed assertion line " + file.toString() + ": " + lineno);
				}
				Annotation left = getConcept(tokens, m, "left", source, conceptLayer);
				Annotation right = getConcept(tokens, m, "right", source, conceptLayer);
				String relationName = m.group("rel");
				Relation rel = sec.ensureRelation(this, relationName);
				Tuple t = new Tuple(this, rel);
				t.setArgument(leftRole, left);
				t.setArgument(rightRole, right);
			}
		}
	}
	
	private void readAssertions(Corpus corpus, File f, Map<TokenRef,Annotation> tokens) throws IOException, ProcessingException {
		String docId = f.getName();
		Document doc = corpus.getDocument(docId);
		Section sec = doc.sectionIterator(sectionName).next();
		Layer conceptLayer = sec.ensureLayer(conceptsLayerName);
		String source = docId.replace(".txt", ".ast");
		InputFile file = new InputFile(assertionsDir, source);
		SourceStream sourceStream = new FileSourceStream("UTF-8", file);
		try (BufferedReader br = sourceStream.getBufferedReader()) {
			int lineno = 0;
			while (true) {
				String line = br.readLine();
				if (line == null) {
					break;
				}
				lineno++;
				Matcher m = Patterns.ASSERTION.matcher(line);
				if (!m.matches()) {
					throw new ProcessingException("malformed assertion line " + file.toString() + ": " + lineno);
				}
				Annotation concept = getConcept(tokens, m, "", source, conceptLayer);
				String type = m.group("type");
				if (!type.equals(concept.getLastFeature(conceptTypeFeature))) {
					throw new ProcessingException("concept type mismatch in " + source + ":" + lineno);
				}
				String ast = m.group("ast");
				concept.addFeature(assertionFeature, ast);
			}
		}
	}
	
	private static Annotation getToken(Map<TokenRef,Annotation> tokens, Matcher m, String groupPrefix, String source) throws ProcessingException {
		int lineno = Integer.parseInt(m.group(groupPrefix + "lineno"));
		int tokenno = Integer.parseInt(m.group(groupPrefix + "tokenno"));
		TokenRef tr = new TokenRef(lineno, tokenno);
		if (!tokens.containsKey(tr)) {
			System.err.println("tokens = " + tokens);
			throw new ProcessingException("cannot find token " + lineno + ":" + tokenno + " in " + source);
		}
		return tokens.get(tr);
	}
	
	private String tokenToString(Annotation t) {
		return t.getLastFeature(linenoFeature) + ":" + t.getLastFeature(tokenNumberFeature);
	}
	
	private Annotation getConcept(Map<TokenRef,Annotation> tokens, Matcher m, String groupPrefix, String source, Layer conceptLayer) throws ProcessingException {
		Annotation startToken = getToken(tokens, m, groupPrefix + "start", source);
		Annotation endToken = getToken(tokens, m, groupPrefix + "end", source);
		int start = startToken.getStart();
		int end = endToken.getEnd();
		Layer l = conceptLayer.span(start, end);
		if (l.isEmpty()) {
			throw new ProcessingException("cannot find concept " + tokenToString(startToken) + " " + tokenToString(endToken) + " in " + source);
		}
		if (l.size() > 1) {
			throw new ProcessingException("ambiguous concept " + tokenToString(startToken) + " " + tokenToString(endToken) + " in " + source);
		}
		return l.get(0);
	}

	private void readConcepts(Corpus corpus, File f, Map<TokenRef,Annotation> tokens) throws ProcessingException, IOException {
		String docId = f.getName();
		Document doc = corpus.getDocument(docId);
		Section sec = doc.sectionIterator(sectionName).next();
		Layer conceptLayer = sec.ensureLayer(conceptsLayerName);
		String source = docId.replace(".txt", ".con");
		InputFile file = new InputFile(conceptsDir, source);
		SourceStream sourceStream = new FileSourceStream("UTF-8", file);
		try (BufferedReader br = sourceStream.getBufferedReader()) {
			int lineno = 0;
			while (true) {
				String line = br.readLine();
				if (line == null) {
					break;
				}
				lineno++;
				Matcher m = Patterns.CONCEPT.matcher(line);
				if (!m.matches()) {
					throw new ProcessingException("malformed concept line " + file.toString() + ": " + lineno);
				}
				Annotation startToken = getToken(tokens, m, "start", source);
				Annotation endToken = getToken(tokens, m, "end", source);
				String type = m.group("type");
				Annotation a = new Annotation(this, conceptLayer, startToken.getStart(), endToken.getEnd());
				a.addFeature(conceptTypeFeature, type);
			}
		}
	}

	private Map<TokenRef,Annotation> readText(Corpus corpus, File f) throws IOException {
		String docId = f.getName();
		Document doc = Document.getDocument(this, corpus, docId);
		List<String> lines = readTextLines(f);
		Section sec = createSection(doc, lines);
		return createBasicAnnotations(sec, lines);
	}
	
	private Map<TokenRef,Annotation> createBasicAnnotations(Section sec, List<String> lines) {
		Map<TokenRef,Annotation> result = new TreeMap<TokenRef,Annotation>();
		Layer linesLayer = sec.ensureLayer(linesLayerName);
		Layer tokensLayer = sec.ensureLayer(tokensLayerName);
		int start = 0;
		for (int lineno = 1; lineno <= lines.size(); ++lineno) {
			String line = lines.get(lineno-1);
			String linenoString = Integer.toString(lineno);
			
			int end = start + line.length() + (lineno == lines.size() ? 0 : 1);
			Annotation la = new Annotation(this, linesLayer, start, end);
			la.addFeature(linenoFeature, linenoString);
			
			int tokenno = 0;
			Matcher m = Patterns.NON_SPACE.matcher(line);
			while (m.find()) {
				Annotation ta = new Annotation(this, tokensLayer, start + m.start(), start + m.end());
				ta.addFeature(linenoFeature, linenoString);
				ta.addFeature(tokenNumberFeature, Integer.toString(tokenno));
				TokenRef tr = new TokenRef(lineno, tokenno);
				result.put(tr, ta);
				tokenno++;
			}
			start = end;
		}
		return result;
	}
	
	private Section createSection(Document doc, List<String> lines) {
		String contents = Strings.join(lines, '\n');
		return new Section(this, doc, sectionName , contents);
	}
	
	private static List<String> readTextLines(File f) throws IOException {
		List<String> result = new ArrayList<String>();
		SourceStream source = new FileSourceStream("UTF-8", f.getPath());
		try (BufferedReader br = source.getBufferedReader()) {
			while (true) {
				String line = br.readLine();
				if (line == null) {
					break;
				}
				result.add(line);
			}
			return result;
		}
	}

	@Param
	public InputDirectory getTextDir() {
		return textDir;
	}

	@Param(mandatory=false)
	public InputDirectory getConceptsDir() {
		return conceptsDir;
	}

	@Param(mandatory=false)
	public InputDirectory getAssertionsDir() {
		return assertionsDir;
	}

	@Param(mandatory=false)
	public InputDirectory getRelationsDir() {
		return relationsDir;
	}

	@Param(nameType=NameType.SECTION)
	public String getSectionName() {
		return sectionName;
	}

	@Param(nameType=NameType.LAYER)
	public String getLinesLayerName() {
		return linesLayerName;
	}

	@Param(nameType=NameType.FEATURE)
	public String getLinenoFeature() {
		return linenoFeature;
	}

	@Param(nameType=NameType.LAYER)
	public String getTokensLayerName() {
		return tokensLayerName;
	}

	@Param(nameType=NameType.FEATURE)
	public String getTokenNumberFeature() {
		return tokenNumberFeature;
	}

	@Param(nameType=NameType.LAYER)
	public String getConceptsLayerName() {
		return conceptsLayerName;
	}

	@Param(nameType=NameType.FEATURE)
	public String getConceptTypeFeature() {
		return conceptTypeFeature;
	}

	@Param(nameType=NameType.FEATURE)
	public String getAssertionFeature() {
		return assertionFeature;
	}

	@Param(nameType=NameType.ARGUMENT)
	public String getLeftRole() {
		return leftRole;
	}

	@Param(nameType=NameType.ARGUMENT)
	public String getRightRole() {
		return rightRole;
	}

	public void setTextDir(InputDirectory textDir) {
		this.textDir = textDir;
	}

	public void setConceptsDir(InputDirectory conceptsDir) {
		this.conceptsDir = conceptsDir;
	}

	public void setAssertionsDir(InputDirectory assertionsDir) {
		this.assertionsDir = assertionsDir;
	}

	public void setRelationsDir(InputDirectory relationsDir) {
		this.relationsDir = relationsDir;
	}

	public void setSectionName(String sectionName) {
		this.sectionName = sectionName;
	}

	public void setLinesLayerName(String linesLayerName) {
		this.linesLayerName = linesLayerName;
	}

	public void setLinenoFeature(String linenoFeature) {
		this.linenoFeature = linenoFeature;
	}

	public void setTokensLayerName(String tokensLayerName) {
		this.tokensLayerName = tokensLayerName;
	}

	public void setTokenNumberFeature(String tokenNumberFeature) {
		this.tokenNumberFeature = tokenNumberFeature;
	}

	public void setConceptsLayerName(String conceptsLayerName) {
		this.conceptsLayerName = conceptsLayerName;
	}

	public void setConceptTypeFeature(String conceptTypeFeature) {
		this.conceptTypeFeature = conceptTypeFeature;
	}

	public void setAssertionFeature(String assertionFeature) {
		this.assertionFeature = assertionFeature;
	}

	public void setLeftRole(String leftRole) {
		this.leftRole = leftRole;
	}

	public void setRightRole(String rightRole) {
		this.rightRole = rightRole;
	}
}
