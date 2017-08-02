# org.bibliome.alvisnlp.modules.AnchorTuples

## Synopsis

Creates tuples with a common argument.

## Description

*org.bibliome.alvisnlp.modules.AnchorTuples* evaluates [anchor](#anchor) as an element list with the corpus as the context element (see [alvisnlp.corpus.Expression](../converter/alvisnlp.corpus.Expression)>). For each anchor, *org.bibliome.alvisnlp.modules.AnchorTuples* creates a tuple in the relation named after [relationName](#relationName), with the anchor as an argument with role [anchorRole](#anchorRole) and the first annotation in the result of each value of [arguments](#arguments) with the role of the corresponding key. [arguments](#arguments) values are evaluated as element lists with the anchor as the context element.

## Parameters

<a name="anchor">

### anchor

Optional

Type: [Expression](../converter/alvisnlp.corpus.expressions.Expression)

First argument of each created tuple.

<a name="anchorRole">

### anchorRole

Optional

Type: [String](../converter/java.lang.String)

Name of the role for the anchor in the created tuple.

<a name="arguments">

### arguments

Optional

Type: [ExpressionMapping](../converter/alvisnlp.module.types.ExpressionMapping)

Role/expression pairs of additional arguments for the created tuples. Expressions are evaluated as element lists with the anchor as the context element.

<a name="relationName">

### relationName

Optional

Type: [String](../converter/java.lang.String)

Name of the relation in which tuples must be created.

<a name="constantRelationFeatures">

### constantRelationFeatures

Optional

Type: [Mapping](../converter/alvisnlp.module.types.Mapping)

Constant features to add to each relation created by this module

<a name="constantTupleFeatures">

### constantTupleFeatures

Optional

Type: [Mapping](../converter/alvisnlp.module.types.Mapping)

Constant features to add to each tuple created by this module

<a name="documentFilter">

### documentFilter

Default value: `true`

Type: [Expression](../converter/alvisnlp.corpus.expressions.Expression)

Only process document that satisfy this filter.

<a name="sectionFilter">

### sectionFilter

Default value: `true`

Type: [Expression](../converter/alvisnlp.corpus.expressions.Expression)

Process only sections that satisfy this filter.

