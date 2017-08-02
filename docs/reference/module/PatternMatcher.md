<h1 class="module">PatternMatcher</h1>

## Synopsis

Matches a regular expression-like pattern on the sequence of annotations in a given layer.

## Description

*PatternMatcher* searches for <a href="#pattern" class="param">pattern</a> on the sequence of annotations in layer <a href="#layerName" class="param">layerName</a>. Note that in a layer, annotations are sorted in increasing order of start boundary, then decreasing order of end boundary; the order is undefined for annotations with the exact same span.

For each match, *PatternMatcher* applies all actions specified by <a href="#actions" class="param">actions</a>. Each action concerns a sub-group of the pattern, if no sub-group is specified then the action applies to the whole match.

## Parameters

<a name="actions">

### actions

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/org.bibliome.alvisnlp.modules.pattern.action.MatchAction[]" class="converter">MatchAction[]</a>
</div>
Actions to perform each time the pattern is matched on the annotation sequence. See <a href="../converter/MatchActionArray" class="converter">MatchActionArray</a> for all available actions.

<a name="pattern">

### pattern

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/org.bibliome.alvisnlp.modules.pattern.ElementPattern" class="converter">ElementPattern</a>
</div>
Pattern to match see <a href="../converter/ElementPattern" class="converter">ElementPattern</a> for pattern syntax.

<a name="constantAnnotationFeatures">

### constantAnnotationFeatures

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/alvisnlp.module.types.Mapping" class="converter">Mapping</a>
</div>
Features to add to all annotations created by this module, these features are added for all actions that create an annotation.

<a name="constantRelationFeatures">

### constantRelationFeatures

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/alvisnlp.module.types.Mapping" class="converter">Mapping</a>
</div>
Features to add to all relations created by this module, these features are added for all realtions that have been created by an action that creates a tuple.

<a name="constantTupleFeatures">

### constantTupleFeatures

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/alvisnlp.module.types.Mapping" class="converter">Mapping</a>
</div>
Features to add to all tuples created by this module, these features are added for all actions that create a tuple.

<a name="annotationComparator">

### annotationComparator

<div class="param-level param-level-default-value">Default value: `length`
</div>
<div class="param-type">Type: <a href="../converter/alvisnlp.corpus.AnnotationComparator" class="converter">AnnotationComparator</a>
</div>
Comparator to use when removing overlaps.

<a name="documentFilter">

### documentFilter

<div class="param-level param-level-default-value">Default value: `true`
</div>
<div class="param-type">Type: <a href="../converter/alvisnlp.corpus.expressions.Expression" class="converter">Expression</a>
</div>
Only process documents that satisfy this filter.

<a name="layerName">

### layerName

<div class="param-level param-level-default-value">Default value: `words`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Match the pattern on the annotations contained in this layer.

<a name="overlappingBehaviour">

### overlappingBehaviour

<div class="param-level param-level-default-value">Default value: `remove`
</div>
<div class="param-type">Type: <a href="../converter/org.bibliome.alvisnlp.modules.pattern.OverlappingBehaviour" class="converter">OverlappingBehaviour</a>
</div>
What to do if the layer contains overlapping annotations.

<a name="sectionFilter">

### sectionFilter

<div class="param-level param-level-default-value">Default value: `boolean:and(true, nav:layer:words())`
</div>
<div class="param-type">Type: <a href="../converter/alvisnlp.corpus.expressions.Expression" class="converter">Expression</a>
</div>
Only process sections that satisfy this filter.

