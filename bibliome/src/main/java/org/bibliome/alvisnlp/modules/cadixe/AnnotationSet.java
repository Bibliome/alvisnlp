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

import java.util.ArrayList;
import java.util.Collection;

import org.bibliome.alvisnlp.converters.expression.parser.ExpressionParser;
import org.bibliome.alvisnlp.modules.DefaultExpressions;
import org.bibliome.alvisnlp.modules.cadixe.AnnotationSet.Resolved;
import org.bibliome.alvisnlp.modules.cadixe.ExportCadixeJSON.CadixeExportContext;
import org.bibliome.util.Iterators;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import alvisnlp.corpus.Document;
import alvisnlp.corpus.Element;
import alvisnlp.corpus.Section;
import alvisnlp.corpus.expressions.Evaluator;
import alvisnlp.corpus.expressions.Expression;
import alvisnlp.corpus.expressions.LibraryResolver;
import alvisnlp.corpus.expressions.Resolvable;
import alvisnlp.corpus.expressions.ResolverException;
import alvisnlp.module.ModuleException;
import alvisnlp.module.NameUsage;
import alvisnlp.module.NameUser;

public class AnnotationSet implements Resolvable<Resolved> {
	private String description;
	private int revision;
	private String type;
	private int owner;
	private int id;
	private int taskId;
	private boolean head = true;
	private final Collection<TextAnnotationDefinition> textAnnotationDefinitions = new ArrayList<TextAnnotationDefinition>();
	private final Collection<RelationDefinition> relationDefinitions = new ArrayList<RelationDefinition>();
	private final Collection<GroupDefinition> groupDefinitions = new ArrayList<GroupDefinition>();
	private final Expression unmatched = ExpressionParser.parseUnsafe("sections.relations.tuples[@unmatched == \"unmatched\"]");
	private final Expression unmatchedAnnotationSet = DefaultExpressions.feature("annotation-set");
	private final Expression unmatchedId = DefaultExpressions.feature("id");

	@SuppressWarnings("hiding")
	public class Resolved implements NameUser {
		private final Collection<TextAnnotationDefinition.Resolved> textAnnotationDefinitions = new ArrayList<TextAnnotationDefinition.Resolved>();
		private final Collection<RelationDefinition.Resolved> relationDefinitions = new ArrayList<RelationDefinition.Resolved>();
		private final Collection<GroupDefinition.Resolved> groupDefinitions = new ArrayList<GroupDefinition.Resolved>();
		private final Evaluator unmatched;
		private final Evaluator unmatchedId;
		private final Evaluator unmatchedAnnotationSet;
		
		private Resolved(Evaluator unmatched, Evaluator unmatchedId, Evaluator unmatchedAnnotationSet) {
			super();
			this.unmatched = unmatched;
			this.unmatchedId = unmatchedId;
			this.unmatchedAnnotationSet = unmatchedAnnotationSet;
		}

		@Override
		public void collectUsedNames(NameUsage nameUsage, String defaultType) throws ModuleException {
			for (TextAnnotationDefinition.Resolved tad : textAnnotationDefinitions) {
				tad.collectUsedNames(nameUsage, defaultType);
			}
			for (RelationDefinition.Resolved rd : relationDefinitions) {
				rd.collectUsedNames(nameUsage, defaultType);
			}
			for (GroupDefinition.Resolved gd : groupDefinitions) {
				gd.collectUsedNames(nameUsage, defaultType);
			}
			unmatched.collectUsedNames(nameUsage, defaultType);
			unmatchedId.collectUsedNames(nameUsage, defaultType);
			unmatchedAnnotationSet.collectUsedNames(nameUsage, defaultType);
		}

		@SuppressWarnings("unchecked")
		private void processDefinitions(Collection<? extends AnnotationDefinition.Resolved> defs, JSONArray array, Section sec, int offset, CadixeExportContext ctx) {
			for (AnnotationDefinition.Resolved def : defs) {
				for (Element elt : Iterators.loop(def.getElements(ctx.evalCtx, sec))) {
					ctx.addAnnotationReference(elt, id);
					array.add(def.toJSON(elt, offset, ctx));
				}
			}
		}

