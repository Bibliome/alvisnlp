<h1 class="module">EnrichedDocumentWriter</h1>

## Synopsis

Writes the corpus in the infamous Alvis Enriched Document Format suitable for indexation with Zebra-Alvis.

## Description

Writes the corpus in the infamous Alvis Enriched Document Format suitable for indexation with Zebra-Alvis.

## Snippet



```xml
<enricheddocumentwriter class="EnrichedDocumentWriter>
    <idMetaFeature></idMetaFeature>
    <metaTrans></metaTrans>
    <neLayerName></neLayerName>
    <outDir></outDir>
    <outFilePrefix></outFilePrefix>
    <termCanonicalFormFeature></termCanonicalFormFeature>
    <termLayerName></termLayerName>
    <tokenLayerName></tokenLayerName>
    <tokenTypeFeature></tokenTypeFeature>
    <urlPrefix></urlPrefix>
</enricheddocumentwriter>
```

## Mandatory parameters

<h3 id="idMetaFeature" class="param">idMetaFeature</h3>

<div class="param-level param-level-mandatory">Mandatory
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Metadata key for the document id.

<h3 id="metaTrans" class="param">metaTrans</h3>

<div class="param-level param-level-mandatory">Mandatory
</div>
<div class="param-type">Type: <a href="../converter/fr.inra.maiage.bibliome.alvisnlp.core.module.types.Mapping" class="converter">Mapping</a>
</div>
Metadata key translation.

<h3 id="neLayerName" class="param">neLayerName</h3>

<div class="param-level param-level-mandatory">Mandatory
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Name of the layer containing named entity annotations.

<h3 id="outDir" class="param">outDir</h3>

<div class="param-level param-level-mandatory">Mandatory
</div>
<div class="param-type">Type: <a href="../converter/fr.inra.maiage.bibliome.util.files.OutputDirectory" class="converter">OutputDirectory</a>
</div>
Path to the directory where to write files.

<h3 id="outFilePrefix" class="param">outFilePrefix</h3>

<div class="param-level param-level-mandatory">Mandatory
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Prefix of the name of generated files.

<h3 id="termCanonicalFormFeature" class="param">termCanonicalFormFeature</h3>

<div class="param-level param-level-mandatory">Mandatory
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Name of the feature containing the term canonical form.

<h3 id="termLayerName" class="param">termLayerName</h3>

<div class="param-level param-level-mandatory">Mandatory
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Name of the layer containing the term annotations.

<h3 id="tokenLayerName" class="param">tokenLayerName</h3>

<div class="param-level param-level-mandatory">Mandatory
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Name of the layer containing token annotations.

<h3 id="tokenTypeFeature" class="param">tokenTypeFeature</h3>

<div class="param-level param-level-mandatory">Mandatory
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Name of the feature in token annotations containing the token type.

<h3 id="urlPrefix" class="param">urlPrefix</h3>

<div class="param-level param-level-mandatory">Mandatory
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Prefix for the document URL.

## Optional parameters

<h3 id="semanticFeature" class="param">semanticFeature</h3>

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Name of the feature containing semantic features of named entities and terms.

<h3 id="blockSize" class="param">blockSize</h3>

<div class="param-level param-level-default-value">Default value: `100`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.Integer" class="converter">Integer</a>
</div>
Number of documents in each document block.

<h3 id="blockStart" class="param">blockStart</h3>

<div class="param-level param-level-default-value">Default value: `0`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.Integer" class="converter">Integer</a>
</div>
Start point for document block numbering.

<h3 id="documentFilter" class="param">documentFilter</h3>

<div class="param-level param-level-default-value">Default value: `true`
</div>
<div class="param-type">Type: <a href="../converter/fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.Expression" class="converter">Expression</a>
</div>
Only process document that satisfy this filter.

<h3 id="lemmaFeature" class="param">lemmaFeature</h3>

<div class="param-level param-level-default-value">Default value: `lemma`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Name of the feature in word annotations containing the lemma.

<h3 id="neCanonicalFormFeature" class="param">neCanonicalFormFeature</h3>

<div class="param-level param-level-default-value">Default value: `lemma`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Name of the feature in named entity annotations containing the canonical form.

<h3 id="neTypeFeature" class="param">neTypeFeature</h3>

<div class="param-level param-level-default-value">Default value: `ne-type`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Name of the feature in named entity annotations containing the named entity type.

<h3 id="outFileSuffix" class="param">outFileSuffix</h3>

<div class="param-level param-level-default-value">Default value: `.sem`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Suffix of the name of generated files.

<h3 id="posFeature" class="param">posFeature</h3>

<div class="param-level param-level-default-value">Default value: `pos`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Name of the feature in word annotations containing the POS tag.

<h3 id="sectionFilter" class="param">sectionFilter</h3>

<div class="param-level param-level-default-value">Default value: `true`
</div>
<div class="param-type">Type: <a href="../converter/fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.Expression" class="converter">Expression</a>
</div>
Process only sections that satisfy this filter.

<h3 id="sentenceLayerName" class="param">sentenceLayerName</h3>

<div class="param-level param-level-default-value">Default value: `sentences`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Name of the layer containing sentence annotations.

<h3 id="urlSuffixFeature" class="param">urlSuffixFeature</h3>

<div class="param-level param-level-default-value">Default value: `id`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Document feature to use as the URL suffix.

<h3 id="wordLayerName" class="param">wordLayerName</h3>

<div class="param-level param-level-default-value">Default value: `words`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Name of the layer containing word annotations.

