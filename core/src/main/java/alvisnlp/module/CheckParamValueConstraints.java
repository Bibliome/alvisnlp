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

import org.bibliome.util.Checkable;

public class CheckParamValueConstraints<A extends Annotable> extends AbstractParamVisitor<A,Logger> {
	private final ProcessingContext<A> processingContext;
	private boolean hasConstraintsErrors = false;

	private CheckParamValueConstraints(ProcessingContext<A> processingContext) {
		super();
		this.processingContext = processingContext;
	}

	@Override
	public void visitModule(Module<A> module, Logger logger) throws ModuleException {
		if (processingContext != null)
			logger = module.getLogger(processingContext);
		for (ParamHandler<A> paramHandler : module.getAllParamHandlers())
			paramHandler.accept(this, logger);
	}

	@Override
	public void visitParam(ParamHandler<A> paramHandler, Logger logger) {
		if (!paramHandler.isSet())
			return;
		if (paramHandler.isInhibitCheck())
			return;
		Class<?> type = paramHandler.getType();
		if (isCheckable(type)) {
			checkCheckable(logger, paramHandler.getValue());
		}
		else if (type.isArray()) {
			if (isCheckable(type.getComponentType())) {
				for (Object value : (Object[]) paramHandler.getValue())
					checkCheckable(logger, value);
			}
		}
	}
	
	private static boolean isCheckable(Class<?> type) {
		return Checkable.class.isAssignableFrom(type);
	}
	
	private void checkCheckable(Logger logger, Object value) {
		Checkable checkable = (Checkable) value;
		hasConstraintsErrors = !checkable.check(logger) || hasConstraintsErrors;
	}
	
	public static final <A extends Annotable> boolean visit(ProcessingContext<A> processingContext, Logger logger, Module<A> module) throws ModuleException {
		CheckParamValueConstraints<A> visitor = new CheckParamValueConstraints<A>(processingContext);
		module.accept(visitor, logger);
		return visitor.hasConstraintsErrors;
	}
}
