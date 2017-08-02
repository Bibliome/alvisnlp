# org.bibliome.alvisnlp.modules.TabularExport

## Synopsis

Writes the corpus data structure in files in tabular format.

## Description

*org.bibliome.alvisnlp.modules.TabularExport* evaluates [files](#files) as a list of elements with the corpus as the context element and creates a file for each result.
      	The file is located in [outDir](#outDir) and named after the result of [fileName](#fileName) (evaluated as a string).
      


      	The file is a table where each line is the result of the evaluation of [lines](#lines) as a list of element with the file element as the context element.
      	Each line will have as many columns as the size of the [columns](#columns) array.
      


      	Each expression of [columns](#columns) is evaluated as a string with the line element as the context element.
      

## Parameters

<a name="columns">

### columns

Optional

Type: [Expression[]](../converter/alvisnlp.corpus.expressions.Expression[])

Expressions that specify the contents of each column.

<a name="fileName">

### fileName

Optional

Type: [Expression](../converter/alvisnlp.corpus.expressions.Expression)

Name of the file.

<a name="files">

### files

Optional

Type: [Expression](../converter/alvisnlp.corpus.expressions.Expression)

Expression that specifies which element corresponds to each file.

<a name="lines">

### lines

Optional

Type: [Expression](../converter/alvisnlp.corpus.expressions.Expression)

Expression that specifies which element corresponds to each line.

<a name="outDir">

### outDir

Optional

Type: [OutputDirectory](../converter/org.bibliome.util.files.OutputDirectory)

Directory where files are written.

<a name="footers">

### footers

Optional

Type: [Expression[]](../converter/alvisnlp.corpus.expressions.Expression[])

Last line of output files.

<a name="headers">

### headers

Optional

Type: [Expression[]](../converter/alvisnlp.corpus.expressions.Expression[])

First line of output files.

<a name="append">

### append

Default value: `false`

Type: [Boolean](../converter/java.lang.Boolean)

Either to append the export at the end of a file, if the file exists.

<a name="charset">

### charset

Default value: `UTF-8`

Type: [String](../converter/java.lang.String)

Character encoding of the written files.

<a name="separator">

### separator

Default value: `	`

Type: [String](../converter/java.lang.String)

Character that separates columns.

<a name="trim">

### trim

Default value: `false`

Type: [Boolean](../converter/java.lang.Boolean)



