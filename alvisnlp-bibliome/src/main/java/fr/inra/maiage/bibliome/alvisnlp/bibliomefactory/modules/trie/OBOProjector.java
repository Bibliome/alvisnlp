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


package fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.trie;

import java.io.IOException;
import java.util.logging.Logger;

import org.obo.dataadapter.OBOParseException;
import org.obo.datamodel.Dbxref;
import org.obo.datamodel.Link;
import org.obo.datamodel.LinkedObject;
import org.obo.datamodel.OBOClass;
import org.obo.datamodel.OBOProperty;
import org.obo.datamodel.OBOSession;
import org.obo.datamodel.Synonym;
import org.obo.util.TermUtil;

import fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.SectionModule.SectionResolvedObjects;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Annotation;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Corpus;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.NameType;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.ResolverException;
import fr.inra.maiage.bibliome.alvisnlp.core.module.ModuleException;
import fr.inra.maiage.bibliome.alvisnlp.core.module.ProcessingContext;
import fr.inra.maiage.bibliome.alvisnlp.core.module.ProcessingException;
import fr.inra.maiage.bibliome.alvisnlp.core.module.TimerCategory;
import fr.inra.maiage.bibliome.alvisnlp.core.module.lib.AlvisNLPModule;
import fr.inra.maiage.bibliome.alvisnlp.core.module.lib.Param;
import fr.inra.maiage.bibliome.alvisnlp.core.module.lib.TimeThis;
import fr.inra.maiage.bibliome.util.files.InputFile;
import fr.inra.maiage.bibliome.util.marshall.Decoder;
import fr.inra.maiage.bibliome.util.marshall.Encoder;
import fr.inra.maiage.bibliome.util.obo.OBOUtils;
import fr.inra.maiage.bibliome.util.trie.Trie;

@AlvisNLPModule
public abstract class OBOProjector extends TrieProjector<SectionResolvedObjects,OBOClass> {
	private InputFile[] oboFiles;
	private String nameFeature;
	private String idFeature;
	private String pathFeature;
	private String parentsFeature;
	private String childrenFeature;
	private String ancestorsFeature;
	private String versionFeature;
	private Boolean keepDBXref = false;
	
	private String ontologyVersion;

	@Override
	protected SectionResolvedObjects createResolvedObjects(ProcessingContext<Corpus> ctx) throws ResolverException {
		return new SectionResolvedObjects(ctx, this);
	}

	@Override
	protected void fillTrie(Logger logger, Trie<OBOClass> trie, Corpus corpus) throws IOException, ModuleException {
		try {
			OBOSession session = OBOUtils.parseOBO(oboFiles);
			if (versionFeature != null) {
				ontologyVersion = session.getCurrentHistory().getVersion();
			}
			for (OBOClass term : TermUtil.getTerms(session)) {
				trie.addEntry(term.getName(), term);
				for (Synonym syn : term.getSynonyms())
					trie.addEntry(syn.getText(), term);
			}
		}
		catch (OBOParseException e) {
			throw new ProcessingException(e);
		}
	}

	@Override
	protected void finish() {
	}

	@Override
	protected boolean marshallingSupported() {
		return false;
	}

	@Override
	protected Decoder<OBOClass> getDecoder() {
		throw new UnsupportedOperationException();
	}

	@Override
	protected Encoder<OBOClass> getEncoder() {
		throw new UnsupportedOperationException();
	}

	@Override
	@TimeThis(task="create-trie", category=TimerCategory.LOAD_RESOURCE)
	protected Trie<OBOClass> getTrie(ProcessingContext<Corpus> ctx, Logger logger, Corpus corpus) throws IOException, ModuleException {
		return super.getTrie(ctx, logger, corpus);
	}

	@Override
	protected void handleMatch(OBOClass value, Annotation a) {
		if (nameFeature != null)
			a.addFeature(nameFeature, value.getName());
		if (idFeature != null)
			a.addFeature(idFeature, value.getID());
		if (pathFeature != null) {
			for (StringBuilder path : OBOUtils.getPaths(value)) {
				a.addFeature(pathFeature, path.toString());
			}
		}
		if (parentsFeature != null) {
			for (Link link : value.getParents()) {
				if (link.getType().equals(OBOProperty.IS_A)) {
					a.addFeature(parentsFeature, link.getParent().getID());
				}
			}
		}
		if (childrenFeature != null) {
			for (Link link : value.getChildren()) {
				if (link.getType().equals(OBOProperty.IS_A)) {
					a.addFeature(childrenFeature	, link.getChild().getID());
				}
			}
		}
		if (ancestorsFeature != null) {
			for (LinkedObject anc : OBOUtils.getAncestors(value, true)) {
				a.addFeature(ancestorsFeature, anc.getID());
			}
		}
		if (versionFeature != null) {
			a.addFeature(versionFeature, ontologyVersion);
		}
		if (keepDBXref) {
			for (Dbxref dbxref : value.getDbxrefs()) {
				String db = dbxref.getDatabase();
				String id = dbxref.getDatabaseID();
				a.addFeature(db, id);
			}
		}
	}

	@Param
	public InputFile[] getOboFiles() {
		return oboFiles;
	}

	@Param(nameType=NameType.FEATURE, mandatory=false)
	public String getNameFeature() {
		return nameFeature;
	}

	@Param(nameType=NameType.FEATURE, mandatory=false)
	public String getIdFeature() {
		return idFeature;
	}

	@Param(nameType=NameType.FEATURE, mandatory=false)
	public String getPathFeature() {
		return pathFeature;
	}

	@Param(nameType=NameType.FEATURE, mandatory=false)
	public String getVersionFeature() {
		return versionFeature;
	}

	@Param
	public Boolean getKeepDBXref() {
		return keepDBXref;
	}

	@Param(nameType=NameType.FEATURE, mandatory=false)
	public String getParentsFeature() {
		return parentsFeature;
	}

	@Param(nameType=NameType.FEATURE, mandatory=false)
	public String getChildrenFeature() {
		return childrenFeature;
	}

	@Param(nameType=NameType.FEATURE, mandatory=false)
	public String getAncestorsFeature() {
		return ancestorsFeature;
	}

	public void setParentsFeature(String parentFeature) {
		this.parentsFeature = parentFeature;
	}

	public void setChildrenFeature(String childrenFeature) {
		this.childrenFeature = childrenFeature;
	}

	public void setAncestorsFeature(String ancestorsFeature) {
		this.ancestorsFeature = ancestorsFeature;
	}

	public void setKeepDBXref(Boolean keepDBXref) {
		this.keepDBXref = keepDBXref;
	}

	public void setVersionFeature(String versionFeature) {
		this.versionFeature = versionFeature;
	}

	public void setOboFiles(InputFile[] oboFiles) {
		this.oboFiles = oboFiles;
	}

	public void setNameFeature(String nameFeature) {
		this.nameFeature = nameFeature;
	}

	public void setIdFeature(String idFeature) {
		this.idFeature = idFeature;
	}

	public void setPathFeature(String pathFeature) {
		this.pathFeature = pathFeature;
	}
}
