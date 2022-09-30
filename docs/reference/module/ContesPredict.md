<h1 class="module">ContesPredict</h1>

## Synopsis

Predict terms concepts using [CONTES](https://github.com/ArnaudFerre/CONTES) .

**This module is experimental.**

## Description

 *ContesPredict* predicts the concept in <a href="#ontology" class="param">ontology</a> associated to each term in <a href="#termLayer" class="param">termLayer</a> .

 *ContesPredict* uses a classifier specified by <a href="#regressionMatrix" class="param">regressionMatrix</a> that must have been produced by <a href="../module/ContesTrain" class="module">ContesTrain</a> .

## Snippet



<button class="copy-code-button" title="Copy to clipboard" onclick="copy_code(this)">📋</button>
```xml
<contespredict class="ContesPredict>
    <contesDir></contesDir>
    <ontology></ontology>
    <python3Executable></python3Executable>
    <termClassifiers></termClassifiers>
</contespredict>
```

## Mandatory parameters

<h3 id="contesDir" class="param">contesDir</h3>

<div class="param-level param-level-mandatory">Mandatory
</div>
<div class="param-type">Type: <a href="../converter/fr.inra.maiage.bibliome.util.files.InputDirectory" class="converter">InputDirectory</a>
</div>
Root directory of CONTES.

<h3 id="ontology" class="param">ontology</h3>

<div class="param-level param-level-mandatory">Mandatory
</div>
<div class="param-type">Type: <a href="../converter/fr.inra.maiage.bibliome.util.files.InputFile" class="converter">InputFile</a>
</div>
Path to the ontology file in OBO or OWL format.

<h3 id="python3Executable" class="param">python3Executable</h3>

<div class="param-level param-level-mandatory">Mandatory
</div>
<div class="param-type">Type: <a href="../converter/fr.inra.maiage.bibliome.util.files.ExecutableFile" class="converter">ExecutableFile</a>
</div>
Path to the Python 3 executable.

<h3 id="termClassifiers" class="param">termClassifiers</h3>

<div class="param-level param-level-mandatory">Mandatory
</div>
<div class="param-type">Type: <a href="../converter/fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.contes.ContesPredictTermClassifier%5B%5D" class="converter">ContesPredictTermClassifier[]</a>
</div>
UNDOCUMENTED

## Optional parameters

<h3 id="additionalArguments" class="param">additionalArguments</h3>

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String%5B%5D" class="converter">String[]</a>
</div>
UNDOCUMENTED

<h3 id="wordEmbeddings" class="param">wordEmbeddings</h3>

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/fr.inra.maiage.bibliome.util.files.InputFile" class="converter">InputFile</a>
</div>
Path to the file containing word embeddings, as produced by <a href="../module/Word2Vec" class="module">Word2Vec</a> .

<h3 id="wordEmbeddingsModel" class="param">wordEmbeddingsModel</h3>

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/fr.inra.maiage.bibliome.util.files.InputFile" class="converter">InputFile</a>
</div>
UNDOCUMENTED

<h3 id="defaultFactor" class="param">defaultFactor</h3>

<div class="param-level param-level-default-value">Default value: `1.0`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.Double" class="converter">Double</a>
</div>
Default value for the decay factor.

<h3 id="formFeatureName" class="param">formFeatureName</h3>

<div class="param-level param-level-default-value">Default value: `form`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
UNDOCUMENTED

<h3 id="tokenLayerName" class="param">tokenLayerName</h3>

<div class="param-level param-level-default-value">Default value: `words`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
UNDOCUMENTED

