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


package fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.alvisae;

import java.io.BufferedReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Logger;

import org.json.simple.parser.ParseException;

import fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.CorpusModule;
import fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.ResolvedObjects;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Annotation;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Corpus;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.DefaultNames;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Document;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Layer;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.NameType;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Relation;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Section;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Tuple;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.creators.AnnotationCreator;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.creators.DocumentCreator;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.creators.SectionCreator;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.creators.TupleCreator;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.ResolverException;
import fr.inra.maiage.bibliome.alvisnlp.core.module.ModuleException;
import fr.inra.maiage.bibliome.alvisnlp.core.module.ProcessingContext;
import fr.inra.maiage.bibliome.alvisnlp.core.module.ProcessingException;
import fr.inra.maiage.bibliome.alvisnlp.core.module.TimerCategory;
import fr.inra.maiage.bibliome.alvisnlp.core.module.lib.AlvisNLPModule;
import fr.inra.maiage.bibliome.alvisnlp.core.module.lib.Param;
import fr.inra.maiage.bibliome.alvisnlp.core.module.lib.TimeThis;
import fr.inra.maiage.bibliome.util.Iterators;
import fr.inra.maiage.bibliome.util.alvisae.AlvisAEAnnotation;
import fr.inra.maiage.bibliome.util.alvisae.AlvisAEDocument;
import fr.inra.maiage.bibliome.util.alvisae.AnnotationSet;
import fr.inra.maiage.bibliome.util.alvisae.Campaign;
import fr.inra.maiage.bibliome.util.alvisae.Group;
import fr.inra.maiage.bibliome.util.alvisae.LoadOptions;
import fr.inra.maiage.bibliome.util.alvisae.SourceAnnotation;
import fr.inra.maiage.bibliome.util.alvisae.TextBound;
import fr.inra.maiage.bibliome.util.fragments.Fragment;
import fr.inra.maiage.bibliome.util.streams.SourceStream;

@AlvisNLPModule
public abstract class AlvisAEReader extends CorpusModule<ResolvedObjects> implements DocumentCreator, SectionCreator, TupleCreator, AnnotationCreator {
	private SourceStream databasePropsFile;
	private String url;
	private String schema;
	private String username;
	private String password;
	private Integer[] campaignId;
	private Integer[] docIds;
	private String[] docExternalIds;
	private String[] docDescriptions;
	private Boolean head = true;
	private Integer taskId;
	private String taskName;
	private Integer[] userIds;
	private String[] userNames;
	private Boolean loadTextBound = true;
	private Boolean loadGroups = true;
	private Boolean loadRelations = true;
	private String section = DefaultNames.getDefaultSectionName();
	private String fragmentsLayer = "alvisae";
	private String fragmentRolePrefix = "frag";
	private String itemRolePrefix = "item";
	private String campaignIdFeature;
	private String campaignNameFeature;
	private String userFeature;
	private String userIdFeature;
	private String taskFeature;
	private String taskIdFeature;
	private String typeFeature = "type";
	private String fragmentTypeFeature = "type";
	private Boolean oldModel = false;
	private String externalIdFeature = "external-id";
	private String descriptionFeature = "description";
	private String createdFeature = "created";
	private String kindFeature = "kind";
	private Boolean loadDependencies = false;
	private Boolean adjudicate = false;
	private String sourceRolePrefix = "source";
	private String annotationSetIdFeature = "annotation-set";
	private String annotationIdFeature = "id";
	private String referentFeature = "referent";
	private String unmatchedFeature = "unmatched";
	private String htmlLayer = "html";
	private String htmlTagFeature = "tag";

	@Override
	protected ResolvedObjects createResolvedObjects(ProcessingContext ctx) throws ResolverException {
		return new ResolvedObjects(ctx, this);
	}

	@Override
	public void process(ProcessingContext ctx, Corpus corpus) throws ModuleException {
		LoadOptions options = getLoadOptions();
		Properties props = new Properties();
		try (BufferedReader r = databasePropsFile.getBufferedReader()) {
			props.load(r);
		}
		catch (IOException e) {
			throw new ProcessingException(e);
		}
		String type = props.getProperty("db.type");
		String server = props.getProperty("db.server");
		String port = props.getProperty("db.port");
		String dbname = props.getProperty("db.dbname");
		String url = String.format("jdbc:%s://%s:%s/%s", type, server, port, dbname);
		String username = props.getProperty("db.username");
		String password = props.getProperty("db.password");
		String schema = props.getProperty("db.schema");
		try (Connection connection = openConnection(ctx, url, username, password)) {
			for (Integer cid : campaignId) {
				Campaign campaign = loadCampaign(ctx, options, connection, schema, cid);
				convertCorpus(ctx, corpus, campaign);
			}
		}
		catch (ClassNotFoundException|SQLException|ParseException e) {
			throw new ProcessingException(e);
		}
	}

