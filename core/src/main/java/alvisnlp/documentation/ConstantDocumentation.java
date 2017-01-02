package alvisnlp.documentation;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.w3c.dom.Document;

public class ConstantDocumentation implements Documentation {
	private final Map<Locale,Document> documents = new HashMap<Locale,Document>();

	@Override
	public Document getDocument() {
		Locale locale = Locale.getDefault();
		return getDocument(locale);
	}

	@Override
	public Document getDocument(Locale locale) {
		if (documents.containsKey(locale)) {
			return documents.get(locale);
		}
		throw new RuntimeException("No documentation for locale " + locale);
	}
	
	public void setDocument(Locale locale, Document doc) {
		documents.put(locale, doc);
	}
}
