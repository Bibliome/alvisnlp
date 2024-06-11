<h1 class="module">BioNLPSTReader</h1>

## Synopsis

Reads documents and annotations in the [BioNLP-ST 2013 a1/a2 format](https://2013.bionlp-st.org/file-formats) .

## Description

 *BioNLPSTReader* reads text files in the directory specified by <a href="#textDir" class="param">textDir</a> as documents, then imports annotations in the corresponding files in the directories specified by <a href="#a1Dir" class="param">a1Dir</a> and <a href="#a2Dir" class="param">a2Dir</a> .

## Snippet



<button class="copy-code-button" title="Copy to clipboard" onclick="copy_code(this)">📋</button>
```xml
<bionlpstreader class="BioNLPSTReader">
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
Path to the directory containing `.a1` files. If this parameter is not set, then *BioNLPSTReader* does not read `a1` files. This directory must contain one `.a1` file for each `.txt` file found in <a href="#textDir" class="param">textDir</a> .

<div class="param-examples-header" onclick="toggle_examples(this)" id="examples-a1Dir">> Examples
</div>

<button class="copy-code-button" title="Copy to clipboard" onclick="copy_code(this)">📋</button>
```xml
<a1Dir>/path/to/a1/</a1Dir>
```

<h3 id="a2Dir" class="param">a2Dir</h3>

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/fr.inra.maiage.bibliome.util.files.InputDirectory" class="converter">InputDirectory</a>
</div>
Path to the directory containing `.a2` files. If this parameter is not set, then *BioNLPSTReader* does not read `a2` files. This directory must contain one `.a2` file for each `.txt` file found in <a href="#textDir" class="param">textDir</a> .

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
Annotation schema against which the annotations are checked. *BioNLPSTReader* aborts if the annotations do not check. If not set, the annotations are not checked.

<div class="param-examples-header" onclick="toggle_examples(this)" id="examples-schema">> Examples
</div>

<button class="copy-code-button" title="Copy to clipboard" onclick="copy_code(this)">📋</button>
```xml
<schema>
			<text-bound type="Title"/>
			<text-bound type="Paragraph"/>
			<text-bound type="Microorganism"/>
			<text-bound type="Habitat"/>
			<text-bound type="Phenotype"/>
			<text-bound type="Geographical"/>
			<relation type="Lives_In">
				<roles>Microorganism,Location</roles>
				<mandatory-arguments>Microorganism,Location</mandatory-arguments>
				<argument-types role="Microorganism">Microorganism</argument-types>
				<argument-types role="Location">Habitat,Geographical,Microorganism</argument-types>
			</relation>
			<relation type="Exhibits">
				<roles>Microorganism,Property</roles>
				<mandatory-arguments>Microorganism,Property</mandatory-arguments>
				<argument-types role="Microorganism">Microorganism</argument-types>
				<argument-types role="Property">Phenotype </argument-types>
			</relation>
        </schema>
```

<h3 id="charset" class="param">charset</h3>

<div class="param-level param-level-default-value">Default value: `UTF-8`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Character encoding of all imported files.

<div class="param-examples-header" onclick="toggle_examples(this)" id="examples-charset">> Examples
</div>

<button class="copy-code-button" title="Copy to clipboard" onclick="copy_code(this)">📋</button>
```xml
<charset>UTF-8</charset>
```

<h3 id="equivalenceItemPrefix" class="param">equivalenceItemPrefix</h3>

<div class="param-level param-level-default-value">Default value: `item`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Prefix of the role of arguments that represent group elements.

<div class="param-examples-header" onclick="toggle_examples(this)" id="examples-equivalenceItemPrefix">> Examples
</div>

<button class="copy-code-button" title="Copy to clipboard" onclick="copy_code(this)">📋</button>
```xml
<equivalenceItemPrefix>item</equivalenceItemPrefix>
```

<h3 id="equivalenceRelation" class="param">equivalenceRelation</h3>

<div class="param-level param-level-default-value">Default value: `equiv`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Name of the relation whose tuples represent equivalences.

<div class="param-examples-header" onclick="toggle_examples(this)" id="examples-equivalenceRelation">> Examples
</div>

<button class="copy-code-button" title="Copy to clipboard" onclick="copy_code(this)">📋</button>
```xml
<equivalenceRelation>equiv</equivalenceRelation>
```

<h3 id="eventKind" class="param">eventKind</h3>

<div class="param-level param-level-default-value">Default value: `event`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Kind value for event annotations.

<div class="param-examples-header" onclick="toggle_examples(this)" id="examples-eventKind">> Examples
</div>

<button class="copy-code-button" title="Copy to clipboard" onclick="copy_code(this)">📋</button>
```xml
<eventKind>event</eventKind>
```

<h3 id="fragmentCountFeature" class="param">fragmentCountFeature</h3>

<div class="param-level param-level-default-value">Default value: `fragments`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Feature where to store the number of merged fragments. This feature is only set if <a href="#textBoundAsAnnotations" class="param">textBoundAsAnnotations</a> is `true` .

<div class="param-examples-header" onclick="toggle_examples(this)" id="examples-fragmentCountFeature">> Examples
</div>

<button class="copy-code-button" title="Copy to clipboard" onclick="copy_code(this)">📋</button>
```xml
<fragmentCountFeature>fragments</fragmentCountFeature>
```

<h3 id="idFeature" class="param">idFeature</h3>

<div class="param-level param-level-default-value">Default value: `id`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Feature where to store the annotation identifier.

<div class="param-examples-header" onclick="toggle_examples(this)" id="examples-idFeature">> Examples
</div>

<button class="copy-code-button" title="Copy to clipboard" onclick="copy_code(this)">📋</button>
```xml
<idFeature>id</idFeature>
```

<h3 id="kindFeature" class="param">kindFeature</h3>

<div class="param-level param-level-default-value">Default value: `kind`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Feature of relations contatining the annotation kind.

<div class="param-examples-header" onclick="toggle_examples(this)" id="examples-kindFeature">> Examples
</div>

<button class="copy-code-button" title="Copy to clipboard" onclick="copy_code(this)">📋</button>
```xml
<kindFeature>kind</kindFeature>
```

<h3 id="relationKind" class="param">relationKind</h3>

<div class="param-level param-level-default-value">Default value: `relation`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Kind value for relation annotations.

<div class="param-examples-header" onclick="toggle_examples(this)" id="examples-relationKind">> Examples
</div>

<button class="copy-code-button" title="Copy to clipboard" onclick="copy_code(this)">📋</button>
```xml
<relationKind>relation</relationKind>
```

<h3 id="section" class="param">section</h3>

<div class="param-level param-level-default-value">Default value: `text`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Name of the unique section contating all the text.

<div class="param-examples-header" onclick="toggle_examples(this)" id="examples-section">> Examples
</div>

<button class="copy-code-button" title="Copy to clipboard" onclick="copy_code(this)">📋</button>
```xml
<section>text</section>
```

<h3 id="textBoundAsAnnotations" class="param">textBoundAsAnnotations</h3>

<div class="param-level param-level-default-value">Default value: `false`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.Boolean" class="converter">Boolean</a>
</div>
If `true` , *BioNLPSTReader* imports text-bound annotations as annotations instead of tuples. Multiple fragments text-bound annotations are merged.

<div class="param-examples-header" onclick="toggle_examples(this)" id="examples-textBoundAsAnnotations">> Examples
</div>

<button class="copy-code-button" title="Copy to clipboard" onclick="copy_code(this)">📋</button>
```xml
<textBoundAsAnnotations/>
```

<h3 id="textBoundFragmentRolePrefix" class="param">textBoundFragmentRolePrefix</h3>

<div class="param-level param-level-default-value">Default value: `frag`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Prefix of the role of arguments that represent text-bound fragments.

<div class="param-examples-header" onclick="toggle_examples(this)" id="examples-textBoundFragmentRolePrefix">> Examples
</div>

<button class="copy-code-button" title="Copy to clipboard" onclick="copy_code(this)">📋</button>
```xml
<textBoundFragmentRolePrefix>frag</textBoundFragmentRolePrefix>
```

<h3 id="textKind" class="param">textKind</h3>

<div class="param-level param-level-default-value">Default value: `text`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Kind value for text annotations.

<div class="param-examples-header" onclick="toggle_examples(this)" id="examples-textKind">> Examples
</div>

<button class="copy-code-button" title="Copy to clipboard" onclick="copy_code(this)">📋</button>
```xml
<textKind>text-bound</textKind>
```

<h3 id="triggerRole" class="param">triggerRole</h3>

<div class="param-level param-level-default-value">Default value: `trigger`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Role of the argument that represent an event trigger.

<h3 id="typeFeature" class="param">typeFeature</h3>

<div class="param-level param-level-default-value">Default value: `type`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Feature where to store the type of the annotation.

<div class="param-examples-header" onclick="toggle_examples(this)" id="examples-typeFeature">> Examples
</div>

<button class="copy-code-button" title="Copy to clipboard" onclick="copy_code(this)">📋</button>
```xml
<typeFeature>type</typeFeature>
```

## Deprecated parameters

<h3 id="equivalenceRelationName" class="param">equivalenceRelationName</h3>

<div class="param-level param-level-deprecated">Deprecated
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Deprecated alias for <a href="#equivalenceRelation" class="param">equivalenceRelation</a> .

<h3 id="fragmentCountFeatureName" class="param">fragmentCountFeatureName</h3>

<div class="param-level param-level-deprecated">Deprecated
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Deprecated alias for <a href="#fragmentCountFeature" class="param">fragmentCountFeature</a> .

<h3 id="idFeatureName" class="param">idFeatureName</h3>

<div class="param-level param-level-deprecated">Deprecated
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Deprecated alias for <a href="#idFeature" class="param">idFeature</a> .

<h3 id="kindFeatureName" class="param">kindFeatureName</h3>

<div class="param-level param-level-deprecated">Deprecated
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Deprecated alias for <a href="#kindFeature" class="param">kindFeature</a> .

<h3 id="sectionName" class="param">sectionName</h3>

<div class="param-level param-level-deprecated">Deprecated
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Deprecated alias for <a href="#section" class="param">section</a> .

<h3 id="typeFeatureName" class="param">typeFeatureName</h3>

<div class="param-level param-level-deprecated">Deprecated
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Deprecated alias for <a href="#typeFeature" class="param">typeFeature</a> .

