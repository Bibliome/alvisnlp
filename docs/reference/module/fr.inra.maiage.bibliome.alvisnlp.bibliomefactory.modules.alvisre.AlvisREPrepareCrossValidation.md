<h1 class="module">AlvisREPrepareCrossValidation</h1>

## Synopsis

synopsis

**This module is experimental.**

## Description

synopsis

## Mandatory parameters

<h3 name="cParameter" class="param">cParameter</h3>

<div class="param-level param-level-mandatory">Mandatory
</div>
<div class="param-type">Type: <a href="../converter/java.lang.Double" class="converter">Double</a>
</div>


<h3 name="outDir" class="param">outDir</h3>

<div class="param-level param-level-mandatory">Mandatory
</div>
<div class="param-type">Type: <a href="../converter/fr.inra.maiage.bibliome.util.files.OutputDirectory" class="converter">OutputDirectory</a>
</div>


<h3 name="relations" class="param">relations</h3>

<div class="param-level param-level-mandatory">Mandatory
</div>
<div class="param-type">Type: <a href="../converter/fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.alvisre.AlvisRERelations%5B%5D" class="converter">AlvisRERelations[]</a>
</div>


<h3 name="schema" class="param">schema</h3>

<div class="param-level param-level-mandatory">Mandatory
</div>
<div class="param-type">Type: <a href="../converter/org.w3c.dom.DocumentFragment" class="converter">DocumentFragment</a>
</div>


<h3 name="similarityFunction" class="param">similarityFunction</h3>

<div class="param-level param-level-mandatory">Mandatory
</div>
<div class="param-type">Type: <a href="../converter/org.w3c.dom.DocumentFragment" class="converter">DocumentFragment</a>
</div>


<h3 name="terms" class="param">terms</h3>

<div class="param-level param-level-mandatory">Mandatory
</div>
<div class="param-type">Type: <a href="../converter/fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.alvisre.AlvisRETokens%5B%5D" class="converter">AlvisRETokens[]</a>
</div>


## Optional parameters

<h3 name="dependencies" class="param">dependencies</h3>

<div class="param-level param-level-default-value">Default value: `fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.alvisre.AlvisRERelations@6f1de4c7`
</div>
<div class="param-type">Type: <a href="../converter/fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.alvisre.AlvisRERelations" class="converter">AlvisRERelations</a>
</div>


<h3 name="documentFilter" class="param">documentFilter</h3>

<div class="param-level param-level-default-value">Default value: `true`
</div>
<div class="param-type">Type: <a href="../converter/fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.Expression" class="converter">Expression</a>
</div>
Only process document that satisfy this filter.

<h3 name="folds" class="param">folds</h3>

<div class="param-level param-level-default-value">Default value: `10`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.Integer" class="converter">Integer</a>
</div>


<h3 name="sectionFilter" class="param">sectionFilter</h3>

<div class="param-level param-level-default-value">Default value: `true`
</div>
<div class="param-type">Type: <a href="../converter/fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.Expression" class="converter">Expression</a>
</div>
Process only sections that satisfy this filter.

<h3 name="sectionSeparator" class="param">sectionSeparator</h3>

<div class="param-level param-level-default-value">Default value: `
`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>


<h3 name="sentences" class="param">sentences</h3>

<div class="param-level param-level-default-value">Default value: `fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.alvisre.AlvisRETokens@459e9125`
</div>
<div class="param-type">Type: <a href="../converter/fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.alvisre.AlvisRETokens" class="converter">AlvisRETokens</a>
</div>


<h3 name="words" class="param">words</h3>

<div class="param-level param-level-default-value">Default value: `fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.alvisre.AlvisRETokens@7b2bbc3`
</div>
<div class="param-type">Type: <a href="../converter/fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.alvisre.AlvisRETokens" class="converter">AlvisRETokens</a>
</div>


