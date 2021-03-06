<h1 class="module">XMIExport</h1>

## Synopsis

Writes the corpus in XMI format using the AlvisNLP/ML proxy typesystem.

**This module is experimental.**

## Description

*XMIExport* writes a file for each document in <a href="#outDir" class="param">outDir</a> in the XMI format using the AlvisNLP/ML proxy typesystem.

Files written by this module can be read by <a href="../module/XMIImport" class="module">XMIImport</a>

## Parameters

<h3 name="outDir" class="param">outDir</h3>

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/fr.inra.maiage.bibliome.util.files.OutputDirectory" class="converter">OutputDirectory</a>
</div>
Directory where to write XMI files.

<h3 name="typeSystemFile" class="param">typeSystemFile</h3>

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/fr.inra.maiage.bibliome.util.files.OutputFile" class="converter">OutputFile</a>
</div>
If set write the AlvisNLP/ML proxy typesystem into the specified file.

<h3 name="dkproCompatibility" class="param">dkproCompatibility</h3>

<div class="param-level param-level-default-value">Default value: `false`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.Boolean" class="converter">Boolean</a>
</div>
UNDOCUMENTED

<h3 name="documentFilter" class="param">documentFilter</h3>

<div class="param-level param-level-default-value">Default value: `true`
</div>
<div class="param-type">Type: <a href="../converter/fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.Expression" class="converter">Expression</a>
</div>
Only process document that satisfy this expression.

<h3 name="sectionFilter" class="param">sectionFilter</h3>

<div class="param-level param-level-default-value">Default value: `true`
</div>
<div class="param-type">Type: <a href="../converter/fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.Expression" class="converter">Expression</a>
</div>
Process only sections that satisfy this expression.

