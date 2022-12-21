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


package fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.wapiti;

import java.io.IOException;

import fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.SectionModule.SectionResolvedObjects;
import fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.wapiti.WapitiLabel.WapitiLabelResolvedObjects;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Corpus;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.NameType;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.Evaluator;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.Expression;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.ResolverException;
import fr.inra.maiage.bibliome.alvisnlp.core.module.ModuleException;
import fr.inra.maiage.bibliome.alvisnlp.core.module.NameUsage;
import fr.inra.maiage.bibliome.alvisnlp.core.module.ProcessingContext;
import fr.inra.maiage.bibliome.alvisnlp.core.module.ProcessingException;
import fr.inra.maiage.bibliome.alvisnlp.core.module.lib.AlvisNLPModule;
import fr.inra.maiage.bibliome.alvisnlp.core.module.lib.Param;
import fr.inra.maiage.bibliome.util.files.InputFile;

@AlvisNLPModule
public class WapitiLabel extends AbstractWapiti<WapitiLabelResolvedObjects> {
	private String labelFeature;
	private InputFile modelFile;
	private Expression[] attributes;

	@Override
	public void process(ProcessingContext<Corpus> ctx, Corpus corpus) throws ModuleException {
		try {
			new WapitiLabelExternalHandler(ctx, this, corpus).start();
		}
		catch (IOException | InterruptedException e) {
			throw new ProcessingException(e);
		}
	}

	protected static class WapitiLabelResolvedObjects extends SectionResolvedObjects {
		private final Evaluator[] attributes;
		
		public WapitiLabelResolvedObjects(ProcessingContext<Corpus> ctx, WapitiLabel module) throws ResolverException {
			super(ctx, module);
			this.attributes = rootResolver.resolveArray(module.attributes, Evaluator.class);
		}

		@Override
		public void collectUsedNames(NameUsage nameUsage, String defaultType) throws ModuleException {
			super.collectUsedNames(nameUsage, defaultType);
			nameUsage.collectUsedNamesArray(attributes, defaultType);
		}
	}

	@Override
	protected WapitiLabelResolvedObjects createResolvedObjects(ProcessingContext<Corpus> ctx) throws ResolverException {
		return new WapitiLabelResolvedObjects(ctx, this);
	}

	@Override
	protected Evaluator[] getAttributeEvaluators() {
		WapitiLabelResolvedObjects resObj = getResolvedObjects();
		return resObj.attributes;
	}

	@Param(nameType=NameType.FEATURE)
	public String getLabelFeature() {
		return labelFeature;
	}

	@Param
	public InputFile getModelFile() {
		return modelFile;
	}

	@Param(nameType=NameType.FEATURE)
	public Expression[] getAttributes() {
		return attributes;
	}

	public void setAttributes(Expression[] attributes) {
		this.attributes = attributes;
	}

	public void setLabelFeature(String labelFeature) {
		this.labelFeature = labelFeature;
	}

	public void setModelFile(InputFile modelFile) {
		this.modelFile = modelFile;
	}
}
