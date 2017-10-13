<h1 class="module">KeywordsSelector</h1>

## Synopsis

Selects most relevant keywords in documents.

**This module is experimental.**

## Description

*KeywordsSelector* selects the most relevant keywords in documents. The candidate keywords are specified with <a href="#keywords" class="param">keywords</a> evaluated as a list of elements with the document as the context element. The keyword text is specified by <a href="#keywordForm" class="param">keywordForm</a>.
  

*KeywordsSelector* ranks the keywords according to the <a href="#scoreFunction" class="param">scoreFunction</a> function, then selects the <a href="#keywordCount" class="param">keywordCount</a> keywords with the highest value. The selected keywords are stored in the document feature <a href="#keywordFeature" class="param">keywordFeature</a>, and the corresponding scores in <a href="#scoreFeature" class="param">scoreFeature</a>. 
  

## Parameters

<a name="keywordFeature">

### keywordFeature

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Document feature where to store the selected keywords.

<a name="outFile">

### outFile

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/fr.inra.maiage.bibliome.util.streams.TargetStream" class="converter">TargetStream</a>
</div>


<a name="scoreFeature">

### scoreFeature

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Document feature where to store the score of selected keywords computed by <a href="#scoreFunction" class="param">scoreFunction</a>.

<a name="charset">

### charset

<div class="param-level param-level-default-value">Default value: `UTF-8`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>


<a name="documentId">

### documentId

<div class="param-level param-level-default-value">Default value: `properties:@:id()`
</div>
<div class="param-type">Type: <a href="../converter/fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.Expression" class="converter">Expression</a>
</div>


<a name="documents">

### documents

<div class="param-level param-level-default-value">Default value: `nav:documents()`
</div>
<div class="param-type">Type: <a href="../converter/fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.Expression" class="converter">Expression</a>
</div>


<a name="keywordCount">

### keywordCount

<div class="param-level param-level-default-value">Default value: `2147483647`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.Integer" class="converter">Integer</a>
</div>
Number of keywords to select.

<a name="keywordForm">

### keywordForm

<div class="param-level param-level-default-value">Default value: `properties:@:form()`
</div>
<div class="param-type">Type: <a href="../converter/fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.Expression" class="converter">Expression</a>
</div>
Text of the keyword. This expression is evaluated as a string with the keyword element as the context.

<a name="keywords">

### keywords

<div class="param-level param-level-default-value">Default value: `nav:.(nav:sections(), nav:layer:words())`
</div>
<div class="param-type">Type: <a href="../converter/fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.Expression" class="converter">Expression</a>
</div>
Expression evaluated as a list of elements with the document as the context element. Each element represents a keyword of the document.

<a name="scoreFunction">

### scoreFunction

<div class="param-level param-level-default-value">Default value: `ABSOLUTE`
</div>
<div class="param-type">Type: <a href="../converter/fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.keyword.KeywordScoreFunction" class="converter">KeywordScoreFunction</a>
</div>
Function to use for ranking keywords. Available functions include the keyword frequency, different variants of tf-idf and Okapi BM25.

<a name="scoreThreshold">

### scoreThreshold

<div class="param-level param-level-default-value">Default value: `0.0`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.Double" class="converter">Double</a>
</div>


<a name="separator">

### separator

<div class="param-level param-level-default-value">Default value: `	`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.Character" class="converter">Character</a>
</div>


