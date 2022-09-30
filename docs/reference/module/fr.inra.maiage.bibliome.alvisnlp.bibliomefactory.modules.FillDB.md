<h1 class="module">FillDB</h1>

## Synopsis

Stores the corpus into a SQL database.

**This module is experimental.**

## Description



## Snippet



<button class="copy-code-button" title="Copy to clipboard" onclick="copy_code(this)">📋</button>
```xml
<filldb class="FillDB>
    <password></password>
    <schema></schema>
    <url></url>
    <username></username>
</filldb>
```

## Mandatory parameters

<h3 id="password" class="param">password</h3>

<div class="param-level param-level-mandatory">Mandatory
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Password for RDBMS access.

<h3 id="schema" class="param">schema</h3>

<div class="param-level param-level-mandatory">Mandatory
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Schema of the filled tables.

<h3 id="url" class="param">url</h3>

<div class="param-level param-level-mandatory">Mandatory
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
URL of the database.

<h3 id="username" class="param">username</h3>

<div class="param-level param-level-mandatory">Mandatory
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
User for RDBMS access.

## Optional parameters

<h3 id="documentFilter" class="param">documentFilter</h3>

<div class="param-level param-level-default-value">Default value: `true`
</div>
<div class="param-type">Type: <a href="../converter/fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.Expression" class="converter">Expression</a>
</div>
Only process document that satisfy this filter.

<h3 id="jdbcDriver" class="param">jdbcDriver</h3>

<div class="param-level param-level-default-value">Default value: `org.postgresql.Driver`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
JDBC driver for the RDBMS.

<h3 id="sectionFilter" class="param">sectionFilter</h3>

<div class="param-level param-level-default-value">Default value: `true`
</div>
<div class="param-type">Type: <a href="../converter/fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.Expression" class="converter">Expression</a>
</div>
Process only sections that satisfy this filter.

