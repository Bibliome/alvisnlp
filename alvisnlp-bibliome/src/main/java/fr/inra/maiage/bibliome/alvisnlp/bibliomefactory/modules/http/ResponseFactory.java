package fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.http;

import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

import fi.iki.elonen.NanoHTTPD;
import fi.iki.elonen.NanoHTTPD.IHTTPSession;
import fi.iki.elonen.NanoHTTPD.Response;
import fi.iki.elonen.NanoHTTPD.Response.Status;

public abstract class ResponseFactory {
	protected final Logger logger;

	protected ResponseFactory(Logger logger) {
		super();
		this.logger = logger;
	}

	protected abstract Response createResponse(IHTTPSession session, List<String> path) throws IOException, Exception;
	
	protected Response createNotFoundResponse(IHTTPSession session) {
		logger.info("not found: " + session.getUri());
		return NanoHTTPD.newFixedLengthResponse(Status.NOT_FOUND, NanoHTTPD.MIME_PLAINTEXT, "");		
	}
	
	protected Response createBadRequestResponse(String msg) {
		logger.info("bad request: " + msg);
		return NanoHTTPD.newFixedLengthResponse(Status.BAD_REQUEST, NanoHTTPD.MIME_PLAINTEXT, msg);
	}
}
