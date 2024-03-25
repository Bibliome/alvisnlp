# Change log

## `0.6.0`

Initial version since version policy is in force.

### `0.7.0`

* Updated and fixed `pom.xml` (maven release plugin, https repos)
* Module `PESVReader`: is more forgiving about missing columns

### `0.7.1`

* Module `RDFProjector`: added parameters `rdfFormat` and `language`

## 0.8.0

* Fixed:
  - Embedded resource `reader.plan`
  - Documentation in command-line (embedded resource `alvisnlp-doc2ansi.xslt`)
  - Documentation online
* Added embedded resource `yatea.plan`
* Added current version in the online documentation
* Refactored release script

### 0.8.1

* Fixed:
  - launch script now interprets correctly arguments protected by quotes that contains spaces
  - release script correctly replaces versions in POM files
 
## 0.9.0

* Removed support for alvisnlp-rest
* Fixed
  - online documentation
  - function `after`
  - module MergeSections: relations
  - dependencies updates
* New
  - command line option -maxMmapSize for systems with CONFIG_STRICT_DEVMEM
  - module LinguaLID: language identification
  - module OpenNLPDocumentCategorizerTrain, OpenNLPDocumentCategorizer: document classification
  - module CompareFeatures: compare two features of specified elements
  - module FasttextClassifierLabel, FasttextClassifierTrain: document classification
  - library hash
  - str:find
* Improved
  - temp dir cleaning: if -cleanTmp is set each module cleans after processing, prevents filesystem bloating
  - pre-process checking: more complete, skip inactive and not mentioned in select parameters
  - module CompareElements: new similarity functions
  - module TabularReader: reads true CSV, handle header line
  - module StanfordParser: more recent models, language parameter
  - module XMLReader: a:inline also copy attributes
  - modules OBOMapper and OBOProjector: added parameter altPathFeatures
* Improved documentation
  - refctored module docs
  - modules TomapTrain and TomapProjector
  - modules WapitiTrain and WapitiLabel
  - module PatternMatcher
  - module RemoveOverlaps
  - module YateaTermsProjector

## 0.10.0

* Definitive name is AlvisNLP
* Removed
  - support for OpenMinTeD
  - modules XMIImport, XMIExport
  - module BioLG
  - module CopyFeature
  - module RunProlog
  - module FillDB
  - module EnrichedDocumentWriter
  - module ProminentConceptReporter
  - module XMLWriter2ForINIST
  - module AlvisREPrepareCrossValidation
  - module AlvisDBIndexer
  - module ADBWriter
  - modules AlvisAEReader2, ExportCadixeJSON
  - module EnjuParser2
  - modules AttestedTermsProjector, TyDIProjector, YateaProjector, ElementProjector2, SimpleProjector2
  - module FileMapper2
  - modules SelectingElementClassifier, TaggingElementClassifier, TrainingElementClassifier
  - module Script
  - module WhatsWrongExport
  - module AnimalReader
  - support for custom entities in plans
  - old plan syntax
  - dumping from plan
  - miscellanous deprecated parameters
* Fixed
  - documentation
  - bug in TabularExport, create output directory if it does not exist
  - dump framework
  - bug in XMLWriter, will not crash if attribute name is illegal
* New
  - Python script interoperability framework
  - module PythonScript: generic Python script module
  - module Stanza: Stanza pipeline
  - module REBERTPredict: relation extraction with BERT
  - functions xafter, xbefore, xinside, xoutside, xoverlapping, xspan: same as no-x counterparts but includes context element
  - parameter FileMapper#headerLine
  - parameter QuickHTML#documentTitle
  - command-line option -analysisFile
  - module CoreNLP
  - module MultiRegExp
  - module JsonExport
  - library ctx
  - module TagTogReader
  - parameter TikaReader#baseNameId
* Improved
  - compiled trie works around mmap limitations
  - support for snippet examples in modules documentation
  - homogenize parameter names: source for readers, feature/layer/section/relation/arg names
  - homogenize dependency parser parameter names
  - API: replace obsoleteUseInstead method with @deprecated
  - cleaned code a bit
  - install AlvisNLP in a dedicated directory
  - updated dependencies versions

### 0.10.1

* Added
  - REBERTPredict#ensembleModels
* Improved
  - QuickHTML
  - check that output-feed parameters do not exist

### 10.2

* Improved
  - memory management when deleting elements
  - alias syntax see #169
  - code simplification
  - test for QuickHTML
  - RDFExport supports type and language for literals
  - Shell exports

* Added
  - PythonScript#outputFile
  - AlvisAEReader#databasePropsFile replaces database access parameters

* Fixed
  - bug in MergeSections
  - bug in HttpServer
  - bug in overlapping()
  - bug in parameter conversion of user functions
  