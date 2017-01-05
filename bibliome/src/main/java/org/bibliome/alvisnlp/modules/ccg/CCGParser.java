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


package org.bibliome.alvisnlp.modules.ccg;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bibliome.alvisnlp.modules.ccg.CCGBase.CCGResolvedObjects;
import org.bibliome.util.Strings;
import org.bibliome.util.files.ExecutableFile;
import org.bibliome.util.files.InputDirectory;
import org.bibliome.util.files.InputFile;
import org.bibliome.util.files.OutputFile;
import org.bibliome.util.streams.FileSourceStream;
import org.bibliome.util.streams.FileTargetStream;
import org.bibliome.util.streams.SourceStream;
import org.bibliome.util.streams.TargetStream;

import alvisnlp.corpus.Annotation;
import alvisnlp.corpus.Corpus;
import alvisnlp.corpus.DefaultNames;
import alvisnlp.corpus.Layer;
import alvisnlp.corpus.NameType;
import alvisnlp.corpus.Relation;
import alvisnlp.corpus.Tuple;
import alvisnlp.corpus.creators.TupleCreator;
import alvisnlp.corpus.expressions.EvaluationContext;
import alvisnlp.corpus.expressions.ResolverException;
import alvisnlp.module.Module;
import alvisnlp.module.ModuleException;
import alvisnlp.module.ProcessingContext;
import alvisnlp.module.TimerCategory;
import alvisnlp.module.lib.AlvisNLPModule;
import alvisnlp.module.lib.External;
import alvisnlp.module.lib.ExternalFailureException;
import alvisnlp.module.lib.Param;
import alvisnlp.module.lib.TimeThis;

@AlvisNLPModule
public abstract class CCGParser extends CCGBase<CCGResolvedObjects> implements TupleCreator {
	private static final Pattern DEPENDENCY = Pattern.compile("\\((\\S+)(?: (\\S+))? (\\S+)_(\\d+) (\\S+)_(\\d+)(?: (\\S+))?\\)");

	private ExecutableFile executable;
	private InputDirectory parserModel;
	private InputDirectory superModel;
	private InputFile stanfordMarkedUpScript;
	private ExecutableFile stanfordScript;
	private String internalEncoding = "UTF-8";
	private Integer maxSuperCats = 500000;
	private String relationName = DefaultNames.getDependencyRelationName();
	private String labelFeatureName = DefaultNames.getDependencyLabelFeatureName();
	private String sentenceRole = DefaultNames.getDependencySentenceRole();
	private String headRole = DefaultNames.getDependencyHeadRole();
	private String dependentRole = DefaultNames.getDependencyDependentRole();
	private Boolean lpTransformation = false;
	private String supertagFeatureName = "supertag";

	@Override
	public void process(ProcessingContext<Corpus> ctx, Corpus corpus) throws ModuleException {
		try {
			Logger logger = getLogger(ctx);
			EvaluationContext evalCtx = new EvaluationContext(logger);
			List<List<Layer>> sentenceRuns = getSentences(logger, evalCtx, corpus);
			for (int run = 0; run < sentenceRuns.size(); ++run) {
				logger.info(String.format("run %d/%d", run+1, sentenceRuns.size())); 
				List<Layer> sentences = sentenceRuns.get(run);
				CCGParserExternal parserExt = new CCGParserExternal(ctx, run, getMaxLength(sentences));
				TargetStream target = new FileTargetStream(internalEncoding, parserExt.input);
				try (PrintStream out = target.getPrintStream()) {
					printSentences(ctx, out, sentences, true);
				}
				try {
					callExternal(ctx, "run-ccg", parserExt, internalEncoding, "ccg.sh");
				}
				catch (ExternalFailureException e) {
					logger.severe(e.getMessage());
					logger.severe("we know sometimes CCG accidentally the sentences");
					logger.severe("let's try to proceed anyway. No guarantee...");
					logger.severe("btw, input that caused the crash: " + parserExt.input.getAbsolutePath());
				}
				try (BufferedReader r = getBufferedReader(ctx, parserExt)) {
					readSentences(ctx, r, sentences);
				}
			}
		}
		catch (UnsupportedEncodingException uee) {
			rethrow(uee);
		}
		catch (FileNotFoundException fnfe) {
			rethrow(fnfe);
		}
		catch (IOException ioe) {
			rethrow(ioe);
		}
	}

	@Override
	protected CCGResolvedObjects createResolvedObjects(ProcessingContext<Corpus> ctx) throws ResolverException {
		return new CCGResolvedObjects(ctx, this);
	}

	private BufferedReader getBufferedReader(ProcessingContext<Corpus> ctx, CCGParser.CCGParserExternal parserExt) throws IOException, ModuleException {
		if (stanfordScript == null) {
			SourceStream source = new FileSourceStream(internalEncoding, parserExt.output);
			return source.getBufferedReader();
		}
		StanfordScriptExternal sdExt = new StanfordScriptExternal(ctx);
		callExternal(ctx, "call-stanford", sdExt, internalEncoding, "stanford.sh");
		SourceStream source = new FileSourceStream(internalEncoding, sdExt.output);
		return source.getBufferedReader();
	}
	
