package fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.http;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.logging.Logger;

import fi.iki.elonen.NanoHTTPD;
import fi.iki.elonen.NanoHTTPD.IHTTPSession;
import fi.iki.elonen.NanoHTTPD.Response;
import fr.inra.maiage.bibliome.util.Strings;
import fr.inra.maiage.bibliome.util.files.InputDirectory;

public class DirectoryResourceResponseFactory extends ResponseFactory {
	private final InputDirectory baseDir;
	
	protected DirectoryResourceResponseFactory(Logger logger, InputDirectory baseDir) {
		super(logger);
		this.baseDir = baseDir;
	}

	@Override
	protected Response createResponse(IHTTPSession session, List<String> path) throws IOException {
		String name = Strings.join(path, '/');
		File file = new File(baseDir, name);
		InputStream is = getInputStream(file);
		if (is == null) {
			return createNotFoundResponse(session);
		}
		String mimeType = NanoHTTPD.getMimeTypeForFile(name);
		logger.fine("serving: " + name + " (" + mimeType + ")");
		return NanoHTTPD.newChunkedResponse(Response.Status.OK, mimeType, is);
	}
	
	private static InputStream getInputStream(File file) {
		try {
			return new FileInputStream(file);
		}
		catch (FileNotFoundException e) {
			return null;
		}
	}
}
