# org.bibliome.alvisnlp.modules.clone.MergeSections

## Synopsis

Merge several sections into a single one.

**This module is experimental.**

## Description

*org.bibliome.alvisnlp.modules.clone.MergeSections* creates a section named [targetSectionName](#targetSectionName) that is a concatenation of all sections that satisfy [sectionFilter](#sectionFilter). Layers, annotations, relations and tuples of the source sections are copied to the new section. Additionally, *org.bibliome.alvisnlp.modules.clone.MergeSections* can select or strip contents from annotations from [fragmentLayerName](#fragmentLayerName).

## Parameters

<a name="targetSectionName">

### targetSectionName

Optional

Type: [String](../converter/java.lang.String)

Name of the section to create.

<a name="constantAnnotationFeatures">

### constantAnnotationFeatures

Optional

Type: [Mapping](../converter/alvisnlp.module.types.Mapping)

Constant features to add to each annotation created by this module

<a name="constantRelationFeatures">

### constantRelationFeatures

Optional

Type: [Mapping](../converter/alvisnlp.module.types.Mapping)

Constant features to add to each relation created by this module

<a name="constantSectionFeatures">

### constantSectionFeatures

Optional

Type: [Mapping](../converter/alvisnlp.module.types.Mapping)

Constant features to add to each section created by this module

<a name="constantTupleFeatures">

### constantTupleFeatures

Optional

Type: [Mapping](../converter/alvisnlp.module.types.Mapping)

Constant features to add to each tuple created by this module

<a name="fragmentLayerName">

### fragmentLayerName

Optional

Type: [String](../converter/java.lang.String)

Name of the layer that contains annotations to include/exclude in/from the new section contents. If this parameter is not set, then *org.bibliome.alvisnlp.modules.clone.MergeSections* concatenates the whole contents of the sections.

<a name="sectionsLayerName">

### sectionsLayerName

Optional

Type: [String](../converter/java.lang.String)

Name of the layer in the new section that contains annotations that have the span of the contents of the source sections. Each source section is represented by a distinct annotation. This annotations have the same features as the corresponding section (including `name`). If this parameter is not set, then *org.bibliome.alvisnlp.modules.clone.MergeSections* does not create thses annotations.

<a name="documentFilter">

### documentFilter

Default value: `true`

Type: [Expression](../converter/alvisnlp.corpus.expressions.Expression)

Only process document that satisfy this filter.

<a name="fragmentSelection">

### fragmentSelection

Default value: `exclude`

Type: [FragmentSelection](../converter/org.bibliome.alvisnlp.modules.clone.FragmentSelection)

If this parameter equals `include`, then *org.bibliome.alvisnlp.modules.clone.MergeSections* only concatenates contents that is included in annotations in the layer [fragmentLayerName](#fragmentLayerName). If this parameter equals `exclude`, then *org.bibliome.alvisnlp.modules.clone.MergeSections* only concatenates contents that is *not* included in annotations in the layer [fragmentLayerName](#fragmentLayerName). If [fragmentLayerName](#fragmentLayerName) is not set, then this parameter is ignored.

<a name="fragmentSeparator">

### fragmentSeparator

Default value: ``

Type: [String](../converter/java.lang.String)

Text to insert between the contents of concatenated fragments. If [fragmentLayerName](#fragmentLayerName) is not set, then this parameter is ignored.

<a name="removeSections">

### removeSections

Default value: `true`

Type: [Boolean](../converter/java.lang.Boolean)

Either to remove the sections that have been concatenated after the new section has been created.

<a name="sectionFilter">

### sectionFilter

Default value: `true`

Type: [Expression](../converter/alvisnlp.corpus.expressions.Expression)

Process only sections that satisfy this filter.

<a name="sectionSeparator">

### sectionSeparator

Default value: ``

Type: [String](../converter/java.lang.String)

Text to insert between the contents of the concatenated sections.

