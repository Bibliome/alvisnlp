package fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.python;

import fr.inra.maiage.bibliome.alvisnlp.core.corpus.DefaultNames;
import fr.inra.maiage.bibliome.alvisnlp.core.module.lib.AlvisNLPModule;
import fr.inra.maiage.bibliome.alvisnlp.core.module.lib.Param;
import fr.inra.maiage.bibliome.alvisnlp.core.module.types.ExpressionMapping;
import fr.inra.maiage.bibliome.util.files.OutputFile;

@AlvisNLPModule(beta = true)
public abstract class Stanza extends InternalPythonScript {
	private Boolean pretokenized = false;
	private Boolean parse = false;
	private Boolean ner = false;
	private Boolean constituency = false;
	private String language = "en";

	@Override
	protected String getScriptName() {
		return "stanza-alvisnlp.py";
	}

	@Override
	public String[] getLayerNames() {
		if (pretokenized) {
			return new String[] { DefaultNames.getSentenceLayer(), DefaultNames.getWordLayer() };
		}
		return new String[0];
	}

	@Override
	public String[] getLayers() {
		if (pretokenized) {
			return new String[] { DefaultNames.getSentenceLayer(), DefaultNames.getWordLayer() };
		}
		return new String[0];
	}

	@Override
	public String[] getRelations() {
		return new String[0];
	}

	@Override
	public void fillScriptParams(ExpressionMapping mapping) {
		addScriptParam(mapping, "lang", language);
		addScriptParam(mapping, "pretokenized", pretokenized);
		addScriptParam(mapping, "parse", parse);
		addScriptParam(mapping, "constituency", constituency);
		addScriptParam(mapping, "ner", ner);
	}

	@Override
	public OutputFile getOutputFile() {
		return null;
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

	@Param
	public Boolean getConstituency() {
		return constituency;
	}

	public void setConstituency(Boolean constituency) {
		this.constituency = constituency;
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
