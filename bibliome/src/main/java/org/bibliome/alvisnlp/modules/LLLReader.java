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

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bibliome.util.Strings;
import org.bibliome.util.Timer;
import org.bibliome.util.streams.SourceStream;

import alvisnlp.corpus.Annotation;
import alvisnlp.corpus.Corpus;
import alvisnlp.corpus.DefaultNames;
import alvisnlp.corpus.Document;
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
import alvisnlp.module.TimerCategory;
import alvisnlp.module.lib.AlvisNLPModule;
import alvisnlp.module.lib.Param;

@AlvisNLPModule
public abstract class LLLReader extends CorpusModule<ResolvedObjects> implements DocumentCreator, SectionCreator, AnnotationCreator, TupleCreator {
	private static final Pattern WORD_PATTERN = Pattern.compile("word\\((\\d+),'.*',(\\d+),(\\d+)\\)");
	private static final Pattern LEMMA_PATTERN = Pattern.compile("lemma\\((\\d+),'(.*)'\\)");
	private static final Pattern SYNTACTIC_RELATION_PATTERN = Pattern.compile("relation\\('(.*)',(\\d+),(\\d+)\\)");
	private static final Pattern AGENT_PATTERN = Pattern.compile("agent\\((\\d+)\\)");
	private static final Pattern TARGET_PATTERN = Pattern.compile("target\\((\\d+)\\)");
	private static final Pattern GENIC_INTERACTION_PATTERN = Pattern.compile("genic_interaction\\((\\d+),(\\d+)\\)");

	private SourceStream source;
	private String sectionName = "sentence";
	private String wordLayerName = DefaultNames.getWordLayer();
	private String sentenceLayerName = DefaultNames.getSentenceLayer();
	private String idFeatureName = "id";
	private String lemmaFeatureName = "lemma";
	private String dependenciesRelationName = DefaultNames.getDependencyRelationName();
	private String dependencyLabelFeatureName = DefaultNames.getDependencyLabelFeatureName();
	private String headRole = DefaultNames.getDependencyHeadRole();
	private String dependentRole = DefaultNames.getDependencyDependentRole();
	private String agentFeatureName = "agent";
	private String targetFeatureName = "target";
	private String genicInteractionRelationName = "genicInteraction";
	private String genicAgentRole = "agent";
	private String genicTargetRole = "target";

	private void error(BufferedReader r, int lineno, String msg) throws ProcessingException {
		String src = source.getStreamName(r);
		processingException("in '" + src + "', line " + lineno + ": " + msg);
	} 

	@Override
	protected ResolvedObjects createResolvedObjects(ProcessingContext<Corpus> ctx) throws ResolverException {
		return new ResolvedObjects(ctx, this);
	}

