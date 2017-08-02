# org.bibliome.alvisnlp.modules.WhatsWrongExport

## Synopsis

Writes files in [What's Wrong with my NLP]() format.

## Description

synopsis

## Parameters

<a name="outFile">

### outFile

Optional

Type: [TargetStream](../converter/org.bibliome.util.streams.TargetStream)

Data sink where to write.

<a name="relationName">

### relationName

Optional

Type: [String](../converter/java.lang.String)

Name of the relation that contains tuples that will be represented as relations.

<a name="sentences">

### sentences

Optional

Type: [Expression](../converter/alvisnlp.corpus.expressions.Expression)

Expression evaluated as a list of elements with the section as the context element. The result specifies the sentences in the section.

<a name="entities">

### entities

Optional

Type: [String[]](../converter/java.lang.String[])

Names of layers containing named entities.

<a name="entityType">

### entityType

Optional

Type: [String](../converter/java.lang.String)

Name of the feature of entity annotations containing the named entity type.

<a name="dependent">

### dependent

Default value: `dependent`

Type: [String](../converter/java.lang.String)

Name of the tuple role that references the dependent. The dependent must be in the [words](#words) layer.

<a name="documentFilter">

### documentFilter

Default value: `true`

Type: [Expression](../converter/alvisnlp.corpus.expressions.Expression)

Only process document that satisfy this filter.

<a name="head">

### head

Default value: `head`

Type: [String](../converter/java.lang.String)

Name of the tuple role that references the head. The head must be in the [words](#words) layer.

<a name="label">

### label

Default value: `label`

Type: [String](../converter/java.lang.String)

Name of the tuple feature that specifies the relation label.

<a name="sectionFilter">

### sectionFilter

Default value: `boolean:and(true, nav:layer:words())`

Type: [Expression](../converter/alvisnlp.corpus.expressions.Expression)

Process only sections that satisfy this filter.

<a name="sentence">

### sentence

Default value: `sentence`

Type: [String](../converter/java.lang.String)

Name of the tuple role that references the sentence.

<a name="wordForm">

### wordForm

Default value: `form`

Type: [String](../converter/java.lang.String)

Name of the feature that specifies the word form.

<a name="words">

### words

Default value: `words`

Type: [String](../converter/java.lang.String)

Nme of the layer that contains the tokens.

