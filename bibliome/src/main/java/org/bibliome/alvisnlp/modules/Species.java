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


package org.bibliome.alvisnlp.modules;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.bibliome.alvisnlp.modules.SectionModule.SectionResolvedObjects;
import org.bibliome.util.Iterators;
import org.bibliome.util.filelines.FileLines;
import org.bibliome.util.filelines.InvalidFileLineEntry;
import org.bibliome.util.filelines.TabularFormat;
import org.bibliome.util.files.InputDirectory;
import org.bibliome.util.files.OutputFile;
import org.bibliome.util.streams.FileTargetStream;
import org.bibliome.util.streams.TargetStream;

import alvisnlp.corpus.Annotation;
import alvisnlp.corpus.Corpus;
import alvisnlp.corpus.Layer;
import alvisnlp.corpus.NameType;
import alvisnlp.corpus.Section;
import alvisnlp.corpus.creators.AnnotationCreator;
import alvisnlp.corpus.expressions.EvaluationContext;
import alvisnlp.corpus.expressions.ResolverException;
import alvisnlp.module.Module;
import alvisnlp.module.ModuleException;
import alvisnlp.module.ProcessingContext;
import alvisnlp.module.TimerCategory;
import alvisnlp.module.lib.AlvisNLPModule;
import alvisnlp.module.lib.External;
import alvisnlp.module.lib.Param;
import alvisnlp.module.lib.TimeThis;

@AlvisNLPModule(beta=true)
public abstract class Species extends SectionModule<SectionResolvedObjects> implements AnnotationCreator {
	private InputDirectory speciesDir;
	private String targetLayerName;
	private String taxidFeature;
	
	@Override
	protected SectionResolvedObjects createResolvedObjects(ProcessingContext<Corpus> ctx) throws ResolverException {
		return new SectionResolvedObjects(ctx, this);
	}

	@Override
	public void process(ProcessingContext<Corpus> ctx, Corpus corpus) throws ModuleException {
		Logger logger = getLogger(ctx);
		EvaluationContext evalCtx = new EvaluationContext(logger);
		File tmpDir = getTempDir(ctx);
		Map<String,Section> sectionMap = writeSpeciesInput(ctx, evalCtx, corpus, tmpDir);
		SpeciesExternal external = new SpeciesExternal(logger, new InputDirectory(tmpDir.getAbsolutePath()), sectionMap);
		callExternal(ctx, "run-species", external, "ISO-8859-1", "species.sh");
	}
	
	@TimeThis(task="write-species-input", category=TimerCategory.PREPARE_DATA)
	protected Map<String,Section> writeSpeciesInput(@SuppressWarnings("unused") ProcessingContext<Corpus> ctx, EvaluationContext evalCtx, Corpus corpus, File tmpDir) throws ModuleException {
		Map<String,Section> result = new HashMap<String,Section>();
		try {
			for (Section sec : Iterators.loop(sectionIterator(evalCtx, corpus))) {
				int n = result.size();
				String fileName = Integer.toHexString(n) + ".txt";
				result.put(fileName, sec);
				TargetStream target = new FileTargetStream("ISO-8859-1", new OutputFile(tmpDir, fileName));
				PrintStream ps = target.getPrintStream();
				String contents = sec.getContents().replace('\n', ' ');
				ps.print(contents);
				ps.close();
			}
		}
		catch (IOException e) {
			rethrow(e);
		}
		return result;
	}

	@Override
	protected String[] addLayersToSectionFilter() {
		return null;
	}

	@Override
	protected String[] addFeaturesToSectionFilter() {
		return null;
	}
		
	private final class SpeciesExternal extends FileLines<Map<String,Section>> implements External<Corpus> {
		private final InputDirectory corpusDir;
		private final Map<String,Section> sectionMap;
		
		private SpeciesExternal(Logger logger, InputDirectory corpusDir, Map<String,Section> sectionMap) {
			super(logger);
			TabularFormat format = getFormat();
			format.setMinColumns(4);
			format.setMaxColumns(5);
			format.setStrictColumnNumber(true);
			this.corpusDir = corpusDir;
			this.sectionMap = sectionMap;
		}

		@Override
		public void processEntry(Map<String, Section> data, int lineno, List<String> entry) throws InvalidFileLineEntry {
			String sourceFile = entry.get(0);
			Section sec = data.get(sourceFile);
			if (sec == null) {
				getLogger().warning("could not make sense of: " + sourceFile);
				return;
			}
			Layer layer = sec.ensureLayer(targetLayerName);
			int start = Integer.parseInt(entry.get(1));
			int end = Integer.parseInt(entry.get(2)) + 1;
			Annotation a = new Annotation(Species.this, layer, start, end);
			if (taxidFeature != null && entry.size() == 5) {
				String taxid = entry.get(4);
				a.addFeature(taxidFeature, taxid);
			}
		}

		@Override
		public Module<Corpus> getOwner() {
			return Species.this;
		}

		@Override
		public String[] getCommandLineArgs() throws ModuleException {
			return new String[] {
					"./species",
					corpusDir.getAbsolutePath()
			};
		}

		@Override
		public String[] getEnvironment() throws ModuleException {
			return null;
		}

		@Override
		public File getWorkingDirectory() throws ModuleException {
			return speciesDir;
		}

		@Override
		public void processOutput(BufferedReader out, BufferedReader err) throws ModuleException {
			try {
				process(out, sectionMap);
				Logger logger = getLogger();
				while (true) {
					String line = err.readLine();
					if (line == null)
						break;
					logger.info("    " + line);
				}
			}
			catch (IOException|InvalidFileLineEntry e) {
				rethrow(e);
			}
		}
	}

	@Param
	public InputDirectory getSpeciesDir() {
		return speciesDir;
	}

	@Param(nameType=NameType.LAYER)
	public String getTargetLayerName() {
		return targetLayerName;
	}

	@Param(nameType=NameType.FEATURE, mandatory=false)
	public String getTaxidFeature() {
		return taxidFeature;
	}

	public void setSpeciesDir(InputDirectory speciesDir) {
		this.speciesDir = speciesDir;
	}

	public void setTargetLayerName(String targetLayerName) {
		this.targetLayerName = targetLayerName;
	}

	public void setTaxidFeature(String taxidFeature) {
		this.taxidFeature = taxidFeature;
	}
	
	
}
