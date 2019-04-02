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

@AlvisNLPModule(beta=true)
public class HttpServer extends CorpusModule<ResolvedObjects> {
	private Integer port = 8878;
	
	@Override
	public void process(ProcessingContext<Corpus> ctx, Corpus corpus) throws ModuleException {
		try {
			Logger logger = getLogger(ctx);
			LibraryResolver resolver = getLibraryResolver(ctx);
			Server server = new Server(port, logger, corpus, resolver);
			server.start(NanoHTTPD.SOCKET_READ_TIMEOUT, false);
			logger.info("server started on http://localhost:" + port);
			logger.config("hit enter to stop server and proceed to next module");
			System.in.read();
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
}
