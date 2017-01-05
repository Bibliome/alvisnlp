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


package alvisnlp.module;

import java.util.ArrayList;
import java.util.List;

public class CollectModules<A extends Annotable> extends AbstractModuleVisitor<A,List<Module<A>>> {
	private final boolean sequences;

	private CollectModules(boolean sequences) {
		super();
		this.sequences = sequences;
	}

	@Override
	public void visitModule(Module<A> module, List<Module<A>> param) {
		param.add(module);
	}

	@Override
	public void visitSequence(Sequence<A> sequence, List<Module<A>> param) throws ModuleException {
		if (sequences)
			param.add(sequence);
		super.visitSequence(sequence, param);
	}
	
	public static final <A extends Annotable> List<Module<A>> visit(Module<A> module, boolean sequences) throws ModuleException {
		CollectModules<A> visitor = new CollectModules<A>(sequences);
		List<Module<A>> result = new ArrayList<Module<A>>();
		module.accept(visitor, result);
		return result;
	}
}
