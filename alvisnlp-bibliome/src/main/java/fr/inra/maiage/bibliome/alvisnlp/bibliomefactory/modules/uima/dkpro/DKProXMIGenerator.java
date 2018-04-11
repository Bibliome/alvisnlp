package fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.uima.dkpro;

import static org.apache.uima.fit.factory.AnalysisEngineFactory.createEngineDescription;
import static org.apache.uima.fit.factory.CollectionReaderFactory.createReaderDescription;
import static org.apache.uima.fit.pipeline.SimplePipeline.runPipeline;

import java.io.IOException;

import org.apache.uima.UIMAException;
import org.apache.uima.resource.ResourceInitializationException;

import de.tudarmstadt.ukp.dkpro.core.api.io.JCasFileWriter_ImplBase;
import de.tudarmstadt.ukp.dkpro.core.api.io.ResourceCollectionReaderBase;
import de.tudarmstadt.ukp.dkpro.core.io.text.TextReader;
import de.tudarmstadt.ukp.dkpro.core.io.xmi.XmiWriter;
import de.tudarmstadt.ukp.dkpro.core.languagetool.LanguageToolLemmatizer;
import de.tudarmstadt.ukp.dkpro.core.opennlp.OpenNlpNamedEntityRecognizer;
import de.tudarmstadt.ukp.dkpro.core.opennlp.OpenNlpPosTagger;
import de.tudarmstadt.ukp.dkpro.core.opennlp.OpenNlpSegmenter;

public class DKProXMIGenerator {
	public static void main(String[] args) throws ResourceInitializationException, UIMAException, IOException {
		runPipeline(
				createReaderDescription(TextReader.class,
						ResourceCollectionReaderBase.PARAM_SOURCE_LOCATION, "../alvisnlp-test/share/BioNLP-ST-2016_BB-cat+ner/train",
						ResourceCollectionReaderBase.PARAM_LANGUAGE, "en",
						ResourceCollectionReaderBase.PARAM_PATTERNS, "[+]*.txt"),
				createEngineDescription(OpenNlpSegmenter.class),
				createEngineDescription(OpenNlpPosTagger.class),
				createEngineDescription(LanguageToolLemmatizer.class),
				createEngineDescription(
						OpenNlpNamedEntityRecognizer.class,
						OpenNlpNamedEntityRecognizer.PARAM_VARIANT, "person"
						),
				createEngineDescription(
						OpenNlpNamedEntityRecognizer.class,
						OpenNlpNamedEntityRecognizer.PARAM_VARIANT, "organization"
						),
				createEngineDescription(
						OpenNlpNamedEntityRecognizer.class,
						OpenNlpNamedEntityRecognizer.PARAM_VARIANT, "location"
						),
				createEngineDescription(
						XmiWriter.class,
						JCasFileWriter_ImplBase.PARAM_TARGET_LOCATION, "../alvisnlp-test/xmi/dkpro",
						JCasFileWriter_ImplBase.PARAM_OVERWRITE, true
						)
				);
	}
}
