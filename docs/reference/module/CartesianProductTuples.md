<h1 class="module">CartesianProductTuples</h1>

## Synopsis

Creates tuples for each element of a Cartesian product.

## Description

*CartesianProductTuples* evaluates <a href="#anchor" class="param">anchor</a> as an element list with the corpus as the context element. Each item is hereby called the *anchor*. *CartesianProductTuples* evaluates all values of <a href="#arguments" class="param">arguments</a> as lists of elements with the anchor as the context element. Then it creates a tuple for each item of the cartesian product of the result of the evaluation of <a href="#arguments" class="param">arguments</a>. The tuple will have an argument for each item with the role specified by the keys of <a href="#arguments" class="param">arguments</a>.

## Parameters

<a name="anchor">

### anchor

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/alvisnlp.corpus.expressions.Expression" class="converter">Expression</a>
</div>
Elements from which arguments are evaluated.

<a name="arguments">

### arguments

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/alvisnlp.module.types.ExpressionMapping" class="converter">ExpressionMapping</a>
</div>
Tuple role/argument pairs.

<a name="relationName">

### relationName

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Name of the relation to which tuples are added.

<a name="constantRelationFeatures">

### constantRelationFeatures

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/alvisnlp.module.types.Mapping" class="converter">Mapping</a>
</div>
Constant features to add to each relation created by this module

<a name="constantTupleFeatures">

### constantTupleFeatures

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/alvisnlp.module.types.Mapping" class="converter">Mapping</a>
</div>
Constant features to add to each tuple created by this module

<a name="documentFilter">

### documentFilter

<div class="param-level param-level-default-value">Default value: `true`
</div>
<div class="param-type">Type: <a href="../converter/alvisnlp.corpus.expressions.Expression" class="converter">Expression</a>
</div>
Only process document that satisfy this filter.

<a name="sectionFilter">

### sectionFilter

<div class="param-level param-level-default-value">Default value: `true`
</div>
<div class="param-type">Type: <a href="../converter/alvisnlp.corpus.expressions.Expression" class="converter">Expression</a>
</div>
Process only sections that satisfy this filter.

