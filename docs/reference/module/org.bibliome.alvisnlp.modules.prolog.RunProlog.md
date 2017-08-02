# org.bibliome.alvisnlp.modules.prolog.RunProlog

## Synopsis

Runs a Prolog program with the corpus data structure encoded as facts.

**This module is experimental.**

## Description

*org.bibliome.alvisnlp.modules.prolog.RunProlog* runs a Prolog program specified in two parameters:
	
1. [theory](#theory) is read as a regular Prolog program;
2. [facts](#facts) generate a set of facts.


	Additionally [goals](#goals) specifies the goals of the Prolog programs, and an associated action expression that will be evaluated for each solution.
      

*org.bibliome.alvisnlp.modules.prolog.RunProlog* will run generate facts and resolve goals for each element specified by [target](#target).
      

## Parameters

<a name="facts">

### facts

Optional

Type: [FactDefinition[]](../converter/org.bibliome.alvisnlp.modules.prolog.FactDefinition[])

Fact specifications.

<a name="goals">

### goals

Optional

Type: [GoalDefinition[]](../converter/org.bibliome.alvisnlp.modules.prolog.GoalDefinition[])

Goal specifications.

<a name="target">

### target

Optional

Type: [Expression](../converter/alvisnlp.corpus.expressions.Expression)

Top-level elements for which facts and goals are computed. This expression is evaluated as a list of elements with the corpus as context.

<a name="theory">

### theory

Optional

Type: [SourceStream](../converter/org.bibliome.util.streams.SourceStream)

Source of the main Prolog program.

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

<a name="addToLayer">

### addToLayer

Default value: `false`

Type: [Boolean](../converter/java.lang.Boolean)

Either the goal actions may add annotations to layers.

<a name="createAnnotations">

### createAnnotations

Default value: `false`

Type: [Boolean](../converter/java.lang.Boolean)

Either the goal actions may create annotations.

<a name="createDocuments">

### createDocuments

Default value: `false`

Type: [Boolean](../converter/java.lang.Boolean)

Either the goal actions may create documents.

<a name="createRelations">

### createRelations

Default value: `false`

Type: [Boolean](../converter/java.lang.Boolean)

Either the goal actions may create relations.

<a name="createSections">

### createSections

Default value: `false`

Type: [Boolean](../converter/java.lang.Boolean)

Either the goal actions may create sections.

<a name="createTuples">

### createTuples

Default value: `false`

Type: [Boolean](../converter/java.lang.Boolean)

Either the goal actions may create tuples.

<a name="deleteElements">

### deleteElements

Default value: `false`

Type: [Boolean](../converter/java.lang.Boolean)

Either the goal actions may delete elements.

<a name="removeFromLayer">

### removeFromLayer

Default value: `false`

Type: [Boolean](../converter/java.lang.Boolean)

Either the goal actions may remove annotations from layers.

<a name="setArguments">

### setArguments

Default value: `false`

Type: [Boolean](../converter/java.lang.Boolean)

Either the goal actions may set tuple arguments.

<a name="setFeatures">

### setFeatures

Default value: `false`

Type: [Boolean](../converter/java.lang.Boolean)

Either the goal actions may add element features.

