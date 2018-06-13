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


package fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Logger;

import fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.SectionModule.SectionResolvedObjects;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Corpus;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.NameType;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Section;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.creators.AnnotationCreator;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.EvaluationContext;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.ResolverException;
import fr.inra.maiage.bibliome.alvisnlp.core.module.ModuleException;
import fr.inra.maiage.bibliome.alvisnlp.core.module.ProcessingContext;
import fr.inra.maiage.bibliome.alvisnlp.core.module.TimerCategory;
import fr.inra.maiage.bibliome.alvisnlp.core.module.lib.AlvisNLPModule;
import fr.inra.maiage.bibliome.alvisnlp.core.module.lib.Param;
import fr.inra.maiage.bibliome.alvisnlp.core.module.lib.TimeThis;
import fr.inra.maiage.bibliome.util.Iterators;
import fr.inra.maiage.bibliome.util.files.InputDirectory;
import fr.inra.maiage.bibliome.util.files.OutputFile;
import fr.inra.maiage.bibliome.util.streams.FileTargetStream;
import fr.inra.maiage.bibliome.util.streams.TargetStream;

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
		SpeciesExternal external = new SpeciesExternal(this, logger, new InputDirectory(tmpDir.getAbsolutePath()), sectionMap);
		callExternal(ctx, "run-species", external, "ISO-8859-1", "species.sh");
	}
	
	@TimeThis(task="write-species-input", category=TimerCategory.PREPARE_DATA)
	protected Map<String,Section> writeSpeciesInput(@SuppressWarnings("unused") ProcessingContext<Corpus> ctx, EvaluationContext evalCtx, Corpus corpus, File tmpDir) throws ModuleException {
		Map<String,Section> result = new LinkedHashMap<String,Section>();
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
