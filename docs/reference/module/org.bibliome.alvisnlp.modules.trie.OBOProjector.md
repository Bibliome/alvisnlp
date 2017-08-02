# org.bibliome.alvisnlp.modules.trie.OBOProjector

## Synopsis

Projects OBO terms and synonyms on sections.

## Description

*org.bibliome.alvisnlp.modules.trie.OBOProjector* reads [oboFiles](#oboFiles) in [OBO format](XXX) and searches for term names and synonyms in sections.

The parameters [allowJoined](#allowJoined), [allUpperCaseInsensitive](#allUpperCaseInsensitive), [caseInsensitive](#caseInsensitive), [ignoreDiacritics](#ignoreDiacritics), [joinDash](#joinDash), [matchStartCaseInsensitive](#matchStartCaseInsensitive), [skipConsecutiveWhitespaces](#skipConsecutiveWhitespaces), [skipWhitespace](#skipWhitespace) and [wordStartCaseInsensitive](#wordStartCaseInsensitive) control the matching between the section and the entry keys.

The [subject](#subject) parameter specifies which text of the section should be matched. There are two options:
      
* the entries are matched on the contents of the section, [subject](#subject) can also control if matches boundaries coincide with word delimiters;
* the entries are matched on the feature value of annotations of a given layer separated by a whitespace, in this way entries can be searched against word lemmas for instance.



*org.bibliome.alvisnlp.modules.trie.OBOProjector* creates an annotation for each matched entry and adds these annotations to the layer named [targetLayerName](#targetLayerName). The created annotations will have features [nameFeature](#nameFeature), [idFeature](#idFeature) and [pathFeature](#pathFeature) set to the matched term name, identifier and path.

If specified, then *org.bibliome.alvisnlp.modules.trie.OBOProjector* assumes that [trieSource](#trieSource) contains a compiled version of the dictionary. [dictFile](#dictFile) is not read. If specified, *org.bibliome.alvisnlp.modules.trie.OBOProjector* writes a compiled version of the dictionary in [trieSink](#trieSink). The use of compiled dictionaries may accelerate the processing for large dictionaries.

## Parameters

<a name="oboFiles">

### oboFiles

Optional

Type: [String[]](../converter/java.lang.String[])

Path to the source OBO files.

<a name="targetLayerName">

### targetLayerName

Optional

Type: [String](../converter/java.lang.String)

Name of the layer that contains the match annotations.

<a name="ancestorsFeature">

### ancestorsFeature

Optional

Type: [String](../converter/java.lang.String)

Name of the feature that contains the term ancestors ids.

<a name="childrenFeature">

### childrenFeature

Optional

Type: [String](../converter/java.lang.String)

Name of the feature that contains the term children ids.

<a name="constantAnnotationFeatures">

### constantAnnotationFeatures

Optional

Type: [Mapping](../converter/alvisnlp.module.types.Mapping)

Constant features to add to each annotation created by this module

<a name="idFeature">

### idFeature

Optional

Type: [String](../converter/java.lang.String)

Feature where to store the matched term identifier.

<a name="nameFeature">

### nameFeature

Optional

Type: [String](../converter/java.lang.String)

Feature where to store the matched term name.

<a name="parentsFeature">

### parentsFeature

Optional

Type: [String](../converter/java.lang.String)

Name of the feature that contains the term parents ids.

<a name="pathFeature">

### pathFeature

Optional

Type: [String](../converter/java.lang.String)

Feature where to store the matched term path.

<a name="trieSink">

### trieSink

Optional

Type: [OutputFile](../converter/org.bibliome.util.files.OutputFile)

If set, *org.bibliome.alvisnlp.modules.trie.OBOProjector* writes the compiled dictionary to the specified file.

<a name="trieSource">

### trieSource

Optional

Type: [InputFile](../converter/org.bibliome.util.files.InputFile)

If set, read the compiled dictionary from the specified files. Compiled dictionaries are generally faster for large dictionaries.

<a name="versionFeature">

### versionFeature

Optional

Type: [String](../converter/java.lang.String)

Name of the feature where to store the ontology version.

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

<a name="keepDBXref">

### keepDBXref

Default value: `false`

Type: [Boolean](../converter/java.lang.Boolean)

Add all database cross-references of the term. *org.bibliome.alvisnlp.modules.trie.OBOProjector* creates a feature key-value pair for each *dbxref* in the matching term.

<a name="matchStartCaseInsensitive">

### matchStartCaseInsensitive

Default value: `false`

Type: [Boolean](../converter/java.lang.Boolean)

Either the match allows case substitution on the first character of the entry key.

<a name="multipleEntryBehaviour">

### multipleEntryBehaviour

Default value: `all`

Type: [MultipleEntryBehaviour](../converter/org.bibliome.alvisnlp.modules.trie.MultipleEntryBehaviour)

Specifies the behavious of *org.bibliome.alvisnlp.modules.trie.OBOProjector* if [dictFile](#dictFile) contains several entries with the same key.

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

