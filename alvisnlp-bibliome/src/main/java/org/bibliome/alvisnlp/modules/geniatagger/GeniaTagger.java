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


package org.bibliome.alvisnlp.modules.geniatagger;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

import org.bibliome.alvisnlp.modules.SectionModule;
import org.bibliome.alvisnlp.modules.SectionModule.SectionResolvedObjects;
import org.bibliome.alvisnlp.modules.geniatagger.GeniaTagger.GeniaTaggerResolvedObjects;

import alvisnlp.corpus.Annotation;
import alvisnlp.corpus.Corpus;
import alvisnlp.corpus.DefaultNames;
import alvisnlp.corpus.Layer;
import alvisnlp.corpus.NameType;
import alvisnlp.corpus.Section;
import alvisnlp.corpus.expressions.ConstantsLibrary;
import alvisnlp.corpus.expressions.EvaluationContext;
import alvisnlp.corpus.expressions.Evaluator;
import alvisnlp.corpus.expressions.Expression;
import alvisnlp.corpus.expressions.ResolverException;
import alvisnlp.module.Module;
import alvisnlp.module.ModuleException;
import alvisnlp.module.NameUsage;
import alvisnlp.module.ProcessingContext;
import alvisnlp.module.ProcessingException;
import alvisnlp.module.TimerCategory;
import alvisnlp.module.lib.AlvisNLPModule;
import alvisnlp.module.lib.External;
import alvisnlp.module.lib.Param;
import alvisnlp.module.lib.TimeThis;
import fr.inra.maiage.bibliome.util.Files;
import fr.inra.maiage.bibliome.util.Iterators;
import fr.inra.maiage.bibliome.util.Strings;
import fr.inra.maiage.bibliome.util.files.InputFile;
import fr.inra.maiage.bibliome.util.files.OutputFile;
import fr.inra.maiage.bibliome.util.filters.Filters;
import fr.inra.maiage.bibliome.util.mappers.Mapper;
import fr.inra.maiage.bibliome.util.mappers.Mappers;
import fr.inra.maiage.bibliome.util.streams.FileSourceStream;
import fr.inra.maiage.bibliome.util.streams.FileTargetStream;
import fr.inra.maiage.bibliome.util.streams.SourceStream;
import fr.inra.maiage.bibliome.util.streams.TargetStream;

@AlvisNLPModule
public class GeniaTagger extends SectionModule<GeniaTaggerResolvedObjects> {
	private String sentenceLayerName = DefaultNames.getSentenceLayer();
	private String wordLayerName = DefaultNames.getWordLayer();
	private String wordFormFeature = Annotation.FORM_FEATURE_NAME;
	private String posFeature = DefaultNames.getPosTagFeature();
	private String lemmaFeature = DefaultNames.getCanonicalFormFeature();
	private String chunkFeature;
	private String entityFeature;
	private File geniaDir;
	private File geniaTaggerExecutable = new File("geniatagger");
	private String geniaCharset = "UTF-8";
	private Expression sentenceFilter = ConstantsLibrary.TRUE;
	private Boolean treeTaggerTagset = false;
	
	static class GeniaTaggerResolvedObjects extends SectionResolvedObjects {
		private final Evaluator sentenceFilter;
		
		private GeniaTaggerResolvedObjects(ProcessingContext<Corpus> ctx, GeniaTagger module) throws ResolverException {
			super(ctx, module);
			sentenceFilter = module.sentenceFilter.resolveExpressions(rootResolver);
		}

		@Override
		public void collectUsedNames(NameUsage nameUsage, String defaultType) throws ModuleException {
			super.collectUsedNames(nameUsage, defaultType);
			sentenceFilter.collectUsedNames(nameUsage, defaultType);
		}
	}
	
	@Override
	protected GeniaTaggerResolvedObjects createResolvedObjects(ProcessingContext<Corpus> ctx) throws ResolverException {
		return new GeniaTaggerResolvedObjects(ctx, this);
	}

	@Override
	public void process(ProcessingContext<Corpus> ctx, Corpus corpus) throws ModuleException {
		try {
			GeniaTaggerExternal ext = prepare(ctx, corpus);
			callExternal(ctx, "genia", ext, geniaCharset, "genia-command.sh");
			collect(ctx, corpus, ext);
		}
		catch (IOException ioe) {
			rethrow(ioe);
		}
	}
	
	@TimeThis(task="prepare-data", category=TimerCategory.PREPARE_DATA)
	protected GeniaTaggerExternal prepare(ProcessingContext<Corpus> ctx, Corpus corpus) throws IOException {
		return new GeniaTaggerExternal(ctx, corpus, getTempDir(ctx));
	}

