package fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.http;

import java.io.IOException;
import java.util.logging.Logger;

import fi.iki.elonen.NanoHTTPD;
import fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.CorpusModule;
import fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.ResolvedObjects;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Corpus;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.LibraryResolver;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.ResolverException;
import fr.inra.maiage.bibliome.alvisnlp.core.module.ModuleException;
import fr.inra.maiage.bibliome.alvisnlp.core.module.ProcessingContext;
import fr.inra.maiage.bibliome.alvisnlp.core.module.lib.AlvisNLPModule;
import fr.inra.maiage.bibliome.alvisnlp.core.module.lib.Param;
import fr.inra.maiage.bibliome.util.files.InputDirectory;

@AlvisNLPModule(beta=true)
public class HttpServer extends CorpusModule<ResolvedObjects> {
	private Integer port = 8878;
	private InputDirectory resourceBaseDir;
	
	@Override
	public void process(ProcessingContext<Corpus> ctx, Corpus corpus) throws ModuleException {
		try {
			Logger logger = getLogger(ctx);
			LibraryResolver resolver = getLibraryResolver(ctx);
			Server server = new Server(port, logger, corpus, resolver, resourceBaseDir);
			server.start(NanoHTTPD.SOCKET_READ_TIMEOUT, false);
			logger.info("server started on http://localhost:" + port);
			logger.config("hit enter to stop server and proceed to next module");
			System.in.read();
			server.stop();
			logger.info("server stopped");
		}
		catch (IOException e) {
			throw new ModuleException(e);
		}
	}

	@Override
	protected ResolvedObjects createResolvedObjects(ProcessingContext<Corpus> ctx) throws ResolverException {
		return new ResolvedObjects(ctx, this);
	}

	@Param(mandatory=false)
	public InputDirectory getResourceBaseDir() {
		return resourceBaseDir;
	}

	@Param
	public Integer getPort() {
		return port;
	}

	public void setPort(Integer port) {
		this.port = port;
	}

	public void setResourceBaseDir(InputDirectory resourceBaseDir) {
		this.resourceBaseDir = resourceBaseDir;
	}
}
