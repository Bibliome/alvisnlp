<h1 class="module">OBOReader</h1>

## Synopsis

Reads terms in [OBO files](XXX) as documents.

## Description

*OBOReader* reads files specified by <a href="#oboFiles" class="param">oboFiles</a> in [OBO format](XXX).
  Each term is loaded as a distinct document with the term identifier as the document identifier.
  Each document contains a section (<a href="#nameSectionName" class="param">nameSectionName</a>) containing the term name, and one section for each term synonym (<a href="#synonymSectionName" class="param">synonymSectionName</a>).
  Optionally *OBOReader* also sets features on the document with the term path from the root (<a href="#pathFeature" class="param">pathFeature</a>), the identifier of the parent term (<a href="#parentFeature" class="param">parentFeature</a>), the identifiers of each ancestor (<a href="#ancestorsFeature" class="param">ancestorsFeature</a>), of the identifiers of each child term (<a href="#childrenFeature" class="param">childrenFeature</a>).

## Parameters

<h3 name="oboFiles" class="param">oboFiles</h3>

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/fr.inra.maiage.bibliome.util.files.InputFile[]" class="converter">InputFile[]</a>
</div>
OBO files to read.

<h3 name="ancestorsFeature" class="param">ancestorsFeature</h3>

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Name of the feature that contains the term ancestors ids.

<h3 name="childrenFeature" class="param">childrenFeature</h3>

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Name of the feature that contains the term children ids.

<h3 name="constantDocumentFeatures" class="param">constantDocumentFeatures</h3>

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/fr.inra.maiage.bibliome.alvisnlp.core.module.types.Mapping" class="converter">Mapping</a>
</div>
Constant features to add to each document created by this module.

<h3 name="constantSectionFeatures" class="param">constantSectionFeatures</h3>

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/fr.inra.maiage.bibliome.alvisnlp.core.module.types.Mapping" class="converter">Mapping</a>
</div>
Constant features to add to each section created by this module.

<h3 name="excludeOBOBuiltins" class="param">excludeOBOBuiltins</h3>

<div class="param-level param-level-default-value">Default value: `true`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.Boolean" class="converter">Boolean</a>
</div>
Either to exclude builtin OBO terms.

<h3 name="idPrefix" class="param">idPrefix</h3>

<div class="param-level param-level-default-value">Default value: ``
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Prefix to prepend to each Term identifier.

<h3 name="nameSectionName" class="param">nameSectionName</h3>

<div class="param-level param-level-default-value">Default value: `name`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Name of the section that contains the term name.

<h3 name="parentFeature" class="param">parentFeature</h3>

<div class="param-level param-level-default-value">Default value: `is_a`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Name of the feature that contains the term parents.

<h3 name="pathFeature" class="param">pathFeature</h3>

<div class="param-level param-level-default-value">Default value: `path`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Name of the feature that contains the term paths.

<h3 name="synonymSectionName" class="param">synonymSectionName</h3>

<div class="param-level param-level-default-value">Default value: `synonym`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Name of the sections that contains the term synonyms.

