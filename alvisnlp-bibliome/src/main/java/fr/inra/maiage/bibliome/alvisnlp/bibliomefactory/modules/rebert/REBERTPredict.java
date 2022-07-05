package fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.rebert;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.CorpusModule;
import fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.DefaultExpressions;
import fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.ResolvedObjects;
import fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.rebert.REBERTPredict.REBERTPredictResolvedObjects;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Corpus;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.DefaultNames;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Element;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.NameType;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.creators.RelationCreator;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.creators.TupleCreator;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.EvaluationContext;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.Evaluator;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.Expression;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.ResolverException;
import fr.inra.maiage.bibliome.alvisnlp.core.module.ModuleException;
import fr.inra.maiage.bibliome.alvisnlp.core.module.ProcessingContext;
import fr.inra.maiage.bibliome.alvisnlp.core.module.ProcessingException;
import fr.inra.maiage.bibliome.alvisnlp.core.module.lib.AlvisNLPModule;
import fr.inra.maiage.bibliome.alvisnlp.core.module.lib.Param;
import fr.inra.maiage.bibliome.util.Iterators;
import fr.inra.maiage.bibliome.util.files.ExecutableFile;
import fr.inra.maiage.bibliome.util.files.InputDirectory;
import fr.inra.maiage.bibliome.util.files.InputFile;
import fr.inra.maiage.bibliome.util.streams.FileSourceStream;
import fr.inra.maiage.bibliome.util.streams.SourceStream;

@AlvisNLPModule(beta = true)
public abstract class REBERTPredict extends CorpusModule<REBERTPredictResolvedObjects> implements RelationCreator, TupleCreator {
	private ExecutableFile conda;
	private String condaEnvironment;
	private ExecutableFile python;
	private InputDirectory rebertDir;
	private Expression candidateScope;
	private Expression subjects;
	private Expression objects;
	private Expression start = DefaultExpressions.ANNOTATION_START;
	private Expression end = DefaultExpressions.ANNOTATION_END;
	private String relationName;
	private String subjectRole = "subject";
	private String objectRole = "object";
	private Boolean negativeTuples;
	private Integer negativeCategory = 0;
	private String sentenceLayer = DefaultNames.getSentenceLayer();
	private String labelFeature = "label";
	private String explainFeaturePrefix;
	private String modelType;
	private InputDirectory finetunedModel;
	private Integer ensembleNumber = 1;
	private Boolean useGPU = false;
	private EnsembleAggregator aggregator = EnsembleAggregator.VOTE;

	public class REBERTPredictResolvedObjects extends ResolvedObjects {
		private final Evaluator candidateScope;
		private final Evaluator subjects;
		private final Evaluator objects;
		private final Evaluator start;
		private final Evaluator end;
				
		public REBERTPredictResolvedObjects(ProcessingContext<Corpus> ctx) throws ResolverException {
			super(ctx, REBERTPredict.this);
			this.candidateScope = REBERTPredict.this.candidateScope.resolveExpressions(rootResolver);
			this.subjects = REBERTPredict.this.subjects.resolveExpressions(rootResolver);
			this.objects = REBERTPredict.this.objects.resolveExpressions(rootResolver);
			this.start = REBERTPredict.this.start.resolveExpressions(rootResolver);
			this.end = REBERTPredict.this.end.resolveExpressions(rootResolver);
		}

		public Evaluator getCandidateScope() {
			return candidateScope;
		}

		public Evaluator getSubjects() {
			return subjects;
		}

		public Evaluator getObjects() {
			return objects;
		}

		public Evaluator getStart() {
			return start;
		}

		public Evaluator getEnd() {
			return end;
		}

		public List<Candidate> createCandidates(EvaluationContext evalCtx, Corpus corpus) throws ModuleException {
			List<Candidate> result = new ArrayList<Candidate>();
			for (Element scope : Iterators.loop(getCandidateScope().evaluateElements(evalCtx, corpus))) {
				for (Element subject : Iterators.loop(getSubjects().evaluateElements(evalCtx, scope))) {
					for (Element object : Iterators.loop(getObjects().evaluateElements(evalCtx, scope))) {
						Candidate cand = new Candidate(REBERTPredict.this, evalCtx, scope, subject, object, "");
						result.add(cand);
					}
				}
			}
			return result;
		}
	}

	String[] readLabels() throws ModuleException {
		SourceStream source = new FileSourceStream("UTF-8", new InputFile(getFinetunedModel(), "id2label.json"));
		try (Reader r = source.getReader()) {
			JSONParser parser = new JSONParser();
			JSONObject jLabels = (JSONObject) parser.parse(r);
			String[] result = new String[jLabels.size()];
			for (int cn = 0; cn < result.length; ++cn) {
				result[cn] = (String) jLabels.get(Integer.toString(cn));
			}
			return result;
		}
		catch (ParseException | IOException e) {
			throw new ModuleException(e);
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
		return new REBERTPredictResolvedObjects(ctx);
	}

	@Param
	public InputDirectory getRebertDir() {
		return rebertDir;
	}

	@Param
	public Expression getCandidateScope() {
		return candidateScope;
	}

	@Param
	public Expression getSubjects() {
		return subjects;
	}

	@Param
	public Expression getObjects() {
		return objects;
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

	@Param(mandatory = false, nameType = NameType.RELATION)
	public String getRelationName() {
		return relationName;
	}

	@Param
	public Boolean getNegativeTuples() {
		return negativeTuples;
	}

	@Param
	public Integer getNegativeCategory() {
		return negativeCategory;
	}

	@Param(nameType = NameType.ARGUMENT)
	public String getSubjectRole() {
		return subjectRole;
	}

	@Param(nameType = NameType.ARGUMENT)
	public String getObjectRole() {
		return objectRole;
	}

	public void setSubjectRole(String subjectRole) {
		this.subjectRole = subjectRole;
	}

	public void setObjectRole(String objectRole) {
		this.objectRole = objectRole;
	}

	public void setRelationName(String relationName) {
		this.relationName = relationName;
	}

	public void setNegativeTuples(Boolean negativeTuples) {
		this.negativeTuples = negativeTuples;
	}

	public void setNegativeCategory(Integer negativeCategory) {
		this.negativeCategory = negativeCategory;
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

	public void setCandidateScope(Expression candidateScope) {
		this.candidateScope = candidateScope;
	}

	public void setSubjects(Expression subjects) {
		this.subjects = subjects;
	}

	public void setObjects(Expression objects) {
		this.objects = objects;
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
