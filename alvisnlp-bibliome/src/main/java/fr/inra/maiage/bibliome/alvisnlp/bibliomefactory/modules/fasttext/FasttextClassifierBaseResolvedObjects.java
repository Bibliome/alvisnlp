package fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.fasttext;

import fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.ResolvedObjects;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Corpus;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.Evaluator;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.ResolverException;
import fr.inra.maiage.bibliome.alvisnlp.core.module.ModuleException;
import fr.inra.maiage.bibliome.alvisnlp.core.module.NameUsage;
import fr.inra.maiage.bibliome.alvisnlp.core.module.ProcessingContext;

class FasttextClassifierBaseResolvedObjects extends ResolvedObjects {
	private final Evaluator documents;
	private final FasttextAttribute.Resolved[] attributes;
	private final Evaluator validationDocuments;
	private final FasttextAttribute.Resolved[] validationAttributes;

	public FasttextClassifierBaseResolvedObjects(ProcessingContext<Corpus> ctx, FasttextClassifierBase module) throws ResolverException {
		super(ctx, module);
		this.documents = module.getDocuments().resolveExpressions(rootResolver);
		this.attributes = rootResolver.resolveArray(module.getAttributes(), FasttextAttribute.Resolved.class);
		this.validationDocuments = rootResolver.resolveNullable(module.getValidationDocuments());
		this.validationAttributes = rootResolver.resolveNullableArray(module.getValidationAttributes(), FasttextAttribute.Resolved.class);
	}

	@Override
	public void collectUsedNames(NameUsage nameUsage, String defaultType) throws ModuleException {
		super.collectUsedNames(nameUsage, defaultType);
		this.documents.collectUsedNames(nameUsage, defaultType);
		nameUsage.collectUsedNamesArray(attributes, defaultType);
		nameUsage.collectUsedNamesNullable(validationDocuments, defaultType);
		nameUsage.collectUsedNamesNullableArray(validationAttributes, defaultType);
	}

	public Evaluator getDocuments() {
		return documents;
	}

	public FasttextAttribute.Resolved[] getAttributes() {
		return attributes;
	}

	public Evaluator getValidationDocuments() {
		return validationDocuments;
	}

	public FasttextAttribute.Resolved[] getValidationAttributes() {
		return validationAttributes;
	}
}
