<!--
    Copyright 2016, 2017 Institut National de la Recherche Agronomique

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
-->


<xsl:stylesheet version="1.0"
		xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
		xmlns:str="http://exslt.org/strings"
		extension-element-prefixes="str"
		>

  <xsl:output method="text" />

  <xsl:param name="nl"><xsl:text>
</xsl:text></xsl:param>

  <xsl:template match="/">
    <xsl:apply-templates/>
  </xsl:template>

  <xsl:template match="alvisnlp-doc">
    <xsl:variable name="class">
      <xsl:value-of select="substring-before(name(module-doc|converter-doc|library-doc), '-')"/>
    </xsl:variable>
    <xsl:value-of select="concat('&lt;h1 class=&quot;' , $class, '&quot;>', @short-target, '&lt;/h1>', $nl, $nl)"/>
    <xsl:apply-templates select="synopsis"/>
    <xsl:apply-templates select="module-doc|converter-doc|library-doc|plan-doc"/>
  </xsl:template>

  <xsl:template match="alvisnlp-supported-modules">
    <xsl:value-of select="concat('# Alvisnlp/ML Supported Modules', $nl, $nl)"/>
    <xsl:apply-templates select="module-item">
      <xsl:sort select="@target"/>
    </xsl:apply-templates>
  </xsl:template>

  <xsl:template match="alvisnlp-supported-libraries">
    <xsl:value-of select="concat('# Alvisnlp/ML Supported Libraries', $nl, $nl)"/>
    <xsl:apply-templates select="library-item">
      <xsl:sort select="@short-target"/>
    </xsl:apply-templates>
  </xsl:template>

  <xsl:template match="alvisnlp-supported-converters">
    <xsl:value-of select="concat('# Alvisnlp/ML Supported Converters', $nl, $nl)"/>
    <xsl:apply-templates select="converter-item">
      <xsl:sort select="@short-target"/>
    </xsl:apply-templates>
  </xsl:template>

  <xsl:template match="module-item|converter-item|library-item|plan-item">
    <xsl:variable name="class">
      <xsl:value-of select="substring-before(name(), '-')"/>
    </xsl:variable>
    <xsl:variable name="list">
      <xsl:choose>
	<xsl:when test="name() = 'library-item'">libraries</xsl:when>
	<xsl:otherwise>
	  <xsl:value-of select="concat($class, 's')"/>
	</xsl:otherwise>
      </xsl:choose>
    </xsl:variable>
    <xsl:text>&lt;h2>
&lt;a href="{{ &apos;/reference/</xsl:text>
    <xsl:value-of select="concat($class, '/', @target)"/>
    <xsl:text>&apos; | relative_url }}" class="</xsl:text>
    <xsl:value-of select="concat($class, '&quot;>', @short-target)"/>
    <xsl:text>&lt;/a>
&lt;/h2>

</xsl:text>
    <!--<xsl:value-of select='concat("&lt;h2>", $nl, "&lt;a href={{ &apos;/reference/", $class, "/", @target, "&apos; | relative_url }}>", @short-target, "&lt;/a>", $nl, "&lt;/h2>", $nl, $nl)'/>-->
  </xsl:template>

  <xsl:template match="synopsis">
    <xsl:value-of select="concat('## Synopsis', $nl, $nl)"/>
    <xsl:apply-templates/>
  </xsl:template>

  <xsl:template match="module-doc">
    <xsl:if test="/alvisnlp-doc/@beta = 'true'">
      <xsl:value-of select="concat('**This module is experimental.**', $nl, $nl)"/>
    </xsl:if>
    <xsl:if test="/alvisnlp-doc/@use-instead != ''">
      <xsl:value-of select="concat('**This module is obsolete, superceded by ', /alvisnlp-doc/@use-instead, '**', $nl, $nl)"/>
    </xsl:if>
    <xsl:apply-templates select="description"/>
    <xsl:value-of select="concat('## Parameters', $nl, $nl)"/>
    <xsl:apply-templates select="param-doc[not(@default-value) and @mandatory = 'required']">
      <xsl:sort select="@name"/>
    </xsl:apply-templates>
    <xsl:apply-templates select="param-doc[not(@default-value) and @mandatory = 'optional']">
      <xsl:sort select="@name"/>
    </xsl:apply-templates>
    <xsl:apply-templates select="param-doc[@default-value and @name != 'active' and @name != 'userFunctions']">
      <xsl:sort select="@name"/>
    </xsl:apply-templates>
  </xsl:template>
  
  <xsl:template match="description">
    <xsl:value-of select="concat('## Description', $nl, $nl)"/>
    <xsl:apply-templates/>
  </xsl:template>
  
  <xsl:template match="param-doc">
    <xsl:value-of select="concat('&lt;a name=&quot;', @name, '&quot;>', $nl, $nl)"/>
    <xsl:value-of select="concat('### ', @name, $nl, $nl)"/>
    <xsl:choose>
      <xsl:when test="@default-value">
	<xsl:text>&lt;div class="param-level param-level-default-value"></xsl:text>
	<xsl:value-of select="concat('Default value: `', @default-value, '`', $nl)"/>
	<xsl:text>&lt;/div>