	@SuppressWarnings("static-method")
	@TimeThis(task="read-genia", category=TimerCategory.COLLECT_DATA)
	protected void collect(@SuppressWarnings("unused") ProcessingContext<Corpus> ctx, Corpus corpus, GeniaTaggerExternal ext) throws ProcessingException, IOException {
		ext.readOutput(corpus);
	}

	@Override
	protected String[] addLayersToSectionFilter() {
		return new String[] { sentenceLayerName, wordLayerName };
	}
	
	@Override
	protected String[] addFeaturesToSectionFilter() {
		return null;
	}
    
    private static final class AnnotationCollector implements Mapper<Section,Iterator<Annotation>> {
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

	private Iterator<Annotation> getSentenceIterator(EvaluationContext ctx, Corpus corpus, Evaluator resolvedDocumentFilter, Evaluator resolvedSectionFilter, Evaluator resolvedSentenceFilter) {
        return Iterators.flatten(Mappers.apply(new AnnotationCollector(ctx, sentenceLayerName, resolvedSentenceFilter), corpus.sectionIterator(ctx, resolvedDocumentFilter, resolvedSectionFilter)));
	}

	protected class GeniaTaggerExternal implements External<Corpus> {
		private final ProcessingContext<Corpus> ctx;
		private final EvaluationContext evalCtx;
		private final File script;
		private final OutputFile input;
		private final InputFile output;
		
		public GeniaTaggerExternal(ProcessingContext<Corpus> ctx, Corpus corpus, File tmpDir) throws IOException {
			super();
			this.ctx = ctx;
			this.evalCtx = new EvaluationContext(getLogger(ctx));
			
			script = new File(tmpDir, "genia.sh");
			// same ClassLoader as this class
			try (InputStream is = GeniaTagger.class.getResourceAsStream("genia.sh")) {
				Files.copy(is, script, 1024, true);
			}
			script.setExecutable(true);

			GeniaTaggerResolvedObjects resObj = getResolvedObjects();
			input = new OutputFile(tmpDir, "corpus.txt");
			TargetStream target = new FileTargetStream(geniaCharset, input);
			PrintStream ps = target.getPrintStream();
			for (Annotation sent : Iterators.loop(getSentenceIterator(evalCtx, corpus, resObj.getDocumentFilter(), resObj.getSectionFilter(), resObj.sentenceFilter)))
				writeSentence(sent, ps);
			ps.close();
			
			output = new InputFile(tmpDir, "corpus.genia");
		}
		
		private void writeSentence(Annotation sent, PrintStream ps) {
			Layer wordLayer = sent.getSection().getLayer(wordLayerName);
			StringBuilder sb = new StringBuilder();
			boolean notFirst = false;
			for (Annotation w : wordLayer.between(sent)) {
				if (notFirst)
					sb.append(' ');
				else
					notFirst = true;
				Strings.escapeWhitespaces(sb, w.getLastFeature(wordFormFeature));
			}
			sb.append(" .");
			ps.println(sb);
		}

		private void readOutput(Corpus corpus) throws ProcessingException, IOException {
			GeniaTaggerResolvedObjects resObj = getResolvedObjects();
			SourceStream source = new FileSourceStream(geniaCharset, output);
			BufferedReader r = source.getBufferedReader();
			for (Annotation sent : Iterators.loop(getSentenceIterator(evalCtx, corpus, resObj.getDocumentFilter(), resObj.getSectionFilter(), resObj.sentenceFilter)))
				readSentence(sent, r);
			if (r.readLine() != null)
				processingException("genia tagger output is too long");
			r.close();
		}

		private void readSentence(Annotation sent, BufferedReader r) throws ProcessingException, IOException {
			Layer wordLayer = sent.getSection().getLayer(wordLayerName);
			for (Annotation w : wordLayer.between(sent)) {
				String line = r.readLine();
				if (line == null)
					processingException("geniatagger output is short on lines");
				List<String> cols = Strings.split(line, '\t', -1);
				if (cols.size() != 5) {
					processingException("malformed genia tagger output: " + line);
				}
				String form = cols.get(0);
				String lemma = cols.get(1);
				String pos = cols.get(2);
				String chunk = cols.get(3);
				String entity = cols.get(4);
				w.addFeature(lemmaFeature, lemma);
				w.addFeature(posFeature, getPOS(form, pos, lemma).intern());
				w.addFeature(chunkFeature, chunk);
				w.addFeature(entityFeature, entity);
			}
			if (r.readLine() == null) // final dot
				processingException("geniatagger output is short on lines");
			if (r.readLine() == null) // geniatagger adds a blank line after each sentence
				processingException("geniatagger output is short on lines");
		}
		
