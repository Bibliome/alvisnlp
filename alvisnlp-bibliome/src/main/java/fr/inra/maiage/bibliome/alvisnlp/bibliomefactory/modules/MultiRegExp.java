package fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.ws.rs.ProcessingException;

import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.SectionModule.SectionResolvedObjects;
import fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.csv.CSVReaderModule;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Annotation;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Corpus;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Layer;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.NameType;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Section;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.creators.AnnotationCreator;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.EvaluationContext;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.ResolverException;
import fr.inra.maiage.bibliome.alvisnlp.core.module.ModuleException;
import fr.inra.maiage.bibliome.alvisnlp.core.module.ProcessingContext;
import fr.inra.maiage.bibliome.alvisnlp.core.module.lib.AlvisNLPModule;
import fr.inra.maiage.bibliome.alvisnlp.core.module.lib.Param;
import fr.inra.maiage.bibliome.util.Iterators;
import fr.inra.maiage.bibliome.util.Pair;
import fr.inra.maiage.bibliome.util.filelines.InvalidFileLineEntry;
import fr.inra.maiage.bibliome.util.streams.SourceStream;

@AlvisNLPModule(beta = true)
public abstract class MultiRegExp extends SectionModule<SectionResolvedObjects> implements AnnotationCreator, CSVReaderModule {
	private Integer keyColumn = 0;
	private String[] valueFeatures;
	private SourceStream patternsFile;
	private String targetLayer;
	private Boolean caseInsensitive = false;
	private Boolean matchWordBoundaries = false;

	@Override
	public void process(ProcessingContext ctx, Corpus corpus) throws ModuleException {
        try {
			Logger logger = getLogger(ctx);
			List<Pair<Pattern,List<String>>> patterns = getPatterns(logger);
			EvaluationContext evalCtx = new EvaluationContext(logger);
			for (Section sec : Iterators.loop(sectionIterator(evalCtx , corpus))) {
				matchSection(patterns, sec);
			}
		}
        catch (InvalidFileLineEntry|IOException e) {
        	throw new ProcessingException(e);
		}
	}
	
	private List<Pair<Pattern,List<String>>> getPatterns(Logger logger) throws InvalidFileLineEntry, IOException {
		List<Pair<Pattern,List<String>>> result = new ArrayList<Pair<Pattern,List<String>>>();
		try (BufferedReader r = patternsFile.getBufferedReader()) {
			CSVParser parser = getParser(r);
			int flags = getFlags();
			for (CSVRecord record : parser) {
				String re = record.get(keyColumn);
				Pattern pattern = getPattern(re, flags);
				List<String> values = record.toList();
				Pair<Pattern,List<String>> p = new Pair<Pattern,List<String>>(pattern, values);
				result.add(p);
			}
		}
		return result;
	}
	
	private int getFlags() {
		int flags = Pattern.CANON_EQ;
		if (caseInsensitive) {
			flags |= Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE;
		}
		return flags;
	}
	
	private Pattern getPattern(String re, int flags) {
		if (matchWordBoundaries) {
			re = "\\b" + re + "\\b";
		}
		return Pattern.compile(re);
	}
	
	private void matchSection(List<Pair<Pattern,List<String>>> patterns, Section sec) {
		String txt = sec.getContents();
        Layer layer = sec.ensureLayer(targetLayer);
		for (Pair<Pattern,List<String>> e : patterns) {
			Pattern pat = e.first;
			List<String> values = e.second;
			Matcher matcher = pat.matcher(txt);
            while (matcher.find()) {
            	createAnnotation(layer, values, matcher);
            }
		}
	}
	
	private void createAnnotation(Layer layer, List<String> values, Matcher matcher) {
    	Annotation a = new Annotation(this, layer, matcher.start(), matcher.end());
    	for (int i = 0; i < valueFeatures.length; ++i) {
    		a.addFeature(valueFeatures[i], values.get(i));
    	}
	}

	@Override
	protected String[] addLayersToSectionFilter() {
		return null;
	}

	@Override
	protected String[] addFeaturesToSectionFilter() {
		return null;
	}

	@Override
	protected SectionResolvedObjects createResolvedObjects(ProcessingContext ctx) throws ResolverException {
		return new SectionResolvedObjects(ctx, this);
	}

	@Param
	public Integer getKeyColumn() {
		return keyColumn;
	}

	@Param(nameType = NameType.FEATURE)
	public String[] getValueFeatures() {
		return valueFeatures;
	}

	@Param
	public SourceStream getPatternsFile() {
		return patternsFile;
	}

	@Param(nameType = NameType.LAYER)
	public String getTargetLayer() {
		return targetLayer;
	}

	@Param
	public Boolean getCaseInsensitive() {
		return caseInsensitive;
	}

	@Param
	public Boolean getMatchWordBoundaries() {
		return matchWordBoundaries;
	}

	public void setCaseInsensitive(Boolean caseInsensitive) {
		this.caseInsensitive = caseInsensitive;
	}

	public void setMatchWordBoundaries(Boolean matchWordBoundaries) {
		this.matchWordBoundaries = matchWordBoundaries;
	}

	public void setKeyColumn(Integer keyColumn) {
		this.keyColumn = keyColumn;
	}

	public void setValueFeatures(String[] valueFeatures) {
		this.valueFeatures = valueFeatures;
	}

	public void setPatternsFile(SourceStream patternsFile) {
		this.patternsFile = patternsFile;
	}

	public void setTargetLayer(String targetLayer) {
		this.targetLayer = targetLayer;
	}
}
