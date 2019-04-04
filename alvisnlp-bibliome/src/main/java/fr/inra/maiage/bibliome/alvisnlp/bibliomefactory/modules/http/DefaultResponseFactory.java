package fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.http;

import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import fi.iki.elonen.NanoHTTPD;
import fi.iki.elonen.NanoHTTPD.IHTTPSession;
import fi.iki.elonen.NanoHTTPD.Response;

class DefaultResponseFactory extends ResponseFactory {

	DefaultResponseFactory(Logger logger) {
		super(logger);
	}
	
	@Override
	protected Response createResponse(IHTTPSession session, List<String> path) {
		return showSession(session);
	}
	
	private static Response showSession(IHTTPSession session) {
        StringBuilder msg = new StringBuilder("<html><body><h1>Hello server</h1>\n");
        single(msg, "URI", "uri", session.getUri());
        single(msg, "Method", "method", session.getMethod().toString());
        multiple(msg, "Headers", session.getHeaders());
        multiple(msg, "Params", session.getParms());
        msg.append("</body></html>\n");
        return NanoHTTPD.newFixedLengthResponse(msg.toString());
	}
	
	private static void title(StringBuilder sb, String title) {
		sb.append("<h2>");
		sb.append(title);
		sb.append("</h2>");
	}
	
	private static void openTable(StringBuilder sb) {
		sb.append("<table>");
	}
	
	private static void closeTable(StringBuilder sb) {
		sb.append("</table>");
	}
	
	private static void row(StringBuilder sb, String key, String value) {
		sb.append("<tr><th>");
		sb.append(key);
		sb.append("</th><td>");
		sb.append(value);
		sb.append("</td></tr>");
	}
	
	private static void single(StringBuilder sb, String title, String key, String value) {
		title(sb, title);
		openTable(sb);
		row(sb, key, value);
		closeTable(sb);
	}
	
	private static void multiple(StringBuilder sb, String title, Map<String,String> map) {
		title(sb, title);
		openTable(sb);
		for (Map.Entry<String,String> e : map.entrySet()) {
			row(sb, e.getKey(), e.getValue());
		}
		closeTable(sb);
	}

}
