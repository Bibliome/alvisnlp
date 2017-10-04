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


package alvisnlp.corpus.expressions;


/**
 * Expression evaluation type.
 * @author rbossy
 *
 */
public enum EvaluationType {
	UNDEFINED(false),
	BOOLEAN(false),
	INT(false),
	DOUBLE(false),
	STRING(false),
	ELEMENTS(true),
	CORPUS(true),
	DOCUMENTS(true),
	SECTIONS(true),
	ANNOTATIONS(true),
	RELATIONS(true),
	TUPLES(true);
	
	public final boolean element;

	private EvaluationType(boolean element) {
		this.element = element;
	}
}
