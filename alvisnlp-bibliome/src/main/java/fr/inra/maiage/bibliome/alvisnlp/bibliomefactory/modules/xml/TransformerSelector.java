package fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.xml;

import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Logger;

import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamSource;

import org.w3c.dom.Document;

import fr.inra.maiage.bibliome.util.filters.Filter;
import fr.inra.maiage.bibliome.util.streams.SourceStream;

public class TransformerSelector implements Filter<Document> {
	private final Filter<Document> filter;
	private final SourceStream transformerSource;
	private Transformer transformer = null;
	
	public TransformerSelector(Filter<Document> filter, SourceStream transformerSource) {
		super();
		this.filter = filter;
		this.transformerSource = transformerSource;
	}
	
	public Transformer getTransformer(Logger logger, TransformerFactory transformerFactory) throws IOException, TransformerConfigurationException {
		if (transformer == null) {
			try (InputStream is = transformerSource.getInputStream()) {
	    		logger.info("loading stylesheet: " + transformerSource.getStreamName(is));
	    		logger.info("loading stylesheet: " + transformerSource.getClass());
	    		logger.info("loading stylesheet: " + is);
//	    		Reader r = new InputStreamReader(is);
//	    		BufferedReader br = new BufferedReader(r);
//	    		while (true) {
//	    			String line = br.readLine();
//	    			if (line == null) {
//	    				break;
//	    			}
//	    			logger.info(line);
//	    		}
	    		Source source = new StreamSource(is);
				transformer = transformerFactory.newTransformer(source);
			}
		}
		return transformer;
	}

	@Override
	public boolean accept(Document x) {
		return filter.accept(x);
	}
	
	void clear() {
		if (transformer != null) {
			transformer.reset();
			transformer = null;
		}
	}
}
