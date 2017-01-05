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



package alvisnlp.app.cli;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.LogRecord;

import alvisnlp.module.lib.ModuleBase;

/**
 * AlvisNLP-specific log formatter.
 */
public class CommandLineLogFormatter extends Formatter {
	public static final CommandLineLogFormatter INSTANCE = new CommandLineLogFormatter(false);
	public static final CommandLineLogFormatter COLORS = new CommandLineLogFormatter(true);
	
	private static final Map<Level,String> LEVEL_COLORS = new HashMap<Level,String>();
	static {
		LEVEL_COLORS.put(ModuleBase.HIGHLIGHT, "\u001B[1m");
		LEVEL_COLORS.put(Level.CONFIG, "\u001B[36;1m");
		LEVEL_COLORS.put(Level.WARNING, "\u001B[33;1m");
		LEVEL_COLORS.put(Level.SEVERE, "\u001B[31;1m");
	}
	
    /** The df. */
    private static final DateFormat df = new SimpleDateFormat("[yyyy-MM-dd HH:mm:ss.SSS]");

    private final boolean colors;
    
    /**
     * Constructs a new AlvisNLPLogFormatter object.
     */
    private CommandLineLogFormatter(boolean colors) {
        super();
        this.colors = colors;
    }

    /**
     * Returns a string representation of the specified record.
     * 
     * @param rec
     *            the rec
     * 
     * @return a string representation of the specified record
     */
    @Override
    public String format(LogRecord rec) {
        StringBuilder sb = new StringBuilder();
        Level lvl = rec.getLevel();
        sb.append(df.format(new Date(rec.getMillis())));
        sb.append('[');
        sb.append(rec.getLoggerName().replace("alvisnlp.", ""));
        sb.append("] ");
        if (colors && LEVEL_COLORS.containsKey(lvl)) {
        	sb.append(LEVEL_COLORS.get(lvl));
        }
        if ((lvl == Level.WARNING) || (lvl == Level.SEVERE)) {
            sb.append(lvl);
            sb.append(' ');
        }
        sb.append(rec.getMessage());
        for (Throwable cause = rec.getThrown(); cause != null; cause = cause.getCause()) {
            sb.append("\n\n### error type:\n###     ");
            sb.append(cause.getClass().getCanonicalName());
            sb.append("\n###\n### error message:\n###     ");
            sb.append(cause.getMessage());
            sb.append("\n###\n### stack trace:\n");
            for (StackTraceElement elt : cause.getStackTrace()) {
                sb.append("###     ");
                sb.append(elt.toString());
                sb.append('\n');
            }
        }
        sb.append('\n');
        if (colors && LEVEL_COLORS.containsKey(lvl)) {
        	sb.append("\u001B[0m");
        }
        return sb.toString();
    }
}
