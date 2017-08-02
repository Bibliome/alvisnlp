# org.bibliome.alvisnlp.modules.Action

## Synopsis

Applies action expressions on selected elements.

## Description

*org.bibliome.alvisnlp.modules.Action* evaluates [target](#target) as a list of elements, then it evaluates [action](#action) on each element.

*org.bibliome.alvisnlp.modules.Action* is useful when [action](#action) is a side-effect expression. The side-effect expressions allowed are controlled by: [createDocuments](#createDocuments), [createSections](#createSections), [createRelations](#createRelations), [createTuples](#createTuples), [createAnnotations](#createAnnotations), [setArguments](#setArguments), [setFeatures](#setFeatures) and [deleteElements](#deleteElements). If these parameters are not set to true then *org.bibliome.alvisnlp.modules.Action* will refuse to evaluate the corresponding side-effect expressions.

## Parameters

<a name="action">

### action

Optional

Type: [Expression](../converter/alvisnlp.corpus.expressions.Expression)

Action to perform on each result of [target](#target).

<a name="target">

### target

Optional

Type: [Expression](../converter/alvisnlp.corpus.expressions.Expression)

Elements on which to perform the action. The expression is evaluated as a list of elements with the corpus as the context element.

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

Allow to add annotations to layers.

<a name="createAnnotations">

### createAnnotations

Default value: `false`

Type: [Boolean](../converter/java.lang.Boolean)

Allow the creation of annotations.

<a name="createDocuments">

### createDocuments

Default value: `false`

Type: [Boolean](../converter/java.lang.Boolean)

Allow the creation of documents.

<a name="createRelations">

### createRelations

Default value: `false`

Type: [Boolean](../converter/java.lang.Boolean)

Allow the creation of relations.

<a name="createSections">

### createSections

Default value: `false`

Type: [Boolean](../converter/java.lang.Boolean)

Allow the creation of sections.

<a name="createTuples">

### createTuples

Default value: `false`

Type: [Boolean](../converter/java.lang.Boolean)

Allow to create tuples.

<a name="deleteElements">

### deleteElements

Default value: `false`

Type: [Boolean](../converter/java.lang.Boolean)

Allow to delete elements.

<a name="removeFromLayer">

### removeFromLayer

Default value: `false`

Type: [Boolean](../converter/java.lang.Boolean)

Allow to remove annotations from layers.

<a name="setArguments">

### setArguments

Default value: `false`

Type: [Boolean](../converter/java.lang.Boolean)

Allow to set tuple arguments.

<a name="setFeatures">

### setFeatures

Default value: `false`

Type: [Boolean](../converter/java.lang.Boolean)

Allow to set element features.

