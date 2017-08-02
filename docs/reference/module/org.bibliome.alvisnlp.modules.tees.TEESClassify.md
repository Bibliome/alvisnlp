<h1 class="module">TEESClassify</h1>

## Synopsis

 Classify an alvis relation with the TEES Classifier

## Description

*TEESClassify* executes the TEES classify from <a href="#Corpus" class="param">Corpus</a> and record the results in <a href="#Relation" class="param">Relation</a>. The param <a href="#relationName" class="param">relationName</a> sets the name of the binary rlation to predict. <a href="#relationRole1" class="param">relationRole1</a> and and <a href="#relationRole" class="param">relationRole</a> set the two roles of the relation.*TEESClassify*

## Parameters

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
 Path to the tees Classify executable file.

<a name="teesModel">

### teesModel

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/org.bibliome.util.files.InputFile" class="converter">InputFile</a>
</div>
 Path to the trained model to use.

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

<a name="dependencyLabelFeatureName">

### dependencyLabelFeatureName

<div class="param-level param-level-default-value">Default value: `label`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
UNDOCUMENTED

<a name="dependencyRelationName">

### dependencyRelationName

<div class="param-level param-level-default-value">Default value: `dependencies`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
UNDOCUMENTED

<a name="dependentRole">

### dependentRole

<div class="param-level param-level-default-value">Default value: `dependent`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
UNDOCUMENTED

<a name="documentFilter">

### documentFilter

<div class="param-level param-level-default-value">Default value: `true`
</div>
<div class="param-type">Type: <a href="../converter/alvisnlp.corpus.expressions.Expression" class="converter">Expression</a>
</div>
UNDOCUMENTED

<a name="headRole">

### headRole

<div class="param-level param-level-default-value">Default value: `head`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
UNDOCUMENTED

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
Set the preprocessing steps to omit in the form of PREPROCESS=[SPLIT-SENTENCES][,NER][,PARSE][,FIND-HEADS]

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

<a name="sentenceRole">

### sentenceRole

<div class="param-level param-level-default-value">Default value: `sentence`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
UNDOCUMENTED

<a name="tokenLayerName">

### tokenLayerName

<div class="param-level param-level-default-value">Default value: `words`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
UNDOCUMENTED

