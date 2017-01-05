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


package org.bibliome.alvisnlp.modules.cadixe;

import java.io.Reader;
import java.io.StringReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import org.bibliome.alvisnlp.modules.CorpusModule;
import org.bibliome.alvisnlp.modules.ResolvedObjects;
import org.bibliome.util.defaultmap.DefaultMap;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import alvisnlp.corpus.Annotation;
import alvisnlp.corpus.Corpus;
import alvisnlp.corpus.Document;
import alvisnlp.corpus.Element;
import alvisnlp.corpus.Layer;
import alvisnlp.corpus.NameType;
import alvisnlp.corpus.Relation;
import alvisnlp.corpus.Section;
import alvisnlp.corpus.Tuple;
import alvisnlp.corpus.creators.AnnotationCreator;
import alvisnlp.corpus.creators.DocumentCreator;
import alvisnlp.corpus.creators.SectionCreator;
import alvisnlp.corpus.creators.TupleCreator;
import alvisnlp.corpus.expressions.ResolverException;
import alvisnlp.module.ModuleException;
import alvisnlp.module.ProcessingContext;
import alvisnlp.module.ProcessingException;
import alvisnlp.module.lib.AlvisNLPModule;
import alvisnlp.module.lib.Param;

@AlvisNLPModule(obsoleteUseInstead=AlvisAEReader2.class)
public abstract class AlvisAEReader extends CorpusModule<ResolvedObjects> implements DocumentCreator, SectionCreator, AnnotationCreator, TupleCreator {
	private String url;
	private String schema;
	private String username;
	private String password;
	private Integer campaignId;
	private String sectionName = "alvisae";
	private String htmlLayerName = "html";
	private String userLayerName = "user";
	private String typeFeature = "type";
	private String groupItemRolePrefix = "arg";
	private String textBoundRelationName = "textBound";
	private String textBoundFragmentRolePrefix = "frag";
	private Boolean linkToAnnotation = true;
	private String maxDate;
	private Integer taskId;
	private Integer userId;
	
	@Override
	protected ResolvedObjects createResolvedObjects(ProcessingContext<Corpus> ctx) throws ResolverException {
		return new ResolvedObjects(ctx, this);
	}

	@Override
	public void process(ProcessingContext<Corpus> ctx, Corpus corpus) throws ModuleException {
		loadPGSQLDriver();
		try (Connection connection = DriverManager.getConnection(url, username, password)) {
			DocumentMap docMap = new DocumentMap(corpus, connection);
			Statement asetStatement = connection.createStatement();
			String query = "SELECT DISTINCT ON (doc_id, type) doc_id, text_annotations, groups, relations, type, max(created) FROM " + schema + ".annotationset WHERE " + (maxDate == null ? "head" : ("created < '" + maxDate + "'")) + " AND campaign_id = " + campaignId + (taskId == null ? "" : " AND task_id = " + taskId) + (userId == null ? "" : " AND user_id = " + userId) + " GROUP BY doc_id, text_annotations, groups, relations, type";
			getLogger(ctx).fine("query = " + query);
			ResultSet asets = asetStatement.executeQuery(query);
			while (asets.next()) {
				int docId = asets.getInt("doc_id");
				Section sec = docMap.safeGet(docId);
				int type = asets.getInt("type");
				String layerName = type == 1 ? userLayerName : htmlLayerName;
				Layer layer = sec.ensureLayer(layerName);
				String annotations = asets.getString("text_annotations");
				String groups = asets.getString("groups");
				String relations = asets.getString("relations");
				fillSection(sec, layer, annotations, groups, relations);
			}
		}
		catch (SQLException e) {
			rethrow(e);
		}
	}

	private final class DocumentMap extends DefaultMap<Integer,Section> {
		private final Corpus corpus;
		private final PreparedStatement docStatement;
		
		private DocumentMap(Corpus corpus, Connection connection) throws SQLException {
			super(true, new HashMap<Integer,Section>());
			this.corpus = corpus;
			docStatement = connection.prepareStatement("SELECT description, contents, props FROM " + schema + ".document where id = ?");
		}

