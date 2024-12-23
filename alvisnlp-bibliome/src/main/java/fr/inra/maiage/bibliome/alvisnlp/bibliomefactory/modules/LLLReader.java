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

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
import fr.inra.maiage.bibliome.util.Strings;
import fr.inra.maiage.bibliome.util.Timer;
import fr.inra.maiage.bibliome.util.streams.SourceStream;

@AlvisNLPModule
public abstract class LLLReader extends CorpusModule<ResolvedObjects> implements DocumentCreator, SectionCreator, AnnotationCreator, TupleCreator {
	private static final Pattern WORD_PATTERN = Pattern.compile("word\\((\\d+),'.*',(\\d+),(\\d+)\\)");
	private static final Pattern LEMMA_PATTERN = Pattern.compile("lemma\\((\\d+),'(.*)'\\)");
	private static final Pattern SYNTACTIC_RELATION_PATTERN = Pattern.compile("relation\\('(.*)',(\\d+),(\\d+)\\)");
	private static final Pattern AGENT_PATTERN = Pattern.compile("agent\\((\\d+)\\)");
	private static final Pattern TARGET_PATTERN = Pattern.compile("target\\((\\d+)\\)");
	private static final Pattern GENIC_INTERACTION_PATTERN = Pattern.compile("genic_interaction\\((\\d+),(\\d+)\\)");

	private SourceStream source;
	private String section = DefaultNames.getDefaultSectionName();
	private String wordLayer = DefaultNames.getWordLayer();
	private String sentenceLayer = DefaultNames.getSentenceLayer();
	private String idFeature = "id";
	private String lemmaFeature = "lemma";
	private String dependenciesRelation = DefaultNames.getDependencyRelationName();
	private String dependencyLabelFeature = DefaultNames.getDependencyLabelFeatureName();
	private String headRole = DefaultNames.getDependencyHeadRole();
	private String dependentRole = DefaultNames.getDependencyDependentRole();
	private String agentFeature = "agent";
	private String targetFeature = "target";
	private String genicInteractionRelation = "genicInteraction";
	private String genicAgentRole = "agent";
	private String genicTargetRole = "target";

	private void error(BufferedReader r, int lineno, String msg) throws ProcessingException {
		String src = source.getStreamName(r);
		throw new ProcessingException("in '" + src + "', line " + lineno + ": " + msg);
	}

	@Override
	protected ResolvedObjects createResolvedObjects(ProcessingContext ctx) throws ResolverException {
		return new ResolvedObjects(ctx, this);
	}