	@TimeThis(task="load-sql", category=TimerCategory.LOAD_RESOURCE)
	protected Campaign loadCampaign(ProcessingContext ctx, LoadOptions options, Connection connection, String schema, int cid) throws SQLException, ParseException {
		Campaign campaign = new Campaign(oldModel, schema, cid);
		campaign.load(getLogger(ctx), connection, options);
		return campaign;
	}

	@TimeThis(task="open-connection")
	protected Connection openConnection(ProcessingContext ctx, String url, String username, String password) throws SQLException, ClassNotFoundException {
//		Class.forName("org.postgresql.Driver");
		Class.forName("org.postgresql.Driver", true, AlvisAEReader.class.getClassLoader());
		return DriverManager.getConnection(url, username, password);
	}

	private LoadOptions getLoadOptions() {
		LoadOptions result = new LoadOptions();
		result.setDocIds(asCollection(docIds));
		result.setDocExternalIds(asCollection(docExternalIds));
		result.setDocDescriptions(asCollection(docDescriptions));
		result.setHead(head);
		result.setTaskId(taskId);
		result.setTaskName(taskName);
		result.setUserIds(asCollection(userIds));
		result.setUserNames(asCollection(userNames));
		result.setLoadTextBound(loadTextBound);
		result.setLoadGroups(loadGroups);
		result.setLoadRelations(loadRelations);
		result.setLoadContents(true);
		result.setLoadDependencies(true);
		result.setAdjudicate(adjudicate);
		return result;
	}

	private static <T> Collection<T> asCollection(T[] a) {
		if (a == null)
			return null;
		return Arrays.asList(a);
	}

	@TimeThis(task="convert-documents", category=TimerCategory.COLLECT_DATA)
	protected void convertCorpus(ProcessingContext ctx, Corpus corpus, Campaign campaign) {
		Logger logger = getLogger(ctx);
		for (AlvisAEDocument doc : campaign.getDocuments()) {
			convertDocument(logger, corpus, doc);
		}
	}

	private void convertDocument(Logger logger, Corpus corpus, AlvisAEDocument doc) {
		Document aDoc = Document.getDocument(this, corpus, Integer.toString(doc.getId()));
		logger.finer("converting document " + doc.getId() + " [" + doc.getExternalId() + "] (" + doc.getDescription() + ")");
		aDoc.addFeature(externalIdFeature, doc.getExternalId());
		aDoc.addFeature(descriptionFeature, doc.getDescription());
		aDoc.addFeature(campaignIdFeature, Integer.toString(doc.getCampaign().getId()));
		aDoc.addFeature(campaignNameFeature, doc.getCampaign().getName());
		Section sec = getSection(doc, aDoc);
		Map<AlvisAEAnnotation,Tuple> mapping = new LinkedHashMap<AlvisAEAnnotation,Tuple>();
		for (AnnotationSet aset : doc.getAnnotationSets()) {
			if (loadDependencies || (taskId != null && taskId.equals(aset.getTaskId())) || (taskName != null && taskName.equals(aset.getTask()))) {
				convertAnnotationSet(mapping, aset, sec);
			}
		}
		Layer html = sec.ensureLayer(htmlLayer);
		for (TextBound tag : doc.getHTML()) {
			for (Fragment frag : tag.getFragments()) {
				Annotation a = new Annotation(this, html, frag.getStart(), frag.getEnd());
				a.addFeature(htmlTagFeature, tag.getType());
			}
		}
		if (loadDependencies) {
			for (AnnotationSet aset : doc.getAnnotationSets()) {
				for (SourceAnnotation unm : aset.getUnmatched()) {
					AlvisAEAnnotation unmA = unm.getAnnotation();
					Tuple unmT = mapping.get(unmA);
					unmT.addFeature(unmatchedFeature, unmatchedFeature);
				}
			}
		}
	}

