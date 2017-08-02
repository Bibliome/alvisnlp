<h1 class="module">TomapTrain</h1>

## Synopsis

synopsis

**This module is experimental.**

## Description

synopsis

## Parameters

<a name="conceptIdentifier">

### conceptIdentifier

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/alvisnlp.corpus.expressions.Expression" class="converter">Expression</a>
</div>


<a name="outFile">

### outFile

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/org.bibliome.util.streams.TargetStream" class="converter">TargetStream</a>
</div>


<a name="rcFile">

### rcFile

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/org.bibliome.util.streams.SourceStream" class="converter">SourceStream</a>
</div>


<a name="workingDir">

### workingDir

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/org.bibliome.util.files.WorkingDirectory" class="converter">WorkingDirectory</a>
</div>


<a name="yateaExecutable">

### yateaExecutable

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/org.bibliome.util.files.ExecutableFile" class="converter">ExecutableFile</a>
</div>


<a name="configDir">

### configDir

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/org.bibliome.util.files.InputDirectory" class="converter">InputDirectory</a>
</div>


<a name="language">

### language

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>


<a name="localeDir">

### localeDir

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/org.bibliome.util.files.InputDirectory" class="converter">InputDirectory</a>
</div>


<a name="outputDir">

### outputDir

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/org.bibliome.util.files.OutputDirectory" class="converter">OutputDirectory</a>
</div>


<a name="perlLib">

### perlLib

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Contents of the PERLLIB in the environment of Yatea binary.

<a name="postProcessingConfig">

### postProcessingConfig

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/org.bibliome.util.files.InputFile" class="converter">InputFile</a>
</div>


<a name="postProcessingOutput">

### postProcessingOutput

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/org.bibliome.util.files.OutputFile" class="converter">OutputFile</a>
</div>


<a name="suffix">

### suffix

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>


<a name="bioYatea">

### bioYatea

<div class="param-level param-level-default-value">Default value: `false`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.Boolean" class="converter">Boolean</a>
</div>


<a name="documentFilter">

### documentFilter

<div class="param-level param-level-default-value">Default value: `true`
</div>
<div class="param-type">Type: <a href="../converter/alvisnlp.corpus.expressions.Expression" class="converter">Expression</a>
</div>
Only process document that satisfy this filter.

<a name="formFeature">

### formFeature

<div class="param-level param-level-default-value">Default value: `form`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Feature containing the word form.

<a name="lemmaFeature">

### lemmaFeature

<div class="param-level param-level-default-value">Default value: `lemma`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Feature containing the word lemma.

<a name="posFeature">

### posFeature

<div class="param-level param-level-default-value">Default value: `pos`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Feature containing the word POS tag.

<a name="sectionFilter">

### sectionFilter

<div class="param-level param-level-default-value">Default value: `boolean:and(true, nav:layer:words())`
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
Name of the layer containing sentence annotations, sentences are reinforced.

<a name="wordLayerName">

### wordLayerName

<div class="param-level param-level-default-value">Default value: `words`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Name of the layer containing the word annotations.

<a name="yateaDefaultConfig">

### yateaDefaultConfig

<div class="param-level param-level-default-value">Default value: `{}`
</div>
<div class="param-type">Type: <a href="../converter/alvisnlp.module.types.Mapping" class="converter">Mapping</a>
</div>


<a name="yateaOptions">

### yateaOptions

<div class="param-level param-level-default-value">Default value: `{}`
</div>
<div class="param-type">Type: <a href="../converter/alvisnlp.module.types.Mapping" class="converter">Mapping</a>
</div>


