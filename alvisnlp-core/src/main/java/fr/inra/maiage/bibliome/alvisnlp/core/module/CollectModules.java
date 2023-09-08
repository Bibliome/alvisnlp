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

import java.util.ArrayList;
import java.util.List;

public class CollectModules extends AbstractModuleVisitor<List<Module>> {
	private final boolean sequences;

	private CollectModules(boolean onlyActiveModules, boolean sequences) {
		super(onlyActiveModules);
		this.sequences = sequences;
	}

	@Override
	public void visitModule(Module module, List<Module> param) {
		param.add(module);
	}

	@Override
	public void visitSequence(Sequence sequence, List<Module> param) throws ModuleException {
		if (sequences)
			param.add(sequence);
		super.visitSequence(sequence, param);
	}
	
	public static final List<Module> visit(Module module, boolean onlyActiveModules, boolean sequences) throws ModuleException {
		CollectModules visitor = new CollectModules(onlyActiveModules, sequences);
		List<Module> result = new ArrayList<Module>();
		module.accept(visitor, result);
		return result;
	}
}
