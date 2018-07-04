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



package fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;

import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Corpus;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.DefaultNames;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Document;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.NameType;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Section;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.creators.DocumentCreator;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.creators.SectionCreator;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.ResolverException;
import fr.inra.maiage.bibliome.alvisnlp.core.module.ModuleException;
import fr.inra.maiage.bibliome.alvisnlp.core.module.ProcessingContext;
import fr.inra.maiage.bibliome.alvisnlp.core.module.ProcessingException;
import fr.inra.maiage.bibliome.alvisnlp.core.module.lib.AlvisNLPModule;
import fr.inra.maiage.bibliome.alvisnlp.core.module.lib.Param;
import fr.inra.maiage.bibliome.util.Iterators;
import fr.inra.maiage.bibliome.util.streams.SourceStream;

/**
 * Reads the contents of text files and creates a document with a single section
 * for each file.
 */
@AlvisNLPModule
public abstract class TextFileReader extends CorpusModule<ResolvedObjects> implements DocumentCreator, SectionCreator {
    private String  sectionName = DefaultNames.getDefaultSectionName();
    private Integer sizeLimit = null;
    private Integer linesLimit = null;
    private String  charset   = "UTF-8";
    private SourceStream sourcePath;
    private Boolean baseNameId = false;

	/**
     * Gets the section name.
     * 
     * @return the section name
     */
    @Param(nameType=NameType.SECTION, defaultDoc = "Name of the single section containing the whole contents of a file.")
    public String getSectionName() {
        return sectionName;
    }

    /**
     * Sets the section name.
     * 
     * @param sectionName
     *            the new section name
     */
    public void setSectionName(String sectionName) {
        this.sectionName = sectionName;
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

    @Param
	public SourceStream getSourcePath() {
		return sourcePath;
	}

    @Param
	public Boolean getBaseNameId() {
		return baseNameId;
	}

	public void setBaseNameId(Boolean baseNameId) {
		this.baseNameId = baseNameId;
	}

	public void setSourcePath(SourceStream sourcePath) {
		this.sourcePath = sourcePath;
	}

	@Override
	protected ResolvedObjects createResolvedObjects(ProcessingContext<Corpus> ctx) throws ResolverException {
		return new ResolvedObjects(ctx, this);
	}

	@Override
	public void process(ProcessingContext<Corpus> ctx, Corpus corpus) throws ModuleException {
		try {
			for (BufferedReader r : Iterators.loop(sourcePath.getBufferedReaders())) {
				processFile(corpus, r);
				r.close();
			}
		}
		catch (IOException e) {
			throw new ProcessingException(e);
		}
	}

	private void processFile(Corpus corpus, BufferedReader r) throws IOException {
		String name = sourcePath.getStreamName(r);
		if (baseNameId) {
			int slash = name.lastIndexOf(File.separatorChar) + 1;
			int dot = name.lastIndexOf('.');
			if (dot == -1 || dot < slash)
				dot = name.length();
			name = name.substring(slash, dot);
		}
    	StringBuilder sb = new StringBuilder();
    	int n = 0;
    	int l = 0;
    	for (String line = r.readLine(); line != null; line = r.readLine()) {
    		sb.append(line);
    		sb.append('\n');
    		++l;
    		if (((sizeLimit != null) && (sb.length() >= sizeLimit)) || ((linesLimit != null) && (l >= linesLimit))) {
    			createDocument(corpus, name + Integer.toString(n++), sb.toString());
    			sb.setLength(0);
    			l = 0;
    		}
    	}
    	if (n == 0)
    		createDocument(corpus, name, sb.toString());
    	else
    		createDocument(corpus, name + Integer.toString(n), sb.toString());
    }
   
    /**
     * Creates the document.
     * @param corpus
     *            the corpus
     * @param id
     *            the id
     * @param contents
     *            the contents
     * @return the document
     */
    private Document createDocument(Corpus corpus, String id, String contents) {
        Document result = Document.getDocument(this, corpus, id);
        new Section(this, result, sectionName, contents);
        return result;
    }
}
