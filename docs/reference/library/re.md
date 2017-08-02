<h1 class="library">re</h1>

## Synopsis



## Functons

<a name="find">

### find

`re:find:pattern(target, fun)`

<a name="findall">

### findall

`re:findall:pattern(target, fun)`

Evaluates *target* as a string, then applies the *pattern* regular expression to *target*. Evaluates *fun* as a list of elements for each match, *fun* accepts additional functions from <a href="../library/match" class="library">match</a>. Returns the list of all elements that each call the *fun* returned.

