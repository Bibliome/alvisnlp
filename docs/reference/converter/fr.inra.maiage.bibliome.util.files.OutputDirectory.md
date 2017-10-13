<h1 class="converter">OutputDirectory</h1>

## Synopsis

Converts into the path to a writable directory.

## String conversion

The string value is interpreted as a path to a directory. The value must be a valid file system path to an existing directory or a descendant to an existing directory with reading, writing and traversal rights for the current user.

## XML conversion

```xml
<param value="PATH"/>
```


	or
	```xml
<param path="PATH"/>
```


	or
	```xml
<param file="PATH"/>
```


	or
	```xml
<param>PATH</param>
```

*PATH* is converted into an *OutputDirectory* as specified by the string conversion.
  

