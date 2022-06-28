<h1 class="module">MergeSections</h1>

## Synopsis

Merge several sections into a single one.

**This module is experimental.**

## Description

*MergeSections* creates a section named <a href="#targetSectionName" class="param">targetSectionName</a> that is a concatenation of all sections that satisfy <a href="#sectionFilter" class="param">sectionFilter</a>. Layers, annotations, relations and tuples of the source sections are copied to the new section. Additionally, *MergeSections* can select or strip contents from annotations from <a href="#fragmentLayerName" class="param">fragmentLayerName</a>.

## Mandatory parameters

<h3 name="targetSectionName" class="param">targetSectionName</h3>

<div class="param-level param-level-mandatory">Mandatory
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Name of the section to create.

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

<h3 name="constantSectionFeatures" class="param">constantSectionFeatures</h3>

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/fr.inra.maiage.bibliome.alvisnlp.core.module.types.Mapping" class="converter">Mapping</a>
</div>
Constant features to add to each section created by this module.

<h3 name="constantTupleFeatures" class="param">constantTupleFeatures</h3>

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/fr.inra.maiage.bibliome.alvisnlp.core.module.types.Mapping" class="converter">Mapping</a>
</div>
Constant features to add to each tuple created by this module.

<h3 name="fragmentLayerName" class="param">fragmentLayerName</h3>

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Name of the layer that contains annotations to include/exclude in/from the new section contents. If this parameter is not set, then *MergeSections* concatenates the whole contents of the sections.

<h3 name="sectionsLayerName" class="param">sectionsLayerName</h3>

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Name of the layer in the new section that contains annotations that have the span of the contents of the source sections. Each source section is represented by a distinct annotation. This annotations have the same features as the corresponding section (including `name`). If this parameter is not set, then *MergeSections* does not create thses annotations.

<h3 name="documentFilter" class="param">documentFilter</h3>

<div class="param-level param-level-default-value">Default value: `true`
</div>
<div class="param-type">Type: <a href="../converter/fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.Expression" class="converter">Expression</a>
</div>
Only process document that satisfy this expression.

<h3 name="fragmentSelection" class="param">fragmentSelection</h3>

<div class="param-level param-level-default-value">Default value: `exclude`
</div>
<div class="param-type">Type: <a href="../converter/fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.clone.FragmentSelection" class="converter">FragmentSelection</a>
</div>
If this parameter equals `include`, then *MergeSections* only concatenates contents that is included in annotations in the layer <a href="#fragmentLayerName" class="param">fragmentLayerName</a>. If this parameter equals `exclude`, then *MergeSections* only concatenates contents that is *not* included in annotations in the layer <a href="#fragmentLayerName" class="param">fragmentLayerName</a>. If <a href="#fragmentLayerName" class="param">fragmentLayerName</a> is not set, then this parameter is ignored.

<h3 name="fragmentSeparator" class="param">fragmentSeparator</h3>

<div class="param-level param-level-default-value">Default value: ``
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Text to insert between the contents of concatenated fragments. If <a href="#fragmentLayerName" class="param">fragmentLayerName</a> is not set, then this parameter is ignored.

<h3 name="removeSections" class="param">removeSections</h3>

<div class="param-level param-level-default-value">Default value: `true`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.Boolean" class="converter">Boolean</a>
</div>
Either to remove the sections that have been concatenated after the new section has been created.

<h3 name="sectionFilter" class="param">sectionFilter</h3>

<div class="param-level param-level-default-value">Default value: `true`
</div>
<div class="param-type">Type: <a href="../converter/fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.Expression" class="converter">Expression</a>
</div>
Process only sections that satisfy this expression.

<h3 name="sectionSeparator" class="param">sectionSeparator</h3>

<div class="param-level param-level-default-value">Default value: ``
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Text to insert between the contents of the concatenated sections.

