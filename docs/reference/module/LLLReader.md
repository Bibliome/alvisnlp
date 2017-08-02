# org.bibliome.alvisnlp.modules.LLLReader

## Synopsis

Read files and annotations in [LLL format](XXX).

## Description

*org.bibliome.alvisnlp.modules.LLLReader* reads files in [source](#source) in [LLL challenge](XXX) format.

## Parameters

<a name="source">

### source

Optional

Type: [SourceStream](../converter/org.bibliome.util.streams.SourceStream)

Path of the file or directory, or URL, containg the files to import.

<a name="constantAnnotationFeatures">

### constantAnnotationFeatures

Optional

Type: [Mapping](../converter/alvisnlp.module.types.Mapping)

Constant features to add to each annotation created by this module

<a name="constantDocumentFeatures">

### constantDocumentFeatures

Optional

Type: [Mapping](../converter/alvisnlp.module.types.Mapping)

Constant features to add to each document created by this module

<a name="constantRelationFeatures">

### constantRelationFeatures

Optional

Type: [Mapping](../converter/alvisnlp.module.types.Mapping)

Constant features to add to each relation created by this module

<a name="constantSectionFeatures">

### constantSectionFeatures

Optional

Type: [Mapping](../converter/alvisnlp.module.types.Mapping)

Constant features to add to each section created by this module

<a name="constantTupleFeatures">

### constantTupleFeatures

Optional

Type: [Mapping](../converter/alvisnlp.module.types.Mapping)

Constant features to add to each tuple created by this module

<a name="agentFeatureName">

### agentFeatureName

Default value: `agent`

Type: [String](../converter/java.lang.String)

Feature that has the value `yes` for entities that represent an agent.

<a name="dependenciesRelationName">

### dependenciesRelationName

Default value: `dependencies`

Type: [String](../converter/java.lang.String)

Name of the relation whose tuples represent dependencies.

<a name="dependencyLabelFeatureName">

### dependencyLabelFeatureName

Default value: `label`

Type: [String](../converter/java.lang.String)

Feature where to store the dependency label.

<a name="dependentRole">

### dependentRole

Default value: `dependent`

Type: [String](../converter/java.lang.String)

Name of the role of the argument that represent the dependency dependent.

<a name="genicAgentRole">

### genicAgentRole

Default value: `agent`

Type: [String](../converter/java.lang.String)

Role of the argument that represent the genic interaction target.

<a name="genicInteractionRelationName">

### genicInteractionRelationName

Default value: `genicInteraction`

Type: [String](../converter/java.lang.String)

Name of the relation whose tuples represent genic interactions.

<a name="genicTargetRole">

### genicTargetRole

Default value: `target`

Type: [String](../converter/java.lang.String)

Role of the argument that represent the genic interaction target.

<a name="headRole">

### headRole

Default value: `head`

Type: [String](../converter/java.lang.String)

Name of the role of the argument that represent the dependency head.

<a name="idFeatureName">

### idFeatureName

Default value: `id`

Type: [String](../converter/java.lang.String)

Feature where to store annotations and tuples identifiers.

<a name="lemmaFeatureName">

### lemmaFeatureName

Default value: `lemma`

Type: [String](../converter/java.lang.String)

Feature where to store the word lemma.

<a name="sectionName">

### sectionName

Default value: `sentence`

Type: [String](../converter/java.lang.String)

Name of the unique section created.

<a name="sentenceLayerName">

### sentenceLayerName

Default value: `sentences`

Type: [String](../converter/java.lang.String)

Name of the layer where to store sentence annotations.

<a name="targetFeatureName">

### targetFeatureName

Default value: `target`

Type: [String](../converter/java.lang.String)

Feature that has the value `yes` for entities that represent a target.

<a name="wordLayerName">

### wordLayerName

Default value: `words`

Type: [String](../converter/java.lang.String)

Name of the layer where to store word annotations.

