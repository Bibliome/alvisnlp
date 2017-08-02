# org.bibliome.alvisnlp.modules.DisambiguateAlternatives

## Synopsis

Disambiguate features that have multiple values.

## Description

*org.bibliome.alvisnlp.modules.DisambiguateAlternatives* evaluates [target](#target) as a list of elements with the current document as the context element. Then it tries to keep a single value for the feature [ambiguousFeature](#ambiguousFeature) in each item in the result. To achieve this, it keeps a set of unambiguous values found in the document. Unambiguous values are found in elements for which there is a single value for [ambiguousFeature](#ambiguousFeature).
      

## Parameters

<a name="ambiguousFeature">

### ambiguousFeature

Optional

Type: [String](../converter/java.lang.String)

Feature to disambiguate.

<a name="target">

### target

Optional

Type: [Expression](../converter/alvisnlp.corpus.expressions.Expression)

Elements to disambiguate.

<a name="documentFilter">

### documentFilter

Default value: `true`

Type: [Expression](../converter/alvisnlp.corpus.expressions.Expression)

Only process document that satisfy this filter.

<a name="warnIfAmbiguous">

### warnIfAmbiguous

Default value: `false`

Type: [Boolean](../converter/java.lang.Boolean)

Either to issue a warning if an element has still ambiguous values after processing.

