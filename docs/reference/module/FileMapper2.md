<h1 class="module">FileMapper2</h1>

## Synopsis

Deprecated alias for <a href="../module/FileMapper" class="module">FileMapper</a>.

**This module is obsolete, superceded by org.bibliome.alvisnlp.modules.mapper.FileMapper**

## Description

## Parameters

<a name="form">

### form

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/alvisnlp.corpus.expressions.Expression" class="converter">Expression</a>
</div>
Expression evaluated as a string with the target element as context that specifies the target key.

<a name="mappingFile">

### mappingFile

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/org.bibliome.util.streams.SourceStream" class="converter">SourceStream</a>
</div>
Tab-separated file containing the dictionary.

<a name="target">

### target

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/alvisnlp.corpus.expressions.Expression" class="converter">Expression</a>
</div>
Expression evaluated as a list of elements with the corpus as context that specify the elements to be mapped.

<a name="targetFeatures">

### targetFeatures

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String[]" class="converter">String[]</a>
</div>
Name of the features where to store the columns of matched lines.

<a name="ignoreCase">

### ignoreCase

<div class="param-level param-level-default-value">Default value: `false`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.Boolean" class="converter">Boolean</a>
</div>
Either to match ignoring the case.

<a name="keyColumn">

### keyColumn

<div class="param-level param-level-default-value">Default value: `0`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.Integer" class="converter">Integer</a>
</div>
Index of the line key in <a href="#mappingFile" class="param">mappingFile</a>.

<a name="operator">

### operator

<div class="param-level param-level-default-value">Default value: `exact`
</div>
<div class="param-type">Type: <a href="../converter/org.bibliome.alvisnlp.modules.mapper.MappingOperator" class="converter">MappingOperator</a>
</div>
Matching operator.

<a name="separator">

### separator

<div class="param-level param-level-default-value">Default value: `	`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.Character" class="converter">Character</a>
</div>
Separator character between columns in <a href="#mappingFile" class="param">mappingFile</a>.

