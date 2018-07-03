package fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.uima;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.logging.Logger;

import org.apache.uima.UIMAException;
import org.apache.uima.cas.CASRuntimeException;
import org.apache.uima.cas.admin.CASAdminException;
import org.apache.uima.cas.impl.XmiCasSerializer;
import org.xml.sax.SAXException;

import fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.SectionModule;
import fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.SectionModule.SectionResolvedObjects;
import fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.uima.types.DocumentProxy;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Corpus;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Document;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.EvaluationContext;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.ResolverException;
import fr.inra.maiage.bibliome.alvisnlp.core.module.ModuleException;
import fr.inra.maiage.bibliome.alvisnlp.core.module.ProcessingContext;
import fr.inra.maiage.bibliome.alvisnlp.core.module.ProcessingException;
import fr.inra.maiage.bibliome.alvisnlp.core.module.lib.AlvisNLPModule;
import fr.inra.maiage.bibliome.alvisnlp.core.module.lib.Param;
import fr.inra.maiage.bibliome.util.Iterators;
import fr.inra.maiage.bibliome.util.files.OutputDirectory;
import fr.inra.maiage.bibliome.util.files.OutputFile;

@AlvisNLPModule(beta=true)
public class XMIExport extends SectionModule<SectionResolvedObjects> {
	private OutputDirectory outDir;
	private OutputFile typeSystemFile;
	private Boolean dkproCompatibility = false;
	
	@Override
	public void process(ProcessingContext<Corpus> ctx, Corpus corpus) throws ModuleException {
		Logger logger = getLogger(ctx);
		EvaluationContext evalCtx = new EvaluationContext(logger);
		try {
			ExportHelper helper = new ExportHelper(this, logger, evalCtx);
			for (Document doc : Iterators.loop(documentIterator(evalCtx, corpus))) {
				DocumentProxy docProxy = helper.convertDocument(doc);
				docProxy.addToIndexes();
				try (OutputStream os = openDocumentFile(doc)) {
					XmiCasSerializer.serialize(helper.getCas(), null, os, true, null);
				}
			}
			if (typeSystemFile != null) {
				try (InputStream is = getClass().getResourceAsStream("/fr/inra/maiage/bibliome/alvisnlp/bibliomefactory/modules/uima/uima-document.xml")) {
					fr.inra.maiage.bibliome.util.Files.copy(is, typeSystemFile, 1024, false);
				}
			}
		}
		catch (CASRuntimeException|UIMAException|CASAdminException|SAXException|IOException e) {
			throw new ProcessingException(e);
		}
	}
	
	private OutputStream openDocumentFile(Document doc) throws FileNotFoundException {
		File file = new File(outDir, doc.getId() + ".xmi");
		File dir = file.getParentFile();
		dir.mkdirs();
		return new FileOutputStream(file);
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
	public OutputDirectory getOutDir() {
		return outDir;
	}

	@Param(mandatory=false)
	public OutputFile getTypeSystemFile() {
		return typeSystemFile;
	}

	@Param
	public Boolean getDkproCompatibility() {
		return dkproCompatibility;
	}

	public void setDkproCompatibility(Boolean dkproCompatibility) {
		this.dkproCompatibility = dkproCompatibility;
	}

	public void setTypeSystemFile(OutputFile typeSystemFile) {
		this.typeSystemFile = typeSystemFile;
	}

	public void setOutDir(OutputDirectory outDir) {
		this.outDir = outDir;
	}
}
