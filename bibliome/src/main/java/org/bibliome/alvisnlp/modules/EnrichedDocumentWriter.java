/*
Copyright 2016, 2017 Institut National de la Recherche Agronomique

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

        http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/



package org.bibliome.alvisnlp.modules;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import org.bibliome.alvisnlp.modules.SectionModule.SectionResolvedObjects;
import org.bibliome.util.Iterators;
import org.bibliome.util.StringCat;
import org.bibliome.util.Strings;
import org.bibliome.util.Timer;
import org.bibliome.util.files.OutputDirectory;

import alvisnlp.corpus.Annotation;
import alvisnlp.corpus.Corpus;
import alvisnlp.corpus.DefaultNames;
import alvisnlp.corpus.Document;
import alvisnlp.corpus.Layer;
import alvisnlp.corpus.NameType;
import alvisnlp.corpus.Section;
import alvisnlp.corpus.expressions.EvaluationContext;
import alvisnlp.corpus.expressions.ResolverException;
import alvisnlp.module.ModuleException;
import alvisnlp.module.ProcessingContext;
import alvisnlp.module.TimerCategory;
import alvisnlp.module.lib.AlvisNLPModule;
import alvisnlp.module.lib.Param;
import alvisnlp.module.types.Mapping;

/**
 * Writes a corpus into Alvis Enriched Document format.
 */
@AlvisNLPModule
public class EnrichedDocumentWriter extends SectionModule<SectionResolvedObjects> {
    private OutputDirectory                    outDir                   = null;
    private String                  outFilePrefix            = null;
    private String                  outFileSuffix            = ".sem";
    private Integer                 blockSize                = 100;
    private Integer                 blockStart               = 0;
    private String                  urlPrefix                = null;
    private String urlSuffixFeature = "id";
    private Mapping                 metaTrans                = null;
    private String                  idMetaFeature            = null;
    private String tokenLayerName           = null;
    private String                  tokenTypeFeature         = null;
    private String wordLayerName            = DefaultNames.getWordLayer();
    private String                  lemmaFeature             = DefaultNames.getCanonicalFormFeature();
    private String                  posFeature               = DefaultNames.getPosTagFeature();
    private String sentenceLayerName        = DefaultNames.getSentenceLayer();
    private String neLayerName              = null;
    private String                  neCanonicalFormFeature   = DefaultNames.getCanonicalFormFeature();
    private String                  neTypeFeature            = DefaultNames.getNamedEntityTypeFeature();
    private String termLayerName            = null;
    private String                  termCanonicalFormFeature = null;
    private String                  semanticFeature          = null;
    private static final String dateFormat               = "yyyy-MM-dd";

    private final StringCat strcat                       = new StringCat();

    /**
     * Instantiates a new enriched document writer.
     * 
     * @throws NoSuchAlgorithmException
     *             the no such algorithm exception
     */
    public EnrichedDocumentWriter() {
    	super();
    }

    @Override
    public String[] addFeaturesToSectionFilter() {
    	return new String[] {};
    }

    @Override
    public String[] addLayersToSectionFilter() {
    	return null;
    }

    @Override
	protected SectionResolvedObjects createResolvedObjects(ProcessingContext<Corpus> ctx) throws ResolverException {
		return new SectionResolvedObjects(ctx, this);
	}

