<h1 class="converter">Aggregator</h1>

## Synopsis

Converts into aggregator functions (see <a href="../module/AggregateValues" class="module">AggregateValues</a>).

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

Converts *VALUE* as an <a href="../converter/Expression" class="converter">Expression</a> evaluated as an integer from each entry. The aggregate value is the sum of the results.

