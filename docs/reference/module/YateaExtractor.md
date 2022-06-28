<h1 class="module">YateaExtractor</h1>

## Synopsis

Extract terms from the corpus using the YaTeA term extractor.

## Description

*YateaExtractor* hands the corpus to the [YaTeA](http://search.cpan.org/~thhamon/Lingua-YaTeA) extractor. The corpus is first written in a file in the YaTeA input format. Tokens are annotations in the layer <a href="#wordLayerName" class="param">wordLayerName</a>, their surface form, POS tag and lemma are taken from <a href="#formFeature" class="param">formFeature</a>, <a href="#posFeature" class="param">posFeature</a> and <a href="#lemmaFeature" class="param">lemmaFeature</a> features respectively. If <a href="#sentenceLayerName" class="param">sentenceLayerName</a> is set, then an additional *SENT* marker is added to reinforce sentence boundaries corresponding to annotations in this layer.

The YaTeA is called using the executable set in <a href="#yateaExecutable" class="param">yateaExecutable</a>, it will run as if it is called from directory <a href="#workingDir" class="param">workingDir</a>: the result will be written in the subdirectory named <a href="#corpusName" class="param">corpusName</a>.

## Mandatory parameters

<h3 name="rcFile" class="param">rcFile</h3>

<div class="param-level param-level-mandatory">Mandatory
</div>
<div class="param-type">Type: <a href="../converter/fr.inra.maiage.bibliome.util.streams.SourceStream" class="converter">SourceStream</a>
</div>
Path to the YaTeA configuration file.

<h3 name="yateaExecutable" class="param">yateaExecutable</h3>

<div class="param-level param-level-mandatory">Mandatory
</div>
<div class="param-type">Type: <a href="../converter/fr.inra.maiage.bibliome.util.files.ExecutableFile" class="converter">ExecutableFile</a>
</div>
Path to the YaTeA executable file.

## Optional parameters

<h3 name="configDir" class="param">configDir</h3>

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/fr.inra.maiage.bibliome.util.files.InputDirectory" class="converter">InputDirectory</a>
</div>


<h3 name="language" class="param">language</h3>

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>


<h3 name="localeDir" class="param">localeDir</h3>

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/fr.inra.maiage.bibliome.util.files.InputDirectory" class="converter">InputDirectory</a>
</div>


<h3 name="outputDir" class="param">outputDir</h3>

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/fr.inra.maiage.bibliome.util.files.OutputDirectory" class="converter">OutputDirectory</a>
</div>


<h3 name="perlLib" class="param">perlLib</h3>

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Contents of the PERLLIB in the environment of Yatea binary.

<h3 name="postProcessingConfig" class="param">postProcessingConfig</h3>

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/fr.inra.maiage.bibliome.util.files.InputFile" class="converter">InputFile</a>
</div>
BioYaTeA option: path to the post-processing file option.

<h3 name="postProcessingOutput" class="param">postProcessingOutput</h3>

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/fr.inra.maiage.bibliome.util.files.OutputFile" class="converter">OutputFile</a>
</div>
BioYaTeA option: path to the result file after post-processing.

<h3 name="suffix" class="param">suffix</h3>

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>


<h3 name="termListFile" class="param">termListFile</h3>

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/fr.inra.maiage.bibliome.util.files.OutputFile" class="converter">OutputFile</a>
</div>
Path where to write the candidates list produced by YaTeA.

<h3 name="testifiedTerminology" class="param">testifiedTerminology</h3>

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.yatea.TestifiedTerminology" class="converter">TestifiedTerminology</a>
</div>


<h3 name="workingDir" class="param">workingDir</h3>

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/fr.inra.maiage.bibliome.util.files.WorkingDirectory" class="converter">WorkingDirectory</a>
</div>
Path to the directory where YaTeA is launched. This parameter is **deprecated**, use <a href="#xmlTermsFile" class="param">xmlTermsFile</a> and <a href="#termListFile" class="param">termListFile</a> instead.

<h3 name="xmlTermsFile" class="param">xmlTermsFile</h3>

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/fr.inra.maiage.bibliome.util.files.OutputFile" class="converter">OutputFile</a>
</div>
Path where to write the candidates XML file produced by YaTeA.

<h3 name="bioYatea" class="param">bioYatea</h3>

<div class="param-level param-level-default-value">Default value: `false`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.Boolean" class="converter">Boolean</a>
</div>


<h3 name="documentFilter" class="param">documentFilter</h3>

<div class="param-level param-level-default-value">Default value: `true`
</div>
<div class="param-type">Type: <a href="../converter/fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.Expression" class="converter">Expression</a>
</div>
UNDOCUMENTED

<h3 name="documentTokens" class="param">documentTokens</h3>

<div class="param-level param-level-default-value">Default value: `true`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.Boolean" class="converter">Boolean</a>
</div>
Either to write DOCUMENT special tokens. Not every YaTeA version accepts them.

<h3 name="formFeature" class="param">formFeature</h3>

<div class="param-level param-level-default-value">Default value: `form`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Feature containing the word form.

<h3 name="lemmaFeature" class="param">lemmaFeature</h3>

<div class="param-level param-level-default-value">Default value: `lemma`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Feature containing the word lemma.

<h3 name="posFeature" class="param">posFeature</h3>

<div class="param-level param-level-default-value">Default value: `pos`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Feature containing the word POS tag.

<h3 name="sectionFilter" class="param">sectionFilter</h3>

<div class="param-level param-level-default-value">Default value: `boolean:and(true, nav:layer:words())`
</div>
<div class="param-type">Type: <a href="../converter/fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.Expression" class="converter">Expression</a>
</div>
UNDOCUMENTED

<h3 name="sentenceLayerName" class="param">sentenceLayerName</h3>

<div class="param-level param-level-default-value">Default value: `sentences`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Name of the layer containing sentence annotations, sentences are reinforced.

<h3 name="wordLayerName" class="param">wordLayerName</h3>

<div class="param-level param-level-default-value">Default value: `words`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Name of the layer containing the word annotations.

<h3 name="yateaDefaultConfig" class="param">yateaDefaultConfig</h3>

<div class="param-level param-level-default-value">Default value: `{}`
</div>
<div class="param-type">Type: <a href="../converter/fr.inra.maiage.bibliome.alvisnlp.core.module.types.Mapping" class="converter">Mapping</a>
</div>


<h3 name="yateaOptions" class="param">yateaOptions</h3>

<div class="param-level param-level-default-value">Default value: `{}`
</div>
<div class="param-type">Type: <a href="../converter/fr.inra.maiage.bibliome.alvisnlp.core.module.types.Mapping" class="converter">Mapping</a>
</div>


