# org.bibliome.alvisnlp.modules.OgmiosTokenizer

## Synopsis

Tokenizes the sections contents according to the [Ogmios]() tokenizer specifications.

## Description

*org.bibliome.alvisnlp.modules.OgmiosTokenizer* creates an annotation for each token found in the section contents according to the [Ogmios]() tokenizer specifications and adds these annotations to the [targetLayerName](#targetLayerName) layer. The created annotations have a the feature [tokenTypeFeature](#tokenTypeFeature) with one of the values:
      
* *alpha*: for an alphabetic token;
* *num*: for a numeric token;
* *sep*: for a whitespace token;
* *symb*: for all other tokens.



If [separatorTokens](#separatorTokens) is false, the *org.bibliome.alvisnlp.modules.OgmiosTokenizer* does not create annotations corresponding to whitespace tokens.

## Parameters

<a name="targetLayerName">

### targetLayerName

Optional

Type: [String](../converter/java.lang.String)

Name of the layer where to store the tokens.

<a name="tokenTypeFeature">

### tokenTypeFeature

Optional

Type: [String](../converter/java.lang.String)

Name of the token feature where to store the token type (alpha, num, sep, symb).

<a name="constantAnnotationFeatures">

### constantAnnotationFeatures

Optional

Type: [Mapping](../converter/alvisnlp.module.types.Mapping)

Constant features to add to each annotation created by this module

<a name="documentFilter">

### documentFilter

Default value: `true`

Type: [Expression](../converter/alvisnlp.corpus.expressions.Expression)

Only process document that satisfy this filter.

<a name="sectionFilter">

### sectionFilter

Default value: `true`

Type: [Expression](../converter/alvisnlp.corpus.expressions.Expression)

Process only sections that satisfy this filter.

<a name="separatorTokens">

### separatorTokens

Default value: `true`

Type: [Boolean](../converter/java.lang.Boolean)

Either if separator tokens should be added.

