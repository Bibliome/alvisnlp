<h1 class="module">FSOVFileReader</h1>

## Synopsis

Project-specific text file reader.

## Description

*FSOVFileReader* reads text files in the same way as <a href="../module/TextFileReader" class="module">TextFileReader</a>. Additionally, for each file read, it also reads metadata in a file with the same name with the *.xml* extension.

## Parameters

<a name="sourcePath">

### sourcePath

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/org.bibliome.util.streams.SourceStream" class="converter">SourceStream</a>
</div>
Path to the source directory or source file.

<a name="xmlDir">

### xmlDir

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/org.bibliome.util.files.InputDirectory" class="converter">InputDirectory</a>
</div>
Directory where to find metadata files.

<a name="constantDocumentFeatures">

### constantDocumentFeatures

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/alvisnlp.module.types.Mapping" class="converter">Mapping</a>
</div>
Constant features to add to each document created by this module

<a name="constantSectionFeatures">

### constantSectionFeatures

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/alvisnlp.module.types.Mapping" class="converter">Mapping</a>
</div>
Constant features to add to each section created by this module

<a name="linesLimit">

### linesLimit

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/java.lang.Integer" class="converter">Integer</a>
</div>
Maximum number of lines per document.

<a name="sizeLimit">

### sizeLimit

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/java.lang.Integer" class="converter">Integer</a>
</div>
Maximum number of characters per document. No limit if not set.

<a name="bodySectionName">

### bodySectionName

<div class="param-level param-level-default-value">Default value: `body`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Name of the section containing the contents of the document.

<a name="charset">

### charset

<div class="param-level param-level-default-value">Default value: `UTF-8`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Character set of the input files.

<a name="titleSectionName">

### titleSectionName

<div class="param-level param-level-default-value">Default value: `title`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Name of the section containing the title of the document.

