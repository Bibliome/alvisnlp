# Expression reference

## Introduction

Element Expressions is a language for exploring and querying the
AlvisNLP Corpus. It can be used to test features, count elements,
retrieve annotations with certain characteristics, etc. This language
shares a lot of common points with XPath, so if you are, or become,
familiar with XPath, then good for you. You may find the following pages
also useful:

-   <a href="{{ '/Element-expression-examples' | relative_url }}">Expression Examples</a> contains several examples of
expressions in increasing order of trickiness;
-   {% include module class="Shell" %} describes the AlvisNLP/ML Shell that you can use to
train yourself to write expressions.



## Context Element

Expressions are evaluated within a context that includes an element. The
context element can be one of the following:

-   the corpus
-   a document
-   a section
-   an annotation
-   a relation
-   a tuple

Some expressions are independent of the context element; their
evaluation does not depend on it (for instance arithmetic operators).
However the most useful expressions depend on the context element, for
instance the evaluation of a feature value expression obviously depends
on the context element. Wherever an expression is expected, for instance
as a module parameter, the context element sould be documented.



## Evaluation types

An expression can be evaluated as on of four types: boolean, number,
string or element list. The evaluation type should be documented along
with the context element.



### Scalar types



#### Boolean

The boolean type has two values: `false` and `true`.



#### Double

The double type is a double precision 64-bit floating point number (Java
`double`).



#### Integer

The integer type is a 32-bit signed integer (Java `int`).



#### String

The string type is a 16-bit unicode character sequence (Java `String`).



### Element list

The element list type is an ordered collection of elements. In most
cases elements in an element list are of the same type (all Annotations
or all Documents etc.).



### Type coercion

The majority of expressions have a priviledged or primary evaluation
type, however they can be evaluated into any other type. The value is
computed using the following type coercion rules:

|    | **boolean** | **number** | **string** | **list** |
|----|------------|------------|------------|----------|
| **boolean**        |                  |       false=0, true=1             |                    false="false", true="true"    |     empty list |
| **number**  |  0=false, otherwise true    |      |                                             decimal notation string     |       empty list |
| **string**   | ""=false, otherwise true   |   decimal conversion, 0 if string is not number   |    |                               empty list |
| **list**   |   empty=false, otherwise true  | element count          |                         concatenation of static features   |     |

Some expressions have specific coercion rules.




## Syntax for names

Some expressions require a name (feature key or layer name for
instance). Names are single quote character sequences. The quotes can be
omitted if all the following conditions are met:

-   all characters are alphabetic (`A-Za-z`) or undescore (`_`)
-   the name is different from any reserved word:

```
after and any arg args before boolean contents corpus delete document documents double elements else end false feat fun if in int inside layer length not new or outside overlapping relation relations section sections span start string then true tuples
```

Note that names and keywords are case-sensitive. All keywords are all
lowercase.



## Construct reference

In the following sections each available expression is described. The
usage of the expression is given in code blocks with the
following conventions:

```
EXPR         uppercase words are variable parts of the expression construct, they are meant to be replaced either by sub-expressions, names or literals
layer        lowercase words are keywords
( ) . + :    all other symbols are operators, parentheses or a column, they are part of the expression syntax
```

If there is a preferred type for the expression, then this type is
specified between brackets in the expression name.



### Boolean literal \[boolean\]

```
false
true
```



### Integer literal \[integer\]

```
[0-9]+
```



### Double literal \[double\]

```
([0-9]*\.)?[0-9]+
```



### String literal \[string\]

```
"..."
```

String constants are double quoted character sequences. The usual Java
escape sequences apply.



### Boolean operators \[boolean\]

```
LEFT and RIGHT
LEFT or RIGHT
not EXPR
```

`LEFT`, `RIGHT` and `EXPR` are evaluated as boolean with the same
context element.
Binary boolean operator evaluation is short-circuited.



### General comparison \[boolean\]

```
LEFT == RIGHT
LEFT != RIGHT
```

`LEFT` and `RIGHT` are evaluated as the same type with the same context
element. If `LEFT` is an expression of scalar type, then its type is
used. Otherwise, the type of `RIGHT` is used.



### Number comparison \[boolean\]

```
LEFT < RIGHT
LEFT > RIGHT
LEFT <= RIGHT
LEFT >= RIGHT
```

