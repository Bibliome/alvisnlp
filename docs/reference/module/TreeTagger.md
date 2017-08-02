# org.bibliome.alvisnlp.modules.treetagger.TreeTagger

## Synopsis

Runs *tree-tagger*.

## Description

*org.bibliome.alvisnlp.modules.treetagger.TreeTagger* applies *tree-tagger* on annotations in [wordLayerName](#wordLayerName) by generating an appropriate input file. This file will contain one line for each annotation. The first column, the token surface form, is the value of the [formFeature](#formFeature) feature. The second column, the token predefined POS tag, is the value [posFeature](#posFeature) feature. The third column, the token predefined lemma, is the value of [lemmaFeature](#lemmaFeature) feature. If [posFeature](#posFeature) or [lemmaFeature](#lemmaFeature) are not defined, then the second and third column are left blank.

The *tree-tagger* binary is specified by [treeTaggerExecutable](#treeTaggerExecutable) and the language model to use is specified by [parFile](#parFile). Additionally a lexicon file can be given through [lexiconFile](#lexiconFile).

If [sentenceLayerName](#sentenceLayerName) is defined, then *org.bibliome.alvisnlp.modules.treetagger.TreeTagger* considers annotations in this layer as sentences. Sentence boundaries are reinforced by providing *tree-tagger* an additional end-of-sentence marker.

Once *tree-tagger* has processed the corpus, *org.bibliome.alvisnlp.modules.treetagger.TreeTagger* adds the predicted POS tag and lemma to the respective [posFeature](#posFeature) and [lemmaFeature](#lemmaFeature) features of the corresponding annotations.

If [recordDir](#recordDir) and [recordFeatures](#recordFeatures) are both defined, then *tree-tagger* predictions are written into files in one file per section in the [recordDir](#recordDir) directory. [recordFeatures](#recordFeatures) is an array of feature names to record. An additional feature *n* is recognized as the annotation ordinal in the section.

## Parameters

<a name="parFile">

### parFile

Optional

Type: [InputFile](../converter/org.bibliome.util.files.InputFile)

Path to the language model file.

<a name="treeTaggerExecutable">

### treeTaggerExecutable

Optional

Type: [ExecutableFile](../converter/org.bibliome.util.files.ExecutableFile)

Path to the tree-tagger executable file.

<a name="constantAnnotationFeatures">

### constantAnnotationFeatures

Optional

Type: [Mapping](../converter/alvisnlp.module.types.Mapping)

Constant features to add to each annotation created by this module

<a name="lexiconFile">

### lexiconFile

Optional

Type: [SourceStream](../converter/org.bibliome.util.streams.SourceStream)

Path to a tree-tagger lexicon file, if set the lexicon will be applied to the corpus before treetagger processes it.

<a name="recordDir">

### recordDir

Optional

Type: [OutputDirectory](../converter/org.bibliome.util.files.OutputDirectory)

Path to the directory where to write tree-tagger result files (one file per section).

<a name="recordFeatures">

### recordFeatures

Optional

Type: [String[]](../converter/java.lang.String[])

List of attributes to display in result files.

<a name="documentFilter">

### documentFilter

Default value: `true`

Type: [Expression](../converter/alvisnlp.corpus.expressions.Expression)

Only process document that satisfy this filter.

<a name="formFeature">

### formFeature

Default value: `form`

Type: [String](../converter/java.lang.String)

Name of the feature denoting the token surface form.

<a name="inputCharset">

### inputCharset

Default value: `ISO-8859-1`

Type: [String](../converter/java.lang.String)

Tree-tagger input corpus character set.

<a name="lemmaFeature">

### lemmaFeature

Default value: `lemma`

Type: [String](../converter/java.lang.String)

Name of the feature to set with the lemma.

<a name="noUnknownLemma">

### noUnknownLemma

Default value: `false`

Type: [Boolean](../converter/java.lang.Boolean)

Either to replace unknown lemmas with the surface form.

<a name="outputCharset">

### outputCharset

Default value: `ISO-8859-1`

Type: [String](../converter/java.lang.String)

Tree-tagger output character set.

<a name="posFeature">

### posFeature

Default value: `pos`

Type: [String](../converter/java.lang.String)

Name of the feature to set with the POS tag.

<a name="recordCharset">

### recordCharset

Default value: `UTF-8`

Type: [String](../converter/java.lang.String)

Character encoding of the result files.

<a name="sectionFilter">

### sectionFilter

Default value: `true`

Type: [Expression](../converter/alvisnlp.corpus.expressions.Expression)

Process only sections that satisfy this filter.

<a name="sentenceLayerName">

### sentenceLayerName

Default value: `sentences`

Type: [String](../converter/java.lang.String)

Name of the layer containing sentence annotations, sentences are reinforced.

<a name="wordLayerName">

### wordLayerName

Default value: `words`

Type: [String](../converter/java.lang.String)

Name of the layer containing the word annotations.

