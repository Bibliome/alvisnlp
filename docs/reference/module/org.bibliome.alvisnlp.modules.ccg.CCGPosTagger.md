<h1 class="module">CCGPosTagger</h1>

## Synopsis

Applies the [CCG](http://svn.ask.it.usyd.edu.au/trac/candc/wiki) POS tagger on annotations.

## Description

*CCGPosTagger* applies the [CCG](http://svn.ask.it.usyd.edu.au/trac/candc/wiki) POS tagger on annotations in the layer named <a href="#wordLayerName" class="param">wordLayerName</a>. Sentences are enforced if <a href="#sentenceLayerName" class="param">sentenceLayerName</a> is set.

If <a href="#keepPreviousPos" class="param">keepPreviousPos</a> is set to *true*, then the POS tag predicted by CCG will not be added to annotations that already have a POS tag.

## Parameters

<a name="executable">

### executable

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/org.bibliome.util.files.ExecutableFile" class="converter">ExecutableFile</a>
</div>
Path to the CCG POS tagger executable.

<a name="model">

### model

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/org.bibliome.util.files.InputDirectory" class="converter">InputDirectory</a>
</div>
Path to the CCG POS model.

<a name="documentFilter">

### documentFilter

<div class="param-level param-level-default-value">Default value: `true`
</div>
<div class="param-type">Type: <a href="../converter/alvisnlp.corpus.expressions.Expression" class="converter">Expression</a>
</div>
Only process document that satisfy this filter.

<a name="formFeatureName">

### formFeatureName

<div class="param-level param-level-default-value">Default value: `form`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Name of the feature in word annotations that contains the surface form.

<a name="internalEncoding">

### internalEncoding

<div class="param-level param-level-default-value">Default value: `UTF-8`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Character encoding to use for CCG input and output files.

<a name="keepPreviousPos">

### keepPreviousPos

<div class="param-level param-level-default-value">Default value: `false`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.Boolean" class="converter">Boolean</a>
</div>
Either to keep previous POS tags.

<a name="maxRuns">

### maxRuns

<div class="param-level param-level-default-value">Default value: `1`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.Integer" class="converter">Integer</a>
</div>
Maximal number of CCG runs.

<a name="posFeatureName">

### posFeatureName

<div class="param-level param-level-default-value">Default value: `pos`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Name of the feature in word annotations where to write POS tags. This feature is read for previous POS tags if <a href="#keepPreviousPos" class="param">keepPreviousPos</a> is set to *true*.

<a name="sectionFilter">

### sectionFilter

<div class="param-level param-level-default-value">Default value: `boolean:and(true, boolean:and(nav:layer:sentences(), nav:layer:words()))`
</div>
<div class="param-type">Type: <a href="../converter/alvisnlp.corpus.expressions.Expression" class="converter">Expression</a>
</div>
Process only sections that satisfy this filter.

<a name="sentenceFilter">

### sentenceFilter

<div class="param-level param-level-default-value">Default value: `true`
</div>
<div class="param-type">Type: <a href="../converter/alvisnlp.corpus.expressions.Expression" class="converter">Expression</a>
</div>
Process only sentences that satisfy this filter.

<a name="sentenceLayerName">

### sentenceLayerName

<div class="param-level param-level-default-value">Default value: `sentences`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Name of the layer containing sentence annotations.

<a name="silent">

### silent

<div class="param-level param-level-default-value">Default value: `false`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.Boolean" class="converter">Boolean</a>
</div>
Either to shut the CCG output (CCG can be quite verbose).

<a name="wordLayerName">

### wordLayerName

<div class="param-level param-level-default-value">Default value: `words`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Name of the layer containing word annotations.

