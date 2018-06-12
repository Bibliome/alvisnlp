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


package fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.ab3p;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.SectionModule;
import fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.SectionModule.SectionResolvedObjects;

import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Annotation;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Corpus;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Document;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Layer;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.NameType;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Relation;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Section;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Tuple;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.creators.AnnotationCreator;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.creators.TupleCreator;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.EvaluationContext;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.ResolverException;
import fr.inra.maiage.bibliome.alvisnlp.core.module.Module;
import fr.inra.maiage.bibliome.alvisnlp.core.module.ModuleException;
import fr.inra.maiage.bibliome.alvisnlp.core.module.ProcessingContext;
import fr.inra.maiage.bibliome.alvisnlp.core.module.ProcessingException;
import fr.inra.maiage.bibliome.alvisnlp.core.module.lib.AlvisNLPModule;
import fr.inra.maiage.bibliome.alvisnlp.core.module.lib.External;
import fr.inra.maiage.bibliome.alvisnlp.core.module.lib.Param;
import fr.inra.maiage.bibliome.util.Files;
import fr.inra.maiage.bibliome.util.Iterators;
import fr.inra.maiage.bibliome.util.Strings;
import fr.inra.maiage.bibliome.util.files.InputDirectory;

@AlvisNLPModule(beta=true)
public abstract class Ab3P extends SectionModule<SectionResolvedObjects> implements AnnotationCreator, TupleCreator {
	private InputDirectory installDir;
	private String shortFormsLayerName = "short-forms";
	private String longFormsLayerName = "long-forms";
	private String relationName = "abbreviations";
	private String shortFormRole = "short-form";
	private String longFormRole = "long-form";
	private String longFormFeature = "long-form";

	@Override
	public void process(ProcessingContext<Corpus> ctx, Corpus corpus) throws ModuleException {
		try {
			Ab3PExternal external = new Ab3PExternal(ctx, corpus);
			callExternal(ctx, "run Ab3P", external);
			external.readOutput(corpus);
		}
		catch (IOException e) {
			rethrow(e);
		}
	}

	@Override
	protected String[] addLayersToSectionFilter() {
		return null;
	}

	@Override
	protected String[] addFeaturesToSectionFilter() {
		return null;
	}


	@Override
	protected SectionResolvedObjects createResolvedObjects(ProcessingContext<Corpus> ctx) throws ResolverException {
		return new SectionResolvedObjects(ctx, this);
	}

	@Param
	public InputDirectory getInstallDir() {
		return installDir;
	}

	@Param(nameType=NameType.LAYER)
	public String getShortFormsLayerName() {
		return shortFormsLayerName;
	}

	@Param(nameType=NameType.LAYER)
	public String getLongFormsLayerName() {
		return longFormsLayerName;
	}

	@Param(nameType=NameType.RELATION)
	public String getRelationName() {
		return relationName;
	}

	@Param(nameType=NameType.ARGUMENT)
	public String getShortFormRole() {
		return shortFormRole;
	}

	@Param(nameType=NameType.ARGUMENT)
	public String getLongFormRole() {
		return longFormRole;
	}

	@Param(nameType=NameType.FEATURE)
	public String getLongFormFeature() {
		return longFormFeature;
	}

	public void setLongFormFeature(String longFormFeature) {
		this.longFormFeature = longFormFeature;
	}

	public void setInstallDir(InputDirectory installDir) {
		this.installDir = installDir;
	}

	public void setShortFormsLayerName(String shortFormsLayerName) {
		this.shortFormsLayerName = shortFormsLayerName;
	}

	public void setLongFormsLayerName(String longFormsLayerName) {
		this.longFormsLayerName = longFormsLayerName;
	}

	public void setRelationName(String relationName) {
		this.relationName = relationName;
	}

	public void setShortFormRole(String shortFormRole) {
		this.shortFormRole = shortFormRole;
	}

	public void setLongFormRole(String longFormRole) {
		this.longFormRole = longFormRole;
	}

	private class Ab3PExternal implements External<Corpus> {
		private final Logger logger;
		private final File scriptFile;
		private final File inputFile;
		private final File outputFile;

