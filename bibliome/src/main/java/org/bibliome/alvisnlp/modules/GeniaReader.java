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
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import org.bibliome.alvisnlp.modules.bionlpst.BioNLPSTReader;
import org.bibliome.util.EquivalenceHashSets;
import org.bibliome.util.EquivalenceSets;
import org.bibliome.util.Iterators;
import org.bibliome.util.StringCat;
import org.bibliome.util.Strings;
import org.bibliome.util.filelines.FileLines;
import org.bibliome.util.filelines.InvalidFileLineEntry;
import org.bibliome.util.filelines.TabularFormat;
import org.bibliome.util.files.InputDirectory;
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
import alvisnlp.module.NameUsage;
import alvisnlp.module.ProcessingContext;
import alvisnlp.module.lib.AlvisNLPModule;
import alvisnlp.module.lib.Param;
import alvisnlp.module.types.Mapping;

@AlvisNLPModule(obsoleteUseInstead=BioNLPSTReader.class)
public abstract class GeniaReader extends CorpusModule<ResolvedObjects> implements DocumentCreator, SectionCreator, AnnotationCreator, TupleCreator {
	private Boolean readA1 = true;
	private Boolean readA2 = false;
	private String sectionName = "whole";
	private Mapping layerNames = new Mapping();
	private String idFeatureKey;
	private String typeFeatureKey;
	private SourceStream sourcePath;
	private String equivalenceRelationName = "equiv";
	private String equivalenceRolePrefix = "arg";
	private String wordLayerName = DefaultNames.getWordLayer();
	private String headRoleName = DefaultNames.getDependencyHeadRole();
	private String dependentRoleName = DefaultNames.getDependencyDependentRole();
	private String dependencyRelationName = DefaultNames.getDependencyRelationName();
	private String dependencyLabelFeatureName = DefaultNames.getDependencyLabelFeatureName();
	private String entitiesLayerName;
	private InputDirectory aDir;
	
	@Override
	protected ResolvedObjects createResolvedObjects(ProcessingContext<Corpus> ctx) throws ResolverException {
		return new ResolvedObjects(ctx, this);
	}

	@Override
	public void process(ProcessingContext<Corpus> ctx, Corpus corpus) throws ModuleException {
		try {
			if (!readA1 && !readA2)
				getLogger(ctx).warning("this module does not read A1 or A2 files!");
			for (BufferedReader r : Iterators.loop(sourcePath.getBufferedReaders())) {
				processFile(ctx, corpus, r);
				r.close();
			}
		}
		catch (IOException e) {
			rethrow(e);
		}
	}
	
	private static String getBasename(String name) {
		int slash = name.lastIndexOf(File.separatorChar);
		if (slash == -1)
			return name;
		return name.substring(slash + 1);
	}
	
	private static String getDirname(String name) {
		int slash = name.lastIndexOf(File.separatorChar);
		if (slash == -1)
			return name;
		return name.substring(0, slash);
	}
	
	private void processFile(ProcessingContext<Corpus> ctx, Corpus corpus, BufferedReader r) throws ModuleException, IOException {
		Logger logger = getLogger(ctx);
		String name = sourcePath.getStreamName(r);
		String id = getBasename(name.replaceFirst("\\.txt$", ""));
		String aDir = this.aDir == null ? getDirname(name) : this.aDir.getAbsolutePath();
		Document doc = Document.getDocument(this, corpus, id);
		StringCat strcat = new StringCat();
		while (true) {
			String line = r.readLine();
			if (line == null)
				break;
			strcat.append(line + "\n");
		}
		Section sec = new Section(this, doc, sectionName , strcat.toString());
		if (readA1 || readA2) {
			if (!name.endsWith(".txt")) {
				logger.warning("file " + name + " does not end with .txt, will not read any .a1 or .a2 file");
				return;
			}
			String prefix = aDir + File.separatorChar + id;
			GeniaFileLines fl = new GeniaFileLines(ctx);
			try {
				if (readA1)
					fl.process(new File(prefix + ".a1"), "US-ASCII", sec);
				if (readA2)
					fl.process(new File(prefix + ".a2"), "US-ASCII", sec);
			}
			catch (InvalidFileLineEntry ifle) {
				rethrow(ifle);
			}
			fl.commit(sec);
		}
	}

