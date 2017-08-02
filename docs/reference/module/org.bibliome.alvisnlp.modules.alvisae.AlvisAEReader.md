<h1 class="module">AlvisAEReader</h1>

## Synopsis

*AlvisAEReader* reads documents and annotations from an AlvisAE campaign.

## Description

*AlvisAEReader* reads an AlvisAE server database and imports documents and annotation sets from an annotation campaign. The database connection is specified with <a href="#url" class="param">url</a>, <a href="#username" class="param">username</a>, <a href="#password" class="param">password</a> and <a href="#schema" class="param">schema</a>. The <a href="#campaignId" class="param">campaignId</a> parameter specifies the campaign identifier in the AlvisAE database (the AlvisAE client displays this identifier).


	All AlvisAE annotations, including text-bound annotations, will be represented in AlvisNLP/ML as relation tuples, in a relation named after the annotation type.
	
* For *text-bound* annotations, each fragment is represented in an annotation stored in the layer <a href="#fragmentsLayerName" class="param">fragmentsLayerName</a>. The tuple references the fragments through its arguments; their role name is <a href="#fragmentRolePrefix" class="param">fragmentRolePrefix</a> with the fragment order appended (starting at zero). Thus a single-fragment annotation will have a single argument with role `frag0`. The type of the annotation is stored in the feature <a href="#typeFeature" class="param">typeFeature</a> of the tuple and of each fragment.
* A *group* tuple references its items through its arguments; their role name is <a href="#itemRolePrefix" class="param">itemRolePrefix</a> with the item order appended (starting at zero).
* A *relation* tuple hareferences its arguments in a straightforward way.





## Parameters

<a name="campaignId">

### campaignId

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/java.lang.Integer" class="converter">Integer</a>
</div>
Identifier of the AlvisAE campaign to import.

<a name="password">

### password

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
User password for JDBC connection.

<a name="schema">

### schema

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
PostgreSQL schema.

<a name="url">

### url

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
PostgreSQL database URL.

<a name="username">

### username

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
PostgreSQL user name.

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

<a name="docDescriptions">

### docDescriptions

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String[]" class="converter">String[]</a>
</div>
If set, *AlvisAEReader* imports only documents whose description is included in the value. If not set, then *AlvisAEReader* imports all documents.

<a name="docExternalIds">

### docExternalIds

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String[]" class="converter">String[]</a>
</div>
If set, *AlvisAEReader* imports only documents whose external id is included in the value. If not set, then *AlvisAEReader* imports all documents.

<a name="docIds">

### docIds

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/java.lang.Integer[]" class="converter">Integer[]</a>
</div>
If set, *AlvisAEReader* imports only documents whose external id is included in the value. If not set, then *AlvisAEReader* imports all documents.

<a name="taskFeature">

### taskFeature

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Name of the feature where to store the name task where the annotation belongs.

<a name="taskId">

### taskId

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/java.lang.Integer" class="converter">Integer</a>
</div>
If set, *AlvisAEReader* imports only annotation sets of tasks whose id is included in the value. If this parameter and <a href="#taskName" class="param">taskName</a> are not set, *AlvisAEReader* imports annotations of all tasks.

<a name="taskIdFeature">

### taskIdFeature

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Feature where to store the task identifier from which the annotation was imported.

<a name="taskName">

### taskName

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
If set, *AlvisAEReader* imports only annotation sets of tasks whose name is included in the value. If this parameter and <a href="#taskId" class="param">taskId</a> are not set, *AlvisAEReader* imports annotations of all tasks.

<a name="userFeature">

### userFeature

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Name of the feature where to store the name of the AlvisAE user that created the annotation.

<a name="userIdFeature">

### userIdFeature

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Feature where to store the user identifier from which the annotation was imported.

<a name="userIds">

### userIds

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/java.lang.Integer[]" class="converter">Integer[]</a>
</div>
If set, *AlvisAEReader* imports only annotation sets created by an user whose id is included in the value. If this parameter and <a href="#userNames" class="param">userNames</a> are not set, *AlvisAEReader* imports annotations of all users.

<a name="userNames">

### userNames

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String[]" class="converter">String[]</a>
</div>
If set, *AlvisAEReader* imports only annotation sets created by an user included in the value. If this parameter and <a href="#userIds" class="param">userIds</a> are not set, *AlvisAEReader* imports annotations of all users.

<a name="adjudicate">

### adjudicate

