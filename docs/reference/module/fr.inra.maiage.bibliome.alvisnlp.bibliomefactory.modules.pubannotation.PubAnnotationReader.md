<h1 class="module">PubAnnotationReader</h1>

## Synopsis

Read stream in [PubAnnotation JSON format](http://www.pubannotation.org/docs/annotation-format/) .

**This module is experimental.**

## Description

 *PubAnnotationReader* reads the contents of <a href="#source" class="param">source</a> in [PubAnnotation JSON format](http://www.pubannotation.org/docs/annotation-format/) . The following conversions are observed:
* single-span denotations are converted as annotations in a layer named as the denotation object;
* multi-span denotations are converted as tuples, in a relation named as the denotation object, with one argument for each fragment with roles `fragN` ;
* relations are converted as tuples, in a relation named as the relation predicate, with two arguments;
* modifications are converted as additional features on the element representing the modification object.



## Snippet



<button class="copy-code-button" title="Copy to clipboard" onclick="copy_code(this)">📋</button>
```xml
<pubannotationreader class="PubAnnotationReader">
    <source></source>
</pubannotationreader>
```

## Mandatory parameters

<h3 id="source" class="param">source</h3>

<div class="param-level param-level-mandatory">Mandatory
</div>
<div class="param-type">Type: <a href="../converter/fr.inra.maiage.bibliome.util.streams.SourceStream" class="converter">SourceStream</a>
</div>
Source stream in PubAnnotation JSON format.

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

## Deprecated parameters

