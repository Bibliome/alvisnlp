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

import java.io.File;
import java.util.logging.Logger;

import fr.inra.maiage.bibliome.util.Checkable;
import fr.inra.maiage.bibliome.util.files.InputDirectory;
import fr.inra.maiage.bibliome.util.files.InputFile;
import fr.inra.maiage.bibliome.util.streams.DirectorySourceStream;
import fr.inra.maiage.bibliome.util.streams.FileSourceStream;

public class CheckParamValueConstraints<A extends Annotable> extends AbstractParamVisitor<A,Logger> {
	private final ProcessingContext<A> processingContext;
	private boolean hasConstraintsErrors = false;

	private CheckParamValueConstraints(ProcessingContext<A> processingContext) {
		super(true);
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
		if (paramHandler.isOutputFeed()) {
			checkOutputFeed(paramHandler, logger);
			return;
		}
		Class<?> type = paramHandler.getType();
		if (isCheckable(type)) {
			checkCheckable(logger, paramHandler.getValue());
			return;
		}
		if (type.isArray()) {
			if (isCheckable(type.getComponentType())) {
				for (Object value : (Object[]) paramHandler.getValue())
					checkCheckable(logger, value);
			}
		}
	}
	
	private void checkOutputFeed(ParamHandler<A> paramHandler, Logger logger) {
		Object value = paramHandler.getValue();
		Class<?> type = value.getClass();
		if (InputFile.class.isAssignableFrom(type)) {
			if (((InputFile) value).exists()) {
				logger.severe("parameter " + paramHandler.getName() + " declared as output-feed but file " + value + " exists");
				hasConstraintsErrors = true;
			}
			return;
		}
		if (InputDirectory.class.isAssignableFrom(type)) {
			if (((InputDirectory) value).exists()) {
				logger.severe("parameter " + paramHandler.getName() + " declared as output-feed but directory " + value + " exists");
				hasConstraintsErrors = true;
			}
			return;
		}
		if (FileSourceStream.class.isAssignableFrom(type)) {
			FileSourceStream fss = (FileSourceStream) value;
			for (String name : fss.getStreamNames()) {
				if (new File(name).exists()) {
					logger.severe("parameter " + paramHandler.getName() + " declared as output-feed but file " + name + " exists");
					hasConstraintsErrors = true;
				}
			}
			return;
		}
		if (DirectorySourceStream.class.isAssignableFrom(type)) {
			DirectorySourceStream fss = (DirectorySourceStream) value;
			for (String name : fss.getStreamNames()) {
				if (new File(name).exists()) {
					logger.severe("parameter " + paramHandler.getName() + " declared as output-feed but file " + name + " exists");
					hasConstraintsErrors = true;
				}
			}
			return;
		}
		logger.warning("parameter " + paramHandler.getName() + " declared as output-feed but the type is not appropriate");
		hasConstraintsErrors = true;
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
