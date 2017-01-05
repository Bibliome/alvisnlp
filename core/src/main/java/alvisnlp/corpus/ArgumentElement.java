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


package alvisnlp.corpus;

import java.io.IOException;
import java.io.PrintStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import alvisnlp.corpus.Element;
import alvisnlp.corpus.ElementType;
import alvisnlp.corpus.ElementVisitor;
import alvisnlp.corpus.Tuple;

public final class ArgumentElement implements Element {
	public static final String ROLE_FEATURE_KEY = "role";

	private final Tuple tuple;
	private final String role;
	private final Element argument;

	public ArgumentElement(Tuple t, String role, Element argument) {
		this.tuple = t;
		this.role = role;
		this.argument = argument;
//		System.err.println("argument = " + argument);
	}

	@Override
	public String getStaticFeatureValue() {
		return role;
	}

	@Override
	public String getStaticFeatureKey() {
		return ROLE_FEATURE_KEY;
	}

	@Override
	public boolean isStaticFeatureKey(String key) {
		return ROLE_FEATURE_KEY.equals(key);
	}

	@Override
	public boolean hasFeature(String key) {
		return argument.hasFeature(key) || ROLE_FEATURE_KEY.equals(key);
	}

//	@Override
//	public String getStaticFeatureKey() {
//		return ROLE_FEATURE_KEY;
//	}
//
//	@Override
//	public boolean isStaticFeatureKey(String key) {
//		return ROLE_FEATURE_KEY.equals(key);
//	}

	@Override
	public Set<String> getFeatureKeys() {
		Set<String> result = new HashSet<String>(argument.getFeatureKeys());
		result.add(ROLE_FEATURE_KEY);
		return result;
	}

	@Override
	public List<String> getFeature(String key) {
		if (ROLE_FEATURE_KEY.equals(key))
			return Collections.singletonList(role);
		return argument.getFeature(key);
	}

	@Override
	public boolean removeFeature(String key, String value) {
		if (ROLE_FEATURE_KEY.equals(key))
			return false;
		return argument.removeFeature(key, value);
	}

	@Override
	public List<String> removeFeatures(String key) {
		if (ROLE_FEATURE_KEY.equals(key))
			return Collections.emptyList();
		return argument.removeFeatures(key);
	}

	@Override
	public String getFirstFeature(String key) {
		if (ROLE_FEATURE_KEY.equals(key))
			return role;
		return argument.getFirstFeature(key);
	}

	@Override
	public String getLastFeature(String key) {
		if (ROLE_FEATURE_KEY.equals(key))
			return role;
		return argument.getLastFeature(key);
	}

	@Override
	public void addFeature(String key, String value) {
		argument.addFeature(key, value);
	}

	@Override
	public void addFeatures(Map<String,String> mapping) {
		argument.addFeatures(mapping);
	}

	@Override
	public void addMultiFeatures(Map<String,List<String>> mapping) {
		argument.addMultiFeatures(mapping);
	}

	@Override
	public void featuresToXML(PrintStream out, String tag, String name, String value) throws IOException {
		argument.featuresToXML(out, tag, name, value);
	}

	@Override
	public void write(PrintStream out, String s) throws IOException {
		argument.write(out, s);
	}

	@Override
	public boolean isFeatureless() {
		return argument.isFeatureless();
	}

	@Override
	public <R,P> R accept(ElementVisitor<R, P> visitor, P param) {
		return argument.accept(visitor, param);
	}

	@Override
	public String getStringId() {
		return argument.getStringId();
	}

	@Override
	public Map<String,List<String>> getFeatures() {
		Map<String,List<String>> result = new HashMap<String,List<String>>();
		if (result.containsKey(ROLE_FEATURE_KEY))
			result.get(ROLE_FEATURE_KEY).add(role);
		else {
			List<String> list = Collections.singletonList(role);
			result.put(ROLE_FEATURE_KEY, list);
		}
		return result;
	}

	@Override
	public ElementType getType() {
		return argument.getType();
	}

	@Override
	public Element getParent() {
		return tuple;
	}

	@Override
	public Element getOriginal() {
		return argument.getOriginal();
	}

	@Override
	public int hashCode() {
		return argument.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		return argument.equals(obj);
	}
}
