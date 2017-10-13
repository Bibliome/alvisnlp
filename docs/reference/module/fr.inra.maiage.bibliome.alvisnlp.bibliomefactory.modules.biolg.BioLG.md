<h1 class="module">BioLG</h1>

## Synopsis

Applies [BioLG](http://mars.cs.utu.fi/biolg/) and *lp2lp* to sentences.

## Description

*BioLG* applies [BioLG](http://mars.cs.utu.fi/biolg/) and lp2lp to sentences specified as annotations from the <a href="#sentenceLayer" class="param">sentenceLayer</a> layer. Sentence words are specified by annotations in the <a href="#wordLayer" class="param">wordLayer</a> layer. For each sentence, only words entirely included in the sentence will be considered; <a href="../module/WoSMig" class="module">WoSMig</a> and <a href="../module/SeSMig" class="module">SeSMig</a> should create these layers with the appropriate annotations. Additionally BioLG can take advantage of word POS tag specified in the <a href="#posFeature" class="param">posFeature</a> feature.

The BioLG executable and all necessary resources (affix and worddictionaries) must be in the directory specified by <a href="#parserPath" class="param">parserPath</a>. Options to the executable are <a href="#maxLinkages" class="param">maxLinkages</a> and <a href="#timeout" class="param">timeout</a>.

The BioLG output is directly fed to *lp2lp* executable specified by <a href="#lp2lpExecutable" class="param">lp2lpExecutable</a>, its configuration file is specified by <a href="#lp2lpConf" class="param">lp2lpConf</a>.

*BioLG* creates a relation named <a href="#dependencyRelation" class="param">dependencyRelation</a> in each section and a tuple in this relation for each dependency. This relation is ternary:
  
1. <a href="#sentenceRole" class="param">sentenceRole</a>: the first argument is the sentence in which the dependency was found;
2. <a href="#headRole" class="param">headRole</a>: the second argument is the head word of the dependency;
3. <a href="#modifierRole" class="param">modifierRole</a>: the third argument is the modifier word of the dependency.

*BioLG* adds to each dependency tuple a feature <a href="#linkageNumberFeature" class="param">linkageNumberFeature</a> with the linkage number to which begongs the tuple, and a feature <a href="#dependencyLabelFeature" class="param">dependencyLabelFeature</a> with the label of the dependency.

## Parameters

<a name="linkageNumberFeature">

### linkageNumberFeature

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Name of the dependecy tuple feature containing the linkage number.

<a name="lp2lpConf">

### lp2lpConf

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/fr.inra.maiage.bibliome.util.files.InputFile" class="converter">InputFile</a>
</div>
Path to the lp2lp configuration file.

<a name="lp2lpExecutable">

### lp2lpExecutable

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/fr.inra.maiage.bibliome.util.files.ExecutableFile" class="converter">ExecutableFile</a>
</div>
Path to the lp2lp executable.

<a name="parserPath">

### parserPath

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/fr.inra.maiage.bibliome.util.files.WorkingDirectory" class="converter">WorkingDirectory</a>
</div>
Directory where BioLG is installed.

<a name="constantRelationFeatures">

### constantRelationFeatures

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/fr.inra.maiage.bibliome.alvisnlp.core.module.types.Mapping" class="converter">Mapping</a>
</div>
Constant features to add to each relation created by this module

<a name="constantTupleFeatures">

### constantTupleFeatures

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/fr.inra.maiage.bibliome.alvisnlp.core.module.types.Mapping" class="converter">Mapping</a>
</div>
Constant features to add to each tuple created by this module

<a name="maxLinkages">

### maxLinkages

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/java.lang.Integer" class="converter">Integer</a>
</div>
Maximum number of linkages to store.

<a name="dependencyLabelFeature">

### dependencyLabelFeature

<div class="param-level param-level-default-value">Default value: `label`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Name of the dependency tuple feature containing the dependency label.

<a name="dependencyRelation">

### dependencyRelation

<div class="param-level param-level-default-value">Default value: `dependencies`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Name of the relation containing the dependencies.

<a name="dependentRole">

### dependentRole

<div class="param-level param-level-default-value">Default value: `dependent`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Name of the role of the modifier in the dependency relation.

<a name="documentFilter">

### documentFilter

<div class="param-level param-level-default-value">Default value: `true`
</div>
<div class="param-type">Type: <a href="../converter/fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.Expression" class="converter">Expression</a>
</div>
Only process document that satisfy this filter.

<a name="headRole">

### headRole

<div class="param-level param-level-default-value">Default value: `head`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Name of the role of the head in the dependency relation.

<a name="posFeature">

### posFeature

<div class="param-level param-level-default-value">Default value: `pos`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Name of the feature in word annotations containing the POS tag.

<a name="sectionFilter">

### sectionFilter

<div class="param-level param-level-default-value">Default value: `boolean:and(true, boolean:and(nav:layer:sentences(), nav:layer:words()))`
</div>
<div class="param-type">Type: <a href="../converter/fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.Expression" class="converter">Expression</a>
</div>
Process only sections that satisfy this filter.

<a name="sentenceFilter">

### sentenceFilter

<div class="param-level param-level-default-value">Default value: `true`
</div>
<div class="param-type">Type: <a href="../converter/fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.Expression" class="converter">Expression</a>
</div>
Expression evaluated as a boolean for each sentence. *BioLG* only parses sentences for which the result is true.

<a name="sentenceLayer">

### sentenceLayer

<div class="param-level param-level-default-value">Default value: `sentences`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Layer containing sentence annotations.

<a name="sentenceRole">

### sentenceRole

<div class="param-level param-level-default-value">Default value: `sentence`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Name of the role of the sentence in the dependency relation.

<a name="timeout">

### timeout

<div class="param-level param-level-default-value">Default value: `120`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.Integer" class="converter">Integer</a>
</div>
Time in seconds before entering in panic mode.

<a name="union">

### union

<div class="param-level param-level-default-value">Default value: `true`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.Boolean" class="converter">Boolean</a>
</div>
Either to process unions.

<a name="wordLayer">

### wordLayer

<div class="param-level param-level-default-value">Default value: `words`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Layer containing word annotations.

<a name="wordNumberLimit">

### wordNumberLimit

<div class="param-level param-level-default-value">Default value: `1000`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.Integer" class="converter">Integer</a>
</div>
Maximum number of words per biolg/lp2lp run.

