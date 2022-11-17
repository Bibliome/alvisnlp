package fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.pubannotation;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.logging.Logger;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.SectionModule;
import fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.SectionModule.SectionResolvedObjects;
import fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.pubannotation.PubAnnotationExport.PubAnnotationExportResolvedObjects;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Corpus;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Section;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.EvaluationContext;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.Evaluator;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.Expression;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.LibraryResolver;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.ResolverException;
import fr.inra.maiage.bibliome.alvisnlp.core.module.ModuleException;
import fr.inra.maiage.bibliome.alvisnlp.core.module.NameUsage;
import fr.inra.maiage.bibliome.alvisnlp.core.module.ProcessingContext;
import fr.inra.maiage.bibliome.alvisnlp.core.module.ProcessingException;
import fr.inra.maiage.bibliome.alvisnlp.core.module.lib.AlvisNLPModule;
import fr.inra.maiage.bibliome.alvisnlp.core.module.lib.Param;
import fr.inra.maiage.bibliome.util.Iterators;
import fr.inra.maiage.bibliome.util.files.OutputFile;

@AlvisNLPModule(beta=true)
public class PubAnnotationExport extends SectionModule<PubAnnotationExportResolvedObjects> {
	private OutputFile outFile;
	private DenotationSpecification[] denotations = { new DenotationSpecification() };
	private RelationSpecification[] relations = { new RelationSpecification() };
	private Expression sourcedb;
	private Expression sourceid;
	private Expression divid;

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
	protected PubAnnotationExportResolvedObjects createResolvedObjects(ProcessingContext<Corpus> ctx) throws ResolverException {
		return new PubAnnotationExportResolvedObjects(ctx, this);
	}

	public static class PubAnnotationExportResolvedObjects extends SectionResolvedObjects {
		private final DenotationSpecification.Resolved[] denotations;
		private final RelationSpecification.Resolved[] relations;
		private final Evaluator sourcedb;
		private final Evaluator sourceid;
		private final Evaluator divid;

		private PubAnnotationExportResolvedObjects(ProcessingContext<Corpus> ctx, PubAnnotationExport module) throws ResolverException {
			super(ctx, module);
			LibraryResolver resolver = module.getLibraryResolver(ctx);
			this.denotations = resolver.resolveArray(module.denotations, DenotationSpecification.Resolved.class);
			this.relations = resolver.resolveArray(module.relations, RelationSpecification.Resolved.class);
			this.sourcedb = resolver.resolveNullable(module.sourcedb);
			this.sourceid = resolver.resolveNullable(module.sourceid);
			this.divid = resolver.resolveNullable(module.divid);
		}

		@Override
		public void collectUsedNames(NameUsage nameUsage, String defaultType) throws ModuleException {
			super.collectUsedNames(nameUsage, defaultType);
			nameUsage.collectUsedNamesArray(denotations, defaultType);
			nameUsage.collectUsedNamesArray(relations, defaultType);
			nameUsage.collectUsedNamesNullable(sourcedb, defaultType);
			nameUsage.collectUsedNamesNullable(sourceid, defaultType);
			nameUsage.collectUsedNamesNullable(divid, defaultType);
		}

		@SuppressWarnings("unchecked")
		JSONObject convert(EvaluationContext ctx, Section sec) {
			JSONObject result = new JSONObject();
			result.put("text", sec.getContents());
			result.put("denotations", getDenotations(ctx, sec));
			result.put("relations", getRelations(ctx, sec));
			if (sourcedb != null) {
				result.put("sourcedb", sourcedb.evaluateString(ctx, sec));
			}
			if (sourceid != null) {
				result.put("sourceid", sourceid.evaluateString(ctx, sec));
			}
			if (divid != null) {
				result.put("divid", divid.evaluateInt(ctx, sec));
			}
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

	@Param(mandatory=false)
	public Expression getSourcedb() {
		return sourcedb;
	}

	@Param(mandatory=false)
	public Expression getSourceid() {
		return sourceid;
	}

	@Param(mandatory=false)
	public Expression getDivid() {
		return divid;
	}

	public void setSourcedb(Expression sourcedb) {
		this.sourcedb = sourcedb;
	}

	public void setSourceid(Expression sourceid) {
		this.sourceid = sourceid;
	}

	public void setDivid(Expression divid) {
		this.divid = divid;
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
