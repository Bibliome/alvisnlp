<h1 class="module">OpenNLPDocumentCategorizerTrain</h1>

## Synopsis

Train a document categorizer using the [OpenNLP](https://opennlp.apache.org/) library.

**This module is experimental.**

## Description

 *OpenNLPDocumentCategorizerTrain* trains a document categorizer using the [OpenNLP](https://opennlp.apache.org/) library. The documents and their class are specified by <a href="#documents" class="param">documents</a> and <a href="#categoryFeature" class="param">categoryFeature</a> . The classifier algorithm uses the document content specified by <a href="#tokens" class="param">tokens</a> and <a href="#form" class="param">form</a> .

By default the features are BOW but can be deactivated with <a href="#bagOfWords" class="param">bagOfWords</a> . Additionally <a href="#nGrams" class="param">nGrams</a> can be set to add n-gram features.

The classifier is stored in <a href="#classifier" class="param">classifier</a> . This file can be used by <a href="../module/OpenNLPDocumentCategorizer" class="module">OpenNLPDocumentCategorizer</a> .

## Snippet



<button class="copy-code-button" title="Copy to clipboard" onclick="copy_code(this)">📋</button>
```xml
<opennlpdocumentcategorizertrain class="OpenNLPDocumentCategorizerTrain>
    <categoryFeature></categoryFeature>
    <language></language>
    <model></model>
</opennlpdocumentcategorizertrain>
```

## Mandatory parameters

<h3 id="categoryFeature" class="param">categoryFeature</h3>

<div class="param-level param-level-mandatory">Mandatory
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Feature where the category is read.

<h3 id="language" class="param">language</h3>

<div class="param-level param-level-mandatory">Mandatory
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Language of the documents (ISO 639-1 two-letter code).

<h3 id="model" class="param">model</h3>

<div class="param-level param-level-mandatory">Mandatory
</div>
<div class="param-type">Type: <a href="../converter/fr.inra.maiage.bibliome.util.streams.TargetStream" class="converter">TargetStream</a>
</div>
File where to store the classifier.

## Optional parameters

<h3 id="classWeights" class="param">classWeights</h3>

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/fr.inra.maiage.bibliome.alvisnlp.core.module.types.IntegerMapping" class="converter">IntegerMapping</a>
</div>
Weight of samples of each class. This parameter is useful to compensate unbalanced training sets. The default weight is 1.

<h3 id="nGrams" class="param">nGrams</h3>

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/java.lang.Integer" class="converter">Integer</a>
</div>
Maximum size of n-gram features (minimum is 2). If not set, then do not use n-gram features.

<h3 id="algorithm" class="param">algorithm</h3>

<div class="param-level param-level-default-value">Default value: `PERCEPTRON`
</div>
<div class="param-type">Type: <a href="../converter/fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.opennlp.OpenNLPAlgorithm" class="converter">OpenNLPAlgorithm</a>
</div>
Categorization algorithm. Must be one of:
*  *naive-bayes* , *nb* 
*  *generalized-iterative-scaling* , *gis* 
*  *perceptron* 
*  *quasi-newton* , *qn* , *l-bfgs* , *lbfgs* , *bfgs* 



<h3 id="bagOfWords" class="param">bagOfWords</h3>

<div class="param-level param-level-default-value">Default value: `true`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.Boolean" class="converter">Boolean</a>
</div>
Either to generate single-word features.

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

<h3 id="iterations" class="param">iterations</h3>

<div class="param-level param-level-default-value">Default value: `100`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.Integer" class="converter">Integer</a>
</div>
Number of learning iterations.

<h3 id="tokens" class="param">tokens</h3>

<div class="param-level param-level-default-value">Default value: `sections.layer:words`
</div>
<div class="param-type">Type: <a href="../converter/fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.Expression" class="converter">Expression</a>
</div>
Tokens of the elements to classify. This expression is evaluated as a list of elements from the element to classify.

