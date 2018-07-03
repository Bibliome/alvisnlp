package fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.treetagger;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Annotation;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Corpus;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Layer;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Section;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.EvaluationContext;
import fr.inra.maiage.bibliome.alvisnlp.core.module.ModuleException;
import fr.inra.maiage.bibliome.alvisnlp.core.module.ProcessingContext;
import fr.inra.maiage.bibliome.alvisnlp.core.module.ProcessingException;
import fr.inra.maiage.bibliome.alvisnlp.core.module.lib.ExternalHandler;
import fr.inra.maiage.bibliome.util.Iterators;
import fr.inra.maiage.bibliome.util.Pair;
import fr.inra.maiage.bibliome.util.Strings;
import fr.inra.maiage.bibliome.util.filelines.FileLines;
import fr.inra.maiage.bibliome.util.filelines.InvalidFileLineEntry;
import fr.inra.maiage.bibliome.util.filelines.TabularFormat;
import fr.inra.maiage.bibliome.util.streams.SourceStream;

class TreeTaggerExternalHandler extends ExternalHandler<Corpus,TreeTagger> {
	private final Map<String,String> lexicon = new LinkedHashMap<String,String>();
	
	TreeTaggerExternalHandler(ProcessingContext<Corpus> processingContext, TreeTagger module, Corpus annotable) {
		super(processingContext, module, annotable);
	}

	@Override
	protected void prepare() throws IOException, ModuleException {
        loadLexicon();
        writeTTInput();
	}

	public void loadLexicon() throws FileNotFoundException, IOException, InvalidFileLineEntry {
		SourceStream lexiconFile = getModule().getLexiconFile();
        if (lexiconFile != null) {
        	Logger logger = getLogger();
        	LexiconFileLines lexiconFileLines = new LexiconFileLines(logger);
        	logger.fine("opening lexicon file: " + lexiconFile);
        	lexiconFileLines.process(lexiconFile, lexicon);
        }
    }
	
	private static class LexiconFileLines extends FileLines<Map<String,String>> {
	    private static final Pattern LEXICON_INFO = Pattern.compile("\\S+ [01](?:\\.\\d+)? \\S+");

	    private LexiconFileLines(Logger logger) {
			super(logger);
	        TabularFormat lexiconTabularFormat = getFormat();
	        lexiconTabularFormat.setMinColumns(2);
	        lexiconTabularFormat.setStrictColumnNumber(true);
	        lexiconTabularFormat.setSkipBlank(true);
	        lexiconTabularFormat.setSkipEmpty(true);
	        lexiconTabularFormat.setTrimColumns(true);
		}

		@Override
        public void processEntry(Map<String,String> data, int lineno, List<String> entry) throws InvalidFileLineEntry {
            String word = entry.get(0);
            if (data.containsKey(word)) {
				getLogger().warning("duplicate lexicon entry " + word + ", ignoring previous settings");
			}
            List<String> info = new ArrayList<String>(entry.size() - 1);
            for (int i = 1; i < entry.size(); ++i) {
            	String alt = entry.get(i);
            	if (alt.isEmpty()) {
            		getLogger().warning("malformed lexicon entry at line " + lineno + ": tab-tab");
            		continue;
            	}
            	Matcher m = LEXICON_INFO.matcher(alt);
            	if (!m.matches()) {
            		getLogger().warning("malformed lexicon entry at line " + lineno + ": '" + alt + "'");
            		continue;
            	}
            	info.add(alt);
            }
            data.put(word, Strings.join(info, '\t'));
        }
    };

    private void writeTTInput() throws IOException {
    	TreeTagger owner = getModule();
    	try (PrintStream out = new PrintStream(getTreeTaggerInputFile(), owner.getInputCharset())) {
    		EvaluationContext evalCtx = new EvaluationContext(getLogger());
    		for (Section sec : Iterators.loop(owner.sectionIterator(evalCtx, getAnnotable()))) {
    			for (Layer sent : sec.getSentences(owner.getWordLayerName(), owner.getSentenceLayerName())) {
    				for (Annotation word : sent) {
    					addInputToken(out, word.getLastFeature(owner.getFormFeature()), word.getLastFeature(owner.getPosFeature()), word.getLastFeature(owner.getLemmaFeature()));
    				}
    				addInputToken(out, ".", "SENT", ".");
    			}
    		}
    	}
    }

    private void addInputToken(PrintStream out, String token, String posTag, String lemma) {
        token = Strings.normalizeSpace(token);
        if (token.trim().isEmpty())
        	token = ".";
        if (posTag == null) {
            if (lexicon.containsKey(token)) {
				out.printf("%s\t%s\n", token, lexicon.get(token));
			} else {
				out.println(token);
			}
        } else {
			out.printf("%s\t%s 1 %s\n", token, Strings.normalizeSpace(posTag), lemma == null ? token : Strings.normalizeSpace(lemma));
		}
    }
    
    private File getTreeTaggerInputFile() {
    	return getTempFile("corpus.txt");
    }
    
    private File getTreeTaggerOutputFile() {
    	return getTempFile("corpus.ttg");
    }
    
	@Override
	protected void collect() throws IOException, ModuleException {
		TreeTagger owner = getModule();
		try (BufferedReader r = new BufferedReader(new InputStreamReader(new FileInputStream(getTreeTaggerOutputFile()), owner.getOutputCharset()))) {
			EvaluationContext evalCtx = new EvaluationContext(getLogger());
			for (Section sec : Iterators.loop(owner.sectionIterator(evalCtx, getAnnotable()))) {
				for (Layer sent : sec.getSentences(owner.getWordLayerName(), owner.getSentenceLayerName())) {
					for (Annotation word : sent) {
						Pair<String,String> tok = scanNextLine(r);
						if (tok == null) {
							throw new ProcessingException("tree tagger output is short on lines");
						}
						word.addFeature(owner.getPosFeature(), tok.first);
						String lemma = tok.second;
						if (owner.getNoUnknownLemma() && "<unknown>".equals(lemma)) {
							word.addFeature(owner.getLemmaFeature(), word.getLastFeature(owner.getFormFeature()));
						} else {
							word.addFeature(owner.getLemmaFeature(), lemma);
						}
					}
					// eat sentence reinforcement
					Pair<String,String> stok = scanNextLine(r);
					if (stok == null) { 
						throw new ProcessingException("tree tagger output is short on lines");
					}
				}
			}
		}
	}

	private static Pair<String,String> scanNextLine(BufferedReader r) throws IOException {
        String line = r.readLine();
        if (line == null) {
        	return null;
        }
        int col = line.indexOf('\t');
        if (col < 0) {
        	return null;
        }
        String posTag = line.substring(0, col).intern();
        String lemma = line.substring(col + 1);
        return new Pair<String,String>(posTag, lemma);
    }

	@Override
	protected String getPrepareTask() {
		return "alvisnlp-to-treetagger";
	}

	@Override
	protected String getExecTask() {
		return "treetagger";
	}

	@Override
	protected String getCollectTask() {
		return "treetagger-to-alvisnlp";
	}

	@Override
	protected List<String> getCommandLine() {
		TreeTagger owner = getModule();
		return Arrays.asList(
				owner.getTreeTaggerExecutable().getAbsolutePath(),
				"-quiet",
				"-lemma",
				"-pt-with-lemma",
				"-pt-with-prob",
				owner.getParFile().getAbsolutePath(),
				getTreeTaggerInputFile().getAbsolutePath(),
				getTreeTaggerOutputFile().getAbsolutePath()
				);
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
		return null;
	}

	@Override
	protected String getOutputFilename() {
		return null;
	}
}
