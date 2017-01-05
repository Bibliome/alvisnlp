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
import java.io.IOException;
import java.util.List;

import org.bibliome.alvisnlp.modules.SectionModule.SectionResolvedObjects;
import org.bibliome.util.StringCat;
import org.bibliome.util.filelines.FileLines;
import org.bibliome.util.filelines.InvalidFileLineEntry;
import org.bibliome.util.newprojector.CharFilter;
import org.bibliome.util.newprojector.CharMapper;
import org.bibliome.util.newprojector.Dictionary;
import org.bibliome.util.newprojector.State;
import org.bibliome.util.newprojector.states.AllValuesState;
import org.bibliome.util.streams.SourceStream;

import alvisnlp.corpus.Annotation;
import alvisnlp.corpus.Corpus;
import alvisnlp.corpus.DefaultNames;
import alvisnlp.corpus.NameType;
import alvisnlp.corpus.expressions.ResolverException;
import alvisnlp.module.ProcessingContext;
import alvisnlp.module.lib.AlvisNLPModule;
import alvisnlp.module.lib.Param;

@AlvisNLPModule
public abstract class AttestedTermsProjector extends Projector<SectionResolvedObjects,String[],Dictionary<String[]>> {
	private String termFeatureName = null;
	private String posFeatureName = DefaultNames.getPosTagFeature();
	private String lemmaFeatureName = DefaultNames.getCanonicalFormFeature();
	private Boolean lemmaKeys = true;
	private SourceStream termsFile = null;
	
	@Override
	protected SectionResolvedObjects createResolvedObjects(ProcessingContext<Corpus> ctx) throws ResolverException {
		return new SectionResolvedObjects(ctx, this);
	}

	@Override
	protected Dictionary<String[]> newDictionary(State<String[]> root, CharFilter charFilter, CharMapper charMapper) {
		return new Dictionary<String[]>(new AllValuesState<String[]>(), charFilter, charMapper);
	}

	@Override
	protected void fillDictionary(ProcessingContext<Corpus> ctx, Corpus corpus, Dictionary<String[]> dict) throws IOException, InvalidFileLineEntry {
		AttestedTermsFileLines fileLines = new AttestedTermsFileLines(lemmaKeys);
        fileLines.setLogger(getLogger(ctx));
        fileLines.getFormat().setSeparator('\t');
        BufferedReader r = termsFile.getBufferedReader();
        fileLines.process(r, dict);
        r.close();
	}

	@Override
	protected void handleEntryValues(ProcessingContext<Corpus> ctx, Dictionary<String[]> dict, Annotation a, String[] entry) {
		if (termFeatureName != null)
			a.addFeature(termFeatureName, entry[0]);
		a.addFeature(posFeatureName, entry[1]);
		a.addFeature(lemmaFeatureName, entry[2]);
	}

	@Override
	protected String[] addFeaturesToSectionFilter() {
		return null;
	}

	@Override
	protected String[] addLayersToSectionFilter() {
		return null;
	}
	
	private static final class AttestedTermsFileLines extends FileLines<Dictionary<String[]>> {
		private final StringCat termFormBuilder = new StringCat();
		private final StringCat termPosBuilder = new StringCat();
		private final StringCat termLemmaBuilder = new StringCat();
		private final boolean lemmaKeys;
		
		private AttestedTermsFileLines(boolean lemmaKeys) {
			super();
			this.lemmaKeys = lemmaKeys;
			getFormat().setNumColumns(3);
			getFormat().setStrictColumnNumber(true);
		}

		private void init() {
			termFormBuilder.clear();
			termPosBuilder.clear();
			termLemmaBuilder.clear();			
		}

		@Override
		public void processEntry(Dictionary<String[]> data, int lineno, List<String> entry) throws InvalidFileLineEntry {
			String pos = entry.get(1);
			if ("SENT".equals(pos)) {
				if (termFormBuilder.isEmpty())
					return;
				String termForm = termFormBuilder.toString();
				String termLemma = termLemmaBuilder.toString();
				data.addEntry(lemmaKeys ? termLemma : termForm, new String[] { termForm, termPosBuilder.toString(), termLemma });
				init();
				return;
			}
			if (!termFormBuilder.isEmpty()) {
				termFormBuilder.append(" ");
				termPosBuilder.append(" ");
				termLemmaBuilder.append(" ");
			}
			termFormBuilder.append(entry.get(0));
			termPosBuilder.append(pos);
			termLemmaBuilder.append(entry.get(2));
		}
	}

	@Param(nameType=NameType.FEATURE, defaultDoc = "Name of the feature where to write the term POS tags.")
	public String getPosFeatureName() {
		return posFeatureName;
	}

	@Param(nameType=NameType.FEATURE, defaultDoc = "Name of the feature where to write the term lemma.")
	public String getLemmaFeatureName() {
		return lemmaFeatureName;
	}

	@Param(defaultDoc = "Either to project lemmas instead of the forms.")
	public Boolean getLemmaKeys() {
		return lemmaKeys;
	}

	@Param
	public SourceStream getTermsFile() {
		return termsFile;
	}

	@Param(nameType=NameType.FEATURE, mandatory=false, defaultDoc = "Name of the feature where to write the term form.")
	public String getTermFeatureName() {
		return termFeatureName;
	}

	public void setTermFeatureName(String termFeatureName) {
		this.termFeatureName = termFeatureName;
	}

	public void setPosFeatureName(String posFeatureName) {
		this.posFeatureName = posFeatureName;
	}

	public void setLemmaFeatureName(String lemmaFeatureName) {
		this.lemmaFeatureName = lemmaFeatureName;
	}

	public void setLemmaKeys(Boolean lemmaKeys) {
		this.lemmaKeys = lemmaKeys;
	}

	public void setTermsFile(SourceStream termsFile) {
		this.termsFile = termsFile;
	}
}
