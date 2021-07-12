package fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.fasttext;

import java.io.IOException;

import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Corpus;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.NameType;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.ResolverException;
import fr.inra.maiage.bibliome.alvisnlp.core.module.ModuleException;
import fr.inra.maiage.bibliome.alvisnlp.core.module.ProcessingContext;
import fr.inra.maiage.bibliome.alvisnlp.core.module.ProcessingException;
import fr.inra.maiage.bibliome.alvisnlp.core.module.lib.AlvisNLPModule;
import fr.inra.maiage.bibliome.alvisnlp.core.module.lib.Param;
import fr.inra.maiage.bibliome.util.files.InputFile;

@AlvisNLPModule(beta = true)
public class FasttextClassifierLabel extends FasttextClassifierBase<FasttextClassifierBaseResolvedObjects> {
	private InputFile modelFile;
	private String probabilityFeature;
	
	@Override
	public void process(ProcessingContext<Corpus> ctx, Corpus corpus) throws ModuleException {
		try {
			FasttextClassifierLabelExternalHandler ext = new FasttextClassifierLabelExternalHandler(ctx, this, corpus);
			ext.start();
		}
		catch (InterruptedException|IOException e) {
			throw new ProcessingException(e);
		}
	}

	@Override
	protected FasttextClassifierBaseResolvedObjects createResolvedObjects(ProcessingContext<Corpus> ctx) throws ResolverException {
		return new FasttextClassifierBaseResolvedObjects(ctx, this);
	}

	@Param
	public InputFile getModelFile() {
		return modelFile;
	}

	@Param(mandatory = false, nameType = NameType.FEATURE)
	public String getProbabilityFeature() {
		return probabilityFeature;
	}

	public void setProbabilityFeature(String probabilityFeature) {
		this.probabilityFeature = probabilityFeature;
	}

	public void setModelFile(InputFile modelFile) {
		this.modelFile = modelFile;
	}
}