		@Override
		protected Section defaultValue(Integer key) {
			try {
				docStatement.setInt(1, key);
				ResultSet docs = docStatement.executeQuery();
				if (!docs.next())
					throw new RuntimeException("no document with id: " + key);
				String description = docs.getString("description");
				String contents = docs.getString("contents");
				String properties = docs.getString("props");
				if (docs.next())
					throw new RuntimeException("more than one document with id: " + key);
				Document doc = Document.getDocument(AlvisAEReader.this, corpus, description);
				setFeatures(doc, properties);
				return new Section(AlvisAEReader.this, doc, sectionName, contents);
			} 
			catch (SQLException e) {
				throw new RuntimeException(e);
			}
		}
	}

	private static void setFeatures(Element elt, String properties) {
		setFeatures(elt, (JSONObject) parseJSON(properties));
	}
	
	private static void setFeatures(Element elt, JSONObject json) {
		for (Object o : json.entrySet()) {
			@SuppressWarnings("rawtypes")
			Map.Entry e = (Map.Entry) o;
			String k = (String) e.getKey();
			for (Object v : (JSONArray) e.getValue())
				elt.addFeature(k, v.toString());
		}
	}

	private void fillSection(Section sec, Layer layer, String annotations, String groups, String relations) {
		Map<String,Tuple> annotationMap = fillSectionAnnotations(sec, layer, (JSONArray) parseJSON(annotations));
		fillSectionGroups(sec, annotationMap, (JSONArray) parseJSON(groups));
		fillSectionRelations(sec, annotationMap, (JSONArray) parseJSON(relations));
	}
	
	private void fillSectionRelations(Section sec, Map<String,Tuple> annotationMap, JSONArray relations) {
		Map<String,Tuple> relationMap = new HashMap<String,Tuple>();
		for (Object r : relations) {
			JSONObject aeRel = (JSONObject) r;
			String type = (String) aeRel.get("type");
			Relation rel = sec.ensureRelation(this, type);
			Tuple t = new Tuple(this, rel);
			JSONObject args = (JSONObject) aeRel.get("relation");
			for (Object o : args.entrySet()) {
				@SuppressWarnings("rawtypes")
				Map.Entry e = (Map.Entry) o;
				String role = (String) e.getKey();
				String ref = (String) ((JSONObject) e.getValue()).get("ann_id");
				t.addFeature("__arg__" + role, ref);
			}
			setFeatures(t, (JSONObject) aeRel.get("properties"));
			relationMap.put((String) aeRel.get("id"), t);
		}
		String firstFragmentRole = textBoundFragmentRolePrefix + "0";
		for (Tuple t : relationMap.values()) {
			Collection<String> refs = new HashSet<String>();
			for (String key : t.getFeatureKeys()) {
				if (key.startsWith("__arg__"))
					refs.add(key);
			}
			for (String key : refs) {
				String role = key.substring(7);
				String ref = t.getLastFeature(key);
				Element arg;
				if (annotationMap.containsKey(ref)) {
//					System.err.println("entity");
					Tuple a = annotationMap.get(ref);
					if (linkToAnnotation) {
						arg = a.getArgument(firstFragmentRole);
					}
					else
						arg = a;
				}
				else if (relationMap.containsKey(ref)) {
//					System.err.println("event");
					arg = relationMap.get(ref);
				}
				else
					throw new RuntimeException();
//				System.err.println("role = " + role);
//				System.err.println("arg = " + arg);
				t.setArgument(role, arg);
				t.removeFeature(key, ref);
			}
		}
	}

	private void fillSectionGroups(Section sec, Map<String,Tuple> annotationMap, JSONArray groups) {
		for (Object g : groups) {
			JSONObject grp = (JSONObject) g;
			String type = (String) grp.get("type");
			Relation rel = sec.ensureRelation(this, type);
			Tuple t = new Tuple(this, rel);
			String firstFragmentRole = textBoundFragmentRolePrefix + "0";
			int n = 0;
//			System.err.println("id = " + grp.get("id"));
//			System.err.println("doc = " + sec.getDocument().getId());
			for (Object ref : (JSONArray) grp.get("group")) {
				Tuple a = getAnnotation(annotationMap, (JSONObject) ref);
//				System.err.println("ref = " + ref);
//				System.err.println("a = " + a);
				Element arg = linkToAnnotation ? a.getArgument(firstFragmentRole) : a;
				t.setArgument(groupItemRolePrefix + (++n), arg);
			}
			setFeatures(t, (JSONObject) grp.get("properties"));
		}
	}

	private static Tuple getAnnotation(Map<String,Tuple> annotationMap, JSONObject ref) {
		String annotationId = (String) ref.get("ann_id");
		return annotationMap.get(annotationId);
	}
	
