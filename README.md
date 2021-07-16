# Description

AlvisNLP/ML is a configurable NLP batch processing pipeline. It annotates text documents for the semantic annotation of textual documents. It integrates Natural Language Processing (NLP) tools for sentence and word segmentation, named-entity recognition, term analysis, semantic typing and relation extraction. These tools rely on resources such as terminologies or ontologies for the adaptation to the application domain. Alvis NLP/ML contains several tools for (semi)-automatic acquisition of these resources, using Machine Learning (ML) techniques. New components can be easily integrated into the pipeline. Part of this work has been funded by the European project [Alvis](http://cordis.europa.eu/ist/kct/alvis_synopsis.htm) and the French project [Quaero](http://www.quaero.org/module_technologique/alvisae-alvis-annotation-editor/).


AlvisNLP/ML is held by the [Bibliome group](http://maiage.jouy.inra.fr/?q=fr/bibliome/) at [Inra Jouy-en-Josas](http://www.jouy.inra.fr/en), France

See
- [Nédellec et al., Handbook on Ontology, 2009](http://www.springer.com/us/book/9783540709992).
- [Ba et Bossy, Interoperability of corpus processing work-flow engines: the case of AlvisNLP/ML in OpenMinTeD, 2016](http://interop2016.github.io//pdf/INTEROP-4.pdf)
 
      
Please contact [Robert Bossy](mailto:robert.bossy@inra.fr) if you have any questions.

# Foreword

This file contains instructions to download, compile AlvisNLP/ML, and install the command-line interface.

The instructions assume you are running on a Unix system with a shell.
If you are running on Windows, then check the instructions in the `WINDOWS.md` file.

# Prerequisites

* git
* Java >= 8
* Maven >= 3.0.5

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

`cp share/default-param-values.xml.template share/default-param-values.xml`

This will create a standard default parameter file in `share/default-param-values.xml`.
Edit this file and fill parameter values appropriate for your host.

### Default command-line options

If you wish your installed AlvisNLP/ML to run with default command-line options, then you put them on the file named `default-options.txt` in the `share` directory.
Once installed, the options will be automatically prepended to each invocation of `alvisnlp`.

In the `share` directory, there is an example file named `default-options.txt.template`.

### Copy files to installation directory

`./install.sh DIR`

`DIR` is the base directory of your AlvisNLP/ML install.
This directory must exist.
Launch this script as `root` if necessary.

## Running AlvisNLP/ML

`DIR/bin/alvisnlp -help`

`DIR` is the base directory of your AlvisNLP/ML install. You migh also add the `DIR/bin` sub-directory to your `PATH` environment variable.

`export PATH=DIR/bin:$PATH`

