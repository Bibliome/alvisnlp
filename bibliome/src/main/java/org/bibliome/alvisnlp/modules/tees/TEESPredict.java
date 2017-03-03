package org.bibliome.alvisnlp.modules.tees;

import org.bibliome.alvisnlp.modules.CorpusModule;
import org.bibliome.alvisnlp.modules.ResolvedObjects;

import alvisnlp.corpus.Corpus;
import alvisnlp.corpus.expressions.ResolverException;
import alvisnlp.module.ModuleException;
import alvisnlp.module.ProcessingContext;

public class TEESPredict extends CorpusModule<ResolvedObjects> {

	@Override
	public void process(ProcessingContext<Corpus> ctx, Corpus corpus) throws ModuleException {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected ResolvedObjects createResolvedObjects(ProcessingContext<Corpus> ctx) throws ResolverException {
		// TODO Auto-generated method stub
		return new ResolvedObjects(ctx, this);
	}

}
