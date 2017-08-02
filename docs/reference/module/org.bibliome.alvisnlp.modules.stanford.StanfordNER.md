# org.bibliome.alvisnlp.modules.stanford.StanfordNER

## Synopsis

synopsis

**This module is experimental.**

## Description

synopsis

## Parameters

<a name="classifierFile">

### classifierFile

Optional

Type: [InputFile](../converter/org.bibliome.util.files.InputFile)



<a name="labelFeatureName">

### labelFeatureName

Optional

Type: [String](../converter/java.lang.String)



<a name="targetLayerName">

### targetLayerName

Optional

Type: [String](../converter/java.lang.String)



<a name="constantAnnotationFeatures">

### constantAnnotationFeatures

Optional

Type: [Mapping](../converter/alvisnlp.module.types.Mapping)

Constant features to add to each annotation created by this module

<a name="documentFilter">

### documentFilter

Default value: `true`

Type: [Expression](../converter/alvisnlp.corpus.expressions.Expression)

Only process document that satisfy this filter.

<a name="formFeatureName">

### formFeatureName

Default value: `form`

Type: [String](../converter/java.lang.String)



<a name="searchInContents">

### searchInContents

Default value: `false`

Type: [Boolean](../converter/java.lang.Boolean)



<a name="sectionFilter">

### sectionFilter

Default value: `boolean:and(true, boolean:and(nav:layer:words(), nav:layer:sentences()))`

Type: [Expression](../converter/alvisnlp.corpus.expressions.Expression)

Process only sections that satisfy this filter.

<a name="sentenceLayerName">

### sentenceLayerName

Default value: `sentences`

Type: [String](../converter/java.lang.String)



<a name="wordLayerName">

### wordLayerName

Default value: `words`

Type: [String](../converter/java.lang.String)



