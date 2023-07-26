<h1 class="module">JsonExport</h1>

## Synopsis

Writes the corpus data structure as a JSON file.

**This module is experimental.**

## Description

 *JsonExport* evaluates <a href="#files" class="param">files</a> as a list of elements with the corpus as the context element and creates a file for each result. The file is located in <a href="#outDir" class="param">outDir</a> and named after the result of <a href="#fileName" class="param">fileName</a> (evaluated as a string).

If <a href="#corpusFile" class="param">corpusFile</a> is set, then it overrides <a href="#files" class="param">files</a> and <a href="#fileName" class="param">fileName</a> . The whole corpus goes into a single file.

Each file is a JSON file with a structure specified by <a href="#json" class="param">json</a> .

## Snippet



<button class="copy-code-button" title="Copy to clipboard" onclick="copy_code(this)">📋</button>
```xml
<jsonexport class="JsonExport">
    <json></json>
    <outDir></outDir>
</jsonexport>
```

## Mandatory parameters

<h3 id="json" class="param">json</h3>

<div class="param-level param-level-mandatory">Mandatory
</div>
<div class="param-type">Type: <a href="../converter/fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.json.JsonValue" class="converter">JsonValue</a>
</div>
JSON value specification.

<h3 id="outDir" class="param">outDir</h3>

<div class="param-level param-level-mandatory">Mandatory
</div>
<div class="param-type">Type: <a href="../converter/fr.inra.maiage.bibliome.util.files.OutputDirectory" class="converter">OutputDirectory</a>
</div>
Directory where files are written.

## Optional parameters

<h3 id="corpusFile" class="param">corpusFile</h3>

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/java.io.File" class="converter">File</a>
</div>
Path to a single file for the whole corpus. This parameter will override both <a href="#files" class="param">files</a> and <a href="#fileName" class="param">fileName</a> .

<h3 id="fileName" class="param">fileName</h3>

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.Expression" class="converter">Expression</a>
</div>
Name of the file.

<h3 id="files" class="param">files</h3>

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.Expression" class="converter">Expression</a>
</div>
Expression that specifies which element corresponds to each file.

## Deprecated parameters