<div class="param-level param-level-default-value">Default value: `false`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.Boolean" class="converter">Boolean</a>
</div>
Either to automatically adjudicate identical source annotations, implies <a href="#loadDependencies" class="param">loadDependencies</a>.

<a name="annotationIdFeature">

### annotationIdFeature

<div class="param-level param-level-default-value">Default value: `id`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Name of the feature where to store AlvisAE identifier of the annotation.

<a name="annotationSetIdFeature">

### annotationSetIdFeature

<div class="param-level param-level-default-value">Default value: `annotation-set`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Name of the feature where to store the identifier of the annotation set to which belongs the annotation.

<a name="createdFeature">

### createdFeature

<div class="param-level param-level-default-value">Default value: `created`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Feature containing the annotation creation date.

<a name="descriptionFeature">

### descriptionFeature

<div class="param-level param-level-default-value">Default value: `description`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Feature containing the document description.

<a name="externalIdFeature">

### externalIdFeature

<div class="param-level param-level-default-value">Default value: `external-id`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Feature containing the document external id.

<a name="fragmentRolePrefix">

### fragmentRolePrefix

<div class="param-level param-level-default-value">Default value: `frag`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
For tuples that represent text-bound annotations, prefix of the role of fragment arguments.

<a name="fragmentTypeFeature">

### fragmentTypeFeature

<div class="param-level param-level-default-value">Default value: `type`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
In annotations that represent text-bound fragments, name of the feature where to store the type of the annotation.

<a name="fragmentsLayerName">

### fragmentsLayerName

<div class="param-level param-level-default-value">Default value: `alvisae`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Name of the layer where to store text-bound annotation fragments.

<a name="head">

### head

<div class="param-level param-level-default-value">Default value: `true`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.Boolean" class="converter">Boolean</a>
</div>
If `true`, then *AlvisAEReader* imports the *head* annotation set. If `false`, then *AlvisAEReader* imports the annotation set with version 1.

<a name="htmlLayerName">

### htmlLayerName

<div class="param-level param-level-default-value">Default value: `html`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Name of the layer where to store annotations that represent HTML tags.

<a name="htmlTagFeature">

### htmlTagFeature

<div class="param-level param-level-default-value">Default value: `tag`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Feature where to store HTML tag name for annotations imported from the HTML annotation set.

<a name="itemRolePrefix">

### itemRolePrefix

<div class="param-level param-level-default-value">Default value: `item`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Prefix of the roles of arguments that represent group items.

<a name="kindFeature">

### kindFeature

<div class="param-level param-level-default-value">Default value: `kind`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Name of the relation feature containing the annotation kind (values are: *text-bound*, *group*, or *relation*).

<a name="loadDependencies">

### loadDependencies

<div class="param-level param-level-default-value">Default value: `false`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.Boolean" class="converter">Boolean</a>
</div>
Either to load dependencies if the annotation set task is a review.

<a name="loadGroups">

### loadGroups

<div class="param-level param-level-default-value">Default value: `true`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.Boolean" class="converter">Boolean</a>
</div>
Either to import group annotations.

<a name="loadRelations">

### loadRelations

<div class="param-level param-level-default-value">Default value: `true`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.Boolean" class="converter">Boolean</a>
</div>
Either to import relation annotations.

<a name="loadTextBound">

### loadTextBound

<div class="param-level param-level-default-value">Default value: `true`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.Boolean" class="converter">Boolean</a>
</div>
Either to import text-bound annotations.

<a name="oldModel">

### oldModel

<div class="param-level param-level-default-value">Default value: `false`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.Boolean" class="converter">Boolean</a>
</div>
Support database model for AlvisAE v0.3.

<a name="referentFeature">

### referentFeature

<div class="param-level param-level-default-value">Default value: `referent`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Name of the feature where to store back-reference of sources.

<a name="sectionName">

### sectionName

<div class="param-level param-level-default-value">Default value: `alvisae`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Name of the unique section created in each document.

<a name="sourceRolePrefix">

### sourceRolePrefix

<div class="param-level param-level-default-value">Default value: `source`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Prefix for the roles for source annotations, will only be used if <a href="#loadDependencies" class="param">loadDependencies</a> is true.

<a name="typeFeature">

### typeFeature

<div class="param-level param-level-default-value">Default value: `type`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Feature that contains the type of the annotation.

<a name="unmatchedFeature">

### unmatchedFeature

<div class="param-level param-level-default-value">Default value: `unmatched`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Feature where to store the AlvisAE identifiers of unmatched annotations (for review annotation sets).

