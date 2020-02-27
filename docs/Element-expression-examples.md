# Expression examples

## Introduction

The type of the expected kind of context element is given between
brackets in the example title. For detailed information on all the
expression parts, you may refer to \[\[Element expression reference\]\].



## Sections in the corpus \[corpus\]

```
documents.sections
```



## Abstract sections \[corpus\]

```
documents.sections:abstract
```

or

```
documents.sections[@name == "abstract"]
```



## Abstract sections in the test set \[corpus\]


```
documents[@set == "test"].sections:abstract
```

This assumes that documents have a feature with key *set* that denote
the set to which it pertains.



## Documents whose PMID is in a file \[corpus\]


```
documents[@pmid in "good_pmids.txt"]
```

This assumes that the PMID of the document is in a feature with key
*pmid*, and there is a file in the current directory named
*good\_pmids.txt* containing all PMIDs of interest (one per line).



## All genes and taxa \[section\]


```
layer:genes | layer:taxa
```

or

```
layer[@'ne-tpe' == "gene" or @'ne-type' == "species"]
```

The first is faster. It assumes that all gene annotations are in a layer
named *genes*, and that all taxon annotations are in a layer *taxa*. The
annotations are given in the following order: first genes in standard
order, then taxa in standard order.

The second assumes that annotations have a feature names *ne-type*
containing the named entity type of the annotation. The annotations are
given in standard order regardless of the type.



## All words included in a sentence \[annotation\]


```
inside:words
```

This assumes that the context element is an annotation representing a
sentence. It also assumes that all words are in a layer named *words*.



## All verbs \[section\]


```
layer:words[@pos ^= "V"]
```

or


```
layer:words[@pos =~ "^V"]
```

Both assume all word annotations are in a layer named *words* and have a
feature with key *pos* whose value denote its POS.



## All syntactic dependencies \[section\]


```
relations:dependencies.tuples
```

This assumes syntactic dependencies are in a relation named
*dependencies*.



## Words that are subject \[section\]


```
relations:dependencies.tuples[@label == "SUBJ:V-N"].args:dependent
```

If you insist to check they are subjet to verbs:


```
relations:dependencies.tuples[@label == "SUBJ:V-N" and args:head.@pos ^= "V"].args:dependent
```



## Subject of a verb \[annotation\]


```
tuple:dependencies:head[@label == "SUBJ:V-N"].args:dependent
```
