<h1 class="module">TabularProjector</h1>

## Synopsis

Search in the sections content for entries specified in a tabular text file.

## Description

 *TabularProjector* reads a list of entries from <a href="#dictFile" class="param">dictFile</a> and searches for each entry key in sections contents. The format of the dictionary is one entry per line. Each line is split into columns separated by tab characters. The column specified by <a href="#keyIndex" class="param">keyIndex</a> will be the entry key to be searched and the other columns are data associated to the entry.

The parameters <a href="#skipBlank" class="param">skipBlank</a> , <a href="#skipEmpty" class="param">skipEmpty</a> , <a href="#strictColumnNumber" class="param">strictColumnNumber</a> , <a href="#trimColumns" class="param">trimColumns</a> , <a href="#separator" class="param">separator</a> , <a href="#multipleEntryBehaviour" class="param">multipleEntryBehaviour</a> control how the dictionary file is read by *TabularProjector* .

The parameters <a href="#allowJoined" class="param">allowJoined</a> , <a href="#allUpperCaseInsensitive" class="param">allUpperCaseInsensitive</a> , <a href="#caseInsensitive" class="param">caseInsensitive</a> , <a href="#ignoreDiacritics" class="param">ignoreDiacritics</a> , <a href="#joinDash" class="param">joinDash</a> , <a href="#matchStartCaseInsensitive" class="param">matchStartCaseInsensitive</a> , <a href="#skipConsecutiveWhitespaces" class="param">skipConsecutiveWhitespaces</a> , <a href="#skipWhitespace" class="param">skipWhitespace</a> and <a href="#wordStartCaseInsensitive" class="param">wordStartCaseInsensitive</a> control how the keys can match the sections content.

The <a href="#subject" class="param">subject</a> parameter specifies which text of the section should be matched. There are two alternatives:
* the entries are matched on the contents of the section (the default), <a href="#subject" class="param">subject</a> can also control if matches boundaries coincide with word delimiters;
* the entries are matched on the value of a specified feature of annotations in a given layer separated by a whitespace, in this way entries can be searched against word lemmas, for instance.



 *TabularProjector* creates an annotation for each matched key and adds these annotations to the layer specified by <a href="#targetLayer" class="param">targetLayer</a> . The created annotations will have features that correspond to the entry columns. Feature keys are specified by <a href="#valueFeatures" class="param">valueFeatures</a> . For instance if <a href="#valueFeatures" class="param">valueFeatures</a> is *[a,b,c]* , then each annotation will have three features named *a* , *b* and *c* with the respective values of the entry's first, second and third columns. A feature name left blank in <a href="#valueFeatures" class="param">valueFeatures</a> will not create a feature. Thus, in order to drop the first column of the entry, <a href="#valueFeatures" class="param">valueFeatures</a> should be *[,b,c]* . In addition, the created annotations will have the constant features specified in <a href="#constantAnnotationFeatures" class="param">constantAnnotationFeatures</a> .

If <a href="#trieSource" class="param">trieSource</a> is specified, then *TabularProjector* assumes that the file contains a compiled version of the dictionary. In this case <a href="#dictFile" class="param">dictFile</a> is not read.

If <a href="#trieSink" class="param">trieSink</a> is specified, *TabularProjector* writes a compiled version of the dictionary in the file. The use of compiled dictionaries may accelerate the processing for large dictionaries.

## Snippet



<button class="copy-code-button" title="Copy to clipboard" onclick="copy_code(this)">📋</button>
```xml
<tabularprojector class="TabularProjector">
    <dictFile></dictFile>
    <targetLayer></targetLayer>
</tabularprojector>
```

## Mandatory parameters

<h3 id="dictFile" class="param">dictFile</h3>

<div class="param-level param-level-mandatory">Mandatory
</div>
<div class="param-type">Type: <a href="../converter/fr.inra.maiage.bibliome.util.streams.SourceStream" class="converter">SourceStream</a>
</div>
The dictionary.

