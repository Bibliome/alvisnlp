# org.bibliome.alvisnlp.modules.tees.TEESClassify

## Synopsis

 Classify an alvis relation with the TEES Classifier

## Description

*org.bibliome.alvisnlp.modules.tees.TEESClassify* executes the TEES classify from [Corpus](#Corpus) and record the results in [Relation](#Relation). The param [relationName](#relationName) sets the name of the binary rlation to predict. [relationRole1](#relationRole1) and and [relationRole](#relationRole) set the two roles of the relation.*org.bibliome.alvisnlp.modules.tees.TEESClassify*

## Parameters

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

 Path to the tees Classify executable file.

<a name="teesModel">

### teesModel

Optional

Type: [InputFile](../converter/org.bibliome.util.files.InputFile)

 Path to the trained model to use.

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

<a name="dependencyLabelFeatureName">

### dependencyLabelFeatureName

Default value: `label`

Type: [String](../converter/java.lang.String)

UNDOCUMENTED

<a name="dependencyRelationName">

### dependencyRelationName

Default value: `dependencies`

Type: [String](../converter/java.lang.String)

UNDOCUMENTED

<a name="dependentRole">

### dependentRole

Default value: `dependent`

Type: [String](../converter/java.lang.String)

UNDOCUMENTED

<a name="documentFilter">

### documentFilter

Default value: `true`

Type: [Expression](../converter/alvisnlp.corpus.expressions.Expression)

UNDOCUMENTED

<a name="headRole">

### headRole

Default value: `head`

Type: [String](../converter/java.lang.String)

UNDOCUMENTED

<a name="namedEntityTypeFeature">

### namedEntityTypeFeature

Default value: `neType`

Type: [String](../converter/java.lang.String)

 Name of the feature to access the type of the named entities 

<a name="omitSteps">

### omitSteps

Default value: `SPLIT-SENTENCES,NER`

Type: [String](../converter/java.lang.String)

Set the preprocessing steps to omit in the form of PREPROCESS=[SPLIT-SENTENCES][,NER][,PARSE][,FIND-HEADS]

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

<a name="sentenceRole">

### sentenceRole

Default value: `sentence`

Type: [String](../converter/java.lang.String)

UNDOCUMENTED

<a name="tokenLayerName">

### tokenLayerName

Default value: `words`

Type: [String](../converter/java.lang.String)

UNDOCUMENTED

