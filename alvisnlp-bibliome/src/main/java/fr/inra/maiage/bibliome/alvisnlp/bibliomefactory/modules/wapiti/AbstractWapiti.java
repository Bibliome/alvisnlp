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

import fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.SectionModule;
import fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.SectionModule.SectionResolvedObjects;
import fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.wapiti.AbstractWapiti.WapitiResolvedObjects;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Corpus;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.DefaultNames;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.NameType;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.Evaluator;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.Expression;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.ResolverException;
import fr.inra.maiage.bibliome.alvisnlp.core.module.ModuleException;
import fr.inra.maiage.bibliome.alvisnlp.core.module.NameUsage;
import fr.inra.maiage.bibliome.alvisnlp.core.module.ProcessingContext;
import fr.inra.maiage.bibliome.alvisnlp.core.module.lib.Param;
import fr.inra.maiage.bibliome.util.files.ExecutableFile;

public abstract class AbstractWapiti extends SectionModule<WapitiResolvedObjects> {
	private ExecutableFile wapitiExecutable;
	private String[] commandLineOptions;
	private String sentenceLayerName = DefaultNames.getSentenceLayer();
	private String tokenLayerName = DefaultNames.getWordLayer();
	private Expression[] features;
	
	@Override
	protected String[] addLayersToSectionFilter() {
		return new String[] { tokenLayerName };
	}

	@Override
	protected String[] addFeaturesToSectionFilter() {
		return null;
	}
	
	@Param
	public ExecutableFile getWapitiExecutable() {
		return wapitiExecutable;
	}

	@Param(nameType=NameType.FEATURE)
	public Expression[] getFeatures() {
		return features;
	}

	@Param(mandatory=false)
	public String[] getCommandLineOptions() {
		return commandLineOptions;
	}

	@Param(mandatory=false, nameType=NameType.LAYER)
	public String getSentenceLayerName() {
		return sentenceLayerName;
	}

	@Param(nameType=NameType.LAYER)
	public String getTokenLayerName() {
		return tokenLayerName;
	}

	public void setSentenceLayerName(String sentenceLayerName) {
		this.sentenceLayerName = sentenceLayerName;
	}

	public void setTokenLayerName(String tokenLayerName) {
		this.tokenLayerName = tokenLayerName;
	}

	public void setCommandLineOptions(String[] commandLineOptions) {
		this.commandLineOptions = commandLineOptions;
	}

	public void setWapitiExecutable(ExecutableFile wapitiExecutable) {
		this.wapitiExecutable = wapitiExecutable;
	}

	public void setFeatures(Expression[] features) {
		this.features = features;
	}

	protected static class WapitiResolvedObjects extends SectionResolvedObjects {
		private final Evaluator[] features;
		
		public WapitiResolvedObjects(ProcessingContext<Corpus> ctx, AbstractWapiti module) throws ResolverException {
			super(ctx, module);
			this.features = rootResolver.resolveArray(module.features, Evaluator.class);
		}

		@Override
		public void collectUsedNames(NameUsage nameUsage, String defaultType) throws ModuleException {
			super.collectUsedNames(nameUsage, defaultType);
			nameUsage.collectUsedNamesArray(getFeatures(), defaultType);
		}

		public Evaluator[] getFeatures() {
			return features;
		}
	}
}
