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

import java.io.IOException;
import java.util.Collection;

/**
 * Annotable objects are store object for modules.
 * @author rbossy
 *
 */
public interface Annotable {
	/**
	 * Returns either this annotable was processed by the module with the specified path.
	 * @param modulePath
	 */
	boolean wasProcessedBy(String modulePath);

	/**
	 * Declares that this annotable was processed by the module with the specified path.
	 * @param modulePath
	 */
	void hasBeenProcessedBy(String modulePath);

	/**
	 * Returns all module paths that have processed this annotable.
	 */
	Collection<String> wasProcessedBy();
	
	public static interface Dumper<A extends Annotable> extends AutoCloseable {
		void dump(A annotatble) throws IOException;
		@Override
		void close() throws IOException;
	}
}
