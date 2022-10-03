# Module reference

## List of all modules

<a href="{{ '/reference/Module-list' | relative_url }}">Alphabetical list of all available modules</a>

## Readers

Reader modules read files or streams as documents and sections.

| **Modue class**      | **Formats**         | **Source parameters**                             | **Comments** |
|----------------------|---------------------|---------------------------------------------------|--------------|
| {% include module class="AlvisAEReader" %}        | <a href="https://github.com/Bibliome/alvisae">AlvisAE Database</a>    | `url`, `schema`, `username`, `password`, `campaignId`...    | Also creates annotations and tuples |
| {% include module class="BioNLPSTReader" %}       | <a href="http://2013.bionlp-st.org/file-formats">BioNLP-ST challenge</a> | `textDir`, `a1Dir`, `a2Dir`                             | Also creates annotations and tuples |
| {% include module class="GeniaJSONReader" %}      | <a href="http://2016.bionlp-st.org/tasks/ge4">GENIA JSON</a>          | `source`                                            | Also creates annotations |
| {% include module class="I2B2Reader" %}           | <a href="https://www.i2b2.org/NLP/DataSets/Main.php">I2B2 challenge</a>      | `textDir`, `conceptsDir`, `assertionsDir`, `relationsDir` | Also creates annotations |
| {% include module class="LLLReader" %}            | <a href="http://genome.jouy.inra.fr/texte/LLLchallenge/">LLL challenge</a>       | `source`                                            | Also creates annotations |
| {% include module class="OBOReader" %}            | <a href="ftp://ftp.geneontology.org/go/www/GO.format.obo-1_2.shtml">OBO</a>                 | `oboFiles`                                          | Each term as a document, name and synonyms as sections |
| {% include module class="PESVReader" %}           | PESV export            | `docStream`, `entitiesStream`                                        | Also creates annotations |
| {% include module class="PubAnnotationReader" %}  | <a href="http://www.pubannotation.org/docs/annotation-format/">PubAnnotation JSON format</a>            | `source`                                        | Also creates annotations and tuples |
| {% include module class="PubTatorReader" %}       | <a href="https://www.ncbi.nlm.nih.gov/CBBresearch/Lu/Demo/PubTator/">PubTator</a>            | `sourcePath`                                        | Also creates annotations |
| {% include module class="SQLImport" %}            | SGBDR               | `url`, `schema`, `username`, `password`, `query`            |  |
| {% include module class="TabularReader" %}        | Tab-separated text  | `source`                                            |              |
| {% include module class="TextFileReader" %}       | Text                | `sourcePath`                                        |              |
| {% include module class="TikaReader" %}           | DOC, DOCX, PDF      | `source`                                            | Uses <a href="https://tika.apache.org/">Apache Tika</a> |
| {% include module class="TokenizedReader" %}      | one line per token  | `source`                                            |  |
| {% include module class="TreeTaggerReader" %}     | tree-tagger         | `sourcePath`                                        | Also creates words, POS-tags and lemmas |
| {% include module class="WebOfKnowledgeReader" %} | <a href="https://webofknowledge.com/">Web of Knowledge</a>    | `source`                                            |                  |
| {% include module class="XMLReader" %}            | XML, HTML           | `sourcePath`                                        | Requires an XSLT stylesheet |

### Stylesheets for XMLReader

The AlvisNLP distribution contains pre-defined stylesheets.

| **Stylesheet location**  | **Schema**        |
|--------------------------|-------------------|
| `res://XMLReader/endnote2alvisnlp.xslt`    | <a href="http://endnote.com">EndNote</a>           |
| `res://XMLReader/gene-train2alvisnlp.xslt` | gene-train        |
| `res://XMLReader/html2alvisnlp.xslt`       | HTML              |
| `res://XMLReader/pmc2alvisnlp.xslt`        | PubMed Central OA |
| `res://XMLReader/prodINRA2alvisnlp.xslt`   | ProdINRA          |
| `res://XMLReader/pubmed2alvisnlp.xslt`     | PubMed            |

### Multi-purpose reader

The AlvisNLP distribution ships with a plan that can read documents in various formats:

```xml
<read href="res://reader.plan">
  <select>...</select>
  <source>...</source>
</read>
```

The `source` parameter is the location of the document(s), its type and conversion is like <a class="converter" href="{{ '/reference/converter/SourceStream' | relative_url }}">SourceStream</a>.

The `select` parameter is the format of the documents.
It may take one of the following values:

