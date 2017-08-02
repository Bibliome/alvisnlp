<h1 class="converter">TargetStream</h1>

## Synopsis

Converts into a data sink.

## String conversion

The data source takes either the form of a file/directory path, or an URL. The following protocols are recognized:
  
* *file*: the data is written in the specified regular file path;
* *stream*: the data is written to the AlvisNLP/ML standard output or error, the host part of the URI must be either "stdout" or "stderr" (*stream://stdout*);


  If an absolute or relative path is specified, then *file* is assumed.
  Relative paths are relative to the current working directory.
  

## XML conversion

```xml
<param value="URL"/>
```


	or
	```xml
<param file="URL"/>
```


	or
	```xml
<param path="URL"/>
```

*URL* is converted as described in the string conversion.
	All different forms are equivalent; the name of the used attribute has no influence on the data source type.
	It is thus perfectly legal to write ```xml
<param file="stream://stdout"/>
```

.
	Two other attributes allow to control finely the data source:
	
* *charset*: specifies the character encoding of the data ("UTF-8" by default);
* *encoding*: same as above;



If ```xml
param
```

 has children elements, then each element will be converted as a data sink. The data will be duplicated into each data sink.

