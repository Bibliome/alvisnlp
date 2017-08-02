# org.bibliome.alvisnlp.modules.yatea.YateaExtractor

## Synopsis

Extract terms from the corpus using the YaTeA term extractor.

## Description

*org.bibliome.alvisnlp.modules.yatea.YateaExtractor* hands the corpus to the [YaTeA](http://search.cpan.org/~thhamon/Lingua-YaTeA) extractor. The corpus is first written in a file in the YaTeA input format. Tokens are annotations in the layer [wordLayerName](#wordLayerName), their surface form, POS tag and lemma are taken from [formFeature](#formFeature), [posFeature](#posFeature) and [lemmaFeature](#lemmaFeature) features respectively. If [sentenceLayerName](#sentenceLayerName) is set, then an additional *SENT* marker is added to reinforce sentence boundaries corresponding to annotations in this layer.

The YaTeA is called using the executable set in [yateaExecutable](#yateaExecutable), it will run as if it is called from directory [workingDir](#workingDir): the result will be written in the subdirectory named [corpusName](#corpusName).

## Parameters

<a name="rcFile">

### rcFile

Optional

Type: [SourceStream](../converter/org.bibliome.util.streams.SourceStream)

Path to the YaTeA configuration file.

<a name="workingDir">

### workingDir

Optional

Type: [WorkingDirectory](../converter/org.bibliome.util.files.WorkingDirectory)

Path to the directory where YaTeA is launched.

<a name="yateaExecutable">

### yateaExecutable

Optional

Type: [ExecutableFile](../converter/org.bibliome.util.files.ExecutableFile)

Path to the YaTeA executable file.

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

BioYaTeA option: path to the post-processing file option.

<a name="postProcessingOutput">

### postProcessingOutput

Optional

Type: [OutputFile](../converter/org.bibliome.util.files.OutputFile)

BioYaTeA option: path to the result file after post-processing.

<a name="suffix">

### suffix

Optional

Type: [String](../converter/java.lang.String)



<a name="testifiedTerminology">

### testifiedTerminology

Optional

Type: [TestifiedTerminology](../converter/org.bibliome.alvisnlp.modules.yatea.TestifiedTerminology)



<a name="bioYatea">

### bioYatea

Default value: `false`

Type: [Boolean](../converter/java.lang.Boolean)



<a name="documentFilter">

### documentFilter

Default value: `true`

Type: [Expression](../converter/alvisnlp.corpus.expressions.Expression)

Only process document that satisfy this filter.

<a name="documentTokens">

### documentTokens

Default value: `true`

Type: [Boolean](../converter/java.lang.Boolean)

Either to write DOCUMENT special tokens. Not every YaTeA version accepts them.

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



