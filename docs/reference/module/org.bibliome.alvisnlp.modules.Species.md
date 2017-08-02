# org.bibliome.alvisnlp.modules.Species

## Synopsis

Calls the [Species]() taxon tagger.

**This module is experimental.**

## Description

*org.bibliome.alvisnlp.modules.Species* applies the [Species](XXX) taxon tagger on the contents of the sections. The *Species* software must be installed in the [speciesDir](#speciesDir) directory. *org.bibliome.alvisnlp.modules.Species* crerates an annotation for each taxon tagged by *Species*.

## Parameters

<a name="speciesDir">

### speciesDir

Optional

Type: [InputDirectory](../converter/org.bibliome.util.files.InputDirectory)

Directory where the *Species* software is installed.

<a name="targetLayerName">

### targetLayerName

Optional

Type: [String](../converter/java.lang.String)

Layer where to store the tagged taxon names.

<a name="constantAnnotationFeatures">

### constantAnnotationFeatures

Optional

Type: [Mapping](../converter/alvisnlp.module.types.Mapping)

Constant features to add to each annotation created by this module

<a name="taxidFeature">

### taxidFeature

Optional

Type: [String](../converter/java.lang.String)

Feature where to store the identifier of the tagged taxon.

<a name="documentFilter">

### documentFilter

Default value: `true`

Type: [Expression](../converter/alvisnlp.corpus.expressions.Expression)

Only process document that satisfy this filter.

<a name="sectionFilter">

### sectionFilter

Default value: `true`

Type: [Expression](../converter/alvisnlp.corpus.expressions.Expression)

Process only sections that satisfy this filter.

