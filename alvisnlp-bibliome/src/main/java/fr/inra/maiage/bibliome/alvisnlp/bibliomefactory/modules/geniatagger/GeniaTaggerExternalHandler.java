package fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.geniatagger;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.geniatagger.GeniaTagger.GeniaTaggerResolvedObjects;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Annotation;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Corpus;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Layer;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Section;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.EvaluationContext;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.Evaluator;
import fr.inra.maiage.bibliome.alvisnlp.core.module.ProcessingContext;
import fr.inra.maiage.bibliome.alvisnlp.core.module.ProcessingException;
import fr.inra.maiage.bibliome.alvisnlp.core.module.lib.ExternalHandler;
import fr.inra.maiage.bibliome.alvisnlp.core.module.lib.ModuleBase;
import fr.inra.maiage.bibliome.alvisnlp.core.module.lib.OutputHandler;
import fr.inra.maiage.bibliome.util.Files;
import fr.inra.maiage.bibliome.util.Iterators;
import fr.inra.maiage.bibliome.util.Strings;
import fr.inra.maiage.bibliome.util.filters.Filters;
import fr.inra.maiage.bibliome.util.mappers.Mapper;
import fr.inra.maiage.bibliome.util.mappers.Mappers;
import fr.inra.maiage.bibliome.util.streams.FileSourceStream;
import fr.inra.maiage.bibliome.util.streams.FileTargetStream;
import fr.inra.maiage.bibliome.util.streams.SourceStream;
import fr.inra.maiage.bibliome.util.streams.TargetStream;

public class GeniaTaggerExternalHandler extends ExternalHandler<Corpus,GeniaTagger> {
	private final EvaluationContext evalCtx;
	
	public GeniaTaggerExternalHandler(ProcessingContext<Corpus> processingContext, GeniaTagger module, Corpus annotable) {
		super(processingContext, module, annotable);
		this.evalCtx = new EvaluationContext(getLogger());
	}

	@Override
	protected void prepare() throws IOException {
		createScriptFile();
		createGeniaInput();
	}
	
	private void createScriptFile() throws IOException {
		File script = getScriptFile();
		// same ClassLoader as this class
		try (InputStream is = GeniaTagger.class.getResourceAsStream("genia.sh")) {
			Files.copy(is, script, 1024, true);
		}
		script.setExecutable(true);
	}
	
	private File getScriptFile() {
		return getTempFile("genia.sh");
	}
	
	private void createGeniaInput() throws IOException {
		GeniaTagger owner = getModule();
		GeniaTaggerResolvedObjects resObj = owner.getResolvedObjects();
		File input = getGeniaInputFile();
		TargetStream target = new FileTargetStream(owner.getGeniaCharset(), input.getAbsolutePath());
		try (PrintStream ps = target.getPrintStream()) {
			for (Annotation sent : Iterators.loop(getSentenceIterator(resObj.getDocumentFilter(), resObj.getSectionFilter(), resObj.sentenceFilter)))
				writeSentence(sent, ps);
		}
	}
	
	private void writeSentence(Annotation sent, PrintStream ps) {
		GeniaTagger owner = getModule();
		Layer wordLayer = sent.getSection().getLayer(owner.getWordLayerName());
		StringBuilder sb = new StringBuilder();
		boolean notFirst = false;
		for (Annotation w : wordLayer.between(sent)) {
			if (notFirst)
				sb.append(' ');
			else
				notFirst = true;
			Strings.escapeWhitespaces(sb, w.getLastFeature(owner.getWordFormFeature()));
		}
		sb.append(" .");
		ps.println(sb);
	}

	private File getGeniaInputFile() {
		return getTempFile("corpus.txt");
	}

	@Override
	protected ProcessBuilder getProcessBuilder() {
		GeniaTagger owner = getModule();
		ProcessBuilder result = new ProcessBuilder(getScriptFile().getAbsolutePath());
		Map<String,String> env = result.environment();
		env.put("GENIA_DIR", owner.getGeniaDir().getAbsolutePath());
		env.put("GENIA_BIN", owner.getGeniaTaggerExecutable().getAbsolutePath());
		env.put("GENIA_IN", getGeniaInputFile().getAbsolutePath());
		env.put("GENIA_OUT", getGeniaOutputFile().getAbsolutePath());
		result.directory(owner.getGeniaDir());
		return result;
	}
	
	private File getGeniaOutputFile() {
		return getTempFile("corpus.genia");
	}

