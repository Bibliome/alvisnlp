<h1 class="module">BioNLPSTReader</h1>

## Synopsis

Reads documents and annotations in the [BioNLP-ST 2013 a1/a2 format](XXX) .

## Description

*BioNLPSTReader*reads text files in the directory specified by <a href="#textDir" class="param">textDir</a> as documents, then imports annotations in the corresponding files in the directories specified by <a href="#a1Dir" class="param">a1Dir</a> and <a href="#a2Dir" class="param">a2Dir</a> .

## Snippet



```xml
<bionlpstreader class="BioNLPSTReader>
    <textDir></textDir>
</bionlpstreader>
```

## Mandatory parameters

<h3 id="textDir" class="param">textDir</h3>

<div class="param-level param-level-mandatory">Mandatory
</div>
<div class="param-type">Type: <a href="../converter/fr.inra.maiage.bibliome.util.files.InputDirectory" class="converter">InputDirectory</a>
</div>
Path to the directory containing `.txt` files.

## Optional parameters

<h3 id="a1Dir" class="param">a1Dir</h3>

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/fr.inra.maiage.bibliome.util.files.InputDirectory" class="converter">InputDirectory</a>
</div>
Path to the directory containing `.a1` files. If this parameter is not set, then*BioNLPSTReader*does not read `a1` files. This directory must contain one `.a1` file for each `.txt` file found in <a href="#textDir" class="param">textDir</a> .

<h3 id="a2Dir" class="param">a2Dir</h3>

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/fr.inra.maiage.bibliome.util.files.InputDirectory" class="converter">InputDirectory</a>
</div>
Path to the directory containing `.a2` files. If this parameter is not set, then*BioNLPSTReader*does not read `a2` files. This directory must contain one `.a2` file for each `.txt` file found in <a href="#textDir" class="param">textDir</a> .

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

<h3 id="schema" class="param">schema</h3>

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/fr.inra.maiage.bibliome.util.bionlpst.schema.DocumentSchema" class="converter">DocumentSchema</a>
</div>
Annotation schema against which the annotations are checked.*BioNLPSTReader*aborts if the annotations do not check. If not set, the annotations are not checked.

<h3 id="charset" class="param">charset</h3>

<div class="param-level param-level-default-value">Default value: `UTF-8`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Character encoding of all imported files.

<h3 id="equivalenceItemPrefix" class="param">equivalenceItemPrefix</h3>

<div class="param-level param-level-default-value">Default value: `item`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Prefix of the role of arguments that represent group elements.

<h3 id="equivalenceRelationName" class="param">equivalenceRelationName</h3>

<div class="param-level param-level-default-value">Default value: `equiv`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Name of the relation whose tuples represent equivalences.

<h3 id="eventKind" class="param">eventKind</h3>

<div class="param-level param-level-default-value">Default value: `event`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Kind value for event annotations.

<h3 id="fragmentCountFeatureName" class="param">fragmentCountFeatureName</h3>

<div class="param-level param-level-default-value">Default value: `fragments`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Feature where to store the number of merged fragments. This feature is only set if <a href="#textBoundAsAnnotations" class="param">textBoundAsAnnotations</a> is `true` .

<h3 id="idFeatureName" class="param">idFeatureName</h3>

<div class="param-level param-level-default-value">Default value: `id`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Feature where to store the annotation identifier.

<h3 id="kindFeatureName" class="param">kindFeatureName</h3>

<div class="param-level param-level-default-value">Default value: `kind`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Feature of relations contatining the annotation kind.

<h3 id="relationKind" class="param">relationKind</h3>

<div class="param-level param-level-default-value">Default value: `relation`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Kind value for relation annotations.

<h3 id="sectionName" class="param">sectionName</h3>

<div class="param-level param-level-default-value">Default value: `text`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Name of the unique section contating all the text.

<h3 id="textBoundAsAnnotations" class="param">textBoundAsAnnotations</h3>

<div class="param-level param-level-default-value">Default value: `false`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.Boolean" class="converter">Boolean</a>
</div>
If `true` ,*BioNLPSTReader*imports text-bound annotations as annotations instead of tuples. Multiple fragments text-bound annotations are merged.

<h3 id="textBoundFragmentRolePrefix" class="param">textBoundFragmentRolePrefix</h3>

<div class="param-level param-level-default-value">Default value: `frag`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Prefix of the role of arguments that represent text-bound fragments.

<h3 id="textKind" class="param">textKind</h3>

<div class="param-level param-level-default-value">Default value: `text`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Kind value for text annotations.

<h3 id="triggerRole" class="param">triggerRole</h3>

<div class="param-level param-level-default-value">Default value: `trigger`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Role of the argument that represent an event trigger.

<h3 id="typeFeatureName" class="param">typeFeatureName</h3>

<div class="param-level param-level-default-value">Default value: `type`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Feature where to store the type of the annotation.

