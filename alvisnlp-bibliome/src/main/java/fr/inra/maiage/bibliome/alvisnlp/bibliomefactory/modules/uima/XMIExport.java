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
import org.apache.uima.fit.factory.JCasFactory;
import org.apache.uima.jcas.JCas;
import org.xml.sax.SAXException;

import fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.SectionModule;
import fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.SectionModule.SectionResolvedObjects;
import fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.uima.types.DocumentProxy;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Corpus;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Document;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Section;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.EvaluationContext;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.ResolverException;
import fr.inra.maiage.bibliome.alvisnlp.core.module.ModuleException;
import fr.inra.maiage.bibliome.alvisnlp.core.module.ProcessingContext;
import fr.inra.maiage.bibliome.alvisnlp.core.module.lib.AlvisNLPModule;
import fr.inra.maiage.bibliome.alvisnlp.core.module.lib.Param;
import fr.inra.maiage.bibliome.util.Iterators;
import fr.inra.maiage.bibliome.util.StringCat;
import fr.inra.maiage.bibliome.util.files.OutputDirectory;
import fr.inra.maiage.bibliome.util.files.OutputFile;

@AlvisNLPModule(beta=true)
public class XMIExport extends SectionModule<SectionResolvedObjects> {
	private OutputDirectory outDir;
	private OutputFile typeSystemFile;
	
	@Override
	public void process(ProcessingContext<Corpus> ctx, Corpus corpus) throws ModuleException {
		Logger logger = getLogger(ctx);
		EvaluationContext evalCtx = new EvaluationContext(logger);
		try {
			JCas jcas = JCasFactory.createJCas();
			ExportHelper helper = new ExportHelper(this, logger, evalCtx, jcas);
			for (Document doc : Iterators.loop(documentIterator(evalCtx, corpus))) {
				jcas.setDocumentText(convertContents(evalCtx, doc));
				DocumentProxy docProxy = helper.convertDocument(doc);
				docProxy.addToIndexes();
				try (OutputStream os = openDocumentFile(doc)) {
					XmiCasSerializer.serialize(jcas.getCas(), null, os, true, null);
				}
				jcas.reset();
			}
			if (typeSystemFile != null) {
				try (InputStream is = getClass().getResourceAsStream("/fr/inra/maiage/bibliome/alvisnlp/bibliomefactory/modules/uima/uima-document.xml")) {
					fr.inra.maiage.bibliome.util.Files.copy(is, typeSystemFile, 1024, false);
				}
			}
		}
		catch (CASRuntimeException|UIMAException|CASAdminException|SAXException|IOException e) {
			rethrow(e);
		}
	}
	
	private OutputStream openDocumentFile(Document doc) throws FileNotFoundException {
		File file = new File(outDir, doc.getId() + ".xmi");
		File dir = file.getParentFile();
		dir.mkdirs();
		return new FileOutputStream(file);
	}

	private String convertContents(EvaluationContext evalCtx, Document doc) {
		StringCat strcat = new StringCat();
		for (Section sec : Iterators.loop(sectionIterator(evalCtx, doc))) {
			strcat.append(sec.getContents());
		}
		return strcat.toString();
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

	public void setTypeSystemFile(OutputFile typeSystemFile) {
		this.typeSystemFile = typeSystemFile;
	}

	public void setOutDir(OutputDirectory outDir) {
		this.outDir = outDir;
	}
}
