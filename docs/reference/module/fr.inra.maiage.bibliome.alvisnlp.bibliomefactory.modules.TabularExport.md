<h1 class="module">TabularExport</h1>

## Synopsis

Writes the corpus data structure in files in tabular format.

## Description

*TabularExport* evaluates <a href="#files" class="param">files</a> as a list of elements with the corpus as the context element and creates a file for each result.
  	The file is located in <a href="#outDir" class="param">outDir</a> and named after the result of <a href="#fileName" class="param">fileName</a> (evaluated as a string).
  


  	The file is a table where each line is the result of the evaluation of <a href="#lines" class="param">lines</a> as a list of element with the file element as the context element.
  	Each line will have as many columns as the size of the <a href="#columns" class="param">columns</a> array.
  


  	Each expression of <a href="#columns" class="param">columns</a> is evaluated as a string with the line element as the context element.
  

## Parameters

<a name="columns">

### columns

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.Expression[]" class="converter">Expression[]</a>
</div>
Expressions that specify the contents of each column.

<a name="fileName">

### fileName

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.Expression" class="converter">Expression</a>
</div>
Name of the file.

<a name="files">

### files

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.Expression" class="converter">Expression</a>
</div>
Expression that specifies which element corresponds to each file.

<a name="lines">

### lines

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.Expression" class="converter">Expression</a>
</div>
Expression that specifies which element corresponds to each line.

<a name="outDir">

### outDir

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/fr.inra.maiage.bibliome.util.files.OutputDirectory" class="converter">OutputDirectory</a>
</div>
Directory where files are written.

<a name="footers">

### footers

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.Expression[]" class="converter">Expression[]</a>
</div>
Last line of output files.

<a name="headers">

### headers

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.Expression[]" class="converter">Expression[]</a>
</div>
First line of output files.

<a name="append">

### append

<div class="param-level param-level-default-value">Default value: `false`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.Boolean" class="converter">Boolean</a>
</div>
Either to append the export at the end of a file, if the file exists.

<a name="charset">

### charset

<div class="param-level param-level-default-value">Default value: `UTF-8`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Character encoding of the written files.

<a name="separator">

### separator

<div class="param-level param-level-default-value">Default value: `	`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Character that separates columns.

<a name="trim">

### trim

<div class="param-level param-level-default-value">Default value: `false`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.Boolean" class="converter">Boolean</a>
</div>


