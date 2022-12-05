<h1 class="module">PythonScript</h1>

## Synopsis

Runs a Python script. This module is useful for processing the corpus with Python libraries dedicated to NLP.

**This module is experimental.**

## Description

 *PythonScript* assumes the script reads from standard input the AlvisNLP data structure serialized as JSON. *PythonScript* also assumes the script writes the modifications serialized in JSON to the standard output.

The `alvisnlp.py` library facilitates the deserialization, serialization, and manipulation of the AlvisNLP data structure. It is located in the directory specified by <a href="#alvisnlpPythonDirectory" class="param">alvisnlpPythonDirectory</a> .

The script to run is specified with <a href="#script" class="param">script</a> .

## Snippet



<button class="copy-code-button" title="Copy to clipboard" onclick="copy_code(this)">📋</button>
```xml
<pythonscript class="PythonScript>
    <alvisnlpPythonDirectory></alvisnlpPythonDirectory>
    <script></script>
</pythonscript>
```

## Mandatory parameters

<h3 id="alvisnlpPythonDirectory" class="param">alvisnlpPythonDirectory</h3>

<div class="param-level param-level-mandatory">Mandatory
</div>
<div class="param-type">Type: <a href="../converter/fr.inra.maiage.bibliome.util.files.InputDirectory" class="converter">InputDirectory</a>
</div>
Directory where the AlvisNLP Python library is found. In principle this parameter is set by default during AlvisNLP install.

<h3 id="script" class="param">script</h3>

<div class="param-level param-level-mandatory">Mandatory
</div>
<div class="param-type">Type: <a href="../converter/fr.inra.maiage.bibliome.util.streams.SourceStream" class="converter">SourceStream</a>
</div>
Path to the script to run.

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

<h3 id="layers" class="param">layers</h3>

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String%5B%5D" class="converter">String[]</a>
</div>
Names of layers to serialize. Layers not mentioned in this parameter will not be serialized. Use this to limit the amount of serialized data. By default *PythonScript* serializes all annotations in all layers.

<h3 id="python" class="param">python</h3>

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/fr.inra.maiage.bibliome.util.files.ExecutableFile" class="converter">ExecutableFile</a>
</div>
Path to the python executable. By default, let the `PATH` environment determine the location of the Python executable.

<h3 id="relations" class="param">relations</h3>

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String%5B%5D" class="converter">String[]</a>
</div>
Names of relations to serialize. Relations not mentioned in this parameter will not be serialized. Use this to limit the amount of serialized data. By default *PythonScript* serializes all tuples in all relations.

<h3 id="workingDirectory" class="param">workingDirectory</h3>

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/fr.inra.maiage.bibliome.util.files.WorkingDirectory" class="converter">WorkingDirectory</a>
</div>
Directory where to run the script. By default the working directory of AlvisNLP.

<h3 id="callPython" class="param">callPython</h3>

<div class="param-level param-level-default-value">Default value: `false`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.Boolean" class="converter">Boolean</a>
</div>
Either to call Python interpreter as executable and the script as the command. If this parameter is `false` , then the user must have execution rights on the script, and the script must have the appropriate shebang to locate the Python interpreter.

<h3 id="commandLine" class="param">commandLine</h3>

<div class="param-level param-level-default-value">Default value: ``
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String%5B%5D" class="converter">String[]</a>
</div>
Additional command line arguments to pass to the script.

<h3 id="documentFilter" class="param">documentFilter</h3>

<div class="param-level param-level-default-value">Default value: `true`
</div>
<div class="param-type">Type: <a href="../converter/fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.Expression" class="converter">Expression</a>
</div>
Only process document that satisfy this expression.

<h3 id="scriptParams" class="param">scriptParams</h3>

<div class="param-level param-level-default-value">Default value: `{}`
</div>
<div class="param-type">Type: <a href="../converter/fr.inra.maiage.bibliome.alvisnlp.core.module.types.ExpressionMapping" class="converter">ExpressionMapping</a>
</div>
Parameters to pass through the the serialized data structure. Expressions are evaluated from the corpus as strings.

<h3 id="sectionFilter" class="param">sectionFilter</h3>

<div class="param-level param-level-default-value">Default value: `true`
</div>
<div class="param-type">Type: <a href="../converter/fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.Expression" class="converter">Expression</a>
</div>
Process only sections that satisfy this expression.

## Deprecated parameters

<h3 id="layerNames" class="param">layerNames</h3>

<div class="param-level param-level-deprecated">Deprecated
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String%5B%5D" class="converter">String[]</a>
</div>
Deprecated alias for <a href="#layers" class="param">layers</a> .

<h3 id="relationNames" class="param">relationNames</h3>

<div class="param-level param-level-deprecated">Deprecated
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String%5B%5D" class="converter">String[]</a>
</div>
Deprecated alias for <a href="#relations" class="param">relations</a> .

