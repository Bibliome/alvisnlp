package fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.python;

import java.io.IOException;

import fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.SectionModule;
import fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.SectionModule.SectionResolvedObjects;
import fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.python.PythonScript.PythonScriptResolvedObjects;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Corpus;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.creators.AnnotationCreator;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.creators.DocumentCreator;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.creators.RelationCreator;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.creators.SectionCreator;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.creators.TupleCreator;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.ResolverException;
import fr.inra.maiage.bibliome.alvisnlp.core.module.ModuleException;
import fr.inra.maiage.bibliome.alvisnlp.core.module.ProcessingContext;
import fr.inra.maiage.bibliome.alvisnlp.core.module.ProcessingException;
import fr.inra.maiage.bibliome.alvisnlp.core.module.lib.AlvisNLPModule;
import fr.inra.maiage.bibliome.alvisnlp.core.module.lib.Param;
import fr.inra.maiage.bibliome.alvisnlp.core.module.types.Mapping;
import fr.inra.maiage.bibliome.util.files.WorkingDirectory;

@AlvisNLPModule(beta=true)
public abstract class PythonScript extends SectionModule<PythonScriptResolvedObjects> implements DocumentCreator, SectionCreator, AnnotationCreator, RelationCreator, TupleCreator {
	private String[] commandLine;
	private WorkingDirectory workingDirectory;
	private Mapping environment;
	private Boolean update = false;
	
	class PythonScriptResolvedObjects extends SectionResolvedObjects {
		public PythonScriptResolvedObjects(ProcessingContext<Corpus> ctx, PythonScript module) throws ResolverException {
			super(ctx, module);
		}
	}

	@Override
	public void process(ProcessingContext<Corpus> ctx, Corpus corpus) throws ModuleException {
		try {
			PythonScriptExternalHandler external = new PythonScriptExternalHandler(ctx, this, corpus);
			external.start();
		}
		catch (IOException|InterruptedException e) {
			throw new ProcessingException(e);
		}
	}

	@Override
	protected String[] addLayersToSectionFilter() {
		return null;
	}

	@Override
	protected String[] addFeaturesToSectionFilter() {
		return null;
	}

	@Override
	protected PythonScriptResolvedObjects createResolvedObjects(ProcessingContext<Corpus> ctx) throws ResolverException {
		return new PythonScriptResolvedObjects(ctx, this);
	}

	@Param
	public String[] getCommandLine() {
		return commandLine;
	}

	@Param(mandatory = false)
	public WorkingDirectory getWorkingDirectory() {
		return workingDirectory;
	}

	@Param(mandatory = false)
	public Mapping getEnvironment() {
		return environment;
	}

	@Param
	public Boolean getUpdate() {
		return update;
	}

	public void setUpdate(Boolean update) {
		this.update = update;
	}

	public void setEnvironment(Mapping environment) {
		this.environment = environment;
	}

	public void setWorkingDirectory(WorkingDirectory workingDirectory) {
		this.workingDirectory = workingDirectory;
	}

	public void setCommandLine(String[] commandLine) {
		this.commandLine = commandLine;
	}
}
