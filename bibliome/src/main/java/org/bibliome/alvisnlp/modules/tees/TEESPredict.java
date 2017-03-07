package org.bibliome.alvisnlp.modules.tees;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.logging.Logger;

import org.bibliome.alvisnlp.modules.SectionModule;
import org.bibliome.alvisnlp.modules.SectionModule.SectionResolvedObjects;
import org.bibliome.alvisnlp.modules.tees.CorpusTEES.Document.Sentence.Interaction;
import org.bibliome.util.Iterators;

import alvisnlp.corpus.Annotation;
import alvisnlp.corpus.Corpus;
import alvisnlp.corpus.DefaultNames;
import alvisnlp.corpus.Document;
import alvisnlp.corpus.DownCastElement;
import alvisnlp.corpus.Element;
import alvisnlp.corpus.Layer;
import alvisnlp.corpus.NameType;
import alvisnlp.corpus.Relation;
import alvisnlp.corpus.Section;
import alvisnlp.corpus.Tuple;
import alvisnlp.corpus.expressions.EvaluationContext;
import alvisnlp.corpus.expressions.ResolverException;
import alvisnlp.module.ModuleException;
import alvisnlp.module.ProcessingContext;
import alvisnlp.module.lib.Param;

public class TEESPredict extends SectionModule<SectionResolvedObjects> {
	private String tokenLayerName = DefaultNames.getWordLayer();
	private String sentenceLayerName = DefaultNames.getSentenceLayer();
	private String dependencyLabelFeatureName = DefaultNames.getDependencyLabelFeatureName();
	private String sentenceRole = DefaultNames.getDependencySentenceRole();
	private String headRole = DefaultNames.getDependencyHeadRole();
	private String dependentRole = DefaultNames.getDependencyDependentRole();
	private String dependencyRelationName = DefaultNames.getDependencyRelationName();

