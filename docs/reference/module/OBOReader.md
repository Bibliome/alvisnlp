<h1 class="module">OBOReader</h1>

## Synopsis

Reads terms in [OBO files](XXX) as documents.

## Description

*OBOReader* reads files specified by <a href="#oboFiles" class="param">oboFiles</a> in [OBO format](XXX).
  Each term is loaded as a distinct document with the term identifier as the document identifier.
  Each document contains a section (<a href="#nameSectionName" class="param">nameSectionName</a>) containing the term name, and one section for each term synonym (<a href="#synonymSectionName" class="param">synonymSectionName</a>).
  Optionally *OBOReader* also sets features on the document with the term path from the root (<a href="#pathFeature" class="param">pathFeature</a>), the identifier of the parent term (<a href="#parentFeature" class="param">parentFeature</a>), the identifiers of each ancestor (<a href="#ancestorsFeature" class="param">ancestorsFeature</a>), of the identifiers of each child term (<a href="#childrenFeature" class="param">childrenFeature</a>).

## Parameters

<a name="oboFiles">

### oboFiles

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String[]" class="converter">String[]</a>
</div>
OBO files to read.

<a name="ancestorsFeature">

### ancestorsFeature

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Name of the feature that contains the term ancestors ids.

<a name="childrenFeature">

### childrenFeature

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Name of the feature that contains the term children ids.

<a name="constantDocumentFeatures">

### constantDocumentFeatures

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/fr.inra.maiage.bibliome.alvisnlp.core.module.types.Mapping" class="converter">Mapping</a>
</div>
Constant features to add to each document created by this module

<a name="constantSectionFeatures">

### constantSectionFeatures

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/fr.inra.maiage.bibliome.alvisnlp.core.module.types.Mapping" class="converter">Mapping</a>
</div>
Constant features to add to each section created by this module

<a name="excludeOBOBuiltins">

### excludeOBOBuiltins

<div class="param-level param-level-default-value">Default value: `true`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.Boolean" class="converter">Boolean</a>
</div>
Either to exclude builtin OBO terms.

<a name="idPrefix">

### idPrefix

<div class="param-level param-level-default-value">Default value: ``
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Prefix to prepend to each Term identifier.

<a name="nameSectionName">

### nameSectionName

<div class="param-level param-level-default-value">Default value: `name`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Name of the section that contains the term name.

<a name="parentFeature">

### parentFeature

<div class="param-level param-level-default-value">Default value: `is_a`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Name of the feature that contains the term parents.

<a name="pathFeature">

### pathFeature

<div class="param-level param-level-default-value">Default value: `path`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Name of the feature that contains the term paths.

<a name="synonymSectionName">

### synonymSectionName

<div class="param-level param-level-default-value">Default value: `synonym`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Name of the sections that contains the term synonyms.

