<!--
Copyright 2016 Institut National de la Recherche Agronomique

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

<xsl:stylesheet version = "1.0"
		xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
		xmlns:bibliome="xalan://org.bibliome.util.xml.Functions"
		>
  
  <xsl:output method="xml" indent="yes" encoding="UTF-8"/>

  <xsl:param name="name-section"/>
  <xsl:param name="synopsis"/>
  <xsl:param name="description"/>
  <xsl:param name="parameters"/>
  <xsl:param name="string-conversion"/>
  <xsl:param name="xml-conversion"/>
  <xsl:param name="functions"/>
  <xsl:param name="modules"/>
  <xsl:param name="converters"/>
  <xsl:param name="full-names"/>
  <xsl:param name="short-names"/>

  <xsl:param name="copyright"/>

  <xsl:template name="target">
    <xsl:value-of select="/alvisnlp-doc/@target"/>
  </xsl:template>

  <xsl:template name="short-target">
    <xsl:value-of select="/alvisnlp-doc/@short-target"/>
  </xsl:template>

  <xsl:template name="full-target">
    <xsl:value-of select="/alvisnlp-doc/@target"/>
  </xsl:template>

  <xsl:template name="type">
    <xsl:choose>
      <xsl:when test="/alvisnlp-doc/module-doc">module</xsl:when>
      <xsl:when test="/alvisnlp-doc/converter-doc">converter</xsl:when>
      <xsl:when test="/alvisnlp-doc/library-doc">library</xsl:when>
    </xsl:choose>
  </xsl:template>

  <xsl:template match="alvisnlp-doc">
    <html>
      <head>
	<title>
	  <xsl:value-of select="@target"/>
	</title>
	<link rel="stylesheet" type="text/css" href="../alvisnlp-doc.css" />
      </head>
      <body>
	<xsl:call-template name="doc-header"/>
	<xsl:apply-templates select="synopsis"/>
	<xsl:apply-templates select="module-doc|converter-doc|library-doc"/>
	<xsl:call-template name="doc-footer"/>
      </body>
    </html>
  </xsl:template>

  <xsl:template match="module-doc">
    <xsl:apply-templates select="description"/>
    <xsl:call-template name="section-header">
      <xsl:with-param name="name" select="$parameters"/>
      <xsl:with-param name="id">params</xsl:with-param>
    </xsl:call-template>
    <xsl:apply-templates select="param-doc">
      <xsl:sort select="@name"/>
    </xsl:apply-templates>
  </xsl:template>

  <xsl:template match="converter-doc">
    <xsl:apply-templates select="string-conversion"/>
    <xsl:apply-templates select="xml-conversion"/>
  </xsl:template>

  <xsl:template match="library-doc">
    <xsl:call-template name="section-header">
      <xsl:with-param name="name" select="$functions"/>
      <xsl:with-param name="id">functions</xsl:with-param>
    </xsl:call-template>
    <xsl:apply-templates select="function-doc">
      <xsl:sort select="@first-ftor"/>
    </xsl:apply-templates>
  </xsl:template>

  <xsl:template match="synopsis|description|string-conversion|xml-conversion">
    <xsl:call-template name="section-header">
      <xsl:with-param name="name">
	<xsl:choose>
	  <xsl:when test="name() = 'synopsis'"><xsl:value-of select="$synopsis"/></xsl:when>
	  <xsl:when test="name() = 'description'"><xsl:value-of select="$description"/></xsl:when>
	  <xsl:when test="name() = 'string-conversion'"><xsl:value-of select="$string-conversion"/></xsl:when>
	  <xsl:when test="name() = 'xml-conversion'"><xsl:value-of select="$xml-conversion"/></xsl:when>
	</xsl:choose>
      </xsl:with-param>
      <xsl:with-param name="id" select="name()"/>
    </xsl:call-template>
    <xsl:apply-templates select="*"/>
  </xsl:template>

  <xsl:template match="param-doc">
    <xsl:call-template name="param-header">
      <xsl:with-param name="param-doc" select="."/>
    </xsl:call-template>
    <xsl:apply-templates select="p"/>
  </xsl:template>

  <xsl:template match="function-doc">
    <xsl:call-template name="function-header">
      <xsl:with-param name="function-doc" select="."/>
    </xsl:call-template>
    <xsl:apply-templates select="p"/>
  </xsl:template>

  <xsl:template name="doc-header">
    <p class="nav"><a href="../index.html">return to list</a></p>
    <h1 class="short"><xsl:call-template name="short-target"/></h1>
    <h1 class="full"><xsl:call-template name="full-target"/></h1>
    <xsl:if test="/alvisnlp-doc/@beta">
      <p class="warning">Note: this module has not been fully tested, it is still in beta testing, bug are likely to manifest.</p>
    </xsl:if>
    <xsl:if test="/alvisnlp-doc/@use-instead">
      <p class="warning">
	Note: this module is <strong>obsolete</strong>, you should use instead 
	<xsl:element name="a">
	  <xsl:attribute name="class">modulelink</xsl:attribute>
	  <xsl:attribute name="href"><xsl:value-of select="concat('../module/', /alvisnlp-doc/@use-instead, '.html')"/></xsl:attribute>
	  <xsl:value-of select="/alvisnlp-doc/@use-instead"/>
	</xsl:element>
      </p>
    </xsl:if>
  </xsl:template>

  <xsl:template name="doc-footer">
    <p class="nav"><a href="../index.html">return to list</a></p>
    <p class="copyright"><em><xsl:value-of select="$copyright"/></em></p>
  </xsl:template>

  <xsl:template name="section-header">
    <xsl:param name="name"/>  <!-- name of the section header -->
    <xsl:param name="id"/>    <!-- id of the section header -->
    <xsl:element name="h2">
      <xsl:attribute name="id">
	<xsl:value-of select="$id"/>
      </xsl:attribute>
      <xsl:attribute name="class">section</xsl:attribute>
      <xsl:value-of select="bibliome:capital($name)"/>
    </xsl:element>
  </xsl:template>

  <xsl:template name="param-header">
    <xsl:param name="param-doc"/>  <!-- param-doc node -->
    <xsl:element name="h3">
      <xsl:attribute name="id">
	<xsl:value-of select="concat('param__', $param-doc/@name)"/>
      </xsl:attribute>
      <xsl:attribute name="class">param</xsl:attribute>
      <xsl:value-of select="@name"/>
    </xsl:element>
    <p class="paraminfo">
      <span class="paramtype">
	<strong class="paramtype">Type: </strong>
	<xsl:element name="a">
	  <xsl:attribute name="href">
	    <xsl:value-of select="concat('../converter/', $param-doc/@type, '.html')"/>
	  </xsl:attribute>
	  <xsl:attribute name="class">converterlink</xsl:attribute>
	  <xsl:value-of select="$param-doc/@short-type"/>
	</xsl:element>
      </span>,
      <span class="paramdefault">
	<xsl:value-of select="$param-doc/@mandatory"/>
      </span>
    </p>
  </xsl:template>

  <xsl:template name="function-header">
    <xsl:param name="function-doc"/>
    <xsl:element name="h3">
      <xsl:attribute name="id">
	<xsl:value-of select="concat('fun__', $function-doc/@first-ftor)"/>
      </xsl:attribute>
      <xsl:attribute name="class">fun</xsl:attribute>
      <xsl:value-of select="@first-ftor"/>
    </xsl:element>
    <p class="funsyn">
      <xsl:value-of select="$function-doc/@synopsis"/>
    </p>    
  </xsl:template>

  <xsl:template match="text()">
    <xsl:value-of select="."/>
  </xsl:template>

  <xsl:template match="warning">
    <span class="warning">
      <xsl:apply-templates select="*|text()"/>
    </span>
  </xsl:template>

  <xsl:template match="li">
    <xsl:element name="li">
      <xsl:attribute name="class">
	<xsl:choose>
	  <xsl:when test="name(..) = 'ol'">enumitem</xsl:when>
	  <xsl:otherwise>listitem</xsl:otherwise>
	</xsl:choose>
      </xsl:attribute>
      <xsl:apply-templates select="*|text()"/>
    </xsl:element>
  </xsl:template>

  <xsl:template match="a">
    <xsl:element name="a">
      <xsl:attribute name="class">text</xsl:attribute>
      <xsl:attribute name="href">
	<xsl:value-of select="@href"/>
      </xsl:attribute>
      <xsl:value-of select="."/>
    </xsl:element>
  </xsl:template>

  <xsl:template match="tag">
    <xsl:if test="@indent">
      <br/>
    </xsl:if>
    <xsl:element name="span">
      <xsl:attribute name="class">tag</xsl:attribute>
      <xsl:if test="@indent">
	<xsl:attribute name="style">
	  <xsl:value-of select="concat('margin-left: ', @indent, '0mm')"/>
	</xsl:attribute>
      </xsl:if>
      <xsl:value-of select="concat('&lt;', @name)"/>
      <xsl:apply-templates select="attr"/>
      <xsl:choose>
	<xsl:when test="count(tag|text) = 0"> <!-- empty tag -->
	  <xsl:text>/></xsl:text>
	</xsl:when>
	<xsl:otherwise>
	  <xsl:text>></xsl:text>
	  <xsl:choose>
	    <xsl:when test="count(text) = 0"> <!-- sub-elements -->
	      <xsl:for-each select="tag">
		<!-- TODO: sub-tag pre-marker -->
		<xsl:apply-templates select="."/>
		<!-- TODO: sub-tag post-marker -->
	      </xsl:for-each>
	    </xsl:when>
	    <xsl:otherwise> <!-- text or mixed tag/text -->
	      <xsl:apply-templates select="tag|text"/>
	    </xsl:otherwise>
	  </xsl:choose>
	  <xsl:value-of select="concat('&lt;/', @name, '>')"/>
	</xsl:otherwise>
      </xsl:choose>
    </xsl:element>
  </xsl:template>

  <xsl:template match="attr">
    <span class="attr">
      <xsl:value-of select="concat(' ', @name, '=')"/>"<xsl:value-of select="@value"/>"<xsl:text/>
    </span>
  </xsl:template>

  <xsl:template match="text">
    <span class="tagtext">
      <xsl:value-of select="."/>
    </span>
  </xsl:template>

  <xsl:template match="this"> 
    <span class="this">
      <xsl:value-of select="/alvisnlp-doc/@short-target"/>
    </span>
  </xsl:template>

  <xsl:template match="param">
    <xsl:variable name="module">
      <xsl:choose>
	<xsl:when test="@module">
	  <xsl:value-of select="@module"/>
	</xsl:when>
	<xsl:otherwise>
	  <xsl:value-of select="/alvisnlp-doc/@target"/>
	</xsl:otherwise>
      </xsl:choose>
    </xsl:variable>
    <xsl:element name="a">
      <xsl:attribute name="class">paramlink</xsl:attribute>
      <xsl:attribute name="href">
	<xsl:value-of select="concat('../module/', $module, '.html#param__', @name)"/>
      </xsl:attribute>
      <xsl:value-of select="@name"/>
    </xsl:element>
  </xsl:template>

  <xsl:template match="module">
    <xsl:element name="a">
      <xsl:attribute name="class">modulelink</xsl:attribute>
      <xsl:attribute name="href">
	<xsl:value-of select="concat('../module/', @name, '.html')"/>
      </xsl:attribute>
      <xsl:value-of select="@name"/>
    </xsl:element>
  </xsl:template>

  <xsl:template match="converter">
    <xsl:element name="a">
      <xsl:attribute name="class">converterlink</xsl:attribute>
      <xsl:attribute name="href">
	<xsl:value-of select="concat('../converter/', @name, '.html')"/>
      </xsl:attribute>
      <xsl:value-of select="@name"/>
    </xsl:element>
  </xsl:template>

  <xsl:template match="library">
    <xsl:element name="a">
      <xsl:attribute name="class">librarylink</xsl:attribute>
      <xsl:attribute name="href">
	<xsl:value-of select="concat('../library', @name, '.html')"/>
      </xsl:attribute>
    </xsl:element>
  </xsl:template>

  <xsl:template match="function">
    <xsl:variable name="library">
      <xsl:choose>
	<xsl:when test="@library">
	  <xsl:value-of select="@library"/>
	</xsl:when>
	<xsl:otherwise>
	  <xsl:value-of select="/alvisnlp-doc/@target"/>
	</xsl:otherwise>
      </xsl:choose>
    </xsl:variable>
    <xsl:element name="a">
      <xsl:attribute name="class">functionlink</xsl:attribute>
      <xsl:attribute name="href">
	<xsl:value-of select="concat('../library', $library, '.html#fun__', @name)"/>
      </xsl:attribute>
    </xsl:element>
  </xsl:template>

  <xsl:template match="xverb">
    <xsl:apply-templates select="*|text()" mode="xverb"/>
  </xsl:template>

  <xsl:template match="text()" mode="xverb">
    <span class="xverb">
      <xsl:value-of select="."/>
    </span>
  </xsl:template>

  <xsl:template match="*" mode="xverb">
    <span class="xverb">&lt;</span>
    <span class="xverbtag"><xsl:value-of select="name()"/></span>
    <xsl:for-each select="@*">
      <span class="xverbattr"><xsl:text disable-output-escaping="yes">&amp;</xsl:text>nbsp;<xsl:value-of select="name()"/></span>
      <span class="xverb">=</span>
      <span class="xverbstr">"<xsl:value-of select="."/>"</span>
    </xsl:for-each>
    <xsl:choose>
      <xsl:when test=". = ''">
	<span class="xverb">/&gt;</span>
      </xsl:when>
      <xsl:otherwise>
	<span class="xverb">&gt;</span>
	<xsl:apply-templates select="*|text()" mode="xverb"/>
	<span class="xverb">&lt;/</span>
	<span class="xverbtag"><xsl:value-of select="name()"/></span>
	<span class="xverb">&gt;</span>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>

  <xsl:template match="*">
    <xsl:copy>
      <xsl:apply-templates select="*|text()"/>
    </xsl:copy>
  </xsl:template>

</xsl:stylesheet>
