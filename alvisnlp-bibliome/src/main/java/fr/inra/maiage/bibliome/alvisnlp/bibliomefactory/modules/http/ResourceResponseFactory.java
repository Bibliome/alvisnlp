package fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.http;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.logging.Logger;

import fi.iki.elonen.NanoHTTPD;
import fi.iki.elonen.NanoHTTPD.IHTTPSession;
import fi.iki.elonen.NanoHTTPD.Response;
import fr.inra.maiage.bibliome.util.Strings;

public class ResourceResponseFactory extends ResponseFactory {
	protected ResourceResponseFactory(Logger logger) {
		super(logger);
	}

	@Override
	protected Response createResponse(IHTTPSession session, List<String> path) throws IOException {
		Class<?> klass = getClass();
		String name = Strings.join(path, '/');
		InputStream is = klass.getResourceAsStream(name);
		if (is == null) {
			return createNotFoundResponse(session);
		}
		String mimeType = NanoHTTPD.getMimeTypeForFile(name);
		logger.fine("serving: " + name + " (" + mimeType + ")");
		return NanoHTTPD.newChunkedResponse(Response.Status.OK, mimeType, is);
	}
}
