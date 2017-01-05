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

import java.io.BufferedReader;
import java.io.File;

import alvisnlp.module.Annotable;
import alvisnlp.module.Module;
import alvisnlp.module.ModuleException;

/**
 * Helper interface to run external programs in modules.
 */
public interface External<T extends Annotable> {
    /**
     * Returns the owner module.
     * @return the owner
     */
    public Module<T> getOwner();

    /**
     * Returns all command line arguments including the program path.
     * @return all command line arguments including the program path
     * @throws ModuleException
     */
    public String[] getCommandLineArgs() throws ModuleException;

    /**
     * Returns all variable values for the external program environment.
     * @return all variable values for the external program environment, or null to use the current environment
     * @throws ModuleException
     */
    public String[] getEnvironment() throws ModuleException;

    /**
     * Returns the external program working directory.
     * @return the external program working directory, or null to use the current directory
     * @throws ModuleException
     */
    public File getWorkingDirectory() throws ModuleException;

    /**
     * Handles standard file descriptors of the called executable.
     * @param out
     * @param err
     * @throws ModuleException
     */
    public void processOutput(BufferedReader out, BufferedReader err) throws ModuleException;
}
