<h1 class="module">AnchorTuples</h1>

## Synopsis

Creates tuples with a common argument.

## Description

*AnchorTuples* evaluates <a href="#anchor" class="param">anchor</a> as an element list with the corpus as the context element (see <a href="../converter/alvisnlp.corpus.Expression" class="converter">alvisnlp.corpus.Expression</a>>). For each anchor, *AnchorTuples* creates a tuple in the relation named after <a href="#relationName" class="param">relationName</a>, with the anchor as an argument with role <a href="#anchorRole" class="param">anchorRole</a> and the first annotation in the result of each value of <a href="#arguments" class="param">arguments</a> with the role of the corresponding key. <a href="#arguments" class="param">arguments</a> values are evaluated as element lists with the anchor as the context element.

## Parameters

<h3 name="anchor" class="param">anchor</h3>

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.Expression" class="converter">Expression</a>
</div>
First argument of each created tuple.

<h3 name="anchorRole" class="param">anchorRole</h3>

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Name of the role for the anchor in the created tuple.

<h3 name="arguments" class="param">arguments</h3>

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/fr.inra.maiage.bibliome.alvisnlp.core.module.types.ExpressionMapping" class="converter">ExpressionMapping</a>
</div>
Role/expression pairs of additional arguments for the created tuples. Expressions are evaluated as element lists with the anchor as the context element.

<h3 name="relationName" class="param">relationName</h3>

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Name of the relation in which tuples must be created.

<h3 name="constantRelationFeatures" class="param">constantRelationFeatures</h3>

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/fr.inra.maiage.bibliome.alvisnlp.core.module.types.Mapping" class="converter">Mapping</a>
</div>
Constant features to add to each relation created by this module

<h3 name="constantTupleFeatures" class="param">constantTupleFeatures</h3>

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/fr.inra.maiage.bibliome.alvisnlp.core.module.types.Mapping" class="converter">Mapping</a>
</div>
Constant features to add to each tuple created by this module

<h3 name="documentFilter" class="param">documentFilter</h3>

<div class="param-level param-level-default-value">Default value: `true`
</div>
<div class="param-type">Type: <a href="../converter/fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.Expression" class="converter">Expression</a>
</div>
Only process document that satisfy this expression.

<h3 name="sectionFilter" class="param">sectionFilter</h3>

<div class="param-level param-level-default-value">Default value: `true`
</div>
<div class="param-type">Type: <a href="../converter/fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.Expression" class="converter">Expression</a>
</div>
Process only sections that satisfy this expression.

