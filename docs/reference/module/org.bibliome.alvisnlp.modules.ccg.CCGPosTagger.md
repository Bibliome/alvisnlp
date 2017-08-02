# org.bibliome.alvisnlp.modules.ccg.CCGPosTagger

## Synopsis

Applies the [CCG](http://svn.ask.it.usyd.edu.au/trac/candc/wiki) POS tagger on annotations.

## Description

*org.bibliome.alvisnlp.modules.ccg.CCGPosTagger* applies the [CCG](http://svn.ask.it.usyd.edu.au/trac/candc/wiki) POS tagger on annotations in the layer named [wordLayerName](#wordLayerName). Sentences are enforced if [sentenceLayerName](#sentenceLayerName) is set.

If [keepPreviousPos](#keepPreviousPos) is set to *true*, then the POS tag predicted by CCG will not be added to annotations that already have a POS tag.

## Parameters

<a name="executable">

### executable

Optional

Type: [ExecutableFile](../converter/org.bibliome.util.files.ExecutableFile)

Path to the CCG POS tagger executable.

<a name="model">

### model

Optional

Type: [InputDirectory](../converter/org.bibliome.util.files.InputDirectory)

Path to the CCG POS model.

<a name="documentFilter">

### documentFilter

Default value: `true`

Type: [Expression](../converter/alvisnlp.corpus.expressions.Expression)

Only process document that satisfy this filter.

<a name="formFeatureName">

### formFeatureName

Default value: `form`

Type: [String](../converter/java.lang.String)

Name of the feature in word annotations that contains the surface form.

<a name="internalEncoding">

### internalEncoding

Default value: `UTF-8`

Type: [String](../converter/java.lang.String)

Character encoding to use for CCG input and output files.

<a name="keepPreviousPos">

### keepPreviousPos

Default value: `false`

Type: [Boolean](../converter/java.lang.Boolean)

Either to keep previous POS tags.

<a name="maxRuns">

### maxRuns

Default value: `1`

Type: [Integer](../converter/java.lang.Integer)

Maximal number of CCG runs.

<a name="posFeatureName">

### posFeatureName

Default value: `pos`

Type: [String](../converter/java.lang.String)

Name of the feature in word annotations where to write POS tags. This feature is read for previous POS tags if [keepPreviousPos](#keepPreviousPos) is set to *true*.

<a name="sectionFilter">

### sectionFilter

Default value: `boolean:and(true, boolean:and(nav:layer:sentences(), nav:layer:words()))`

Type: [Expression](../converter/alvisnlp.corpus.expressions.Expression)

Process only sections that satisfy this filter.

<a name="sentenceFilter">

### sentenceFilter

Default value: `true`

Type: [Expression](../converter/alvisnlp.corpus.expressions.Expression)

Process only sentences that satisfy this filter.

<a name="sentenceLayerName">

### sentenceLayerName

Default value: `sentences`

Type: [String](../converter/java.lang.String)

Name of the layer containing sentence annotations.

<a name="silent">

### silent

Default value: `false`

Type: [Boolean](../converter/java.lang.Boolean)

Either to shut the CCG output (CCG can be quite verbose).

<a name="wordLayerName">

### wordLayerName

Default value: `words`

Type: [String](../converter/java.lang.String)

Name of the layer containing word annotations.

