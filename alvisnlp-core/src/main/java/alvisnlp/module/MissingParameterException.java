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
 * Exception thrown by modules when trying to process a corpus if a required
 * parameter has not been set.
 */
public class MissingParameterException extends ParameterException {
    private static final long serialVersionUID = 1;

    private static final String buildMessage(Module<?> module, String parameter) {
        return "mandatory parameter " + parameter + " is not set in " + module.getPath();
    }

    public MissingParameterException(String parameter) {
		super(parameter);
	}

	public MissingParameterException(Module<?> module, String parameter) {
        super(buildMessage(module, parameter), parameter);
    }

    private MissingParameterException(Throwable cause, Module<?> module, String parameter) {
        super(buildMessage(module, parameter), cause, parameter);
    }
}
