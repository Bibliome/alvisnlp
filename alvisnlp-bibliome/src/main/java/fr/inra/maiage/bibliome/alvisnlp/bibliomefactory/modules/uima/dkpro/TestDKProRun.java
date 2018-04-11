package fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.uima.dkpro;

import static org.apache.uima.fit.factory.AnalysisEngineFactory.createEngineDescription;
import static org.apache.uima.fit.factory.CollectionReaderFactory.createReaderDescription;
import static org.apache.uima.fit.pipeline.SimplePipeline.runPipeline;

import java.io.IOException;

import org.apache.uima.UIMAException;
import org.apache.uima.resource.ResourceInitializationException;

import de.tudarmstadt.ukp.dkpro.core.api.io.JCasFileWriter_ImplBase;
import de.tudarmstadt.ukp.dkpro.core.api.io.ResourceCollectionReaderBase;
import de.tudarmstadt.ukp.dkpro.core.io.conll.Conll2006Writer;
import de.tudarmstadt.ukp.dkpro.core.io.text.TextReader;
import de.tudarmstadt.ukp.dkpro.core.languagetool.LanguageToolLemmatizer;
import de.tudarmstadt.ukp.dkpro.core.opennlp.OpenNlpPosTagger;
import de.tudarmstadt.ukp.dkpro.core.opennlp.OpenNlpSegmenter;

public class TestDKProRun {
	public static void main(String[] args) throws ResourceInitializationException, UIMAException, IOException {
		runPipeline(
				createReaderDescription(TextReader.class,
						ResourceCollectionReaderBase.PARAM_SOURCE_LOCATION, "/home/rbossy/code/alvisnlp/alvisnlp-test/share/BioNLP-ST-2016_BB-event/train/BB-event-23702192.txt",
						ResourceCollectionReaderBase.PARAM_LANGUAGE, "en"),
				createEngineDescription(OpenNlpSegmenter.class),
				createEngineDescription(OpenNlpPosTagger.class),
				createEngineDescription(LanguageToolLemmatizer.class),
				createEngineDescription(
						Conll2006Writer.class,
						JCasFileWriter_ImplBase.PARAM_TARGET_LOCATION, "/home/rbossy/code/alvisnlp")
				);
	}
}
