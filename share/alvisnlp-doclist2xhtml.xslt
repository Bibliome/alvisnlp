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

  <xsl:param name="synopsis"/>
  <xsl:param name="description"/>
  <xsl:param name="parameters"/>
  <xsl:param name="string-conversion"/>
  <xsl:param name="xml-conversion"/>
  <xsl:param name="modules"/>
  <xsl:param name="converters"/>
  <xsl:param name="libraries"/>
  <xsl:param name="full-names"/>
  <xsl:param name="short-names"/>

  <xsl:param name="title"/>

  <xsl:param name="copyright"/>

  <xsl:template match="alvisnlp-doclist">
    <html>
      <head>
	<title><xsl:value-of select="$title"/></title>
	<link rel="stylesheet" type="text/css" href="alvisnlp-doc.css" />
      </head>
      <body>
	<h1 class="title"><xsl:value-of select="$title"/></h1>
	<h2 class="doctype" id="shortmodules">
	  <xsl:value-of select="bibliome:capital($modules)"/>
	</h2>
	<p class="nav">
	  <a href="#fullmodules">
	    <xsl:value-of select="$full-names"/>
	  </a>
	  <a href="#shortconverters">
	    <xsl:value-of select="$converters"/>
	  </a>
	  <a href="#libraries">
	    <xsl:value-of select="$libraries"/>
	  </a>
	</p>
	<xsl:call-template name="list">
	  <xsl:with-param name="doc-type">module-doc</xsl:with-param>
	  <xsl:with-param name="label">short-target</xsl:with-param>
	</xsl:call-template>

	<h2 class="doctype" id="shortconverters">
	  <xsl:value-of select="bibliome:capital($converters)"/>
	</h2>
	<p class="nav">
	  <a href="#fullconverters">
	    <xsl:value-of select="$full-names"/>
	  </a>
	  <a href="#shortmodules">
	    <xsl:value-of select="$modules"/>
	  </a>
	  <a href="#libraries">
	    <xsl:value-of select="$libraries"/>
	  </a>
	</p>
	<xsl:call-template name="list">
	  <xsl:with-param name="doc-type">converter-doc</xsl:with-param>
	  <xsl:with-param name="label">short-target</xsl:with-param>
	</xsl:call-template>

	<h2 class="doctype" id="libraries">
	  <xsl:value-of select="bibliome:capital($libraries)"/>
	</h2>
	<p class="nav">
	  <a href="#shortmodules">
	    <xsl:value-of select="$modules"/>
	  </a>
	  <a href="#shortconverters">
	    <xsl:value-of select="$converters"/>
	  </a>
	</p>
	<xsl:call-template name="list">
	  <xsl:with-param name="doc-type">library-doc</xsl:with-param>
	  <xsl:with-param name="label">short-target</xsl:with-param>
	</xsl:call-template>

	<hr/>
	<h1 class="title">
	  <xsl:value-of select="bibliome:capital($full-names)"/>
	</h1>

	<h2 class="doctype" id="fullmodules">
	  <xsl:value-of select="bibliome:capital($modules)"/>
	</h2>
	<p class="nav">
	  <a href="#shortmodules">
	    <xsl:value-of select="$short-names"/>
	  </a>
	  <a href="#fullconverters">
	    <xsl:value-of select="$converters"/>
	  </a>
	</p>
	<xsl:call-template name="list">
	  <xsl:with-param name="doc-type">module-doc</xsl:with-param>
	  <xsl:with-param name="label">target</xsl:with-param>
	</xsl:call-template>

	<h2 class="doctype" id="fullconverters">
	  <xsl:value-of select="bibliome:capital($converters)"/>
	</h2>
	<p class="nav">
	  <a href="#shortconverters">
	    <xsl:value-of select="$short-names"/>
	  </a>
	  <a href="#fullmodules">
	    <xsl:value-of select="$modules"/>
	  </a>
	</p>
	<xsl:call-template name="list">
	  <xsl:with-param name="doc-type">converter-doc</xsl:with-param>
	  <xsl:with-param name="label">target</xsl:with-param>
	</xsl:call-template>

	<p class="copyright"><xsl:value-of select="$copyright"/></p>
     </body>
    </html>
  </xsl:template>

  <xsl:template name="list">
    <xsl:param name="doc-type"/>
    <xsl:param name="label"/>
    <table class="doctable">
      <xsl:for-each select="/alvisnlp-doclist/alvisnlp-doc/*[name() = $doc-type]/..">
	<xsl:sort select="@*[name() = $label]"/>
	<xsl:call-template name="item">
	  <xsl:with-param name="doc" select="."/>
	  <xsl:with-param name="label" select="@*[name() = $label]"/>
	</xsl:call-template>
      </xsl:for-each>
    </table>
  </xsl:template>

  <xsl:template name="item">
    <xsl:param name="doc"/>
    <xsl:param name="label"/>
    <xsl:variable name="type">
      <xsl:choose>
	<xsl:when test="module-doc">module</xsl:when>
	<xsl:when test="converter-doc">converter</xsl:when>
	<xsl:when test="library-doc">library</xsl:when>
      </xsl:choose>
    </xsl:variable>
    <tr class="docrow">
      <td class="doccell">
	<xsl:element name="a">
	  <xsl:attribute name="href">
	    <xsl:value-of select="concat($type, '/', $doc/@target, '.html')"/>
	  </xsl:attribute>
	  <xsl:attribute name="class">
	    <xsl:value-of select="concat($type, 'link')"/>
	  </xsl:attribute>
	  <xsl:value-of select="$label"/>
	</xsl:element>
      </td>
      <td class="syncell">
	<xsl:if test="$doc/@beta">
	  <span class="warning">Beta. </span>
	</xsl:if>
	<xsl:if test="$doc/@use-instead">
	  <span class="warning">Obsolete. </span>
	</xsl:if>
	<xsl:apply-templates select="$doc/synopsis/p"/>
      </td>
    </tr>
  </xsl:template>

  <xsl:template match="p">
    <xsl:apply-templates select="*|text()"/>
  </xsl:template>

  <xsl:template match="text()">
    <xsl:value-of select="."/>
  </xsl:template>

  <xsl:template match="warning">
    <span class="warning">
      <xsl:apply-templates select="*|text()"/>
    </span>
  </xsl:template>

  <xsl:template match="emph">
    <em class="text">
      <xsl:value-of select="."/>
    </em>
  </xsl:template>

  <xsl:template match="strong">
    <strong class="text">
      <xsl:value-of select="."/>
    </strong>
  </xsl:template>

  <xsl:template match="list">...</xsl:template>

  <xsl:template match="enum">...</xsl:template>

  <xsl:template match="a">
    <xsl:element name="a">
      <xsl:attribute name="class">text</xsl:attribute>
      <xsl:attribute name="href">
	<xsl:value-of select="@href"/>
      </xsl:attribute>
      <xsl:value-of select="."/>
    </xsl:element>
  </xsl:template>

  <xsl:template match="tag">...</xsl:template>

  <xsl:template match="this"> 
    <span class="this">
      <xsl:value-of select="ancestor::alvisnlp-doc/@short-target"/>
    </span>
  </xsl:template>

  <xsl:template match="param">
    <xsl:value-of select="@name"/>
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

</xsl:stylesheet>
