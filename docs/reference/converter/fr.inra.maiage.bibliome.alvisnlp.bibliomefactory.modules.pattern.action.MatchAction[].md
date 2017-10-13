<h1 class="converter">MatchAction[]</h1>

## Synopsis

Converts into an array of acions for <a href="../module/PatternMatcher" class="module">PatternMatcher</a>.

## String conversion

String conversion is not available for this type.

## XML conversion

The parameter element accepts any number of children elements. Each child element is converted into a single action. Available aactions are:
  
* ```xml
<addToLayer group="GROUP_NAME" layer="LAYER_NAME"/>
```

:
	  add all annotations in *GROUP* sub-pattern to the layer specified by *LAYER_NAME*. If the *group* attribute is not specified, then add all annotations in the match.
	
* ```xml
<createAnnotation features="FEATURES" group="GROUP_NAME" layer="LAYER_NAME"/>
```

:
	  create an annotation that spans over the *GROUP* sub-pattern. If the *group* attribute is not specified, then the created annotation spans over the whole match. The created annotation is added to the layer *LAYER_NAME* and the features specified in *FEATURES* are added. The features follow the syntax described in the string conversion of <a href="../converter/Mapping" class="converter">Mapping</a>.
	
* ```xml
<removeAnnotations group="GROUP_NAME" layer="LAYER_NAME"/>
```

:
	  remove all annotations in *GROUP* sub-pattern from the layer named *LAYER_NAME*. If the *group* attribute is not specified, then all annotations of the match are removed. If *LAYER_NAME* is the same as the matched layer, then annotation removal is effective after all matches have been processed in the layer.
	
* ```xml
<setFeatures features="FEATURES" group="GROUP_NAME" layer="LAYER_NAME"/>
```

:
	  add features to all annotations in *GROUP* sub-pattern. If the *group* attribute is not specified, then features are added to all annotations of the match. The features follow the syntax described in the string conversion of <a href="../converter/Mapping" class="converter">Mapping</a>.
	
* ```xml
<createTuple arguments="ARGS" features="FEATURES" relation="RELATION_NAME"/>
```

:
	  create a tuple in relation specified by *RELATION_NAME* for each match. Features specified by *FEATURES* are added to the created tuple. The features follow the syntax described in the string conversion of <a href="../converter/Mapping" class="converter">Mapping</a>. *ARGS* is a comma separated list of arguments. Each argument must be in the form: *ROLE=GROUP*, where *ROLE* is a role name and *GROUP* is the name of a sub-pattern group. The argument is added with the specified role iff the specified group can be associated with a single annotation: the group matches a sequence of exactly one annotation or the group is the target of a *createAnnotation* action.
	



