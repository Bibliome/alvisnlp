<h1 class="module">BioNLPSTReader</h1>

## Synopsis

Reads documents and annotations in the [BioNLP-ST 2013 a1/a2 format](XXX).

## Description

*BioNLPSTReader* reads text files in the directory specified by <a href="#textDir" class="param">textDir</a> as documents, then imports annotations in the corresponding files in the directories specified by <a href="#a1Dir" class="param">a1Dir</a> and <a href="#a2Dir" class="param">a2Dir</a>.

## Parameters

<a name="textDir">

### textDir

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/org.bibliome.util.files.InputDirectory" class="converter">InputDirectory</a>
</div>
Path to the directory containing `.a2` files.

<a name="a1Dir">

### a1Dir

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/org.bibliome.util.files.InputDirectory" class="converter">InputDirectory</a>
</div>
Path to the directory containing `.a1` files. If this parameter is not set, then *BioNLPSTReader* does not read `a1` files. This directory must contain one `.a1` file for each `.txt` file found in <a href="#textDir" class="param">textDir</a>.

<a name="a2Dir">

### a2Dir

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/org.bibliome.util.files.InputDirectory" class="converter">InputDirectory</a>
</div>
Path to the directory containing `.a2` files. If this parameter is not set, then *BioNLPSTReader* does not read `a2` files. This directory must contain one `.a2` file for each `.txt` file found in <a href="#textDir" class="param">textDir</a>.

<a name="constantAnnotationFeatures">

### constantAnnotationFeatures

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/alvisnlp.module.types.Mapping" class="converter">Mapping</a>
</div>
Constant features to add to each annotation created by this module

<a name="constantDocumentFeatures">

### constantDocumentFeatures

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/alvisnlp.module.types.Mapping" class="converter">Mapping</a>
</div>
Constant features to add to each document created by this module

<a name="constantRelationFeatures">

### constantRelationFeatures

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/alvisnlp.module.types.Mapping" class="converter">Mapping</a>
</div>
Constant features to add to each relation created by this module

<a name="constantSectionFeatures">

### constantSectionFeatures

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/alvisnlp.module.types.Mapping" class="converter">Mapping</a>
</div>
Constant features to add to each section created by this module

<a name="constantTupleFeatures">

### constantTupleFeatures

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/alvisnlp.module.types.Mapping" class="converter">Mapping</a>
</div>
Constant features to add to each tuple created by this module

<a name="schema">

### schema

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/org.bibliome.util.bionlpst.schema.DocumentSchema" class="converter">DocumentSchema</a>
</div>
Annotation schema against which the annotations are checked. *BioNLPSTReader* aborts if the annotations do not check. If not set, the annotations are not checked.

<a name="charset">

### charset

<div class="param-level param-level-default-value">Default value: `UTF-8`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Character encoding of all imported files.

<a name="equivalenceItemPrefix">

### equivalenceItemPrefix

<div class="param-level param-level-default-value">Default value: `item`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Prefix of the role of arguments that represent group elements.

<a name="equivalenceRelationName">

### equivalenceRelationName

<div class="param-level param-level-default-value">Default value: `equiv`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Name of the relation whose tuples represent equivalences.

<a name="eventKind">

### eventKind

<div class="param-level param-level-default-value">Default value: `event`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Kind value for event annotations.

<a name="fragmentCountFeatureName">

### fragmentCountFeatureName

<div class="param-level param-level-default-value">Default value: `fragments`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Feature where to store the number of merged fragments. This feature is only set if <a href="#textBoundAsAnnotations" class="param">textBoundAsAnnotations</a> is `true`.

<a name="idFeatureName">

### idFeatureName

<div class="param-level param-level-default-value">Default value: `id`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Feature where to store the annotation identifier.

<a name="kindFeatureName">

### kindFeatureName

<div class="param-level param-level-default-value">Default value: `kind`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Feature of relations contatining the annotation kind.

<a name="relationKind">

### relationKind

<div class="param-level param-level-default-value">Default value: `relation`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Kind value for relation annotations.

<a name="sectionName">

### sectionName

<div class="param-level param-level-default-value">Default value: `text`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Name of the unique section contating all the text.

<a name="textBoundAsAnnotations">

### textBoundAsAnnotations

<div class="param-level param-level-default-value">Default value: `false`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.Boolean" class="converter">Boolean</a>
</div>
If `true`, *BioNLPSTReader* imports text-bound annotations as annotations instead of tuples. Multiple fragments text-bound annotations are merged.

<a name="textBoundFragmentRolePrefix">

### textBoundFragmentRolePrefix

<div class="param-level param-level-default-value">Default value: `frag`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Prefix of the role of arguments that represent text-bound fragments.

<a name="textKind">

### textKind

<div class="param-level param-level-default-value">Default value: `text`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Kind value for text annotations.

<a name="triggerRole">

### triggerRole

<div class="param-level param-level-default-value">Default value: `trigger`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Role of the argument that represent an event trigger.

<a name="typeFeatureName">

### typeFeatureName

<div class="param-level param-level-default-value">Default value: `type`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Feature where to store the type of the annotation.

