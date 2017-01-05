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


package org.bibliome.alvisnlp.modules.biolg;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bibliome.alvisnlp.modules.DefaultExpressions;
import org.bibliome.alvisnlp.modules.SectionModule;
import org.bibliome.alvisnlp.modules.SectionModule.SectionResolvedObjects;
import org.bibliome.util.Files;
import org.bibliome.util.Iterators;
import org.bibliome.util.Strings;
import org.bibliome.util.Timer;
import org.bibliome.util.files.ExecutableFile;
import org.bibliome.util.files.InputFile;
import org.bibliome.util.files.WorkingDirectory;

import alvisnlp.corpus.Annotation;
import alvisnlp.corpus.Corpus;
import alvisnlp.corpus.DefaultNames;
import alvisnlp.corpus.Layer;
import alvisnlp.corpus.NameType;
import alvisnlp.corpus.Relation;
import alvisnlp.corpus.Section;
import alvisnlp.corpus.Tuple;
import alvisnlp.corpus.creators.TupleCreator;
import alvisnlp.corpus.expressions.EvaluationContext;
import alvisnlp.corpus.expressions.Evaluator;
import alvisnlp.corpus.expressions.Expression;
import alvisnlp.corpus.expressions.ResolverException;
import alvisnlp.module.Module;
import alvisnlp.module.ModuleException;
import alvisnlp.module.ProcessingContext;
import alvisnlp.module.ProcessingException;
import alvisnlp.module.TimerCategory;
import alvisnlp.module.lib.AlvisNLPModule;
import alvisnlp.module.lib.External;
import alvisnlp.module.lib.Param;

// TODO: Auto-generated Javadoc
/**
 * The Class BioLG.
 * 
 * @author rbossy
 */
@AlvisNLPModule
public abstract class BioLG extends SectionModule<SectionResolvedObjects> implements TupleCreator {
	private static final Pattern startSentence = Pattern.compile("\\[Sentence (\\d+)\\]");
	private static final Pattern startLinkage = Pattern.compile("\\[Linkage (\\d+)\\]");
	private static final Pattern dependency = Pattern.compile("\\[(\\d+) (\\d+) \\d+ \\('(.+?)'(?:\\((\\d+)\\))?\\)\\]");
	private String sentenceLayer = DefaultNames.getSentenceLayer();
	private Expression sentenceFilter = DefaultExpressions.TRUE;
	private String wordLayer = DefaultNames.getWordLayer();
	private String posFeature = DefaultNames.getPosTagFeature();
	private WorkingDirectory parserPath = null;
	private Integer timeout = 120;
	private Boolean union = true;
	private ExecutableFile lp2lpExecutable = null;
	private InputFile lp2lpConf = null;
	private String dependencyRelation = DefaultNames.getDependencyRelationName();
	private String headRole = DefaultNames.getDependencyHeadRole();
	private String dependentRole = DefaultNames.getDependencyDependentRole();
	private String sentenceRole = DefaultNames.getDependencySentenceRole();
	private String linkageNumberFeature = null;
	private String dependencyLabelFeature = DefaultNames.getDependencyLabelFeatureName();
	private Integer maxLinkages = null;
	private File parseScript;
	private Integer wordNumberLimit = 1000;

	public BioLG() {
	}

	private List<List<Layer>> getSentenceBlocks(ProcessingContext<Corpus> ctx, EvaluationContext evalCtx, Corpus corpus) throws ModuleException {
		List<List<Layer>> result = new ArrayList<List<Layer>>();
		int nWords = 0;
		List<Layer> currentBlock = new ArrayList<Layer>();
		result.add(currentBlock);
		Evaluator sentenceFilter = getLibraryResolver(ctx).resolveNullable(this.sentenceFilter);
		for (Section sec : Iterators.loop(sectionIterator(evalCtx, corpus))) {
			for (Layer sentence : sec.getSentences(wordLayer, sentenceLayer)) {
				if (!sentenceFilter.getFilter(evalCtx).accept(sentence.getSentenceAnnotation()))
					continue;
				int n = sentence.size();
				if (nWords + n > wordNumberLimit) {
					currentBlock = new ArrayList<Layer>();
					result.add(currentBlock);
					nWords = 0;
				}
				currentBlock.add(sentence);
				nWords += n;
			}
		}
		return result;
	}
	
