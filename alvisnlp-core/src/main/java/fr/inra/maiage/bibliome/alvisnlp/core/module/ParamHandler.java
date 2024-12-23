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


/**
 * A parameter handler allows to manage a module parameter.
 * @author rbossy
 */
public interface ParamHandler {

    /**
     * Returns the type of the parameter.
     * @return the type of the parameter
     */
    Class<?> getType();

    /**
     * Returns the name of the parameter.
     * @return the name of the parameter
     */
    String getName();

    /**
     * Returns the current value of the parameter.
     * @return the current value of the parameter
     */
    Object getValue();

    /**
     * Sets the value of the parameter.
     * @param value
     */
    void setValue(Object value) throws ParameterException;

    /**
     * Returns either if this parameter is set.
     * @return either if this parameter is set
     */
    boolean isSet();

    /**
     * Returns either if this parameter is mandatory.
     * @return either if this parameter is mandatory
     */
    boolean isMandatory();

    boolean isOutputFeed();

    void setOutputFeed(boolean outputFeed);
    
    Module getModule();
    
    String getNameType();
    
    <P> void accept(ModuleVisitor<P> visitor, P param) throws ModuleException;
    
    String getParamSourceName();
    
    void setParamSourceName(String sourceName);

	boolean isDeprecated();
}
