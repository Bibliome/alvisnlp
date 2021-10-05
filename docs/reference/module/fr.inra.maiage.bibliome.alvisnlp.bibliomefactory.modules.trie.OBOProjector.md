<h1 class="module">OBOProjector</h1>

## Synopsis

Projects OBO terms and synonyms on sections.

## Description

*OBOProjector* reads <a href="#oboFiles" class="param">oboFiles</a> in [OBO format](XXX) and searches for term names and synonyms in sections.

The parameters <a href="#allowJoined" class="param">allowJoined</a>, <a href="#allUpperCaseInsensitive" class="param">allUpperCaseInsensitive</a>, <a href="#caseInsensitive" class="param">caseInsensitive</a>, <a href="#ignoreDiacritics" class="param">ignoreDiacritics</a>, <a href="#joinDash" class="param">joinDash</a>, <a href="#matchStartCaseInsensitive" class="param">matchStartCaseInsensitive</a>, <a href="#skipConsecutiveWhitespaces" class="param">skipConsecutiveWhitespaces</a>, <a href="#skipWhitespace" class="param">skipWhitespace</a> and <a href="#wordStartCaseInsensitive" class="param">wordStartCaseInsensitive</a> control the matching between the section and the entry keys.

The <a href="#subject" class="param">subject</a> parameter specifies which text of the section should be matched. There are two options:
  
* the entries are matched on the contents of the section, <a href="#subject" class="param">subject</a> can also control if matches boundaries coincide with word delimiters;
* the entries are matched on the feature value of annotations of a given layer separated by a whitespace, in this way entries can be searched against word lemmas for instance.



*OBOProjector* creates an annotation for each matched entry and adds these annotations to the layer named <a href="#targetLayerName" class="param">targetLayerName</a>. The created annotations will have features <a href="#nameFeature" class="param">nameFeature</a>, <a href="#idFeature" class="param">idFeature</a> and <a href="#pathFeature" class="param">pathFeature</a> set to the matched term name, identifier and path.

If specified, then *OBOProjector* assumes that <a href="#trieSource" class="param">trieSource</a> contains a compiled version of the dictionary. <a href="#dictFile" class="param">dictFile</a> is not read. If specified, *OBOProjector* writes a compiled version of the dictionary in <a href="#trieSink" class="param">trieSink</a>. The use of compiled dictionaries may accelerate the processing for large dictionaries.

## Parameters

<h3 name="oboFiles" class="param">oboFiles</h3>

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/fr.inra.maiage.bibliome.util.files.InputFile%5B%5D" class="converter">InputFile[]</a>
</div>
Path to the source OBO files.

<h3 name="targetLayerName" class="param">targetLayerName</h3>

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Name of the layer that contains the match annotations.

<h3 name="ancestorsFeature" class="param">ancestorsFeature</h3>

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Name of the feature that contains the term ancestors ids.

<h3 name="childrenFeature" class="param">childrenFeature</h3>

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Name of the feature that contains the term children ids.

<h3 name="constantAnnotationFeatures" class="param">constantAnnotationFeatures</h3>

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/fr.inra.maiage.bibliome.alvisnlp.core.module.types.Mapping" class="converter">Mapping</a>
</div>
Constant features to add to each annotation created by this module.

<h3 name="idFeature" class="param">idFeature</h3>

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Feature where to store the matched term identifier.

<h3 name="nameFeature" class="param">nameFeature</h3>

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Feature where to store the matched term name.

<h3 name="parentsFeature" class="param">parentsFeature</h3>

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Name of the feature that contains the term parents ids.

<h3 name="pathFeature" class="param">pathFeature</h3>

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Feature where to store the matched term path.

<h3 name="trieSink" class="param">trieSink</h3>

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/fr.inra.maiage.bibliome.util.files.OutputFile" class="converter">OutputFile</a>
</div>
If set, then *OBOProjector* writes the compiled dictionary to the specified file.

<h3 name="trieSource" class="param">trieSource</h3>

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/fr.inra.maiage.bibliome.util.files.InputFile" class="converter">InputFile</a>
</div>
If set, read the compiled dictionary from the specified file. Compiled dictionaries are usually faster for large dictionaries.

<h3 name="versionFeature" class="param">versionFeature</h3>

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Name of the feature where to store the ontology version.

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

<h3 name="keepDBXref" class="param">keepDBXref</h3>

<div class="param-level param-level-default-value">Default value: `false`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.Boolean" class="converter">Boolean</a>
</div>
Add all database cross-references of the term. *OBOProjector* creates a feature key-value pair for each *dbxref* in the matching term.

<h3 name="matchStartCaseInsensitive" class="param">matchStartCaseInsensitive</h3>

<div class="param-level param-level-default-value">Default value: `false`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.Boolean" class="converter">Boolean</a>
</div>
If set to *true*, then allow case folding on the first character of the entry key.

<h3 name="multipleEntryBehaviour" class="param">multipleEntryBehaviour</h3>

<div class="param-level param-level-default-value">Default value: `all`
</div>
<div class="param-type">Type: <a href="../converter/fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.trie.MultipleEntryBehaviour" class="converter">MultipleEntryBehaviour</a>
</div>
Specifies the behavior if <a href="#dictFile" class="param">dictFile</a> contains several entries with the same key.

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

<h3 name="wordStartCaseInsensitive" class="param">wordStartCaseInsensitive</h3>

<div class="param-level param-level-default-value">Default value: `false`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.Boolean" class="converter">Boolean</a>
</div>
If set to *true*, then allow case folding on the first character of each word.

