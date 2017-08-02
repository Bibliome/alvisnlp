# org.bibliome.alvisnlp.modules.tomap.TomapTrain

## Synopsis

synopsis

**This module is experimental.**

## Description

synopsis

## Parameters

<a name="conceptIdentifier">

### conceptIdentifier

Optional

Type: [Expression](../converter/alvisnlp.corpus.expressions.Expression)



<a name="outFile">

### outFile

Optional

Type: [TargetStream](../converter/org.bibliome.util.streams.TargetStream)



<a name="rcFile">

### rcFile

Optional

Type: [SourceStream](../converter/org.bibliome.util.streams.SourceStream)



<a name="workingDir">

### workingDir

Optional

Type: [WorkingDirectory](../converter/org.bibliome.util.files.WorkingDirectory)



<a name="yateaExecutable">

### yateaExecutable

Optional

Type: [ExecutableFile](../converter/org.bibliome.util.files.ExecutableFile)



<a name="configDir">

### configDir

Optional

Type: [InputDirectory](../converter/org.bibliome.util.files.InputDirectory)



<a name="language">

### language

Optional

Type: [String](../converter/java.lang.String)



<a name="localeDir">

### localeDir

Optional

Type: [InputDirectory](../converter/org.bibliome.util.files.InputDirectory)



<a name="outputDir">

### outputDir

Optional

Type: [OutputDirectory](../converter/org.bibliome.util.files.OutputDirectory)



<a name="perlLib">

### perlLib

Optional

Type: [String](../converter/java.lang.String)

Contents of the PERLLIB in the environment of Yatea binary.

<a name="postProcessingConfig">

### postProcessingConfig

Optional

Type: [InputFile](../converter/org.bibliome.util.files.InputFile)



<a name="postProcessingOutput">

### postProcessingOutput

Optional

Type: [OutputFile](../converter/org.bibliome.util.files.OutputFile)



<a name="suffix">

### suffix

Optional

Type: [String](../converter/java.lang.String)



<a name="bioYatea">

### bioYatea

Default value: `false`

Type: [Boolean](../converter/java.lang.Boolean)



<a name="documentFilter">

### documentFilter

Default value: `true`

Type: [Expression](../converter/alvisnlp.corpus.expressions.Expression)

Only process document that satisfy this filter.

<a name="formFeature">

### formFeature

Default value: `form`

Type: [String](../converter/java.lang.String)

Feature containing the word form.

<a name="lemmaFeature">

### lemmaFeature

Default value: `lemma`

Type: [String](../converter/java.lang.String)

Feature containing the word lemma.

<a name="posFeature">

### posFeature

Default value: `pos`

Type: [String](../converter/java.lang.String)

Feature containing the word POS tag.

<a name="sectionFilter">

### sectionFilter

Default value: `boolean:and(true, nav:layer:words())`

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

<a name="yateaDefaultConfig">

### yateaDefaultConfig

Default value: `{}`

Type: [Mapping](../converter/alvisnlp.module.types.Mapping)



<a name="yateaOptions">

### yateaOptions

Default value: `{}`

Type: [Mapping](../converter/alvisnlp.module.types.Mapping)



