<h1 class="converter">ExpressionMapping</h1>

## Synopsis

Converts to a mapping of string keys to <a href="../converter/alvisnlp.document.expression.Expression" class="converter">alvisnlp.document.expression.Expression</a>.

## String conversion

Comma separated of key/value pairs. The keys are strings and values are element expressions. Keys and values are separated by an equal sign (*=*).

The entry separator can be set with the *separator* attribute. The separator between the key and its value can be set with the *qualifier* attribute.

## XML conversion


1. String conversion of the tag contents or the value of attribute *value*.
2. ```xml
<KEY>VALUE</KEY>
```

```xml
<entry>VALUE</entry>
```





