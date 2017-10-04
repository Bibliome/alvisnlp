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


package alvisnlp.module;

import alvisnlp.corpus.creators.AnnotationCreator;
import alvisnlp.corpus.creators.DocumentCreator;
import alvisnlp.corpus.creators.SectionCreator;
import alvisnlp.corpus.creators.TupleCreator;
import alvisnlp.module.lib.Param;

public interface ActionInterface extends DocumentCreator, SectionCreator, AnnotationCreator, TupleCreator {
	@Param(defaultValue="false")
	Boolean getDeleteElements();

	@Param(defaultValue="false")
	Boolean getSetArguments();
	
	@Param(defaultValue="false")
	Boolean getSetFeatures();
	
	@Param(defaultValue="false")
	Boolean getCreateDocuments();
	
	@Param(defaultValue="false")
	Boolean getCreateSections();
	
	@Param(defaultValue="false")
	Boolean getCreateAnnotations();
	
	@Param(defaultValue="false")
	Boolean getCreateRelations();
	
	@Param(defaultValue="false")
	Boolean getCreateTuples();
	
	@Param(defaultValue="false")
	Boolean getAddToLayer();
	
	@Param(defaultValue="false")
	Boolean getRemoveFromLayer();
	
	void setDeleteElements(Boolean deleteElements);
	
	void setSetArguments(Boolean setArguments);
	
	void setSetFeatures(Boolean setFeatures);
	
	void setCreateDocuments(Boolean createDocuments);
	
	void setCreateSections(Boolean createSections);
	
	void setCreateAnnotations(Boolean createAnnotations);
	
	void setCreateRelations(Boolean createRelations);
	
	void setCreateTuples(Boolean createTuples);
	
	void setAddToLayer(Boolean addToLayer);
	
	void setRemoveFromLayer(Boolean removeFromLayer);
}
