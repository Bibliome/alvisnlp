package fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.rdf;

import fr.inra.maiage.bibliome.alvisnlp.core.converters.ConverterException;
import fr.inra.maiage.bibliome.alvisnlp.core.converters.lib.Converter;
import fr.inra.maiage.bibliome.alvisnlp.core.converters.lib.SimpleParamConverter;
import org.apache.jena.riot.Lang;

@Converter(targetType=Lang.class)
public class LangParamConverter extends SimpleParamConverter<Lang> {
	@Override
	public String[] getAlternateAttributes() {
		return new String[] { "format", "fmt", "lang", "language" };
	}

	@Override
	protected Lang convertTrimmed(String stringValue) throws ConverterException {
		String canonicalValue = stringValue.toLowerCase().replace("-", "");
		switch (canonicalValue) {
			case "xml":
			case "rdfxml":
			case "xmlrdf":
				return Lang.RDFXML;
			case "turtle":
			case "ttl":
			case "n3":
				return Lang.TURTLE;
			case "ntriple":
			case "ntriples":
			case "nt":
				return Lang.NTRIPLES;
			case "jsonld":
				return Lang.JSONLD;
			case "rdfjson":
			case "jsonrdf":
			case "json":
				return Lang.RDFJSON;
			case "trig":
				return Lang.TRIG;
			case "nquads":
			case "nq":
				return Lang.NQUADS;
			case "rdfthrift":
				return Lang.RDFTHRIFT;
			case "csv":
				return Lang.CSV;
			case "tsv":
				return Lang.TSV;
			case "trix":
				return Lang.TRIX;
		}
		cannotConvertString(stringValue, "unknown format (xml, rdfxml, xmlrdf, turtle, ttl, n3, ntriples, ntriple, nt, jsonld, rdfjson, jsonrdf, json, trig, nquads, nq, nthrift, csv, tsv, trix)");
		return null;
	}
}
