<h1 class="library">str</h1>

## Synopsis

Functions to manipulate character strings.

## Functons

<a name="after">

### after

`str:before(target, sub)`

Evaluates *target* and *sub* as strings, then returns the substring of *target* before the first occurrence of *sub*. If *sub* cannot be found, then this function returns *target*.

<a name="after">

### after

`str:after:sub(target)`

Evaluates *target* as a string, then returns the substring of *target* after the first occurrence of *sub*. If *sub* cannot be found, then this function returns *target*.

<a name="basename">

### basename

`str:basename(path)`

Treats *path* as a file path and returns the last element of *path*.

<a name="before">

### before

`str:after(target, sub)`

Evaluates *target* and *sub* as strings, then returns the substring of *target* after the first occurrence of *sub*. If *sub* cannot be found, then this function returns *target*.

<a name="before">

### before

`str:before:sub(target)`

Evaluates *target* as a string, then returns the substring of *target* before the first occurrence of *sub*. If *sub* cannot be found, then this function returns *target*.

<a name="cmp">

### cmp

`str:cmp(a, b)`

Evaluates *a* and *b* as strings, then compares the results. This function returns -1, 0 or 1 when *a* is lower, equal or greater than *b*.

<a name="diacritics">

### diacritics

`str:diacritics(s)`

Evaluates *s* as a string, then returns the result with diacritics removed.

<a name="equalsIgnoreCase">

### equalsIgnoreCase

`str:equalsIgnoreCase(a, b)`

Evaluates *a* and *b* as strings, then returns either the two strings are equal ignoring the character case.

<a name="hash">

### hash

`str:hash(s)`

Returns a hash code computed from *s*.

<a name="index">

### index

`str:index(s, target)`

Evaluates *s* and *target* as strings, then returns the number of characters in *s* before the first occurrence of *target* in *s*. If there is no occurrence of *target* in *s*, then this function returns -1.

<a name="join">

### join

`str:join(items, string, separator)`

Evaluates *items* as a list of elements and *separator* as a string. Then for each element of *items*, this function evaluates *string* as a string and returns a concatenation of the results seperated with *separator*

<a name="join">

### join

`str:join:separator(items, string)`

Evaluates *items* as a list of elements, then for each element of *items*, this function evaluates *string* as a string and returns a concatenation of the results seperated with *separator*

<a name="join">

### join

`str:join(items, string)`

Evaluates *items* as a list of elements, then for each element of *items*, this function evaluates *string* as a string and returns a concatenation of the results seperated with a single whitespace.

<a name="len">

### len

`str:len(target)`

Evaluates *target* as a string, then returns the number of characters in the result.

<a name="levenshtein">

### levenshtein

`str:levenshtein(a, b)`

Evaluates *a* and *b* as strings, then returns the edit distance between the results.

<a name="levenshteinSimilar">

### levenshteinSimilar

`str:levenshteinSimilar(a, b, d)`

Evaluates *a* and *b* as strings and *d* as a float, then returns either the edit distance between *a* and *b* is lower or equal than *d*. This function is useful for approximate string matching.

<a name="lower">

### lower

`str:lower(target)`

Evaluates *target* as a string and returns it with all characters converted to lower case.

<a name="normalizeSpace">

### normalizeSpace

`str:normalizeSpace(s)`

Evaluates *s* as a string, then returns the result with leading and trailing whitespace characters removed, and sequences of whitespaces characters replaced with a single space character.

<a name="padl">

### padl

`str:padl(s, n)`

Forces *s* to length *n*. If shorter, then this function adds as many spaces necessary at the beginning. If longer, then this function truncates *s*.

<a name="padl">

### padl

`str:padl:filler(s, n)`

Forces *s* to length *n*. If shorter, then this function adds as many *filler* necessary at the beginning. If longer, then this function truncates *s*.

<a name="padr">

### padr

`str:padr(s, n)`

Forces *s* to length *n*. If shorter, then this function adds as many spaces necessary at the end. If longer, then this function truncates *s*.

<a name="padr">

### padr

`str:padr:filler(s, n)`

Forces *s* to length *n*. If shorter, then this function adds as many *filler* necessary at the end. If longer, then this function truncates *s*.

<a name="regrp">

### regrp

`str:regrp(s, re, n)`

Compiles *re* as a regular expression and searches in *s*. This function returns the *n* capturing group of the regular expression.

<a name="replace">

### replace

`str:replace(target, search, replace)`

Evaluates *target*, *search* and *replace* as strings, then returns *target* with all non-overlapping occurrences of *search* substituted with *replace*.

<a name="rindex">

### rindex

`str:rindex(s, target)`

Evaluates *s* and *target* as strings, then returns the number of characters in *s* after the last occurrence of *target* in *s*. If there is no occurrence of *target* in *s*, then this function returns -1.

<a name="seds">

### seds

`str:seds(target, pattern, replace)`

Evaluates *target*, *pattern* and *replace* as strings, then interprets *pattern* as a [Java Regular Expression](http://download.oracle.com/javase/7/docs/api/java/util/regex/Pattern.html). This function returns *target* with all non-overlapping matches of *pattern* substituted with *replace*.

<a name="split">

### split

`str:split:separator:feature(target)`

Evaluate *target* as a string, then splits it by the first character of *separator*. The adds each string into the context feature named *feature*. Feature modification must be allowed.

<a name="sub">

### sub

`str:sub(target, from, to)`

Evaluates *target* as a string, *from* and *to* as integers, then returns the subsequence of *target* between indexes *from* (inclusive) and *to* (exclusive). Indexes start at 0.

<a name="sub">

### sub

`str:sub(target, from)`

Evaluates *target* as a string and *from* as an integer, then returns the subsequence of *target* from index *from* (inclusive) and *to* (exclusive)to its end. Indexes start at 0.

<a name="trim">

### trim

`str:trim(s)`

Evaluates *s* as a string, then returns the result with leading and trailing whitespace characters removed.

<a name="upper">

### upper

`str:upper(target)`

Evaluates *target* as a string and returns it with all characters converted to upper case.

