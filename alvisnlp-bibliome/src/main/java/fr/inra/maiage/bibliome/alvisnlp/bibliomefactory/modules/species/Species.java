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


package fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.species;

import java.io.IOException;

import fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.SectionModule;
import fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.SectionModule.SectionResolvedObjects;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Corpus;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.NameType;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.creators.AnnotationCreator;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.ResolverException;
import fr.inra.maiage.bibliome.alvisnlp.core.module.ModuleException;
import fr.inra.maiage.bibliome.alvisnlp.core.module.ProcessingContext;
import fr.inra.maiage.bibliome.alvisnlp.core.module.ProcessingException;
import fr.inra.maiage.bibliome.alvisnlp.core.module.lib.AlvisNLPModule;
import fr.inra.maiage.bibliome.alvisnlp.core.module.lib.Param;
import fr.inra.maiage.bibliome.util.files.InputDirectory;

@AlvisNLPModule
public abstract class Species extends SectionModule<SectionResolvedObjects> implements AnnotationCreator {
	private InputDirectory speciesDir;
	private String targetLayer;
	private String taxidFeature;

	@Override
	protected SectionResolvedObjects createResolvedObjects(ProcessingContext<Corpus> ctx) throws ResolverException {
		return new SectionResolvedObjects(ctx, this);
	}

	@Override
	public void process(ProcessingContext<Corpus> ctx, Corpus corpus) throws ModuleException {
		try {
			new SpeciesExternalHandler(ctx, this, corpus).start();
		}
		catch (InterruptedException | IOException e) {
			throw new ProcessingException(e);
		}
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
	public String getTargetLayer() {
	    return this.targetLayer;
	};

	public void setTargetLayer(String targetLayer) {
	    this.targetLayer = targetLayer;
	};

	@Deprecated
	@Param(nameType=NameType.LAYER)
	public String getTargetLayerName() {
		return targetLayer;
	}

	@Param(nameType=NameType.FEATURE, mandatory=false)
	public String getTaxidFeature() {
		return taxidFeature;
	}

	public void setSpeciesDir(InputDirectory speciesDir) {
		this.speciesDir = speciesDir;
	}

	public void setTargetLayerName(String targetLayer) {
		this.targetLayer = targetLayer;
	}

	public void setTaxidFeature(String taxidFeature) {
		this.taxidFeature = taxidFeature;
	}
}
