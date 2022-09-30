<h1 class="module">RDFProjector</h1>

## Synopsis

Projects OBO terms and synonyms on sections.

**This module is experimental.**

## Description

 *RDFProjector* reads <a href="#source" class="param">source</a> SKOS terminologies or OWL ontologies and searches for class and concept labels in sections.

The parameters <a href="#allowJoined" class="param">allowJoined</a> , <a href="#allUpperCaseInsensitive" class="param">allUpperCaseInsensitive</a> , <a href="#caseInsensitive" class="param">caseInsensitive</a> , <a href="#ignoreDiacritics" class="param">ignoreDiacritics</a> , <a href="#joinDash" class="param">joinDash</a> , <a href="#matchStartCaseInsensitive" class="param">matchStartCaseInsensitive</a> , <a href="#skipConsecutiveWhitespaces" class="param">skipConsecutiveWhitespaces</a> , <a href="#skipWhitespace" class="param">skipWhitespace</a> and <a href="#wordStartCaseInsensitive" class="param">wordStartCaseInsensitive</a> control the matching between the section and the entry keys.

The <a href="#subject" class="param">subject</a> parameter specifies which text of the section should be matched. There are two options:
* the entries are matched on the contents of the section, <a href="#subject" class="param">subject</a> can also control if matches boundaries coincide with word delimiters;
* the entries are matched on the feature value of annotations of a given layer separated by a whitespace, in this way entries can be searched against word lemmas for instance.



 *RDFProjector* creates an annotation for each matched entry and adds these annotations to the layer named <a href="#targetLayerName" class="param">targetLayerName</a> . The created annotations will have the feature <a href="#uriFeatureName" class="param">uriFeatureName</a> containing the URI of the matched class or concept. *RDFProjector* may also map property object values into features specified by <a href="#labelFeatures" class="param">labelFeatures</a> .

## Snippet



<button class="copy-code-button" title="Copy to clipboard" onclick="copy_code(this)">📋</button>
```xml
<rdfprojector class="RDFProjector>
    <source></source>
    <targetLayerName></targetLayerName>
    <uriFeatureName></uriFeatureName>
</rdfprojector>
```

## Mandatory parameters

<h3 id="source" class="param">source</h3>

<div class="param-level param-level-mandatory">Mandatory
</div>
<div class="param-type">Type: <a href="../converter/fr.inra.maiage.bibliome.util.streams.SourceStream" class="converter">SourceStream</a>
</div>
Path to the source SKOS/OWL files.

<h3 id="targetLayerName" class="param">targetLayerName</h3>

<div class="param-level param-level-mandatory">Mandatory
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Name of the layer that contains the match annotations.

<h3 id="uriFeatureName" class="param">uriFeatureName</h3>

<div class="param-level param-level-mandatory">Mandatory
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Feature where to store the entry URI.

## Optional parameters

<h3 id="constantAnnotationFeatures" class="param">constantAnnotationFeatures</h3>

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/fr.inra.maiage.bibliome.alvisnlp.core.module.types.Mapping" class="converter">Mapping</a>
</div>
Constant features to add to each annotation created by this module.

<h3 id="language" class="param">language</h3>

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Specify the language of labels to project. If this parameter is not set then labels of any language are projected. Labels without a language qualifier are always projected regardless of the value of this parameter.

<h3 id="trieSink" class="param">trieSink</h3>

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/fr.inra.maiage.bibliome.util.files.OutputFile" class="converter">OutputFile</a>
</div>
If set, then *RDFProjector* writes the compiled dictionary to the specified file.

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

<h3 id="labelFeatures" class="param">labelFeatures</h3>

<div class="param-level param-level-default-value">Default value: `{rdfs-label=rdfs:label, skos-prefLabel=skos:prefLabel}`
</div>
<div class="param-type">Type: <a href="../converter/fr.inra.maiage.bibliome.alvisnlp.core.module.types.Mapping" class="converter">Mapping</a>
</div>
Mapping from feature names to property URIs. This parameter indicates the properties of the entry to record in features.

<h3 id="labelURIs" class="param">labelURIs</h3>

<div class="param-level param-level-default-value">Default value: `rdfs:label,skos:prefLabel,skos:altLabel,skos:hiddenLabel,skos:notation,oboInOwl:hasBroadSynonym,oboInOwl:hasExactSynonym,oboInOwl:hasRelatedSynonym,oboInOwl:hasSynonym`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String%5B%5D" class="converter">String[]</a>
</div>
RDF properties whose object values that represent entry keys.

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
Specifies the behavior if <a href="#dictFile" class="param">dictFile</a> contains several entries with the same key.

<h3 id="prefixes" class="param">prefixes</h3>

<div class="param-level param-level-default-value">Default value: `{}`
</div>
<div class="param-type">Type: <a href="../converter/fr.inra.maiage.bibliome.alvisnlp.core.module.types.Mapping" class="converter">Mapping</a>
</div>
Specify URI prefixes to be used in <a href="#resourceTypeURIs" class="param">resourceTypeURIs</a> , <a href="#labelURIs" class="param">labelURIs</a> , and <a href="#labelFeatures" class="param">labelFeatures</a> 

<h3 id="rdfFormat" class="param">rdfFormat</h3>

<div class="param-level param-level-default-value">Default value: `Lang:RDF/XML`
</div>
<div class="param-type">Type: <a href="../converter/org.apache.jena.riot.Lang" class="converter">Lang</a>
</div>
Specify the RDF serialization format (xml, rdfxml, xmlrdf, turtle, ttl, n3, ntriples, ntriple, nt, jsonld, rdfjson, jsonrdf, json, trig, nquads, nq, nthrift, csv, tsv, trix).

<h3 id="resourceTypeURIs" class="param">resourceTypeURIs</h3>

<div class="param-level param-level-default-value">Default value: `owl:Class,skos:Concept`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String%5B%5D" class="converter">String[]</a>
</div>
Type of RDF resources that represent an entry.

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

<h3 id="wordStartCaseInsensitive" class="param">wordStartCaseInsensitive</h3>

<div class="param-level param-level-default-value">Default value: `false`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.Boolean" class="converter">Boolean</a>
</div>
If set to *true* , then allow case folding on the first character of each word.

