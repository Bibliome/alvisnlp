# org.bibliome.alvisnlp.modules.segmig.WoSMig

## Synopsis

Performs word segmentation on section contents.

## Description

*org.bibliome.alvisnlp.modules.segmig.WoSMig* searches for word boundaries in the section contents, creates an annotation for each word and adds it to the layer [targetLayerName](#targetLayerName). The following are considered as word boundaries:
      
* consecutive whitespace characters, including ' ', newline, carriage return and horizontal tabulation;
* the positions before and after each punctuation character defined in [punctuation](#punctuation) and [balancedPunctuations](#balancedPunctuations), thus a punctuation character always form a single-character word, a balanced punctuation breaks a word iff the corresponding punctuation is found.



If [fixedFormLayerName](#fixedFormLayerName) is defined then non-overlapping annotations in this layer will be added as is in [targetLayerName](#targetLayerName), the start and end positions of these annotations are considered as word boundaries and no word boundary is searched inside.

The created annotations have the feature [annotationTypeFeature](#annotationTypeFeature) with a value corresponding to the word type:
      
* **punctuation**: if the word is a single-character punctuation;
* **word**: if the word is a plain non-punctuation word.


      The [eosStatusFeature](#eosStatusFeature) feature contains the end-of-sentence status of the word:
      
* **not-eos**: if the word cannot be an end of sentence;
* **maybe-eos**: if the word may be an end of sentence;
* **eos**: if the word is definitely an end of sentence.



## Parameters

<a name="constantAnnotationFeatures">

### constantAnnotationFeatures

Optional

Type: [Mapping](../converter/alvisnlp.module.types.Mapping)

Constant features to add to each annotation created by this module

<a name="fixedFormLayerName">

### fixedFormLayerName

Optional

Type: [String](../converter/java.lang.String)

Name of the layer containing annotations that should not be split into several words.

<a name="annotationComparator">

### annotationComparator

Default value: `length`

Type: [AnnotationComparator](../converter/alvisnlp.corpus.AnnotationComparator)

Comparator to use when removing overlapping fixed form annotations.

<a name="annotationTypeFeature">

### annotationTypeFeature

Default value: `wordType`

Type: [String](../converter/java.lang.String)

Name of the feature where to put the word type (word, punctuation, etc).

<a name="balancedPunctuations">

### balancedPunctuations

Default value: `()[]{}""`

Type: [String](../converter/java.lang.String)

Balanced punctuation characters. The opening punctuation must be immediately followed by the corresponding closing punctuation. If this parameter value has an odd length, then a warning will be issued and the last character will be ignored.

<a name="documentFilter">

### documentFilter

Default value: `true`

Type: [Expression](../converter/alvisnlp.corpus.expressions.Expression)

Only process document that satisfy this filter.

<a name="fixedType">

### fixedType

Default value: `fixed`

Type: [String](../converter/java.lang.String)

Value of the type feature for annotations copied from fixed forms.

<a name="punctuationType">

### punctuationType

Default value: `punctuation`

Type: [String](../converter/java.lang.String)

Value of the type feature for punctuation annotations.

<a name="punctuations">

### punctuations

Default value: `?.!;,:-`

Type: [String](../converter/java.lang.String)

List of punctuations, be them weak or strong.

<a name="sectionFilter">

### sectionFilter

Default value: `true`

Type: [Expression](../converter/alvisnlp.corpus.expressions.Expression)

Process only sections that satisfy this filter.

<a name="targetLayerName">

### targetLayerName

Default value: `words`

Type: [String](../converter/java.lang.String)

Layer where to store word annotations.

<a name="wordType">

### wordType

Default value: `word`

Type: [String](../converter/java.lang.String)

Value of the type feature for regular word annotations.

