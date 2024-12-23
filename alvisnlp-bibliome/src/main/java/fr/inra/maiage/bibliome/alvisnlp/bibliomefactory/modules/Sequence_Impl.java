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



package fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Corpus;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.ResolverException;
import fr.inra.maiage.bibliome.alvisnlp.core.documentation.Documentation;
import fr.inra.maiage.bibliome.alvisnlp.core.module.Module;
import fr.inra.maiage.bibliome.alvisnlp.core.module.ModuleException;
import fr.inra.maiage.bibliome.alvisnlp.core.module.ModuleVisitor;
import fr.inra.maiage.bibliome.alvisnlp.core.module.ParamHandler;
import fr.inra.maiage.bibliome.alvisnlp.core.module.ParameterException;
import fr.inra.maiage.bibliome.alvisnlp.core.module.ProcessingContext;
import fr.inra.maiage.bibliome.alvisnlp.core.module.Sequence;
import fr.inra.maiage.bibliome.alvisnlp.core.module.UnexpectedParameterException;
import fr.inra.maiage.bibliome.alvisnlp.core.module.lib.AlvisNLPModule;
import fr.inra.maiage.bibliome.alvisnlp.core.module.lib.Param;

@AlvisNLPModule
public class Sequence_Impl extends CorpusModule<ResolvedObjects> implements Sequence {
    private final List<Module> moduleSequence = new ArrayList<Module>();
    private final Map<String,CompositeParamHandler> params = new LinkedHashMap<String,CompositeParamHandler>();
    private final Map<String,String> properties = new LinkedHashMap<String,String>();
    private String sourceName = null;
    private String[] select;

    public Sequence_Impl() {
    	super();
    }

	@Override
	protected ResolvedObjects createResolvedObjects(ProcessingContext ctx) throws ResolverException {
		return new ResolvedObjects(ctx, this);
	}

    @Override
    public void appendModule(Module module) {
        moduleSequence.add(module);
        module.setSequence(this);
    }

    @Override
    public void addModule(Module module, int index) {
        moduleSequence.add(index, module);
        module.setSequence(this);
    }

    @Override
    public void addModuleAfter(Module module, Module ref) {
        int index = moduleSequence.indexOf(ref);
        if (index < 0) {
			throw new RuntimeException("reference module " + ref.getId() + " does not belong to sequence " + getId());
		}
        addModule(module, index + 1);
    }

    @Override
    public void addModuleBefore(Module module, Module ref) {
        int index = moduleSequence.indexOf(ref);
        if (index < 0) {
			throw new RuntimeException("reference module " + ref.getId() + " does not belong to sequence " + getId());
		}
        addModule(module, index);
    }

    @Override
    public void removeModule(Module module) {
        moduleSequence.remove(module);
        Collection<String> toRemove = new ArrayList<String>();
        for (CompositeParamHandler cph : params.values()) {
        	ParamHandler ph = cph.firstHandler();
        	Module m = ph.getModule();
        	if (m == module) {
        		toRemove.add(cph.getName());
        	}
        }
        for (String name : toRemove) {
        	params.remove(name);
        }
    }

    @Override
    public void process(ProcessingContext ctx, Corpus corpus) throws ModuleException {
    	if (select == null) {
    		for (Module mod : moduleSequence) {
    			ctx.processCorpus(mod, corpus);
    		}
    	}
    	else {
    		Set<String> set = new LinkedHashSet<String>(Arrays.asList(select));
    		for (Module mod : moduleSequence) {
    			if (set.contains(mod.getId())) {
    				ctx.processCorpus(mod, corpus);
    			}
    		}
    	}
    }

	@Override
    public String getResourceBundleName() {
        return "alvisnlp.app.Sequence_ImplDoc";
    }

	@Override
	public boolean hasModule(String id) {
    	for (Module m : moduleSequence) {
			if (id.equals(m.getId())) {
				return true;
			}
		}
    	return false;
	}

	@Override
    public Module getModule(String id) {
    	for (Module m : moduleSequence) {
			if (id.equals(m.getId())) {
				return m;
			}
		}
    	return null;
    }

    @Override
    public Collection<ParamHandler> getAllParamHandlers() {
    	Collection<ParamHandler> result = new ArrayList<ParamHandler>(super.getAllParamHandlers());
    	result.addAll(params.values());
    	return result;
    }

    @Override
    public ParamHandler getParamHandler(String name) throws UnexpectedParameterException {
    	if (params.containsKey(name))
    		return params.get(name);
    	return super.getParamHandler(name);
    }

    @Override
    public void init(ProcessingContext ctx) throws ModuleException {
    	super.init(ctx);
        for (Module mod : moduleSequence) {
			mod.init(ctx);
		}
    }

	@Override
	public Module getModule(int index) {
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
	public int getIndexOf(Module module) {
		for (int i = 0; i < moduleSequence.size(); ++i)
			if (module == moduleSequence.get(i))
				return i;
		return -1;
	}

	@Override
	public Module getModuleByPath(String modulePath) {
		int dot = modulePath.indexOf('.');
		if (dot < 0)
			return getModule(modulePath);
		String moduleName = modulePath.substring(0, dot);
		Module subModule = getModule(moduleName);
		if (subModule == null)
			return null;
		return subModule.getModuleByPath(modulePath.substring(dot + 1));
	}

	@Override
	public CompositeParamHandler createAliasParam(String name) throws ParameterException {
		if (params.containsKey(name))
			throw new ParameterException("duplicate alias " + name + " in " + getPath(), name);
		CompositeParamHandler result = new CompositeParamHandler(this, name);
		params.put(name, result);
		return result;
	}

	@Override
	public <P> void accept(ModuleVisitor<P> visitor, P param) throws ModuleException {
		visitor.visitSequence(this, param);
	}

	@Override
	public List<Module> getSubModules() {
		return Collections.unmodifiableList(moduleSequence);
	}

    @Override
	public List<Module> getActiveSubModules() {
    	if (select == null) {
    		return getSubModules();
    	}
    	List<Module> result = new ArrayList<Module>(moduleSequence.size());
		Set<String> set = new LinkedHashSet<String>(Arrays.asList(select));
		for (Module mod : moduleSequence) {
			if (set.contains(mod.getId())) {
				result.add(mod);
			}
		}
		return Collections.unmodifiableList(result);
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

	@Override
	public void setSequenceSourceName(String sourceName) {
		this.sourceName = sourceName;
	}

	@Override
	public String getModuleSourceName() {
		if (sourceName == null) {
			return super.getModuleSourceName();
		}
		return sourceName;
	}

	@Param(mandatory=false)
	public String[] getSelect() {
		return select;
	}

	public void setSelect(String[] select) {
		this.select = select;
	}
}
