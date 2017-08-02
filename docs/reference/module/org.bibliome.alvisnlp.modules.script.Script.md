<h1 class="module">Script</h1>

## Synopsis

Runs a script.

**This module is experimental.**

## Description

*Script* reads, parses and runs <a href="#script" class="param">script</a>, a script in the language indicated by <a href="#language" class="param">language</a>. AlcisNLP supports all languages supported by the Java Scripting Engine in your system.

The script context will have a binding named *alvisnlp* to an object of type [org.bibliome.alvisnlp.modules.script.HelperObject](), the way this object fields and methods are accessed depends on the scripting language.

*Script* gives access to the corpus elements methods, in particular creation methods. However, for creating elements we advise to use [HelperObject]()'s *create** methods. One of the reasons is that elements created with these methods will have the constant features defined by <a href="#constantDocumentFeatures" class="param">constantDocumentFeatures</a>, <a href="#constantSectionFeatures" class="param">constantSectionFeatures</a>, <a href="#constantAnnotationFeatures" class="param">constantAnnotationFeatures</a>, <a href="#constantRelationFeatures" class="param">constantRelationFeatures</a> and <a href="#constantTupleFeatures" class="param">constantTupleFeatures</a>.

Parameters <a href="#stdin" class="param">stdin</a> and <a href="#stdout" class="param">stdout</a> may not work on all systems.

## Parameters

<a name="script">

### script

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
The script to run (the source is inside the plan, not a path to a file).

<a name="constantAnnotationFeatures">

### constantAnnotationFeatures

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/alvisnlp.module.types.Mapping" class="converter">Mapping</a>
</div>
Constant features to add to each annotation created by this module

<a name="constantDocumentFeatures">

### constantDocumentFeatures

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/alvisnlp.module.types.Mapping" class="converter">Mapping</a>
</div>
Constant features to add to each document created by this module

<a name="constantRelationFeatures">

### constantRelationFeatures

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/alvisnlp.module.types.Mapping" class="converter">Mapping</a>
</div>
Constant features to add to each relation created by this module

<a name="constantSectionFeatures">

### constantSectionFeatures

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/alvisnlp.module.types.Mapping" class="converter">Mapping</a>
</div>
Constant features to add to each section created by this module

<a name="constantTupleFeatures">

### constantTupleFeatures

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/alvisnlp.module.types.Mapping" class="converter">Mapping</a>
</div>
Constant features to add to each tuple created by this module

<a name="language">

### language

<div class="param-level param-level-default-value">Default value: `JavaScript`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
The language of the script.

