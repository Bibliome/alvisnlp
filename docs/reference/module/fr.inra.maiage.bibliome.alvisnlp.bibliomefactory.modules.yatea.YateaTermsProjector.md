<h1 class="module">YateaTermsProjector</h1>

## Synopsis

Search in the sections content for terms extracted by YaTeA (see <a href="../module/YateaExtractor" class="module">YateaExtractor</a>).

## Description

*YateaTermsProjector* reads terms in a YaTeA XML output file produced by <a href="../module/YateaExtractor" class="module">YateaExtractor</a> and searches for terms in section contents, or whatever specified by <a href="#subject" class="param">subject</a>.

The parameters <a href="#skipBlank" class="param">skipBlank</a>, <a href="#skipEmpty" class="param">skipEmpty</a>, <a href="#strictColumnNumber" class="param">strictColumnNumber</a>, <a href="#trimColumns" class="param">trimColumns</a>, <a href="#separator" class="param">separator</a>, <a href="#multipleEntryBehaviour" class="param">multipleEntryBehaviour</a> control how the dictionary file is read by *YateaTermsProjector*.

The parameters <a href="#allowJoined" class="param">allowJoined</a>, <a href="#allUpperCaseInsensitive" class="param">allUpperCaseInsensitive</a>, <a href="#caseInsensitive" class="param">caseInsensitive</a>, <a href="#ignoreDiacritics" class="param">ignoreDiacritics</a>, <a href="#joinDash" class="param">joinDash</a>, <a href="#matchStartCaseInsensitive" class="param">matchStartCaseInsensitive</a>, <a href="#skipConsecutiveWhitespaces" class="param">skipConsecutiveWhitespaces</a>, <a href="#skipWhitespace" class="param">skipWhitespace</a> and <a href="#wordStartCaseInsensitive" class="param">wordStartCaseInsensitive</a> control how the keys can match the sections content.

The <a href="#subject" class="param">subject</a> parameter specifies which text of the section should be matched. There are two alternatives:
  
* the entries are matched on the contents of the section (the default), <a href="#subject" class="param">subject</a> can also control if matches boundaries coincide with word delimiters;
* the entries are matched on the value of a specified feature of annotations in a given layer separated by a whitespace, in this way entries can be searched against word lemmas, for instance.



*YateaTermsProjector* creates an annotation for each matched key and adds these annotations to the layer specified by <a href="#targetLayerName" class="param">targetLayerName</a>. Term structure information can be recorded in the features specified by <a href="#term-id" class="param">term-id</a>, <a href="#head" class="param">head</a>, <a href="#monoHeadId" class="param">monoHeadId</a>, <a href="#modifier" class="param">modifier</a>, and <a href="#pos" class="param">pos</a>. In addition, the created annotations will have the constant features specified in <a href="#constantAnnotationFeatures" class="param">constantAnnotationFeatures</a>.

<a href="#trieSource" class="param">trieSource</a> and <a href="#trieSink" class="param">trieSink</a> are not supported by *YateaTermsProjector*.

## Parameters

<h3 name="targetLayerName" class="param">targetLayerName</h3>

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Name of the layer that contains the match annotations.

<h3 name="yateaFile" class="param">yateaFile</h3>

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/fr.inra.maiage.bibliome.util.streams.SourceStream" class="converter">SourceStream</a>
</div>
YaTeA output XML file, as produced by <a href="../module/YateaExtractor" class="module">YateaExtractor</a>.

<h3 name="constantAnnotationFeatures" class="param">constantAnnotationFeatures</h3>

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/fr.inra.maiage.bibliome.alvisnlp.core.module.types.Mapping" class="converter">Mapping</a>
</div>
Constant features to add to each annotation created by this module.

<h3 name="trieSink" class="param">trieSink</h3>

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/fr.inra.maiage.bibliome.util.files.OutputFile" class="converter">OutputFile</a>
</div>
If set, then *YateaTermsProjector* writes the compiled dictionary to the specified file.

<h3 name="trieSource" class="param">trieSource</h3>

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/fr.inra.maiage.bibliome.util.files.InputFile" class="converter">InputFile</a>
</div>
If set, read the compiled dictionary from the specified file. Compiled dictionaries are usually faster for large dictionaries.

<h3 name="allUpperCaseInsensitive" class="param">allUpperCaseInsensitive</h3>

<div class="param-level param-level-default-value">Default value: `false`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.Boolean" class="converter">Boolean</a>
</div>
If set to *true*, then allow case folding on all characters in words that are all upper case.

<h3 name="allowJoined" class="param">allowJoined</h3>

<div class="param-level param-level-default-value">Default value: `false`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.Boolean" class="converter">Boolean</a>
</div>
If set to *true*, then allow arbitrary suppression of whitespace characters in the subject. For instance, the contents *aminoacid* matches the key *amino acid*.

<h3 name="caseInsensitive" class="param">caseInsensitive</h3>

