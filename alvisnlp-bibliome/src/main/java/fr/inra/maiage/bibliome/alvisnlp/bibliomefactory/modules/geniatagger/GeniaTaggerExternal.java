package fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.geniatagger;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.List;

import fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.geniatagger.GeniaTagger.GeniaTaggerResolvedObjects;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Annotation;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Corpus;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Layer;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.EvaluationContext;
import fr.inra.maiage.bibliome.alvisnlp.core.module.ModuleException;
import fr.inra.maiage.bibliome.alvisnlp.core.module.ProcessingContext;
import fr.inra.maiage.bibliome.alvisnlp.core.module.ProcessingException;
import fr.inra.maiage.bibliome.alvisnlp.core.module.lib.AbstractExternal;
import fr.inra.maiage.bibliome.alvisnlp.core.module.lib.ModuleBase;
import fr.inra.maiage.bibliome.util.Files;
import fr.inra.maiage.bibliome.util.Iterators;
import fr.inra.maiage.bibliome.util.Strings;
import fr.inra.maiage.bibliome.util.files.InputFile;
import fr.inra.maiage.bibliome.util.files.OutputFile;
import fr.inra.maiage.bibliome.util.streams.FileSourceStream;
import fr.inra.maiage.bibliome.util.streams.FileTargetStream;
import fr.inra.maiage.bibliome.util.streams.SourceStream;
import fr.inra.maiage.bibliome.util.streams.TargetStream;

public class GeniaTaggerExternal extends AbstractExternal<Corpus,GeniaTagger> {
	private final EvaluationContext evalCtx;
	private final File script;
	private final OutputFile input;
	private final InputFile output;
	
	GeniaTaggerExternal(GeniaTagger owner, ProcessingContext<Corpus> ctx, Corpus corpus, File tmpDir) throws IOException {
		super(owner, ctx);
		this.evalCtx = new EvaluationContext(getLogger());
//		
		script = new File(tmpDir, "genia.sh");
//		// same ClassLoader as this class
//		try (InputStream is = GeniaTagger.class.getResourceAsStream("genia.sh")) {
//			Files.copy(is, script, 1024, true);
//		}
//		script.setExecutable(true);
//
//		GeniaTaggerResolvedObjects resObj = owner.getResolvedObjects();
		input = new OutputFile(tmpDir, "corpus.txt");
//		TargetStream target = new FileTargetStream(owner.getGeniaCharset(), input);
//		PrintStream ps = target.getPrintStream();
//		for (Annotation sent : Iterators.loop(owner.getSentenceIterator(evalCtx, corpus, resObj.getDocumentFilter(), resObj.getSectionFilter(), resObj.sentenceFilter)))
//			writeSentence(sent, ps);
//		ps.close();
//		
		output = new InputFile(tmpDir, "corpus.genia");
	}
	
	private void writeSentence(Annotation sent, PrintStream ps) {
		Layer wordLayer = sent.getSection().getLayer(getOwner().getWordLayerName());
		StringBuilder sb = new StringBuilder();
		boolean notFirst = false;
		for (Annotation w : wordLayer.between(sent)) {
			if (notFirst)
				sb.append(' ');
			else
				notFirst = true;
			Strings.escapeWhitespaces(sb, w.getLastFeature(getOwner().getWordFormFeature()));
		}
		sb.append(" .");
		ps.println(sb);
	}

	void readOutput(Corpus corpus) throws ProcessingException, IOException {
//		GeniaTaggerResolvedObjects resObj = getOwner().getResolvedObjects();
//		SourceStream source = new FileSourceStream(getOwner().getGeniaCharset(), output);
//		BufferedReader r = source.getBufferedReader();
//		for (Annotation sent : Iterators.loop(getOwner().getSentenceIterator(evalCtx, corpus, resObj.getDocumentFilter(), resObj.getSectionFilter(), resObj.sentenceFilter)))
//			readSentence(sent, r);
//		if (r.readLine() != null)
//			ModuleBase.processingException("genia tagger output is too long");
//		r.close();
	}

	private void readSentence(Annotation sent, BufferedReader r) throws ProcessingException, IOException {
		Layer wordLayer = sent.getSection().getLayer(getOwner().getWordLayerName());
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
			w.addFeature(getOwner().getLemmaFeature(), lemma);
			w.addFeature(getOwner().getPosFeature(), getPOS(form, pos, lemma).intern());
			w.addFeature(getOwner().getChunkFeature(), chunk);
			w.addFeature(getOwner().getEntityFeature(), entity);
		}
		if (r.readLine() == null) // final dot
			ModuleBase.processingException("geniatagger output is short on lines");
		if (r.readLine() == null) // geniatagger adds a blank line after each sentence
			ModuleBase.processingException("geniatagger output is short on lines");
	}
	
	private String getPOS(@SuppressWarnings("unused") String form, String pos, String lemma) {
		if (!getOwner().getTreeTaggerTagset()) {
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
	public String[] getCommandLineArgs() throws ModuleException {
		return new String[] {
				script.getAbsolutePath()
		};
	}

	@Override
	public String[] getEnvironment() throws ModuleException {
		return new String[] {
			"GENIA_DIR=" + getOwner().getGeniaDir().getAbsolutePath(),
			"GENIA_BIN=" + getOwner().getGeniaTaggerExecutable().getPath(),
			"GENIA_IN="  + input.getAbsolutePath(),
			"GENIA_OUT=" + output.getAbsolutePath()
		};
	}

	@Override
	public File getWorkingDirectory() throws ModuleException {
		return getOwner().getGeniaDir();
	}
}
