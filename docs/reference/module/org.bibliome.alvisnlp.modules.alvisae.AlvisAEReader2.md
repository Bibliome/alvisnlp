# org.bibliome.alvisnlp.modules.alvisae.AlvisAEReader2

## Synopsis

Deprecated alias for [AlvisAEReader](../module/AlvisAEReader).

**This module is obsolete, superceded by org.bibliome.alvisnlp.modules.alvisae.AlvisAEReader**

## Description

## Parameters

<a name="campaignId">

### campaignId

Optional

Type: [Integer](../converter/java.lang.Integer)

Identifier of the AlvisAE campaign to import.

<a name="password">

### password

Optional

Type: [String](../converter/java.lang.String)

User password for JDBC connection.

<a name="schema">

### schema

Optional

Type: [String](../converter/java.lang.String)

PostgreSQL schema.

<a name="url">

### url

Optional

Type: [String](../converter/java.lang.String)

PostgreSQL database URL.

<a name="username">

### username

Optional

Type: [String](../converter/java.lang.String)

PostgreSQL user name.

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

<a name="docDescriptions">

### docDescriptions

Optional

Type: [String[]](../converter/java.lang.String[])

If set, *org.bibliome.alvisnlp.modules.alvisae.AlvisAEReader2* imports only documents whose description is included in the value. If not set, then *org.bibliome.alvisnlp.modules.alvisae.AlvisAEReader2* imports all documents.

<a name="docExternalIds">

### docExternalIds

Optional

Type: [String[]](../converter/java.lang.String[])

If set, *org.bibliome.alvisnlp.modules.alvisae.AlvisAEReader2* imports only documents whose external id is included in the value. If not set, then *org.bibliome.alvisnlp.modules.alvisae.AlvisAEReader2* imports all documents.

<a name="docIds">

### docIds

Optional

Type: [Integer[]](../converter/java.lang.Integer[])

If set, *org.bibliome.alvisnlp.modules.alvisae.AlvisAEReader2* imports only documents whose external id is included in the value. If not set, then *org.bibliome.alvisnlp.modules.alvisae.AlvisAEReader2* imports all documents.

<a name="taskFeature">

### taskFeature

Optional

Type: [String](../converter/java.lang.String)

Name of the feature where to store the name task where the annotation belongs.

<a name="taskId">

### taskId

Optional

Type: [Integer](../converter/java.lang.Integer)

