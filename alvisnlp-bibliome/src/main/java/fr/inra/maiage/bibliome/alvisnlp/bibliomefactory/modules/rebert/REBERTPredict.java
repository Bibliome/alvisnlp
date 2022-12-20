package fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.rebert;

import java.io.IOException;
import java.io.Reader;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.logging.Logger;

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
import fr.inra.maiage.bibliome.util.Checkable;
import fr.inra.maiage.bibliome.util.Iterators;
import fr.inra.maiage.bibliome.util.files.ExecutableFile;
import fr.inra.maiage.bibliome.util.files.InputDirectory;
import fr.inra.maiage.bibliome.util.files.InputFile;
import fr.inra.maiage.bibliome.util.streams.FileSourceStream;
import fr.inra.maiage.bibliome.util.streams.SourceStream;

@AlvisNLPModule(beta = true)
public abstract class REBERTPredict extends CorpusModule<REBERTPredictResolvedObjects> implements RelationCreator, TupleCreator, Checkable {
	private ExecutableFile conda;
	private String condaEnvironment;
	private ExecutableFile python;
	private InputDirectory rebertDir;
	private Expression assertedCandidates;
	private Expression assertedSubject;
	private Expression assertedObject;
	private Expression candidateGenerationScope;
	private Expression generatedSubjects;
	private Expression generatedObjects;
	private Expression start = DefaultExpressions.ANNOTATION_START;
	private Expression end = DefaultExpressions.ANNOTATION_END;
	private Boolean createAssertedTuples = false;
	private Boolean createNegativeTuples = false;
	private Integer negativeCategory = 0;
	private String relationName;
	private String subjectRole = "subject";
	private String objectRole = "object";
	private String sentenceLayer = DefaultNames.getSentenceLayer();
	private String labelFeature = "predicted-label";
	private String explainFeaturePrefix;
	private String modelType;
	private InputDirectory finetunedModel;
	private Integer ensembleNumber = 1;
	private Boolean useGPU = false;
	private EnsembleAggregator aggregator = EnsembleAggregator.VOTE;

	public class REBERTPredictResolvedObjects extends ResolvedObjects {
		private final Evaluator assertedCandidates;
		private final Evaluator assertedSubject;
		private final Evaluator assertedObject;
		private final Evaluator candidateGenerationScope;
		private final Evaluator generatedSubjects;
		private final Evaluator generatedObjects;
		private final Evaluator start;
		private final Evaluator end;
				
		public REBERTPredictResolvedObjects(ProcessingContext<Corpus> ctx) throws ResolverException {
			super(ctx, REBERTPredict.this);
			this.assertedCandidates = rootResolver.resolveNullable(REBERTPredict.this.assertedCandidates);
			this.assertedSubject = rootResolver.resolveNullable(REBERTPredict.this.assertedSubject);
			this.assertedObject = rootResolver.resolveNullable(REBERTPredict.this.assertedObject);
			this.candidateGenerationScope = REBERTPredict.this.candidateGenerationScope.resolveExpressions(rootResolver);
			this.generatedSubjects = REBERTPredict.this.generatedSubjects.resolveExpressions(rootResolver);
			this.generatedObjects = REBERTPredict.this.generatedObjects.resolveExpressions(rootResolver);
			this.start = REBERTPredict.this.start.resolveExpressions(rootResolver);
			this.end = REBERTPredict.this.end.resolveExpressions(rootResolver);
		}

		public Evaluator getAssertedCandidates() {
			return assertedCandidates;
		}

		public Evaluator getAssertedSubject() {
			return assertedSubject;
		}

		public Evaluator getAssertedObject() {
			return assertedObject;
		}

		public Evaluator getCandidateGenerationScope() {
			return candidateGenerationScope;
		}

		public Evaluator getGeneratedSubjects() {
			return generatedSubjects;
		}

		public Evaluator getGeneratedObjects() {
			return generatedObjects;
		}

		public Evaluator getStart() {
			return start;
		}

		public Evaluator getEnd() {
			return end;
		}
		
		private Element getSingleArgument(EvaluationContext evalCtx, Element example, Evaluator argEval) {
			if (argEval == null) {
				return null;
			}
			Iterator<Element> argIt = argEval.evaluateElements(evalCtx, example);
			if (argIt.hasNext()) {
				return argIt.next();
			}
			return null;
		}

