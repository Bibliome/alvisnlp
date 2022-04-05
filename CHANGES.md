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
