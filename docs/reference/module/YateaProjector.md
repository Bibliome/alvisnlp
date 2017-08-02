# org.bibliome.alvisnlp.modules.projectors.YateaProjector

## Synopsis

synopsis

**This module is obsolete, superceded by org.bibliome.alvisnlp.modules.yatea.YateaTermsProjector**

## Description

synopsis

## Parameters

<a name="targetLayerName">

### targetLayerName

Optional

Type: [String](../converter/java.lang.String)

Name of the layer where to put match annotations.

<a name="yateaFile">

### yateaFile

Optional

Type: [SourceStream](../converter/org.bibliome.util.streams.SourceStream)



<a name="constantAnnotationFeatures">

### constantAnnotationFeatures

Optional

Type: [Mapping](../converter/alvisnlp.module.types.Mapping)

Constant features to add to each annotation created by this module

<a name="termLemma">

### termLemma

Optional

Type: [String](../converter/java.lang.String)



<a name="termPOS">

### termPOS

Optional

Type: [String](../converter/java.lang.String)



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

<a name="head">

### head

Default value: `head`

Type: [String](../converter/java.lang.String)



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

<a name="mnpOnly">

### mnpOnly

Default value: `false`

Type: [Boolean](../converter/java.lang.Boolean)



<a name="modifier">

### modifier

Default value: `modifier`

Type: [String](../converter/java.lang.String)



<a name="monoHeadId">

### monoHeadId

Default value: `mono-head`

Type: [String](../converter/java.lang.String)



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

<a name="projectLemmas">

### projectLemmas

Default value: `false`

Type: [Boolean](../converter/java.lang.Boolean)



<a name="sectionFilter">

### sectionFilter

Default value: `true`

Type: [Expression](../converter/alvisnlp.corpus.expressions.Expression)

Process only sections that satisfy this filter.

<a name="subject">

### subject

Default value: `org.bibliome.alvisnlp.modules.projectors.ContentsSubject@40ef3420`

Type: [Subject](../converter/org.bibliome.alvisnlp.modules.projectors.Subject)

Subject on which to project the dictionary.

<a name="termId">

### termId

Default value: `term-id`

Type: [String](../converter/java.lang.String)



