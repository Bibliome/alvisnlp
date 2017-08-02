<h1 class="module">TabularProjector</h1>

## Synopsis

Projects a simple dictionary on sections.

## Description

*TabularProjector* reads a list of entries from <a href="#dictFile" class="param">dictFile</a> and searches for these entries in sections. The format of the dictionary is one entry per line. Each line is split into columns separated by tab characters, or whichever character defined by <a href="#separator" class="param">separator</a>. The column specified by <a href="#keyIndex" class="param">keyIndex</a> will be the entry to be searched and the other columns are data associated to the entry.

The parameters <a href="#skipBlank" class="param">skipBlank</a>, <a href="#skipEmpty" class="param">skipEmpty</a>, <a href="#strictColumnNumber" class="param">strictColumnNumber</a>, <a href="#trimColumns" class="param">trimColumns</a>, <a href="#separator" class="param">separator</a>, <a href="#multipleEntryBehaviour" class="param">multipleEntryBehaviour</a> control the loading of the dictionary file.

The parameters <a href="#allowJoined" class="param">allowJoined</a>, <a href="#allUpperCaseInsensitive" class="param">allUpperCaseInsensitive</a>, <a href="#caseInsensitive" class="param">caseInsensitive</a>, <a href="#ignoreDiacritics" class="param">ignoreDiacritics</a>, <a href="#joinDash" class="param">joinDash</a>, <a href="#matchStartCaseInsensitive" class="param">matchStartCaseInsensitive</a>, <a href="#skipConsecutiveWhitespaces" class="param">skipConsecutiveWhitespaces</a>, <a href="#skipWhitespace" class="param">skipWhitespace</a> and <a href="#wordStartCaseInsensitive" class="param">wordStartCaseInsensitive</a> control the matching between the section and the entry keys.

The <a href="#subject" class="param">subject</a> parameter specifies which text of the section should be matched. There are two options:
  
* the entries are matched on the contents of the section, <a href="#subject" class="param">subject</a> can also control if matches boundaries coincide with word delimiters;
* the entries are matched on the feature value of annotations of a given layer separated by a whitespace, in this way entries can be searched against word lemmas for instance.



*TabularProjector* creates an annotation for each matched entry and adds these annotations to the layer named <a href="#targetLayerName" class="param">targetLayerName</a>. The created annotations will have features whose keys correspond to <a href="#entryFeatureNames" class="param">entryFeatureNames</a> and values to the data associated to the matched entry (columns in the dictionary file). For instance if <a href="#entryFeatureNames" class="param">entryFeatureNames</a> is *[a,b,c]*, then each annotation will have three features named *a*, *b* and *c* with the respective values of the entry's second, third and fourth columns. A feature name left blank in <a href="#entryFeatureNames" class="param">entryFeatureNames</a> will not create a feature. Thus, in order not to keep the entry in the *a* feature, <a href="#entryFeatureNames" class="param">entryFeatureNames</a> should be *[,b,c]*. In addition, the created annotations will have the feature keys and values defined in <a href="#constantAnnotationFeatures" class="param">constantAnnotationFeatures</a>.

If specified, then *TabularProjector* assumes that <a href="#trieSource" class="param">trieSource</a> contains a compiled version of the dictionary. <a href="#dictFile" class="param">dictFile</a> is not read. If specified, *TabularProjector* writes a compiled version of the dictionary in <a href="#trieSink" class="param">trieSink</a>. The use of compiled dictionaries may accelerate the processing for large dictionaries.

## Parameters

<a name="dictFile">

### dictFile

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/org.bibliome.util.streams.SourceStream" class="converter">SourceStream</a>
</div>
Source of the dictionary.

<a name="targetLayerName">

### targetLayerName

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Name of the layer that contains the match annotations.

<a name="valueFeatures">

### valueFeatures

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String[]" class="converter">String[]</a>
</div>
Target features in match annotations. The values are the columns in the matched entry line.

<a name="constantAnnotationFeatures">

### constantAnnotationFeatures

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/alvisnlp.module.types.Mapping" class="converter">Mapping</a>
</div>
Constant features to add to each annotation created by this module

<a name="trieSink">

### trieSink

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/org.bibliome.util.files.OutputFile" class="converter">OutputFile</a>
</div>
If set, *TabularProjector* writes the compiled dictionary to the specified file.

<a name="trieSource">

### trieSource

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/org.bibliome.util.files.InputFile" class="converter">InputFile</a>
</div>
If set, read the compiled dictionary from the specified files. Compiled dictionaries are generally faster for large dictionaries.

<a name="allUpperCaseInsensitive">

### allUpperCaseInsensitive

<div class="param-level param-level-default-value">Default value: `false`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.Boolean" class="converter">Boolean</a>
</div>
Either the match allows case substitution on all characters in words that are all upper case.

<a name="allowJoined">

### allowJoined

<div class="param-level param-level-default-value">Default value: `false`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.Boolean" class="converter">Boolean</a>
</div>
Either the match allows arbitrary suppression of whitespace characters in the subject. For instance, the contents *aminoacid* matches the entry *amino acid*.