</xsl:text>
      </xsl:when>
      <xsl:when test="@mandatory = 'true'">
	<xsl:text>&lt;div class="param-level param-level-mandatory"></xsl:text>
	<xsl:value-of select="concat('Mandatory', $nl)"/>
	<xsl:text>&lt;/div>
</xsl:text>
      </xsl:when>
      <xsl:otherwise>
	<xsl:text>&lt;div class="param-level param-level-optional"></xsl:text>
	<xsl:value-of select="concat('Optional', $nl)"/>
	<xsl:text>&lt;/div>
</xsl:text>
      </xsl:otherwise>
    </xsl:choose>
    <xsl:text>&lt;div class="param-type"></xsl:text>
    <xsl:value-of select="concat('Type: &lt;a href=&quot;../converter/', @type, '&quot; class=&quot;converter&quot;>', @short-type, '&lt;/a>', $nl)"/>
	<xsl:text>&lt;/div>
</xsl:text>
    <xsl:apply-templates/>
  </xsl:template>
  
  <xsl:template match="converter-doc">
    <xsl:apply-templates select="string-conversion"/>
    <xsl:apply-templates select="xml-conversion"/>
  </xsl:template>
  
  <xsl:template match="string-conversion">
    <xsl:value-of select="concat('## String conversion', $nl, $nl)"/>
    <xsl:apply-templates select="*"/>
  </xsl:template>
  
  <xsl:template match="xml-conversion">
    <xsl:value-of select="concat('## XML conversion', $nl, $nl)"/>
    <xsl:apply-templates select="*"/>
  </xsl:template>
  
  <xsl:template match="library-doc">
    <xsl:value-of select="concat('## Functons', $nl, $nl)"/>
    <xsl:apply-templates select="function-doc">
      <xsl:sort select="@first-ftor"/>
    </xsl:apply-templates>
  </xsl:template>
  
  <xsl:template match="function-doc">
    <xsl:value-of select="concat('&lt;a name=&quot;', @first-ftor, '&quot;&gt;', $nl, $nl)"/>
    <xsl:value-of select="concat('### ', @first-ftor, $nl, $nl)"/>
    <xsl:value-of select="concat('`', @synopsis, '`', $nl, $nl)"/>
    <xsl:apply-templates select="*"/>
  </xsl:template>
  
  <xsl:template match="this">
    <xsl:value-of select="concat('*', /alvisnlp-doc/@short-target, '*')"/>
  </xsl:template>

  <xsl:template match="param">
    <xsl:choose>
      <xsl:when test="@module">
	<xsl:value-of select="concat('&lt;a href=&quot;../module/', @module, '#', @name, ., '&quot; class=&quot;param&quot;>', @module, '#', @name, ., '&lt;/a>')"/>
      </xsl:when>
      <xsl:otherwise>
	<xsl:value-of select="concat('&lt;a href=&quot;#', @name, ., '&quot; class=&quot;param&quot;>', @name, ., '&lt;/a>')"/>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>
  
  <xsl:template match="function">
    <xsl:choose>
      <xsl:when test="@library">
	<xsl:value-of select="concat('&lt;a href=&quot;../library/', @library, '#', @name, ., '&quot; class=&quot;function&quot;>', @library, '#', @name, ., '&lt;/a>')"/>
      </xsl:when>
      <xsl:otherwise>
	<xsl:value-of select="concat('&lt;a href=&quot;#', @name, ., '&quot; class=&quot;function&quot;>', @name, ., '&lt;/a>')"/>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>

  <xsl:template match="module">
    <xsl:value-of select="concat('&lt;a href=&quot;../module/', @name, ., '&quot; class=&quot;module&quot;>', @name, ., '&lt;/a>')"/>
  </xsl:template>

  <xsl:template match="converter">
    <xsl:value-of select="concat('&lt;a href=&quot;../converter/', @name, ., '&quot; class=&quot;converter&quot;>', @name, ., '&lt;/a>')"/>
  </xsl:template>

  <xsl:template match="library">
    <xsl:value-of select="concat('&lt;a href=&quot;../library/', @name, ., '&quot; class=&quot;library&quot;>', @name, ., '&lt;/a>')"/>
  </xsl:template>
  
  <xsl:template match="a">
    <xsl:text>[</xsl:text>
    <xsl:apply-templates select="*|text()" />
    <xsl:value-of select="concat('](', @href, ')')"/>
  </xsl:template>

  <xsl:template match="ul">
    <xsl:value-of select="$nl"/>
    <xsl:apply-templates select="*|text()" />
    <xsl:value-of select="$nl"/>
  </xsl:template>

  <xsl:template match="ol">
    <xsl:value-of select="$nl"/>
    <xsl:apply-templates select="*|text()" />
    <xsl:value-of select="$nl"/>
  </xsl:template>

  <xsl:template match="li">
    <xsl:for-each select="ancestor::ul|ol">
      <xsl:if test="not(last())">
	<xsl:text>  </xsl:text>
      </xsl:if>
    </xsl:for-each>
    <xsl:choose>
      <xsl:when test="name(..) = 'ul'">
	<xsl:text>* </xsl:text>
      </xsl:when>
      <xsl:otherwise>
	<xsl:value-of select="concat(count(preceding-sibling::li) + 1, '. ')"/>
      </xsl:otherwise>
    </xsl:choose>
    <xsl:apply-templates select="*|text()" />
    <xsl:value-of select="$nl"/>
  </xsl:template>

  <xsl:template match="h3">
    <xsl:text>### </xsl:text>
    <xsl:apply-templates select="*|text()" />
  </xsl:template>

  <xsl:template match="h4">
    <xsl:text>#### </xsl:text>
    <xsl:apply-templates select="*|text()" />
  </xsl:template>

  <xsl:template match="h5">
    <xsl:text>#### </xsl:text>
    <xsl:apply-templates select="*|text()" />
  </xsl:template>

  <xsl:template match="p">
    <xsl:apply-templates select="*|text()" />
    <xsl:value-of select="concat($nl, $nl)"/>
  </xsl:template>

  <xsl:template match="br">
    <xsl:value-of select="$nl"/>
  </xsl:template>

  <xsl:template match="strong">
    <xsl:text>**</xsl:text>
    <xsl:apply-templates select="*|text()" />
    <xsl:text>**</xsl:text>
  </xsl:template>

  <xsl:template match="em">
    <xsl:text>*</xsl:text>
    <xsl:apply-templates select="*|text()" />
    <xsl:text>*</xsl:text>
  </xsl:template>

  <xsl:template match="code">
    <xsl:text>`</xsl:text>
    <xsl:apply-templates select="*|text()" />
    <xsl:text>`</xsl:text>
  </xsl:template>
  
  <xsl:template match="*">
    <xsl:message>Default template for tag: <xsl:value-of select="name()"/></xsl:message>
    <xsl:apply-templates select="*|text()" />
  </xsl:template>

  <xsl:template match="text()">
    <xsl:variable name="clean">
      <xsl:value-of select="normalize-space(.)"/>
    </xsl:variable>
    <xsl:if test="$clean != ''">
      <xsl:value-of select="str:replace(., '&#x0A;    ', $nl)"/>
    </xsl:if>
  </xsl:template>

  <xsl:template match="xverb">
    <xsl:value-of select="concat('```xml', $nl)"/>
    <xsl:apply-templates select="*|text()" mode="xverb" />
    <xsl:value-of select="concat($nl, '```', $nl, $nl)"/>
  </xsl:template>

  <xsl:template match="*" mode="xverb">
    <xsl:value-of select="concat('&lt;', name())"/>
    <xsl:for-each select="@*">
      <xsl:value-of select="concat(' ', name(), '=&quot;', ., '&quot;')"/>
    </xsl:for-each>
    <xsl:choose>
      <xsl:when test='not(*|text())'>
	<xsl:text>/></xsl:text>
      </xsl:when>
      <xsl:otherwise>
	<xsl:text>></xsl:text>
	<xsl:apply-templates select="*|text()" mode="xverb"/>
	<xsl:value-of select="concat('&lt;/', name(), '>')"/>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>

  <xsl:template match="text()" mode="xverb">
    <xsl:value-of select="."/>
  </xsl:template>
</xsl:stylesheet>
