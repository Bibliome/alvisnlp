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


package org.bibliome.alvisnlp.modules.mapper;

import java.io.IOException;
import java.util.List;

import org.bibliome.alvisnlp.modules.mapper.Mapper2.MapperResolvedObjects;
import org.bibliome.util.defaultmap.DefaultMap;
import org.bibliome.util.obo.OBOUtils;
import org.obo.dataadapter.OBOParseException;
import org.obo.datamodel.Dbxref;
import org.obo.datamodel.Link;
import org.obo.datamodel.LinkedObject;
import org.obo.datamodel.OBOClass;
import org.obo.datamodel.OBOProperty;
import org.obo.datamodel.OBOSession;
import org.obo.datamodel.Synonym;
import org.obo.util.TermUtil;

import alvisnlp.corpus.Corpus;
import alvisnlp.corpus.Element;
import alvisnlp.corpus.NameType;
import alvisnlp.corpus.expressions.ResolverException;
import alvisnlp.module.ProcessingContext;
import alvisnlp.module.ProcessingException;
import alvisnlp.module.lib.AlvisNLPModule;
import alvisnlp.module.lib.Param;

@AlvisNLPModule
public class OBOMapper extends Mapper2<MapperResolvedObjects,OBOClass> {
	private String[] oboFiles;
	private String nameFeature;
	private String idFeature;
	private String pathFeature;
	private String parentsFeature;
	private String childrenFeature;
	private String ancestorsFeature;
	private String versionFeature;
	private Boolean keepDBXref = false;
	private Boolean idKeys = false;
	private String ontologyVersion;

    @Override
	protected MapperResolvedObjects createResolvedObjects(ProcessingContext<Corpus> ctx) throws ResolverException {
		return new MapperResolvedObjects(ctx, this);
	}

	@Override
	public void fillMapping(DefaultMap<String,List<OBOClass>> mapping, ProcessingContext<Corpus> ctx, Corpus corpus) throws ProcessingException {
		try {
			OBOSession session = OBOUtils.parseOBO(oboFiles);
			if (versionFeature != null) {
				ontologyVersion = session.getCurrentHistory().getVersion();
			}
			for (OBOClass term : TermUtil.getTerms(session)) {
				if (idKeys) {
					addEntry(mapping, term.getID(), term);
				}
				else {
					addEntry(mapping, term.getName(), term);
					for (Synonym syn : term.getSynonyms())
						addEntry(mapping, syn.getText(), term);
				}
			}
		}
		catch (OBOParseException|IOException e) {
			rethrow(e);
		}
	}
	
	private void addEntry(DefaultMap<String,List<OBOClass>> mapping, String key, OBOClass term) {
		if (getIgnoreCase()) {
			key = key.toLowerCase();
		}
		List<OBOClass> value = mapping.safeGet(key);
		value.add(term);
	}
	
	@Override
	protected void handleMatch(Element target, OBOClass value) {
		if (nameFeature != null)
			target.addFeature(nameFeature, value.getName());
		if (idFeature != null)
			target.addFeature(idFeature, value.getID());
		if (pathFeature != null) {
			for (StringBuilder path : OBOUtils.getPaths(value)) {
				target.addFeature(pathFeature, path.toString());
			}
		}
		if (parentsFeature != null) {
			for (Link link : value.getParents()) {
				if (link.getType().equals(OBOProperty.IS_A)) {
					target.addFeature(parentsFeature, link.getParent().getID());
				}
			}
		}
		if (childrenFeature != null) {
			for (Link link : value.getChildren()) {
				if (link.getType().equals(OBOProperty.IS_A)) {
					target.addFeature(childrenFeature	, link.getChild().getID());
				}
			}
		}
		if (ancestorsFeature != null) {
			for (LinkedObject anc : OBOUtils.getAncestors(value, true)) {
				target.addFeature(ancestorsFeature, anc.getID());
			}
		}
		if (versionFeature != null) {
			target.addFeature(versionFeature, ontologyVersion);
		}
		if (keepDBXref) {
			for (Dbxref dbxref : value.getDbxrefs()) {
				String db = dbxref.getDatabase();
				String id = dbxref.getDatabaseID();
				target.addFeature(db, id);
			}
		}
	}

	@Param
	public String[] getOboFiles() {
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

	@Param
	public Boolean getIdKeys() {
		return idKeys;
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

	public void setIdKeys(Boolean idKeys) {
		this.idKeys = idKeys;
	}

	public void setOboFiles(String[] oboFiles) {
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

	public void setVersionFeature(String versionFeature) {
		this.versionFeature = versionFeature;
	}

	public void setKeepDBXref(Boolean keepDBXref) {
		this.keepDBXref = keepDBXref;
	}
}
