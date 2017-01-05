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


package org.bibliome.alvisnlp.modules.projectors;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import org.bibliome.alvisnlp.modules.SectionModule.SectionResolvedObjects;
import org.bibliome.util.CartesianProduct;
import org.bibliome.util.EquivalenceHashSets;
import org.bibliome.util.EquivalenceSets;
import org.bibliome.util.filelines.EquivFileLines;
import org.bibliome.util.filelines.FileLines;
import org.bibliome.util.filelines.InvalidFileLineEntry;
import org.bibliome.util.filelines.TabularFormat;
import org.bibliome.util.newprojector.CharFilter;
import org.bibliome.util.newprojector.CharMapper;
import org.bibliome.util.newprojector.Dictionary;
import org.bibliome.util.newprojector.Match;
import org.bibliome.util.newprojector.Matcher;
import org.bibliome.util.newprojector.State;
import org.bibliome.util.newprojector.chars.Filters;
import org.bibliome.util.newprojector.chars.Mappers;
import org.bibliome.util.newprojector.states.AllValuesState;
import org.bibliome.util.streams.SourceStream;
import org.bibliome.util.streams.TargetStream;

import alvisnlp.corpus.Annotation;
import alvisnlp.corpus.Corpus;
import alvisnlp.corpus.DefaultNames;
import alvisnlp.corpus.NameType;
import alvisnlp.corpus.expressions.ResolverException;
import alvisnlp.module.ProcessingContext;
import alvisnlp.module.lib.AlvisNLPModule;
import alvisnlp.module.lib.Param;

@AlvisNLPModule
public abstract class TyDIProjector extends Projector<SectionResolvedObjects,String,Dictionary<String>> {
	private String canonicalFormFeature = DefaultNames.getCanonicalFormFeature();
    private SourceStream lemmaFile = null;
    private SourceStream synonymsFile = null;
    private SourceStream quasiSynonymsFile = null;
    private SourceStream acronymsFile = null;
    private SourceStream mergeFile = null;
    private SourceStream typographicVariationsFile = null;
    private TargetStream saveDictFile = null;
	
	@Override
	protected SectionResolvedObjects createResolvedObjects(ProcessingContext<Corpus> ctx) throws ResolverException {
		return new SectionResolvedObjects(ctx, this);
	}

	@Override
	protected Dictionary<String> newDictionary(State<String> root, CharFilter charFilter, CharMapper charMapper) {
		return new Dictionary<String>(new AllValuesState<String>(), charFilter, charMapper);
	}