<h3 id="targetLayer" class="param">targetLayer</h3>

<div class="param-level param-level-mandatory">Mandatory
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Name of the layer that contains the match annotations.

## Optional parameters

<h3 id="constantAnnotationFeatures" class="param">constantAnnotationFeatures</h3>

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/fr.inra.maiage.bibliome.alvisnlp.core.module.types.Mapping" class="converter">Mapping</a>
</div>
Constant features to add to each annotation created by this module.

<h3 id="trieSink" class="param">trieSink</h3>

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/fr.inra.maiage.bibliome.util.files.OutputFile" class="converter">OutputFile</a>
</div>
If set, then *TabularProjector* writes the compiled dictionary to the specified file.

<h3 id="trieSource" class="param">trieSource</h3>

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/fr.inra.maiage.bibliome.util.files.InputFile" class="converter">InputFile</a>
</div>
If set, read the compiled dictionary from the specified file. Compiled dictionaries are usually faster for large dictionaries.

<h3 id="valueFeatures" class="param">valueFeatures</h3>

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String%5B%5D" class="converter">String[]</a>
</div>
Target features in match annotations. The values are the columns in the entry. Ignored if <a href="#headerLine" class="param">headerLine</a> is set (unless <a href="#trieSource" class="param">trieSource</a> is set).

<h3 id="allUpperCaseInsensitive" class="param">allUpperCaseInsensitive</h3>

<div class="param-level param-level-default-value">Default value: `false`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.Boolean" class="converter">Boolean</a>
</div>
If set to *true* , then allow case folding on all characters in words that are all upper case.

<h3 id="allowJoined" class="param">allowJoined</h3>

<div class="param-level param-level-default-value">Default value: `false`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.Boolean" class="converter">Boolean</a>
</div>
If set to *true* , then allow arbitrary suppression of whitespace characters in the subject. For instance, the contents *aminoacid* matches the key *amino acid* .

<h3 id="caseInsensitive" class="param">caseInsensitive</h3>

<div class="param-level param-level-default-value">Default value: `false`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.Boolean" class="converter">Boolean</a>
</div>
If set to *true* , then allows case folding on all characters.

<h3 id="documentFilter" class="param">documentFilter</h3>

<div class="param-level param-level-default-value">Default value: `true`
</div>
<div class="param-type">Type: <a href="../converter/fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.Expression" class="converter">Expression</a>
</div>
Only process document that satisfy this expression.

<h3 id="headerLine" class="param">headerLine</h3>

<div class="param-level param-level-default-value">Default value: `false`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.Boolean" class="converter">Boolean</a>
</div>
Assume the first line of the dictionary is a header, the feature values will be taken from the header line. Ignored if <a href="#trieSource" class="param">trieSource</a> is set.

<h3 id="ignoreDiacritics" class="param">ignoreDiacritics</h3>

<div class="param-level param-level-default-value">Default value: `false`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.Boolean" class="converter">Boolean</a>
</div>
If set to *true* , then allow dicacritic removal on all characters. For instance the contents *acide amine* matches the key *acide aminé* .

<h3 id="joinDash" class="param">joinDash</h3>

<div class="param-level param-level-default-value">Default value: `false`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.Boolean" class="converter">Boolean</a>
</div>
If set to *true* , then treat dash characters (-) as whitespace characters with regard to <a href="#allowJoined" class="param">allowJoined</a> . For instance, the contents *aminoacid* matches the entry *amino-acid* .

<h3 id="keyIndex" class="param">keyIndex</h3>

<div class="param-level param-level-default-value">Default value: `0`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.Integer%5B%5D" class="converter">Integer[]</a>
</div>
Specifies the index of the column that contains the entry key ( *0* is the first).

<h3 id="matchStartCaseInsensitive" class="param">matchStartCaseInsensitive</h3>

<div class="param-level param-level-default-value">Default value: `false`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.Boolean" class="converter">Boolean</a>
</div>
If set to *true* , then allow case folding on the first character of the entry key.

