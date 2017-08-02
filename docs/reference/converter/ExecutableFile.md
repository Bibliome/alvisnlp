<h1 class="converter">ExecutableFile</h1>

## Synopsis

Converts into the path to an executable file.

## String conversion

The string value is interpreted as a path to a file. The value must be a valid file system path to an existing file with execution rights for the current user.

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

*PATH* is converted into an *ExecutableFile* as specified by the string conversion.
  

