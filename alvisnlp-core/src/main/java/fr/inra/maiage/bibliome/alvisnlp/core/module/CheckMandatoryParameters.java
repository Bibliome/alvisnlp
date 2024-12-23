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


package fr.inra.maiage.bibliome.alvisnlp.core.module;

import java.util.logging.Logger;

public class CheckMandatoryParameters extends AbstractParamVisitor<Logger> {
	private boolean hasUnsetMandatoryParam = false;
	
	public CheckMandatoryParameters() {
		super(true);
	}

	@Override
	public void visitParam(ParamHandler paramHandler, Logger logger) {
		if ((!paramHandler.isDeprecated()) && paramHandler.isMandatory() && (!paramHandler.isSet())) {
			Module module = paramHandler.getModule();
			String moduleClass = module.getModuleClass();
			logger.severe("unset mandatory parameter: " + module.getPath() + " (" + moduleClass.substring(moduleClass.lastIndexOf('.')) + ") " + paramHandler.getName());
			hasUnsetMandatoryParam = true;
		}
	}
	
	public static boolean visit(Logger logger, Module module) throws ModuleException {
		CheckMandatoryParameters result = new CheckMandatoryParameters();
		module.accept(result, logger);
		return result.hasUnsetMandatoryParam;
	}
}
