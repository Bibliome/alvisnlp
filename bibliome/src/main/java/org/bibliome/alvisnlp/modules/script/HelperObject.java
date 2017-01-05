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


package org.bibliome.alvisnlp.modules.script;

import java.util.logging.Logger;

import alvisnlp.corpus.Annotation;
import alvisnlp.corpus.Corpus;
import alvisnlp.corpus.Document;
import alvisnlp.corpus.Layer;
import alvisnlp.corpus.Relation;
import alvisnlp.corpus.Section;
import alvisnlp.corpus.Tuple;
import alvisnlp.module.ProcessingContext;

public class HelperObject {
    private final Script owner;
    private final ProcessingContext<Corpus> ctx;
    
    public final Corpus corpus;
    
    public HelperObject(ProcessingContext<Corpus> ctx, Script owner, Corpus corpus) {
        super();
        this.owner = owner;
        this.ctx = ctx;
        this.corpus = corpus;
    }

    public Document createDocument(String id) {
        return Document.getDocument(owner, corpus, id);
    }
    
    public Section createSection(Document doc, String name, String contents) {
        return new Section(owner, doc, name, contents);
    }
    
    public Annotation createAnnotation(Section sec, int start, int end) {
        return new Annotation(owner, sec, start, end);
    }
    
    public static Layer ensureLayer(Section sec, String name) {
        return sec.ensureLayer(name);
    }
    
    public Annotation createAnnotation(Layer layer, int start, int end) {
        return new Annotation(owner, layer, start, end);
    }
    
    public Relation createRelation(Section sec, String name) {
        return new Relation(owner, sec, name);
    }
    
    public Tuple createTuple(Relation relation) {
        return new Tuple(owner, relation);
    }
    
    public Logger getLogger() {
        return owner.getLogger(ctx);
    }
}
