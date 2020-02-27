<h1 class="module">TreeTagger</h1>

## Synopsis

Runs *tree-tagger*.

## Description

*TreeTagger* applies *tree-tagger* on annotations in <a href="#wordLayerName" class="param">wordLayerName</a> by generating an appropriate input file. This file will contain one line for each annotation. The first column, the token surface form, is the value of the <a href="#formFeature" class="param">formFeature</a> feature. The second column, the token predefined POS tag, is the value <a href="#posFeature" class="param">posFeature</a> feature. The third column, the token predefined lemma, is the value of <a href="#lemmaFeature" class="param">lemmaFeature</a> feature. If <a href="#posFeature" class="param">posFeature</a> or <a href="#lemmaFeature" class="param">lemmaFeature</a> are not defined, then the second and third column are left blank.

The *tree-tagger* binary is specified by <a href="#treeTaggerExecutable" class="param">treeTaggerExecutable</a> and the language model to use is specified by <a href="#parFile" class="param">parFile</a>. Additionally a lexicon file can be given through <a href="#lexiconFile" class="param">lexiconFile</a>.

If <a href="#sentenceLayerName" class="param">sentenceLayerName</a> is defined, then *TreeTagger* considers annotations in this layer as sentences. Sentence boundaries are reinforced by providing *tree-tagger* an additional end-of-sentence marker.

Once *tree-tagger* has processed the corpus, *TreeTagger* adds the predicted POS tag and lemma to the respective <a href="#posFeature" class="param">posFeature</a> and <a href="#lemmaFeature" class="param">lemmaFeature</a> features of the corresponding annotations.

If <a href="#recordDir" class="param">recordDir</a> and <a href="#recordFeatures" class="param">recordFeatures</a> are both defined, then *tree-tagger* predictions are written into files in one file per section in the <a href="#recordDir" class="param">recordDir</a> directory. <a href="#recordFeatures" class="param">recordFeatures</a> is an array of feature names to record. An additional feature *n* is recognized as the annotation ordinal in the section.

## Parameters

<h3 name="parFile" class="param">parFile</h3>

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/fr.inra.maiage.bibliome.util.files.InputFile" class="converter">InputFile</a>
</div>
Path to the language model file.

<h3 name="treeTaggerExecutable" class="param">treeTaggerExecutable</h3>

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/fr.inra.maiage.bibliome.util.files.ExecutableFile" class="converter">ExecutableFile</a>
</div>
Path to the tree-tagger executable file.

<h3 name="constantAnnotationFeatures" class="param">constantAnnotationFeatures</h3>

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/fr.inra.maiage.bibliome.alvisnlp.core.module.types.Mapping" class="converter">Mapping</a>
</div>
Constant features to add to each annotation created by this module.

<h3 name="lexiconFile" class="param">lexiconFile</h3>

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/fr.inra.maiage.bibliome.util.streams.SourceStream" class="converter">SourceStream</a>
</div>
Path to a tree-tagger lexicon file, if set the lexicon will be applied to the corpus before treetagger processes it.

<h3 name="recordDir" class="param">recordDir</h3>

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/fr.inra.maiage.bibliome.util.files.OutputDirectory" class="converter">OutputDirectory</a>
</div>
Path to the directory where to write tree-tagger result files (one file per section).

<h3 name="recordFeatures" class="param">recordFeatures</h3>

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String[]" class="converter">String[]</a>
</div>
List of attributes to display in result files.

<h3 name="documentFilter" class="param">documentFilter</h3>

<div class="param-level param-level-default-value">Default value: `true`
</div>
<div class="param-type">Type: <a href="../converter/fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.Expression" class="converter">Expression</a>
</div>
Only process document that satisfy this expression.

<h3 name="formFeature" class="param">formFeature</h3>

<div class="param-level param-level-default-value">Default value: `form`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Name of the feature denoting the token surface form.

<h3 name="inputCharset" class="param">inputCharset</h3>

<div class="param-level param-level-default-value">Default value: `ISO-8859-1`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Tree-tagger input corpus character set.

<h3 name="lemmaFeature" class="param">lemmaFeature</h3>

<div class="param-level param-level-default-value">Default value: `lemma`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Name of the feature to set with the lemma.

<h3 name="noUnknownLemma" class="param">noUnknownLemma</h3>

<div class="param-level param-level-default-value">Default value: `false`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.Boolean" class="converter">Boolean</a>
</div>
Either to replace unknown lemmas with the surface form.

<h3 name="outputCharset" class="param">outputCharset</h3>

<div class="param-level param-level-default-value">Default value: `ISO-8859-1`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Tree-tagger output character set.

<h3 name="posFeature" class="param">posFeature</h3>

<div class="param-level param-level-default-value">Default value: `pos`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Name of the feature to set with the POS tag.

<h3 name="recordCharset" class="param">recordCharset</h3>

<div class="param-level param-level-default-value">Default value: `UTF-8`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Character encoding of the result files.

<h3 name="sectionFilter" class="param">sectionFilter</h3>

<div class="param-level param-level-default-value">Default value: `true`
</div>
<div class="param-type">Type: <a href="../converter/fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.Expression" class="converter">Expression</a>
</div>
Process only sections that satisfy this expression.

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

