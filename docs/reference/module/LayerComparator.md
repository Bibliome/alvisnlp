# org.bibliome.alvisnlp.modules.LayerComparator

## Synopsis

Compares annotations in two different layers.

## Description

*org.bibliome.alvisnlp.modules.LayerComparator* traverses the annotations in the layers [predictedLayerName](#predictedLayerName) and [referenceLayerName](#referenceLayerName) in each section. This is useful when analyzing the result of named entity recognition systems. The result of the comparison is written for each section in terms of recall and precision into the file [outFile](#outFile). This file will also point boundary mismatches.

## Parameters

<a name="outFile">

### outFile

Optional

Type: [TargetStream](../converter/org.bibliome.util.streams.TargetStream)

Path to the file where to store results.

<a name="predictedLayerName">

### predictedLayerName

Optional

Type: [String](../converter/java.lang.String)

Name of the source layer.

<a name="referenceLayerName">

### referenceLayerName

Optional

Type: [String](../converter/java.lang.String)

Name of the reference layer.

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

