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


package org.bibliome.alvisnlp.modules.xml;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.stream.StreamSource;

import org.bibliome.alvisnlp.modules.CorpusModule;
import org.bibliome.alvisnlp.modules.ResolvedObjects;
import org.bibliome.util.Iterators;
import org.bibliome.util.streams.SourceStream;

import alvisnlp.corpus.Corpus;
import alvisnlp.corpus.creators.AnnotationCreator;
import alvisnlp.corpus.creators.DocumentCreator;
import alvisnlp.corpus.creators.SectionCreator;
import alvisnlp.corpus.creators.TupleCreator;
import alvisnlp.corpus.expressions.ResolverException;
import alvisnlp.module.ModuleException;
import alvisnlp.module.ProcessingContext;
import alvisnlp.module.lib.AlvisNLPModule;
import alvisnlp.module.lib.Param;
import alvisnlp.module.types.Mapping;

// TODO: Auto-generated Javadoc
/**
 * The Class XMLReader.
 */
@AlvisNLPModule(obsoleteUseInstead=XMLReader2.class)
public abstract class XMLReader extends CorpusModule<ResolvedObjects> implements DocumentCreator, SectionCreator, AnnotationCreator, TupleCreator {
    private static final TransformerFactory transformerFactory = TransformerFactory.newInstance();

    private SourceStream                            xslTransform       = null;
    private Mapping                         stringParams       = null;
    private SourceStream sourcePath;

    @Override
	protected ResolvedObjects createResolvedObjects(ProcessingContext<Corpus> ctx) throws ResolverException {
		return new ResolvedObjects(ctx, this);
	}
    
	private void processFile(ProcessingContext<Corpus> ctx, InputStream is, Transformer transformer, Result result) throws ModuleException {
        try {
            String path = sourcePath.getStreamName(is);
            getLogger(ctx).finer("reading file: " + path);
            transformer.setParameter("source", path);
            transformer.transform(new StreamSource(is), result);
        }
        catch (TransformerException te) {
            rethrow(te);
        }
    }
    
	
    @Override
	public void process(ProcessingContext<Corpus> ctx, Corpus corpus) throws ModuleException {
        try {
			Transformer transformer = getTransformer(ctx);
			if (stringParams != null)
				for (Map.Entry<String,String> e : stringParams.entrySet())
					transformer.setParameter(e.getKey(), e.getValue());
			XMLHandler xmlHandler = new XMLHandler(ctx, this, corpus);
			Result result = new SAXResult(xmlHandler);
			for (InputStream is : Iterators.loop(sourcePath.getInputStreams())) {
				processFile(ctx, is, transformer, result);
				is.close();
			}
		}
		catch (TransformerConfigurationException|IOException e) {
			rethrow(e);
		}
	}

	protected Transformer getTransformer(ProcessingContext<Corpus> ctx) throws TransformerConfigurationException, IOException {
		if (xslTransform == null)
			return transformerFactory.newTransformer();
		getLogger(ctx).info("loading XSL transform: " + xslTransform);
		InputStream is = xslTransform.getInputStream();
		Source source = new StreamSource(is);
		Transformer result = transformerFactory.newTransformer(source);
		is.close();
		return result;
	}

	/**
     * Gets the xsl transform.
     * 
     * @return the xslTransform
     */
    @Param(mandatory=false)
    public SourceStream getXslTransform() {
        return xslTransform;
    }

    /**
     * Gets the string params.
     * 
     * @return the stringParams
     */
    @Param(mandatory = false, defaultDoc = "String parameters for the XSLT stylesheet.")
    public Mapping getStringParams() {
        return stringParams;
    }

    @Param
    public SourceStream getSourcePath() {
		return sourcePath;
	}


	public void setSourcePath(SourceStream sourcePath) {
		this.sourcePath = sourcePath;
	}


	/**
     * Sets the xsl transform.
     * 
     * @param xslTransform
     *            the xslTransform to set
     */
    public void setXslTransform(SourceStream xslTransform) {
        this.xslTransform = xslTransform;
    }

    /**
     * Sets the string params.
     * 
     * @param stringParams
     *            the stringParams to set
     */
    public void setStringParams(Mapping stringParams) {
        this.stringParams = stringParams;
    }
}
