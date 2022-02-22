import operator
import uuid
import collections
import json


def check_type(o, t):
    '''Check that an object has the specified type. This function uses `isinstance`, then raises ValueError if the result is false.

    Args:
        o: The object
        t: The type
    '''
    if not isinstance(o, t):
        raise ValueError('expected: ' + t + ', got: ' + o + ' of type ' + o.__class__)


class Element:
    '''Abstract class for all AlvisNLP elements.

    Attributes:
    serid (str): serialization identifier. This attribute can be set only once, attempts to assign a value to this attribute a second time will raise a Value Error. The serid must be unique within the corpus of this object, assignment to a value that is already in the corpus will raise a ValueError. Note that the first time a value is required, this object will automatically assign a value based on uuid.uuid4().
    corpus (Corpus): returns the corpus to which this element belongs. Immutable.
    '''

    def __init__(self):
        self._features = collections.defaultdict(list)
        self._serid = None

    @property
    def serid(self):
        if self._serid is None:
            self._serid = str(uuid.uuid4())
            self.corpus.all_elements[self._serid] = self
        return self._serid

    @serid.setter
    def serid(self, serid):
        if self._serid is not None:
            raise ValueError('serid already assigned to element')
        check_type(serid, str)
        if serid in self.corpus.all_elements:
            raise ValueError('duplicate serid')
        self._serid = serid
        self.corpus.all_elements[serid] = self

    @property
    def corpus(self):
        raise NotImplementedError()

    def get_feature(self, key):
        '''Returns a list containing all feature values with the specified key. Returns an empty list if there are no features with the specified key. Modifications of the returned list will not change the features in this object.

        Args:
        key (str): Feature key.

        Returns:
        get_feature: a list of feature values.
        '''
        if key in self._features:
            return list(self._features[key])
        return []

    def get_last_feature(self, key):
        '''Returns the value of the last feature with the specified key. This is probably what you're looking for. If tthere is no feature with the specified key, then returns an empty string.

        Args:
        key (str): Feature key.

        Returns:
        get_last_feature (str): a feature value.
        '''
        if key in self._features:
            return self._features[key][-1]
        return ''

    def add_feature(self, key, value):
        '''Adds a feature key/value pair.

        Args:
        key (str): The feature key.
        value (str): The feature value.

        Raises:
        ValueError: key or value are not str.
        '''
        check_type(key, str)
        check_type(value, str)
        self._features[key].append(value)

    def _features_and_id_to_json(self):
        j = {'id': self.serid}
        j['f'] = dict(self._features.items())
        return j

    def _features_and_id_from_json(self, j):
        if 'id' in j:
            self.serid = j['id']
        self._features.update(j['f'])


class Corpus(Element):
    '''AlvisNLP Corpus object.

    Attributes:
    documents (list[Document]): list of documents contained in this corpus. Modifications to this list will not change this corpus content. Immutable.
    '''
    def __init__(self):
        Element.__init__(self)
        self.all_elements = {}
        self._documents = {}

    @property
    def corpus(self):
        return self

    @property
    def documents(self):
        return list(self._documents.values())

    def has_document(self, identifier):
        '''Checks if this corpus contains a document with the specified identifier.

        Args:
        identifier (str): Document identifier.

        Returns:
        has_document (bool): either this corpus contains a document with the specified identifier.
        '''
        return identifier in self._documents

    def get_document(self, identifier):
        '''Returns the document in this corpus with the specified identifier.

        Args:
        identifier (str): Document identifier.

        Raises:
        KeyError: There is no document in this corpus with the specified identifier.

        Returns:
        get_document (Document): the document in this corpus with the specified identifier.
        '''
        return self._documents[identifier]

    def add_document(self, doc):
        '''Adds the specified document in this corpus. This function is called by the Document contructor.

        Args:
        doc (Document): The document.

        Raises:
        ValueError: doc is not of type document, or this corpus has already a document with the same identifier.
        '''
        check_type(doc, Document)
        if doc.identifier in self._documents:
            raise ValueError('duplicate document identifier: ' + doc.identifier)
        self._documents[doc.identifier] = doc

    def to_json(self):
        j = self._features_and_id_to_json()
        j['documents'] = list(doc._to_json() for doc in self.documents)
        return j

    def write_json(self, f):
        json.dump(self.to_json(), f)

    @staticmethod
    def from_json(j):
        corpus = Corpus()
        corpus._features_and_id_from_json(j)
        for dj in j['documents']:
            corpus._document_from_json(dj)
        corpus._dereference_tuple_arguments()
        return corpus

    @staticmethod
    def parse_json(f):
        return Corpus.from_json(json.load(f))

    def _document_from_json(self, j):
        doc = Document(self, j['identifier'])
        doc._features_and_id_from_json(j)
        for sj in j['sections']:
            doc._section_from_json(sj)
        return doc

    def _dereference_tuple_arguments(self):
        for doc in self.documents:
            for sec in doc.sections:
                for rel in sec.relations:
                    for t in rel.tuples:
                        for role, ref in t.args.items():
                            t.set_arg(role, self.all_elements[ref])


