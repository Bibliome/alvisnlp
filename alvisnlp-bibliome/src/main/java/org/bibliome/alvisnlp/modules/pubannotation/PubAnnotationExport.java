package org.bibliome.alvisnlp.modules.pubannotation;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.logging.Logger;

import org.bibliome.alvisnlp.modules.SectionModule;
import org.bibliome.alvisnlp.modules.SectionModule.SectionResolvedObjects;
import org.bibliome.alvisnlp.modules.pubannotation.PubAnnotationExport.PubAnnotationExportResolvedObjects;
import org.bibliome.util.Iterators;
import org.bibliome.util.files.OutputFile;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import alvisnlp.corpus.Corpus;
import alvisnlp.corpus.Section;
import alvisnlp.corpus.expressions.EvaluationContext;
import alvisnlp.corpus.expressions.LibraryResolver;
import alvisnlp.corpus.expressions.ResolverException;
import alvisnlp.module.ModuleException;
import alvisnlp.module.NameUsage;
import alvisnlp.module.ProcessingContext;
import alvisnlp.module.lib.AlvisNLPModule;
import alvisnlp.module.lib.Param;

@AlvisNLPModule(beta=true)
public class PubAnnotationExport extends SectionModule<PubAnnotationExportResolvedObjects> {
	private OutputFile outFile;
	private DenotationSpecification[] denotations = { new DenotationSpecification() };
	private RelationSpecification[] relations = { new RelationSpecification() };

	@SuppressWarnings("unchecked")
	@Override
	public void process(ProcessingContext<Corpus> ctx, Corpus corpus) throws ModuleException {
		Logger logger = getLogger(ctx);
		EvaluationContext evalCtx = new EvaluationContext(logger);
		PubAnnotationExportResolvedObjects resObj = getResolvedObjects();
		JSONArray result = new JSONArray();
		for (Section sec : Iterators.loop(sectionIterator(evalCtx, corpus))) {
			JSONObject j = resObj.convert(evalCtx, sec);
			result.add(j);
		}
		if (result.isEmpty()) {
			return;
		}
		try (Writer w = new OutputStreamWriter(new FileOutputStream(outFile), "UTF-8")) {
			if (result.size() == 1) {
				((JSONObject) result.get(0)).writeJSONString(w);
			}
			else {
				result.writeJSONString(w);
			}
			w.flush();
		}
		catch (IOException e) {
			rethrow(e);
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
	protected PubAnnotationExportResolvedObjects createResolvedObjects(ProcessingContext<Corpus> ctx) throws ResolverException {
		return new PubAnnotationExportResolvedObjects(ctx, this);
	}
	
	public static class PubAnnotationExportResolvedObjects extends SectionResolvedObjects {
		private DenotationSpecification.Resolved[] denotations;
		private RelationSpecification.Resolved[] relations;
		
		private PubAnnotationExportResolvedObjects(ProcessingContext<Corpus> ctx, PubAnnotationExport module) throws ResolverException {
			super(ctx, module);
			LibraryResolver resolver = module.getLibraryResolver(ctx);
			this.denotations = resolver.resolveArray(module.denotations, DenotationSpecification.Resolved.class);
			this.relations = resolver.resolveArray(module.relations, RelationSpecification.Resolved.class);
		}

		@Override
		public void collectUsedNames(NameUsage nameUsage, String defaultType) throws ModuleException {
			super.collectUsedNames(nameUsage, defaultType);
			nameUsage.collectUsedNamesArray(denotations, defaultType);
			nameUsage.collectUsedNamesArray(relations, defaultType);
		}
		
		@SuppressWarnings("unchecked")
		JSONObject convert(EvaluationContext ctx, Section sec) {
			JSONObject result = new JSONObject();
			result.put("text", sec.getContents());
			result.put("denotations", getDenotations(ctx, sec));
			result.put("relations", getRelations(ctx, sec));
			return result;
		}
		
		private JSONArray getDenotations(EvaluationContext ctx, Section sec) {
			JSONArray result = new JSONArray();
			for (DenotationSpecification.Resolved ds : denotations) {
				ds.addDenotations(ctx, sec, result);
			}
			return result;
		}
		
		private JSONArray getRelations(EvaluationContext ctx, Section sec) {
			JSONArray result = new JSONArray();
			for (RelationSpecification.Resolved rs : relations) {
				rs.addRelations(ctx, sec, result);
			}
			return result;
		}
	}

	@Param
	public OutputFile getOutFile() {
		return outFile;
	}

	@Param
	public DenotationSpecification[] getDenotations() {
		return denotations;
	}

	@Param
	public RelationSpecification[] getRelations() {
		return relations;
	}

	public void setOutFile(OutputFile outFile) {
		this.outFile = outFile;
	}

	public void setDenotations(DenotationSpecification[] denotations) {
		this.denotations = denotations;
	}

	public void setRelations(RelationSpecification[] relations) {
		this.relations = relations;
	}
	
	
}