If set, *org.bibliome.alvisnlp.modules.alvisae.AlvisAEReader2* imports only annotation sets of tasks whose id is included in the value. If this parameter and [taskName](#taskName) are not set, *org.bibliome.alvisnlp.modules.alvisae.AlvisAEReader2* imports annotations of all tasks.

<a name="taskIdFeature">

### taskIdFeature

Optional

Type: [String](../converter/java.lang.String)

Feature where to store the task identifier from which the annotation was imported.

<a name="taskName">

### taskName

Optional

Type: [String](../converter/java.lang.String)

If set, *org.bibliome.alvisnlp.modules.alvisae.AlvisAEReader2* imports only annotation sets of tasks whose name is included in the value. If this parameter and [taskId](#taskId) are not set, *org.bibliome.alvisnlp.modules.alvisae.AlvisAEReader2* imports annotations of all tasks.

<a name="userFeature">

### userFeature

Optional

Type: [String](../converter/java.lang.String)

Name of the feature where to store the name of the AlvisAE user that created the annotation.

<a name="userIdFeature">

### userIdFeature

Optional

Type: [String](../converter/java.lang.String)

Feature where to store the user identifier from which the annotation was imported.

<a name="userIds">

### userIds

Optional

Type: [Integer[]](../converter/java.lang.Integer[])

If set, *org.bibliome.alvisnlp.modules.alvisae.AlvisAEReader2* imports only annotation sets created by an user whose id is included in the value. If this parameter and [userNames](#userNames) are not set, *org.bibliome.alvisnlp.modules.alvisae.AlvisAEReader2* imports annotations of all users.

<a name="userNames">

### userNames

Optional

Type: [String[]](../converter/java.lang.String[])

If set, *org.bibliome.alvisnlp.modules.alvisae.AlvisAEReader2* imports only annotation sets created by an user included in the value. If this parameter and [userIds](#userIds) are not set, *org.bibliome.alvisnlp.modules.alvisae.AlvisAEReader2* imports annotations of all users.

<a name="adjudicate">

### adjudicate

Default value: `false`

Type: [Boolean](../converter/java.lang.Boolean)

Either to automatically adjudicate identical source annotations, implies [loadDependencies](#loadDependencies).

<a name="annotationIdFeature">

### annotationIdFeature

Default value: `id`

Type: [String](../converter/java.lang.String)

Name of the feature where to store AlvisAE identifier of the annotation.

<a name="annotationSetIdFeature">

### annotationSetIdFeature

Default value: `annotation-set`

Type: [String](../converter/java.lang.String)

Name of the feature where to store the identifier of the annotation set to which belongs the annotation.

<a name="createdFeature">

### createdFeature

Default value: `created`

Type: [String](../converter/java.lang.String)

Feature containing the annotation creation date.

<a name="descriptionFeature">

### descriptionFeature

Default value: `description`

Type: [String](../converter/java.lang.String)

Feature containing the document description.

<a name="externalIdFeature">

### externalIdFeature

Default value: `external-id`

Type: [String](../converter/java.lang.String)

Feature containing the document external id.

<a name="fragmentRolePrefix">

### fragmentRolePrefix

Default value: `frag`

Type: [String](../converter/java.lang.String)

For tuples that represent text-bound annotations, prefix of the role of fragment arguments.

<a name="fragmentTypeFeature">

### fragmentTypeFeature

Default value: `type`

Type: [String](../converter/java.lang.String)

In annotations that represent text-bound fragments, name of the feature where to store the type of the annotation.

<a name="fragmentsLayerName">

### fragmentsLayerName

Default value: `alvisae`

Type: [String](../converter/java.lang.String)

Name of the layer where to store text-bound annotation fragments.

<a name="head">

### head

Default value: `true`

Type: [Boolean](../converter/java.lang.Boolean)

If `true`, then *org.bibliome.alvisnlp.modules.alvisae.AlvisAEReader2* imports the *head* annotation set. If `false`, then *org.bibliome.alvisnlp.modules.alvisae.AlvisAEReader2* imports the annotation set with version 1.

<a name="htmlLayerName">

### htmlLayerName

Default value: `html`

Type: [String](../converter/java.lang.String)

Name of the layer where to store annotations that represent HTML tags.

<a name="htmlTagFeature">

### htmlTagFeature

Default value: `tag`

Type: [String](../converter/java.lang.String)

Feature where to store HTML tag name for annotations imported from the HTML annotation set.

<a name="itemRolePrefix">

### itemRolePrefix

Default value: `item`

Type: [String](../converter/java.lang.String)

Prefix of the roles of arguments that represent group items.

<a name="kindFeature">

### kindFeature

Default value: `kind`

Type: [String](../converter/java.lang.String)

Name of the relation feature containing the annotation kind (values are: *text-bound*, *group*, or *relation*).

<a name="loadDependencies">

### loadDependencies

Default value: `false`

Type: [Boolean](../converter/java.lang.Boolean)

Either to load dependencies if the annotation set task is a review.

<a name="loadGroups">

### loadGroups

Default value: `true`

Type: [Boolean](../converter/java.lang.Boolean)

Either to import group annotations.

<a name="loadRelations">

### loadRelations

Default value: `true`

Type: [Boolean](../converter/java.lang.Boolean)

Either to import relation annotations.

<a name="loadTextBound">

### loadTextBound

Default value: `true`

Type: [Boolean](../converter/java.lang.Boolean)

Either to import text-bound annotations.

<a name="oldModel">

### oldModel

Default value: `false`

Type: [Boolean](../converter/java.lang.Boolean)

Support database model for AlvisAE v0.3.

<a name="referentFeature">

### referentFeature

Default value: `referent`

Type: [String](../converter/java.lang.String)

Name of the feature where to store back-reference of sources.

<a name="sectionName">

### sectionName

Default value: `alvisae`

Type: [String](../converter/java.lang.String)

Name of the unique section created in each document.

<a name="sourceRolePrefix">

### sourceRolePrefix

Default value: `source`

Type: [String](../converter/java.lang.String)

Prefix for the roles for source annotations, will only be used if [loadDependencies](#loadDependencies) is true.

<a name="typeFeature">

### typeFeature

Default value: `type`

Type: [String](../converter/java.lang.String)

Feature that contains the type of the annotation.

<a name="unmatchedFeature">

### unmatchedFeature

Default value: `unmatched`

Type: [String](../converter/java.lang.String)

Feature where to store the AlvisAE identifiers of unmatched annotations (for review annotation sets).

