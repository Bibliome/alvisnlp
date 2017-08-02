# org.bibliome.alvisnlp.modules.bionlpst.BioNLPSTReader

## Synopsis

Reads documents and annotations in the [BioNLP-ST 2013 a1/a2 format](XXX).

## Description

*org.bibliome.alvisnlp.modules.bionlpst.BioNLPSTReader* reads text files in the directory specified by [textDir](#textDir) as documents, then imports annotations in the corresponding files in the directories specified by [a1Dir](#a1Dir) and [a2Dir](#a2Dir).

## Parameters

<a name="textDir">

### textDir

Optional

Type: [InputDirectory](../converter/org.bibliome.util.files.InputDirectory)

Path to the directory containing `.a2` files.

<a name="a1Dir">

### a1Dir

Optional

Type: [InputDirectory](../converter/org.bibliome.util.files.InputDirectory)

Path to the directory containing `.a1` files. If this parameter is not set, then *org.bibliome.alvisnlp.modules.bionlpst.BioNLPSTReader* does not read `a1` files. This directory must contain one `.a1` file for each `.txt` file found in [textDir](#textDir).

<a name="a2Dir">

### a2Dir

Optional

Type: [InputDirectory](../converter/org.bibliome.util.files.InputDirectory)

Path to the directory containing `.a2` files. If this parameter is not set, then *org.bibliome.alvisnlp.modules.bionlpst.BioNLPSTReader* does not read `a2` files. This directory must contain one `.a2` file for each `.txt` file found in [textDir](#textDir).

<a name="constantAnnotationFeatures">

### constantAnnotationFeatures

Optional

Type: [Mapping](../converter/alvisnlp.module.types.Mapping)

Constant features to add to each annotation created by this module

<a name="constantDocumentFeatures">

### constantDocumentFeatures

Optional

Type: [Mapping](../converter/alvisnlp.module.types.Mapping)

Constant features to add to each document created by this module

<a name="constantRelationFeatures">

### constantRelationFeatures

Optional

Type: [Mapping](../converter/alvisnlp.module.types.Mapping)

Constant features to add to each relation created by this module

<a name="constantSectionFeatures">

### constantSectionFeatures

Optional

Type: [Mapping](../converter/alvisnlp.module.types.Mapping)

Constant features to add to each section created by this module

<a name="constantTupleFeatures">

### constantTupleFeatures

Optional

Type: [Mapping](../converter/alvisnlp.module.types.Mapping)

Constant features to add to each tuple created by this module

<a name="schema">

### schema

Optional

Type: [DocumentSchema](../converter/org.bibliome.util.bionlpst.schema.DocumentSchema)

Annotation schema against which the annotations are checked. *org.bibliome.alvisnlp.modules.bionlpst.BioNLPSTReader* aborts if the annotations do not check. If not set, the annotations are not checked.

<a name="charset">

### charset

Default value: `UTF-8`

Type: [String](../converter/java.lang.String)

Character encoding of all imported files.

<a name="equivalenceItemPrefix">

### equivalenceItemPrefix

Default value: `item`

Type: [String](../converter/java.lang.String)

Prefix of the role of arguments that represent group elements.

<a name="equivalenceRelationName">

### equivalenceRelationName

Default value: `equiv`

Type: [String](../converter/java.lang.String)

Name of the relation whose tuples represent equivalences.

<a name="eventKind">

### eventKind

Default value: `event`

Type: [String](../converter/java.lang.String)

Kind value for event annotations.

<a name="fragmentCountFeatureName">

### fragmentCountFeatureName

Default value: `fragments`

Type: [String](../converter/java.lang.String)

Feature where to store the number of merged fragments. This feature is only set if [textBoundAsAnnotations](#textBoundAsAnnotations) is `true`.

<a name="idFeatureName">

### idFeatureName

Default value: `id`

Type: [String](../converter/java.lang.String)

Feature where to store the annotation identifier.

<a name="kindFeatureName">

### kindFeatureName

Default value: `kind`

Type: [String](../converter/java.lang.String)

Feature of relations contatining the annotation kind.

<a name="relationKind">

### relationKind

Default value: `relation`

Type: [String](../converter/java.lang.String)

Kind value for relation annotations.

<a name="sectionName">

### sectionName

Default value: `text`

Type: [String](../converter/java.lang.String)

Name of the unique section contating all the text.

<a name="textBoundAsAnnotations">

### textBoundAsAnnotations

Default value: `false`

Type: [Boolean](../converter/java.lang.Boolean)

If `true`, *org.bibliome.alvisnlp.modules.bionlpst.BioNLPSTReader* imports text-bound annotations as annotations instead of tuples. Multiple fragments text-bound annotations are merged.

<a name="textBoundFragmentRolePrefix">

### textBoundFragmentRolePrefix

Default value: `frag`

Type: [String](../converter/java.lang.String)

Prefix of the role of arguments that represent text-bound fragments.

<a name="textKind">

### textKind

Default value: `text`

Type: [String](../converter/java.lang.String)

Kind value for text annotations.

<a name="triggerRole">

### triggerRole

Default value: `trigger`

Type: [String](../converter/java.lang.String)

Role of the argument that represent an event trigger.

<a name="typeFeatureName">

### typeFeatureName

Default value: `type`

Type: [String](../converter/java.lang.String)

Feature where to store the type of the annotation.

