# Aggregator

## Synopsis

Converts into aggregator functions (see [AggregateValues](../module/AggregateValues)).

## String conversion

**String conversion is not available for this data type.**

## XML conversion

```xml

	<count/>
      ```

Count the number of occurrences of each key.

```xml

	<sum>VALUE</sum>
      ```

Converts *VALUE* as an [Expression](../converter/Expression) evaluated as an integer from each entry. The aggregate value is the sum of the results.

