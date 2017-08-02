# org.bibliome.alvisnlp.modules.script.Script

## Synopsis

Runs a script.

**This module is experimental.**

## Description

*org.bibliome.alvisnlp.modules.script.Script* reads, parses and runs [script](#script), a script in the language indicated by [language](#language). AlcisNLP supports all languages supported by the Java Scripting Engine in your system.

The script context will have a binding named *alvisnlp* to an object of type [org.bibliome.alvisnlp.modules.script.HelperObject](), the way this object fields and methods are accessed depends on the scripting language.

*org.bibliome.alvisnlp.modules.script.Script* gives access to the corpus elements methods, in particular creation methods. However, for creating elements we advise to use [HelperObject]()'s *create** methods. One of the reasons is that elements created with these methods will have the constant features defined by [constantDocumentFeatures](#constantDocumentFeatures), [constantSectionFeatures](#constantSectionFeatures), [constantAnnotationFeatures](#constantAnnotationFeatures), [constantRelationFeatures](#constantRelationFeatures) and [constantTupleFeatures](#constantTupleFeatures).

Parameters [stdin](#stdin) and [stdout](#stdout) may not work on all systems.

## Parameters

<a name="script">

### script

Optional

Type: [String](../converter/java.lang.String)

The script to run (the source is inside the plan, not a path to a file).

<a name="constantAnnotationFeatures">

### constantAnnotationFeatures

Optional

Type: [Mapping](../converter/alvisnlp.module.types.Mapping)

Constant features to add to each annotation created by this module

<a name="constantDocumentFeatures">

### constantDocumentFeatures

Optional

Type: [Mapping](../converter/alvisnlp.module.types.Mapping)

Constant features to add to each document created by this module

<a name="constantRelationFeatures">

### constantRelationFeatures

Optional

Type: [Mapping](../converter/alvisnlp.module.types.Mapping)

Constant features to add to each relation created by this module

<a name="constantSectionFeatures">

### constantSectionFeatures

Optional

Type: [Mapping](../converter/alvisnlp.module.types.Mapping)

Constant features to add to each section created by this module

<a name="constantTupleFeatures">

### constantTupleFeatures

Optional

Type: [Mapping](../converter/alvisnlp.module.types.Mapping)

Constant features to add to each tuple created by this module

<a name="language">

### language

Default value: `JavaScript`

Type: [String](../converter/java.lang.String)

The language of the script.

