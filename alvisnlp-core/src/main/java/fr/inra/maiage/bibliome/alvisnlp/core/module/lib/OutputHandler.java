package fr.inra.maiage.bibliome.alvisnlp.core.module.lib;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.logging.Level;
import java.util.logging.Logger;

import fr.inra.maiage.bibliome.util.Files;
import fr.inra.maiage.bibliome.util.files.OutputFile;
import fr.inra.maiage.bibliome.util.filters.AcceptAll;
import fr.inra.maiage.bibliome.util.filters.Filter;

public interface OutputHandler {
	void handle(InputStream is) throws IOException;
	
	public static class Redirect implements OutputHandler {
		private final OutputFile destination;

		public Redirect(OutputFile destination) {
			super();
			this.destination = destination;
		}

		@Override
		public void handle(InputStream is) throws IOException {
			Files.copy(is, destination, new byte[2048], false);
		}
	}
	
	public static class ToLogger implements OutputHandler {
		private final Logger logger;
		private final Level level;
		private final String prefix;
		private final Filter<String> lineFilter;
		
		public ToLogger(Logger logger, Level level, String prefix, Filter<String> lineFilter) {
			super();
			this.logger = logger;
			this.prefix = prefix;
			this.level = level;
			this.lineFilter = lineFilter;
		}
		
		public ToLogger(Logger logger, Level level, String prefix) {
			this(logger, level, prefix, new AcceptAll<String>());
		}
		
		public ToLogger(Logger logger, Filter<String> lineFilter, String prefix) {
			this(logger, Level.FINE, prefix, lineFilter);
		}
		
		public ToLogger(Logger logger, String prefix) {
			this(logger, Level.FINE, prefix, new AcceptAll<String>());
		}
		
		public ToLogger(Logger logger, Level level) {
			this(logger, level, null, new AcceptAll<String>());
		}
		
		public ToLogger(Logger logger, Filter<String> lineFilter) {
			this(logger, Level.FINE, null, lineFilter);
		}
		
		public ToLogger(Logger logger) {
			this(logger, Level.FINE, null, new AcceptAll<String>());
		}

		@Override
		public void handle(InputStream is) throws IOException {
			BufferedReader r = new BufferedReader(new InputStreamReader(is));
			while (true) {
				String line = r.readLine();
				if (line == null) {
					break;
				}
				line = line.trim();
				if (lineFilter.accept(line)) {
					if (prefix != null) {
						line = prefix + line;
					}
					logger.log(level, line);
				}
			}
		}
	}
}