<div class="param-level param-level-default-value">Default value: `false`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.Boolean" class="converter">Boolean</a>
</div>
If set to *true*, then allows case folding on all characters.

<h3 name="documentFilter" class="param">documentFilter</h3>

<div class="param-level param-level-default-value">Default value: `true`
</div>
<div class="param-type">Type: <a href="../converter/fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.Expression" class="converter">Expression</a>
</div>
Only process document that satisfy this expression.

<h3 name="head" class="param">head</h3>

<div class="param-level param-level-default-value">Default value: `head`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Feature where to record the matched term's head identifier.

<h3 name="ignoreDiacritics" class="param">ignoreDiacritics</h3>

<div class="param-level param-level-default-value">Default value: `false`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.Boolean" class="converter">Boolean</a>
</div>
If set to *true*, then allow dicacritic removal on all characters. For instance the contents *acide amine* matches the key *acide aminé*.

<h3 name="joinDash" class="param">joinDash</h3>

<div class="param-level param-level-default-value">Default value: `false`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.Boolean" class="converter">Boolean</a>
</div>
If set to *true*, then treat dash characters (-) as whitespace characters with regard to <a href="#allowJoined" class="param">allowJoined</a>. For instance, the contents *aminoacid* matches the entry *amino-acid*.

<h3 name="matchStartCaseInsensitive" class="param">matchStartCaseInsensitive</h3>

<div class="param-level param-level-default-value">Default value: `false`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.Boolean" class="converter">Boolean</a>
</div>
If set to *true*, then allow case folding on the first character of the entry key.

<h3 name="mnpOnly" class="param">mnpOnly</h3>

<div class="param-level param-level-default-value">Default value: `false`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.Boolean" class="converter">Boolean</a>
</div>
If *true*, then *YateaTermsProjector* only searches for MNP terms.

<h3 name="modifier" class="param">modifier</h3>

<div class="param-level param-level-default-value">Default value: `modifier`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Feature where to record the matched term's modifier identifier.

<h3 name="monoHeadId" class="param">monoHeadId</h3>

<div class="param-level param-level-default-value">Default value: `mono-head`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Feature where to record the matched term's mono-head (or superhead, or single-token head) identifier.

<h3 name="multipleEntryBehaviour" class="param">multipleEntryBehaviour</h3>

<div class="param-level param-level-default-value">Default value: `all`
</div>
<div class="param-type">Type: <a href="../converter/fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.trie.MultipleEntryBehaviour" class="converter">MultipleEntryBehaviour</a>
</div>
Specifies the behavior if <a href="#dictFile" class="param">dictFile</a> contains several entries with the same key.

<h3 name="projectLemmas" class="param">projectLemmas</h3>

<div class="param-level param-level-default-value">Default value: `false`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.Boolean" class="converter">Boolean</a>
</div>
If *true*, the this searches for term lemmas instead of surface forms.

<h3 name="sectionFilter" class="param">sectionFilter</h3>

<div class="param-level param-level-default-value">Default value: `true`
</div>
<div class="param-type">Type: <a href="../converter/fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.Expression" class="converter">Expression</a>
</div>
Process only sections that satisfy this expression.

<h3 name="skipConsecutiveWhitespaces" class="param">skipConsecutiveWhitespaces</h3>

<div class="param-level param-level-default-value">Default value: `false`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.Boolean" class="converter">Boolean</a>
</div>
If set to *true*, then allow the insertion of consecutive whitespace characters in the subject. For instance, the contents *amino  acid* matches the entry *amino acid*.

<h3 name="skipWhitespace" class="param">skipWhitespace</h3>

<div class="param-level param-level-default-value">Default value: `false`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.Boolean" class="converter">Boolean</a>
</div>
If set to *true*, then allow arbitrary insertion of whitespace characters in the subject. For instance, the contents *amino acid* matches the key *aminoacid*.

<h3 name="subject" class="param">subject</h3>

<div class="param-level param-level-default-value">Default value: `WORD`
</div>
<div class="param-type">Type: <a href="../converter/fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.trie.Subject" class="converter">Subject</a>
</div>
Specifies the contents to match.

<h3 name="termId" class="param">termId</h3>

<div class="param-level param-level-default-value">Default value: `term-id`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Feature where to record the matched term's identifier.

<h3 name="termLemma" class="param">termLemma</h3>

<div class="param-level param-level-default-value">Default value: `lemma`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Feature where to record the matched term's lemma string.

<h3 name="termPOS" class="param">termPOS</h3>

<div class="param-level param-level-default-value">Default value: `pos`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Feature where to record the matched term's components POS tags.

<h3 name="wordStartCaseInsensitive" class="param">wordStartCaseInsensitive</h3>

<div class="param-level param-level-default-value">Default value: `false`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.Boolean" class="converter">Boolean</a>
</div>
If set to *true*, then allow case folding on the first character of each word.