	@Override
	protected void fillDictionary(ProcessingContext<Corpus> ctx, Corpus corpus, Dictionary<String> dict) throws Exception {
		Logger logger = getLogger(ctx);
		logger.info("reading lemma");
		LemmaLines lemmaLines = new LemmaLines(ctx);
		BufferedReader r = lemmaFile.getBufferedReader();
		lemmaLines.process(r, 0);
		r.close();
		logger.fine(Integer.toString(lemmaLines.lemmaToTerm.size()) + " entries");
		synonymLines.setLogger(logger);
		logger.info("reading synonyms");
		r = synonymsFile.getBufferedReader();
		synonymLines.process(r, lemmaLines.lemmaToTerm);
		r.close();
		logger.fine(Integer.toString(lemmaLines.lemmaToTerm.size()) + " entries");
		logger.info("reading quasi-synonyms");
		r = quasiSynonymsFile.getBufferedReader();
		synonymLines.process(r, lemmaLines.lemmaToTerm);
		r.close();
		logger.fine(Integer.toString(lemmaLines.lemmaToTerm.size()) + " entries");
		logger.info("saturating merged");
		saturateMerged(ctx, lemmaLines.lemmaToTerm);
		logger.fine(Integer.toString(lemmaLines.lemmaToTerm.size()) + " entries");
		logger.info("reading typographic variants");
        EquivalenceSets<String> variants = new EquivalenceHashSets<String>();
        loadEquivalenceFile(ctx, typographicVariationsFile, variants);
		if (acronymsFile != null) {
			logger.info("reading acronyms");
			loadEquivalenceFile(ctx, acronymsFile, variants);
		}
		Dictionary<Set<String>> variantsDict = new Dictionary<Set<String>>(new AllValuesState<Set<String>>(), Filters.ACCEPT_ALL, Mappers.IDENTITY);
		variantsDict.addEntries(variants.getMap());
		Matcher<Set<String>> variantMatcher = new Matcher<Set<String>>(variantsDict, Filters.START_WORD, Filters.END_WORD);
		logger.info("saturating with typographic variants");
		Map<String,String> map = new HashMap<String,String>();
		for (Map.Entry<String,String> e : lemmaLines.lemmaToTerm.entrySet()) {
			String lemma = e.getKey();
			List<Match<Set<String>>> matches = searchVariants(variantMatcher, lemma);
			if (matches.isEmpty()) {
				map.put(lemma, e.getValue());
				continue;
			}
			StringBuilder variant = new StringBuilder();
			List<String> suffixes = new ArrayList<String>(matches.size());
			List<Collection<String>> variations = new ArrayList<Collection<String>>(matches.size());
			int lastPos = 0;
			String lastVariation = null;
			for (int i = 0; i < matches.size(); ++i) {
				Match<Set<String>> w = matches.get(i);
				String currentVariation = lemma.substring(w.getStart(), w.getEnd());
				if (i == 0) {
					variant.append(lemma.substring(0, w.getStart()));
					lastPos = w.getEnd();
				}
				else {
					if (w.getStart() < lastPos) {
						logger.warning(String.format("overlapping variations: '%s' / '%s'", lastVariation, currentVariation));
						continue;
					}
					suffixes.add(lemma.substring(lastPos, w.getStart()));
					lastPos = w.getEnd();
				}
				lastVariation = currentVariation;
				variations.add(w.getState().getValues().iterator().next());
			}
			suffixes.add(lemma.substring(lastPos));
			int prefixLength = variant.length();
			CartesianProduct<String> cp = new CartesianProduct<String>(variations);
			List<String> v = cp.getElements();
			String canonical = e.getValue();
			while (cp.next()) {
				variant.setLength(prefixLength);
				for (int i = 0; i < v.size(); ++i) {
					variant.append(v.get(i));
					variant.append(suffixes.get(i));
				}
				String sv = variant.toString();
				if (lemmaLines.lemmaToTerm.containsKey(sv)) {
					String cv = lemmaLines.lemmaToTerm.get(sv);
					if (!cv.equals(canonical)) {
						logger.warning(String.format("%s has canonical %s, but variant %s has canonical %s", lemma, canonical, sv, cv));
					}
				}
				map.put(sv, canonical);
			}
		}
		logger.info(String.format("%d entries", map.size()));
		/*if (commaKludge) {
                logger.info("kludging commas");
                Map<String,String[]> toAdd = new HashMap<String,String[]>();
                for (Map.Entry<String,String[]> e : map.entrySet()) {
                    String form = e.getKey();
                    if (form.indexOf(',') == -1)
                        continue;
                    String commaFreeForm = form.replace(",", "");
                    String[] entry = e.getValue();
                    String[] commaFreeEntry = Arrays.copyOf(entry, entry.length);
                    commaFreeEntry[0] = commaFreeForm;
                    toAdd.put(commaFreeForm, commaFreeEntry);
                }
                map.putAll(toAdd);
            }*/
		logger.info(String.format("%d entries", map.size()));
		for (Map.Entry<String,String> e : map.entrySet())
			dict.addEntry(e.getKey(), e.getValue());
		if (saveDictFile != null) {
			logger.info("saving developped terminology into " + saveDictFile.getName());
			PrintStream ps = saveDictFile.getPrintStream();
			for (Map.Entry<String,String> e : map.entrySet()) {
				ps.print(e.getKey());
				ps.print('\t');
				ps.println(e.getValue());
			}
			ps.close();
		}
	}

	@Override
	protected void handleEntryValues(ProcessingContext<Corpus> ctx, Dictionary<String> dict, Annotation a, String entry) {
		a.addFeature(canonicalFormFeature, entry);
	}
	
    private void saturateMerged(ProcessingContext<Corpus> ctx, Map<String,String> map) throws FileNotFoundException, UnsupportedEncodingException, IOException, InvalidFileLineEntry {
        EquivalenceSets<String> merged = loadEquivalenceFile(ctx, mergeFile, null);
        Map<String,String> toAdd = new HashMap<String,String>();
        for (Map.Entry<String,String> e : map.entrySet()) {
            String lemma = e.getKey();
            if (merged.getMap().containsKey(lemma)) {
                String term = e.getValue();
                for (String m : merged.getMap().get(lemma))
                    toAdd.put(m, term);
            }
        }
        map.putAll(toAdd);
    }
    
    private static final TabularFormat equivalenceSetsTabularFormat = new TabularFormat();
    static {
    	equivalenceSetsTabularFormat.setMinColumns(2);
    	equivalenceSetsTabularFormat.setMaxColumns(2);
    	equivalenceSetsTabularFormat.setSkipBlank(true);
    	equivalenceSetsTabularFormat.setSkipEmpty(true);
    }
    
