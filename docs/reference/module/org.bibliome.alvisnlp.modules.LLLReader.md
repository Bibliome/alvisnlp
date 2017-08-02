<h1 class="module">LLLReader</h1>

## Synopsis

Read files and annotations in [LLL format](XXX).

## Description

*LLLReader* reads files in <a href="#source" class="param">source</a> in [LLL challenge](XXX) format.

## Parameters

<a name="source">

### source

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/org.bibliome.util.streams.SourceStream" class="converter">SourceStream</a>
</div>
Path of the file or directory, or URL, containg the files to import.

<a name="constantAnnotationFeatures">

### constantAnnotationFeatures

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/alvisnlp.module.types.Mapping" class="converter">Mapping</a>
</div>
Constant features to add to each annotation created by this module

<a name="constantDocumentFeatures">

### constantDocumentFeatures

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/alvisnlp.module.types.Mapping" class="converter">Mapping</a>
</div>
Constant features to add to each document created by this module

<a name="constantRelationFeatures">

### constantRelationFeatures

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/alvisnlp.module.types.Mapping" class="converter">Mapping</a>
</div>
Constant features to add to each relation created by this module

<a name="constantSectionFeatures">

### constantSectionFeatures

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/alvisnlp.module.types.Mapping" class="converter">Mapping</a>
</div>
Constant features to add to each section created by this module

<a name="constantTupleFeatures">

### constantTupleFeatures

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/alvisnlp.module.types.Mapping" class="converter">Mapping</a>
</div>
Constant features to add to each tuple created by this module

<a name="agentFeatureName">

### agentFeatureName

<div class="param-level param-level-default-value">Default value: `agent`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Feature that has the value `yes` for entities that represent an agent.

<a name="dependenciesRelationName">

### dependenciesRelationName

<div class="param-level param-level-default-value">Default value: `dependencies`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Name of the relation whose tuples represent dependencies.

<a name="dependencyLabelFeatureName">

### dependencyLabelFeatureName

<div class="param-level param-level-default-value">Default value: `label`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Feature where to store the dependency label.

<a name="dependentRole">

### dependentRole

<div class="param-level param-level-default-value">Default value: `dependent`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Name of the role of the argument that represent the dependency dependent.

<a name="genicAgentRole">

### genicAgentRole

<div class="param-level param-level-default-value">Default value: `agent`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Role of the argument that represent the genic interaction target.

<a name="genicInteractionRelationName">

### genicInteractionRelationName

<div class="param-level param-level-default-value">Default value: `genicInteraction`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Name of the relation whose tuples represent genic interactions.

<a name="genicTargetRole">

### genicTargetRole

<div class="param-level param-level-default-value">Default value: `target`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Role of the argument that represent the genic interaction target.

<a name="headRole">

### headRole

<div class="param-level param-level-default-value">Default value: `head`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Name of the role of the argument that represent the dependency head.

<a name="idFeatureName">

### idFeatureName

<div class="param-level param-level-default-value">Default value: `id`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Feature where to store annotations and tuples identifiers.

<a name="lemmaFeatureName">

### lemmaFeatureName

<div class="param-level param-level-default-value">Default value: `lemma`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Feature where to store the word lemma.

<a name="sectionName">

### sectionName

<div class="param-level param-level-default-value">Default value: `sentence`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Name of the unique section created.

<a name="sentenceLayerName">

### sentenceLayerName

<div class="param-level param-level-default-value">Default value: `sentences`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Name of the layer where to store sentence annotations.

<a name="targetFeatureName">

### targetFeatureName

<div class="param-level param-level-default-value">Default value: `target`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Feature that has the value `yes` for entities that represent a target.

<a name="wordLayerName">

### wordLayerName

<div class="param-level param-level-default-value">Default value: `words`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Name of the layer where to store word annotations.

