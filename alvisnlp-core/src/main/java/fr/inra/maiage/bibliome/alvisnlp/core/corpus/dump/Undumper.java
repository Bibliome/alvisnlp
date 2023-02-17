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
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.logging.Logger;

import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Annotation;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Corpus;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Document;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Element;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Relation;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Section;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Tuple;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.dump.codec.CorpusDecoder;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.dump.codec.DocumentDecoder;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.dump.codec.LayerDecoder;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.dump.codec.RelationDecoder;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.dump.codec.SectionDecoder;
import fr.inra.maiage.bibliome.util.marshall.MapReadCache;
import fr.inra.maiage.bibliome.util.marshall.ReadCache;
import fr.inra.maiage.bibliome.util.marshall.StringCodec;
import fr.inra.maiage.bibliome.util.marshall.Unmarshaller;

public class Undumper implements AutoCloseable {
	private final Logger logger;
	private final FileChannel channel;
	private final int maxMmapSize;
	
	public Undumper(Logger logger, FileChannel channel, int maxMmapSize) {
		super();
		this.logger = logger;
		this.channel = channel;
		this.maxMmapSize = maxMmapSize;
	}
	
	public Undumper(Logger logger, Path path, int maxMmapSize) throws IOException {
		this(logger, FileChannel.open(path, StandardOpenOption.READ), maxMmapSize);
	}
	
	public Undumper(Logger logger, File file, int maxMmapSize) throws IOException {
		this(logger, file.toPath(), maxMmapSize);
	}
	
	@Override
	public void close() throws IOException {
		channel.close();
	}

	public void readCorpus(Corpus corpus) throws IOException {
		logger.info("undumping...");
		ReadCache<String> stringCache = MapReadCache.hashMap();
		Unmarshaller<String> stringUnmarshaller = new Unmarshaller<String>(channel, StringCodec.INSTANCE, stringCache, maxMmapSize);
		CorpusDecoder corpusDecoder = new CorpusDecoder(stringUnmarshaller, corpus, maxMmapSize);
		ReadCache<Corpus> corpusCache = MapReadCache.hashMap();
		Unmarshaller<Corpus> corpusUnmarshaller = new Unmarshaller<Corpus>(channel, corpusDecoder, corpusCache, maxMmapSize);
		corpusUnmarshaller.read((int) channel.position());
		processTuplesArguments(corpusDecoder, corpusUnmarshaller, stringUnmarshaller);
	}
	
	private static void processTuplesArguments(CorpusDecoder corpusDecoder, Unmarshaller<Corpus> corpusUnmarshaller, Unmarshaller<String> stringUnmarshaller) {
		ElementDereferencer argDeref = new ElementDereferencer(corpusDecoder, corpusUnmarshaller);
		Map<Long,Tuple> tuples = getAllTuples(corpusDecoder);
		for (Map.Entry<Long,Tuple> e : tuples.entrySet()) {
			long tRef = e.getKey();
			Tuple t = e.getValue();
			ByteBuffer buf = corpusUnmarshaller.getBuffer(tRef);
			processTupleArguments(buf, stringUnmarshaller, argDeref, t);
		}
	}
	
	private static Map<Long,Tuple> getAllTuples(CorpusDecoder corpusDecoder) {
		DocumentDecoder docDecoder = corpusDecoder.getDocDecoder();
		SectionDecoder secDecoder = docDecoder.getSectionDecoder();
		RelationDecoder relDecoder = secDecoder.getRelationDecoder();
		return relDecoder.getAllTuples();
	}

	private static void processTupleArguments(ByteBuffer buf, Unmarshaller<String> stringUnmarshaller, ElementDereferencer argDeref, Tuple t) {
		int arity = buf.getInt();
		for (int i = 0; i < arity; ++i) {
			long roleRef = buf.getLong();
			String role = stringUnmarshaller.read(roleRef);
			long argRef = buf.getLong();
			Element arg = argDeref.getElement(argRef);
			t.setArgument(role, arg);
		}
	}
	
	private static class ElementDereferencer {
		private final Collection<ReadCache<? extends Element>> caches = new ArrayList<ReadCache<? extends Element>>(6);
		
		private ElementDereferencer(CorpusDecoder corpusDecoder, Unmarshaller<Corpus> corpusUnmarshaller) {
			ReadCache<Corpus> corpusCache = corpusUnmarshaller.getCache();
			caches.add(corpusCache);
			Unmarshaller<Document> docUnmarshaller = corpusDecoder.getDocUnmarshaller();
			ReadCache<Document> docCache = docUnmarshaller.getCache();
			caches.add(docCache);
			DocumentDecoder docDecoder = corpusDecoder.getDocDecoder();
			Unmarshaller<Section> secUnmarshaller = docDecoder.getSectionUnmarshaller();
			ReadCache<Section> secCache = secUnmarshaller.getCache();
			caches.add(secCache);
			SectionDecoder secDecoder = docDecoder.getSectionDecoder();
			LayerDecoder layerDecoder = secDecoder.getLayerDecoder();
			Unmarshaller<Annotation> annUnmarshaller = layerDecoder.getAnnotationUnmarshaller();
			ReadCache<Annotation> annCache = annUnmarshaller.getCache();
			caches.add(annCache);
			Unmarshaller<Relation> relUnmarshaller = secDecoder.getRelationUnmarshaller();
			ReadCache<Relation> relCache = relUnmarshaller.getCache();
			caches.add(relCache);
			RelationDecoder relDecoder = secDecoder.getRelationDecoder();
			Unmarshaller<Tuple> tupleUnmarshaller = relDecoder.getTupleUnmarshaller();
			ReadCache<Tuple> tupleCache = tupleUnmarshaller.getCache();
			caches.add(tupleCache);
		}

		private Element getElement(long reference) {
			for (ReadCache<? extends Element> cache : caches) {
				Element result = cache.get(reference);
				if (result != null) {
					return result;
				}
			}
			throw new RuntimeException();
		}
	}
}
