package org.bibliome.alvisnlp;

import java.util.Properties;

import org.apache.log4j.PropertyConfigurator;

public enum MiscUtils {
;
	
	public static void configureSilentLog4J() {
		Properties props = new Properties();
		props.setProperty("log4j.rootLogger", "WARNING, A1");
		props.setProperty("log4j.appender.A1", "org.apache.log4j.varia.NullAppender");
		PropertyConfigurator.configure(props);
	}
}