<a name="caseInsensitive">

### caseInsensitive

<div class="param-level param-level-default-value">Default value: `false`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.Boolean" class="converter">Boolean</a>
</div>
Either the match allows case substitutions on all characters.

<a name="documentFilter">

### documentFilter

<div class="param-level param-level-default-value">Default value: `true`
</div>
<div class="param-type">Type: <a href="../converter/alvisnlp.corpus.expressions.Expression" class="converter">Expression</a>
</div>
Only process document that satisfy this filter.

<a name="ignoreDiacritics">

### ignoreDiacritics

<div class="param-level param-level-default-value">Default value: `false`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.Boolean" class="converter">Boolean</a>
</div>
Either the match allows dicacritics substitutions on all characters. For instance the contents *acide amine* matches the entry *acide aminé*.

<a name="joinDash">

### joinDash

<div class="param-level param-level-default-value">Default value: `false`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.Boolean" class="converter">Boolean</a>
</div>
Either to treat dash characters (-) as whitespace characters if <a href="#allowJoined" class="param">allowJoined</a> is `true`. For instance, the contents *aminoacid* matches the entry *amino-acid*.

<a name="keyIndex">

### keyIndex

<div class="param-level param-level-default-value">Default value: `0`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.Integer[]" class="converter">Integer[]</a>
</div>
Specifies the key column index (starting at 0).

<a name="matchStartCaseInsensitive">

### matchStartCaseInsensitive

<div class="param-level param-level-default-value">Default value: `false`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.Boolean" class="converter">Boolean</a>
</div>
Either the match allows case substitution on the first character of the entry key.

<a name="multipleEntryBehaviour">

### multipleEntryBehaviour

<div class="param-level param-level-default-value">Default value: `all`
</div>
<div class="param-type">Type: <a href="../converter/org.bibliome.alvisnlp.modules.trie.MultipleEntryBehaviour" class="converter">MultipleEntryBehaviour</a>
</div>
Specifies the behavious of *TabularProjector* if <a href="#dictFile" class="param">dictFile</a> contains several entries with the same key.

<a name="sectionFilter">

### sectionFilter

<div class="param-level param-level-default-value">Default value: `true`
</div>
<div class="param-type">Type: <a href="../converter/alvisnlp.corpus.expressions.Expression" class="converter">Expression</a>
</div>
Process only sections that satisfy this filter.

<a name="separator">

### separator

<div class="param-level param-level-default-value">Default value: `	`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.Character" class="converter">Character</a>
</div>
Specifies the character that separates columns in <a href="#dictFile" class="param">dictFile</a>.

<a name="skipBlank">

### skipBlank

<div class="param-level param-level-default-value">Default value: `false`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.Boolean" class="converter">Boolean</a>
</div>
In <a href="#dictFile" class="param">dictFile</a>, skip lines that contain only whitespace characters.

<a name="skipConsecutiveWhitespaces">

### skipConsecutiveWhitespaces

<div class="param-level param-level-default-value">Default value: `false`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.Boolean" class="converter">Boolean</a>
</div>
Either the match allows insertion of consecutive whitespace characters in the subject. For instance, the contents *amino  acid* matches the entry *amino acid*.

<a name="skipEmpty">

### skipEmpty

<div class="param-level param-level-default-value">Default value: `false`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.Boolean" class="converter">Boolean</a>
</div>
In <a href="#dictFile" class="param">dictFile</a>, skip empty lines.

<a name="skipWhitespace">

### skipWhitespace

<div class="param-level param-level-default-value">Default value: `false`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.Boolean" class="converter">Boolean</a>
</div>
Either the match allows arbitrary insertion of whitespace characters in the subject. For instance, the contents *amino acid* matches the entry *aminoacid*.

<a name="strictColumnNumber">

### strictColumnNumber

<div class="param-level param-level-default-value">Default value: `true`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.Boolean" class="converter">Boolean</a>
</div>
Either to check that every line in <a href="#dictFile" class="param">dictFile</a> has the same number of columns as the number of features specified in <a href="#entryFeatureNames" class="param">entryFeatureNames</a>.

<a name="subject">

### subject

<div class="param-level param-level-default-value">Default value: `WORD`
</div>
<div class="param-type">Type: <a href="../converter/org.bibliome.alvisnlp.modules.trie.Subject" class="converter">Subject</a>
</div>
Specifies the contents to match.

<a name="trimColumns">

### trimColumns

<div class="param-level param-level-default-value">Default value: `false`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.Boolean" class="converter">Boolean</a>
</div>
Either to trim column values in <a href="#dictFile" class="param">dictFile</a> from leading and trailing whitespace characters.

<a name="wordStartCaseInsensitive">

### wordStartCaseInsensitive

<div class="param-level param-level-default-value">Default value: `false`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.Boolean" class="converter">Boolean</a>
</div>
Either the match allows case substitution on the first character of words.