| **`select`** | **Format** | **Equivalent Module class** |
|--------------|------------|-----------------------------|
| lll | <a href="http://genome.jouy.inra.fr/texte/LLLchallenge/">LLL challenge</a> | {% include module class="LLLReader" %}
| pubtator | <a href="https://www.ncbi.nlm.nih.gov/CBBresearch/Lu/Demo/PubTator/">PubTator</a> | {% include module class="PubTatorReader" %}
| text | Text | {% include module class="TextFileReader" %}
| pdf | PDF | {% include module class="TikaReader" %}
| doc | DOC, DOCX | {% include module class="TikaReader" %}
| tree-tagger | tree-tagger | {% include module class="TreeTaggerReader" %}
| wok | <a href="https://webofknowledge.com/">Web of Knowledge</a> | {% include module class="WebOfKnowledgeReader" %}
| endnote | <a href="http://endnote.com">EndNote</a> | {% include module class="XMLReader" %}
| html | HTML | {% include module class="XMLReader" %}
| prod-inra | ProdINRA | {% include module class="XMLReader" %}
| pubmed | PubMed | {% include module class="XMLReader" %}
| pmc | PubMed Central OA | {% include module class="XMLReader" %}


## Export

Export modules translate the contents of the data structure and write it into file or a set of files.

| **Module class**       | **Outut parameter** | **Format**            | **Comments** |
|------------------------|---------------------|-----------------------|--------------|
| {% include module class="AggregateValues" %}        | `outFile`               | Tab-separated text    | |
| {% include module class="AlvisAEWriter" %}          | `outDir`                | AlvisAE JSON          | Uses the json-simple library |
| {% include module class="AlvisIRIndexer" %}         | `indexDir`              | AlvisIR index         | Uses the Lucene and alvisir-core libraries |
| {% include module class="CompareElements" %}        | `outFile`               | Text                  | |
| {% include module class="CompareFeatures" %}        | `outFile`               | Text                  | |
| {% include module class="LayerComparator" %}        | `outFile`               | Text                  | |
| {% include module class="PubAnnotationExport" %}    | `outFile`               | PubAnnotation JSON    | Uses the json-simple library |
| {% include module class="QuickHTML" %}              | `outDir`                | HTML                  | |
| {% include module class="RDFExport" %}              | `outDir`, `fileName`    | RDF                   | Uses the Jena library |
| {% include module class="RelpWriter" %}             | `outFile`               | Relp                  | |
| {% include module class="TabularExport" %}          | `outDir`, `fileName`    | tab-separated text    | |
| {% include module class="WhatsWrongExport" %}       | `outFile`               | WhatsWrongWithMyNLP   | |
| {% include module class="XMLWriter" %}              | `outDir`, `fileName`    | XML                   | Requires an XSLT stylesheet |


## Projectors

Projector modules match entries from a lexicon on the section contents or annotations of a layer.
Each projector class accepts a different format for the lexicon.

| **Module class**         | **Lexicon parameter**      | **Lexicon format**         | **Comments** |
|--------------------------|----------------------------|----------------------------|--------------|
| {% include module class="ElementProjector" %}         | `entries`                      | AlvisNLP data structure | |
| {% include module class="OBOProjector" %}             | `oboFiles`                     | OBO                        | Uses the OBO library |
| {% include module class="RDFProjector" %}             | `source`                       | RDF (OWL, SKOS)            | Uses the Jena library |
| {% include module class="TabularProjector" %}         | `dictFile`                     | tab-separated text         | |
| {% include module class="TomapProjector" %}           | `yateaFile`, `tomapClassifier` | YaTeA and ToMap            | |
| {% include module class="TreeTaggerTermsProjector" %} | `termsFile`                    | TreeTagger                 | |
| {% include module class="TyDIExportProjector" %}      | `lemmaFile`, `synonymsFile`, `quasiSynonymsFile`, `acronymsFile`, `mergeFile`, `typographicVariationsFile` | TyDI | |
| {% include module class="XLSProjector" %}             | `xlsFile`                      | Excel XLS or XLSX          | Uses the POI library |
| {% include module class="YateaTermsProjector" %}      | `yateaFile`                    | YaTeA                      | |

## Pattern-matching

Pattern-matching modules matches user-defined patterns on section contents of annotations from a layer.

| **Module class**       | **Pattern type**     | **Output** |
|------------------------|----------------------|------------|
| {% include module class="Action" %}                 | Expressions          | Action expressions |
| {% include module class="CartesianProductTuples" %} | Expressions          | Tuples |
| {% include module class="PatternMatcher" %}         | Hearst-like patterns | Annotations and tuples |
| {% include module class="RegExp" %}                 | Regular expressions  | Annotations

## Mappers

Mapper modules associate data from a dictionary file.
Each mapper class accepts a different format for the dictionary.

| **Module class** | **Dictionary parameter** | **Format** |
|------------------|--------------------------|------------|
| {% include module class="ElementMapper" %}  | `entries`                  | AlvisNLP data structure |
| {% include module class="FileMapper" %}     | `mappingFile`              | tab-separated text |
| {% include module class="OBOMapper" %}      | `oboFiles`                 | OBO |


## Machine-learning

This section presents the module classes that can be used to train and classify elements.

