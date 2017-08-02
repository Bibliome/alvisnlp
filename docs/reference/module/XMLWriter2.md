# org.bibliome.alvisnlp.modules.xml.XMLWriter2

## Synopsis

Deprecated alias for [XMLWriter](../module/XMLWriter).

**This module is obsolete, superceded by org.bibliome.alvisnlp.modules.xml.XMLWriter**

## Description

## Parameters

<a name="fileName">

### fileName

Optional

Type: [Expression](../converter/alvisnlp.corpus.expressions.Expression)

Expression evaluated as a string with the file root element as the context element. The result specifies the file where to write the result.

<a name="outDir">

### outDir

Optional

Type: [OutputDirectory](../converter/org.bibliome.util.files.OutputDirectory)

Base directory where all file are written.

<a name="roots">

### roots

Optional

Type: [Expression](../converter/alvisnlp.corpus.expressions.Expression)

Expression evaluated as a list of elements with the corpus as the context element. *org.bibliome.alvisnlp.modules.xml.XMLWriter2* writes a file for each element in the result.

<a name="xslTransform">

### xslTransform

Optional

Type: [SourceStream](../converter/org.bibliome.util.streams.SourceStream)

XSLT stylesheet that specifies the output.

<a name="indent">

### indent

Default value: `true`

Type: [Boolean](../converter/java.lang.Boolean)

Either to indent the resulting XML.

