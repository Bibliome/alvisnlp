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


package fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules;

import java.io.IOException;

import org.obo.dataadapter.OBOParseException;
import org.obo.datamodel.Link;
import org.obo.datamodel.LinkedObject;
import org.obo.datamodel.OBOClass;
import org.obo.datamodel.OBOProperty;
import org.obo.datamodel.OBOSession;
import org.obo.datamodel.Synonym;
import org.obo.util.TermUtil;

import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Corpus;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Document;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.NameType;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Section;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.creators.DocumentCreator;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.creators.SectionCreator;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.ResolverException;
import fr.inra.maiage.bibliome.alvisnlp.core.module.ModuleException;
import fr.inra.maiage.bibliome.alvisnlp.core.module.ProcessingContext;
import fr.inra.maiage.bibliome.alvisnlp.core.module.ProcessingException;
import fr.inra.maiage.bibliome.alvisnlp.core.module.TimerCategory;
import fr.inra.maiage.bibliome.alvisnlp.core.module.lib.AlvisNLPModule;
import fr.inra.maiage.bibliome.alvisnlp.core.module.lib.Param;
import fr.inra.maiage.bibliome.util.Timer;
import fr.inra.maiage.bibliome.util.files.InputFile;
import fr.inra.maiage.bibliome.util.obo.OBOUtils;

@AlvisNLPModule
public abstract class OBOReader extends CorpusModule<ResolvedObjects> implements DocumentCreator, SectionCreator {
	private InputFile[] oboFiles;
	private String nameSection = "name";
	private String synonymSection = "synonym";
	private String pathFeature = "path";
	private String parentFeature = "is_a";
	private String ancestorsFeature = null;
	private String childrenFeature = null;
	private String idPrefix = "";
	private Boolean excludeOBOBuiltins = true;

	@Override
	protected ResolvedObjects createResolvedObjects(ProcessingContext ctx) throws ResolverException {
		return new ResolvedObjects(ctx, this);
	}

	@Override
	public void process(ProcessingContext ctx, Corpus corpus) throws ModuleException {
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
				new Section(this, doc, nameSection, term.getName());
				for (Synonym syn : term.getSynonyms()) {
					new Section(this, doc, synonymSection, syn.getText());
				}
			}
		}
		catch (OBOParseException|IOException e) {
			throw new ProcessingException(e);
		}
	}

	@Param
	public InputFile[] getOboFiles() {
		return oboFiles;
	}

	@Deprecated
	@Param(nameType=NameType.SECTION)
	public String getNameSectionName() {
		return nameSection;
	}

	@Deprecated
	@Param(nameType=NameType.SECTION)
	public String getSynonymSectionName() {
		return synonymSection;
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

	@Param(nameType=NameType.SECTION)
	public String getNameSection() {
		return nameSection;
	}

	@Param(nameType=NameType.SECTION)
	public String getSynonymSection() {
		return synonymSection;
	}

	public void setNameSection(String nameSection) {
		this.nameSection = nameSection;
	}

	public void setSynonymSection(String synonymSection) {
		this.synonymSection = synonymSection;
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

	public void setOboFiles(InputFile[] oboFiles) {
		this.oboFiles = oboFiles;
	}

	public void setNameSectionName(String nameSectionName) {
		this.nameSection = nameSectionName;
	}

	public void setSynonymSectionName(String synonymSectionName) {
		this.synonymSection = synonymSectionName;
	}

	public void setPathFeature(String pathFeature) {
		this.pathFeature = pathFeature;
	}
}
