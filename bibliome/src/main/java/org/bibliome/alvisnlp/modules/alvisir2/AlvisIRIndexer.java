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


package org.bibliome.alvisnlp.modules.alvisir2;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.logging.Logger;

import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.bibliome.alvisnlp.modules.CorpusModule;
import org.bibliome.alvisnlp.modules.ResolvedObjects;
import org.bibliome.alvisnlp.modules.alvisir2.AlvisIRIndexer.AlvisIRIndexerResolvedObjects;
import org.bibliome.util.Files;
import org.bibliome.util.files.OutputDirectory;

import alvisnlp.corpus.Corpus;
import alvisnlp.corpus.expressions.EvaluationContext;
import alvisnlp.corpus.expressions.ResolverException;
import alvisnlp.module.ModuleException;
import alvisnlp.module.NameUsage;
import alvisnlp.module.ProcessingContext;
import alvisnlp.module.lib.AlvisNLPModule;
import alvisnlp.module.lib.Param;
import alvisnlp.module.types.MultiMapping;
import fr.inra.mig_bibliome.alvisir.core.AlvisIRConstants;
import fr.inra.mig_bibliome.alvisir.core.index.DocumentsIndexer;
import fr.inra.mig_bibliome.alvisir.core.index.IndexGlobalAttributes;

@AlvisNLPModule
public class AlvisIRIndexer extends CorpusModule<AlvisIRIndexerResolvedObjects> {
	private IndexedDocuments documents;
	private OutputDirectory indexDir;
	private Integer tokenPositionGap = AlvisIRConstants.DEFAULT_TOKEN_POSITION_GAP;
	private MultiMapping relations = new MultiMapping();
	private String[] fieldNames;
	private String[] propertyKeys = new String[] {};
	private Boolean recordGlobalIndexAttributes = true;
	private Boolean clearIndex = true;

	static class AlvisIRIndexerResolvedObjects extends ResolvedObjects {
		private final IndexedDocumentsEvaluator documents;
	
		private AlvisIRIndexerResolvedObjects(ProcessingContext<Corpus> ctx, AlvisIRIndexer module) throws ResolverException {
			super(ctx, module);
			documents = module.documents.resolveExpressions(rootResolver);
		}

		@Override
		public void collectUsedNames(NameUsage nameUsage, String defaultType) throws ModuleException {
			super.collectUsedNames(nameUsage, defaultType);
			documents.collectUsedNames(nameUsage, defaultType);
		}
	}
	
	@Override
	protected AlvisIRIndexerResolvedObjects createResolvedObjects(ProcessingContext<Corpus> ctx) throws ResolverException {
		return new AlvisIRIndexerResolvedObjects(ctx, this);
	}

	@Override
	public void process(ProcessingContext<Corpus> ctx, Corpus corpus) throws ModuleException {
		Logger logger = getLogger(ctx);

		IndexGlobalAttributes globalAttributes = new IndexGlobalAttributes(tokenPositionGap, propertyKeys);
		globalAttributes.addRelations(relations);
		globalAttributes.setFieldNames(new LinkedHashSet<String>(Arrays.asList(fieldNames)));

		AlvisIRIndexerResolvedObjects resObj = getResolvedObjects();
		resObj.documents.setCorpus(corpus);
		resObj.documents.setEvaluationContext(new EvaluationContext(logger));
		resObj.documents.setRoleNames(getNamesIndex(globalAttributes.getAllRoleNames()));
		resObj.documents.setPropertyKeys(getNamesIndex(globalAttributes.getPropertyKeys()));
		IndexWriterConfig conf = new IndexWriterConfig(Version.LUCENE_36, null);
		try (Directory indexDir = FSDirectory.open(this.indexDir)) {
			if (IndexReader.indexExists(indexDir)) {
				if (clearIndex) {
					logger.info("clearing index directory: " + this.indexDir.getAbsolutePath());
					if (!Files.deleteDir(this.indexDir)) {
						logger.warning("could not clear index directory " + this.indexDir.getAbsolutePath());
					}
				}
				else {
					logger.warning("index directory exists: " + this.indexDir.getAbsolutePath());
				}
			}
			try (IndexWriter indexWriter = new IndexWriter(indexDir, conf)) {
				if (recordGlobalIndexAttributes) {
					logger.info("recording global attributes");
					DocumentsIndexer.recordGlobalIndexAttributes(indexWriter, globalAttributes);
				}
				logger.info("indexing documents");
				DocumentsIndexer.index(indexWriter, globalAttributes, resObj.documents);
			}
			catch (IOException e) {
				rethrow(e);
			}
		}
		catch (IOException e) {
			rethrow(e);
		}
	}

	private static Map<String,Integer> getNamesIndex(String[] names) {
		Map<String,Integer> result = new HashMap<String,Integer>();
		for (String s : names) {
			if (!result.containsKey(s)) {
				result.put(s, result.size());
			}
		}
		return result;		
	}

	@Param
	public Integer getTokenPositionGap() {
		return tokenPositionGap;
	}

	@Param
	public IndexedDocuments getDocuments() {
		return documents;
	}

	@Param
	public OutputDirectory getIndexDir() {
		return indexDir;
	}

	@Param
	public Boolean getRecordGlobalIndexAttributes() {
		return recordGlobalIndexAttributes;
	}

	@Param
	public Boolean getClearIndex() {
		return clearIndex;
	}

	@Param
	public MultiMapping getRelations() {
		return relations;
	}

	@Param
	public String[] getFieldNames() {
		return fieldNames;
	}

	@Param
	public String[] getPropertyKeys() {
		return propertyKeys;
	}

	public void setPropertyKeys(String[] propertyKeys) {
		this.propertyKeys = propertyKeys;
	}

	public void setRelations(MultiMapping relations) {
		this.relations = relations;
	}

	public void setFieldNames(String[] fieldNames) {
		this.fieldNames = fieldNames;
	}

	public void setClearIndex(Boolean clearIndex) {
		this.clearIndex = clearIndex;
	}

	public void setRecordGlobalIndexAttributes(Boolean recordGlobalIndexAttributes) {
		this.recordGlobalIndexAttributes = recordGlobalIndexAttributes;
	}

	public void setDocuments(IndexedDocuments documents) {
		this.documents = documents;
	}

	public void setIndexDir(OutputDirectory indexDir) {
		this.indexDir = indexDir;
	}

	public void setTokenPositionGap(Integer tokenPositionGap) {
		this.tokenPositionGap = tokenPositionGap;
	}
}
