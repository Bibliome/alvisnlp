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
 * Base class of exceptions thrown by modules when processing a corpus when something is seriously wrong with a parameter.
 */
public class ParameterException extends ModuleException {
    private static final long serialVersionUID = 1;

    private final String      parameter;

    public ParameterException(String parameter) {
        super();
        this.parameter = parameter;
    }

    public ParameterException(String message, String parameter) {
        super(message);
        this.parameter = parameter;
    }

    public ParameterException(Throwable cause, String parameter) {
        super(cause);
        this.parameter = parameter;
    }

    public ParameterException(String message, Throwable cause, String parameter) {
        super(message, cause);
        this.parameter = parameter;
    }

    /**
     * Returns the parameter concerned by this exception.
     * @return the parameter
     */
    public String getParameter() {
        return parameter;
    }
}
