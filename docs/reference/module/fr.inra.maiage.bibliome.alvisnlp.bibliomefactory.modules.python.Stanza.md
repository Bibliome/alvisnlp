<h1 class="module">Stanza</h1>

## Synopsis

Applies a [Stanza](https://stanfordnlp.github.io/stanza/) pipeline on the sections.

**This module is experimental.**

## Description

 *Stanza* applies a [Stanza](https://stanfordnlp.github.io/stanza/) pipeline on the contents of sections.

By default the pipeline tokenizes and predicts POS-tags. *Stanza* also applies dependency parsing if <a href="#parse" class="param">parse</a> is set, constituency parsing if <a href="#constituency" class="param">constituency</a> is set, and named entity recognition if <a href="#ner" class="param">ner</a> is set.

The tokenization can be inhibited for using the existing tokens and sentences by setting <a href="#pretokenized" class="param">pretokenized</a> .

## Snippet



```xml
<stanza class="Stanza>
    <alvisnlpPythonDirectory></alvisnlpPythonDirectory>
</stanza>
```

## Mandatory parameters

<h3 id="alvisnlpPythonDirectory" class="param">alvisnlpPythonDirectory</h3>

<div class="param-level param-level-mandatory">Mandatory
</div>
<div class="param-type">Type: <a href="../converter/fr.inra.maiage.bibliome.util.files.InputDirectory" class="converter">InputDirectory</a>
</div>
Directory where the AlvisNLP Python library is found. In principle this parameter is set by default during AlvisNLP install.

## Optional parameters

<h3 id="conda" class="param">conda</h3>

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/fr.inra.maiage.bibliome.util.files.ExecutableFile" class="converter">ExecutableFile</a>
</div>
Path to the conda executable for running the script under a conda environment. If this parameter is not set, then the script is not run in a conda environment. If this parameter is set, then <a href="#condaEnvironment" class="param">condaEnvironment</a> must be set.

<h3 id="condaEnvironment" class="param">condaEnvironment</h3>

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Name of the conda environment in which the script must be run. This parameter is ignored if <a href="#conda" class="param">conda</a> is not set.

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

<h3 id="environment" class="param">environment</h3>

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/fr.inra.maiage.bibliome.alvisnlp.core.module.types.Mapping" class="converter">Mapping</a>
</div>
Additional variable values to pass to the script's environment.

<h3 id="python" class="param">python</h3>

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/fr.inra.maiage.bibliome.util.files.ExecutableFile" class="converter">ExecutableFile</a>
</div>
Path to the python executable. By default, let the `PATH` environment determine the location of the Python executable.

<h3 id="workingDirectory" class="param">workingDirectory</h3>

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/fr.inra.maiage.bibliome.util.files.WorkingDirectory" class="converter">WorkingDirectory</a>
</div>
Directory where to run the script. By default the working directory of AlvisNLP.

<h3 id="constituency" class="param">constituency</h3>

<div class="param-level param-level-default-value">Default value: `false`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.Boolean" class="converter">Boolean</a>
</div>
Either to predict constituents.

<h3 id="documentFilter" class="param">documentFilter</h3>

<div class="param-level param-level-default-value">Default value: `true`
</div>
<div class="param-type">Type: <a href="../converter/fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.Expression" class="converter">Expression</a>
</div>
Only process document that satisfy this expression.

<h3 id="language" class="param">language</h3>

<div class="param-level param-level-default-value">Default value: `en`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Language of the text.

<h3 id="ner" class="param">ner</h3>

<div class="param-level param-level-default-value">Default value: `false`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.Boolean" class="converter">Boolean</a>
</div>
Either to perform NER. Named entities will be stored in a layer named *entities* .

<h3 id="parse" class="param">parse</h3>

<div class="param-level param-level-default-value">Default value: `false`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.Boolean" class="converter">Boolean</a>
</div>
Either to predict dependency trees.

<h3 id="pretokenized" class="param">pretokenized</h3>

<div class="param-level param-level-default-value">Default value: `false`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.Boolean" class="converter">Boolean</a>
</div>
Either to skip tokenization and use the existing tokens and sentences.

<h3 id="sectionFilter" class="param">sectionFilter</h3>

<div class="param-level param-level-default-value">Default value: `true`
</div>
<div class="param-type">Type: <a href="../converter/fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.Expression" class="converter">Expression</a>
</div>
Process only sections that satisfy this expression.

