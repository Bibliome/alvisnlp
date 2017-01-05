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


package alvisnlp.module;

import java.util.logging.Logger;

public class CheckMandatoryParameters<A extends Annotable> extends AbstractParamVisitor<A,Logger> {
	private boolean hasUnsetMandatoryParam = false;
	
	@Override
	public void visitParam(ParamHandler<A> paramHandler, Logger logger) {
		if (paramHandler.isMandatory() && !paramHandler.isSet()) {
			logger.severe("unset mandatory parameter: " + paramHandler.getModule().getId() + " " + paramHandler.getName());
			hasUnsetMandatoryParam = true;
		}
	}
	
	public static <A extends Annotable> boolean visit(Logger logger, Module<A> module) throws ModuleException {
		CheckMandatoryParameters<A> result = new CheckMandatoryParameters<A>();
		module.accept(result, logger);
		return result.hasUnsetMandatoryParam;
	}
}
