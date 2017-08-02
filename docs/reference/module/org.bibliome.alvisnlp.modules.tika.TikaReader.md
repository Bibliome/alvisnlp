# org.bibliome.alvisnlp.modules.tika.TikaReader

## Synopsis

Reads PDF or DOC files and adds a document in the corpus for each file.

**This module is experimental.**

## Description



## Parameters

<a name="source">

### source

Optional

Type: [SourceStream](../converter/org.bibliome.util.streams.SourceStream)

Path to the source directory or source file.

<a name="constantAnnotationFeatures">

### constantAnnotationFeatures

Optional

Type: [Mapping](../converter/alvisnlp.module.types.Mapping)

UNDOCUMENTED

<a name="constantDocumentFeatures">

### constantDocumentFeatures

Optional

Type: [Mapping](../converter/alvisnlp.module.types.Mapping)

Constant features to add to each document created by this module

<a name="constantSectionFeatures">

### constantSectionFeatures

Optional

Type: [Mapping](../converter/alvisnlp.module.types.Mapping)

Constant features to add to each section created by this module

<a name="htmlLayerName">

### htmlLayerName

Default value: `html`

Type: [String](../converter/java.lang.String)



<a name="sectionName">

### sectionName

Default value: `text`

Type: [String](../converter/java.lang.String)

Name of the single section containing the whole contents of a file.

<a name="tagFeatureName">

### tagFeatureName

Default value: `tag`

Type: [String](../converter/java.lang.String)



