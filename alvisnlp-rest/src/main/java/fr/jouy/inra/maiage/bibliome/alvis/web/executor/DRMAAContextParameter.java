package fr.jouy.inra.maiage.bibliome.alvis.web.executor;

import java.io.File;

import javax.servlet.ServletContext;

public enum DRMAAContextParameter {
	JARS_PATH("alvisnlp.drmaa-exec.jars-path"),
	NATIVE_SPECIFICATION("alvisnlp.drmaa-exec.native-specification");
	
	public final String key;

	private DRMAAContextParameter(String key) {
		this.key = key;
	}

	public String getStringValue(ServletContext servletContext) {
		return servletContext.getInitParameter(key);
	}

	public File getFileValue(ServletContext servletContext) {
		return new File(getStringValue(servletContext));
	}
}