	private void convertAnnotationSet(Map<AlvisAEAnnotation,Tuple> mapping, AnnotationSet aset, Section sec) {
		String created = aset.getCreated();
		String asetId = Integer.toString(aset.getId());
		String referent = Boolean.toString(aset.isReferent());
		for (AlvisAEAnnotation a : aset.getAnnotations()) {
			String type = a.getType();
			Relation rel = sec.ensureRelation(this, type);
			Tuple result = new Tuple(this, rel);
			mapping.put(a, result);
			for (String key : a.getPropertyKeys())
				for (Object value : a.getProperty(key))
					result.addFeature(key, value.toString());
			result.addFeature(annotationIdFeature, a.getId());
			result.addFeature(userFeature, aset.getUser());
			result.addFeature(userIdFeature, Integer.toString(aset.getUserId()));
			result.addFeature(taskFeature, aset.getTask());
			result.addFeature(taskIdFeature, Integer.toString(aset.getTaskId()));
			result.addFeature(createdFeature, created);
			result.addFeature(typeFeature, type);
			result.addFeature(annotationSetIdFeature, asetId);
			result.addFeature(referentFeature, referent);
		}
		if (loadDependencies) {
			for (Map.Entry<AlvisAEAnnotation,Tuple> e : mapping.entrySet()) {
				AlvisAEAnnotation a = e.getKey();
				Tuple t = e.getValue();
				int n = 0;
				for (SourceAnnotation src : a.getSources()) {
					AlvisAEAnnotation srcA = src.getAnnotation();
					Tuple srcT = mapping.get(srcA);
					t.setArgument(sourceRolePrefix + (n++), srcT);
				}
			}
		}
		for (TextBound txt : aset.getTextBounds())
			convertTextBound(txt, mapping.get(txt), sec);
		for (Group grp : aset.getGroups())
			convertGroup(grp, mapping);
		for (fr.inra.maiage.bibliome.util.alvisae.Relation rel : aset.getRelations())
			convertRelation(rel, mapping);
	}

	private void setKind(Tuple t, String kind) {
		Relation rel = t.getRelation();
		if (!rel.hasFeature(kindFeature)) {
			rel.addFeature(kindFeature, kind);
		}
	}

	private void convertRelation(fr.inra.maiage.bibliome.util.alvisae.Relation rel, Map<AlvisAEAnnotation,Tuple> mapping) {
		Tuple t = mapping.get(rel);
		for (String role : rel.getRoles())
			t.setArgument(role, mapping.get(rel.getArgument(role)));
		setKind(t, "relation");
	}

	private void convertGroup(Group grp, Map<AlvisAEAnnotation,Tuple> mapping) {
		Tuple t = mapping.get(grp);
		int n = 0;
		for (AlvisAEAnnotation item : grp.getItems())
			t.setArgument(itemRolePrefix + (n++), mapping.get(item));
		setKind(t, "group");
	}

	private void convertTextBound(TextBound txt, Tuple t, Section sec) {
		Layer layer = sec.ensureLayer(fragmentsLayer);
		int n = 0;
		for (Fragment f : txt.getFragments()) {
			Annotation a = new Annotation(this, layer, f.getStart(), f.getEnd());
			a.addFeature(fragmentTypeFeature , txt.getType());
			a.addFeature(userFeature, txt.getAnnotationSet().getUser());
			a.addFeature(userIdFeature, Integer.toString(txt.getAnnotationSet().getUserId()));
			t.setArgument(fragmentRolePrefix + (n++), a);
		}
		setKind(t, "text-bound");
	}

	private Section getSection(AlvisAEDocument doc, Document aDoc) {
		for (Section sec : Iterators.loop(aDoc.sectionIterator(section)))
			return sec;
		return new Section(this, aDoc, section, doc.getContents());
	}

	@Deprecated
	@Param(mandatory = false)
	public String getUrl() {
		return url;
	}

	@Deprecated
	@Param(mandatory = false)
	public String getSchema() {
		return schema;
	}

	@Deprecated
	@Param(mandatory = false)
	public String getUsername() {
		return username;
	}

	@Deprecated
	@Param(mandatory = false)
	public String getPassword() {
		return password;
	}

	@Param
	public Integer[] getCampaignId() {
		return campaignId;
	}

	@Param(mandatory=false)
	public Integer[] getDocIds() {
		return docIds;
	}

	@Param(mandatory=false)
	public String[] getDocExternalIds() {
		return docExternalIds;
	}

	@Param(mandatory=false)
	public String[] getDocDescriptions() {
		return docDescriptions;
	}

