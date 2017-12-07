package fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.trie;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.SectionModule.SectionResolvedObjects;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Annotation;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Corpus;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.ResolverException;
import fr.inra.maiage.bibliome.alvisnlp.core.module.ModuleException;
import fr.inra.maiage.bibliome.alvisnlp.core.module.ProcessingContext;
import fr.inra.maiage.bibliome.util.streams.SourceStream;
import fr.inra.maiage.bibliome.util.trie.Trie;

public abstract class XLSProjector extends TrieProjector<SectionResolvedObjects,List<String>> {
	private SourceStream xlsFile;
	private String[] valueFeatures;
	private Integer[] sheets;
	private Integer[] keyIndex = new Integer[] { 0 };

	@Override
	protected void fillTrie(Logger logger, Trie<List<String>> trie, Corpus corpus) throws IOException, ModuleException {
		Iterator<InputStream> inputStreams = xlsFile.getInputStreams();
		while (inputStreams.hasNext()) {
			try (InputStream is = inputStreams.next()) {
				Workbook wb = WorkbookFactory.create(is);
				for (int sheetNumber : sheets) {
					Sheet sheet = wb.getSheetAt(sheetNumber);
					for (Row row : sheet) {
						List<String> value = new ArrayList<String>(valueFeatures.length);
						for (int i = 0; i < valueFeatures.length; ++i) {
							Cell cell = row.getCell(i);
							
						}
					}
				}
			}
			catch (EncryptedDocumentException|InvalidFormatException e) {
				rethrow(e);
			}
		}
	}

	@Override
	protected void finish() {
	}

	@Override
	protected boolean marshallingSupported() {
		return false;
	}

	@Override
	protected void handleMatch(List<String> value, Annotation a) {
		final int len = Math.min(valueFeatures.length, value.size());
		for (int i = 0; i < len; ++i)
			a.addFeature(valueFeatures[i], value.get(i));
	}

	@Override
	protected SectionResolvedObjects createResolvedObjects(ProcessingContext<Corpus> ctx) throws ResolverException {
		return new SectionResolvedObjects(ctx, this);
	}

}
