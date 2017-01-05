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

import java.io.IOException;

import org.bibliome.util.Timer;
import org.bibliome.util.obo.OBOUtils;
import org.obo.dataadapter.OBOParseException;
import org.obo.datamodel.Link;
import org.obo.datamodel.LinkedObject;
import org.obo.datamodel.OBOClass;
import org.obo.datamodel.OBOProperty;
import org.obo.datamodel.OBOSession;
import org.obo.datamodel.Synonym;
import org.obo.util.TermUtil;

import alvisnlp.corpus.Corpus;
import alvisnlp.corpus.Document;
import alvisnlp.corpus.NameType;
import alvisnlp.corpus.Section;
import alvisnlp.corpus.creators.DocumentCreator;
import alvisnlp.corpus.creators.SectionCreator;
import alvisnlp.corpus.expressions.ResolverException;
import alvisnlp.module.ModuleException;
import alvisnlp.module.ProcessingContext;
import alvisnlp.module.TimerCategory;
import alvisnlp.module.lib.AlvisNLPModule;
import alvisnlp.module.lib.Param;

@AlvisNLPModule
public abstract class OBOReader extends CorpusModule<ResolvedObjects> implements DocumentCreator, SectionCreator {
	private String[] oboFiles;
	private String nameSectionName = "name";
	private String synonymSectionName = "synonym";
	private String pathFeature = "path";
	private String parentFeature = "is_a";
	private String ancestorsFeature = null;
	private String childrenFeature = null;
	private String idPrefix = "";
	private Boolean excludeOBOBuiltins = true;

	@Override
	protected ResolvedObjects createResolvedObjects(ProcessingContext<Corpus> ctx) throws ResolverException {
		return new ResolvedObjects(ctx, this);
	}

	@Override
	public void process(ProcessingContext<Corpus> ctx, Corpus corpus) throws ModuleException {
		try {
			Timer<TimerCategory> parseTimer = getTimer(ctx, "read-obo", TimerCategory.LOAD_RESOURCE, true);
			OBOSession session = OBOUtils.parseOBO(oboFiles);
			parseTimer.stop();
			for (OBOClass term : TermUtil.getTerms(session)) {
				if (excludeOBOBuiltins && (term.isBuiltIn()))
					continue;
				Document doc = Document.getDocument(this, corpus, idPrefix + term.getID());
				for (Link link : term.getParents()) {
					if (link.getType().equals(OBOProperty.IS_A)) {
						doc.addFeature(parentFeature, link.getParent().getID());
					}
				}
				if (childrenFeature != null) {
					for (Link link : term.getChildren()) {
						if (link.getType().equals(OBOProperty.IS_A)) {
							doc.addFeature(childrenFeature	, link.getChild().getID());
						}
					}
				}
				for (StringBuilder path : OBOUtils.getPaths(term))
					doc.addFeature(pathFeature, path.toString());
				if (ancestorsFeature != null) {
					for (LinkedObject anc : OBOUtils.getAncestors(term, true)) {
						doc.addFeature(ancestorsFeature, anc.getID());
					}
				}
				new Section(this, doc, nameSectionName, term.getName());
				for (Synonym syn : term.getSynonyms()) {
					new Section(this, doc, synonymSectionName, syn.getText());
				}
			}
		}
		catch (OBOParseException|IOException e) {
			rethrow(e);
		}
	}

	@Param
	public String[] getOboFiles() {
		return oboFiles;
	}

	@Param(nameType=NameType.SECTION)
	public String getNameSectionName() {
		return nameSectionName;
	}

	@Param(nameType=NameType.SECTION)
	public String getSynonymSectionName() {
		return synonymSectionName;
	}

	@Param(nameType=NameType.FEATURE)
	public String getPathFeature() {
		return pathFeature;
	}

	@Param(nameType=NameType.FEATURE)
	public String getParentFeature() {
		return parentFeature;
	}

	@Param
	public Boolean getExcludeOBOBuiltins() {
		return excludeOBOBuiltins;
	}

	@Param
	public String getIdPrefix() {
		return idPrefix;
	}

	@Param(mandatory=false, nameType=NameType.FEATURE)
	public String getAncestorsFeature() {
		return ancestorsFeature;
	}

	@Param(mandatory=false, nameType=NameType.FEATURE)
	public String getChildrenFeature() {
		return childrenFeature;
	}

	public void setAncestorsFeature(String ancestorsFeature) {
		this.ancestorsFeature = ancestorsFeature;
	}

	public void setChildrenFeature(String childrenFeature) {
		this.childrenFeature = childrenFeature;
	}

	public void setIdPrefix(String idPrefix) {
		this.idPrefix = idPrefix;
	}

	public void setExcludeOBOBuiltins(Boolean excludeOBOBuiltins) {
		this.excludeOBOBuiltins = excludeOBOBuiltins;
	}

	public void setParentFeature(String parentFeature) {
		this.parentFeature = parentFeature;
	}

	public void setOboFiles(String[] oboFiles) {
		this.oboFiles = oboFiles;
	}

	public void setNameSectionName(String nameSectionName) {
		this.nameSectionName = nameSectionName;
	}

	public void setSynonymSectionName(String synonymSectionName) {
		this.synonymSectionName = synonymSectionName;
	}

	public void setPathFeature(String pathFeature) {
		this.pathFeature = pathFeature;
	}
}