	@Override
    public void process(ProcessingContext<Corpus> ctx, Corpus corpus) throws ModuleException {
    	Logger logger = getLogger(ctx);
    	EvaluationContext evalCtx = new EvaluationContext(logger);
    	outDir.mkdir();
    	int n = 0;
    	Timer<TimerCategory> writeTimer = getTimer(ctx, "write-xml", TimerCategory.PREPARE_DATA, false);
    	String urlPrefix = Strings.escapeXML(this.urlPrefix);
    	try {
    		MessageDigest md5 = MessageDigest.getInstance("MD5");
    		for (Document doc : Iterators.loop(documentIterator(evalCtx, corpus))) {
    			DateFormat df = new SimpleDateFormat(dateFormat);
    			String date = df.format(new Date(System.currentTimeMillis()));
    			String block = String.format("%03d", (n / blockSize) + blockStart);
    			String docN = String.format("%03d", n % blockSize);
    			File blockDir = new File(outDir, block);
    			blockDir.mkdirs();
    			File docFile = new File(blockDir, outFilePrefix + "_" + block + "_" + docN + outFileSuffix);
    			Collection<Section> sections = Iterators.fill(sectionIterator(evalCtx, doc), new ArrayList<Section>());
    			String contents = Strings.escapeXML(getContents(sections, strcat));
    			PrintStream out = new PrintStream(new BufferedOutputStream(new FileOutputStream(docFile)), false, "UTF-8");
    			n++;
    			writeTimer.start();
    			out.print("<?xml version=\"1.0\" encoding=\"UTF-8\"?><documentCollection xmlns=\"http://alvis.info/enriched/\" version=\"1.1\">");
    			out.printf("<documentRecord id=\"%s\">", getHash(contents, md5));
    			out.printf("<acquisition><acquisitionData><modifiedDate>%s</modifiedDate>", date);
    			String urlSuffix;
    			if (doc.hasFeature(urlSuffixFeature))
    				urlSuffix = Strings.escapeXML(doc.getLastFeature(urlSuffixFeature));
    			else {
    				getLogger(ctx).warning("document " + doc.getId() + " has no feature " + urlSuffixFeature);
    				urlSuffix = "";
    			}
    			out.printf("<urls><url>%s%s</url></urls></acquisitionData>", urlPrefix, urlSuffix);
    			out.printf("<originalDocument mimeType=\"text/xml\" charSet=\"UTF-8\">%s</originalDocument><canonicalDocument>%s</canonicalDocument>", contents, contents);
    			out.print("<metaData>");
    			for (String metaKey : metaTrans.keySet()) {
    				List<String> values = doc.getFeature(metaKey);
    				if (values == null)
    					continue;
    				String transMetaKey = metaTrans.get(metaKey);
//    				if ("author".equals(transMetaKey)) {
//    					for (String v : values) {
//    						List<String> author = Strings.split(v, ':', 0);
//    						if (author.size() > 0) {
//    							out.printf("<meta name=\"author\">");
//    							if (!"".equals(author.get(0)))
//    								out.printf("<meta name=\"last-name\">%s</meta>", Strings.escapeXML(author.get(0)));
//    							if ((author.size() > 1) && !"".equals(author.get(1)))
//    								out.printf("<meta name=\"fore-name\">%s</meta>", Strings.escapeXML(author.get(1)));
//    							if ((author.size() > 2) && !"".equals(author.get(2)))
//    								out.printf("<meta name=\"initials\">%s</meta>", Strings.escapeXML(author.get(2)));
//    							out.printf("</meta>");
//    						}
//    					}
//    				}
//    				else {
    					for (String v : values) {
    						out.printf("<meta name=\"%s\">%s</meta>", Strings.escapeXML(transMetaKey), Strings.escapeXML(v));
    					}
//    				}
    			}
    			if (idMetaFeature != null) {
    				out.printf("<meta name=\"%s\">%s</meta>", Strings.escapeXML(idMetaFeature), Strings.escapeXML(doc.getId()));
    			}
    			out.print("</metaData>");
    			out.print("</acquisition>");
    			out.print("<linguisticAnalysis>");
    			writeTimer.stop();

    			List<EDToken> tokens = new ArrayList<EDToken>();
    			int offset = 0;
    			for (Section sec : sections) {
    				for (Annotation ann : sec.getLayer(tokenLayerName)) {
    					tokens.add(new EDToken(ann, offset));
    				}
    				offset += sec.getContents().length() + 1;
    			}

    			List<EDWord> words = new ArrayList<EDWord>();
    			for (Section sec : sections) {
    				Layer tokenLayer = sec.getLayer(tokenLayerName);
    				for (Annotation ann : sec.getLayer(wordLayerName)) {
    					Annotation[] toks = tokenLayer.between(ann).toArray(new Annotation[0]);
    					int[] wordTokenIds = new int[toks.length];
    					for (int i = 0; i < wordTokenIds.length; i++) {
    						wordTokenIds[i] = toks[i].hashCode();
    					}
    					words.add(new EDWord(ann, wordTokenIds));
    				}
    			}

    			List<EDSent> sents = new ArrayList<EDSent>();
    			for (Section sec : sections) {
    				Layer tokenLayer = sec.getLayer(tokenLayerName);
    				for (Annotation ann : sec.ensureLayer(sentenceLayerName)) {
    					Layer toks = tokenLayer.between(ann);
    					sents.add(new EDSent(ann, toks.first().hashCode(), toks.last().hashCode()));
    				}
    			}

    			List<EDNE> nes = new ArrayList<EDNE>();
    			for (Section sec : sections) {
    				Layer tokenLayer = sec.getLayer(tokenLayerName);
    				if (sec.hasLayer(neLayerName)) {
    					for (Annotation ann : sec.getLayer(neLayerName)) {
    						Annotation[] toks = tokenLayer.between(ann).toArray(new Annotation[0]);
    						int[] tokenIds = new int[toks.length];
    						for (int i = 0; i < tokenIds.length; i++) {
    							tokenIds[i] = toks[i].hashCode();
    						}
    						nes.add(new EDNE(ann, tokenIds));
    					}
    				}
    			}

    			List<EDMonoTerm> monoTerms = new ArrayList<EDMonoTerm>();
    			List<EDMultiTerm> multiTerms = new ArrayList<EDMultiTerm>();
    			if (termLayerName != null) {
    				for (Section sec : sections) {
    					if (!sec.hasLayer(termLayerName))
    						continue;
    					// Layer tokenLayer = sec.getLayer(tokenAnnotations);
    					Layer wordLayer = sec.getLayer(wordLayerName);
    					for (Annotation ann : sec.getLayer(termLayerName)) {
    						Annotation[] ws = wordLayer.between(ann).toArray(new Annotation[0]);
    						if (ws.length == 1) {
    							monoTerms.add(new EDMonoTerm(ann, ws[0].hashCode()));
    						}
    						else {
    							int[] wordIds = new int[ws.length];
    							for (int i = 0; i < ws.length; i++) {
    								wordIds[i] = ws[i].hashCode();
    							}
    							multiTerms.add(new EDMultiTerm(ann, wordIds));
    						}
    					}
    				}
    			}

    			writeTimer.start();
    			if (lemmaFeature != null) {
    				out.print("<lemma_level>");
    				for (EDWord word : words) {
    					word.toLemmaXML(out);
    				}
    				out.print("</lemma_level>");
    			}

    			if (posFeature != null) {
    				out.print("<morphosyntactic_features_level>");
    				for (EDWord word : words) {
    					word.toPOSXML(out);
    				}
    				out.print("</morphosyntactic_features_level>");
    			}

    			if ((termLayerName != null) && (!multiTerms.isEmpty())) {
    				out.print("<phrase_level>");
    				for (EDMultiTerm term : multiTerms)
    					term.toPhraseXML(out);
    				out.print("</phrase_level>");
    			}

    			out.print("<semantic_unit_level>");
    			for (EDNE ne : nes) {
    				ne.toXML(out);
    			}
    			for (EDMultiTerm term : multiTerms)
    				term.toXML(out);
    			for (EDMonoTerm term : monoTerms)
    				term.toXML(out);
    			out.print("</semantic_unit_level>");

    			out.print("<sentence_level>");
    			for (EDSent sent : sents) {
    				sent.toXML(out);
    			}
    			out.print("</sentence_level>");

    			out.print("<token_level>");
    			for (EDToken tok : tokens) {
    				tok.toXML(out);
    			}
    			out.print("</token_level>");

    			out.print("<word_level>");
    			for (EDWord word : words) {
    				word.toXML(out);
    			}
    			out.print("</word_level>");

    			if (semanticFeature != null) {
    				out.print("<semantic_features_level>");
    				int id = 1;
    				for (EDNE ne : nes) {
    					if (ne.ann.hasFeature(semanticFeature))
    						for (String feat : ne.ann.getFeature(semanticFeature))
    							out.printf("<semantic_features><id>semantic_features%d</id><semantic_category><list_refid_ontology_node><refid_ontology_node>%s</refid_ontology_node></list_refid_ontology_node></semantic_category><refid_semantic_unit>named_entity%d</refid_semantic_unit></semantic_features>", id++, feat, ne.id);
    				}
    				for (EDMonoTerm term : monoTerms) {
    					if (term.ann.hasFeature(semanticFeature))
    						out.printf("<semantic_features><id>semantic_features%d</id><semantic_category><list_refid_ontology_node><refid_ontology_node>%s</refid_ontology_node></list_refid_ontology_node></semantic_category><refid_semantic_unit>term%d</refid_semantic_unit></semantic_features>", id++, term.ann.getLastFeature(semanticFeature), term.id);
    				}
    				for (EDMultiTerm term : multiTerms) {
    					if (term.ann.hasFeature(semanticFeature))
    						out.printf("<semantic_features><id>semantic_features%d</id><semantic_category><list_refid_ontology_node><refid_ontology_node>%s</refid_ontology_node></list_refid_ontology_node></semantic_category><refid_semantic_unit>term%d</refid_semantic_unit></semantic_features>", id++, term.ann.getLastFeature(semanticFeature), term.id);
    				}
    				out.print("</semantic_features_level>");
    			}

    			out.print("</linguisticAnalysis></documentRecord></documentCollection>");
    			out.close();
    			writeTimer.stop();
    		}
    	}
    	catch (FileNotFoundException|UnsupportedEncodingException|NoSuchAlgorithmException e) {
    		rethrow(e);
    	}
    }

