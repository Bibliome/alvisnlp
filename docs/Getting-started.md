# Getting started

## Using the command-line

In order to use *AlvisNLP* you need to [Download](Download) and install it.

*AlvisNLP* consists on a single command-line executable: `alvisnlp`.
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
    -browser                                      runs a browser after processing the corpus
    -creator                FEAT                  set the name of the feature containing the module that created each element
    -feat                   KEY VALUE             set feature for the corpus
    -inputDir               DIR                   add default input directory
    -outputDir              DIR                   set root output directory
    -baseDir                NAME PATH             defines a base directory
    -resourceBase           BASE                  add default resource address base

Resume mode and dump options:
    -resume      FILE           resume processing from a dumped corpus (BROKEN)
    -dumpModule  MODULE FILE    set the dump file after the specified module (BROKEN)
    -nodumps                    ignore all corpus dumps
    -maxMmapSize SZ             maximum size of mmap blocks in bytes (if CONFIG_STRICT_DEVMEM is on, the set to 1048576)

Other options:
    -version              print version and exit
    -analysisFile FILE    analyze plan resource usage and write a report
    -noProcess            do not process the corpus
    -writePlan            write the plan to standard output, adding type to the parameter aliases, using the XSLT provided by -docTransformer
    -tmp          DIR     set root of temporary directories
    -cleanTmp             delete the temp directory after processing
    -noColors             do not use ANSI color escape codes for logging or documentation

```

There are a *lot* of options because *AlvisNLP* is very versatile. The most important is the *PLANFILE*.

## Plan file

You specify the sequence of modules with an XML file called the **plan file**.
The plan file contains:

* the **sequence of processing steps** taken from a library of modules or from pre-made reusable sequences;
* the **parameter values** for each step, which allows you customize the processing step and to specify resources (documents, lexicons, terminologies, ontologies).

Here's an example of a simple plan file that tells *AlvisNLP* to read text files, look for a specific regular expression pattern and writes the result as HTML so we can take a look at it.

`example.plan`

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

In this example, we define a plan of three successive steps named *read*, *regexp*, and *write*. These steps use the modules *TextFileReader*, *RegExp*, and *QuickHTML* respectively.

Within each step, we set some parameter values:

1. For the step *read*, there's a single parameter *sourcePath* that specifies where to read text files.
2. For *regexp*, there are two parameters. *pattern* specifies the regular expression to search. *targetLayerName* is the name of the container where *regexp* will store matches. Here we call it *"capitalized"*.

Ready to run this plan:

```shell
$ alvisnlp example.plan
```

### Further reading

* [Data model](Data-model) describes  the data structure. A good grasp of this data model is **very important** for optimal use of *AlvisNLP*.
* [Write a plan](Write-a-plan) details parameters, plan reuse, etc.
* [Module Reference](reference/Module-list) lists modules available in the library, and documents each one including purpose and available parameters.
* [Element expression examples](Element-expression-examples) and [reference](Element-expression-reference) describe a path-like language used to navigate through the data structure.
