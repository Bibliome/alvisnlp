<h1 class="module">PESVReader</h1>

## Synopsis

Read documents and entities in the PESV format.

## Description

 *PESVReader* reads CSV files in <a href="#docStream" class="param">docStream</a> and creates one document for each record. The identifier of the document is the *id* column. The section content is created from the tokenization provided in the *processed_text* column. The tokenization itself is recorded in the layer named after <a href="#tokenLayer" class="param">tokenLayer</a> .

 *PESVReader* also reads CSV files in <a href="#entitiesStream" class="param">entitiesStream</a> and creates one entity annotation in the layer named <a href="#entityLayer" class="param">entityLayer</a> for each record. All properties are recorded in the corresponding feature, as well as in a single feature names <a href="#propertiesFeature" class="param">propertiesFeature</a> .

## Snippet



<button class="copy-code-button" title="Copy to clipboard" onclick="copy_code(this)">📋</button>
```xml
<pesvreader class="PESVReader">
    <docStream></docStream>
    <entitiesStream></entitiesStream>
</pesvreader>
```

## Mandatory parameters

<h3 id="docStream" class="param">docStream</h3>

<div class="param-level param-level-mandatory">Mandatory
</div>
<div class="param-type">Type: <a href="../converter/fr.inra.maiage.bibliome.util.streams.SourceStream" class="converter">SourceStream</a>
</div>
Path to the file(s) or directory(ies) where to look for document files.

<h3 id="entitiesStream" class="param">entitiesStream</h3>

<div class="param-level param-level-mandatory">Mandatory
</div>
<div class="param-type">Type: <a href="../converter/fr.inra.maiage.bibliome.util.streams.SourceStream" class="converter">SourceStream</a>
</div>
Path to the file(s) or directory(ies) where to look for entities files.

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

<h3 id="constantSectionFeatures" class="param">constantSectionFeatures</h3>

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/fr.inra.maiage.bibliome.alvisnlp.core.module.types.Mapping" class="converter">Mapping</a>
</div>
Constant features to add to each section created by this module.

<h3 id="entityLayer" class="param">entityLayer</h3>

<div class="param-level param-level-default-value">Default value: `entities`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Name of the layer where to create entities.

<h3 id="ordFeature" class="param">ordFeature</h3>

<div class="param-level param-level-default-value">Default value: `ord`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Name of the feature where to record the token ordinal.

<h3 id="propertiesFeature" class="param">propertiesFeature</h3>

<div class="param-level param-level-default-value">Default value: `properties`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Name of the feature where to record entities properties. *PESVReader* also records each property in a separate feature.

<h3 id="section" class="param">section</h3>

<div class="param-level param-level-default-value">Default value: `text`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Name of the (unique) section.

<h3 id="tokenLayer" class="param">tokenLayer</h3>

<div class="param-level param-level-default-value">Default value: `tokens`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Name of the layer where to create tokens.

## Deprecated parameters

<h3 id="entityLayerName" class="param">entityLayerName</h3>

<div class="param-level param-level-deprecated">Deprecated
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Deprecated alias for <a href="#entityLayer" class="param">entityLayer</a> .

<h3 id="ordFeatureKey" class="param">ordFeatureKey</h3>

<div class="param-level param-level-deprecated">Deprecated
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Deprecated alias for <a href="#ordFeature" class="param">ordFeature</a> .

<h3 id="propertiesFeatureKey" class="param">propertiesFeatureKey</h3>

<div class="param-level param-level-deprecated">Deprecated
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Deprecated alias for <a href="#propertiesFeature" class="param">propertiesFeature</a> .

<h3 id="sectionName" class="param">sectionName</h3>

<div class="param-level param-level-deprecated">Deprecated
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Deprecated alias for <a href="#section" class="param">section</a> .

<h3 id="tokenLayerName" class="param">tokenLayerName</h3>

<div class="param-level param-level-deprecated">Deprecated
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Deprecated alias for <a href="#tokenLayer" class="param">tokenLayer</a> .

