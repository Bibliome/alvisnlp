# org.bibliome.alvisnlp.modules.keyword.KeywordsSelector

## Synopsis

Selects most relevant keywords in documents.

**This module is experimental.**

## Description

*org.bibliome.alvisnlp.modules.keyword.KeywordsSelector* selects the most relevant keywords in documents. The candidate keywords are specified with [keywords](#keywords) evaluated as a list of elements with the document as the context element. The keyword text is specified by [keywordForm](#keywordForm).
      

*org.bibliome.alvisnlp.modules.keyword.KeywordsSelector* ranks the keywords according to the [scoreFunction](#scoreFunction) function, then selects the [keywordCount](#keywordCount) keywords with the highest value. The selected keywords are stored in the document feature [keywordFeature](#keywordFeature), and the corresponding scores in [scoreFeature](#scoreFeature). 
      

## Parameters

<a name="keywordFeature">

### keywordFeature

Optional

Type: [String](../converter/java.lang.String)

Document feature where to store the selected keywords.

<a name="outFile">

### outFile

Optional

Type: [TargetStream](../converter/org.bibliome.util.streams.TargetStream)



<a name="scoreFeature">

### scoreFeature

Optional

Type: [String](../converter/java.lang.String)

Document feature where to store the score of selected keywords computed by [scoreFunction](#scoreFunction).

<a name="charset">

### charset

Default value: `UTF-8`

Type: [String](../converter/java.lang.String)



<a name="documentId">

### documentId

Default value: `properties:@:id()`

Type: [Expression](../converter/alvisnlp.corpus.expressions.Expression)



<a name="documents">

### documents

Default value: `nav:documents()`

Type: [Expression](../converter/alvisnlp.corpus.expressions.Expression)



<a name="keywordCount">

### keywordCount

Default value: `2147483647`

Type: [Integer](../converter/java.lang.Integer)

Number of keywords to select.

<a name="keywordForm">

### keywordForm

Default value: `properties:@:form()`

Type: [Expression](../converter/alvisnlp.corpus.expressions.Expression)

Text of the keyword. This expression is evaluated as a string with the keyword element as the context.

<a name="keywords">

### keywords

Default value: `nav:.(nav:sections(), nav:layer:words())`

Type: [Expression](../converter/alvisnlp.corpus.expressions.Expression)

Expression evaluated as a list of elements with the document as the context element. Each element represents a keyword of the document.

<a name="scoreFunction">

### scoreFunction

Default value: `ABSOLUTE`

Type: [KeywordScoreFunction](../converter/org.bibliome.alvisnlp.modules.keyword.KeywordScoreFunction)

Function to use for ranking keywords. Available functions include the keyword frequency, different variants of tf-idf and Okapi BM25.

<a name="scoreThreshold">

### scoreThreshold

Default value: `0.0`

Type: [Double](../converter/java.lang.Double)



<a name="separator">

### separator

Default value: `	`

Type: [Character](../converter/java.lang.Character)



