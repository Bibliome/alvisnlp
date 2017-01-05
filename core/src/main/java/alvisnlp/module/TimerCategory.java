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

/**
 * Timer categories for module processing.
 * @author rbossy
 */
public enum TimerCategory {
	/**
	 * Native module processing.
	 */
	MODULE("module processing"),
	
	/**
	 * External program.
	 */
    EXTERNAL("external program"),
    
    /**
     * Data import for external programs.
     */
    PREPARE_DATA("prepare data"),
    
    /**
     * Data import from external programs.
     */
    COLLECT_DATA("collect data"),
    
    /**
     * Data export.
     */
    EXPORT("export"),
    
    /**
     * Resource import.
     */
    LOAD_RESOURCE("load resource"),
    
    /**
     * Corpus dump.
     */
    DUMP("dump corpus");
    
    private final String face;

	private TimerCategory(String face) {
		this.face = face;
	}

	@Override
	public String toString() {
		return face;
	}
}
