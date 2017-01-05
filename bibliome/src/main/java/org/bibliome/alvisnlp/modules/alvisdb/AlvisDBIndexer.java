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


package org.bibliome.alvisnlp.modules.alvisdb;

import java.io.IOException;
import java.util.logging.Logger;

import org.apache.lucene.analysis.KeywordAnalyzer;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.bibliome.alvisnlp.modules.CorpusModule;
import org.bibliome.alvisnlp.modules.ResolvedObjects;
import org.bibliome.alvisnlp.modules.alvisdb.AlvisDBIndexer.AlvisDBIndexerResolvedObjects;
import org.bibliome.util.files.OutputDirectory;

import alvisnlp.corpus.Corpus;
import alvisnlp.corpus.expressions.EvaluationContext;
import alvisnlp.corpus.expressions.ResolverException;
import alvisnlp.module.ModuleException;
import alvisnlp.module.NameUsage;
import alvisnlp.module.ProcessingContext;
import alvisnlp.module.lib.AlvisNLPModule;
import alvisnlp.module.lib.Param;

@AlvisNLPModule(beta=true)
public class AlvisDBIndexer extends CorpusModule<AlvisDBIndexerResolvedObjects>{
	private OutputDirectory indexDir;
	private Boolean append = false;
	private ADBElements[] elements;
	
	static class AlvisDBIndexerResolvedObjects extends ResolvedObjects {
		private final ADBElements.Resolved[] elements;
		
		AlvisDBIndexerResolvedObjects(ProcessingContext<Corpus> ctx, AlvisDBIndexer module) throws ResolverException {
			super(ctx, module);
			this.elements = rootResolver.resolveArray(module.elements, ADBElements.Resolved.class);
		}

		@Override
		public void collectUsedNames(NameUsage nameUsage, String defaultType) throws ModuleException {
			super.collectUsedNames(nameUsage, defaultType);
			nameUsage.collectUsedNamesArray(elements, defaultType);
		}
	}

	@Override
	public void process(ProcessingContext<Corpus> ctx, Corpus corpus) throws ModuleException {
		IndexWriterConfig writerConfig = new IndexWriterConfig(Version.LUCENE_36, new KeywordAnalyzer());
		writerConfig.setOpenMode(append ? OpenMode.CREATE_OR_APPEND : OpenMode.CREATE);
		try (Directory dir = FSDirectory.open(indexDir)) {
			try (IndexWriter writer = new IndexWriter(dir, writerConfig)) {
				AlvisDBIndexerResolvedObjects resObj = getResolvedObjects();
				Logger logger = getLogger(ctx);
				EvaluationContext evalCtx = new EvaluationContext(logger);
				for (ADBElements.Resolved ent : resObj.elements) {
					ent.indexElements(logger, writer, evalCtx, corpus);
				}
			}
		}
		catch (IOException e) {
			rethrow(e);
		}
	}

	@Override
	protected AlvisDBIndexerResolvedObjects createResolvedObjects(ProcessingContext<Corpus> ctx) throws ResolverException {
		return new AlvisDBIndexerResolvedObjects(ctx, this);
	}

	@Param
	public OutputDirectory getIndexDir() {
		return indexDir;
	}

	@Param
	public ADBElements[] getElements() {
		return elements;
	}

	@Param
	public Boolean getAppend() {
		return append;
	}

	public void setAppend(Boolean append) {
		this.append = append;
	}

	public void setElements(ADBElements[] elements) {
		this.elements = elements;
	}

	public void setIndexDir(OutputDirectory indexDir) {
		this.indexDir = indexDir;
	}
}
