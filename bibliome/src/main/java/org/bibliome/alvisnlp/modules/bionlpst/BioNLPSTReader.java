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


package org.bibliome.alvisnlp.modules.bionlpst;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import org.bibliome.alvisnlp.modules.CorpusModule;
import org.bibliome.alvisnlp.modules.ResolvedObjects;
import org.bibliome.util.Iterators;
import org.bibliome.util.Timer;
import org.bibliome.util.bionlpst.AnnotationWithArgs;
import org.bibliome.util.bionlpst.BioNLPSTAnnotation;
import org.bibliome.util.bionlpst.BioNLPSTAnnotationVisitor;
import org.bibliome.util.bionlpst.BioNLPSTDocument;
import org.bibliome.util.bionlpst.BioNLPSTException;
import org.bibliome.util.bionlpst.BioNLPSTRelation;
import org.bibliome.util.bionlpst.CheckIdPrefix;
import org.bibliome.util.bionlpst.Equivalence;
import org.bibliome.util.bionlpst.Event;
import org.bibliome.util.bionlpst.Modification;
import org.bibliome.util.bionlpst.Normalization;
import org.bibliome.util.bionlpst.TextBound;
import org.bibliome.util.bionlpst.Visibility;
import org.bibliome.util.bionlpst.schema.AnnotationSchema;
import org.bibliome.util.bionlpst.schema.AnnotationSchemaVisitor;
import org.bibliome.util.bionlpst.schema.DocumentSchema;
import org.bibliome.util.bionlpst.schema.EventSchema;
import org.bibliome.util.bionlpst.schema.ModificationSchema;
import org.bibliome.util.bionlpst.schema.NormalizationSchema;
import org.bibliome.util.bionlpst.schema.RelationSchema;
import org.bibliome.util.bionlpst.schema.TextBoundSchema;
import org.bibliome.util.files.InputDirectory;
import org.bibliome.util.fragments.Fragment;
import org.bibliome.util.streams.PatternFileFilter;

import alvisnlp.corpus.Annotation;
import alvisnlp.corpus.Corpus;
import alvisnlp.corpus.Document;
import alvisnlp.corpus.DownCastElement;
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
import alvisnlp.module.NameUsage;
import alvisnlp.module.ProcessingContext;
import alvisnlp.module.TimerCategory;
import alvisnlp.module.lib.AlvisNLPModule;
import alvisnlp.module.lib.Param;

@AlvisNLPModule
public abstract class BioNLPSTReader extends CorpusModule<ResolvedObjects> implements DocumentCreator, SectionCreator, AnnotationCreator, TupleCreator {
	private String equivalenceRelationName = "equiv";
	private String equivalenceItemPrefix = "item";
	private String typeFeatureName = "type";
	private String idFeatureName = "id";
	private Boolean textBoundAsAnnotations = false;
	private String fragmentCountFeatureName = "fragments";
	private String textBoundFragmentRolePrefix = "frag";
	private String triggerRole = "trigger";
	private String kindFeatureName = "kind";
	private String textKind = "text";
	private String eventKind = "event";
	private String relationKind = "relation";
	private InputDirectory textDir;
	private InputDirectory a1Dir;
	private InputDirectory a2Dir;
	private String charset = "UTF-8";
	private String sectionName = "text";
	private DocumentSchema schema;
	
	@Override
	protected ResolvedObjects createResolvedObjects(ProcessingContext<Corpus> ctx) throws ResolverException {
		return new ResolvedObjects(ctx, this);
	}

