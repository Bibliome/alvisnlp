# org.bibliome.alvisnlp.modules.mapper.ElementMapper

## Synopsis

Maps elements according to a collection of mapping elements.

**This module is experimental.**

## Description

*org.bibliome.alvisnlp.modules.mapper.ElementMapper* evaluates [entries](#entries) as a list of elements with the corpus as the context element. These elements represent the entries from which target elements are matched. [key](#key) specifies the key of each entry, and [values](#values) specifies the values of the entries. [target](#target) specifies the mapped elements; the [form](#form) is matched against the key of entries. If the target element matches, *org.bibliome.alvisnlp.modules.mapper.ElementMapper* adds the features [targetFeatures](#targetFeatures) with the matched entry values.

## Parameters

<a name="entries">

### entries

Optional

Type: [Expression](../converter/alvisnlp.corpus.expressions.Expression)

Expression evaluated as a list of elements with the corpus as the context element. Each element represents an entry.

<a name="form">

### form

Optional

Type: [Expression](../converter/alvisnlp.corpus.expressions.Expression)

Expression evaluated as a string with the target element as context that specifies the target key.

<a name="key">

### key

Optional

Type: [Expression](../converter/alvisnlp.corpus.expressions.Expression)

Expression evaluated as a string with the entry element as the context. The result determines the entry key.

<a name="target">

### target

Optional

Type: [Expression](../converter/alvisnlp.corpus.expressions.Expression)

Expression evaluated as a list of elements with the corpus as the context element. The result determines the elements to be mapped.

<a name="targetFeatures">

### targetFeatures

Optional

Type: [String[]](../converter/java.lang.String[])

Name of the features where to store the values of matched entries.

<a name="values">

### values

Optional

Type: [Expression[]](../converter/alvisnlp.corpus.expressions.Expression[])

Expressions evaluated as strings with the entry element as the context. The results specify the feature values of targets that match the entry key.

<a name="ignoreCase">

### ignoreCase

Default value: `false`

Type: [Boolean](../converter/java.lang.Boolean)

Either to match ignoring the case.

<a name="operator">

### operator

Default value: `exact`

Type: [MappingOperator](../converter/org.bibliome.alvisnlp.modules.mapper.MappingOperator)

Matching operator.

