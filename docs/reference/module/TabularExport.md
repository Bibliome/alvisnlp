<h1 class="module">TabularExport</h1>

## Synopsis

Writes the corpus data structure in files in tabular format.

## Description

 *TabularExport* evaluates <a href="#files" class="param">files</a> as a list of elements with the corpus as the context element and creates a file for each result. The file is located in <a href="#outDir" class="param">outDir</a> and named after the result of <a href="#fileName" class="param">fileName</a> (evaluated as a string).

If <a href="#corpusFile" class="param">corpusFile</a> is set, then it overrides <a href="#files" class="param">files</a> and <a href="#fileName" class="param">fileName</a> . The whole corpus goes into a single file.

The file is a table where each line is the result of the evaluation of <a href="#lines" class="param">lines</a> as a list of element with the file element as the context element. Each line will have as many columns as the size of the <a href="#columns" class="param">columns</a> array.

Each expression of <a href="#columns" class="param">columns</a> is evaluated as a string with the line element as the context element.

## Snippet



```xml
<tabularexport class="TabularExport>
    <columns></columns>
    <lines></lines>
    <outDir></outDir>
</tabularexport>
```

## Mandatory parameters

<h3 id="columns" class="param">columns</h3>

<div class="param-level param-level-mandatory">Mandatory
</div>
<div class="param-type">Type: <a href="../converter/fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.Expression%5B%5D" class="converter">Expression[]</a>
</div>
Expressions that specify the contents of each column.

<h3 id="lines" class="param">lines</h3>

<div class="param-level param-level-mandatory">Mandatory
</div>
<div class="param-type">Type: <a href="../converter/fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.Expression" class="converter">Expression</a>
</div>
Expression that specifies which element corresponds to each line.

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
Path to a single file for the whole corpus. This parameter will override both <a href="#files" class="param">files</a> and <a href="#fileNames" class="param">fileNames</a> .

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

<h3 id="footers" class="param">footers</h3>

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.Expression%5B%5D" class="converter">Expression[]</a>
</div>
Last line of output files.

<h3 id="headers" class="param">headers</h3>

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.Expression%5B%5D" class="converter">Expression[]</a>
</div>
First line of output files.

<h3 id="append" class="param">append</h3>

<div class="param-level param-level-default-value">Default value: `false`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.Boolean" class="converter">Boolean</a>
</div>
Either to append the export at the end of a file, if the file exists.

<h3 id="charset" class="param">charset</h3>

<div class="param-level param-level-default-value">Default value: `UTF-8`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Character encoding of the written files.

<h3 id="separator" class="param">separator</h3>

<div class="param-level param-level-default-value">Default value: `	`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Character that separates columns.

<h3 id="trim" class="param">trim</h3>

<div class="param-level param-level-default-value">Default value: `false`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.Boolean" class="converter">Boolean</a>
</div>


<h3 id="trueCSV" class="param">trueCSV</h3>

<div class="param-level param-level-default-value">Default value: `false`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.Boolean" class="converter">Boolean</a>
</div>
Use CSV Commons library for the output.

