# org.bibliome.alvisnlp.modules.RemoveOverlaps

## Synopsis

Removes overlapping annotations from a given layer.

## Description

*org.bibliome.alvisnlp.modules.RemoveOverlaps* removes overlapping annotations in the layer [layerName](#layerName).

*org.bibliome.alvisnlp.modules.RemoveOverlaps* scans each specified layer and finds clusters of overlapping annotations.
	  	*org.bibliome.alvisnlp.modules.RemoveOverlaps* distinguishes three overlapping situations:
	  	
1. *equal*: two annotations have exactly the same span;
2. *included*: one annotation includes the other completely;
3. *overlapping*: one annotation overlaps with the head or the tail of the other annotation.



[removeEqual](#removeEqual), [removeIncluded](#removeIncluded) and [removeOverlapping](#removeOverlapping) specify the behavior for each situation.
	  If the parameter is `true`, then *org.bibliome.alvisnlp.modules.RemoveOverlaps* will remove one of the annotations. 
	  

[annotationComparator](#annotationComparator) controls which annotation is removed.
	  


	  By default *org.bibliome.alvisnlp.modules.RemoveOverlaps* removes all kinds of annotations, keeping the longest one.
	  

## Parameters

<a name="layerName">

### layerName

Optional

Type: [String](../converter/java.lang.String)

Name of the layer to clear.

<a name="annotationComparator">

### annotationComparator

Default value: `length`

Type: [AnnotationComparator](../converter/alvisnlp.corpus.AnnotationComparator)

Comparator to use in order to choose between overlapping annotations.

<a name="documentFilter">

### documentFilter

Default value: `true`

Type: [Expression](../converter/alvisnlp.corpus.expressions.Expression)

Only process document that satisfy this filter.

<a name="removeEqual">

### removeEqual

Default value: `true`

Type: [Boolean](../converter/java.lang.Boolean)

Either to remove annotations with equal spans.

<a name="removeIncluded">

### removeIncluded

Default value: `true`

Type: [Boolean](../converter/java.lang.Boolean)

Either to remove annotations fully included in another annotation.

<a name="removeOverlapping">

### removeOverlapping

Default value: `true`

Type: [Boolean](../converter/java.lang.Boolean)

Either to remove strictly overlapping annotations.

<a name="sectionFilter">

### sectionFilter

Default value: `true`

Type: [Expression](../converter/alvisnlp.corpus.expressions.Expression)

Process only sections that satisfy this filter.

