<h1 class="library">sort</h1>

## Synopsis

Functions to sort lists of elements.

The functions in this library come in three versions:
  
* *simple sort*: the list is sorted, duplicates and equivalent elements are kept;
* *sort and remove duplicates*: the list is sorted and duplicates are removed, however equivalent elements are kept (modifier: "u");
* *sort and remove equivalents*: the list is sorted and equivalent elements are removed (modifier: "n").



## Functons

<a name="comp">

### comp

`sort:comp(list, comparator)`

Evaluates *list* as a list of elements, then sorts the result according to the expression *comparator*. The *comparator* expression is evaluated as an integer and must return a number below zero (lower than), zero (equals) or above zero (greater than).

<a name="dval">

### dval

`sort:dval(list, value)`

Evaluates *list* as a list of elements, then evaluates *value* as a double for each element. Returns the list sorted by *value*.

<a name="ival">

### ival

`sort:ival(list, value)`

Evaluates *list* as a list of elements, then evaluates *value* as an integer for each element. Returns the list sorted by *value*.

<a name="ncomp">

### ncomp

`sort:ncomp(list, comparator)`

Same as <a href="#comp" class="function">comp</a>, remove equivalents.

<a name="ndval">

### ndval

`sort:ndval(list, value)`

Same as <a href="#dval" class="function">dval</a>, remove equivalents.

<a name="nival">

### nival

`sort:nival(list, value)`

Same as <a href="#ival" class="function">ival</a>, remove equivalents.

<a name="nsval">

### nsval

`sort:nsval(list, value)`

Same as <a href="#sval" class="function">sval</a>, remove equivalents.

<a name="reverse">

### reverse

`sort:reverse(list)`

Evaluates *list* as a list of elements and returns this list reversed.

<a name="sval">

### sval

`sort:sval(list, value)`

Evaluates *list* as a list of elements, then evaluates *value* as a string for each element. Returns the list sorted by *value* (lexical sort).

<a name="ucomp">

### ucomp

`sort:ucomp(list, comparator)`

Same as <a href="#comp" class="function">comp</a>, remove duplicates.

<a name="udval">

### udval

`sort:udval(list, value)`

Same as <a href="#dval" class="function">dval</a>, remove duplicates.

<a name="uival">

### uival

`sort:uival(list, value)`

Same as <a href="#ival" class="function">ival</a>, remove duplicates.

<a name="usval">

### usval

`sort:usval(list, value)`

Same as <a href="#sval" class="function">sval</a>, remove duplicates.

