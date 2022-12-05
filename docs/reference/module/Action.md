<h1 class="module">Action</h1>

## Synopsis

Applies action expressions on selected elements.

## Description

 *Action* evaluates <a href="#commit" class="param">commit</a> as a list of elements, then <a href="#target" class="param">target</a> as a list of elements on each commit element, then <a href="#action" class="param">action</a> on each target element.

 *Action* is useful when <a href="#action" class="param">action</a> is a side-effect expression. The side-effect expressions allowed are controlled by: <a href="#createDocuments" class="param">createDocuments</a> , <a href="#createSections" class="param">createSections</a> , <a href="#createRelations" class="param">createRelations</a> , <a href="#createTuples" class="param">createTuples</a> , <a href="#createAnnotations" class="param">createAnnotations</a> , <a href="#setArguments" class="param">setArguments</a> , <a href="#setFeatures" class="param">setFeatures</a> and <a href="#deleteElements" class="param">deleteElements</a> . If these parameters are not set to true then *Action* will refuse to evaluate the corresponding side-effect expressions.

 *Action* commits the changes on the data structure when each <a href="#commit" class="param">commit</a> element is done. When processing very large corpora, set <a href="#commit" class="param">commit</a> to reach documents or sections.

## Snippet



<button class="copy-code-button" title="Copy to clipboard" onclick="copy_code(this)">📋</button>
```xml
<action class="Action>
    <action></action>
    <target></target>
</action>
```

## Mandatory parameters

<h3 id="action" class="param">action</h3>

<div class="param-level param-level-mandatory">Mandatory
</div>
<div class="param-type">Type: <a href="../converter/fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.Expression" class="converter">Expression</a>
</div>
Action to perform on each result of <a href="#target" class="param">target</a> .

<h3 id="target" class="param">target</h3>

<div class="param-level param-level-mandatory">Mandatory
</div>
<div class="param-type">Type: <a href="../converter/fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.Expression" class="converter">Expression</a>
</div>
Elements on which to perform the action. The expression is evaluated as a list of elements with the commit element as the context element.

## Optional parameters

<h3 id="constantAnnotationFeatures" class="param">constantAnnotationFeatures</h3>

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/fr.inra.maiage.bibliome.alvisnlp.core.module.types.Mapping" class="converter">Mapping</a>
</div>
Constant features to add to each annotation created by this module.

<h3 id="constantDocumentFeatures" class="param">constantDocumentFeatures</h3>

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/fr.inra.maiage.bibliome.alvisnlp.core.module.types.Mapping" class="converter">Mapping</a>
</div>
Constant features to add to each document created by this module.

<h3 id="constantRelationFeatures" class="param">constantRelationFeatures</h3>

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/fr.inra.maiage.bibliome.alvisnlp.core.module.types.Mapping" class="converter">Mapping</a>
</div>
Constant features to add to each relation created by this module.

<h3 id="constantSectionFeatures" class="param">constantSectionFeatures</h3>

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/fr.inra.maiage.bibliome.alvisnlp.core.module.types.Mapping" class="converter">Mapping</a>
</div>
Constant features to add to each section created by this module.

<h3 id="constantTupleFeatures" class="param">constantTupleFeatures</h3>

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/fr.inra.maiage.bibliome.alvisnlp.core.module.types.Mapping" class="converter">Mapping</a>
</div>
Constant features to add to each tuple created by this module.

<h3 id="addToLayer" class="param">addToLayer</h3>

<div class="param-level param-level-default-value">Default value: `false`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.Boolean" class="converter">Boolean</a>
</div>
Allow to add annotations to layers.

<h3 id="commit" class="param">commit</h3>

<div class="param-level param-level-default-value">Default value: `$`
</div>
<div class="param-type">Type: <a href="../converter/fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.Expression" class="converter">Expression</a>
</div>
Elements after which to commit changes. The expression is evaluated as a list of elements with the corpus as the context element.

<h3 id="createAnnotations" class="param">createAnnotations</h3>

<div class="param-level param-level-default-value">Default value: `false`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.Boolean" class="converter">Boolean</a>
</div>
Allow the creation of annotations.

<h3 id="createDocuments" class="param">createDocuments</h3>

<div class="param-level param-level-default-value">Default value: `false`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.Boolean" class="converter">Boolean</a>
</div>
Allow the creation of documents.

<h3 id="createRelations" class="param">createRelations</h3>

<div class="param-level param-level-default-value">Default value: `false`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.Boolean" class="converter">Boolean</a>
</div>
Allow the creation of relations.

<h3 id="createSections" class="param">createSections</h3>

<div class="param-level param-level-default-value">Default value: `false`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.Boolean" class="converter">Boolean</a>
</div>
Allow the creation of sections.

<h3 id="createTuples" class="param">createTuples</h3>

<div class="param-level param-level-default-value">Default value: `false`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.Boolean" class="converter">Boolean</a>
</div>
Allow to create tuples.

<h3 id="deleteElements" class="param">deleteElements</h3>

<div class="param-level param-level-default-value">Default value: `false`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.Boolean" class="converter">Boolean</a>
</div>
Allow to delete elements.

<h3 id="removeFromLayer" class="param">removeFromLayer</h3>

<div class="param-level param-level-default-value">Default value: `false`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.Boolean" class="converter">Boolean</a>
</div>
Allow to remove annotations from layers.

<h3 id="setArguments" class="param">setArguments</h3>

<div class="param-level param-level-default-value">Default value: `false`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.Boolean" class="converter">Boolean</a>
</div>
Allow to set tuple arguments.

<h3 id="setFeatures" class="param">setFeatures</h3>

<div class="param-level param-level-default-value">Default value: `false`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.Boolean" class="converter">Boolean</a>
</div>
Allow to set element features.

## Deprecated parameters

