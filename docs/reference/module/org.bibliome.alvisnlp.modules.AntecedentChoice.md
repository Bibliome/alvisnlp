# org.bibliome.alvisnlp.modules.AntecedentChoice

## Synopsis

Biotopes-specific module: chooses an antecedent.

## Description

This module is project-specific and should be short-lived. It assumes a relation named *coreferences* containing tuples with at least one argument with role *Anaphora* and another with role *AntePreviousLowerTaxon*, a layer named *biAnaphora*.

## Parameters

<a name="documentFilter">

### documentFilter

Default value: `true`

Type: [Expression](../converter/alvisnlp.corpus.expressions.Expression)

Only process documents that satisfy this filter.

<a name="sectionFilter">

### sectionFilter

Default value: `true`

Type: [Expression](../converter/alvisnlp.corpus.expressions.Expression)

Process only sections that satisfy this filter.

