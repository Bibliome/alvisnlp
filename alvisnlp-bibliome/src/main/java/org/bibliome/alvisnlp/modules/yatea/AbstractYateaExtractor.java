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



package org.bibliome.alvisnlp.modules.yatea;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bibliome.alvisnlp.modules.SectionModule;
import org.bibliome.alvisnlp.modules.SectionModule.SectionResolvedObjects;
import org.bibliome.util.Iterators;
import org.bibliome.util.Pair;
import org.bibliome.util.Strings;
import org.bibliome.util.Timer;
import org.bibliome.util.files.ExecutableFile;
import org.bibliome.util.files.InputDirectory;
import org.bibliome.util.files.InputFile;
import org.bibliome.util.files.OutputDirectory;
import org.bibliome.util.files.OutputFile;
import org.bibliome.util.files.WorkingDirectory;
import org.bibliome.util.streams.SourceStream;

import alvisnlp.corpus.Annotation;
import alvisnlp.corpus.Corpus;
import alvisnlp.corpus.DefaultNames;
import alvisnlp.corpus.Layer;
import alvisnlp.corpus.NameType;
import alvisnlp.corpus.Section;
import alvisnlp.corpus.expressions.EvaluationContext;
import alvisnlp.module.Module;
import alvisnlp.module.ModuleException;
import alvisnlp.module.ProcessingContext;
import alvisnlp.module.TimerCategory;
import alvisnlp.module.lib.External;
import alvisnlp.module.lib.Param;
import alvisnlp.module.types.Mapping;

/**
 * Uses YaTeA to extract terms from the corpus.
 */
