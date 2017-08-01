# Table of Contents

> * [Introduction](#Introduction-1)
> * [Overview](#Overview-1)
> * [Target elements](#Target-elements-1)
> * [Target examples](#Target-examples-1)
>   * [Documents](#Documents-1)
>   * [Annotations](#Annotations-1)
>   * [Tuples](#Tuples-1)
> * [Relation definition](#Relation-definition-1)
>   * [Attributes](#Attributes-1)
>     * [Attribute Examples](#Attribute-Examples-1)
>       * [All-uppercase word](#All-uppercase-word-1)
>       * [Number of words in sentence](#Number-of-words-in-sentence-1)
>       * [POS category of word](#POS-category-of-word-1)
>   * [Bags](#Bags-1)
>     * [Bag examples](#Bag-examples-1)
>       * [Document word vector](#Document-word-vector-1)
>       * [Syntactic dependency argument](#Syntactic-dependency-argument-1)
</toc>



<a name="Introduction-1" />

# Introduction

This page provides pragmatic insights about the generic Weka wrapper
modules, for complete parameter description refer to the reference
documentation of
[TrainingElementClassifier](http://bibliome.jouy.inra.fr/demo/alvisnlp/api/modules/org.bibliome.alvisnlp.modules.newclassifiers.TrainingElementClassifier),
[TaggingElementClassifier](http://bibliome.jouy.inra.fr/demo/alvisnlp/api/modules/org.bibliome.alvisnlp.modules.newclassifiers.TaggingElementClassifier)
and
[SelectingElementClassifier](http://bibliome.jouy.inra.fr/demo/alvisnlp/api/modules/org.bibliome.alvisnlp.modules.newclassifiers.SelectingElementClassifier).


<a name="Overview-1" />

# Overview

1.  decide the target elements
2.  writing a relation definition
3.  select attributes with `SelectingElementClassifier`
4.  training a classifier on training elements with
`TrainingElementClassifier`
5.  using the classifier to tag elements with `TaggingElementClassifier`


<a name="Target-elements-1" />

# Target elements

The target elements are the elements you want to classify, these are
specified by the `example` parameter in each of the three modules. It is
an \[\[Element Expression\]\] evaluated as a list of elements with the
corpus as the context element. The resulting collection of elements will
be the training set in `TrainingElementClassifier` and
`SelectingElementClassifier`, or the elements to predict the class in
`TaggingElementClassifier`.


<a name="Target-examples-1" />

# Target examples


<a name="Documents-1" />

## Documents

```
documents
```

To restrict the target to only some documents, for instance the training
set:

```
documents(set == "train")
```

This assumes that documents have a feature with key `set` and an
appropriate value. For instance, this feature could have been added by
the reader module that loaded some files into the corpus.


<a name="Annotations-1" />

## Annotations


```
documents.sections.layer:sentences
```

This assumes a layer named `sentences` that contains annotations
representing sentences. For instance this layer could have been filled
with
[SeSMig](http://bibliome.jouy.inra.fr/demo/alvisnlp/api/modules/org.bibliome.alvisnlp.modules.segmig.SeSMig).

To restrict the target to only some sentences, for instance those that
contain at least two gene names:

```
documents.sections.layer:sentences(inside:genes >= 2)
```

This assumes a layer named `genes` containing all gene names acquired
from previous modules.

Now for NER tasks, you may want to classify annotation n-grams, then
you'd use the
[NGrams](http://bibliome.jouy.inra.fr/demo/alvisnlp/api/modules/org.bibliome.alvisnlp.modules.NGrams)
module:

```xml
<tokenize class="OgmiosTokenizer">
<tokenTypeFeature>type</tokenTypeFeature>
<separatorTokens>false</separatorTokens>
<targetLayerName>tokens</targetLayerName>
</tokenize>

<ngrams class="NGrams">
<targetLayerName>ngrams</targetLayerName>
<tokenLayerName>tokens</targetLayerName>
<maxNGramSize>3</maxNGramSize>
</ngrams>
```


<a name="Tuples-1" />

## Tuples

Why not?


```
documents.sections.relations:genePairs.tuples
```

This assumes a relation named `genePairs`. Note that all gene name pairs
in a sentence can be generated with the module
[CartesianProductTuples](http://bibliome.jouy.inra.fr/demo/alvisnlp/api/modules/org.bibliome.alvisnlp.modules.CartesianProductTuples)
like this:

```xml
<genePairs class="CartesianProductTuples">
<anchor>documents.sections.layer:sentences</anchor>
<relationName>genePairs</relationName>
<arguments>
<first>inside:genes</first>
<second>inside:genes</second>
</arguments>
</genePairs>
```


Of course, you need to adjust the target so that your classifier does
not attempt to classify pairs of the same gene:


```
documents.sections.relations:genePairs.tuples(args:first != args:second)
```


<a name="Relation-definition-1" />

# Relation definition

Here, *relation* is used in the [meaning of
Weka](http://www.cs.waikato.ac.nz/ml/weka/arff.html), it does not mean
AlvisNLP/ML's relations.

The relation definition is specified by the `relationDefinition`
parameter in the three modules:

```xml
<relationDefinition>
<relation name="myrelation">
attribute and bag definitions
</relation>
</relationDefinition>
```

However we recommend to place the `relation` subtree in a separate file
and invoke it like this:

```xml
<relationDefinition load="myfile.xml"/>
```

Indeed it is important you use the same relation definition in the three
modules.

The relation name is optional and doesn't actually make a difference at
all.


<a name="Attributes-1" />

## Attributes

Each attribute is specified with an `attribute` tag:


```xml
<attribute
name="NAME"
type="TYPE"
class="CLASS">
EXPR
</attribute>
```

-   `NAME` is the name of the attribute, it is mandatory and must be
unique in the relation.

-   `TYPE` is the type of the attribute and can take either one of three
values: `bool`, `int` or `nominal`. If the type is omited, then it
is `bool` by default. If the type is `nominal`, then the attribute
definition must also specify all possible values:

```xml
<attribute
name="NAME"
type="nominal"
class="CLASS"
value="EXPR">
<value>value1</value>
<value>value2</value>
...
</attribute>
```


Note the alternative way to specify `EXPR`.

-   `CLASS` is a boolean (values allowed: `true`, `false`, `yes` and
`no`); it indicates either the attribute is the class attribute,
that is to say either if the attribute is the one predicted by
the classifier. If omitted then the attribute is not the class
attribute by default. There must be one and only one class attribute
in the relation definition.

-   `EXPR` is an expression that specifies the value of the attribute
for a given example element. To compute the value of the attribute
for a given element, AlvisNLP/ML evaluates `EXPR` with the element
as the context element. The type of the evaluation depends on the
type of the attribute:

| **Attribute type** | **Evaluation type** |
|--------------------|---------------------|
| `bool`             | boolean             |
| `int`              | number              |
| `nominal`          | string              |

If a nominal value evaluates to a string different from all declared
possible values then AlvisNLP/ML will issue an error.


<a name="Attribute-Examples-1" />

### Attribute Examples


<a name="All-uppercase-word-1" />

#### All-uppercase word

```xml
<attribute name="allcaps" type="bool">@form =~ "^[A-Z]$"</attribute>
```



<a name="Number-of-words-in-sentence-1" />

#### Number of words in sentence

```xml
<attribute name="wordcount" type="int">inside:words</attribute>
```


Do not count punctuations:

```xml
<attribute name="wordcount" type="int">inside:words[@type != "punctuation"]</attribute>
```

This assumes that words have a feature `type` indicating the word type
(see [WoSMig annotationTypeFeature
parameter](http://bibliome.jouy.inra.fr/demo/alvisnlp/api/modules/org.bibliome.alvisnlp.modules.segmig.WoSMig#annotationTypeFeature)).


<a name="POS-category-of-word-1" />

#### POS category of word


```xml
<attribute name="wordcount" type="nominal" value='@pos =~ "^."'>
<value>N</value>
<value>V</value>
<value>J</value>
<value>R</value>
<value>D</value>
</attribute>
```



<a name="Bags-1" />

## Bags

Bags are attribute generators mainly used to emulate bag-of-word
representations.

```xml
<bag
prefix="PREFIX"
key="KEY"
count="COUNT"
loadValues="FILE">
EXPR
</bag>
```

-   `PREFIX` is the prefix of all generated attribute names, it is
mandatory and xhoose it wisely so it does not create a name clash
with other attributes.

-   `KEY` is a feature name

-   `COUNT` is a boolean value that specifies the type of the generated
attributes:

**Value** | **Atribute type** | **Test** |
|---------|----------|----------|
| `false` |  boolean |  presence |
| `true`  |  number  |  count |

-   `FILE` is the path to a file containing all forms of the bags, it is
an UTF-8 encoded file with one value per line. AlvisNLP/ML generates
one attribute for each entry.

-   `EXPR` is an expression evaluated as a list of elements with the
example as the context element. For each element in the result, the
value of feature `KEY` sets or increments the corresponding
attribute (depending on `COUNT`).


<a name="Bag-examples-1" />

### Bag examples


<a name="Document-word-vector-1" />

#### Document word vector

```xml
<bag prefix="w__" key="lemma" count="yes" loadValues="words.txt">sections.layer:words</bar>
```

You may generate `words.txt` with
[AggregateValues](http://bibliome.jouy.inra.fr/demo/alvisnlp/api/modules/org.bibliome.alvisnlp.modules.AggregateValues):

```xml
<vocabulary class="AggregateValues">
<entries>documents.sections.layer:words</entries>
<key>@lemma</key>
<outFile>words.txt</outFile>
</vocabulary>
```



<a name="Syntactic-dependency-argument-1" />

#### Syntactic dependency argument

```xml
<bag prefix="syn__" key="lemma" loadValues="words.txt">tuple:dependencies:head.args:dependent</bar>
```
