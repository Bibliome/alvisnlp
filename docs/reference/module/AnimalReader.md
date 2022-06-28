<h1 class="module">AnimalReader</h1>

## Synopsis

Project-specific file reader.

## Description

*AnimalReader*reads text files in the same way as <a href="../module/TextFileReader" class="module">TextFileReader</a> . Additionally, for each file read, it also reads metadata in a file with the same name with the *.xml* extension.

## Snippet



```xml
<animalreader class="AnimalReader>
    <sourcePath></sourcePath>
    <xmlDir></xmlDir>
</animalreader>
```

## Mandatory parameters

<h3 id="sourcePath" class="param">sourcePath</h3>

<div class="param-level param-level-mandatory">Mandatory
</div>
<div class="param-type">Type: <a href="../converter/fr.inra.maiage.bibliome.util.streams.SourceStream" class="converter">SourceStream</a>
</div>
Path to the source directory or source file.

<h3 id="xmlDir" class="param">xmlDir</h3>

<div class="param-level param-level-mandatory">Mandatory
</div>
<div class="param-type">Type: <a href="../converter/fr.inra.maiage.bibliome.util.files.InputDirectory" class="converter">InputDirectory</a>
</div>
Directory where to find metadata files.

## Optional parameters

<h3 id="constantDocumentFeatures" class="param">constantDocumentFeatures</h3>

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/fr.inra.maiage.bibliome.alvisnlp.core.module.types.Mapping" class="converter">Mapping</a>
</div>
Constant features to add to each document created by this module.

<h3 id="constantSectionFeatures" class="param">constantSectionFeatures</h3>

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/fr.inra.maiage.bibliome.alvisnlp.core.module.types.Mapping" class="converter">Mapping</a>
</div>
Constant features to add to each section created by this module.

<h3 id="linesLimit" class="param">linesLimit</h3>

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/java.lang.Integer" class="converter">Integer</a>
</div>
Maximum number of lines per document.

<h3 id="sizeLimit" class="param">sizeLimit</h3>

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/java.lang.Integer" class="converter">Integer</a>
</div>
Maximum number of characters per document. No limit if not set.

<h3 id="bodySectionName" class="param">bodySectionName</h3>

<div class="param-level param-level-default-value">Default value: `body`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Name of the section for the text body.

<h3 id="charset" class="param">charset</h3>

<div class="param-level param-level-default-value">Default value: `UTF-8`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Character encoding of input files.

<h3 id="titleSectionName" class="param">titleSectionName</h3>

<div class="param-level param-level-default-value">Default value: `title`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Name of the section of the document title.

