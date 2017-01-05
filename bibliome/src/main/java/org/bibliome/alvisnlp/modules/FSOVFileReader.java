/*
Copyright 2016, 2017 Institut National de la Recherche Agronomique

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

        http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/



package org.bibliome.alvisnlp.modules;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.xml.xpath.XPathExpressionException;

import org.bibliome.util.Iterators;
import org.bibliome.util.files.InputDirectory;
import org.bibliome.util.streams.SourceStream;
import org.bibliome.util.xml.XMLUtils;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import alvisnlp.corpus.Corpus;
import alvisnlp.corpus.Document;
import alvisnlp.corpus.NameType;
import alvisnlp.corpus.Section;
import alvisnlp.corpus.creators.DocumentCreator;
import alvisnlp.corpus.creators.SectionCreator;
import alvisnlp.corpus.expressions.ResolverException;
import alvisnlp.module.ModuleException;
import alvisnlp.module.ProcessingContext;
import alvisnlp.module.lib.AlvisNLPModule;
import alvisnlp.module.lib.Param;

/**
 * Reads the contents of text files and creates a document with a single section
 * for each file.
 */
@AlvisNLPModule
public abstract class FSOVFileReader extends CorpusModule<ResolvedObjects> implements DocumentCreator, SectionCreator {
	private String titleSectionName = "title";
    private String  bodySectionName = "body";
    private Integer sizeLimit = null;
    private Integer linesLimit = null;
    private String  charset   = "UTF-8";
    private InputDirectory xmlDir = null;
    private SourceStream sourcePath;
    
    @Param
    public SourceStream getSourcePath() {
		return sourcePath;
	}

	public void setSourcePath(SourceStream sourcePath) {
		this.sourcePath = sourcePath;
	}

	/**
     * Gets the section name.
     * 
     * @return the section name
     */
    @Param(nameType=NameType.SECTION, defaultDoc = "Name of the single section containing the whole contents of a file.")
    public String getBodySectionName() {
        return bodySectionName;
    }

    /**
     * Sets the section name.
     * 
     * @param sectionName
     *            the new section name
     */
    public void setBodySectionName(String sectionName) {
        this.bodySectionName = sectionName;
    }

    /**
     * Gets the size limit.
     * 
     * @return the size limit
     */
    @Param(mandatory = false)
    public Integer getSizeLimit() {
        return sizeLimit;
    }

    /**
     * Sets the size limit.
     * 
     * @param sizeLimit
     *            the new size limit
     */
    public void setSizeLimit(Integer sizeLimit) {
        this.sizeLimit = sizeLimit;
    }

    /**
     * Gets the charset.
     * 
     * @return the charset
     */
    @Param(defaultDoc = "Character set of the input files.")
    public String getCharset() {
        return charset;
    }

    @Param
    public InputDirectory getXmlDir() {
		return xmlDir;
	}

	public void setXmlDir(InputDirectory xmlDir) {
		this.xmlDir = xmlDir;
	}

	@Param(nameType=NameType.SECTION)
	public String getTitleSectionName() {
		return titleSectionName;
	}

	public void setTitleSectionName(String titleSectionName) {
		this.titleSectionName = titleSectionName;
	}

	/**
     * Sets the charset.
     * 
     * @param charset
     *            the new charset
     */
    public void setCharset(String charset) {
        this.charset = charset;
    }

    /**
     * @param linesLimit the linesLimit to set
     */
    public void setLinesLimit(Integer linesLimit) {
        this.linesLimit = linesLimit;
    }

    /**
     * @return the linesLimit
     */
    @Param(mandatory=false)
    public Integer getLinesLimit() {
        return linesLimit;
    }

	@Override
	protected ResolvedObjects createResolvedObjects(ProcessingContext<Corpus> ctx) throws ResolverException {
		return new ResolvedObjects(ctx, this);
	}

