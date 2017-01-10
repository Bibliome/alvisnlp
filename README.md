# Description

AlvisNLP/ML is a configurable NLP batch processing pipeline.


# Prerequisites

* Java >= 7
* Maven >= 3.0.5

# Download

Clone the git repository or download from https://github.com/Bibliome/alvisnlp

# Build

`mvn clean package`


# Command-line interface

## Install

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
| `alvisnlp.executor-class` | Fully qualified name of the class that launches runs. <br> Default: `fr.jouy.inra.maiage.bibliome.alvis.web.executor.ThreadExecutor`, executes each run in a separate thread on the same server. |

## Use it

From a browser open the URL of the AlvisNLP/ML application.
