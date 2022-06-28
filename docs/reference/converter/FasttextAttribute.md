<h1 class="converter">FasttextAttribute</h1>

## Synopsis

Converts a <a href="../converter/FasttextAttribute" class="converter">FasttextAttribute</a> used to describe documents in <a href="../module/FasttextClassifierTrain" class="module">FasttextClassifierTrain</a> and <a href="../module/FasttextClassifierLabel" class="module">FasttextClassifierLabel</a> .

## String conversion

String conversion is not available for this type.

## XML conversion



```xml

					<attribute form="FORM" tokens="TOKENS"/>
				
```



 *TOKENS* is an expression evaluated from the document element as a list of elements. Each element represents a token.

 *FORM* is an expression evaluated from each token as a string. If omitted then it defaults to `@form` .

