# org.bibliome.alvisnlp.modules.SplitOverlaps

## Synopsis

Splits overlapping annotations.

**This module is experimental.**

## Description

*org.bibliome.alvisnlp.modules.SplitOverlaps* copies annotations from [checkedlayerNames](#checkedlayerNames) into [modifiedlayerName](#modifiedlayerName) and ensures that this layer does not contain overlaping annotations. If [checkedlayerNames](#checkedlayerNames) contains overlaps, then *org.bibliome.alvisnlp.modules.SplitOverlaps* splits annotations at the start or end positions of overlapping annotations.


* Features copied?
* Non-split annotations deep-copied?
* Split embedded annotations?

## Parameters

<a name="checkedlayerNames">

### checkedlayerNames

Optional

Type: [String[]](../converter/java.lang.String[])

Source layers.

<a name="modifiedlayerName">

### modifiedlayerName

Optional

Type: [String](../converter/java.lang.String)

Target layer.

<a name="indexFeatureName">

### indexFeatureName

Optional

Type: [String](../converter/java.lang.String)

Feature where to store the index of the split annotations.

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

