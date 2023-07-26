<h1 class="module">XMLReader</h1>

## Synopsis

Reads XML files and creates elements.

## Description

 *XMLReader* reads its input from <a href="#source" class="param">source</a> as XML and creates documents, sections, annotations, relations or tuples. The structure of the input XML is handled through the <a href="#xslTransform" class="param">xslTransform</a> XSLT stylesheet.

 *XMLReader* also provides XSLT function and element extensions. The namespace for all extensions is `xalan://fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.xml.XMLReader2` .

### Element extensions
*  `document` : creates a document in the current corpus. The identifier is either a string specified by the attribute id, or an XPath expression specified by the xpath-id attribute. The expression is evaluated as a string.
*  `section` : creates a section in the current document; this element should occur inside a document element. The name of the section is either specified as a string in the name attribute, or as an XPath expression in the xpath-name attribute. The contents of the section is either specified as a string in the contents attribute, or as an XPath expression in the xpath-contents attribute.
*  `annotation` : creates an annotation in the current section; this element should occur inside a section element. The start and end positions are specified by the start and end attributes respectively. The value of these attributes are XPath expressions evaluated as integers. The layers in which the annotation should be added are either specified as a string by the layers attribute, or as an XPath expression by the xpath-layers attribute. The layers should be a space spearated list of layer names, the annotation will be added in each named layer. If the layers list is empty, then no annotation is created at all. Moreover an identifier for the created annotation may be specified either as a string by the ref attribute, or as an XPath expression by the xpath-ref attribute. This identifier can be referenced later to set tuple arguments.
*  `relation` : creates a relation in the current section; this element should occur inside a section element. The name of the relation is specified either as a string by the name attribute, or as an XPath expression by the xpath-name attribute.
*  `tuple` : creates a tuple inside the current relation; this element should occur inside a relation element.
*  `arg` : sets an argument of the current tuple; this element should occur inside a tuple element. The role of the argument is specified either as a string by the role attribute, or as an XPath expression by the xpath-role attribute. The identifier of the argument is specified either as a string by the ref attribute, or as an XPath expression by the xpath-ref attribute. The value must have been set previously in an annotation element.
*  `feature` : adds a feature to the current element; this element should occur inside a document, section, annotation, relation or tuple element. The name of the feature is set either as a string by the name attribute, or as an XPath expression by the xpath-name attribute. The value of the feature is set either as a string by the value attribute, or as an XPath expression by the xpath-value attribute.



### Function extensions
*  `inline` : this function evaluates as a node set containing a copy of each element inside the current node. The elements will have two additional attributes start and end that indicate the character positions of the start and end tags. These attributes have the namespace http://bibliome.jouy.inra.fr/alvisnlp/bibliome-module-factory/inline. This function is useful to read in-text annotations. Note: the inline() function also process comment and processing instruction nodes. These nodes are then wrapped within an extra element named wrapper (in the namespace http://bibliome.jouy.inra.fr/alvisnlp/bibliome-module-factory/inline), which will have the two attributes start and end to indicate the character position where the comment or the processiçng instruction is inserted.



## Snippet



<button class="copy-code-button" title="Copy to clipboard" onclick="copy_code(this)">📋</button>
```xml
<xmlreader class="XMLReader">
    <source></source>
    <xslTransform></xslTransform>
</xmlreader>
```

## Mandatory parameters

<h3 id="source" class="param">source</h3>

<div class="param-level param-level-mandatory">Mandatory
</div>
<div class="param-type">Type: <a href="../converter/fr.inra.maiage.bibliome.util.streams.SourceStream" class="converter">SourceStream</a>
</div>
Path to the source directory or source file.

<h3 id="xslTransform" class="param">xslTransform</h3>

<div class="param-level param-level-mandatory">Mandatory
</div>
<div class="param-type">Type: <a href="../converter/fr.inra.maiage.bibliome.util.streams.SourceStream" class="converter">SourceStream</a>
</div>
XSLT Stylesheet to apply on the input.

## Optional parameters

<h3 id="constantAnnotationFeatures" class="param">constantAnnotationFeatures</h3>

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/fr.inra.maiage.bibliome.alvisnlp.core.module.types.Mapping" class="converter">Mapping</a>
</div>
Constant features to add to each annotation created by this module.

<h3 id="constantDocumentFeatures" class="param">constantDocumentFeatures</h3>

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/fr.inra.maiage.bibliome.alvisnlp.core.module.types.Mapping" class="converter">Mapping</a>
</div>
Constant features to add to each document created by this module.

<h3 id="constantRelationFeatures" class="param">constantRelationFeatures</h3>

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/fr.inra.maiage.bibliome.alvisnlp.core.module.types.Mapping" class="converter">Mapping</a>
</div>
Constant features to add to each relation created by this module.

<h3 id="constantSectionFeatures" class="param">constantSectionFeatures</h3>

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/fr.inra.maiage.bibliome.alvisnlp.core.module.types.Mapping" class="converter">Mapping</a>
</div>
Constant features to add to each section created by this module.

<h3 id="constantTupleFeatures" class="param">constantTupleFeatures</h3>

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/fr.inra.maiage.bibliome.alvisnlp.core.module.types.Mapping" class="converter">Mapping</a>
</div>
Constant features to add to each tuple created by this module.

<h3 id="stringParams" class="param">stringParams</h3>

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/fr.inra.maiage.bibliome.alvisnlp.core.module.types.Mapping" class="converter">Mapping</a>
</div>
Parameters to pass to the XSLT Stylesheet specified by <a href="#xslTransform" class="param">xslTransform</a> .

<h3 id="html" class="param">html</h3>

<div class="param-level param-level-default-value">Default value: `false`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.Boolean" class="converter">Boolean</a>
</div>
Set to true if the input is HTML rather than XML.

<h3 id="rawTagNames" class="param">rawTagNames</h3>

<div class="param-level param-level-default-value">Default value: `false`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.Boolean" class="converter">Boolean</a>
</div>
If true, do not convert tag names to upper case.

## Deprecated parameters

<h3 id="sourcePath" class="param">sourcePath</h3>

<div class="param-level param-level-deprecated">Deprecated
</div>
<div class="param-type">Type: <a href="../converter/fr.inra.maiage.bibliome.util.streams.SourceStream" class="converter">SourceStream</a>
</div>
Alias for <a href="#source" class="param">source</a> . Use <a href="#source" class="param">source</a> instead.

