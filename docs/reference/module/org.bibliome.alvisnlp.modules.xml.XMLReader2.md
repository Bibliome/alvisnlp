# org.bibliome.alvisnlp.modules.xml.XMLReader2

## Synopsis

Deprecated alias for [XMLReader](../module/XMLReader).

**This module is obsolete, superceded by org.bibliome.alvisnlp.modules.xml.XMLReader**

## Description

## Parameters

<a name="sourcePath">

### sourcePath

Optional

Type: [SourceStream](../converter/org.bibliome.util.streams.SourceStream)

Path to the source directory or source file.

<a name="xslTransform">

### xslTransform

Optional

Type: [SourceStream](../converter/org.bibliome.util.streams.SourceStream)

XSLT Stylesheet to apply on the input.

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

<a name="stringParams">

### stringParams

Optional

Type: [Mapping](../converter/alvisnlp.module.types.Mapping)

Parameters to pass to the XSLT Stylesheet specified by [xslTransform](#xslTransform).

<a name="html">

### html

Default value: `false`

Type: [Boolean](../converter/java.lang.Boolean)

Set to true if the input is HTML rather than XML.

<a name="rawTagNames">

### rawTagNames

Default value: `false`

Type: [Boolean](../converter/java.lang.Boolean)

If true, do not convert tag names to upper case.