	@Override
	public void process(ProcessingContext<Corpus> ctx, Corpus corpus) throws ModuleException {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected SectionResolvedObjects createResolvedObjects(ProcessingContext<Corpus> ctx) throws ResolverException {
		return new SectionResolvedObjects(ctx, this);
	}
	
	@Override
	protected String[] addLayersToSectionFilter() {
		return new String[] {
				tokenLayerName,
				sentenceLayerName
		};
	}

	@Override
	protected String[] addFeaturesToSectionFilter() {
		// TODO Auto-generated method stub
		return null;
	}

	@Param(nameType = NameType.LAYER)
	public String getTokenLayerName() {
		return tokenLayerName;
	}

	@Param(nameType = NameType.LAYER)
	public String getSentenceLayerName() {
		return sentenceLayerName;
	}

	@Param(nameType = NameType.FEATURE)
	public String getDependencyLabelFeatureName() {
		return dependencyLabelFeatureName;
	}

	@Param(nameType = NameType.ARGUMENT)
	public String getSentenceRole() {
		return sentenceRole;
	}

	@Param(nameType = NameType.ARGUMENT)
			public String getHeadRole() {
				return headRole;
	}

	@Param(nameType = NameType.ARGUMENT)
	public String getDependentRole() {
				return dependentRole;
	}

	@Param(nameType = NameType.RELATION)
	public String getDependencyRelationName() {
				return dependencyRelationName;
	}
			
	public void setDependencyRelationName(String dependencyRelationName) {
		this.dependencyRelationName = dependencyRelationName;
	}

	public void setDependencyLabelFeatureName(String dependencyLabelFeatureName) {
		this.dependencyLabelFeatureName = dependencyLabelFeatureName;
	}

	public void setSentenceRole(String sentenceRole) {
		this.sentenceRole = sentenceRole;
	}

	public void setHeadRole(String headRole) {
		this.headRole = headRole;
	}

	public void setDependentRole(String dependentRole) {
		this.dependentRole = dependentRole;
	}
	
	public void setTokenLayerName(String tokenLayerName) {
		this.tokenLayerName = tokenLayerName;
	}

	public void setSentenceLayerName(String sentenceLayerName) {
		this.sentenceLayerName = sentenceLayerName;
	}

	private void iteratorSnippet(ProcessingContext<Corpus> ctx, Corpus corpus) {
		Logger logger = getLogger(ctx);
		EvaluationContext evalCtx = new EvaluationContext(logger);

		// iteration des documents du corpus
		for (Document doc : Iterators.loop(documentIterator(evalCtx, corpus))) {
			// faire qqch avec doc, par ex
			doc.getId();
			doc.getLastFeature("DOCFEATUREKEY");
			// iteration des sections du document
			for (Section sec : Iterators.loop(sectionIterator(evalCtx, doc))) {
				// faire qqch avec sec, par ex
				sec.getName();
				sec.getLastFeature("SECFEATUREKEY");
				sec.getDocument();
				// iteration des annotations d'un layer dans une section
				Layer layer = sec.ensureLayer("LAYERNAME");
				for (Annotation a : layer) {
					// faire qqch avec a, par ex
					a.getStart();
					a.getEnd();
					a.getLength();
					a.getLastFeature("ANNOTATIONFEATUREKEY");
					a.getSection();
				}
				
				// iteration des sentences dans une section
				for (Layer sentLayer : sec.getSentences(getTokenLayerName(), getSentenceLayerName())) {
					Annotation sent = sentLayer.getSentenceAnnotation();
					// faire qqch avec sent
					// iteration des mots dans une sentence
					for (Annotation token : sentLayer) {
						// faire qqch avec token
					}
				}
				
				// iteration des relations dans une section
				for (Relation rel : sec.getAllRelations()) {
					// faire qqch avec la relation, par ex
					rel.getName();
					rel.getSection();
					rel.getLastFeature("RELFEATUREKEY");
					// iteration des tuples d'une relation
					for (Tuple t : rel.getTuples()) {
						// faire qqch avec t, par ex
						t.getRelation();
						t.getLastFeature("TUPLEFEATUREKEY");
						t.getArgument("ROLE");
						// iterer les arguments
						for (String role : t.getRoles()) {
							Element arg = t.getArgument(role);
							// faire qqch avec arg, par ex
							arg.getLastFeature("FEATUREKEY");
							Annotation a = DownCastElement.toAnnotation(arg);
						}
					}
				}
				
				// une relation en particulier
				Relation dependencies = sec.getRelation(dependencyRelationName );
				// iterer les tuples
				for (Tuple dep : dependencies.getTuples()) {
					String label = dep.getLastFeature(dependencyLabelFeatureName);
					Element sentence = dep.getArgument(sentenceRole);
					Element head = dep.getArgument(headRole);
					Element dependent = dep.getArgument(dependentRole);
				}
			}
		}
		
		// on peut iterer les sections sans passer par les documents
		for (Section sec : Iterators.loop(sectionIterator(evalCtx, corpus))) {
			//
		}
	}
	
	/***
	 * 
	 * 
	 *  Mapping adds
	 */
	
	/**
	 * Access the alvis corpus and create the TEES Corpus and documents
	 * @param ctx
	 * @param corpusAlvis
	 * @return
	 */
	public CorpusTEES createTheTeesCorpus(ProcessingContext<Corpus> ctx, Corpus corpusAlvis) {
		Logger logger = getLogger(ctx);
		EvaluationContext evalCtx = new EvaluationContext(logger);

		CorpusTEES corpusTEES = new CorpusTEES();

		// iteration des documents du corpus
		for (Document documentAlvis : Iterators.loop(documentIterator(evalCtx, corpusAlvis))) {
			// create a TEES document
			CorpusTEES.Document documentTees = new CorpusTEES.Document();
			// adding the id of the TEES document
			documentTees.setId(documentAlvis.getId());
			// set all sentences the TEES document /!\ instruction to be to be changed
			documentTees.getSentence().addAll(createTheTeesSentences(sectionIterator(evalCtx, documentAlvis), corpusAlvis));
			// adding the document to the TEES corpus
			corpusTEES.getDocument().add(documentTees);
		}

		return corpusTEES;
	}

	/**
	 * Access the alvis corpus and create all the TEES Sentences
	 * 
	 * @param documentAlvis
	 * @return
	 */
	private ArrayList<CorpusTEES.Document.Sentence> createTheTeesSentences(Iterator<Section> sectionIt, Corpus corpus) {
		ArrayList<CorpusTEES.Document.Sentence> sentences = new ArrayList<CorpusTEES.Document.Sentence>();
		
		// loop on sections
		for (Section sectionAlvis : Iterators.loop(sectionIt)) {
			// loop on sentences
			for (Layer sentLayer : sectionAlvis.getSentences(getTokenLayerName(), getSentenceLayerName())) {
				// get an alvis sentence
				Annotation sentenceAlvis = sentLayer.getSentenceAnnotation();
				// mapping to a tees sentence
				CorpusTEES.Document.Sentence sentenceTees = new CorpusTEES.Document.Sentence();
				sentenceTees.setId(sentenceAlvis.getStringId());
				sentenceTees.setText(sentenceAlvis.getForm()); // is it the text content  ?
				sentenceTees.setCharOffset(sentenceAlvis.getStart() + "-" + sentenceAlvis.getEnd());
				// sentenceTees.setTail(sentenceAlvis.??));
				
	

				// add all the entities, /!\ instruction to be to be changed
				sentenceTees.getEntity().addAll(createTheTeesEntities(sentLayer, corpus));
				
				// The tees interactions of this sentence /!\ instruction to be to be changed
				sentenceTees.getInteraction().addAll(createTheInteractions(sectionAlvis.getAllRelations())); 
				
				// the tees analyses of this sentence /!\ instruction to be to be changed
				// sentenceTees.setAnalyses(createTheAnalyses(sentenceAlvis.get(i), corpus)); 
				
				// adding
				sentences.add(sentenceTees);
			}
		} // *** end for adding the list of sentences to a document

		return sentences;
	}

	private ArrayList<Interaction> createTheInteractions(Collection<Relation> allRelations) {
		ArrayList<Interaction> interactions = new ArrayList<Interaction>();
		// iteration des relations dans une section
		for (Relation rel : allRelations) {
			Interaction interaction = new Interaction();
			// faire qqch avec la relation, par ex
			rel.getName();
			rel.getSection();
			rel.getLastFeature("RELFEATUREKEY");
			
			interaction.setId(rel.getStringId());
			// iteration des tuples d'une relation
			interaction.setOrigId(rel.getStringId());
			//
			// interaction.setType(rel.getType());
			
			for (Tuple t : rel.getTuples()) {
				// faire qqch avec t, par ex
				t.getRelation();
				t.getLastFeature("TUPLEFEATUREKEY");
				t.getArgument("ROLE");
				
				if(t.getArity()==2){
					
					Iterator<Element> itr = t.getAllArguments().iterator();
					Element arg1 = itr.next();
					interaction.setE1(arg1.getStringId());

					Element arg2 = itr.next();
					interaction.setE2(arg2.getStringId());
					
				}
			}
		interactions.add(interaction);
		}
		
		return interactions;
	}

	/**
	 * Access the alvis corpus and create all the entities of a sentence
	 * 
	 * @param sentenceAlvis
	 * @return
	 */
	private ArrayList<CorpusTEES.Document.Sentence.Entity> createTheTeesEntities(Layer entitylayer, Corpus corpus) {
		ArrayList<CorpusTEES.Document.Sentence.Entity> entities = new ArrayList<CorpusTEES.Document.Sentence.Entity>();

		// loop on entities
		for (Annotation entityAlvis : entitylayer) {
			CorpusTEES.Document.Sentence.Entity entityTees = new CorpusTEES.Document.Sentence.Entity();
			entityTees.setId(entityAlvis.getStringId());
			entityTees.setCharOffset(entityAlvis.getStart() + "-" + entityAlvis.getEnd());
			entityTees.setOrigOffset(entityAlvis.getStart() + "-" + entityAlvis.getEnd()); // is it, I'm not sure
			// entityTees.setHeadOffset(???);
			entityTees.setText(entityAlvis.getForm());
			entityTees.setType(entityAlvis.getLastFeature("ANNOTATIONFEATUREKEY"));
			// entityTees.setType(token.);
			
		}
		return entities;

	}
}
