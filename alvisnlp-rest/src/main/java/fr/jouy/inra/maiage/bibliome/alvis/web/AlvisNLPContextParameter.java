package fr.jouy.inra.maiage.bibliome.alvis.web;

import java.io.File;

import javax.servlet.ServletContext;

public enum AlvisNLPContextParameter {
	URL_BASE("alvisnlp.url-base"),
	ROOT_PROCESSING_DIR("alvisnlp.processing-dir"),
	PLAN_DIR("alvisnlp.plan-dir"),
	EXECUTOR_CLASS("alvisnlp.executor-class");

	public final String key;

	private AlvisNLPContextParameter(String key) {
		this.key = key;
	}

	public String getStringValue(ServletContext servletContext) {
		return servletContext.getInitParameter(key);
	}

	public File getFileValue(ServletContext servletContext) {
		return new File(getStringValue(servletContext));
	}
}
