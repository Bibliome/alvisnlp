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


package org.bibliome.alvisnlp.library.standard;

import java.io.File;
import java.util.Iterator;
import java.util.logging.Logger;

import org.bibliome.util.Iterators;

import alvisnlp.corpus.Corpus;
import alvisnlp.corpus.Element;
import alvisnlp.corpus.expressions.Function;
import alvisnlp.corpus.expressions.FunctionLibrary;
import alvisnlp.corpus.expressions.Library;
import alvisnlp.module.Module;
import alvisnlp.module.ProcessingContext;
import alvisnlp.module.Sequence;
import alvisnlp.module.lib.ModuleBase;

@Library("module")
public abstract class ModuleLibrary extends FunctionLibrary {
	public static final String NAME = "module";

	private ProcessingContext<Corpus> ctx;
	private Module<Corpus> module;
	
	public ProcessingContext<Corpus> getCtx() {
		return ctx;
	}

	public void setCtx(ProcessingContext<Corpus> ctx) {
		this.ctx = ctx;
	}

	public Module<Corpus> getModule() {
		return module;
	}

	public void setModule(Module<Corpus> module) {
		this.module = module;
	}

	@Function
	public String id() {
		return module.getId();
	}
	
	@Function
	public String path() {
		return module.getPath();
	}
	
	@Function(firstFtor="class")
	public String klass() {
		return module.getModuleClass();
	}
	
	@Function
	public String dump() {
		File file = module.getDumpFile();
		if (file == null) {
			return "";
		}
		return file.getPath();
	}
	
	@Function
	public String sequence() {
		Sequence<?> seq = module.getSequence();
		if (seq == null) {
			return "";
		}
		return seq.getId();
	}
	
	@Function
	public boolean obsolete() {
		return module.getUseInstead().length > 0;
	}
	
	@Function
	public boolean beta() {
		return module.isBeta();
	}
	
	@Function
	public Iterator<Element> log(String msg) {
		Logger logger = module.getLogger(ctx);
		logger.info(msg);
		return Iterators.emptyIterator();
	}
	
	@Function
	public Iterator<Element> warn(String msg) {
		Logger logger = module.getLogger(ctx);
		logger.warning(msg);
		return Iterators.emptyIterator();
	}
	
	@Function
	public Iterator<Element> highlight(String msg) {
		Logger logger = module.getLogger(ctx);
		logger.log(ModuleBase.HIGHLIGHT, msg);
		return Iterators.emptyIterator();
	}
}
