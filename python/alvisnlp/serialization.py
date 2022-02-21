from .data import Corpus, Document, Section, Layer, Annotation, Relation, Tuple


class JsonSerializer:
    def __init__(self):
        pass

    def _create_object(self, elt):
        j = {'id': elt.serid}
        j['f'] = elt._features
        return j

    def serialize_corpus(self, corpus):
        j = self._create_object(corpus)
        j['documents'] = list(self.serialize_document(doc) for doc in corpus.documents)
        return j

    def serialize_document(self, doc):
        j = self._create_object(doc)
        j['identifier'] = doc.identifier
        j['sections'] = list(self.serialize_section(sec) for sec in doc.sections)
        return j

    def serialize_section(self, sec):
        j = self._create_object(sec)
        j['name'] = sec.name
        j['contents'] = sec.contents
        annotations = set()
        for layer in sec.layers:
            for a in layer.annotations:
                annotations.add(a)
        j['annotations'] = list(self.serialize_annotation(a) for a in annotations)
        j['layers'] = dict((layer.name, list(a.serid for a in layer.annotations)) for layer in sec.layers)
        j['relations'] = list(self.serialize_relation(rel) for rel in sec.relations)
        return j

    def serialize_annotation(self, a):
        j = self._create_object(a)
        j['off'] = [a.start, a.end]
        return j

    def serialize_relation(self, rel):
        j = self._create_object(rel)
        j['name'] = rel.name
        j['tuples'] = list(self.serialize_tuple(t) for t in rel.tuples)
        return j

    def serialize_tuple(self, t):
        j = self._create_object(t)
        j['args'] = dict((role, arg.serid) for (role, arg) in t.args.items())
        return j


class JsonDeserializer:
    def _deserialize_serid_and_features(self, j, elt):
        if 'id' in j:
            elt.serid = j['id']
        elt._features = j['f']

    def deserialize_corpus(self, j):
        corpus = Corpus()
        self._deserialize_serid_and_features(j, corpus)
        for dj in j['documents']:
            self.deserialize_document(corpus, dj)
        self.dereference_tuple_arguments(corpus)
        return corpus

    def deserialize_document(self, corpus, j):
        doc = Document(corpus, j['identifier'])
        self._deserialize_serid_and_features(j, doc)
        for sj in j['sections']:
            self.deserialize_section(doc, sj)
        return doc

    def deserialize_section(self, doc, j):
        corpus = doc.corpus
        sec = Section(doc, j['name'], j['contents'])
        self._deserialize_serid_and_features(j, sec)
        for aj in j['annotations']:
            self.deserialize_annotation(sec, aj)
        for name, aj in j['layers'].items():
            layer = Layer(sec, name)
            for a in aj:
                layer.add_annotation(corpus.all_elements[a])
        for rj in j['relations']:
            self.deserialize_relation(sec, rj)
        return sec

    def deserialize_annotation(self, sec, j):
        oj = j['off']
        a = Annotation(sec, oj[0], oj[1])
        self._deserialize_serid_and_features(j, a)
        return a

    def deserialize_relation(self, sec, j):
        rel = Relation(sec, j['name'])
        self._deserialize_serid_and_features(j, rel)
        for tj in j['tuples']:
            self.deserialize_tuple(rel, tj)
        return rel

    def deserialize_tuple(self, rel, j):
        t = Tuple(rel)
        self._deserialize_serid_and_features(j, t)
        for role, ref in j['args'].items():
            t.set_arg(role, ref)
        return t

    def dereference_tuple_arguments(self, corpus):
        for doc in corpus.documents:
            for sec in doc.sections:
                for rel in sec.relations:
                    for t in rel.tuples:
                        for role, ref in t.args.items():
                            t.set_arg(role, corpus.all_elements[ref])
