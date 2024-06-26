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



package fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.yatea;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.Properties;
import java.util.logging.Logger;

import fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.SectionModule;
import fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.SectionModule.SectionResolvedObjects;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Annotation;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.DefaultNames;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.NameType;
import fr.inra.maiage.bibliome.alvisnlp.core.module.Module;
import fr.inra.maiage.bibliome.alvisnlp.core.module.ProcessingContext;
import fr.inra.maiage.bibliome.alvisnlp.core.module.lib.Param;
import fr.inra.maiage.bibliome.alvisnlp.core.module.types.Mapping;
import fr.inra.maiage.bibliome.util.Checkable;
import fr.inra.maiage.bibliome.util.files.ExecutableFile;
import fr.inra.maiage.bibliome.util.files.InputDirectory;
import fr.inra.maiage.bibliome.util.files.InputFile;
import fr.inra.maiage.bibliome.util.files.OutputDirectory;
import fr.inra.maiage.bibliome.util.files.OutputFile;
import fr.inra.maiage.bibliome.util.files.WorkingDirectory;
import fr.inra.maiage.bibliome.util.streams.SourceStream;

/**
 * Uses YaTeA to extract terms from the corpus.
 */
public abstract class AbstractYateaExtractor<S extends SectionResolvedObjects> extends SectionModule<S> implements Checkable {
    private String wordLayer = DefaultNames.getWordLayer();
    private String formFeature = Annotation.FORM_FEATURE_NAME;
    private String posFeature = DefaultNames.getPosTagFeature();
    private String lemmaFeature = DefaultNames.getCanonicalFormFeature();
    private String sentenceLayer = DefaultNames.getSentenceLayer();
    private ExecutableFile yateaExecutable;
    private SourceStream rcFile;
    private WorkingDirectory workingDir;
    private OutputFile xmlTermsFile;
    private OutputFile termListFile;
    private String perlLib;
    private Boolean bioYatea = false;
    private InputFile postProcessingConfig;
    private OutputFile postProcessingOutput;
    private Boolean documentTokens = true;
    private Mapping yateaDefaultConfig = new Mapping();
    private Mapping yateaOptions = new Mapping();
    private InputDirectory configDir;
    private InputDirectory localeDir;
    private OutputDirectory outputDir;
    private String language;
    private TestifiedTerminology testifiedTerminology;
    private String suffix;
    
    /**
     * Constructs a new YateaExtractor object.
     */
    public AbstractYateaExtractor() {
    }

    @Override
    public String[] addFeaturesToSectionFilter() {
        return null;
    }

    @Override
    public String[] addLayersToSectionFilter() {
        return new String[] {
            wordLayer
        };
    }

    public Module getOwner() {
        return this;
    }

    /**
     * Gets the word layer name.
     * 
     * @return the wordLayerName
     */
    @Deprecated
    @Param(nameType=NameType.LAYER)
    public String getWordLayerName() {
        return wordLayer;
    }

    /**
     * Gets the form feature.
     * 
     * @return the formFeature
     */
    @Param(nameType=NameType.FEATURE)
    public String getFormFeature() {
        return formFeature;
    }

    /**
     * Gets the pos feature.
     * 
     * @return the posFeature
     */
    @Param(nameType=NameType.FEATURE)
    public String getPosFeature() {
        return posFeature;
    }

    /**
     * Gets the lemma feature.
     * 
     * @return the lemmaFeature
     */
    @Param(nameType=NameType.FEATURE)
    public String getLemmaFeature() {
        return lemmaFeature;
    }

    /**
     * Gets the sentence layer name.
     * 
     * @return the sentenceLayerName
     */
    @Deprecated
    @Param(nameType=NameType.LAYER)
    public String getSentenceLayerName() {
        return sentenceLayer;
    }

    /**
     * Gets the yatea executable.
     * 
     * @return the yateaExecutable
     */
    @Param
    public ExecutableFile getYateaExecutable() {
        return yateaExecutable;
    }

    /**
     * Gets the rc file.
     * 
     * @return the rcFile
     */
    @Param
    public SourceStream getRcFile() {
        return rcFile;
    }

    /**
     * Gets the working dir.
     * 
     * @return the workingDir
     */
    @Param(mandatory=false)
    @Deprecated
    public WorkingDirectory getWorkingDir() {
        return workingDir;
    }

    /**
     * Gets the perl lib.
     * 
     * @return the perlLib
     */
    @Param(mandatory=false)
    public String getPerlLib() {
        return perlLib;
    }

    @Param(mandatory=false)
	public InputFile getPostProcessingConfig() {
		return postProcessingConfig;
	}

    @Param(mandatory=false)
	public OutputFile getPostProcessingOutput() {
		return postProcessingOutput;
	}

    @Param
	public Boolean getDocumentTokens() {
		return documentTokens;
	}

    @Param
	public Boolean getBioYatea() {
		return bioYatea;
	}

    @Param
	public Mapping getYateaDefaultConfig() {
		return yateaDefaultConfig;
	}

    @Param
	public Mapping getYateaOptions() {
		return yateaOptions;
	}

    @Param(mandatory=false)
	public InputDirectory getConfigDir() {
		return configDir;
	}

    @Param(mandatory=false)
	public InputDirectory getLocaleDir() {
		return localeDir;
	}

