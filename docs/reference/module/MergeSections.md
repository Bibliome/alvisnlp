<h1 class="module">MergeSections</h1>

## Synopsis

Merge several sections into a single one.

**This module is experimental.**

## Description

*MergeSections* creates a section named <a href="#targetSectionName" class="param">targetSectionName</a> that is a concatenation of all sections that satisfy <a href="#sectionFilter" class="param">sectionFilter</a>. Layers, annotations, relations and tuples of the source sections are copied to the new section. Additionally, *MergeSections* can select or strip contents from annotations from <a href="#fragmentLayerName" class="param">fragmentLayerName</a>.

## Parameters

<a name="targetSectionName">

### targetSectionName

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Name of the section to create.

<a name="constantAnnotationFeatures">

### constantAnnotationFeatures

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/alvisnlp.module.types.Mapping" class="converter">Mapping</a>
</div>
Constant features to add to each annotation created by this module

<a name="constantRelationFeatures">

### constantRelationFeatures

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/alvisnlp.module.types.Mapping" class="converter">Mapping</a>
</div>
Constant features to add to each relation created by this module

<a name="constantSectionFeatures">

### constantSectionFeatures

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/alvisnlp.module.types.Mapping" class="converter">Mapping</a>
</div>
Constant features to add to each section created by this module

<a name="constantTupleFeatures">

### constantTupleFeatures

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/alvisnlp.module.types.Mapping" class="converter">Mapping</a>
</div>
Constant features to add to each tuple created by this module

<a name="fragmentLayerName">

### fragmentLayerName

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Name of the layer that contains annotations to include/exclude in/from the new section contents. If this parameter is not set, then *MergeSections* concatenates the whole contents of the sections.

<a name="sectionsLayerName">

### sectionsLayerName

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Name of the layer in the new section that contains annotations that have the span of the contents of the source sections. Each source section is represented by a distinct annotation. This annotations have the same features as the corresponding section (including `name`). If this parameter is not set, then *MergeSections* does not create thses annotations.

<a name="documentFilter">

### documentFilter

<div class="param-level param-level-default-value">Default value: `true`
</div>
<div class="param-type">Type: <a href="../converter/alvisnlp.corpus.expressions.Expression" class="converter">Expression</a>
</div>
Only process document that satisfy this filter.

<a name="fragmentSelection">

### fragmentSelection

<div class="param-level param-level-default-value">Default value: `exclude`
</div>
<div class="param-type">Type: <a href="../converter/org.bibliome.alvisnlp.modules.clone.FragmentSelection" class="converter">FragmentSelection</a>
</div>
If this parameter equals `include`, then *MergeSections* only concatenates contents that is included in annotations in the layer <a href="#fragmentLayerName" class="param">fragmentLayerName</a>. If this parameter equals `exclude`, then *MergeSections* only concatenates contents that is *not* included in annotations in the layer <a href="#fragmentLayerName" class="param">fragmentLayerName</a>. If <a href="#fragmentLayerName" class="param">fragmentLayerName</a> is not set, then this parameter is ignored.

<a name="fragmentSeparator">

### fragmentSeparator

<div class="param-level param-level-default-value">Default value: ``
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Text to insert between the contents of concatenated fragments. If <a href="#fragmentLayerName" class="param">fragmentLayerName</a> is not set, then this parameter is ignored.

<a name="removeSections">

### removeSections

<div class="param-level param-level-default-value">Default value: `true`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.Boolean" class="converter">Boolean</a>
</div>
Either to remove the sections that have been concatenated after the new section has been created.

<a name="sectionFilter">

### sectionFilter

<div class="param-level param-level-default-value">Default value: `true`
</div>
<div class="param-type">Type: <a href="../converter/alvisnlp.corpus.expressions.Expression" class="converter">Expression</a>
</div>
Process only sections that satisfy this filter.

<a name="sectionSeparator">

### sectionSeparator

<div class="param-level param-level-default-value">Default value: ``
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Text to insert between the contents of the concatenated sections.