	@Override
	public void process(ProcessingContext<Corpus> ctx, Corpus corpus) throws ModuleException {
		Iterator<BufferedReader> rit = null;
		try {
			rit = source.getBufferedReaders();
		}
		catch (IOException e) {
			rethrow(e);
		}
		Timer<TimerCategory> timer = getTimer(ctx, "read", TimerCategory.LOAD_RESOURCE, true);
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
						sec = new Section(this, doc, sectionName, contents);
						Layer sentenceLayer = sec.ensureLayer(sentenceLayerName);
						new Annotation(this, sentenceLayer, 0, contents.length());
						doc = null;
						break;
					case "words":
						if (cols.size() <= 2)
							error(r, lineno, "expected at least two columns");
						if (sec == null)
							error(r, lineno, "missed sentence text");
						words.clear();
						Layer wordLayer = sec.ensureLayer(wordLayerName);
						for (int i = 1; i < cols.size(); ++i) {
							Matcher m = WORD_PATTERN.matcher(cols.get(i));
							if (!m.matches())
								error(r, lineno, "ill-formed word: " + cols.get(i));
							String id = m.group(1);
							int start = Integer.parseInt(m.group(2));
							int end = Integer.parseInt(m.group(3)) + 1;
							Annotation w = new Annotation(this, wordLayer, start, end);
							w.addFeature(idFeatureName, id);
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
							w.addFeature(lemmaFeatureName, lemma);
						}
						break;
					case "syntactic_relations":
						if (cols.size() <= 2)
							error(r, lineno, "expected at least two columns");
						if (sec == null)
							error(r, lineno, "missed sentence text");
						if (words.isEmpty())
							error(r, lineno, "missed word segmentation");
						Relation rel = sec.ensureRelation(this, dependenciesRelationName);
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
							t.addFeature(dependencyLabelFeatureName, label);
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
							w.addFeature(agentFeatureName, "yes");
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
							w.addFeature(targetFeatureName, "yes");
						}
						break;
					case "genic_interactions":
						if (sec == null)
							error(r, lineno, "missed sentence text");
						if (words.isEmpty())
							error(r, lineno, "missed word segmentation");
						rel = sec.ensureRelation(this, genicInteractionRelationName);
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
			catch (IOException e) {
				rethrow(e);
			}
		}
		timer.stop();
	}

	@Param
	public SourceStream getSource() {
		return source;
	}

	@Param(nameType=NameType.SECTION)
	public String getSectionName() {
		return sectionName;
	}

	@Param(nameType=NameType.LAYER)
	public String getWordLayerName() {
		return wordLayerName;
	}

	@Param(nameType=NameType.FEATURE)
	public String getIdFeatureName() {
		return idFeatureName;
	}

	@Param(nameType=NameType.FEATURE)
	public String getLemmaFeatureName() {
		return lemmaFeatureName;
	}

	@Param(nameType=NameType.RELATION)
	public String getDependenciesRelationName() {
		return dependenciesRelationName;
	}

	@Param(nameType=NameType.FEATURE)
	public String getDependencyLabelFeatureName() {
		return dependencyLabelFeatureName;
	}

	@Param(nameType=NameType.ARGUMENT)
	public String getHeadRole() {
		return headRole;
	}

	@Param(nameType=NameType.ARGUMENT)
	public String getDependentRole() {
		return dependentRole;
	}

	@Param(nameType=NameType.FEATURE)
	public String getAgentFeatureName() {
		return agentFeatureName;
	}

	@Param(nameType=NameType.FEATURE)
	public String getTargetFeatureName() {
		return targetFeatureName;
	}

	@Param(nameType=NameType.RELATION)
	public String getGenicInteractionRelationName() {
		return genicInteractionRelationName;
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
	public String getSentenceLayerName() {
		return sentenceLayerName;
	}

	public void setSentenceLayerName(String sentenceLayerName) {
		this.sentenceLayerName = sentenceLayerName;
	}

	public void setSource(SourceStream source) {
		this.source = source;
	}

	public void setSectionName(String sectionName) {
		this.sectionName = sectionName;
	}

	public void setWordLayerName(String wordLayerName) {
		this.wordLayerName = wordLayerName;
	}

	public void setIdFeatureName(String idFeatureName) {
		this.idFeatureName = idFeatureName;
	}

	public void setLemmaFeatureName(String lemmaFeatureName) {
		this.lemmaFeatureName = lemmaFeatureName;
	}

	public void setDependenciesRelationName(String dependenciesRelationName) {
		this.dependenciesRelationName = dependenciesRelationName;
	}

	public void setDependencyLabelFeatureName(String dependencyLabelFeatureName) {
		this.dependencyLabelFeatureName = dependencyLabelFeatureName;
	}

	public void setHeadRole(String headRole) {
		this.headRole = headRole;
	}

	public void setDependentRole(String dependentRole) {
		this.dependentRole = dependentRole;
	}

	public void setAgentFeatureName(String agentFeatureName) {
		this.agentFeatureName = agentFeatureName;
	}

	public void setTargetFeatureName(String targetFeatureName) {
		this.targetFeatureName = targetFeatureName;
	}

	public void setGenicInteractionRelationName(String genicInteractionRelationName) {
		this.genicInteractionRelationName = genicInteractionRelationName;
	}

	public void setGenicAgentRole(String genicAgentRole) {
		this.genicAgentRole = genicAgentRole;
	}

	public void setGenicTargetRole(String genicTargetRole) {
		this.genicTargetRole = genicTargetRole;
	}
}
