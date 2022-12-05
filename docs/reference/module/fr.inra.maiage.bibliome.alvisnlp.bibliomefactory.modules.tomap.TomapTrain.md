<h1 class="module">TomapTrain</h1>

## Synopsis

 *TomapTrain* analyzes terms in preparation of the classification of candidates with [ToMap](https://github.com/Bibliome/bibliome-java-utils/blob/master/src/main/java/fr/inra/maiage/bibliome/util/tomap/ToMap.md) .

## Description

 *TomapTrain* assumes each sentence or section is a proxy term according to the [ToMap](https://github.com/Bibliome/bibliome-java-utils/blob/master/src/main/java/fr/inra/maiage/bibliome/util/tomap/ToMap.md) method. *TomapTrain* analyzes the syntactic structure of sections and stores them in <a href="#outFile" class="param">outFile</a> . Use this file for classifying terms with <a href="../module/TomapProjector" class="module">TomapProjector</a> . The identifier associated with the proxy is specified with <a href="#conceptIdentifier" class="param">conceptIdentifier</a> .

 <a href="#conceptIdentifier" class="param">conceptIdentifier</a> is

## Snippet



<button class="copy-code-button" title="Copy to clipboard" onclick="copy_code(this)">📋</button>
```xml
<tomaptrain class="TomapTrain>
    <conceptIdentifier></conceptIdentifier>
    <outFile></outFile>
    <rcFile></rcFile>
    <yateaExecutable></yateaExecutable>
</tomaptrain>
```

## Mandatory parameters

<h3 id="conceptIdentifier" class="param">conceptIdentifier</h3>

<div class="param-level param-level-mandatory">Mandatory
</div>
<div class="param-type">Type: <a href="../converter/fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.Expression" class="converter">Expression</a>
</div>
An expression evaluated as a string from the section or sentence that specifies the identifier associated with the proxy.

<h3 id="outFile" class="param">outFile</h3>

<div class="param-level param-level-mandatory">Mandatory
</div>
<div class="param-type">Type: <a href="../converter/fr.inra.maiage.bibliome.util.streams.TargetStream" class="converter">TargetStream</a>
</div>
Path to the file where to store the proxy syntactic structures and associated identifiers/

<h3 id="rcFile" class="param">rcFile</h3>

<div class="param-level param-level-mandatory">Mandatory
</div>
<div class="param-type">Type: <a href="../converter/fr.inra.maiage.bibliome.util.streams.SourceStream" class="converter">SourceStream</a>
</div>
Path to the YaTeA configuration file.

<h3 id="yateaExecutable" class="param">yateaExecutable</h3>

<div class="param-level param-level-mandatory">Mandatory
</div>
<div class="param-type">Type: <a href="../converter/fr.inra.maiage.bibliome.util.files.ExecutableFile" class="converter">ExecutableFile</a>
</div>
Path to the YaTeA executable file.

## Optional parameters

<h3 id="configDir" class="param">configDir</h3>

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/fr.inra.maiage.bibliome.util.files.InputDirectory" class="converter">InputDirectory</a>
</div>


<h3 id="language" class="param">language</h3>

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>


<h3 id="localeDir" class="param">localeDir</h3>

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/fr.inra.maiage.bibliome.util.files.InputDirectory" class="converter">InputDirectory</a>
</div>


<h3 id="outputDir" class="param">outputDir</h3>

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/fr.inra.maiage.bibliome.util.files.OutputDirectory" class="converter">OutputDirectory</a>
</div>


<h3 id="perlLib" class="param">perlLib</h3>

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Contents of the PERLLIB in the environment of Yatea binary.

<h3 id="postProcessingConfig" class="param">postProcessingConfig</h3>

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/fr.inra.maiage.bibliome.util.files.InputFile" class="converter">InputFile</a>
</div>
BioYaTeA option: path to the post-processing file option.

<h3 id="postProcessingOutput" class="param">postProcessingOutput</h3>

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/fr.inra.maiage.bibliome.util.files.OutputFile" class="converter">OutputFile</a>
</div>
BioYaTeA option: path to the result file after post-processing.

<h3 id="suffix" class="param">suffix</h3>

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>


<h3 id="termListFile" class="param">termListFile</h3>

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/fr.inra.maiage.bibliome.util.files.OutputFile" class="converter">OutputFile</a>
</div>
Path where to write the candidates list produced by YaTeA.

<h3 id="xmlTermsFile" class="param">xmlTermsFile</h3>

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/fr.inra.maiage.bibliome.util.files.OutputFile" class="converter">OutputFile</a>
</div>
Path where to write the candidates XML file produced by YaTeA.

<h3 id="bioYatea" class="param">bioYatea</h3>

<div class="param-level param-level-default-value">Default value: `false`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.Boolean" class="converter">Boolean</a>
</div>


<h3 id="documentFilter" class="param">documentFilter</h3>

<div class="param-level param-level-default-value">Default value: `true`
</div>
<div class="param-type">Type: <a href="../converter/fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.Expression" class="converter">Expression</a>
</div>
UNDOCUMENTED

<h3 id="formFeature" class="param">formFeature</h3>

<div class="param-level param-level-default-value">Default value: `form`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Feature containing the word form.

<h3 id="lemmaFeature" class="param">lemmaFeature</h3>

<div class="param-level param-level-default-value">Default value: `lemma`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Feature containing the word lemma.

<h3 id="posFeature" class="param">posFeature</h3>

<div class="param-level param-level-default-value">Default value: `pos`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Feature containing the word POS tag.

<h3 id="sectionFilter" class="param">sectionFilter</h3>

<div class="param-level param-level-default-value">Default value: `true and layer:words`
</div>
<div class="param-type">Type: <a href="../converter/fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.Expression" class="converter">Expression</a>
</div>
UNDOCUMENTED

<h3 id="sentenceLayer" class="param">sentenceLayer</h3>

<div class="param-level param-level-default-value">Default value: `sentences`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Name of the layer containing sentence annotations, sentences are reinforced.

<h3 id="wordLayer" class="param">wordLayer</h3>

<div class="param-level param-level-default-value">Default value: `words`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Name of the layer containing the word annotations.

<h3 id="yateaDefaultConfig" class="param">yateaDefaultConfig</h3>

<div class="param-level param-level-default-value">Default value: `{}`
</div>
<div class="param-type">Type: <a href="../converter/fr.inra.maiage.bibliome.alvisnlp.core.module.types.Mapping" class="converter">Mapping</a>
</div>


<h3 id="yateaOptions" class="param">yateaOptions</h3>

<div class="param-level param-level-default-value">Default value: `{}`
</div>
<div class="param-type">Type: <a href="../converter/fr.inra.maiage.bibliome.alvisnlp.core.module.types.Mapping" class="converter">Mapping</a>
</div>


## Deprecated parameters

<h3 id="sentenceLayerName" class="param">sentenceLayerName</h3>

<div class="param-level param-level-deprecated">Deprecated
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Deprecated alias for <a href="#sentenceLayer" class="param">sentenceLayer</a> .

<h3 id="wordLayerName" class="param">wordLayerName</h3>

<div class="param-level param-level-deprecated">Deprecated
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Deprecated alias for <a href="#wordLayer" class="param">wordLayer</a> .

<h3 id="workingDir" class="param">workingDir</h3>

<div class="param-level param-level-deprecated">Deprecated
</div>
<div class="param-type">Type: <a href="../converter/fr.inra.maiage.bibliome.util.files.WorkingDirectory" class="converter">WorkingDirectory</a>
</div>
Path to the directory where YaTeA is launched. This parameter is **deprecated** , use <a href="#xmlTermsFile" class="param">xmlTermsFile</a> and <a href="#termListFile" class="param">termListFile</a> instead.