	@Override
	public void collectUsedNames(NameUsage nameUsage, String defaultType) throws ModuleException {
		if (layerNames != null) {
			nameUsage.addNames(NameType.LAYER, layerNames.values());
		}
	}

	private static final TabularFormat geniaTabularFormat = new TabularFormat();
	static {
		geniaTabularFormat.setMinColumns(2);
		geniaTabularFormat.setMaxColumns(3);
		geniaTabularFormat.setStrictColumnNumber(true);
		geniaTabularFormat.setNullifyEmpty(false);
		geniaTabularFormat.setSkipBlank(true);
		geniaTabularFormat.setSkipEmpty(true);
		geniaTabularFormat.setTrimColumns(true);

	}

	private final class GeniaFileLines extends FileLines<Section> {
		private final Map<String,Annotation> annotations = new HashMap<String,Annotation>();
		private final EquivalenceSets<Annotation> equiv = new EquivalenceHashSets<Annotation>();
		
		
		private GeniaFileLines(ProcessingContext<Corpus> ctx) {
			super(geniaTabularFormat, GeniaReader.this.getLogger(ctx));
		}
		
		private void commit(Section sec) {
			if (!equiv.isEmpty()) {
				Relation rel = sec.ensureRelation(GeniaReader.this, equivalenceRelationName);
				for (Set<Annotation> set : equiv.getSets()) {
					Tuple t = new Tuple(GeniaReader.this, rel);
					int i = 1;
					for (Annotation a : set) {
						String role = equivalenceRolePrefix + (i++);
						t.setArgument(role, a);
					}
				}
				equiv.clear();
			}
		}

		@Override
		public void processEntry(Section data, int lineno, List<String> entry) throws InvalidFileLineEntry {
			String id = entry.get(0);
			char cat = id.charAt(0);
			if (cat == '*') {
				List<String> info = Strings.split(entry.get(1), ' ', 3);
				if (!info.get(0).equals("Equiv"))
					return;
				String ref1 = info.get(1);
				if (!annotations.containsKey(ref1))
					return;
				String ref2 = info.get(2);
				if (!annotations.containsKey(ref2))
					return;
				Annotation a1 = annotations.get(ref1);
				Annotation a2 = annotations.get(ref2);
				equiv.setEquivalent(a1, a2);
				return;
			}
			if (cat == 'T' || cat == 'W') {
				List<String> info = Strings.split(entry.get(1), ' ', 3);
				String type = info.get(0);
				if (cat == 'W' && !type.equals("Word"))
					getLogger().warning(id + " has not type Word");
				int start = Integer.parseInt(info.get(1));
				int end = Integer.parseInt(info.get(2));
				String ln;
				if (type.equals("Word"))
					ln = wordLayerName;
				else if (layerNames.containsKey(type))
					ln = layerNames.get(type);
				else
					ln = type;
				Layer layer = data.ensureLayer(ln);
				Annotation a = new Annotation(GeniaReader.this, layer, start, end);
				a.addFeature(typeFeatureKey, type);
				a.addFeature(idFeatureKey, id);
				annotations.put(id, a);
				if (cat == 'T' && entitiesLayerName != null)
					data.ensureLayer(entitiesLayerName).add(a);
				return;
			}
			if (cat == 'E' || cat == 'R') {
				List<String> info = Strings.split(entry.get(1), ' ', -1);
				String relName = cat == 'R' ? dependencyRelationName : info.get(0);
				Relation rel = data.ensureRelation(GeniaReader.this, relName);
				Tuple t = new Tuple(GeniaReader.this, rel);
				if (cat == 'R')
					t.addFeature(dependencyLabelFeatureName, info.get(0));
				t.addFeature(idFeatureKey, id);
				for (int i = 1; i < info.size(); ++i) {
					String arg = info.get(i);
					String role;
					String argId;
					if (cat == 'E') {
						int col = arg.indexOf(':');
						if (col < 0) {
							getLogger().warning("missing colon: " + entry);
							continue;
						}
						argId = arg.substring(col + 1);
						role = arg.substring(0, col);
					}
					else {
						if (i == 1)
							role = headRoleName;
						else
							role = dependentRoleName;
						argId = arg;
					}
					if (!annotations.containsKey(argId)) {
						getLogger().warning("unknown entity " + argId + " in event " + id);
						continue;
					}
					t.setArgument(role, annotations.get(argId));
				}
				return;
			}
		}
	}

