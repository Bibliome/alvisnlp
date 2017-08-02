# org.bibliome.alvisnlp.modules.TextFileReader

## Synopsis

Reads files and adds a document in the corpus for each file.

## Description

*org.bibliome.alvisnlp.modules.TextFileReader* reads file(s) from [sourcePath](#sourcePath) and creates a document in the corpus for each file. The identifier of the created document is the absolute path of the corresponding file. The created document has a single section named [section](#section) whose contents is the contents of the corresponding file.

If [sourcePath](#sourcePath) is a path to a file, then *org.bibliome.alvisnlp.modules.TextFileReader* will read this file. If [sourcePath](#sourcePath) is a path to a directory, then *org.bibliome.alvisnlp.modules.TextFileReader* will read the files in this directory. If [recursive](#recursive) is set to true, then the files in sub-directories will be read recursively. *org.bibliome.alvisnlp.modules.TextFileReader* only reads files whose name match [acceptPattern](#acceptPattern). If [acceptPattern](#acceptPattern) is not set, then *org.bibliome.alvisnlp.modules.TextFileReader* reads all files.

If [linesLimit](#linesLimit) is set, then *org.bibliome.alvisnlp.modules.TextFileReader* creates a new document for each set of lines. For instance, if [linesLimit](#linesLimit) is set to 10 and a file contains 25 lines, then 3 documents are created: two containing 10 lines and one containing the las 5 lines.

Files are read using the same encoding [charset](#charset).

The created documents will all have the features defined in [constantDocumentFeatures](#constantDocumentFeatures). The unique section will have the features defined in [constantSectionFeatures](#constantSectionFeatures).

## Parameters

<a name="sourcePath">

### sourcePath

Optional

Type: [SourceStream](../converter/org.bibliome.util.streams.SourceStream)

Path to the source directory or source file.

<a name="constantDocumentFeatures">

### constantDocumentFeatures

Optional

Type: [Mapping](../converter/alvisnlp.module.types.Mapping)

Constant features to add to each document created by this module

<a name="constantSectionFeatures">

### constantSectionFeatures

Optional

Type: [Mapping](../converter/alvisnlp.module.types.Mapping)

Constant features to add to each section created by this module

<a name="linesLimit">

### linesLimit

Optional

Type: [Integer](../converter/java.lang.Integer)

Maximum number of lines per document.

<a name="sizeLimit">

### sizeLimit

Optional

Type: [Integer](../converter/java.lang.Integer)

Maximum number of characters per document. No limit if not set.

<a name="baseNameId">

### baseNameId

Default value: `false`

Type: [Boolean](../converter/java.lang.Boolean)

Use the filename base name instead of the full path as document identifier.

<a name="charset">

### charset

Default value: `UTF-8`

Type: [String](../converter/java.lang.String)

Character set of the input files.

<a name="sectionName">

### sectionName

Default value: `contents`

Type: [String](../converter/java.lang.String)

Name of the single section containing the whole contents of a file.

