package fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.csv;

import java.io.IOException;
import java.io.Reader;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;

import fr.inra.maiage.bibliome.alvisnlp.core.module.lib.Param;

public interface CSVReaderModule {
	public default CSVParser getParser(Reader r) throws IOException {
		CSVFormat.Builder builder = getFormatBuilder();
		CSVFormat format = builder.build();
		return format.parse(r);
	}
	
	public default CSVFormat.Builder getFormatBuilder() {
		CSVFormat.Builder result = getBaseFormat().builder();
		if (getHeaderLine() != null) {
			result = result.setSkipHeaderRecord(getHeaderLine());
		}
		if (getDelimiter() != null) {
			result = result.setDelimiter(getDelimiter());
		}
		if (getEscape() != null) {
			result = result.setEscape(getEscape());
		}
		if (getTrimValues() != null) {
			result = result.setIgnoreSurroundingSpaces(getTrimValues()).setTrim(getTrimValues());
		}
		if (getQuote() != null) {
			result = result.setQuote(getQuote());
		}
		return result;
	}

	@Param(mandatory = false)
	public Character getQuote();

	@Param(mandatory = false)
	public Boolean getTrimValues();

	@Param(mandatory = false)
	public Character getEscape();

	@Param(mandatory = false)
	public Character getDelimiter();

	@Param(mandatory = false)
	public Boolean getHeaderLine();

	@Param(defaultValue = "org.apache.commons.csv.CSVFormat.DEFAULT")
	public CSVFormat getBaseFormat();

	public void setQuote(Character quote);

	public void setTrimValues(Boolean trimValues);

	public void setEscape(Character escape);

	public void setDelimiter(Character delimiter);

	public void setHeaderLine(Boolean headerLine);
	
	public void setBaseFormat(CSVFormat baseFormat);
}
