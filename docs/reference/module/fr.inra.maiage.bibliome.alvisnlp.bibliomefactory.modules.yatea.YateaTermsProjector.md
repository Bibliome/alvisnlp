<h1 class="module">YateaTermsProjector</h1>

## Synopsis

Search in the sections content for terms extracted by YaTeA (see <a href="../module/YateaExtractor" class="module">YateaExtractor</a> ).

## Description

 *YateaTermsProjector* reads terms in a YaTeA XML output file produced by <a href="../module/YateaExtractor" class="module">YateaExtractor</a> and searches for terms in section contents, or whatever specified by <a href="#subject" class="param">subject</a> .

The parameters <a href="#allowJoined" class="param">allowJoined</a> , <a href="#allUpperCaseInsensitive" class="param">allUpperCaseInsensitive</a> , <a href="#caseInsensitive" class="param">caseInsensitive</a> , <a href="#ignoreDiacritics" class="param">ignoreDiacritics</a> , <a href="#joinDash" class="param">joinDash</a> , <a href="#matchStartCaseInsensitive" class="param">matchStartCaseInsensitive</a> , <a href="#skipConsecutiveWhitespaces" class="param">skipConsecutiveWhitespaces</a> , <a href="#skipWhitespace" class="param">skipWhitespace</a> and <a href="#wordStartCaseInsensitive" class="param">wordStartCaseInsensitive</a> control how the keys can match the sections content.

The <a href="#subject" class="param">subject</a> parameter specifies which text of the section should be matched. There are two alternatives:
* the entries are matched on the contents of the section (the default), <a href="#subject" class="param">subject</a> can also control if matches boundaries coincide with word delimiters;
* the entries are matched on the value of a specified feature of annotations in a given layer separated by a whitespace, in this way entries can be searched against word lemmas, for instance.



 *YateaTermsProjector* creates an annotation for each matched key and adds these annotations to the layer specified by <a href="#targetLayer" class="param">targetLayer</a> . Term structure information can be recorded in the features specified by <a href="#termIdFeature" class="param">termIdFeature</a> , <a href="#headFeature" class="param">headFeature</a> , <a href="#monoHeadIdFeature" class="param">monoHeadIdFeature</a> , <a href="#modifierFeature" class="param">modifierFeature</a> , and <a href="#termPosFeature" class="param">termPosFeature</a> . In addition, the created annotations will have the constant features specified in <a href="#constantAnnotationFeatures" class="param">constantAnnotationFeatures</a> .

 <a href="#trieSource" class="param">trieSource</a> and <a href="#trieSink" class="param">trieSink</a> are not supported by *YateaTermsProjector* .

## Snippet



<button class="copy-code-button" title="Copy to clipboard" onclick="copy_code(this)">📋</button>
```xml
<yateatermsprojector class="YateaTermsProjector>
    <targetLayer></targetLayer>
    <targetLayerName></targetLayerName>
    <yateaFile></yateaFile>
</yateatermsprojector>
```

## Mandatory parameters

<h3 id="targetLayer" class="param">targetLayer</h3>

<div class="param-level param-level-mandatory">Mandatory
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Name of the layer that contains the match annotations.

<h3 id="yateaFile" class="param">yateaFile</h3>

<div class="param-level param-level-mandatory">Mandatory
</div>
<div class="param-type">Type: <a href="../converter/fr.inra.maiage.bibliome.util.streams.SourceStream" class="converter">SourceStream</a>
</div>
YaTeA output XML file, as produced by <a href="../module/YateaExtractor" class="module">YateaExtractor</a> .

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
If set, then *YateaTermsProjector* writes the compiled dictionary to the specified file.

<h3 id="trieSource" class="param">trieSource</h3>

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/fr.inra.maiage.bibliome.util.files.InputFile" class="converter">InputFile</a>
</div>
If set, read the compiled dictionary from the specified file. Compiled dictionaries are usually faster for large dictionaries.

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

<h3 id="headFeature" class="param">headFeature</h3>

<div class="param-level param-level-default-value">Default value: `head`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Feature where to record the matched term's head identifier.

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

<h3 id="matchStartCaseInsensitive" class="param">matchStartCaseInsensitive</h3>

<div class="param-level param-level-default-value">Default value: `false`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.Boolean" class="converter">Boolean</a>
</div>
If set to *true* , then allow case folding on the first character of the entry key.

<h3 id="mnpOnly" class="param">mnpOnly</h3>

