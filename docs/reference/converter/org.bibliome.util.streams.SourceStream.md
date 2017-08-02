<h1 class="converter">SourceStream</h1>

## Synopsis

Converts into a data source.

## String conversion

The data source takes either the form of a file/directory path, or an URL. The following protocols are recognized:
  
* *file*: the data is in the specified regular file path;
* *dir*: the data is in several files in the specified directory path, if the specified path is a regular file the it behaves like *file*;
* *stream*: the data comes from the AlvisNLP/ML standard input, the host part of the URI must be "stdin" (*stream://stdin*);
* *resource*: the data is embedded with the AlvisNLP/ML distribution, the host and directory part of the URL specifies the resource to be used: each module should document which suitable resources are available;
* *http*, *https*, *ftp* ...: the data is at the specified URL, AlvisNLP/ML will fetch the data across the net.


  If an absolute or relative path is specified, then *dir* is assumed.
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


	or
	```xml
<param url="URL"/>
```


	or
	```xml
<param href="URL"/>
```


	or
	```xml
<param dir="URL"/>
```


	or
	```xml
<param resource="URL"/>
```


	or
	```xml
<param>URL</param>
```

*URL* is converted as described in the string conversion.
	All different forms are equivalent; the name of the used attribute has no influence on the data source type.
	It is thus perfectly legal to write ```xml
<param resource="dir:///path/to/dir"/>
```

.
	Other attributes allow to specify the data source:
	
* *charset*: specifies the character encoding of the data ("UTF-8" by default);
* *encoding*: same as above;
* *filter*: a regular expression that specifies which files to read for the *dir* scheme (no filter by default);
* *fullNameFilter*: a boolean that specifies if the filter applies to the full name of the files in the directory (false by default: the filter applies to the file name);
* *wholeMatch*: a boolean that specifies if the filter must match the entire name (or full name) of the file in the directory (false by default: the regular expression is searched within the file name);
* *recursive*: a boolean that specifies if sub-directories should be read in the *dir* scheme (false by default);
* *compression*: compression algorithm of the stream: *none*, *gz* or *gzip* (none by default);



If ```xml
param
```

 has children elements, then each element will be converted as a data source. The resulting is the concatenation of all data sources.

