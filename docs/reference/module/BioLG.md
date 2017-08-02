# org.bibliome.alvisnlp.modules.biolg.BioLG

## Synopsis

Applies [BioLG](http://mars.cs.utu.fi/biolg/) and *lp2lp* to sentences.

## Description

*org.bibliome.alvisnlp.modules.biolg.BioLG* applies [BioLG](http://mars.cs.utu.fi/biolg/) and lp2lp to sentences specified as annotations from the [sentenceLayer](#sentenceLayer) layer. Sentence words are specified by annotations in the [wordLayer](#wordLayer) layer. For each sentence, only words entirely included in the sentence will be considered; [WoSMig](../module/WoSMig) and [SeSMig](../module/SeSMig) should create these layers with the appropriate annotations. Additionally BioLG can take advantage of word POS tag specified in the [posFeature](#posFeature) feature.

The BioLG executable and all necessary resources (affix and worddictionaries) must be in the directory specified by [parserPath](#parserPath). Options to the executable are [maxLinkages](#maxLinkages) and [timeout](#timeout).

The BioLG output is directly fed to *lp2lp* executable specified by [lp2lpExecutable](#lp2lpExecutable), its configuration file is specified by [lp2lpConf](#lp2lpConf).

*org.bibliome.alvisnlp.modules.biolg.BioLG* creates a relation named [dependencyRelation](#dependencyRelation) in each section and a tuple in this relation for each dependency. This relation is ternary:
      
1. [sentenceRole](#sentenceRole): the first argument is the sentence in which the dependency was found;
2. [headRole](#headRole): the second argument is the head word of the dependency;
3. [modifierRole](#modifierRole): the third argument is the modifier word of the dependency.

*org.bibliome.alvisnlp.modules.biolg.BioLG* adds to each dependency tuple a feature [linkageNumberFeature](#linkageNumberFeature) with the linkage number to which begongs the tuple, and a feature [dependencyLabelFeature](#dependencyLabelFeature) with the label of the dependency.

## Parameters

<a name="linkageNumberFeature">

### linkageNumberFeature

Optional

Type: [String](../converter/java.lang.String)

Name of the dependecy tuple feature containing the linkage number.

<a name="lp2lpConf">

### lp2lpConf

Optional

Type: [InputFile](../converter/org.bibliome.util.files.InputFile)

Path to the lp2lp configuration file.

<a name="lp2lpExecutable">

### lp2lpExecutable

Optional

Type: [ExecutableFile](../converter/org.bibliome.util.files.ExecutableFile)

Path to the lp2lp executable.

<a name="parserPath">

### parserPath

Optional

Type: [WorkingDirectory](../converter/org.bibliome.util.files.WorkingDirectory)

Directory where BioLG is installed.

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

<a name="maxLinkages">

### maxLinkages

Optional

Type: [Integer](../converter/java.lang.Integer)

Maximum number of linkages to store.

<a name="dependencyLabelFeature">

### dependencyLabelFeature

Default value: `label`

Type: [String](../converter/java.lang.String)

Name of the dependency tuple feature containing the dependency label.

<a name="dependencyRelation">

### dependencyRelation

Default value: `dependencies`

Type: [String](../converter/java.lang.String)

Name of the relation containing the dependencies.

<a name="dependentRole">

### dependentRole

Default value: `dependent`

Type: [String](../converter/java.lang.String)

Name of the role of the modifier in the dependency relation.

<a name="documentFilter">

### documentFilter

Default value: `true`

Type: [Expression](../converter/alvisnlp.corpus.expressions.Expression)

Only process document that satisfy this filter.

<a name="headRole">

### headRole

Default value: `head`

Type: [String](../converter/java.lang.String)

Name of the role of the head in the dependency relation.

<a name="posFeature">

### posFeature

Default value: `pos`

Type: [String](../converter/java.lang.String)

Name of the feature in word annotations containing the POS tag.

<a name="sectionFilter">

### sectionFilter

Default value: `boolean:and(true, boolean:and(nav:layer:sentences(), nav:layer:words()))`

Type: [Expression](../converter/alvisnlp.corpus.expressions.Expression)

Process only sections that satisfy this filter.

<a name="sentenceFilter">

### sentenceFilter

Default value: `true`

Type: [Expression](../converter/alvisnlp.corpus.expressions.Expression)

Expression evaluated as a boolean for each sentence. *org.bibliome.alvisnlp.modules.biolg.BioLG* only parses sentences for which the result is true.

<a name="sentenceLayer">

### sentenceLayer

Default value: `sentences`

Type: [String](../converter/java.lang.String)

Layer containing sentence annotations.

<a name="sentenceRole">

### sentenceRole

Default value: `sentence`

Type: [String](../converter/java.lang.String)

Name of the role of the sentence in the dependency relation.

<a name="timeout">

### timeout

Default value: `120`

Type: [Integer](../converter/java.lang.Integer)

Time in seconds before entering in panic mode.

<a name="union">

### union

Default value: `true`

Type: [Boolean](../converter/java.lang.Boolean)

Either to process unions.

<a name="wordLayer">

### wordLayer

Default value: `words`

Type: [String](../converter/java.lang.String)

Layer containing word annotations.

<a name="wordNumberLimit">

### wordNumberLimit

Default value: `1000`

Type: [Integer](../converter/java.lang.Integer)

Maximum number of words per biolg/lp2lp run.