	@Override
	public void process(ProcessingContext<Corpus> ctx, Corpus corpus) throws ModuleException {
		FileFilter filter = new PatternFileFilter(Pattern.compile("\\.txt$"), false, false);
		Logger logger = getLogger(ctx);
		Timer<TimerCategory> a1Timer = getTimer(ctx, "parse-a1", TimerCategory.LOAD_RESOURCE, false);
		Timer<TimerCategory> a2Timer = getTimer(ctx, "parse-a2", TimerCategory.LOAD_RESOURCE, false);
		Timer<TimerCategory> collectTimer = getTimer(ctx, "import-data", TimerCategory.COLLECT_DATA, false);
		try {
			File[] txtFiles = textDir.listFiles(filter);
			for (File f : txtFiles) {
				BioNLPSTDocument doc = new BioNLPSTDocument(charset, f);
				if (a1Dir != null) {
					a1Timer.start();
					doc.parseAFile(a1Dir, Visibility.A1, charset);
					a1Timer.stop();
				}
				if (a2Dir != null) {
					a2Timer.start();
					doc.parseAFile(a2Dir, Visibility.A2, charset);
					a2Timer.stop();
				}
				collectTimer.start();
				doc.resolveAllIds();
				for (String msg : CheckIdPrefix.check(doc))
					logger.warning(msg);
				if (schema != null) {
					Collection<String> messages = schema.check(doc);
					for (String msg : messages) {
						logger.warning(msg);
					}
				}

				Document alvisnlpDoc = Document.getDocument(this, corpus, doc.getId());
				Section section = getSection(alvisnlpDoc, doc.getText());
				Map<String,Element> map = new HashMap<String,Element>();
				for (BioNLPSTAnnotation a : doc.getAnnotations()) {
					Element e = a.accept(pass1, section);
					if (e != null) {
						e.addFeature(idFeatureName, a.getId());
						e.addFeature(typeFeatureName, a.getType());
						map.put(a.getId(), e);
					}
				}

				Relation equivRel = section.ensureRelation(this, equivalenceRelationName);
				for (Equivalence equiv : doc.getEquivalences()) {
					Tuple t = new Tuple(this, equivRel);
					List<String> items = new ArrayList<String>(equiv.getAnnotationIds());
					for (int i = 0; i < items.size(); ++i) {
						String id = items.get(i);
						t.setArgument(equivalenceItemPrefix + i, map.get(id));
					}
				}
				
				for (BioNLPSTAnnotation a : doc.getAnnotations()) {
					a.accept(pass2, map);
				}
				collectTimer.stop();
			}
			logger.info("created " + txtFiles.length + " documents");
		}
		catch (IOException|BioNLPSTException e) {
			rethrow(e);
		}
	}
	
	private void ensureKindFeature(Relation relation, String kind) {
		if (!relation.hasFeature(kindFeatureName)) {
			relation.addFeature(kindFeatureName, kind);
		}
	}
	
	private Section getSection(Document alvisnlpDoc, String text) {
		for (Section sec : Iterators.loop(alvisnlpDoc.sectionIterator(sectionName))) {
			if (text.equals(sec.getContents())) {
				return sec;
			}
		}
		return new Section(this, alvisnlpDoc, sectionName, text);
	}
	
	@Override
	public void collectUsedNames(NameUsage nameUsage, String defaultType) throws ModuleException {
		if (schema == null) {
			return;
		}
		for (AnnotationSchema as : schema.getAnnotationSchemas()) {
			nameUsage.addNames(NameType.RELATION, as.getType());
			as.accept(NAME_USAGE_SCHEMA_VISITOR, nameUsage);
		}
	}
	
	private static final AnnotationSchemaVisitor<Void,NameUsage> NAME_USAGE_SCHEMA_VISITOR = new AnnotationSchemaVisitor<Void,NameUsage>() {
		@Override
		public Void visit(EventSchema event, NameUsage param) {
			param.addNames(NameType.ARGUMENT, event.getArgs());
			return null;
		}

		@Override
		public Void visit(ModificationSchema mod, NameUsage param) {
			return null;
		}

		@Override
		public Void visit(NormalizationSchema norm, NameUsage param) {
			return null;
		}

		@Override
		public Void visit(RelationSchema rel, NameUsage param) {
			param.addNames(NameType.ARGUMENT, rel.getArgs());
			return null;
		}

		@Override
		public Void visit(TextBoundSchema txt, NameUsage param) {
			param.addNames(NameType.LAYER, txt.getType());
			return null;
		}
	};

