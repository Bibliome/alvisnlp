<h1 class="module">BioLG</h1>

## Synopsis

Applies [BioLG](http://mars.cs.utu.fi/biolg/) and *lp2lp* to sentences.

## Description

 *BioLG* applies [BioLG](http://mars.cs.utu.fi/biolg/) and lp2lp to sentences specified as annotations from the <a href="#sentenceLayer" class="param">sentenceLayer</a> layer. Sentence words are specified by annotations in the <a href="#wordLayer" class="param">wordLayer</a> layer. For each sentence, only words entirely included in the sentence will be considered; <a href="../module/WoSMig" class="module">WoSMig</a> and <a href="../module/SeSMig" class="module">SeSMig</a> should create these layers with the appropriate annotations. Additionally BioLG can take advantage of word POS tag specified in the <a href="#posFeature" class="param">posFeature</a> feature.

The BioLG executable and all necessary resources (affix and worddictionaries) must be in the directory specified by <a href="#parserPath" class="param">parserPath</a> . Options to the executable are <a href="#maxLinkages" class="param">maxLinkages</a> and <a href="#timeout" class="param">timeout</a> .

The BioLG output is directly fed to *lp2lp* executable specified by <a href="#lp2lpExecutable" class="param">lp2lpExecutable</a> , its configuration file is specified by <a href="#lp2lpConf" class="param">lp2lpConf</a> .

 *BioLG* creates a relation named <a href="#dependencyRelation" class="param">dependencyRelation</a> in each section and a tuple in this relation for each dependency. This relation is ternary:
1.  <a href="#sentenceRole" class="param">sentenceRole</a> : the first argument is the sentence in which the dependency was found;
2.  <a href="#headRole" class="param">headRole</a> : the second argument is the head word of the dependency;
3.  <a href="#modifierRole" class="param">modifierRole</a> : the third argument is the modifier word of the dependency.

 *BioLG* adds to each dependency tuple a feature <a href="#linkageNumberFeature" class="param">linkageNumberFeature</a> with the linkage number to which begongs the tuple, and a feature <a href="#dependencyLabelFeature" class="param">dependencyLabelFeature</a> with the label of the dependency.

## Snippet



```xml
<biolg class="BioLG>
    <linkageNumberFeature></linkageNumberFeature>
    <lp2lpConf></lp2lpConf>
    <lp2lpExecutable></lp2lpExecutable>
    <parserPath></parserPath>
</biolg>
```

## Mandatory parameters

<h3 id="linkageNumberFeature" class="param">linkageNumberFeature</h3>

<div class="param-level param-level-mandatory">Mandatory
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Name of the dependecy tuple feature containing the linkage number.

<h3 id="lp2lpConf" class="param">lp2lpConf</h3>

<div class="param-level param-level-mandatory">Mandatory
</div>
<div class="param-type">Type: <a href="../converter/fr.inra.maiage.bibliome.util.files.InputFile" class="converter">InputFile</a>
</div>
Path to the lp2lp configuration file.

<h3 id="lp2lpExecutable" class="param">lp2lpExecutable</h3>

<div class="param-level param-level-mandatory">Mandatory
</div>
<div class="param-type">Type: <a href="../converter/fr.inra.maiage.bibliome.util.files.ExecutableFile" class="converter">ExecutableFile</a>
</div>
Path to the lp2lp executable.

<h3 id="parserPath" class="param">parserPath</h3>

<div class="param-level param-level-mandatory">Mandatory
</div>
<div class="param-type">Type: <a href="../converter/fr.inra.maiage.bibliome.util.files.WorkingDirectory" class="converter">WorkingDirectory</a>
</div>
Directory where BioLG is installed.

## Optional parameters

<h3 id="constantRelationFeatures" class="param">constantRelationFeatures</h3>

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/fr.inra.maiage.bibliome.alvisnlp.core.module.types.Mapping" class="converter">Mapping</a>
</div>
Constant features to add to each relation created by this module

<h3 id="constantTupleFeatures" class="param">constantTupleFeatures</h3>

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/fr.inra.maiage.bibliome.alvisnlp.core.module.types.Mapping" class="converter">Mapping</a>
</div>
Constant features to add to each tuple created by this module

<h3 id="maxLinkages" class="param">maxLinkages</h3>

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/java.lang.Integer" class="converter">Integer</a>
</div>
Maximum number of linkages to store.

<h3 id="dependencyLabelFeature" class="param">dependencyLabelFeature</h3>

<div class="param-level param-level-default-value">Default value: `label`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Name of the dependency tuple feature containing the dependency label.

<h3 id="dependencyRelation" class="param">dependencyRelation</h3>

<div class="param-level param-level-default-value">Default value: `dependencies`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Name of the relation containing the dependencies.

<h3 id="dependentRole" class="param">dependentRole</h3>

<div class="param-level param-level-default-value">Default value: `dependent`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Name of the role of the modifier in the dependency relation.

<h3 id="documentFilter" class="param">documentFilter</h3>

<div class="param-level param-level-default-value">Default value: `true`
</div>
<div class="param-type">Type: <a href="../converter/fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.Expression" class="converter">Expression</a>
</div>
Only process document that satisfy this filter.

<h3 id="headRole" class="param">headRole</h3>

<div class="param-level param-level-default-value">Default value: `head`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Name of the role of the head in the dependency relation.

<h3 id="posFeature" class="param">posFeature</h3>

<div class="param-level param-level-default-value">Default value: `pos`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Name of the feature in word annotations containing the POS tag.

<h3 id="sectionFilter" class="param">sectionFilter</h3>

<div class="param-level param-level-default-value">Default value: `true and layer:sentences and layer:words`
</div>
<div class="param-type">Type: <a href="../converter/fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.Expression" class="converter">Expression</a>
</div>
Process only sections that satisfy this filter.

<h3 id="sentenceFilter" class="param">sentenceFilter</h3>

<div class="param-level param-level-default-value">Default value: `true`
</div>
<div class="param-type">Type: <a href="../converter/fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.Expression" class="converter">Expression</a>
</div>
Expression evaluated as a boolean for each sentence. *BioLG* only parses sentences for which the result is true.

<h3 id="sentenceLayer" class="param">sentenceLayer</h3>

<div class="param-level param-level-default-value">Default value: `sentences`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Layer containing sentence annotations.

<h3 id="sentenceRole" class="param">sentenceRole</h3>

<div class="param-level param-level-default-value">Default value: `sentence`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Name of the role of the sentence in the dependency relation.

<h3 id="timeout" class="param">timeout</h3>

<div class="param-level param-level-default-value">Default value: `120`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.Integer" class="converter">Integer</a>
</div>
Time in seconds before entering in panic mode.

<h3 id="union" class="param">union</h3>

<div class="param-level param-level-default-value">Default value: `true`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.Boolean" class="converter">Boolean</a>
</div>
Either to process unions.

<h3 id="wordLayer" class="param">wordLayer</h3>

<div class="param-level param-level-default-value">Default value: `words`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Layer containing word annotations.

<h3 id="wordNumberLimit" class="param">wordNumberLimit</h3>

<div class="param-level param-level-default-value">Default value: `1000`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.Integer" class="converter">Integer</a>
</div>
Maximum number of words per biolg/lp2lp run.

