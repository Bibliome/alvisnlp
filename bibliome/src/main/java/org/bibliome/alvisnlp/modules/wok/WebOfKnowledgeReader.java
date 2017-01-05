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


package org.bibliome.alvisnlp.modules.wok;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Collection;
import java.util.logging.Logger;

import org.bibliome.alvisnlp.modules.CorpusModule;
import org.bibliome.alvisnlp.modules.ResolvedObjects;
import org.bibliome.util.Strings;
import org.bibliome.util.streams.SourceStream;

import alvisnlp.corpus.Corpus;
import alvisnlp.corpus.NameType;
import alvisnlp.corpus.creators.DocumentCreator;
import alvisnlp.corpus.creators.SectionCreator;
import alvisnlp.corpus.expressions.ResolverException;
import alvisnlp.module.ModuleException;
import alvisnlp.module.NameUsage;
import alvisnlp.module.ProcessingContext;
import alvisnlp.module.ProcessingException;
import alvisnlp.module.TimerCategory;
import alvisnlp.module.lib.AlvisNLPModule;
import alvisnlp.module.lib.Param;
import alvisnlp.module.lib.TimeThis;

@AlvisNLPModule
public abstract class WebOfKnowledgeReader extends CorpusModule<ResolvedObjects> implements DocumentCreator, SectionCreator {
	private SourceStream source;
	private Boolean tabularFormat = false;

    @Override
	protected ResolvedObjects createResolvedObjects(ProcessingContext<Corpus> ctx) throws ResolverException {
		return new ResolvedObjects(ctx, this);
	}

	@Override
	public void process(ProcessingContext<Corpus> ctx, Corpus corpus) throws ModuleException {
		try (BufferedReader reader = source.getBufferedReader()) {
			WoKReaderStatus status = new WoKReaderStatus(this, corpus);
			while (true) {
				String line = reader.readLine();
				if (line == null)
					break;
				if (line.trim().isEmpty())
					continue;
				read(ctx, status, line);
			}
			Logger logger = getLogger(ctx);
			logger.info("created, " + status.getDocumentCount() + " documents");
			Collection<String> unhandledFields = status.getUnhandledFields();
			if (!unhandledFields.isEmpty()) {
				logger.warning("unhandled fields, " + Strings.join(unhandledFields, ", "));
			}
		}
		catch (IOException e) {
			rethrow(e);
		}
	}
	
	@TimeThis(task="read", category=TimerCategory.LOAD_RESOURCE)
	protected void read(@SuppressWarnings("unused") ProcessingContext<Corpus> corpus, WoKReaderStatus status, String line) throws ProcessingException {
		if (tabularFormat) {
			status.readTabularLine(line);
		}
		else {
			status.readLine(line);
		}
	}
	
	@Override
	public void collectUsedNames(NameUsage nameUsage, String defaultType) throws ModuleException {
		nameUsage.addNames(NameType.SECTION, "TI", "AB", "FX");
		nameUsage.addNames(NameType.FEATURE, "AU", "AF", "BA", "BF", "CA", "GP", "BE", "SO", "SE",
				"BS", "LA", "CT", "CY", "CL", "SP", "HO", "C1", "RP", "EM", "RI", "OI", "FU",
				"TC", "Z9", "PU", "PI", "PA", "SN", "BN", "J9", "JI", "PD", "PY", "VL", "IS", "PN",
				"SU", "MA", "BP", "EP", "AR", "DI", "D2", "PG", "P2", "GA", "UT", "SI", "NR", "DE",
				"DT", "ID", "CR", "WC", "SC");
	}

	@Param
	public SourceStream getSource() {
		return source;
	}

	@Param
	public Boolean getTabularFormat() {
		return tabularFormat;
	}

	public void setTabularFormat(Boolean tabularFormat) {
		this.tabularFormat = tabularFormat;
	}

	public void setSource(SourceStream source) {
		this.source = source;
	}
}
