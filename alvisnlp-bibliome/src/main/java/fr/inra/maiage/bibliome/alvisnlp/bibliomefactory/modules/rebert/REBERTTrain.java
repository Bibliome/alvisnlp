package fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.rebert;

import java.io.IOException;
import java.io.PrintStream;
import java.util.logging.Logger;

import org.apache.commons.io.FileUtils;

import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Corpus;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.Expression;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.ResolverException;
import fr.inra.maiage.bibliome.alvisnlp.core.module.ModuleException;
import fr.inra.maiage.bibliome.alvisnlp.core.module.ProcessingContext;
import fr.inra.maiage.bibliome.alvisnlp.core.module.ProcessingException;
import fr.inra.maiage.bibliome.alvisnlp.core.module.lib.AlvisNLPModule;
import fr.inra.maiage.bibliome.alvisnlp.core.module.lib.Param;
import fr.inra.maiage.bibliome.util.Strings;
import fr.inra.maiage.bibliome.util.files.OutputDirectory;
import fr.inra.maiage.bibliome.util.files.OutputFile;
import fr.inra.maiage.bibliome.util.streams.FileTargetStream;
import fr.inra.maiage.bibliome.util.streams.TargetStream;

@AlvisNLPModule(beta = true)
public abstract class REBERTTrain extends REBERTBase {
	private String[] labels;
	private Expression assertedLabel;
	private Expression generatedLabel;
	private Double trainDevSplit = 0.15;
	private Integer batchSize = 16;
	private OutputDirectory finetunedModel;
	
	@Override
	public void process(ProcessingContext ctx, Corpus corpus) throws ModuleException {
		try {
			REBERTBaseExternalHandler<REBERTTrain> ext = new REBERTTrainExternalHandler(ctx, this, corpus);
			Logger logger = getLogger(ctx);
			createFinetunedModelDirectory(logger);
			if (ext.hasCandidates()) {
				if (runScriptDirectory != null) {
					getLogger(ctx).info("running inhibited, writing data and run scripts in " + runScriptDirectory.getAbsolutePath());
					ext.writeRunScript();
				}
				else {
					ext.start();
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

	private void createFinetunedModelDirectory(Logger logger) throws IOException {
		OutputFile id2labelFile = new OutputFile(finetunedModel, "id2label.json");
		logger.info("creating " + id2labelFile.getAbsolutePath());
		FileUtils.createParentDirectories(id2labelFile);
		TargetStream ts = new FileTargetStream("UTF-8", id2labelFile);
		try (PrintStream ps = ts.getPrintStream()) {
			ps.print('{');
			for (int i = 0; i < labels.length; ++i) {
				if (i > 0) {
					ps.print(',');
				}
				ps.printf("\"%d\": \"%s\"", i, Strings.escapeJava(labels[i]));
			}
			ps.print('}');
		}
	}

	@Override
	protected REBERTBaseResolvedObjects createResolvedObjects(ProcessingContext ctx) throws ResolverException {
		return new REBERTBaseResolvedObjects(ctx);
	}

	@Param
	@Override
	public String[] getLabels() {
		return labels;
	}

	@Param
	@Override
	public Expression getAssertedLabel() {
		return assertedLabel;
	}

	@Param
	@Override
	public Expression getGeneratedLabel() {
		return generatedLabel;
	}
	
	@Param
	@Override
	public Integer getEnsembleNumber() {
		return super.getEnsembleNumber();
	}

	@Param
	public Double getTrainDevSplit() {
		return trainDevSplit;
	}

	public void setTrainDevSplit(Double trainDevSplit) {
		this.trainDevSplit = trainDevSplit;
	}

	@Param
	public Integer getBatchSize() {
		return batchSize;
	}

	public void setBatchSize(Integer batchSize) {
		this.batchSize = batchSize;
	}

	@Param
	public OutputDirectory getFinetunedModel() {
		return finetunedModel;
	}

	public void setFinetunedModel(OutputDirectory finetunedModel) {
		this.finetunedModel = finetunedModel;
	}

	public void setGeneratedLabel(Expression generatedLabel) {
		this.generatedLabel = generatedLabel;
	}

	public void setLabels(String[] labels) {
		this.labels = labels;
	}

	public void setAssertedLabel(Expression assertedLabel) {
		this.assertedLabel = assertedLabel;
	}
}
