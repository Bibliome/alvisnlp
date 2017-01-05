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


package alvisnlp.annotation;

/**
 * Exception thrown when a class declaration does not meet the conditions to be a valid module, converter, function library or function.
 * @author rbossy
 *
 */
public class ModelException extends Exception { // NO_UCD
	private static final long serialVersionUID = 1L;

	public ModelException() {
		super();
	}

	public ModelException(String msg, Throwable cause) {
		super(msg, cause);
	}

	public ModelException(String msg) {
		super(msg);
	}

	public ModelException(Throwable cause) {
		super(cause);
	}
}
