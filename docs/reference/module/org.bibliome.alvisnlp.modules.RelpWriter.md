<h1 class="module">RelpWriter</h1>

## Synopsis

Writes the corpus in relp format.

## Description

*RelpWriter* writes the corpus into <a href="#outFilet" class="param">outFilet</a> in relp format.

## Parameters

<a name="outFile">

### outFile

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/org.bibliome.util.streams.TargetStream" class="converter">TargetStream</a>
</div>
File where to write the dependencies.

<a name="linkageNumberFeature">

### linkageNumberFeature

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Feature containing the linkage number to which a dependency belongs.

<a name="dependencyLabelFeature">

### dependencyLabelFeature

<div class="param-level param-level-default-value">Default value: `label`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Feature containing the dependency label.

<a name="dependencyRelation">

### dependencyRelation

<div class="param-level param-level-default-value">Default value: `dependencies`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Name of the dependecy relation.

<a name="dependentForm">

### dependentForm

<div class="param-level param-level-default-value">Default value: `properties:@:form()`
</div>
<div class="param-type">Type: <a href="../converter/alvisnlp.corpus.expressions.Expression" class="converter">Expression</a>
</div>
Expression evaluated as a string with the dependency dependent element as context. The result is the surface form of the dependent.

<a name="dependentRole">

### dependentRole

<div class="param-level param-level-default-value">Default value: `dependent`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Name of the role of the dependent word.

<a name="documentFilter">

### documentFilter

<div class="param-level param-level-default-value">Default value: `true`
</div>
<div class="param-type">Type: <a href="../converter/alvisnlp.corpus.expressions.Expression" class="converter">Expression</a>
</div>
Only process document that satisfy this filter.

<a name="headForm">

### headForm

<div class="param-level param-level-default-value">Default value: `properties:@:form()`
</div>
<div class="param-type">Type: <a href="../converter/alvisnlp.corpus.expressions.Expression" class="converter">Expression</a>
</div>
Expression evaluated as a string with the dependency head element as context. The result is the surface form of the head.

<a name="headRole">

### headRole

<div class="param-level param-level-default-value">Default value: `head`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Name of the role of the head word.

<a name="lemmaForm">

### lemmaForm

<div class="param-level param-level-default-value">Default value: `properties:@:lemma()`
</div>
<div class="param-type">Type: <a href="../converter/alvisnlp.corpus.expressions.Expression" class="converter">Expression</a>
</div>
Expression evaluated as a string with the word element as context. The result is the lemma of the word.

<a name="pmid">

### pmid

<div class="param-level param-level-default-value">Default value: `properties:@:id()`
</div>
<div class="param-type">Type: <a href="../converter/alvisnlp.corpus.expressions.Expression" class="converter">Expression</a>
</div>
Expression evaluated as a string with the document as context. The result is the identifier of the document.

<a name="sectionFilter">

### sectionFilter

<div class="param-level param-level-default-value">Default value: `boolean:and(true, boolean:and(nav:layer:sentences(), nav:layer:words()))`
</div>
<div class="param-type">Type: <a href="../converter/alvisnlp.corpus.expressions.Expression" class="converter">Expression</a>
</div>
Process only sections that satisfy this filter.

<a name="sentenceLayer">

### sentenceLayer

<div class="param-level param-level-default-value">Default value: `sentences`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Name of the layer containing sentence annotations.

<a name="sentenceRole">

### sentenceRole

<div class="param-level param-level-default-value">Default value: `sentence`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Name of the role of the parsed sentence in the dependency relation.

<a name="wordForm">

### wordForm

<div class="param-level param-level-default-value">Default value: `properties:@:form()`
</div>
<div class="param-type">Type: <a href="../converter/alvisnlp.corpus.expressions.Expression" class="converter">Expression</a>
</div>
Expression evaluated as a string with the word element as context. The result is the surface form of the word.

<a name="wordLayer">

### wordLayer

<div class="param-level param-level-default-value">Default value: `words`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Name of the layer containing word annotations.