class Document(Element):
    '''An AlvisNLP Document object.

    Args:
    corpus (Corpus): the corpus where to insert this document. The constructor calls Corpus#add_document().
    identifier (str): this document identifier.

    Raises:
    ValueError: corpus is not of type Corpus, identifier is not of type str, or the corpus has already a document with the specified identifier.

    Attributes:
    identifier (str): This document identifier. Immutable.
    sections (list[Section]): List of sections in this document. Modifications will not change this document content. Immutable.
    '''
    def __init__(self, corpus, identifier):
        Element.__init__(self)
        check_type(corpus, Corpus)
        check_type(identifier, str)
        self._corpus = corpus
        self._identifier = identifier
        self._sections = []
        corpus.add_document(self)

    @property
    def corpus(self):
        return self._corpus

    @property
    def identifier(self):
        return self._identifier

    @property
    def sections(self):
        return list(self._sections)

    def has_section(self, name):
        '''Check either this document contains at least one section with the specified name.

        Args:
        name (str): Section name.

        Returns:
        has_section (bool): Either this document has at least one section with the specified name.
        '''
        for sec in self._sections:
            if sec.name == name:
                return True
        return False

    def get_sections(self, name):
        '''Iterates through sections in this document with the specified name.

        Args:
        name (str): Sections name.

        Returns:
        get_sections (Iterable[Section]): all sections in this document with the specified name.
        '''
        for sec in self._sections:
            if sec.name == name:
                yield sec

    def add_section(self, sec):
        '''Adds the specified section in this document.

        Args:
        sec (Section): The section to add.

        Raises:
        ValueError: sec is not of thype Section.
        '''
        check_type(sec, Section)
        self._sections.append(sec)

    def _to_json(self):
        j = self._features_and_id_to_json()
        j['identifier'] = self.identifier
        j['sections'] = list(sec._to_json() for sec in self.sections)
        return j

    def _section_from_json(self, j):
        corpus = self.corpus
        sec = Section(self, j['name'], j['contents'])
        sec._features_and_id_from_json(j)
        for aj in j['annotations']:
            sec._annotation_from_json(aj)
        for name, aj in j['layers'].items():
            layer = Layer(sec, name)
            for a in aj:
                layer.add_annotation(corpus.all_elements[a])
        for rj in j['relations']:
            sec._relation_from_json(rj)
        return sec


