# org.bibliome.alvisnlp.modules.EnrichedDocumentWriter

## Synopsis

Writes the corpus in the infamous Alvis Enriched Document Format suitable for indexation with Zebra-Alvis.

## Description

Writes the corpus in the infamous Alvis Enriched Document Format suitable for indexation with Zebra-Alvis.

## Parameters

<a name="idMetaFeature">

### idMetaFeature

Optional

Type: [String](../converter/java.lang.String)

Metadata key for the document id.

<a name="metaTrans">

### metaTrans

Optional

Type: [Mapping](../converter/alvisnlp.module.types.Mapping)

Metadata key translation.

<a name="neLayerName">

### neLayerName

Optional

Type: [String](../converter/java.lang.String)

Name of the layer containing named entity annotations.

<a name="outDir">

### outDir

Optional

Type: [OutputDirectory](../converter/org.bibliome.util.files.OutputDirectory)

Path to the directory where to write files.

<a name="outFilePrefix">

### outFilePrefix

Optional

Type: [String](../converter/java.lang.String)

Prefix of the name of generated files.

<a name="termCanonicalFormFeature">

### termCanonicalFormFeature

Optional

Type: [String](../converter/java.lang.String)

Name of the feature containing the term canonical form.

<a name="termLayerName">

### termLayerName

Optional

Type: [String](../converter/java.lang.String)

Name of the layer containing the term annotations.

<a name="tokenLayerName">

### tokenLayerName

Optional

Type: [String](../converter/java.lang.String)

Name of the layer containing token annotations.

<a name="tokenTypeFeature">

### tokenTypeFeature

Optional

Type: [String](../converter/java.lang.String)

Name of the feature in token annotations containing the token type.

<a name="urlPrefix">

### urlPrefix

Optional

Type: [String](../converter/java.lang.String)

Prefix for the document URL.

<a name="semanticFeature">

### semanticFeature

Optional

Type: [String](../converter/java.lang.String)

Name of the feature containing semantic features of named entities and terms.

<a name="blockSize">

### blockSize

Default value: `100`

Type: [Integer](../converter/java.lang.Integer)

Number of documents in each document block.

<a name="blockStart">

### blockStart

Default value: `0`

Type: [Integer](../converter/java.lang.Integer)

Start point for document block numbering.

<a name="documentFilter">

### documentFilter

Default value: `true`

Type: [Expression](../converter/alvisnlp.corpus.expressions.Expression)

Only process document that satisfy this filter.

<a name="lemmaFeature">

### lemmaFeature

Default value: `lemma`

Type: [String](../converter/java.lang.String)

Name of the feature in word annotations containing the lemma.

<a name="neCanonicalFormFeature">

### neCanonicalFormFeature

Default value: `lemma`

Type: [String](../converter/java.lang.String)

Name of the feature in named entity annotations containing the canonical form.

<a name="neTypeFeature">

### neTypeFeature

Default value: `neType`

Type: [String](../converter/java.lang.String)

Name of the feature in named entity annotations containing the named entity type.

<a name="outFileSuffix">

### outFileSuffix

Default value: `.sem`

Type: [String](../converter/java.lang.String)

Suffix of the name of generated files.

<a name="posFeature">

### posFeature

Default value: `pos`

Type: [String](../converter/java.lang.String)

Name of the feature in word annotations containing the POS tag.

<a name="sectionFilter">

### sectionFilter

Default value: `true`

Type: [Expression](../converter/alvisnlp.corpus.expressions.Expression)

Process only sections that satisfy this filter.

<a name="sentenceLayerName">

### sentenceLayerName

Default value: `sentences`

Type: [String](../converter/java.lang.String)

Name of the layer containing sentence annotations.

<a name="urlSuffixFeature">

### urlSuffixFeature

Default value: `id`

Type: [String](../converter/java.lang.String)

Document feature to use as the URL suffix.

<a name="wordLayerName">

### wordLayerName

Default value: `words`

Type: [String](../converter/java.lang.String)

Name of the layer containing word annotations.