	@Override
	protected String[] addLayersToSectionFilter() {
		return new String[] { sentenceLayer, wordLayer };
	}

	@Override
	protected String[] addFeaturesToSectionFilter() {
		return null;
	}

	@Override
	protected SectionResolvedObjects createResolvedObjects(ProcessingContext<Corpus> ctx) throws ResolverException {
		return new SectionResolvedObjects(ctx, this);
	}

	@Override
	public void process(ProcessingContext<Corpus> ctx, Corpus corpus) throws ModuleException {
		try {
			File tmpDir = getTempDir(ctx);
			Timer<TimerCategory> scriptTimer = getTimer(ctx, "build-parse-script", TimerCategory.LOAD_RESOURCE, true);
			parseScript = getParseScript(tmpDir);
			scriptTimer.stop();
			Timer<TimerCategory> biolgTimer = getTimer(ctx, "biolg-input", TimerCategory.PREPARE_DATA, false);
			Timer<TimerCategory> lp2lpTimer = getTimer(ctx, "lp2lp-output", TimerCategory.COLLECT_DATA, false);
			Logger logger = getLogger(ctx);
			List<List<Layer>> sentenceBlocks = getSentenceBlocks(ctx, new EvaluationContext(logger), corpus);
			int nWords = 0;
			int nSentences = 0;
			for (List<Layer> l : sentenceBlocks) {
				nSentences += l.size();
				for (Layer s : l)
					nWords += s.size();
			}
			logger.info("runs: " + sentenceBlocks.size());
			logger.info("sentences: " + nSentences);
			logger.info("words: " + nWords);
			int n = 0;
			for (List<Layer> block : sentenceBlocks) {
				biolgTimer.start();
				BioLGExternal bioLG = new BioLGExternal(ctx, tmpDir, block, n);
				biolgTimer.stop();
				callExternal(ctx, "biolg", bioLG);
				logger.fine("translating dependencies with lp2lp");
				LP2LPExternal lp2lp = new LP2LPExternal(ctx, tmpDir, n++, bioLG.getOut());
				callExternal(ctx, "lp2lp", lp2lp);
				logger.fine("reading lp2lp output");
				BufferedReader r = new BufferedReader(new FileReader(lp2lp.getOut()));
				Iterator<Layer> sentIt = block.iterator();
				Layer currentSentence = null;
				int currentLinkage = -1;
				boolean isSkipping = false;
				boolean leftWall = false;
				Section currentSection = null;
				Relation dependencies = null;
				lp2lpTimer.start();
				while (true) {
					String line = r.readLine();
					if (line == null) {
						break;
					}
					if (line.isEmpty()) {
						continue;
					}
					if ("[]".equals(line)) {
						continue;
					}
					Matcher startSentenceMatcher = startSentence.matcher(line);
					if (startSentenceMatcher.matches()) {
						currentSentence = sentIt.next();
						logger.finer("document "
								+ currentSentence.getSection().getDocument().getId() + ", section "
								+ currentSentence.getSection().getName() + ": "
								+ line);
						logger.finer("sentence: " + Strings.joinStrings(currentSentence, ' '));
						currentLinkage = -1;
						isSkipping = false;
						continue;
					}
					Matcher startLinkageMatcher = startLinkage.matcher(line);
					if (startLinkageMatcher.matches()) {
						currentLinkage = Integer.parseInt(startLinkageMatcher.group(1));
						logger.finer("linkage " + currentLinkage);
						isSkipping = true;
						leftWall = false;
						continue;
					}
					if (isSkipping) {
						if (line.startsWith("[(LEFT-WALL)"))
							leftWall = true;
						if (line.charAt(line.length() - 1) == ']') {
							isSkipping = false;
							continue;
						}
					}
					Matcher dependencyMatcher = dependency.matcher(line);
					while (dependencyMatcher.find()) {
						int w1 = Integer.parseInt(dependencyMatcher.group(1));
						int w2 = Integer.parseInt(dependencyMatcher.group(2));
						if (leftWall) {
							w1--;
							w2--;
						}
						// else
						// logger.fine("no left wall!");
						String label = dependencyMatcher.group(3);
                        String prep = dependencyMatcher.group(4);
                        if ((prep != null) && !prep.isEmpty()) {
                            int wPrep = Integer.parseInt(prep);
                            if (leftWall)
                                wPrep--;
                            label = label + "(" + currentSentence.get(wPrep).getForm() + ")";
                        }
						if (currentSentence.getSection() != currentSection) {
							currentSection = currentSentence.getSection();
							dependencies = currentSection.ensureRelation(this, dependencyRelation);
						}
						Tuple dep = new Tuple(this, dependencies);
						dep.setArgument(sentenceRole, currentSentence.getSentenceAnnotation());
						dep.setArgument(headRole, currentSentence.get(w1));
						dep.setArgument(dependentRole, currentSentence.get(w2));
						dep.addFeature(dependencyLabelFeature, label);
						dep.addFeature(linkageNumberFeature, Integer.toString(currentLinkage));
					}
				}
				r.close();
				lp2lpTimer.stop();
			}
		}
		catch (IOException ioe) {
			rethrow(ioe);
		}
	}


