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



package alvisnlp.module.lib;

import alvisnlp.module.Module;
import alvisnlp.module.ProcessingException;

/**
 * Exception thrown when an external program was called and returned a non-zero status.
 */
public class ExternalFailureException extends ProcessingException {
    private static final long serialVersionUID = 1;

    private final String      program;
    private final int         retVal;

    private static final String buildMessage(Module<?> module, String program, int retVal) {
        return program + " called by " + module.getPath() + " has failed returning value " + retVal;
    }

    public ExternalFailureException(Module<?> module, String program, int retVal) {
        super(buildMessage(module, program, retVal));
        this.program = program;
        this.retVal = retVal;
    }

    public ExternalFailureException(Throwable cause, Module<?> module, String program, int retVal) {
        super(buildMessage(module, program, retVal), cause);
        this.program = program;
        this.retVal = retVal;
    }

    /**
     * Returns the program name.
     * @return the program name
     */
    public String getProgram() {
        return program;
    }

    /**
     * Returns the program return value.
     * @return the program return value
     */
    public int getRetVal() {
        return retVal;
    }
}
