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


package org.bibliome.alvisnlp.modules.alvisre;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bibliome.alvisnlp.modules.SectionModule;
import org.bibliome.alvisnlp.modules.alvisre.AbstractAlvisRE.AlvisREResolvedObjects;
import org.bibliome.util.xml.XMLUtils;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import alvisnlp.corpus.Corpus;
import alvisnlp.corpus.expressions.ResolverException;
import alvisnlp.module.ProcessingContext;
import alvisnlp.module.ProcessingException;

public abstract class AbstractAlvisRE<R extends AlvisREResolvedObjects> extends SectionModule<R> {
	private Integer threads = 2;
	
	protected static class AlvisREResolvedObjects extends SectionResolvedObjects {
		protected AlvisREResolvedObjects(ProcessingContext<Corpus> ctx, SectionModule<? extends AlvisREResolvedObjects> module) throws ResolverException {
			super(ctx, module);
		}
	}

	@Override
	protected String[] addLayersToSectionFilter() {
		return null;
	}

	@Override
	protected String[] addFeaturesToSectionFilter() {
		return null;
	}

	protected void writeConfigurationFile(ProcessingContext<Corpus> ctx, File outDir) throws SAXException, IOException, ProcessingException {
		File configFile = new File(outDir, "config.xml");
		Logger logger = getLogger(ctx);
		logger.info("creating AlvisRE configuration file");
		org.w3c.dom.Document doc = createConfigurationFile(ctx);
		XMLUtils.writeDOMToFile(doc, null, configFile);
	}

	private org.w3c.dom.Document createConfigurationFile(ProcessingContext<Corpus> ctx) throws IOException, SAXException, ProcessingException {
		String action = getActionString();
		Logger logger = getLogger(ctx);
		int verbosity = getVerbosity(logger);
		boolean writeObjects = isTrain();
		// same ClassLoader as this class
		try (InputStream is = AbstractAlvisRE.class.getResourceAsStream("alvisre-conf.xml")) {
			org.w3c.dom.Document result = XMLUtils.docBuilder.parse(is);
			Element experimentElt = result.getDocumentElement();
			XMLUtils.createElement(result, experimentElt, 0, "action", action);
			XMLUtils.createElement(result, experimentElt, 0, "verbose", Integer.toString(verbosity));
			XMLUtils.createElement(result, experimentElt, 0, "writeObjects", Boolean.toString(writeObjects));
			XMLUtils.createElement(result, experimentElt, 0, "threads", threads.toString());
			fillConfigParameters(ctx, experimentElt);
			return result;
		}
	}

	private static int getVerbosity(Logger logger) throws ProcessingException {
		Level level = null;
		do {
			if (logger == null) {
				processingException("logger is null");
			}
			level = logger.getLevel();
			logger = logger.getParent();
		}
		while (level == null);
		int levelValue = level.intValue();
		if (levelValue == Level.SEVERE.intValue()) {
			return 0;
		}
		if (levelValue == Level.WARNING.intValue()) {
			return 1;
		}
		if (levelValue == Level.INFO.intValue()) {
			return 2;
		}
		return 3;
	}
	
	protected abstract void fillConfigParameters(ProcessingContext<Corpus> ctx, Element experimentElt);
	
	protected abstract String getActionString();
	
	protected abstract boolean isTrain();

	public Integer getThreads() {
		return threads;
	}

	public void setThreads(Integer threads) {
		this.threads = threads;
	}
}
