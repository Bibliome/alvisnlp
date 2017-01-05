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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.bibliome.util.Checkable;
import org.bibliome.util.Iterators;

import weka.core.FastVector;
import weka.core.Instance;
import alvisnlp.corpus.Element;
import alvisnlp.corpus.expressions.EvaluationContext;
import alvisnlp.corpus.expressions.LibraryResolver;
import alvisnlp.corpus.expressions.Resolvable;
import alvisnlp.corpus.expressions.ResolverException;
import alvisnlp.module.ModuleException;
import alvisnlp.module.NameUsage;
import alvisnlp.module.NameUser;

public class RelationDefinition implements Resolvable<RelationDefinition>, Checkable, NameUser {
	private final String name;
	private final List<AttributeDefinition> attributes = new ArrayList<AttributeDefinition>();
	private final List<BagDefinition> bags = new ArrayList<BagDefinition>();
	private int classAttributeIndex = -1;
	private int bagAttributesOffset = -1;
	
	private RelationDefinition(String name, boolean addIdentifier) {
		super();
		this.name = name;
		if (addIdentifier)
			attributes.add(new IdentifierAttributeDefinition());
	}
	
	RelationDefinition(String name) {
		this(name, true);
	}
	
	boolean isBagsInitialized() {
		return bagAttributesOffset != -1;
	}

	IdentifiedInstances<Element> createInstances() throws IOException {
		if (!isBagsInitialized())
			initializeBags();
		FastVector attrVector = new FastVector(attributes.size());
		for (AttributeDefinition ad : attributes)
			attrVector.addElement(ad.getAttribute());
		IdentifiedInstances<Element> result = new IdentifiedInstances<Element>(name, attrVector, 0);
		result.setClassIndex(classAttributeIndex);
		return result;
	}
	
	int addAttribute(AttributeDefinition attrDef) {
		if (isBagsInitialized())
			throw new IllegalStateException();
		return unsafeAddAttribute(attrDef);
	}

	private int unsafeAddAttribute(AttributeDefinition attrDef) {
		int result = attributes.size();
		if (attrDef.isClassAttribute())
			classAttributeIndex = result;
		attributes.add(attrDef);
		return result;
	}
	
	void addBag(BagDefinition bagDef) {
		if (isBagsInitialized())
			throw new IllegalStateException();
		bags.add(bagDef);
	}

	void initializeBags() throws IOException {
		if (isBagsInitialized())
			throw new IllegalStateException();
		bagAttributesOffset = attributes.size();
		for (BagDefinition bagDef : bags) {
			bagDef.init();
			boolean count = bagDef.isCount();
			String prefix = bagDef.getPrefix();
			for (String value : bagDef.getValueSpace()) {
				String name = prefix + value;
				AttributeDefinition a;
				if (count)
					a = new CountBagAttributeDefinition(name);
				else
					a = new BooleanBagAttributeDefinition(name);
				int index = unsafeAddAttribute(a);
				bagDef.setAttributeIndex(value, index);
			}
		}
	}

	double[] evaluateExample(EvaluationContext ctx, Element example, boolean train) {
		if (!isBagsInitialized())
			throw new IllegalStateException();
		double[] result = new double[attributes.size()];
		for (int i = 1; i < bagAttributesOffset; ++i) {
			AttributeDefinition attrDef = attributes.get(i);
			if (train || !attrDef.isClassAttribute())
				result[i] = attributes.get(i).evaluate(ctx, example);
		}
		for (BagDefinition bagDef : bags) {
			String featureKey = bagDef.getFeatureKey();
			boolean count = bagDef.isCount();
			for (Element e : Iterators.loop(bagDef.getBag(ctx, example))) {
				if (!e.hasFeature(featureKey))
					continue;
				Integer index = bagDef.getAttributeIndex(e.getLastFeature(featureKey));
				if (index == null)
					continue;
				if (count)
					result[index]++;
				else
					result[index] = 1;
			}
		}
		return result;
	}

	Instance addExample(IdentifiedInstances<Element> instances, EvaluationContext ctx, Element example, boolean train, boolean withId) {
		double[] values = evaluateExample(ctx, example, train);
		Instance inst = new Instance(1, values);
		if (withId)
			instances.add(example, inst);
		else
			instances.add(inst);
		inst.setDataset(instances);
		return inst;
	}

	@Override
	public RelationDefinition resolveExpressions(LibraryResolver resolver) throws ResolverException {
		if (isBagsInitialized())
			throw new IllegalStateException();
		RelationDefinition result = new RelationDefinition(name, false);
		for (AttributeDefinition a : attributes)
			result.addAttribute(a.resolveExpressions(resolver));
		for (BagDefinition b : bags)
			result.addBag(b.resolveExpressions(resolver));
		return result;
	}

	@Override
	public boolean check(Logger logger) {
		boolean result = true;
		for (BagDefinition b : bags)
			result = b.check(logger) && result;
		return result;
	}

	@Override
	public void collectUsedNames(NameUsage nameUsage, String defaultType) throws ModuleException {
		for (AttributeDefinition attr : attributes) {
			attr.collectUsedNames(nameUsage, defaultType);
		}
		for (BagDefinition bag : bags) {
			bag.collectUsedNames(nameUsage, defaultType);
		}
	}
}
