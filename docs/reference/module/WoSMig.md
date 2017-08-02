<h1 class="module">WoSMig</h1>

## Synopsis

Performs word segmentation on section contents.

## Description

*WoSMig* searches for word boundaries in the section contents, creates an annotation for each word and adds it to the layer <a href="#targetLayerName" class="param">targetLayerName</a>. The following are considered as word boundaries:
  
* consecutive whitespace characters, including ' ', newline, carriage return and horizontal tabulation;
* the positions before and after each punctuation character defined in <a href="#punctuation" class="param">punctuation</a> and <a href="#balancedPunctuations" class="param">balancedPunctuations</a>, thus a punctuation character always form a single-character word, a balanced punctuation breaks a word iff the corresponding punctuation is found.



If <a href="#fixedFormLayerName" class="param">fixedFormLayerName</a> is defined then non-overlapping annotations in this layer will be added as is in <a href="#targetLayerName" class="param">targetLayerName</a>, the start and end positions of these annotations are considered as word boundaries and no word boundary is searched inside.

The created annotations have the feature <a href="#annotationTypeFeature" class="param">annotationTypeFeature</a> with a value corresponding to the word type:
  
* **punctuation**: if the word is a single-character punctuation;
* **word**: if the word is a plain non-punctuation word.


  The <a href="#eosStatusFeature" class="param">eosStatusFeature</a> feature contains the end-of-sentence status of the word:
  
* **not-eos**: if the word cannot be an end of sentence;
* **maybe-eos**: if the word may be an end of sentence;
* **eos**: if the word is definitely an end of sentence.



## Parameters

<a name="constantAnnotationFeatures">

### constantAnnotationFeatures

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/alvisnlp.module.types.Mapping" class="converter">Mapping</a>
</div>
Constant features to add to each annotation created by this module

<a name="fixedFormLayerName">

### fixedFormLayerName

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Name of the layer containing annotations that should not be split into several words.

<a name="annotationComparator">

### annotationComparator

<div class="param-level param-level-default-value">Default value: `length`
</div>
<div class="param-type">Type: <a href="../converter/alvisnlp.corpus.AnnotationComparator" class="converter">AnnotationComparator</a>
</div>
Comparator to use when removing overlapping fixed form annotations.

<a name="annotationTypeFeature">

### annotationTypeFeature

<div class="param-level param-level-default-value">Default value: `wordType`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Name of the feature where to put the word type (word, punctuation, etc).

<a name="balancedPunctuations">

### balancedPunctuations

<div class="param-level param-level-default-value">Default value: `()[]{}""`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Balanced punctuation characters. The opening punctuation must be immediately followed by the corresponding closing punctuation. If this parameter value has an odd length, then a warning will be issued and the last character will be ignored.

<a name="documentFilter">

### documentFilter

<div class="param-level param-level-default-value">Default value: `true`
</div>
<div class="param-type">Type: <a href="../converter/alvisnlp.corpus.expressions.Expression" class="converter">Expression</a>
</div>
Only process document that satisfy this filter.

<a name="fixedType">

### fixedType

<div class="param-level param-level-default-value">Default value: `fixed`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Value of the type feature for annotations copied from fixed forms.

<a name="punctuationType">

### punctuationType

<div class="param-level param-level-default-value">Default value: `punctuation`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Value of the type feature for punctuation annotations.

<a name="punctuations">

### punctuations

<div class="param-level param-level-default-value">Default value: `?.!;,:-`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
List of punctuations, be them weak or strong.

<a name="sectionFilter">

### sectionFilter

<div class="param-level param-level-default-value">Default value: `true`
</div>
<div class="param-type">Type: <a href="../converter/alvisnlp.corpus.expressions.Expression" class="converter">Expression</a>
</div>
Process only sections that satisfy this filter.

<a name="targetLayerName">

### targetLayerName

<div class="param-level param-level-default-value">Default value: `words`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Layer where to store word annotations.

<a name="wordType">

### wordType

<div class="param-level param-level-default-value">Default value: `word`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Value of the type feature for regular word annotations.

