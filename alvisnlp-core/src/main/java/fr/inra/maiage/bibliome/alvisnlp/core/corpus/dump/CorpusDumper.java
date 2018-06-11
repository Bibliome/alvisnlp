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


package fr.inra.maiage.bibliome.alvisnlp.core.corpus.dump;

import java.io.File;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Map;
import java.util.logging.Logger;

import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Annotation;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Corpus;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Document;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Element;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.ElementVisitor;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Relation;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Section;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Tuple;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.dump.codec.CorpusEncoder;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.dump.codec.DocumentEncoder;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.dump.codec.LayerEncoder;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.dump.codec.RelationEncoder;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.dump.codec.SectionEncoder;
import fr.inra.maiage.bibliome.alvisnlp.core.module.Annotable;
import fr.inra.maiage.bibliome.util.marshall.Encoder;
import fr.inra.maiage.bibliome.util.marshall.MapWriteCache;
import fr.inra.maiage.bibliome.util.marshall.Marshaller;
import fr.inra.maiage.bibliome.util.marshall.WriteCache;

public class CorpusDumper implements Annotable.Dumper<Corpus> {
	private final Logger logger;
	private final FileChannel channel;
	
	public CorpusDumper(Logger logger, FileChannel channel) {
		super();
		this.logger = logger;
		this.channel = channel;
	}
	
	public CorpusDumper(Logger logger, Path path) throws IOException {
		this(logger, FileChannel.open(path, StandardOpenOption.READ, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.CREATE));
	}
	
	public CorpusDumper(Logger logger, File file) throws IOException {
		this(logger, file.toPath());
	}

	@Override
	public void close() throws IOException {
		channel.close();
	}

	@Override
	public void dump(Corpus annotable) throws IOException {
		logger.info("dumping...");
		CorpusEncoder corpusEncoder = new CorpusEncoder(channel);
		WriteCache<Corpus> corpusCache = MapWriteCache.hashMap();
		Marshaller<Corpus> corpusMarshaller = new Marshaller<Corpus>(channel, corpusEncoder, corpusCache);
		corpusMarshaller.write(annotable);
		processTuplesArguments(corpusEncoder, corpusMarshaller);
	}

	private static void processTuplesArguments(CorpusEncoder corpusEncoder, Marshaller<Corpus> corpusMarshaller) throws IOException {
		FileChannel channel = corpusMarshaller.getChannel();
		ArgumentReferencer argReferencer = new ArgumentReferencer(corpusEncoder, corpusMarshaller);
		Map<Tuple,Long> tuples = getAllTuples(corpusEncoder);
		for (Map.Entry<Tuple,Long> e : tuples.entrySet()) {
			Tuple t = e.getKey();
			long tRef = e.getValue();
			processTupleArgument(channel, argReferencer, t, tRef);
		}
	}
	
	private static Map<Tuple,Long> getAllTuples(CorpusEncoder corpusEncoder) {
		DocumentEncoder docEncoder = corpusEncoder.getDocEncoder();
		SectionEncoder secEncoder = docEncoder.getSectionEncoder();
		RelationEncoder relEncoder = secEncoder.getRelationEncoder();
		return relEncoder.getAllTuples();
	}
	
	private static void processTupleArgument(FileChannel channel, ArgumentReferencer argReferencer, Tuple t, long tRef) throws IOException {
		int arity = t.getArity();
		int argSz = 4 + (Encoder.REFERENCE_SIZE + Encoder.REFERENCE_SIZE) * arity;
		MappedByteBuffer buf = channel.map(MapMode.READ_WRITE, tRef, argSz);
		int checkArity = buf.getInt();
		if (arity != checkArity) {
			throw new RuntimeException();
		}
		for (Element arg : t.getAllArguments()) {
			buf.getLong(); // skip role
			long argRef = argReferencer.getReference(arg);
			buf.putLong(argRef);
		}
	}
	
	private static class ArgumentReferencer implements ElementVisitor<Long,Void> {
		private final WriteCache<Corpus> corpusCache;
		private final WriteCache<Document> documentCache;
		private final WriteCache<Section> sectionCache;
		private final WriteCache<Annotation> annotationCache;
		private final WriteCache<Relation> relationCache;
		private final WriteCache<Tuple> tupleCache;
		
		private ArgumentReferencer(CorpusEncoder corpusEncoder, Marshaller<Corpus> corpusMarshaller) {
			super();
			this.corpusCache = corpusMarshaller.getCache();
			Marshaller<Document> docMarshaller = corpusEncoder.getDocMarshaller();
			this.documentCache = docMarshaller.getCache();
			DocumentEncoder docEncoder = corpusEncoder.getDocEncoder();
			Marshaller<Section> secMarshaller = docEncoder.getSectionMarshaller();
			this.sectionCache = secMarshaller.getCache();
			SectionEncoder secEncoder = docEncoder.getSectionEncoder();
			LayerEncoder layerEncoder = secEncoder.getLayerEncoder();
			Marshaller<Annotation> aMarshaller = layerEncoder.getAnnotationMarshaller();
			this.annotationCache = aMarshaller.getCache();
			Marshaller<Relation> relMarshaller = secEncoder.getRelationMarshaller();
			this.relationCache = relMarshaller.getCache();
			RelationEncoder relEncoder = secEncoder.getRelationEncoder();
			Marshaller<Tuple> tMarshaller = relEncoder.getTupleMarshaller();
			this.tupleCache = tMarshaller.getCache();
		}
		
		private long getReference(Element elt) {
			return elt.accept(this, null);
		}

		@Override
		public Long visit(Annotation a, Void param) {
			return annotationCache.get(a);
		}

		@Override
		public Long visit(Corpus corpus, Void param) {
			return corpusCache.get(corpus);
		}

		@Override
		public Long visit(Document doc, Void param) {
			return documentCache.get(doc);
		}

		@Override
		public Long visit(Relation rel, Void param) {
			return relationCache.get(rel);
		}

		@Override
		public Long visit(Section sec, Void param) {
			return sectionCache.get(sec);
		}

		@Override
		public Long visit(Tuple t, Void param) {
			return tupleCache.get(t);
		}

		@Override
		public Long visit(Element e, Void param) {
			return e.getOriginal().accept(this, param);
		}
	}
}
