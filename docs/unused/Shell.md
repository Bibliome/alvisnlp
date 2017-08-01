# Table of Contents

> * [Introduction](#Introduction-1)
> * [How to invoke the shell](#How-to-invoke-the-shell-1)
>   * [The `Shell` module](#The-Shell-module-1)
>   * [The `-shell` command line option](#The--shell-command-line-option-1)
> * [How to interact with the shell](#How-to-interact-with-the-shell-1)
> * [Shell commands](#Shell-commands-1)
>   * [`@query`](#@query-1)
>   * [`@move`](#@move-1)
>   * [`@next`](#@next-1)
>   * [`@prev`](#@prev-1)
>   * [`@features`](#@features-1)
</toc>



<a name="Introduction-1" />

# Introduction

The AlvisNLP/ML Shell is an console that allows you to evaluate
[[Element expression]]s interactively. The Shell is useful to
explore the corpus annotations and to debug your plans.


<a name="How-to-invoke-the-shell-1" />

# How to invoke the shell


<a name="The-Shell-module-1" />

## The `Shell` module

Add a module of class
[Shell](http://bibliome.jouy.inra.fr/demo/alvisnlp/api/modules/Shell)
in your plan at the point where you want to explore the corpus.


<a name="The--shell-command-line-option-1" />

## The `-shell` command line option

If you launch AlvisNLP/ML with the `-shell` option, then it will open
the shell after the corpus has been processed by the whole plan.


<a name="How-to-interact-with-the-shell-1" />

# How to interact with the shell

The shell will display a prompt ("`>`", by default) and wait for a
command. If you type an expression, then it will be evaluated and the
result displayed. For example:

```
> documents
```

can produce the following output:

```
Document: 9795118
Document: 7797557
Document: 9353924
Document: 8419299
Document: 9826772
Document: 9701599
Document: 9852010
Document: 9786190
Document: 9988713
Document: 9671823
Document: 9805371
Document: 9811636
Document: 100614
Document: 9680198
...
```

Each line represents an element of the result set and indicates the type
of the element (`Document`) and its identifier. The Shell may evaluate
any type of expression:

```
> number(documents)
```

may produce the following output:

```
375.0
```

After the Shell has evaluated an expression and printed the result, it
is ready for the next one...\
In order to leave the shell, type `Ctrl-D`.


<a name="Shell-commands-1" />

# Shell commands

The general syntax for the Shell is the following:

```
> [@command] [expression]
```

`@command` is a Shell command, when it is omitted, then it is an
expression query by default.\
`expression` is the command's operand. The operand is optional or
mandatory depending on the command.


<a name="@query-1" />

## `@query`

This command evaluates `expression` with the current context element and
displays the result. AlvisNLP/ML tries its best to guess the most
sensible evaluation type.\
The `expression` operand is mandatory.


<a name="@move-1" />

## `@move`

This command evaluates `expression` as a list of elements and uses the
first one as the context element for the following commands. When the
shell is opened, the default context element is the corpus. In order to
get back at the corpus, simply type:

```
> @move corpus
```

Subsequent `@next` and `@prev` commands will navigate along the elements
of the result set.\
The `expression` operand is mandatory.


<a name="@next-1" />

## `@next`

For the following commands, the shell uses the next element from the
result of the last `@move` command as the context element.\
This command accepts no operand.


<a name="@prev-1" />

## `@prev`

For the following commands, the shell uses the previous element from the
result of the last `@move` command as the context element.\
This command accepts no operand.


<a name="@features-1" />

## `@features`

This command evaluates `expression` as a list of elements and prints all
features of each one.\
If `expression` is omitted, then the shell prints all features of the
current context element.