    /**
     * Gets the contents.
     * 
     * @param sections
     *            the sections
     * @param strcat
     *            the sb
     * 
     * @return the contents
     */
    private static String getContents(Collection<Section> sections, StringCat strcat) {
        strcat.clear();
        for (Section sec : sections) {
            strcat.append(sec.getContents());
            strcat.append("\n");
        }
        return strcat.toString();
    }

    /**
     * Gets the hash.
     * 
     * @param msg
     *            the msg
     * @param md
     *            the md
     * 
     * @return the hash
     * 
     * @throws UnsupportedEncodingException
     *             the unsupported encoding exception
     */
    private static String getHash(String msg, MessageDigest md) throws UnsupportedEncodingException {
        md.reset();
        StringBuffer sb = new StringBuffer();
        for (byte b : md.digest(msg.getBytes("UTF-8")))
            sb.append(Integer.toHexString(0xFF & b));
        return sb.toString().toUpperCase();
    }

    /**
     * Gets the out dir.
     * 
     * @return the outDir
     */
    @Param
    public OutputDirectory getOutDir() {
        return outDir;
    }

    /**
     * Sets the out dir.
     * 
     * @param outDir
     *            the outDir to set
     */
    public void setOutDir(OutputDirectory outDir) {
        this.outDir = outDir;
    }

