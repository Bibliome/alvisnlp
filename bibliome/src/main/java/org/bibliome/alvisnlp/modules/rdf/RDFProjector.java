package org.bibliome.alvisnlp.modules.rdf;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Logger;

import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.shared.PrefixMapping;
import org.apache.log4j.PropertyConfigurator;
import org.bibliome.alvisnlp.MiscUtils;
import org.bibliome.alvisnlp.modules.SectionModule.SectionResolvedObjects;
import org.bibliome.alvisnlp.modules.trie.TrieProjector;
import org.bibliome.util.Iterators;
import org.bibliome.util.marshall.Decoder;
import org.bibliome.util.marshall.Encoder;
import org.bibliome.util.streams.SourceStream;
import org.bibliome.util.trie.Trie;

import alvisnlp.corpus.Annotation;
import alvisnlp.corpus.Corpus;
import alvisnlp.corpus.NameType;
import alvisnlp.corpus.expressions.ResolverException;
import alvisnlp.module.ModuleException;
import alvisnlp.module.ProcessingContext;
import alvisnlp.module.TimerCategory;
import alvisnlp.module.lib.AlvisNLPModule;
import alvisnlp.module.lib.Param;
import alvisnlp.module.lib.TimeThis;
import alvisnlp.module.types.Mapping;

@AlvisNLPModule(beta=true)
public abstract class RDFProjector extends TrieProjector<SectionResolvedObjects,Resource> {
	private SourceStream source;
	private String[] resourceTypeURIs = {
			"owl:Class",
			"skos:Concept"
	};
	private String[] labelURIs = {
			"rdfs:label",
			"skos:prefLabel",
			"skos:altLabel",
			"skos:hiddenLabel",
			"skos:notation",
			"oboInOwl:hasExactSynonym",
			"oboInOwl:hasRelatedSynonym",
			"oboInOwl:hasSynonym"
	};
	private String uriFeatureName;
	private Mapping labelFeatures = new Mapping(
			"rdfs-label", "rdfs:label",
			"skos-prefLabel", "skos:prefLabel"
			);

	@Override
	@TimeThis(task="create-trie", category=TimerCategory.LOAD_RESOURCE)
	protected Trie<Resource> getTrie(ProcessingContext<Corpus> ctx, Logger logger, Corpus corpus) throws IOException, ModuleException {
		return super.getTrie(ctx, logger, corpus);
	}

	@Override
	protected void fillTrie(Logger logger, Trie<Resource> trie, Corpus corpus) throws IOException, ModuleException {
		Model model = createModel(logger);
		Property typeProp = model.getProperty("http://www.w3.org/1999/02/22-rdf-syntax-ns#type");
		Property[] labelPropertyProps = getProperties(model, labelURIs);
		int nEntries = 0;
		for (String resourceTypeURI : resourceTypeURIs) {
			Property resourceType = model.getProperty(model.expandPrefix(resourceTypeURI));
			for (Resource res : Iterators.loop(model.listSubjectsWithProperty(typeProp , resourceType))) {
				if (res.isAnon()) {
					continue;
				}
				for (Property prop : labelPropertyProps) {
					for (RDFNode node : Iterators.loop(model.listObjectsOfProperty(res, prop))) {
						String label = getNodeValue(node);
						trie.addEntry(label, res);
						nEntries++;
					}
				}
			}
		}
		logger.info("Entries: " + nEntries);
	}
	
	private static Property[] getProperties(Model model, String[] uris) {
		Property[] result = new Property[uris.length];
		for (int i = 0; i < result.length; ++i) {
			result[i] = model.getProperty(model.expandPrefix(uris[i]));
		}
		return result;
	}
	
	private static String getNodeValue(RDFNode node) {
		if (node.isLiteral()) {
			Literal lit = node.asLiteral();
			return lit.getLexicalForm();
		}
		if (node.isResource()) {
			Resource res = node.asResource();
			return res.getURI();
		}
		throw new RuntimeException("RDF node " + node + " is neither a literal nor a resource!");
	}

	private Model createModel(Logger logger) throws IOException {
		MiscUtils.configureSilentLog4J();
		Model model = ModelFactory.createDefaultModel();
		model.setNsPrefixes(PrefixMapping.Standard);
		model.setNsPrefix("xsd", "http://www.w3.org/2001/XMLSchema#");
		model.setNsPrefix("skos", "http://www.w3.org/2004/02/skos/core#");
		model.setNsPrefix("oboInOwl", "http://www.geneontology.org/formats/oboInOwl#");
//		model.setNsPrefixes(module.prefixes);
		for (InputStream is : Iterators.loop(source.getInputStreams())) {
			logger.info("loading model from: " + source.getStreamName(is));
//			System.err.println("is = " + is);
//			model.read(is, null, Lang.RDFXML.toString());
			RDFDataMgr.read(model, is, Lang.RDFXML);
		}
		return model;
	}

	@Override
	protected void finish() {
	}

	@Override
	protected boolean marshallingSupported() {
		return false;
	}

	@Override
	protected Decoder<Resource> getDecoder() {
		return null;
	}

	@Override
	protected Encoder<Resource> getEncoder() {
		return null;
	}

	@Override
	protected void handleMatch(Resource value, Annotation a) {
		a.addFeature(uriFeatureName, value.getURI());
		Model model = value.getModel();
		for (Map.Entry<String,String> e : labelFeatures.entrySet()) {
			String propURI = e.getValue();
			Property prop = model.getProperty(model.expandPrefix(propURI));
			for (RDFNode node : Iterators.loop(model.listObjectsOfProperty(value, prop))) {
				a.addFeature(e.getKey(), getNodeValue(node));
			}
		}
	}

	@Override
	protected SectionResolvedObjects createResolvedObjects(ProcessingContext<Corpus> ctx) throws ResolverException {
		return new SectionResolvedObjects(ctx, this);
	}

	@Param
	public SourceStream getSource() {
		return source;
	}

	@Param
	public String[] getResourceTypeURIs() {
		return resourceTypeURIs;
	}

	@Param
	public String[] getLabelURIs() {
		return labelURIs;
	}

	@Param(nameType=NameType.FEATURE)
	public String getUriFeatureName() {
		return uriFeatureName;
	}

	@Param(nameType=NameType.FEATURE)
	public Mapping getLabelFeatures() {
		return labelFeatures;
	}

	public void setSource(SourceStream source) {
		this.source = source;
	}

	public void setResourceTypeURIs(String[] resourceTypeURIs) {
		this.resourceTypeURIs = resourceTypeURIs;
	}

	public void setLabelURIs(String[] labelURIs) {
		this.labelURIs = labelURIs;
	}

	public void setUriFeatureName(String uriFeatureName) {
		this.uriFeatureName = uriFeatureName;
	}

	public void setLabelFeatures(Mapping labelFeatures) {
		this.labelFeatures = labelFeatures;
	}
}
