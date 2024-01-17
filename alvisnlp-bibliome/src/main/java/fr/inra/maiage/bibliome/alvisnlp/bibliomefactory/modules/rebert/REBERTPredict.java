package fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.rebert;

import java.io.IOException;
import java.io.Reader;
import java.util.logging.Logger;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Corpus;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.NameType;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.creators.RelationCreator;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.creators.TupleCreator;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.ConstantsLibrary;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.Expression;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.ResolverException;
import fr.inra.maiage.bibliome.alvisnlp.core.module.ModuleException;
import fr.inra.maiage.bibliome.alvisnlp.core.module.ProcessingContext;
import fr.inra.maiage.bibliome.alvisnlp.core.module.ProcessingException;
import fr.inra.maiage.bibliome.alvisnlp.core.module.lib.AlvisNLPModule;
import fr.inra.maiage.bibliome.alvisnlp.core.module.lib.Param;
import fr.inra.maiage.bibliome.util.files.InputDirectory;
import fr.inra.maiage.bibliome.util.files.InputFile;
import fr.inra.maiage.bibliome.util.streams.FileSourceStream;
import fr.inra.maiage.bibliome.util.streams.SourceStream;

@AlvisNLPModule(beta = true)
public abstract class REBERTPredict extends REBERTBase implements RelationCreator, TupleCreator {
	private Boolean createAssertedTuples = false;
	private Boolean createNegativeTuples = false;
	private Integer negativeCategory = 0;
	private String relation;
	private String subjectRole = "subject";
	private String objectRole = "object";
	private String labelFeature = "predicted-label";
	private String explainFeaturePrefix;
	private InputDirectory finetunedModel;
	private Integer[] ensembleModels;
	private EnsembleAggregator aggregator = EnsembleAggregator.VOTE;

	@Override
	protected String[] getLabels() {
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
			throw new RuntimeException(e);
		}
	}

	@Override
	public boolean check(Logger logger) {
		boolean result = super.check(logger);
		if ((candidateGenerationScope == null) && (!createAssertedTuples) && (relation == null)) {
			logger.warning("relation will be ignored since candidateGenerationScope is not set and createAssertedTuples is false");
		}
		if ((candidateGenerationScope != null) && (relation == null)) {
			logger.severe("relation is mandatory since candidateGenerationScope is set");
			result = false;
		}
		if (createAssertedTuples && (relation != null)) {
			logger.severe("relation is mandatory since createAssertedTuples is true");
			result = false;
		}
		if (getEnsembleNumber() == null) {
			if (ensembleModels == null) {
				logger.severe("either ensembleNumber or ensembleModels is mandatory");
				result = false;
			}
		}
		else {
			if (getEnsembleNumber() < 1) {
				logger.severe("ensembleNumber must be greater or equalt to 1");
				result = false;
			}
			if (ensembleModels != null) {
				logger.warning("ensembleNumber will be ignored since ensembleModels is set");
			}
		}
		return result;
	}
	
	@Override
	protected REBERTBaseResolvedObjects createResolvedObjects(ProcessingContext ctx) throws ResolverException {
		return new REBERTBaseResolvedObjects(ctx);
	}

	@Override
	public void process(ProcessingContext ctx, Corpus corpus) throws ModuleException {
		try {
			REBERTPredictExternalHandler ext = new REBERTPredictExternalHandler(ctx, this, corpus);
			if (ext.hasCandidates()) {
				if (runScriptDirectory != null) {
					getLogger(ctx).info("running inhibited, writing data and run scripts in " + runScriptDirectory.getAbsolutePath());
					ext.writeRunScript();
				}
				else {
					if (predictionsDirectory != null) {
						getLogger(ctx).info("running inhibited, reading predictions from " + predictionsDirectory.getAbsolutePath());
						ext.readPredictions();
					}
					else {
						ext.start();
					}
				}
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
	public Expression getAssertedLabel() {
		return ConstantsLibrary.create("");
	}
	
	@Override
	public Expression getGeneratedLabel() {
		return ConstantsLibrary.create("");
	}

	@Param(nameType = NameType.FEATURE)
	public String getLabelFeature() {
		return labelFeature;
	}

	@Param
	public InputDirectory getFinetunedModel() {
		return finetunedModel;
	}

	@Param
	public EnsembleAggregator getAggregator() {
		return aggregator;
	}

	@Param(mandatory = false)
	public String getExplainFeaturePrefix() {
		return explainFeaturePrefix;
	}

	@Deprecated
	@Param(mandatory = false, nameType = NameType.RELATION)
	public String getRelationName() {
		return relation;
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

	@Param(mandatory = false, nameType = NameType.RELATION)
	public String getRelation() {
		return relation;
	}

	@Param(mandatory = false)
	public Integer[] getEnsembleModels() {
		return ensembleModels;
	}

	@Param
	public Boolean getCreateNegativeTuples() {
		return createNegativeTuples;
	}

	@Param
	public Boolean getCreateAssertedTuples() {
		return createAssertedTuples;
	}

	public void setRelation(String relation) {
		this.relation = relation;
	}

	public void setEnsembleModels(Integer[] ensembleModels) {
		this.ensembleModels = ensembleModels;
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
		this.relation = relationName;
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

	public void setLabelFeature(String labelFeature) {
		this.labelFeature = labelFeature;
	}

	public void setFinetunedModel(InputDirectory finetunedModel) {
		this.finetunedModel = finetunedModel;
	}
}
