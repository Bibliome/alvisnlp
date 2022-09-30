<h1 class="module">Word2Vec</h1>

## Synopsis

Computes word embeddings using the [CONTES/Gensis](https://github.com/ArnaudFerre/CONTES) implementation.

**This module is experimental.**

## Description

Computes word embeddings using the [CONTES/Gensis](https://github.com/ArnaudFerre/CONTES) implementation.

## Snippet



<button class="copy-code-button" title="Copy to clipboard" onclick="copy_code(this)">📋</button>
```xml
<word2vec class="Word2Vec>
    <contesDir></contesDir>
    <python3Executable></python3Executable>
    <workers></workers>
</word2vec>
```

## Mandatory parameters

<h3 id="contesDir" class="param">contesDir</h3>

<div class="param-level param-level-mandatory">Mandatory
</div>
<div class="param-type">Type: <a href="../converter/fr.inra.maiage.bibliome.util.files.InputDirectory" class="converter">InputDirectory</a>
</div>
Root directory of CONTES.

<h3 id="python3Executable" class="param">python3Executable</h3>

<div class="param-level param-level-mandatory">Mandatory
</div>
<div class="param-type">Type: <a href="../converter/fr.inra.maiage.bibliome.util.files.ExecutableFile" class="converter">ExecutableFile</a>
</div>
Path to the Python 3 executable.

<h3 id="workers" class="param">workers</h3>

<div class="param-level param-level-mandatory">Mandatory
</div>
<div class="param-type">Type: <a href="../converter/java.lang.Integer" class="converter">Integer</a>
</div>
Use this many worker threads to train the model (=faster training with multicore machines).

## Optional parameters

<h3 id="additionalArguments" class="param">additionalArguments</h3>

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String%5B%5D" class="converter">String[]</a>
</div>
UNDOCUMENTED

<h3 id="jsonFile" class="param">jsonFile</h3>

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/fr.inra.maiage.bibliome.util.files.OutputFile" class="converter">OutputFile</a>
</div>
File where to write embeddings as a JSON object.

<h3 id="modelFile" class="param">modelFile</h3>

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/fr.inra.maiage.bibliome.util.files.OutputFile" class="converter">OutputFile</a>
</div>
UNDOCUMENTED

<h3 id="txtFile" class="param">txtFile</h3>

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/fr.inra.maiage.bibliome.util.files.OutputFile" class="converter">OutputFile</a>
</div>
File where to write embeddings as a table.

<h3 id="vectorFeatureName" class="param">vectorFeatureName</h3>

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
UNDOCUMENTED

<h3 id="documentFilter" class="param">documentFilter</h3>

<div class="param-level param-level-default-value">Default value: `true`
</div>
<div class="param-type">Type: <a href="../converter/fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.Expression" class="converter">Expression</a>
</div>
Only process document that satisfy this expression.

<h3 id="formFeatureName" class="param">formFeatureName</h3>

<div class="param-level param-level-default-value">Default value: `form`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
UNDOCUMENTED

<h3 id="minCount" class="param">minCount</h3>

<div class="param-level param-level-default-value">Default value: `0`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.Integer" class="converter">Integer</a>
</div>
UNDOCUMENTED

<h3 id="sectionFilter" class="param">sectionFilter</h3>

<div class="param-level param-level-default-value">Default value: `true and layer:sentences and layer:words`
</div>
<div class="param-type">Type: <a href="../converter/fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.Expression" class="converter">Expression</a>
</div>
Process only sections that satisfy this expression.

<h3 id="sentenceLayerName" class="param">sentenceLayerName</h3>

<div class="param-level param-level-default-value">Default value: `sentences`
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

<h3 id="vectorSize" class="param">vectorSize</h3>

<div class="param-level param-level-default-value">Default value: `200`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.Integer" class="converter">Integer</a>
</div>
The dimensionality of the feature vectors. Often effective between 100 and 300.

<h3 id="windowSize" class="param">windowSize</h3>

<div class="param-level param-level-default-value">Default value: `2`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.Integer" class="converter">Integer</a>
</div>
The maximum distance between the current and predicted word within a sentence.

