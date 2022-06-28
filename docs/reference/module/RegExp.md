<h1 class="module">RegExp</h1>

## Synopsis

Matches a regular expression on sections contents and create an annotation for each match.

## Description

*RegExp* searches for <a href="#pattern" class="param">pattern</a> in the contents of sections, then creates an annotation for each match. The created annotations will span on the entire match. The created annotations will be added in the layer named <a href="#targetLayerName" class="param">targetLayerName</a> of the corresponding section. If <a href="#pattern" class="param">pattern</a> contains groups, then the pattern elements inside groups will be matched but the grouping will not be taken into account in the creation of the annotation.

The created annotations will automatically have all features defined in <a href="#constantAnnotationFeatures" class="param">constantAnnotationFeatures</a>.

## Mandatory parameters

<h3 name="pattern" class="param">pattern</h3>

<div class="param-level param-level-mandatory">Mandatory
</div>
<div class="param-type">Type: <a href="../converter/java.util.regex.Pattern" class="converter">Pattern</a>
</div>
Regular expression to match.

<h3 name="targetLayerName" class="param">targetLayerName</h3>

<div class="param-level param-level-mandatory">Mandatory
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Name of the layer where to store matches.

## Optional parameters

<h3 name="constantAnnotationFeatures" class="param">constantAnnotationFeatures</h3>

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/fr.inra.maiage.bibliome.alvisnlp.core.module.types.Mapping" class="converter">Mapping</a>
</div>
Constant features to add to each annotation created by this module.

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

