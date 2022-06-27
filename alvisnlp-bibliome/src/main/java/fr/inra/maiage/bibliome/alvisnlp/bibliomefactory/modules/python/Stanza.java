package fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.python;

import java.util.Collections;
import java.util.List;

import fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.converters.expression.parser.ExpressionParser;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.DefaultNames;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.Expression;
import fr.inra.maiage.bibliome.alvisnlp.core.module.lib.AlvisNLPModule;
import fr.inra.maiage.bibliome.alvisnlp.core.module.lib.Param;
import fr.inra.maiage.bibliome.alvisnlp.core.module.types.ExpressionMapping;
import fr.inra.maiage.bibliome.util.streams.CompressionFilter;
import fr.inra.maiage.bibliome.util.streams.ResourceSourceStream;
import fr.inra.maiage.bibliome.util.streams.SourceStream;

@AlvisNLPModule(beta = true)
public abstract class Stanza extends PythonScriptBase {
	private Boolean pretokenized;
	private Boolean parse;
	private Boolean ner;
	private String language = "en";
	
	@Override
	public String[] getCommandLine() {
		return new String[0];
	}

	@Override
	protected boolean isScriptCopy() {
		return true;
	}

	@Override
	public Boolean getCallPython() {
		return true;
	}

	@Override
	public SourceStream getScript() {
		List<String> bases = Collections.singletonList(Stanza.class.getPackage().getName().replace('.', '/'));
		String name = "stanza-alvisnlp.py";
		return new ResourceSourceStream("UTF-8", CompressionFilter.NONE, bases, name);
	}

	@Override
	public String[] getLayerNames() {
		if (pretokenized) {
			return new String[] { DefaultNames.getSentenceLayer(), DefaultNames.getWordLayer() };
		}
		return new String[0];
	}

	private static Expression getStringConstant(String s) {
		return ExpressionParser.parseUnsafe("\"" + s + "\"");
	}
	
	private static Expression getStringConstant(boolean b) {
		return getStringConstant(b ? "yes" : "no");
	}
	
	@Override
	public ExpressionMapping getScriptParams() {
		ExpressionMapping result = new ExpressionMapping();
		result.put("lang", getStringConstant(language));
		result.put("pretokenized", getStringConstant(pretokenized));
		result.put("parse", getStringConstant(parse));
		result.put("ner", getStringConstant(ner));
		return result;
	}

	@Param
	public Boolean getPretokenized() {
		return pretokenized;
	}

	@Param
	public Boolean getParse() {
		return parse;
	}

	@Param
	public Boolean getNer() {
		return ner;
	}

	@Param
	public String getLanguage() {
		return language;
	}

	public void setPretokenized(Boolean pretokenized) {
		this.pretokenized = pretokenized;
	}

	public void setParse(Boolean parse) {
		this.parse = parse;
	}

	public void setNer(Boolean ner) {
		this.ner = ner;
	}

	public void setLanguage(String language) {
		this.language = language;
	}
}
