package fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.contes;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Corpus;
import fr.inra.maiage.bibliome.alvisnlp.core.module.ModuleException;
import fr.inra.maiage.bibliome.alvisnlp.core.module.lib.External;

abstract class AbstractContesExternal<T extends AbstractContes> implements External<Corpus> {
	private final T owner;
	private final Logger logger;

	protected AbstractContesExternal(T owner, Logger logger) {
		this.owner = owner;
		this.logger = logger;
	}

	@Override
	public T getOwner() {
		return owner;
	}

	public Logger getLogger() {
		return logger;
	}

	@Override
	public String[] getEnvironment() throws ModuleException {
		return new String[] {
				"PYTHONPATH=" + owner.getContesDir().getAbsolutePath(),
				"PATH=" + System.getenv("PATH")
		};
	}

	@Override
	public File getWorkingDirectory() throws ModuleException {
		return null;
	}

	protected abstract String getLoggingLabel();
	
	@Override
	public void processOutput(BufferedReader out, BufferedReader err) throws ModuleException {
        try {
            logger.fine(getLoggingLabel() + " standard error:");
            for (String line = err.readLine(); line != null; line = err.readLine()) {
                logger.fine("    " + line);
            }
            logger.fine("end of " + getLoggingLabel() + " standard error");
        }
        catch (IOException ioe) {
            logger.warning("could not read " + getLoggingLabel() + " standard error: " + ioe.getMessage());
        }
	}
	
	protected String getContesCommand() {
		return new File(owner.getContesDir(), getContesModule()).getAbsolutePath();
	}

	protected abstract String getContesModule();
}
