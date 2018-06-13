package fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.contes;

import java.io.File;
import java.util.logging.Logger;

import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Corpus;
import fr.inra.maiage.bibliome.alvisnlp.core.module.ModuleException;
import fr.inra.maiage.bibliome.alvisnlp.core.module.lib.AbstractExternal;

abstract class AbstractContesExternal<T extends AbstractContes> extends AbstractExternal<Corpus,T> {
	protected AbstractContesExternal(T owner, Logger logger) {
		super(owner, logger);
	}

	@Override
	public String[] getEnvironment() throws ModuleException {
		return new String[] {
				"PYTHONPATH=" + getOwner().getContesDir().getAbsolutePath(),
				"PATH=" + System.getenv("PATH")
		};
	}

	@Override
	public File getWorkingDirectory() throws ModuleException {
		return null;
	}

	protected abstract String getLoggingLabel();
	
	protected String getContesCommand() {
		return new File(getOwner().getContesDir(), getContesModule()).getAbsolutePath();
	}

	protected abstract String getContesModule();
}