    @Param(mandatory=false)
	public OutputDirectory getOutputDir() {
		return outputDir;
	}

    @Param(mandatory=false)
	public String getLanguage() {
		return language;
	}

    @Param(mandatory=false)
	public TestifiedTerminology getTestifiedTerminology() {
		return testifiedTerminology;
	}

    @Param(mandatory=false)
	public String getSuffix() {
		return suffix;
	}

    @Param(mandatory=false)
	public OutputFile getXmlTermsFile() {
		return xmlTermsFile;
	}

    @Param(mandatory=false)
	public OutputFile getTermListFile() {
		return termListFile;
	}

    @Param(nameType=NameType.LAYER)
	public String getWordLayer() {
		return wordLayer;
	}

    @Param(nameType=NameType.LAYER)
	public String getSentenceLayer() {
		return sentenceLayer;
	}

	public void setWordLayer(String wordLayer) {
		this.wordLayer = wordLayer;
	}

	public void setSentenceLayer(String sentenceLayer) {
		this.sentenceLayer = sentenceLayer;
	}

	public void setTermListFile(OutputFile termListFile) {
		this.termListFile = termListFile;
	}

	public void setXmlTermsFile(OutputFile xmlTermsFile) {
		this.xmlTermsFile = xmlTermsFile;
	}

	public void setConfigDir(InputDirectory configDir) {
		this.configDir = configDir;
	}

	public void setLocaleDir(InputDirectory localeDir) {
		this.localeDir = localeDir;
	}

	public void setOutputDir(OutputDirectory outputDir) {
		this.outputDir = outputDir;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public void setTestifiedTerminology(TestifiedTerminology testifiedTerminology) {
		this.testifiedTerminology = testifiedTerminology;
	}

	public void setSuffix(String suffix) {
		this.suffix = suffix;
	}

	public void setYateaDefaultConfig(Mapping yateaDefaultConfig) {
		this.yateaDefaultConfig = yateaDefaultConfig;
	}

	public void setYateaOptions(Mapping yateaOptions) {
		this.yateaOptions = yateaOptions;
	}

	public void setBioYatea(Boolean bioYatea) {
		this.bioYatea = bioYatea;
	}

	public void setDocumentTokens(Boolean documentTokens) {
		this.documentTokens = documentTokens;
	}

	public void setPostProcessingConfig(InputFile postProcessingConfig) {
		this.postProcessingConfig = postProcessingConfig;
	}

	public void setPostProcessingOutput(OutputFile postProcessingOutput) {
		this.postProcessingOutput = postProcessingOutput;
	}

	/**
     * Sets the word layer name.
     * 
     * @param wordLayerName
     *            the wordLayerName to set
     */
    public void setWordLayerName(String wordLayerName) {
        this.wordLayer = wordLayerName;
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
     * Sets the sentence layer name.
     * 
     * @param sentenceLayerName
     *            the sentenceLayerName to set
     */
    public void setSentenceLayerName(String sentenceLayerName) {
        this.sentenceLayer = sentenceLayerName;
    }

    /**
     * Sets the yatea executable.
     * 
     * @param yateaExecutable
     *            the yateaExecutable to set
     */
    public void setYateaExecutable(ExecutableFile yateaExecutable) {
        this.yateaExecutable = yateaExecutable;
    }

    /**
     * Sets the rc file.
     * 
     * @param rcFile
     *            the rcFile to set
     */
    public void setRcFile(SourceStream rcFile) {
        this.rcFile = rcFile;
    }

    /**
     * Sets the working dir.
     * 
     * @param workingDir
     *            the workingDir to set
     */
    public void setWorkingDir(WorkingDirectory workingDir) {
        this.workingDir = workingDir;
    }

    /**
     * Sets the perl lib.
     * 
     * @param perlLib
     *            the perlLib to set
     */
    public void setPerlLib(String perlLib) {
        this.perlLib = perlLib;
    }
    
    @Override
	public boolean check(Logger logger) {
    	if ((workingDir == null) && (xmlTermsFile == null) && (termListFile == null)) {
    		logger.severe("neither workingDir, xmlTermsFile or termListFile is set, set xmlTermsFile and termListFile to specify yatea output file");
    		logger.severe("workingDir is deprecated, future versions may not support workingDir");
    		return false;
    	}
    	return true;
	}

	protected WorkingDirectory getWorkingDirectory(ProcessingContext ctx) {
    	if (workingDir != null) {
    		return workingDir;
    	}
    	File tempDir = getTempDir(ctx);
    	return new WorkingDirectory(tempDir, "yatea");
    }
    
    private static void writeConfigProperties(PrintStream out, Properties properties, String tag) {
		out.println("<" + tag + ">");
		for (String key : properties.stringPropertyNames()) {
			String value = properties.getProperty(key);
			out.println(key + " = " + value);
		}
		out.println("</" + tag + ">");
    }
    
    static void writeYateaConfig(File f, Properties defaultConfig, Properties options) throws FileNotFoundException {
    	try (PrintStream out = new PrintStream(f)) {
    		writeConfigProperties(out, defaultConfig, "DefaultConfig");
    		writeConfigProperties(out, options, "OPTIONS");
    	}
    }
}
