package fr.inra.maiage.bibliome.alvisnlp.core.module;

import java.io.File;
import java.util.logging.Logger;

import fr.inra.maiage.bibliome.util.streams.SourceStream;
import fr.inra.maiage.bibliome.util.streams.TargetStream;

public class CollectResources<A extends Annotable> extends AbstractParamVisitor<A,Logger> {
	private final ProcessingContext<A> processingContext;
	private final String defaultSource;

	private CollectResources(ProcessingContext<A> processingContext, String defaultSource) {
		super(true);
		this.processingContext = processingContext;
		this.defaultSource = defaultSource;
	}

	@Override
	public void visitParam(ParamHandler<A> paramHandler, Logger logger) throws ModuleException {
		Object value = paramHandler.getValue();
		if (value == null) {
			return;
		}
		Class<?> type = paramHandler.getType();
		if (File.class.isAssignableFrom(type)) {
			log(logger, paramHandler, ((File) value).getAbsolutePath());
			return;
		}
		if (SourceStream.class.isAssignableFrom(type)) {
			log(logger, paramHandler, ((SourceStream) value).toString());
			return;
		}
		if (TargetStream.class.isAssignableFrom(type)) {
			log(logger, paramHandler, ((TargetStream) value).toString());
			return;
		}
	}
	
	private void log(Logger logger, ParamHandler<?> paramHandler, String value) {
		String msg = String.format("R: %s = %s", paramHandler.getName(), value);
		String src = paramHandler.getParamSourceName();
		if ((defaultSource != null) && !defaultSource.equals(src)) {
			msg += String.format(" (%s)", src);
		}
		logger.fine(msg);
	}

	@Override
	public void visitModule(Module<A> module, Logger logger) throws ModuleException {
		if (processingContext != null) {
			logger = module.getLogger(processingContext);
		}
		for (ParamHandler<A> paramHandler : module.getAllParamHandlers()) {
			paramHandler.accept(this, logger);
		}
	}
	
	public static final <A extends Annotable> void visit(ProcessingContext<A> processingContext, Logger logger, Module<A> module) throws ModuleException {
		CollectResources<A> visitor = new CollectResources<A>(processingContext, module.getModuleSourceName());
		module.accept(visitor, logger);
	}
}