	@Param
	public Boolean getReadA1() {
		return readA1;
	}

	@Param
	public Boolean getReadA2() {
		return readA2;
	}

	@Param(nameType=NameType.SECTION)
	public String getSectionName() {
		return sectionName;
	}

	@Param(nameType=NameType.FEATURE, mandatory = false)
	public String getIdFeatureKey() {
		return idFeatureKey;
	}

	@Param(nameType=NameType.FEATURE, mandatory = false)
	public String getTypeFeatureKey() {
		return typeFeatureKey;
	}

	@Param
	public Mapping getLayerNames() {
		return layerNames;
	}

	@Param
	public SourceStream getSourcePath() {
		return sourcePath;
	}

	@Param(nameType=NameType.RELATION)
	public String getEquivalenceRelationName() {
		return equivalenceRelationName;
	}

	@Param
	public String getEquivalenceRolePrefix() {
		return equivalenceRolePrefix;
	}

	@Param(nameType=NameType.LAYER)
	public String getWordLayerName() {
		return wordLayerName;
	}

	@Param(nameType=NameType.ARGUMENT)
	public String getHeadRoleName() {
		return headRoleName;
	}

	@Param(nameType=NameType.ARGUMENT)
	public String getDependentRoleName() {
		return dependentRoleName;
	}

	@Param(nameType=NameType.RELATION)
	public String getDependencyRelationName() {
		return dependencyRelationName;
	}

	@Param(nameType=NameType.FEATURE)
	public String getDependencyLabelFeatureName() {
		return dependencyLabelFeatureName;
	}

	@Param(nameType=NameType.LAYER, mandatory=false)
	public String getEntitiesLayerName() {
		return entitiesLayerName;
	}

	@Param(mandatory=false)
	public InputDirectory getaDir() {
		return aDir;
	}

	public void setaDir(InputDirectory aDir) {
		this.aDir = aDir;
	}

	public void setEntitiesLayerName(String entitiesLayerName) {
		this.entitiesLayerName = entitiesLayerName;
	}

	public void setWordLayerName(String wordLayerName) {
		this.wordLayerName = wordLayerName;
	}

	public void setHeadRoleName(String headRoleName) {
		this.headRoleName = headRoleName;
	}

	public void setDependentRoleName(String dependentRoleName) {
		this.dependentRoleName = dependentRoleName;
	}

	public void setDependencyRelationName(String dependencyRelationName) {
		this.dependencyRelationName = dependencyRelationName;
	}

	public void setDependencyLabelFeatureName(String dependencyLabelFeatureName) {
		this.dependencyLabelFeatureName = dependencyLabelFeatureName;
	}

	public void setEquivalenceRelationName(String equivalenceRelationName) {
		this.equivalenceRelationName = equivalenceRelationName;
	}

	public void setEquivalenceRolePrefix(String equivalenceRolePrefix) {
		this.equivalenceRolePrefix = equivalenceRolePrefix;
	}

	public void setSourcePath(SourceStream sourcePath) {
		this.sourcePath = sourcePath;
	}

	public void setLayerNames(Mapping layerNames) {
		this.layerNames = layerNames;
	}

	public void setReadA1(Boolean readA1) {
		this.readA1 = readA1;
	}

	public void setReadA2(Boolean readA2) {
		this.readA2 = readA2;
	}

	public void setSectionName(String sectionName) {
		this.sectionName = sectionName;
	}

	public void setIdFeatureKey(String idFeatureKey) {
		this.idFeatureKey = idFeatureKey;
	}

	public void setTypeFeatureKey(String typeFeatureKey) {
		this.typeFeatureKey = typeFeatureKey;
	}
}
