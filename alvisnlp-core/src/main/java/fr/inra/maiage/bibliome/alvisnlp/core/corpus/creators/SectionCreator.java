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


package fr.inra.maiage.bibliome.alvisnlp.core.corpus.creators;

import fr.inra.maiage.bibliome.alvisnlp.core.corpus.NameType;
import fr.inra.maiage.bibliome.alvisnlp.core.module.lib.Param;
import fr.inra.maiage.bibliome.alvisnlp.core.module.types.Mapping;

/**
 * A section creator sets constant features to new sections.
 * 
 * @author rbossy
 */
public interface SectionCreator extends ElementCreator {
    /**
     * Gets the constant section features.
     * @return the constant section features
     */
    @Param(mandatory=false, nameType=NameType.FEATURE)
    public Mapping getConstantSectionFeatures();

    /**
     * Sets the constant section features.
     * @param constantSectionFeatures
     *            the new constant section features
     */
    public void setConstantSectionFeatures(Mapping constantSectionFeatures);
}
