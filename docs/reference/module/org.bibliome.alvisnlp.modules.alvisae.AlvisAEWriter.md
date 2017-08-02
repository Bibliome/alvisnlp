# org.bibliome.alvisnlp.modules.alvisae.AlvisAEWriter

## Synopsis

Writes each document in a file in the AlvisAE protocol format.

## Description

Writes each document in a file in the AlvisAE protocol format.

## Parameters

<a name="annotationSets">

### annotationSets

Optional

Type: [AnnotationSet[]](../converter/org.bibliome.alvisnlp.modules.alvisae.AnnotationSet[])

Annotation Set specifications.

<a name="documentDescription">

### documentDescription

Optional

Type: [Expression](../converter/alvisnlp.corpus.expressions.Expression)

Document description.

<a name="outDir">

### outDir

Optional

Type: [OutputDirectory](../converter/org.bibliome.util.files.OutputDirectory)

Directory where to write files.

<a name="schemaFile">

### schemaFile

Optional

Type: [InputFile](../converter/org.bibliome.util.files.InputFile)

AlvisAE schema file to include in all document files.

<a name="documentFilter">

### documentFilter

Default value: `true`

Type: [Expression](../converter/alvisnlp.corpus.expressions.Expression)

Only process document that satisfy this filter.

<a name="documentProperties">

### documentProperties

Default value: `{}`

Type: [ExpressionMapping](../converter/alvisnlp.module.types.ExpressionMapping)

Document properties specification.

<a name="fileName">

### fileName

Default value: `str:concat(properties:@:id(), constant:string:.json())`

Type: [Expression](../converter/alvisnlp.corpus.expressions.Expression)



<a name="owner">

### owner

Default value: `0`

Type: [Integer](../converter/java.lang.Integer)

Owner ID of the documents.

<a name="publish">

### publish

Default value: `false`

Type: [Boolean](../converter/java.lang.Boolean)

Publish the exported annotation sets.

<a name="sectionFilter">

### sectionFilter

Default value: `true`

Type: [Expression](../converter/alvisnlp.corpus.expressions.Expression)

Process only sections that satisfy this filter.