	@TimeThis(task="convert-parse", category=TimerCategory.COLLECT_DATA)
	protected void readSentences(ProcessingContext<Corpus> ctx, BufferedReader r, List<Layer> sentences) throws IOException {
		Iterator<Layer> sentenceIt = sentences.iterator();
		if (!sentenceIt.hasNext())
			return;
		Layer sentence = sentenceIt.next();
		List<String> dependencyLines = new ArrayList<String>();
		int lineno = 0;
		while (true) {
			String line = r.readLine();
			if (line == null) {
				while (true) {
					getLogger(ctx).warning("sentence not parsed (reached EOF): " + sentence.getSentenceAnnotation());
					if (!sentenceIt.hasNext())
						return;
					sentence = sentenceIt.next();
				}
			}
			lineno++;
			if (line.isEmpty())
				continue;
			if (line.charAt(0) == '#')
				continue;
			if (line.startsWith("<c>")) {
				List<String> tags0 = Strings.split(line.substring(4), ' ', -1);
				@SuppressWarnings({"unchecked"})
				List<String>[] tags = (List<String>[]) Array.newInstance(List.class, tags0.size());
				for (int i = 0; i < tags.length; ++i) {
					List<String> wordTag = Strings.split(tags0.get(i), '|', -1);
					if (wordTag.size() != 3) {
						getLogger(ctx).warning(Integer.toString(lineno) + ": unexpected word tag: " + tags[i]);
						continue;
					}
					tags[i] = wordTag;
				}
				while (!readSupertags(tags, sentence)) {
					getLogger(ctx).warning(Integer.toString(lineno) + ": sentence not parsed: " + sentence.getSentenceAnnotation());
					if (!sentenceIt.hasNext())
						return;
					sentence = sentenceIt.next();
				}
				getLogger(ctx).finer(Integer.toString(lineno) + ": sentence: " + sentence.getSentenceAnnotation().getForm());
				List<Dependency> originalDependencies = new ArrayList<Dependency>();
				for (String depLine : dependencyLines) {
					Dependency dep = readDependency(depLine, sentence);
					if (dep == null)
						getLogger(ctx).warning(Integer.toString(lineno) + ": could not parse dependency: " + line);
					else
						originalDependencies.add(dep);
				}
				Relation rel = sentence.getSection().ensureRelation(this, relationName);
				Annotation sentenceAnnotation = sentence.getSentenceAnnotation();
				List<Dependency> dependencies = lpTransformation ? convertSentenceDependencies(ctx, originalDependencies) : originalDependencies;
				for (Dependency dep : dependencies)
					dep.addTuple(rel, sentenceAnnotation);
				if (!sentenceIt.hasNext())
					return;
				sentence = sentenceIt.next();
				dependencyLines.clear();
				continue;
			}
			dependencyLines.add(line);
		}
	}

	private Dependency readDependency(String line, Layer sentence) {
		Matcher m = DEPENDENCY.matcher(line);
		if (!m.matches())
			return null;
		String label = m.group(1);
		int headIndex = Integer.parseInt(m.group(4));
		Annotation head = sentence.get(headIndex);
		int depIndex = Integer.parseInt(m.group(6));
		Annotation dep = sentence.get(depIndex);
		return new Dependency(label, head, dep);
	}

