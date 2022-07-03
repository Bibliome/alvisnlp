package fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.rebert;

import java.io.IOException;

import fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.CorpusModule;
import fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.DefaultExpressions;
import fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.ResolvedObjects;
import fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.rebert.REBERTPredict.REBERTPredictResolvedObjects;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Corpus;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.DefaultNames;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.NameType;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.Evaluator;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.Expression;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.ResolverException;
import fr.inra.maiage.bibliome.alvisnlp.core.module.ModuleException;
import fr.inra.maiage.bibliome.alvisnlp.core.module.ProcessingContext;
import fr.inra.maiage.bibliome.alvisnlp.core.module.ProcessingException;
import fr.inra.maiage.bibliome.alvisnlp.core.module.lib.AlvisNLPModule;
import fr.inra.maiage.bibliome.alvisnlp.core.module.lib.Param;
import fr.inra.maiage.bibliome.util.files.ExecutableFile;
import fr.inra.maiage.bibliome.util.files.InputDirectory;

@AlvisNLPModule(beta = true)
public class REBERTPredict extends CorpusModule<REBERTPredictResolvedObjects> {
	private ExecutableFile conda;
	private String condaEnvironment;
	private ExecutableFile python;
	private InputDirectory rebertDir;
	private Expression candidates;
	private Expression subject;
	private Expression object;
	private Expression start = DefaultExpressions.ANNOTATION_START;
	private Expression end = DefaultExpressions.ANNOTATION_END;
	private String sentenceLayer = DefaultNames.getSentenceLayer();
	private String labelFeature;
	private String explainFeaturePrefix;
	private String modelType;
	private InputDirectory finetunedModel;
	private Integer ensembleNumber;
	private Boolean useGPU = false;
	private EnsembleAggregator aggregator = EnsembleAggregator.VOTE;

	public static class REBERTPredictResolvedObjects extends ResolvedObjects {
		private final Evaluator candidates;
		private final Evaluator subject;
		private final Evaluator object;
		private final Evaluator start;
		private final Evaluator end;
				
		public REBERTPredictResolvedObjects(ProcessingContext<Corpus> ctx, REBERTPredict module) throws ResolverException {
			super(ctx, module);
			this.candidates = module.candidates.resolveExpressions(rootResolver);
			this.subject = module.subject.resolveExpressions(rootResolver);
			this.object = module.object.resolveExpressions(rootResolver);
			this.start = module.start.resolveExpressions(rootResolver);
			this.end = module.end.resolveExpressions(rootResolver);
		}

		public Evaluator getCandidates() {
			return candidates;
		}

		public Evaluator getSubject() {
			return subject;
		}

		public Evaluator getObject() {
			return object;
		}

		public Evaluator getStart() {
			return start;
		}

		public Evaluator getEnd() {
			return end;
		}
	}

	@Override
	public void process(ProcessingContext<Corpus> ctx, Corpus corpus) throws ModuleException {
		try {
			REBERTPredictExternalHandler ext = new REBERTPredictExternalHandler(ctx, this, corpus);
			ext.start();
		}
		catch (IOException | InterruptedException e) {
			throw new ProcessingException(e);
		}
	}

	@Override
	protected REBERTPredictResolvedObjects createResolvedObjects(ProcessingContext<Corpus> ctx) throws ResolverException {
		return new REBERTPredictResolvedObjects(ctx, this);
	}

	@Param
	public InputDirectory getRebertDir() {
		return rebertDir;
	}

	@Param
	public Expression getCandidates() {
		return candidates;
	}

	@Param
	public Expression getSubject() {
		return subject;
	}

	@Param
	public Expression getObject() {
		return object;
	}

	@Param
	public Expression getStart() {
		return start;
	}

	@Param
	public Expression getEnd() {
		return end;
	}

	@Param(nameType = NameType.LAYER)
	public String getSentenceLayer() {
		return sentenceLayer;
	}

	@Param(nameType = NameType.FEATURE)
	public String getLabelFeature() {
		return labelFeature;
	}

	@Param
	public String getModelType() {
		return modelType;
	}

	@Param
	public InputDirectory getFinetunedModel() {
		return finetunedModel;
	}

	@Param
	public Integer getEnsembleNumber() {
		return ensembleNumber;
	}

	@Param(mandatory = false)
	public ExecutableFile getConda() {
		return conda;
	}

	@Param(mandatory = false)
	public String getCondaEnvironment() {
		return condaEnvironment;
	}

	@Param(mandatory = false)
	public ExecutableFile getPython() {
		return python;
	}

	@Param
	public Boolean getUseGPU() {
		return useGPU;
	}

	@Param
	public EnsembleAggregator getAggregator() {
		return aggregator;
	}

	@Param(mandatory = false)
	public String getExplainFeaturePrefix() {
		return explainFeaturePrefix;
	}

	public void setExplainFeaturePrefix(String explainFeaturePrefix) {
		this.explainFeaturePrefix = explainFeaturePrefix;
	}

	public void setAggregator(EnsembleAggregator aggregator) {
		this.aggregator = aggregator;
	}

	public void setUseGPU(Boolean useGPU) {
		this.useGPU = useGPU;
	}

	public void setConda(ExecutableFile conda) {
		this.conda = conda;
	}

	public void setCondaEnvironment(String condaEnvironment) {
		this.condaEnvironment = condaEnvironment;
	}

	public void setPython(ExecutableFile python) {
		this.python = python;
	}

	public void setRebertDir(InputDirectory rebertDir) {
		this.rebertDir = rebertDir;
	}

	public void setCandidates(Expression candidates) {
		this.candidates = candidates;
	}

	public void setSubject(Expression subject) {
		this.subject = subject;
	}

	public void setObject(Expression object) {
		this.object = object;
	}

	public void setStart(Expression start) {
		this.start = start;
	}

	public void setEnd(Expression end) {
		this.end = end;
	}

	public void setSentenceLayer(String sentenceLayer) {
		this.sentenceLayer = sentenceLayer;
	}

	public void setLabelFeature(String labelFeature) {
		this.labelFeature = labelFeature;
	}

	public void setModelType(String modelType) {
		this.modelType = modelType;
	}

	public void setFinetunedModel(InputDirectory finetunedModel) {
		this.finetunedModel = finetunedModel;
	}

	public void setEnsembleNumber(Integer ensembleNumber) {
		this.ensembleNumber = ensembleNumber;
	}
}
