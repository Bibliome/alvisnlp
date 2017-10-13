<h1 class="module">RemoveOverlaps</h1>

## Synopsis

Removes overlapping annotations from a given layer.

## Description

*RemoveOverlaps* removes overlapping annotations in the layer <a href="#layerName" class="param">layerName</a>.

*RemoveOverlaps* scans each specified layer and finds clusters of overlapping annotations.
	  	*RemoveOverlaps* distinguishes three overlapping situations:
	  	
1. *equal*: two annotations have exactly the same span;
2. *included*: one annotation includes the other completely;
3. *overlapping*: one annotation overlaps with the head or the tail of the other annotation.



<a href="#removeEqual" class="param">removeEqual</a>, <a href="#removeIncluded" class="param">removeIncluded</a> and <a href="#removeOverlapping" class="param">removeOverlapping</a> specify the behavior for each situation.
	  If the parameter is `true`, then *RemoveOverlaps* will remove one of the annotations. 
	  

<a href="#annotationComparator" class="param">annotationComparator</a> controls which annotation is removed.
	  


	  By default *RemoveOverlaps* removes all kinds of annotations, keeping the longest one.
	  

## Parameters

<a name="layerName">

### layerName

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Name of the layer to clear.

<a name="annotationComparator">

### annotationComparator

<div class="param-level param-level-default-value">Default value: `length`
</div>
<div class="param-type">Type: <a href="../converter/fr.inra.maiage.bibliome.alvisnlp.core.corpus.AnnotationComparator" class="converter">AnnotationComparator</a>
</div>
Comparator to use in order to choose between overlapping annotations.

<a name="documentFilter">

### documentFilter

<div class="param-level param-level-default-value">Default value: `true`
</div>
<div class="param-type">Type: <a href="../converter/fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.Expression" class="converter">Expression</a>
</div>
Only process document that satisfy this filter.

<a name="removeEqual">

### removeEqual

<div class="param-level param-level-default-value">Default value: `true`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.Boolean" class="converter">Boolean</a>
</div>
Either to remove annotations with equal spans.

<a name="removeIncluded">

### removeIncluded

<div class="param-level param-level-default-value">Default value: `true`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.Boolean" class="converter">Boolean</a>
</div>
Either to remove annotations fully included in another annotation.

<a name="removeOverlapping">

### removeOverlapping

<div class="param-level param-level-default-value">Default value: `true`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.Boolean" class="converter">Boolean</a>
</div>
Either to remove strictly overlapping annotations.

<a name="sectionFilter">

### sectionFilter

<div class="param-level param-level-default-value">Default value: `true`
</div>
<div class="param-type">Type: <a href="../converter/fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.Expression" class="converter">Expression</a>
</div>
Process only sections that satisfy this filter.

