<h1 class="module">FasttextClassifierLabel</h1>

## Synopsis

 *FasttextClassifierLabel* classifies documents with a FastText classifier trained with <a href="../module/FasttextClassifierTrain" class="module">FasttextClassifierTrain</a> .

**This module is experimental.**

## Description

 *FasttextClassifierLabel* evaluates <a href="#documents" class="param">documents</a> as a list of elements and classifies each item with the classifier specified by <a href="#modelFile" class="param">modelFile</a> . Documents are discriminated with <a href="#attributes" class="param">attributes</a> which must be the same as used for training. The predicted category is stored in <a href="#classFeature" class="param">classFeature</a> .

## Snippet



<button class="copy-code-button" title="Copy to clipboard" onclick="copy_code(this)">📋</button>
```xml
<fasttextclassifierlabel class="FasttextClassifierLabel">
    <attributes></attributes>
    <classFeature></classFeature>
    <documents></documents>
    <fasttextExecutable></fasttextExecutable>
    <modelFile></modelFile>
</fasttextclassifierlabel>
```

## Mandatory parameters

<h3 id="attributes" class="param">attributes</h3>

<div class="param-level param-level-mandatory">Mandatory
</div>
<div class="param-type">Type: <a href="../converter/fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.fasttext.FasttextAttribute%5B%5D" class="converter">FasttextAttribute[]</a>
</div>
Attributes of each document. The set of attributes must be identical in training with <a href="../module/FasttextClassifierTrain" class="module">FasttextClassifierTrain</a> and in labeling with <a href="../module/FasttextClassifierLabel" class="module">FasttextClassifierLabel</a> .

<h3 id="classFeature" class="param">classFeature</h3>

<div class="param-level param-level-mandatory">Mandatory
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Feature where to store the predicted category.

<h3 id="documents" class="param">documents</h3>

<div class="param-level param-level-mandatory">Mandatory
</div>
<div class="param-type">Type: <a href="../converter/fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.Expression" class="converter">Expression</a>
</div>
Documents to classify. This expression is evaluated as a list of elements from the corpus.

<h3 id="fasttextExecutable" class="param">fasttextExecutable</h3>

<div class="param-level param-level-mandatory">Mandatory
</div>
<div class="param-type">Type: <a href="../converter/fr.inra.maiage.bibliome.util.files.ExecutableFile" class="converter">ExecutableFile</a>
</div>
Path to the FastText executable (see the [GitHub](https://github.com/facebookresearch/fastText) page for installation instructions).

<h3 id="modelFile" class="param">modelFile</h3>

<div class="param-level param-level-mandatory">Mandatory
</div>
<div class="param-type">Type: <a href="../converter/fr.inra.maiage.bibliome.util.files.InputFile" class="converter">InputFile</a>
</div>
Model trained with <a href="../module/FasttextClassifierTrain" class="module">FasttextClassifierTrain</a> .

## Optional parameters

<h3 id="probabilityFeature" class="param">probabilityFeature</h3>

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Feature where to store the probability of the predicted category.

## Deprecated parameters