<h3 id="multipleEntryBehaviour" class="param">multipleEntryBehaviour</h3>

<div class="param-level param-level-default-value">Default value: `all`
</div>
<div class="param-type">Type: <a href="../converter/fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.trie.MultipleEntryBehaviour" class="converter">MultipleEntryBehaviour</a>
</div>
Specifies the behavior if the lexicon contains several entries with the same key.

<h3 id="sectionFilter" class="param">sectionFilter</h3>

<div class="param-level param-level-default-value">Default value: `true`
</div>
<div class="param-type">Type: <a href="../converter/fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.Expression" class="converter">Expression</a>
</div>
Process only sections that satisfy this expression.

<h3 id="separator" class="param">separator</h3>

<div class="param-level param-level-default-value">Default value: `	`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.Character" class="converter">Character</a>
</div>
Specifies the character that separates columns in <a href="#dictFile" class="param">dictFile</a> .

<h3 id="skipBlank" class="param">skipBlank</h3>

<div class="param-level param-level-default-value">Default value: `false`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.Boolean" class="converter">Boolean</a>
</div>
In <a href="#dictFile" class="param">dictFile</a> , skip lines that contain only whitespace characters.

<h3 id="skipConsecutiveWhitespaces" class="param">skipConsecutiveWhitespaces</h3>

<div class="param-level param-level-default-value">Default value: `false`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.Boolean" class="converter">Boolean</a>
</div>
If set to *true* , then allow the insertion of consecutive whitespace characters in the subject. For instance, the contents *amino acid* matches the entry *amino acid* .

<h3 id="skipEmpty" class="param">skipEmpty</h3>

<div class="param-level param-level-default-value">Default value: `false`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.Boolean" class="converter">Boolean</a>
</div>
In <a href="#dictFile" class="param">dictFile</a> , skip empty lines.

<h3 id="skipWhitespace" class="param">skipWhitespace</h3>

<div class="param-level param-level-default-value">Default value: `false`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.Boolean" class="converter">Boolean</a>
</div>
If set to *true* , then allow arbitrary insertion of whitespace characters in the subject. For instance, the contents *amino acid* matches the key *aminoacid* .

<h3 id="strictColumnNumber" class="param">strictColumnNumber</h3>

<div class="param-level param-level-default-value">Default value: `true`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.Boolean" class="converter">Boolean</a>
</div>
If set to true, then check that every line in <a href="#dictFile" class="param">dictFile</a> has the same number of columns as the number of features specified in <a href="#valueFeatures" class="param">valueFeatures</a> .

<h3 id="subject" class="param">subject</h3>

<div class="param-level param-level-default-value">Default value: `WORD`
</div>
<div class="param-type">Type: <a href="../converter/fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.trie.Subject" class="converter">Subject</a>
</div>
Specifies the contents to match.

<h3 id="substituteWhitespace" class="param">substituteWhitespace</h3>

<div class="param-level param-level-default-value">Default value: `false`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.Boolean" class="converter">Boolean</a>
</div>
If set to *true* , then all whitespace characters match each other (including '\n', '\r', '\t', and non-breaking spaces).

<h3 id="trimColumns" class="param">trimColumns</h3>

<div class="param-level param-level-default-value">Default value: `false`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.Boolean" class="converter">Boolean</a>
</div>
If set to *true* , then trim leading and trailing whitespace character from column values in <a href="#dictFile" class="param">dictFile</a> .

<h3 id="wordStartCaseInsensitive" class="param">wordStartCaseInsensitive</h3>

<div class="param-level param-level-default-value">Default value: `false`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.Boolean" class="converter">Boolean</a>
</div>
If set to *true* , then allow case folding on the first character of each word.

## Deprecated parameters

<h3 id="targetLayerName" class="param">targetLayerName</h3>

<div class="param-level param-level-deprecated">Deprecated
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Deprecated alias for <a href="#targetLayer" class="param">targetLayer</a> .

