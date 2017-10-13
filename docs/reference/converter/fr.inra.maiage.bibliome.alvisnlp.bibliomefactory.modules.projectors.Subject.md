<h1 class="converter">Subject</h1>

## Synopsis

Converts into a projection subject.

## String conversion

In string conversion the subject is necessarily the section contents. The converter recognizes one of the following values:
  
* *plain*: searches for entries in the section contents without bounday check;
* *words*: searches for entries in the section contents and checks the match starts and ends at word boundaries;
* *prefix*: searches for entries in the section contents and checks the match starts at a word boundary;
* *suffix*: searches for entries in the section contents and checks the match ends at a word boundary;



## XML conversion

For matching the section contents:
```xml
<param value="plain|words|prefix|suffix"/>
```



  or:
```xml
<param contents="plain|words|prefix|suffix"/>
```



  or:
```xml
<param>plain|words|prefix|suffix</param>
```



For matching annotation feature values:
  ```xml
<param feature="FEATUREKEY" layer="LAYERNAME"/>
```



