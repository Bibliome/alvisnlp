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

import org.bibliome.util.mappers.ParamMapper;

public class FeatureElement implements Element {
	public static final String KEY_FEATURE_KEY = "key";
	public static final String VALUE_FEATURE_KEY = "value";
	
	private final Element element;
	private final String key;
	private final String value;

	public FeatureElement(Element elt, String key, String value) {
		super();
		this.element = elt;
		this.key = key;
		this.value = value;
	}


	@Override
	public String getStaticFeatureValue() {
		return element.getStaticFeatureValue();
	}

	@Override
	public boolean hasFeature(String key) {
		return element.hasFeature(key) || KEY_FEATURE_KEY.equals(key) || VALUE_FEATURE_KEY.equals(key);
	}

	@Override
	public Set<String> getFeatureKeys() {
		Set<String> result = new HashSet<String>(element.getFeatureKeys());
		result.add(KEY_FEATURE_KEY);
		result.add(VALUE_FEATURE_KEY);
		return result;
	}

	@Override
	public List<String> getFeature(String key) {
		if (KEY_FEATURE_KEY.equals(key)) {
			return Collections.singletonList(this.key);
		}
		if (VALUE_FEATURE_KEY.equals(key)) {
			return Collections.singletonList(value);
		}
		return element.getFeature(key);
	}

	@Override
	public boolean removeFeature(String key, String value) {
		if (KEY_FEATURE_KEY.equals(key) || VALUE_FEATURE_KEY.equals(key))
			return false;
		return element.removeFeature(key, value);
	}

	@Override
	public List<String> removeFeatures(String key) {
		if (KEY_FEATURE_KEY.equals(key) || VALUE_FEATURE_KEY.equals(key))
			return Collections.emptyList();
		return element.removeFeatures(key);
	}

	@Override
	public String getFirstFeature(String key) {
		if (KEY_FEATURE_KEY.equals(key)) {
			return this.key;
		}
		if (VALUE_FEATURE_KEY.equals(key)) {
			return this.value;
		}
		return element.getFirstFeature(key);
	}

	@Override
	public String getLastFeature(String key) {
		if (KEY_FEATURE_KEY.equals(key)) {
			return this.key;
		}
		if (VALUE_FEATURE_KEY.equals(key)) {
			return this.value;
		}
		return element.getLastFeature(key);
	}

	@Override
	public void addFeature(String key, String value) {
		element.addFeature(key, value);
	}

	@Override
	public void addFeatures(Map<String,String> mapping) {
		element.addFeatures(mapping);
	}

	@Override
	public void addMultiFeatures(Map<String,List<String>> mapping) {
		element.addMultiFeatures(mapping);
	}

	@Override
	public void featuresToXML(PrintStream out, String tag, String name, String value) throws IOException {
		element.featuresToXML(out, tag, name, value);
	}

	@Override
	public void write(PrintStream out, String s) throws IOException {
		element.write(out, s);
	}

	@Override
	public boolean isFeatureless() {
		return element.isFeatureless();
	}

	@Override
	public <R,P> R accept(ElementVisitor<R,P> visitor, P param) {
		return element.accept(visitor, param);
	}

	@Override
	public String getStringId() {
		return element.getStringId();
	}

	@Override
	public Map<String,List<String>> getFeatures() {
		Map<String,List<String>> result = new HashMap<String,List<String>>(element.getFeatures());
		result.put(KEY_FEATURE_KEY, Collections.singletonList(this.key));
		result.put(VALUE_FEATURE_KEY, Collections.singletonList(this.value));
		return result;
	}

	@Override
	public Element getParent() {
		return element;
	}

	@Override
	public Element getOriginal() {
		return element.getOriginal();
	}

	@Override
	public String getStaticFeatureKey() {
		return element.getStaticFeatureKey();
	}

	@Override
	public boolean isStaticFeatureKey(String key) {
		return element.isStaticFeatureKey(key);
	}
	
	@Override
	public ElementType getType() {
		return ElementType.OTHER;
	}

	public static final class FeatureElementMapper implements ParamMapper<String,Element,String> {
		private final Element element;

		public FeatureElementMapper(Element element) {
			super();
			this.element = element;
		}
		
		@Override
		public Element map(String x, String param) {
			return new FeatureElement(element, param, x);
		}

	}

	
//	static final ParamMapper<String,Element,String> MAPPER = new ParamMapper<String,Element,String>() {
//		@Override
//		public Element map(String x, String param) {
//			return new FeatureElement(param, x);
//		}
//	};
}
