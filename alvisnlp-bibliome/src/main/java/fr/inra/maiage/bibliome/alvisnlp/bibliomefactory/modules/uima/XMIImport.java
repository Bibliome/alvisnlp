package fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.uima;

import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Logger;

import org.apache.uima.UIMAException;
import org.apache.uima.cas.admin.CASAdminException;
import org.apache.uima.cas.impl.XmiCasDeserializer;

import fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.CorpusModule;
import fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.ResolvedObjects;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Corpus;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.DefaultNames;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.NameType;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.creators.AnnotationCreator;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.creators.DocumentCreator;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.creators.SectionCreator;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.creators.TupleCreator;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.ResolverException;
import fr.inra.maiage.bibliome.alvisnlp.core.module.ModuleException;
import fr.inra.maiage.bibliome.alvisnlp.core.module.ProcessingContext;
import fr.inra.maiage.bibliome.alvisnlp.core.module.ProcessingException;
import fr.inra.maiage.bibliome.alvisnlp.core.module.lib.AlvisNLPModule;
import fr.inra.maiage.bibliome.alvisnlp.core.module.lib.Param;
import fr.inra.maiage.bibliome.util.Iterators;
import fr.inra.maiage.bibliome.util.streams.SourceStream;

@AlvisNLPModule(beta=true)
public abstract class XMIImport extends CorpusModule<ResolvedObjects> implements DocumentCreator, SectionCreator, AnnotationCreator, TupleCreator {
	private SourceStream source;
	private String defaultSectionName = DefaultNames.getDefaultSectionName();
    private Boolean baseNameId = false;
    private Boolean ignoreMalformedXMI = false;
    private Boolean dkproCompatibility = false;
    
	@Override
	public void process(ProcessingContext<Corpus> ctx, Corpus corpus) throws ModuleException {
		Logger logger = getLogger(ctx);
		try {
			ImportHelper helper = new ImportHelper(this, corpus);
			for (InputStream is : Iterators.loop(source.getInputStreams())) {
				String sourceName = source.getStreamName(is);
				logger.info("reading " + sourceName);
				try {
					XmiCasDeserializer.deserialize(is, helper.getCas(), true);
				}
				catch (Exception e) {
					if (ignoreMalformedXMI) {
						logger.warning("ignoring " + sourceName);
						continue;
					}
					throw new ProcessingException(e);
				}
				helper.convertDocument(sourceName);
			}
		}
		catch (UIMAException|CASAdminException|IOException e) {
			throw new ProcessingException(e);
		}
	}
	
	@Override
	protected ResolvedObjects createResolvedObjects(ProcessingContext<Corpus> ctx) throws ResolverException {
		return new ResolvedObjects(ctx, this);
	}

	@Param
	public SourceStream getSource() {
		return source;
	}

	@Param(nameType=NameType.SECTION)
	public String getDefaultSectionName() {
		return defaultSectionName;
	}

	@Param
	public Boolean getBaseNameId() {
		return baseNameId;
	}

	@Param
	public Boolean getIgnoreMalformedXMI() {
		return ignoreMalformedXMI;
	}

	@Param
	public Boolean getDkproCompatibility() {
		return dkproCompatibility;
	}

	public void setDkproCompatibility(Boolean dkproCompatibility) {
		this.dkproCompatibility = dkproCompatibility;
	}

	public void setIgnoreMalformedXMI(Boolean ignoreMalformedXMI) {
		this.ignoreMalformedXMI = ignoreMalformedXMI;
	}

	public void setDefaultSectionName(String defaultSectionName) {
		this.defaultSectionName = defaultSectionName;
	}

	public void setBaseNameId(Boolean baseNameId) {
		this.baseNameId = baseNameId;
	}

	public void setSource(SourceStream source) {
		this.source = source;
	}
	
}
