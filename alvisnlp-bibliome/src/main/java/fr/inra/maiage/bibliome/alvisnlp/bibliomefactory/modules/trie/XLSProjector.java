package fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.trie;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

import org.apache.poi.EncryptedDocumentException;
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
import fr.inra.maiage.bibliome.alvisnlp.core.module.ProcessingException;
import fr.inra.maiage.bibliome.alvisnlp.core.module.TimerCategory;
import fr.inra.maiage.bibliome.alvisnlp.core.module.lib.AlvisNLPModule;
import fr.inra.maiage.bibliome.alvisnlp.core.module.lib.Param;
import fr.inra.maiage.bibliome.alvisnlp.core.module.lib.TimeThis;
import fr.inra.maiage.bibliome.util.marshall.Decoder;
import fr.inra.maiage.bibliome.util.marshall.Encoder;
import fr.inra.maiage.bibliome.util.streams.SourceStream;
import fr.inra.maiage.bibliome.util.trie.Trie;

@AlvisNLPModule(beta=true)
public abstract class XLSProjector extends TrieProjector<SectionResolvedObjects,List<String>> {
	private SourceStream xlsFile;
	private String[] valueFeatures;
	private Integer[] sheets = new Integer[] { 0 };
	private Integer[] keyIndex = new Integer[] { 0 };
	private Boolean headerRow = false;

	@Override
	@TimeThis(task="create-trie", category=TimerCategory.LOAD_RESOURCE)
	protected Trie<List<String>> getTrie(ProcessingContext<Corpus> ctx, Logger logger, Corpus corpus) throws IOException, ModuleException {
		return super.getTrie(ctx, logger, corpus);
	}

	@Override
	protected void fillTrie(Logger logger, Trie<List<String>> trie, Corpus corpus) throws IOException, ModuleException {
		Iterator<InputStream> inputStreams = xlsFile.getInputStreams();
		while (inputStreams.hasNext()) {
			try (InputStream is = inputStreams.next()) {
				Workbook wb = WorkbookFactory.create(is);
				for (int sheetNumber : sheets) {
					Sheet sheet = wb.getSheetAt(sheetNumber);
					fillSheetEntries(trie, sheet);
				}
			}
			catch (EncryptedDocumentException e) {
				throw new ProcessingException(e);
			}
		}
	}

	private void fillSheetEntries(Trie<List<String>> trie, Sheet sheet) {
		boolean headerRow = this.headerRow;
		for (Row row : sheet) {
			if (headerRow) {
				headerRow = false;
				continue;
			}
			List<String> value = createRowValue(row);
			for (int i : keyIndex) {
				trie.addEntry(value.get(i), value);
			}
		}
	}

	private List<String> createRowValue(Row row) {
		List<String> result = new ArrayList<String>(valueFeatures.length);
		for (int i = 0; i < valueFeatures.length; ++i) {
			Cell cell = row.getCell(i, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
			String text = getCellText(cell);
			result.add(text);
		}
		return result;
	}

	private static String getCellText(Cell cell) {
		switch (cell.getCellType()) {
			case STRING: return cell.getStringCellValue();
			case BOOLEAN: return Boolean.toString(cell.getBooleanCellValue());
			case NUMERIC: return Double.toString(cell.getNumericCellValue());
			default: return "";
		}
	}

	@Override
	protected void finish() {
	}

	@Override
	protected boolean marshallingSupported() {
		return true;
	}

	@Override
	protected Decoder<List<String>> getDecoder() {
		return StringListCodex.INSANCE;
	}

	@Override
	protected Encoder<List<String>> getEncoder() {
		return StringListCodex.INSANCE;
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

	@Param
	public SourceStream getXlsFile() {
		return xlsFile;
	}

	@Param
	public String[] getValueFeatures() {
		return valueFeatures;
	}

	@Param
	public Integer[] getSheets() {
		return sheets;
	}

	@Param
	public Integer[] getKeyIndex() {
		return keyIndex;
	}

	@Param
	public Boolean getHeaderRow() {
		return headerRow;
	}

	public void setHeaderRow(Boolean headerRow) {
		this.headerRow = headerRow;
	}

	public void setXlsFile(SourceStream xlsFile) {
		this.xlsFile = xlsFile;
	}

	public void setValueFeatures(String[] valueFeatures) {
		this.valueFeatures = valueFeatures;
	}

	public void setSheets(Integer[] sheets) {
		this.sheets = sheets;
	}

	public void setKeyIndex(Integer[] keyIndex) {
		this.keyIndex = keyIndex;
	}
}