	@Param
	public Boolean getHead() {
		return head;
	}

	@Param(mandatory=false)
	public Integer getTaskId() {
		return taskId;
	}

	@Param(mandatory=false)
	public String getTaskName() {
		return taskName;
	}

	@Param(mandatory=false)
	public Integer[] getUserIds() {
		return userIds;
	}

	@Param(mandatory=false)
	public String[] getUserNames() {
		return userNames;
	}

	@Param
	public Boolean getLoadTextBound() {
		return loadTextBound;
	}

	@Param
	public Boolean getLoadGroups() {
		return loadGroups;
	}

	@Param
	public Boolean getLoadRelations() {
		return loadRelations;
	}

	@Deprecated
	@Param(nameType=NameType.SECTION)
	public String getSectionName() {
		return section;
	}

	@Param(nameType=NameType.LAYER)
	public String getFragmentsLayer() {
	    return this.fragmentsLayer;
	};

	public void setFragmentsLayer(String fragmentsLayer) {
	    this.fragmentsLayer = fragmentsLayer;
	};

	@Deprecated
	@Param(nameType=NameType.LAYER)
	public String getFragmentsLayerName() {
		return fragmentsLayer;
	}

	@Param
	public String getFragmentRolePrefix() {
		return fragmentRolePrefix;
	}

	@Param
	public String getItemRolePrefix() {
		return itemRolePrefix;
	}

	@Param(mandatory=false, nameType=NameType.FEATURE)
	public String getUserFeature() {
		return userFeature;
	}

	@Param(mandatory=false, nameType=NameType.FEATURE)
	public String getTaskFeature() {
		return taskFeature;
	}

	@Param(nameType=NameType.FEATURE)
	public String getFragmentTypeFeature() {
		return fragmentTypeFeature;
	}

	@Param
	public Boolean getOldModel() {
		return oldModel;
	}

	@Param(nameType=NameType.FEATURE)
	public String getExternalIdFeature() {
		return externalIdFeature;
	}

	@Param(nameType=NameType.FEATURE)
	public String getDescriptionFeature() {
		return descriptionFeature;
	}

	@Param(nameType=NameType.FEATURE)
	public String getCreatedFeature() {
		return createdFeature;
	}

	@Param(nameType=NameType.FEATURE)
	public String getKindFeature() {
		return kindFeature;
	}

	@Param(nameType=NameType.FEATURE)
	public String getTypeFeature() {
		return typeFeature;
	}

	@Param
	public Boolean getLoadDependencies() {
		return loadDependencies;
	}

	@Param
	public String getSourceRolePrefix() {
		return sourceRolePrefix;
	}

	@Param
	public Boolean getAdjudicate() {
		return adjudicate;
	}

	@Param(nameType=NameType.FEATURE)
	public String getAnnotationSetIdFeature() {
		return annotationSetIdFeature;
	}

	@Param(nameType=NameType.FEATURE)
	public String getAnnotationIdFeature() {
		return annotationIdFeature;
	}

	@Param(nameType=NameType.FEATURE)
	public String getReferentFeature() {
		return referentFeature;
	}

	@Param(nameType=NameType.FEATURE)
	public String getUnmatchedFeature() {
		return unmatchedFeature;
	}

	@Param(nameType=NameType.LAYER)
	public String getHtmlLayer() {
	    return this.htmlLayer;
	};

	public void setHtmlLayer(String htmlLayer) {
	    this.htmlLayer = htmlLayer;
	};

	@Deprecated
	@Param(nameType=NameType.LAYER)
	public String getHtmlLayerName() {
		return htmlLayer;
	}

	@Param(nameType=NameType.FEATURE)
	public String getHtmlTagFeature() {
		return htmlTagFeature;
	}

	@Param(mandatory=false, nameType=NameType.FEATURE)
	public String getUserIdFeature() {
		return userIdFeature;
	}

	@Param(mandatory=false, nameType=NameType.FEATURE)
	public String getTaskIdFeature() {
		return taskIdFeature;
	}

	@Param(mandatory=false, nameType=NameType.FEATURE)
	public String getCampaignIdFeature() {
		return campaignIdFeature;
	}

	@Param(mandatory=false, nameType=NameType.FEATURE)
	public String getCampaignNameFeature() {
		return campaignNameFeature;
	}

	@Param(nameType=NameType.SECTION)
	public String getSection() {
		return section;
	}

	@Param
	public SourceStream getDatabasePropsFile() {
		return databasePropsFile;
	}