		private Ab3PExternal(ProcessingContext<Corpus> ctx, Corpus corpus) throws IOException {
			logger = getLogger(ctx);
			File tmpDir = getTempDir(ctx);
			scriptFile = new File(tmpDir, "script.sh");
			inputFile = new File(tmpDir, "input.txt");
			outputFile = new File(tmpDir, "output.txt");
			// same ClassLoader as this class
			try (InputStream is = Ab3P.class.getResourceAsStream("script.sh")) {
				Files.copy(is, scriptFile, 1024, true);
				scriptFile.setExecutable(true);
			}
			EvaluationContext evalCtx = new EvaluationContext(logger);
			try (PrintStream ps = new PrintStream(inputFile)) {
				for (Document doc : Iterators.loop(documentIterator(evalCtx, corpus))) {
					for (Section sec : Iterators.loop(sectionIterator(evalCtx, doc))) {
						String rawContents = sec.getContents();
						String lineContents = rawContents.replace('\n', ' ').trim();
						ps.println(lineContents);
						ps.println();
					}
				}
			}
		}
		
		private void readOutput(Corpus corpus) throws IOException, ProcessingException {
			try (BufferedReader reader = new BufferedReader(new FileReader(outputFile))) {
				EvaluationContext evalCtx = new EvaluationContext(logger);
				boolean eof = false;
				for (Section sec : Iterators.loop(sectionIterator(evalCtx, corpus))) {
					if (eof) {
						processingException("output has too few lines");
					}
					String checkContents = reader.readLine().replace('\n', ' ').trim();
					String rawContents = sec.getContents();
					String lineContents = rawContents.replace('\n', ' ').trim();
					if (!checkContents.equals(lineContents)) {
						processingException("failed check line : '" + checkContents + "' / '" + lineContents + "'");
					}
					while (true) {
						String line = reader.readLine();
						if (line == null) {
							eof = true;
							break;
						}
						line = line.trim();
						if (line.isEmpty()) {
							break;
						}
						if (line.startsWith("//")) {
							logger.info("ignoring line " + line);
							continue;
						}
						List<String> cols = Strings.split(line, '|', 0);
						String shortForm = cols.get(0);
						String longForm = cols.get(1);
						createAbbreviations(sec, shortForm, longForm);
					}
				}
			}
		}

		private void createAbbreviations(Section sec, String shortForm, String longForm) {
			Collection<Annotation> shortForms = lookup(sec, shortFormsLayerName, shortForm);
			Collection<Annotation> longForms = lookup(sec, longFormsLayerName, longForm);
			Relation rel = sec.ensureRelation(Ab3P.this, relationName);
			for (Annotation shortA : shortForms) {
				shortA.addFeature(longFormFeature, longForm);
				for (Annotation longA : longForms) {
					Tuple t = new Tuple(Ab3P.this, rel);
					t.setArgument(shortFormRole, shortA);
					t.setArgument(longFormRole, longA);
				}
			}
		}

		private Collection<Annotation> lookup(Section sec, String layerName, String form) {
			Collection<Annotation> annotations = new ArrayList<Annotation>();
			String contents = sec.getContents();
			Layer layer = sec.ensureLayer(layerName);
			Pattern formPattern = Pattern.compile(Pattern.quote(form));
			Matcher m = formPattern.matcher(contents);
			while (m.find()) {
				int start = m.start();
				int end = m.end();
				Annotation a = new Annotation(Ab3P.this, layer, start, end);
				annotations.add(a);
			}
			return annotations;
		}

		@Override
		public Module<Corpus> getOwner() {
			return Ab3P.this;
		}

		@Override
		public String[] getCommandLineArgs() throws ModuleException {
			return new String[] {
					scriptFile.getAbsolutePath()
			};
		}

		@Override
		public String[] getEnvironment() throws ModuleException {
			return new String[] {
					"INSTALL_DIR=" + installDir.getAbsolutePath(),
					"INPUT_FILE=" + inputFile.getAbsolutePath(),
					"OUTPUT_FILE=" + outputFile.getAbsolutePath()
			};
		}

		@Override
		public File getWorkingDirectory() throws ModuleException {
			return installDir;
		}

		@Override
		public void processOutput(BufferedReader out, BufferedReader err) throws ModuleException {
			try {
				logger.fine("Ab3P standard error:");
				for (String line = err.readLine(); line != null; line = err.readLine()) {
					logger.fine("    " + line);
				}
				logger.fine("end of Ab3P standard error");
			}
			catch (IOException ioe) {
				logger.warning("could not read Ab3P standard error: " + ioe.getMessage());
			}
		}
	}
}
