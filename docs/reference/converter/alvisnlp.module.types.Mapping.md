<h1 class="converter">Mapping</h1>

## Synopsis

Converts into a mapping from character strings keys to character strings values.

## String conversion

Entries are separated by commas. The entry key and value are separated by an equal (*=*) character. Keys and values are stripped from leading and trailing whitespace characters.

The entry separator can be modified with the *separator* attribute. The separator between keys and their respective value can be modified with the *qualifier* attribute.

## XML conversion

```xml
<param value="MAPPING"/>
```


	or
	```xml
<param>MAPPING</param>
```


	where *MAPPING* will be converted according to the string conversion specifications.
  

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



