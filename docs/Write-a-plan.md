# How to write a plan



## The basics

The plan file is an XML file that specifies the sequence of modules and
their parameters.

The top-level tag of a plan file is `alvisnlp-plan`:

```xml
<alvisnlp-plan id="foo">
...
</alvisnlp>
```

The `id` attribute is mandatory, its value is the identifier of the
plan. Make sure to fill a value that has some meaning for you.

Then, the `alvisnlp-plan` contains several `module` tags:

```xml
<alvisnlp-plan id="foo">
<read class="TextFileReader">
...
</read>

<words class="WoSMig">
...
</words>

...
</alvisnlp>
```

Each tag specifies an AlvisNLP/ML module that will process the
corpus. The `class` attribute is mandatory, it specifies the class of the module, that is what it does on the
corpus. The value must be a [supported module
class](http://bibliome.jouy.inra.fr/demo/alvisnlp/api/modules).

Finally, each module tag contains tags that specify the parameter
values for the module:

```xml
<alvisnlp-plan id="foo">
<read class="TextFileReader">
<sourcePath>...</sourcePath>
<sectionName>contents</sectionName>
</read>

<words class="WoSMig"/>

...
</alvisnlp>
```

In this example, we set two parameters in the module `read`:
`sourcePath` and `sectionName`, and no parameter in `words`. The module
class documentation should specify which parameters are supported, which
are mandatory and what are the default values.



## Parameter value conversion

The type of a parameter and conversion of the contents of the tag into
the type are documented in the module class description. In this section
we review the most used parameter types:



### [String](http://bibliome.jouy.inra.fr/demo/alvisnlp/api/converters/java.lang.String)

The contents of the tag must be a character string. Leading and trailing
whitespaces are trimmed.



### [Integer](http://bibliome.jouy.inra.fr/demo/alvisnlp/api/converters/java.lang.Integer)

The contents of the tag must be a character string. The conversion is in
base 10, with an optional a leading sign symbol (`+` or `-`). Leading
and trailing whitespaces are trimmed.



### [Boolean](http://bibliome.jouy.inra.fr/demo/alvisnlp/api/converters/java.lang.Boolean)

The contents of the tag must be a character string.

|**true values**  | **false values**|
|-----------------|------------------|
|`true`           | `false`|
|`on`             | `off`|
|`yes`            | `no`|

Leading and trailing whitespaces are trimmed.



### [SourceStream](http://bibliome.jouy.inra.fr/demo/alvisnlp/api/converters/org.bibliome.util.streams.SourceStream) and [TargetStream](http://bibliome.jouy.inra.fr/demo/alvisnlp/api/converters/org.bibliome.util.streams.TargetStream)

A `SourceStream` and `TargetStream`represents a file, a directory or an
internet resource. `SourceStream` are input resources, while
`TargetStream` represent outputs. The contents of the tag is either a
path in the local filesystem or an URL to an remote resource. The
conversion supports the main internet protocols (`http`, `https`, `ftp`)
or the standard streams (`stdin`, `stdout`, `stderr`). It also supports
the concatenation of several resources, the specification of all files
in a directory, with filename filters. It also supports compression
schemes.



### [File](http://bibliome.jouy.inra.fr/demo/alvisnlp/api/converters/java.io.File), [InputFile](http://bibliome.jouy.inra.fr/demo/alvisnlp/api/converters/org.bibliome.util.files.InputFile), [InputDirectory](http://bibliome.jouy.inra.fr/demo/alvisnlp/api/converters/org.bibliome.util.files.InputDirectory), [OutputDirectory](http://bibliome.jouy.inra.fr/demo/alvisnlp/api/converters/org.bibliome.util.files.OutputDirectory), [OutputFile](http://bibliome.jouy.inra.fr/demo/alvisnlp/api/converters/org.bibliome.util.files.OutputFile), [ExecutableFile](http://bibliome.jouy.inra.fr/demo/alvisnlp/api/converters/org.bibliome.util.files.ExecutableFile)

These types represent resources in the local filesystem, their value
cannot be remote URLs. AlvisNLP/ML will check the values according to
the type:

| **parameter type** | **file exists** | **file type**            | **permissions**
|--------------------|-----------------|--------------------------|-----------------
| `InputDirectory`   | yes             | directory                | rx
| `InputFile`        | yes             | regular                  | r
| `OutputDirectory`  | yes <sup>1</sup>          | directory <sup>1</sup>             | rwx <sup>1</sup>
| `OutputFile`       | yes <sup>1</sup>          | directory or regular <sup>2</sup>  | rwx <sup>2</sup>
| `ExecutableFile`   | yes             | regular                  | rx
| `File`             | no              | regular <sup>3</sup>               |

<sup>1</sup> some ancestor must exist and be a writable directory\
<sup>2</sup> if the file exists then it must be regular and writable, the
innermost existing ancestor must be a writable directory\
<sup>3</sup> if the file exists then it must be regular



### [Expression](http://bibliome.jouy.inra.fr/demo/alvisnlp/api/converters/alvisnlp.corpus.Expression)

`Expression` parameters are evaluated only when the module is
processing. Expressions allow to set values that depend on the state of
the corpus. The value of the tag must follow the element expression syntax.
See [[Element expression examples]] and [[Element expression reference]].




### Arrays

Array parameter types can be recognized by a pair of brackets at the end
of the type (`[]`). An array value is a sequence of values of the same
type. You can either specify the elements separated with commas (`,`), or
each element inside an enclosed tag (of arbitrary name).

The parameter tag may specify an alternate separator character with the `separator` attribute.



### Mappings

Mapping parameter types map strings to values, where all values of the
mapping are of the same type. The mapping can be specified by separating
each entry with commas (`,`), and by separating the key from the value
with an equal sign (`=`). Alternatively each entry can be specified with
a tag, whose name is the entry key, end the contents is converted into a
value.

The parameter tag may specify an alternate separator character with the `separator` attribute, and an alternate key-value separator with the `qualifier` attribute.



### Complex types

Some modules accept parameters with more complex and composite types. Refer to the documentation in the converter reference.



## Sequences

Sequences are sub-parts of the plan that contain modules (or other
sequences):

```xml
<alvisnlp-plan id="foo">
<read class="TextFileReader">
<sourcePath>...</sourcePath>
<sectionName>contents</sectionName>
</read>

<segmentation>
<words class="WoSMig"/>
<sentences class="SeSMig"/>
</segmentation>

...
</alvisnlp>
```

Sequences do not alter the order of processing, their purpose is the
organization of modules in logical bundles. Note that sequences affect
the logging and may help you to read the AlvisNLP/ML log.

## Plan import

Plans can be reused inside other plans:

```xml
<alvisnlp-plan id="foo">
<read class="TextFileReader">
<sourcePath>...</sourcePath>
<sectionName>contents</sectionName>
</read>

<import file="/path/to/another/plan.xml"/>
...
</alvisnlp>
```

In this example, all the modules specified in
`/path/to/another/plan.xml` will process the corpus as if the plan file
had been included.



### Plan-level parameters

You can define parameters for the whole plan, so you can set these
parameters when the plan is imported.

A Plan-level parameter looks like this in `project_species.xml`:

```xml
<param name="speciesFile">
<alias module="project.species" param="dictFile" />
</param>

<project>
<species class="SimpleContentsProjector">
<!-- ... snip ... -->
</species>
</project>
```

An import of this file could look like:

```xml
<import file="project_species.xml">
<speciesFile value="/bibdev/resources/..."/>
</import>
```

This will import the plan specified in `project_species.xml` and set the
parameter `speciesFile`. Since `speciesFile` is defined as an alias to
the `dictFile` parameter in `project.species`, then it will be set.

A Plan-level parameter can be an alias for several parameters in
different modules. When importing and setting these parameters, all
aliases will have the same value. You have no excuses left: make modular
plans!



## More on parameters

The parameter tag may have attributes that change the conversion:

| **Option** | **Effect** |
|-----------------------|------------------------------------------------------------------|
| `inhibitCheck="true"` |  prevents AlvisNLP/ML from checking this parameter value, for instance it will not check for the existence of `InputFile` parameters. |
| `separator="C"`       |  sets the separator character between array elements or mapping entries (default: `,`). |
| `qualifier="C"`       |  sets the separator character between the key and the value of a mapping entry (default: `=`). |
| `trim="false"`        |  prevents AlvisNLP/ML from trimming leading and trailing whitespaces off the parameter value. |
| `load="..."`          |  loads the specified file. This file must be an XML file, AlvisNLP/ML sets the parameter value as if the parameter tag was the root element of this file. This attribute is useful for complex parameter values. |

More attributes may be supported for the conversion to specific types.



## Command-line control

The plan, especially parameter values, can be controlled from the
command line.



### `-param`

```
alvisnlp -param MODULE PARAM VALUE
```

The `-param` option sets the value of parameter `PARAM` in `MODULE`.
`MODULE` is the identifier of a module specified in the plan. If the
module is inside a sequence, then its identifier is in the form
`SEQUENCE.MODULE`.\
The `VALUE` is a string and it is converted as if it was the contents of
the parameter tag.



### `-xparam`

alvisnlp -param MODULE PARAM XVALUE

The `-xparam` option behaves the same way as `-param` but it expects an
XML tag instead of a string value. This option is useful if you want to
set parameters with conversion options.



### `-feat`

alvisnlp -feat KEY VALUE

The `-feat` options adds a feature pair to the corpus before the
processing starts. Expression parameters can get the value of this
feature to alter the behavior of the modules.



### `-entity`

alvisnlp -entity NAME REPLACEMENT

This option defines an XML entity that is used when the plan file is
parsed.



### `-environmentEntities`

alvisnlp -environmentEntities

This option defines an XML entity for each environment variable.
