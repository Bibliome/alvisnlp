<h1 class="module">KeywordsSelector</h1>

## Synopsis

Selects most relevant keywords in documents.

**This module is experimental.**

## Description

 *KeywordsSelector* selects the most relevant keywords in documents. The candidate keywords are specified with <a href="#keywords" class="param">keywords</a> evaluated as a list of elements with the document as the context element. The keyword text is specified by <a href="#keywordForm" class="param">keywordForm</a> .

 *KeywordsSelector* ranks the keywords according to the <a href="#scoreFunction" class="param">scoreFunction</a> function, then selects the <a href="#keywordCount" class="param">keywordCount</a> keywords with the highest value. The selected keywords are stored in the document feature <a href="#keywordFeature" class="param">keywordFeature</a> , and the corresponding scores in <a href="#scoreFeature" class="param">scoreFeature</a> .

## Snippet



```xml
<keywordsselector class="KeywordsSelector>
</keywordsselector>
```

## Mandatory parameters

## Optional parameters

<h3 id="keywordFeature" class="param">keywordFeature</h3>

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Document feature where to store the selected keywords.

<h3 id="outFile" class="param">outFile</h3>

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/fr.inra.maiage.bibliome.util.streams.TargetStream" class="converter">TargetStream</a>
</div>


<h3 id="scoreFeature" class="param">scoreFeature</h3>

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Document feature where to store the score of selected keywords computed by <a href="#scoreFunction" class="param">scoreFunction</a> .

<h3 id="charset" class="param">charset</h3>

<div class="param-level param-level-default-value">Default value: `UTF-8`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>


<h3 id="documentId" class="param">documentId</h3>

<div class="param-level param-level-default-value">Default value: `@id`
</div>
<div class="param-type">Type: <a href="../converter/fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.Expression" class="converter">Expression</a>
</div>


<h3 id="documents" class="param">documents</h3>

<div class="param-level param-level-default-value">Default value: `documents`
</div>
<div class="param-type">Type: <a href="../converter/fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.Expression" class="converter">Expression</a>
</div>


<h3 id="keywordCount" class="param">keywordCount</h3>

<div class="param-level param-level-default-value">Default value: `2147483647`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.Integer" class="converter">Integer</a>
</div>
Number of keywords to select.

<h3 id="keywordForm" class="param">keywordForm</h3>

<div class="param-level param-level-default-value">Default value: `@form`
</div>
<div class="param-type">Type: <a href="../converter/fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.Expression" class="converter">Expression</a>
</div>
Text of the keyword. This expression is evaluated as a string with the keyword element as the context.

<h3 id="keywords" class="param">keywords</h3>

<div class="param-level param-level-default-value">Default value: `sections.layer:words`
</div>
<div class="param-type">Type: <a href="../converter/fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.Expression" class="converter">Expression</a>
</div>
Expression evaluated as a list of elements with the document as the context element. Each element represents a keyword of the document.

<h3 id="scoreFunction" class="param">scoreFunction</h3>

<div class="param-level param-level-default-value">Default value: `ABSOLUTE`
</div>
<div class="param-type">Type: <a href="../converter/fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.keyword.KeywordScoreFunction" class="converter">KeywordScoreFunction</a>
</div>
Function to use for ranking keywords. Available functions include the keyword frequency, different variants of tf-idf and Okapi BM25.

<h3 id="scoreThreshold" class="param">scoreThreshold</h3>

<div class="param-level param-level-default-value">Default value: `0.0`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.Double" class="converter">Double</a>
</div>


<h3 id="separator" class="param">separator</h3>

<div class="param-level param-level-default-value">Default value: `	`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.Character" class="converter">Character</a>
</div>


