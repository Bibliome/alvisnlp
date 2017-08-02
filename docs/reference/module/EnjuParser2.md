# org.bibliome.alvisnlp.modules.enju.EnjuParser2

## Synopsis

This module is an alias for [EnjuParser](../module/EnjuParser)

**This module is obsolete, superceded by org.bibliome.alvisnlp.modules.enju.EnjuParser**

## Description

synopsis

## Parameters

<a name="enjuExecutable">

### enjuExecutable

Optional

Type: [ExecutableFile](../converter/org.bibliome.util.files.ExecutableFile)



<a name="constantRelationFeatures">

### constantRelationFeatures

Optional

Type: [Mapping](../converter/alvisnlp.module.types.Mapping)

Constant features to add to each relation created by this module

<a name="constantTupleFeatures">

### constantTupleFeatures

Optional

Type: [Mapping](../converter/alvisnlp.module.types.Mapping)

Constant features to add to each tuple created by this module

<a name="biology">

### biology

Default value: `false`

Type: [Boolean](../converter/java.lang.Boolean)



<a name="dependenciesRelationName">

### dependenciesRelationName

Default value: `dependencies`

Type: [String](../converter/java.lang.String)



<a name="dependencyDependentRole">

### dependencyDependentRole

Default value: `dependent`

Type: [String](../converter/java.lang.String)



<a name="dependencyHeadRole">

### dependencyHeadRole

Default value: `head`

Type: [String](../converter/java.lang.String)



<a name="dependencyLabelFeatureName">

### dependencyLabelFeatureName

Default value: `label`

Type: [String](../converter/java.lang.String)



<a name="dependentTypeFeatureName">

### dependentTypeFeatureName

Default value: `arg-type`

Type: [String](../converter/java.lang.String)



<a name="documentFilter">

### documentFilter

Default value: `true`

Type: [Expression](../converter/alvisnlp.corpus.expressions.Expression)

Only process document that satisfy this filter.

<a name="enjuEncoding">

### enjuEncoding

Default value: `UTF-8`

Type: [String](../converter/java.lang.String)



<a name="nBest">

### nBest

Default value: `1`

Type: [Integer](../converter/java.lang.Integer)



<a name="parseNumberFeatureName">

### parseNumberFeatureName

Default value: `parse`

Type: [String](../converter/java.lang.String)



<a name="parseStatusFeatureName">

### parseStatusFeatureName

Default value: `parse-status`

Type: [String](../converter/java.lang.String)



<a name="posFeatureName">

### posFeatureName

Default value: `pos`

Type: [String](../converter/java.lang.String)



<a name="sectionFilter">

### sectionFilter

Default value: `boolean:and(true, boolean:and(nav:layer:sentences(), nav:layer:words()))`

Type: [Expression](../converter/alvisnlp.corpus.expressions.Expression)

Process only sections that satisfy this filter.

<a name="sentenceFilter">

### sentenceFilter

Default value: `true`

Type: [Expression](../converter/alvisnlp.corpus.expressions.Expression)



<a name="sentenceLayerName">

### sentenceLayerName

Default value: `sentences`

Type: [String](../converter/java.lang.String)



<a name="sentenceRole">

### sentenceRole

Default value: `sentence`

Type: [String](../converter/java.lang.String)



<a name="wordFormFeatureName">

### wordFormFeatureName

Default value: `form`

Type: [String](../converter/java.lang.String)



<a name="wordLayerName">

### wordLayerName

Default value: `words`

Type: [String](../converter/java.lang.String)



