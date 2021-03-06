<h1 class="module">Chemspot</h1>

## Synopsis

Looks for mentions of chemicals and molecule names using [Chemspot](https://www.informatik.hu-berlin.de/de/forschung/gebiete/wbi/resources/chemspot/chemspot).

**This module is experimental.**

## Description

*Chemspot* runs Chemspot on the sections content, then creates an annotation for each chemical mention in the layer <a href="#targetLayerName" class="param">targetLayerName</a>.
	Each annotation will have the feature <a href="#chemTypeFeatureName" class="param">chemTypeFeatureName</a> set to the chemical mention type, and one feature for each type of identifier.
  

## Parameters

<h3 name="chemspotDir" class="param">chemspotDir</h3>

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/fr.inra.maiage.bibliome.util.files.InputDirectory" class="converter">InputDirectory</a>
</div>
Chemspot install directory.

<h3 name="constantAnnotationFeatures" class="param">constantAnnotationFeatures</h3>

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/fr.inra.maiage.bibliome.alvisnlp.core.module.types.Mapping" class="converter">Mapping</a>
</div>
Constant features to add to each annotation created by this module.

<h3 name="casFeatureName" class="param">casFeatureName</h3>

<div class="param-level param-level-default-value">Default value: `CAS`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Feature where to store the CAS identifier.

<h3 name="chebFeatureName" class="param">chebFeatureName</h3>

<div class="param-level param-level-default-value">Default value: `CHEB`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Feature where to store the CHEB identifier.

<h3 name="chemTypeFeatureName" class="param">chemTypeFeatureName</h3>

<div class="param-level param-level-default-value">Default value: `chem-type`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Feature where to store the chemical type (SYSTEMATIC, IDENTIFIER, FORMULA, TRIVIAL, ABBREVIATION, FAMILY, MULTIPLE, UNKNOWN).

<h3 name="chidFeatureName" class="param">chidFeatureName</h3>

<div class="param-level param-level-default-value">Default value: `CHID`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Feature where to store the CHID identifier.

<h3 name="documentFilter" class="param">documentFilter</h3>

<div class="param-level param-level-default-value">Default value: `true`
</div>
<div class="param-type">Type: <a href="../converter/fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.Expression" class="converter">Expression</a>
</div>
Only process document that satisfy this expression.

<h3 name="drugFeatureName" class="param">drugFeatureName</h3>

<div class="param-level param-level-default-value">Default value: `DRUG`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Feature where to store the DRUG identifier.

<h3 name="fdaDateFeatureName" class="param">fdaDateFeatureName</h3>

<div class="param-level param-level-default-value">Default value: `FDA`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Feature where to store the FDA_DATE identifier.

<h3 name="fdaFeatureName" class="param">fdaFeatureName</h3>

<div class="param-level param-level-default-value">Default value: `FDA_DATE`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Feature where to store the FDA identifier.

<h3 name="hmdbFeatureName" class="param">hmdbFeatureName</h3>

<div class="param-level param-level-default-value">Default value: `HMDB`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Feature where to store the HMBD identifier.

<h3 name="inchFeatureName" class="param">inchFeatureName</h3>

<div class="param-level param-level-default-value">Default value: `INCH`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Feature where to store the INCH identifier.

<h3 name="javaHome" class="param">javaHome</h3>

<div class="param-level param-level-default-value">Default value: `/home/rbossy/dist/jdk1.8.0_121`
</div>
<div class="param-type">Type: <a href="../converter/fr.inra.maiage.bibliome.util.files.InputDirectory" class="converter">InputDirectory</a>
</div>
Root directory of the Java implementation.

<h3 name="kegdFeatureName" class="param">kegdFeatureName</h3>

<div class="param-level param-level-default-value">Default value: `KEGD`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Feature where to store the KEGD identifier.

<h3 name="keggFeatureName" class="param">keggFeatureName</h3>

<div class="param-level param-level-default-value">Default value: `KEGG`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Feature where to store the KEGG identifier.

<h3 name="meshFeatureName" class="param">meshFeatureName</h3>

<div class="param-level param-level-default-value">Default value: `MESH`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Feature where to store the MESH identifier.

<h3 name="noDict" class="param">noDict</h3>

<div class="param-level param-level-default-value">Default value: `false`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.Boolean" class="converter">Boolean</a>
</div>
Do not use lexicons, only the CRF classifier. Uses less memory.

<h3 name="pubcFeatureName" class="param">pubcFeatureName</h3>

<div class="param-level param-level-default-value">Default value: `PUBC`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Feature where to store the PUBC identifier.

<h3 name="pubsFeatureName" class="param">pubsFeatureName</h3>

<div class="param-level param-level-default-value">Default value: `PUBS`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Feature where to store the PUBS identifier.

<h3 name="sectionFilter" class="param">sectionFilter</h3>

<div class="param-level param-level-default-value">Default value: `true`
</div>
<div class="param-type">Type: <a href="../converter/fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.Expression" class="converter">Expression</a>
</div>
Process only sections that satisfy this expression.

<h3 name="targetLayerName" class="param">targetLayerName</h3>

<div class="param-level param-level-default-value">Default value: `chemspot`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Name of the layer where to store annotations created by *Chemspot*.