		@SuppressWarnings("unchecked")
		JSONObject toJSON(Document doc, Evaluator secFilter, CadixeExportContext ctx) {
			JSONObject result = new JSONObject();
			result.put("id", getId());
			result.put("task_id", getTaskId());
			result.put("owner", getOwner());
			result.put("task_id", getTaskId());
			result.put("type", getType());
			result.put("timestamp", ctx.timestamp);
			if (ctx.publish()) {
				result.put("published", ctx.timestamp);
			}
			result.put("revision", getRevision());
			result.put("description", getDescription());
			result.put("head", head);
			int offset = 0;
			JSONArray textAnnotations = new JSONArray();
			JSONArray relations = new JSONArray();
			JSONArray groups = new JSONArray();
			for (Section sec : Iterators.loop(doc.sectionIterator())) {
				if (secFilter.getFilter(ctx.evalCtx).accept(sec)) {
					processDefinitions(textAnnotationDefinitions, textAnnotations, sec, offset, ctx);
					processDefinitions(relationDefinitions, relations, sec, offset, ctx);
					processDefinitions(groupDefinitions, groups, sec, offset, ctx);
				}
				offset += sec.getContents().length();
			}
			result.put("text_annotations", textAnnotations);
			result.put("relations", relations);
			result.put("groups", groups);
			JSONArray unmatched = new JSONArray();
			for (Element e : Iterators.loop(this.unmatched.evaluateElements(ctx.evalCtx, doc))) {
				JSONObject u = new JSONObject();
				u.put("ann_id", unmatchedId.evaluateString(ctx.evalCtx, e));
				u.put("set_id", unmatchedAnnotationSet.evaluateInt(ctx.evalCtx, e));
				u.put("status", 2);
				unmatched.add(u);
			}
			result.put("unmatched", unmatched);
			return result;
		}
	}

	@Override
	public Resolved resolveExpressions(LibraryResolver resolver) throws ResolverException {
		Evaluator unmatched = this.unmatched.resolveExpressions(resolver);
		Evaluator unmatchedId = this.unmatchedId.resolveExpressions(resolver);
		Evaluator unmatchedAnnotationSet = this.unmatchedAnnotationSet.resolveExpressions(resolver);
		Resolved result = new Resolved(unmatched, unmatchedId, unmatchedAnnotationSet);
		for (TextAnnotationDefinition t : textAnnotationDefinitions)
			result.textAnnotationDefinitions.add(t.resolveExpressions(resolver));
		for (GroupDefinition g : groupDefinitions)
			result.groupDefinitions.add(g.resolveExpressions(resolver));
		for (RelationDefinition r : relationDefinitions)
			result.relationDefinitions.add(r.resolveExpressions(resolver));
		return result;
	}

	public String getDescription() {
		return description;
	}

	public int getRevision() {
		return revision;
	}

	public String getType() {
		return type;
	}

	public int getOwner() {
		return owner;
	}

	public int getId() {
		return id;
	}

	public int getTaskId() {
		return taskId;
	}

	public boolean isHead() {
		return head;
	}

	public void setTaskId(int task_id) {
		this.taskId = task_id;
	}

	public void setHead(boolean head) {
		this.head = head;
	}

	void setDescription(String description) {
		this.description = description;
	}

	void setRevision(int revision) {
		this.revision = revision;
	}

	void setType(String type) {
		this.type = type;
	}

	void setOwner(int owner) {
		this.owner = owner;
	}

	void setId(int id) {
		this.id = id;
	}

	void addTextAnnotationDefinition(TextAnnotationDefinition def) {
		textAnnotationDefinitions.add(def);
	}

	void addRelationDefinition(RelationDefinition def) {
		relationDefinitions.add(def);
	}

	void addGroupDefinition(GroupDefinition def) {
		groupDefinitions.add(def);
	}
}
