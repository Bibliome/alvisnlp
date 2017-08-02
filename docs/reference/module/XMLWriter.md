# org.bibliome.alvisnlp.modules.xml.XMLWriter

## Synopsis

Writes the corpus data structure into a file via an XSLT stylesheet.

## Description

*org.bibliome.alvisnlp.modules.xml.XMLWriter* evaluates [roots](#roots) as a list of elements. The for each element, it writes a file using the [xslTransform](#xslTransform) stylesheet. The file name is specified by the evaluation of [fileName](#fileName) as a string with the root element as he context element. Relative file names are relative to [outDir](#outDir).

The stylesheet operates on an empty XML document bound to the root element, however *org.bibliome.alvisnlp.modules.xml.XMLWriter* provides XSLT element and function extensions in order to retrieve elements as a DOM structure. All extensions are defined in the namespace `xalan://org.bibliome.alvisnlp.modules.xml.XMLWriter2`.

### Extension functions
* `NodeSet elements(String expression)`: evaluates *expression* as a list of elements with the element bound to the context node as the context element. Each element is converted as an XML element; the result of this function is then a node set that can be used in a `for-each` statement. The returned elements have the name `element` in the `http://bibliome.jouy.inra.fr/alvisnlp/XMLReader2` namespace. For each feature of the element, the corresponding XML element has an attribute. Each returned XML element is bound to to the corresponding AlvisNLP/ML element.
* `String string(String expression)`: evaluates *expression* as a string with the element bound to the context node as the context element. The string is returned.
* `Number integer(String expression)`: evaluates *expression* as an integer with the element bound to the context node as the context element. The integer is returned.
* `Number number(String expression)`: evaluates *expression* as a double with the element bound to the context node as the context element. The double is returned.
* `NodeSet features()`: returns all features of the element bound to the context node as a node set. The returned XML elements have the name `feature` in the `http://bibliome.jouy.inra.fr/alvisnlp/XMLReader2` namespace. The feature name and values are set in the attributes `name` and `value`.
* `NodeSet inline(String expression)`: evaluates *expression* as a list of elements with the element bound to the context node as the context element. If the context element was a section, and if the result contains annotations, then this funcion returns the section contents with the annotations as XML elements included in the text. This function is used to convert annotations into in-text XML format.



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

Expression evaluated as a list of elements with the corpus as the context element. *org.bibliome.alvisnlp.modules.xml.XMLWriter* writes a file for each element in the result.

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

