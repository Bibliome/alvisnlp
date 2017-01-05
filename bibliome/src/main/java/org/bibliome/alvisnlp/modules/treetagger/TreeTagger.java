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


package org.bibliome.alvisnlp.modules.treetagger;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bibliome.alvisnlp.modules.SectionModule;
import org.bibliome.alvisnlp.modules.SectionModule.SectionResolvedObjects;
import org.bibliome.util.Iterators;
import org.bibliome.util.Strings;
import org.bibliome.util.filelines.FileLines;
import org.bibliome.util.filelines.InvalidFileLineEntry;
import org.bibliome.util.filelines.TabularFormat;
import org.bibliome.util.files.ExecutableFile;
import org.bibliome.util.files.InputFile;
import org.bibliome.util.files.OutputDirectory;
import org.bibliome.util.files.OutputFile;
import org.bibliome.util.mappers.Mapper;
import org.bibliome.util.mappers.Mappers;
import org.bibliome.util.streams.FileTargetStream;
import org.bibliome.util.streams.SourceStream;
import org.bibliome.util.streams.TargetStream;

import alvisnlp.corpus.Annotation;
import alvisnlp.corpus.Corpus;
import alvisnlp.corpus.DefaultNames;
import alvisnlp.corpus.Layer;
import alvisnlp.corpus.NameType;
import alvisnlp.corpus.Section;
import alvisnlp.corpus.creators.AnnotationCreator;
import alvisnlp.corpus.expressions.EvaluationContext;
import alvisnlp.corpus.expressions.ResolverException;
import alvisnlp.module.ModuleException;
import alvisnlp.module.ProcessingContext;
import alvisnlp.module.ProcessingException;
import alvisnlp.module.TimerCategory;
import alvisnlp.module.lib.AlvisNLPModule;
import alvisnlp.module.lib.Param;
import alvisnlp.module.lib.TimeThis;

// TODO: Auto-generated Javadoc
/**
 * The Class NewTreeTagger.
 * 
 * @author rbossy
 */
@AlvisNLPModule
public abstract class TreeTagger extends SectionModule<SectionResolvedObjects> implements AnnotationCreator {

    /** Path to tree tagger binary. */
    private ExecutableFile                                     treeTaggerExecutable   = null;

    /** Path to the tree tagger PAR file. */
    private InputFile                                     parFile                = null;

    /** Sentence annotations. */
    private String sentenceLayerName      = DefaultNames.getSentenceLayer();

    /** Word annotations. */
    private String wordLayerName          = DefaultNames.getWordLayer();

    /** Word annotation form feature. */
    private String                                   formFeature            = Annotation.FORM_FEATURE_NAME;

    /** Word annotation POS tag feature. */
    private String                                   posFeature             = DefaultNames.getPosTagFeature();

    /** Word annotation lemma feature. */
    private String                                   lemmaFeature           = DefaultNames.getCanonicalFormFeature();

    /** Either to override &lt;unknown&gt; lemmas. */
    private Boolean                                  noUnknownLemma         = false;

    /** Directory where to write tree tagger output. */
    private OutputDirectory                                     recordDir              = null;

    /** Annotation features to include in the output. */
    private String[]                                 recordFeatures         = null;

    /** The record charset. */
    private String                                   recordCharset          = "UTF-8";

    /** Path to a lexicon file. */
    private SourceStream                                     lexiconFile            = null;

    /** Tree-tagger input corpus character set. */
    private String                                   inputCharset           = "ISO-8859-1";

    /** Tree-tagger output character set. */
    private String                                   outputCharset          = "ISO-8859-1";

    private static final TabularFormat lexiconTabularFormat = new TabularFormat();
    static {
        lexiconTabularFormat.setMinColumns(2);
        lexiconTabularFormat.setStrictColumnNumber(true);
        lexiconTabularFormat.setSkipBlank(true);
        lexiconTabularFormat.setSkipEmpty(true);
        lexiconTabularFormat.setTrimColumns(true);
    }
    
    /**
     * Instantiates a new new tree tagger.
     */
    public TreeTagger() {
        super();
    }

