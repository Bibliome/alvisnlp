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


package org.bibliome.alvisnlp.modules.classifiers;

import java.util.ArrayList;
import java.util.List;

import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;

public class IdentifiedInstances<T> extends Instances {
	private static final long serialVersionUID = 1L;

	private final List<T> elements = new ArrayList<T>();
	
	IdentifiedInstances(String name, FastVector attributes, int capacity) {
		super(name, attributes, capacity);
	}

	void add(T elt, Instance inst) {
		inst.setValue(0, elements.size());
		elements.add(elt);
		super.add(inst);
	}
	
	T getElement(int id) {
		return elements.get(id);
	}
}
