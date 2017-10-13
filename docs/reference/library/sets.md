<h1 class="library">sets</h1>

## Synopsis

Functions to compute set operations.

## Functons

<a name="diff">

### diff

`sets:diff(a, b)`

Evaluates *a* and *b* as lists of elements, then returns the elements in the former that are not in the latter.

<a name="included">

### included

`sets:all(a, b)`

Evaluates *a* and *b* as lists of elements, then returns either all elements in the former are in the latter.

<a name="inter">

### inter

`sets:inter(a, b)`

Evaluates *a* and *b* as lists of elements, then returns the intersection of the results.

<a name="union">

### union

`sets:union(a, b)`

Evaluates *a* and *b* as lists of elements, then returns the union of the results. The difference between this function and the union operator "|" is that this function removes duplicates.

