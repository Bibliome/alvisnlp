package fr.inra.maiage.bibliome.alvisnlp.core.module.lib;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.logging.Logger;

import fr.inra.maiage.bibliome.alvisnlp.core.module.Annotable;
import fr.inra.maiage.bibliome.alvisnlp.core.module.Module;
import fr.inra.maiage.bibliome.alvisnlp.core.module.ModuleException;
import fr.inra.maiage.bibliome.alvisnlp.core.module.ProcessingContext;

@Deprecated
public abstract class AbstractExternal<T extends Annotable,M extends Module<T>> implements External<T> {
	private final M owner;
	private final Logger logger;

	protected AbstractExternal(M owner, Logger logger) {
		super();
		this.owner = owner;
		this.logger = logger;
	}

	protected AbstractExternal(M owner, ProcessingContext<T> ctx) {
		this(owner, owner.getLogger(ctx));
	}

	@Override
	public M getOwner() {
		return owner;
	}

	public Logger getLogger() {
		return logger;
	}

	@Override
	public void processOutput(BufferedReader out, BufferedReader err) throws ModuleException {
		new Thread(new StreamLogger("(OUT)", logger, out)).start();
		new Thread(new StreamLogger("(ERR)", logger, err)).start();
	}
	
	protected static class StreamLogger implements Runnable {
		private final String messagePrefix;
		private final Logger logger;
		private final BufferedReader reader;
		
		protected StreamLogger(String messagePrefix, Logger logger, BufferedReader reader) {
			super();
			this.messagePrefix = messagePrefix;
			this.logger = logger;
			this.reader = reader;
		}

		@Override
		public void run() {
			try {
				while (true) {
					String line = reader.readLine();
					if (line == null) {
						break;
					}
					logger.fine(messagePrefix + " " + line.trim());
				}
			}
			catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
	}
}
