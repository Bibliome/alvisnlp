# AlvisNLP data structure

The *AlvisNLP* data structure is an object shared by all processing steps. It is passed from one processing step to the next. The data structure contains the document structure and content, as well as annotations produced by successive steps. The understanding of the data structure is crucial to to use *AlvisNLP* since this object allows the steps to communicate with each other.

The following figure presents an UML-like specification of the *AlvisNLP* data structure.

![Data model](assets/images/alvis_data_model.png)

* **Corpus**: a `Corpus` object represents a collection of documents. In an AlvisNLP run, the corpus is a unique object passed from module to module. A `Corpus` object has *features* and *documents*.

* **Document**: a `Document` object represents a single document. Each document has an identifier which is unique in the corpus. A `Document` object has *features* and *sections*.

* **Section**: a `Section` object contains a piece of the document's text contents. Each section has a *name*, a *contents*, *features*, *layers*, and *relations*.

* **Layer**: a `Layer` object is an *annotation* container. A `Layer` object has a *name* unique in the section.

* **Annotation**: an `Annotation` object represents a span of text created by a module. Each annotation is included in at least one *layer*. An `Annotation` object has a *start* and *end* which represent the coordinates of the annotation in the *section*'s *contents*, and *features*.

* **Relation**: a `Relation` object is a *tuple* container. A `Relation` object has a *name* unique in the section and *features*.

* **Tuple**: a `Tuple` object represents a relation between several elements in the data structure. A `Tuple` object has several *arguments*, each argument is an element (`Corpus`, `Document`, `Section`, `Relation`, but most often `Annotation` or `Tuple`) accessible through a *role* name. A `Tuple` object also has *features*.

* **Features** are key-value pairs that contain information on an element type, tag or property. Feature keys are not unique in an element, though when accessing a feature key, the last value is returned.
