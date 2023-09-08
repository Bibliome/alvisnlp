package fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules;

import java.util.logging.Logger;

import fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.SetFeature.SetFeatureResolvedObjects;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Corpus;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Element;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.NameType;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.EvaluationContext;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.Evaluator;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.Expression;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.ResolverException;
import fr.inra.maiage.bibliome.alvisnlp.core.module.ActionInterface;
import fr.inra.maiage.bibliome.alvisnlp.core.module.ModuleException;
import fr.inra.maiage.bibliome.alvisnlp.core.module.ProcessingContext;
import fr.inra.maiage.bibliome.alvisnlp.core.module.lib.AlvisNLPModule;
import fr.inra.maiage.bibliome.alvisnlp.core.module.lib.Param;
import fr.inra.maiage.bibliome.alvisnlp.core.module.types.Mapping;
import fr.inra.maiage.bibliome.util.Iterators;

@AlvisNLPModule
public class SetFeature extends CorpusModule<SetFeatureResolvedObjects> {
	private Expression target;
	private String feature;
	private String value;

	@Override
	public void process(ProcessingContext ctx, Corpus corpus) throws ModuleException {
		SetFeatureResolvedObjects res = new SetFeatureResolvedObjects(ctx);
		Logger logger = getLogger(ctx);
		EvaluationContext evalCtx = new EvaluationContext(logger);
		EvaluationContext actionCtx = new EvaluationContext(logger, DummyAction.INSTANCE);
		int n = 0;
		for (Element t : Iterators.loop(res.target.evaluateElements(evalCtx, corpus))) {
			actionCtx.registerSetFeature(t, feature, value);
			n++;
		}
		commit(ctx, actionCtx);
		if (n == 0) {
			logger.warning("no targets visited");
		}
		else {
			logger.info("targets visited: " + n);
		}
	}

	@Override
	protected SetFeatureResolvedObjects createResolvedObjects(ProcessingContext ctx) throws ResolverException {
		return new SetFeatureResolvedObjects(ctx);
	}

	class SetFeatureResolvedObjects extends ResolvedObjects {
		private final Evaluator target;

		private SetFeatureResolvedObjects(ProcessingContext ctx) throws ResolverException {
			super(ctx, SetFeature.this);
			target = rootResolver.resolveNullable(SetFeature.this.target);
		}
	}

	@Param
	public Expression getTarget() {
		return target;
	}

	@Deprecated
	@Param(nameType=NameType.FEATURE)
	public String getFeatureName() {
		return feature;
	}

	@Deprecated
	@Param
	public String getFeatureValue() {
		return value;
	}

	@Param(nameType=NameType.FEATURE)
	public String getFeature() {
		return feature;
	}

	@Param
	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public void setFeature(String feature) {
		this.feature = feature;
	}

	public void setFeatureValue(String featureValue) {
		this.value = featureValue;
	}

	public void setTarget(Expression target) {
		this.target = target;
	}

	public void setFeatureName(String featureName) {
		this.feature = featureName;
	}

	private static enum DummyAction implements ActionInterface {
		INSTANCE;

		@Override
		public Mapping getConstantAnnotationFeatures() {
			return null;
		}

		@Override
		public void setConstantAnnotationFeatures(Mapping constantAnnotationFeatures) {
			throw new RuntimeException("shouldn't call");
		}

		@Override
		public Mapping getConstantSectionFeatures() {
			return null;
		}

		@Override
		public void setConstantSectionFeatures(Mapping constantSectionFeatures) {
			throw new RuntimeException("shouldn't call");
		}

		@Override
		public Mapping getConstantTupleFeatures() {
			return null;
		}

		@Override
		public void setConstantTupleFeatures(Mapping constantRelationFeatures) {
			throw new RuntimeException("shouldn't call");
		}

		@Override
		public Mapping getConstantRelationFeatures() {
			return null;
		}

		@Override
		public void setConstantRelationFeatures(Mapping constantRelationFeatures) {
			throw new RuntimeException("shouldn't call");
		}

		@Override
		public Mapping getConstantDocumentFeatures() {
			return null;
		}

		@Override
		public void setConstantDocumentFeatures(Mapping constantDocumentFeatures) {
			throw new RuntimeException("shouldn't call");
		}

		@Override
		public String getCreatorNameFeature() {
			throw new RuntimeException("shouldn't call");
		}

		@Override
		public void setCreatorNameFeature(String nameFeature) {
			throw new RuntimeException("shouldn't call");
		}

		@Override
		public String getCreatorName() {
			throw new RuntimeException("shouldn't call");
		}

		@Override
		public Boolean getDeleteElements() {
			return false;
		}

		@Override
		public Boolean getSetArguments() {
			return false;
		}

		@Override
		public Boolean getSetFeatures() {
			return true;
		}

		@Override
		public Boolean getCreateDocuments() {
			return false;
		}

		@Override
		public Boolean getCreateSections() {
			return false;
		}

		@Override
		public Boolean getCreateAnnotations() {
			return false;
		}

		@Override
		public Boolean getCreateRelations() {
			return false;
		}

		@Override
		public Boolean getCreateTuples() {
			return false;
		}

		@Override
		public Boolean getAddToLayer() {
			return false;
		}

		@Override
		public Boolean getRemoveFromLayer() {
			return false;
		}

		@Override
		public void setDeleteElements(Boolean deleteElements) {
			throw new RuntimeException("shouldn't call");
		}

		@Override
		public void setSetArguments(Boolean setArguments) {
			throw new RuntimeException("shouldn't call");
		}

		@Override
		public void setSetFeatures(Boolean setFeatures) {
			throw new RuntimeException("shouldn't call");
		}

		@Override
		public void setCreateDocuments(Boolean createDocuments) {
			throw new RuntimeException("shouldn't call");
		}

		@Override
		public void setCreateSections(Boolean createSections) {
			throw new RuntimeException("shouldn't call");
		}

		@Override
		public void setCreateAnnotations(Boolean createAnnotations) {
			throw new RuntimeException("shouldn't call");
		}

		@Override
		public void setCreateRelations(Boolean createRelations) {
			throw new RuntimeException("shouldn't call");
		}

		@Override
		public void setCreateTuples(Boolean createTuples) {
			throw new RuntimeException("shouldn't call");
		}

		@Override
		public void setAddToLayer(Boolean addToLayer) {
			throw new RuntimeException("shouldn't call");
		}

		@Override
		public void setRemoveFromLayer(Boolean removeFromLayer) {
			throw new RuntimeException("shouldn't call");
		}
	}
}
