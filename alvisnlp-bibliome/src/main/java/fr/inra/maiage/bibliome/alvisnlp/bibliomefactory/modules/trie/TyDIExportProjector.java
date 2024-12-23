package fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.trie;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.SectionModule.SectionResolvedObjects;

import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Annotation;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Corpus;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.DefaultNames;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.NameType;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.ResolverException;
import fr.inra.maiage.bibliome.alvisnlp.core.module.ModuleException;
import fr.inra.maiage.bibliome.alvisnlp.core.module.ProcessingContext;
import fr.inra.maiage.bibliome.alvisnlp.core.module.TimerCategory;
import fr.inra.maiage.bibliome.alvisnlp.core.module.lib.AlvisNLPModule;
import fr.inra.maiage.bibliome.alvisnlp.core.module.lib.Param;
import fr.inra.maiage.bibliome.alvisnlp.core.module.lib.TimeThis;
import fr.inra.maiage.bibliome.util.CartesianProduct;
import fr.inra.maiage.bibliome.util.EquivalenceHashSets;
import fr.inra.maiage.bibliome.util.EquivalenceSets;
import fr.inra.maiage.bibliome.util.filelines.EquivFileLines;
import fr.inra.maiage.bibliome.util.filelines.FileLines;
import fr.inra.maiage.bibliome.util.filelines.InvalidFileLineEntry;
import fr.inra.maiage.bibliome.util.filelines.TabularFormat;
import fr.inra.maiage.bibliome.util.marshall.Decoder;
import fr.inra.maiage.bibliome.util.marshall.Encoder;
import fr.inra.maiage.bibliome.util.newprojector.Dictionary;
import fr.inra.maiage.bibliome.util.newprojector.Match;
import fr.inra.maiage.bibliome.util.newprojector.Matcher;
import fr.inra.maiage.bibliome.util.newprojector.chars.Filters;
import fr.inra.maiage.bibliome.util.newprojector.chars.Mappers;
import fr.inra.maiage.bibliome.util.newprojector.states.AllValuesState;
import fr.inra.maiage.bibliome.util.streams.SourceStream;
import fr.inra.maiage.bibliome.util.streams.TargetStream;
import fr.inra.maiage.bibliome.util.trie.Trie;

@AlvisNLPModule
public abstract class TyDIExportProjector extends TrieProjector<SectionResolvedObjects,String> {
	private String canonicalFormFeature = DefaultNames.getCanonicalFormFeature();
    private SourceStream lemmaFile = null;
    private SourceStream synonymsFile = null;
    private SourceStream quasiSynonymsFile = null;
    private SourceStream acronymsFile = null;
    private SourceStream mergeFile = null;
    private SourceStream typographicVariationsFile = null;
    private TargetStream saveDictFile = null;

	@Override
	protected void fillTrie(Logger logger, Trie<String> trie, Corpus corpus) throws IOException, ModuleException {
		logger.info("reading lemma");
		LemmaLines lemmaLines = new LemmaLines(logger);
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
		saturateMerged(logger, lemmaLines.lemmaToTerm);
		logger.fine(Integer.toString(lemmaLines.lemmaToTerm.size()) + " entries");
		logger.info("reading typographic variants");
        EquivalenceSets<String> variants = new EquivalenceHashSets<String>();
        loadEquivalenceFile(logger, typographicVariationsFile, variants);
		if (acronymsFile != null) {
			logger.info("reading acronyms");
			loadEquivalenceFile(logger, acronymsFile, variants);
		}
		Dictionary<Set<String>> variantsDict = new Dictionary<Set<String>>(new AllValuesState<Set<String>>(), Filters.ACCEPT_ALL, Mappers.IDENTITY);
		variantsDict.addEntries(variants.getMap());
		Matcher<Set<String>> variantMatcher = new Matcher<Set<String>>(variantsDict, Filters.START_WORD, Filters.END_WORD);
		logger.info("saturating with typographic variants");
		Map<String,String> map = new LinkedHashMap<String,String>();
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
                Map<String,String[]> toAdd = new LinkedHashMap<String,String[]>();
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
			trie.addEntry(e.getKey(), e.getValue());
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
	protected void finish() {
	}

	@Override
	@TimeThis(task="create-trie", category=TimerCategory.LOAD_RESOURCE)
	protected Trie<String> getTrie(ProcessingContext ctx, Logger logger, Corpus corpus) throws IOException, ModuleException {
		return super.getTrie(ctx, logger, corpus);
	}

	@Override
	protected boolean marshallingSupported() {
		return false;
	}

	@Override
	protected Decoder<String> getDecoder() {
		throw new UnsupportedOperationException("marshalling not supported");
	}

	@Override
	protected Encoder<String> getEncoder() {
		throw new UnsupportedOperationException("marshalling not supported");
	}

	@Override
	protected void handleMatch(String entry, Annotation a) {
		a.addFeature(canonicalFormFeature, entry);
	}

	@Override
	protected SectionResolvedObjects createResolvedObjects(ProcessingContext ctx) throws ResolverException {
		return new SectionResolvedObjects(ctx, this);
	}

    private void saturateMerged(Logger logger, Map<String,String> map) throws FileNotFoundException, UnsupportedEncodingException, IOException, InvalidFileLineEntry {
        EquivalenceSets<String> merged = loadEquivalenceFile(logger, mergeFile, null);
        Map<String,String> toAdd = new LinkedHashMap<String,String>();
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

    private static EquivalenceSets<String> loadEquivalenceFile(Logger logger, SourceStream source, EquivalenceSets<String> eqSets) throws FileNotFoundException, UnsupportedEncodingException, IOException, InvalidFileLineEntry {
        if (eqSets == null)
            eqSets = new EquivalenceHashSets<String>();
        EquivFileLines efl = new EquivFileLines(equivalenceSetsTabularFormat, logger);
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
        private final Map<String,String> lemmaToTerm = new LinkedHashMap<String,String>();

        /** The term to lemma. */
        private final Map<String,String> termToLemma = new LinkedHashMap<String,String>();

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
        private LemmaLines(Logger logger) {
            super(logger);
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
}
