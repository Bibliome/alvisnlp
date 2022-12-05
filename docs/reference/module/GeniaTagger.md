<h1 class="module">GeniaTagger</h1>

## Synopsis

Runs Genia Tagger on annotations.

## Description

 *GeniaTagger* executes theGenia Tagger on annotations from the layer <a href="#wordLayer" class="param">wordLayer</a> and record the results in the features specified by <a href="#posFeature" class="param">posFeature</a> , <a href="#lemmaFeature" class="param">lemmaFeature</a> , <a href="#chunkFeature" class="param">chunkFeature</a> and <a href="#entityFeature" class="param">entityFeature</a> . *GeniaTagger* reinforces sentences specified by annotations in the <a href="#sentenceLayer" class="param">sentenceLayer</a> layer.

## Snippet



<button class="copy-code-button" title="Copy to clipboard" onclick="copy_code(this)">📋</button>
```xml
<geniatagger class="GeniaTagger>
    <geniaDir></geniaDir>
</geniatagger>
```

## Mandatory parameters

<h3 id="geniaDir" class="param">geniaDir</h3>

<div class="param-level param-level-mandatory">Mandatory
</div>
<div class="param-type">Type: <a href="../converter/java.io.File" class="converter">File</a>
</div>
Directory where geniatagger is installed.

## Optional parameters

<h3 id="chunkFeature" class="param">chunkFeature</h3>

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Feature where to put the chunk status.

<h3 id="entityFeature" class="param">entityFeature</h3>

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Feature where to put the entity status.

<h3 id="documentFilter" class="param">documentFilter</h3>

<div class="param-level param-level-default-value">Default value: `true`
</div>
<div class="param-type">Type: <a href="../converter/fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.Expression" class="converter">Expression</a>
</div>
Only process document that satisfy this expression.

<h3 id="geniaCharset" class="param">geniaCharset</h3>

<div class="param-level param-level-default-value">Default value: `UTF-8`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Character encoding of geniatagger input and output.

<h3 id="geniaTaggerExecutable" class="param">geniaTaggerExecutable</h3>

<div class="param-level param-level-default-value">Default value: `./geniatagger`
</div>
<div class="param-type">Type: <a href="../converter/java.io.File" class="converter">File</a>
</div>
Name of the geniatagger executable file.

<h3 id="lemmaFeature" class="param">lemmaFeature</h3>

<div class="param-level param-level-default-value">Default value: `lemma`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Feature where to put the word lemma.

<h3 id="posFeature" class="param">posFeature</h3>

<div class="param-level param-level-default-value">Default value: `pos`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Feature where to put the POS tag.

<h3 id="sectionFilter" class="param">sectionFilter</h3>

<div class="param-level param-level-default-value">Default value: `true and layer:sentences and layer:words`
</div>
<div class="param-type">Type: <a href="../converter/fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.Expression" class="converter">Expression</a>
</div>
Process only sections that satisfy this expression.

<h3 id="sentenceFilter" class="param">sentenceFilter</h3>

<div class="param-level param-level-default-value">Default value: `true`
</div>
<div class="param-type">Type: <a href="../converter/fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.Expression" class="converter">Expression</a>
</div>
Evaluated as a boolean with the sentence annotation as the context element. *GeniaTagger* only process the sentence if the result is true. To filter sentences that are too long for Genia Tagger, use "length < 1024".

<h3 id="sentenceLayer" class="param">sentenceLayer</h3>

<div class="param-level param-level-default-value">Default value: `sentences`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Name of the layer containing sentence annotations.

<h3 id="treeTaggerTagset" class="param">treeTaggerTagset</h3>

<div class="param-level param-level-default-value">Default value: `false`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.Boolean" class="converter">Boolean</a>
</div>
UNDOCUMENTED

<h3 id="wordFormFeature" class="param">wordFormFeature</h3>

<div class="param-level param-level-default-value">Default value: `form`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Feature containing the word surface form.

<h3 id="wordLayer" class="param">wordLayer</h3>

<div class="param-level param-level-default-value">Default value: `words`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Name of the layer containing word annotations.

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

