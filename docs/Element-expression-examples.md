# Table of Contents

> * [Introduction](#Introduction-1)
> * [Sections in the corpus \corpus\](#Sections-in-the-corpus-\corpus\-1)
> * [Abstract sections \corpus\](#Abstract-sections-\corpus\-1)
> * [Abstract sections in the test set \corpus\](#Abstract-sections-in-the-test-set-\corpus\-1)
> * [Documents whose PMID is in a file \corpus\](#Documents-whose-PMID-is-in-a-file-\corpus\-1)
> * [All genes and taxa \section\](#All-genes-and-taxa-\section\-1)
> * [All words included in a sentence \annotation\](#All-words-included-in-a-sentence-\annotation\-1)
> * [All verbs \section\](#All-verbs-\section\-1)
> * [All syntactic dependencies \section\](#All-syntactic-dependencies-\section\-1)
> * [Words that are subject \section\](#Words-that-are-subject-\section\-1)
> * [Subject of a verb \annotation\](#Subject-of-a-verb-\annotation\-1)
</toc>



<a name="Introduction-1" />

# Introduction

The type of the expected kind of context element is given between
brackets in the example title. For detailed information on all the
expression parts, you may refer to \[\[Element expression reference\]\].


<a name="Sections-in-the-corpus-\corpus\-1" />

# Sections in the corpus \[corpus\]

```
documents.sections
```


<a name="Abstract-sections-\corpus\-1" />

# Abstract sections \[corpus\]

```
documents.sections:abstract
```

or

```
documents.sections[@name == "abstract"]
```


<a name="Abstract-sections-in-the-test-set-\corpus\-1" />

# Abstract sections in the test set \[corpus\]


```
documents[@set == "test"].sections:abstract
```

This assumes that documents have a feature with key *set* that denote
the set to which it pertains.


<a name="Documents-whose-PMID-is-in-a-file-\corpus\-1" />

# Documents whose PMID is in a file \[corpus\]


```
documents[@pmid in "good_pmids.txt"]
```

This assumes that the PMID of the document is in a feature with key
*pmid*, and there is a file in the current directory named
*good\_pmids.txt* containing all PMIDs of interest (one per line).


<a name="All-genes-and-taxa-\section\-1" />

# All genes and taxa \[section\]


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


<a name="All-words-included-in-a-sentence-\annotation\-1" />

# All words included in a sentence \[annotation\]


```
inside:words
```

This assumes that the context element is an annotation representing a
sentence. It also assumes that all words are in a layer named *words*.


<a name="All-verbs-\section\-1" />

# All verbs \[section\]


```
layer:words[@pos ^= "V"]
```

or


```
layer:words[@pos =~ "^V"]
```

Both assume all word annotations are in a layer named *words* and have a
feature with key *pos* whose value denote its POS.


<a name="All-syntactic-dependencies-\section\-1" />

# All syntactic dependencies \[section\]


```
relations:dependencies.tuples
```

This assumes syntactic dependencies are in a relation named
*dependencies*.


<a name="Words-that-are-subject-\section\-1" />

# Words that are subject \[section\]


```
relations:dependencies.tuples[@label == "SUBJ:V-N"].args:dependent
```

If you insist to check they are subjet to verbs:


```
relations:dependencies.tuples[@label == "SUBJ:V-N" and args:head.@pos ^= "V"].args:dependent
```


<a name="Subject-of-a-verb-\annotation\-1" />

# Subject of a verb \[annotation\]


```
tuple:dependencies:head[@label == "SUBJ:V-N"].args:dependent
```
