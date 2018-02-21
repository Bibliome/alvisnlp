package fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.contes;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Annotation;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Corpus;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.DefaultNames;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Layer;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.NameType;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Section;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.EvaluationContext;
import fr.inra.maiage.bibliome.alvisnlp.core.module.lib.Param;
import fr.inra.maiage.bibliome.util.Iterators;
import fr.inra.maiage.bibliome.util.files.InputDirectory;
import fr.inra.maiage.bibliome.util.files.InputFile;

public abstract class AbstractContesTerms extends AbstractContes {
	private InputDirectory contesDir;
	private String tokenLayer = DefaultNames.getWordLayer();
	private String formFeature = Annotation.FORM_FEATURE_NAME;
	private String termLayer;
	private String conceptFeature;
	private InputFile ontology;
	private InputFile wordEmbeddings;

	@SuppressWarnings("unchecked")
	protected JSONObject getTermTokens(EvaluationContext ctx, Corpus corpus) {
		JSONObject result = new JSONObject();
		for (Section sec : Iterators.loop(sectionIterator(ctx, corpus))) {
			Layer tokens = sec.getLayer(getTokenLayer());
			for (Annotation term : sec.getLayer(getTermLayer())) {
				String id = term.getStringId();
				JSONArray termTokens = new JSONArray();
				for (Annotation t : tokens.between(term)) {
					String form = t.getLastFeature(getFormFeature());
					termTokens.add(form);
				}
				result.put(id, termTokens);
			}
		}
		return result;
	}

	@Override
	protected String[] addLayersToSectionFilter() {
		return new String[] { getTokenLayer(), getTermLayer() };
	}

	@Override
	protected String[] addFeaturesToSectionFilter() {
		return null;
	}

	@Param(nameType=NameType.LAYER)
	public String getTokenLayer() {
		return tokenLayer;
	}

	@Param(nameType=NameType.FEATURE)
	public String getFormFeature() {
		return formFeature;
	}

	@Param(nameType=NameType.LAYER)
	public String getTermLayer() {
		return termLayer;
	}

	@Param(nameType=NameType.FEATURE)
	public String getConceptFeature() {
		return conceptFeature;
	}

	@Param
	public InputFile getWordEmbeddings() {
		return wordEmbeddings;
	}

	@Param
	public InputDirectory getContesDir() {
		return contesDir;
	}

	@Param
	public InputFile getOntology() {
		return ontology;
	}

	public void setOntology(InputFile ontology) {
		this.ontology = ontology;
	}

	public void setContesDir(InputDirectory contesDir) {
		this.contesDir = contesDir;
	}

	public void setTokenLayer(String tokenLayer) {
		this.tokenLayer = tokenLayer;
	}

	public void setFormFeature(String formFeature) {
		this.formFeature = formFeature;
	}

	public void setTermLayer(String termLayer) {
		this.termLayer = termLayer;
	}

	public void setConceptFeature(String conceptFeature) {
		this.conceptFeature = conceptFeature;
	}

	public void setWordEmbeddings(InputFile wordEmbeddings) {
		this.wordEmbeddings = wordEmbeddings;
	}
}
