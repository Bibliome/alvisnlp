# org.bibliome.alvisnlp.modules.rdf.RDFProjector

## Synopsis

Projects OBO terms and synonyms on sections.

**This module is experimental.**

## Description

*org.bibliome.alvisnlp.modules.rdf.RDFProjector* reads [source](#source) SKOS terminologies or OWL ontologies and searches for class and concept labels in sections.

The parameters [allowJoined](#allowJoined), [allUpperCaseInsensitive](#allUpperCaseInsensitive), [caseInsensitive](#caseInsensitive), [ignoreDiacritics](#ignoreDiacritics), [joinDash](#joinDash), [matchStartCaseInsensitive](#matchStartCaseInsensitive), [skipConsecutiveWhitespaces](#skipConsecutiveWhitespaces), [skipWhitespace](#skipWhitespace) and [wordStartCaseInsensitive](#wordStartCaseInsensitive) control the matching between the section and the entry keys.

The [subject](#subject) parameter specifies which text of the section should be matched. There are two options:
      
* the entries are matched on the contents of the section, [subject](#subject) can also control if matches boundaries coincide with word delimiters;
* the entries are matched on the feature value of annotations of a given layer separated by a whitespace, in this way entries can be searched against word lemmas for instance.



*org.bibliome.alvisnlp.modules.rdf.RDFProjector* creates an annotation for each matched entry and adds these annotations to the layer named [targetLayerName](#targetLayerName). The created annotations will have the feature [uriFeatureName](#uriFeatureName) containing the URI of the matched class or concept. *org.bibliome.alvisnlp.modules.rdf.RDFProjector* may also map property object values into features specified by [labelFeatures](#labelFeatures).

## Parameters

<a name="source">

### source

Optional

Type: [SourceStream](../converter/org.bibliome.util.streams.SourceStream)

Path to the source SKOS/OWL files.

<a name="targetLayerName">

### targetLayerName

Optional

Type: [String](../converter/java.lang.String)

Name of the layer that contains the match annotations.

<a name="uriFeatureName">

### uriFeatureName

Optional

Type: [String](../converter/java.lang.String)

Feature where to store the entry URI.

<a name="constantAnnotationFeatures">

### constantAnnotationFeatures

Optional

Type: [Mapping](../converter/alvisnlp.module.types.Mapping)

Constant features to add to each annotation created by this module

<a name="trieSink">

### trieSink

Optional

Type: [OutputFile](../converter/org.bibliome.util.files.OutputFile)

Serialization is not supported.

<a name="trieSource">

### trieSource

Optional

Type: [InputFile](../converter/org.bibliome.util.files.InputFile)

Serialization is not supported.

<a name="allUpperCaseInsensitive">

### allUpperCaseInsensitive

Default value: `false`

Type: [Boolean](../converter/java.lang.Boolean)

Either the match allows case substitution on all characters in words that are all upper case.

<a name="allowJoined">

### allowJoined

Default value: `false`

Type: [Boolean](../converter/java.lang.Boolean)

Either the match allows arbitrary suppression of whitespace characters in the subject. For instance, the contents *aminoacid* matches the entry *amino acid*.

<a name="caseInsensitive">

### caseInsensitive

Default value: `false`

Type: [Boolean](../converter/java.lang.Boolean)

Either the match allows case substitutions on all characters.

<a name="documentFilter">

### documentFilter

Default value: `true`

Type: [Expression](../converter/alvisnlp.corpus.expressions.Expression)

Only process document that satisfy this filter.

<a name="ignoreDiacritics">

### ignoreDiacritics

Default value: `false`

Type: [Boolean](../converter/java.lang.Boolean)

Either the match allows dicacritics substitutions on all characters. For instance the contents *acide amine* matches the entry *acide aminé*.

<a name="joinDash">

### joinDash

Default value: `false`

Type: [Boolean](../converter/java.lang.Boolean)

Either to treat dash characters (-) as whitespace characters if [allowJoined](#allowJoined) is `true`. For instance, the contents *aminoacid* matches the entry *amino-acid*.

<a name="labelFeatures">

### labelFeatures

Default value: `{rdfs-label=rdfs:label, skos-prefLabel=skos:prefLabel}`

Type: [Mapping](../converter/alvisnlp.module.types.Mapping)

Mapping from feature names to property URIs. This parameter indicates the properties of the entry to record in features.

<a name="labelURIs">

### labelURIs

Default value: `rdfs:label,skos:prefLabel,skos:altLabel,skos:hiddenLabel,skos:notation,oboInOwl:hasExactSynonym,oboInOwl:hasRelatedSynonym,oboInOwl:hasSynonym`

Type: [String[]](../converter/java.lang.String[])

RDF properties whose object values that represent entry keys.

<a name="matchStartCaseInsensitive">

### matchStartCaseInsensitive

Default value: `false`

Type: [Boolean](../converter/java.lang.Boolean)

Either the match allows case substitution on the first character of the entry key.

<a name="multipleEntryBehaviour">

### multipleEntryBehaviour

Default value: `all`

Type: [MultipleEntryBehaviour](../converter/org.bibliome.alvisnlp.modules.trie.MultipleEntryBehaviour)

Specifies the behavious of *org.bibliome.alvisnlp.modules.rdf.RDFProjector* if [dictFile](#dictFile) contains several entries with the same key.

<a name="prefixes">

### prefixes

Default value: `{}`

Type: [Mapping](../converter/alvisnlp.module.types.Mapping)

Specify URI prefixes to be used in [resourceTypeURIs](#resourceTypeURIs), [labelURIs](#labelURIs), and [labelFeatures](#labelFeatures)

<a name="resourceTypeURIs">

### resourceTypeURIs

Default value: `owl:Class,skos:Concept`

Type: [String[]](../converter/java.lang.String[])

Type of RDF resources that represent an entry.

<a name="sectionFilter">

### sectionFilter

Default value: `true`

Type: [Expression](../converter/alvisnlp.corpus.expressions.Expression)

Process only sections that satisfy this filter.

<a name="skipConsecutiveWhitespaces">

### skipConsecutiveWhitespaces

Default value: `false`

Type: [Boolean](../converter/java.lang.Boolean)

Either the match allows insertion of consecutive whitespace characters in the subject. For instance, the contents *amino  acid* matches the entry *amino acid*.

<a name="skipWhitespace">

### skipWhitespace

Default value: `false`

Type: [Boolean](../converter/java.lang.Boolean)

Either the match allows arbitrary insertion of whitespace characters in the subject. For instance, the contents *amino acid* matches the entry *aminoacid*.

<a name="subject">

### subject

Default value: `WORD`

Type: [Subject](../converter/org.bibliome.alvisnlp.modules.trie.Subject)

Specifies the contents to match.

<a name="wordStartCaseInsensitive">

### wordStartCaseInsensitive

Default value: `false`

Type: [Boolean](../converter/java.lang.Boolean)

Either the match allows case substitution on the first character of words.

