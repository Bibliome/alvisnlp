<h1 class="module">ElementMapper</h1>

## Synopsis

Maps elements according to a collection of mapping elements.

**This module is experimental.**

## Description

*ElementMapper* evaluates <a href="#entries" class="param">entries</a> as a list of elements with the corpus as the context element. These elements represent the entries from which target elements are matched. <a href="#key" class="param">key</a> specifies the key of each entry, and <a href="#values" class="param">values</a> specifies the values of the entries. <a href="#target" class="param">target</a> specifies the mapped elements; the <a href="#form" class="param">form</a> is matched against the key of entries. If the target element matches, *ElementMapper* adds the features <a href="#targetFeatures" class="param">targetFeatures</a> with the matched entry values.

## Parameters

<a name="entries">

### entries

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/alvisnlp.corpus.expressions.Expression" class="converter">Expression</a>
</div>
Expression evaluated as a list of elements with the corpus as the context element. Each element represents an entry.

<a name="form">

### form

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/alvisnlp.corpus.expressions.Expression" class="converter">Expression</a>
</div>
Expression evaluated as a string with the target element as context that specifies the target key.

<a name="key">

### key

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/alvisnlp.corpus.expressions.Expression" class="converter">Expression</a>
</div>
Expression evaluated as a string with the entry element as the context. The result determines the entry key.

<a name="target">

### target

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/alvisnlp.corpus.expressions.Expression" class="converter">Expression</a>
</div>
Expression evaluated as a list of elements with the corpus as the context element. The result determines the elements to be mapped.

<a name="targetFeatures">

### targetFeatures

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String[]" class="converter">String[]</a>
</div>
Name of the features where to store the values of matched entries.

<a name="values">

### values

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/alvisnlp.corpus.expressions.Expression[]" class="converter">Expression[]</a>
</div>
Expressions evaluated as strings with the entry element as the context. The results specify the feature values of targets that match the entry key.

<a name="ignoreCase">

### ignoreCase

<div class="param-level param-level-default-value">Default value: `false`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.Boolean" class="converter">Boolean</a>
</div>
Either to match ignoring the case.

<a name="operator">

### operator

<div class="param-level param-level-default-value">Default value: `exact`
</div>
<div class="param-type">Type: <a href="../converter/org.bibliome.alvisnlp.modules.mapper.MappingOperator" class="converter">MappingOperator</a>
</div>
Matching operator.

