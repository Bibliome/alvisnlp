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
 * Exception thrown when a module is required to handle an unknown parameter.
 */
public class UnexpectedParameterException extends ParameterException {
    private static final long serialVersionUID = 1;

    private static final String buildMessage(Module<?> module, String parameter) {
        return "parameter " + parameter + " is not recognized by " + module.getPath();
    }

    public UnexpectedParameterException(Module<?> module, String parameter) {
        super(buildMessage(module, parameter), parameter);
    }

    private UnexpectedParameterException(Throwable cause, Module<?> module, String parameter) {
        super(buildMessage(module, parameter), cause, parameter);
    }
}