| **Training class** | **Prediction class** | **Target**  | **Algorithm** | **Comments** |
|--------------------|----------------------|-------------|---------------|--------------|
| {% include module class="TEESTrain" %}    | {% include module class="TEESClassify" %}   | Tuples      | SVM                 | |
| {% include module class="TomapTrain" %}   | {% include module class="TomapProjector" %} | Annotations | ToMap               | |
| {% include module class="WapitiTrain" %}  | {% include module class="WapitiLabel" %}    | Annotations | CRF                 | |
| {% include module class="WekaTrain" %}    | {% include module class="WekaPredict" %}    | Any         | Various             | Uses the <a href="https://www.cs.waikato.ac.nz/ml/weka/">Weka library</a> |
| {% include module class="FasttextClassifierTrain" %}    | {% include module class="FasttextClassifierLabel" %}    | Any         | Word vectors             | Uses <a href="https://fasttext.cc/">Fasttext</a> |
| {% include module class="OpenNLPDocumentCategorizerTrain" %}    | {% include module class="OpenNLPDocumentCategorizer" %}    | Any         | ME             | Uses the <a href="https://opennlp.apache.org/">OpenNLP library</a> |
| {% include module class="ContesTrain" %}    | {% include module class="ContesPredict" %}    | Annotations         | Word Embedding, LR             | Uses <a href="https://github.com/ArnaudFerre/CONTES">CONTES</a> |

Additionally, the module class {% include module class="WekaSelectAttributes" %} uses the Weka library for attribute selection.

{% include module class="ContesTrain" %} and {% include module class="ContesPredict" %} require word embeddings that can be generated with {% include module class="Word2Vec" %}.

## NER

Named entity recognition modules.

| **Module class** | **NE types** |
|------------------|--------------|
| {% include module class="Chemspot" %}    | Chemical |
| {% include module class="GeniaTagger" %} | Gene, Protein |
| {% include module class="Species" %}     | Taxon |
| {% include module class="StanfordNER" %} | Person, Location, Organization |
| {% include module class="Stanza" %} | Person, Location, Organization, Number, Currency |


## Segmentation

| **Module class** | **Segments** |
|------------------|--------------|
| {% include module class="SeSMig" %}           | Sentences |
| {% include module class="WoSMig" %}           | Words |
| {% include module class="Stanza" %}           | Tokens, sentences |

### Word and sentence splitting

The AlvisNLP distribution ships with a ready-made complete word and sentence splitter plan that can be imported like this:

```xml
<seg href="res://segmentation.plan"/>
```

This plan combines several modules that nadles correctly latin abbreviations, cesure hyphens, numbers, and dates.
If you want to force entities as tokens, this plan assumes they are annotations in the layer named `rigid-entities`.


## Linguistic processing

| **Module class** | **Function** |
|------------------|--------------|
| {% include module class="LinguaLID" %}              | Language identification |
| {% include module class="Ab3P" %}              | Abbreviation recognition |
| {% include module class="CCGParser" %}       | Dependency parsing |
| {% include module class="CCGPosTagger" %}    | POS-tagging |
| {% include module class="EnjuParser" %}      | Dependency parsing |
| {% include module class="StanfordParser" %}      | Dependency parsing |
| {% include module class="GeniaTagger" %}     | POS-tagging, lemmatiation |
| {% include module class="LinguaLID" %}     | Language identification |
| {% include module class="PorterStemmer" %}   | Stemming |
| {% include module class="Stanza" %}   | Tokenization, POS-tagging, lemmatization, dependency parsing |
| {% include module class="TreeTagger" %}      | POS-tagging, lemmatiation |
| {% include module class="YateaExtractor" %} | Term extraction |


## Miscellanous


| **Module class** | **Function** |
|------------------|--------------|
| {% include module class="Assert" %}           | Check assertions on selected elements |
| {% include module class="ClearLayers" %}      | Empty layers of all annotations |
| {% include module class="HttpServer" %}       | Halts processing and allows to browse the data structure |
| {% include module class="InsertContents" %}   | Clone sections and insert contents |
| {% include module class="KeywordsSelector" %} | Select keywords using the specified metric |
| {% include module class="MergeLayers" %}      | Copy annotations from several layers to one target layer |
| {% include module class="MergeSections" %}    | Merge several sections of each document into a single one |
| {% include module class="NGrams" %}           | Create n-grams of annotations |
| {% include module class="PythonScript" %}     | Runs a Python script |
| {% include module class="RemoveContents" %}   | Clone sections and crop contents |
| {% include module class="RemoveEquivalent" %} | Deduplicate elements using custom equality |
| {% include module class="RemoveOverlaps" %}   | Remove overlapping annotations in a layer |
| {% include module class="Script" %}           | Run a script written in a language supported by the <a href="https://docs.oracle.com/javase/8/docs/technotes/guides/scripting/prog_guide/api.html">Java Scripting API</a> |
| {% include module class="SetFeature" %}       | Set a feature on selected elements |
| {% include module class="Shell" %}            | Enter interactive mode |
| {% include module class="SplitOverlaps" %}    | Split overlapping annotations |
| {% include module class="SplitSections" %}    | Split sections |

