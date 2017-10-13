<h1 class="module">Chemspot</h1>

## Synopsis

Looks for mentions of chemicals and molecule names using [Chemspot](https://www.informatik.hu-berlin.de/de/forschung/gebiete/wbi/resources/chemspot/chemspot).

**This module is experimental.**

## Description

*Chemspot* runs Chemspot on the sections content, then creates an annotation for each chemical mention in the layer <a href="#targetLayerName" class="param">targetLayerName</a>.
	Each annotation will have the feature <a href="#chemTypeFeatureName" class="param">chemTypeFeatureName</a> set to the chemical mention type, and one feature for each type of identifier.
  

## Parameters

<a name="chemspotDir">

### chemspotDir

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/fr.inra.maiage.bibliome.util.files.InputDirectory" class="converter">InputDirectory</a>
</div>
Chemspot install directory.

<a name="constantAnnotationFeatures">

### constantAnnotationFeatures

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/fr.inra.maiage.bibliome.alvisnlp.core.module.types.Mapping" class="converter">Mapping</a>
</div>
Constant features to add to each annotation created by this module

<a name="casFeatureName">

### casFeatureName

<div class="param-level param-level-default-value">Default value: `CAS`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Feature where to store the CAS identifier.

<a name="chebFeatureName">

### chebFeatureName

<div class="param-level param-level-default-value">Default value: `CHEB`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Feature where to store the CHEB identifier.

<a name="chemTypeFeatureName">

### chemTypeFeatureName

<div class="param-level param-level-default-value">Default value: `chem-type`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Feature where to store the chemical type (SYSTEMATIC, IDENTIFIER, FORMULA, TRIVIAL, ABBREVIATION, FAMILY, MULTIPLE, UNKNOWN).

<a name="chidFeatureName">

### chidFeatureName

<div class="param-level param-level-default-value">Default value: `CHID`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Feature where to store the CHID identifier.

<a name="documentFilter">

### documentFilter

<div class="param-level param-level-default-value">Default value: `true`
</div>
<div class="param-type">Type: <a href="../converter/fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.Expression" class="converter">Expression</a>
</div>
Only process document that satisfy this filter.

<a name="drugFeatureName">

### drugFeatureName

<div class="param-level param-level-default-value">Default value: `DRUG`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Feature where to store the DRUG identifier.

<a name="fdaDateFeatureName">

### fdaDateFeatureName

<div class="param-level param-level-default-value">Default value: `FDA`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Feature where to store the FDA_DATE identifier.

<a name="fdaFeatureName">

### fdaFeatureName

<div class="param-level param-level-default-value">Default value: `FDA_DATE`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Feature where to store the FDA identifier.

<a name="hmdbFeatureName">

### hmdbFeatureName

<div class="param-level param-level-default-value">Default value: `HMDB`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Feature where to store the HMBD identifier.

<a name="inchFeatureName">

### inchFeatureName

<div class="param-level param-level-default-value">Default value: `INCH`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Feature where to store the INCH identifier.

<a name="javaHome">

### javaHome

<div class="param-level param-level-default-value">Default value: `/home/rbossy/dist/jdk1.8.0_121`
</div>
<div class="param-type">Type: <a href="../converter/fr.inra.maiage.bibliome.util.files.InputDirectory" class="converter">InputDirectory</a>
</div>
Root directory of the Java implementation.

<a name="kegdFeatureName">

### kegdFeatureName

<div class="param-level param-level-default-value">Default value: `KEGD`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Feature where to store the KEGD identifier.

<a name="keggFeatureName">

### keggFeatureName

<div class="param-level param-level-default-value">Default value: `KEGG`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Feature where to store the KEGG identifier.

<a name="meshFeatureName">

### meshFeatureName

<div class="param-level param-level-default-value">Default value: `MESH`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Feature where to store the MESH identifier.

<a name="noDict">

### noDict

<div class="param-level param-level-default-value">Default value: `false`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.Boolean" class="converter">Boolean</a>
</div>
Do not use lexicons, only the CRF classifier. Uses less memory.

<a name="pubcFeatureName">

### pubcFeatureName

<div class="param-level param-level-default-value">Default value: `PUBC`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Feature where to store the PUBC identifier.

<a name="pubsFeatureName">

### pubsFeatureName

<div class="param-level param-level-default-value">Default value: `PUBS`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Feature where to store the PUBS identifier.

<a name="sectionFilter">

### sectionFilter

<div class="param-level param-level-default-value">Default value: `true`
</div>
<div class="param-type">Type: <a href="../converter/fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.Expression" class="converter">Expression</a>
</div>
Process only sections that satisfy this filter.

<a name="targetLayerName">

### targetLayerName

<div class="param-level param-level-default-value">Default value: `chemspot`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Name of the layer where to store annotations created by *Chemspot*.

