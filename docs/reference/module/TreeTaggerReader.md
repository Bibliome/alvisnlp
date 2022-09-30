<h1 class="module">TreeTaggerReader</h1>

## Synopsis

Read files in tree-tagger output format and creates a document for each file read.

## Description

Each document contains a single section named <a href="#sectionName" class="param">sectionName</a> ; its contents is constructed by concatenating the first column of each token separated with a space character.

 *TreeTaggerReader* keeps the tree-tagger tokenization in annotations added into the layer <a href="#wordLayerName" class="param">wordLayerName</a> . The POS tag and lemma are recorded in the annotation's <a href="#posFeatureKey" class="param">posFeatureKey</a> and <a href="#lemmaFeatureKey" class="param">lemmaFeatureKey</a> features respectively.

The document identifier is the path of the corresponding file.

## Snippet



<button class="copy-code-button" title="Copy to clipboard" onclick="copy_code(this)">📋</button>
```xml
<treetaggerreader class="TreeTaggerReader>
    <sourcePath></sourcePath>
</treetaggerreader>
```

## Mandatory parameters

<h3 id="sourcePath" class="param">sourcePath</h3>

<div class="param-level param-level-mandatory">Mandatory
</div>
<div class="param-type">Type: <a href="../converter/fr.inra.maiage.bibliome.util.streams.SourceStream" class="converter">SourceStream</a>
</div>
Path to the source directory or source file.

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

<h3 id="constantSectionFeatures" class="param">constantSectionFeatures</h3>

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/fr.inra.maiage.bibliome.alvisnlp.core.module.types.Mapping" class="converter">Mapping</a>
</div>
Constant features to add to each section created by this module.

<h3 id="lemmaFeatureKey" class="param">lemmaFeatureKey</h3>

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Name of the feature where to store word lemmas.

<h3 id="posFeatureKey" class="param">posFeatureKey</h3>

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Name of the feature where to store word POS tags.

<h3 id="charset" class="param">charset</h3>

<div class="param-level param-level-default-value">Default value: `UTF-8`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Character set of input files.

<h3 id="sectionName" class="param">sectionName</h3>

<div class="param-level param-level-default-value">Default value: `text`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Name of the section of each document.

<h3 id="sentenceLayerName" class="param">sentenceLayerName</h3>

<div class="param-level param-level-default-value">Default value: `sentences`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Name of the layer where to store sentence annotations.

<h3 id="wordLayerName" class="param">wordLayerName</h3>

<div class="param-level param-level-default-value">Default value: `words`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Name of the layer where to store word annotations.

