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
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.DefaultNames;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.NameType;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.Evaluator;
import fr.inra.maiage.bibliome.alvisnlp.core.module.lib.Param;
import fr.inra.maiage.bibliome.util.files.ExecutableFile;

public abstract class AbstractWapiti<T extends SectionResolvedObjects> extends SectionModule<T> {
	private ExecutableFile wapitiExecutable;
	private String[] commandLineOptions;
	private String sentenceLayer = DefaultNames.getSentenceLayer();
	private String tokenLayer = DefaultNames.getWordLayer();
	
	@Override
	protected String[] addLayersToSectionFilter() {
		return new String[] { tokenLayer };
	}

	@Override
	protected String[] addFeaturesToSectionFilter() {
		return null;
	}
	
	protected abstract Evaluator[] getAttributeEvaluators();
	
	@Param
	public ExecutableFile getWapitiExecutable() {
		return wapitiExecutable;
	}

	@Param(mandatory=false)
	public String[] getCommandLineOptions() {
		return commandLineOptions;
	}

	@Deprecated
	@Param(mandatory=false, nameType=NameType.LAYER)
	public String getSentenceLayerName() {
		return sentenceLayer;
	}

	@Deprecated
	@Param(nameType=NameType.LAYER)
	public String getTokenLayerName() {
		return tokenLayer;
	}

	@Param(mandatory=false, nameType=NameType.LAYER)
	public String getSentenceLayer() {
		return sentenceLayer;
	}

	@Param(nameType=NameType.LAYER)
	public String getTokenLayer() {
		return tokenLayer;
	}

	public void setSentenceLayer(String sentenceLayer) {
		this.sentenceLayer = sentenceLayer;
	}

	public void setTokenLayer(String tokenLayer) {
		this.tokenLayer = tokenLayer;
	}

	public void setSentenceLayerName(String sentenceLayerName) {
		this.sentenceLayer = sentenceLayerName;
	}

	public void setTokenLayerName(String tokenLayerName) {
		this.tokenLayer = tokenLayerName;
	}

	public void setCommandLineOptions(String[] commandLineOptions) {
		this.commandLineOptions = commandLineOptions;
	}

	public void setWapitiExecutable(ExecutableFile wapitiExecutable) {
		this.wapitiExecutable = wapitiExecutable;
	}
}