	/**
	 * Gets the parses the script.
	 * 
	 * @return the parses the script
	 * 
	 * @throws FileNotFoundException
	 *             the file not found exception
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	private static File getParseScript(File tmpDir) throws IOException {
		File result = new File(tmpDir, "parse.sh");
		// same ClassLoader as this class
		InputStream is = BioLG.class.getResourceAsStream("parse.sh");
		Files.copy(is, result, 1024, true);
		result.setExecutable(true);
		return result;
	}

	/**
	 * Gets the sentence layer.
	 * 
	 * @return the sentenceLayer
	 */
	@Param(nameType=NameType.LAYER, defaultDoc = "Layer containing sentence annotations.")
	public String getSentenceLayer() {
		return sentenceLayer;
	}

	/**
	 * Sets the sentence layer.
	 * 
	 * @param sentenceLayer
	 *            the sentenceLayer to set
	 */
	public void setSentenceLayer(String sentenceLayer) {
		this.sentenceLayer = sentenceLayer;
	}

	/**
	 * Gets the word layer.
	 * 
	 * @return the wordLayer
	 */
	@Param(nameType=NameType.LAYER, defaultDoc = "Layer containing word annotations.")
	public String getWordLayer() {
		return wordLayer;
	}

	@Param(defaultDoc = "Either to show union linkages")
	public Boolean getUnion() {
		return union;
	}

	public void setUnion(Boolean union) {
		this.union = union;
	}

	/**
	 * Sets the word layer.
	 * 
	 * @param wordLayer
	 *            the wordLayer to set
	 */
	public void setWordLayer(String wordLayer) {
		this.wordLayer = wordLayer;
	}

	/**
	 * Gets the pos feature.
	 * 
	 * @return the posFeature
	 */
	@Param(nameType=NameType.FEATURE, defaultDoc = "Name of the feature in word annotations containing the POS tag.")
	public String getPosFeature() {
		return posFeature;
	}

	/**
	 * Sets the pos feature.
	 * 
	 * @param posFeature
	 *            the posFeature to set
	 */
	public void setPosFeature(String posFeature) {
		this.posFeature = posFeature;
	}

	/**
	 * Gets the parser path.
	 * 
	 * @return the parserPath
	 */
	@Param
	public WorkingDirectory getParserPath() {
		return parserPath;
	}

	/**
	 * Sets the parser path.
	 * 
	 * @param parserPath
	 *            the parserPath to set
	 */
	public void setParserPath(WorkingDirectory parserPath) {
		this.parserPath = parserPath;
	}