    /*
     * (non-Javadoc)
     * 
     * @see alvisnlp.module.lib.SectionModule#addFeaturesToSectionFilter()
     */
    @Override
    public String[] addFeaturesToSectionFilter() {
        return new String[] {};
    }

    /*
     * (non-Javadoc)
     * 
     * @see alvisnlp.module.lib.SectionModule#addLayersToSectionFilter()
     */
    @Override
    public String[] addLayersToSectionFilter() {
        return null;
    }
	
	@Override
	protected SectionResolvedObjects createResolvedObjects(ProcessingContext<Corpus> ctx) throws ResolverException {
		return new SectionResolvedObjects(ctx, this);
	}

    /*
     * (non-Javadoc)
     * 
     * @see
     * alvisnlp.module.lib.ModuleBase#process(alvisnlp.module.ProcessingContext,
     * alvisnlp.document.Corpus)
     */
    @Override
    public void process(ProcessingContext<Corpus> ctx, Corpus corpus) throws ModuleException {
        try {
        	Logger logger = getLogger(ctx);
            EvaluationContext evalCtx = new EvaluationContext(logger);

            Map<String,String> lexicon = getLexicon(ctx);
            File tempDir = getTempDir(ctx);
            TreeTaggerExternal<Corpus> tte = new TreeTaggerExternal<Corpus>(ctx, this, tempDir, treeTaggerExecutable, parFile, lexicon);

            writeTTInput(ctx, evalCtx, corpus, tte);

            logger.info("running tree-tagger");
            callExternal(ctx, "tree-tagger", tte, outputCharset, "script.sh");

            readTTOutput(ctx, evalCtx, corpus, tte);
            if ((recordDir != null) && (recordFeatures != null)) {
                for (Section sec : Iterators.loop(sectionIterator(evalCtx, corpus))) {
                	OutputFile recordFile = recordDir.getOutputFile(sec.getFileName() + ".ttg");
                	TargetStream target = new FileTargetStream(recordCharset, recordFile);
                    PrintStream ps = target.getPrintStream();
                    int n = 0;
                    for (Layer sent : sec.getSentences(wordLayerName, sentenceLayerName)) {
                        for (Annotation word : sent) {
							writeTTOutput(ps, word, ++n);
						}
                        ps.print(".\tSENT\t.\n");
                    }
                    ps.close();
                }
            }
        }
        catch (UnsupportedEncodingException uee) {
            rethrow(uee);
        }
        catch (FileNotFoundException fnfe) {
            rethrow(fnfe);
        }
        catch (IOException ioe) {
            rethrow(ioe);
        }
        catch (Exception e) {
            rethrow(e);
        }
    }

    private static final Pattern LEXICON_INFO = Pattern.compile("\\S+ [01](?:\\.\\d+)? \\S+");
    
