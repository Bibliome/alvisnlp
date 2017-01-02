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


package org.bibliome.alvisnlp.modules;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import alvisnlp.corpus.Corpus;
import alvisnlp.corpus.expressions.ResolverException;
import alvisnlp.documentation.Documentation;
import alvisnlp.module.Module;
import alvisnlp.module.ModuleException;
import alvisnlp.module.ModuleVisitor;
import alvisnlp.module.ParamHandler;
import alvisnlp.module.ParameterException;
import alvisnlp.module.ProcessingContext;
import alvisnlp.module.Sequence;
import alvisnlp.module.UnexpectedParameterException;
import alvisnlp.module.lib.AlvisNLPModule;

@AlvisNLPModule
public class Sequence_Impl extends CorpusModule<ResolvedObjects> implements Sequence<Corpus> {
    private final List<Module<Corpus>> moduleSequence = new ArrayList<Module<Corpus>>();
    private final Map<String,CompositeParamHandler<Corpus>> params = new HashMap<String,CompositeParamHandler<Corpus>>();
    private final Map<String,String> properties = new LinkedHashMap<String,String>();

    public Sequence_Impl() {
    	super();
    }

	@Override
	protected ResolvedObjects createResolvedObjects(ProcessingContext<Corpus> ctx) throws ResolverException {
		return new ResolvedObjects(ctx, this);
	}

    @Override
    public void appendModule(Module<Corpus> module) {
        moduleSequence.add(module);
        module.setSequence(this);
    }

    @Override
    public void addModule(Module<Corpus> module, int index) {
        moduleSequence.add(index, module);
        module.setSequence(this);
    }

    @Override
    public void addModuleAfter(Module<Corpus> module, Module<Corpus> ref) {
        int index = moduleSequence.indexOf(ref);
        if (index < 0) {
			throw new RuntimeException("reference module " + ref.getId() + " does not belong to sequence " + getId());
		}
        addModule(module, index + 1);
    }

    @Override
    public void addModuleBefore(Module<Corpus> module, Module<Corpus> ref) {
        int index = moduleSequence.indexOf(ref);
        if (index < 0) {
			throw new RuntimeException("reference module " + ref.getId() + " does not belong to sequence " + getId());
		}
        addModule(module, index);
    }

    @Override
    public void removeModule(Module<Corpus> module) {
        moduleSequence.remove(module);
    }

    @Override
    public void process(ProcessingContext<Corpus> ctx, Corpus corpus) throws ModuleException {
        for (Module<Corpus> mod : moduleSequence) {
			ctx.processCorpus(mod, corpus);
		}
    }

    @Override
    public String getResourceBundleName() {
        return "alvisnlp.app.Sequence_ImplDoc";
    }
    
	@Override
	public boolean hasModule(String id) {
    	for (Module<Corpus> m : moduleSequence) {
			if (id.equals(m.getId())) {
				return true;
			}
		}
    	return false;
	}

	@Override
    public Module<Corpus> getModule(String id) {
    	for (Module<Corpus> m : moduleSequence) {
			if (id.equals(m.getId())) {
				return m;
			}
		}
    	return null;
    }
	
    @Override
    public Collection<ParamHandler<Corpus>> getAllParamHandlers() {
    	Collection<ParamHandler<Corpus>> result = new ArrayList<ParamHandler<Corpus>>(super.getAllParamHandlers());
    	result.addAll(params.values());
    	return result;
    }

    @Override
    public ParamHandler<Corpus> getParamHandler(String name) throws UnexpectedParameterException {
    	if (params.containsKey(name))
    		return params.get(name);
    	return super.getParamHandler(name);
    }
    
    @Override
    public void init(ProcessingContext<Corpus> ctx) throws ModuleException {
    	super.init(ctx);
        for (Module<Corpus> mod : moduleSequence) {
			mod.init(ctx);
		}
    }

	@Override
	public Module<Corpus> getModule(int index) {
		if (index < 0)
			return null;
		if (index >= moduleSequence.size())
			return null;
		return moduleSequence.get(index);
	}

	@Override
	public int getNumModules() {
		return moduleSequence.size();
	}

	@Override
	public int getIndexOf(Module<Corpus> module) {
		for (int i = 0; i < moduleSequence.size(); ++i)
			if (module == moduleSequence.get(i))
				return i;
		return -1;
	}

	@Override
	public Module<Corpus> getModuleByPath(String modulePath) {
		int dot = modulePath.indexOf('.');
		if (dot < 0)
			return getModule(modulePath);
		String moduleName = modulePath.substring(0, dot);
		Module<Corpus> subModule = getModule(moduleName);
		if (subModule == null)
			return null;
		return subModule.getModuleByPath(modulePath.substring(dot + 1));
	}
	
	@Override
	public CompositeParamHandler<Corpus> createAliasParam(String name) throws ParameterException {
		if (params.containsKey(name))
			throw new ParameterException("duplicate alias " + name + " in " + getPath(), name);
		CompositeParamHandler<Corpus> result = new CompositeParamHandler<Corpus>(this, name);
		params.put(name, result);
		return result;
	}

	@Override
	public <P> void accept(ModuleVisitor<Corpus,P> visitor, P param) throws ModuleException {
		visitor.visitSequence(this, param);
	}

	@Override
	public List<Module<Corpus>> getSubModules() {
		return Collections.unmodifiableList(moduleSequence);
	}

	@Override
	public void setDocumentation(Documentation documentation) {
		super.setDocumentation(documentation);
	}

	@Override
	public Collection<String> getProperties() {
		Collection<String> names = properties.keySet();
		return Collections.unmodifiableCollection(names);
	}

	@Override
	public String getProperty(String name) {
		return properties.get(name);
	}

	@Override
	public void setProperty(String name, String value) {
		properties.put(name, value);
	}
}
