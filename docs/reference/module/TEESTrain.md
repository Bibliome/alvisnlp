<h1 class="module">TEESTrain</h1>

## Synopsis

Train a model that can be used to predict binary relations using TEES

## Description



## Snippet



<button class="copy-code-button" title="Copy to clipboard" onclick="copy_code(this)">📋</button>
```xml
<teestrain class="TEESTrain>
    <modelTargetDir></modelTargetDir>
    <namedEntityLayer></namedEntityLayer>
    <namedEntityLayerName></namedEntityLayerName>
    <python2Executable></python2Executable>
    <schema></schema>
    <teesHome></teesHome>
</teestrain>
```

## Mandatory parameters

<h3 id="modelTargetDir" class="param">modelTargetDir</h3>

<div class="param-level param-level-mandatory">Mandatory
</div>
<div class="param-type">Type: <a href="../converter/fr.inra.maiage.bibliome.util.files.OutputFile" class="converter">OutputFile</a>
</div>
Path to the directory where put the trained model

<h3 id="namedEntityLayer" class="param">namedEntityLayer</h3>

<div class="param-level param-level-mandatory">Mandatory
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Name of the layer containing the named entities.

<h3 id="python2Executable" class="param">python2Executable</h3>

<div class="param-level param-level-mandatory">Mandatory
</div>
<div class="param-type">Type: <a href="../converter/fr.inra.maiage.bibliome.util.files.ExecutableFile" class="converter">ExecutableFile</a>
</div>
Path to the Python 2 executable, TEES will fail if run through Python 3.

<h3 id="schema" class="param">schema</h3>

<div class="param-level param-level-mandatory">Mandatory
</div>
<div class="param-type">Type: <a href="../converter/fr.inra.maiage.bibliome.alvisnlp.core.module.types.MultiMapping" class="converter">MultiMapping</a>
</div>
Give the schema of the relations to train i.e.

```xml

      	  <schema>
	    <Lives_In>Bacteria,Location</Lives_In>
      	  </schema>
	
```



<h3 id="teesHome" class="param">teesHome</h3>

<div class="param-level param-level-mandatory">Mandatory
</div>
<div class="param-type">Type: <a href="../converter/fr.inra.maiage.bibliome.util.files.InputDirectory" class="converter">InputDirectory</a>
</div>
Path to the TEES home directory.

## Optional parameters

<h3 id="constantRelationFeatures" class="param">constantRelationFeatures</h3>

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/fr.inra.maiage.bibliome.alvisnlp.core.module.types.Mapping" class="converter">Mapping</a>
</div>
Constant features to add to each relation created by this module.

<h3 id="constantTupleFeatures" class="param">constantTupleFeatures</h3>

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/fr.inra.maiage.bibliome.alvisnlp.core.module.types.Mapping" class="converter">Mapping</a>
</div>
Constant features to add to each tuple created by this module.

<h3 id="corpusSetFeature" class="param">corpusSetFeature</h3>

<div class="param-level param-level-default-value">Default value: `set`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
UNDOCUMENTED

<h3 id="devSetValue" class="param">devSetValue</h3>

<div class="param-level param-level-default-value">Default value: `dev`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Feature key of the dev set corpus.

<h3 id="documentFilter" class="param">documentFilter</h3>

<div class="param-level param-level-default-value">Default value: `true`
</div>
<div class="param-type">Type: <a href="../converter/fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.Expression" class="converter">Expression</a>
</div>
Only process document that satisfy this expression.

<h3 id="modelName" class="param">modelName</h3>

<div class="param-level param-level-default-value">Default value: `test-model`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
give a name to the trained model

<h3 id="namedEntityTypeFeature" class="param">namedEntityTypeFeature</h3>

<div class="param-level param-level-default-value">Default value: `ne-type`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Name of the feature to access the type of the named entities.

<h3 id="omitSteps" class="param">omitSteps</h3>

<div class="param-level param-level-default-value">Default value: `GENIA_SPLITTER,BANNER`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Set the preprocessing steps to ignore in the form of [GENIA_SPLITTER][,BANNER][,BLLIP_BIO][,STANFORD_CONVERT][,SPLIT_NAMES][,FIND_HEADS]

<h3 id="posFeature" class="param">posFeature</h3>

<div class="param-level param-level-default-value">Default value: `pos`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Name of the feature containing the POS-tag.

<h3 id="sectionFilter" class="param">sectionFilter</h3>

<div class="param-level param-level-default-value">Default value: `true and layer:words and layer:sentences`
</div>
<div class="param-type">Type: <a href="../converter/fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.Expression" class="converter">Expression</a>
</div>
Process only sections that satisfy this expression.

<h3 id="sentenceLayer" class="param">sentenceLayer</h3>

<div class="param-level param-level-default-value">Default value: `sentences`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Name of the layer that contains sentence annotations.

<h3 id="testSetValue" class="param">testSetValue</h3>

<div class="param-level param-level-default-value">Default value: `test`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Feature key of the test set corpus.

<h3 id="tokenLayer" class="param">tokenLayer</h3>

<div class="param-level param-level-default-value">Default value: `words`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Name of the layer that contains tokens.

<h3 id="trainSetValue" class="param">trainSetValue</h3>

<div class="param-level param-level-default-value">Default value: `train`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Feature key of the train set corpus.

## Deprecated parameters

<h3 id="namedEntityLayerName" class="param">namedEntityLayerName</h3>

<div class="param-level param-level-deprecated">Deprecated
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Deprecated alias for <a href="#namedEntityLayer" class="param">namedEntityLayer</a> .

<h3 id="sentenceLayerName" class="param">sentenceLayerName</h3>

<div class="param-level param-level-deprecated">Deprecated
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Deprecated alias for <a href="#sentenceLayer" class="param">sentenceLayer</a> .

<h3 id="tokenLayerName" class="param">tokenLayerName</h3>

<div class="param-level param-level-deprecated">Deprecated
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Deprecated alias for <a href="#tokenLayer" class="param">tokenLayer</a> .

