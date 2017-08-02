# org.bibliome.alvisnlp.modules.aggregate.AggregateValues

## Synopsis

*org.bibliome.alvisnlp.modules.aggregate.AggregateValues* lists a set of values and computes aggregate values.

**This module is experimental.**

## Description

*org.bibliome.alvisnlp.modules.aggregate.AggregateValues* lists a set of elements specified by [entries](#entries), and computes a string value for each specified by [key](#key).
      	*org.bibliome.alvisnlp.modules.aggregate.AggregateValues* computes aggregates functions specified by [aggregators](#aggregators) for each distinct value.
      	The list of entry keys and their aggregate values are written into [outFile](#outFile)

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

<a name="entries">

### entries

Optional

Type: [Expression](../converter/alvisnlp.corpus.expressions.Expression)

Expression evaluated from the corpus as a list of elements.

<a name="key">

### key

Optional

Type: [Expression](../converter/alvisnlp.corpus.expressions.Expression)

Value of each entry. This expression is evaluated as a string from the entry element.

<a name="outFile">

### outFile

Optional

Type: [TargetStream](../converter/org.bibliome.util.streams.TargetStream)

File where to write the result.

<a name="aggregators">

### aggregators

Default value: ``

Type: [Aggregator[]](../converter/org.bibliome.alvisnlp.modules.aggregate.Aggregator[])

Aggregate functions to compute for each value.

<a name="separator">

### separator

Default value: `	`

Type: [Character](../converter/java.lang.Character)

Character that separates columns in the result file.

