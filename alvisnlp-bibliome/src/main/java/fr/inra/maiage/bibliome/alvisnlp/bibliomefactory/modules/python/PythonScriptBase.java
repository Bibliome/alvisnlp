package fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.python;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;

import fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.SectionModule;
import fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.SectionModule.SectionResolvedObjects;
import fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.python.PythonScriptBase.PythonScriptResolvedObjects;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Annotation;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Corpus;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Layer;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Relation;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Section;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.creators.AnnotationCreator;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.creators.DocumentCreator;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.creators.RelationCreator;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.creators.SectionCreator;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.creators.TupleCreator;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.ResolverException;
import fr.inra.maiage.bibliome.alvisnlp.core.module.ModuleException;
import fr.inra.maiage.bibliome.alvisnlp.core.module.ProcessingContext;
import fr.inra.maiage.bibliome.alvisnlp.core.module.ProcessingException;
import fr.inra.maiage.bibliome.alvisnlp.core.module.lib.Param;
import fr.inra.maiage.bibliome.alvisnlp.core.module.types.EvaluatorMapping;
import fr.inra.maiage.bibliome.alvisnlp.core.module.types.ExpressionMapping;
import fr.inra.maiage.bibliome.alvisnlp.core.module.types.Mapping;
import fr.inra.maiage.bibliome.util.files.ExecutableFile;
import fr.inra.maiage.bibliome.util.files.InputDirectory;
import fr.inra.maiage.bibliome.util.files.WorkingDirectory;
import fr.inra.maiage.bibliome.util.streams.SourceStream;

public abstract class PythonScriptBase extends SectionModule<PythonScriptResolvedObjects> implements DocumentCreator, SectionCreator, AnnotationCreator, RelationCreator, TupleCreator {
	private ExecutableFile conda;
	private String condaEnvironment;
	private ExecutableFile python;
	private WorkingDirectory workingDirectory;
	private Mapping environment;
	private InputDirectory alvisnlpPythonDirectory;

	static class PythonScriptResolvedObjects extends SectionResolvedObjects {
		private final Collection<String> layerNames;
		private final Collection<String> relationNames;
		private final EvaluatorMapping scriptParams;
		
		PythonScriptResolvedObjects(ProcessingContext<Corpus> ctx, PythonScriptBase module) throws ResolverException {
			super(ctx, module);
			this.layerNames = module.getLayerNames() == null ? null : new HashSet<String>(Arrays.asList(module.getLayerNames()));
			this.relationNames = module.getRelationNames() == null ? null : new HashSet<String>(Arrays.asList(module.getRelationNames()));
			this.scriptParams = module.getScriptParams().resolveExpressions(rootResolver);
		}
		
		boolean acceptRelation(Relation rel) {
			if (relationNames == null) {
				return true;
			}
			return relationNames.contains(rel.getName());
		}
		
		boolean acceptLayer(Layer layer) {
			if (layerNames == null) {
				return true;
			}
			return layerNames.contains(layer.getName());
		}
		
		EvaluatorMapping getScriptParams() {
			return scriptParams;
		}

		Layer getAnnotations(Section sec) {
			if (layerNames == null) {
				return sec.getAllAnnotations();
			}
	        Layer result = new Layer(sec);
	        for (Layer layer : sec.getAllLayers()) {
	        	if ((layerNames == null) || layerNames.contains(layer.getName())) {
	        		for (Annotation ann : layer) {
	        			result.add(ann);
	        		}
	        	}
	        }
	        return result;
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
	
	protected abstract boolean isScriptCopy();

	public abstract String[] getCommandLine();

	public abstract String[] getLayerNames();

	public abstract String[] getRelationNames();

	public abstract ExpressionMapping getScriptParams();

	public abstract Boolean getCallPython();

	public abstract SourceStream getScript();

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

	@Param(mandatory = false)
	public WorkingDirectory getWorkingDirectory() {
		return workingDirectory;
	}

	@Param(mandatory = false)
	public Mapping getEnvironment() {
		return environment;
	}

	@Param(mandatory = false)
	public String getCondaEnvironment() {
		return condaEnvironment;
	}

	@Param(mandatory = false)
	public ExecutableFile getConda() {
		return conda;
	}

	@Param
	public InputDirectory getAlvisnlpPythonDirectory() {
		return alvisnlpPythonDirectory;
	}

	@Param(mandatory = false)
	public ExecutableFile getPython() {
		return python;
	}

	public void setPython(ExecutableFile python) {
		this.python = python;
	}

	public void setAlvisnlpPythonDirectory(InputDirectory alvisnlpPythonDirectory) {
		this.alvisnlpPythonDirectory = alvisnlpPythonDirectory;
	}

	public void setConda(ExecutableFile conda) {
		this.conda = conda;
	}

	public void setCondaEnvironment(String condaEnvironment) {
		this.condaEnvironment = condaEnvironment;
	}

	public void setEnvironment(Mapping environment) {
		this.environment = environment;
	}

	public void setWorkingDirectory(WorkingDirectory workingDirectory) {
		this.workingDirectory = workingDirectory;
	}
}