<div class="param-level param-level-default-value">Default value: `false`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.Boolean" class="converter">Boolean</a>
</div>
If *true* , then *YateaTermsProjector* only searches for MNP terms.

<h3 id="modifierFeature" class="param">modifierFeature</h3>

<div class="param-level param-level-default-value">Default value: `modifier`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Feature where to record the matched term's modifier identifier.

<h3 id="monoHeadIdFeature" class="param">monoHeadIdFeature</h3>

<div class="param-level param-level-default-value">Default value: `mono-head`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Feature where to record the matched term's mono-head (or superhead, or single-token head) identifier.

<h3 id="multipleEntryBehaviour" class="param">multipleEntryBehaviour</h3>

<div class="param-level param-level-default-value">Default value: `all`
</div>
<div class="param-type">Type: <a href="../converter/fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.trie.MultipleEntryBehaviour" class="converter">MultipleEntryBehaviour</a>
</div>
Specifies the behavior if the lexicon contains several entries with the same key.

<h3 id="projectLemmas" class="param">projectLemmas</h3>

<div class="param-level param-level-default-value">Default value: `false`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.Boolean" class="converter">Boolean</a>
</div>
If *true* , the this searches for term lemmas instead of surface forms.

<h3 id="sectionFilter" class="param">sectionFilter</h3>

<div class="param-level param-level-default-value">Default value: `true`
</div>
<div class="param-type">Type: <a href="../converter/fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.Expression" class="converter">Expression</a>
</div>
Process only sections that satisfy this expression.

<h3 id="skipConsecutiveWhitespaces" class="param">skipConsecutiveWhitespaces</h3>

<div class="param-level param-level-default-value">Default value: `false`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.Boolean" class="converter">Boolean</a>
</div>
If set to *true* , then allow the insertion of consecutive whitespace characters in the subject. For instance, the contents *amino acid* matches the entry *amino acid* .

<h3 id="skipWhitespace" class="param">skipWhitespace</h3>

<div class="param-level param-level-default-value">Default value: `false`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.Boolean" class="converter">Boolean</a>
</div>
If set to *true* , then allow arbitrary insertion of whitespace characters in the subject. For instance, the contents *amino acid* matches the key *aminoacid* .

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

<h3 id="termIdFeature" class="param">termIdFeature</h3>

<div class="param-level param-level-default-value">Default value: `term-id`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Feature where to record the matched term's identifier.

<h3 id="termLemmaFeature" class="param">termLemmaFeature</h3>

<div class="param-level param-level-default-value">Default value: `lemma`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Feature where to record the matched term's lemma string.

<h3 id="termPosFeature" class="param">termPosFeature</h3>

<div class="param-level param-level-default-value">Default value: `pos`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Feature where to record the matched term's components POS tags.

<h3 id="wordStartCaseInsensitive" class="param">wordStartCaseInsensitive</h3>

<div class="param-level param-level-default-value">Default value: `false`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.Boolean" class="converter">Boolean</a>
</div>
If set to *true* , then allow case folding on the first character of each word.

## Deprecated parameters

<h3 id="head" class="param">head</h3>

<div class="param-level param-level-deprecated">Deprecated
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Deprecated alias for <a href="#headFeature" class="param">headFeature</a> .

<h3 id="modifier" class="param">modifier</h3>

<div class="param-level param-level-deprecated">Deprecated
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Deprecated alias for <a href="#modifierFeature" class="param">modifierFeature</a> .

<h3 id="monoHeadId" class="param">monoHeadId</h3>

<div class="param-level param-level-deprecated">Deprecated
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Deprecated alias for <a href="#monoHeadIdFeature" class="param">monoHeadIdFeature</a> .

<h3 id="targetLayerName" class="param">targetLayerName</h3>

<div class="param-level param-level-deprecated">Deprecated
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Deprecated alias for <a href="#targetLayer" class="param">targetLayer</a> .

<h3 id="termId" class="param">termId</h3>

<div class="param-level param-level-deprecated">Deprecated
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Deprecated alias for <a href="#termIdFeature" class="param">termIdFeature</a> .

<h3 id="termLemma" class="param">termLemma</h3>

<div class="param-level param-level-deprecated">Deprecated
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Deprecated alias for <a href="#termLemmaFeature" class="param">termLemmaFeature</a> .

<h3 id="termPOS" class="param">termPOS</h3>

<div class="param-level param-level-deprecated">Deprecated
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Deprecated alias for <a href="#termPosFeature" class="param">termPosFeature</a> .

