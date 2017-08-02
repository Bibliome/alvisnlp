<h1 class="module">EnjuParser</h1>

## Synopsis

synopsis

## Description

synopsis

## Parameters

<a name="enjuExecutable">

### enjuExecutable

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/org.bibliome.util.files.ExecutableFile" class="converter">ExecutableFile</a>
</div>


<a name="constantRelationFeatures">

### constantRelationFeatures

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/alvisnlp.module.types.Mapping" class="converter">Mapping</a>
</div>
Constant features to add to each relation created by this module

<a name="constantTupleFeatures">

### constantTupleFeatures

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/alvisnlp.module.types.Mapping" class="converter">Mapping</a>
</div>
Constant features to add to each tuple created by this module

<a name="biology">

### biology

<div class="param-level param-level-default-value">Default value: `false`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.Boolean" class="converter">Boolean</a>
</div>


<a name="dependenciesRelationName">

### dependenciesRelationName

<div class="param-level param-level-default-value">Default value: `dependencies`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>


<a name="dependencyDependentRole">

### dependencyDependentRole

<div class="param-level param-level-default-value">Default value: `dependent`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>


<a name="dependencyHeadRole">

### dependencyHeadRole

<div class="param-level param-level-default-value">Default value: `head`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>


<a name="dependencyLabelFeatureName">

### dependencyLabelFeatureName

<div class="param-level param-level-default-value">Default value: `label`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>


<a name="dependentTypeFeatureName">

### dependentTypeFeatureName

<div class="param-level param-level-default-value">Default value: `arg-type`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>


<a name="documentFilter">

### documentFilter

<div class="param-level param-level-default-value">Default value: `true`
</div>
<div class="param-type">Type: <a href="../converter/alvisnlp.corpus.expressions.Expression" class="converter">Expression</a>
</div>
Only process document that satisfy this filter.

<a name="enjuEncoding">

### enjuEncoding

<div class="param-level param-level-default-value">Default value: `UTF-8`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>


<a name="nBest">

### nBest

<div class="param-level param-level-default-value">Default value: `1`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.Integer" class="converter">Integer</a>
</div>


<a name="parseNumberFeatureName">

### parseNumberFeatureName

<div class="param-level param-level-default-value">Default value: `parse`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>


<a name="parseStatusFeatureName">

### parseStatusFeatureName

<div class="param-level param-level-default-value">Default value: `parse-status`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>


<a name="posFeatureName">

### posFeatureName

<div class="param-level param-level-default-value">Default value: `pos`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>


<a name="sectionFilter">

### sectionFilter

<div class="param-level param-level-default-value">Default value: `boolean:and(true, boolean:and(nav:layer:sentences(), nav:layer:words()))`
</div>
<div class="param-type">Type: <a href="../converter/alvisnlp.corpus.expressions.Expression" class="converter">Expression</a>
</div>
Process only sections that satisfy this filter.

<a name="sentenceFilter">

### sentenceFilter

<div class="param-level param-level-default-value">Default value: `true`
</div>
<div class="param-type">Type: <a href="../converter/alvisnlp.corpus.expressions.Expression" class="converter">Expression</a>
</div>


<a name="sentenceLayerName">

### sentenceLayerName

<div class="param-level param-level-default-value">Default value: `sentences`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>


<a name="sentenceRole">

### sentenceRole

<div class="param-level param-level-default-value">Default value: `sentence`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>


<a name="wordFormFeatureName">

### wordFormFeatureName

<div class="param-level param-level-default-value">Default value: `form`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>


<a name="wordLayerName">

### wordLayerName

<div class="param-level param-level-default-value">Default value: `words`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>


