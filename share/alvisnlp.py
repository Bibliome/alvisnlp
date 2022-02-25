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


class Features:
    def __init__(self, elt):
        self._elt = elt
        self._data = collections.defaultdict(list)

    def has(self, key):
        check_type(key, str)
        return key in self._data

    def __contains__(self, key):
        return self.has(key)

    def get(self, key):
        '''Returns a list containing all feature values with the specified key. Returns an empty list if there are no features with the specified key. Modifications of the returned list will not change the features in this object.

        Args:
        key (str): Feature key.

        Returns:
        get_feature: a list of feature values.
        '''
        return list(self._data[key])

    def get_last(self, key):
        '''Returns the value of the last feature with the specified key. This is probably what you're looking for. If tthere is no feature with the specified key, then returns an empty string.

        Args:
        key (str): Feature key.

        Returns:
        get_last_feature (str): a feature value.
        '''
        if key in self._data:
            return self._data[key][-1]
        return ''

    def __getitem__(self, key):
        return self.get_last(key)

    def add(self, key, value):
        '''Adds a feature key/value pair.

        Args:
        key (str): The feature key.
        value (str): The feature value.

        Raises:
        ValueError: key or value are not str.
        '''
        check_type(key, str)
        check_type(value, str)
        self._data[key].append(value)
        self._elt._add_event(AddFeature(key, value))

    def __setitem__(self, key, value):
        self.add(key, value)

    def remove(self, key, value=None):
        check_type(key, str)
        check_type(value, (str, None.__class__))
        if key not in self._data:
            raise ValueError()
        if value is None:
            del self._data[key]
        else:
            self._data[key].remove(value)
        self._elt._add_event(RemoveFeature(key, value))


class DetachedElementError(Exception):
    def __init__(self, *args, **kwargs):
        Exception.__init__(self, *args, **kwargs)


class Element:
    '''Abstract class for all AlvisNLP elements.

    Attributes:
    serid (str): serialization identifier. This attribute can be set only once, attempts to assign a value to this attribute a second time will raise a Value Error. The serid must be unique within the corpus of this object, assignment to a value that is already in the corpus will raise a ValueError. Note that the first time a value is required, this object will automatically assign a value based on uuid.uuid4().
    corpus (Corpus): returns the corpus to which this element belongs. Immutable.
    '''

    def __init__(self):
        self._features = Features(self)
        self._events = []
        self._serid = None
        self._detached = True

    def check_detached(self):
        if self._detached:
            raise DetachedElementError()

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
    def events(self):
        self.check_detached()
        return list(self._events)

    @property
    def features(self):
        self.check_detached()
        return self._features

    def _add_event(self, e):
        check_type(e, Event)
        if self.corpus.log_events:
            self.check_detached()
            if any(e.supercedes_event(pe) for pe in self._events):
                self._events = list(pe for pe in self._events if not e.supercedes_event(pe))
                if not e.remove_self:
                    self._events.append(e)
            else:
                self._events.append(e)

    def has_event(self):
        raise NotImplementedError()

    @property
    def corpus(self):
        raise NotImplementedError()

    def _features_and_id_from_json(self, j):
        if 'id' in j:
            self.serid = j['id']
        self._features._data.update(j['f'])

    def events_to_json(self):
        self.check_detached()
        j = {'_ev': list(e.to_json() for e in self._events)}
        self._fill_events_json(j)
        return j

    def _fill_events_json(self, j):
        raise NotImplementedError()


class Documents:
    def __init__(self, corpus):
        self._corpus = corpus
        self._data = {}

    def __iter__(self):
        yield from self._data.values()

    def __len__(self):
        return len(self._data)

    def has(self, identifier):
        '''Checks if this corpus contains a document with the specified identifier.

        Args:
        identifier (str): Document identifier.

        Returns:
        has_document (bool): either this corpus contains a document with the specified identifier.
        '''
        return identifier in self._data

    def __contains__(self, identifier):
        return self.has(identifier)

    def get(self, identifier):
        '''Returns the document in this corpus with the specified identifier.

        Args:
        identifier (str): Document identifier.

        Raises:
        KeyError: There is no document in this corpus with the specified identifier.

        Returns:
        get_document (Document): the document in this corpus with the specified identifier.
        '''
        return self._data[identifier]

    def __getitem__(self, identifier):
        return self.get(identifier)

    def _add(self, doc):
        '''Adds the specified document in this corpus. This function is called by the Document contructor.

        Args:
        doc (Document): The document.

        Raises:
        ValueError: doc is not of type document, or this corpus has already a document with the same identifier.
        '''
        check_type(doc, Document)
        if doc.identifier in self._data:
            raise ValueError('duplicate document identifier: ' + doc.identifier)
        self._data[doc.identifier] = doc
        doc._detached = False
        self._corpus._add_event(CreateDocument(doc))

    def _remove(self, doc):
        check_type(doc, Document)
        cdoc = self.get(doc.identifier)
        if cdoc is not doc:
            raise ValueError()
        del self._data[doc.identifier]
        doc._detached = True
        self._corpus._add_event(DeleteDocument(doc))


