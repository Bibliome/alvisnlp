# org.bibliome.alvisnlp.modules.MergeLayers

## Synopsis

Creates a new layer in each section containing all annotations in source layers.

## Description

*org.bibliome.alvisnlp.modules.MergeLayers* adds annotations in the layers [sourceLayerNames](#sourceLayerNames) into the layer [targetLayerName](#targetLayerName).

## Parameters

<a name="sourceLayerNames">

### sourceLayerNames

Optional

Type: [String[]](../converter/java.lang.String[])

Name of the layers where to get annotations.

<a name="targetLayerName">

### targetLayerName

Optional

Type: [String](../converter/java.lang.String)

Name of the layer to create.

<a name="documentFilter">

### documentFilter

Default value: `true`

Type: [Expression](../converter/alvisnlp.corpus.expressions.Expression)

Only process document that satisfy this filter.

<a name="sectionFilter">

### sectionFilter

Default value: `true`

Type: [Expression](../converter/alvisnlp.corpus.expressions.Expression)

Process only sections that satisfy this filter.

