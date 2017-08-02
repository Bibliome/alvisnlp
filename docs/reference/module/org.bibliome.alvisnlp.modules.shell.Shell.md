<h1 class="module">Shell</h1>

## Synopsis

Starts an interactive shell that allows to query the corpus data structure.

## Description

*Shell* starts an interactive shell. The user may issue commands in order to explore the state of the corpus.

### Shell commands`@query expression`

	Evaluates *expression* with the current element as the context element and prints the result. The shell attempts to determine the priviledged type of the expression, if the type could not be determined, the the user must coerce it with one of the type coercion expressions. If the result is a list of elements then a short summary of each element is printed on screen.
  

`@allow everything|delete|args|features`
`@allow create all|documents|sections|relations|tuples|annotations`

	Allows action expressions in query commands.
  

`@features [expression]`

	Evaluates *expression* as a list of elements, then prints all features for each element in the result. If *expression* is omitted, then prints all features of the current element.
  

`@ref name expression`

	Evaluates *expression*, then assigns the result to the reference *name*. This reference is accessible to all expressions in subsequent commands.
  

`@move expression`

	Evaluates *expression* as a list of elements. If the result is not empty, then the shell sets the current element to the first element of the result. *Shell* keeps track of all *@move* commands in a stack, the following commands allow to navigate through this stack.
  

`@next`

	Sets the current element to the next element in the result list of the last *@move* command. If the current element was the last element, then this command does nothing.
  

`@prev`

	Sets the current element to the previous element in the result list of the last *@move* command. If the current element was the first element, then this command does nothing.
  

`@up`

	Sets the current element to the current element in the result of the *@move* command before the last one. If the last *@move* command was the first in the shell session, then this command sets the current element to the corpus.
  

`@stack`

	This command prints the current element of all *@move* commands in order of execution.
  

`@state`

	This command prints which action expressions are allowed in the current session, as well as all defined references.
  

## Parameters

<a name="constantAnnotationFeatures">

### constantAnnotationFeatures

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/alvisnlp.module.types.Mapping" class="converter">Mapping</a>
</div>
Constant features to add to each annotation created by this module

<a name="constantDocumentFeatures">

### constantDocumentFeatures

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/alvisnlp.module.types.Mapping" class="converter">Mapping</a>
</div>
Constant features to add to each document created by this module

<a name="constantRelationFeatures">

### constantRelationFeatures

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/alvisnlp.module.types.Mapping" class="converter">Mapping</a>
</div>
Constant features to add to each relation created by this module

<a name="constantSectionFeatures">

### constantSectionFeatures

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/alvisnlp.module.types.Mapping" class="converter">Mapping</a>
</div>
Constant features to add to each section created by this module

<a name="constantTupleFeatures">

### constantTupleFeatures

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/alvisnlp.module.types.Mapping" class="converter">Mapping</a>
</div>
Constant features to add to each tuple created by this module

<a name="historyFile">

### historyFile

<div class="param-level param-level-default-value">Default value: `/home/rbossy/.alvisnlp/shell_history`
</div>
<div class="param-type">Type: <a href="../converter/org.bibliome.util.files.OutputFile" class="converter">OutputFile</a>
</div>
Path to the command history file.

<a name="prompt">

### prompt

<div class="param-level param-level-default-value">Default value: `> `
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Specifies the shell prompt.

