<h1 class="module">EnrichedDocumentWriter</h1>

## Synopsis

Writes the corpus in the infamous Alvis Enriched Document Format suitable for indexation with Zebra-Alvis.

## Description

Writes the corpus in the infamous Alvis Enriched Document Format suitable for indexation with Zebra-Alvis.

## Parameters

<a name="idMetaFeature">

### idMetaFeature

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Metadata key for the document id.

<a name="metaTrans">

### metaTrans

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/alvisnlp.module.types.Mapping" class="converter">Mapping</a>
</div>
Metadata key translation.

<a name="neLayerName">

### neLayerName

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Name of the layer containing named entity annotations.

<a name="outDir">

### outDir

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/org.bibliome.util.files.OutputDirectory" class="converter">OutputDirectory</a>
</div>
Path to the directory where to write files.

<a name="outFilePrefix">

### outFilePrefix

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Prefix of the name of generated files.

<a name="termCanonicalFormFeature">

### termCanonicalFormFeature

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Name of the feature containing the term canonical form.

<a name="termLayerName">

### termLayerName

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Name of the layer containing the term annotations.

<a name="tokenLayerName">

### tokenLayerName

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Name of the layer containing token annotations.

<a name="tokenTypeFeature">

### tokenTypeFeature

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Name of the feature in token annotations containing the token type.

<a name="urlPrefix">

### urlPrefix

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Prefix for the document URL.

<a name="semanticFeature">

### semanticFeature

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Name of the feature containing semantic features of named entities and terms.

<a name="blockSize">

### blockSize

<div class="param-level param-level-default-value">Default value: `100`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.Integer" class="converter">Integer</a>
</div>
Number of documents in each document block.

<a name="blockStart">

### blockStart

<div class="param-level param-level-default-value">Default value: `0`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.Integer" class="converter">Integer</a>
</div>
Start point for document block numbering.

<a name="documentFilter">

### documentFilter

<div class="param-level param-level-default-value">Default value: `true`
</div>
<div class="param-type">Type: <a href="../converter/alvisnlp.corpus.expressions.Expression" class="converter">Expression</a>
</div>
Only process document that satisfy this filter.

<a name="lemmaFeature">

### lemmaFeature

<div class="param-level param-level-default-value">Default value: `lemma`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Name of the feature in word annotations containing the lemma.

<a name="neCanonicalFormFeature">

### neCanonicalFormFeature

<div class="param-level param-level-default-value">Default value: `lemma`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Name of the feature in named entity annotations containing the canonical form.

<a name="neTypeFeature">

### neTypeFeature

<div class="param-level param-level-default-value">Default value: `neType`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Name of the feature in named entity annotations containing the named entity type.

<a name="outFileSuffix">

### outFileSuffix

<div class="param-level param-level-default-value">Default value: `.sem`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Suffix of the name of generated files.

<a name="posFeature">

### posFeature

<div class="param-level param-level-default-value">Default value: `pos`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Name of the feature in word annotations containing the POS tag.

<a name="sectionFilter">

### sectionFilter

<div class="param-level param-level-default-value">Default value: `true`
</div>
<div class="param-type">Type: <a href="../converter/alvisnlp.corpus.expressions.Expression" class="converter">Expression</a>
</div>
Process only sections that satisfy this filter.

<a name="sentenceLayerName">

### sentenceLayerName

<div class="param-level param-level-default-value">Default value: `sentences`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Name of the layer containing sentence annotations.

<a name="urlSuffixFeature">

### urlSuffixFeature

<div class="param-level param-level-default-value">Default value: `id`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Document feature to use as the URL suffix.

<a name="wordLayerName">

### wordLayerName

<div class="param-level param-level-default-value">Default value: `words`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Name of the layer containing word annotations.

