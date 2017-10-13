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



package org.bibliome.alvisnlp.modules;

import java.util.logging.Logger;

import org.bibliome.alvisnlp.modules.SectionModule.SectionResolvedObjects;

import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Annotation;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Corpus;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Layer;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.NameType;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Section;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.creators.AnnotationCreator;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.EvaluationContext;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.ResolverException;
import fr.inra.maiage.bibliome.alvisnlp.core.module.ModuleException;
import fr.inra.maiage.bibliome.alvisnlp.core.module.ProcessingContext;
import fr.inra.maiage.bibliome.alvisnlp.core.module.lib.AlvisNLPModule;
import fr.inra.maiage.bibliome.alvisnlp.core.module.lib.Param;
import fr.inra.maiage.bibliome.util.Iterators;

/**
 * Native java implementation of the toknizer embedded in Ogmios.
 */
@AlvisNLPModule
public abstract class OgmiosTokenizer extends SectionModule<SectionResolvedObjects> implements AnnotationCreator {
    private String   targetLayerName  = null;
    private String  tokenTypeFeature = null;
    private Boolean separatorTokens  = true;

    private enum TokenType {
        alpha,
        num,
        sep,
        symb;

        private static final TokenType getTokenType(char c) {
            if (Character.isLetter(c))
                return alpha;
            if (Character.isDigit(c))
                return num;
            if (Character.isWhitespace(c))
                return sep;
            return symb;
        }
    }

    @Override
    public String[] addFeaturesToSectionFilter() {
        return new String[] {};
    }

    @Override
    public String[] addLayersToSectionFilter() {
        return null;
    }
	
	@Override
	protected SectionResolvedObjects createResolvedObjects(ProcessingContext<Corpus> ctx) throws ResolverException {
		return new SectionResolvedObjects(ctx, this);
	}

    @Override
    public void process(ProcessingContext<Corpus> ctx, Corpus corpus) throws ModuleException {
		Logger logger = getLogger(ctx);
		EvaluationContext evalCtx = new EvaluationContext(logger);
        for (Section sec : Iterators.loop(sectionIterator(evalCtx, corpus))) {
            Layer layer = sec.ensureLayer(targetLayerName);
            TokenType lastTokenType = TokenType.symb;
            String contents = sec.getContents();
            int lastPos = 0;
            for (int i = 0; i < contents.length(); i++) {
                TokenType tokenType = TokenType.getTokenType(contents.charAt(i));
                if ((tokenType == TokenType.symb) || (tokenType != lastTokenType)) {
                    if ((i > 0) && (separatorTokens || (lastTokenType != TokenType.sep))) {
                            Annotation ann = new Annotation(this, layer, lastPos, i);
                            ann.addFeature(tokenTypeFeature, lastTokenType.toString());
                    }
                    lastPos = i;
                    lastTokenType = tokenType;
                }
            }
            if (separatorTokens || (lastTokenType != TokenType.sep)) {
                Annotation ann = new Annotation(this, layer, lastPos, contents.length());
                ann.addFeature(tokenTypeFeature, lastTokenType.toString());
            }
        }
    }

    @Param(nameType=NameType.LAYER, defaultDoc = "Name of the layer where to store the tokens.")
    public String getTargetLayerName() {
        return targetLayerName;
    }

    public void setTargetLayerName(String targetLayerName) {
        this.targetLayerName = targetLayerName;
    }

    @Param(nameType=NameType.FEATURE, defaultDoc = "Name of the token feature where to store the token type (alpha, num, sep, symb).")
    public String getTokenTypeFeature() {
        return tokenTypeFeature;
    }

    public void setTokenTypeFeature(String tokenTypeFeature) {
        this.tokenTypeFeature = tokenTypeFeature;
    }

    @Param(defaultDoc = "Either if separator tokens should be added.")
    public Boolean getSeparatorTokens() {
        return separatorTokens;
    }

    public void setSeparatorTokens(Boolean separatorTokens) {
        this.separatorTokens = separatorTokens;
    }
}
