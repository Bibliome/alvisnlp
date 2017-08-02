<h1 class="module">OgmiosTokenizer</h1>

## Synopsis

Tokenizes the sections contents according to the [Ogmios]() tokenizer specifications.

## Description

*OgmiosTokenizer* creates an annotation for each token found in the section contents according to the [Ogmios]() tokenizer specifications and adds these annotations to the <a href="#targetLayerName" class="param">targetLayerName</a> layer. The created annotations have a the feature <a href="#tokenTypeFeature" class="param">tokenTypeFeature</a> with one of the values:
  
* *alpha*: for an alphabetic token;
* *num*: for a numeric token;
* *sep*: for a whitespace token;
* *symb*: for all other tokens.



If <a href="#separatorTokens" class="param">separatorTokens</a> is false, the *OgmiosTokenizer* does not create annotations corresponding to whitespace tokens.

## Parameters

<a name="targetLayerName">

### targetLayerName

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Name of the layer where to store the tokens.

<a name="tokenTypeFeature">

### tokenTypeFeature

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Name of the token feature where to store the token type (alpha, num, sep, symb).

<a name="constantAnnotationFeatures">

### constantAnnotationFeatures

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/alvisnlp.module.types.Mapping" class="converter">Mapping</a>
</div>
Constant features to add to each annotation created by this module

<a name="documentFilter">

### documentFilter

<div class="param-level param-level-default-value">Default value: `true`
</div>
<div class="param-type">Type: <a href="../converter/alvisnlp.corpus.expressions.Expression" class="converter">Expression</a>
</div>
Only process document that satisfy this filter.

<a name="sectionFilter">

### sectionFilter

<div class="param-level param-level-default-value">Default value: `true`
</div>
<div class="param-type">Type: <a href="../converter/alvisnlp.corpus.expressions.Expression" class="converter">Expression</a>
</div>
Process only sections that satisfy this filter.

<a name="separatorTokens">

### separatorTokens

<div class="param-level param-level-default-value">Default value: `true`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.Boolean" class="converter">Boolean</a>
</div>
Either if separator tokens should be added.