public abstract class AbstractYateaExtractor<S extends SectionResolvedObjects> extends SectionModule<S> {
    private String wordLayerName = DefaultNames.getWordLayer();
    private String formFeature = Annotation.FORM_FEATURE_NAME;
    private String posFeature = DefaultNames.getPosTagFeature();
    private String lemmaFeature = DefaultNames.getCanonicalFormFeature();
    private String sentenceLayerName = DefaultNames.getSentenceLayer();
    private ExecutableFile yateaExecutable;
    private SourceStream rcFile;
    private WorkingDirectory workingDir;
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
            wordLayerName
        };
    }

    @Override
    public void process(ProcessingContext<Corpus> ctx, Corpus corpus) throws ModuleException {
		Logger logger = getLogger(ctx);
		EvaluationContext evalCtx = new EvaluationContext(logger);

		if (outputDir != null && !outputDir.exists() && !outputDir.mkdirs()) {
			processingException("could not create " + outputDir.getAbsolutePath());
		}
		if (!workingDir.exists() && !workingDir.mkdirs()) {
			processingException("could not create " + outputDir.getAbsolutePath());
		}
		try {
			YateaExtractorExternal yateaExt = new YateaExtractorExternal(ctx);
			InputFile testifiedTerminology = this.testifiedTerminology == null ? null : this.testifiedTerminology.ensureFile(this, ctx, corpus);
			yateaExt.createRCFile(testifiedTerminology);
			yateaExt.createInput(evalCtx, corpus);
			callExternal(ctx, "yatea", yateaExt, "UTF-8", "call-yatea.sh");
		}
		catch (IOException e) {
			rethrow(e);
		}
    }

    public Module<Corpus> getOwner() {
        return this;
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
    @Param(nameType=NameType.FEATURE, defaultDoc = "Feature containing the word form.")
    public String getFormFeature() {
        return formFeature;
    }

    /**
     * Gets the pos feature.
     * 
     * @return the posFeature
     */
    @Param(nameType=NameType.FEATURE, defaultDoc = "Feature containing the word POS tag.")
    public String getPosFeature() {
        return posFeature;
    }

    /**
     * Gets the lemma feature.
     * 
     * @return the lemmaFeature
     */
    @Param(nameType=NameType.FEATURE, defaultDoc = "Feature containing the word lemma.")
    public String getLemmaFeature() {
        return lemmaFeature;
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
    @Param
    public WorkingDirectory getWorkingDir() {
        return workingDir;
    }

    /**
     * Gets the perl lib.
     * 
     * @return the perlLib
     */
    @Param(mandatory=false, defaultDoc = "Contents of the PERLLIB in the environment of Yatea binary.")
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
     * Sets the sentence layer name.
     * 
     * @param sentenceLayerName
     *            the sentenceLayerName to set
     */
    public void setSentenceLayerName(String sentenceLayerName) {
        this.sentenceLayerName = sentenceLayerName;
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
	
	private static final Pattern COMMENT = Pattern.compile("#.*$");
	
	private static String removeComments(String s) {
		Matcher m = COMMENT.matcher(s);
		if (m.find()) {
			int hash = m.start();
			return s.substring(0, hash);
		}
		return s;
	}

    private static void readYateaConfig(SourceStream source, Properties defaultConfig, Properties options) throws IOException {
		BufferedReader r = source.getBufferedReader();
		Properties current = null;
		LOOP: while (true) {
			String line = r.readLine();
			if (line == null) {
				break;
			}
			line = removeComments(line).trim();
			if (line.isEmpty()) {
				continue;
			}
			switch (line) {
				case "<DefaultConfig>":
					current = defaultConfig;
					continue LOOP;
				case "</DefaultConfig>":
					current = null;
					continue LOOP;
				case "<OPTIONS>":
					current = options;
					continue LOOP;
				case "</OPTIONS>":
					current = null;
					continue LOOP;
			}
			StringReader sr = new StringReader(line);
			current.load(sr);
		}
		r.close();
	}
    
    private static void writeConfigProperties(PrintStream out, Properties properties, String tag) {
		out.println("<" + tag + ">");
		for (String key : properties.stringPropertyNames()) {
			String value = properties.getProperty(key);
			out.println(key + " = " + value);
		}
		out.println("</" + tag + ">");
    }
    
    private static void writeYateaConfig(File f, Properties defaultConfig, Properties options) throws FileNotFoundException {
    	try (PrintStream out = new PrintStream(f)) {
    		writeConfigProperties(out, defaultConfig, "DefaultConfig");
    		writeConfigProperties(out, options, "OPTIONS");
    	}
    }
    
    private static void updateProperties(Properties target, Map<String,String> source) {
    	for (Map.Entry<String,String> e : source.entrySet()) {
    		target.setProperty(e.getKey(), e.getValue());
    	}
    }
    
    private static void updateProperty(Properties props, String key, Object value) {
    	if (value != null) {
    		props.setProperty(key, value.toString());
    	}
    }
    
    protected Pair<Properties,Properties> createConfig(InputFile testifiedTerminology) throws IOException {
    	Properties defaultConfig = new Properties();
    	Properties options = new Properties();
    	readYateaConfig(rcFile, defaultConfig, options);
    	updateProperties(defaultConfig, yateaDefaultConfig);
    	updateProperties(options, yateaOptions);
    	updateProperty(defaultConfig, "CONFIG_DIR", configDir);
    	updateProperty(defaultConfig, "LOCALE_DIR", localeDir);
    	updateProperty(options, "output-path", outputDir);
    	updateProperty(options, "language", language);
    	updateProperty(options, "termino", testifiedTerminology);
    	updateProperty(options, "suffix", suffix);
    	return new Pair<Properties,Properties>(defaultConfig, options);
    }
    
    private final class YateaExtractorExternal implements External<Corpus> {
    	private final ProcessingContext<Corpus> ctx;
        private File   ttgCorpus         = null;
        private File rcTempFile;

        private YateaExtractorExternal(ProcessingContext<Corpus> ctx) {
			super();
			this.ctx = ctx;
		}
        
        private void createRCFile(InputFile testifiedTerminology) throws IOException {
        	Pair<Properties,Properties> p = createConfig(testifiedTerminology);
        	Properties defaultConfig = p.first;
        	Properties options = p.second;
        	File tmpDir = getTempDir(ctx);
        	rcTempFile = new File(tmpDir, "config.rc");
        	writeYateaConfig(rcTempFile, defaultConfig, options);
        }

        private void createInput(EvaluationContext evalCtx, Corpus corpus) throws ModuleException {
            Timer<TimerCategory> inputTimer = getTimer(ctx, "yatea-input", TimerCategory.PREPARE_DATA, true);
            PrintStream ttgOut = null;
            try {
            	File tmpDir = getTempDir(ctx);
            	ttgCorpus = new File(tmpDir, "corpus.ttg");
                ttgCorpus.getParentFile().mkdirs();
                ttgOut = new PrintStream(new BufferedOutputStream(new FileOutputStream(ttgCorpus)), false, "UTF-8");
            }
            catch (FileNotFoundException fnfe) {
                rethrow(fnfe);
            }
            catch (UnsupportedEncodingException uee) {
                rethrow(uee);
            }
            for (Section sec : Iterators.loop(sectionIterator(evalCtx, corpus))) {
                if (documentTokens) {
                    String s = Strings.normalizeSpace(sec.getDocument().getId() + "/" + sec.getName());
                	ttgOut.printf("%s\tDOCUMENT\t%s\n", s, s);
                }
                for (Layer sent : sec.getSentences(wordLayerName, sentenceLayerName)) {
                    for (Annotation word : sent) {
                    	String token = Strings.normalizeSpace(word.getLastFeature(formFeature));
                    	if (token.isEmpty())
                    		ttgOut.println(".\tSENT\t.");
                    	else
                    		ttgOut.printf("%s\t%s\t%s\n", token, Strings.normalizeSpace(word.getLastFeature(posFeature)), Strings.normalizeSpace(word.getLastFeature(lemmaFeature)));
    				}
                    ttgOut.printf(".\tSENT\t.\n");
                }
            }
            ttgOut.close();
            inputTimer.stop();
        }

		@Override
        public String[] getCommandLineArgs() throws ModuleException {
			List<String> result = new ArrayList<String>();
			result.add(yateaExecutable.getAbsolutePath());
			if (bioYatea || (postProcessingOutput != null && postProcessingConfig != null)) {
				result.add("--extract");
			}
			result.add("--rcfile");
			result.add(rcTempFile.getAbsolutePath());
			if (postProcessingOutput != null) {
				result.add("--post-processing");
				result.add(postProcessingOutput.getAbsolutePath());
			}
			if (postProcessingConfig != null) {
				result.add("--post-processing-config");
				result.add(postProcessingConfig.getAbsolutePath());
			}
			result.add(ttgCorpus.getAbsolutePath());
			return result.toArray(new String[result.size()]);
        }

        @Override
        public String[] getEnvironment() {
            if (perlLib == null) {
    			return null;
    		}
            String a[] = {
                "PERL5LIB=" + perlLib
            };
            return a;
        }

        @Override
        public File getWorkingDirectory() {
            return workingDir;
        }

        @Override
        public void processOutput(BufferedReader out, BufferedReader err) {
            try {
                Logger logger = getLogger(ctx);
                logger.fine("yatea standard error:");
                for (String line = err.readLine(); line != null; line = err.readLine()) {
                	if (line.startsWith("CHERCHE")) {
                		continue;
                	}
                	if (line.startsWith("Unparsed phrases...")) {
                		continue;
                	}
                    logger.fine("    " + line);
                }
                logger.fine("end of yatea standard error");
            }
            catch (IOException ioe) {
                getLogger(ctx).warning("could not read yatea standard error: " + ioe.getMessage());
            }
        }

		@Override
		public Module<Corpus> getOwner() {
			return AbstractYateaExtractor.this;
		}
    }
}
