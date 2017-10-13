<h1 class="library">align</h1>

## Synopsis

*align* provides functions to align two list of elements using the [Needleman-Wunsch]() algorithm.

## Functons

<a name="proba">

### proba

`align:proba(gap, match, a, b)`

Evaluates *a* and *b* as lists of elements and aligns them using the [Needleman-Wunsch]() algorithm, then returns the optimal alignment probability.

*gap* is evaluated as a number and specifies the gap penalty.

*match* is evaluated as a number for each pair of elements. The context element is an item form *a* and the item from *b* can be retrieved with the function *other*.

<a name="score">

### score

`align:score(gap, match, a, b)`

This function aligns two lists. The difference from *align:proba* is that this function returns the alignment score.

