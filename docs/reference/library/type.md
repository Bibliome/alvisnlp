<h1 class="library">type</h1>

## Synopsis

*type* provides functions to examine the type of the context element. Element types are coded as single character strings:
		
* **C**: the corpus element;
* **D**: a document;
* **S**: a section;
* **A**: an annotation;
* **R**: a relation;
* **T**: a tuple.



## Functons

<a name="annotation">

### annotation

`type:annotation()`

Returns true if the context element is an annotation.

<a name="corpus">

### corpus

`type:corpus()`

Returns true if the context element is the corpus.

<a name="document">

### document

`type:document()`

Returns true if the context element is a document.

<a name="get">

### get

`type:get()`

Returns the type of the context element as a single character code.

<a name="relation">

### relation

`type:relation()`

Returns true if the context element is a relation.

<a name="section">

### section

`type:section()`

Returns true if the context element is a section.

<a name="test">

### test

`type:test:t()`

Tests that the context element has the type *t*. *t* must be a single character element type code.

<a name="tuple">

### tuple

`type:tuple()`

Returns true if the context element is a tuple.

