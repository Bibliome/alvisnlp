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
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Elements can have multi-valued features and one static single-valued feature.
 */
public interface Element {
	String getStaticFeatureValue();
	
	boolean hasFeature(String key);

    /**
     * Returns the keys of all feature associated to this element, including the static feature key.
     * @return the keys of all feature associated to this element
     */
    Set<String> getFeatureKeys();
    
    /**
     * Returns the values of feature associated with the specified key.
     * @param key key of the feature to get
     * @return the values of feature with the specified key
     */
    List<String> getFeature(String key);

    /**
     * Removes the feature with the specified key/value pair.
     * @param key
     * @param value
     */
    boolean removeFeature(String key, String value);
    
    List<String> removeFeatures(String key);
    
    /**
     * Gets the first feature value associated with the specified key.
     * @param key the key
     * @return the first feature
     */
    String getFirstFeature(String key);

    /**
     * Gets the last feature value associated with the specified key.
     * @param key the key
     * @return the last feature
     */
    String getLastFeature(String key);
    
    /**
     * Adds a new feature to this document.
     * @param key feature key
     * @param value feature value
     * @throws IllegalArgumentException if key is equal to the static feature key
     */
    void addFeature(String key, String value);
    
    /**
     * Adds the features from the specified mapping.
     * @param mapping key-value pairs to add
     * @throws IllegalArgumentException if one of the keys is equal to the static feature key
     */
    void addFeatures(Map<String,String> mapping);
    
    /**
     * Adds all feature values from the specified mapping.
     * @param mapping key-values pairs to add
     * @throws IllegalArgumentException if one of the keys is equal to the static feature key
     */
    void addMultiFeatures(Map<String,List<String>> mapping);
    
    /**
     * Writes the features as XML.
     * Does not write the static feature.
     * @param out
     * @param tag
     * @param name
     * @param value
     * @throws IOException 
     */
    void featuresToXML(PrintStream out, String tag, String name, String value) throws IOException;

    /**
     * Optimized UTF8 write.
     * @throws IOException 
     */
    void write(PrintStream out, String s) throws IOException;

    /**
     * Checks if this element is featureless.
     */
    boolean isFeatureless();

    /**
     * Accepts the specified visitor.
     * @param <R>
     * @param <P>
     * @param visitor
     * @param param
     * @return the visitor return value
     */
    <R,P> R accept(ElementVisitor<R,P> visitor, P param);
    
    String getStringId();
    
	Map<String,List<String>> getFeatures();
	
	ElementType getType();
	
	Element getParent();
	
	Element getOriginal();

	String getStaticFeatureKey();

	boolean isStaticFeatureKey(String key);
}
