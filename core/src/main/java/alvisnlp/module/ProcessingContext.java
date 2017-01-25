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

import java.io.File;
import java.io.IOException;
import java.util.Locale;
import java.util.logging.Logger;

import org.bibliome.util.Timer;

import alvisnlp.module.lib.External;

/**
 * A ProcessingContext object stores global parameters for the processing engine, such as: current locale, root temporary directory, etc.
 */
public interface ProcessingContext<T extends Annotable> {
    /**
     * Returns the current locale used for displaying documentation and error messages.
     * @return the current locale used for displaying documentation and error messages
     */
    Locale getLocale();

    /**
     * Sets the current locale used for displaying documentation and error messages.
     * @param locale
     */
    void setLocale(Locale locale);

    /**
     * Returns the top level temporary directory.
     * @return the top level temporary directory
     */
    File getRootTempDir();
    
    /**
     * Sets the top level temporary directory. Modules running in this context will put temporary files in a subdirectory of the specified directory.
     * @param rootTempDir
     */
    void setRootTempDir(File rootTempDir);
    
    File getTempDir(Module<T> module);

    /**
     * Processes the specified corpus with the specified module.
     * @param module
     * @param corpus
     * @throws ModuleException
     */
    void processCorpus(Module<T> module, T corpus) throws ModuleException;

    /**
     * Returns the resume mode status. In the resume mode status, each module will check if the corpus has already been processed by it. If it is the case, the module will skip processing.
     * @return the resume mode status
     */
    boolean isResumeMode();

    /**
     * Sets the resume mode status.
     * @param mode
     */
    void setResumeMode(boolean mode);

    /**
     * Calls an external program.
     * @param ext
     * @throws ModuleException
     */
    void callExternal(External<T> ext, String outCharset) throws ModuleException;

    void callExternal(External<T> ext, String outCharset, File saveCL) throws ModuleException;

    void callExternal(External<T> ext) throws ModuleException;

    void callExternal(External<T> ext, File saveCL) throws ModuleException;

    /**
     * Sets either to dump the corpus if a module requires it.
     * @param dumps
     */
    void setDumps(boolean dumps);

    /**
     * Returns either to dump the corpus if required.
     * @return true, if is dumps
     */
    boolean isDumps();

    /**
     * Returns the timer for this processing context.
     */
    Timer<TimerCategory> getTimer();
    
    /**
     * Performs all possible checks on the current main module.
     * @param logger
     * @throws ModuleException 
     */
    public boolean checkPlan(Logger logger, Module<T> mainModule) throws ModuleException;
    
    Logger getLogger(String name);

	boolean isCleanTmpDir();
	
	Annotable.Dumper<T> getDumper(File file) throws IOException;
}

