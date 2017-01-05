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
import java.util.logging.Logger;

import org.bibliome.alvisnlp.modules.mapper.Mapper2.MapperResolvedObjects;
import org.bibliome.util.defaultmap.DefaultMap;
import org.bibliome.util.filelines.FileLines;
import org.bibliome.util.filelines.InvalidFileLineEntry;
import org.bibliome.util.streams.SourceStream;

import alvisnlp.corpus.Corpus;
import alvisnlp.corpus.Element;
import alvisnlp.corpus.NameType;
import alvisnlp.corpus.expressions.ResolverException;
import alvisnlp.module.ProcessingContext;
import alvisnlp.module.ProcessingException;
import alvisnlp.module.lib.AlvisNLPModule;
import alvisnlp.module.lib.Param;

/**
 * Maps features according to a mapping file.
 */
@AlvisNLPModule
public class FileMapper2 extends Mapper2<MapperResolvedObjects,List<String>> {
    private SourceStream                            mappingFile            = null;
    private Character                       separator              = '\t';
    private Integer keyColumn = 0;
    private String[] targetFeatures;

    private final FileLines<DefaultMap<String,List<List<String>>>> mapEntryLines = new FileLines<DefaultMap<String,List<List<String>>>>() {
        @Override
        public void processEntry(DefaultMap<String,List<List<String>>> data, int lineno, List<String> entry) throws InvalidFileLineEntry {
            String key = ignoreCase ? entry.get(keyColumn).toLowerCase() : entry.get(keyColumn);
            List<List<String>> values = data.safeGet(key);
            values.add(entry);
        }
    };

    @Override
	protected MapperResolvedObjects createResolvedObjects(ProcessingContext<Corpus> ctx) throws ResolverException {
		return new MapperResolvedObjects(ctx, this);
	}

	@Override
	public void fillMapping(DefaultMap<String, List<List<String>>> mapping, ProcessingContext<Corpus> ctx, Corpus corpus) throws ProcessingException {
        try {
            Logger logger = getLogger(ctx);
            mapEntryLines.setLogger(logger);
            mapEntryLines.getFormat().setSeparator(separator);
            BufferedReader r = mappingFile.getBufferedReader();
            mapEntryLines.process(r, mapping);
            r.close();
        }
        catch (IOException ioe) {
            rethrow(ioe);
        }
        catch (InvalidFileLineEntry ifle) {
            rethrow(ifle);
        }
	}

	@Override
	protected void handleMatch(Element target, List<String> value) {
		for (int i = 0; i < value.size() && i < targetFeatures.length; i++) {
			if (targetFeatures[i].isEmpty())
				continue;
			String s = value.get(i);
			if (s == null)
				continue;
			if (s.isEmpty())
				continue;
			target.addFeature(targetFeatures[i], s);
		}
	}

    @Param
    public SourceStream getMappingFile() {
        return mappingFile;
    }

    @Param(defaultDoc = "Separator character between map key and map values.")
    public Character getSeparator() {
        return separator;
    }

    @Param
    public Integer getKeyColumn() {
		return keyColumn;
	}

    @Param(nameType=NameType.FEATURE)
	public String[] getTargetFeatures() {
		return targetFeatures;
	}

	public void setTargetFeatures(String[] targetFeatures) {
		this.targetFeatures = targetFeatures;
	}

	public void setKeyColumn(Integer keyColumn) {
		this.keyColumn = keyColumn;
	}

	public void setMappingFile(SourceStream mappingFile) {
        this.mappingFile = mappingFile;
    }

    public void setSeparator(Character separator) {
        this.separator = separator;
    }

}
