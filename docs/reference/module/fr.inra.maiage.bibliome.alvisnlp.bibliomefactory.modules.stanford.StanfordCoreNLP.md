<h1 class="module">StanfordCoreNLP</h1>

## Synopsis

Process the documents with Stanford's [CoreNLP](https://stanfordnlp.github.io/CoreNLP/) .

**This module is experimental.**

## Description

 *StanfordCoreNLP* tokenizes, POS-tags and lemmatizes each section using [CoreNLP](https://stanfordnlp.github.io/CoreNLP/) .

If <a href="#ner" class="param">ner</a> is set then *StanfordCoreNLP* also performs Named Entity Recognition. Refer to [CoreNLP NER](https://stanfordnlp.github.io/CoreNLP/ner.html) for details on methods and Named Entity Types.

If <a href="#parse" class="param">parse</a> is set, then *StanfordCoreNLP* parses the sentences and creates dependencies tuples in <a href="#dependencyRelation" class="param">dependencyRelation</a> .

If <a href="#pretokenized" class="param">pretokenized</a> is set, then *StanfordCoreNLP* will not create annotations for tokens and sentences. Thus the segmentation must be performed beforehand.

## Snippet



<button class="copy-code-button" title="Copy to clipboard" onclick="copy_code(this)">📋</button>
```xml
<stanfordcorenlp class="StanfordCoreNLP>
</stanfordcorenlp>
```

## Mandatory parameters

## Optional parameters

<h3 id="constantAnnotationFeatures" class="param">constantAnnotationFeatures</h3>

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/fr.inra.maiage.bibliome.alvisnlp.core.module.types.Mapping" class="converter">Mapping</a>
</div>
Constant features to add to each annotation created by this module.

<h3 id="constantRelationFeatures" class="param">constantRelationFeatures</h3>

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/fr.inra.maiage.bibliome.alvisnlp.core.module.types.Mapping" class="converter">Mapping</a>
</div>
Constant features to add to each relation created by this module.

<h3 id="constantTupleFeatures" class="param">constantTupleFeatures</h3>

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/fr.inra.maiage.bibliome.alvisnlp.core.module.types.Mapping" class="converter">Mapping</a>
</div>
Constant features to add to each tuple created by this module.

<h3 id="dependencyLabelFeature" class="param">dependencyLabelFeature</h3>

<div class="param-level param-level-default-value">Default value: `label`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Name of the feature where to store the dependency label.

<h3 id="dependencyRelation" class="param">dependencyRelation</h3>

<div class="param-level param-level-default-value">Default value: `dependencies`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Name of the relation where to store dependency tuples.

<h3 id="dependencySentenceRole" class="param">dependencySentenceRole</h3>

<div class="param-level param-level-default-value">Default value: `sentence`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Name of the role of the dependency tuple argument that references the parsed sentence.

<h3 id="dependentRole" class="param">dependentRole</h3>

<div class="param-level param-level-default-value">Default value: `dependent`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Name of the role of the dependency tuple argument that references the modifier (dependent) token.

<h3 id="documentFilter" class="param">documentFilter</h3>

<div class="param-level param-level-default-value">Default value: `true`
</div>
<div class="param-type">Type: <a href="../converter/fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.Expression" class="converter">Expression</a>
</div>
Only process document that satisfy this expression.

<h3 id="headRole" class="param">headRole</h3>

<div class="param-level param-level-default-value">Default value: `head`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Name of the role of the dependency tuple argument that references the head (governor) token.

<h3 id="lemmaFeature" class="param">lemmaFeature</h3>

<div class="param-level param-level-default-value">Default value: `lemma`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Feature where to record the lemma.

<h3 id="namedEntityLayer" class="param">namedEntityLayer</h3>

<div class="param-level param-level-default-value">Default value: `named-entities`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Layer where to create named entity annotations.

<h3 id="namedEntityTypeFeature" class="param">namedEntityTypeFeature</h3>

<div class="param-level param-level-default-value">Default value: `ne-type`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Feature where to record the named entity type.

<h3 id="ner" class="param">ner</h3>

<div class="param-level param-level-default-value">Default value: `false`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.Boolean" class="converter">Boolean</a>
</div>
Perform NER.

<h3 id="parse" class="param">parse</h3>

<div class="param-level param-level-default-value">Default value: `false`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.Boolean" class="converter">Boolean</a>
</div>
Perform dependency parsing.

<h3 id="pipelineProperties" class="param">pipelineProperties</h3>

<div class="param-level param-level-default-value">Default value: `{}`
</div>
<div class="param-type">Type: <a href="../converter/fr.inra.maiage.bibliome.alvisnlp.core.module.types.Mapping" class="converter">Mapping</a>
</div>
Additional properties to pass to CoreNLP pipeline. See the [documentation of each pipeline annotator](https://stanfordnlp.github.io/CoreNLP/pipeline.html) for available options.

<h3 id="posFeature" class="param">posFeature</h3>

<div class="param-level param-level-default-value">Default value: `pos`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Feature where to record the POS tag.

<h3 id="pretokenized" class="param">pretokenized</h3>

<div class="param-level param-level-default-value">Default value: `false`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.Boolean" class="converter">Boolean</a>
</div>
Do not perform tokenization and sentence splitting. Read tokens and sentences generated by previous steps.

<h3 id="sectionFilter" class="param">sectionFilter</h3>

<div class="param-level param-level-default-value">Default value: `true`
</div>
<div class="param-type">Type: <a href="../converter/fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.Expression" class="converter">Expression</a>
</div>
Process only sections that satisfy this expression.

<h3 id="sentenceLayer" class="param">sentenceLayer</h3>

<div class="param-level param-level-default-value">Default value: `sentences`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Layer where to place (or read if <a href="#pretokenized" class="param">pretokenized</a> is set) sentence annotations.

<h3 id="wordLayer" class="param">wordLayer</h3>

<div class="param-level param-level-default-value">Default value: `words`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Layer where to place (or read if <a href="#pretokenized" class="param">pretokenized</a> is set) tokens annotations.

## Deprecated parameters