    /**
     * Gets the out file prefix.
     * 
     * @return the outFilePrefix
     */
    @Param(defaultDoc = "Prefix of the name of generated files.")
    public String getOutFilePrefix() {
        return outFilePrefix;
    }

    /**
     * Sets the out file prefix.
     * 
     * @param outFilePrefix
     *            the outFilePrefix to set
     */
    public void setOutFilePrefix(String outFilePrefix) {
        this.outFilePrefix = outFilePrefix;
    }

    /**
     * Gets the out file suffix.
     * 
     * @return the outFileSuffix
     */
    @Param(nameType=NameType.FEATURE, defaultDoc = "Suffix of the name of generated files.")
    public String getOutFileSuffix() {
        return outFileSuffix;
    }

    /**
     * Sets the out file suffix.
     * 
     * @param outFileSuffix
     *            the outFileSuffix to set
     */
    public void setOutFileSuffix(String outFileSuffix) {
        this.outFileSuffix = outFileSuffix;
    }

    /**
     * Gets the block size.
     * 
     * @return the blockSize
     */
    @Param
    public Integer getBlockSize() {
        return blockSize;
    }

    /**
     * Sets the block size.
     * 
     * @param blockSize
     *            the blockSize to set
     */
    public void setBlockSize(Integer blockSize) {
        this.blockSize = blockSize;
    }

    /**
     * Gets the block start.
     * 
     * @return the blockStart
     */
    @Param
    public Integer getBlockStart() {
        return blockStart;
    }

    /**
     * Sets the block start.
     * 
     * @param blockStart
     *            the blockStart to set
     */
    public void setBlockStart(Integer blockStart) {
        this.blockStart = blockStart;
    }

    /**
     * Gets the url prefix.
     * 
     * @return the urlPrefix
     */
    @Param(defaultDoc = "Prefix for the document URL.")
    public String getUrlPrefix() {
        return urlPrefix;
    }

