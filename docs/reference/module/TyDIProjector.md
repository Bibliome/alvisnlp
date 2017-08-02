# org.bibliome.alvisnlp.modules.projectors.TyDIProjector

## Synopsis

Projects terms from a TiDI export.

**This module is obsolete, superceded by org.bibliome.alvisnlp.modules.trie.TyDIExportProjector**

## Description

*org.bibliome.alvisnlp.modules.projectors.TyDIProjector* reads different files from a [TyDI](https://migale.jouy.inra.fr/redmine/projects/tydi) text export, resolves all synonymies and projects the terms into sections.

The parameters [lemmaFile](#lemmaFile), [synonymsFile](#synonymsFile), [quasiSynonymsFile](#quasiSynonymsFile), [acronymsFile](#acronymsFile) and [typographicVariationsFile](#typographicVariationsFile) point to the paths to the corresponding TyDI file export.

The parameters [normalizeSpace](#normalizeSpace), [ignoreCase](#ignoreCase), [ignoreDiacritics](#ignoreDiacritics) and [ignoreWhitespace](#ignoreWhitespace) control the matching of entries on the sections.

The [subject](#subject) parameter specifies which text of the section should be matched. There are two options:
      
* the entries are matched on the contents of the section, [subject](#subject) can also control if matches boundaries coincide with word delimiters;
* the entries are matched on the feature value of annotations of a given layer separated by a whitespace, in this way entries can be searched against word lemmas for instance.



*org.bibliome.alvisnlp.modules.projectors.TyDIProjector* creates an annotation for each matched entry and adds these annotations to the layer named [targetLayerName](#targetLayerName). The created annotations will have a feature named [canonicalFormFeature](#canonicalFormFeature) containing the canonical form of the matched term. In addition, the created annotations will have the feature keys and values defined in [constantAnnotationFeatures](#constantAnnotationFeatures).

## Parameters

<a name="lemmaFile">

### lemmaFile

Optional

Type: [SourceStream](../converter/org.bibliome.util.streams.SourceStream)

Path to the file containing lemmas.

<a name="mergeFile">

### mergeFile

Optional

Type: [SourceStream](../converter/org.bibliome.util.streams.SourceStream)

Path to the merged terms file.

<a name="quasiSynonymsFile">

### quasiSynonymsFile

Optional

Type: [SourceStream](../converter/org.bibliome.util.streams.SourceStream)

Path to the quasi-synonyms file.

<a name="synonymsFile">

### synonymsFile

Optional

Type: [SourceStream](../converter/org.bibliome.util.streams.SourceStream)

Path to the synonyms file.

<a name="targetLayerName">

### targetLayerName

Optional

Type: [String](../converter/java.lang.String)

Name of the layer where to put match annotations.

<a name="acronymsFile">

### acronymsFile

Optional

Type: [SourceStream](../converter/org.bibliome.util.streams.SourceStream)

Path to the acronyms file.

<a name="constantAnnotationFeatures">

### constantAnnotationFeatures

Optional

Type: [Mapping](../converter/alvisnlp.module.types.Mapping)

Constant features to add to each annotation created by this module

<a name="saveDictFile">

### saveDictFile

Optional

Type: [TargetStream](../converter/org.bibliome.util.streams.TargetStream)

Path of the file where to save the dictionary.

<a name="typographicVariationsFile">

### typographicVariationsFile

Optional

Type: [SourceStream](../converter/org.bibliome.util.streams.SourceStream)

Path to the typographic variations file.

<a name="canonicalFormFeature">

### canonicalFormFeature

Default value: `lemma`

Type: [String](../converter/java.lang.String)

Feature where to store the term canonical form.

<a name="documentFilter">

### documentFilter

Default value: `true`

Type: [Expression](../converter/alvisnlp.corpus.expressions.Expression)

Only process document that satisfy this filter.

<a name="errorDuplicateValues">

### errorDuplicateValues

Default value: `false`

Type: [Boolean](../converter/java.lang.Boolean)

Either to stop when a duplicate entry is seen.

<a name="ignoreCase">

### ignoreCase

Default value: `false`

Type: [Boolean](../converter/java.lang.Boolean)

Match ignoring case.

<a name="ignoreDiacritics">

### ignoreDiacritics

Default value: `false`

Type: [Boolean](../converter/java.lang.Boolean)

Match ignoring diacritics.

<a name="ignoreWhitespace">

### ignoreWhitespace

Default value: `false`

Type: [Boolean](../converter/java.lang.Boolean)

Match ignoring whitespace characters.

<a name="multipleValueAction">

### multipleValueAction

Default value: `add`

Type: [MultipleValueAction](../converter/org.bibliome.alvisnlp.modules.projectors.MultipleValueAction)

Either to stop when multiple entries with the same key is seen.

<a name="normalizeSpace">

### normalizeSpace

Default value: `false`

Type: [Boolean](../converter/java.lang.Boolean)

Match normalizing whitespace.

<a name="sectionFilter">

### sectionFilter

Default value: `true`

Type: [Expression](../converter/alvisnlp.corpus.expressions.Expression)

Process only sections that satisfy this filter.

<a name="subject">

### subject

Default value: `org.bibliome.alvisnlp.modules.projectors.ContentsSubject@4d9e68d0`

Type: [Subject](../converter/org.bibliome.alvisnlp.modules.projectors.Subject)

Subject on which to project the dictionary.