    private EquivalenceSets<String> loadEquivalenceFile(ProcessingContext<Corpus> ctx, SourceStream source, EquivalenceSets<String> eqSets) throws FileNotFoundException, UnsupportedEncodingException, IOException, InvalidFileLineEntry {
        if (eqSets == null)
            eqSets = new EquivalenceHashSets<String>();
        EquivFileLines efl = new EquivFileLines(equivalenceSetsTabularFormat, getLogger(ctx));
        BufferedReader r = source.getBufferedReader();
        efl.process(r, eqSets);
        r.close();
        return eqSets;
    }
    
    private static final List<Match<Set<String>>> searchVariants(Matcher<Set<String>> variantMatcher, String lemma) {
    	variantMatcher.reset();
//      variantMatcher.getDictionary().match(variantMatcher, lemma);
    	variantMatcher.match(lemma);
        variantMatcher.endMatches();
        return variantMatcher.getMatches();
    }

    private class LemmaLines extends FileLines<Integer> {

        /** The lemma to term. */
        private final Map<String,String> lemmaToTerm = new HashMap<String,String>();

        /** The term to lemma. */
        private final Map<String,String> termToLemma = new HashMap<String,String>();

        /**
         * Instantiates a new lemma lines.
         * @param ctx 
         * 
         * @throws FileNotFoundException
         *             the file not found exception
         * @throws UnsupportedEncodingException
         *             the unsupported encoding exception
         * @throws IOException
         *             Signals that an I/O exception has occurred.
         */
        private LemmaLines(ProcessingContext<Corpus> ctx) {
            super(TyDIProjector.this.getLogger(ctx));
            getFormat().setNumColumns(2);
        }

        /*
         * (non-Javadoc)
         * 
         * @see org.bibliome.filelines.FileLines#processArray(int,
         * java.lang.String[])
         */
        @Override
        public void processEntry(Integer foo, int lineno, List<String> entry) {
            String lemma = entry.get(0);
            String term = entry.get(1);

            if (lemmaToTerm.containsKey(lemma)) {
                String prevTerm = lemmaToTerm.get(lemma);
                if (!prevTerm.equals(term)) {
                    getLogger().warning(String.format("ambiguous lemma %s: %s / %s", lemma, prevTerm, term));
                }
            }
            lemmaToTerm.put(lemma, term);

            if (termToLemma.containsKey(term)) {
                String prevLemma = termToLemma.get(term);
                if (!prevLemma.equals(lemma)) {
                    getLogger().warning(String.format("several lemmas for %s: %s / %s", term, prevLemma, lemma));
                }
            }
            termToLemma.put(term, lemma);
        }
    }
    
    private final FileLines<Map<String,String>> synonymLines = new FileLines<Map<String,String>>() {
        @Override
        public void processEntry(Map<String,String> lemmaToTerm, int lineno, List<String> entry) {
            String lemma = entry.get(0);
            String canonical = entry.get(1);
            lemmaToTerm.put(lemma, canonical);
        }
    };

    @Param(nameType=NameType.FEATURE)
	public String getCanonicalFormFeature() {
		return canonicalFormFeature;
	}

    @Param
	public SourceStream getLemmaFile() {
		return lemmaFile;
	}

    @Param
	public SourceStream getSynonymsFile() {
		return synonymsFile;
	}

    @Param
	public SourceStream getQuasiSynonymsFile() {
		return quasiSynonymsFile;
	}

    @Param(mandatory = false)
	public SourceStream getAcronymsFile() {
		return acronymsFile;
	}

    @Param
	public SourceStream getMergeFile() {
		return mergeFile;
	}

    @Param(mandatory = false)
	public SourceStream getTypographicVariationsFile() {
		return typographicVariationsFile;
	}

    @Param(mandatory = false)
	public TargetStream getSaveDictFile() {
		return saveDictFile;
	}

	public void setSaveDictFile(TargetStream saveDictFile) {
		this.saveDictFile = saveDictFile;
	}

	public void setCanonicalFormFeature(String canonicalFormFeature) {
		this.canonicalFormFeature = canonicalFormFeature;
	}

	public void setLemmaFile(SourceStream lemmaFile) {
		this.lemmaFile = lemmaFile;
	}

	public void setSynonymsFile(SourceStream synonymsFile) {
		this.synonymsFile = synonymsFile;
	}

	public void setQuasiSynonymsFile(SourceStream quasiSynonymsFile) {
		this.quasiSynonymsFile = quasiSynonymsFile;
	}

	public void setAcronymsFile(SourceStream acronymsFile) {
		this.acronymsFile = acronymsFile;
	}

	public void setMergeFile(SourceStream mergeFile) {
		this.mergeFile = mergeFile;
	}

	public void setTypographicVariationsFile(SourceStream typographicVariationsFile) {
		this.typographicVariationsFile = typographicVariationsFile;
	}

	@Override
	protected String[] addFeaturesToSectionFilter() {
		return null;
	}

	@Override
	protected String[] addLayersToSectionFilter() {
		return null;
	}
}
