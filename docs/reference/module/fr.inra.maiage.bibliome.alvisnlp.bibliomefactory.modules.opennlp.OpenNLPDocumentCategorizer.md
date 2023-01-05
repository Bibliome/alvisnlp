<h1 class="module">OpenNLPDocumentCategorizer</h1>

## Synopsis

Categorizes documents with a model trained with <a href="../module/OpenNLPDocumentCategorizerTrain" class="module">OpenNLPDocumentCategorizerTrain</a> .

**This module is experimental.**

## Description

 *OpenNLPDocumentCategorizer* uses a model trained with <a href="../module/OpenNLPDocumentCategorizerTrain" class="module">OpenNLPDocumentCategorizerTrain</a> to categorize unlabeled documents. The documents are specified by <a href="#documents" class="param">documents</a> . The classifier algorithm uses the document content specified by <a href="#tokens" class="param">tokens</a> and <a href="#form" class="param">form</a> .

## Snippet



<button class="copy-code-button" title="Copy to clipboard" onclick="copy_code(this)">📋</button>
```xml
<opennlpdocumentcategorizer class="OpenNLPDocumentCategorizer">
    <categoryFeature></categoryFeature>
    <model></model>
</opennlpdocumentcategorizer>
```

## Mandatory parameters

<h3 id="categoryFeature" class="param">categoryFeature</h3>

<div class="param-level param-level-mandatory">Mandatory
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Feature where to store the predicted category.

<h3 id="model" class="param">model</h3>

<div class="param-level param-level-mandatory">Mandatory
</div>
<div class="param-type">Type: <a href="../converter/fr.inra.maiage.bibliome.util.streams.SourceStream" class="converter">SourceStream</a>
</div>
Model file generated with <a href="../module/OpenNLPDocumentCategorizerTrain" class="module">OpenNLPDocumentCategorizerTrain</a> .

## Optional parameters

<h3 id="scoreFeature" class="param">scoreFeature</h3>

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Feature where to store the score of the predicted category.

<h3 id="scoresFeaturePrefix" class="param">scoresFeaturePrefix</h3>

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Prefix of feature names where to store the score of each category.

<h3 id="documents" class="param">documents</h3>

<div class="param-level param-level-default-value">Default value: `documents`
</div>
<div class="param-type">Type: <a href="../converter/fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.Expression" class="converter">Expression</a>
</div>
Elements to classify. This expression is evaluated from the corpus.

<h3 id="form" class="param">form</h3>

<div class="param-level param-level-default-value">Default value: `@form`
</div>
<div class="param-type">Type: <a href="../converter/fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.Expression" class="converter">Expression</a>
</div>
Form of the token. This expression is evaluated as a string from the token.

<h3 id="tokens" class="param">tokens</h3>

<div class="param-level param-level-default-value">Default value: `sections.layer:words`
</div>
<div class="param-type">Type: <a href="../converter/fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.Expression" class="converter">Expression</a>
</div>
Tokens of the elements to classify. This expression is evaluated as a list of elements from the element to classify.

## Deprecated parameters

