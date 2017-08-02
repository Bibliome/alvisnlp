<h1 class="converter">RelationDefinition</h1>

## Synopsis

Converts into a relation definition for <a href="../module/TaggingElementClassifier" class="module">TaggingElementClassifier</a>, <a href="../module/SelectingElementClassifier" class="module">SelectingElementClassifier</a> or <a href="../module/TrainingElementClassifier" class="module">TrainingElementClassifier</a>.

## String conversion

String conversion is not available for this type.

## XML conversion

```xml
<relation>... attribute and bag definitions</relation>
```


	The *name* attribute is optional.
  


	Attribute definition:
	```xml
<attr>EXPR</attr>
```


* *NAME* is the name of the attribute (mandatory);
* *CLASS* is a boolean that indicates if this attribute is the prediction target, by default it is false, it is an error if there are several class attributes;
* *TYPE* is either *int*, *bool* or *nominal* indicating the attribute type, it is boolean by default, if it is *nominal* then each possible value must be set in a separate *value* tag;
* *EXPR* is an <a href="../converter/alvisnlp.document.expression.Expression" class="converter">alvisnlp.document.expression.Expression</a> evaluated as the type corresponding to *TYPE* with the example element as the context element.




	Bag definition:
	```xml
<bag><value>VALUE</value>...</bag>
```


	or
	```xml
<bag>EXPR</bag>
```


* *PREFIX* is the prefix of each attribute name (mandatory);
* *KEY* is the feature name of the element that will indicate the attribute suffix;
* *COUNT* is a boolean indicating either the attribute value is numeric (occurrences count), by default it is false;
* *EXPR* is an <a href="../converter/alvisnlp.document.expression.Expression" class="converter">alvisnlp.document.expression.Expression</a> evaluated as a list of elements with the example element as the context element.


	The list of attribute suffixes can be given either with *value* tags or by *FILE*, the path to a file containing one suffix per line.
  


	The relation definition can be loaded from an external file specified by the *load* attribute.
  

