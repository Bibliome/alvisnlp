package fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.rebert;

import java.util.logging.Logger;

import fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.CorpusModule;
import fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.DefaultExpressions;
import fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.rebert.REBERTPredict.REBERTPredictResolvedObjects;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.DefaultNames;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.NameType;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.Expression;
import fr.inra.maiage.bibliome.alvisnlp.core.module.lib.Param;
import fr.inra.maiage.bibliome.util.Checkable;
import fr.inra.maiage.bibliome.util.files.ExecutableFile;
import fr.inra.maiage.bibliome.util.files.InputDirectory;
import fr.inra.maiage.bibliome.util.files.OutputDirectory;

public abstract class REBERTBase extends CorpusModule<REBERTPredictResolvedObjects> implements Checkable {
	private ExecutableFile conda;
	private String condaEnvironment;
	private ExecutableFile python;
	private InputDirectory rebertDir;
	protected Expression assertedCandidates;
	protected Expression assertedSubject;
	protected Expression assertedObject;
	protected Expression candidateGenerationScope;
	protected Expression generatedSubjects;
	protected Expression generatedObjects;
	protected Expression start = DefaultExpressions.ANNOTATION_START;
	protected Expression end = DefaultExpressions.ANNOTATION_END;
	private String sentenceLayer = DefaultNames.getSentenceLayer();
	private String modelType;
	private Integer ensembleNumber;
	private Boolean useGPU = false;
	protected OutputDirectory runScriptDirectory = null;
	protected InputDirectory predictionsDirectory = null;

	public REBERTBase() {
		super();
	}
	
	protected abstract String[] getLabels();

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
		return result;
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

	@Param
	public String getModelType() {
		return modelType;
	}

	@Param(mandatory = false)
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

	@Param(mandatory = false)
	public OutputDirectory getRunScriptDirectory() {
		return runScriptDirectory;
	}

	@Param(mandatory = false)
	public InputDirectory getPredictionsDirectory() {
		return predictionsDirectory;
	}

	public void setPredictionsDirectory(InputDirectory predictionsDirectory) {
		this.predictionsDirectory = predictionsDirectory;
	}

	public void setRunScriptDirectory(OutputDirectory runScriptDirectory) {
		this.runScriptDirectory = runScriptDirectory;
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

	public void setModelType(String modelType) {
		this.modelType = modelType;
	}

	public void setEnsembleNumber(Integer ensembleNumber) {
		this.ensembleNumber = ensembleNumber;
	}

}