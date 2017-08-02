# org.bibliome.alvisnlp.modules.RelpWriter

## Synopsis

Writes the corpus in relp format.

## Description

*org.bibliome.alvisnlp.modules.RelpWriter* writes the corpus into [outFilet](#outFilet) in relp format.

## Parameters

<a name="outFile">

### outFile

Optional

Type: [TargetStream](../converter/org.bibliome.util.streams.TargetStream)

File where to write the dependencies.

<a name="linkageNumberFeature">

### linkageNumberFeature

Optional

Type: [String](../converter/java.lang.String)

Feature containing the linkage number to which a dependency belongs.

<a name="dependencyLabelFeature">

### dependencyLabelFeature

Default value: `label`

Type: [String](../converter/java.lang.String)

Feature containing the dependency label.

<a name="dependencyRelation">

### dependencyRelation

Default value: `dependencies`

Type: [String](../converter/java.lang.String)

Name of the dependecy relation.

<a name="dependentForm">

### dependentForm

Default value: `properties:@:form()`

Type: [Expression](../converter/alvisnlp.corpus.expressions.Expression)

Expression evaluated as a string with the dependency dependent element as context. The result is the surface form of the dependent.

<a name="dependentRole">

### dependentRole

Default value: `dependent`

Type: [String](../converter/java.lang.String)

Name of the role of the dependent word.

<a name="documentFilter">

### documentFilter

Default value: `true`

Type: [Expression](../converter/alvisnlp.corpus.expressions.Expression)

Only process document that satisfy this filter.

<a name="headForm">

### headForm

Default value: `properties:@:form()`

Type: [Expression](../converter/alvisnlp.corpus.expressions.Expression)

Expression evaluated as a string with the dependency head element as context. The result is the surface form of the head.

<a name="headRole">

### headRole

Default value: `head`

Type: [String](../converter/java.lang.String)

Name of the role of the head word.

<a name="lemmaForm">

### lemmaForm

Default value: `properties:@:lemma()`

Type: [Expression](../converter/alvisnlp.corpus.expressions.Expression)

Expression evaluated as a string with the word element as context. The result is the lemma of the word.

<a name="pmid">

### pmid

Default value: `properties:@:id()`

Type: [Expression](../converter/alvisnlp.corpus.expressions.Expression)

Expression evaluated as a string with the document as context. The result is the identifier of the document.

<a name="sectionFilter">

### sectionFilter

Default value: `boolean:and(true, boolean:and(nav:layer:sentences(), nav:layer:words()))`

Type: [Expression](../converter/alvisnlp.corpus.expressions.Expression)

Process only sections that satisfy this filter.

<a name="sentenceLayer">

### sentenceLayer

Default value: `sentences`

Type: [String](../converter/java.lang.String)

Name of the layer containing sentence annotations.

<a name="sentenceRole">

### sentenceRole

Default value: `sentence`

Type: [String](../converter/java.lang.String)

Name of the role of the parsed sentence in the dependency relation.

<a name="wordForm">

### wordForm

Default value: `properties:@:form()`

Type: [Expression](../converter/alvisnlp.corpus.expressions.Expression)

Expression evaluated as a string with the word element as context. The result is the surface form of the word.

<a name="wordLayer">

### wordLayer

Default value: `words`

Type: [String](../converter/java.lang.String)

Name of the layer containing word annotations.

