<h1 class="module">XMLWriter2</h1>

## Synopsis

Deprecated alias for <a href="../module/XMLWriter" class="module">XMLWriter</a>.

**This module is obsolete, superceded by fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.xml.XMLWriter**

## Description

## Parameters

<h3 name="fileName" class="param">fileName</h3>

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.Expression" class="converter">Expression</a>
</div>
Expression evaluated as a string with the file root element as the context element. The result specifies the file where to write the result.

<h3 name="outDir" class="param">outDir</h3>

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/fr.inra.maiage.bibliome.util.files.OutputDirectory" class="converter">OutputDirectory</a>
</div>
Base directory where all file are written.

<h3 name="roots" class="param">roots</h3>

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.Expression" class="converter">Expression</a>
</div>
Expression evaluated as a list of elements with the corpus as the context element. *XMLWriter2* writes a file for each element in the result.

<h3 name="xslTransform" class="param">xslTransform</h3>

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/fr.inra.maiage.bibliome.util.streams.SourceStream" class="converter">SourceStream</a>
</div>
XSLT stylesheet that specifies the output.

<h3 name="indent" class="param">indent</h3>

<div class="param-level param-level-default-value">Default value: `true`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.Boolean" class="converter">Boolean</a>
</div>
Either to indent the resulting XML.

