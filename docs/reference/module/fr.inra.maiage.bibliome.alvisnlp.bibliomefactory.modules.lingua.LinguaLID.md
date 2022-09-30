<h1 class="module">LinguaLID</h1>

## Synopsis

Identifies the language of a content using [Lingua](https://github.com/pemistahl/lingua) .

**This module is experimental.**

## Description

 *LinguaLID* evaluates <a href="#target" class="param">target</a> as a list of elements, then evaluates <a href="#form" class="param">form</a> for each one as a string. The language of evaluated content is predicted using the [Lingua](https://github.com/pemistahl/lingua) library.

The predicted language is stored in the feature specified by <a href="#languageFeature" class="param">languageFeature</a> using ISO 639-1 two-letter code. Optionally the confidence score is stored in <a href="#languageConfidenceFeature" class="param">languageConfidenceFeature</a> .

There may be more than one prediction if <a href="#languageCandidates" class="param">languageCandidates</a> is set to a number above 1. The last language value has the highest confidence. Low-confidence predictions can be excluded by specifying a value to <a href="#confidenceThreshold" class="param">confidenceThreshold</a> .

The set of predicted languages can be restricted with <a href="#includeLanguages" class="param">includeLanguages</a> .

## Snippet



<button class="copy-code-button" title="Copy to clipboard" onclick="copy_code(this)">📋</button>
```xml
<lingualid class="LinguaLID>
</lingualid>
```

## Mandatory parameters

## Optional parameters

<h3 id="includeLanguages" class="param">includeLanguages</h3>

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/com.github.pemistahl.lingua.api.Language%5B%5D" class="converter">Language[]</a>
</div>
Languages to consider in the prediction. Languages can be specified using either ISO 639-1 two-letter codes, 639-3 three-letter codes, or full language name.

<h3 id="languageConfidenceFeature" class="param">languageConfidenceFeature</h3>

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Feature where to keep the predicition confidence score.

<h3 id="confidenceThreshold" class="param">confidenceThreshold</h3>

<div class="param-level param-level-default-value">Default value: `0.0`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.Double" class="converter">Double</a>
</div>
Minimum value of confidence.

<h3 id="form" class="param">form</h3>

<div class="param-level param-level-default-value">Default value: `contents`
</div>
<div class="param-type">Type: <a href="../converter/fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.Expression" class="converter">Expression</a>
</div>
String content of the target (section `contents` by default).

<h3 id="languageCandidates" class="param">languageCandidates</h3>

<div class="param-level param-level-default-value">Default value: `1`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.Integer" class="converter">Integer</a>
</div>
Number of languages to predict.

<h3 id="languageFeature" class="param">languageFeature</h3>

<div class="param-level param-level-default-value">Default value: `language`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Feature where to store the predicted language.

<h3 id="target" class="param">target</h3>

<div class="param-level param-level-default-value">Default value: `documents.sections`
</div>
<div class="param-type">Type: <a href="../converter/fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.Expression" class="converter">Expression</a>
</div>
Elements to predict the language, by default `document.contents` .