	private final BioNLPSTAnnotationVisitor<Element,Section> pass1 = new BioNLPSTAnnotationVisitor<Element,Section>() {
		@Override
		public Element visit(TextBound textBound, Section param) {
			Layer layer = param.ensureLayer(textBound.getType());
			List<Fragment> fragments = textBound.getFragments();
			if (textBoundAsAnnotations) {
				int start = Integer.MAX_VALUE;
				int end= 0;
				for (Fragment f : fragments) {
					start = Math.min(start, f.getStart());
					end = Math.max(end, f.getEnd());
				}
				Annotation result = new Annotation(BioNLPSTReader.this, layer, start, end);
				result.addFeature(fragmentCountFeatureName, Integer.toString(fragments.size()));
				return result;
			}
			Relation rel = param.ensureRelation(BioNLPSTReader.this, textBound.getType());
			ensureKindFeature(rel, textKind);
			Tuple result = new Tuple(BioNLPSTReader.this, rel);
			for (int i = 0; i < fragments.size(); ++i) {
				Fragment f = fragments.get(i);
				Annotation a = new Annotation(BioNLPSTReader.this, layer, f.getStart(), f.getEnd());
				result.setArgument(textBoundFragmentRolePrefix + i, a);
			}
			return result;
		}

		@Override
		public Element visit(BioNLPSTRelation relation, Section param) {
			Relation rel = param.ensureRelation(BioNLPSTReader.this, relation.getType());
			ensureKindFeature(rel, relationKind);
			return new Tuple(BioNLPSTReader.this, rel);
		}

		@Override
		public Element visit(Event event, Section param) {
			Relation rel = param.ensureRelation(BioNLPSTReader.this, event.getType());
			ensureKindFeature(rel, eventKind);
			return new Tuple(BioNLPSTReader.this, rel);
		}

		@Override
		public Element visit(Normalization normalization, Section param) {
			return null;
		}

		@Override
		public Element visit(Modification modification, Section param) {
			return null;
		}
	};
	
	private final BioNLPSTAnnotationVisitor<Void,Map<String,Element>> pass2 = new BioNLPSTAnnotationVisitor<Void,Map<String,Element>>() {
		@Override
		public Void visit(TextBound textBound, Map<String,Element> param) {
			return null;
		}

		private Element getElement(String id, Map<String,Element> param) {
			if (param.containsKey(id))
				return param.get(id);
			throw new RuntimeException();
		}
		
		@Override
		public Void visit(BioNLPSTRelation relation, Map<String,Element> param) {
			Tuple t = getTuple(relation, param);
			setArguments(relation, t, param);
			return null;
		}
		
		private void setArguments(AnnotationWithArgs annotation, Tuple t, Map<String,Element> param) {
			Map<String,String> args = annotation.getArgumentIds();
			for (Map.Entry<String,String> e : args.entrySet()) {
				setArgument(t, e.getKey(), e.getValue(), param);
			}			
		}
		
		private void setArgument(Tuple t, String role, String id, Map<String,Element> param) {
			Element elt = getElement(id, param);
			t.setArgument(role, elt);			
		}
		
		private Tuple getTuple(BioNLPSTAnnotation annotation, Map<String,Element> param) {
			String id = annotation.getId();
			Element elt = getElement(id, param);
			return DownCastElement.toTuple(elt);
		}

		@Override
		public Void visit(Event event, Map<String,Element> param) {
			Tuple t = getTuple(event, param);
			setArguments(event, t, param);
			setArgument(t, triggerRole, event.getTriggerId(), param);
			return null;
		}

		@Override
		public Void visit(Normalization normalization, Map<String,Element> param) {
			String id = normalization.getAnnotationId();
			Element elt = getElement(id, param);
			String type = normalization.getType();
			String referent = normalization.getReferent();
			elt.addFeature(type, referent);
			return null;
		}

		@Override
		public Void visit(Modification modification, Map<String,Element> param) {
			String id = modification.getAnnotationId();
			Element elt = getElement(id, param);
			String type = modification.getType();
			elt.addFeature(type, type);
			return null;
		}
	};