	@Override
	public void process(ProcessingContext<Corpus> ctx, Corpus corpus) throws ModuleException {
		try {
			for (BufferedReader r : Iterators.loop(sourcePath.getBufferedReaders())) {
				processFile(ctx, corpus, r);
				r.close();
			}
		}
		catch (IOException e) {
			rethrow(e);
		}
	}

	private static String getBasename(String name) {
		int slash = name.lastIndexOf(File.separatorChar);
		if (slash == -1)
			return name;
		return name.substring(slash + 1);
	}
	
	private void processFile(ProcessingContext<Corpus> ctx, Corpus corpus, BufferedReader r) throws IOException {
		Logger logger = getLogger(ctx);
		String streamName = sourcePath.getStreamName(r);
		logger.finer("reading: " + streamName);
		String name = getBasename(streamName);
    	Map<String,List<String>> metadata = getMetadata(ctx, name);
    	StringBuilder sb = new StringBuilder();
    	int n = 0;
    	int l = 0;
    	for (String line = r.readLine(); line != null; line = r.readLine()) {
    		logger.finer("line.length = " + line.length());
    		for (int i = 0; i < line.length(); ++i) {
    			char c = line.charAt(i);
    			sb.append(c < 32 ? ' ' : c);
    		}
    		sb.append('\n');
    		++l;
    		if (((sizeLimit != null) && (sb.length() >= sizeLimit)) || ((linesLimit != null) && (l >= linesLimit))) {
    			createDocument(corpus, name + Integer.toString(n++), metadata, sb.toString());
    			sb.setLength(0);
    			l = 0;
    		}
    	}
    	logger.finer("done: " + streamName);
    	if (n == 0)
    		createDocument(corpus, name, metadata, sb.toString());
    	else
    		createDocument(corpus, name + Integer.toString(n), metadata, sb.toString());
    }
    
    private Map<String,List<String>> getMetadata(ProcessingContext<Corpus> ctx, String file) {
    	Map<String,List<String>> result = new HashMap<String,List<String>>();
    	File xmlFile = new File(xmlDir, file.replace(".txt", ".xml"));
		try {
			Element root = XMLUtils.docBuilder.parse(xmlFile).getDocumentElement();
			
			String title = XMLUtils.evaluateString("title", root);
			result.put("title", Collections.singletonList(title));
			
			List<String> authors = new ArrayList<String>();
			result.put("author", authors);
			for (Element elt : XMLUtils.evaluateElements("authors/value", root)) {
				String author = XMLUtils.evaluateString(XMLUtils.CONTENTS, elt);
				authors.add(author);
			}
			
			String year = XMLUtils.evaluateString("publisheddate/year", root);
			result.put("year", Collections.singletonList(year));
			
			String journal = XMLUtils.evaluateString("sourcetitle", root);
			result.put("journal", Collections.singletonList(journal));
			
			String pdfPath = file.replace(".txt", ".pdf");
			result.put("pdf", Collections.singletonList(pdfPath));
			
			String pdfLink = XMLUtils.evaluateString("pdflink", root);
			result.put("pdflink", Collections.singletonList(pdfLink));
		}
		catch (XPathExpressionException xpee) {
			getLogger(ctx).severe(xpee.getMessage());
		}
		catch (SAXException saxe) {
			getLogger(ctx).warning("could not fetch metadata from " + xmlFile + ": " + saxe.getMessage());
		}
		catch (IOException ioe) {
			getLogger(ctx).warning("could not fetch metadata for " + file + ": " + ioe.getMessage());
		}
    	return result;
	}

	/**
     * Creates the document.
	 * @param corpus
     *            the corpus
	 * @param id
     *            the id
	 * @param metadata 
	 * @param contents
     *            the contents
	 * @return the document
     */
    private Document createDocument(Corpus corpus, String id, Map<String,List<String>> metadata, String contents) {
        Document result = Document.getDocument(this, corpus, id);
        if (metadata.containsKey("title"))
        	new Section(this, result, titleSectionName, metadata.get("title").get(0));
        new Section(this, result, bodySectionName, contents);
        result.addMultiFeatures(metadata);
        return result;
    }
}
