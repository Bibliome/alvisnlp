# Getting started

## Using the command-line

You will find instructions on the acquisition and installation of *AlvsNLP/ML* in the [Download](Download) page.

*AlvsNLP/ML* consists on a single command-line executable: `alvisnlp`.
In order to get a short help on the options:

```sh
$ alvisnlp -help

alvisnlp
    process text corpora

Usage:
    alvisnlp [OPTIONS] PLANFILE

Documentation options:
    -help                                 print this help
    -supportedModules                     print supported modules and exit
    -supportedConverters                  print all types of parameters that can be converted
    -supportedLibraries                   print the name of all supported expression libraries
    -moduleDoc              MODULE        print documentation for a module and exit
    -converterDoc           TYPE          print documentation for converters to a parameter type and exit
    -libraryDoc             LIB           print documentation for library LIB and exit
    -locale                 LOCALE        set locale for documentation and messages
    -supportedModulesXML                  print supported modules in XML and exit
    -supportedConvertersXML               print all types of parameters that can be converted in XML and exit
    -supportedLibrariesXML                print the name of all supported expression libraries in XML and exit
    -moduleDocXML           MODULE        print documentation for a module in XML and exit
    -converterDocXML        TYPE          print documentation for converter to a parameter type in XML and exit
    -libraryDocXML          LIB           print documentation for library LIB in XML and exit
    -planDoc                              write plan documentation instead of processing
    -docTransformer         XSLT          use the specified XSL transformation file to display documentation
    -xslParam               NAME VALUE    pass parameter to the style sheet specified by -docTransformer

Verbosity options:
    -verbose           log more information
    -quiet             log less information
    -silent            log only warnings
    -log     FILE      write log into FILE
    -append            append log at the end of the log file (ignore if no -log)
    -locale  LOCALE    set locale for documentation and messages

Plan options:
    -param                  MODULE PARAM VALUE    set to VALUE the parameter PARAM of module MODULE
    -xparam                 MODULE XML_PARAM      set MODULE parameter value specified by XML_PARAM
    -alias                  ALIAS VALUE           set to VALUE the parameter alias ALIAS
    -xalias                 XML_ALIAS             set parameter alias value specified by XML_ALIAS
    -unset                  MODULE PARAM          unset the parameter PARAM of module MODULE
    -defaultParamValuesFile FILE                  specifies the file containing default parameter values
    -module                 ID CLASS              append a module with the specified id and class at the end of the plan
    -shell                                        runs a shell after processing the corpus
    -creator                FEAT                  set the name of the feature containing the module that created each element
    -feat                   KEY VALUE             set feature for the corpus
    -entity                 NAME REPLACEMENT      define an XML entity replacement used in the plan file
    -environmentEntities                          define an XML entity replacement for each environment variable used in the plan file
    -propEntities           PROPFILE              define XML entity replacements from a properties file
    -inputDir               DIR                   add default input directory
    -outputDir              DIR                   set root output directory

Resume mode and dump options:
    -resume     FILE           resume processing from a dumped corpus
    -dumpModule MODULE FILE    set the dump file after the specified module
    -nodumps                   ignore all corpus dumps

Other options:
    -version          print version and exit
    -noProcess        do not process the corpus
    -writePlan        write the plan to standard output, adding type to the parameter aliases, using the XSLT provided by -docTransformer
    -tmp       DIR    set root of temporary directories
    -cleanTmp         delete the temp directory after processing
    -noColors         do not use ANSI color escape codes for logging or documentation

```

There are a *lot* of options, that's because

1. *AlvsNLP/ML* is very versatile,
2. the documentation is embedded to the binary.


## Plan file

*AlvsNLP/ML* is a corpus processing engine that allows you to apply a sequence of processing component modules on a collection of documents.
You specify the sequence of modules with an XML file called the **plan file**.
The plan file contains:

* the sequence of processing steps, each step references a module from a library provided by the *AlvsNLP/ML* distribution;
* the parameter values for each step, which allows you to specify resources (documents, lexicons, terminologies, ontologies) and to modulate the modules behaviour (*e.g.* case-sensitiveness).

Here's an example of a simple plan file that tells *AlvsNLP/ML* to read text files, look for a specific regular expression pattern and writes the result as HTML so we can take a look at it.

```xml
<alvisnlp-plan id="example">
    <read class="TextFileReader">
        <sourcePath>/path/to/txt/files</sourcePath>
    </read>

    <regexp class="RegExp">
        <pattern>\b[A-Z]\S*\b</pattern>
	<targetLayerName>capitalized</targetLayerName>
    </regexp>

    <write class="QuickHTML">
        <outDir>/path/to/output/dir</outDir>
	<classFeature>_dummy</classFeature>
    </write>
</alvisnlp-plan>
```

In this example, we define a plan with three successive steps using the modules `TextFileReader`, `RegExp`, and `QuickHTML` respectively.
We give to each step an identifier: `read`, `regexp`, and `write`.

The module classes are taken from the library of modules, there are currently more than fifty different modules at your disposal.
The module identifiers are arbitrary and their purpose is to give a meaningful label.

Within each step, we set some parameter values:

1. For the step `read`, there's a single parameter `sourcePath` that specifies where to read text files. Usually such parameters accept a file name, a directory (all files in the directory are read), or even URLs.
2. For `regexp`, there are two parameters. The first, `pattern` specifies the regular expression to search. This pattern looks for words that start with an uppercase letter. To understand the second one, `targetLayerName`, you have to be aware that `RegExp` modules create annotations: each match of the specified pattern is represented by an **annotation**. Annotations are sorted in **layers**. This parameter specifies that annotation created by this module are stored in a layer named `capitalized`.
3. Finally the `outDir` parameter of the last module indicates in which directory HTML files are created.

Running this plan requires the `alvisnlp` binary:

```shell
$ alvisnlp example.plan
```

`example.plan` is the XML plan file.


### Further reading

* [Write a plan](Write-a-plan) has a more detailed account on parameters and also plan reuse and import mechanisms. 
* [Module Reference](reference/Module-list) contains the purpose and effect of each module, as well as the required and optional parameters and their types.

## Shared data structure

The library of modules includes tools with many different purposes.
The modules understands a variety of file formats, conventions, and data types.
In order to make modules in a plan work together, *AlvsNLP/ML* stores all the results in a single data structure.

During the execution of a plan, this data structure is passed from a module to the next.
You have to have a solid grasp on this data structure in order to understand the effect of each module and to write meaningful and efficient plans.

### Further reading

* [Data model](Data-model) details the shared data structure.
* [Element expression examples](Element-expression-examples) and [reference](Element-expression-reference) describe a path-like language used to navigate through the data structure.

