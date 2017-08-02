# org.bibliome.alvisnlp.modules.pattern.PatternMatcher

## Synopsis

Matches a regular expression-like pattern on the sequence of annotations in a given layer.

## Description

*org.bibliome.alvisnlp.modules.pattern.PatternMatcher* searches for [pattern](#pattern) on the sequence of annotations in layer [layerName](#layerName). Note that in a layer, annotations are sorted in increasing order of start boundary, then decreasing order of end boundary; the order is undefined for annotations with the exact same span.

For each match, *org.bibliome.alvisnlp.modules.pattern.PatternMatcher* applies all actions specified by [actions](#actions). Each action concerns a sub-group of the pattern, if no sub-group is specified then the action applies to the whole match.

## Parameters

<a name="actions">

### actions

Optional

Type: [MatchAction[]](../converter/org.bibliome.alvisnlp.modules.pattern.action.MatchAction[])

Actions to perform each time the pattern is matched on the annotation sequence. See [MatchActionArray](../converter/MatchActionArray) for all available actions.

<a name="pattern">

### pattern

Optional

Type: [ElementPattern](../converter/org.bibliome.alvisnlp.modules.pattern.ElementPattern)

Pattern to match see [ElementPattern](../converter/ElementPattern) for pattern syntax.

<a name="constantAnnotationFeatures">

### constantAnnotationFeatures

Optional

Type: [Mapping](../converter/alvisnlp.module.types.Mapping)

Features to add to all annotations created by this module, these features are added for all actions that create an annotation.

<a name="constantRelationFeatures">

### constantRelationFeatures

Optional

Type: [Mapping](../converter/alvisnlp.module.types.Mapping)

Features to add to all relations created by this module, these features are added for all realtions that have been created by an action that creates a tuple.

<a name="constantTupleFeatures">

### constantTupleFeatures

Optional

Type: [Mapping](../converter/alvisnlp.module.types.Mapping)

Features to add to all tuples created by this module, these features are added for all actions that create a tuple.

<a name="annotationComparator">

### annotationComparator

Default value: `length`

Type: [AnnotationComparator](../converter/alvisnlp.corpus.AnnotationComparator)

Comparator to use when removing overlaps.

<a name="documentFilter">

### documentFilter

Default value: `true`

Type: [Expression](../converter/alvisnlp.corpus.expressions.Expression)

Only process documents that satisfy this filter.

<a name="layerName">

### layerName

Default value: `words`

Type: [String](../converter/java.lang.String)

Match the pattern on the annotations contained in this layer.

<a name="overlappingBehaviour">

### overlappingBehaviour

Default value: `remove`

Type: [OverlappingBehaviour](../converter/org.bibliome.alvisnlp.modules.pattern.OverlappingBehaviour)

What to do if the layer contains overlapping annotations.

<a name="sectionFilter">

### sectionFilter

Default value: `boolean:and(true, nav:layer:words())`

Type: [Expression](../converter/alvisnlp.corpus.expressions.Expression)

Only process sections that satisfy this filter.

