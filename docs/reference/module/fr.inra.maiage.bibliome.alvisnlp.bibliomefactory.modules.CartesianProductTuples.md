<h1 class="module">CartesianProductTuples</h1>

## Synopsis

Creates tuples for each element of a Cartesian product.

## Description

 *CartesianProductTuples* evaluates <a href="#anchor" class="param">anchor</a> as an element list with the corpus as the context element. Each item is hereby called the *anchor* . *CartesianProductTuples* evaluates all values of <a href="#arguments" class="param">arguments</a> as lists of elements with the anchor as the context element. Then it creates a tuple for each item of the cartesian product of the result of the evaluation of <a href="#arguments" class="param">arguments</a> . The tuple will have an argument for each item with the role specified by the keys of <a href="#arguments" class="param">arguments</a> .

## Snippet



<button class="copy-code-button" title="Copy to clipboard" onclick="copy_code(this)">📋</button>
```xml
<cartesianproducttuples class="CartesianProductTuples>
    <anchor></anchor>
    <arguments></arguments>
    <relationName></relationName>
</cartesianproducttuples>
```

## Mandatory parameters

<h3 id="anchor" class="param">anchor</h3>

<div class="param-level param-level-mandatory">Mandatory
</div>
<div class="param-type">Type: <a href="../converter/fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.Expression" class="converter">Expression</a>
</div>
Elements from which arguments are evaluated.

<h3 id="arguments" class="param">arguments</h3>

<div class="param-level param-level-mandatory">Mandatory
</div>
<div class="param-type">Type: <a href="../converter/fr.inra.maiage.bibliome.alvisnlp.core.module.types.ExpressionMapping" class="converter">ExpressionMapping</a>
</div>
Tuple role/argument pairs.

<h3 id="relationName" class="param">relationName</h3>

<div class="param-level param-level-mandatory">Mandatory
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Name of the relation to which tuples are added.

## Optional parameters

<h3 id="constantRelationFeatures" class="param">constantRelationFeatures</h3>

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/fr.inra.maiage.bibliome.alvisnlp.core.module.types.Mapping" class="converter">Mapping</a>
</div>
Constant features to add to each relation created by this module.

<h3 id="constantTupleFeatures" class="param">constantTupleFeatures</h3>

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/fr.inra.maiage.bibliome.alvisnlp.core.module.types.Mapping" class="converter">Mapping</a>
</div>
Constant features to add to each tuple created by this module.

<h3 id="documentFilter" class="param">documentFilter</h3>

<div class="param-level param-level-default-value">Default value: `true`
</div>
<div class="param-type">Type: <a href="../converter/fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.Expression" class="converter">Expression</a>
</div>
Only process document that satisfy this expression.

<h3 id="sectionFilter" class="param">sectionFilter</h3>

<div class="param-level param-level-default-value">Default value: `true`
</div>
<div class="param-type">Type: <a href="../converter/fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.Expression" class="converter">Expression</a>
</div>
Process only sections that satisfy this expression.

