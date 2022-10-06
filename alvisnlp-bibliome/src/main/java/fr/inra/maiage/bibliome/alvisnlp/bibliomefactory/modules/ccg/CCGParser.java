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


package fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.ccg;

import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

import fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.ccg.CCGBase.CCGResolvedObjects;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Corpus;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.DefaultNames;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Layer;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.NameType;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.creators.TupleCreator;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.EvaluationContext;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.ResolverException;
import fr.inra.maiage.bibliome.alvisnlp.core.module.ModuleException;
import fr.inra.maiage.bibliome.alvisnlp.core.module.ProcessingContext;
import fr.inra.maiage.bibliome.alvisnlp.core.module.ProcessingException;
import fr.inra.maiage.bibliome.alvisnlp.core.module.lib.AlvisNLPModule;
import fr.inra.maiage.bibliome.alvisnlp.core.module.lib.Param;
import fr.inra.maiage.bibliome.util.files.ExecutableFile;
import fr.inra.maiage.bibliome.util.files.InputDirectory;
import fr.inra.maiage.bibliome.util.files.InputFile;

@AlvisNLPModule
public abstract class CCGParser extends CCGBase<CCGResolvedObjects> implements TupleCreator {
	private ExecutableFile executable;
	private InputDirectory parserModel;
	private InputDirectory superModel;
	private InputFile stanfordMarkedUpScript;
	private ExecutableFile stanfordScript;
	private Integer maxSuperCats = 500000;
	private String relationName = DefaultNames.getDependencyRelationName();
	private String labelFeature = DefaultNames.getDependencyLabelFeatureName();
	private String sentenceRole = DefaultNames.getDependencySentenceRole();
	private String headRole = DefaultNames.getDependencyHeadRole();
	private String dependentRole = DefaultNames.getDependencyDependentRole();
	private Boolean lpTransformation = false;
	private String supertagFeature = "supertag";

	@Override
	public void process(ProcessingContext<Corpus> ctx, Corpus corpus) throws ModuleException {
		try {
			Logger logger = getLogger(ctx);
			EvaluationContext evalCtx = new EvaluationContext(logger);
			List<List<Layer>> sentenceRuns = getSentences(logger, evalCtx, corpus);
			for (int run = 0; run < sentenceRuns.size(); ++run) {
				logger.info(String.format("run %d/%d", run+1, sentenceRuns.size())); 
				List<Layer> sentences = sentenceRuns.get(run);
				CCGParserExternalHandler ext = new CCGParserExternalHandler(ctx, this, corpus, run, sentences);
				try {
					ext.start(false);
				}
				catch (ModuleException e) {
					logger.severe(e.getMessage());
					logger.severe("we know sometimes CCG accidentally sentences");
					logger.severe("let's try to proceed anyway. No guarantee...");
					logger.severe("btw, input that caused the crash: " + ext.getCCGInputFile().getAbsolutePath());
				}
				ext.doCollect();
			}
		}
		catch (InterruptedException | IOException e) {
			throw new ProcessingException(e);
		}
	}

	@Override
	protected CCGResolvedObjects createResolvedObjects(ProcessingContext<Corpus> ctx) throws ResolverException {
		return new CCGResolvedObjects(ctx, this);
	}
	
	@Param
	public ExecutableFile getExecutable() {
		return executable;
	}

	@Param
	public InputDirectory getParserModel() {
		return parserModel;
	}

	@Param
	public InputDirectory getSuperModel() {
		return superModel;
	}

	@Param(mandatory=false)
	public InputFile getStanfordMarkedUpScript() {
		return stanfordMarkedUpScript;
	}

	@Param(mandatory=false)
	public ExecutableFile getStanfordScript() {
		return stanfordScript;
	}

	@Param
	public Integer getMaxSuperCats() {
		return maxSuperCats;
	}

	@Param(nameType=NameType.RELATION)
	public String getRelationName() {
		return relationName;
	}

	@Deprecated
	@Param(nameType=NameType.FEATURE)
	public String getLabelFeatureName() {
		return labelFeature;
	}

	@Param(nameType=NameType.ARGUMENT)
	public String getSentenceRole() {
		return sentenceRole;
	}

	@Param(nameType=NameType.ARGUMENT)
	public String getHeadRole() {
		return headRole;
	}

	@Param(nameType=NameType.ARGUMENT)
	public String getDependentRole() {
		return dependentRole;
	}

	@Param
	public Boolean getLpTransformation() {
		return lpTransformation;
	}

	@Deprecated
	@Param
	public String getSupertagFeatureName() {
		return supertagFeature;
	}

	@Param(nameType = NameType.FEATURE)
	public String getLabelFeature() {
		return labelFeature;
	}

	@Param(nameType = NameType.FEATURE)
	public String getSupertagFeature() {
		return supertagFeature;
	}

	public void setLabelFeature(String labelFeature) {
		this.labelFeature = labelFeature;
	}

	public void setSupertagFeature(String supertagFeature) {
		this.supertagFeature = supertagFeature;
	}

	public void setSupertagFeatureName(String supertagFeatureName) {
		this.supertagFeature = supertagFeatureName;
	}

	public void setLpTransformation(Boolean lpTransformation) {
		this.lpTransformation = lpTransformation;
	}

	public void setExecutable(ExecutableFile executable) {
		this.executable = executable;
	}

	public void setParserModel(InputDirectory parserModel) {
		this.parserModel = parserModel;
	}

	public void setSuperModel(InputDirectory superModel) {
		this.superModel = superModel;
	}

	public void setStanfordMarkedUpScript(InputFile stanfordMarkedUpScript) {
		this.stanfordMarkedUpScript = stanfordMarkedUpScript;
	}

	public void setStanfordScript(ExecutableFile stanfordScript) {
		this.stanfordScript = stanfordScript;
	}

	public void setMaxSuperCats(Integer maxSuperCats) {
		this.maxSuperCats = maxSuperCats;
	}

	public void setRelationName(String relationName) {
		this.relationName = relationName;
	}

	public void setLabelFeatureName(String labelFeatureName) {
		this.labelFeature = labelFeatureName;
	}

	public void setSentenceRole(String sentenceRole) {
		this.sentenceRole = sentenceRole;
	}

	public void setHeadRole(String headRole) {
		this.headRole = headRole;
	}

	public void setDependentRole(String modifierRole) {
		this.dependentRole = modifierRole;
	}
}
