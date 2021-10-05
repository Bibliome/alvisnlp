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

<h3 name="campaignId" class="param">campaignId</h3>

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/java.lang.Integer" class="converter">Integer</a>
</div>
Identifier of the AlvisAE campaign to import.

<h3 name="password" class="param">password</h3>

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
User password for JDBC connection.

<h3 name="schema" class="param">schema</h3>

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
PostgreSQL schema.

<h3 name="url" class="param">url</h3>

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
PostgreSQL database URL.

<h3 name="username" class="param">username</h3>

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
PostgreSQL user name.

<h3 name="constantAnnotationFeatures" class="param">constantAnnotationFeatures</h3>

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/fr.inra.maiage.bibliome.alvisnlp.core.module.types.Mapping" class="converter">Mapping</a>
</div>
Constant features to add to each annotation created by this module.

<h3 name="constantDocumentFeatures" class="param">constantDocumentFeatures</h3>

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/fr.inra.maiage.bibliome.alvisnlp.core.module.types.Mapping" class="converter">Mapping</a>
</div>
Constant features to add to each document created by this module.

<h3 name="constantRelationFeatures" class="param">constantRelationFeatures</h3>

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/fr.inra.maiage.bibliome.alvisnlp.core.module.types.Mapping" class="converter">Mapping</a>
</div>
Constant features to add to each relation created by this module.

<h3 name="constantSectionFeatures" class="param">constantSectionFeatures</h3>

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/fr.inra.maiage.bibliome.alvisnlp.core.module.types.Mapping" class="converter">Mapping</a>
</div>
Constant features to add to each section created by this module.

<h3 name="constantTupleFeatures" class="param">constantTupleFeatures</h3>

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/fr.inra.maiage.bibliome.alvisnlp.core.module.types.Mapping" class="converter">Mapping</a>
</div>
Constant features to add to each tuple created by this module.

<h3 name="docDescriptions" class="param">docDescriptions</h3>

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String%5B%5D" class="converter">String[]</a>
</div>
If set, *AlvisAEReader* imports only documents whose description is included in the value. If not set, then *AlvisAEReader* imports all documents.

<h3 name="docExternalIds" class="param">docExternalIds</h3>

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String%5B%5D" class="converter">String[]</a>
</div>
If set, *AlvisAEReader* imports only documents whose external id is included in the value. If not set, then *AlvisAEReader* imports all documents.

<h3 name="docIds" class="param">docIds</h3>

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/java.lang.Integer%5B%5D" class="converter">Integer[]</a>
</div>
If set, *AlvisAEReader* imports only documents whose external id is included in the value. If not set, then *AlvisAEReader* imports all documents.

<h3 name="taskFeature" class="param">taskFeature</h3>

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Name of the feature where to store the name task where the annotation belongs.

<h3 name="taskId" class="param">taskId</h3>

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/java.lang.Integer" class="converter">Integer</a>
</div>
If set, *AlvisAEReader* imports only annotation sets of tasks whose id is included in the value. If this parameter and <a href="#taskName" class="param">taskName</a> are not set, *AlvisAEReader* imports annotations of all tasks.

<h3 name="taskIdFeature" class="param">taskIdFeature</h3>

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Feature where to store the task identifier from which the annotation was imported.

<h3 name="taskName" class="param">taskName</h3>

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
If set, *AlvisAEReader* imports only annotation sets of tasks whose name is included in the value. If this parameter and <a href="#taskId" class="param">taskId</a> are not set, *AlvisAEReader* imports annotations of all tasks.

<h3 name="userFeature" class="param">userFeature</h3>

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Name of the feature where to store the name of the AlvisAE user that created the annotation.

<h3 name="userIdFeature" class="param">userIdFeature</h3>

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Feature where to store the user identifier from which the annotation was imported.

<h3 name="userIds" class="param">userIds</h3>

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/java.lang.Integer%5B%5D" class="converter">Integer[]</a>
</div>
If set, *AlvisAEReader* imports only annotation sets created by an user whose id is included in the value. If this parameter and <a href="#userNames" class="param">userNames</a> are not set, *AlvisAEReader* imports annotations of all users.

<h3 name="userNames" class="param">userNames</h3>

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String%5B%5D" class="converter">String[]</a>
</div>
If set, *AlvisAEReader* imports only annotation sets created by an user included in the value. If this parameter and <a href="#userIds" class="param">userIds</a> are not set, *AlvisAEReader* imports annotations of all users.

<h3 name="adjudicate" class="param">adjudicate</h3>

<div class="param-level param-level-default-value">Default value: `false`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.Boolean" class="converter">Boolean</a>
</div>
Either to automatically adjudicate identical source annotations, implies <a href="#loadDependencies" class="param">loadDependencies</a>.

