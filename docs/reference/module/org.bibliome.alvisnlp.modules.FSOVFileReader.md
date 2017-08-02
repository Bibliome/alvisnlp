# org.bibliome.alvisnlp.modules.FSOVFileReader

## Synopsis

Project-specific text file reader.

## Description

*org.bibliome.alvisnlp.modules.FSOVFileReader* reads text files in the same way as [TextFileReader](../module/TextFileReader). Additionally, for each file read, it also reads metadata in a file with the same name with the *.xml* extension.

## Parameters

<a name="sourcePath">

### sourcePath

Optional

Type: [SourceStream](../converter/org.bibliome.util.streams.SourceStream)

Path to the source directory or source file.

<a name="xmlDir">

### xmlDir

Optional

Type: [InputDirectory](../converter/org.bibliome.util.files.InputDirectory)

Directory where to find metadata files.

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

<a name="bodySectionName">

### bodySectionName

Default value: `body`

Type: [String](../converter/java.lang.String)

Name of the section containing the contents of the document.

<a name="charset">

### charset

Default value: `UTF-8`

Type: [String](../converter/java.lang.String)

Character set of the input files.

<a name="titleSectionName">

### titleSectionName

Default value: `title`

Type: [String](../converter/java.lang.String)

Name of the section containing the title of the document.

