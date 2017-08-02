# org.bibliome.alvisnlp.modules.I2B2Reader

## Synopsis

*org.bibliome.alvisnlp.modules.I2B2Reader* reads files in the format of the [I2B2]() challenge.

**This module is experimental.**

## Description

*org.bibliome.alvisnlp.modules.I2B2Reader* reads documents in [I2B2 challenge]() including the text of documents, tokenization as annotations, concepts as annotations, assertions as annotation features and relations as tuples.

## Parameters

<a name="textDir">

### textDir

Optional

Type: [InputDirectory](../converter/org.bibliome.util.files.InputDirectory)

Path to the directory containing I2B2 text files.

<a name="assertionsDir">

### assertionsDir

Optional

Type: [InputDirectory](../converter/org.bibliome.util.files.InputDirectory)

Path to the directory where assertion files can be found.

<a name="conceptsDir">

### conceptsDir

Optional

Type: [InputDirectory](../converter/org.bibliome.util.files.InputDirectory)

Path to the directory that contains concept annotations.

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

<a name="relationsDir">

### relationsDir

Optional

Type: [InputDirectory](../converter/org.bibliome.util.files.InputDirectory)

Path to the directory where relation files can be found.

<a name="assertionFeature">

### assertionFeature

Default value: `assertion`

Type: [String](../converter/java.lang.String)



<a name="conceptTypeFeature">

### conceptTypeFeature

Default value: `type`

Type: [String](../converter/java.lang.String)

Feature where to store the concept type.

<a name="conceptsLayerName">

### conceptsLayerName

Default value: `concepts`

Type: [String](../converter/java.lang.String)

Name of the layer where to store concepts annotations.

<a name="leftRole">

### leftRole

Default value: `left`

Type: [String](../converter/java.lang.String)

Name of the left argument of relations.

<a name="linenoFeature">

### linenoFeature

Default value: `lineno`

Type: [String](../converter/java.lang.String)

Name of the feature where to store the line number.

<a name="linesLayerName">

### linesLayerName

Default value: `lines`

Type: [String](../converter/java.lang.String)

Name of the layer where to store lines.

<a name="rightRole">

### rightRole

Default value: `right`

Type: [String](../converter/java.lang.String)



<a name="sectionName">

### sectionName

Default value: `text`

Type: [String](../converter/java.lang.String)

Name of the unique section of each document.

<a name="tokenNumberFeature">

### tokenNumberFeature

Default value: `tokenno`

Type: [String](../converter/java.lang.String)

Feature where to store the token index.

<a name="tokensLayerName">

### tokensLayerName

Default value: `tokens`

Type: [String](../converter/java.lang.String)

Name of the layer where to store tokens.

