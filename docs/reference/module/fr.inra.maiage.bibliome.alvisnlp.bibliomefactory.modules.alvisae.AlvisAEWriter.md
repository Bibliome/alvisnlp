<h1 class="module">AlvisAEWriter</h1>

## Synopsis

Writes each document in a file in the AlvisAE protocol format.

## Description

Writes each document in a file in the AlvisAE protocol format.

## Snippet



<button class="copy-code-button" title="Copy to clipboard" onclick="copy_code(this)">📋</button>
```xml
<alvisaewriter class="AlvisAEWriter>
    <annotationSets></annotationSets>
    <documentDescription></documentDescription>
    <outDir></outDir>
</alvisaewriter>
```

## Mandatory parameters

<h3 id="annotationSets" class="param">annotationSets</h3>

<div class="param-level param-level-mandatory">Mandatory
</div>
<div class="param-type">Type: <a href="../converter/fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.alvisae.AnnotationSet%5B%5D" class="converter">AnnotationSet[]</a>
</div>
Annotation Set specifications.

<h3 id="documentDescription" class="param">documentDescription</h3>

<div class="param-level param-level-mandatory">Mandatory
</div>
<div class="param-type">Type: <a href="../converter/fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.Expression" class="converter">Expression</a>
</div>
Document description.

<h3 id="outDir" class="param">outDir</h3>

<div class="param-level param-level-mandatory">Mandatory
</div>
<div class="param-type">Type: <a href="../converter/fr.inra.maiage.bibliome.util.files.OutputDirectory" class="converter">OutputDirectory</a>
</div>
Directory where to write files.

## Optional parameters

<h3 id="schemaFile" class="param">schemaFile</h3>

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/fr.inra.maiage.bibliome.util.files.InputFile" class="converter">InputFile</a>
</div>
AlvisAE schema file to include in all document files.

<h3 id="documentFilter" class="param">documentFilter</h3>

<div class="param-level param-level-default-value">Default value: `true`
</div>
<div class="param-type">Type: <a href="../converter/fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.Expression" class="converter">Expression</a>
</div>
Only process document that satisfy this expression.

<h3 id="documentProperties" class="param">documentProperties</h3>

<div class="param-level param-level-default-value">Default value: `{}`
</div>
<div class="param-type">Type: <a href="../converter/fr.inra.maiage.bibliome.alvisnlp.core.module.types.ExpressionMapping" class="converter">ExpressionMapping</a>
</div>
Document properties specification.

<h3 id="fileName" class="param">fileName</h3>

<div class="param-level param-level-default-value">Default value: `@id ^ ".json"`
</div>
<div class="param-type">Type: <a href="../converter/fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.Expression" class="converter">Expression</a>
</div>


<h3 id="owner" class="param">owner</h3>

<div class="param-level param-level-default-value">Default value: `0`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.Integer" class="converter">Integer</a>
</div>
Owner ID of the documents.

<h3 id="publish" class="param">publish</h3>

<div class="param-level param-level-default-value">Default value: `false`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.Boolean" class="converter">Boolean</a>
</div>
Publish the exported annotation sets.

<h3 id="sectionFilter" class="param">sectionFilter</h3>

<div class="param-level param-level-default-value">Default value: `true`
</div>
<div class="param-type">Type: <a href="../converter/fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.Expression" class="converter">Expression</a>
</div>
Process only sections that satisfy this expression.