		public Collection<Candidate> createCandidates(EvaluationContext evalCtx, Corpus corpus) throws ModuleException {
			Collection<Candidate> result = new LinkedHashSet<Candidate>();
			if (getAssertedCandidates() != null) {
				for (Element xpl : Iterators.loop(getAssertedCandidates().evaluateElements(evalCtx, corpus))) {
					Element subject = getSingleArgument(evalCtx, xpl, getAssertedSubject());
					Element object = getSingleArgument(evalCtx, xpl, getAssertedObject());
					Candidate cand = new Candidate(true, REBERTPredict.this, evalCtx, xpl, subject, object, "");
					result.add(cand);
				}
			}
			for (Element scope : Iterators.loop(getCandidateGenerationScope().evaluateElements(evalCtx, corpus))) {
				for (Element subject : Iterators.loop(getGeneratedSubjects().evaluateElements(evalCtx, scope))) {
					for (Element object : Iterators.loop(getGeneratedObjects().evaluateElements(evalCtx, scope))) {
						Candidate cand = new Candidate(false, REBERTPredict.this, evalCtx, scope, subject, object, "");
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
	public boolean check(Logger logger) {
		boolean result = true;
		if (candidateGenerationScope == null) {
			if (assertedCandidates == null) {
				logger.severe("either candidateGenerationScope or assertedCandidates must be set");
				result = false;
			}
			if (generatedSubjects != null) {
				logger.warning("generatedSubjects will be ignored since candidateGenerationScope is not set");
			}
			if (generatedObjects != null) {
				logger.warning("generatedObjects will be ignored since candidateGenerationScope is not set");
			}
		}
		else {
			if (generatedSubjects == null) {
				logger.severe("generatedSubjects is mandatory when candidateGenerationScope is set");
				result = false;
			}
			if (generatedObjects == null) {
				logger.severe("generatedObjects is mandatory when candidateGenerationScope is set");
				result = false;
			}
		}
		if (assertedCandidates == null) {
			if (assertedSubject != null) {
				logger.warning("assertedSubject will be ignored since assertedCandidates is not set");
			}
			if (assertedObject != null) {
				logger.warning("assertedObject will be ignored since assertedCandidates is not set");
			}
		}
		else {
			if (assertedSubject == null) {
				logger.severe("assertedSubject is mandatory when assertedCandidates is set");
				result = true;
			}
			if (assertedObject == null) {
				logger.severe("assertedObject is mandatory when assertedCandidates is set");
				result = true;
			}
		}
		if (conda != null) {
			if (condaEnvironment == null) {
				logger.severe("condaEnvironment is mandatory when conda is set");
				result = false;
			}
		}
		if ((candidateGenerationScope == null) && (!createAssertedTuples) && (relationName == null)) {
			logger.warning("relationName will be ignored since candidateGenerationScope is not set and createAssertedTuples is false");
		}
		if ((candidateGenerationScope != null) && (relationName == null)) {
			logger.severe("relationName is mandatory since candidateGenerationScope is set");
			result = false;
		}
		if (createAssertedTuples && (relationName != null)) {
			logger.severe("relationName is mandatory since createAssertedTuples is true");
			result = false;
		}
		if (ensembleNumber < 1) {
			logger.severe("ensembleNumber must be greater or equalt to 1");
		}
		return result;
	}

	@Override
	public void process(ProcessingContext<Corpus> ctx, Corpus corpus) throws ModuleException {
		try {
			REBERTPredictExternalHandler ext = new REBERTPredictExternalHandler(ctx, this, corpus);
			if (ext.hasCandidates()) {
				ext.start();
			}
			else {
				getLogger(ctx).warning("no candidate");
			}
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

	@Param(mandatory = false)
	public Expression getCandidateGenerationScope() {
		return candidateGenerationScope;
	}

	@Param(mandatory = false)
	public Expression getGeneratedSubjects() {
		return generatedSubjects;
	}

	@Param(mandatory = false)
	public Expression getGeneratedObjects() {
		return generatedObjects;
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
	public Boolean getCreateNegativeTuples() {
		return createNegativeTuples;
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

	@Param(mandatory = false)
	public Expression getAssertedCandidates() {
		return assertedCandidates;
	}

	@Param(mandatory = false)
	public Expression getAssertedSubject() {
		return assertedSubject;
	}

	@Param(mandatory = false)
	public Expression getAssertedObject() {
		return assertedObject;
	}

	@Param
	public Boolean getCreateAssertedTuples() {
		return createAssertedTuples;
	}

	public void setAssertedCandidates(Expression assertedCandidates) {
		this.assertedCandidates = assertedCandidates;
	}

	public void setAssertedSubject(Expression assertedSubject) {
		this.assertedSubject = assertedSubject;
	}

	public void setAssertedObject(Expression assertedObject) {
		this.assertedObject = assertedObject;
	}

	public void setCreateAssertedTuples(Boolean createAssertedTuples) {
		this.createAssertedTuples = createAssertedTuples;
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

	public void setCreateNegativeTuples(Boolean createNegativeTuples) {
		this.createNegativeTuples = createNegativeTuples;
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

	public void setCandidateGenerationScope(Expression candidateGenerationScope) {
		this.candidateGenerationScope = candidateGenerationScope;
	}

	public void setGeneratedSubjects(Expression generatedSubjects) {
		this.generatedSubjects = generatedSubjects;
	}

	public void setGeneratedObjects(Expression generatedObjects) {
		this.generatedObjects = generatedObjects;
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
