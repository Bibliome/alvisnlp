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

import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Corpus;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.NameType;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.ResolverException;
import fr.inra.maiage.bibliome.alvisnlp.core.module.ModuleException;
import fr.inra.maiage.bibliome.alvisnlp.core.module.ProcessingContext;
import fr.inra.maiage.bibliome.alvisnlp.core.module.ProcessingException;
import fr.inra.maiage.bibliome.alvisnlp.core.module.lib.AlvisNLPModule;
import fr.inra.maiage.bibliome.alvisnlp.core.module.lib.Param;
import fr.inra.maiage.bibliome.util.files.InputFile;

@AlvisNLPModule
public class WapitiLabel extends AbstractWapiti {
	private String labelFeature;
	private InputFile modelFile;

	@Override
	public void process(ProcessingContext<Corpus> ctx, Corpus corpus) throws ModuleException {
		try {
			new WapitiLabelExternalHandler(ctx, this, corpus).start();
		}
		catch (IOException | InterruptedException e) {
			throw new ProcessingException(e);
		}
	}

	@Override
	protected WapitiResolvedObjects createResolvedObjects(ProcessingContext<Corpus> ctx) throws ResolverException {
		return new WapitiResolvedObjects(ctx, this);
	}

	@Param(nameType=NameType.FEATURE)
	public String getLabelFeature() {
		return labelFeature;
	}

	@Param
	public InputFile getModelFile() {
		return modelFile;
	}

	public void setLabelFeature(String labelFeature) {
		this.labelFeature = labelFeature;
	}

	public void setModelFile(InputFile modelFile) {
		this.modelFile = modelFile;
	}
}
