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


package alvisnlp.corpus.creators;

import alvisnlp.corpus.NameType;
import alvisnlp.module.lib.Param;
import alvisnlp.module.types.Mapping;

/**
 * A relation creator sets constant features to new relations.
 * 
 * @author rbossy
 */
public interface RelationCreator extends ElementCreator {

    /**
     * Gets the constant relation features.
     * @return the constant relation features
     */
    @Param(mandatory=false, nameType=NameType.FEATURE, defaultDoc = "Constant features to add to each relation created by this module")
    public Mapping getConstantRelationFeatures();

    /**
     * Sets the constant relation features.
     * @param constantRelationFeatures the new constant relation features
     */
    public void setConstantRelationFeatures(Mapping constantRelationFeatures);

}
