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



package org.bibliome.alvisnlp.modules.mapper;

import java.util.List;
import java.util.logging.Logger;

import org.bibliome.alvisnlp.modules.CorpusModule;
import org.bibliome.alvisnlp.modules.ResolvedObjects;
import org.bibliome.alvisnlp.modules.mapper.Mapper.MapperResolvedObjects;

import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Corpus;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Element;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.EvaluationContext;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.Evaluator;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.Expression;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.ResolverException;
import fr.inra.maiage.bibliome.alvisnlp.core.module.ModuleException;
import fr.inra.maiage.bibliome.alvisnlp.core.module.NameUsage;
import fr.inra.maiage.bibliome.alvisnlp.core.module.ProcessingContext;
import fr.inra.maiage.bibliome.alvisnlp.core.module.ProcessingException;
import fr.inra.maiage.bibliome.alvisnlp.core.module.TimerCategory;
import fr.inra.maiage.bibliome.alvisnlp.core.module.lib.Param;
import fr.inra.maiage.bibliome.util.Iterators;
import fr.inra.maiage.bibliome.util.Timer;
import fr.inra.maiage.bibliome.util.defaultmap.DefaultArrayListHashMap;
import fr.inra.maiage.bibliome.util.defaultmap.DefaultMap;

public abstract class Mapper<S extends MapperResolvedObjects,T> extends CorpusModule<S> {
	private Expression target;
	private Expression form;
    protected Boolean ignoreCase = false;
    private MappingOperator operator = MappingOperator.EXACT;

    static class MapperResolvedObjects extends ResolvedObjects {
    	private final Evaluator target;
		private final Evaluator form;

		public MapperResolvedObjects(ProcessingContext<Corpus> ctx, Mapper<? extends MapperResolvedObjects,?> module) throws ResolverException {
			super(ctx, module);
			target = rootResolver.resolveNullable(module.target);
			form = rootResolver.resolveNullable(module.form);
		}

		@Override
		public void collectUsedNames(NameUsage nameUsage, String defaultType) throws ModuleException {
			super.collectUsedNames(nameUsage, defaultType);
			nameUsage.collectUsedNamesNullable(form, defaultType);
			nameUsage.collectUsedNamesNullable(target, defaultType);
		}
    }
    
    @Override
	public void process(ProcessingContext<Corpus> ctx, Corpus corpus) throws ModuleException {
    	MapperResolvedObjects resObj = getResolvedObjects();
    	Logger logger = getLogger(ctx);
		EvaluationContext evalCtx = new EvaluationContext(logger);
    	Timer<TimerCategory> dictTimer = getTimer(ctx, "load-dictionary", TimerCategory.LOAD_RESOURCE, true);
    	DefaultMap<String,List<T>> mapping = new DefaultArrayListHashMap<String,T>();
    	fillMapping(mapping, ctx, corpus);
    	dictTimer.stop();
    	int n = 0;
    	int m = 0;
    	Timer<TimerCategory> mapTimer = getTimer(ctx, "map", TimerCategory.MODULE, false);
    	Timer<TimerCategory> handleTimer = getTimer(ctx, "handle", TimerCategory.MODULE, false);    	
    	for (Element elt : Iterators.loop(resObj.target.evaluateElements(evalCtx, corpus))) {
    		++n;
    		mapTimer.start();
    		String candidateKey = resObj.form.evaluateString(evalCtx, elt);
    		if (ignoreCase)
    			candidateKey = candidateKey.toLowerCase();
    		List<T> values = operator.getMatches(mapping, candidateKey);
    		mapTimer.stop();
    		if (values.isEmpty()) {
    			continue;
    		}
    		++m;
    		handleTimer.start();
    		for (T v : values) {
    			handleMatch(elt, v);
    		}
    		handleTimer.stop();
    	}
        if (n == 0) {
        	logger.warning("no annotations visited");
        }
        else if (m == 0) {
        	logger.warning(String.format("no annotations mapped (%d visited)", n));
        }
        else {
        	logger.fine(String.format("mapped %d/%d annotations", m, n));
        }
    }

    public abstract void fillMapping(DefaultMap<String,List<T>> mapping, ProcessingContext<Corpus> ctx, Corpus corpus) throws ProcessingException;
    
    protected abstract void handleMatch(Element target, T value);

    @Param
	public Expression getTarget() {
		return target;
	}

    @Param
	public Expression getForm() {
		return form;
	}

    @Param
	public Boolean getIgnoreCase() {
		return ignoreCase;
	}

    @Param
	public MappingOperator getOperator() {
		return operator;
	}

	public void setOperator(MappingOperator operator) {
		this.operator = operator;
	}

	public void setTarget(Expression target) {
		this.target = target;
	}

	public void setForm(Expression form) {
		this.form = form;
	}

	public void setIgnoreCase(Boolean ignoreCase) {
		this.ignoreCase = ignoreCase;
	}
}
