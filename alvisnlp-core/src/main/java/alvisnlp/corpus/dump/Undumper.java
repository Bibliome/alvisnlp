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
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import org.bibliome.util.marshall.MapReadCache;
import org.bibliome.util.marshall.ReadCache;
import org.bibliome.util.marshall.StringCodec;
import org.bibliome.util.marshall.Unmarshaller;

import alvisnlp.corpus.Annotation;
import alvisnlp.corpus.Corpus;
import alvisnlp.corpus.Document;
import alvisnlp.corpus.Element;
import alvisnlp.corpus.Relation;
import alvisnlp.corpus.Section;
import alvisnlp.corpus.Tuple;
import alvisnlp.corpus.dump.codec.CorpusDecoder;
import alvisnlp.corpus.dump.codec.DocumentDecoder;
import alvisnlp.corpus.dump.codec.LayerDecoder;
import alvisnlp.corpus.dump.codec.RelationDecoder;
import alvisnlp.corpus.dump.codec.SectionDecoder;

public class Undumper implements AutoCloseable {
	private final FileChannel channel;
	
	public Undumper(FileChannel channel) {
		super();
		this.channel = channel;
	}
	
	public Undumper(Path path) throws IOException {
		this(FileChannel.open(path, StandardOpenOption.READ));
	}
	
	public Undumper(File file) throws IOException {
		this(file.toPath());
	}
	
	@Override
	public void close() throws IOException {
		channel.close();
	}

	public Corpus readCorpus() throws IOException {
		ReadCache<String> stringCache = MapReadCache.hashMap();
		Unmarshaller<String> stringUnmarshaller = new Unmarshaller<String>(channel, StringCodec.INSTANCE, stringCache);
		CorpusDecoder corpusDecoder = new CorpusDecoder(stringUnmarshaller);
		ReadCache<Corpus> corpusCache = MapReadCache.hashMap();
		Unmarshaller<Corpus> corpusUnmarshaller = new Unmarshaller<Corpus>(channel, corpusDecoder, corpusCache);
		Corpus result = corpusUnmarshaller.read((int) channel.position());
		processTuplesArguments(corpusDecoder, corpusUnmarshaller, stringUnmarshaller);
		return result;
	}
	
	private static void processTuplesArguments(CorpusDecoder corpusDecoder, Unmarshaller<Corpus> corpusUnmarshaller, Unmarshaller<String> stringUnmarshaller) {
		ByteBuffer buf = corpusUnmarshaller.getBuffer();
		ElementDereferencer argDeref = new ElementDereferencer(corpusDecoder, corpusUnmarshaller);
		Map<Integer,Tuple> tuples = getAllTuples(corpusDecoder);
		for (Map.Entry<Integer,Tuple> e : tuples.entrySet()) {
			int tRef = e.getKey();
			Tuple t = e.getValue();
			processTupleArguments(buf, stringUnmarshaller, argDeref, t, tRef);
		}
	}
	
	private static Map<Integer,Tuple> getAllTuples(CorpusDecoder corpusDecoder) {
		DocumentDecoder docDecoder = corpusDecoder.getDocDecoder();
		SectionDecoder secDecoder = docDecoder.getSectionDecoder();
		RelationDecoder relDecoder = secDecoder.getRelationDecoder();
		return relDecoder.getAllTuples();
	}
	
	private static void processTupleArguments(ByteBuffer buf, Unmarshaller<String> stringUnmarshaller, ElementDereferencer argDeref, Tuple t, int ref) {
		buf.position(ref);
		int arity = buf.getInt();
		for (int i = 0; i < arity; ++i) {
			int roleRef = buf.getInt();
			String role = stringUnmarshaller.read(roleRef);
			int argRef = buf.getInt();
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

		private Element getElement(int reference) {
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
