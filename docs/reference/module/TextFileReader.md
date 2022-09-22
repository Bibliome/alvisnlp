<h1 class="module">TextFileReader</h1>

## Synopsis

Reads files and adds a document in the corpus for each file.

## Description

 *TextFileReader* reads file(s) from <a href="#sourcePath" class="param">sourcePath</a> and creates a document in the corpus for each file. The identifier of the created document is the absolute path of the corresponding file. The created document has a single section named <a href="#section" class="param">section</a> whose contents is the contents of the corresponding file.

If <a href="#sourcePath" class="param">sourcePath</a> is a path to a file, then *TextFileReader* will read this file. If <a href="#sourcePath" class="param">sourcePath</a> is a path to a directory, then *TextFileReader* will read the files in this directory. If <a href="#recursive" class="param">recursive</a> is set to true, then the files in sub-directories will be read recursively. *TextFileReader* only reads files whose name match <a href="#acceptPattern" class="param">acceptPattern</a> . If <a href="#acceptPattern" class="param">acceptPattern</a> is not set, then *TextFileReader* reads all files.

If <a href="#linesLimit" class="param">linesLimit</a> is set, then *TextFileReader* creates a new document for each set of lines. For instance, if <a href="#linesLimit" class="param">linesLimit</a> is set to 10 and a file contains 25 lines, then 3 documents are created: two containing 10 lines and one containing the las 5 lines.

Files are read using the same encoding <a href="#charset" class="param">charset</a> .

The created documents will all have the features defined in <a href="#constantDocumentFeatures" class="param">constantDocumentFeatures</a> . The unique section will have the features defined in <a href="#constantSectionFeatures" class="param">constantSectionFeatures</a> .

## Snippet



```xml
<textfilereader class="TextFileReader>
    <sourcePath></sourcePath>
</textfilereader>
```

## Mandatory parameters

<h3 id="sourcePath" class="param">sourcePath</h3>

<div class="param-level param-level-mandatory">Mandatory
</div>
<div class="param-type">Type: <a href="../converter/fr.inra.maiage.bibliome.util.streams.SourceStream" class="converter">SourceStream</a>
</div>
Path to the source directory or source file.

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

<h3 id="baseNameId" class="param">baseNameId</h3>

<div class="param-level param-level-default-value">Default value: `false`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.Boolean" class="converter">Boolean</a>
</div>
Use the filename base name instead of the full path as document identifier.

<h3 id="charset" class="param">charset</h3>

<div class="param-level param-level-default-value">Default value: `UTF-8`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Character set of the input files.

<h3 id="sectionName" class="param">sectionName</h3>

<div class="param-level param-level-default-value">Default value: `text`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Name of the single section containing the whole contents of a file.