	@Override
	public void process(ProcessingContext ctx, Corpus corpus) throws ModuleException {
		Timer<TimerCategory> timer = getTimer(ctx, "read", TimerCategory.LOAD_RESOURCE, true);
		try {
			Iterator<BufferedReader> rit = source.getBufferedReaders();
			while (rit.hasNext()) {
				try (BufferedReader r = rit.next()) {
					Document doc = null;
					Section sec = null;
					Map<String,Annotation> words = new LinkedHashMap<String,Annotation>();
					int lineno = 0;
					while (true) {
						String line = r.readLine();
						if (line == null)
							break;
						lineno++;
						line = line.trim();
						if (line.isEmpty())
							continue;
						if (line.charAt(0) == '%')
							continue;
						List<String> cols = Strings.split(line, '\t', -1);
						switch (cols.get(0)) {
							case "ID":
								if (cols.size() != 2)
									error(r, lineno, "expected 2 columns");
								doc = Document.getDocument(this, corpus, cols.get(1));
								sec = null;
								break;
							case "sentence":
								if (cols.size() != 2)
									error(r, lineno, "expected 2 columns");
								if (doc == null)
									error(r, lineno, "missed line with document ID");
								if (sec != null)
									error(r, lineno, "duplicate sentence");
								String contents = cols.get(1);
								sec = new Section(this, doc, section, contents);
								Layer sentenceLayer = sec.ensureLayer(this.sentenceLayer);
								new Annotation(this, sentenceLayer, 0, contents.length());
								doc = null;
								break;
							case "words":
								if (cols.size() <= 2)
									error(r, lineno, "expected at least two columns");
								if (sec == null)
									error(r, lineno, "missed sentence text");
								words.clear();
								Layer wordLayer = sec.ensureLayer(this.wordLayer);
								for (int i = 1; i < cols.size(); ++i) {
									Matcher m = WORD_PATTERN.matcher(cols.get(i));
									if (!m.matches())
										error(r, lineno, "ill-formed word: " + cols.get(i));
									String id = m.group(1);
									int start = Integer.parseInt(m.group(2));
									int end = Integer.parseInt(m.group(3)) + 1;
									Annotation w = new Annotation(this, wordLayer, start, end);
									w.addFeature(idFeature, id);
									words.put(id, w);
								}
								break;
							case "lemmas":
								if (cols.size() != words.size() + 1)
									error(r, lineno, "expected " + (words.size() + 1) + " columns");
								if (sec == null)
									error(r, lineno, "missed sentence text");
								for (int i = 1; i < cols.size(); ++i) {
									Matcher m = LEMMA_PATTERN.matcher(cols.get(i));
									if (!m.matches())
										error(r, lineno, "ill-formed lemma: " + cols.get(i));
									String id = m.group(1);
									if (!words.containsKey(id))
										error(r, lineno, "no word with id: " + id);
									Annotation w = words.get(id);
									String lemma = m.group(2);
									w.addFeature(lemmaFeature, lemma);
								}
								break;
							case "syntactic_relations":
								if (cols.size() <= 2)
									error(r, lineno, "expected at least two columns");
								if (sec == null)
									error(r, lineno, "missed sentence text");
								if (words.isEmpty())
									error(r, lineno, "missed word segmentation");
								Relation rel = sec.ensureRelation(this, dependenciesRelation);
								for (int i = 1; i < cols.size(); ++i) {
									Matcher m = SYNTACTIC_RELATION_PATTERN.matcher(cols.get(i));
									if (!m.matches())
										error(r, lineno, "ill-formed syntactic relation: " + cols.get(i));
									String headId = m.group(2);
									if (!words.containsKey(headId))
										error(r, lineno, "no word with id: " + headId);
									String dependentId = m.group(3);
									if (!words.containsKey(dependentId))
										error(r, lineno, "no word with id: " + dependentId);
									String label = m.group(1);
									Tuple t = new Tuple(this, rel);
									t.addFeature(dependencyLabelFeature, label);
									t.setArgument(headRole, words.get(headId));
									t.setArgument(dependentRole, words.get(dependentId));
								}
								break;
							case "agents":
								if (sec == null)
									error(r, lineno, "missed sentence text");
								if (words.isEmpty())
									error(r, lineno, "missed word segmentation");
								for (int i = 1; i < cols.size(); ++i) {
									Matcher m = AGENT_PATTERN.matcher(cols.get(i));
									if (!m.matches())
										error(r, lineno, "ill-formed agent: " + cols.get(i));
									String id = m.group(1);
									if (!words.containsKey(id))
										error(r, lineno, "no word with id: " + id);
									Annotation w = words.get(id);
									w.addFeature(agentFeature, "yes");
								}
								break;
							case "targets":
								if (sec == null)
									error(r, lineno, "missed sentence text");
								if (words.isEmpty())
									error(r, lineno, "missed word segmentation");
								for (int i = 1; i < cols.size(); ++i) {
									Matcher m = TARGET_PATTERN.matcher(cols.get(i));
									if (!m.matches())
										error(r, lineno, "ill-formed target: " + cols.get(i));
									String id = m.group(1);
									if (!words.containsKey(id))
										error(r, lineno, "no word with id: " + id);
									Annotation w = words.get(id);
									w.addFeature(targetFeature, "yes");
								}
								break;
							case "genic_interactions":
								if (sec == null)
									error(r, lineno, "missed sentence text");
								if (words.isEmpty())
									error(r, lineno, "missed word segmentation");
								rel = sec.ensureRelation(this, genicInteractionRelation);
								for (int i = 1; i < cols.size(); ++i) {
									Matcher m = GENIC_INTERACTION_PATTERN.matcher(cols.get(i));
									if (!m.matches())
										error(r, lineno, "ill-formed genic interaction: " + cols.get(i));
									String agentId = m.group(1);
									if (!words.containsKey(agentId))
										error(r, lineno, "no word with id: " + agentId);
									String targetId = m.group(2);
									if (!words.containsKey(targetId))
										error(r, lineno, "no word with id: " + targetId);
									Tuple t = new Tuple(this, rel);
									t.setArgument(genicAgentRole, words.get(agentId));
									t.setArgument(genicTargetRole, words.get(targetId));
								}
								break;
							default:
								error(r, lineno, "unhandled line");
						}
					}
				}
			}
		}
		catch (IOException e) {
			throw new ProcessingException(e);
		}
		timer.stop();
	}

	@Param
	public SourceStream getSource() {
		return source;
	}

	@Deprecated
	@Param(nameType=NameType.SECTION)
	public String getSectionName() {
		return section;
	}

	@Param(nameType=NameType.LAYER)
	public String getWordLayer() {
	    return this.wordLayer;
	};

	public void setWordLayer(String wordLayer) {
	    this.wordLayer = wordLayer;
	};

	@Deprecated
	@Param(nameType=NameType.LAYER)
	public String getWordLayerName() {
		return wordLayer;
	}

	@Deprecated
	@Param(nameType=NameType.FEATURE)
	public String getIdFeatureName() {
		return idFeature;
	}

	@Deprecated
	@Param(nameType=NameType.FEATURE)
	public String getLemmaFeatureName() {
		return lemmaFeature;
	}

	@Deprecated
	@Param(nameType=NameType.RELATION)
	public String getDependenciesRelationName() {
		return dependenciesRelation;
	}

