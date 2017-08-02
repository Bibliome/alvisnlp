# org.bibliome.alvisnlp.modules.CartesianProductTuples

## Synopsis

Creates tuples for each element of a Cartesian product.

## Description

*org.bibliome.alvisnlp.modules.CartesianProductTuples* evaluates [anchor](#anchor) as an element list with the corpus as the context element. Each item is hereby called the *anchor*. *org.bibliome.alvisnlp.modules.CartesianProductTuples* evaluates all values of [arguments](#arguments) as lists of elements with the anchor as the context element. Then it creates a tuple for each item of the cartesian product of the result of the evaluation of [arguments](#arguments). The tuple will have an argument for each item with the role specified by the keys of [arguments](#arguments).

## Parameters

<a name="anchor">

### anchor

Optional

Type: [Expression](../converter/alvisnlp.corpus.expressions.Expression)

Elements from which arguments are evaluated.

<a name="arguments">

### arguments

Optional

Type: [ExpressionMapping](../converter/alvisnlp.module.types.ExpressionMapping)

Tuple role/argument pairs.

<a name="relationName">

### relationName

Optional

Type: [String](../converter/java.lang.String)

Name of the relation to which tuples are added.

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

