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
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import fr.inra.maiage.bibliome.alvisnlp.core.documentation.Documentation;

/**
 * A sequence is a module that gathers a list of submodules to run sequentially.
 */
public interface Sequence extends Module {
    /**
     * Adds a submodule at the end of the current list of modules.
     * @param module
     */
    void appendModule(Module module) throws ModuleException;

    /**
     * Adds a module at a specified position in the list of submodules.
     * @param module
     * @param index
     */
    void addModule(Module module, int index) throws ModuleException;

    /**
     * Adds a module in the list of submodules at the position after the specified reference module.
     * ref should be already in the submodules of this sequence.
     * @param module
     * @param ref
     */
    void addModuleAfter(Module module, Module ref) throws ModuleException;

    /**
     * Adds a module in the list of submodules at the position before the specified reference module.
     * ref should be already in the submodules of this sequence.
     * @param module
     * @param ref
     */
    void addModuleBefore(Module module, Module ref) throws ModuleException;

    /**
     * Removes a module from the submodules.
     * @param module
     */
    void removeModule(Module module);

    boolean hasModule(String id);
    
    /**
     * Returns the module in this sequence with the specified identifier.
     * @param id
     */
    Module getModule(String id);
    
    /**
     * Returns the module in this sequence at the specified position.
     * @param index
     */
    Module getModule(int index);
    
    /**
     * Returns the number of modules in this sequence.
     */
    int getNumModules();
    
    /**
     * Returns the index of the specified module.
     * @param module
     */
    int getIndexOf(Module module);
    
    List<Module> getSubModules();
    
    List<Module> getActiveSubModules();

    CompositeParamHandler createAliasParam(String name) throws ParameterException;

	static final class CompositeParamHandler implements ParamHandler {
		private final Sequence module;
		private final String name;
		private final List<ParamHandler> paramHandlers = new ArrayList<ParamHandler>();
	    private String paramSourceName;
		
		public CompositeParamHandler(Sequence module, String name) {
			super();
			this.module = module;
			this.name = name;
		}
		
		private void addParamHandler(ParamHandler ph) throws ParameterException {
			if (paramHandlers.contains(ph))
				throw new RuntimeException("duplicate parameter");
			if (!paramHandlers.isEmpty()) {
				ParamHandler first = firstHandler();
				Class<?> type = first.getType();
				if (!type.equals(ph.getType()))
					throw new RuntimeException("type mismatch");
				Object value = first.getValue();
				ph.setValue(value);
			}
			paramHandlers.add(ph);
		}
		
		public void addParamHandler(String modulePath, String parameterName) throws ParameterException {
			Module module = getModule().getModuleByPath(modulePath);
			if (module == null)
				throw new RuntimeException("no such module: " + modulePath);
			ParamHandler ph = module.getParamHandler(parameterName);
			addParamHandler(ph);
		}

		public final ParamHandler firstHandler() {
			return paramHandlers.get(0);
		}
		
		public List<ParamHandler> getAllParamHandlers() {
			return Collections.unmodifiableList(paramHandlers);
		}
		
		@Override
		public Class<?> getType() {
			return firstHandler().getType();
		}

		@Override
		public String getName() {
			return name;
		}

		@Override
		public Object getValue() {
			return firstHandler().getValue();
		}

		@Override
		public void setValue(Object value) throws ParameterException {
			for (ParamHandler ph : paramHandlers)
				ph.setValue(value);
		}

		@Override
		public boolean isSet() {
			return firstHandler().isSet();
		}

		@Override
		public boolean isMandatory() {
			for (ParamHandler ph : paramHandlers)
				if (ph.isMandatory())
					return true;
			return false;
		}

		@Override
		public boolean isOutputFeed() {
			for (ParamHandler ph : paramHandlers)
				if (!ph.isOutputFeed())
					return false;
			return true;
		}

		@Override
		public void setOutputFeed(boolean outputFeed) {
			for (ParamHandler ph : paramHandlers)
				ph.setOutputFeed(outputFeed);
		}

		@Override
		public <P> void accept(ModuleVisitor<P> visitor, P param) throws ModuleException {
			for (ParamHandler ph : paramHandlers) {
				ph.accept(visitor, param);
				return;
			}
		}

		@Override
		public Module getModule() {
			return module;
		}

		@Override
		public String getNameType() {
			return firstHandler().getNameType();
		}

		@Override
		public String getParamSourceName() {
			if (paramSourceName == null) {
				return module.getModuleSourceName();
			}
			return paramSourceName;
		}

		@Override
		public void setParamSourceName(String sourceName) {
			paramSourceName = sourceName;
		}

		@Override
		public boolean isDeprecated() {
			return false;
		}
	}

	void setDocumentation(Documentation documentation);

	Collection<String> getProperties();
	
	String getProperty(String name);
	
	void setProperty(String name, String value);
	
	void setSequenceSourceName(String sourceName);
}