	/**
	 * Gets the timeout.
	 * 
	 * @return the timeout
	 */
	@Param
	public Integer getTimeout() {
		return timeout;
	}

	/**
	 * Sets the timeout.
	 * 
	 * @param timeout
	 *            the timeout to set
	 */
	public void setTimeout(Integer timeout) {
		this.timeout = timeout;
	}

	/**
	 * Gets the lp2lp executable.
	 * 
	 * @return the lp2lpExecutable
	 */
	@Param
	public ExecutableFile getLp2lpExecutable() {
		return lp2lpExecutable;
	}

	/**
	 * Sets the lp2lp executable.
	 * 
	 * @param lp2lpExecutable
	 *            the lp2lpExecutable to set
	 */
	public void setLp2lpExecutable(ExecutableFile lp2lpExecutable) {
		this.lp2lpExecutable = lp2lpExecutable;
	}

	/**
	 * Gets the lp2lp conf.
	 * 
	 * @return the lp2lpConf
	 */
	@Param
	public InputFile getLp2lpConf() {
		return lp2lpConf;
	}

	/**
	 * Sets the lp2lp conf.
	 * 
	 * @param lp2lpConf
	 *            the lp2lpConf to set
	 */
	public void setLp2lpConf(InputFile lp2lpConf) {
		this.lp2lpConf = lp2lpConf;
	}

	/**
	 * Gets the dependency relation.
	 * 
	 * @return the dependencyRelation
	 */
	@Param(nameType=NameType.RELATION, defaultDoc = "Name of the relation containing the dependencies.")
	public String getDependencyRelation() {
		return dependencyRelation;
	}

	/**
	 * Sets the dependency relation.
	 * 
	 * @param dependencyRelation
	 *            the dependencyRelation to set
	 */
	public void setDependencyRelation(String dependencyRelation) {
		this.dependencyRelation = dependencyRelation;
	}

	/**
	 * Gets the head role.
	 * 
	 * @return the headRole
	 */
	@Param(nameType=NameType.ARGUMENT, defaultDoc = "Name of the role of the head in the dependency relation.")
	public String getHeadRole() {
		return headRole;
	}

	/**
	 * Sets the head role.
	 * 
	 * @param headRole
	 *            the headRole to set
	 */
	public void setHeadRole(String headRole) {
		this.headRole = headRole;
	}

	/**
	 * Gets the modifier role.
	 * 
	 * @return the modifierRole
	 */
	@Param(nameType=NameType.ARGUMENT, defaultDoc = "Name of the role of the dependent in the dependency relation.")
	public String getDependentRole() {
		return dependentRole;
	}

	/**
	 * Sets the modifier role.
	 * 
	 * @param modifierRole
	 *            the modifierRole to set
	 */
	public void setDependentRole(String modifierRole) {
		this.dependentRole = modifierRole;
	}

	/**
	 * Gets the sentence role.
	 * 
	 * @return the sentenceRole
	 */
	@Param(nameType=NameType.ARGUMENT, defaultDoc = "Name of the role of the sentence in the dependency relation.")
	public String getSentenceRole() {
		return sentenceRole;
	}

	/**
	 * Sets the sentence role.
	 * 
	 * @param sentenceRole
	 *            the sentenceRole to set
	 */
	public void setSentenceRole(String sentenceRole) {
		this.sentenceRole = sentenceRole;
	}

	/**
	 * Gets the linkage number feature.
	 * 
	 * @return the linkageNumberFeature
	 */
	@Param(nameType=NameType.FEATURE, defaultDoc = "Name of the dependecy tuple feature containing the linkage number.")
	public String getLinkageNumberFeature() {
		return linkageNumberFeature;
	}

	/**
	 * Sets the linkage number feature.
	 * 
	 * @param linkageNumberFeature
	 *            the linkageNumberFeature to set
	 */
	public void setLinkageNumberFeature(String linkageNumberFeature) {
		this.linkageNumberFeature = linkageNumberFeature;
	}

