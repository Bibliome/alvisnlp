# org.bibliome.alvisnlp.modules.OBOReader

## Synopsis

Reads terms in [OBO files](XXX) as documents.

## Description

*org.bibliome.alvisnlp.modules.OBOReader* reads files specified by [oboFiles](#oboFiles) in [OBO format](XXX).
      Each term is loaded as a distinct document with the term identifier as the document identifier.
      Each document contains a section ([nameSectionName](#nameSectionName)) containing the term name, and one section for each term synonym ([synonymSectionName](#synonymSectionName)).
      Optionally *org.bibliome.alvisnlp.modules.OBOReader* also sets features on the document with the term path from the root ([pathFeature](#pathFeature)), the identifier of the parent term ([parentFeature](#parentFeature)), the identifiers of each ancestor ([ancestorsFeature](#ancestorsFeature)), of the identifiers of each child term ([childrenFeature](#childrenFeature)).

## Parameters

<a name="oboFiles">

### oboFiles

Optional

Type: [String[]](../converter/java.lang.String[])

OBO files to read.

<a name="ancestorsFeature">

### ancestorsFeature

Optional

Type: [String](../converter/java.lang.String)

Name of the feature that contains the term ancestors ids.

<a name="childrenFeature">

### childrenFeature

Optional

Type: [String](../converter/java.lang.String)

Name of the feature that contains the term children ids.

<a name="constantDocumentFeatures">

### constantDocumentFeatures

Optional

Type: [Mapping](../converter/alvisnlp.module.types.Mapping)

Constant features to add to each document created by this module

<a name="constantSectionFeatures">

### constantSectionFeatures

Optional

Type: [Mapping](../converter/alvisnlp.module.types.Mapping)

Constant features to add to each section created by this module

<a name="excludeOBOBuiltins">

### excludeOBOBuiltins

Default value: `true`

Type: [Boolean](../converter/java.lang.Boolean)

Either to exclude builtin OBO terms.

<a name="idPrefix">

### idPrefix

Default value: ``

Type: [String](../converter/java.lang.String)

Prefix to prepend to each Term identifier.

<a name="nameSectionName">

### nameSectionName

Default value: `name`

Type: [String](../converter/java.lang.String)

Name of the section that contains the term name.

<a name="parentFeature">

### parentFeature

Default value: `is_a`

Type: [String](../converter/java.lang.String)

Name of the feature that contains the term parents.

<a name="pathFeature">

### pathFeature

Default value: `path`

Type: [String](../converter/java.lang.String)

Name of the feature that contains the term paths.

<a name="synonymSectionName">

### synonymSectionName

Default value: `synonym`

Type: [String](../converter/java.lang.String)

Name of the sections that contains the term synonyms.

