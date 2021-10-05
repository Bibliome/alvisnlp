<h1 class="module">AggregateValues</h1>

## Synopsis

*AggregateValues* lists a set of values and computes aggregate values.

**This module is experimental.**

## Description

*AggregateValues* lists a set of elements specified by <a href="#entries" class="param">entries</a>, and computes a string value for each specified by <a href="#key" class="param">key</a>.
  	*AggregateValues* computes aggregates functions specified by <a href="#aggregators" class="param">aggregators</a> for each distinct value.
  	The list of entry keys and their aggregate values are written into <a href="#outFile" class="param">outFile</a>

### Example
  	Counting words in the corpus:
  



```xml

      	<word-count class="AggregateValues">
      		<entries>documents.sections.layer:words</entries>
      		<key>@form</key>
      		<aggregators>
      			<count/>
      		</aggregators>
      		<outFile>word-count.txt</outFile>
      	</word-count>
      
```

## Parameters

<h3 name="entries" class="param">entries</h3>

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.Expression" class="converter">Expression</a>
</div>
Expression evaluated from the corpus as a list of elements.

<h3 name="key" class="param">key</h3>

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.Expression" class="converter">Expression</a>
</div>
Value of each entry. This expression is evaluated as a string from the entry element.

<h3 name="outFile" class="param">outFile</h3>

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/fr.inra.maiage.bibliome.util.streams.TargetStream" class="converter">TargetStream</a>
</div>
File where to write the result.

<h3 name="aggregators" class="param">aggregators</h3>

<div class="param-level param-level-default-value">Default value: ``
</div>
<div class="param-type">Type: <a href="../converter/fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.aggregate.Aggregator%5B%5D" class="converter">Aggregator[]</a>
</div>
Aggregate functions to compute for each value.

<h3 name="separator" class="param">separator</h3>

<div class="param-level param-level-default-value">Default value: `	`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.Character" class="converter">Character</a>
</div>
Character that separates columns in the result file.

