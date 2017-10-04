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


package org.bibliome.alvisnlp.modules.rdf;

import org.apache.jena.riot.RDFFormat;

import alvisnlp.converters.ConverterException;
import alvisnlp.converters.lib.Converter;
import alvisnlp.converters.lib.SimpleParamConverter;

@Converter(targetType=RDFFormat.class)
public class RDFFormatParamConverter extends SimpleParamConverter<RDFFormat> {
	@Override
	public String[] getAlternateAttributes() {
		return new String[] { "format" };
	}

	@Override
	protected RDFFormat convertTrimmed(String stringValue) throws ConverterException {
		String format = stringValue.toLowerCase().replaceAll("[_/-]", "");
		switch (format) {
			 case "jsonld": return RDFFormat.JSONLD;
			 case "jsonldflat": return RDFFormat.JSONLD_FLAT;
			 case "jsonldpretty": return RDFFormat.JSONLD_PRETTY;
			 case "nq": return RDFFormat.NQ;
			 case "nquads": return RDFFormat.NQUADS;
			 case "nquadsascii": return RDFFormat.NQUADS_ASCII;
			 case "nquadsutf8": return RDFFormat.NQUADS_UTF8;
			 case "nt": return RDFFormat.NT;
			 case "ntriples": return RDFFormat.NTRIPLES;
			 case "ntriplesascii": return RDFFormat.NTRIPLES_ASCII;
			 case "ntriplesutf8": return RDFFormat.NTRIPLES_UTF8;
			 case "rdfjson": return RDFFormat.RDFJSON;
			 case "rdfxml": return RDFFormat.RDFXML;
			 case "rdfxmlabbrev": return RDFFormat.RDFXML_ABBREV;
			 case "rdfxmlplain": return RDFFormat.RDFXML_PLAIN;
			 case "rdfxmlpretty": return RDFFormat.RDFXML_PRETTY;
			 case "trig": return RDFFormat.TRIG;
			 case "trigblocks": return RDFFormat.TRIG_BLOCKS;
			 case "trigflat": return RDFFormat.TRIG_FLAT;
			 case "trigpretty": return RDFFormat.TRIG_PRETTY;
			 case "ttl": return RDFFormat.TTL;
			 case "turtle": return RDFFormat.TURTLE;
			 case "turtleblocks": return RDFFormat.TURTLE_BLOCKS;
			 case "turtleflat": return RDFFormat.TURTLE_FLAT;
			 case "turtlepretty": return RDFFormat.TURTLE_PRETTY;
		}
		cannotConvertString(stringValue, "unknown format");
		return null;
	}

	@Override
	public String getStringValue(Object value) throws ConverterException {
        if (value.equals(RDFFormat.JSONLD)) { return "jsonld"; }
        if (value.equals(RDFFormat.JSONLD_FLAT)) { return "jsonld-flat"; }
        if (value.equals(RDFFormat.JSONLD_PRETTY)) { return "jsonld-pretty"; }
        if (value.equals(RDFFormat.NQ)) { return "nq"; }
        if (value.equals(RDFFormat.NQUADS)) { return "nquads"; }
        if (value.equals(RDFFormat.NQUADS_ASCII)) { return "nquad-sascii"; }
        if (value.equals(RDFFormat.NQUADS_UTF8)) { return "nquads-utf8"; }
        if (value.equals(RDFFormat.NT)) { return "nt"; }
        if (value.equals(RDFFormat.NTRIPLES)) { return "ntriples"; }
        if (value.equals(RDFFormat.NTRIPLES_ASCII)) { return "ntriples-ascii"; }
        if (value.equals(RDFFormat.NTRIPLES_UTF8)) { return "ntriples-utf8"; }
        if (value.equals(RDFFormat.RDFJSON)) { return "rdf-json"; }
        if (value.equals(RDFFormat.RDFXML)) { return "rdf-xml"; }
        if (value.equals(RDFFormat.RDFXML_ABBREV)) { return "rdf-xml-abbrev"; }
        if (value.equals(RDFFormat.RDFXML_PLAIN)) { return "rdf-xml-plain"; }
        if (value.equals(RDFFormat.RDFXML_PRETTY)) { return "rdf-xml-pretty"; }
        if (value.equals(RDFFormat.TRIG)) { return "trig"; }
        if (value.equals(RDFFormat.TRIG_BLOCKS)) { return "trig-blocks"; }
        if (value.equals(RDFFormat.TRIG_FLAT)) { return "trig-flat"; }
        if (value.equals(RDFFormat.TRIG_PRETTY)) { return "trig-pretty"; }
        if (value.equals(RDFFormat.TTL)) { return "ttl"; }
        if (value.equals(RDFFormat.TURTLE)) { return "turtle"; }
        if (value.equals(RDFFormat.TURTLE_BLOCKS)) { return "turtle-blocks"; }
        if (value.equals(RDFFormat.TURTLE_FLAT)) { return "turtle-flat"; }
        if (value.equals(RDFFormat.TURTLE_PRETTY)) { return "turtle-pretty"; }
        throw new RuntimeException();
	}
}
