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
import java.util.Collection;
import java.util.List;

import alvisnlp.documentation.Documentation;

/**
 * A sequence is a module that gathers a list of submodules to run sequentially.
 */
public interface Sequence<T extends Annotable> extends Module<T> {
    /**
     * Adds a submodule at the end of the current list of modules.
     * @param module
     */
    void appendModule(Module<T> module) throws ModuleException;

    /**
     * Adds a module at a specified position in the list of submodules.
     * @param module
     * @param index
     */
    void addModule(Module<T> module, int index) throws ModuleException;

    /**
     * Adds a module in the list of submodules at the position after the specified reference module.
     * ref should be already in the submodules of this sequence.
     * @param module
     * @param ref
     */
    void addModuleAfter(Module<T> module, Module<T> ref) throws ModuleException;

    /**
     * Adds a module in the list of submodules at the position before the specified reference module.
     * ref should be already in the submodules of this sequence.
     * @param module
     * @param ref
     */
    void addModuleBefore(Module<T> module, Module<T> ref) throws ModuleException;

    /**
     * Removes a module from the submodules.
     * @param module
     */
    void removeModule(Module<T> module);

    boolean hasModule(String id);
    
    /**
     * Returns the module in this sequence with the specified identifier.
     * @param id
     */
    Module<T> getModule(String id);
    
    /**
     * Returns the module in this sequence at the specified position.
     * @param index
     */
    Module<T> getModule(int index);
    
    /**
     * Returns the number of modules in this sequence.
     */
    int getNumModules();
    
    /**
     * Returns the index of the specified module.
     * @param module
     */
    int getIndexOf(Module<T> module);
    
    List<Module<T>> getSubModules();

    CompositeParamHandler<T> createAliasParam(String name) throws ParameterException;

	static final class CompositeParamHandler<T extends Annotable> implements ParamHandler<T> {
		private final Sequence<T> module;
		private final String name;
		private final List<ParamHandler<T>> paramHandlers = new ArrayList<ParamHandler<T>>();
		
		public CompositeParamHandler(Sequence<T> module, String name) {
			super();
			this.module = module;
			this.name = name;
		}
		
		private void addParamHandler(ParamHandler<T> ph) throws ParameterException {
			if (paramHandlers.contains(ph))
				throw new RuntimeException("duplicate parameter");
			if (!paramHandlers.isEmpty()) {
				ParamHandler<T> first = firstHandler();
				Class<?> type = first.getType();
				if (!type.equals(ph.getType()))
					throw new RuntimeException("type mismatch");
				Object value = first.getValue();
				ph.setValue(value);
			}
			paramHandlers.add(ph);
		}
		
		public void addParamHandler(String modulePath, String parameterName) throws ParameterException {
			Module<T> module = getModule().getModuleByPath(modulePath);
			if (module == null)
				throw new RuntimeException("no such module: " + modulePath);
			ParamHandler<T> ph = module.getParamHandler(parameterName);
			addParamHandler(ph);
		}

		public final ParamHandler<T> firstHandler() {
			return paramHandlers.get(0);
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
			for (ParamHandler<T> ph : paramHandlers)
				ph.setValue(value);
		}

		@Override
		public boolean isSet() {
			return firstHandler().isSet();
		}

		@Override
		public boolean isMandatory() {
			for (ParamHandler<T> ph : paramHandlers)
				if (ph.isMandatory())
					return true;
			return false;
		}

		@Override
		public boolean isInhibitCheck() {
			for (ParamHandler<T> ph : paramHandlers)
				if (!ph.isInhibitCheck())
					return false;
			return true;
		}

		@Override
		public void setInhibitCheck(boolean inhibitFileCheck) {
			for (ParamHandler<T> ph : paramHandlers)
				ph.setInhibitCheck(inhibitFileCheck);
		}

		@Override
		public <P> void accept(ModuleVisitor<T,P> visitor, P param) throws ModuleException {
			for (ParamHandler<T> ph : paramHandlers) {
				ph.accept(visitor, param);
				return;
			}
		}

		@Override
		public Module<T> getModule() {
			return module;
		}

		@Override
		public String getNameType() {
			return firstHandler().getNameType();
		}
	}

	void setDocumentation(Documentation documentation);

	Collection<String> getProperties();
	
	String getProperty(String name);
	
	void setProperty(String name, String value);
}
