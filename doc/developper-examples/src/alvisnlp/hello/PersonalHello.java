/*
Copyright 2016 Institut National de la Recherche Agronomique

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

package alvisnlp.hello;

import java.util.logging.Logger;

import fr.inra.maiage.bibliome.alvisnlp.core.document.Corpus;
import fr.inra.maiage.bibliome.alvisnlp.core.module.ModuleException;
import fr.inra.maiage.bibliome.alvisnlp.core.module.ProcessingContext;
import fr.inra.maiage.bibliome.alvisnlp.core.module.lib.AlvisNLPModule;
import fr.inra.maiage.bibliome.alvisnlp.core.module.lib.CorpusModule;
import fr.inra.maiage.bibliome.alvisnlp.core.module.lib.Param;

@AlvisNLPModule
public class PersonalHello extends CorpusModule {
	private String name;

	@Override
	public void process(ProcessingContext<Corpus> ctx, Corpus corpus) throws ModuleException {
		Logger logger = getLogger(ctx);
		logger.info("hello " + name + "!");		
	}

	@Param
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