class Corpus(Element):
    '''AlvisNLP Corpus object.

    Attributes:
    documents (list[Document]): list of documents contained in this corpus. Modifications to this list will not change this corpus content. Immutable.
    '''
    def __init__(self, log_events=True):
        Element.__init__(self)
        self._detached = False
        self.all_elements = {}
        self._documents = Documents(self)
        self.log_events = log_events

    @property
    def corpus(self):
        return self

    def has_event(self):
        self.check_detached()
        return (len(self._events) > 0) or any(doc.has_event() for doc in self._documents)

    @property
    def documents(self):
        return self._documents

    def _fill_events_json(self, j):
        self.check_detached()
        j['docs'] = dict((doc.serid, doc.events_to_json()) for doc in self.documents if doc.has_event())

    def write_events_json(self, f):
        self.check_detached()
        json.dump(self.events_to_json(), f)

    @staticmethod
    def from_json(j, log_events):
        corpus = Corpus(log_events=log_events)
        corpus._features_and_id_from_json(j)
        for dj in j['documents']:
            corpus._document_from_json(dj)
        corpus._dereference_tuple_arguments()
        return corpus

    @staticmethod
    def parse_json(f, log_events=True):
        corpus = Corpus.from_json(json.load(f), False)
        corpus.log_events = log_events
        return corpus

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
                            t.args.set(role, self.all_elements[ref])


class Sections:
    def __init__(self, doc):
        self._doc = doc
        self._data = []

    def __iter__(self):
        yield from self._data

    def __len__(self):
        return len(self._data)

    def has(self, name):
        '''Check either this document contains at least one section with the specified name.

        Args:
        name (str): Section name.

        Returns:
        has_section (bool): Either this document has at least one section with the specified name.
        '''
        for sec in self._data:
            if sec.name == name:
                return True
        return False

    def __contains__(self, name):
        return self.has(name)

    def get(self, name):
        '''Iterates through sections in this document with the specified name.

        Args:
        name (str): Sections name.

        Returns:
        get_sections (Iterable[Section]): all sections in this document with the specified name.
        '''
        return list(sec for sec in self._data if sec.name == name)

    def __getitem__(self, key):
        if isinstance(key, int):
            return self._data[key]
        return self.get(key)

    def _add(self, sec):
        '''Adds the specified section in this document.

        Args:
        sec (Section): The section to add.

        Raises:
        ValueError: sec is not of thype Section.
        '''
        check_type(sec, Section)
        self._data.append(sec)
        sec._detached = False
        self._doc._add_event(CreateSection(sec))

    def _remove(self, sec):
        check_type(sec, Section)
        if sec not in self._data:
            raise ValueError()
        self._data.remove(sec)
        sec._detached = True
        self._doc._add_event(DeleteSection(sec))


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
        self._sections = Sections(self)
        corpus.documents._add(self)

    @property
    def corpus(self):
        return self._corpus

    @property
    def identifier(self):
        return self._identifier

    def has_event(self):
        self.check_detached()
        return (len(self._events) > 0) or any(sec.has_event() for sec in self._sections)

    @property
    def sections(self):
        self.check_detached()
        return self._sections

    def detach(self):
        self.check_detached()
        self.corpus.documents._remove(self)

    def _fill_events_json(self, j):
        self.check_detached()
        j['secs'] = dict((sec.serid, sec.events_to_json()) for sec in self._sections if sec.has_event())

    def _section_from_json(self, j):
        corpus = self.corpus
        sec = Section(self, j['name'], j['contents'])
        sec._features_and_id_from_json(j)
        for aj in j['annotations']:
            sec._annotation_from_json(aj)
        for name, aj in j['layers'].items():
            layer = Layer(sec, name)
            for a in aj:
                layer.add(corpus.all_elements[a])
        for rj in j['relations']:
            sec._relation_from_json(rj)
        return sec