	/**
	 * Gets the dependency label feature.
	 * 
	 * @return the dependencyLabelFeature
	 */
	@Param(nameType=NameType.FEATURE, defaultDoc = "Name of the dependency tuple feature containing the dependency label.")
	public String getDependencyLabelFeature() {
		return dependencyLabelFeature;
	}

	/**
	 * Sets the dependency label feature.
	 * 
	 * @param dependencyLabelFeature
	 *            the dependencyLabelFeature to set
	 */
	public void setDependencyLabelFeature(String dependencyLabelFeature) {
		this.dependencyLabelFeature = dependencyLabelFeature;
	}

	/**
	 * Gets the max linkages.
	 * 
	 * @return the maxLinkages
	 */
	@Param(mandatory = false)
	public Integer getMaxLinkages() {
		return maxLinkages;
	}

	/**
	 * Sets the max linkages.
	 * 
	 * @param maxLinkages
	 *            the maxLinkages to set
	 */
	public void setMaxLinkages(Integer maxLinkages) {
		this.maxLinkages = maxLinkages;
	}

	@Param(defaultDoc="Maximum number of words for each biolg run.")
	public Integer getWordNumberLimit() {
		return wordNumberLimit;
	}

	public void setWordNumberLimit(Integer wordNumberLimit) {
		this.wordNumberLimit = wordNumberLimit;
	}

	@Param
	public Expression getSentenceFilter() {
		return sentenceFilter;
	}

	public void setSentenceFilter(Expression sentenceFilter) {
		this.sentenceFilter = sentenceFilter;
	}

	/**
	 * The Class BioLGExternal.
	 */
	private class BioLGExternal implements External<Corpus> {
		private final File in;
		private final File out;
		private final ProcessingContext<Corpus> ctx;

