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


package fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.treetagger;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;

import fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.SectionModule;
import fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.SectionModule.SectionResolvedObjects;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Annotation;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Corpus;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.DefaultNames;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Layer;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.NameType;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Section;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.creators.AnnotationCreator;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.EvaluationContext;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.ResolverException;
import fr.inra.maiage.bibliome.alvisnlp.core.module.ModuleException;
import fr.inra.maiage.bibliome.alvisnlp.core.module.ProcessingContext;
import fr.inra.maiage.bibliome.alvisnlp.core.module.lib.AlvisNLPModule;
import fr.inra.maiage.bibliome.alvisnlp.core.module.lib.Param;
import fr.inra.maiage.bibliome.util.Iterators;
import fr.inra.maiage.bibliome.util.Strings;
import fr.inra.maiage.bibliome.util.filelines.TabularFormat;
import fr.inra.maiage.bibliome.util.files.ExecutableFile;
import fr.inra.maiage.bibliome.util.files.InputFile;
import fr.inra.maiage.bibliome.util.files.OutputDirectory;
import fr.inra.maiage.bibliome.util.files.OutputFile;
import fr.inra.maiage.bibliome.util.mappers.Mapper;
import fr.inra.maiage.bibliome.util.mappers.Mappers;
import fr.inra.maiage.bibliome.util.streams.FileTargetStream;
import fr.inra.maiage.bibliome.util.streams.SourceStream;
import fr.inra.maiage.bibliome.util.streams.TargetStream;

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
     * @see fr.inra.maiage.bibliome.alvisnlp.core.module.lib.SectionModule#addFeaturesToSectionFilter()
     */
    @Override
    public String[] addFeaturesToSectionFilter() {
        return new String[] {};
    }

    /*
     * (non-Javadoc)
     * 
     * @see fr.inra.maiage.bibliome.alvisnlp.core.module.lib.SectionModule#addLayersToSectionFilter()
     */
    @Override
    public String[] addLayersToSectionFilter() {
        return null;
    }
	
	@Override
	protected SectionResolvedObjects createResolvedObjects(ProcessingContext<Corpus> ctx) throws ResolverException {
		return new SectionResolvedObjects(ctx, this);
	}

    @Override
    public void process(ProcessingContext<Corpus> ctx, Corpus corpus) throws ModuleException {
        try {
            new TreeTaggerExternalHandler(ctx, this, corpus).start();

            if ((recordDir != null) && (recordFeatures != null)) {
            	EvaluationContext evalCtx = new EvaluationContext(getLogger(ctx));
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
