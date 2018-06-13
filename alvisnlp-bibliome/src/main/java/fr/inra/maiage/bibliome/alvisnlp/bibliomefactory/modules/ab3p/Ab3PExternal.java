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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Annotation;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Corpus;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Document;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Layer;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Relation;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Section;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Tuple;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.EvaluationContext;
import fr.inra.maiage.bibliome.alvisnlp.core.module.ModuleException;
import fr.inra.maiage.bibliome.alvisnlp.core.module.ProcessingContext;
import fr.inra.maiage.bibliome.alvisnlp.core.module.ProcessingException;
import fr.inra.maiage.bibliome.alvisnlp.core.module.lib.AbstractExternal;
import fr.inra.maiage.bibliome.alvisnlp.core.module.lib.ModuleBase;
import fr.inra.maiage.bibliome.util.Files;
import fr.inra.maiage.bibliome.util.Iterators;
import fr.inra.maiage.bibliome.util.Strings;

class Ab3PExternal extends AbstractExternal<Corpus,Ab3P> {
	private final File scriptFile;
	private final File inputFile;
	private final File outputFile;

	Ab3PExternal(Ab3P owner, ProcessingContext<Corpus> ctx, Corpus corpus) throws IOException {
		super(owner, ctx);
		File tmpDir = owner.getTempDir(ctx);
		scriptFile = new File(tmpDir, "script.sh");
		inputFile = new File(tmpDir, "input.txt");
		outputFile = new File(tmpDir, "output.txt");
		// same ClassLoader as this class
		try (InputStream is = Ab3P.class.getResourceAsStream("script.sh")) {
			Files.copy(is, scriptFile, 1024, true);
			scriptFile.setExecutable(true);
		}
		EvaluationContext evalCtx = new EvaluationContext(getLogger());
		try (PrintStream ps = new PrintStream(inputFile)) {
			for (Document doc : Iterators.loop(owner.documentIterator(evalCtx, corpus))) {
				for (Section sec : Iterators.loop(owner.sectionIterator(evalCtx, doc))) {
					String rawContents = sec.getContents();
					String lineContents = rawContents.replace('\n', ' ').trim();
					ps.println(lineContents);
					ps.println();
				}
			}
		}
	}
	
	void readOutput(Corpus corpus) throws IOException, ProcessingException {
		try (BufferedReader reader = new BufferedReader(new FileReader(outputFile))) {
			EvaluationContext evalCtx = new EvaluationContext(getLogger());
			boolean eof = false;
			for (Section sec : Iterators.loop(getOwner().sectionIterator(evalCtx, corpus))) {
				if (eof) {
					ModuleBase.processingException("output has too few lines");
				}
				String checkContents = reader.readLine().replace('\n', ' ').trim();
				String rawContents = sec.getContents();
				String lineContents = rawContents.replace('\n', ' ').trim();
				if (!checkContents.equals(lineContents)) {
					ModuleBase.processingException("failed check line : '" + checkContents + "' / '" + lineContents + "'");
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
						getLogger().info("ignoring line " + line);
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
		Ab3P owner = getOwner();
		Collection<Annotation> shortForms = lookup(sec, owner.getShortFormsLayerName(), shortForm);
		Collection<Annotation> longForms = lookup(sec, owner.getLongFormsLayerName(), longForm);
		Relation rel = sec.ensureRelation(owner, owner.getRelationName());
		for (Annotation shortA : shortForms) {
			shortA.addFeature(owner.getLongFormFeature(), longForm);
			for (Annotation longA : longForms) {
				Tuple t = new Tuple(owner, rel);
				t.setArgument(owner.getShortFormRole(), shortA);
				t.setArgument(owner.getLongFormRole(), longA);
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
			Annotation a = new Annotation(getOwner(), layer, start, end);
			annotations.add(a);
		}
		return annotations;
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
				"INSTALL_DIR=" + getOwner().getInstallDir().getAbsolutePath(),
				"INPUT_FILE=" + inputFile.getAbsolutePath(),
				"OUTPUT_FILE=" + outputFile.getAbsolutePath()
		};
	}

	@Override
	public File getWorkingDirectory() throws ModuleException {
		return getOwner().getInstallDir();
	}
}