	@Param(nameType=NameType.RELATION)
	public String getEquivalenceRelationName() {
		return equivalenceRelationName;
	}

	@Param
	public String getEquivalenceItemPrefix() {
		return equivalenceItemPrefix;
	}

	@Param(nameType=NameType.FEATURE)
	public String getTypeFeatureName() {
		return typeFeatureName;
	}

	@Param(nameType=NameType.FEATURE)
	public String getIdFeatureName() {
		return idFeatureName;
	}

	@Param
	public Boolean getTextBoundAsAnnotations() {
		return textBoundAsAnnotations;
	}

	@Param(nameType=NameType.FEATURE)
	public String getFragmentCountFeatureName() {
		return fragmentCountFeatureName;
	}

	@Param
	public String getTextBoundFragmentRolePrefix() {
		return textBoundFragmentRolePrefix;
	}

	@Param(nameType=NameType.ARGUMENT)
	public String getTriggerRole() {
		return triggerRole;
	}

	@Param
	public InputDirectory getTextDir() {
		return textDir;
	}

	@Param(mandatory=false)
	public InputDirectory getA1Dir() {
		return a1Dir;
	}

	@Param(mandatory=false)
	public InputDirectory getA2Dir() {
		return a2Dir;
	}

	@Param
	public String getCharset() {
		return charset;
	}

	@Param(nameType=NameType.SECTION)
	public String getSectionName() {
		return sectionName;
	}

	@Param(mandatory=false)
	public DocumentSchema getSchema() {
		return schema;
	}

	@Param(nameType=NameType.FEATURE)
	public String getKindFeatureName() {
		return kindFeatureName;
	}

	@Param
	public String getTextKind() {
		return textKind;
	}

	@Param
	public String getEventKind() {
		return eventKind;
	}

	@Param
	public String getRelationKind() {
		return relationKind;
	}

	public void setKindFeatureName(String kindFeatureName) {
		this.kindFeatureName = kindFeatureName;
	}

	public void setTextKind(String textKind) {
		this.textKind = textKind;
	}

	public void setEventKind(String eventKind) {
		this.eventKind = eventKind;
	}

	public void setRelationKind(String relationKind) {
		this.relationKind = relationKind;
	}

	public void setSchema(DocumentSchema schema) {
		this.schema = schema;
	}

	public void setEquivalenceRelationName(String equivalenceRelationName) {
		this.equivalenceRelationName = equivalenceRelationName;
	}

	public void setEquivalenceItemPrefix(String equivalenceItemPrefix) {
		this.equivalenceItemPrefix = equivalenceItemPrefix;
	}

	public void setTypeFeatureName(String typeFeatureName) {
		this.typeFeatureName = typeFeatureName;
	}

	public void setIdFeatureName(String idFeatureName) {
		this.idFeatureName = idFeatureName;
	}

	public void setTextBoundAsAnnotations(Boolean textBoundAsAnnotations) {
		this.textBoundAsAnnotations = textBoundAsAnnotations;
	}

	public void setFragmentCountFeatureName(String fragmentCountFeatureName) {
		this.fragmentCountFeatureName = fragmentCountFeatureName;
	}

	public void setTextBoundFragmentRolePrefix(String textBoundFragmentRolePrefix) {
		this.textBoundFragmentRolePrefix = textBoundFragmentRolePrefix;
	}

	public void setTriggerRole(String triggerRole) {
		this.triggerRole = triggerRole;
	}

	public void setTextDir(InputDirectory textDir) {
		this.textDir = textDir;
	}

	public void setA1Dir(InputDirectory a1Dir) {
		this.a1Dir = a1Dir;
	}

	public void setA2Dir(InputDirectory a2Dir) {
		this.a2Dir = a2Dir;
	}

	public void setCharset(String charset) {
		this.charset = charset;
	}

	public void setSectionName(String sectionName) {
		this.sectionName = sectionName;
	}
}
