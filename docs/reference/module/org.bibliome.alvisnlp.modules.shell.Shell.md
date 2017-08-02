# org.bibliome.alvisnlp.modules.shell.Shell

## Synopsis

Starts an interactive shell that allows to query the corpus data structure.

## Description

*org.bibliome.alvisnlp.modules.shell.Shell* starts an interactive shell. The user may issue commands in order to explore the state of the corpus.

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

	Evaluates *expression* as a list of elements. If the result is not empty, then the shell sets the current element to the first element of the result. *org.bibliome.alvisnlp.modules.shell.Shell* keeps track of all *@move* commands in a stack, the following commands allow to navigate through this stack.
      

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

Optional

Type: [Mapping](../converter/alvisnlp.module.types.Mapping)

Constant features to add to each annotation created by this module

<a name="constantDocumentFeatures">

### constantDocumentFeatures

Optional

Type: [Mapping](../converter/alvisnlp.module.types.Mapping)

Constant features to add to each document created by this module

<a name="constantRelationFeatures">

### constantRelationFeatures

Optional

Type: [Mapping](../converter/alvisnlp.module.types.Mapping)

Constant features to add to each relation created by this module

<a name="constantSectionFeatures">

### constantSectionFeatures

Optional

Type: [Mapping](../converter/alvisnlp.module.types.Mapping)

Constant features to add to each section created by this module

<a name="constantTupleFeatures">

### constantTupleFeatures

Optional

Type: [Mapping](../converter/alvisnlp.module.types.Mapping)

Constant features to add to each tuple created by this module

<a name="historyFile">

### historyFile

Default value: `/home/rbossy/.alvisnlp/shell_history`

Type: [OutputFile](../converter/org.bibliome.util.files.OutputFile)

Path to the command history file.

<a name="prompt">

### prompt

Default value: `> `

Type: [String](../converter/java.lang.String)

Specifies the shell prompt.

