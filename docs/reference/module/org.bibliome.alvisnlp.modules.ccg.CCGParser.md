# org.bibliome.alvisnlp.modules.ccg.CCGParser

## Synopsis

Syntax parsing with [CCG Parser](http://svn.ask.it.usyd.edu.au/trac/candc/wiki).

## Description

*org.bibliome.alvisnlp.modules.ccg.CCGParser* applies the [CCG Parser](http://svn.ask.it.usyd.edu.au/trac/candc/wiki) to sentences specified as annotations from the [sentenceLayerName](#sentenceLayerName) layer. Sentence words are specified by annotations in the [wordLayerName](#wordLayerName) layer. For each sentence, only words entirely included in the sentence will be considered; [WoSMig](../module/WoSMig) and [SeSMig](../module/SeSMig) should create these layers with the appropriate annotations. Additionally CCGParser takes advantage of word POS tag specified in the [posFeatureName](#posFeatureName) feature.

*org.bibliome.alvisnlp.modules.ccg.CCGParser* creates a relation named [relationName](#relationName) in each section and a tuple in this relation for each dependency. This relation is ternary:
      
1. [sentenceRole](#sentenceRole): the first argument is the sentence in which the dependency was found;
2. [headRole](#headRole): the second argument is the head word of the dependency;
3. [dependentRole](#dependentRole): the third argument is the dependent word of the dependency.

*org.bibliome.alvisnlp.modules.ccg.CCGParser* adds to each dependency tuple a feature [linkageNumberFeature](#linkageNumberFeature) with the linkage number to which begongs the tuple, and a feature [dependencyLabelFeature](#dependencyLabelFeature) with the label of the dependency.

## Parameters

<a name="executable">

### executable

Optional

Type: [ExecutableFile](../converter/org.bibliome.util.files.ExecutableFile)

Path to the CCG Parser executable.

<a name="parserModel">

### parserModel

Optional

Type: [InputDirectory](../converter/org.bibliome.util.files.InputDirectory)

Path to the parser model file.

<a name="superModel">

### superModel

Optional

Type: [InputDirectory](../converter/org.bibliome.util.files.InputDirectory)

Path to the CCG supertagger model file.

<a name="constantRelationFeatures">

### constantRelationFeatures

Optional

Type: [Mapping](../converter/alvisnlp.module.types.Mapping)

Constant features to add to each relation created by this module

<a name="constantTupleFeatures">

### constantTupleFeatures

Optional

Type: [Mapping](../converter/alvisnlp.module.types.Mapping)

Constant features to add to each tuple created by this module

<a name="stanfordMarkedUpScript">

### stanfordMarkedUpScript

Optional

Type: [InputFile](../converter/org.bibliome.util.files.InputFile)

Path to the markedup script for Stanford tagset output. See [Biomedical parsing for CCG](http://svn.ask.it.usyd.edu.au/trac/candc/wiki/Biomedical).

<a name="stanfordScript">

### stanfordScript

Optional

Type: [ExecutableFile](../converter/org.bibliome.util.files.ExecutableFile)

Post-processing script for Stanford tagset output. See [Biomedical parsing for CCG](http://svn.ask.it.usyd.edu.au/trac/candc/wiki/Biomedical).

<a name="dependentRole">

### dependentRole

Default value: `dependent`

Type: [String](../converter/java.lang.String)

Name of the role that denote the dependent word in the dependency tuple.

<a name="documentFilter">

### documentFilter

Default value: `true`

Type: [Expression](../converter/alvisnlp.corpus.expressions.Expression)

Only process document that satisfy this filter.

<a name="formFeatureName">

### formFeatureName

Default value: `form`

Type: [String](../converter/java.lang.String)

Name of the feature containing the word surface form.

<a name="headRole">

### headRole

Default value: `head`

Type: [String](../converter/java.lang.String)

Name of the role that denote the head word in the dependency tuple.

<a name="internalEncoding">

### internalEncoding

Default value: `UTF-8`

Type: [String](../converter/java.lang.String)

Character encoding of CCG tools input and output.

<a name="labelFeatureName">

### labelFeatureName

Default value: `label`

Type: [String](../converter/java.lang.String)

Name of the feature containing the dependency label.

<a name="lpTransformation">

### lpTransformation

Default value: `false`

Type: [Boolean](../converter/java.lang.Boolean)

Either to translate into LP tag-set.

<a name="maxRuns">

### maxRuns

Default value: `1`

Type: [Integer](../converter/java.lang.Integer)

Maximal number of CCG runs.

<a name="maxSuperCats">

### maxSuperCats

Default value: `500000`

Type: [Integer](../converter/java.lang.Integer)

Maximum number of supercats before the parse explodes (cited from CCG documentation).

<a name="posFeatureName">

### posFeatureName

Default value: `pos`

Type: [String](../converter/java.lang.String)

Name of the feature containing the word POS tag.

<a name="relationName">

### relationName

Default value: `dependencies`

Type: [String](../converter/java.lang.String)

Name of the relation containing dependencies.

<a name="sectionFilter">

### sectionFilter

Default value: `boolean:and(true, boolean:and(nav:layer:sentences(), nav:layer:words()))`

Type: [Expression](../converter/alvisnlp.corpus.expressions.Expression)

Process only sections that satisfy this filter.

<a name="sentenceFilter">

### sentenceFilter

Default value: `true`

Type: [Expression](../converter/alvisnlp.corpus.expressions.Expression)

Process only sentences that satisfy this filter.

<a name="sentenceLayerName">

### sentenceLayerName

Default value: `sentences`

Type: [String](../converter/java.lang.String)

Name of the layer containing sentence annotations.

<a name="sentenceRole">

### sentenceRole

Default value: `sentence`

Type: [String](../converter/java.lang.String)

Name of the role that denote the sentence to which belongs a dependency tuple.

<a name="wordLayerName">

### wordLayerName

Default value: `words`

Type: [String](../converter/java.lang.String)

Name of the layer containing word annotations.

