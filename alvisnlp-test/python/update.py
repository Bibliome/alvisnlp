import alvisnlp
import sys


corpus = alvisnlp.Corpus.parse_json(sys.stdin)
doc1, doc2 = corpus.documents

corpus.documents.remove(doc2)

doc1.features.add('doc-feature', 'doc-feature')
doc1.features.remove('set')

sec = doc1.sections[0]

layer = sec.layers.get('Habitat')
a1 = layer.annotations[0]
layer.remove(a1)

layer = sec.layers.get('Bacteria')
a2 = layer.annotations[0]
layer.add(a1)

rel = sec.relations.get('Lives_In')
t = rel.tuples[0]
t.args.set('role', a2)


# corpus.write_jsondiff(sys.stdout)
corpus.write_events_json(sys.stdout)
