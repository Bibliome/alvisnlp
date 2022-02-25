package fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.python;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;

import fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.SectionModule;
import fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.SectionModule.SectionResolvedObjects;
import fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.python.PythonScript.PythonScriptResolvedObjects;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Annotation;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Corpus;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Layer;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.NameType;
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
import fr.inra.maiage.bibliome.alvisnlp.core.module.lib.AlvisNLPModule;
import fr.inra.maiage.bibliome.alvisnlp.core.module.lib.Param;
import fr.inra.maiage.bibliome.alvisnlp.core.module.types.Mapping;
import fr.inra.maiage.bibliome.util.files.WorkingDirectory;

@AlvisNLPModule(beta=true)
public abstract class PythonScript extends SectionModule<PythonScriptResolvedObjects> implements DocumentCreator, SectionCreator, AnnotationCreator, RelationCreator, TupleCreator {
	private String[] commandLine;
	private WorkingDirectory workingDirectory;
	private Mapping environment;
	private String[] relationNames;
	private String[] layerNames;
	
	static class PythonScriptResolvedObjects extends SectionResolvedObjects {
		private final Collection<String> layerNames;
		private final Collection<String> relationNames;
		
		PythonScriptResolvedObjects(ProcessingContext<Corpus> ctx, PythonScript module) throws ResolverException {
			super(ctx, module);
			this.layerNames = module.layerNames == null ? null : new HashSet<String>(Arrays.asList(module.layerNames));
			this.relationNames = module.relationNames == null ? null : new HashSet<String>(Arrays.asList(module.relationNames));
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

	@Param(mandatory = false, nameType = NameType.LAYER)
	public String[] getLayerNames() {
		return layerNames;
	}

	@Param(mandatory = false, nameType = NameType.RELATION)
	public String[] getRelationNames() {
		return relationNames;
	}

	public void setRelationNames(String[] relationNames) {
		this.relationNames = relationNames;
	}

	public void setLayerNames(String[] layerNames) {
		this.layerNames = layerNames;
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