	private boolean readSupertags(List<String>[] tags, Layer sentence) {
		if (tags.length != sentence.size()) {
			System.err.println("bad length: " + tags.length + " / " + sentence.size());
			return false;
		}
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < tags.length; ++i) {
			String form = tags[i].get(0);
			sb.setLength(0);
			Strings.escapeWhitespaces(sb, sentence.get(i).getForm(), '|', '.');
			if (!form.equals(sb.toString())) {
				System.err.println("bad form: " + form + " / " + sb.toString());
				return false;
			}
		}
		for (int i = 0; i < tags.length; ++i)
			sentence.get(i).addFeature(supertagFeatureName, tags[i].get(2));
		return true;
	}

	private final class StanfordScriptExternal implements External<Corpus> {
		private final OutputFile input;
		private final String output;
		private final ProcessingContext<Corpus> ctx;

		private StanfordScriptExternal(ProcessingContext<Corpus> ctx) {
			super();
			this.ctx = ctx;
			File tmp = getTempDir(ctx);
			input = new OutputFile(tmp, "corpus.dep");
			output = tmp + "/corpus.sd";
		}

		@Override
		public Module<Corpus> getOwner() {
			return CCGParser.this;
		}

		@Override
		public String[] getCommandLineArgs() throws ModuleException {
			return new String[] {
					stanfordScript.getAbsolutePath(),
					"--ccgbank",
					input.getAbsolutePath()
			};
		}

		@Override
		public String[] getEnvironment() throws ModuleException {
			return null;
		}

		@Override
		public File getWorkingDirectory() throws ModuleException {
			return null;
		}

		@Override
		public void processOutput(BufferedReader out, BufferedReader err) throws ModuleException {
			try {
				getLogger(ctx).info("reading Stanford Script output");
				TargetStream target = new FileTargetStream(internalEncoding, this.output);
				PrintStream output = target.getPrintStream();
				while (true) {
					String line = out.readLine();
					if (line == null)
						break;
					output.println(line);
				}
				output.close();
			}
			catch (FileNotFoundException fnfe) {
				rethrow(fnfe);
			}
			catch (IOException ioe) {
				rethrow(ioe);
			}
		}
	}

	private final class CCGParserExternal implements External<Corpus> {
		private final int maxLength;
		private final OutputFile input;
		private final InputFile output;
		private final File log;
		private final ProcessingContext<Corpus> ctx;

		private CCGParserExternal(ProcessingContext<Corpus> ctx, int n, int maxLength) {
			super();
			this.ctx = ctx;
			this.maxLength = maxLength;
			File tmp = getTempDir(ctx);
			String h = String.format("corpus_%8H", n);
			input = new OutputFile(tmp, h + ".txt");
			output = new InputFile(tmp, h + ".dep");
			log = new File(tmp, "ccg.log");
		}

		@Override
		public Module<Corpus> getOwner() {
			return CCGParser.this;
		}

		@Override
		public String[] getCommandLineArgs() throws ModuleException {
			List<String> clArgs = new ArrayList<String>();
			clArgs.addAll(Arrays.asList(
					executable.getAbsolutePath(),
					"--model",
					parserModel.getAbsolutePath(),
					"--super",
					superModel.getAbsolutePath(),
					"--parser-maxsupercats",
					maxSuperCats.toString(),
					"--input",
					input.getAbsolutePath(),
					"--output",
					output.getAbsolutePath(),
					"--parser-maxwords",
					Integer.toString(maxLength * 2),
					"--super-maxwords",
					Integer.toString(maxLength * 2),
					"--log",
					log.getAbsolutePath()
			));
			if (stanfordMarkedUpScript != null) {
				clArgs.add("--parser-markedup");
				clArgs.add(stanfordMarkedUpScript.getAbsolutePath());
			}
			return clArgs.toArray(new String[clArgs.size()]);
		}

		@Override
		public String[] getEnvironment() throws ModuleException {
			return null;
		}

		@Override
		public File getWorkingDirectory() throws ModuleException {
			return null;
		}

		@Override
		public void processOutput(BufferedReader out, BufferedReader err) throws ModuleException {
			Logger logger = getLogger(ctx);
			try {
				logger.fine("CCG standard error:");
				for (String line = err.readLine(); line != null; line = err.readLine()) {
					logger.fine("    " + line);
				}
				logger.fine("end of CCG standard error");
			}
			catch (IOException ioe) {
				logger.warning("could not read CCG standard error: " + ioe.getMessage());
			}
		}
	}











	@Param
	public ExecutableFile getExecutable() {
		return executable;
	}

	@Param
	public InputDirectory getParserModel() {
		return parserModel;
	}

	@Param
	public InputDirectory getSuperModel() {
		return superModel;
	}

	@Param(mandatory=false)
	public InputFile getStanfordMarkedUpScript() {
		return stanfordMarkedUpScript;
	}

	@Param(mandatory=false)
	public ExecutableFile getStanfordScript() {
		return stanfordScript;
	}

	@Param
	public String getInternalEncoding() {
		return internalEncoding;
	}

	@Param
	public Integer getMaxSuperCats() {
		return maxSuperCats;
	}

	@Param(nameType=NameType.RELATION)
	public String getRelationName() {
		return relationName;
	}

	@Param(nameType=NameType.FEATURE)
	public String getLabelFeatureName() {
		return labelFeatureName;
	}

	@Param(nameType=NameType.ARGUMENT)
	public String getSentenceRole() {
		return sentenceRole;
	}

	@Param(nameType=NameType.ARGUMENT)
	public String getHeadRole() {
		return headRole;
	}

	@Param(nameType=NameType.ARGUMENT)
	public String getDependentRole() {
		return dependentRole;
	}

	@Param
	public Boolean getLpTransformation() {
		return lpTransformation;
	}

	public void setLpTransformation(Boolean lpTransformation) {
		this.lpTransformation = lpTransformation;
	}

	public void setExecutable(ExecutableFile executable) {
		this.executable = executable;
	}

	public void setParserModel(InputDirectory parserModel) {
		this.parserModel = parserModel;
	}

	public void setSuperModel(InputDirectory superModel) {
		this.superModel = superModel;
	}

	public void setStanfordMarkedUpScript(InputFile stanfordMarkedUpScript) {
		this.stanfordMarkedUpScript = stanfordMarkedUpScript;
	}

	public void setStanfordScript(ExecutableFile stanfordScript) {
		this.stanfordScript = stanfordScript;
	}

	public void setInternalEncoding(String internalEncoding) {
		this.internalEncoding = internalEncoding;
	}

	public void setMaxSuperCats(Integer maxSuperCats) {
		this.maxSuperCats = maxSuperCats;
	}

	public void setRelationName(String relationName) {
		this.relationName = relationName;
	}

	public void setLabelFeatureName(String labelFeatureName) {
		this.labelFeatureName = labelFeatureName;
	}

	public void setSentenceRole(String sentenceRole) {
		this.sentenceRole = sentenceRole;
	}

	public void setHeadRole(String headRole) {
		this.headRole = headRole;
	}

	public void setDependentRole(String modifierRole) {
		this.dependentRole = modifierRole;
	}







	public class Dependency {
		private final String dep;
		private final Annotation a1;
		private final Annotation a2;


		public Dependency(String dep, Annotation a1, Annotation a2) {
			super();
			this.a1 = a1;
			this.a2 = a2;
			this.dep = dep;
		}

		void addTuple(Relation rel, Annotation sentence) {
			Tuple t = new Tuple(CCGParser.this, rel);
			t.setArgument(headRole, a1);
			t.setArgument(dependentRole, a2);
			t.setArgument(sentenceRole, sentence);
			t.addFeature(labelFeatureName, dep);
		}

		@Override
		public String toString() {
			return Strings.join(new String[] {
					dep, getArg1(), getPOS1(), getSTag1(), getArg2(), getPOS2(), getSTag2()
			}, '\t');
		}

		public boolean matchArg1(String dep, Annotation w) {
			return a1 == w && this.dep.equals(dep);
		}

		public boolean matchArg2(String dep, Annotation w) {
			return a2 == w && this.dep.equals(dep);
		}

		public String getDep() {
			return this.dep;
		}

		public String getArg1() {
			return a1.getForm();
		}

		public String getArg2() {
			return a2.getForm();
		}

		public String getPOS1() {
			return a1.getLastFeature(getPosFeatureName());
		}

		public String getPOS2() {
			return a2.getLastFeature(getPosFeatureName());
		}

		public String getSTag1() {
			return a1.getLastFeature(supertagFeatureName);
		}

		public String getSTag2() {
			return a2.getLastFeature(supertagFeatureName);
		}
	}

	Dependency fromArg1Arg1(String dep, Dependency rel1, Dependency rel2) {
		return new Dependency(dep, rel1.a1, rel2.a1);
	}

	Dependency fromArg1Arg2(String dep, Dependency rel1, Dependency rel2) {
		return new Dependency(dep, rel1.a1, rel2.a2);
	}

	Dependency fromArg2Arg1(String dep, Dependency rel1, Dependency rel2) {
		return new Dependency(dep, rel1.a2, rel2.a1);
	}

	Dependency fromArg2Arg2(String dep, Dependency rel1, Dependency rel2) {
		return new Dependency(dep, rel1.a2, rel2.a2);
	}

	Dependency fromArg1Arg2(String dep, Dependency rel) {
		return fromArg1Arg2(dep, rel, rel);
	}

        Dependency switchArgs(String dep, Dependency rel) {
                return new Dependency(dep, rel.a2, rel.a1);
        }


	List<Dependency> convertSentenceDependencies(ProcessingContext<Corpus> ctx, List<Dependency> dependencies) {
		List<Dependency> newRelations = new ArrayList<Dependency>();
		skip= 0;
		for (int i=0; i < dependencies.size(); i++) {
			if (skip > 0) {
				i= i+skip;
				skip= 0;
			}
			if (i >= dependencies.size()) {
				break;
			}
			newRelations.addAll(convertDependency(ctx, dependencies, i));
		}
		return newRelations;
	}

	Collection<Dependency> convertDependency(ProcessingContext<Corpus> ctx, List<Dependency> dependencies, int i) {
		Dependency curr = dependencies.get(i);
		String currDep= curr.getDep();
		String currArg1= curr.getArg1();
		String currArg2= curr.getArg2();
		String currPOS1= curr.getPOS1();
		String currPOS2= curr.getPOS2();
		String currSTag1= curr.getSTag1();
		String currSTag2= curr.getSTag2();

		if (currDep.equals("ncsubj")) {
			// Look for rule [NCSUBJ word1 word2] + [XCOMP word1 word3]  = [MOD_PRED word2 word3]
			//Dependency temp= containsRelationArgOne(dependencies, "xcomp", curr.a1, 0);
			//List<Dependency> result= new ArrayList<Dependency>();
			//if (temp != null) {
			//	List<Dependency> result= new ArrayList<Dependency>();
			//	if (noun(currPOS2)) {
			//		if (noun(temp.getPOS2())) {
						//result.add(fromArg2Arg2("MOD_PRED:N-N", curr, temp));
			//			result.add(fromArg1Arg2("SUBJ:" + getLP2LPPOS(currPOS1, currSTag1) + "-N", curr));
						//result.add(fromArg1Arg2("XCOMP:" + getLP2LPPOS(temp.getPOS1(), temp.getSTag1()) + "-N", temp));
			//		}
			//		if (temp.getPOS2().startsWith("JJ")) {
						//result.add(fromArg2Arg2("MOD_PRED:N-ADJ", curr, temp));
			//			result.add(fromArg1Arg2("SUBJ:" + getLP2LPPOS(currPOS1, currSTag1) + "-N", curr));
						//result.add(fromArg1Arg2("XCOMP:" + getLP2LPPOS(temp.getPOS1(), temp.getSTag1()) + "-ADJ", temp));
			//		}
			//		if (result.size() > 0 )
			//			return result;
                        //	}

			//	return result;
				//else {
				//	return Collections.singleton(fromArg1Arg2("XSUBJ:" + getLP2LPPOS(currPOS1, currSTag1) + "-" + getLP2LPPOS(currPOS2, currSTag2), curr));
				//}
				// Verbs like "seemed" and "appear" will try and form a mod_pred with a verb and a noun
				// This is not an error, but just need to consider the SUBJ relation 
		//	}
			// If  no XCOMP found, NCSUBJ designates a SUBJ relation
			//***********************************************************************************************
			// TO DO: If have ncsubj (without possibility of a MOD_PRED relation), with an incorrect tag,
			// this signifies an tagging error
			// Count this as a subject relation and change the POS tag of the verb
			//***********************************************************************************************
			if ((currPOS1.startsWith("V") || currPOS1.equals("MD")) && noun(currPOS2)) {
				if (passive(currSTag1))
					return Collections.singleton(fromArg1Arg2("SUBJ:V_PASS-N", curr));
				return Collections.singleton(fromArg1Arg2("SUBJ:V-N", curr));
			}
			// TEST: change the POS and re-parse this sentence
			//else {
				//currDep.a1.addFeature(getPosTagFeatureName(), "");
			//}
			//getLogger(ctx).warning("have a subject relation (" + currArg1 + ", " + currArg2 + ") that does not have a verb/modal as its first arg!!!");
			return Collections.emptyList();
		}

               if (currDep.equals("cmod")) {
			Dependency temp= containsRelationArgOne(dependencies, "ccomp", curr.a2, 0);
			if (temp == null) {
				return Collections.singleton(fromArg1Arg2("CMOD:" + getLP2LPPOS(currPOS1, currSTag1) + "-" + getLP2LPPOS(currPOS2, currSTag2), curr));
			}
                         //       if (noun(currPOS1)) {
                          //              if (noun(temp.getPOS1()))
                            //                    return Collections.singleton(fromArg1Arg2("MOD_PRED:N-N", curr, temp));
                              //          if (temp.getPOS1().startsWith("JJ"))
                                //                return Collections.singleton(fromArg1Arg2("MOD_PRED:N-ADJ", curr, temp));
                                //}
			//}
                        if ((currPOS2.startsWith("V") || currPOS2.equals("MD")) && noun(currPOS1)) {
                                if (passive(currSTag2))
                                       return Collections.singleton(switchArgs("SUBJ:V_PASS-N", curr));
                               return Collections.singleton(switchArgs("SUBJ:V-N", curr));
                        }
			//else {
			//	return Collections.singleton(fromArg1Arg2("CMOD:" + getLP2LPPOS(currPOS1, currSTag1) + "-" + getLP2LPPOS(currPOS2, currSTag2), curr));
			//}
                }


		if (currDep.equals("dobj") || currDep.equals("iobj")) {
			// If DOBJ or IOBJ have a preposition as the first argument, 
			// look for the rules:
			// [DOBJ PREP word1] + [NCMOD word2 PREP] = [COMP_prep word1 word2]
			// [IOBJ PREP word1] + [NCMOD word2 PREP] = [COMP_prep word1 word2]
			// Look for different COMP relations, based on the POS of the arguments; prepositions have IN or TO as POS tags
			if (currPOS1.startsWith("IN") || currPOS1.equals("TO") || currArg1.equals("-")) {
				Dependency temp= containsRelationArgTwo(dependencies, "ncmod", curr.a1, 0);
				if (temp != null) {
					if (temp.getPOS1().startsWith("JJ") && noun(currPOS2))
						return Collections.singleton(fromArg1Arg2("COMP_" + currArg1.toLowerCase() + ":ADJ-N", temp, curr));
					if (temp.getPOS1().contains("RB") && noun(currPOS2))		
						return Collections.singleton(fromArg1Arg2("COMP_" + currArg1.toLowerCase() + ":ADV-N", temp, curr));
					if (noun(temp.getPOS1()) && noun(currPOS2))		
						return Collections.singleton(fromArg1Arg2("COMP_" + currArg1.toLowerCase() + ":N-N", temp, curr));
					if (noun(temp.getPOS1()) && currPOS2.startsWith("V")) {	
						if (passive(currSTag2))	
							return Collections.singleton(fromArg1Arg2("COMP_" + currArg1.toLowerCase() + ":N-V_PASS", temp, curr));
						return Collections.singleton(fromArg1Arg2("COMP_" + currArg1.toLowerCase() + ":N-V", temp, curr));
					}
					if (temp.getPOS1().startsWith("V") && noun(currPOS2)) {
						if (passive(currSTag1))	
							return Collections.singleton(fromArg1Arg2("COMP_" + currArg1.toLowerCase() + ":V_PASS-N", temp, curr));
						return Collections.singleton(fromArg1Arg2("COMP_" + currArg1.toLowerCase() + ":V-N", temp, curr));
					}				
					if (temp.getPOS1().startsWith("V") && currPOS2.startsWith("V")) {
						if (passive(temp.getSTag1())) {
							if (passive(currSTag2))
								return Collections.singleton(fromArg1Arg2("COMP_" + currArg1.toLowerCase() + ":V_PASS-V_PASS", temp, curr));
							return Collections.singleton(fromArg1Arg2("COMP_" + currArg1.toLowerCase() + ":V_PASS-V", temp, curr));
						}
						if (passive(currSTag2))
							return Collections.singleton(fromArg1Arg2("COMP_" + currArg1.toLowerCase() + ":V-V_PASS", temp, curr));
						return Collections.singleton(fromArg1Arg2("COMP_" + currArg1.toLowerCase() + ":V-V", temp, curr));
					}
					// Have a dobj/iobj with an IN/TO arg, but cannot find the corresponging
					// NCMOD to make the COMP relation
					getLogger(ctx).warning("have comp_" + currArg1 + " relation with (" + temp.getArg1() + "-" + temp.getPOS1() + ", " + currArg2 + "-" + currPOS2 + ")!!!");
					return Collections.emptyList();
				}
				// Look for rule [DOBJ PREP word1] + [IOBJ word 2 PREP] = [COMP_prep word1 word2]
				temp= containsRelationArgTwo(dependencies, "iobj", curr.a1, i);
				if (temp != null) {
					// Get the POS format needed for the tranformation
					String lp2lpPOSs= ":" +  getLP2LPPOS(temp.getPOS1(), temp.getSTag1()) + "-"  + getLP2LPPOS(currPOS2, currSTag2);
					return Collections.singleton(fromArg1Arg2("COMP_" + currArg1.toLowerCase() + lp2lpPOSs, temp, curr));
					//newRelations.add("COMP_" + currArg1 + ":" + getLP2LPPOS(currPOS1, currSTag1) + "-" +  getLP2LPPOS(temp.getPOS2(), temp.getSTag2()) + "(" + currArg2 + ", "  + temp.getArg2() + ")");
					//return Collections.emptyList();
				}
				getLogger(ctx).warning("cannot find ncmod/iobj for dependency " + currDep + ", arg1:" + currArg1 + ", arg2:" + currArg2 + " relation to complete dobj comp relation!!!");
				return Collections.emptyList();
			}
			// Have APPOS relation [DOBJ ( word1] + [NCMOD word2 (] = APPOS
			if (currArg1.equals("(")) {
				Dependency temp= containsRelationArgTwo(dependencies, "ncmod", curr.a1, 0);
				if (temp != null)
					return Collections.singleton(fromArg2Arg1("APPOS:N-N", curr, temp));
				getLogger(ctx).warning("have appos relation with missing ncmod arg!!!");
				return Collections.emptyList();
			}
			// Have DOBJ relation
			if (currDep.equals("dobj") && currPOS1.startsWith("V") && (noun(currPOS2) || currPOS2.startsWith("JJ"))) {
				if (passive(currSTag1))
					return Collections.singleton(fromArg1Arg2("OBJ:V_PASS-N", curr));
				return Collections.singleton(fromArg1Arg2("OBJ:V-N", curr));
			}
			
			// If have [IOBJ word1 prep], ignore this (part of DOBJ + IOBJ relation)
			if (!currDep.equals("iobj") && (!currPOS2.equals("IN") || !currPOS2.equals("TO"))) {
				getLogger(ctx).warning("have a DOBJ (OBJ); dependency=" + currDep + " between (" + currArg1 + " with pos " + currPOS1 + ", " + currArg2 + " with pos " + currPOS2 + ")!!!");
				return Collections.emptyList();
			}
		}

		// Have DOBJ relation, designated by obj2
		if (currDep.equals("obj2")) {
			if (currPOS1.startsWith("V") && noun(currPOS2)) {
				if (passive(currSTag1))
					return Collections.singleton(fromArg1Arg2("OBJ:V_PASS-N", curr));
				return Collections.singleton(fromArg1Arg2("OBJ:V-N", curr));
			}
			getLogger(ctx).warning("have a dobj from OBJ2 between " + currArg1 + " and " + currArg2 + "!!!");
			return Collections.emptyList();
		}

		// NCMOD relation, without a preposition, a bracket, modal, coordination or determiner as one of its args is a MOD relation
		// Ignore relations with Modals (i.e. 'may')
		if (currDep.equals("ncmod") && !currPOS2.startsWith("IN") && !currPOS1.equals("MD") && !currPOS2.equals("TO") && !currArg2.equals("(") && !currPOS1.equals("CC") && !currPOS2.equals("CC") && !currPOS1.equals("DT") && !currPOS2.equals("DT") && !currPOS2.equals("CC")) {
			if (currArg2.equals("not") && currPOS2.equals("RB")) {
				Dependency temp= containsRelationArgOne(dependencies, "dobj", curr.a1, 0);
				if (temp != null) 
					// Loosened restriction for not relation: don't care about the POS of the second arg
					//if (temp.getPOS2().startsWith("V"))
					return Collections.singleton(fromArg2Arg2("NEG", temp, curr));
				//getLogger().warning("Problem with NOT relation; have " + temp.getArg2() + " as first arg!!!");	
				//return Collections.emptyList();
				//getLogger().warning("Problem with NOT relations; cannot find dobj with arg " + currArg1 + "!!!");
				return Collections.singleton(fromArg1Arg2("NEG",curr));
			}
			// If the dependent is a number (POS=CD), then this is a special type of mod relation
			// called NUM
			if (currPOS2.equals("CD"))
				return Collections.singleton(fromArg1Arg2("MOD:" + getLP2LPPOS(currPOS2, currSTag2) + "-N", curr));
			if (noun(currPOS1) && currPOS2.startsWith("RB"))
				return Collections.singleton(fromArg1Arg2("MOD:N-ADJ", curr));
			if (currPOS1.startsWith("JJ") && currPOS2.startsWith("RB"))
				return Collections.singleton(fromArg1Arg2("MOD:ADJ-ADV", curr));
			if (currPOS1.startsWith("JJ") && noun(currPOS2))
				return Collections.singleton(fromArg1Arg2("MOD:ADJ-N", curr));
			if (currPOS1.contains("RB") && currPOS2.startsWith("RB"))
				return Collections.singleton(fromArg1Arg2("MOD:ADV-ADV", curr));
			if (currPOS1.startsWith("V") && currPOS2.startsWith("RB"))
				return Collections.singleton(fromArg1Arg2("MOD:V-ADV", curr));
			if (noun(currPOS1) && currPOS2.startsWith("JJ")) {
				if (curr.a1.getStart() > curr.a2.getStart())
					return Collections.singleton(fromArg1Arg2("MOD:N-ADJ", curr));
				// If the adjectival modifier comes after the noun it modifies 
				// then the relation is MOD_POST
				return Collections.singleton(fromArg1Arg2("MOD:N-ADJ", curr));
			}				
			if (noun(currPOS1) && noun(currPOS2))
				return Collections.singleton(fromArg1Arg2("MOD:N-N", curr));


			// When have ncmod (noun, gerund verb), get MOD_ATT:N-ADJ (noun, gerund verb)
			// This is a correction of the tagger error - the gerund should be tagged JJ not VBG
			//*************************************************************************************
			// CHANGE POS TAG (to do)
			//*************************************************************************************
			if (noun(currPOS1) && currPOS2.equals("VBG"))
				return Collections.singleton(fromArg1Arg2("MOD:N-ADJ", curr)); 
			//else {
			//	return Collections.singleton(fromArg1Arg2("NMOD:" + getLP2LPPOS(currPOS2, currSTag2) + "-" + getLP2LPPOS(currPOS1, currSTag1), curr));
			//}
			//getLogger(ctx).warning("have a ncmod between " + currArg1 + " and " + currArg2 + "!!!");
			return Collections.emptyList();
		}

		// Look for relation [XCOMP PREP word1] + [XMOD word2 PREP] = [COMP_prep word1 word2]
		if (currDep.equals("xcomp")) {
			if (currPOS1.startsWith("IN") || currPOS1.equals("TO") || currArg1.equals("-")) {
				Dependency temp= containsRelationArgTwo(dependencies, "xmod", curr.a1, 0);
				if (temp != null) {
					if (temp.getPOS1().startsWith("JJ") && noun(currPOS2))		
						return Collections.singleton(fromArg1Arg2("COMP_" + currArg1.toLowerCase() + ":ADJ-N", temp, curr));
					if (temp.getPOS1().contains("RB") && noun(currPOS2))		
						return Collections.singleton(fromArg1Arg2("COMP_" + currArg1.toLowerCase() + ":ADV-N", temp, curr));
					if (noun(temp.getPOS1()) && noun(currPOS2))		
						return Collections.singleton(fromArg1Arg2("COMP_" + currArg1.toLowerCase() + ":N-N", temp, curr));
					if (noun(temp.getPOS1()) && currPOS2.startsWith("V")) {	
						if (passive(currSTag2))	
							return Collections.singleton(fromArg1Arg2("COMP_" + currArg1.toLowerCase() + ":N-V_PASS", temp, curr));
						return Collections.singleton(fromArg1Arg2("COMP_" + currArg1.toLowerCase() + ":N-V", temp, curr));
					}
					if (temp.getPOS1().startsWith("V") && noun(currPOS2)) {	
						if (passive(currSTag1))
							return Collections.singleton(fromArg1Arg2("COMP_" + currArg1.toLowerCase() + ":V_PASS-N", temp, curr));
						return Collections.singleton(fromArg1Arg2("COMP_" + currArg1.toLowerCase() + ":V-N", temp, curr));
					}				
					if (temp.getPOS1().startsWith("V") && currPOS2.startsWith("V")) {
						if (passive(temp.getSTag1())) {
							if (passive(currSTag2))
								return Collections.singleton(fromArg1Arg2("COMP_" + currArg1.toLowerCase() + ":V_PASS-V_PASS", temp, curr));
							return Collections.singleton(fromArg1Arg2("COMP_" + currArg1.toLowerCase() + ":V_PASS-V", temp, curr));
						}
						if (passive(currSTag2))
							return Collections.singleton(fromArg1Arg2("COMP_" + currArg1.toLowerCase() + ":V-V_PASS", temp, curr));
						return Collections.singleton(fromArg1Arg2("COMP_" + currArg1.toLowerCase() + ":V-V", temp, curr));
					}
					getLogger(ctx).warning("have xcomp comp relation with " + temp.getArg1() + " and " + currArg2 + "!!!");
					return Collections.emptyList();
				}
			}
			if (!currPOS1.startsWith("IN") && !currPOS1.equals("TO")) {
				return Collections.singleton(fromArg1Arg2("XCOMP:" + getLP2LPPOS(currPOS1, currSTag2) + "-" + getLP2LPPOS(currPOS2, currSTag2), curr));
			}
		}

		// Look for relation [CCOMP PREP word1] + [CMOD word2 PREP] = [COMP_prep word1 word2]
		if (currDep.equals("ccomp")) {
			if (currPOS1.startsWith("IN") || currPOS1.equals("TO") || currArg1.equals("-")) {
				Dependency temp= containsRelationArgTwo(dependencies, "cmod", curr.a1, 0);
				if (temp != null) {
					if (temp.getPOS1().startsWith("JJ") && noun(currPOS2))
						return Collections.singleton(fromArg1Arg2("COMP_" + currArg1.toLowerCase() + ":ADJ-N", temp, curr));
					if (temp.getPOS1().contains("RB") && noun(currPOS2))		
						return Collections.singleton(fromArg1Arg2("COMP_" + currArg1.toLowerCase() + ":ADV-N", temp, curr));
					if (noun(temp.getPOS1()) && noun(currPOS2))		
						return Collections.singleton(fromArg1Arg2("COMP_" + currArg1.toLowerCase() + ":N-N", temp, curr));
					if (noun(temp.getPOS1()) && currPOS2.startsWith("V")) {	
						if (passive(currSTag2))
							return Collections.singleton(fromArg1Arg2("COMP_" + currArg1.toLowerCase() + ":N-V_PASS", temp, curr));
						return Collections.singleton(fromArg1Arg2("COMP_" + currArg1.toLowerCase() + ":N-V", temp, curr));
					}
					if (temp.getPOS1().startsWith("V") && noun(currPOS2)) {	
						if (passive(currSTag1))
							return Collections.singleton(fromArg1Arg2("COMP_" + currArg1.toLowerCase() + ":V_PASS-N", temp, curr));
						return Collections.singleton(fromArg1Arg2("COMP_" + currArg1.toLowerCase() + ":V-N", temp, curr));
					}				
					if (temp.getPOS1().startsWith("V") && currPOS2.startsWith("V")) {
						if (passive(temp.getSTag1())) {
							if (passive(currSTag2))
								return Collections.singleton(fromArg1Arg2("COMP_" + currArg1.toLowerCase() + ":V_PASS-V_PASS", temp, curr));
							return Collections.singleton(fromArg1Arg2("COMP_" + currArg1.toLowerCase() + ":V_PASS-V", temp, curr));
						}
						if (passive(currSTag2))
							return Collections.singleton(fromArg1Arg2("COMP_" + currArg1.toLowerCase() + ":V-V_PASS", temp, curr));
						return Collections.singleton(fromArg1Arg2("COMP_" + currArg1.toLowerCase() + ":V-V", temp, curr));
					}
					getLogger(ctx).warning("have ccomp comp relation with " + temp.getArg1() + " and " + currArg2 + "!!!");
					return Collections.emptyList();
				}
			}
			if (!currPOS1.startsWith("IN") && !currPOS1.equals("TO")) {
				return Collections.singleton(fromArg1Arg2("CCOMP:" + getLP2LPPOS(currPOS1, currSTag2) + "-" + getLP2LPPOS(currPOS2, currSTag2), curr));
			}
		}
		if (currDep.equals("xmod")) {
			if (!currPOS2.startsWith("IN") && !currPOS2.equals("TO")) {
				boolean alreadyContains= containsRelation(dependencies, "ncsubj", curr.a2, curr.a1);
				//getLogger(ctx).warning("Did I find the relation?: " + alreadyContains + ", " + curr.a1 + " " + curr.a2);
				//if (!alreadyContains) {
				//	if (passive(currSTag1))
                                 //      		return Collections.singleton(switchArgs("SUBJ:V_PASS-N", curr));
                               	//	return Collections.singleton(switchArgs("SUBJ:V-N", curr));
				//}
				if (noun(getLP2LPPOS(currPOS1, currSTag1)) && getLP2LPPOS(currPOS2, currSTag2).startsWith("V") && !alreadyContains)
					return Collections.singleton(switchArgs("SUBJ:" + getLP2LPPOS(currPOS2, currSTag2) + "-N", curr));
				if (!(noun(getLP2LPPOS(currPOS1, currSTag1)) && getLP2LPPOS(currPOS2, currSTag2).startsWith("V")))
					return Collections.singleton(fromArg1Arg2("XMOD:" + getLP2LPPOS(currPOS1, currSTag1) + "-" + getLP2LPPOS(currPOS2, currSTag2), curr));
			}
		}

		// Auxiliary relation
		// ************************************************************
		// Current version of LP2LP
		//************************************************************
		if (currDep.equals("aux")) {
			return Collections.singleton(fromArg1Arg2("AUX:V-V",curr));
		}

		// Relative modifier, designated with the relation "CMOD" with 3 full arguments
		// (found in CCG as , which has been changed to relmod with the second and third arguments)
		if (currDep.equals("relmod"))
			return Collections.singleton(fromArg1Arg2("CMOD:" + getLP2LPPOS(currPOS1, currSTag1) + "-" + getLP2LPPOS(currPOS2, currSTag2), curr));

		// Conjunction relation can designate a coordination or an apposition
		if (currDep.equals("conj")) {
			Dependency prev= null;
			if (i >= 1) {
				prev = dependencies.get(i - 1);
			}
			Dependency next= null;
			Dependency nextNext= null;
			if (i < dependencies.size() - 1) {
				next= dependencies.get(i + 1);
				if (i < dependencies.size() - 2) {
					nextNext= dependencies.get(i + 2);
				}
			}

                        // If the conj relation has a non-comma first argument (i.e. will have a conjunct as "and" or "or" as its arg)
                        // than this represents a coordination
                        // CCG does a factoring of all the possibilities, want to condense this into a single relation for each pair
                        // with the last term in the conjunction as the head for all the COORD relations
                        if (!currArg1.equals(",") && (prev == null || !prev.getDep().equals("conj") || prev.getArg1().equals(","))) {
                                List<Dependency> result = new ArrayList<Dependency>();
                                String conjunct= currArg1;
                                Collection<Annotation> seen = new HashSet<Annotation>();
                                seen.add(curr.a2);
                                for (Dependency nextConj : dependencies.subList(i + 1, dependencies.size())) {
                                        if (!nextConj.getDep().equals("conj"))
                                                break;
					skip++;
                                        Annotation nextW = nextConj.a2;
                                        if (!seen.contains(nextW)) {
						boolean foundCoord= false;
					//	Iterator<Annotation> iter= seen.iterator();
						//while (iter.hasNext())
                                                //result.add(new Dependency("COORD_" + conjunct + ":" + getLP2LPPOS(currPOS2, currSTag2) + "-" + getLP2LPPOS(nextConj.getPOS2(), nextConj.getSTag2()), iter.next(), nextW));
						if ((currPOS2.equals("TO") || currPOS2.equals("IN")) && (nextConj.getPOS2().equals("IN") || nextConj.getPOS2().equals("TO"))) {
							Dependency temp1= containsRelationArgOne(dependencies, "dobj", curr.a2, 0);
							Dependency temp2= containsRelationArgOne(dependencies, "dobj", nextConj.a2, 0);
							if (temp1 != null && temp2 != null) {
								foundCoord= true;
								result.add(fromArg2Arg2("COORD_" + conjunct + ":" + getLP2LPPOS(temp1.getPOS2(), temp1.getSTag2()) + "-" + getLP2LPPOS(temp2.getPOS2(), temp2.getSTag2()), temp1, temp2));
							}
						}
						if ((currPOS2.equals("TO") || currPOS2.equals("IN")) && (!nextConj.getPOS2().equals("IN") && !nextConj.getPOS2().equals("TO"))) {
							Dependency temp1= containsRelationArgOne(dependencies, "dobj", curr.a2, 0);
							if (temp1 != null) {
								foundCoord= true;
								result.add(fromArg2Arg2("COORD_" + conjunct + ":" + getLP2LPPOS(temp1.getPOS2(), temp1.getSTag2()) + "-" + getLP2LPPOS(nextConj.getPOS2(), nextConj.getSTag2()), temp1, nextConj));
							}
						}
						if (!currPOS2.equals("TO") && !currPOS2.equals("IN") && !nextConj.getPOS2().equals("IN") && nextConj.getPOS2().equals("TO")) {
							Dependency temp2= containsRelationArgOne(dependencies, "dobj", nextConj.a2, 0);
							if (temp2 != null) {
								foundCoord= true;
								result.add(fromArg2Arg2("COORD_" + conjunct + ":" + getLP2LPPOS(currPOS2, currSTag2) + "-" + getLP2LPPOS(temp2.getPOS2(), temp2.getSTag2()), curr, temp2));
							}
						}
						if (!foundCoord) {
                                                result.add(fromArg2Arg2("COORD_" + conjunct + ":" + getLP2LPPOS(currPOS2, currSTag2) + "-" + getLP2LPPOS(nextConj.getPOS2(), nextConj.getSTag2()), curr, nextConj));
						}
						seen.add(nextW);
                                        }
                                }
				return result;
                        }


			// This is the case where the conjunction denotes an apposition
			// [CONJ , word1] + [CONJ , word2] = [APPOS word1 word2], the relations just before and just after
			// the conj bust not be conjuntions themselves
			else if (currArg1.equals(",")) { 
				if ((prev == null || !prev.getDep().equals("conj")) && (next != null && next.getDep().equals("conj") && (next.a1.getStart() == curr.a1.getStart())) && (nextNext == null || !nextNext.getDep().equals("conj"))) {
					return Collections.singleton(fromArg2Arg2("APPOS:" + getLP2LPPOS(currPOS2, currSTag2)+ "-" + getLP2LPPOS(next.getPOS2(), next.getSTag2()), curr, next));
				}
				if ((next != null && next.getDep().equals("conj") && (next.a1.getStart() == curr.a1.getStart())) && (nextNext == null || !nextNext.getDep().equals("conj") || nextNext.a1.getStart() != next.a1.getStart())) {
					return Collections.singleton(fromArg2Arg2("APPOS:" + getLP2LPPOS(currPOS2, currSTag2)+ "-" + getLP2LPPOS(next.getPOS2(), next.getSTag2()), curr, next));
				}
				if((prev == null || !prev.getDep().equals("conj")) && (next != null && next.getDep().equals("conj") && (next.a1.getStart() == curr.a1.getStart())) && (nextNext.getDep().equals("conj") && (!nextNext.getArg1().equals(",") || (nextNext.a1.getStart() != next.a1.getStart())))) {
					return Collections.singleton(fromArg2Arg2("APPOS:" + getLP2LPPOS(currPOS2, currSTag2)+ "-" + getLP2LPPOS(next.getPOS2(), next.getSTag2()), curr, next));
				}

			}


			// If the conj relation has a non-comma first argument (i.e. will have a conjunct as "and" or "or" as its arg)
			// than this represents a coordination
			// CCG does a factoring of all the possibilities, want to condense this into a single relation for each pair
			// with the last term in the conjunction as the head for all the COORD relations
			/*if (!currArg1.equals(",") && (prev == null || !prev.getDep().equals("conj"))) {
				List<Dependency> result = new ArrayList<Dependency>();
				String conjunct= currArg1;
				Collection<Annotation> seen = new HashSet<Annotation>();
				seen.add(curr.a2);
				for (Dependency nextConj : dependencies.subList(i + 1, dependencies.size())) {
					if (!nextConj.getDep().equals("conj"))
						break;
					Annotation nextW = nextConj.a2;
					if (!seen.contains(nextW)) {
						result.add(fromArg2Arg2("COORD_" + conjunct + ":" + getLP2LPPOS(currPOS2, currSTag2) + "-" + getLP2LPPOS(nextConj.getPOS2(), nextConj.getSTag2()), curr, nextConj));
						seen.add(nextW);
					}
				}
				return result;
			}*/
		}

		return Collections.emptyList();
	}

	private static Dependency containsRelationArgOne(List<Dependency> dependencies, String rel, Annotation w, int sInd) {
		for (Dependency curr : dependencies.subList(sInd, dependencies.size()))
			if (curr.matchArg1(rel, w))
				return curr;
		for (Dependency curr : dependencies.subList(0, sInd))
			if (curr.matchArg1(rel, w))
				return curr;
		return null;
	}

	private static Dependency containsRelationArgTwo(List<Dependency> dependencies, String rel, Annotation w, int sInd) {
		for (Dependency curr : dependencies.subList(sInd, dependencies.size()))
			if (curr.matchArg2(rel, w))
				return curr;
		for (Dependency curr : dependencies.subList(0, sInd))
			if (curr.matchArg2(rel, w))
				return curr;
		return null;
	}

        private static boolean containsRelation(List<Dependency> dependencies, String rel, Annotation w1, Annotation w2) {
                for (Dependency curr : dependencies.subList(0, dependencies.size()))
                        if (curr.matchArg1(rel, w1) && curr.matchArg2(rel, w2))
                                return true;
                return false;
	}

	/*
	 *  Returns whether a given sTag contains the String [pss],
	 *  designating a passive verb.
	 */

	private static boolean passive(String sTag) {
		return sTag.contains("[pss]");
	}


	/*
	 * Returns whether a given POS tag is a noun, meaning it is NN, NNS, CD, PRP or PRP$
	 */
	private static boolean noun(String pos) {
		return (pos.startsWith("N") || pos.startsWith("P") || pos.equals("CD") || pos.equals("WP$"));
	}


	private static String getLP2LPPOS(String pos, String st) {
		if (pos.startsWith("N") || pos.startsWith("CD") || pos.startsWith("P") || pos.equals("WP$"))
			return "N";
		if (pos.startsWith("V") || pos.equals("MD")) {
			if (passive(st))
				return "V_PASS";
			return "V";
		}
		if (pos.startsWith("JJ"))
			return "ADJ";
		if (pos.startsWith("RB") || pos.equals("WRB")) {
			return "ADV";
		}
		return pos;
	}

	// Argument which states when some relations should be skipped in the transformation
	// This concerns the conj=>COORD transformation
	// Skip needs to be reset for each sentence
	private static int skip;
}
