# Description

AlvisNLP/ML is a configurable NLP batch processing pipeline. It annotates text documents for the semantic annotation of textual documents. It integrates Natural Language Processing (NLP) tools for sentence and word segmentation, named-entity recognition, term analysis, semantic typing and relation extraction. These tools rely on resources such as terminologies or ontologies for the adaptation to the application domain. Alvis NLP/ML contains several tools for (semi)-automatic acquisition of these resources, using Machine Learning (ML) techniques. New components can be easily integrated into the pipeline. Part of this work has been funded by the European project [Alvis](http://cordis.europa.eu/ist/kct/alvis_synopsis.htm) and the French project [Quaero](http://www.quaero.org/module_technologique/alvisae-alvis-annotation-editor/).


AlvisNLP/ML is held by the [Bibliome group](http://maiage.jouy.inra.fr/?q=fr/bibliome/) at [Inra Jouy-en-Josas](http://www.jouy.inra.fr/en), France

See
- [Nédellec et al., Handbook on Ontology, 2009](http://www.springer.com/us/book/9783540709992).
- [Ba et Bossy, Interoperability of corpus processing work-flow engines: the case of AlvisNLP/ML in OpenMinTeD, 2016](http://interop2016.github.io//pdf/INTEROP-4.pdf)
 
      
Please contact [Robert Bossy](mailto:robert.bossy@inra.fr) if you have any questions.

# Prerequisites

* git
* Java >= 8
* Maven >= 3.0.5

## Java

The full JDK is necessary to install AlvisNLP/ML.
There are instructions to [download and install JDK 8](http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html).

Set the `JAVA_HOME` environment variable to the JDK directory, usually something like `C:\\Program Files\\jdk1.8.0_nnn`, where `nnn` is the update version of the JDK you downloaded.

You can set environment variables through the control panel, the exact procedure depends on the version of Windows.

## git

Instructions to [download and install git for windows](https://git-scm.com/download/win).

## Maven

[Download](https://maven.apache.org/download.cgi) and [install](https://maven.apache.org/guides/getting-started/windows-prerequisites.html) Maven. Installing Maven means extracting the archive in a sensible place like your home or `Program Files`.

Set the `Path` environment variable to `%Path%;C:\\sensibleplace\\apache-maven-3.5.2\\bin`.

# Open a command-line console

The rest of the installation procedure is done through the command-line interface.
The `cmd.exe` tool is supported and tested.
There is a support for PowerShell, though it has not been thoroughly tested.

# Download

`git clone https://github.com/Bibliome/alvisnlp`

# Compile and build

`cd alvisnlp`

`mvn clean install`

# Command-line interface

## Install

### Host-specific parameter values

We recommend that you set default parameter values for your host.
These parameter values avoid to set parameters in plans for external tools.

`copy share\\default-param-values.xml.template share\\default-param-values.xml`

This will create a standard default parameter file in `share\\default-param-values.xml`.
Edit this file and fill parameter values appropriate for your host.

### Copy files to installation directory

`install.bat DIR`

`DIR` is the base directory of your AlvisNLP/ML install.
This directory must exist.

If you are installing from PowerShell, run instead:

`install.ps1 DIR`

Note that the PowerShell `install.ps1` script has not been thoroughly tested.

## Running AlvisNLP/ML

`DIR\\bin\\alvisnlp -help`

`DIR` is the base directory of your AlvisNLP/ML install. You migh also add the `DIR/bin` sub-directory to your `Path` environment variable.

# Web service

## Deploy

Deploy the the `alvisnlp-rest\\target\\alvisnlp-rest.war` file in your favourite application container.

For instance, on *glassfish*, run:

`asadmin deploy --contextroot CONTEXT --name NAME alvisnlp-rest\\target\\alvisnlp-rest.war`

## Set context parameters

Set the following context parameters:

| Variable | Description |
| --- | --- |
| `alvisnlp.url-base` | Absolute URL of the deployed AlvisNLP/ML application. <br> It should usually be the URL of the container cocatenated with the application context root. |
| `alvisnlp.processing-dir` | Directory where the data for each run will be stored. |
| `alvisnlp.plan-dir` | Directory where exposed plans are found. |
| `alvisnlp.resource-dir` | Directory where to find resources used in plans. |
| `alvisnlp.executor-class` | Fully qualified name of the class that launches runs. <br> Default: `fr.inra.maiage.bibliome.alvisnlp.web.executor.ThreadExecutor`, executes each run in a separate thread on the same server. |

## Jetty Maven Plugin

You can quick-test the Web Service with the Jetty Maven Plugin:

`mvn jetty:run-war`

## Use it

From a browser open the URL of the AlvisNLP/ML application.
