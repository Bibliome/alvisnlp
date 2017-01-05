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

/**
 * Element types.
 * @author rbossy
 *
 */
public enum ElementType {
	CORPUS() {
		@Override
		public String toString() {
			return "corpus";
		}
	},
	
	DOCUMENT() {
		@Override
		public String toString() {
			return "document";
		}
	},
	
	SECTION() {
		@Override
		public String toString() {
			return "section";
		}
	},
	
	ANNOTATION() {
		@Override
		public String toString() {
			return "annotation";
		}
	},
	
	RELATION() {
		@Override
		public String toString() {
			return "relation";
		}
	},
	
	TUPLE() {
		@Override
		public String toString() {
			return "tuple";
		}
	},
	
	OTHER() {
		@Override
		public String toString() {
			return "other";
		}		
	};
}