	@Override
	protected OutputHandler getOutputHandler() {
		return new OutputHandler.ToLogger(getLogger(), "(genia stdout) "); 
	}

	@Override
	protected OutputHandler getErrorHandler() {
		return new OutputHandler.ToLogger(getLogger(), "(genia stderr) "); 
	}

	@Override
	protected void collect() throws IOException, ProcessingException {
		GeniaTagger owner = getModule();
		GeniaTaggerResolvedObjects resObj = owner.getResolvedObjects();
		SourceStream source = new FileSourceStream(owner.getGeniaCharset(), getGeniaOutputFile().getAbsolutePath());
		try (BufferedReader r = source.getBufferedReader()) {
			for (Annotation sent : Iterators.loop(getSentenceIterator(resObj.getDocumentFilter(), resObj.getSectionFilter(), resObj.sentenceFilter))) {
				readSentence(sent, r);
			}
			if (r.readLine() != null) {
				ModuleBase.processingException("genia tagger output is too long");
			}
		}
	}

	private Iterator<Annotation> getSentenceIterator(Evaluator resolvedDocumentFilter, Evaluator resolvedSectionFilter, Evaluator resolvedSentenceFilter) {
        return Iterators.flatten(Mappers.apply(new AnnotationCollector(evalCtx, getModule().getSentenceLayerName(), resolvedSentenceFilter), getAnnotable().sectionIterator(evalCtx, resolvedDocumentFilter, resolvedSectionFilter)));
	}
    
    private static class AnnotationCollector implements Mapper<Section,Iterator<Annotation>> {
		private final EvaluationContext ctx;
        private final String layerName;
        private final Evaluator filter;
        
        private AnnotationCollector(EvaluationContext ctx, String layerName, Evaluator filter) {
            super();
            this.ctx = ctx;
            this.layerName = layerName;
            this.filter = filter;
        }

        @Override
        public Iterator<Annotation> map(Section sec) {
            return annotationIterator(ctx, sec, layerName, filter);
        }
    }
    
    private static Iterator<Annotation> annotationIterator(EvaluationContext ctx, Section sec, String name, Evaluator filter) {
        if (sec.hasLayer(name)) {
            if (filter == null)
                return sec.getLayer(name).iterator();
            return Filters.apply(filter.getFilter(ctx), sec.getLayer(name).iterator());
        }
        return Iterators.emptyIterator();
    }

	private void readSentence(Annotation sent, BufferedReader r) throws ProcessingException, IOException {
		GeniaTagger owner = getModule();
		Layer wordLayer = sent.getSection().getLayer(owner.getWordLayerName());
		for (Annotation w : wordLayer.between(sent)) {
			String line = r.readLine();
			if (line == null)
				ModuleBase.processingException("geniatagger output is short on lines");
			List<String> cols = Strings.split(line, '\t', -1);
			if (cols.size() != 5) {
				ModuleBase.processingException("malformed genia tagger output: " + line);
			}
			String form = cols.get(0);
			String lemma = cols.get(1);
			String pos = cols.get(2);
			String chunk = cols.get(3);
			String entity = cols.get(4);
			w.addFeature(owner.getLemmaFeature(), lemma);
			w.addFeature(owner.getPosFeature(), getPOS(form, pos, lemma).intern());
			w.addFeature(owner.getChunkFeature(), chunk);
			w.addFeature(owner.getEntityFeature(), entity);
		}
		if (r.readLine() == null) // final dot
			ModuleBase.processingException("geniatagger output is short on lines");
		if (r.readLine() == null) // geniatagger adds a blank line after each sentence
			ModuleBase.processingException("geniatagger output is short on lines");
	}
	
	private String getPOS(@SuppressWarnings("unused") String form, String pos, String lemma) {
		if (!getModule().getTreeTaggerTagset()) {
			return pos;
		}
		if (lemma.equals("have") && pos.startsWith("VB")) {
			return "VH" + pos.substring(2);
		}
		if (lemma.equals("be") && pos.startsWith("VB")) {
			return "VV" + pos.substring(2);
		}
		if (pos.startsWith("NNP")) {
			return "NP" + pos.substring(3);
		}
		if (pos.startsWith("PRP")) {
			return "PP" + pos.substring(3);
		}
		if (pos.equals(".")) {
			return "SENT";
		}
		return pos;
	}

	@Override
	protected String getPrepareTask() {
		return "alvisnlp-to-genia";
	}

	@Override
	protected String getExecTask() {
		return "genia";
	}

	@Override
	protected String getCollectTask() {
		return "genia-to-alvisnlp";
	}
}
