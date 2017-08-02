# org.bibliome.alvisnlp.modules.FillDB

## Synopsis

Stores the corpus into a SQL database.

**This module is experimental.**

## Description



## Parameters

<a name="password">

### password

Optional

Type: [String](../converter/java.lang.String)

Password for RDBMS access.

<a name="schema">

### schema

Optional

Type: [String](../converter/java.lang.String)

Schema of the filled tables.

<a name="url">

### url

Optional

Type: [String](../converter/java.lang.String)

URL of the database.

<a name="username">

### username

Optional

Type: [String](../converter/java.lang.String)

User for RDBMS access.

<a name="documentFilter">

### documentFilter

Default value: `true`

Type: [Expression](../converter/alvisnlp.corpus.expressions.Expression)

Only process document that satisfy this filter.

<a name="jdbcDriver">

### jdbcDriver

Default value: `org.postgresql.Driver`

Type: [String](../converter/java.lang.String)

JDBC driver for the RDBMS.

<a name="sectionFilter">

### sectionFilter

Default value: `true`

Type: [Expression](../converter/alvisnlp.corpus.expressions.Expression)

Process only sections that satisfy this filter.

