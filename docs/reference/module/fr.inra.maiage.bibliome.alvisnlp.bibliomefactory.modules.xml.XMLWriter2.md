<h1 class="module">XMLWriter2</h1>

## Synopsis

Deprecated alias for <a href="../module/XMLWriter" class="module">XMLWriter</a> .

## Description

## Snippet



<button class="copy-code-button" title="Copy to clipboard" onclick="copy_code(this)">📋</button>
```xml
<xmlwriter2 class="XMLWriter2>
    <fileName></fileName>
    <outDir></outDir>
    <roots></roots>
    <xslTransform></xslTransform>
</xmlwriter2>
```

## Mandatory parameters

<h3 id="fileName" class="param">fileName</h3>

<div class="param-level param-level-mandatory">Mandatory
</div>
<div class="param-type">Type: <a href="../converter/fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.Expression" class="converter">Expression</a>
</div>
Expression evaluated as a string with the file root element as the context element. The result specifies the file where to write the result.

<h3 id="outDir" class="param">outDir</h3>

<div class="param-level param-level-mandatory">Mandatory
</div>
<div class="param-type">Type: <a href="../converter/fr.inra.maiage.bibliome.util.files.OutputDirectory" class="converter">OutputDirectory</a>
</div>
Base directory where all file are written.

<h3 id="roots" class="param">roots</h3>

<div class="param-level param-level-mandatory">Mandatory
</div>
<div class="param-type">Type: <a href="../converter/fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.Expression" class="converter">Expression</a>
</div>
Expression evaluated as a list of elements with the corpus as the context element. *XMLWriter2* writes a file for each element in the result.

<h3 id="xslTransform" class="param">xslTransform</h3>

<div class="param-level param-level-mandatory">Mandatory
</div>
<div class="param-type">Type: <a href="../converter/fr.inra.maiage.bibliome.util.streams.SourceStream" class="converter">SourceStream</a>
</div>
XSLT stylesheet that specifies the output.

## Optional parameters

<h3 id="indent" class="param">indent</h3>

<div class="param-level param-level-default-value">Default value: `true`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.Boolean" class="converter">Boolean</a>
</div>
Either to indent the resulting XML.

## Deprecated parameters