	@Deprecated
	@Param(nameType=NameType.FEATURE)
	public String getDependencyLabelFeatureName() {
		return dependencyLabelFeature;
	}

	@Param(nameType=NameType.ARGUMENT)
	public String getHeadRole() {
		return headRole;
	}

	@Param(nameType=NameType.ARGUMENT)
	public String getDependentRole() {
		return dependentRole;
	}

	@Deprecated
	@Param(nameType=NameType.FEATURE)
	public String getAgentFeatureName() {
		return agentFeature;
	}

	@Deprecated
	@Param(nameType=NameType.FEATURE)
	public String getTargetFeatureName() {
		return targetFeature;
	}

	@Deprecated
	@Param(nameType=NameType.RELATION)
	public String getGenicInteractionRelationName() {
		return genicInteractionRelation;
	}

	@Param(nameType=NameType.ARGUMENT)
	public String getGenicAgentRole() {
		return genicAgentRole;
	}

	@Param(nameType=NameType.ARGUMENT)
	public String getGenicTargetRole() {
		return genicTargetRole;
	}

	@Param(nameType=NameType.LAYER)
	public String getSentenceLayer() {
	    return this.sentenceLayer;
	};

	public void setSentenceLayer(String sentenceLayer) {
	    this.sentenceLayer = sentenceLayer;
	};

	@Deprecated
	@Param(nameType=NameType.LAYER)
	public String getSentenceLayerName() {
		return sentenceLayer;
	}

	@Param(nameType=NameType.FEATURE)
	public String getIdFeature() {
		return idFeature;
	}

	@Param(nameType=NameType.FEATURE)
	public String getLemmaFeature() {
		return lemmaFeature;
	}

	@Param(nameType=NameType.FEATURE)
	public String getAgentFeature() {
		return agentFeature;
	}

	@Param(nameType=NameType.FEATURE)
	public String getTargetFeature() {
		return targetFeature;
	}

	@Param(nameType=NameType.RELATION)
	public String getDependenciesRelation() {
		return dependenciesRelation;
	}

	@Param(nameType=NameType.RELATION)
	public String getGenicInteractionRelation() {
		return genicInteractionRelation;
	}

	@Param(nameType=NameType.FEATURE)
	public String getDependencyLabelFeature() {
		return dependencyLabelFeature;
	}

	@Param(nameType=NameType.SECTION)
	public String getSection() {
		return section;
	}

	public void setSection(String section) {
		this.section = section;
	}

	public void setDependencyLabelFeature(String dependencyLabelFeature) {
		this.dependencyLabelFeature = dependencyLabelFeature;
	}

	public void setDependenciesRelation(String dependenciesRelation) {
		this.dependenciesRelation = dependenciesRelation;
	}

	public void setGenicInteractionRelation(String genicInteractionRelation) {
		this.genicInteractionRelation = genicInteractionRelation;
	}

	public void setIdFeature(String idFeature) {
		this.idFeature = idFeature;
	}

	public void setLemmaFeature(String lemmaFeature) {
		this.lemmaFeature = lemmaFeature;
	}

	public void setAgentFeature(String agentFeature) {
		this.agentFeature = agentFeature;
	}

	public void setTargetFeature(String targetFeature) {
		this.targetFeature = targetFeature;
	}

	public void setSentenceLayerName(String sentenceLayer) {
		this.sentenceLayer = sentenceLayer;
	}

	public void setSource(SourceStream source) {
		this.source = source;
	}

	public void setSectionName(String sectionName) {
		this.section = sectionName;
	}

	public void setWordLayerName(String wordLayer) {
		this.wordLayer = wordLayer;
	}

	public void setIdFeatureName(String idFeatureName) {
		this.idFeature = idFeatureName;
	}

	public void setLemmaFeatureName(String lemmaFeatureName) {
		this.lemmaFeature = lemmaFeatureName;
	}

	public void setDependenciesRelationName(String dependenciesRelationName) {
		this.dependenciesRelation = dependenciesRelationName;
	}

	public void setDependencyLabelFeatureName(String dependencyLabelFeatureName) {
		this.dependencyLabelFeature = dependencyLabelFeatureName;
	}

	public void setHeadRole(String headRole) {
		this.headRole = headRole;
	}

	public void setDependentRole(String dependentRole) {
		this.dependentRole = dependentRole;
	}

	public void setAgentFeatureName(String agentFeatureName) {
		this.agentFeature = agentFeatureName;
	}

	public void setTargetFeatureName(String targetFeatureName) {
		this.targetFeature = targetFeatureName;
	}

	public void setGenicInteractionRelationName(String genicInteractionRelationName) {
		this.genicInteractionRelation = genicInteractionRelationName;
	}

	public void setGenicAgentRole(String genicAgentRole) {
		this.genicAgentRole = genicAgentRole;
	}

	public void setGenicTargetRole(String genicTargetRole) {
		this.genicTargetRole = genicTargetRole;
	}
}
