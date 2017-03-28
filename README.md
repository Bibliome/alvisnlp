# Description

AlvisNLP/ML is a configurable NLP batch processing pipeline. It annotates text documents for the semantic annotation of textual documents. It integrates Natural Language Processing (NLP) tools for sentence and word segmentation, named-entity recognition, term analysis, semantic typing and relation extraction. These tools rely on resources such as terminologies or ontologies for the adaptation to the application domain. Alvis NLP/ML contains several tools for (semi)-automatic acquisition of these resources, using Machine Learning (ML) techniques. New components can be easily integrated into the pipeline. Part of this work has been funded by the European project [Alvis](http://cordis.europa.eu/ist/kct/alvis_synopsis.htm) and the French project [Quaero](http://www.quaero.org/module_technologique/alvisae-alvis-annotation-editor/).


AlvisNLP/ML is held by the [Bibliome group](http://maiage.jouy.inra.fr/?q=fr/bibliome/) at [Inra Jouy-en-Josas](http://www.jouy.inra.fr/en), France

See
- [Nédellec et al., Handbook on Ontology, 2009](http://www.springer.com/us/book/9783540709992).
- [Ba et Bossy, Interoperability of corpus processing work-flow engines: the case of AlvisNLP/ML in OpenMinTeD, 2016](http://interop2016.github.io//pdf/INTEROP-4.pdf)
 
      
Please contact [Robert Bossy](mailto:robert.bossy@inra.fr) if you have any questions.

# Prerequisites

* Java >= 7
* Maven >= 3.0.5

# Download

Clone the git repository or download from https://github.com/Bibliome/alvisnlp

# Build and install from the package homedir

`mvn clean install`


# Command-line interface

## Install from the package homedir

`./install.sh DIR`

*DIR* is the base directory of your AlvisNLP/ML install.


## Running AlvisNLP/ML

`DIR/bin/alvisnlp -help`

*DIR* is the base directory of your AlvisNLP/ML install. You migh also add the *bin* sub-directory to the *PATH* environment variable.

`export PATH=DIR/bin:$PATH`

# Web service

## Deploy

Deploy the the `alvisnlp-rest/target/alvisnlp-rest.war` file in your favourite application container.

For instance, on *glassfish*, run:

`asadmin deploy --contextroot CONTEXT --name NAME alvisnlp-rest/target/alvisnlp-rest.war`

## Set context parameters

Set the following context parameters:

| Variable | Description |
| --- | --- |
| `alvisnlp.url-base` | Absolute URL of the deployed AlvisNLP/ML application. <br> It should usually be the URL of the container cocatenated with the application context root. |
| `alvisnlp.processing-dir` | Directory where the data for each run will be stored. |
| `alvisnlp.plan-dir` | Directory where exposed plans are found. |
| `alvisnlp.resource-dir` | Directory where to find resources used in plans. |
| `alvisnlp.executor-class` | Fully qualified name of the class that launches runs. <br> Default: `fr.jouy.inra.maiage.bibliome.alvis.web.executor.ThreadExecutor`, executes each run in a separate thread on the same server. |

## Jetty Maven Plugin

You can quick-test the Web Service with the Jetty Maven Plugin:

`mvn jetty:run-war`

## Use it

From a browser open the URL of the AlvisNLP/ML application.