	public void setDatabasePropsFile(SourceStream databasePropsFile) {
		this.databasePropsFile = databasePropsFile;
	}

	public void setSection(String section) {
		this.section = section;
	}

	public void setCampaignIdFeature(String campaignIdFeature) {
		this.campaignIdFeature = campaignIdFeature;
	}

	public void setCampaignNameFeature(String campaignNameFeature) {
		this.campaignNameFeature = campaignNameFeature;
	}

	public void setTaskIdFeature(String taskIdFeature) {
		this.taskIdFeature = taskIdFeature;
	}

	public void setUserIdFeature(String userIdFeature) {
		this.userIdFeature = userIdFeature;
	}

	public void setUnmatchedFeature(String unmatchedFeature) {
		this.unmatchedFeature = unmatchedFeature;
	}

	public void setHtmlLayerName(String htmlLayer) {
		this.htmlLayer = htmlLayer;
	}

	public void setHtmlTagFeature(String htmlTagFeature) {
		this.htmlTagFeature = htmlTagFeature;
	}

	public void setReferentFeature(String referentFeature) {
		this.referentFeature = referentFeature;
	}

	public void setAnnotationIdFeature(String annotationIdFeature) {
		this.annotationIdFeature = annotationIdFeature;
	}

	public void setAnnotationSetIdFeature(String annotationSetId) {
		this.annotationSetIdFeature = annotationSetId;
	}

	public void setAdjudicate(Boolean adjudicate) {
		this.adjudicate = adjudicate;
	}

	public void setLoadDependencies(Boolean loadDependencies) {
		this.loadDependencies = loadDependencies;
	}

	public void setSourceRolePrefix(String sourceRolePrefix) {
		this.sourceRolePrefix = sourceRolePrefix;
	}

	public void setTypeFeature(String typeFeature) {
		this.typeFeature = typeFeature;
	}

	public void setKindFeature(String kindFeature) {
		this.kindFeature = kindFeature;
	}

	public void setExternalIdFeature(String externalIdFeature) {
		this.externalIdFeature = externalIdFeature;
	}

	public void setDescriptionFeature(String descriptionFeature) {
		this.descriptionFeature = descriptionFeature;
	}

	public void setCreatedFeature(String createdFeature) {
		this.createdFeature = createdFeature;
	}

	public void setOldModel(Boolean oldModel) {
		this.oldModel = oldModel;
	}

	public void setFragmentTypeFeature(String fragmentTypeFeature) {
		this.fragmentTypeFeature = fragmentTypeFeature;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public void setSchema(String schema) {
		this.schema = schema;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public void setCampaignId(Integer[] campaignId) {
		this.campaignId = campaignId;
	}

	public void setDocIds(Integer[] docIds) {
		this.docIds = docIds;
	}

	public void setDocExternalIds(String[] docExternalIds) {
		this.docExternalIds = docExternalIds;
	}

	public void setDocDescriptions(String[] docDesciptions) {
		this.docDescriptions = docDesciptions;
	}

	public void setHead(Boolean head) {
		this.head = head;
	}

	public void setTaskId(Integer taskId) {
		this.taskId = taskId;
	}

	public void setTaskName(String taskName) {
		this.taskName = taskName;
	}

	public void setUserIds(Integer[] userIds) {
		this.userIds = userIds;
	}

	public void setUserNames(String[] userNames) {
		this.userNames = userNames;
	}

	public void setLoadTextBound(Boolean loadTextBound) {
		this.loadTextBound = loadTextBound;
	}

	public void setLoadGroups(Boolean loadGroups) {
		this.loadGroups = loadGroups;
	}

	public void setLoadRelations(Boolean loadRelations) {
		this.loadRelations = loadRelations;
	}

	public void setSectionName(String sectionName) {
		this.section = sectionName;
	}

	public void setFragmentsLayerName(String fragmentsLayer) {
		this.fragmentsLayer = fragmentsLayer;
	}

	public void setFragmentRolePrefix(String fragmentRolePrefix) {
		this.fragmentRolePrefix = fragmentRolePrefix;
	}

	public void setItemRolePrefix(String itemRolePrefix) {
		this.itemRolePrefix = itemRolePrefix;
	}

	public void setUserFeature(String userFeature) {
		this.userFeature = userFeature;
	}

	public void setTaskFeature(String taskFeature) {
		this.taskFeature = taskFeature;
	}
}
