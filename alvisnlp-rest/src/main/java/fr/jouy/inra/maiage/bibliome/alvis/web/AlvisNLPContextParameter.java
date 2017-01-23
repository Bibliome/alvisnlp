/*
Copyright 2017 Institut National de la Recherche Agronomique

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

package fr.jouy.inra.maiage.bibliome.alvis.web;

import java.io.File;

import javax.servlet.ServletContext;

public enum AlvisNLPContextParameter {
	URL_BASE("alvisnlp.url-base"),
	RESOURCE_DIR("alvisnlp.resource-dir"),
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