		private BioLGExternal(ProcessingContext<Corpus> ctx, File tmpDir, List<Layer> sentences, int n) throws IOException {
			this.ctx = ctx;
			in = new File(tmpDir, "block_" + n + ".biolg");
			out = new File(tmpDir, "block_" + n + ".lp");
			in.getParentFile().mkdirs();
			out.getParentFile().mkdirs();
			getLogger(ctx).fine("creating biolg input file " + in.getCanonicalPath());
			PrintStream ps = new PrintStream(in, "UTF-8");
			int maxLength = 0;
			ps.print("<sentences>\n");
			if (union)
				ps.print("<command string=\"union\"/>\n");
			if (timeout != null)
				ps.print("<command string=\"timeout=" + timeout + "\"/>\n");
			if (maxLinkages != null)
				ps.print("<command string=\"limit=" + maxLinkages + "\"/>\n");
			StringBuilder sb = new StringBuilder();
			for (Layer sentence : sentences) {
				sb.append("<sentence>\n");
				for (Annotation annot : sentence) {
					if (annot.hasFeature(posFeature)) {
						sb.append("<w c=\"");
						sb.append(Strings.escapeXML(annot.getLastFeature(posFeature)));
						sb.append("\">");
						// ps.printf("<w c=\"%s\">%s</w>",
						// Strings.escapeXML(annot.getLastFeature(posFeature)),
						// Strings.escapeXML(annot.getForm()));
					} else {
						sb.append("<w>");
						// ps.printf("<w>%s</w>",
						// Strings.escapeXML(annot.getForm()));
					}
					sb.append(Strings.escapeXML(annot.getForm()));
					sb.append("</w>\n");
				}
				sb.append("</sentence>\n");
				maxLength = Math.max(maxLength, sentence.size());
			}
			ps.print("<command string=\"max-length=" + (maxLength + 2) + "\"/>\n");
			ps.print(sb);
			ps.println("</sentences>\n");
			ps.close();
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see alvisnlp.module.lib.External#getCommandLineArgs()
		 */
		@Override
		public String[] getCommandLineArgs() throws ModuleException {
			return new String[] { parseScript.getAbsolutePath() };
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see alvisnlp.module.lib.External#getEnvironment()
		 */
		@Override
		public String[] getEnvironment() throws ModuleException {
			return new String[] {
					"BIOLG_PATH=" + parserPath,
					"BIOLG_IN=" + in.getAbsolutePath(),
					"BIOLG_OUT=" + out.getAbsolutePath() };
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see alvisnlp.module.lib.External#getOwner()
		 */
		@Override
		public Module<Corpus> getOwner() {
			return BioLG.this;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see alvisnlp.module.lib.External#getWorkingDirectory()
		 */
		@Override
		public File getWorkingDirectory() throws ModuleException {
			return null;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see alvisnlp.module.lib.External#processCall(java.io.PrintStream,
		 * java.io.BufferedReader, java.io.BufferedReader)
		 */
		@Override
		public void processOutput(BufferedReader out, BufferedReader err)
				throws ModuleException {
			try {
				Logger logger = getLogger(ctx);
				logger.fine("bioLG standard error:");
				for (String line = err.readLine(); line != null; line = err.readLine()) {
					logger.fine("    " + line);
				}
				logger.fine("end of bioLG standard error");
			} catch (IOException ioe) {
				getLogger(ctx).warning("could not read bioLG standard error: " + ioe.getMessage());
			}
		}

		/**
		 * Gets the out.
		 * 
		 * @return the out
		 */
		public File getOut() {
			return out;
		}
	}

	/**
	 * The Class LP2LPExternal.
	 */
	private class LP2LPExternal implements External<Corpus> {
		private final File in;
		private final File out;
		private final ProcessingContext<Corpus> ctx;
		
		/**
		 * Instantiates a new l p2 lp external.
		 * 
		 * @param sec
		 *            the sec
		 * @param in
		 *            the in
		 */
		/*
		private LP2LPExternal(File tmpDir, Section sec, File in) {
			this.in = in;
			this.out = new File(tmpDir, sec.getFileName() + ".lp2lp");
		}
		*/
		
		private LP2LPExternal(ProcessingContext<Corpus> ctx, File tmpDir, int n, File in) {
			this.ctx = ctx;
			this.in = in;
			this.out = new File(tmpDir, "block_" + n + ".lp2lp");
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see alvisnlp.module.lib.External#getCommandLineArgs()
		 */
		@Override
		public String[] getCommandLineArgs() throws ModuleException {
			return new String[] {
					lp2lpExecutable.getAbsolutePath(),
					"-r",
					lp2lpConf.getAbsolutePath(),
					in.getAbsolutePath() };
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see alvisnlp.module.lib.External#getEnvironment()
		 */
		@Override
		public String[] getEnvironment() throws ModuleException {
			return new String[] {
					"GLOBALSZ=262144",
					"CSTRSZ=1024",
					"TRAILSZ=131071",
					"LOCALSZ=16384" };
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see alvisnlp.module.lib.External#getOwner()
		 */
		@Override
		public Module<Corpus> getOwner() {
			return BioLG.this;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see alvisnlp.module.lib.External#getWorkingDirectory()
		 */
		@Override
		public File getWorkingDirectory() throws ModuleException {
			return null;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see alvisnlp.module.lib.External#processCall(java.io.PrintStream,
		 * java.io.BufferedReader, java.io.BufferedReader)
		 */
		@Override
		public void processOutput(BufferedReader out, BufferedReader err)
				throws ModuleException {
			try {
				PrintStream ps = new PrintStream(this.out);
				for (String line = out.readLine(); line != null; line = out.readLine()) {
					ps.println(line);
				}
				ps.close();
			} catch (FileNotFoundException fnfe) {
				throw new ProcessingException(fnfe);
			} catch (IOException ioe) {
				throw new ProcessingException(ioe);
			}
			try {
				Logger logger = getLogger(ctx);
				logger.fine("lp2lp standard error:");
				for (String line = err.readLine(); line != null; line = err.readLine()) {
					logger.fine("    " + line);
				}
				logger.fine("end of lp2lp standard error");
			} catch (IOException ioe) {
				getLogger(ctx).warning("could not read lp2lp standard error: " + ioe.getMessage());
			}
		}

		/**
		 * Gets the out.
		 * 
		 * @return the out
		 */
		public File getOut() {
			return out;
		}
	}
}
