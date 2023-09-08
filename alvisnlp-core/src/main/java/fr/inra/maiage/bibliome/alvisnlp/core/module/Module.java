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



package fr.inra.maiage.bibliome.alvisnlp.core.module;

import java.util.Collection;
import java.util.logging.Logger;

import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Corpus;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.creators.ElementCreator;
import fr.inra.maiage.bibliome.alvisnlp.core.documentation.Documentable;
import fr.inra.maiage.bibliome.util.Timer;
import fr.inra.maiage.bibliome.util.files.OutputFile;

/**
 * A module is processing unit.
 */
public interface Module extends ElementCreator, Documentable {
    /**
     * Returns this module identifier.
     * @return this module identifier
     */
    public String getId();

    /**
     * Sets this module identifier.
     * @param id
     */
    public void setId(String id);

    /**
     * Returns this module path.
     * @return this module path
     */
    public String getPath();

    String getModuleClass();
    
    /**
     * Returns this module logger.
     * @param ctx TODO
     * @return this module logger
     */
    public Logger getLogger(ProcessingContext ctx);

    /**
     * Returns the parent sequence of this module.
     * @return the parent sequence of this module
     */
    public Sequence getSequence();

    /**
     * Sets this module parent sequence.
     * @param sequence
     */
    public void setSequence(Sequence sequence);

    /**
     * Processes a corpus with this module.
     * @param ctx processing context
     * @param corpus corpus to process
     * @throws ModuleException if anything went seriously wrong during the processing
     */
    public void process(ProcessingContext ctx, Corpus corpus) throws ModuleException;

    /**
     * Returns the file where to dump an XML serialization of the corpus, if any.
     * @return the file where to dump the corpus, if this module is not required to dump the corpus, then returns null.
     */
    public OutputFile getDumpFile();

    /**
     * Sets the file where to dump the corpus.
     * @param file path to the dump file
     */
    public void setDumpFile(OutputFile file);

    

    /**
     * Returns a handler for the parameter with the specified name.
     * @param name name of the parameter
     * @return a handler for the parameter with the specified name
     * @throws UnexpectedParameterException if this module does not support the specified parameter
     */
    public ParamHandler getParamHandler(String name) throws UnexpectedParameterException;

    /**
     * Returns parameter handlers for all supported parameters.
     * @return parameter handlers for all supported parameters
     */
    public Collection<ParamHandler> getAllParamHandlers();

    /**
     * Release resources created during this module processing.
     */
    public void clean();
    
    /**
     * Initializes this module.
     * This method should be called after its creation.
     * @throws ModuleException
     */
    public void init(ProcessingContext ctx) throws ModuleException;

	/**
	 * Returns the timer for this module.
	 * @param ctx
	 */
	Timer<TimerCategory> getTimer(ProcessingContext ctx);

	/**
	 * Returns a timer for the specified task and category.
	 * @param ctx
	 * @param task
	 * @param category
	 * @param start either to start the timer
	 */
	Timer<TimerCategory> getTimer(ProcessingContext ctx, String task, TimerCategory category, boolean start);
	
	Module getModuleByPath(String modulePath);
	
	boolean isBeta();
	
	<P> void accept(ModuleVisitor<P> visitor, P param) throws ModuleException;

	boolean testProcess(ProcessingContext ctx, Corpus corpus) throws ModuleException;
	
	String getModuleSourceName();

	boolean isDeprecated();
}
