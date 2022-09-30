<h1 class="module">RelpWriter</h1>

## Synopsis

Writes the corpus in relp format.

## Description

 *RelpWriter* writes the corpus into <a href="#outFilet" class="param">outFilet</a> in relp format.

## Snippet



<button class="copy-code-button" title="Copy to clipboard" onclick="copy_code(this)">📋</button>
```xml
<relpwriter class="RelpWriter>
    <outFile></outFile>
</relpwriter>
```

## Mandatory parameters

<h3 id="outFile" class="param">outFile</h3>

<div class="param-level param-level-mandatory">Mandatory
</div>
<div class="param-type">Type: <a href="../converter/fr.inra.maiage.bibliome.util.streams.TargetStream" class="converter">TargetStream</a>
</div>
File where to write the dependencies.

## Optional parameters

<h3 id="linkageNumberFeature" class="param">linkageNumberFeature</h3>

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Feature containing the linkage number to which a dependency belongs.

<h3 id="dependencyLabelFeature" class="param">dependencyLabelFeature</h3>

<div class="param-level param-level-default-value">Default value: `label`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Feature containing the dependency label.

<h3 id="dependencyRelation" class="param">dependencyRelation</h3>

<div class="param-level param-level-default-value">Default value: `dependencies`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Name of the dependecy relation.

<h3 id="dependentForm" class="param">dependentForm</h3>

<div class="param-level param-level-default-value">Default value: `@form`
</div>
<div class="param-type">Type: <a href="../converter/fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.Expression" class="converter">Expression</a>
</div>
Expression evaluated as a string with the dependency dependent element as context. The result is the surface form of the dependent.

<h3 id="dependentRole" class="param">dependentRole</h3>

<div class="param-level param-level-default-value">Default value: `dependent`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Name of the role of the dependent word.

<h3 id="documentFilter" class="param">documentFilter</h3>

<div class="param-level param-level-default-value">Default value: `true`
</div>
<div class="param-type">Type: <a href="../converter/fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.Expression" class="converter">Expression</a>
</div>
Only process document that satisfy this expression.

<h3 id="headForm" class="param">headForm</h3>

<div class="param-level param-level-default-value">Default value: `@form`
</div>
<div class="param-type">Type: <a href="../converter/fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.Expression" class="converter">Expression</a>
</div>
Expression evaluated as a string with the dependency head element as context. The result is the surface form of the head.

<h3 id="headRole" class="param">headRole</h3>

<div class="param-level param-level-default-value">Default value: `head`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Name of the role of the head word.

<h3 id="lemmaForm" class="param">lemmaForm</h3>

<div class="param-level param-level-default-value">Default value: `@lemma`
</div>
<div class="param-type">Type: <a href="../converter/fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.Expression" class="converter">Expression</a>
</div>
Expression evaluated as a string with the word element as context. The result is the lemma of the word.

<h3 id="pmid" class="param">pmid</h3>

<div class="param-level param-level-default-value">Default value: `@id`
</div>
<div class="param-type">Type: <a href="../converter/fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.Expression" class="converter">Expression</a>
</div>
Expression evaluated as a string with the document as context. The result is the identifier of the document.

<h3 id="sectionFilter" class="param">sectionFilter</h3>

<div class="param-level param-level-default-value">Default value: `true and layer:sentences and layer:words`
</div>
<div class="param-type">Type: <a href="../converter/fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.Expression" class="converter">Expression</a>
</div>
Process only sections that satisfy this expression.

<h3 id="sentenceLayer" class="param">sentenceLayer</h3>

<div class="param-level param-level-default-value">Default value: `sentences`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Name of the layer containing sentence annotations.

<h3 id="sentenceRole" class="param">sentenceRole</h3>

<div class="param-level param-level-default-value">Default value: `sentence`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Name of the role of the parsed sentence in the dependency relation.

<h3 id="wordForm" class="param">wordForm</h3>

<div class="param-level param-level-default-value">Default value: `@form`
</div>
<div class="param-type">Type: <a href="../converter/fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.Expression" class="converter">Expression</a>
</div>
Expression evaluated as a string with the word element as context. The result is the surface form of the word.

<h3 id="wordLayer" class="param">wordLayer</h3>

<div class="param-level param-level-default-value">Default value: `words`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Name of the layer containing word annotations.

