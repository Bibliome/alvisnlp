# org.bibliome.alvisnlp.modules.projectors.AttestedTermsProjector

## Synopsis

Projects a list of terms given in tree-tagger format.

**This module is obsolete, superceded by org.bibliome.alvisnlp.modules.treetagger.TreeTaggerTermsProjector**

## Description

*org.bibliome.alvisnlp.modules.projectors.AttestedTermsProjector* reads a list of terms from [termsFile](#termsFile) and searches for these terms in sections. The terms must be in tree-tagger format: each line contains a token/POS/lemma and each term is terminated by a period/*SENT*. The searched string for each term is the concatenation of token surface forms, or lemma if [lemmaKeys](#lemmaKeys) is true, separated with a space character.

The parameters [warnDuplicateValues](#warnDuplicateValues), [multipleValueAction](#multipleValueAction), [errorDuplicateValues](#errorDuplicateValues) and [warnMultipleValues](#warnMultipleValues) control who *org.bibliome.alvisnlp.modules.projectors.AttestedTermsProjector* reacts when encountering duplicate terms.

The parameters [normalizeSpace](#normalizeSpace), [ignoreCase](#ignoreCase), [ignoreDiacritics](#ignoreDiacritics) and [ignoreWhitespace](#ignoreWhitespace) control the matching of entries on the sections.

The [subject](#subject) parameter specifies which text of the section should be matched. There are two options:
      
* the entries are matched on the contents of the section, [subject](#subject) can also control if matches boundaries coincide with word delimiters;
* the entries are matched on the feature value of annotations of a given layer separated by a whitespace, in this way entries can be searched against word lemmas for instance.



*org.bibliome.alvisnlp.modules.projectors.AttestedTermsProjector* creates an annotation for each matched term and adds these annotations to the layer named [targetLayerName](#targetLayerName). The created annotations will have the features [termFeatureName](#termFeatureName), [posFeatureName](#posFeatureName) and [lemmaFeatureName](#lemmaFeatureName) containing the concatenation of the corresponding term tokens surface form, POS tag and lemma respectively. In addition, the created annotations will have the feature keys and values defined in [constantAnnotationFeatures](#constantAnnotationFeatures).

## Parameters

<a name="targetLayerName">

### targetLayerName

Optional

Type: [String](../converter/java.lang.String)

Name of the layer where to put match annotations.

<a name="termsFile">

### termsFile

Optional

Type: [SourceStream](../converter/org.bibliome.util.streams.SourceStream)

Attested terms file.

<a name="constantAnnotationFeatures">

### constantAnnotationFeatures

Optional

Type: [Mapping](../converter/alvisnlp.module.types.Mapping)

Constant features to add to each annotation created by this module

<a name="termFeatureName">

### termFeatureName

Optional

Type: [String](../converter/java.lang.String)

Name of the feature where to write the term form.

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

<a name="lemmaFeatureName">

### lemmaFeatureName

Default value: `lemma`

Type: [String](../converter/java.lang.String)

Name of the feature where to write the term lemma.

<a name="lemmaKeys">

### lemmaKeys

Default value: `true`

Type: [Boolean](../converter/java.lang.Boolean)

Either to project lemmas instead of the forms.

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

<a name="posFeatureName">

### posFeatureName

Default value: `pos`

Type: [String](../converter/java.lang.String)

Name of the feature where to write the term POS tags.

<a name="sectionFilter">

### sectionFilter

Default value: `true`

Type: [Expression](../converter/alvisnlp.corpus.expressions.Expression)

Process only sections that satisfy this filter.

<a name="subject">

### subject

Default value: `org.bibliome.alvisnlp.modules.projectors.ContentsSubject@6e171cd7`

Type: [Subject](../converter/org.bibliome.alvisnlp.modules.projectors.Subject)

Subject on which to project the dictionary.