    private FileLines<Map<String,String>> lexiconFileLines = new FileLines<Map<String,String>>(lexiconTabularFormat) {
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
    
    @TimeThis(task="load-lexicon", category=TimerCategory.LOAD_RESOURCE)
    public Map<String,String> getLexicon(ProcessingContext<Corpus> ctx) throws FileNotFoundException, IOException, InvalidFileLineEntry {
        if (lexiconFile == null) {
			return Collections.emptyMap();
		}
        Map<String,String> result = new HashMap<String,String>();
        Logger logger = getLogger(ctx);
        logger.fine("opening lexicon file: " + lexiconFile);
        lexiconFileLines.setLogger(logger);
        BufferedReader r = lexiconFile.getBufferedReader();
        lexiconFileLines.process(r, result);
        r.close();
        return result;
    }

    @TimeThis(task="treetagger-input", category=TimerCategory.PREPARE_DATA)
    protected void writeTTInput(@SuppressWarnings("unused") ProcessingContext<Corpus> ctx, EvaluationContext evalCtx, Corpus corpus, TreeTaggerExternal<Corpus> tte) throws IOException {
        tte.openInput("corpus", inputCharset);
        for (Section sec : Iterators.loop(sectionIterator(evalCtx, corpus))) {
//        	logger.finer("preparing TT input for " + sec.getDocument().getId() + " / " + sec.getName());
            for (Layer sent : sec.getSentences(wordLayerName, sentenceLayerName)) {
                for (Annotation word : sent) {
                    tte.addInputToken(word.getLastFeature(formFeature), word.getLastFeature(posFeature), word.getLastFeature(lemmaFeature));
                }
                tte.addInputToken(".", "SENT", ".");
            }
        }
        tte.closeInput();
    }

    @TimeThis(task="treetagger-output", category=TimerCategory.COLLECT_DATA)
    protected void readTTOutput(@SuppressWarnings("unused") ProcessingContext<Corpus> ctx, EvaluationContext evalCtx, Corpus corpus, TreeTaggerExternal<Corpus> tte) throws ProcessingException, ModuleException, IOException {
        tte.openOutput(outputCharset);
        for (Section sec : Iterators.loop(sectionIterator(evalCtx, corpus))) {
//        	logger.finer("reading TT output for " + sec.getDocument().getId() + " / " + sec.getName());
            for (Layer sent : sec.getSentences(wordLayerName, sentenceLayerName)) {
                for (Annotation word : sent) {
                    if (!tte.hasNext()) {
                        processingException("tree tagger output is short on lines");
                    }
                    word.addFeature(posFeature, tte.getPosTag());
                    String lemma = tte.getLemma();
                    if (noUnknownLemma && "<unknown>".equals(lemma)) {
						word.addFeature(lemmaFeature, word.getLastFeature(formFeature));
					} else {
						word.addFeature(lemmaFeature, lemma);
					}
                }
                if (!tte.hasNext()) { // eat sentence reinforcement
                    processingException("tree tagger output is short on lines");
                }
            }
        }
        tte.closeOutput();
    }
    
    /**
     * Write tt output.
     * 
     * @param ps
     *            the ps
     * @param word
     *            the word
     * @param n
     *            the n
     * @throws IOException 
     */
    private void writeTTOutput(PrintStream ps, final Annotation word, final int n) throws IOException {
        Mapper<String,String> mapper = new Mapper<String,String>() {
            @Override
			public String map(String a) {
                if ("n".equals(a.toString())) {
					return Integer.toString(n);
				}
                return word.getLastFeature(a).replace("\n", "");
            }
        };
        Strings.join(ps, Mappers.mappedList(mapper, Arrays.asList(recordFeatures)), '\t');
        ps.println();
    }

    /**
     * Gets the tree tagger executable.
     * 
     * @return the treeTaggerExecutable
     */
    @Param
    public ExecutableFile getTreeTaggerExecutable() {
        return treeTaggerExecutable;
    }

    /**
     * Gets the par file.
     * 
     * @return the parFile
     */
    @Param
    public InputFile getParFile() {
        return parFile;
    }

    /**
     * Gets the sentence layer name.
     * 
     * @return the sentenceLayerName
     */
    @Param(nameType=NameType.LAYER, defaultDoc = "Name of the layer containing sentence annotations, sentences are reinforced.")
    public String getSentenceLayerName() {
        return sentenceLayerName;
    }

    /**
     * Gets the word layer name.
     * 
     * @return the wordLayerName
     */
    @Param(nameType=NameType.LAYER, defaultDoc = "Name of the layer containing the word annotations.")
    public String getWordLayerName() {
        return wordLayerName;
    }

    /**
     * Gets the form feature.
     * 
     * @return the formFeature
     */
    @Param(nameType=NameType.FEATURE, defaultDoc = "Name of the feature denoting the token surface form.")
    public String getFormFeature() {
        return formFeature;
    }

    /**
     * Gets the pos feature.
     * 
     * @return the posFeature
     */
    @Param(nameType=NameType.FEATURE, defaultDoc = "Name of the feature to set with the POS tag.")
    public String getPosFeature() {
        return posFeature;
    }

    /**
     * Gets the lemma feature.
     * 
     * @return the lemmaFeature
     */
    @Param(nameType=NameType.FEATURE, defaultDoc = "Name of the feature to set with the lemma.")
    public String getLemmaFeature() {
        return lemmaFeature;
    }

    /**
     * Gets the no unknown lemma.
     * 
     * @return the noUnknownLemma
     */
    @Param(defaultDoc = "Either to replace unknown lemmas with the surface form.")
    public Boolean getNoUnknownLemma() {
        return noUnknownLemma;
    }

    /**
     * Gets the record dir.
     * 
     * @return the recordDir
     */
    @Param(mandatory = false)
    public OutputDirectory getRecordDir() {
        return recordDir;
    }

    /**
     * Gets the record features.
     * 
     * @return the recordFeatures
     */
    @Param(mandatory = false, nameType=NameType.FEATURE, defaultDoc = "List of attributes to display in result files.")
    public String[] getRecordFeatures() {
        return recordFeatures;
    }

    /**
     * Gets the lexicon file.
     * 
     * @return the lexiconFile
     */
    @Param(mandatory=false)
    public SourceStream getLexiconFile() {
        return lexiconFile;
    }

    /**
     * Gets the input charset.
     * 
     * @return the inputCharset
     */
    @Param(defaultDoc = "Tree-tagger input corpus character set.")
    public String getInputCharset() {
        return inputCharset;
    }

    /**
     * Gets the output charset.
     * 
     * @return the outputCharset
     */
    @Param(defaultDoc = "Tree-tagger output character set.")
    public String getOutputCharset() {
        return outputCharset;
    }

    @Param
    public String getRecordCharset() {
		return recordCharset;
	}

	public void setRecordCharset(String recordCharset) {
		this.recordCharset = recordCharset;
	}

	/**
     * Sets the tree tagger executable.
     * 
     * @param treeTaggerExecutable
     *            the treeTaggerExecutable to set
     */
    public void setTreeTaggerExecutable(ExecutableFile treeTaggerExecutable) {
        this.treeTaggerExecutable = treeTaggerExecutable;
    }

    /**
     * Sets the par file.
     * 
     * @param parFile
     *            the parFile to set
     */
    public void setParFile(InputFile parFile) {
        this.parFile = parFile;
    }

    /**
     * Sets the sentence layer name.
     * 
     * @param sentenceLayerName
     *            the sentenceLayerName to set
     */
    public void setSentenceLayerName(String sentenceLayerName) {
        this.sentenceLayerName = sentenceLayerName;
    }

    /**
     * Sets the word layer name.
     * 
     * @param wordLayerName
     *            the wordLayerName to set
     */
    public void setWordLayerName(String wordLayerName) {
        this.wordLayerName = wordLayerName;
    }

    /**
     * Sets the form feature.
     * 
     * @param formFeature
     *            the formFeature to set
     */
    public void setFormFeature(String formFeature) {
        this.formFeature = formFeature;
    }

    /**
     * Sets the pos feature.
     * 
     * @param posFeature
     *            the posFeature to set
     */
    public void setPosFeature(String posFeature) {
        this.posFeature = posFeature;
    }

    /**
     * Sets the lemma feature.
     * 
     * @param lemmaFeature
     *            the lemmaFeature to set
     */
    public void setLemmaFeature(String lemmaFeature) {
        this.lemmaFeature = lemmaFeature;
    }

    /**
     * Sets the no unknown lemma.
     * 
     * @param noUnknownLemma
     *            the noUnknownLemma to set
     */
    public void setNoUnknownLemma(Boolean noUnknownLemma) {
        this.noUnknownLemma = noUnknownLemma;
    }

    /**
     * Sets the record dir.
     * 
     * @param recordDir
     *            the recordDir to set
     */
    public void setRecordDir(OutputDirectory recordDir) {
        this.recordDir = recordDir;
    }

    /**
     * Sets the record features.
     * 
     * @param recordFeatures
     *            the recordFeatures to set
     */
    public void setRecordFeatures(String[] recordFeatures) {
        this.recordFeatures = recordFeatures;
    }

    /**
     * Sets the lexicon file.
     * 
     * @param lexiconFile
     *            the lexiconFile to set
     */
    public void setLexiconFile(SourceStream lexiconFile) {
        this.lexiconFile = lexiconFile;
    }

    /**
     * Sets the input charset.
     * 
     * @param inputCharset
     *            the inputCharset to set
     */
    public void setInputCharset(String inputCharset) {
        this.inputCharset = inputCharset;
    }

    /**
     * Sets the output charset.
     * 
     * @param outputCharset
     *            the outputCharset to set
     */
    public void setOutputCharset(String outputCharset) {
        this.outputCharset = outputCharset;
    }
}
