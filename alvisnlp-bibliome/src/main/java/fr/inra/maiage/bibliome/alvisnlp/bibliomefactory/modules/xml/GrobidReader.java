package fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.xml;

import java.util.Arrays;

import org.grobid.core.engines.ProcessEngine;
import org.grobid.core.main.GrobidHomeFinder;
import org.grobid.core.utilities.GrobidProperties;

import fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.CorpusModule;
import fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.ResolvedObjects;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Corpus;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.ResolverException;
import fr.inra.maiage.bibliome.alvisnlp.core.module.ModuleException;
import fr.inra.maiage.bibliome.alvisnlp.core.module.ProcessingContext;
import fr.inra.maiage.bibliome.alvisnlp.core.module.ProcessingException;
import fr.inra.maiage.bibliome.alvisnlp.core.module.lib.AlvisNLPModule;
import fr.inra.maiage.bibliome.alvisnlp.core.module.lib.Param;
import fr.inra.maiage.bibliome.util.files.InputDirectory;

@AlvisNLPModule(beta = true)
public class GrobidReader extends CorpusModule<ResolvedObjects> {
	private InputDirectory grobidHome;
	
	@Override
	public void process(ProcessingContext ctx, Corpus corpus) throws ModuleException {
		GrobidHomeFinder grobidHomeFinder = new GrobidHomeFinder(Arrays.asList(grobidHome.getAbsolutePath()));
		grobidHomeFinder.findGrobidHomeOrFail();
		GrobidProperties.getInstance(grobidHomeFinder);
        try (ProcessEngine processEngine = new ProcessEngine()) {
        	processEngine.processFullText(null); /* XXX
        	https://grobid.readthedocs.io/en/latest/Grobid-batch/
        	https://grobid.readthedocs.io/en/latest/Grobid-java-library/
        	https://grobid.github.io/grobid-core/index.html
        	https://grobid.readthedocs.io/en/latest/Consolidation/
        	 */
        }
        catch (Exception e) {
        	throw new ProcessingException(e);
		}
	}

	@Override
	protected ResolvedObjects createResolvedObjects(ProcessingContext ctx) throws ResolverException {
		return new ResolvedObjects(ctx, this);
	}

	@Param
	public InputDirectory getGrobidHome() {
		return grobidHome;
	}

	public void setGrobidHome(InputDirectory grobidHome) {
		this.grobidHome = grobidHome;
	}
}