`LEFT` and `RIGHT` are evaluated as doubles using the same context
element.



### String comparison \[boolean\]

```
LEFT ?= RIGHT
LEFT ^= RIGHT
LEFT =^ RIGHT
```

`LEFT` and `RIGHT` are evaluated as strings using the same context
element.

| **Operator** |  |
|--------------|--|
| `?=` | contains |
| `^=` | starts with |
| `=^` | ends with |



### String concatenation \[string\]

```
LEFT ^ RIGHT
```

`LEFT` and `RIGHT` are evaluated as strings with the same context
element.



### Regexp match

```
TARGET =~ "PATTERN"
```

`TARGET` is evaluated as a string with the same context element.
`"PATTERN"` is a string constant containing a regular expression in
[Java syntax](http://download.oracle.com/javase/8/docs/api/java/util/regex/Pattern.html)\
If evaluated as a boolean, then this expression returns either the
target matches the pattern.\
If evaluated as a number, then this expression returns the number of
non-overlapping matches of the pattern in the target.\
If evaluated as a string, then this expression returns the first match
of the pattern in the target.\
If evaluated as an element list, then this expression returns an empty
list.



### Arithmetic \[double\]

```
LEFT + RIGHT
LEFT - RIGHT
LEFT * RIGHT
LEFT / RIGHT
LEFT % RIGHT
```

`LEFT` and `RIGHT` are both evaluated as numbers with the same context
element.



### Unary minus \[double\]

```
- EXPR
```

`EXPR` is evaluated as a number with the same context element.



### Dictionary lookup \[boolean\]

```
EXPR in "FILE"[:"ENCODING"]
```

`EXPR` is evaluated as a string with the same context element. `"FILE"`
is a string constant containg the path to a dictionary file.
`"ENCODING"` is a string constant containing the name of the dictionary
file character set. If the encoding is omitted, UTF-8 is assumed.\
The dictionary file must contain one entry per line. This expression
returns true if and only if the dictionary contains the first operand.



### Feature \[string\]

```
@KEY
```

`KEY` is a name. This expression returns the last value of the feature
with key `KEY` of context element.\
If this expression is evaluated as a boolean, then it returns `true` if
and only if the context element has a feature with key `KEY`, *even if
the feature value is an empty string*.



### Any feature value equals \[boolean\]

```
any KEY == EXPR
```

`KEY` is a name. `EXPR` is evaluated as a string with the same context
element.\
This expression returns true if at least one of the values of the
feature with key `KEY` in the context element equals `EXPR`.



### Annotation positions \[integer\]

```
start
end
```

These expressions return respectively the start and end positions if the
context element is an annotation. Otherwise it returns `0`.



### Element length \[integer\]

```
length
```

If the context element is an annotation, then this expression returns
its length.\
If the context element is a section, then this expression returns
returns the length of the section's contents.



### Section contents \[string\]

```
contents
```

If the context element is a section, then this expression returns its
contents. Otherwise the empty string is returned.



### Conditional

```
if CONDITION then TRUE else TRUE
```

`CONDITION` is evaluated as a boolean with the same context element. If
the result is, then `TRUE` is evaluated as the same type with the same
context element. Otherwise `FALSE` is evaluated as the same type with
the same context element.



### Union \[list\]

```
LEFT | RIGHT
```

`LEFT` and `RIGHT` are evaluated as element lists with the same context
element. This expression returns the concatenation of the two results.\
Elements in the result list are not reordered. Duplicate lements remain.



### Path

```
LEFT . RIGHT
```

`LEFT` is evaluated as an element list, then each element of the result
is used as the context element to evaluate `RIGHT`.\
If this expression is evaluated as a boolean, then it retuns `true` if
any evaluation of `RIGHT` as a boolean is true.\
If this expression is evaluated as a number, then it returns the sum of
all successive evaluations of `RIGHT` as a number.\
If this expression is evaluated as a string, then it returns the
concatenation of all successive evaluations of `RIGHT` as a string.\
If this expression is evaluated as a list, then it returns the
concatenation of all successive evaluations of `RIGHT` as a list.



### Element navigation expressions \[list\]

Element navigation expressions returns elements according to a
navigation specification. The following subsections describe each
available specification.\
A specification can be followed by filters and ranges. The order of
filter and ranges specifies the order in which they are applied. If a
range follows a filter, then range is applied after the filter.



#### Filters

```
SPEC [ EXPR ]
```

`SPEC` is a navigation specification. `EXPR` is evaluated as a boolean
with the current element as the context element.\
The expression returns the list of elements for which `EXPR` was
evaluated as `true`.



#### Ranges

```
SPEC { N }
SPEC { N : M }
SPEC { : M }
SPEC { N : }
```

`SPEC` is a navigation specification. `N` and `M` are integer
constants.\
The returned list is a sublist of the list returned by `SPEC`:

| **Specification** |  |
|-------------------|--|
| `N` | a singleton list with the `N`th element|
| `N : M` | the sublist from the `N`th (inclusive) to the `M`th (exclusive) elements|
| `: M` | the sublist from the start to the `M`th element (exlusive)|
| `N :` | the sublist from the `N`th element (inclusive) to the end|

List indexes are zero-based: `0` is the first, `1` is the second, etc.
If `N` or `M` are negative, then the length of the list + 1 is added to
their value: `-1` is the last (inclusive).\
If the indexes are out of the list boundaries then the index is
"cropped".



#### Self

```
$
```

This expression returns the context element.



#### Element corpus

```
corpus
```

This expression returns the currently annotated corpus.



#### Element document

```
document
```

This expression returns a singleton list containing the document to
which the context element belongs.\
If the context element is a document, then this document is returned.\
If the context element is the corpus, then the empty list is returned.



#### Element section

```
section
```

This expression returns a singleton list containing the section to which
the context element belongs.\
If the context element is a section, then this section is returned.\
If the context element is the corpus or a document, then the empty list
is returned.



#### Tuple relation

```
relation
```

If the context element is a tuple, then this element returns a singleton
list with the relation to which the tuple belongs. Otherwise it returns
the empty list.



#### Corpus documents

```
documents
documents : ID
```

`ID` is a name. If the context element is the corpus, then this
expression returns a singleton list containing the document with the
identifier `ID`. If `ID` is omitted, then this expression returns a list
containing all documents in the corpus.\
If the context element is not the corpus, or there is no document in the
corpus, or there is no document with the specified identifier, then this
expression returns an empty list.



#### Document sections

```
sections
sections : NAME
```

`NAME` is a name. If the context element is a document, then this
expression returns a list containing all sections in the document with
the name `NAME`. If `NAME` is omitted, then this expression returns all
sections of the document.\
If the context element is not a document, or there is no section in the
document, or there is no section with the specified name, then this
expression returns the empty list.



#### Section annotations

```
layer
layer : NAME
```

`NAME` is a name. If the context element is a section, then this
expression returns a list containing all annotations in the layer named
`NAME`. If `NAME` is omitted, then this expression returns all
annotations of all layers of the section.\
If the context element is not a section, or there is no layer with the
specified name, or there is no annotation in the section, or the layer
with the specified name is empty, then this expression returns the empty
list.

In all cases, the list of annotations is sorted by standard order
(increasing start, then decreasing end) and duplicates are removed.



#### Section relations

```
relations
relations : NAME
```

`NAME` is a name. If the context element is a section, then this
expression returns a singleton list containing the relation in the
section with the name `NAME`. If `NAME` is omitted, then this expression
returns all relations of the section.\
If the context element is not a section, or there is no relation in the
section, or there is no relation with the specified name, then this
expression returns the empty list.



#### Relation tuples

```
tuples
```

If the context element is a relation, then this expression returns a
list of all tuples of the relation. Otherwise it returns the empty list.



#### Tuple arguments

```
args
args : ROLE
```

`ROLE` is a name. If the context element is a tuple, then this
expression returns a singleton list containing the annotation which is
the argument of the tuple with the role `ROLE`. If `ROLE` is omitted,
then this expression returns a list containg all arguments of the tuple
(in no particular order).\
If the context element is not a tuple, or the tuple has no arguments, or
if the tuple does not have an argument with the specified role, then
this expression returns the empty list.



#### Reverse tuple lookup

```
tuples : RELATION
tuples : RELATION : ROLE
```

`RELATION` and `ROLE` are names. If the context element is an
annotation, then this expression retuerns a list containing all tuples
that satisfy all the following conditions:

1.  the tuple pertain to the relation with name `RELATION` in the same
section

2.  the annotation is the argument of the tuple with role `ROLE`, if
`ROLE` is omitted, then the annotation is an argument of the tuple
regardless of the role\
If the context element is not an annotation, or the section does not
contain a relation with the specified name, then this expression
returns the empty list.



#### Annotation siblings

```
after : NAME
before : NAME
inside : NAME
outside : NAME
overlapping : NAME
span : NAME
```

If the context element is an annotation, then this expression returns a
list of annotations in the layer with name `NAME` in the same section.
The annotations included in the result list depend on the keyword:

| **Functor** |  |
|---------------|-----------------------------------------------|
| `after`       | start after the context annotation end        |
| `before`      | end before the context annotation start       |
| `inside`      | fully included in the context annotation span |
| `outside`     | fully includes the context annotation         |
| `overlapping` | overlaps (broad sense) the context annotation |
| `span`        | exact same span as the context annotation     |

If the context element is not an annotation, or the section does not
have a layer with the specified name, then this expression returns the
empty list.\
In all cases, the returned list is sorted in standard order.



### Side-effect expressions

Side-effect expressions affect the corpus data structure. Only some
modules allow actions inside an expression; refer to the module
documentation.



#### Element creation

```
new : document ( ID )
new : section ( NAMEEXPR , CONTENTS)
new : section : NAME ( CONTENTS )
new : relation : NAME
new : tuple
new : LAYER ( START , END )
new : LAYER ( ANNOTATIONS )
```

Expressions that start with `new:` create a new element and return it.
The type of the element depends on the next functor. The created element
is attached to the context element. If the context element is not of the
adequate type then the expression does nothing, and returns nothing.\
`ID` is an expression evaluated as a string. If the context element is
the corpus, then `new:document(ID)` creates a document with the
specified identifier. If the corpus already contains a document with the
specified identifier, then an error is issued.\
`NAMEEXPR` and `CONTENTS` are expressions evaluated as strings. If the
context element is a document, then `new:section(NAMEEXPR,CONTENTS)`
creates a section with the specified name and contents.\
`NAME` is a name: `new:section:NAME(CONTENTS)` allowes to create a
section by specifying the name without an expression.\
If the context element is a section, then `new:relation:NAME` creates a
relation with the specified name.\
`START` and `END` are expressions evaluated as integers. If the context
element is a section, then `new:LAYER(START,END)` creates an annotation
with the specified positions, then adds it to the specified layer.\
`ANNOTATIONS` is an expression evaluated as a list of elements.
`new:LAYER(ANNOTATIONS)` creates an annotation that covers all
annotations of the same section in `ANNOTATIONS`, then adds it to the
specified layer.



#### Set argument

```
arg : ROLE ( ARG )
```

`ROLE` is a name and `ARG` is an expression evaluated as a list of
elements. If the context element is a tuple, the this expression sets
the argument with the specified role to the first annotation in the
result of `ARG`. If the evaluation of `ARG` does not yield any
annotation, then this expression does nothing.\
This expression always return the context element, so different
arguments can be chained by paths.



#### Set feature

```
feat : KEY ( VALUE )
```

`KEY` is a name and `VALUE` is an expression evaluated as a string. This
expression adds a feature with the specified key and value to the
context element.\
This expression always return the context element, so different
arguments can be chained by paths.



#### Delete element

```
delete
```

This expression deletes the context element. The deletion is permanent.
The corpus cannot be deleted.



#### Add to layer

```
add : NAME
```

If the context element is an annotation, then this expression adds it to
the layer named `NAME`.



#### Remove from layer

```
remove : NAME
```

If the context element is an annotation, then this expression removes it
from the layer named `NAME`.



### Library function call

lib : FTOR1 : ... : FTORn ( ARG1 , ... , ARGm )

This expression calls a library function with arguments `ARG1`, ...,
`ARGm`.





## Operator precedence and associativity

The following operators are listed in descending order of precedence.
The precedence can be overriden with parentheses.

| **Operators**                        | **Associative**
|--------------------------------------|-----------------
| `if then else`                       | no
| `or`                                 | yes
| `and`                                | yes
| `not`                                | no
| `== != < > <= >= ?= ^= =^ =~ in any` | no
| `^`                                  | yes
| `+ -`                                | yes
| `* / %`                              | yes
| unary `-`                            | no
| pipe                                 | yes
| `.`                                  | yes
