<h1 class="module">TEESClassify</h1>

## Synopsis

Classify binary relations with the TEES Classifier based on trained model.

## Description



## Snippet



<button class="copy-code-button" title="Copy to clipboard" onclick="copy_code(this)">📋</button>
```xml
<teesclassify class="TEESClassify>
    <namedEntityLayer></namedEntityLayer>
    <namedEntityLayerName></namedEntityLayerName>
    <python2Executable></python2Executable>
    <schema></schema>
    <teesHome></teesHome>
    <teesModel></teesModel>
</teesclassify>
```

## Mandatory parameters

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

<h3 id="teesModel" class="param">teesModel</h3>

<div class="param-level param-level-mandatory">Mandatory
</div>
<div class="param-type">Type: <a href="../converter/fr.inra.maiage.bibliome.util.files.InputFile" class="converter">InputFile</a>
</div>
Path to the trained model to use. Pre-trained models for BB16 and Seedev16 are available in [/alvisnlp-bibliome/src/main/resources/fr/inra/maiage/bibliome/alvisnlp/bibliomefactory/resources/tees-models]() 

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

<h3 id="dependencyLabelFeature" class="param">dependencyLabelFeature</h3>

<div class="param-level param-level-default-value">Default value: `label`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Feature where to store the dependency label.

<h3 id="dependencyRelation" class="param">dependencyRelation</h3>

<div class="param-level param-level-default-value">Default value: `dependencies`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
UNDOCUMENTED

<h3 id="dependentRole" class="param">dependentRole</h3>

<div class="param-level param-level-default-value">Default value: `dependent`
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

<h3 id="headRole" class="param">headRole</h3>

<div class="param-level param-level-default-value">Default value: `head`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
UNDOCUMENTED

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

<h3 id="sentenceRole" class="param">sentenceRole</h3>

<div class="param-level param-level-default-value">Default value: `sentence`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
UNDOCUMENTED

<h3 id="tokenLayer" class="param">tokenLayer</h3>

<div class="param-level param-level-default-value">Default value: `words`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Name of the layer that contains tokens.

## Deprecated parameters

<h3 id="dependencyLabelFeatureName" class="param">dependencyLabelFeatureName</h3>

<div class="param-level param-level-deprecated">Deprecated
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Deprecated alias for <a href="#dependencyLabelFeature" class="param">dependencyLabelFeature</a> .

<h3 id="dependencyRelationName" class="param">dependencyRelationName</h3>

<div class="param-level param-level-deprecated">Deprecated
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Deprecated alias for <a href="#dependencyRelation" class="param">dependencyRelation</a> .

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

