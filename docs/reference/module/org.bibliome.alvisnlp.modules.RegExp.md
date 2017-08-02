<h1 class="module">RegExp</h1>

## Synopsis

Matches a regular expression on sections contents and create an annotation for each match.

## Description

*RegExp* searches for <a href="#pattern" class="param">pattern</a> in the contents of sections, then creates an annotation for each match. The created annotations will span on the entire match. The created annotations will be added in the layer named <a href="#targetLayerName" class="param">targetLayerName</a> of the corresponding section. If <a href="#pattern" class="param">pattern</a> contains groups, then the pattern elements inside groups will be matched but the grouping will not be taken into account in the creation of the annotation.

The created annotations will automatically have all features defined in <a href="#constantAnnotationFeatures" class="param">constantAnnotationFeatures</a>.

## Parameters

<a name="pattern">

### pattern

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/java.util.regex.Pattern" class="converter">Pattern</a>
</div>
Regular expression to match.

<a name="targetLayerName">

### targetLayerName

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Name of the layer where to store matches.

<a name="constantAnnotationFeatures">

### constantAnnotationFeatures

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/alvisnlp.module.types.Mapping" class="converter">Mapping</a>
</div>
Constant features to add to each annotation created by this module.

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

