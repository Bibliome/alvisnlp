package fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.fasttext;

import fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.CorpusModule;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.NameType;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.Expression;
import fr.inra.maiage.bibliome.alvisnlp.core.module.lib.Param;
import fr.inra.maiage.bibliome.util.files.ExecutableFile;

public abstract class FasttextClassifierBase<R extends FasttextClassifierBaseResolvedObjects> extends CorpusModule<R> {
	private ExecutableFile fasttextExecutable;
	private Expression documents;
	private FasttextAttribute[] attributes;
	private String classFeature;

	public FasttextClassifierBase() {
		super();
	}

	@Param
	public Expression getDocuments() {
		return documents;
	}

	@Param(nameType = NameType.FEATURE)
	public String getClassFeature() {
		return classFeature;
	}

	@Param
	public FasttextAttribute[] getAttributes() {
		return attributes;
	}

	@Param
	public ExecutableFile getFasttextExecutable() {
		return fasttextExecutable;
	}

	public void setFasttextExecutable(ExecutableFile fasttextExecutable) {
		this.fasttextExecutable = fasttextExecutable;
	}

	public void setAttributes(FasttextAttribute[] attributes) {
		this.attributes = attributes;
	}

	public void setDocuments(Expression documents) {
		this.documents = documents;
	}

	public void setClassFeature(String classFeature) {
		this.classFeature = classFeature;
	}
}