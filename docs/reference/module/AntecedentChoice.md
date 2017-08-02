<h1 class="module">AntecedentChoice</h1>

## Synopsis

Biotopes-specific module: chooses an antecedent.

## Description

This module is project-specific and should be short-lived. It assumes a relation named *coreferences* containing tuples with at least one argument with role *Anaphora* and another with role *AntePreviousLowerTaxon*, a layer named *biAnaphora*.

## Parameters

<a name="documentFilter">

### documentFilter

<div class="param-level param-level-default-value">Default value: `true`
</div>
<div class="param-type">Type: <a href="../converter/alvisnlp.corpus.expressions.Expression" class="converter">Expression</a>
</div>
Only process documents that satisfy this filter.

<a name="sectionFilter">

### sectionFilter

<div class="param-level param-level-default-value">Default value: `true`
</div>
<div class="param-type">Type: <a href="../converter/alvisnlp.corpus.expressions.Expression" class="converter">Expression</a>
</div>
Process only sections that satisfy this filter.