class Relations:
    def __init__(self, sec):
        self._sec = sec
        self._data = {}

    def __iter__(self):
        yield from self._data.values()

    def __len__(self):
        return len(self._data)

    def has(self, name):
        '''Checks if this section has a relation with the specified name.

        Args:
        name (str): Relation name.

        Returns:
        has_relation (bool): either this section has a relation with the specified name.
        '''
        return name in self._data

    def __contains__(self, name):
        return self.has(name)

    def get(self, name):
        '''Returns the relation in this section with the specified name.

        Args:
        name (str): Relation name.

        Raises:
        KeyError: there is no relation in this section with the specified name.

        Returns:
        get_relation (Relation): the relation in this section with the specified name.
        '''
        return self._data[name]

    def __getitem__(self, name):
        return self.get(name)

    def _add(self, rel):
        '''Adds the specified relation in this section.

        Args:
        rel (Relation): The relation to add.

        Raises:
        ValueError: relation is not of type Relation, or this section has already a relation with the same name.
        '''
        check_type(rel, Relation)
        if rel.name in self._data:
            raise ValueError('duplicate relation name: ' + rel.name + ' in ' + self._sec)
        self._data[rel.name] = rel
        rel._detached = False
        self._sec._add_event(CreateRelation(rel))

    def _remove(self, rel):
        check_type(rel, Relation)
        srel = self._data[rel.name]
        if srel is not rel:
            raise ValueError()
        del self._data[rel.name]
        rel._detached = True
        self._sec._events(DeleteRelation(rel))


class Layers:
    def __init__(self, sec):
        self._sec = sec
        self._data = {}

    def __iter__(self):
        yield from self._data.values()

    def __len__(self):
        return len(self._data)

    def has(self, name):
        '''Checks if this section has a layer with the specified name.

        Args:
        name (str): Layer name.

        Returns:
        has_layer (bool): either this section has a layer with the specified name.
        '''
        return name in self._data

    def __contains__(self, name):
        return self.has(name)

    def get(self, name):
        '''Returns the layer in this section with the specified name.

        Args:
        name (str): Layer name.

        Raises:
        KeyError: there is no layer in this section with the specified name.

        Returns:
        get_layer (Layer): the layer in this section with the specified name.
        '''
        if name not in self._data:
            layer = Layer(self._sec, name)
            self._data[name] = layer
            return layer
        return self._data[name]

    def __getitem__(self, name):
        return self.get(name)


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
        self._layers = Layers(self)
        self._relations = Relations(self)
        document.sections._add(self)

    @property
    def corpus(self):
        return self._document.corpus

    @property
    def document(self):
        self.check_detached()
        return self._document

    @property
    def name(self):
        self.check_detached()
        return self._name

    @property
    def order(self):
        self.check_detached()
        return self._order

    def has_event(self):
        self.check_detached()
        return (len(self._events) > 0) or any(rel.has_event() for rel in self._relations) or any(layer.has_event() for layer in self._layers)

    @property
    def contents(self):
        self.check_detached()
        return self._contents

    @property
    def layers(self):
        self.check_detached()
        return self._layers

    @property
    def relations(self):
        self.check_detached()
        return self._relations

    def detach(self):
        self.check_detached()
        self.document.sections._remove(self)

    def _fill_events_json(self, j):
        self.check_detached()
        j['rels'] = dict((rel.serid, rel.events_to_json()) for rel in self._relations if rel.has_event())
        a_events = dict()
        for layer in self._layers:
            for a in layer.annotations:
                if a.serid not in a_events and a.has_event():
                    a_events[a.serid] = a.events_to_json()
        j['as'] = a_events

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
    def __init__(self, section, name):
        check_type(section, Section)
        check_type(name, str)
        self._section = section
        self._name = name
        self._annotations = []
        section.layers._data[name] = self

    @property
    def section(self):
        return self._section

    @property
    def name(self):
        return self._name

    @property
    def annotations(self):
        return list(self._annotations)

    def has_event(self):
        return any(a.has_event() for a in self._annotations)

    def __len__(self):
        return len(self._annotations)

    def __iter__(self):
        self._annotations.sort(key=Span.ORDER_KEY)
        return iter(self._annotations)

    def __getitem__(self, index):
        return self._annotations[index]

    def add(self, a):
        '''Adds the specified annotation in this layer.

        Args:
        a (Annotation): the annotation to add.

        Raises:
        ValueError: a is not of type Annotation.
        '''
        check_type(a, Annotation)
        self._annotations.append(a)
        a._add_event(AddToLayer(self))

    def __iadd__(self, a):
        self.add(a)
        return self

    def remove(self, a):
        check_type(a, Annotation)
        if a not in self._annotations:
            raise ValueError()
        self._annotations.remove(a)
        a._add_event(RemoveFromLayer(self))


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
        self._detached = False  # XXX check layers instead
        Span.__init__(self, start, end)
        check_type(sec, Section)
        if end > len(sec.contents):
            raise ValueError('end (' + end + ') > length (' + len(sec.contents) + ')')
        self._section = sec
        self._form = sec.contents[start:end]
        sec._add_event(CreateAnnotation(self))

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

    def has_event(self):
        return len(self._events) > 0

    def _fill_events_json(self, j):
        pass


