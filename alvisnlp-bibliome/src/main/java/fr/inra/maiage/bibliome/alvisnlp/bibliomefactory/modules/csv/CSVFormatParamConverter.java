package fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.csv;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.QuoteMode;

import fr.inra.maiage.bibliome.alvisnlp.core.converters.ConverterException;
import fr.inra.maiage.bibliome.alvisnlp.core.converters.lib.Converter;
import fr.inra.maiage.bibliome.alvisnlp.core.converters.lib.SimpleParamConverter;

@Converter(targetType = CSVFormat.class)
public class CSVFormatParamConverter extends SimpleParamConverter<CSVFormat> {
	@Override
	public String[] getAlternateAttributes() {
		return new String[] {};
	}

	@Override
	protected CSVFormat convertTrimmed(String stringValue) throws ConverterException {
		String canonical = stringValue.toLowerCase().replace("_", "").replace("-", "");
		switch (canonical) {
			case "default": return CSVFormat.DEFAULT;
			case "excel": return CSVFormat.EXCEL;
			case "mysql": return CSVFormat.MYSQL;
			case "rfc":
			case "rfc4180": return CSVFormat.RFC4180;
			case "oracle": return CSVFormat.ORACLE;
			case "postgresql":
			case "psql":
			case "psqlcsv":
			case "postgresqlcsv": return CSVFormat.POSTGRESQL_CSV;
			case "psqltext":
			case "psqltxt":
			case "postgresqltxt":
			case "postgresqltext": return CSVFormat.POSTGRESQL_TEXT;
			case "tdf": return CSVFormat.TDF;
			case "tab":
			case "tabular": {
				CSVFormat.Builder builder = CSVFormat.Builder.create()
						.setAllowDuplicateHeaderNames(false)
						.setAllowMissingColumnNames(false)
						.setCommentMarker(null)
						.setDelimiter('\t')
						.setEscape('\\')
						.setIgnoreEmptyLines(false)
						.setIgnoreHeaderCase(false)
						.setIgnoreSurroundingSpaces(true)
						.setNullString(null)
						.setQuote(null)
						.setQuoteMode(QuoteMode.NONE)
						.setRecordSeparator('\n')
						.setSkipHeaderRecord(false)
						.setTrailingDelimiter(false)
						.setTrim(true);
				return builder.build();
			}
		}
		cannotConvertString(stringValue, "must be one of: deault, excel, mysql, rfc4180, oracle, postgresql_csv, postgresql_text, tdf, tab");
		return null;
	}
}
