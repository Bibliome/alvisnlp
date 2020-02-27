<h1 class="module">RDFProjector</h1>

## Synopsis

Projects OBO terms and synonyms on sections.

**This module is experimental.**

## Description

*RDFProjector* reads <a href="#source" class="param">source</a> SKOS terminologies or OWL ontologies and searches for class and concept labels in sections.

The parameters <a href="#allowJoined" class="param">allowJoined</a>, <a href="#allUpperCaseInsensitive" class="param">allUpperCaseInsensitive</a>, <a href="#caseInsensitive" class="param">caseInsensitive</a>, <a href="#ignoreDiacritics" class="param">ignoreDiacritics</a>, <a href="#joinDash" class="param">joinDash</a>, <a href="#matchStartCaseInsensitive" class="param">matchStartCaseInsensitive</a>, <a href="#skipConsecutiveWhitespaces" class="param">skipConsecutiveWhitespaces</a>, <a href="#skipWhitespace" class="param">skipWhitespace</a> and <a href="#wordStartCaseInsensitive" class="param">wordStartCaseInsensitive</a> control the matching between the section and the entry keys.

The <a href="#subject" class="param">subject</a> parameter specifies which text of the section should be matched. There are two options:
  
* the entries are matched on the contents of the section, <a href="#subject" class="param">subject</a> can also control if matches boundaries coincide with word delimiters;
* the entries are matched on the feature value of annotations of a given layer separated by a whitespace, in this way entries can be searched against word lemmas for instance.



*RDFProjector* creates an annotation for each matched entry and adds these annotations to the layer named <a href="#targetLayerName" class="param">targetLayerName</a>. The created annotations will have the feature <a href="#uriFeatureName" class="param">uriFeatureName</a> containing the URI of the matched class or concept. *RDFProjector* may also map property object values into features specified by <a href="#labelFeatures" class="param">labelFeatures</a>.

## Parameters

<h3 name="source" class="param">source</h3>

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/fr.inra.maiage.bibliome.util.streams.SourceStream" class="converter">SourceStream</a>
</div>
Path to the source SKOS/OWL files.

<h3 name="targetLayerName" class="param">targetLayerName</h3>

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Name of the layer that contains the match annotations.

<h3 name="uriFeatureName" class="param">uriFeatureName</h3>

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Feature where to store the entry URI.

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
If set, then *RDFProjector* writes the compiled dictionary to the specified file.

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

<h3 name="labelFeatures" class="param">labelFeatures</h3>

<div class="param-level param-level-default-value">Default value: `{rdfs-label=rdfs:label, skos-prefLabel=skos:prefLabel}`
</div>
<div class="param-type">Type: <a href="../converter/fr.inra.maiage.bibliome.alvisnlp.core.module.types.Mapping" class="converter">Mapping</a>
</div>
Mapping from feature names to property URIs. This parameter indicates the properties of the entry to record in features.

<h3 name="labelURIs" class="param">labelURIs</h3>

<div class="param-level param-level-default-value">Default value: `rdfs:label,skos:prefLabel,skos:altLabel,skos:hiddenLabel,skos:notation,oboInOwl:hasExactSynonym,oboInOwl:hasRelatedSynonym,oboInOwl:hasSynonym`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String[]" class="converter">String[]</a>
</div>
RDF properties whose object values that represent entry keys.

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

<h3 name="prefixes" class="param">prefixes</h3>

<div class="param-level param-level-default-value">Default value: `{}`
</div>
<div class="param-type">Type: <a href="../converter/fr.inra.maiage.bibliome.alvisnlp.core.module.types.Mapping" class="converter">Mapping</a>
</div>
Specify URI prefixes to be used in <a href="#resourceTypeURIs" class="param">resourceTypeURIs</a>, <a href="#labelURIs" class="param">labelURIs</a>, and <a href="#labelFeatures" class="param">labelFeatures</a>

<h3 name="resourceTypeURIs" class="param">resourceTypeURIs</h3>

<div class="param-level param-level-default-value">Default value: `owl:Class,skos:Concept`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String[]" class="converter">String[]</a>
</div>
Type of RDF resources that represent an entry.

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

