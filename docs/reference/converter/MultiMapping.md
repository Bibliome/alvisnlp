<h1 class="converter">MultiMapping</h1>

## Synopsis

Converts into a mapping from character strings keys to arrays of character strings.

## String conversion

String conversion is not available for this type.

## XML conversion

```xml
<param>
	<entry key="KEY1" value="VALUE1"/>
	<entry key="KEY2">VALUE2</entry>
	...</param>
```


	or
	```xml
<param>
	<KEY1>VALUE1</KEY1>
	<KEY2>VALUE2</KEY2>
	...</param>
```



*VALUEn* are converted as <a href="../converter/String[]array of strings" class="converter">String[]array of strings</a>. The value separator can be set with the *separator* attribute.

