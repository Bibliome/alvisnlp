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



package org.bibliome.alvisnlp.modules.yatea;

import org.bibliome.alvisnlp.modules.SectionModule.SectionResolvedObjects;

import alvisnlp.corpus.Corpus;
import alvisnlp.corpus.expressions.ResolverException;
import alvisnlp.module.ProcessingContext;
import alvisnlp.module.lib.AlvisNLPModule;

/**
 * Uses YaTeA to extract terms from the corpus.
 */
@AlvisNLPModule
public class YateaExtractor extends AbstractYateaExtractor<SectionResolvedObjects> {
	@Override
	protected SectionResolvedObjects createResolvedObjects(ProcessingContext<Corpus> ctx) throws ResolverException {
		return new SectionResolvedObjects(ctx, this);
	}
}
