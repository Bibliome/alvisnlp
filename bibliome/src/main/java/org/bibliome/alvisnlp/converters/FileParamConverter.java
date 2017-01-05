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


package org.bibliome.alvisnlp.converters;

import java.io.File;

import alvisnlp.converters.lib.Converter;
import alvisnlp.converters.lib.SimpleParamConverter;

@Converter(targetType = File.class)
public class FileParamConverter extends SimpleParamConverter<File> {

    private static final String FILE_ATTRIBUTE = "file";
    private static final String PATH_ATTRIBUTE = "path";

    @Override
    public File convertTrimmed(String stringValue) {
        return new File(stringValue.trim());
    }

    @Override
    public String[] getAlternateAttributes() {
        return new String[] { FILE_ATTRIBUTE, PATH_ATTRIBUTE };
    }
}