class Tuples:
    def __init__(self, rel):
        self._rel = rel
        self._data = []

    def __iter__(self):
        yield from self._data

    def __len__(self):
        return len(self._data)

    def __getitem__(self, index):
        return self._data[index]

    def _add(self, t):
        '''Adds the specified tuple to this relation.

        Args:
        t (Tuple): the tuple to add.

        Raises:
        ValueError: t is not of type Tuple.
        '''
        check_type(t, Tuple)
        self._data.append(t)
        t._detached = False
        self._rel._add_event(CreateTuple(t))

    def _remove(self, t):
        check_type(t, Tuple)
        if t not in self._tuples:
            raise ValueError()
        self._data.remove(t)
        t._detached = True
        self._rel._add_event(DeleteTuple(t))


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
        self._tuples = Tuples(self)
        section.relations._add(self)

    @property
    def corpus(self):
        return self._section.corpus

    @property
    def section(self):
        self.check_detached()
        return self._section

    @property
    def name(self):
        return self._name

    def has_event(self):
        self.check_detached()
        return (len(self._events) > 0) or any(t.has_event() for t in self._tuples)

    @property
    def tuples(self):
        self.check_detached()
        return self._tuples

    def detach(self):
        self.check_detached()
        self.section.relations._remove(self)

    def _fill_events_json(self, j):
        self.check_detached()
        j['ts'] = dict((t.serid, t.events_to_json()) for t in self._tuples if t.has_event())

    def _tuple_from_json(self, j):
        t = Tuple(self)
        t._features_and_id_from_json(j)
        for role, ref in j['args'].items():
            t._args[role] = ref
        return t


class Arguments:
    def __init__(self, t):
        self._t = t
        self._data = {}

    def __iter__(self):
        yield from self._data

    def __len__(self):
        return len(self._data)

    def keys(self):
        return self._data.keys()

    def values(self):
        return self._data.values()

    def items(self):
        return self._data.items()

    def set(self, role, arg):
        '''Sets an argument. This method will overwrite any previous argument with the same role.

        Args:
        role (str): Argument role.
        arg (Element): Argument itself.

        Raises:
        ValueError: role is not of type str, or arg is not of type Element.
        '''
        check_type(role, str)
        check_type(arg, (Element, str))
        self._data[role] = arg
        self._t._add_event(SetArgument(role, arg))

    def __setitem__(self, role, arg):
        self.set(role, arg)

    def has(self, role):
        '''Checks if this tuple has an argument with the specified role.

        Args:
        role (str): Argument role.

        Returns:
        has_arg (bool): either this tuple has an argument with the specified role.
        '''
        return role in self._data

    def __contains__(self, role):
        return self.has(role)

    def get(self, role):
        '''Returns the argument in this tuple with the specified role.

        Args:
        role (str): Argument role.

        Raises:
        KeyError: this tuple has no argument with the specified role.

        Returns:
        get_arg (Element): The argument in this tuple with the specified role.
        '''
        return self._data[role]

    def __getitem__(self, role):
        return self.get(role)


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
        self._args = Arguments(self)
        relation.tuples._add(self)

    @property
    def corpus(self):
        return self._relation.corpus

    @property
    def relation(self):
        self.check_detached()
        return self._relation

    @property
    def arity(self):
        self.check_detached()
        return len(self._args)

    def has_event(self):
        self.check_detached()
        return len(self._events) > 0

    def detach(self):
        self.check_detached()
        self.relation.tuples._remove(self)

    def _fill_events_json(self, j):
        self.check_detached()
        pass

    @property
    def args(self):
        self.check_detached()
        return self._args


class Event:
    def __init__(self):
        pass

    def to_json(self):
        j = {'_': self.__class__.CODE}
        self._fill_json(j)
        return j

    def _fill_json(self, j):
        raise NotImplementedError()

    def supercedes_event(self, other):
        return False

    def remove_self(self):
        return True


