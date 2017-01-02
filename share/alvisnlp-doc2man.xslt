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
  
  <xsl:output method="text" encoding="UTF-8"/>

  <xsl:param name="name-section"/>
  <xsl:param name="synopsis"/>
  <xsl:param name="description"/>
  <xsl:param name="parameters"/>
  <xsl:param name="string-conversion"/>
  <xsl:param name="xml-conversion"/>
  <xsl:param name="modules"/>
  <xsl:param name="converters"/>
  <xsl:param name="full-names"/>
  <xsl:param name="short-names"/>

  <xsl:param name="section">7.alvisnlp</xsl:param>
  <xsl:param name="source"/>
  <xsl:param name="manual"/>

  <xsl:template name="target">
    <xsl:value-of select="/alvisnlp-doc/@target"/>
  </xsl:template>

  <xsl:template name="short-target">
    <xsl:value-of select="/alvisnlp-doc/@short-target"/>
  </xsl:template>

  <xsl:template name="type">
    <xsl:choose>
      <xsl:when test="/alvisnlp-doc/module-doc">module</xsl:when>
      <xsl:when test="/alvisnlp-doc/converter-doc">converter</xsl:when>
    </xsl:choose>
  </xsl:template>

  <xsl:template match="alvisnlp-doc">
    <xsl:call-template name="doc-header">
      <xsl:with-param name="doc" select="."/>
    </xsl:call-template>
    <xsl:apply-templates select="synopsis"/>
    <xsl:apply-templates select="module-doc|converter-doc"/>
    <xsl:call-template name="doc-footer">
      <xsl:with-param name="doc" select="."/>
    </xsl:call-template>
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

  <xsl:template name="doc-header">
    <xsl:param name="doc"/> <!-- alvisnlp-doc node -->
.TH "<xsl:value-of select="$doc/@short-target"/>" "<xsl:value-of select="$section"/>" "<xsl:value-of select="$doc/@date"/>" "<xsl:value-of select="$source"/>" "<xsl:value-of select="$manual"/>"
  </xsl:template>

  <xsl:template name="doc-footer">
    <xsl:param name="doc"/> <!-- alvisnlp-doc node -->
  </xsl:template>

  <xsl:template name="section-header">
    <xsl:param name="name"/>  <!-- name of the section header -->
    <xsl:param name="id"/>    <!-- id of the section header -->
.SH <xsl:value-of select="bibliome:upper($name)"/><xsl:text>
</xsl:text>
  </xsl:template>

  <xsl:template name="param-header">
    <xsl:param name="param-doc"/>  <!-- param-doc node -->
.TP
.I <xsl:value-of select="@name"/>
.RS
Type: <xsl:value-of select="$param-doc/@short-type"/>
.P
<xsl:value-of select="$param-doc/@mandatory"/>
.RE
  </xsl:template>

  <xsl:template match="p">
    <xsl:text>
</xsl:text>
    <xsl:apply-templates select="*|text()"/>
.P
</xsl:template>

  <xsl:template match="text()">
    <xsl:variable name="contents" select="normalize-space(.)"/>
    <xsl:if test="starts-with($contents, '.')">.cc _
</xsl:if>
    <xsl:value-of select="$contents"/>
    <xsl:if test="starts-with($contents, '.')">
_cc .</xsl:if>
  </xsl:template>

  <xsl:template match="nl">
.P
</xsl:template>

  <xsl:template match="emph">
.I "<xsl:value-of select="."/>"<xsl:text>
</xsl:text>
  </xsl:template>

  <xsl:template match="strong">
.B "<xsl:value-of select="."/>"<xsl:text>
</xsl:text>
  </xsl:template>

  <xsl:template match="list">
.RS
    <xsl:apply-templates select="li"/>
.RE
  </xsl:template>

  <xsl:template match="enum">
.RS
    <xsl:apply-templates select="li"/>
.RE
  </xsl:template>

  <xsl:template match="li">
    <xsl:variable name="bullet">
      <xsl:choose>
	<xsl:when test="name(..) = 'enum'">
	  <xsl:value-of select="position()"/>
	</xsl:when>
	<xsl:otherwise>*</xsl:otherwise>
      </xsl:choose>
    </xsl:variable>
.TP
*
<xsl:apply-templates select="*|text()"/>
  </xsl:template>

  <xsl:template match="a">
    <xsl:value-of select="concat(. ,'[', @href, ']')"/>
  </xsl:template>

  <xsl:template match="tag">
.SM
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
.RS
	      <xsl:apply-templates select="."/>
.RE
	    </xsl:for-each>
	  </xsl:when>
	  <xsl:otherwise> <!-- text or mixed tag/text -->
	    <xsl:apply-templates select="tag|text"/>
	  </xsl:otherwise>
	</xsl:choose>
	<xsl:value-of select="concat('&lt;/', @name, '>')"/>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>

  <xsl:template match="attr">
.SM <xsl:value-of select="concat(@name, '=')"/>"<xsl:value-of select="@value"/>"<xsl:text/>
  </xsl:template>

  <xsl:template match="text">
.SM <xsl:value-of select="."/>
  </xsl:template>

  <xsl:template match="this">
.I <xsl:value-of select="/alvisnlp-doc/@short-target"/><xsl:text>
</xsl:text>
  </xsl:template>

  <xsl:template match="param">
    <xsl:variable name="module">
      <xsl:choose>
	<xsl:when test="@module">
	  <xsl:value-of select="@module"/>
	</xsl:when>
	<xsl:otherwise>
	  <xsl:value-of select="../../@target"/>
	</xsl:otherwise>
      </xsl:choose>
    </xsl:variable>
.I <xsl:value-of select="@name"/><xsl:text>
</xsl:text>
  </xsl:template>

  <xsl:template match="module">
.I <xsl:value-of select="@name"/>
  </xsl:template>

  <xsl:template match="converter">
.I <xsl:value-of select="@name"/>
  </xsl:template>
</xsl:stylesheet>
