# Table of Contents

> * [Introduction](#Introduction-1)
> * [Pattern language](#Pattern-language-1)
>   * [Single annotation clause](#Single-annotation-clause-1)
>   * [Sequence](#Sequence-1)
>   * [Groups](#Groups-1)
>   * [Union](#Union-1)
>   * [Greedy quantifiers](#Greedy-quantifiers-1)
>   * [Reluctant quantifiers](#Reluctant-quantifiers-1)
>   * [Examples](#Examples-1)
> * [Actions](#Actions-1)
>   * [Add to layer](#Add-to-layer-1)
>   * [Create annotation](#Create-annotation-1)
>   * [Remove annotations](#Remove-annotations-1)
>   * [Set annotation features](#Set-annotation-features-1)
>   * [Create a tuple](#Create-a-tuple-1)
</toc>



<a name="Introduction-1" />

# Introduction

[PatternMatcher](http://bibliome.jouy.inra.fr/demo/alvisnlp/api/modules/PatternMatcher) is an AlvisNLP/ML module for searching sequences of
annotations. It features a language similar to regular expressions to
specify annotation sequence queries. It is also capable of several
actions on matched sequences like adding annotations, removing
annotations, setting features and adding tuples.


<a name="Pattern-language-1" />

# Pattern language

The PatternMatcher module requires the *pattern* parameter that
specifies an annotation sequence query. This query is written in a
regex-like language.


<a name="Single-annotation-clause-1" />

## Single annotation clause

```
[ EXPR ]
```

`EXPR` is an element expression (see \[\[Element Expression\]\]). It
will be evaluated as a boolean with each annotation in the sequence as
the context element. This query matches any annotation for which the
expression evaluated to `true`. Matches for this clause are always a
single annotation.


<a name="Sequence-1" />

## Sequence

```
CLAUSE1 CLAUSE2 ... CLAUSE3
```

`CLAUSE1 CLAUSE2 ... CLAUSE3` are clauses (single annotation or groups).
This searches for subsequences of annotations that match all clauses in
the specified order.


<a name="Groups-1" />

## Groups

```
( CLAUSE )
(NAME: CLAUSE )
```

`CLAUSE` is a clause (single annotation or sequence), and `NAME` is a
name (see \[\[Element Expression\]\]).\
The first form is a non capturing group, usually used to apply a
quantifier to a sequence or an union.\
The second form is a capturing group, the `NAME` can be referenced in
PatternMatcher actions.


<a name="Union-1" />

## Union

```
LEFT | RIGHT
```

`LEFT` and `RIGHT` are clauses (single annotation or group). This
searches for a subsequence that match either `LEFT` or `RIGHT`.


<a name="Greedy-quantifiers-1" />

## Greedy quantifiers

```
CLAUSE ?
CLAUSE *
CLAUSE +
CLAUSE {N}
CLAUSE {N,M}
CLAUSE {N,}
CLAUSE {,M}
```

`CLAUSE` is a clause (single annotation or group), `N` and `M` are
integer constants.

| **Operator** | **Quantifier** | **Equivalence** |
|---------|------------------------------|-------|
| `?`     |  optional                    |   {0,1}
| `*`     |  kleene star                 |   {0,}
| `+`     |  repeat                      |   {1,}
| `N`     |  exactly `N` |  |
| `{N,M}` |  at least `N`, at most `M` |  |
| `{N,}`  |  at least `N`, no upper limit |  |
| `{,M}`  |  at most `M`, possibly 0 |  |


<a name="Reluctant-quantifiers-1" />

## Reluctant quantifiers

```
CLAUSE ??
CLAUSE *?
CLAUSE +?
CLAUSE {N}?
CLAUSE {N,M}?
CLAUSE {N,}?
CLAUSE {,M}?
```

Reluctant quantifiers will not attempt to maximize the length of the
match.


<a name="Examples-1" />

## Examples

```
[ @form == "," ]
[ true ]{1,3}
[ @form == "," ]
```

Two commas separated by one, two or three words.

```
[ true ]
[ @form == "(" ]
[ @pos == before:words{-2}.@pos ]
[ @form == ")" ]
```

Apposition; note that the word between parentheses must have the same
POS tag than the word before the opening parenthesis.


<a name="Actions-1" />

# Actions

The *actions* parameter specifies what should be done with the matches.
PatternMatcher can perform several actions for the same match. Each
action is specified by a specific tag.\
All action tags accept an attribute `group`, if this attribute is
specified, then the action concerns annotations in the specified
capturing group. If this attribute is not specified, then the action
concerns all annotations in the whole match.

In most actions, you can specify a set of features to add to one or
several elements. The feature specification is a mapping of expression
in the form:

```
KEY1 = EXPR1, KEY2 = EXPR2, ..., KEYN = EXPRN
```

`KEY1 KEY2 ... KEYN` are feature keys and `EXPR1 EXPR2 EXPRN` are
expressions. The element context for the evaluation of the expression is
the element to which the features will be added. Additionally
PatternMatcher defines a reference named after for each group that
returns the annotations matched in the corresponding group. The *match*
reference returns all annotations of the whole match.


<a name="Add-to-layer-1" />

## Add to layer

```xml
<addToLayer [group="GROUP"] layer="LAYER"/>
```

This action adds all annotations in the group or match into the layer
named `LAYER`.


<a name="Create-annotation-1" />

## Create annotation

```xml
<createAnnotation [group="GROUP"] layer="LAYER" [features="FEATURES"]/>
```
This action creates an annotation that spans over all the group or match
and adds this annotation in the layer named `LAYER`.\
Additionally it adds to this annotation the features specified by
`FEATURES`.


<a name="Remove-annotations-1" />

## Remove annotations

```xml
<removeAnnotations group="GROUP" layer="LAYER"/>
```


This action removes all annotations in the group or match from the layer
named `LAYER`.


<a name="Set-annotation-features-1" />

## Set annotation features

```xml
<setFeatures group="GROUP" features="FEATURES"/>
```


This action adds features specified by `FEATURES` to all annotations in
the group or match.


<a name="Create-a-tuple-1" />

## Create a tuple

```xml
<createTuple relation="RELATION" arguments="ARGS" features="FEATURES"/>
```


This action creates a tuple to the relation named `RELATION` with
arguments specified by `ARGS` and features specified by `FEATURES`.\
`ARGS` is a mapping of expressions (like `FEATURES`) though the
expressions will be evaluated as a list: the argumebnt will be the first
annotation of the list. PatternMatcher will issue a warning if the first
element of the list is not an annotation or if the list is empty. The
context element is the freshly created tuple and references for each
groups are defined.
