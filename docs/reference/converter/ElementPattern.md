<h1 class="converter">ElementPattern</h1>

## Synopsis

Converts into an annotation pattern to be used by <a href="../module/PatternMatcher" class="module">PatternMatcher</a>.

## String conversion

A pattern consists in a sequence of predicates. A predicate is a condition on an annotation, thus the pattern matches subsequences of annotations that yield true for each predicate.

Available predicates:
  
* *any*: yields true for any annotation;
* *featureKey*: yields true iff the annotation has the feature *featureKey*;
* *featureKey == "featureValue"*: yields true iff the annotation has the feature *featureKey* and its value is equal to *featureValue*;
* *"featureValue"*: yields true iff the feature *form* of the annotation is equal to *featureValue*, this is a short for *form == "featureValue"*;
* */regexp/*: yields true iff the feature *form* of the annotation matches the regular expression *regexp* ([Java syntax](http://download.oracle.com/javase/7/docs/api/java/util/regex/Pattern.html));
* *featureKey =~ "regexp"* or *featureKey =~ /regexp/*: yields true iff the annotation has the feature *featureKey* and its value matches the regular expression *regexp* ([Java syntax](http://download.oracle.com/javase/7/docs/api/java/util/regex/Pattern.html));
* *featureKey op integer*: yields true iff the annotation has the feature *featureKey*, and its value can be converted into an integer, and this value satisfies the comparison according to the specified operator and integer, *op* can be either *==*, *!=*, *<*, *>*, *<=* or *>=*;
* *pred1 or pred2*: yields true iff one of predicates *pred1* or *pred2* yield true for the annotation;
* *pred1 and pred2*: yields true iff predicates *pred1* and *pred2* both yield true for the annotation;
* *not pred*: yields true iff the predicate *pred* yields false.


  The operator precedence is the usual one (*or* > *and* > *not*). The precedence can be overriden using parentheses.
 

Additionally the following two pseudo-predicates are available:
 
* *start*: matches the start of the annotation sequence, meaning that the following predicate must match the first annotation in the sequence;
* *end*: matches the end of the annotation sequence, meaning that the preceding predicate must match the last annotation in the sequence.



Predicates can be quantified. The following quantifiers are available:
 
* *?*: matches the preceding predicate or group zero or once;
* ***: matches the preceding predicate or group zero to several times (unlimited);
* *+*: matches the preceding predicate or group at least once (unlimited);
* *{n}*: matches the preceding predicate or group exactly *n* times;
* *{n,}*: matches the preceding predicate or group at least *n* times (unlimited);
* *{n,m}*: matches the preceding predicate or group at least *n* times but no more than *m* times.


 These are *greedy* quantifiers. If there is a question mark (*?*) after the quantifier, then this quantifier is *reluctant*. See [Java Pattern documentation](http://download.oracle.com/javase/7/docs/api/java/util/regex/Pattern.html) for a complete description of greedy and reluctant quantifiers.

Predicate sequences may be grouped between square brackets. This allows the quantification of sub-patterns. Groups can be named by specifying the name after the opening bracket and 

Examples:
 
* *posTag =~ /^N/ +*: matches a single annotation or an unlimited number of consecutive annotations whose feature *posTag* starts with a *N*;
* *posTag =~ /^J/ * posTag =~ /^N/ +*: matches nothing or an unlimited number of consecutive annotations whose feature *posTag* starts with a *J*, then followed by a single annotation or an unlimited number of consecutive annotations whose feature *posTag* starts with a *N*;
* *posTag =~ /^J/ * posTag =~ /^N/ * [head: posTag =~ /^N/ ]*: like the preceding one, but captures the last annotation of the match in a sub-pattern named *head*, this name can be referenced in <a href="../module/PatternMatcher#actions" class="param">PatternMatcher#actions</a>.



## XML conversion

```xml
<param value="PATTERN"/>
```


	or
	```xml
<param pattern="PATTERN"/>
```


	or
	```xml
<param>PATTERN</param>
```

*PATTERN* will be converted as a string.
  

