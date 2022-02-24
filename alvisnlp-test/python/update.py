import alvisnlp
import sys


corpus = alvisnlp.Corpus.parse_json(sys.stdin)


doc1, doc2 = corpus.documents[:2]
sec = doc1.sections[0]
layer = sec.get_layer('Title')
a = layer.annotations[0]
rel = sec.get_relation('Lives_In')
t = rel.tuples[0]
t.set_arg('role', a)
doc2.add_feature('new-doc-feature', 'new-doc-feature')

# add document, section, annotation, layer, relation and tuple
corpus.add_feature('new-corpus-feature', 'new-corpus-feature')
doc = alvisnlp.Document(corpus, 'new-document')
sec = alvisnlp.Section(doc, 'new-section', 'Lorem ipsum')
layer = alvisnlp.Layer(sec, 'new-layer')
a = alvisnlp.Annotation(sec, 0, 5)
layer.add_annotation(a)
rel = alvisnlp.Relation(sec, 'new-relation')
t = alvisnlp.Tuple(rel)
t.set_arg('role', a)


# corpus.write_jsondiff(sys.stdout)
corpus.write_events_json(sys.stdout)
