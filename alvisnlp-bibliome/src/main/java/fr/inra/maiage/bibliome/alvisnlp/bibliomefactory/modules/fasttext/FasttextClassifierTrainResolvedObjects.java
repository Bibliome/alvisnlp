package fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.fasttext;

import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.Evaluator;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.ResolverException;
import fr.inra.maiage.bibliome.alvisnlp.core.module.ModuleException;
import fr.inra.maiage.bibliome.alvisnlp.core.module.NameUsage;
import fr.inra.maiage.bibliome.alvisnlp.core.module.ProcessingContext;

public class FasttextClassifierTrainResolvedObjects extends FasttextClassifierBaseResolvedObjects {
	private final Evaluator validationDocuments;
	private final FasttextAttribute.Resolved[] validationAttributes;

	public FasttextClassifierTrainResolvedObjects(ProcessingContext ctx, FasttextClassifierTrain module) throws ResolverException {
		super(ctx, module);
		this.validationDocuments = rootResolver.resolveNullable(module.getValidationDocuments());
		this.validationAttributes = rootResolver.resolveNullableArray(module.getValidationAttributes(), FasttextAttribute.Resolved.class);
	}

	@Override
	public void collectUsedNames(NameUsage nameUsage, String defaultType) throws ModuleException {
		super.collectUsedNames(nameUsage, defaultType);
		nameUsage.collectUsedNamesNullable(validationDocuments, defaultType);
		nameUsage.collectUsedNamesNullableArray(validationAttributes, defaultType);
	}

	public Evaluator getValidationDocuments() {
		return validationDocuments;
	}

	public FasttextAttribute.Resolved[] getValidationAttributes() {
		return validationAttributes;
	}

}
