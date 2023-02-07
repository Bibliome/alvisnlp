package fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.xml;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.logging.Logger;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamSource;

import org.cyberneko.html.parsers.DOMParser;
import org.w3c.dom.Document;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.CorpusModule;
import fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.ResolvedObjects;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Corpus;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.creators.AnnotationCreator;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.creators.DocumentCreator;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.creators.SectionCreator;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.creators.TupleCreator;
import fr.inra.maiage.bibliome.alvisnlp.core.module.ProcessingContext;
import fr.inra.maiage.bibliome.alvisnlp.core.module.TimerCategory;
import fr.inra.maiage.bibliome.alvisnlp.core.module.lib.TimeThis;
import fr.inra.maiage.bibliome.alvisnlp.core.module.types.Mapping;
import fr.inra.maiage.bibliome.util.Iterators;
import fr.inra.maiage.bibliome.util.streams.SourceStream;

public abstract class AbstractXMLReader<T extends ResolvedObjects> extends CorpusModule<T> implements DocumentCreator, SectionCreator, AnnotationCreator, TupleCreator {
    public static final String SOURCE_PATH_PARAMETER = "source-path";
    public static final String SOURCE_BASENAME_PARAMETER = "source-basename";
    protected static final String XML_READER_CONTEXT_PARAMETER = "xml-reader-context";

    protected void processDocuments(ProcessingContext<Corpus> ctx, Corpus corpus) throws IOException, TransformerException, SAXException, ParserConfigurationException {
		Transformer transformer = getTransformer(ctx);
		Logger logger = getLogger(ctx);
		for (InputStream is : Iterators.loop(getXMLSource().getInputStreams())) {
			processDocument(ctx, logger, corpus, is, transformer);
			is.close();
		}
	}

    @TimeThis(task="read-xslt", category=TimerCategory.LOAD_RESOURCE)
	protected Transformer getTransformer(ProcessingContext<Corpus> ctx) throws IOException, TransformerConfigurationException {
    	Transformer result = null;
    	TransformerFactory transformerFactory = TransformerFactory.newInstance();
    	InputStream is = getXslTransform().getInputStream();
    	Logger logger = getLogger(ctx);
    	logger.info("using transform: " + getXslTransform().getStreamName(is));
    	Source source = new StreamSource(is);
    	result = transformerFactory.newTransformer(source);
    	is.close();
    	if (getStringParams() != null)
    		for (Map.Entry<String,String> e : getStringParams().entrySet())
    			result.setParameter(e.getKey(), e.getValue());
        return result;
	}

	private void processDocument(ProcessingContext<Corpus> ctx, Logger logger, Corpus corpus, InputStream file, Transformer transformer) throws TransformerException, SAXException, IOException, ParserConfigurationException {
		String name = getXMLSource().getStreamName(file);
		logger.finer("reading: " + name);
		transformer.reset();
		transformer.setParameter(SOURCE_PATH_PARAMETER, name);
		transformer.setParameter(SOURCE_BASENAME_PARAMETER, new File(name).getName());
		transformer.setParameter(XML_READER_CONTEXT_PARAMETER, new XMLReaderContext(this, corpus));
		Source source = getSource(ctx, file);
		doTransform(ctx, transformer, source);
	}

    @TimeThis(task="read-file", category=TimerCategory.LOAD_RESOURCE)
	protected Source getSource(ProcessingContext<Corpus> ctx, InputStream file) throws SAXException, IOException, ParserConfigurationException {
		if (getHtml()) {
	        DOMParser parser = new DOMParser();
	        parser.setFeature("http://xml.org/sax/features/namespaces", false);
	        parser.setFeature("http://cyberneko.org/html/features/scanner/cdata-sections", true);
	        parser.setFeature("http://cyberneko.org/html/features/parse-noscript-content", false);
	        parser.setProperty("http://cyberneko.org/html/properties/default-encoding", getXMLSource().getCharset());
	        if (getRawTagNames()) {
	        	parser.setProperty("http://cyberneko.org/html/properties/names/elems", "match");
	        }
	        else {
	        	parser.setProperty("http://cyberneko.org/html/properties/names/elems", "lower");
	        }
	        parser.parse(new InputSource(file));
	        Document doc = parser.getDocument();
	        return new DOMSource(doc);
		}
		SAXParserFactory spf = SAXParserFactory.newInstance();
		spf.setNamespaceAware(true);
		spf.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
	    org.xml.sax.XMLReader xmlReader = spf.newSAXParser().getXMLReader();
	    xmlReader.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
	    xmlReader.setEntityResolver(new EntityResolver() {
	        @Override
			public InputSource resolveEntity(String pid, String sid) throws SAXException {
	            return new InputSource(new ByteArrayInputStream(new byte[] {}));
	        }
	    });
	    return new SAXSource(xmlReader, new InputSource(file));
	}

	@TimeThis(task="transform")
	protected void doTransform(ProcessingContext<Corpus> ctx, Transformer transformer, Source source) throws TransformerException {
		Result result = new SAXResult(new DefaultHandler());
		transformer.transform(source, result);
	}

    public abstract SourceStream getXslTransform();
    
    public abstract Mapping getStringParams();
    
    public abstract SourceStream getXMLSource();
    
    public abstract Boolean getHtml();
    
    public abstract Boolean getRawTagNames();
}
