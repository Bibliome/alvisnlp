<h1 class="library">pred</h1>

## Synopsis



## Functons

<a name="all">

### all

`pred:all(c, pred)`

Evaluates *c* as a list of elements, then for each element evaluates *pred* as a boolean. Returns true if all evaluate as *true*.

<a name="any">

### any

`pred:any(c, pred)`

Evaluates *c* as a list of elements, then for each element evaluates *pred* as a boolean. Returns true if at least one evaluates as *true*.

<a name="enum-while">

### enum-while

`pred:enum-while:varName(start, end, expr, condition)`

Evaluates *start* and *end* as integers, then assigns to *varName* each value between *start* (inclusive) and *end* (exclusive) and evaluates *expr* as a list of elements. Before evaluating *expr*, evaluates *condition* as a boolean. If *condition* is false, then stops iterating the list. Returns all elements in a single list.

<a name="enum-while">

### enum-while

`pred:enum-while:varName(start, expr, condition)`



<a name="enumerate">

### enumerate

`pred:enumerate:varName(start, end, expr)`

Evaluates *start* and *end* as integers, then assigns to *varName* each value between *start* (inclusive) and *end* (exclusive) and evaluates *expr* as a list of elements. Returns all elements in a single list.

