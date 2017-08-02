<h1 class="module">TEESTrain</h1>

## Synopsis

Train a model that predict an Alvis relation with TEES Trainer

## Description

*TEESTrain* executes the TEES training on <a href="#Corpus" class="param">Corpus</a> and record the results in <a href="#Relation" class="param">Relation</a>. Param <a href="#relationName" class="param">relationName</a> sets the name of the binary relation to predict. <a href="#relationRole1" class="param">relationRole1</a> and <a href="#relationRole" class="param">relationRole</a> set the two roles of the relation. Params <a href="#trainSetFeature" class="param">trainSetFeature</a>, <a href="#devSetFeature" class="param">devSetFeature</a> and <a href="#testSetFeature" class="param">testSetFeature</a> give respectively the features key of the train, dev and test corpus. *TEESTrain*

## Parameters

<a name="modelTargetDir">

### modelTargetDir

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/org.bibliome.util.files.OutputFile" class="converter">OutputFile</a>
</div>
 Path to the directory where put the trained model

<a name="namedEntityLayerName">

### namedEntityLayerName

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
 Name of the layer containing the named entities 

<a name="schema">

### schema

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/alvisnlp.module.types.MultiMapping" class="converter">MultiMapping</a>
</div>
 
  	Give the schema of the relations to train i.e.
	```xml

      	  <schema>
	    <Lives_In>Bacteria,Location</Lives_In>
      	  </schema>
	
```



<a name="teesHome">

### teesHome

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/org.bibliome.util.files.InputDirectory" class="converter">InputDirectory</a>
</div>
 Path to tees home directory. 

<a name="constantRelationFeatures">

### constantRelationFeatures

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/alvisnlp.module.types.Mapping" class="converter">Mapping</a>
</div>
UNDOCUMENTED

<a name="constantTupleFeatures">

### constantTupleFeatures

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/alvisnlp.module.types.Mapping" class="converter">Mapping</a>
</div>
UNDOCUMENTED

<a name="corpusSetFeature">

### corpusSetFeature

<div class="param-level param-level-default-value">Default value: `set`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
UNDOCUMENTED

<a name="devSetValue">

### devSetValue

<div class="param-level param-level-default-value">Default value: `dev`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
 Feature key of the dev set corpus. 

<a name="documentFilter">

### documentFilter

<div class="param-level param-level-default-value">Default value: `true`
</div>
<div class="param-type">Type: <a href="../converter/alvisnlp.corpus.expressions.Expression" class="converter">Expression</a>
</div>
UNDOCUMENTED

<a name="modelName">

### modelName

<div class="param-level param-level-default-value">Default value: `test-model`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
 give a name to the trained model

<a name="namedEntityTypeFeature">

### namedEntityTypeFeature

<div class="param-level param-level-default-value">Default value: `neType`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
 Name of the feature to access the type of the named entities 

<a name="omitSteps">

### omitSteps

<div class="param-level param-level-default-value">Default value: `SPLIT-SENTENCES,NER`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
 Set the preprocessing steps to omit in the form of [SPLIT-SENTENCES][,NER][,PARSE][,FIND-HEADS]

<a name="posFeature">

### posFeature

<div class="param-level param-level-default-value">Default value: `pos`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
UNDOCUMENTED

<a name="sectionFilter">

### sectionFilter

<div class="param-level param-level-default-value">Default value: `boolean:and(true, boolean:and(nav:layer:words(), nav:layer:sentences()))`
</div>
<div class="param-type">Type: <a href="../converter/alvisnlp.corpus.expressions.Expression" class="converter">Expression</a>
</div>
UNDOCUMENTED

<a name="sentenceLayerName">

### sentenceLayerName

<div class="param-level param-level-default-value">Default value: `sentences`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
UNDOCUMENTED

<a name="testSetValue">

### testSetValue

<div class="param-level param-level-default-value">Default value: `test`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
 Feature key of the test set corpus. 

<a name="tokenLayerName">

### tokenLayerName

<div class="param-level param-level-default-value">Default value: `words`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
UNDOCUMENTED

<a name="trainSetValue">

### trainSetValue

<div class="param-level param-level-default-value">Default value: `train`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
 Feature key of the train set corpus.

