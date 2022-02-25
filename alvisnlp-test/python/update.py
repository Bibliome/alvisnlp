import alvisnlp
import sys


corpus = alvisnlp.Corpus.parse_json(sys.stdin)
doc1, doc2 = corpus.documents

corpus.remove_document(doc2)

doc1.add_feature('doc-feature', 'doc-feature')
doc1.remove_feature('set')

sec = doc1.sections[0]

layer = sec.get_layer('Habitat')
a1 = layer.annotations[0]
layer.remove_annotation(a1)

layer = sec.get_layer('Bacteria')
a2 = layer.annotations[0]
layer.add_annotation(a1)

rel = sec.get_relation('Lives_In')
t = rel.tuples[0]
t.set_arg('role', a2)


# corpus.write_jsondiff(sys.stdout)
corpus.write_events_json(sys.stdout)
