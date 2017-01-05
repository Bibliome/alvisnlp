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
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bibliome.util.Strings;

import alvisnlp.corpus.creators.ElementCreator;

/**
 * Elements can have multi-valued features and one static single-valued feature.
 */
public abstract class AbstractElement implements Element, Serializable {
    private static final long serialVersionUID = 1L;

    private final String staticFeatureKey;
    private Map<String,List<String>> features = null;

    /**
     * Creates a new element with the specified static feature key.
     * @param staticFeatureKey
     */
    protected AbstractElement(String staticFeatureKey, ElementCreator ec) {
		super();
		this.staticFeatureKey = staticFeatureKey;
		if (ec != null)
			addFeature(ec.getCreatorNameFeature(), ec.getCreatorName());
	}


    @Override
    public String getStaticFeatureKey() {
		return staticFeatureKey;
	}


	@Override
    public boolean isStaticFeatureKey(String key) {
    	if (staticFeatureKey == null)
    		return false;
    	return staticFeatureKey.equals(key);
    }
    
    @Override
    public final boolean hasFeature(String key) {
    	if (isStaticFeatureKey(key))
    		return true;
        if (features == null)
            return false;
        return features.containsKey(key);
    }

    @Override
    public final Set<String> getFeatureKeys() {
        if (features == null) {
        	if (staticFeatureKey == null)
                return Collections.emptySet();
        	return Collections.singleton(staticFeatureKey);
        }
        if (staticFeatureKey == null)
            return Collections.unmodifiableSet(features.keySet());
        Set<String> result = new HashSet<String>(features.keySet());
        result.add(staticFeatureKey);
        return result;
    }

    @Override
    public final List<String> getFeature(String key) {
    	if (isStaticFeatureKey(key))
    		return Collections.singletonList(getStaticFeatureValue());
        if (features == null)
            return null;
        List<String> values = features.get(key);
        if (values == null)
            return null;
        return Collections.unmodifiableList(values);
    }

    @Override
    public boolean removeFeature(String key, String value) {
    	if (isStaticFeatureKey(key))
    		throw new IllegalArgumentException();
    	if (features == null)
    		return false;
    	if (key == null)
    		return false;
    	if (!features.containsKey(key))
    		return false;
    	List<String> values = features.get(key);
    	if (values.remove(value)) {
    		if (values.isEmpty()) {
    			features.remove(key);
    			if (features.isEmpty())
    				features = null;
    		}
    		return true;
    	}
    	return false;
    }
    
    @Override
	public List<String> removeFeatures(String key) {
    	if (isStaticFeatureKey(key))
    		throw new IllegalArgumentException();
    	if (features == null)
    		return Collections.emptyList();
    	if (key == null)
    		return Collections.emptyList();
    	return features.remove(key);
	}


	@Override
    public final String getFirstFeature(String key) {
    	if (isStaticFeatureKey(key))
    		return getStaticFeatureValue();
        if (features == null)
            return null;
        List<String> values = features.get(key);
        if (values == null)
            return null;
        return values.get(0);
    }

    @Override
    public final String getLastFeature(String key) {
    	if (isStaticFeatureKey(key))
    		return getStaticFeatureValue();
        if (features == null)
            return null;
        List<String> values = features.get(key);
        if (values == null)
            return null;
        return values.get(values.size() - 1);
    }

    @Override
    public final void addFeature(String key, String value) {
    	if (key == null)
    		return;
    	if (key.isEmpty())
    		return;
    	if (value == null)
    		return;
    	if (isStaticFeatureKey(key))
    		throw new IllegalArgumentException("attempt to set static feature " + staticFeatureKey);
        if (features == null)
            features = new LinkedHashMap<String,List<String>>();
        if (!features.containsKey(key))
            features.put(key, new ArrayList<String>());
        features.get(key).add(value);
    }

    @Override
    public final void addFeatures(Map<String,String> mapping) {
        if (mapping == null)
            return;
        for (Map.Entry<String,String> e : mapping.entrySet())
            addFeature(e.getKey(), e.getValue());
    }

    @Override
    public final Map<String,List<String>> getFeatures() {
    	if (features == null)
    		return Collections.emptyMap();
    	return Collections.unmodifiableMap(features);
    }

    @Override
    public final void addMultiFeatures(Map<String,List<String>> mapping) {
        if (mapping == null)
            return;
        for (Map.Entry<String,List<String>> e : mapping.entrySet())
        	for (String value : e.getValue())
        		addFeature(e.getKey(), value);
    }

    @Override
    public void featuresToXML(PrintStream out, String tag, String name, String value) throws IOException {
    	if (features == null)
    		return;
    	for (Map.Entry<String,List<String>> e : features.entrySet()) {
    		String k = e.getKey();
    		for (String v : e.getValue()) {
    			write(out,"<" + tag + ' ' + name + "=\"" + Strings.escapeXML(k) + "\" " + value + "=\"" + Strings.escapeXML(v) + "\"/>");
    		}
    	} 
    }

    @Override
    public void write(PrintStream out, String s) throws IOException {
    	try { out.write(s.getBytes("UTF-8")); }
    	catch (UnsupportedEncodingException uee) { uee.printStackTrace(); }
    }

    @Override
    public boolean isFeatureless() {
        return features == null;
    }

    @Override
    public String getStringId() {
    	return Integer.toHexString(System.identityHashCode(this));
    }

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (obj instanceof Element) {
			return super.equals(((Element) obj).getOriginal());
		}
		return false;
	}


	@Override
	public int hashCode() {
		return super.hashCode();
	}
}
