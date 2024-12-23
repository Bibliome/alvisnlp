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



package fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.mapper;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

import fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.mapper.Mapper.MapperResolvedObjects;

import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Corpus;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Element;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.NameType;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.ResolverException;
import fr.inra.maiage.bibliome.alvisnlp.core.module.ProcessingContext;
import fr.inra.maiage.bibliome.alvisnlp.core.module.ProcessingException;
import fr.inra.maiage.bibliome.alvisnlp.core.module.lib.AlvisNLPModule;
import fr.inra.maiage.bibliome.alvisnlp.core.module.lib.Param;
import fr.inra.maiage.bibliome.util.Checkable;
import fr.inra.maiage.bibliome.util.defaultmap.DefaultMap;
import fr.inra.maiage.bibliome.util.filelines.FileLines;
import fr.inra.maiage.bibliome.util.filelines.InvalidFileLineEntry;
import fr.inra.maiage.bibliome.util.streams.SourceStream;

/**
 * Maps features according to a mapping file.
 */

@AlvisNLPModule
public class FileMapper extends Mapper<MapperResolvedObjects,List<String>> implements Checkable {
    private SourceStream mappingFile = null;
    private Character separator = '\t';
    private Integer keyColumn = 0;
    private String[] targetFeatures;
    private Boolean headerLine = false;

    private final FileLines<DefaultMap<String,List<List<String>>>> mapEntryLines = new FileLines<DefaultMap<String,List<List<String>>>>() {
        @Override
        public void processEntry(DefaultMap<String,List<List<String>>> data, int lineno, List<String> entry) throws InvalidFileLineEntry {
        	if (headerLine && (lineno == 1)) {
        		targetFeatures = entry.toArray(new String[entry.size()]);
        	}
        	else {
        		String key = ignoreCase ? entry.get(keyColumn).toLowerCase() : entry.get(keyColumn);
        		List<List<String>> values = data.safeGet(key);
        		values.add(entry);
        	}
        }
    };

    @Override
	protected MapperResolvedObjects createResolvedObjects(ProcessingContext ctx) throws ResolverException {
		return new MapperResolvedObjects(ctx, this);
	}

	@Override
	public boolean check(Logger logger) {
		if (headerLine && (targetFeatures != null)) {
			logger.warning("targetFeatures will be ignored since headerLine is set");
		}
		if ((!headerLine) && (targetFeatures == null)) {
			logger.severe("either targetFeatures or headerLine must be set");
			return false;
		}
		return true;
	}

	@Override
	public void fillMapping(DefaultMap<String, List<List<String>>> mapping, ProcessingContext ctx, Corpus corpus) throws ProcessingException {
        try {
            Logger logger = getLogger(ctx);
            mapEntryLines.setLogger(logger);
            mapEntryLines.getFormat().setSeparator(separator);
            BufferedReader r = mappingFile.getBufferedReader();
            mapEntryLines.process(r, mapping);
            r.close();
        }
        catch (IOException|InvalidFileLineEntry e) {
			throw new ProcessingException(e);
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

    @Param
    public Character getSeparator() {
        return separator;
    }

    @Param
    public Integer getKeyColumn() {
		return keyColumn;
	}

    @Param(mandatory = false, nameType=NameType.FEATURE)
	public String[] getTargetFeatures() {
		return targetFeatures;
	}

    @Param
	public Boolean getHeaderLine() {
		return headerLine;
	}

	public void setHeaderLine(Boolean headerLine) {
		this.headerLine = headerLine;
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
