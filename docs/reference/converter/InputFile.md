<h1 class="converter">InputFile</h1>

## Synopsis

Converts into the path to a readable file.

## String conversion

The string value is interpreted as a path to a regular file. The value must be a valid file system path to an existing regular file with reading rights for the current user.

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

*PATH* is converted into an *InputFile* as specified by the string conversion.
  

