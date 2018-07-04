package fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.yatea;

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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.SectionModule.SectionResolvedObjects;
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
import fr.inra.maiage.bibliome.util.Strings;
import fr.inra.maiage.bibliome.util.files.InputFile;
import fr.inra.maiage.bibliome.util.streams.SourceStream;

public class YateaExtractorExternalHandler<S extends SectionResolvedObjects> extends ExternalHandler<Corpus,AbstractYateaExtractor<S>> {
	private final EvaluationContext evalCtx;
	private final Properties defaultConfig = new Properties();
	private final Properties options = new Properties();
	
	public YateaExtractorExternalHandler(ProcessingContext<Corpus> processingContext, AbstractYateaExtractor<S> module, Corpus annotable) {
		super(processingContext, module, annotable);
		this.evalCtx = new EvaluationContext(getLogger());
	}

	@Override
	protected void prepare() throws IOException, ModuleException {
		ensureDirs();
		TestifiedTerminology testifiedTerminology = getModule().getTestifiedTerminology();
		InputFile testifiedTerminologyFile = testifiedTerminology == null ? null : testifiedTerminology.ensureFile(getModule(), getProcessingContext(), getAnnotable());
		createRCFile(testifiedTerminologyFile);
		createInput();
	}
	
	private void ensureDirs() throws ProcessingException {
		AbstractYateaExtractor<S> owner = getModule();
		File outputDir = owner.getOutputDir();
		if (outputDir != null && !outputDir.exists() && !outputDir.mkdirs()) {
			throw new ProcessingException("could not create " + outputDir.getAbsolutePath());
		}
		File workingDir = owner.getWorkingDir();
		if (!workingDir.exists() && !workingDir.mkdirs()) {
			throw new ProcessingException("could not create " + outputDir.getAbsolutePath());
		}
	}

	private void createRCFile(InputFile testifiedTerminologyFile) throws IOException {
    	createConfig(testifiedTerminologyFile);
    	AbstractYateaExtractor.writeYateaConfig(getRCFile(), defaultConfig, options);
    }
   
    private void createConfig(InputFile testifiedTerminology) throws IOException {
		AbstractYateaExtractor<S> owner = getModule();
    	readYateaConfig(owner.getRcFile(), defaultConfig, options);
    	updateProperties(defaultConfig, owner.getYateaDefaultConfig());
    	updateProperties(options, owner.getYateaOptions());
    	updateProperty(defaultConfig, "CONFIG_DIR", owner.getConfigDir());
    	updateProperty(defaultConfig, "LOCALE_DIR", owner.getLocaleDir());
    	updateProperty(options, "output-path", owner.getOutputDir());
    	updateProperty(options, "language", owner.getLanguage());
    	updateProperty(options, "termino", testifiedTerminology);
    	updateProperty(options, "suffix", owner.getSuffix());
		getLogger().fine("owner.getOutputDir() = " + owner.getOutputDir());
		getLogger().fine("options = " + options);
		getLogger().fine("defaultConfig = " + defaultConfig);
    }

    public Properties getDefaultConfig() {
		return defaultConfig;
	}

	public Properties getOptions() {
		return options;
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
	
	private static final Pattern COMMENT = Pattern.compile("#.*$");
	private static String removeComments(String s) {
		Matcher m = COMMENT.matcher(s);
		if (m.find()) {
			int hash = m.start();
			return s.substring(0, hash);
		}
		return s;
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

	private File getRCFile() {
		return getTempFile("config.rc");
	}
    
    void createInput() throws ModuleException {
    	AbstractYateaExtractor<S> owner = getModule();
        PrintStream ttgOut = null;
        try {
        	File ttgCorpus = getYateaInput();
            ttgCorpus.getParentFile().mkdirs();
            ttgOut = new PrintStream(new BufferedOutputStream(new FileOutputStream(ttgCorpus)), false, "UTF-8");
        }
        catch (FileNotFoundException | UnsupportedEncodingException e) {
			throw new ProcessingException(e);
        }
        for (Section sec : Iterators.loop(owner.sectionIterator(evalCtx, getAnnotable()))) {
            if (owner.getDocumentTokens()) {
                String s = Strings.normalizeSpace(sec.getDocument().getId() + "/" + sec.getName());
            	ttgOut.printf("%s\tDOCUMENT\t%s\n", s, s);
            }
            for (Layer sent : sec.getSentences(owner.getWordLayerName(), owner.getSentenceLayerName())) {
                for (Annotation word : sent) {
                	String token = Strings.normalizeSpace(word.getLastFeature(owner.getFormFeature()));
                	if (token.isEmpty())
                		ttgOut.println(".\tSENT\t.");
                	else
                		ttgOut.printf("%s\t%s\t%s\n", token, Strings.normalizeSpace(word.getLastFeature(owner.getPosFeature())), Strings.normalizeSpace(word.getLastFeature(owner.getLemmaFeature())));
				}
                ttgOut.printf(".\tSENT\t.\n");
            }
        }
        ttgOut.close();
    }
    
    private File getYateaInput() {
    	return getTempFile("corpus.ttg");
    }

	@Override
	protected String getPrepareTask() {
		return "create-input";
	}

	@Override
	protected String getExecTask() {
		return "yatea";
	}

	@Override
	protected List<String> getCommandLine() {
		AbstractYateaExtractor<S> owner = getModule();
		List<String> result = new ArrayList<String>();
		result.add(owner.getYateaExecutable().getAbsolutePath());
		if (owner.getBioYatea() || (owner.getPostProcessingOutput() != null && owner.getPostProcessingConfig() != null)) {
			result.add("--extract");
		}
		result.add("--rcfile");
		result.add(getRCFile().getAbsolutePath());
		if (owner.getPostProcessingOutput() != null) {
			result.add("--post-processing");
			result.add(owner.getPostProcessingOutput().getAbsolutePath());
		}
		if (owner.getPostProcessingConfig() != null) {
			result.add("--post-processing-config");
			result.add(owner.getPostProcessingConfig().getAbsolutePath());
		}
		result.add(getYateaInput().getAbsolutePath());
		return result;
	}

	@Override
	protected void updateEnvironment(Map<String,String> env) {
    	String perlLib = getModule().getPerlLib();
        if (perlLib != null) {
        	env.put("PERL5LIB", perlLib);
        };

	}

	@Override
	protected File getWorkingDirectory() {
        return getModule().getWorkingDir();
	}

	@Override
	protected String getInputFileame() {
		return null;
	}

	@Override
	protected String getOutputFilename() {
		return null;
	}

	@Override
	protected void collect() throws IOException, ModuleException {
	}

	@Override
	protected String getCollectTask() {
		return "collect";
	}
}
