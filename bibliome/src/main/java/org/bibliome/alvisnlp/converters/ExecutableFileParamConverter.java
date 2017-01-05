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

import org.bibliome.util.files.ExecutableFile;

import alvisnlp.converters.lib.Converter;

@Converter(targetType=ExecutableFile.class)
public class ExecutableFileParamConverter extends AbstractInputFileParamConverter<ExecutableFile> {
	@Override
	public ExecutableFile createFile(String path) {
		return new ExecutableFile(path);
	}

	@Override
	public ExecutableFile createFile(String parent, String path) {
		return new ExecutableFile(parent, path);
	}
}
