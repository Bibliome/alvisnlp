<h1 class="module">CCGParser</h1>

## Synopsis

Syntax parsing with [CCG Parser](http://svn.ask.it.usyd.edu.au/trac/candc/wiki).

## Description

*CCGParser* applies the [CCG Parser](http://svn.ask.it.usyd.edu.au/trac/candc/wiki) to sentences specified as annotations from the <a href="#sentenceLayerName" class="param">sentenceLayerName</a> layer. Sentence words are specified by annotations in the <a href="#wordLayerName" class="param">wordLayerName</a> layer. For each sentence, only words entirely included in the sentence will be considered; <a href="../module/WoSMig" class="module">WoSMig</a> and <a href="../module/SeSMig" class="module">SeSMig</a> should create these layers with the appropriate annotations. Additionally CCGParser takes advantage of word POS tag specified in the <a href="#posFeatureName" class="param">posFeatureName</a> feature.

*CCGParser* creates a relation named <a href="#relationName" class="param">relationName</a> in each section and a tuple in this relation for each dependency. This relation is ternary:
  
1. <a href="#sentenceRole" class="param">sentenceRole</a>: the first argument is the sentence in which the dependency was found;
2. <a href="#headRole" class="param">headRole</a>: the second argument is the head word of the dependency;
3. <a href="#dependentRole" class="param">dependentRole</a>: the third argument is the dependent word of the dependency.

*CCGParser* adds to each dependency tuple a feature <a href="#linkageNumberFeature" class="param">linkageNumberFeature</a> with the linkage number to which begongs the tuple, and a feature <a href="#dependencyLabelFeature" class="param">dependencyLabelFeature</a> with the label of the dependency.

## Parameters

<a name="executable">

### executable

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/org.bibliome.util.files.ExecutableFile" class="converter">ExecutableFile</a>
</div>
Path to the CCG Parser executable.

<a name="parserModel">

### parserModel

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/org.bibliome.util.files.InputDirectory" class="converter">InputDirectory</a>
</div>
Path to the parser model file.

<a name="superModel">

### superModel

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/org.bibliome.util.files.InputDirectory" class="converter">InputDirectory</a>
</div>
Path to the CCG supertagger model file.

<a name="constantRelationFeatures">

### constantRelationFeatures

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/alvisnlp.module.types.Mapping" class="converter">Mapping</a>
</div>
Constant features to add to each relation created by this module

<a name="constantTupleFeatures">

### constantTupleFeatures

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/alvisnlp.module.types.Mapping" class="converter">Mapping</a>
</div>
Constant features to add to each tuple created by this module

<a name="stanfordMarkedUpScript">

### stanfordMarkedUpScript

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/org.bibliome.util.files.InputFile" class="converter">InputFile</a>
</div>
Path to the markedup script for Stanford tagset output. See [Biomedical parsing for CCG](http://svn.ask.it.usyd.edu.au/trac/candc/wiki/Biomedical).

<a name="stanfordScript">

### stanfordScript

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/org.bibliome.util.files.ExecutableFile" class="converter">ExecutableFile</a>
</div>
Post-processing script for Stanford tagset output. See [Biomedical parsing for CCG](http://svn.ask.it.usyd.edu.au/trac/candc/wiki/Biomedical).

<a name="dependentRole">

### dependentRole

<div class="param-level param-level-default-value">Default value: `dependent`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Name of the role that denote the dependent word in the dependency tuple.

<a name="documentFilter">

### documentFilter

<div class="param-level param-level-default-value">Default value: `true`
</div>
<div class="param-type">Type: <a href="../converter/alvisnlp.corpus.expressions.Expression" class="converter">Expression</a>
</div>
Only process document that satisfy this filter.

<a name="formFeatureName">

### formFeatureName

<div class="param-level param-level-default-value">Default value: `form`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Name of the feature containing the word surface form.

<a name="headRole">

### headRole

<div class="param-level param-level-default-value">Default value: `head`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Name of the role that denote the head word in the dependency tuple.

<a name="internalEncoding">

### internalEncoding

<div class="param-level param-level-default-value">Default value: `UTF-8`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Character encoding of CCG tools input and output.

<a name="labelFeatureName">

### labelFeatureName

<div class="param-level param-level-default-value">Default value: `label`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Name of the feature containing the dependency label.

<a name="lpTransformation">

### lpTransformation

<div class="param-level param-level-default-value">Default value: `false`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.Boolean" class="converter">Boolean</a>
</div>
Either to translate into LP tag-set.

<a name="maxRuns">

### maxRuns

<div class="param-level param-level-default-value">Default value: `1`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.Integer" class="converter">Integer</a>
</div>
Maximal number of CCG runs.

<a name="maxSuperCats">

### maxSuperCats

<div class="param-level param-level-default-value">Default value: `500000`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.Integer" class="converter">Integer</a>
</div>
Maximum number of supercats before the parse explodes (cited from CCG documentation).

<a name="posFeatureName">

### posFeatureName

<div class="param-level param-level-default-value">Default value: `pos`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Name of the feature containing the word POS tag.

<a name="relationName">

### relationName

<div class="param-level param-level-default-value">Default value: `dependencies`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Name of the relation containing dependencies.

<a name="sectionFilter">

### sectionFilter

<div class="param-level param-level-default-value">Default value: `boolean:and(true, boolean:and(nav:layer:sentences(), nav:layer:words()))`
</div>
<div class="param-type">Type: <a href="../converter/alvisnlp.corpus.expressions.Expression" class="converter">Expression</a>
</div>
Process only sections that satisfy this filter.

<a name="sentenceFilter">

### sentenceFilter

<div class="param-level param-level-default-value">Default value: `true`
</div>
<div class="param-type">Type: <a href="../converter/alvisnlp.corpus.expressions.Expression" class="converter">Expression</a>
</div>
Process only sentences that satisfy this filter.

<a name="sentenceLayerName">

### sentenceLayerName

<div class="param-level param-level-default-value">Default value: `sentences`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Name of the layer containing sentence annotations.

<a name="sentenceRole">

### sentenceRole

<div class="param-level param-level-default-value">Default value: `sentence`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Name of the role that denote the sentence to which belongs a dependency tuple.

<a name="wordLayerName">

### wordLayerName

<div class="param-level param-level-default-value">Default value: `words`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Name of the layer containing word annotations.