class Section(Element):
    '''An AlvisNLP Section object.

    Args:
    document (Document): The document where to add this section.
    name (str): This section name.
    contents (str): The text content of this section.

    Raises:
    ValueError: document is not of type Document, name is not of type str, or contents is not of type str.

    Attributes:
    document (Document): Document to which this section belongs. Immutable.
    name (str): Name of this section. Immutable.
    contents (str): Text content of this section. Immutable.
    layers (list[Layer]): List of all layers in this section. Modifications will not change this section layers. Immutable.
    relations (list[Relation]): List of all relations in this section. Modifications will not change this section relations. Immutable.
    '''
    def __init__(self, document, name, contents):
        Element.__init__(self)
        check_type(document, Document)
        check_type(name, str)
        check_type(contents, str)
        self._document = document
        self._name = name
        self._order = len(document.sections)
        self._contents = contents
        self._layers = {}
        self._relations = {}
        document.add_section(self)

    @property
    def corpus(self):
        return self._document.corpus

    @property
    def document(self):
        return self._document

    @property
    def name(self):
        return self._name

    @property
    def order(self):
        return self._order

    @property
    def contents(self):
        return self._contents

    @property
    def layers(self):
        return list(self._layers.values())

    @property
    def relations(self):
        return list(self._relations.values())

    def has_layer(self, name):
        '''Checks if this section has a layer with the specified name.

        Args:
        name (str): Layer name.

        Returns:
        has_layer (bool): either this section has a layer with the specified name.
        '''
        return name in self._layers

    def get_layer(self, name):
        '''Returns the layer in this section with the specified name.

        Args:
        name (str): Layer name.

        Raises:
        KeyError: there is no layer in this section with the specified name.

        Returns:
        get_layer (Layer): the layer in this section with the specified name.
        '''
        return self._layers[name]

    def add_layer(self, layer):
        '''Adds the specified layer in this section.

        Args:
        layer (Layer): The layer to add.

        Raises:
        ValueError: layer is not of type Layer, or this section has already a layer with the same name.
        '''
        check_type(layer, Layer)
        if layer.name in self._layers:
            raise ValueError('duplicate layer name: ' + layer.name + ' in ' + self)
        self._layers[layer.name] = layer

    def has_relation(self, name):
        '''Checks if this section has a relation with the specified name.

        Args:
        name (str): Relation name.

        Returns:
        has_relation (bool): either this section has a relation with the specified name.
        '''
        return name in self._relations

    def get_relation(self, name):
        '''Returns the relation in this section with the specified name.

        Args:
        name (str): Relation name.

        Raises:
        KeyError: there is no relation in this section with the specified name.

        Returns:
        get_relation (Relation): the relation in this section with the specified name.
        '''
        return self._relations[name]

    def add_relation(self, rel):
        '''Adds the specified relation in this section.

        Args:
        rel (Relation): The relation to add.

        Raises:
        ValueError: relation is not of type Relation, or this section has already a relation with the same name.
        '''
        check_type(rel, Relation)
        if rel.name in self._relations:
            raise ValueError('duplicate relation name: ' + rel.name + ' in ' + self)
        self._relations[rel.name] = rel

    def _to_json(self):
        j = self._features_and_id_to_json()
        j['name'] = self.name
        j['contents'] = self.contents
        annotations = set()
        for layer in self.layers:
            for a in layer.annotations:
                annotations.add(a)
        j['annotations'] = list(a._to_json() for a in annotations)
        j['layers'] = dict((layer.name, list(a.serid for a in layer.annotations)) for layer in self.layers)
        j['relations'] = list(rel._to_json() for rel in self.relations)
        return j

    def _annotation_from_json(self, j):
        oj = j['off']
        a = Annotation(self, oj[0], oj[1])
        a._features_and_id_from_json(j)
        return a

    def _relation_from_json(self, j):
        rel = Relation(self, j['name'])
        rel._features_and_id_from_json(j)
        for tj in j['tuples']:
            rel._tuple_from_json(tj)
        return rel


class Layer:
    '''An AlvisNLP Layer object. Beware: Layer is not an Element, it has no features. A layer acts as an iterable of annotations.

    Args:
    section (Section): Section to which this layer belongs.
    name (str): Layer name.

    Raises:
    ValueError: section is not of type Section, name is not of type str, or the section has already a layer with the specified name.

    Attributes:
    name (str): this layer name. Immutable.
    annotations (list[Annotation]): list of annotations in this layer. Modifications will not change this layer annotations. Immutable.
    '''
    def __init__(self, section, name=None, annotations=None):
        check_type(section, Section)
        check_type(name, str)
        self._section = section
        self._name = name
        if annotations is None:
            self._annotations = []
        else:
            self._annotations = list(annotations)
        if name is not None:
            section.add_layer(self)

    @property
    def section(self):
        return self._section

    @property
    def name(self):
        return self._name

    @property
    def annotations(self):
        return list(self._annotations)

    def _sub_layer(self, annotations):
        return Layer(self._section, annotations=annotations)

    def __len__(self):
        return len(self._annotations)

    def __iter__(self):
        self._annotations.sort(key=Span.ORDER_KEY)
        return iter(self._annotations)

    def add_annotation(self, a):
        '''Adds the specified annotation in this layer.

        Args:
        a (Annotation): the annotation to add.

        Raises:
        ValueError: a is not of type Annotation.
        '''
        check_type(a, Annotation)
        self._annotations.append(a)


class Span:
    '''A span object has a start and end offset. This is a utility class.

    Args:
    start (int): start offset.
    end (int): end offset.

    Raises:
    ValueError: start is not of type int, end is not of type int, start is lower than 0, end is lower than start.

    Attributes:
    start (int): start offset. Immutable.
    end (int): end offset. Immutable.
    '''

    ORDER_KEY = operator.attrgetter('_order')
    '''Key for use in sort().'''

    def __init__(self, start, end):
        check_type(start, int)
        check_type(end, int)
        if start < 0:
            raise ValueError('start (' + start + ') < 0')
        if end < start:
            raise ValueError('end (' + end + ') < start (' + start + ')')
        self._start = start
        self._end = end
        self._order = (start, -end)

    @property
    def start(self):
        return self._start

    @property
    def end(self):
        return self._end


