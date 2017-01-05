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


package org.bibliome.alvisnlp.modules.projectors;

public class ProjectorException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public ProjectorException() {
		super();
	}

	public ProjectorException(String message) {
		super(message);
	}

	public ProjectorException(Throwable cause) {
		super(cause);
	}

	public ProjectorException(String message, Throwable cause) {
		super(message, cause);
	}
}