    /**
     * Sets the url prefix.
     * 
     * @param urlPrefix
     *            the urlPrefix to set
     */
    public void setUrlPrefix(String urlPrefix) {
        this.urlPrefix = urlPrefix;
    }

    /**
     * Gets the meta trans.
     * 
     * @return the metaTrans
     */
    @Param(defaultDoc = "Metadata key translation.")
    public Mapping getMetaTrans() {
        return metaTrans;
    }

    /**
     * Sets the meta trans.
     * 
     * @param metaTrans
     *            the metaTrans to set
     */
    public void setMetaTrans(Mapping metaTrans) {
        this.metaTrans = metaTrans;
    }

    /**
     * Gets the id meta feature.
     * 
     * @return the idMetaFeature
     */
    @Param(nameType=NameType.FEATURE, defaultDoc = "Metadata key for the document id.")
    public String getIdMetaFeature() {
        return idMetaFeature;
    }

    /**
     * Sets the id meta feature.
     * 
     * @param idMetaFeature
     *            the idMetaFeature to set
     */
    public void setIdMetaFeature(String idMetaFeature) {
        this.idMetaFeature = idMetaFeature;
    }

    /**
     * Gets the token layer name.
     * 
     * @return the tokenLayerName
     */
    @Param(nameType=NameType.LAYER, defaultDoc = "Name of the layer containing token annotations.")
    public String getTokenLayerName() {
        return tokenLayerName;
    }

    /**
     * Sets the token layer name.
     * 
     * @param tokenLayerName
     *            the tokenLayerName to set
     */
    public void setTokenLayerName(String tokenLayerName) {
        this.tokenLayerName = tokenLayerName;
    }

    /**
     * Gets the token type feature.
     * 
     * @return the tokenTypeFeature
     */
    @Param(nameType=NameType.FEATURE, defaultDoc = "Name of the feature in token annotations containing the token type.")
    public String getTokenTypeFeature() {
        return tokenTypeFeature;
    }

    /**
     * Sets the token type feature.
     * 
     * @param tokenTypeFeature
     *            the tokenTypeFeature to set
     */
    public void setTokenTypeFeature(String tokenTypeFeature) {
        this.tokenTypeFeature = tokenTypeFeature;
    }

    /**
     * Gets the word layer name.
     * 
     * @return the wordLayerName
     */
    @Param(nameType=NameType.LAYER, defaultDoc = "Name of the layer containing word annotations.")
    public String getWordLayerName() {
        return wordLayerName;
    }

    /**
     * Sets the word layer name.
     * 
     * @param wordLayerName
     *            the wordLayerName to set
     */
    public void setWordLayerName(String wordLayerName) {
        this.wordLayerName = wordLayerName;
    }

    /**
     * Gets the lemma feature.
     * 
     * @return the lemmaFeature
     */
    @Param(nameType=NameType.FEATURE, defaultDoc = "Name of the feature in word annotations containing the lemma.")
    public String getLemmaFeature() {
        return lemmaFeature;
    }

    /**
     * Sets the lemma feature.
     * 
     * @param lemmaFeature
     *            the lemmaFeature to set
     */
    public void setLemmaFeature(String lemmaFeature) {
        this.lemmaFeature = lemmaFeature;
    }

    /**
     * Gets the pos feature.
     * 
     * @return the posFeature
     */
    @Param(nameType=NameType.FEATURE, defaultDoc = "Name of the feature in word annotations containing the POS tag.")
    public String getPosFeature() {
        return posFeature;
    }

    /**
     * Sets the pos feature.
     * 
     * @param posFeature
     *            the posFeature to set
     */
    public void setPosFeature(String posFeature) {
        this.posFeature = posFeature;
    }

    /**
     * Gets the sentence layer name.
     * 
     * @return the sentenceLayerName
     */
    @Param(nameType=NameType.LAYER, defaultDoc = "Name of the layer containing sentence annotations.")
    public String getSentenceLayerName() {
        return sentenceLayerName;
    }

    /**
     * Sets the sentence layer name.
     * 
     * @param sentenceLayerName
     *            the sentenceLayerName to set
     */
    public void setSentenceLayerName(String sentenceLayerName) {
        this.sentenceLayerName = sentenceLayerName;
    }

