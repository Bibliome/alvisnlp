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

import java.util.Arrays;
import java.util.Collection;

import weka.core.Attribute;
import weka.core.FastVector;
import alvisnlp.corpus.Element;
import alvisnlp.corpus.expressions.EvaluationContext;
import alvisnlp.corpus.expressions.Resolvable;
import alvisnlp.module.NameUser;

abstract class AttributeDefinition implements Resolvable<AttributeDefinition>, NameUser {
	private final boolean classAttribute;
	private final Attribute attribute;

	protected AttributeDefinition(boolean classAttribute, Attribute attribute) {
		super();
		this.attribute = attribute;
		this.classAttribute = classAttribute;
	}
	
	protected AttributeDefinition(boolean classAttribute, String name, boolean numeric) {
		this(classAttribute, numeric ? new Attribute(name) : new Attribute(name, fastVector(BOOLEAN_VALUES)));
	}
	
	protected AttributeDefinition(boolean classAttribute, String name, Collection<String> values) {
		this(classAttribute, new Attribute(name, fastVector(values)));
	}
	
	private static final FastVector fastVector(Collection<String> values) {
		FastVector result = new FastVector(values.size());
		for (String v : values)
			result.addElement(v);
		return result;
	}

	private static final Collection<String> BOOLEAN_VALUES = Arrays.asList("0", "1");
	
	boolean isClassAttribute() {
		return classAttribute;
	}

	Attribute getAttribute() {
		return attribute;
	}
	
	abstract double evaluate(EvaluationContext ctx, Element example);
	
	public String getName() {
		return attribute.name();
	}
	
	public Collection<String> getValues() {
		String[] result = new String[attribute.numValues()];
		for (int i = 0; i < result.length; ++i)
			result[i] = attribute.value(i);
		return Arrays.asList(result);
	}
}
