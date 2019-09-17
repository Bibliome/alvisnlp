package fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.http;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

import fi.iki.elonen.NanoHTTPD;
import fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.http.api.APIResponseFactory;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Corpus;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.LibraryResolver;
import fr.inra.maiage.bibliome.util.Strings;
import fr.inra.maiage.bibliome.util.files.InputDirectory;

public class Server extends NanoHTTPD {
	private final Logger logger;
	private final ResponseFactory apiResponseFactory;
	private final ResponseFactory defaultResponseFactory;
	private final ResponseFactory resourceResponseFactory;

	public Server(int port, Logger logger, Corpus corpus, LibraryResolver libraryResolver, InputDirectory resourceBaseDir) {
		super(port);
		this.logger = logger;
		this.defaultResponseFactory = new DefaultResponseFactory(logger);
		this.apiResponseFactory = new APIResponseFactory(logger, libraryResolver, corpus);
		if (resourceBaseDir == null) {
			this.resourceResponseFactory = new ResourceResponseFactory(logger);
		}
		else {
			this.resourceResponseFactory = new DirectoryResourceResponseFactory(logger, resourceBaseDir);
		}
	}

	@Override
	public Response serve(IHTTPSession session) {
		Method method = session.getMethod();
		if (method != Method.GET) {
			return defaultResponseFactory.createBadRequestResponse("server only processes GET requests (not " + method + ")");
		}
		try {
			String uri = session.getUri();
			getLogger().fine("request URI: " + uri);
			getLogger().fine("request params: " + session.getParms());
			List<String> path = parseUri(uri);
			if (path.isEmpty()) {
				return defaultResponseFactory.createResponse(session, path);
			}
			String cmd = path.remove(0);
			switch (cmd) {
				case "index.html":
				case "index.htm":
				case "home":
					return resourceResponseFactory.createResponse(session, Arrays.asList("index.html"));
					//return defaultResponseFactory.createResponse(session, path);
				case "api":
					return apiResponseFactory.createResponse(session, path);
				case "res":
				case "resource":
					return resourceResponseFactory.createResponse(session, path);
			}
			return defaultResponseFactory.createNotFoundResponse(session);
		}
		catch (Exception e) {
			return defaultResponseFactory.createBadRequestResponse(e.getMessage());
		}
	}

	private static List<String> parseUri(String uri) {
		List<String> path = Strings.split(uri, '/', -1);
		List<String> result = new LinkedList<String>();
		for (String s: path) {
			if (!s.isEmpty()) {
				result.add(s);
			}
		}
		return result;
	}

	public Logger getLogger() {
		return logger;
	}
}
