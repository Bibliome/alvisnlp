# org.bibliome.alvisnlp.modules.treetagger.TreeTaggerReader

## Synopsis

Read files in tree-tagger output format and creates a document for each file read.

## Description

Each document contains a single section named [sectionName](#sectionName); its contents is constructed by concatenating the first column of each token separated with a space character.

*org.bibliome.alvisnlp.modules.treetagger.TreeTaggerReader* keeps the tree-tagger tokenization in annotations added into the layer [wordLayerName](#wordLayerName).
      The POS tag and lemma are recorded in the annotation's [posFeatureKey](#posFeatureKey) and [lemmaFeatureKey](#lemmaFeatureKey) features respectively.

The document identifier is the path of the corresponding file.

## Parameters

<a name="sectionName">

### sectionName

Optional

Type: [String](../converter/java.lang.String)

Name of the section of each document.

<a name="sourcePath">

### sourcePath

Optional

Type: [SourceStream](../converter/org.bibliome.util.streams.SourceStream)

Path to the source directory or source file.

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

<a name="constantSectionFeatures">

### constantSectionFeatures

Optional

Type: [Mapping](../converter/alvisnlp.module.types.Mapping)

Constant features to add to each section created by this module

<a name="lemmaFeatureKey">

### lemmaFeatureKey

Optional

Type: [String](../converter/java.lang.String)

Name of the feature where to store word lemmas.

<a name="posFeatureKey">

### posFeatureKey

Optional

Type: [String](../converter/java.lang.String)

Name of the feature where to store word POS tags.

<a name="charset">

### charset

Default value: `UTF-8`

Type: [String](../converter/java.lang.String)

Character set of input files.

<a name="sentenceLayerName">

### sentenceLayerName

Default value: `sentences`

Type: [String](../converter/java.lang.String)

Name of the layer where to store sentence annotations.

<a name="wordLayerName">

### wordLayerName

Default value: `words`

Type: [String](../converter/java.lang.String)

Name of the layer where to store word annotations.

