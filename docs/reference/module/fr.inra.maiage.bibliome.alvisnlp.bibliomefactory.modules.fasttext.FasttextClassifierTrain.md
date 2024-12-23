<h1 class="module">FasttextClassifierTrain</h1>

## Synopsis

 *FasttextClassifierTrain* trains a document classifier using [FastText](https://fasttext.cc/) .

**This module is experimental.**

## Description

 *FasttextClassifierTrain* evaluates <a href="#documents" class="param">documents</a> as a list of elements and trains FastText to classify them. The category of each document is specified by <a href="#classFeature" class="param">classFeature</a> . The attributes used to discriminate classes are specified by <a href="#attributes" class="param">attributes</a> .

 <a href="#modelFile" class="param">modelFile</a> specifies where to write the result: the classification model receives the `.bin` extension, and the word vectors `.vec` .

## Snippet



<button class="copy-code-button" title="Copy to clipboard" onclick="copy_code(this)">📋</button>
```xml
<fasttextclassifiertrain class="FasttextClassifierTrain">
    <attributes></attributes>
    <classFeature></classFeature>
    <documents></documents>
    <fasttextExecutable></fasttextExecutable>
    <modelFile></modelFile>
</fasttextclassifiertrain>
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
Feature that contains the category of the document.

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
<div class="param-type">Type: <a href="../converter/fr.inra.maiage.bibliome.util.files.OutputFile" class="converter">OutputFile</a>
</div>
Prefix for the classifier model and the word vector files.

## Optional parameters

<h3 id="autotuneMetric" class="param">autotuneMetric</h3>

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
UNDOCUMENTED

<h3 id="buckets" class="param">buckets</h3>

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/java.lang.Integer" class="converter">Integer</a>
</div>
Number of buckets [2000000].

<h3 id="classWeights" class="param">classWeights</h3>

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/fr.inra.maiage.bibliome.alvisnlp.core.module.types.IntegerMapping" class="converter">IntegerMapping</a>
</div>
Weight to apply to documents of each category. The mapping keys are the different categories, the values are weights. The default weight is 1.

<h3 id="commandlineOptions" class="param">commandlineOptions</h3>

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String%5B%5D" class="converter">String[]</a>
</div>
Additional [command lines](https://fasttext.cc/docs/en/options.html) options passed to FastText.

<h3 id="epochs" class="param">epochs</h3>

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/java.lang.Integer" class="converter">Integer</a>
</div>
Number of epochs [5]

<h3 id="learningRate" class="param">learningRate</h3>

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/java.lang.Double" class="converter">Double</a>
</div>
Learning rate [0.1].

<h3 id="lossFunction" class="param">lossFunction</h3>

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.fasttext.FasttextLossFunction" class="converter">FasttextLossFunction</a>
</div>
Loss function [softmax].

<h3 id="maxCharGrams" class="param">maxCharGrams</h3>

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/java.lang.Integer" class="converter">Integer</a>
</div>
Max length of char ngram [0].

<h3 id="minCharGrams" class="param">minCharGrams</h3>

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/java.lang.Integer" class="converter">Integer</a>
</div>
Min length of char ngram [0].

<h3 id="pretrainedVectors" class="param">pretrainedVectors</h3>

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/fr.inra.maiage.bibliome.util.files.InputFile" class="converter">InputFile</a>
</div>
Pre-trained word vectors. Pre-trained vectors are publicly available on the [FastText site](https://fasttext.cc/docs/en/english-vectors.html) .

<h3 id="validationAttributes" class="param">validationAttributes</h3>

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.fasttext.FasttextAttribute%5B%5D" class="converter">FasttextAttribute[]</a>
</div>
Attributes of validation documents. By default the same value as <a href="#attributes" class="param">attributes</a> .

<h3 id="validationDocuments" class="param">validationDocuments</h3>

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.Expression" class="converter">Expression</a>
</div>
Validation documents used for autotuning.

<h3 id="wordGrams" class="param">wordGrams</h3>

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/java.lang.Integer" class="converter">Integer</a>
</div>
Max length of word ngram [1].

<h3 id="wordVectorSize" class="param">wordVectorSize</h3>

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/java.lang.Integer" class="converter">Integer</a>
</div>
Size of word vectors [100].

<h3 id="autotune" class="param">autotune</h3>

<div class="param-level param-level-default-value">Default value: `false`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.Boolean" class="converter">Boolean</a>
</div>
Either to autotune hyperparameters that are not set. If *true* , the <a href="#validationDocuments" class="param">validationDocuments</a> must be set.

<h3 id="autotuneDuration" class="param">autotuneDuration</h3>

<div class="param-level param-level-default-value">Default value: `300`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.Integer" class="converter">Integer</a>
</div>
Duration of autotune in seconds.

<h3 id="learningRateUpdateRate" class="param">learningRateUpdateRate</h3>

<div class="param-level param-level-default-value">Default value: `100`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.Integer" class="converter">Integer</a>
</div>
Change the rate of updates for the learning rate [100].

<h3 id="minCount" class="param">minCount</h3>

<div class="param-level param-level-default-value">Default value: `1`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.Integer" class="converter">Integer</a>
</div>
UNDOCUMENTED

<h3 id="minCountLabel" class="param">minCountLabel</h3>

<div class="param-level param-level-default-value">Default value: `0`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.Integer" class="converter">Integer</a>
</div>
Minimal number of word occurrences [1].

<h3 id="negativeSampling" class="param">negativeSampling</h3>

<div class="param-level param-level-default-value">Default value: `5`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.Integer" class="converter">Integer</a>
</div>
Number of negatives sampled [5].

<h3 id="samplingThreshold" class="param">samplingThreshold</h3>

<div class="param-level param-level-default-value">Default value: `1.0E-4`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.Double" class="converter">Double</a>
</div>
Sampling threshold [0.0001].

<h3 id="threads" class="param">threads</h3>

<div class="param-level param-level-default-value">Default value: `12`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.Integer" class="converter">Integer</a>
</div>
Number of threads.

<h3 id="windowSize" class="param">windowSize</h3>

<div class="param-level param-level-default-value">Default value: `5`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.Integer" class="converter">Integer</a>
</div>
Size of the context window [5].

## Deprecated parameters

