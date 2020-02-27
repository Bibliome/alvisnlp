<h1 class="module">OBOMapper</h1>

## Synopsis

Maps names and synonyms of terms defined in OBO files.

## Description

*OBOMapper* maps names and synonyms of terms described in <a href="#oboFiles" class="param">oboFiles</a> on <a href="#target" class="param">target</a>.

## Parameters

<h3 name="form" class="param">form</h3>

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.Expression" class="converter">Expression</a>
</div>
Expression evaluated as a string with the target element as context that specifies the target key.

<h3 name="oboFiles" class="param">oboFiles</h3>

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/fr.inra.maiage.bibliome.util.files.InputFile[]" class="converter">InputFile[]</a>
</div>
Paths to the OBO files to map.

<h3 name="target" class="param">target</h3>

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.Expression" class="converter">Expression</a>
</div>
Expression evaluated as a list of elements with the corpus as context that specify the elements to be mapped.

<h3 name="ancestorsFeature" class="param">ancestorsFeature</h3>

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Name of the feature where to store the term ancestors ids.

<h3 name="childrenFeature" class="param">childrenFeature</h3>

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Name of the feature where to store the term children ids.

<h3 name="idFeature" class="param">idFeature</h3>

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Name of the feature where to store the term id.

<h3 name="nameFeature" class="param">nameFeature</h3>

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Name of the feature where to store the term name.

<h3 name="parentsFeature" class="param">parentsFeature</h3>

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Name of the feature where to store the term parents ids.

<h3 name="pathFeature" class="param">pathFeature</h3>

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Name of the feature where to store the term id path from the root.

<h3 name="synonymsFeature" class="param">synonymsFeature</h3>

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Name of the feature where to store the term synonyms.

<h3 name="versionFeature" class="param">versionFeature</h3>

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Name of the feature where to store the ontology version.

<h3 name="idKeys" class="param">idKeys</h3>

<div class="param-level param-level-default-value">Default value: `false`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.Boolean" class="converter">Boolean</a>
</div>
Map the term keys instead of name and synonyms

<h3 name="ignoreCase" class="param">ignoreCase</h3>

<div class="param-level param-level-default-value">Default value: `false`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.Boolean" class="converter">Boolean</a>
</div>
Either to match ignoring the case.

<h3 name="keepDBXref" class="param">keepDBXref</h3>

<div class="param-level param-level-default-value">Default value: `false`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.Boolean" class="converter">Boolean</a>
</div>
Either to store term dbxrefs. Dbxrefs are stored in features that match the database name.

<h3 name="operator" class="param">operator</h3>

<div class="param-level param-level-default-value">Default value: `exact`
</div>
<div class="param-type">Type: <a href="../converter/fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.mapper.MappingOperator" class="converter">MappingOperator</a>
</div>
Matching operator.

