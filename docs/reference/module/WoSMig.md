<h1 class="module">WoSMig</h1>

## Synopsis

Performs word segmentation on section contents.

## Description

 *WoSMig* searches for word boundaries in the section contents, creates an annotation for each word and adds it to the layer <a href="#targetLayer" class="param">targetLayer</a> . The following are considered as word boundaries:
* consecutive whitespace characters, including ' ', newline, carriage return and horizontal tabulation;
* the positions before and after each punctuation character defined in <a href="#punctuations" class="param">punctuations</a> and <a href="#balancedPunctuations" class="param">balancedPunctuations</a> , thus a punctuation character always form a single-character word, a balanced punctuation breaks a word iff the corresponding punctuation is found.



If <a href="#fixedFormLayer" class="param">fixedFormLayer</a> is defined then non-overlapping annotations in this layer will be added as is in <a href="#targetLayer" class="param">targetLayer</a> , the start and end positions of these annotations are considered as word boundaries and no word boundary is searched inside.

The created annotations have the feature <a href="#annotationTypeFeature" class="param">annotationTypeFeature</a> with a value corresponding to the word type:
*  **punctuation** : if the word is a single-character punctuation;
*  **word** : if the word is a plain non-punctuation word.



## Snippet



<button class="copy-code-button" title="Copy to clipboard" onclick="copy_code(this)">📋</button>
```xml
<wosmig class="WoSMig">
</wosmig>
```

## Mandatory parameters

## Optional parameters

<h3 id="constantAnnotationFeatures" class="param">constantAnnotationFeatures</h3>

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/fr.inra.maiage.bibliome.alvisnlp.core.module.types.Mapping" class="converter">Mapping</a>
</div>
Constant features to add to each annotation created by this module.

<h3 id="fixedFormLayer" class="param">fixedFormLayer</h3>

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Name of the layer containing annotations that should not be split into several words.

<h3 id="annotationComparator" class="param">annotationComparator</h3>

<div class="param-level param-level-default-value">Default value: `length`
</div>
<div class="param-type">Type: <a href="../converter/fr.inra.maiage.bibliome.alvisnlp.core.corpus.AnnotationComparator" class="converter">AnnotationComparator</a>
</div>
Comparator to use when removing overlapping fixed form annotations.

<h3 id="annotationTypeFeature" class="param">annotationTypeFeature</h3>

<div class="param-level param-level-default-value">Default value: `wordType`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Name of the feature where to put the word type (word, punctuation, etc).

<h3 id="balancedPunctuations" class="param">balancedPunctuations</h3>

<div class="param-level param-level-default-value">Default value: `()[]{}""`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Balanced punctuation characters. The opening punctuation must be immediately followed by the corresponding closing punctuation. If this parameter value has an odd length, then a warning will be issued and the last character will be ignored.

<h3 id="documentFilter" class="param">documentFilter</h3>

<div class="param-level param-level-default-value">Default value: `true`
</div>
<div class="param-type">Type: <a href="../converter/fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.Expression" class="converter">Expression</a>
</div>
Only process document that satisfy this expression.

<h3 id="fixedType" class="param">fixedType</h3>

<div class="param-level param-level-default-value">Default value: `fixed`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Value of the type feature for annotations copied from fixed forms.

<h3 id="punctuationType" class="param">punctuationType</h3>

<div class="param-level param-level-default-value">Default value: `punctuation`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Value of the type feature for punctuation annotations.

<h3 id="punctuations" class="param">punctuations</h3>

<div class="param-level param-level-default-value">Default value: `?.!;,:-`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
List of punctuations, be them weak or strong.

<h3 id="sectionFilter" class="param">sectionFilter</h3>

<div class="param-level param-level-default-value">Default value: `true`
</div>
<div class="param-type">Type: <a href="../converter/fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.Expression" class="converter">Expression</a>
</div>
Process only sections that satisfy this expression.

<h3 id="targetLayer" class="param">targetLayer</h3>

<div class="param-level param-level-default-value">Default value: `words`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Layer where to store word annotations.

<h3 id="wordType" class="param">wordType</h3>

<div class="param-level param-level-default-value">Default value: `word`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Value of the type feature for regular word annotations.

## Deprecated parameters

<h3 id="fixedFormLayerName" class="param">fixedFormLayerName</h3>

<div class="param-level param-level-deprecated">Deprecated
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Deprecated alias for <a href="#fixedFormLayer" class="param">fixedFormLayer</a> .

<h3 id="targetLayerName" class="param">targetLayerName</h3>

<div class="param-level param-level-deprecated">Deprecated
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Deprecated alias for <a href="#targetLayer" class="param">targetLayer</a> .

