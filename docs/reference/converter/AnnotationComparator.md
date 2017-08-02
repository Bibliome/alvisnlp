<h1 class="converter">AnnotationComparator</h1>

## Synopsis

Converts into an annotation comparison function.

## String conversion

Accepted values:
  
* *start*: compare annotations by their start position;
* *end*: compare annotations by their end position;
* *length*: compare annotations by their length in number of characters;
* *order*: compare annotations by their start position, then by the inverse of end position;


  By prepending *reverse-*, the comparison is reversed.
  

A composite comparator may be specified by separating multiple values with commas (","). In this case the first comparator is used, if annotations are equal, then the second is used, etc. The *order* comparator is thus equivalent to *start,reverse-end*.

## XML conversion

```xml
<param value="COMPARATOR"/>
```


	or
	```xml
<param comparator="COMPARATOR"/>
```


	or
	```xml
<param>COMPARATOR</param>
```

*COMPARATOR* is converted into an annotation comparator as specified by the string conversion.
  