<h3 name="annotationIdFeature" class="param">annotationIdFeature</h3>

<div class="param-level param-level-default-value">Default value: `id`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Name of the feature where to store AlvisAE identifier of the annotation.

<h3 name="annotationSetIdFeature" class="param">annotationSetIdFeature</h3>

<div class="param-level param-level-default-value">Default value: `annotation-set`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Name of the feature where to store the identifier of the annotation set to which belongs the annotation.

<h3 name="createdFeature" class="param">createdFeature</h3>

<div class="param-level param-level-default-value">Default value: `created`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Feature containing the annotation creation date.

<h3 name="descriptionFeature" class="param">descriptionFeature</h3>

<div class="param-level param-level-default-value">Default value: `description`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Feature containing the document description.

<h3 name="externalIdFeature" class="param">externalIdFeature</h3>

<div class="param-level param-level-default-value">Default value: `external-id`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Feature containing the document external id.

<h3 name="fragmentRolePrefix" class="param">fragmentRolePrefix</h3>

<div class="param-level param-level-default-value">Default value: `frag`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
For tuples that represent text-bound annotations, prefix of the role of fragment arguments.

<h3 name="fragmentTypeFeature" class="param">fragmentTypeFeature</h3>

<div class="param-level param-level-default-value">Default value: `type`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
In annotations that represent text-bound fragments, name of the feature where to store the type of the annotation.

<h3 name="fragmentsLayerName" class="param">fragmentsLayerName</h3>

<div class="param-level param-level-default-value">Default value: `alvisae`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Name of the layer where to store text-bound annotation fragments.

<h3 name="head" class="param">head</h3>

<div class="param-level param-level-default-value">Default value: `true`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.Boolean" class="converter">Boolean</a>
</div>
If `true`, then *AlvisAEReader* imports the *head* annotation set. If `false`, then *AlvisAEReader* imports the annotation set with version 1.

<h3 name="htmlLayerName" class="param">htmlLayerName</h3>

<div class="param-level param-level-default-value">Default value: `html`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Name of the layer where to store annotations that represent HTML tags.

<h3 name="htmlTagFeature" class="param">htmlTagFeature</h3>

<div class="param-level param-level-default-value">Default value: `tag`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Feature where to store HTML tag name for annotations imported from the HTML annotation set.

<h3 name="itemRolePrefix" class="param">itemRolePrefix</h3>

<div class="param-level param-level-default-value">Default value: `item`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Prefix of the roles of arguments that represent group items.

<h3 name="kindFeature" class="param">kindFeature</h3>

<div class="param-level param-level-default-value">Default value: `kind`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Name of the relation feature containing the annotation kind (values are: *text-bound*, *group*, or *relation*).

<h3 name="loadDependencies" class="param">loadDependencies</h3>

<div class="param-level param-level-default-value">Default value: `false`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.Boolean" class="converter">Boolean</a>
</div>
Either to load dependencies if the annotation set task is a review.

<h3 name="loadGroups" class="param">loadGroups</h3>

<div class="param-level param-level-default-value">Default value: `true`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.Boolean" class="converter">Boolean</a>
</div>
Either to import group annotations.

<h3 name="loadRelations" class="param">loadRelations</h3>

<div class="param-level param-level-default-value">Default value: `true`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.Boolean" class="converter">Boolean</a>
</div>
Either to import relation annotations.

<h3 name="loadTextBound" class="param">loadTextBound</h3>

<div class="param-level param-level-default-value">Default value: `true`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.Boolean" class="converter">Boolean</a>
</div>
Either to import text-bound annotations.

<h3 name="oldModel" class="param">oldModel</h3>

<div class="param-level param-level-default-value">Default value: `false`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.Boolean" class="converter">Boolean</a>
</div>
Support database model for AlvisAE v0.3.

<h3 name="referentFeature" class="param">referentFeature</h3>

<div class="param-level param-level-default-value">Default value: `referent`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Name of the feature where to store back-reference of sources.

<h3 name="sectionName" class="param">sectionName</h3>

<div class="param-level param-level-default-value">Default value: `text`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Name of the unique section created in each document.

<h3 name="sourceRolePrefix" class="param">sourceRolePrefix</h3>

<div class="param-level param-level-default-value">Default value: `source`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Prefix for the roles for source annotations, will only be used if <a href="#loadDependencies" class="param">loadDependencies</a> is true.

<h3 name="typeFeature" class="param">typeFeature</h3>

<div class="param-level param-level-default-value">Default value: `type`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Feature that contains the type of the annotation.

<h3 name="unmatchedFeature" class="param">unmatchedFeature</h3>

<div class="param-level param-level-default-value">Default value: `unmatched`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Feature where to store the AlvisAE identifiers of unmatched annotations (for review annotation sets).

