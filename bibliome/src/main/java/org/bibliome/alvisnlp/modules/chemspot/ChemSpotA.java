package org.bibliome.alvisnlp.modules.chemspot;

import java.util.logging.Logger;

import org.bibliome.alvisnlp.modules.SectionModule;
import org.bibliome.alvisnlp.modules.SectionModule.SectionResolvedObjects;
import org.bibliome.util.Iterators;
import org.bibliome.util.files.InputFile;

import alvisnlp.corpus.Annotation;
import alvisnlp.corpus.Corpus;
import alvisnlp.corpus.DefaultNames;
import alvisnlp.corpus.Document;
import alvisnlp.corpus.Layer;
import alvisnlp.corpus.Section;
import alvisnlp.corpus.creators.AnnotationCreator;
import alvisnlp.corpus.expressions.EvaluationContext;
import alvisnlp.corpus.expressions.ResolverException;
import alvisnlp.module.ModuleException;
import alvisnlp.module.ProcessingContext;
import alvisnlp.module.lib.AlvisNLPModule;
import alvisnlp.module.lib.Param;
import alvisnlp.module.types.Mapping;
import de.berlin.hu.chemspot.ChemSpot;
import de.berlin.hu.chemspot.ChemSpotFactory;
import de.berlin.hu.chemspot.Mention;
import de.berlin.hu.util.Constants.ChemicalID;


/**
 * 
 * @author mba
 *
 */

@AlvisNLPModule
public class ChemSpotA extends SectionModule<SectionResolvedObjects> implements AnnotationCreator {



        //-m path to a CRF model file (internal default model file will be used if not provided)
    	private InputFile CRFModel = null;
        //-s path to a OpenNLP sentence model file (internal default model file will be used if not provided)
		private InputFile openNLPSentenceModel = null;
		// -d path to a zipped set of brics dictionary automata (parameter defaults to 'dict.zip' if not provided)"
		private InputFile dictionary = null;
        //-i path to a zipped tab-separated text file representing a map of terms to ids (parameter defaults to 'ids.zip' if not provided)
		private InputFile mapTerms2Ids = null;
        //-M path to a multi-class model file (parameter defaults to 'multiclass.bin' if not provided)
		private InputFile multiClassModel = null;

//    flags:
//        -e if this flag is set, the performance of ChemSpot on an IOB gold-standard corpus (cf. -c) is evaluated"
//        -u if this flag is set, ChemSpot will update the dictionary and ids file
//        -T number of threads to create when processing a document collection
//
//    input control:
//        -c path to a directory containing corpora in IOB format
//        -g path to a directory containing gzipped text files
//        -t path to a text file
        
		// -f path to a directory of text files
		// corresponds to Corpus

//    output control:
        // -o path to output file
		// Corresponds to Corpus
//        -I if this flag is set, the output will be converted into the IOB format

		private String mentionlayerName;
		private String neFeatureName = DefaultNames.getNamedEntityTypeFeature();
	

	@Override
	public void process(ProcessingContext<Corpus> ctx, Corpus corpus) throws ModuleException {
		Logger logger = getLogger(ctx);
		EvaluationContext evalCtx = new EvaluationContext(logger);
		
		
		ChemSpot tagger = ChemSpotFactory.createChemSpot();
		
		for (Document document : Iterators.loop(documentIterator(evalCtx, corpus))) {
			
			for (Section section : Iterators.loop(sectionIterator(evalCtx, document))) {
				Layer layer = section.ensureLayer(this.getMentionlayerName());
				
				for (Mention mention : tagger.tag(section.getContents())) {
					
					Annotation a = new Annotation(this, layer, mention.getStart()+1, mention.getEnd()+2);

					a.addFeature(this.neFeatureName, mention.getType().name());
					
					
					// setting database Ids
					a.addFeature(ChemicalID.CAS.name(), mention.getCAS());
					a.addFeature(ChemicalID.CHEB.name(), mention.getCAS());
					a.addFeature(ChemicalID.CHID.name(), mention.getCAS());
					a.addFeature(ChemicalID.DRUG.name(), mention.getCAS());
					a.addFeature(ChemicalID.FDA.name(), mention.getCAS());
					a.addFeature(ChemicalID.FDA_DATE.name(), mention.getCAS());
					a.addFeature(ChemicalID.HMBD.name(), mention.getCAS());
					a.addFeature(ChemicalID.INCH.name(), mention.getCAS());
					a.addFeature(ChemicalID.KEGD.name(), mention.getCAS());
					a.addFeature(ChemicalID.KEGG.name(), mention.getCAS());
					a.addFeature(ChemicalID.MESH.name(), mention.getCAS());
					a.addFeature(ChemicalID.PUBC.name(), mention.getCAS());
					a.addFeature(ChemicalID.PUBS.name(), mention.getCAS());
										
					layer.add(a);
					
					}
				
				
			}
		}
		
	}

	@Override
	protected String[] addLayersToSectionFilter() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected String[] addFeaturesToSectionFilter() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected org.bibliome.alvisnlp.modules.SectionModule.SectionResolvedObjects createResolvedObjects(
			ProcessingContext<Corpus> ctx) throws ResolverException {
		// TODO Auto-generated method stub
		return null;
	}

	@Param(mandatory = false)
	public InputFile getCRFModel() {
		return CRFModel;
	}

	public void setCRFModel(InputFile cRFModel) {
		CRFModel = cRFModel;
	}

	@Param(mandatory = false)
	public InputFile getOpenNLPSentenceModel() {
		return openNLPSentenceModel;
	}

	public void setOpenNLPSentenceModel(InputFile openNLPSentenceModel) {
		this.openNLPSentenceModel = openNLPSentenceModel;
	}

	@Param(mandatory = false)
	public InputFile getDictionary() {
		return dictionary;
	}

	public void setDictionary(InputFile dictionary) {
		this.dictionary = dictionary;
	}

	@Param(mandatory = false)
	public InputFile getMapTerms2Ids() {
		return mapTerms2Ids;
	}


	public void setMapTerms2Ids(InputFile mapTerms2Ids) {
		this.mapTerms2Ids = mapTerms2Ids;
	}

	@Param(mandatory = false)
	public InputFile getMultiClassModel() {
		return multiClassModel;
	}

	public void setMultiClassModel(InputFile multiClassModel) {
		this.multiClassModel = multiClassModel;
	}

	@Param(mandatory = true)
	public String getMentionlayerName() {
		return mentionlayerName;
	}

	public void setMentionlayerName(String mentionlayerName) {
		this.mentionlayerName = mentionlayerName;
	}

	@Param(mandatory = false)	
	public String getNeFeatureName() {
		return neFeatureName;
	}

	public void setNeFeatureName(String neFeatureName) {
		this.neFeatureName = neFeatureName;
	}

	@Override
	public Mapping getConstantAnnotationFeatures() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setConstantAnnotationFeatures(Mapping constantAnnotationFeatures) {
		// TODO Auto-generated method stub
		
	}

}
