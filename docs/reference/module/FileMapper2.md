# org.bibliome.alvisnlp.modules.mapper.FileMapper2

## Synopsis

Deprecated alias for [FileMapper](../module/FileMapper).

**This module is obsolete, superceded by org.bibliome.alvisnlp.modules.mapper.FileMapper**

## Description

## Parameters

<a name="form">

### form

Optional

Type: [Expression](../converter/alvisnlp.corpus.expressions.Expression)

Expression evaluated as a string with the target element as context that specifies the target key.

<a name="mappingFile">

### mappingFile

Optional

Type: [SourceStream](../converter/org.bibliome.util.streams.SourceStream)

Tab-separated file containing the dictionary.

<a name="target">

### target

Optional

Type: [Expression](../converter/alvisnlp.corpus.expressions.Expression)

Expression evaluated as a list of elements with the corpus as context that specify the elements to be mapped.

<a name="targetFeatures">

### targetFeatures

Optional

Type: [String[]](../converter/java.lang.String[])

Name of the features where to store the columns of matched lines.

<a name="ignoreCase">

### ignoreCase

Default value: `false`

Type: [Boolean](../converter/java.lang.Boolean)

Either to match ignoring the case.

<a name="keyColumn">

### keyColumn

Default value: `0`

Type: [Integer](../converter/java.lang.Integer)

Index of the line key in [mappingFile](#mappingFile).

<a name="operator">

### operator

Default value: `exact`

Type: [MappingOperator](../converter/org.bibliome.alvisnlp.modules.mapper.MappingOperator)

Matching operator.

<a name="separator">

### separator

Default value: `	`

Type: [Character](../converter/java.lang.Character)

Separator character between columns in [mappingFile](#mappingFile).