	private Map<String,Tuple> fillSectionAnnotations(Section sec, Layer layer, JSONArray annotations) {
		Map<String,Tuple> result = new HashMap<String,Tuple>();
		Relation rel = sec.ensureRelation(this, textBoundRelationName);
		for (Object tb : annotations) {
			JSONObject aeAnnotation = (JSONObject) tb;
			Tuple t = new Tuple(this, rel);
			JSONArray frags = (JSONArray) aeAnnotation.get("text");
			for (int i = 0; i < frags.size(); ++i) {
				JSONArray f = (JSONArray) frags.get(i);
				long start = (long) f.get(0);
				long end = (long) f.get(1);
				Annotation a = new Annotation(this, layer, (int) start, (int) end);
				t.setArgument(textBoundFragmentRolePrefix  + i, a);
			}
			String id = (String) aeAnnotation.get("id");
			result.put(id, t);
			t.addFeature(typeFeature, (String) aeAnnotation.get("type"));
			setFeatures(t, (JSONObject) aeAnnotation.get("properties"));
		}
		return result;
	}
 
	private static Object parseJSON(String s) {
		Reader r = new StringReader(s);
		return JSONValue.parse(r);
	}

	private final static void loadPGSQLDriver() throws ProcessingException {
		try {
//			Class.forName("org.postgresql.Driver");
			Class.forName("org.postgresql.Driver", true, AlvisAEReader.class.getClassLoader());
		}
		catch (ClassNotFoundException e) {
			rethrow(e);
		}
	}

	@Param
	public String getUrl() {
		return url;
	}

	@Param
	public String getUsername() {
		return username;
	}

	@Param
	public String getPassword() {
		return password;
	}

	@Param
	public Integer getCampaignId() {
		return campaignId;
	}

	@Param(nameType=NameType.SECTION)
	public String getSectionName() {
		return sectionName;
	}

	@Param(nameType=NameType.LAYER)
	public String getHtmlLayerName() {
		return htmlLayerName;
	}

	@Param
	public String getUserLayerName() {
		return userLayerName;
	}

	@Param(nameType=NameType.FEATURE)
	public String getTypeFeature() {
		return typeFeature;
	}

	@Param
	public String getGroupItemRolePrefix() {
		return groupItemRolePrefix;
	}

	@Param
	public String getSchema() {
		return schema;
	}

	@Param(nameType=NameType.RELATION)
	public String getTextBoundRelationName() {
		return textBoundRelationName;
	}

	@Param
	public String getTextBoundFragmentRolePrefix() {
		return textBoundFragmentRolePrefix;
	}

	@Param
	public Boolean getLinkToAnnotation() {
		return linkToAnnotation;
	}

	@Param(mandatory=false)
	public String getMaxDate() {
		return maxDate;
	}

	@Param(mandatory=false)
	public Integer getTaskId() {
		return taskId;
	}

	@Param(mandatory=false)
	public Integer getUserId() {
		return userId;
	}

	public void setTaskId(Integer taskId) {
		this.taskId = taskId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	public void setMaxDate(String maxDate) {
		this.maxDate = maxDate;
	}

	public void setLinkToAnnotation(Boolean linkToAnnotation) {
		this.linkToAnnotation = linkToAnnotation;
	}

	public void setTextBoundRelationName(String textBoundRelationName) {
		this.textBoundRelationName = textBoundRelationName;
	}

	public void setTextBoundFragmentRolePrefix(String textBoundFragmentRolePrefix) {
		this.textBoundFragmentRolePrefix = textBoundFragmentRolePrefix;
	}

	public void setSchema(String schema) {
		this.schema = schema;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public void setCampaignId(Integer campaignId) {
		this.campaignId = campaignId;
	}

	public void setSectionName(String sectionName) {
		this.sectionName = sectionName;
	}

	public void setHtmlLayerName(String htmlLayerName) {
		this.htmlLayerName = htmlLayerName;
	}

	public void setUserLayerName(String userLayerName) {
		this.userLayerName = userLayerName;
	}

	public void setTypeFeature(String typeFeature) {
		this.typeFeature = typeFeature;
	}

	public void setGroupItemRolePrefix(String groupItemRolePrefix) {
		this.groupItemRolePrefix = groupItemRolePrefix;
	}
}
