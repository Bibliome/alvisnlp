# org.bibliome.alvisnlp.modules.RegExp

## Synopsis

Matches a regular expression on sections contents and create an annotation for each match.

## Description

*org.bibliome.alvisnlp.modules.RegExp* searches for [pattern](#pattern) in the contents of sections, then creates an annotation for each match. The created annotations will span on the entire match. The created annotations will be added in the layer named [targetLayerName](#targetLayerName) of the corresponding section. If [pattern](#pattern) contains groups, then the pattern elements inside groups will be matched but the grouping will not be taken into account in the creation of the annotation.

The created annotations will automatically have all features defined in [constantAnnotationFeatures](#constantAnnotationFeatures).

## Parameters

<a name="pattern">

### pattern

Optional

Type: [Pattern](../converter/java.util.regex.Pattern)

Regular expression to match.

<a name="targetLayerName">

### targetLayerName

Optional

Type: [String](../converter/java.lang.String)

Name of the layer where to store matches.

<a name="constantAnnotationFeatures">

### constantAnnotationFeatures

Optional

Type: [Mapping](../converter/alvisnlp.module.types.Mapping)

Constant features to add to each annotation created by this module.

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

