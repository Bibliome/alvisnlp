package fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.ab3p;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
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
import fr.inra.maiage.bibliome.alvisnlp.core.module.lib.ExternalHandler;
import fr.inra.maiage.bibliome.util.Iterators;
import fr.inra.maiage.bibliome.util.Strings;

class Ab3PExternalHandler extends ExternalHandler<Corpus,Ab3P> {
	Ab3PExternalHandler(ProcessingContext<Corpus> processingContext, Ab3P module, Corpus annotable) {
		super(processingContext, module, annotable);
	}

	@Override
	protected void prepare() throws IOException, ModuleException {
		Ab3P owner = getModule();
		EvaluationContext evalCtx = new EvaluationContext(getLogger());
		try (PrintStream ps = new PrintStream(getAb3PInputFile())) {
			for (Document doc : Iterators.loop(owner.documentIterator(evalCtx, getAnnotable()))) {
				for (Section sec : Iterators.loop(owner.sectionIterator(evalCtx, doc))) {
					String rawContents = sec.getContents();
					String lineContents = rawContents.replace('\n', ' ').trim();
					ps.println(lineContents);
					ps.println();
				}
			}
		}
	}
	
	private File getAb3PInputFile() {
		return getTempFile("input.txt");
	}

	@Override
	protected void collect() throws IOException, ModuleException {
		try (BufferedReader reader = new BufferedReader(new FileReader(getOutputFile()))) {
			EvaluationContext evalCtx = new EvaluationContext(getLogger());
			boolean eof = false;
			for (Section sec : Iterators.loop(getModule().sectionIterator(evalCtx, getAnnotable()))) {
				if (eof) {
					throw new ProcessingException("output has too few lines");
				}
				String checkContents = reader.readLine().replace('\n', ' ').trim();
				String rawContents = sec.getContents();
				String lineContents = rawContents.replace('\n', ' ').trim();
				if (!checkContents.equals(lineContents)) {
					throw new ProcessingException("failed check line : '" + checkContents + "' / '" + lineContents + "'");
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
		Ab3P owner = getModule();
		Collection<Annotation> shortForms = lookup(sec, owner.getShortFormsLayer(), shortForm);
		Collection<Annotation> longForms = lookup(sec, owner.getLongFormsLayer(), longForm);
		Relation rel = sec.ensureRelation(owner, owner.getAbbreviationRelation());
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
			Annotation a = new Annotation(getModule(), layer, start, end);
			annotations.add(a);
		}
		return annotations;
	}

	@Override
	protected String getPrepareTask() {
		return "alvisnlp-to-ab3p";
	}

	@Override
	protected String getExecTask() {
		return "identify_abbr";
	}

	@Override
	protected String getCollectTask() {
		return "ab3p-to-alvisnlp";
	}

	@Override
	protected List<String> getCommandLine() {
		return Arrays.asList(
				"./identify_abbr",
				getAb3PInputFile().getAbsolutePath()
				);
	}

	@Override
	protected void updateEnvironment(Map<String,String> env) {
	}

	@Override
	protected File getWorkingDirectory() {
		return getModule().getInstallDir();
	}

	@Override
	protected String getInputFileame() {
		return null;
	}

	@Override
	protected String getOutputFilename() {
		return "output.txt";
	}
}
