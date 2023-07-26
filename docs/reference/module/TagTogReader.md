<h1 class="module">TagTogReader</h1>

## Synopsis

Reads documents and annotations from [TagTog](https://www.tagtog.com/)  [JSON anndoc format](https://docs.tagtog.com/anndoc.html) .

**This module is experimental.**

## Description

 *TagTogReader* reads a ZIP archive exported by [TagTog](https://www.tagtog.com/) in [JSON anndoc format](https://docs.tagtog.com/anndoc.html) . The archive must be downloaded by exporting a TagTog project.

## Snippet



<button class="copy-code-button" title="Copy to clipboard" onclick="copy_code(this)">📋</button>
```xml
<tagtogreader class="TagTogReader">
    <zipFile></zipFile>
</tagtogreader>
```

## Mandatory parameters

<h3 id="zipFile" class="param">zipFile</h3>

<div class="param-level param-level-mandatory">Mandatory
</div>
<div class="param-type">Type: <a href="../converter/fr.inra.maiage.bibliome.util.files.InputFile" class="converter">InputFile</a>
</div>
Path to TagTog's export archive.

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

<h3 id="annotatorFeature" class="param">annotatorFeature</h3>

<div class="param-level param-level-default-value">Default value: `annotator`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Feature where to store the name of the annotator that created the entity or relation in TagTog.

<h3 id="argumentPrefix" class="param">argumentPrefix</h3>

<div class="param-level param-level-default-value">Default value: `arg`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Prefix of the roles of arguments of relations.

<h3 id="entitiesLayer" class="param">entitiesLayer</h3>

<div class="param-level param-level-default-value">Default value: `entities`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Layer where to place entity annotations.

<h3 id="relation" class="param">relation</h3>

<div class="param-level param-level-default-value">Default value: `relations`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Relation where to place TagTog relations.

<h3 id="typeFeature" class="param">typeFeature</h3>

<div class="param-level param-level-default-value">Default value: `type`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Feature where to store entity and relation types.

## Deprecated parameters

