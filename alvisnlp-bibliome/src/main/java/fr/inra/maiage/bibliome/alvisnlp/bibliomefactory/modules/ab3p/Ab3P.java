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


package fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.ab3p;

import java.io.IOException;

import fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.SectionModule;
import fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.SectionModule.SectionResolvedObjects;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Corpus;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.NameType;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.creators.AnnotationCreator;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.creators.TupleCreator;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.ResolverException;
import fr.inra.maiage.bibliome.alvisnlp.core.module.ModuleException;
import fr.inra.maiage.bibliome.alvisnlp.core.module.ProcessingContext;
import fr.inra.maiage.bibliome.alvisnlp.core.module.ProcessingException;
import fr.inra.maiage.bibliome.alvisnlp.core.module.lib.AlvisNLPModule;
import fr.inra.maiage.bibliome.alvisnlp.core.module.lib.Param;
import fr.inra.maiage.bibliome.util.files.InputDirectory;

@AlvisNLPModule
public abstract class Ab3P extends SectionModule<SectionResolvedObjects> implements AnnotationCreator, TupleCreator {
	private InputDirectory installDir;
	private String shortFormsLayer = "short-forms";
	private String longFormsLayer = "long-forms";
	private String abbreviationRelation = "abbreviations";
	private String shortFormRole = "short-form";
	private String longFormRole = "long-form";
	private String longFormFeature = "long-form";

	@Override
	public void process(ProcessingContext ctx, Corpus corpus) throws ModuleException {
		try {
			new Ab3PExternalHandler(ctx, this, corpus).start();
		}
		catch (IOException | InterruptedException e) {
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


	@Override
	protected SectionResolvedObjects createResolvedObjects(ProcessingContext ctx) throws ResolverException {
		return new SectionResolvedObjects(ctx, this);
	}

	@Param
	public InputDirectory getInstallDir() {
		return installDir;
	}

	@Param(nameType=NameType.LAYER)
	public String getShortFormsLayer() {
	    return this.shortFormsLayer;
	};

	public void setShortFormsLayer(String shortFormsLayer) {
	    this.shortFormsLayer = shortFormsLayer;
	};

	@Deprecated
	@Param(nameType=NameType.LAYER)
	public String getShortFormsLayerName() {
		return shortFormsLayer;
	}

	@Param(nameType=NameType.LAYER)
	public String getLongFormsLayer() {
	    return this.longFormsLayer;
	};

	public void setLongFormsLayer(String longFormsLayer) {
	    this.longFormsLayer = longFormsLayer;
	};

	@Deprecated
	@Param(nameType=NameType.LAYER)
	public String getLongFormsLayerName() {
		return longFormsLayer;
	}

	@Deprecated
	@Param(nameType=NameType.RELATION)
	public String getRelationName() {
		return abbreviationRelation;
	}

	@Param(nameType=NameType.ARGUMENT)
	public String getShortFormRole() {
		return shortFormRole;
	}

	@Param(nameType=NameType.ARGUMENT)
	public String getLongFormRole() {
		return longFormRole;
	}

	@Param(nameType=NameType.FEATURE)
	public String getLongFormFeature() {
		return longFormFeature;
	}

	@Param(nameType=NameType.RELATION)
	public String getAbbreviationRelation() {
		return abbreviationRelation;
	}

	public void setAbbreviationRelation(String abbreviationRelation) {
		this.abbreviationRelation = abbreviationRelation;
	}

	public void setLongFormFeature(String longFormFeature) {
		this.longFormFeature = longFormFeature;
	}

	public void setInstallDir(InputDirectory installDir) {
		this.installDir = installDir;
	}

	public void setShortFormsLayerName(String shortFormsLayer) {
		this.shortFormsLayer = shortFormsLayer;
	}

	public void setLongFormsLayerName(String longFormsLayer) {
		this.longFormsLayer = longFormsLayer;
	}

	public void setRelationName(String relationName) {
		this.abbreviationRelation = relationName;
	}

	public void setShortFormRole(String shortFormRole) {
		this.shortFormRole = shortFormRole;
	}

	public void setLongFormRole(String longFormRole) {
		this.longFormRole = longFormRole;
	}
}
