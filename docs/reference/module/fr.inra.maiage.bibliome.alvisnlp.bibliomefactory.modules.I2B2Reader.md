<h1 class="module">I2B2Reader</h1>

## Synopsis

*I2B2Reader*reads files in the format of the [I2B2]() challenge.

**This module is experimental.**

## Description

*I2B2Reader*reads documents in [I2B2 challenge]() including the text of documents, tokenization as annotations, concepts as annotations, assertions as annotation features and relations as tuples.

## Snippet



```xml
<i2b2reader class="I2B2Reader>
    <textDir></textDir>
</i2b2reader>
```

## Mandatory parameters

<h3 id="textDir" class="param">textDir</h3>

<div class="param-level param-level-mandatory">Mandatory
</div>
<div class="param-type">Type: <a href="../converter/fr.inra.maiage.bibliome.util.files.InputDirectory" class="converter">InputDirectory</a>
</div>
Path to the directory containing I2B2 text files.

## Optional parameters

<h3 id="assertionsDir" class="param">assertionsDir</h3>

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/fr.inra.maiage.bibliome.util.files.InputDirectory" class="converter">InputDirectory</a>
</div>
Path to the directory where assertion files can be found.

<h3 id="conceptsDir" class="param">conceptsDir</h3>

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/fr.inra.maiage.bibliome.util.files.InputDirectory" class="converter">InputDirectory</a>
</div>
Path to the directory that contains concept annotations.

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

<h3 id="relationsDir" class="param">relationsDir</h3>

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/fr.inra.maiage.bibliome.util.files.InputDirectory" class="converter">InputDirectory</a>
</div>
Path to the directory where relation files can be found.

<h3 id="assertionFeature" class="param">assertionFeature</h3>

<div class="param-level param-level-default-value">Default value: `assertion`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>


<h3 id="conceptTypeFeature" class="param">conceptTypeFeature</h3>

<div class="param-level param-level-default-value">Default value: `type`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Feature where to store the concept type.

<h3 id="conceptsLayerName" class="param">conceptsLayerName</h3>

<div class="param-level param-level-default-value">Default value: `concepts`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Name of the layer where to store concepts annotations.

<h3 id="leftRole" class="param">leftRole</h3>

<div class="param-level param-level-default-value">Default value: `left`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Name of the left argument of relations.

<h3 id="linenoFeature" class="param">linenoFeature</h3>

<div class="param-level param-level-default-value">Default value: `lineno`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Name of the feature where to store the line number.

<h3 id="linesLayerName" class="param">linesLayerName</h3>

<div class="param-level param-level-default-value">Default value: `lines`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Name of the layer where to store lines.

<h3 id="rightRole" class="param">rightRole</h3>

<div class="param-level param-level-default-value">Default value: `right`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>


<h3 id="sectionName" class="param">sectionName</h3>

<div class="param-level param-level-default-value">Default value: `text`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Name of the unique section of each document.

<h3 id="tokenNumberFeature" class="param">tokenNumberFeature</h3>

<div class="param-level param-level-default-value">Default value: `tokenno`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Feature where to store the token index.

<h3 id="tokensLayerName" class="param">tokensLayerName</h3>

<div class="param-level param-level-default-value">Default value: `tokens`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Name of the layer where to store tokens.

