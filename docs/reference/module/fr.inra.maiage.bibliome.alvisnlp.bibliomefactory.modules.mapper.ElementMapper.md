<h1 class="module">ElementMapper</h1>

## Synopsis

Maps elements according to a collection of mapping elements.

**This module is experimental.**

## Description

*ElementMapper* evaluates <a href="#entries" class="param">entries</a> as a list of elements with the corpus as the context element. These elements represent the entries from which target elements are matched. <a href="#key" class="param">key</a> specifies the key of each entry, and <a href="#values" class="param">values</a> specifies the values of the entries. <a href="#target" class="param">target</a> specifies the mapped elements; the <a href="#form" class="param">form</a> is matched against the key of entries. If the target element matches, *ElementMapper* adds the features <a href="#targetFeatures" class="param">targetFeatures</a> with the matched entry values.

## Mandatory parameters

<h3 name="entries" class="param">entries</h3>

<div class="param-level param-level-mandatory">Mandatory
</div>
<div class="param-type">Type: <a href="../converter/fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.Expression" class="converter">Expression</a>
</div>
Expression evaluated as a list of elements with the corpus as the context element. Each element represents an entry.

<h3 name="form" class="param">form</h3>

<div class="param-level param-level-mandatory">Mandatory
</div>
<div class="param-type">Type: <a href="../converter/fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.Expression" class="converter">Expression</a>
</div>
Expression evaluated as a string with the target element as context that specifies the target key.

<h3 name="key" class="param">key</h3>

<div class="param-level param-level-mandatory">Mandatory
</div>
<div class="param-type">Type: <a href="../converter/fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.Expression" class="converter">Expression</a>
</div>
Expression evaluated as a string with the entry element as the context. The result determines the entry key.

<h3 name="target" class="param">target</h3>

<div class="param-level param-level-mandatory">Mandatory
</div>
<div class="param-type">Type: <a href="../converter/fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.Expression" class="converter">Expression</a>
</div>
Expression evaluated as a list of elements with the corpus as context that specify the elements to be mapped.

<h3 name="targetFeatures" class="param">targetFeatures</h3>

<div class="param-level param-level-mandatory">Mandatory
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String%5B%5D" class="converter">String[]</a>
</div>
Name of the features where to store the values of matched entries.

<h3 name="values" class="param">values</h3>

<div class="param-level param-level-mandatory">Mandatory
</div>
<div class="param-type">Type: <a href="../converter/fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.Expression%5B%5D" class="converter">Expression[]</a>
</div>
Expressions evaluated as strings with the entry element as the context. The results specify the feature values of targets that match the entry key.

## Optional parameters

<h3 name="ignoreCase" class="param">ignoreCase</h3>

<div class="param-level param-level-default-value">Default value: `false`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.Boolean" class="converter">Boolean</a>
</div>
Either to match ignoring the case.

<h3 name="operator" class="param">operator</h3>

<div class="param-level param-level-default-value">Default value: `exact`
</div>
<div class="param-type">Type: <a href="../converter/fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.mapper.MappingOperator" class="converter">MappingOperator</a>
</div>
Matching operator: either *exact* (default) or *prefix*.