class Annotation(Element, Span):
    '''An AlvisNLP Annotation object.

    Args:
    sec (Section): section of this annotation.
    start (int): start offset.
    end (int): end offset.

    Raises:
    ValueError: sec is not of type Section, or start and end are invalid Span offsets.

    Attributes:
    section (Section): Section to which belongs this annotation. Immutable.
    form (str): Surface form of this annotation. Immutable.
    '''

    def __init__(self, sec, start, end):
        Element.__init__(self)
        Span.__init__(self, start, end)
        check_type(sec, Section)
        if end > len(sec.contents):
            raise ValueError('end (' + end + ') > length (' + len(sec.contents) + ')')
        self._section = sec
        self._form = sec.contents[start:end]

    @property
    def corpus(self):
        return self._section.corpus

    @property
    def section(self):
        return self._section

    @property
    def form(self):
        return self._form

    def __len__(self):
        return self.end - self.start

    def _to_json(self):
        j = self._features_and_id_to_json()
        j['off'] = [self.start, self.end]
        return j


class Relation(Element):
    '''An AlvisNLP Relation object.

    Args:
    section (Section): Section to which this relation belongss.
    name (str): Relation name.

    Raises:
    ValueError: section is not of type Section, name is not of type str, or the section has already a relation with the spectified name.

    Attributes:
    section (Section): Section to which belongs this relation. Immutable.
    name (str): Relation name. Immutable.
    tuples (list[Tuple]): list of tuples in this relation. Modifications of this list will not affect this relation tuples. Immutable.
    '''
    def __init__(self, section, name):
        Element.__init__(self)
        check_type(section, Section)
        check_type(name, str)
        self._section = section
        self._name = name
        self._tuples = []
        section.add_relation(self)

    @property
    def corpus(self):
        return self._section.corpus

    @property
    def section(self):
        return self._section

    @property
    def name(self):
        return self._name

    @property
    def tuples(self):
        return list(self._tuples)

    def add_tuple(self, t):
        '''Adds the specified tuple to this relation.

        Args:
        t (Tuple): the tuple to add.

        Raises:
        ValueError: t is not of type Tuple.
        '''
        check_type(t, Tuple)
        self._tuples.append(t)

    def _to_json(self):
        j = self._features_and_id_to_json()
        j['name'] = self.name
        j['tuples'] = list(t._to_json() for t in self.tuples)
        return j

    def _tuple_from_json(self, j):
        t = Tuple(self)
        t._features_and_id_from_json(j)
        for role, ref in j['args'].items():
            t.set_arg(role, ref)
        return t


class Tuple(Element):
    '''An AlvisNLP Tuple object.

    Args:
    relation (Relation): relation to which this tuple belongs.

    Raises:
    ValueError: relation is not of type Relation.

    Attributes:
    relation (Relation): relation to which this tuple belongs. Immutable.
    arity (int): Number of arguments set in this tuple.
    args (dict[str,Element]): A dictionary with all arguments of this tuple. Modifications to this tuple will not change this tuple arguments. Immutable.
    '''
    def __init__(self, relation):
        Element.__init__(self)
        check_type(relation, Relation)
        self._relation = relation
        self._args = {}
        relation._tuples.append(self)

    @property
    def corpus(self):
        return self._relation.corpus

    @property
    def relation(self):
        return self._relation

    @property
    def arity(self):
        return len(self._args)

    @property
    def args(self):
        return dict(self._args)

    def set_arg(self, role, arg):
        '''Sets an argument. This method will overwrite any previous argument with the same role.

        Args:
        role (str): Argument role.
        arg (Element): Argument itself.

        Raises:
        ValueError: role is not of type str, or arg is not of type Element.
        '''
        check_type(role, str)
        check_type(arg, (Element, str))
        self._args[role] = arg

    def has_arg(self, role):
        '''Checks if this tuple has an argument with the specified role.

        Args:
        role (str): Argument role.

        Returns:
        has_arg (bool): either this tuple has an argument with the specified role.
        '''
        return role in self._args

    def get_arg(self, role):
        '''Returns the argument in this tuple with the specified role.

        Args:
        role (str): Argument role.

        Raises:
        KeyError: this tuple has no argument with the specified role.

        Returns:
        get_arg (Element): The argument in this tuple with the specified role.
        '''
        return self._args[role]

    def _to_json(self):
        j = self._features_and_id_to_json()
        j['args'] = dict((role, arg.serid) for (role, arg) in self.args.items())
        return j
