<h1 class="module">FileMapper</h1>

## Synopsis

Maps elements according to a tab-separated mapping file.

## Description

 *FileMapper* reads the tab-separated mapping file <a href="#mappingFile" class="param">mappingFile</a> and maps the elements specified by the expression <a href="#target" class="param">target</a> . The mapping key of the elements is specified by the expression <a href="#form" class="param">form</a> evaluated as a string with the element as the context. The key is mapped agains the column of <a href="#mappingFile" class="param">mappingFile</a> specified by <a href="#keyColumn" class="param">keyColumn</a> (starting at 0). Each feature in <a href="#targetFeatures" class="param">targetFeatures</a> is set with the value of the corresponding column in the matched line.

## Snippet



<button class="copy-code-button" title="Copy to clipboard" onclick="copy_code(this)">📋</button>
```xml
<filemapper class="FileMapper>
    <form></form>
    <mappingFile></mappingFile>
    <target></target>
</filemapper>
```

## Mandatory parameters

<h3 id="form" class="param">form</h3>

<div class="param-level param-level-mandatory">Mandatory
</div>
<div class="param-type">Type: <a href="../converter/fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.Expression" class="converter">Expression</a>
</div>
Expression evaluated as a string with the target element as context that specifies the target key.

<h3 id="mappingFile" class="param">mappingFile</h3>

<div class="param-level param-level-mandatory">Mandatory
</div>
<div class="param-type">Type: <a href="../converter/fr.inra.maiage.bibliome.util.streams.SourceStream" class="converter">SourceStream</a>
</div>
Tab-separated file containing the dictionary.

<h3 id="target" class="param">target</h3>

<div class="param-level param-level-mandatory">Mandatory
</div>
<div class="param-type">Type: <a href="../converter/fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.Expression" class="converter">Expression</a>
</div>
Expression evaluated as a list of elements with the corpus as context that specify the elements to be mapped.

## Optional parameters

<h3 id="targetFeatures" class="param">targetFeatures</h3>

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String%5B%5D" class="converter">String[]</a>
</div>
Name of the features where to store the columns of matched lines. Ignored if <a href="#headerLine" class="param">headerLine</a> is set.

<h3 id="headerLine" class="param">headerLine</h3>

<div class="param-level param-level-default-value">Default value: `false`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.Boolean" class="converter">Boolean</a>
</div>
Assumes the first line of the dictionary is a header, target feature names will be the column names read from the header.

<h3 id="ignoreCase" class="param">ignoreCase</h3>

<div class="param-level param-level-default-value">Default value: `false`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.Boolean" class="converter">Boolean</a>
</div>
Either to match ignoring the case.

<h3 id="keyColumn" class="param">keyColumn</h3>

<div class="param-level param-level-default-value">Default value: `0`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.Integer" class="converter">Integer</a>
</div>
Index of the line key in <a href="#mappingFile" class="param">mappingFile</a> .

<h3 id="operator" class="param">operator</h3>

<div class="param-level param-level-default-value">Default value: `exact`
</div>
<div class="param-type">Type: <a href="../converter/fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.mapper.MappingOperator" class="converter">MappingOperator</a>
</div>
Matching operator: either *exact* (default) or *prefix*.

<h3 id="separator" class="param">separator</h3>

<div class="param-level param-level-default-value">Default value: `	`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.Character" class="converter">Character</a>
</div>
Separator character between columns in <a href="#mappingFile" class="param">mappingFile</a> .

