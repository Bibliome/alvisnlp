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


package org.bibliome.alvisnlp.modules.shell.browser;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.logging.Logger;

import org.bibliome.alvisnlp.converters.expression.parser.ExpressionParser;
import org.bibliome.alvisnlp.modules.CorpusModule;
import org.bibliome.alvisnlp.modules.DefaultExpressions;

import alvisnlp.corpus.Corpus;
import alvisnlp.corpus.expressions.ConstantsLibrary;
import alvisnlp.corpus.expressions.Expression;
import alvisnlp.corpus.expressions.ResolverException;
import alvisnlp.module.ModuleException;
import alvisnlp.module.ProcessingContext;
import alvisnlp.module.lib.AlvisNLPModule;
import alvisnlp.module.lib.Param;
import fi.iki.elonen.NanoHTTPD;

@AlvisNLPModule(beta=true)
public class BrowserShell extends CorpusModule<BrowserShellResolvedObjects> {
	private Integer port = 4444;
	private Expression corpusLabel = ConstantsLibrary.create("Corpus");
	private Expression documentLabel = DefaultExpressions.DOCUMENT_ID;
	private Expression sectionLabel = DefaultExpressions.SECTION_NAME;
	private Expression annotationLabel = DefaultExpressions.ANNOTATION_FORM;
	private Expression relationLabel = DefaultExpressions.feature("name");
	private Expression tupleLabel = ConstantsLibrary.create("<>");
	private Expression argumentLabelPrefix = ExpressionParser.parseUnsafe("@role ^ \": \"");
	private Boolean openBrowser = false;

	@Override
	public void process(ProcessingContext<Corpus> ctx, Corpus corpus) throws ModuleException {
		try {
			Logger logger = getLogger(ctx);
			HTTPServer server = new HTTPServer(port, logger, corpus, getResolvedObjects());
			server.start(NanoHTTPD.SOCKET_READ_TIMEOUT, false);
			logger.info("server started");
			logger.info("URL is: http://localhost:"+port);
			String hostName = System.getenv("HOSTNAME");
			if (hostName != null) {
				logger.info("Maybe also: http://"+hostName+":"+port);
			}
			if (openBrowser) {
				launchBrowser(logger);
			}
			logger.info("hit enter to shut server down and proceed with the plan");
			System.in.read();
			server.stop();
		}
		catch (Exception e) {
			rethrow(e);
		}
	}
	
	private void launchBrowser(Logger logger) throws IOException, URISyntaxException {
		if (Desktop.isDesktopSupported()) {
			Desktop desktop = Desktop.getDesktop();
			if (desktop.isSupported(Desktop.Action.BROWSE)) {
				desktop.browse(new URI("http://localhost:" + port));
				logger.info("browser launched");
				return;
			}
			logger.warning("desktop does not support opening browser");
			return;
		}
		logger.warning("desktop not supported");
	}

	@Override
	protected BrowserShellResolvedObjects createResolvedObjects(ProcessingContext<Corpus> ctx) throws ResolverException {
		return new BrowserShellResolvedObjects(ctx, this);
	}

	@Param
	public Integer getPort() {
		return port;
	}

	@Param
	public Expression getCorpusLabel() {
		return corpusLabel;
	}

	@Param
	public Expression getDocumentLabel() {
		return documentLabel;
	}

	@Param
	public Expression getSectionLabel() {
		return sectionLabel;
	}

	@Param
	public Expression getAnnotationLabel() {
		return annotationLabel;
	}

	@Param
	public Expression getRelationLabel() {
		return relationLabel;
	}

	@Param
	public Expression getTupleLabel() {
		return tupleLabel;
	}

	@Param
	public Expression getArgumentLabelPrefix() {
		return argumentLabelPrefix;
	}

	@Param
	public Boolean getOpenBrowser() {
		return openBrowser;
	}

	public void setOpenBrowser(Boolean openBrowser) {
		this.openBrowser = openBrowser;
	}

	public void setPort(Integer port) {
		this.port = port;
	}

	public void setCorpusLabel(Expression corpusLabel) {
		this.corpusLabel = corpusLabel;
	}

	public void setDocumentLabel(Expression documentLabel) {
		this.documentLabel = documentLabel;
	}

	public void setSectionLabel(Expression sectionLabel) {
		this.sectionLabel = sectionLabel;
	}

	public void setAnnotationLabel(Expression annotationLabel) {
		this.annotationLabel = annotationLabel;
	}

	public void setRelationLabel(Expression relationLabel) {
		this.relationLabel = relationLabel;
	}

	public void setTupleLabel(Expression tupleLabel) {
		this.tupleLabel = tupleLabel;
	}

	public void setArgumentLabelPrefix(Expression argumentLabelPrefix) {
		this.argumentLabelPrefix = argumentLabelPrefix;
	}
}
