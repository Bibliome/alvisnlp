package fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.yatea;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.SectionModule.SectionResolvedObjects;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Annotation;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Corpus;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Layer;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Section;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.EvaluationContext;
import fr.inra.maiage.bibliome.alvisnlp.core.module.ModuleException;
import fr.inra.maiage.bibliome.alvisnlp.core.module.ProcessingContext;
import fr.inra.maiage.bibliome.alvisnlp.core.module.TimerCategory;
import fr.inra.maiage.bibliome.alvisnlp.core.module.lib.AbstractExternal;
import fr.inra.maiage.bibliome.alvisnlp.core.module.lib.ModuleBase;
import fr.inra.maiage.bibliome.util.Iterators;
import fr.inra.maiage.bibliome.util.Pair;
import fr.inra.maiage.bibliome.util.Strings;
import fr.inra.maiage.bibliome.util.Timer;
import fr.inra.maiage.bibliome.util.files.InputFile;

class YateaExtractorExternal<S extends SectionResolvedObjects> extends AbstractExternal<Corpus,AbstractYateaExtractor<S>> {
	private final ProcessingContext<Corpus> ctx;
    private File   ttgCorpus         = null;
    private File rcTempFile;

    YateaExtractorExternal(AbstractYateaExtractor<S> owner, ProcessingContext<Corpus> ctx) {
		super(owner, ctx);
		this.ctx = ctx;
	}
    
    void createRCFile(InputFile testifiedTerminology) throws IOException {
    	Pair<Properties,Properties> p = getOwner().createConfig(testifiedTerminology);
    	Properties defaultConfig = p.first;
    	Properties options = p.second;
    	File tmpDir = getOwner().getTempDir(ctx);
    	rcTempFile = new File(tmpDir, "config.rc");
    	AbstractYateaExtractor.writeYateaConfig(rcTempFile, defaultConfig, options);
    }

    void createInput(EvaluationContext evalCtx, Corpus corpus) throws ModuleException {
    	AbstractYateaExtractor<S> owner = getOwner();
        Timer<TimerCategory> inputTimer = owner.getTimer(ctx, "yatea-input", TimerCategory.PREPARE_DATA, true);
        PrintStream ttgOut = null;
        try {
        	File tmpDir = owner.getTempDir(ctx);
        	ttgCorpus = new File(tmpDir, "corpus.ttg");
            ttgCorpus.getParentFile().mkdirs();
            ttgOut = new PrintStream(new BufferedOutputStream(new FileOutputStream(ttgCorpus)), false, "UTF-8");
        }
        catch (FileNotFoundException fnfe) {
            ModuleBase.rethrow(fnfe);
        }
        catch (UnsupportedEncodingException uee) {
            ModuleBase.rethrow(uee);
        }
        for (Section sec : Iterators.loop(owner.sectionIterator(evalCtx, corpus))) {
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
        inputTimer.stop();
    }

	@Override
    public String[] getCommandLineArgs() throws ModuleException {
		AbstractYateaExtractor<S> owner = getOwner();
		List<String> result = new ArrayList<String>();
		result.add(owner.getYateaExecutable().getAbsolutePath());
		if (owner.getBioYatea() || (owner.getPostProcessingOutput() != null && owner.getPostProcessingConfig() != null)) {
			result.add("--extract");
		}
		result.add("--rcfile");
		result.add(rcTempFile.getAbsolutePath());
		if (owner.getPostProcessingOutput() != null) {
			result.add("--post-processing");
			result.add(owner.getPostProcessingOutput().getAbsolutePath());
		}
		if (owner.getPostProcessingConfig() != null) {
			result.add("--post-processing-config");
			result.add(owner.getPostProcessingConfig().getAbsolutePath());
		}
		result.add(ttgCorpus.getAbsolutePath());
		return result.toArray(new String[result.size()]);
    }

    @Override
    public String[] getEnvironment() {
    	String perlLib = getOwner().getPerlLib();
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
        return getOwner().getWorkingDir();
    }
}