    /**
     * Gets the ne layer name.
     * 
     * @return the neLayerName
     */
    @Param(nameType=NameType.LAYER, defaultDoc = "Name of the layer containing named entity annotations.")
    public String getNeLayerName() {
        return neLayerName;
    }

    /**
     * Sets the ne layer name.
     * 
     * @param neLayerName
     *            the neLayerName to set
     */
    public void setNeLayerName(String neLayerName) {
        this.neLayerName = neLayerName;
    }

    /**
     * Gets the ne canonical form feature.
     * 
     * @return the neCanonicalFormFeature
     */
    @Param(nameType=NameType.FEATURE, defaultDoc = "Name of the feature in named entity annotations containing the canonical form.")
    public String getNeCanonicalFormFeature() {
        return neCanonicalFormFeature;
    }

    /**
     * Sets the ne canonical form feature.
     * 
     * @param neCanonicalFormFeature
     *            the neCanonicalFormFeature to set
     */
    public void setNeCanonicalFormFeature(String neCanonicalFormFeature) {
        this.neCanonicalFormFeature = neCanonicalFormFeature;
    }

    /**
     * Gets the ne type feature.
     * 
     * @return the neTypeFeature
     */
    @Param(nameType=NameType.FEATURE, defaultDoc = "Name of the feature in named entity annotations containing the named entity type.")
    public String getNeTypeFeature() {
        return neTypeFeature;
    }

    /**
     * Sets the ne type feature.
     * 
     * @param neTypeFeature
     *            the neTypeFeature to set
     */
    public void setNeTypeFeature(String neTypeFeature) {
        this.neTypeFeature = neTypeFeature;
    }

    /**
     * Gets the term layer name.
     * 
     * @return the termLayerName
     */
    @Param(nameType=NameType.LAYER, defaultDoc = "Name of the layer containing the term annotations.")
    public String getTermLayerName() {
        return termLayerName;
    }

    /**
     * Sets the term layer name.
     * 
     * @param termLayerName
     *            the termLayerName to set
     */
    public void setTermLayerName(String termLayerName) {
        this.termLayerName = termLayerName;
    }

    /**
     * Gets the term canonical form feature.
     * 
     * @return the termCanonicalFormFeature
     */
    @Param(nameType=NameType.FEATURE, defaultDoc = "Name of the feature containing the term canonical form.")
    public String getTermCanonicalFormFeature() {
        return termCanonicalFormFeature;
    }

    /**
     * Sets the term canonical form feature.
     * 
     * @param termCanonicalFormFeature
     *            the termCanonicalFormFeature to set
     */
    public void setTermCanonicalFormFeature(String termCanonicalFormFeature) {
        this.termCanonicalFormFeature = termCanonicalFormFeature;
    }

    /**
     * Gets the semantic feature.
     * 
     * @return the semanticFeature
     */
    @Param(nameType=NameType.FEATURE, mandatory = false, defaultDoc = "Name of the feature containing semantic features of named entities and terms.")
    public String getSemanticFeature() {
        return semanticFeature;
    }

    /**
     * Sets the semantic feature.
     * 
     * @param semanticFeature
     *            the semanticFeature to set
     */
    public void setSemanticFeature(String semanticFeature) {
        this.semanticFeature = semanticFeature;
    }

    @Param(nameType=NameType.FEATURE)
    public String getUrlSuffixFeature() {
		return urlSuffixFeature;
	}

	public void setUrlSuffixFeature(String urlSuffixFeature) {
		this.urlSuffixFeature = urlSuffixFeature;
	}

	private abstract class EDElement {
        final int        id;
        final Annotation ann;

        private EDElement(Annotation ann) {
            this.id = ann.hashCode();
            this.ann = ann;
        }

        abstract void toXML(PrintStream out);
    }

    private class EDToken extends EDElement {
        final int offset;

        private EDToken(Annotation ann, int offset) {
            super(ann);
            this.offset = offset;
        }

        @Override
		void toXML(PrintStream out) {
            out.printf("<token><content>%s</content><from>%d</from><id>token%d</id><to>%d</to><type>%s</type></token>", Strings.escapeXML(ann.getLastFeature(Annotation.FORM_FEATURE_NAME)), ann.getStart() + offset, id, ann.getEnd() + offset - 1, (tokenTypeFeature == null ? "token" : ann.getLastFeature(tokenTypeFeature)));
        }
    }

