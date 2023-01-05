<h1 class="module">DisambiguateAlternatives</h1>

## Synopsis

Disambiguate features that have multiple values.

## Description

 *DisambiguateAlternatives* evaluates <a href="#target" class="param">target</a> as a list of elements with the current document as the context element. Then it tries to keep a single value for the feature <a href="#ambiguousFeature" class="param">ambiguousFeature</a> in each item in the result. To achieve this, it keeps a set of unambiguous values found in the document. Unambiguous values are found in elements for which there is a single value for <a href="#ambiguousFeature" class="param">ambiguousFeature</a> .

## Snippet



<button class="copy-code-button" title="Copy to clipboard" onclick="copy_code(this)">📋</button>
```xml
<disambiguatealternatives class="DisambiguateAlternatives">
    <ambiguousFeature></ambiguousFeature>
    <target></target>
</disambiguatealternatives>
```

## Mandatory parameters

<h3 id="ambiguousFeature" class="param">ambiguousFeature</h3>

<div class="param-level param-level-mandatory">Mandatory
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Feature to disambiguate.

<h3 id="target" class="param">target</h3>

<div class="param-level param-level-mandatory">Mandatory
</div>
<div class="param-type">Type: <a href="../converter/fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.Expression" class="converter">Expression</a>
</div>
Elements to disambiguate.

## Optional parameters

<h3 id="documentFilter" class="param">documentFilter</h3>

<div class="param-level param-level-default-value">Default value: `true`
</div>
<div class="param-type">Type: <a href="../converter/fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.Expression" class="converter">Expression</a>
</div>
Only process document that satisfy this expression.

<h3 id="warnIfAmbiguous" class="param">warnIfAmbiguous</h3>

<div class="param-level param-level-default-value">Default value: `false`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.Boolean" class="converter">Boolean</a>
</div>
Either to issue a warning if an element has still ambiguous values after processing.

## Deprecated parameters

