<h1 class="module">AnchorTuples</h1>

## Synopsis

Creates tuples with a common argument.

## Description

*AnchorTuples* evaluates <a href="#anchor" class="param">anchor</a> as an element list with the corpus as the context element (see <a href="../converter/alvisnlp.corpus.Expression" class="converter">alvisnlp.corpus.Expression</a>>). For each anchor, *AnchorTuples* creates a tuple in the relation named after <a href="#relationName" class="param">relationName</a>, with the anchor as an argument with role <a href="#anchorRole" class="param">anchorRole</a> and the first annotation in the result of each value of <a href="#arguments" class="param">arguments</a> with the role of the corresponding key. <a href="#arguments" class="param">arguments</a> values are evaluated as element lists with the anchor as the context element.

## Parameters

<a name="anchor">

### anchor

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/alvisnlp.corpus.expressions.Expression" class="converter">Expression</a>
</div>
First argument of each created tuple.

<a name="anchorRole">

### anchorRole

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Name of the role for the anchor in the created tuple.

<a name="arguments">

### arguments

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/alvisnlp.module.types.ExpressionMapping" class="converter">ExpressionMapping</a>
</div>
Role/expression pairs of additional arguments for the created tuples. Expressions are evaluated as element lists with the anchor as the context element.

<a name="relationName">

### relationName

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Name of the relation in which tuples must be created.

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

