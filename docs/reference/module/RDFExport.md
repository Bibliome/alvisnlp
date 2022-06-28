<h1 class="module">RDFExport</h1>

## Synopsis

synopsis

**This module is experimental.**

## Description

synopsis

## Snippet



```xml
<rdfexport class="RDFExport>
    <fileName></fileName>
    <files></files>
    <outDir></outDir>
    <statements></statements>
</rdfexport>
```

## Mandatory parameters

<h3 id="fileName" class="param">fileName</h3>

<div class="param-level param-level-mandatory">Mandatory
</div>
<div class="param-type">Type: <a href="../converter/fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.Expression" class="converter">Expression</a>
</div>


<h3 id="files" class="param">files</h3>

<div class="param-level param-level-mandatory">Mandatory
</div>
<div class="param-type">Type: <a href="../converter/fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.Expression" class="converter">Expression</a>
</div>


<h3 id="outDir" class="param">outDir</h3>

<div class="param-level param-level-mandatory">Mandatory
</div>
<div class="param-type">Type: <a href="../converter/fr.inra.maiage.bibliome.util.files.OutputDirectory" class="converter">OutputDirectory</a>
</div>


<h3 id="statements" class="param">statements</h3>

<div class="param-level param-level-mandatory">Mandatory
</div>
<div class="param-type">Type: <a href="../converter/fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.Expression%5B%5D" class="converter">Expression[]</a>
</div>


## Optional parameters

<h3 id="charset" class="param">charset</h3>

<div class="param-level param-level-default-value">Default value: `UTF-8`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>


<h3 id="format" class="param">format</h3>

<div class="param-level param-level-default-value">Default value: `RDF/XML/pretty`
</div>
<div class="param-type">Type: <a href="../converter/org.apache.jena.riot.RDFFormat" class="converter">RDFFormat</a>
</div>


<h3 id="prefixes" class="param">prefixes</h3>

<div class="param-level param-level-default-value">Default value: `{}`
</div>
<div class="param-type">Type: <a href="../converter/fr.inra.maiage.bibliome.alvisnlp.core.module.types.Mapping" class="converter">Mapping</a>
</div>


