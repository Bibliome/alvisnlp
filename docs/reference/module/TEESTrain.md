# org.bibliome.alvisnlp.modules.tees.TEESTrain

## Synopsis

Train a model that predict an Alvis relation with TEES Trainer

## Description

*org.bibliome.alvisnlp.modules.tees.TEESTrain* executes the TEES training on [Corpus](#Corpus) and record the results in [Relation](#Relation). Param [relationName](#relationName) sets the name of the binary relation to predict. [relationRole1](#relationRole1) and [relationRole](#relationRole) set the two roles of the relation. Params [trainSetFeature](#trainSetFeature), [devSetFeature](#devSetFeature) and [testSetFeature](#testSetFeature) give respectively the features key of the train, dev and test corpus. *org.bibliome.alvisnlp.modules.tees.TEESTrain*

## Parameters

<a name="modelTargetDir">

### modelTargetDir

Optional

Type: [OutputFile](../converter/org.bibliome.util.files.OutputFile)

 Path to the directory where put the trained model

<a name="namedEntityLayerName">

### namedEntityLayerName

Optional

Type: [String](../converter/java.lang.String)

 Name of the layer containing the named entities 

<a name="schema">

### schema

Optional

Type: [MultiMapping](../converter/alvisnlp.module.types.MultiMapping)

 
      	Give the schema of the relations to train i.e.
	```xml

      	  <schema>
	    <Lives_In>Bacteria,Location</Lives_In>
      	  </schema>
	```



<a name="teesHome">

### teesHome

Optional

Type: [InputDirectory](../converter/org.bibliome.util.files.InputDirectory)

 Path to tees home directory. 

<a name="constantRelationFeatures">

### constantRelationFeatures

Optional

Type: [Mapping](../converter/alvisnlp.module.types.Mapping)

UNDOCUMENTED

<a name="constantTupleFeatures">

### constantTupleFeatures

Optional

Type: [Mapping](../converter/alvisnlp.module.types.Mapping)

UNDOCUMENTED

<a name="corpusSetFeature">

### corpusSetFeature

Default value: `set`

Type: [String](../converter/java.lang.String)

UNDOCUMENTED

<a name="devSetValue">

### devSetValue

Default value: `dev`

Type: [String](../converter/java.lang.String)

 Feature key of the dev set corpus. 

<a name="documentFilter">

### documentFilter

Default value: `true`

Type: [Expression](../converter/alvisnlp.corpus.expressions.Expression)

UNDOCUMENTED

<a name="modelName">

### modelName

Default value: `test-model`

Type: [String](../converter/java.lang.String)

 give a name to the trained model

<a name="namedEntityTypeFeature">

### namedEntityTypeFeature

Default value: `neType`

Type: [String](../converter/java.lang.String)

 Name of the feature to access the type of the named entities 

<a name="omitSteps">

### omitSteps

Default value: `SPLIT-SENTENCES,NER`

Type: [String](../converter/java.lang.String)

 Set the preprocessing steps to omit in the form of [SPLIT-SENTENCES][,NER][,PARSE][,FIND-HEADS]

<a name="posFeature">

### posFeature

Default value: `pos`

Type: [String](../converter/java.lang.String)

UNDOCUMENTED

<a name="sectionFilter">

### sectionFilter

Default value: `boolean:and(true, boolean:and(nav:layer:words(), nav:layer:sentences()))`

Type: [Expression](../converter/alvisnlp.corpus.expressions.Expression)

UNDOCUMENTED

<a name="sentenceLayerName">

### sentenceLayerName

Default value: `sentences`

Type: [String](../converter/java.lang.String)

UNDOCUMENTED

<a name="testSetValue">

### testSetValue

Default value: `test`

Type: [String](../converter/java.lang.String)

 Feature key of the test set corpus. 

<a name="tokenLayerName">

### tokenLayerName

Default value: `words`

Type: [String](../converter/java.lang.String)

UNDOCUMENTED

<a name="trainSetValue">

### trainSetValue

Default value: `train`

Type: [String](../converter/java.lang.String)

 Feature key of the train set corpus.

