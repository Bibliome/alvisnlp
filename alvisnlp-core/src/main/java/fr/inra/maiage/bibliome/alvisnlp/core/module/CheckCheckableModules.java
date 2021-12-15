package fr.inra.maiage.bibliome.alvisnlp.core.module;

import java.util.logging.Logger;

import fr.inra.maiage.bibliome.util.Checkable;

public class CheckCheckableModules<A extends Annotable> extends AbstractModuleVisitor<A,Logger> {
	private boolean hasErrors = false;
	
	public CheckCheckableModules() {
		super(true);
	}

	@Override
	public void visitModule(Module<A> module, Logger param) throws ModuleException {
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
	
	public static final <A extends Annotable> boolean visit(Logger logger, Module<A> module) throws ModuleException {
		CheckCheckableModules<A> visitor = new CheckCheckableModules<A>();
		module.accept(visitor, logger);
		return visitor.hasErrors;
	}
}
