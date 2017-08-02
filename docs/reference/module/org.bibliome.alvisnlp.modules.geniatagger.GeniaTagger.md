# org.bibliome.alvisnlp.modules.geniatagger.GeniaTagger

## Synopsis

Runs Genia Tagger on annotations.

## Description

*org.bibliome.alvisnlp.modules.geniatagger.GeniaTagger* executes theGenia Tagger on annotations from the layer [words](#words) and record the results in the features specified by [pos](#pos), [lemma](#lemma), [chunk](#chunk) and [entity](#entity). *org.bibliome.alvisnlp.modules.geniatagger.GeniaTagger* reinforces sentences specified by annotations in the [sentences](#sentences) layer.

## Parameters

<a name="geniaDir">

### geniaDir

Optional

Type: [File](../converter/java.io.File)

Directory where geniatagger is installed.

<a name="chunkFeature">

### chunkFeature

Optional

Type: [String](../converter/java.lang.String)

UNDOCUMENTED

<a name="entityFeature">

### entityFeature

Optional

Type: [String](../converter/java.lang.String)

UNDOCUMENTED

<a name="documentFilter">

### documentFilter

Default value: `true`

Type: [Expression](../converter/alvisnlp.corpus.expressions.Expression)

Only process document that satisfy this filter.

<a name="geniaCharset">

### geniaCharset

Default value: `UTF-8`

Type: [String](../converter/java.lang.String)

Character encoding of geniatagger input and output.

<a name="geniaTaggerExecutable">

### geniaTaggerExecutable

Default value: `geniatagger`

Type: [File](../converter/java.io.File)

Name of the geniatagger executable file.

<a name="lemmaFeature">

### lemmaFeature

Default value: `lemma`

Type: [String](../converter/java.lang.String)

UNDOCUMENTED

<a name="posFeature">

### posFeature

Default value: `pos`

Type: [String](../converter/java.lang.String)

UNDOCUMENTED

<a name="sectionFilter">

### sectionFilter

Default value: `boolean:and(true, boolean:and(nav:layer:sentences(), nav:layer:words()))`

Type: [Expression](../converter/alvisnlp.corpus.expressions.Expression)

Process only sections that satisfy this filter.

<a name="sentenceFilter">

### sentenceFilter

Default value: `true`

Type: [Expression](../converter/alvisnlp.corpus.expressions.Expression)

Evaluated as a boolean with the sentence annotation as the context element. *org.bibliome.alvisnlp.modules.geniatagger.GeniaTagger* only process the sentence if the result is true. To filter sentences that are too long for Genia Tagger, use "length < 1024".

<a name="sentenceLayerName">

### sentenceLayerName

Default value: `sentences`

Type: [String](../converter/java.lang.String)

UNDOCUMENTED

<a name="treeTaggerTagset">

### treeTaggerTagset

Default value: `false`

Type: [Boolean](../converter/java.lang.Boolean)

UNDOCUMENTED

<a name="wordFormFeature">

### wordFormFeature

Default value: `form`

Type: [String](../converter/java.lang.String)

UNDOCUMENTED

<a name="wordLayerName">

### wordLayerName

Default value: `words`

Type: [String](../converter/java.lang.String)

UNDOCUMENTED

