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


package fr.inra.maiage.bibliome.alvisnlp.core.module;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.logging.Logger;

public class CheckUniquePaths extends AbstractModuleVisitor<Logger> {
	private final Collection<String> seenPaths = new LinkedHashSet<String>();
	private boolean duplicatePath = false;
	
	public CheckUniquePaths() {
		super(false);
	}
	
	@Override
	public void visitSequence(Sequence sequence, Logger logger) throws ModuleException {
		visitModule(sequence, logger);
		super.visitSequence(sequence, logger);
	}

	@Override
	public void visitModule(Module module, Logger logger) {
		String path = module.getPath();
		if (seenPaths.contains(path)) {
			logger.severe("duplicate module path: " + path);
			duplicatePath = true;
		}
		seenPaths.add(path);
	}
	
	public static boolean visit(Logger logger, Module module) throws ModuleException {
		CheckUniquePaths result = new CheckUniquePaths();
		module.accept(result, logger);
		return result.duplicatePath;
	}
}