    private class EDWord extends EDElement {
        final int[] tokens;

        private EDWord(Annotation ann, int[] tokens) {
            super(ann);
            this.tokens = tokens;
        }

        @Override
		void toXML(PrintStream out) {
            out.printf("<word><form>%s</form><id>word%d</id><list_refid_token>", Strings.escapeXML(ann.getLastFeature(Annotation.FORM_FEATURE_NAME)), id);
            for (int tok : tokens)
                out.printf("<refid_token>token%d</refid_token>", tok);
            out.print("</list_refid_token></word>");
        }

        void toLemmaXML(PrintStream out) {
            if (ann.hasFeature(lemmaFeature))
                out.printf("<lemma><canonical_form>%s</canonical_form><id>lemma%d</id><refid_word>word%d</refid_word></lemma>", Strings.escapeXML(ann.getLastFeature(lemmaFeature)), id, id);
        }

        void toPOSXML(PrintStream out) {
            out.printf("<morphosyntactic_features><id>morphosyntactic_features%d</id><refid_word>word%d</refid_word><syntactic_category>%s</syntactic_category></morphosyntactic_features>", id, id, ann.getLastFeature(posFeature));
        }
    }

    private class EDSent extends EDElement {
        final int start;
        final int end;

        private EDSent(Annotation ann, int start, int end) {
            super(ann);
            this.start = start;
            this.end = end;
        }

        @Override
		void toXML(PrintStream out) {
            out.printf("<sentence><form>%s</form><id>sentence%d</id><refid_end_token>token%d</refid_end_token><refid_start_token>token%d</refid_start_token></sentence>", Strings.escapeXML(ann.getLastFeature(Annotation.FORM_FEATURE_NAME)), id, end, start);
        }
    }

    private class EDNE extends EDWord {
    	private EDNE(Annotation ann, int[] tokens) {
            super(ann, tokens);
        }

    	@Override
		void toXML(PrintStream out) {
            String cf = Strings.escapeXML(ann.hasFeature(neCanonicalFormFeature) ? ann.getLastFeature(neCanonicalFormFeature) : ann.getForm());
            String sf = Strings.escapeXML(ann.getForm());
            out.printf("<semantic_unit><named_entity><canonical_form>%s</canonical_form><form>%s</form><id>named_entity%d</id><list_refid_token>", cf, sf, id);
            for (int tok : tokens) {
                out.printf("<refid_token>token%d</refid_token>", tok);
            }
            out.printf("</list_refid_token><named_entity_type>%s</named_entity_type></named_entity></semantic_unit>", ann.getLastFeature(neTypeFeature));
        }
    }

    private class EDMonoTerm extends EDElement {
        final int word;

        private EDMonoTerm(Annotation ann, int word) {
            super(ann);
            this.word = word;
        }

        @Override
		void toXML(PrintStream out) {
            String cf = Strings.escapeXML(ann.getLastFeature(termCanonicalFormFeature));
            String sf = Strings.escapeXML(ann.getLastFeature(Annotation.FORM_FEATURE_NAME));
            out.printf("<semantic_unit><term><canonical_form>%s</canonical_form><form>%s</form><id>term%d</id><refid_word>word%d</refid_word></term></semantic_unit>", cf, sf, id, word);
        }
    }

    private class EDMultiTerm extends EDElement {
        final int[] words;

        private EDMultiTerm(Annotation ann, int[] words) {
            super(ann);
            this.words = words;
        }

        @Override
		void toXML(PrintStream out) {
            String sf = Strings.escapeXML(ann.getLastFeature(Annotation.FORM_FEATURE_NAME));
            String cf;
            if (ann.hasFeature(termCanonicalFormFeature))
                cf = Strings.escapeXML(ann.getLastFeature(termCanonicalFormFeature));
            else
                cf = sf;
            out.printf("<semantic_unit><term><canonical_form>%s</canonical_form><form>%s</form><id>term%d</id><refid_phrase>phrase%d</refid_phrase></term></semantic_unit>", cf, sf, id, id);
        }

        void toPhraseXML(PrintStream out) {
            out.printf("<phrase><id>phrase%d</id><list_refid_components>", id);
            for (int w : words)
                out.printf("<refid_word>word%d</refid_word>", w);
            out.printf("</list_refid_components></phrase>");
        }
    }
}
