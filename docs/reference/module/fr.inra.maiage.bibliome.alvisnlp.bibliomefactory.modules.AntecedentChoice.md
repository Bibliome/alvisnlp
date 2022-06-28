<h1 class="module">AntecedentChoice</h1>

## Synopsis

Biotopes-specific module: chooses an antecedent.

## Description

This module is project-specific and should be short-lived. It assumes a relation named *coreferences* containing tuples with at least one argument with role *Anaphora* and another with role *AntePreviousLowerTaxon*, a layer named *biAnaphora*.

## Mandatory parameters

## Optional parameters

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