class ElementEvent(Event):
    def __init__(self, elt):
        Event.__init__(self)
        self.elt = elt

    def _fill_json(self, j):
        j['_id'] = self.elt.serid


class CreateElementEvent(ElementEvent):
    def __init__(self, elt):
        ElementEvent.__init__(self, elt)


class DeleteElementEvent(ElementEvent):
    def __init__(self, elt):
        ElementEvent.__init__(self, elt)

    def supercedes_event(self, other):
        if isinstance(other, self.__class__.ANTI_EVENT_CLASS):
            return self.elt is other.elt
        return False


class FeatureEvent(Event):
    def __init__(self, key, value):
        Event.__init__(self)
        self.key = key
        self.value = value

    def _fill_json(self, j):
        j['k'] = self.key
        j['v'] = self.value


class AddFeature(FeatureEvent):
    CODE = 1

    def __init__(self, key, value):
        FeatureEvent.__init__(self, key, value)


class RemoveFeature(FeatureEvent):
    CODE = 2

    def __init__(self, key, value=None):
        FeatureEvent.__init__(self, key, value)

    def supercedes_event(self, other):
        if isinstance(other, AddFeature):
            if other.key == self.key:
                return self.value is None or self.value == other.value
        return False


class CreateDocument(CreateElementEvent):
    CODE = 3

    def __init__(self, doc):
        CreateElementEvent.__init__(self, doc)

    def _fill_json(self, j):
        CreateElementEvent._fill_json(self, j)
        j['id'] = self.elt.identifier


class DeleteDocument(DeleteElementEvent):
    CODE = 4
    ANTI_EVENT_CLASS = CreateDocument

    def __init__(self, doc):
        DeleteElementEvent.__init__(self, doc)


class CreateSection(CreateElementEvent):
    CODE = 5

    def __init__(self, sec):
        CreateElementEvent.__init__(self, sec)

    def _fill_json(self, j):
        CreateElementEvent._fill_json(self, j)
        j['name'] = self.elt.name
        j['contents'] = self.elt.contents


class DeleteSection(DeleteElementEvent):
    CODE = 6
    ANTI_EVENT_CLASS = CreateSection

    def __init__(self, sec):
        DeleteElementEvent.__init__(self, sec)


class CreateAnnotation(CreateElementEvent):
    CODE = 7

    def __init__(self, a):
        CreateElementEvent.__init__(self, a)

    def _fill_json(self, j):
        CreateElementEvent._fill_json(self, j)
        j['s'] = self.elt.start
        j['e'] = self.elt.end


class LayerEvent(Event):
    def __init__(self, layer):
        Event.__init__(self)
        self.layer = layer

    def _fill_json(self, j):
        j['l'] = self.layer.name


class AddToLayer(LayerEvent):
    CODE = 9

    def __init__(self, layer):
        LayerEvent.__init__(self, layer)

    def supercedes_event(self, other):
        if isinstance(other, (AddToLayer, RemoveFromLayer)):
            return self.layer is other.layer
        return False

    def remove_self(self):
        return False


class RemoveFromLayer(LayerEvent):
    CODE = 10

    def __init__(self, layer):
        LayerEvent.__init__(self, layer)

    def supercedes_event(self, other):
        if isinstance(other, AddToLayer):
            return self.layer is other.layer
        return False


class CreateRelation(CreateElementEvent):
    CODE = 11

    def __init__(self, rel):
        CreateElementEvent.__init__(self, rel)

    def _fill_json(self, j):
        CreateElementEvent._fill_json(self, j)
        j['name'] = self.elt.name


class DeleteRelation(DeleteElementEvent):
    CODE = 12
    ANTI_EVENT_CLASS = CreateRelation

    def __init__(self, rel):
        DeleteElementEvent.__init__(self, rel)


class CreateTuple(CreateElementEvent):
    CODE = 13

    def __init__(self, t):
        CreateElementEvent.__init__(self, t)


class DeleteTuple(DeleteElementEvent):
    CODE = 14
    ANTI_EVENT_CLASS = CreateTuple

    def __init__(self, t):
        DeleteElementEvent.__init__(self, t)


class SetArgument(Event):
    CODE = 15

    def __init__(self, role, arg):
        self.role = role
        self.arg = arg

    def _fill_json(self, j):
        j['r'] = self.role
        j['a'] = self.arg.serid

    def supercedes_event(self, other):
        if isinstance(other, SetArgument):
            return self.role == other.role
        return False

    def remove_self(self):
        return False
