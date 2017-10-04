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


package alvisnlp.factory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.bibliome.util.service.CompositeServiceFactory;

import alvisnlp.module.Annotable;
import alvisnlp.module.Module;
import alvisnlp.module.Sequence;

/**
 * Modules factory.
 */
public class CompoundModuleFactory<T extends Annotable> extends CompositeServiceFactory<Class<? extends Module<T>>,Module<T>,ModuleFactory<T>> implements ModuleFactory<T> {
    @Override
    public Collection<String> autoAlias(Class<? extends Module<T>> key) {
        return Arrays.asList(key.getCanonicalName(), key.getSimpleName());
    }

    @Override
    public Collection<Class<? extends Module<T>>> supportedServices() {
    	//weird, was that necessary?
        List<Class<? extends Module<T>>> result = new ArrayList<Class<? extends Module<T>>>(super.supportedServices());
        return Collections.unmodifiableCollection(result);
    }

	@Override
	public Sequence<T> newSequence() {
		for (ModuleFactory<T> f : getServiceFactories())
			return f.newSequence();
		return null;
	}

	@Override
	public String getShellModule() {
		for (ModuleFactory<T> f : getServiceFactories())
			return f.getShellModule();
		return null;
	}
}
