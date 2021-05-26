package fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.opennlp;

import java.util.Iterator;
import java.util.List;

import fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.converters.expression.parser.ExpressionParser;
import fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.CorpusModule;
import fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.DefaultExpressions;
import fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.ResolvedObjects;
import fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.opennlp.OpenNLPDocumentCategorizerBase.OpenNLPDocumentCategorizerResolvedObjects;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Corpus;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Element;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.NameType;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.EvaluationContext;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.Evaluator;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.Expression;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.ResolverException;
import fr.inra.maiage.bibliome.alvisnlp.core.module.ModuleException;
import fr.inra.maiage.bibliome.alvisnlp.core.module.NameUsage;
import fr.inra.maiage.bibliome.alvisnlp.core.module.ProcessingContext;
import fr.inra.maiage.bibliome.alvisnlp.core.module.lib.Param;

public abstract class OpenNLPDocumentCategorizerBase extends CorpusModule<OpenNLPDocumentCategorizerResolvedObjects> {
	private Expression documents = DefaultExpressions.CORPUS_DOCUMENTS;
	private Expression tokens = ExpressionParser.parseUnsafe("sections.layer:words");
	private Expression form = DefaultExpressions.ANNOTATION_FORM;
	private String categoryFeature;

	public static class OpenNLPDocumentCategorizerResolvedObjects extends ResolvedObjects {
		private final Evaluator documents;
		private final Evaluator tokens;
		private final Evaluator form;
		
		private OpenNLPDocumentCategorizerResolvedObjects(ProcessingContext<Corpus> ctx, OpenNLPDocumentCategorizerBase module) throws ResolverException {
			super(ctx, module);
			this.documents = module.documents.resolveExpressions(rootResolver);
			this.tokens = module.tokens.resolveExpressions(rootResolver);
			this.form = module.form.resolveExpressions(rootResolver);
		}
	
		@Override
		public void collectUsedNames(NameUsage nameUsage, String defaultType) throws ModuleException {
			super.collectUsedNames(nameUsage, defaultType);
			this.documents.collectUsedNames(nameUsage, defaultType);
			this.tokens.collectUsedNames(nameUsage, defaultType);
			this.form.collectUsedNames(nameUsage, defaultType);
		}
		
		protected Iterator<Element> getDocuments(EvaluationContext evalCtx, Corpus corpus) {
			return this.documents.evaluateElements(evalCtx, corpus);
		}

		protected String[] getDocumentTokens(EvaluationContext evalCtx, Element doc) {
			List<Element> tokens = this.tokens.evaluateList(evalCtx, doc);
			String[] result = new String[tokens.size()];
			for (int i = 0; i < result.length; ++i) {
				result[i] = this.form.evaluateString(evalCtx, tokens.get(i));
			}
			return result;
		}
	}

	public OpenNLPDocumentCategorizerBase() {
		super();
	}

	@Override
	protected OpenNLPDocumentCategorizerResolvedObjects createResolvedObjects(ProcessingContext<Corpus> ctx) throws ResolverException {
		return new OpenNLPDocumentCategorizerResolvedObjects(ctx, this);
	}

	@Param
	public Expression getDocuments() {
		return documents;
	}

	@Param
	public Expression getTokens() {
		return tokens;
	}

	@Param
	public Expression getForm() {
		return form;
	}

	@Param(nameType=NameType.FEATURE)
	public String getCategoryFeature() {
		return categoryFeature;
	}

	public void setDocuments(Expression documents) {
		this.documents = documents;
	}

	public void setTokens(Expression tokens) {
		this.tokens = tokens;
	}

	public void setForm(Expression form) {
		this.form = form;
	}

	public void setCategoryFeature(String categoryFeature) {
		this.categoryFeature = categoryFeature;
	}
}
