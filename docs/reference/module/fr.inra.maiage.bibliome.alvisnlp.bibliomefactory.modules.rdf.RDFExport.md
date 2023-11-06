<h1 class="module">RDFExport</h1>

## Synopsis

Export annotations in RDF format.

## Description

 *RDFExport* creates an RDF file for each element of <a href="#files" class="param">files</a> . The file name is obtained by evaluating <a href="#fileName" class="param">fileName</a> as a string.

The triplet content of the files is specified by <a href="#statements" class="param">statements</a> . <a href="#statements" class="param">statements</a> provide the following libraries:
* one library for each entry in <a href="#prefixes" class="param">prefixes</a> , which allows to write abbreviated URIs ( *e.g.*  `rdf:type` );
*  `uri` with two functions:
*  `uri:set(URI)` : registers the default URI of the current element ( `URI` is evaluated as a string);
*  `uri:get` : evaluates as the URI previously registered for the current element;


*  `stmt` with several functions to specify the triplets to write into files:
*  `stmt:res(SUBJ, PROP, OBJ)` : specifies a triplet where the object is a resource;
*  `stmt:lit(SUBJ, PROP, OBJ)` : specifies a triplet where the object is an untyped literal;
*  `stmt:bool(SUBJ, PROP, OBJ)` : specifies a triplet where the object is a boolean literal;
*  `stmt:int(SUBJ, PROP, OBJ)` : specifies a triplet where the object is an integer literal;
*  `stmt:str(SUBJ, PROP, OBJ)` : specifies a triplet where the object is a string literal;
*  `stmt:double(SUBJ, PROP, OBJ)` : specifies a triplet where the object is a double literal;
*  `stmt:typed(SUBJ, PROP, OBJ, TYPE)` : specifies a triplet where the object is a custom type literal;
*  `stmt:lang(SUBJ, PROP, OBJ, LANG)` : specifies a triplet where the object is a string literal with a language qualifier;

All arguments are evaluated as strings, then interpreted as resource URIs or literals. The `SUBJ` argument is optional: if omitted the subject is the URI registered for the current element.



## Snippet



<button class="copy-code-button" title="Copy to clipboard" onclick="copy_code(this)">📋</button>
```xml
<rdfexport class="RDFExport">
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
Name of the file. Evaluated as a string from the element representing the file (result of <a href="#files" class="param">files</a> .

<h3 id="files" class="param">files</h3>

<div class="param-level param-level-mandatory">Mandatory
</div>
<div class="param-type">Type: <a href="../converter/fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.Expression" class="converter">Expression</a>
</div>
Files to write. Evaluated as elements from the corpus. *RDFExport* creates a file for each element.

<h3 id="outDir" class="param">outDir</h3>

<div class="param-level param-level-mandatory">Mandatory
</div>
<div class="param-type">Type: <a href="../converter/fr.inra.maiage.bibliome.util.files.OutputDirectory" class="converter">OutputDirectory</a>
</div>
Base directory of output files.

<h3 id="statements" class="param">statements</h3>

<div class="param-level param-level-mandatory">Mandatory
</div>
<div class="param-type">Type: <a href="../converter/fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.Expression%5B%5D" class="converter">Expression[]</a>
</div>
Triplets to write in files.

## Optional parameters

<h3 id="charset" class="param">charset</h3>

<div class="param-level param-level-default-value">Default value: `UTF-8`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Charset to use.

<h3 id="format" class="param">format</h3>

<div class="param-level param-level-default-value">Default value: `RDF/XML/pretty`
</div>
<div class="param-type">Type: <a href="../converter/org.apache.jena.riot.RDFFormat" class="converter">RDFFormat</a>
</div>
Serialization format. Accepted values are listed [here](https://javadoc.io/doc/org.apache.jena/jena-arq/latest/org.apache.jena.arq/org/apache/jena/riot/RDFFormat.html) .

<h3 id="prefixes" class="param">prefixes</h3>

<div class="param-level param-level-default-value">Default value: `{}`
</div>
<div class="param-type">Type: <a href="../converter/fr.inra.maiage.bibliome.alvisnlp.core.module.types.Mapping" class="converter">Mapping</a>
</div>
Map of URI prefixes. Prefixes declared in this parameter are visible as libraries in <a href="#statements" class="param">statements</a> . The following prefixes do not need to be specified: `rdf` , `rdfs` , `dc` , `rss` , `owl` .

## Deprecated parameters

