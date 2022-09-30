<h1 class="module">TabularReader</h1>

## Synopsis

Reads a tabular file and applies actions for each line.

**This module is experimental.**

## Description

 *TabularReader* reads <a href="#source" class="param">source</a> as a tabular file and for each line evaluates each <a href="#lineActions" class="param">lineActions</a> .

## Snippet



<button class="copy-code-button" title="Copy to clipboard" onclick="copy_code(this)">📋</button>
```xml
<tabularreader class="TabularReader>
    <lineActions></lineActions>
    <source></source>
    <sourceElement></sourceElement>
</tabularreader>
```

## Mandatory parameters

<h3 id="lineActions" class="param">lineActions</h3>

<div class="param-level param-level-mandatory">Mandatory
</div>
<div class="param-type">Type: <a href="../converter/fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.Expression%5B%5D" class="converter">Expression[]</a>
</div>
Action expressions to evaluate at each row. The expressions are evaluated from the element specified by <a href="#sourceElement" class="param">sourceElement</a> .

The library `tab` defines the following functions:
*  `tab:column(N)` : value of the *Nth* column.
*  `tab:field:NAME` or `tab:field(NAME)` : value of the column named *NAME* (requires <a href="#header" class="param">header</a> set to *true* 
*  `tab:source` : name of the input source being parsed.
*  `tab:line` : current row number.
*  `tab:width` : number of column in the current row.



<h3 id="source" class="param">source</h3>

<div class="param-level param-level-mandatory">Mandatory
</div>
<div class="param-type">Type: <a href="../converter/fr.inra.maiage.bibliome.util.streams.SourceStream" class="converter">SourceStream</a>
</div>
Source of CSV. Maybe a path to a file or an URL

<h3 id="sourceElement" class="param">sourceElement</h3>

<div class="param-level param-level-mandatory">Mandatory
</div>
<div class="param-type">Type: <a href="../converter/fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.Expression" class="converter">Expression</a>
</div>
For each source, this expression is evaluated as a single element from the corpus. <a href="#lineActions" class="param">lineActions</a> will be evaluated from this element.

## Optional parameters

<h3 id="checkNumColumns" class="param">checkNumColumns</h3>

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/java.lang.Integer" class="converter">Integer</a>
</div>
Either to check that all rows have the same number of columns. The execution will fail if one row has a wrong number of columns.

<h3 id="constantAnnotationFeatures" class="param">constantAnnotationFeatures</h3>

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/fr.inra.maiage.bibliome.alvisnlp.core.module.types.Mapping" class="converter">Mapping</a>
</div>
Constant features to add to each annotation created by this module.

<h3 id="constantDocumentFeatures" class="param">constantDocumentFeatures</h3>

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/fr.inra.maiage.bibliome.alvisnlp.core.module.types.Mapping" class="converter">Mapping</a>
</div>
Constant features to add to each document created by this module.

<h3 id="constantRelationFeatures" class="param">constantRelationFeatures</h3>

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/fr.inra.maiage.bibliome.alvisnlp.core.module.types.Mapping" class="converter">Mapping</a>
</div>
Constant features to add to each relation created by this module.

<h3 id="constantSectionFeatures" class="param">constantSectionFeatures</h3>

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/fr.inra.maiage.bibliome.alvisnlp.core.module.types.Mapping" class="converter">Mapping</a>
</div>
Constant features to add to each section created by this module.

<h3 id="constantTupleFeatures" class="param">constantTupleFeatures</h3>

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/fr.inra.maiage.bibliome.alvisnlp.core.module.types.Mapping" class="converter">Mapping</a>
</div>
Constant features to add to each tuple created by this module.

<h3 id="addToLayer" class="param">addToLayer</h3>

<div class="param-level param-level-default-value">Default value: `false`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.Boolean" class="converter">Boolean</a>
</div>
Allow to add annotations to layers.

<h3 id="commitLines" class="param">commitLines</h3>

<div class="param-level param-level-default-value">Default value: `false`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.Boolean" class="converter">Boolean</a>
</div>
Commit changes at each line.

<h3 id="createAnnotations" class="param">createAnnotations</h3>

<div class="param-level param-level-default-value">Default value: `false`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.Boolean" class="converter">Boolean</a>
</div>
Allow the creation of annotations.

<h3 id="createDocuments" class="param">createDocuments</h3>

<div class="param-level param-level-default-value">Default value: `false`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.Boolean" class="converter">Boolean</a>
</div>
Allow the creation of documents.

<h3 id="createRelations" class="param">createRelations</h3>

<div class="param-level param-level-default-value">Default value: `false`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.Boolean" class="converter">Boolean</a>
</div>
Allow the creation of relations.

<h3 id="createSections" class="param">createSections</h3>

<div class="param-level param-level-default-value">Default value: `false`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.Boolean" class="converter">Boolean</a>
</div>
Allow the creation of sections.

<h3 id="createTuples" class="param">createTuples</h3>

<div class="param-level param-level-default-value">Default value: `false`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.Boolean" class="converter">Boolean</a>
</div>
Allow to create tuples.

<h3 id="deleteElements" class="param">deleteElements</h3>

<div class="param-level param-level-default-value">Default value: `false`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.Boolean" class="converter">Boolean</a>
</div>
Allow to delete elements.

<h3 id="header" class="param">header</h3>

<div class="param-level param-level-default-value">Default value: `false`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.Boolean" class="converter">Boolean</a>
</div>
Either to skip the first row.

<h3 id="removeFromLayer" class="param">removeFromLayer</h3>

<div class="param-level param-level-default-value">Default value: `false`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.Boolean" class="converter">Boolean</a>
</div>
Allow to remove annotations from layers.

<h3 id="separator" class="param">separator</h3>

<div class="param-level param-level-default-value">Default value: `	`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.Character" class="converter">Character</a>
</div>
Column separator character.

<h3 id="setArguments" class="param">setArguments</h3>

<div class="param-level param-level-default-value">Default value: `false`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.Boolean" class="converter">Boolean</a>
</div>
Allow to set tuple arguments.

<h3 id="setFeatures" class="param">setFeatures</h3>

<div class="param-level param-level-default-value">Default value: `false`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.Boolean" class="converter">Boolean</a>
</div>
Allow to set element features.

<h3 id="skipBlank" class="param">skipBlank</h3>

<div class="param-level param-level-default-value">Default value: `false`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.Boolean" class="converter">Boolean</a>
</div>
Either to skip blank lines.

<h3 id="trimColumns" class="param">trimColumns</h3>

<div class="param-level param-level-default-value">Default value: `true`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.Boolean" class="converter">Boolean</a>
</div>
Either to trim values from leading and trailing whitespace

<h3 id="trueCSV" class="param">trueCSV</h3>

<div class="param-level param-level-default-value">Default value: `false`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.Boolean" class="converter">Boolean</a>
</div>
Use CSV Commons library (handles quoted values).

