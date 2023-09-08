package fr.inra.maiage.bibliome.alvisnlp.core.module;

import java.util.logging.Logger;

import fr.inra.maiage.bibliome.util.Checkable;

public class CheckCheckableModules extends AbstractModuleVisitor<Logger> {
	private boolean hasErrors = false;
	
	public CheckCheckableModules() {
		super(true);
	}

	@Override
	public void visitModule(Module module, Logger param) throws ModuleException {
		Class<?> moduleClass = module.getClass();
		if (isCheckable(moduleClass)) {
			Checkable checkable = (Checkable) module;
			if (!checkable.check(param)) {
				hasErrors = true;
			}
		}
	}
	
	private static boolean isCheckable(Class<?> type) {
		return Checkable.class.isAssignableFrom(type);
	}
	
	public static final boolean visit(Logger logger, Module module) throws ModuleException {
		CheckCheckableModules visitor = new CheckCheckableModules();
		module.accept(visitor, logger);
		return visitor.hasErrors;
	}
}
