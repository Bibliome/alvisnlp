<h1 class="converter">OutputFile</h1>

## Synopsis

Converts into the path to a writable file.

## String conversion

The string value is interpreted as a path to a directory. The value must be a valid file system path to either an existing regular file or a descendant of an existing directory with writing for the current user, or a file where one of its directory ancestor exists with writing and traversal rights for the current user.

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

*PATH* is converted into an *OutputFile* as specified by the string conversion.
  

