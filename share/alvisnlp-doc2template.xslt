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
    <!-- TODO: document header -->
  </xsl:template>

  <xsl:template name="doc-footer">
    <xsl:param name="doc"/> <!-- alvisnlp-doc node -->
    <!-- TODO: document footer -->
  </xsl:template>

  <xsl:template name="section-header">
    <xsl:param name="name"/>  <!-- name of the section header -->
    <xsl:param name="id"/>    <!-- id of the section header -->
    <!-- TODO: section header, including section label -->
  </xsl:template>

  <xsl:template name="param-header">
    <xsl:param name="param-doc"/>  <!-- param-doc node -->
    <!-- TODO: parameter header, including parameter label -->
  </xsl:template>

  <xsl:template match="p">
    <!-- TODO: paragraph pre-marker -->
    <xsl:apply-templates select="*|text()"/>
    <!-- TODO: paragraph post-marker -->
  </xsl:template>

  <xsl:template match="text()">
    <xsl:value-of select="normalize-space(.)"/>
  </xsl:template>

  <xsl:template match="nl">
    <!-- TODO: newline marker -->
    <!--<xsl:text>
</xsl:text>-->
    <!--<xsl:value-of select="&#10;"/>-->
  </xsl:template>

  <xsl:template match="emph">
    <!-- TODO: emphasis pre-marker -->
    <xsl:value-of select="."/>
    <!-- TODO: emphasis post-marker -->
  </xsl:template>

  <xsl:template match="strong">
    <!-- TODO: strong pre-marker -->
    <xsl:value-of select="."/>
    <!-- TODO: strong post-marker -->
  </xsl:template>

  <xsl:template match="list">
    <!-- TODO: list start -->
    <xsl:apply-templates select="li"/>
    <!-- TODO: list end -->
  </xsl:template>

  <xsl:template match="enum">
    <!-- TODO: enumeration start -->
    <xsl:apply-templates select="li"/>
    <!-- TODO: enumeration end -->
  </xsl:template>

  <xsl:template match="li">
    <xsl:variable name="bullet">
      <xsl:choose>
	<xsl:when test="name(..) = 'enum'">
	  <xsl:value-of select="position()"/>
	</xsl:when>
	<xsl:otherwise>
	  <!-- TODO: list bullet -->
	</xsl:otherwise>
      </xsl:choose>
    </xsl:variable>
    <!-- TODO: item pre-marker, may use $bullet -->
    <xsl:apply-templates select="*|text()"/>
    <!-- TODO: item post-marker -->
  </xsl:template>

  <xsl:template match="a">
    <!-- TODO: external reference pre-marker -->
    <xsl:value-of select="."/>
    <!-- TODO: external reference post-marker -->
  </xsl:template>

  <xsl:template match="tag">
    <!-- TODO: tag pre-marker -->
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
    <!-- TODO: tag post-marker -->
  </xsl:template>

  <xsl:template match="attr">
    <!-- TODO: attribute pre-marker -->
    <xsl:value-of select="concat(' ', @name, '=')"/>"<xsl:value-of select="@value"/>"<xsl:text/>
    <!-- TODO: attribute post-marker -->
  </xsl:template>

  <xsl:template match="text">
    <!-- TODO: text pre-marker -->
    <xsl:value-of select="."/>
    <!-- TODO: text post-marker -->
  </xsl:template>

  <xsl:template match="this"> 
    <!-- TODO: doc pre-marker -->
   <xsl:value-of select="/alvisnlp-doc/@target"/>
    <!-- TODO: doc post-marker -->
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
    <!-- TODO: parameter reference pre-marker -->
    <!-- TODO: parameter reference, using $module and @name -->
    <!-- TODO: parameter reference post-marker -->
  </xsl:template>

  <xsl:template match="module">
    <!-- TODO: module reference pre-marker -->
    <xsl:value-of select="@name"/>
    <!-- TODO: module reference post-marker -->
  </xsl:template>

  <xsl:template match="converter">
    <!-- TODO: converter reference pre-marker -->
    <xsl:value-of select="@name"/>
    <!-- TODO: converter reference post-marker -->
  </xsl:template>
</xsl:stylesheet>
