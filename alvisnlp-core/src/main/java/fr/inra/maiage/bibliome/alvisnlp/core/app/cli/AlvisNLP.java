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


package fr.inra.maiage.bibliome.alvisnlp.core.app.cli;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.xml.transform.TransformerConfigurationException;

import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Corpus;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.dump.Undumper;
import fr.inra.maiage.bibliome.alvisnlp.core.factory.CompoundModuleFactory;
import fr.inra.maiage.bibliome.alvisnlp.core.factory.ModuleFactory;
import fr.inra.maiage.bibliome.alvisnlp.core.module.ProcessingContext;
import fr.inra.maiage.bibliome.util.Pair;
import fr.inra.maiage.bibliome.util.clio.CLIOption;

/**
 * AlvisNLP CLI for legacy corpus annotable model.
 * @author rbossy
 *
 */
public class AlvisNLP extends AbstractAlvisNLP { 
	private final List<Pair<String,String>> corpusFeatures = new ArrayList<Pair<String,String>>();
	
	/**
	 * Creates a AlvisNLP CLI instance.
	 * @throws TransformerConfigurationException
	 * @throws IOException 
	 */
	public AlvisNLP() throws TransformerConfigurationException, IOException {
		super();
	}
	
	@Override
    protected ModuleFactory getModuleFactory() {
        CompoundModuleFactory result = new CompoundModuleFactory();
        result.loadServiceFactories(ModuleFactory.class, null, null, null);
        return result;
    }

	@Override
    protected Corpus getCorpus(Logger logger, ProcessingContext ctx) throws IOException {
		Corpus result = createCorpus(logger, ctx);
        for (Pair<String,String> f : corpusFeatures) {
        	logger.info("setting corpus feature " + f.first + " = " + f.second);
        	result.addFeature(f.first, f.second);
        }
        return result;
    }
	
	private Corpus createCorpus(Logger logger, ProcessingContext ctx) throws IOException {
		Corpus result = new Corpus();
		for (File resumeFile : getResumeFiles()) {
			logger.info("reading corpus dump " + resumeFile.getCanonicalPath());
			try (Undumper undumper = new Undumper(logger, resumeFile)) {
				undumper.readCorpus(result);
			}
		}
		return result;
	}

	@Override
	protected CommandLineProcessingContext newCommandLineProcessingContext() {
		return new CommandLineProcessingContext(timer);
	}

    public static void main(String[] args) throws Exception {
    	AlvisNLP inst = new AlvisNLP();
    	inst.run(args);
    	System.exit(inst.getExitCode());
    }

	@Override
	public String getResourceBundleName() {
		return AlvisNLP.class.getCanonicalName() + "Help";
	}

	@Override
	protected void logFinished(Logger logger, Corpus corpus) {
		if (!noProcess) {
			logger.info("annotations: " + corpus.countAnnotations());
			logger.info("postings: " + corpus.countPostings());
		}
	}
	
	@CLIOption("-feat")
	public void addCorpusFeature(String name, String value) {
		corpusFeatures.add(new Pair<String,String>(name, value));
	}
}
