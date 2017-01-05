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



package org.bibliome.alvisnlp.modules.mapper;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.bibliome.util.defaultmap.DefaultArrayListHashMap;
import org.bibliome.util.defaultmap.DefaultMap;
import org.bibliome.util.filelines.FileLines;
import org.bibliome.util.filelines.InvalidFileLineEntry;
import org.bibliome.util.streams.SourceStream;

import alvisnlp.corpus.Corpus;
import alvisnlp.module.ProcessingContext;
import alvisnlp.module.ProcessingException;
import alvisnlp.module.lib.AlvisNLPModule;
import alvisnlp.module.lib.Param;

/**
 * Maps features according to a mapping file.
 */
@AlvisNLPModule(obsoleteUseInstead=FileMapper2.class)
public class FileMapper extends Mapper {
    private SourceStream                            mappingFile            = null;
    private Character                       separator              = '\t';

    private final FileLines<DefaultMap<String,List<List<String>>>> mapEntryLines = new FileLines<DefaultMap<String,List<List<String>>>>() {
        @Override
        public void processEntry(DefaultMap<String,List<List<String>>> data, int lineno, List<String> entry) throws InvalidFileLineEntry {
            String key = ignoreCase ? entry.get(0).toLowerCase() : entry.get(0);
            List<List<String>> values = data.safeGet(key);
            values.add(entry);
        }
    };
    
    public FileMapper() {
        super();
    }

    @Override
    public String[] addFeaturesToSectionFilter() {
        return new String[] {};
    }

    @Override
	public Map<String,List<List<String>>> getMapping(ProcessingContext<Corpus> ctx) throws ProcessingException {
        try {
            Logger logger = getLogger(ctx);
            mapEntryLines.setLogger(logger);
            mapEntryLines.getFormat().setSeparator(separator);
            DefaultMap<String,List<List<String>>> result = new DefaultArrayListHashMap<String,List<String>>();
            BufferedReader r = mappingFile.getBufferedReader();
            mapEntryLines.process(r, result);
            r.close();
            return result;
        }
        catch (IOException ioe) {
            rethrow(ioe);
        }
        catch (InvalidFileLineEntry ifle) {
            rethrow(ifle);
        }
        return null;
    }

    @Param
    public SourceStream getMappingFile() {
        return mappingFile;
    }

    @Param(defaultDoc = "Separator character between map key and map values.")
    public Character getSeparator() {
        return separator;
    }

    public void setMappingFile(SourceStream mappingFile) {
        this.mappingFile = mappingFile;
    }

    public void setSeparator(Character separator) {
        this.separator = separator;
    }

}
