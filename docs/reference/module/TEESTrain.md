<h1 class="module">TEESTrain</h1>

## Synopsis

Train a model that can be used to predict binary relations using TEES 

## Description

*TEESTrain* executes the TEES training on <a href="#Corpus" class="param">Corpus</a> and record the results in <a href="#Relation" class="param">Relation</a>. Param <a href="#relationName" class="param">relationName</a> sets the name of the binary relation to predict. <a href="#relationRole1" class="param">relationRole1</a> and <a href="#relationRole" class="param">relationRole</a> set the two roles of the relation. Params <a href="#trainSetFeature" class="param">trainSetFeature</a>, <a href="#devSetFeature" class="param">devSetFeature</a> and <a href="#testSetFeature" class="param">testSetFeature</a> give respectively the features key of the train, dev and test corpus. *TEESTrain*

## Parameters

<h3 name="modelTargetDir" class="param">modelTargetDir</h3>

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/fr.inra.maiage.bibliome.util.files.OutputFile" class="converter">OutputFile</a>
</div>
 Path to the directory where put the trained model

<h3 name="namedEntityLayerName" class="param">namedEntityLayerName</h3>

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Name of the layer containing the named entities.

<h3 name="python2Executable" class="param">python2Executable</h3>

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/fr.inra.maiage.bibliome.util.files.ExecutableFile" class="converter">ExecutableFile</a>
</div>
Path to the Python 2 executable, TEES will fail if run through Python 3.

<h3 name="schema" class="param">schema</h3>

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/fr.inra.maiage.bibliome.alvisnlp.core.module.types.MultiMapping" class="converter">MultiMapping</a>
</div>
 
  	Give the schema of the relations to train i.e.
	```xml

      	  <schema>
	    <Lives_In>Bacteria,Location</Lives_In>
      	  </schema>
	
```



<h3 name="teesHome" class="param">teesHome</h3>

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/fr.inra.maiage.bibliome.util.files.InputDirectory" class="converter">InputDirectory</a>
</div>
Path to the TEES home directory.

<h3 name="constantRelationFeatures" class="param">constantRelationFeatures</h3>

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/fr.inra.maiage.bibliome.alvisnlp.core.module.types.Mapping" class="converter">Mapping</a>
</div>
Constant features to add to each relation created by this module.

<h3 name="constantTupleFeatures" class="param">constantTupleFeatures</h3>

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/fr.inra.maiage.bibliome.alvisnlp.core.module.types.Mapping" class="converter">Mapping</a>
</div>
Constant features to add to each tuple created by this module.

<h3 name="corpusSetFeature" class="param">corpusSetFeature</h3>

<div class="param-level param-level-default-value">Default value: `set`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
UNDOCUMENTED

<h3 name="devSetValue" class="param">devSetValue</h3>

<div class="param-level param-level-default-value">Default value: `dev`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
 Feature key of the dev set corpus. 

<h3 name="documentFilter" class="param">documentFilter</h3>

<div class="param-level param-level-default-value">Default value: `true`
</div>
<div class="param-type">Type: <a href="../converter/fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.Expression" class="converter">Expression</a>
</div>
Only process document that satisfy this expression.

<h3 name="modelName" class="param">modelName</h3>

<div class="param-level param-level-default-value">Default value: `test-model`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
 give a name to the trained model

<h3 name="namedEntityTypeFeature" class="param">namedEntityTypeFeature</h3>

<div class="param-level param-level-default-value">Default value: `ne-type`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Name of the feature to access the type of the named entities.

<h3 name="omitSteps" class="param">omitSteps</h3>

<div class="param-level param-level-default-value">Default value: `GENIA_SPLITTER,BANNER`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Set the preprocessing steps to ignore in the form of [GENIA_SPLITTER][,BANNER][,BLLIP_BIO][,STANFORD_CONVERT][,SPLIT_NAMES][,FIND_HEADS] 

<h3 name="posFeature" class="param">posFeature</h3>

<div class="param-level param-level-default-value">Default value: `pos`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Name of the feature containing the POS-tag.

<h3 name="sectionFilter" class="param">sectionFilter</h3>

<div class="param-level param-level-default-value">Default value: `boolean:and(true, boolean:and(nav:layer:words(), nav:layer:sentences()))`
</div>
<div class="param-type">Type: <a href="../converter/fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.Expression" class="converter">Expression</a>
</div>
Process only sections that satisfy this expression.

<h3 name="sentenceLayerName" class="param">sentenceLayerName</h3>

<div class="param-level param-level-default-value">Default value: `sentences`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Name of the layer that contains sentence annotations.

<h3 name="testSetValue" class="param">testSetValue</h3>

<div class="param-level param-level-default-value">Default value: `test`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
 Feature key of the test set corpus. 

<h3 name="tokenLayerName" class="param">tokenLayerName</h3>

<div class="param-level param-level-default-value">Default value: `words`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Name of the layer that contains tokens.

<h3 name="trainSetValue" class="param">trainSetValue</h3>

<div class="param-level param-level-default-value">Default value: `train`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
 Feature key of the train set corpus.