		private String getPOS(@SuppressWarnings("unused") String form, String pos, String lemma) {
			if (!treeTaggerTagset) {
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
		public Module<Corpus> getOwner() {
			return GeniaTagger.this;
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
				"GENIA_DIR=" + geniaDir.getAbsolutePath(),
				"GENIA_BIN=" + geniaTaggerExecutable.getPath(),
				"GENIA_IN="  + input.getAbsolutePath(),
				"GENIA_OUT=" + output.getAbsolutePath()
			};
		}

		@Override
		public File getWorkingDirectory() throws ModuleException {
			return geniaDir;
		}

		@Override
		public void processOutput(BufferedReader out, BufferedReader err) throws ModuleException {
			try {
				Logger logger = getLogger(ctx);
				logger.fine("geniatagger standard error:");
				while (true) {
					String line = err.readLine();
					if (line == null)
						break;
					logger.fine("    " + line);
				}
				logger.fine("end of geniatagger standard error");
			}
			catch (FileNotFoundException fnfe) {
				rethrow(fnfe);
			}
			catch (IOException ioe) {
				rethrow(ioe);
			}
		}
	}

	@Param(nameType=NameType.LAYER, defaultDoc = "Name of the layer containing sentence annotations.")
	public String getSentenceLayerName() {
		return sentenceLayerName;
	}

	@Param(nameType=NameType.LAYER, defaultDoc = "Name of the layer containing word annotations.")
	public String getWordLayerName() {
		return wordLayerName;
	}

	@Param(nameType=NameType.FEATURE, defaultDoc = "Feature containing the word surface form.")
	public String getWordFormFeature() {
		return wordFormFeature;
	}

	@Param(nameType=NameType.FEATURE, defaultDoc = "Feature where to put the POS tag.")
	public String getPosFeature() {
		return posFeature;
	}

	@Param(nameType=NameType.FEATURE, defaultDoc = "Feature where to put the word lemma.")
	public String getLemmaFeature() {
		return lemmaFeature;
	}

	@Param(nameType=NameType.FEATURE, defaultDoc = "Feature where to put the chunk status.", mandatory = false)
	public String getChunkFeature() {
		return chunkFeature;
	}

	@Param(nameType=NameType.FEATURE, defaultDoc = "Feature where to put the entity status.", mandatory = false)
	public String getEntityFeature() {
		return entityFeature;
	}

	@Param(defaultDoc = "Directory where geniatagger is installed.")
	public File getGeniaDir() {
		return geniaDir;
	}

	@Param(defaultDoc = "Name of the geniatagger executable file.")
	public File getGeniaTaggerExecutable() {
		return geniaTaggerExecutable;
	}

	@Param(defaultDoc = "Character encoding of geniatagger input and output.")
	public String getGeniaCharset() {
		return geniaCharset;
	}

	@Param
	public Expression getSentenceFilter() {
		return sentenceFilter;
	}

	@Param
	public Boolean getTreeTaggerTagset() {
		return treeTaggerTagset;
	}

	public void setTreeTaggerTagset(Boolean treeTaggerTagset) {
		this.treeTaggerTagset = treeTaggerTagset;
	}

	public void setSentenceFilter(Expression sentenceFilter) {
		this.sentenceFilter = sentenceFilter;
	}

	public void setSentenceLayerName(String sentences) {
		this.sentenceLayerName = sentences;
	}

	public void setWordLayerName(String words) {
		this.wordLayerName = words;
	}

	public void setWordFormFeature(String wordForm) {
		this.wordFormFeature = wordForm;
	}

	public void setPosFeature(String pos) {
		this.posFeature = pos;
	}

	public void setLemmaFeature(String lemma) {
		this.lemmaFeature = lemma;
	}

	public void setChunkFeature(String chunk) {
		this.chunkFeature = chunk;
	}

	public void setEntityFeature(String entity) {
		this.entityFeature = entity;
	}

	public void setGeniaDir(File geniaDir) {
		this.geniaDir = geniaDir;
	}

	public void setGeniaTaggerExecutable(File geniaTaggerExecutable) {
		this.geniaTaggerExecutable = geniaTaggerExecutable;
	}

	public void setGeniaCharset(String geniaCharset) {
		this.geniaCharset = geniaCharset;
	}
}
