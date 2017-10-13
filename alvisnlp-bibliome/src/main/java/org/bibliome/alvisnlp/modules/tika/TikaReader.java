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

package org.bibliome.alvisnlp.modules.tika;

import java.io.IOException;
import java.io.InputStream;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.bibliome.alvisnlp.modules.CorpusModule;
import org.bibliome.alvisnlp.modules.ResolvedObjects;
import org.xml.sax.SAXException;

import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Annotation;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Corpus;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Document;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Layer;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.NameType;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Section;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.creators.AnnotationCreator;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.creators.DocumentCreator;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.creators.SectionCreator;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.ResolverException;
import fr.inra.maiage.bibliome.alvisnlp.core.module.ModuleException;
import fr.inra.maiage.bibliome.alvisnlp.core.module.ProcessingContext;
import fr.inra.maiage.bibliome.alvisnlp.core.module.lib.AlvisNLPModule;
import fr.inra.maiage.bibliome.alvisnlp.core.module.lib.Param;
import fr.inra.maiage.bibliome.util.Iterators;
import fr.inra.maiage.bibliome.util.streams.SourceStream;

@AlvisNLPModule(beta=true)
public abstract class TikaReader extends CorpusModule<ResolvedObjects> implements DocumentCreator, SectionCreator, AnnotationCreator {
	private SourceStream source;
	private String sectionName = "text";
	private String htmlLayerName = "html";
	private String tagFeatureName = "tag";
	
	@Override
	public void process(ProcessingContext<Corpus> ctx, Corpus corpus) throws ModuleException {
		Logger.getLogger("org.apache.pdfbox").setLevel(Level.OFF);
		AutoDetectParser parser = new AutoDetectParser();
		ParseContext parseContext = new ParseContext();
		try {
			for (InputStream is : Iterators.loop(source.getInputStreams())) {
				TikaReaderHandler handler = parse(parser, parseContext, is);
				Document doc = createDocument(corpus, handler);
				createTagAnnotations(doc, handler);
			}
		}
		catch (IOException|SAXException|TikaException e) {
			rethrow(e);
		}
	}
	
	private TikaReaderHandler parse(AutoDetectParser parser, ParseContext parseContext, InputStream is) throws IOException, SAXException, TikaException {
		String name = source.getStreamName(is);
		TikaReaderHandler result = new TikaReaderHandler(name);
		parser.parse(is, result, result.getMetadata(), parseContext);
		return result;
	}

	private Document createDocument(Corpus corpus, TikaReaderHandler handler) {
		Metadata metadata = handler.getMetadata();
		Document result = Document.getDocument(this, corpus, handler.getName());
		for (String k : metadata.names()) {
			for (String v : metadata.getValues(k)) {
				result.addFeature(k, v);
			}
		}
		return result;
	}
	
	private void createTagAnnotations(Document doc, TikaReaderHandler handler) {
		Section sec = new Section(this, doc, sectionName, handler.getContents());
		Layer html = new Layer(sec, htmlLayerName);
		for (ElementFragment frag : handler.getElements()) {
			Annotation a = new Annotation(this, html, frag.getStart(), frag.getEnd());
			a.addFeature(tagFeatureName, frag.getName());
		}
	}
	
	@Override
	protected ResolvedObjects createResolvedObjects(ProcessingContext<Corpus> ctx) throws ResolverException {
		return new ResolvedObjects(ctx, this);
	}

	@Param
	public SourceStream getSource() {
		return source;
	}

	@Param(nameType=NameType.SECTION)
	public String getSectionName() {
		return sectionName;
	}

	@Param(nameType=NameType.LAYER)
	public String getHtmlLayerName() {
		return htmlLayerName;
	}

	@Param(nameType=NameType.FEATURE)
	public String getTagFeatureName() {
		return tagFeatureName;
	}

	public void setSource(SourceStream source) {
		this.source = source;
	}

	public void setSectionName(String sectionName) {
		this.sectionName = sectionName;
	}

	public void setHtmlLayerName(String htmlLayerName) {
		this.htmlLayerName = htmlLayerName;
	}

	public void setTagFeatureName(String tagFeatureName) {
		this.tagFeatureName = tagFeatureName;
	}
}
