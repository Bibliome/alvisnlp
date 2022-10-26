<h1 class="module">MultiRegExp</h1>

## Synopsis

Search for several regular expressions in sections contents.

**This module is experimental.**

## Description

 *MultiRegExp* attempts to match regular expression patterns read from <a href="#patternsFile" class="param">patternsFile</a> on section contents. The patterns file is a CSV file where one column contains patterns. The patterns must follow the [Java Pattern syntax](https://docs.oracle.com/javase/7/docs/api/java/util/regex/Pattern.html) .

 *MultiRegExp* creates an annotation in <a href="#targetLayer" class="param">targetLayer</a> for each match. Additionally *MultiRegExp* adds to the annotation a feature for each column corresponding to the matched pattern.

The matches for each individual pattern will not overlap, however matches of different patterns may overlap.

## Snippet



<button class="copy-code-button" title="Copy to clipboard" onclick="copy_code(this)">📋</button>
```xml
<multiregexp class="MultiRegExp>
    <patternsFile></patternsFile>
    <targetLayer></targetLayer>
    <valueFeatures></valueFeatures>
</multiregexp>
```

## Mandatory parameters

<h3 id="patternsFile" class="param">patternsFile</h3>

<div class="param-level param-level-mandatory">Mandatory
</div>
<div class="param-type">Type: <a href="../converter/fr.inra.maiage.bibliome.util.streams.SourceStream" class="converter">SourceStream</a>
</div>
CSV file containing patterns.

<h3 id="targetLayer" class="param">targetLayer</h3>

<div class="param-level param-level-mandatory">Mandatory
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Layer where to place annotations.

<h3 id="valueFeatures" class="param">valueFeatures</h3>

<div class="param-level param-level-mandatory">Mandatory
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String%5B%5D" class="converter">String[]</a>
</div>
Name of the features created for each annotation, corresponding to the columns of <a href="#patternsFile" class="param">patternsFile</a> including the patterns column.

## Optional parameters

<h3 id="constantAnnotationFeatures" class="param">constantAnnotationFeatures</h3>

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/fr.inra.maiage.bibliome.alvisnlp.core.module.types.Mapping" class="converter">Mapping</a>
</div>
Constant features to add to each annotation created by this module.

<h3 id="delimiter" class="param">delimiter</h3>

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/java.lang.Character" class="converter">Character</a>
</div>
Column delimiter of CSV file.

<h3 id="escape" class="param">escape</h3>

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/java.lang.Character" class="converter">Character</a>
</div>
Character used to escape characters in column values.

<h3 id="headerLine" class="param">headerLine</h3>

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/java.lang.Boolean" class="converter">Boolean</a>
</div>
Either to skip the first row.

<h3 id="quote" class="param">quote</h3>

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/java.lang.Character" class="converter">Character</a>
</div>
Character used to quote the column values.

<h3 id="trimValues" class="param">trimValues</h3>

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/java.lang.Boolean" class="converter">Boolean</a>
</div>
Either to trim leading and trailing whitespaces from column values.

<h3 id="baseFormat" class="param">baseFormat</h3>

<div class="param-level param-level-default-value">Default value: `Delimiter=<,> QuoteChar=<"> RecordSeparator=<
> EmptyLines:ignored SkipHeaderRecord:false`
</div>
<div class="param-type">Type: <a href="../converter/org.apache.commons.csv.CSVFormat" class="converter">CSVFormat</a>
</div>
Base format of CSV file. Must be either: deault, excel, mysql, rfc4180, oracle, postgresql_csv, postgresql_text, tdf, tab.

<h3 id="caseInsensitive" class="param">caseInsensitive</h3>

<div class="param-level param-level-default-value">Default value: `false`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.Boolean" class="converter">Boolean</a>
</div>
Either the match is insensitive to case.

<h3 id="documentFilter" class="param">documentFilter</h3>

<div class="param-level param-level-default-value">Default value: `true`
</div>
<div class="param-type">Type: <a href="../converter/fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.Expression" class="converter">Expression</a>
</div>
Only process document that satisfy this expression.

<h3 id="keyColumn" class="param">keyColumn</h3>

<div class="param-level param-level-default-value">Default value: `0`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.Integer" class="converter">Integer</a>
</div>
Column index that contains patterns. First column is `0` .

<h3 id="matchWordBoundaries" class="param">matchWordBoundaries</h3>

<div class="param-level param-level-default-value">Default value: `false`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.Boolean" class="converter">Boolean</a>
</div>
Only create annotations for matches that fit exactly between word boundaries.

<h3 id="sectionFilter" class="param">sectionFilter</h3>

<div class="param-level param-level-default-value">Default value: `true`
</div>
<div class="param-type">Type: <a href="../converter/fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.Expression" class="converter">Expression</a>
</div>
Process only sections that satisfy this expression.

