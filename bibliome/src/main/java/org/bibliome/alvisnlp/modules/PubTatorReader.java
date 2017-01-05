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
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bibliome.util.Iterators;
import org.bibliome.util.Strings;
import org.bibliome.util.streams.SourceStream;

import alvisnlp.corpus.Annotation;
import alvisnlp.corpus.Corpus;
import alvisnlp.corpus.Document;
import alvisnlp.corpus.Layer;
import alvisnlp.corpus.NameType;
import alvisnlp.corpus.Section;
import alvisnlp.corpus.creators.AnnotationCreator;
import alvisnlp.corpus.creators.DocumentCreator;
import alvisnlp.corpus.creators.SectionCreator;
import alvisnlp.corpus.expressions.ResolverException;
import alvisnlp.module.ModuleException;
import alvisnlp.module.ProcessingContext;
import alvisnlp.module.ProcessingException;
import alvisnlp.module.lib.AlvisNLPModule;
import alvisnlp.module.lib.Param;

@AlvisNLPModule(beta=true)
public abstract class PubTatorReader extends CorpusModule<ResolvedObjects> implements DocumentCreator, SectionCreator, AnnotationCreator {
	private static final Pattern CONTENTS_LINE = Pattern.compile("(\\d+)\\|(\\w+)\\|(.*)");
	
	private SourceStream sourcePath;
	private String typeFeature = "type";
	private String classFeature = "class";
	private String offsetFeature = "offset";

	@Override
	public void process(ProcessingContext<Corpus> ctx, Corpus corpus) throws ModuleException {
		Logger logger = getLogger(ctx);
		try {
			for (BufferedReader reader : Iterators.loop(sourcePath.getBufferedReaders())) {
				read(logger, corpus, reader);
			}
		}
		catch (IOException e) {
			rethrow(e);
		}
	}
	
	private void read(Logger logger, Corpus corpus, BufferedReader reader) throws IOException, ProcessingException {
		String currentDocId = null;
		int offset = 0;
		while (true) {
			String line = reader.readLine();
			if (line == null) {
				break;
			}
			if (line.trim().isEmpty()) {
				continue;
			}
			Matcher m = CONTENTS_LINE.matcher(line);
			if (m.matches()) {
				String docId = m.group(1);
				if (!docId.equals(currentDocId)) {
					currentDocId = docId;
					offset = 0;
				}
				Document doc = Document.getDocument(this, corpus, docId);
				String sectionName = m.group(2);
				String contents = m.group(3);
				Section sec = new Section(this, doc, sectionName, contents + "\n");
				sec.addFeature(offsetFeature, Integer.toString(offset));
				offset += contents.length() + 1;
				continue;
			}
			List<String> cols = Strings.split(line, '\t', 0);
			String docId = cols.get(0);
			Document doc = corpus.getDocument(docId);
			try {
				int start = Integer.parseInt(cols.get(1));
				int end = Integer.parseInt(cols.get(2));
				Iterator<Section> secIt = doc.sectionIterator();
				Section sec = null;
				while (secIt.hasNext()) {
					Section secCand = secIt.next();
					String contents = secCand.getContents();
					int len = contents.length();
					if (start < len) {
						if (end > len) {
							processingException("bad coordinates: " + cols.get(1) + "-" + cols.get(0));
						}
						sec = secCand;
						break;
					}
					start -= len;
					end -= len;
				}
				if (sec == null) {
					processingException("bad coordinates: " + cols.get(1) + "-" + cols.get(0));
				}
				String layerName = cols.get(4);
				Layer layer = sec.ensureLayer(layerName);
				Annotation a = new Annotation(this, layer, start, end);
				String formCheck = cols.get(3);
				if (!formCheck.equals(a.getForm())) {
					processingException("failed form check " + formCheck + " / " + a.getForm());
				}
				a.addFeature(typeFeature, layerName);
				a.addFeature(classFeature, cols.get(5));
			}
			catch (NumberFormatException e) {
				logger.finer("relation " + cols.get(1) + " ignored");
			}
		}
	}

	@Override
	protected ResolvedObjects createResolvedObjects(ProcessingContext<Corpus> ctx) throws ResolverException {
		return new ResolvedObjects(ctx, this);
	}

	@Param
	public SourceStream getSourcePath() {
		return sourcePath;
	}

	@Param(nameType=NameType.FEATURE)
	public String getTypeFeature() {
		return typeFeature;
	}

	@Param(nameType=NameType.FEATURE)
	public String getClassFeature() {
		return classFeature;
	}

	@Param(nameType=NameType.FEATURE)
	public String getOffsetFeature() {
		return offsetFeature;
	}

	public void setOffsetFeature(String offsetFeature) {
		this.offsetFeature = offsetFeature;
	}

	public void setSourcePath(SourceStream sourcePath) {
		this.sourcePath = sourcePath;
	}

	public void setTypeFeature(String typeFeature) {
		this.typeFeature = typeFeature;
	}

	public void setClassFeature(String className) {
		this.classFeature = className;
	}
}
