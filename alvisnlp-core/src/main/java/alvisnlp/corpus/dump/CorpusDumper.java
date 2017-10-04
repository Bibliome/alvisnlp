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


package alvisnlp.corpus.dump;

import java.io.File;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Map;

import org.bibliome.util.marshall.MapWriteCache;
import org.bibliome.util.marshall.Marshaller;
import org.bibliome.util.marshall.WriteCache;

import alvisnlp.corpus.Annotation;
import alvisnlp.corpus.Corpus;
import alvisnlp.corpus.Document;
import alvisnlp.corpus.Element;
import alvisnlp.corpus.ElementVisitor;
import alvisnlp.corpus.Relation;
import alvisnlp.corpus.Section;
import alvisnlp.corpus.Tuple;
import alvisnlp.corpus.dump.codec.CorpusEncoder;
import alvisnlp.corpus.dump.codec.DocumentEncoder;
import alvisnlp.corpus.dump.codec.LayerEncoder;
import alvisnlp.corpus.dump.codec.RelationEncoder;
import alvisnlp.corpus.dump.codec.SectionEncoder;
import alvisnlp.module.Annotable;

public class CorpusDumper implements Annotable.Dumper<Corpus> {
	private final FileChannel channel;
	
	public CorpusDumper(FileChannel channel) {
		super();
		this.channel = channel;
	}
	
	public CorpusDumper(Path path) throws IOException {
		this(FileChannel.open(path, StandardOpenOption.READ, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.CREATE));
	}
	
	public CorpusDumper(File file) throws IOException {
		this(file.toPath());
	}

	@Override
	public void close() throws IOException {
		channel.close();
	}

	@Override
	public void dump(Corpus annotable) throws IOException {
		CorpusEncoder corpusEncoder = new CorpusEncoder(channel);
		WriteCache<Corpus> corpusCache = MapWriteCache.hashMap();
		Marshaller<Corpus> corpusMarshaller = new Marshaller<Corpus>(channel, corpusEncoder, corpusCache);
		corpusMarshaller.write(annotable);
		processTuplesArguments(corpusEncoder, corpusMarshaller);
	}

	private static void processTuplesArguments(CorpusEncoder corpusEncoder, Marshaller<Corpus> corpusMarshaller) throws IOException {
		FileChannel channel = corpusMarshaller.getChannel();
		ArgumentReferencer argReferencer = new ArgumentReferencer(corpusEncoder, corpusMarshaller);
		Map<Tuple,Integer> tuples = getAllTuples(corpusEncoder);
		for (Map.Entry<Tuple,Integer> e : tuples.entrySet()) {
			Tuple t = e.getKey();
			int tRef = e.getValue();
			processTupleArgument(channel, argReferencer, t, tRef);
		}
	}
	
	private static Map<Tuple,Integer> getAllTuples(CorpusEncoder corpusEncoder) {
		DocumentEncoder docEncoder = corpusEncoder.getDocEncoder();
		SectionEncoder secEncoder = docEncoder.getSectionEncoder();
		RelationEncoder relEncoder = secEncoder.getRelationEncoder();
		return relEncoder.getAllTuples();
	}
	
	private static void processTupleArgument(FileChannel channel, ArgumentReferencer argReferencer, Tuple t, int tRef) throws IOException {
		int arity = t.getArity();
		int argSz = 4 + (4 + 4) * arity;
		MappedByteBuffer buf = channel.map(MapMode.READ_WRITE, tRef, argSz);
		int checkArity = buf.getInt();
		if (arity != checkArity) {
			throw new RuntimeException();
		}
		for (Element arg : t.getAllArguments()) {
			buf.getInt(); // skip role
			int argRef = argReferencer.getReference(arg);
			buf.putInt(argRef);
		}
	}
	
	private static class ArgumentReferencer implements ElementVisitor<Integer,Void> {
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
		
		private int getReference(Element elt) {
			return elt.accept(this, null);
		}

		@Override
		public Integer visit(Annotation a, Void param) {
			return annotationCache.get(a);
		}

		@Override
		public Integer visit(Corpus corpus, Void param) {
			return corpusCache.get(corpus);
		}

		@Override
		public Integer visit(Document doc, Void param) {
			return documentCache.get(doc);
		}

		@Override
		public Integer visit(Relation rel, Void param) {
			return relationCache.get(rel);
		}

		@Override
		public Integer visit(Section sec, Void param) {
			return sectionCache.get(sec);
		}

		@Override
		public Integer visit(Tuple t, Void param) {
			return tupleCache.get(t);
		}

		@Override
		public Integer visit(Element e, Void param) {
			return e.getOriginal().accept(this, param);
		}
	}
}
