# OverlappingBehaviour

## Synopsis

Specifies the behaviour of [PatternMatcher](../module/PatternMatcher) when attempting to match sequence of overlapping annotations.

## String conversion


* *ignore*: search on the sequence of annotations in standard order regardless of overlaps;
* *reject*: skip sequences of annotations if there are overlapping annotations;
* *remove*: remove overlapping annotations from the sequence;
* *multiplex*: multiplex the sequence as a set of non-overlapping sequences.



## XML conversion

String conversion of the tag contents or the value of attribute *value*.

