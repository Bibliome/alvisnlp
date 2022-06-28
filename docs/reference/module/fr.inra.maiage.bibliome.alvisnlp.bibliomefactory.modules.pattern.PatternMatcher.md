<h1 class="module">PatternMatcher</h1>

## Synopsis

Matches a regular expression-like pattern on the sequence of annotations in a given layer.

## Description

*PatternMatcher* searches for <a href="#pattern" class="param">pattern</a> on the sequence of annotations in layer <a href="#layerName" class="param">layerName</a>. Note that in a layer, annotations are sorted in increasing order of start boundary, then decreasing order of end boundary; the order is undefined for annotations with the exact same span.

For each match, *PatternMatcher* applies all actions specified by <a href="#actions" class="param">actions</a>. Each action concerns a sub-group of the pattern, if no sub-group is specified then the action applies to the whole match.

## Mandatory parameters

<h3 name="actions" class="param">actions</h3>

<div class="param-level param-level-mandatory">Mandatory
</div>
<div class="param-type">Type: <a href="../converter/fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.pattern.action.MatchAction%5B%5D" class="converter">MatchAction[]</a>
</div>
Actions to perform each time the pattern is matched on the annotation sequence. See <a href="../converter/MatchActionArray" class="converter">MatchActionArray</a> for all available actions.

<h3 name="pattern" class="param">pattern</h3>

<div class="param-level param-level-mandatory">Mandatory
</div>
<div class="param-type">Type: <a href="../converter/fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.pattern.ElementPattern" class="converter">ElementPattern</a>
</div>
Pattern to match see <a href="../converter/ElementPattern" class="converter">ElementPattern</a> for pattern syntax.

## Optional parameters

<h3 name="constantAnnotationFeatures" class="param">constantAnnotationFeatures</h3>

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/fr.inra.maiage.bibliome.alvisnlp.core.module.types.Mapping" class="converter">Mapping</a>
</div>
Constant features to add to each annotation created by this module.

<h3 name="constantRelationFeatures" class="param">constantRelationFeatures</h3>

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/fr.inra.maiage.bibliome.alvisnlp.core.module.types.Mapping" class="converter">Mapping</a>
</div>
Constant features to add to each relation created by this module.

<h3 name="constantTupleFeatures" class="param">constantTupleFeatures</h3>

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/fr.inra.maiage.bibliome.alvisnlp.core.module.types.Mapping" class="converter">Mapping</a>
</div>
Constant features to add to each tuple created by this module.

<h3 name="annotationComparator" class="param">annotationComparator</h3>

<div class="param-level param-level-default-value">Default value: `length`
</div>
<div class="param-type">Type: <a href="../converter/fr.inra.maiage.bibliome.alvisnlp.core.corpus.AnnotationComparator" class="converter">AnnotationComparator</a>
</div>
Comparator to use when removing overlaps.

<h3 name="documentFilter" class="param">documentFilter</h3>

<div class="param-level param-level-default-value">Default value: `true`
</div>
<div class="param-type">Type: <a href="../converter/fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.Expression" class="converter">Expression</a>
</div>
Only process document that satisfy this expression.

<h3 name="layerName" class="param">layerName</h3>

<div class="param-level param-level-default-value">Default value: `words`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Match the pattern on the annotations contained in this layer.

<h3 name="overlappingBehaviour" class="param">overlappingBehaviour</h3>

<div class="param-level param-level-default-value">Default value: `remove`
</div>
<div class="param-type">Type: <a href="../converter/fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.pattern.OverlappingBehaviour" class="converter">OverlappingBehaviour</a>
</div>
What to do if the layer contains overlapping annotations.

<h3 name="sectionFilter" class="param">sectionFilter</h3>

<div class="param-level param-level-default-value">Default value: `boolean:and(true, nav:layer:words())`
</div>
<div class="param-type">Type: <a href="../converter/fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.Expression" class="converter">Expression</a>
</div>
Process only sections that satisfy this expression.

