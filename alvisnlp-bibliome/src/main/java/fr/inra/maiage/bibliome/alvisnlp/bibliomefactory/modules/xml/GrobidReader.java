package fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.xml;

import java.io.File;

import org.grobid.core.engines.ProcessEngine;
import org.grobid.core.main.batch.GrobidMain;
import org.grobid.core.main.batch.GrobidMainArgs;
import org.grobid.core.utilities.Utilities;

import fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.ResolvedObjects;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Corpus;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.ResolverException;
import fr.inra.maiage.bibliome.alvisnlp.core.module.ModuleException;
import fr.inra.maiage.bibliome.alvisnlp.core.module.ProcessingContext;
import fr.inra.maiage.bibliome.alvisnlp.core.module.ProcessingException;
import fr.inra.maiage.bibliome.alvisnlp.core.module.lib.AlvisNLPModule;
import fr.inra.maiage.bibliome.alvisnlp.core.module.lib.Param;
import fr.inra.maiage.bibliome.alvisnlp.core.module.types.Mapping;
import fr.inra.maiage.bibliome.util.files.InputDirectory;
import fr.inra.maiage.bibliome.util.streams.SourceStream;
import fr.inra.maiage.bibliome.util.streams.StreamFactory;

@AlvisNLPModule(beta = true)
public abstract class GrobidReader extends AbstractXMLReader<ResolvedObjects> {
	private InputDirectory grobidHome;
	private InputDirectory source;
	private String xmlDirPath;
	
	@Override
	public void process(ProcessingContext ctx, Corpus corpus) throws ModuleException {
		try {
			File tmpDir = getTempDir(ctx);
			xmlDirPath = tmpDir.getAbsolutePath();
			GrobidStub.main(buildGrobidCommandline());
			processDocuments(ctx, corpus);
		}
		catch (Exception e) {
			throw new ProcessingException(e);
		}
	}
	
	private String[] buildGrobidCommandline() {
		return new String[] {
				"-gH",
				grobidHome.getAbsolutePath(),
				"-dIn",
				source.getAbsolutePath(),
				"-dOut",
				xmlDirPath,
				"-r",
				"-ignoreAssets",
				"-addElementId",
				"-segmentSentences",
				"-exe",
				"processFullText"
			};
	}

	@Override
	protected ResolvedObjects createResolvedObjects(ProcessingContext ctx) throws ResolverException {
		return new ResolvedObjects(ctx, this);
	}

	@Override
	public SourceStream getXslTransform() {
		//return new StreamFactory().getFileSourceStream("/home/rbossy/code/alvisnlp/alvisnlp-bibliome/src/main/resources/fr/inra/maiage/bibliome/alvisnlp/bibliomefactory/resources/XMLReader/grobid-tei2alvisnlp.xslt");
		return new StreamFactory().getFileSourceStream("/home/rbossy/code/pesv-tm/TEI/trafilatura-tei2alvisnlp.xslt");
	}

	@Override
	public Mapping getStringParams() {
		return new Mapping();
	}

	@Override
	public SourceStream getXMLSource() {
		return new StreamFactory().getDirectorySourceStream(xmlDirPath);
	}

	@Override
	public Boolean getHtml() {
		return false;
	}

	@Override
	public Boolean getRawTagNames() {
		return true;
	}

	@Param
	public InputDirectory getGrobidHome() {
		return grobidHome;
	}

	@Param
	public InputDirectory getSource() {
		return source;
	}

	public void setSource(InputDirectory source) {
		this.source = source;
	}

	public void setGrobidHome(InputDirectory grobidHome) {
		this.grobidHome = grobidHome;
	}
}
