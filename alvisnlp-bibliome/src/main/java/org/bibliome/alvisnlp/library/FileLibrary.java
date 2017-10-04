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


package org.bibliome.alvisnlp.library;

import java.io.File;

import alvisnlp.corpus.expressions.Function;
import alvisnlp.corpus.expressions.FunctionLibrary;
import alvisnlp.corpus.expressions.Library;

@Library("file")
public abstract class FileLibrary extends FunctionLibrary {
	@Function
	public static final boolean exists(String path) {
		return new File(path).exists();
	}
	
	@Function
	public static final boolean regular(String path) {
		return new File(path).isFile();
	}
	
	@Function
	public static final boolean dir(String path) {
		return new File(path).isDirectory();
	}
}
