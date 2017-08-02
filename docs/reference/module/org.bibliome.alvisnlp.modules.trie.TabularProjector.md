# org.bibliome.alvisnlp.modules.trie.TabularProjector

## Synopsis

Projects a simple dictionary on sections.

## Description

*org.bibliome.alvisnlp.modules.trie.TabularProjector* reads a list of entries from [dictFile](#dictFile) and searches for these entries in sections. The format of the dictionary is one entry per line. Each line is split into columns separated by tab characters, or whichever character defined by [separator](#separator). The column specified by [keyIndex](#keyIndex) will be the entry to be searched and the other columns are data associated to the entry.

The parameters [skipBlank](#skipBlank), [skipEmpty](#skipEmpty), [strictColumnNumber](#strictColumnNumber), [trimColumns](#trimColumns), [separator](#separator), [multipleEntryBehaviour](#multipleEntryBehaviour) control the loading of the dictionary file.

The parameters [allowJoined](#allowJoined), [allUpperCaseInsensitive](#allUpperCaseInsensitive), [caseInsensitive](#caseInsensitive), [ignoreDiacritics](#ignoreDiacritics), [joinDash](#joinDash), [matchStartCaseInsensitive](#matchStartCaseInsensitive), [skipConsecutiveWhitespaces](#skipConsecutiveWhitespaces), [skipWhitespace](#skipWhitespace) and [wordStartCaseInsensitive](#wordStartCaseInsensitive) control the matching between the section and the entry keys.

The [subject](#subject) parameter specifies which text of the section should be matched. There are two options:
      
* the entries are matched on the contents of the section, [subject](#subject) can also control if matches boundaries coincide with word delimiters;
* the entries are matched on the feature value of annotations of a given layer separated by a whitespace, in this way entries can be searched against word lemmas for instance.



*org.bibliome.alvisnlp.modules.trie.TabularProjector* creates an annotation for each matched entry and adds these annotations to the layer named [targetLayerName](#targetLayerName). The created annotations will have features whose keys correspond to [entryFeatureNames](#entryFeatureNames) and values to the data associated to the matched entry (columns in the dictionary file). For instance if [entryFeatureNames](#entryFeatureNames) is *[a,b,c]*, then each annotation will have three features named *a*, *b* and *c* with the respective values of the entry's second, third and fourth columns. A feature name left blank in [entryFeatureNames](#entryFeatureNames) will not create a feature. Thus, in order not to keep the entry in the *a* feature, [entryFeatureNames](#entryFeatureNames) should be *[,b,c]*. In addition, the created annotations will have the feature keys and values defined in [constantAnnotationFeatures](#constantAnnotationFeatures).

If specified, then *org.bibliome.alvisnlp.modules.trie.TabularProjector* assumes that [trieSource](#trieSource) contains a compiled version of the dictionary. [dictFile](#dictFile) is not read. If specified, *org.bibliome.alvisnlp.modules.trie.TabularProjector* writes a compiled version of the dictionary in [trieSink](#trieSink). The use of compiled dictionaries may accelerate the processing for large dictionaries.

## Parameters

<a name="dictFile">

### dictFile

Optional

Type: [SourceStream](../converter/org.bibliome.util.streams.SourceStream)

Source of the dictionary.

<a name="targetLayerName">

### targetLayerName

Optional

Type: [String](../converter/java.lang.String)

Name of the layer that contains the match annotations.

<a name="valueFeatures">

### valueFeatures

Optional

Type: [String[]](../converter/java.lang.String[])

Target features in match annotations. The values are the columns in the matched entry line.

<a name="constantAnnotationFeatures">

### constantAnnotationFeatures

Optional

Type: [Mapping](../converter/alvisnlp.module.types.Mapping)

Constant features to add to each annotation created by this module

<a name="trieSink">

### trieSink

Optional

Type: [OutputFile](../converter/org.bibliome.util.files.OutputFile)

If set, *org.bibliome.alvisnlp.modules.trie.TabularProjector* writes the compiled dictionary to the specified file.

<a name="trieSource">

### trieSource

Optional

Type: [InputFile](../converter/org.bibliome.util.files.InputFile)

If set, read the compiled dictionary from the specified files. Compiled dictionaries are generally faster for large dictionaries.

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

<a name="keyIndex">

### keyIndex

Default value: `0`

Type: [Integer[]](../converter/java.lang.Integer[])

Specifies the key column index (starting at 0).

<a name="matchStartCaseInsensitive">

### matchStartCaseInsensitive

Default value: `false`

Type: [Boolean](../converter/java.lang.Boolean)

Either the match allows case substitution on the first character of the entry key.

<a name="multipleEntryBehaviour">

### multipleEntryBehaviour

Default value: `all`

Type: [MultipleEntryBehaviour](../converter/org.bibliome.alvisnlp.modules.trie.MultipleEntryBehaviour)

Specifies the behavious of *org.bibliome.alvisnlp.modules.trie.TabularProjector* if [dictFile](#dictFile) contains several entries with the same key.

<a name="sectionFilter">

### sectionFilter

Default value: `true`

Type: [Expression](../converter/alvisnlp.corpus.expressions.Expression)

Process only sections that satisfy this filter.

<a name="separator">

### separator

Default value: `	`

Type: [Character](../converter/java.lang.Character)

Specifies the character that separates columns in [dictFile](#dictFile).

<a name="skipBlank">

### skipBlank

Default value: `false`

Type: [Boolean](../converter/java.lang.Boolean)

In [dictFile](#dictFile), skip lines that contain only whitespace characters.

<a name="skipConsecutiveWhitespaces">

### skipConsecutiveWhitespaces

Default value: `false`

Type: [Boolean](../converter/java.lang.Boolean)

Either the match allows insertion of consecutive whitespace characters in the subject. For instance, the contents *amino  acid* matches the entry *amino acid*.

<a name="skipEmpty">

### skipEmpty

Default value: `false`

Type: [Boolean](../converter/java.lang.Boolean)

In [dictFile](#dictFile), skip empty lines.

<a name="skipWhitespace">

### skipWhitespace

Default value: `false`

Type: [Boolean](../converter/java.lang.Boolean)

Either the match allows arbitrary insertion of whitespace characters in the subject. For instance, the contents *amino acid* matches the entry *aminoacid*.

<a name="strictColumnNumber">

### strictColumnNumber

Default value: `true`

Type: [Boolean](../converter/java.lang.Boolean)

Either to check that every line in [dictFile](#dictFile) has the same number of columns as the number of features specified in [entryFeatureNames](#entryFeatureNames).

<a name="subject">

### subject

Default value: `WORD`

Type: [Subject](../converter/org.bibliome.alvisnlp.modules.trie.Subject)

Specifies the contents to match.

<a name="trimColumns">

### trimColumns

Default value: `false`

Type: [Boolean](../converter/java.lang.Boolean)

Either to trim column values in [dictFile](#dictFile) from leading and trailing whitespace characters.

<a name="wordStartCaseInsensitive">

### wordStartCaseInsensitive

Default value: `false`

Type: [Boolean](../converter/java.lang.Boolean)

Either the match allows case substitution on the first character of words.

