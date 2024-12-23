<h1 class="module">XMLWriter</h1>

## Synopsis

Writes the corpus data structure into a file via an XSLT stylesheet.

## Description

 *XMLWriter* evaluates <a href="#roots" class="param">roots</a> as a list of elements. The for each element, it writes a file using the <a href="#xslTransform" class="param">xslTransform</a> stylesheet. The file name is specified by the evaluation of <a href="#fileName" class="param">fileName</a> as a string with the root element as he context element. Relative file names are relative to <a href="#outDir" class="param">outDir</a> .

The stylesheet operates on an empty XML document bound to the root element, however *XMLWriter* provides XSLT element and function extensions in order to retrieve elements as a DOM structure. All extensions are defined in the namespace `xalan://fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.xml.XMLWriter2` .

### Extension functions
*  `NodeSet elements(String expression)` : evaluates *expression* as a list of elements with the element bound to the context node as the context element. Each element is converted as an XML element; the result of this function is then a node set that can be used in a `for-each` statement. The returned elements have the name `element` in the `http://bibliome.jouy.inra.fr/alvisnlp/XMLReader2` namespace. For each feature of the element, the corresponding XML element has an attribute. Each returned XML element is bound to to the corresponding AlvisNLP element.
*  `String string(String expression)` : evaluates *expression* as a string with the element bound to the context node as the context element. The string is returned.
*  `Number integer(String expression)` : evaluates *expression* as an integer with the element bound to the context node as the context element. The integer is returned.
*  `Number number(String expression)` : evaluates *expression* as a double with the element bound to the context node as the context element. The double is returned.
*  `NodeSet features()` : returns all features of the element bound to the context node as a node set. The returned XML elements have the name `feature` in the `http://bibliome.jouy.inra.fr/alvisnlp/XMLReader2` namespace. The feature name and values are set in the attributes `name` and `value` .
*  `NodeSet inline(String expression)` : evaluates *expression* as a list of elements with the element bound to the context node as the context element. If the context element was a section, and if the result contains annotations, then this funcion returns the section contents with the annotations as XML elements included in the text. This function is used to convert annotations into in-text XML format.



## Snippet



<button class="copy-code-button" title="Copy to clipboard" onclick="copy_code(this)">📋</button>
```xml
<xmlwriter class="XMLWriter">
    <fileName></fileName>
    <outDir></outDir>
    <roots></roots>
    <xslTransform></xslTransform>
</xmlwriter>
```

## Mandatory parameters

<h3 id="fileName" class="param">fileName</h3>

<div class="param-level param-level-mandatory">Mandatory
</div>
<div class="param-type">Type: <a href="../converter/fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.Expression" class="converter">Expression</a>
</div>
Expression evaluated as a string with the file root element as the context element. The result specifies the file where to write the result.

<h3 id="outDir" class="param">outDir</h3>

<div class="param-level param-level-mandatory">Mandatory
</div>
<div class="param-type">Type: <a href="../converter/fr.inra.maiage.bibliome.util.files.OutputDirectory" class="converter">OutputDirectory</a>
</div>
Base directory where all file are written.

<h3 id="roots" class="param">roots</h3>

<div class="param-level param-level-mandatory">Mandatory
</div>
<div class="param-type">Type: <a href="../converter/fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.Expression" class="converter">Expression</a>
</div>
Expression evaluated as a list of elements with the corpus as the context element. *XMLWriter* writes a file for each element in the result.

<h3 id="xslTransform" class="param">xslTransform</h3>

<div class="param-level param-level-mandatory">Mandatory
</div>
<div class="param-type">Type: <a href="../converter/fr.inra.maiage.bibliome.util.streams.SourceStream" class="converter">SourceStream</a>
</div>
XSLT stylesheet that specifies the output.

## Optional parameters

<h3 id="indent" class="param">indent</h3>

<div class="param-level param-level-default-value">Default value: `true`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.Boolean" class="converter">Boolean</a>
</div>
Either to indent the resulting XML.

## Deprecated parameters

