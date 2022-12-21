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
import fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.wapiti.WapitiTrain.WapitiTrainResolvedObjects;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Corpus;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.Evaluator;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.Expression;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.ResolverException;
import fr.inra.maiage.bibliome.alvisnlp.core.module.ModuleException;
import fr.inra.maiage.bibliome.alvisnlp.core.module.ProcessingContext;
import fr.inra.maiage.bibliome.alvisnlp.core.module.ProcessingException;
import fr.inra.maiage.bibliome.alvisnlp.core.module.lib.AlvisNLPModule;
import fr.inra.maiage.bibliome.alvisnlp.core.module.lib.Param;
import fr.inra.maiage.bibliome.util.files.OutputFile;

@AlvisNLPModule
public class WapitiTrain extends AbstractWapiti<WapitiTrainResolvedObjects> {
	private OutputFile modelFile;
	private String modelType;
	private String trainAlgorithm;
	private WapitiAttribute[] attributes;
	private Expression targetClass;

	@Override
	public void process(ProcessingContext<Corpus> ctx, Corpus corpus) throws ModuleException {
		try {
			new WapitiTrainExternalHandler(ctx, this, corpus).start();
		}
		catch (IOException | InterruptedException e) {
			throw new ProcessingException(e);
		}
	}

	@Override
	protected WapitiTrainResolvedObjects createResolvedObjects(ProcessingContext<Corpus> ctx) throws ResolverException {
		return new WapitiTrainResolvedObjects(ctx, this);
	}
	
	@Override
	protected Evaluator[] getAttributeEvaluators() {
		WapitiTrainResolvedObjects resObj = getResolvedObjects();
		Evaluator[] result = new Evaluator[resObj.attributes.length + 1];
		for (int i = 0; i < resObj.attributes.length; ++i) {
			result[i] = resObj.attributes[i].getValue();
		}
		result[result.length - 1] = resObj.targetClass;
		return result;
	}

	protected static class WapitiTrainResolvedObjects extends SectionResolvedObjects {
		private final WapitiAttribute.Resolved[] attributes;
		private final Evaluator targetClass;
		
		protected WapitiTrainResolvedObjects(ProcessingContext<Corpus> ctx,	WapitiTrain module) throws ResolverException {
			super(ctx, module);
			this.attributes = rootResolver.resolveArray(module.attributes, WapitiAttribute.Resolved.class);
			this.targetClass = module.targetClass.resolveExpressions(rootResolver);
		}

		protected WapitiAttribute.Resolved[] getAttributes() {
			return attributes;
		}
	}

	@Param
	public OutputFile getModelFile() {
		return modelFile;
	}

	@Param(mandatory=false)
	public String getModelType() {
		return modelType;
	}

	@Param(mandatory=false)
	public String getTrainAlgorithm() {
		return trainAlgorithm;
	}

	@Param
	public WapitiAttribute[] getAttributes() {
		return attributes;
	}

	@Param
	public Expression getTargetClass() {
		return targetClass;
	}

	public void setAttributes(WapitiAttribute[] attributes) {
		this.attributes = attributes;
	}

	public void setTargetClass(Expression targetClass) {
		this.targetClass = targetClass;
	}

	public void setModelFile(OutputFile modelFile) {
		this.modelFile = modelFile;
	}

	public void setModelType(String modelType) {
		this.modelType = modelType;
	}

	public void setTrainAlgorithm(String trainAlgorithm) {
		this.trainAlgorithm = trainAlgorithm;
	}
}
