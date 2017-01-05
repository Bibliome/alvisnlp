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

import java.util.logging.Logger;

import org.bibliome.alvisnlp.modules.SectionModule.SectionResolvedObjects;
import org.bibliome.util.Iterators;
import org.tartarus.snowball.SnowballProgram;
import org.tartarus.snowball.ext.EnglishStemmer;
import org.tartarus.snowball.ext.FrenchStemmer;

import alvisnlp.corpus.Annotation;
import alvisnlp.corpus.Corpus;
import alvisnlp.corpus.DefaultNames;
import alvisnlp.corpus.NameType;
import alvisnlp.corpus.Section;
import alvisnlp.corpus.expressions.EvaluationContext;
import alvisnlp.corpus.expressions.ResolverException;
import alvisnlp.module.ModuleException;
import alvisnlp.module.ProcessingContext;
import alvisnlp.module.ProcessingException;
import alvisnlp.module.lib.AlvisNLPModule;
import alvisnlp.module.lib.Param;

@AlvisNLPModule(beta=true)
public class PorterStemmer extends SectionModule<SectionResolvedObjects> {
	private String layerName = DefaultNames.getWordLayer();
	private String formFeature = Annotation.FORM_FEATURE_NAME;
	private String stemFeature = DefaultNames.getStemFeature();
	private String language = "english";
	
	@Override
	public void process(ProcessingContext<Corpus> ctx, Corpus corpus) throws ModuleException {
		Logger logger = getLogger(ctx);
		EvaluationContext evalCtx = new EvaluationContext(logger);
		SnowballProgram stemmer = getStemmer();
		for (Section sec : Iterators.loop(sectionIterator(evalCtx, corpus))) {
			if (!sec.hasLayer(layerName)) {
				continue;
			}
			for (Annotation a : sec.getLayer(layerName)) {
				String form = a.getLastFeature(formFeature);
				stemmer.setCurrent(form.toLowerCase());
				stemmer.stem();
				String stem = stemmer.getCurrent();
				a.addFeature(stemFeature, stem);
			}
		}
	}
	
	private SnowballProgram getStemmer() throws ProcessingException {
		switch (language.toLowerCase()) {
			case "en":
			case "english":
				return new EnglishStemmer();
			case "fr":
			case "french":
				return new FrenchStemmer();
		}
		processingException("unknown language " + language);
		return null;
	}
	
	@Override
	protected String[] addLayersToSectionFilter() {
		return new String[] { layerName };
	}
	
	@Override
	protected String[] addFeaturesToSectionFilter() {
		return null;
	}
	
	@Override
	protected SectionResolvedObjects createResolvedObjects(ProcessingContext<Corpus> ctx) throws ResolverException {
		return new SectionResolvedObjects(ctx, this);
	}

	@Param(nameType=NameType.LAYER)
	public String getLayerName() {
		return layerName;
	}

	@Param(nameType=NameType.FEATURE)
	public String getFormFeature() {
		return formFeature;
	}

	@Param(nameType=NameType.FEATURE)
	public String getStemFeature() {
		return stemFeature;
	}

	@Param
	public String getLanguage() {
		return language;
	}

	public void setLayerName(String layerName) {
		this.layerName = layerName;
	}

	public void setFormFeature(String formFeature) {
		this.formFeature = formFeature;
	}

	public void setStemFeature(String stemFeature) {
		this.stemFeature = stemFeature;
	}

	public void setLanguage(String language) {
		this.language = language;
	}
}
