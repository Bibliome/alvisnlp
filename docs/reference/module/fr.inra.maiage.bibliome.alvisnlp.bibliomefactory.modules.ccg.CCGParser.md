<h1 class="module">CCGParser</h1>

## Synopsis

Syntax parsing with [CCG Parser](http://svn.ask.it.usyd.edu.au/trac/candc/wiki) .

## Description

 *CCGParser* applies the [CCG Parser](http://svn.ask.it.usyd.edu.au/trac/candc/wiki) to sentences specified as annotations from the <a href="#sentenceLayer" class="param">sentenceLayer</a> layer. Sentence words are specified by annotations in the <a href="#wordLayer" class="param">wordLayer</a> layer. For each sentence, only words entirely included in the sentence will be considered; <a href="../module/WoSMig" class="module">WoSMig</a> and <a href="../module/SeSMig" class="module">SeSMig</a> should create these layers with the appropriate annotations. Additionally CCGParser takes advantage of word POS tag specified in the <a href="#posFeature" class="param">posFeature</a> feature.

 *CCGParser* creates a relation named <a href="#dependencyRelation" class="param">dependencyRelation</a> in each section and a tuple in this relation for each dependency. This relation is ternary:
1.  <a href="#dependencySentenceRole" class="param">dependencySentenceRole</a> : the first argument is the sentence in which the dependency was found;
2.  <a href="#headRole" class="param">headRole</a> : the second argument is the head word of the dependency;
3.  <a href="#dependentRole" class="param">dependentRole</a> : the third argument is the dependent word of the dependency.

 *CCGParser* adds to each dependency tuple a feature <a href="#dependencyLabelFeature" class="param">dependencyLabelFeature</a> with the label of the dependency.

## Snippet



<button class="copy-code-button" title="Copy to clipboard" onclick="copy_code(this)">📋</button>
```xml
<ccgparser class="CCGParser>
    <executable></executable>
    <parserModel></parserModel>
    <superModel></superModel>
</ccgparser>
```

## Mandatory parameters

<h3 id="executable" class="param">executable</h3>

<div class="param-level param-level-mandatory">Mandatory
</div>
<div class="param-type">Type: <a href="../converter/fr.inra.maiage.bibliome.util.files.ExecutableFile" class="converter">ExecutableFile</a>
</div>
Path to the CCG Parser executable.

<h3 id="parserModel" class="param">parserModel</h3>

<div class="param-level param-level-mandatory">Mandatory
</div>
<div class="param-type">Type: <a href="../converter/fr.inra.maiage.bibliome.util.files.InputDirectory" class="converter">InputDirectory</a>
</div>
Path to the parser model file.

<h3 id="superModel" class="param">superModel</h3>

<div class="param-level param-level-mandatory">Mandatory
</div>
<div class="param-type">Type: <a href="../converter/fr.inra.maiage.bibliome.util.files.InputDirectory" class="converter">InputDirectory</a>
</div>
Path to the CCG supertagger model file.

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

<h3 id="stanfordMarkedUpScript" class="param">stanfordMarkedUpScript</h3>

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/fr.inra.maiage.bibliome.util.files.InputFile" class="converter">InputFile</a>
</div>
Path to the markedup script for Stanford tagset output. See [Biomedical parsing for CCG](http://svn.ask.it.usyd.edu.au/trac/candc/wiki/Biomedical) .

<h3 id="stanfordScript" class="param">stanfordScript</h3>

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/fr.inra.maiage.bibliome.util.files.ExecutableFile" class="converter">ExecutableFile</a>
</div>
Post-processing script for Stanford tagset output. See [Biomedical parsing for CCG](http://svn.ask.it.usyd.edu.au/trac/candc/wiki/Biomedical) .

<h3 id="dependencyLabelFeature" class="param">dependencyLabelFeature</h3>

<div class="param-level param-level-default-value">Default value: `label`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Name of the feature where to store the dependency label.

<h3 id="dependencyRelation" class="param">dependencyRelation</h3>

<div class="param-level param-level-default-value">Default value: `dependencies`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Name of the relation where to store dependency tuples.

<h3 id="dependencySentenceRole" class="param">dependencySentenceRole</h3>

<div class="param-level param-level-default-value">Default value: `sentence`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Name of the role of the dependency tuple argument that references the parsed sentence.

<h3 id="dependentRole" class="param">dependentRole</h3>

<div class="param-level param-level-default-value">Default value: `dependent`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Name of the role of the dependency tuple argument that references the modifier (dependent) token.

<h3 id="documentFilter" class="param">documentFilter</h3>

<div class="param-level param-level-default-value">Default value: `true`
</div>
<div class="param-type">Type: <a href="../converter/fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.Expression" class="converter">Expression</a>
</div>
Only process document that satisfy this expression.

<h3 id="formFeature" class="param">formFeature</h3>

<div class="param-level param-level-default-value">Default value: `form`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Name of the feature in word annotations that contains the surface form.

<h3 id="headRole" class="param">headRole</h3>

<div class="param-level param-level-default-value">Default value: `head`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Name of the role of the dependency tuple argument that references the head (governor) token.

<h3 id="internalEncoding" class="param">internalEncoding</h3>

<div class="param-level param-level-default-value">Default value: `UTF-8`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Character encoding to use for CCG input and output files.

<h3 id="lpTransformation" class="param">lpTransformation</h3>

<div class="param-level param-level-default-value">Default value: `false`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.Boolean" class="converter">Boolean</a>
</div>
Either to translate into LP tag-set.

<h3 id="maxRuns" class="param">maxRuns</h3>

<div class="param-level param-level-default-value">Default value: `1`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.Integer" class="converter">Integer</a>
</div>
Maximal number of CCG runs.

<h3 id="maxSuperCats" class="param">maxSuperCats</h3>

<div class="param-level param-level-default-value">Default value: `500000`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.Integer" class="converter">Integer</a>
</div>
Maximum number of supercats before the parse explodes (cited from CCG documentation).

<h3 id="posFeature" class="param">posFeature</h3>

<div class="param-level param-level-default-value">Default value: `pos`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Name of the feature in word annotations where to write POS tags.

<h3 id="sectionFilter" class="param">sectionFilter</h3>

<div class="param-level param-level-default-value">Default value: `true and layer:sentences and layer:words`
</div>
<div class="param-type">Type: <a href="../converter/fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.Expression" class="converter">Expression</a>
</div>
Process only sections that satisfy this expression.

<h3 id="sentenceFilter" class="param">sentenceFilter</h3>

<div class="param-level param-level-default-value">Default value: `true`
</div>
<div class="param-type">Type: <a href="../converter/fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.Expression" class="converter">Expression</a>
</div>
Process only sentences that satisfy this filter.

<h3 id="sentenceLayer" class="param">sentenceLayer</h3>

<div class="param-level param-level-default-value">Default value: `sentences`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Name of the layer containing sentence annotations.

<h3 id="supertagFeature" class="param">supertagFeature</h3>

<div class="param-level param-level-default-value">Default value: `supertag`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Name of the feature containing the supertag label.

<h3 id="wordLayer" class="param">wordLayer</h3>

<div class="param-level param-level-default-value">Default value: `words`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Name of the layer containing word annotations.

## Deprecated parameters

<h3 id="formFeatureName" class="param">formFeatureName</h3>

<div class="param-level param-level-deprecated">Deprecated
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Deprecated alias for <a href="#formFeature" class="param">formFeature</a> .

<h3 id="labelFeatureName" class="param">labelFeatureName</h3>

<div class="param-level param-level-deprecated">Deprecated
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Deprecated alias for <a href="#dependencyLabelFeature" class="param">dependencyLabelFeature</a> .

<h3 id="posFeatureName" class="param">posFeatureName</h3>

<div class="param-level param-level-deprecated">Deprecated
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Deprecated alias for <a href="#posFeature" class="param">posFeature</a> .

<h3 id="relationName" class="param">relationName</h3>

<div class="param-level param-level-deprecated">Deprecated
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Deprecated alias for <a href="#dependencyRelation" class="param">dependencyRelation</a> .

<h3 id="sentenceLayerName" class="param">sentenceLayerName</h3>

<div class="param-level param-level-deprecated">Deprecated
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Deprecated alias for <a href="#sentenceLayer" class="param">sentenceLayer</a> .

<h3 id="supertagFeatureName" class="param">supertagFeatureName</h3>

<div class="param-level param-level-deprecated">Deprecated
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Deprecated alias for <a href="#supertagFeature" class="param">supertagFeature</a> .

<h3 id="wordLayerName" class="param">wordLayerName</h3>

<div class="param-level param-level-deprecated">Deprecated
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Deprecated alias for <a href="#wordLayer" class="param">wordLayer</a> .

