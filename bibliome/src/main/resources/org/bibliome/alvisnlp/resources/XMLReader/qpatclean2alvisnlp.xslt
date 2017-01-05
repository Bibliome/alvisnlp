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


<xsl:stylesheet version = "1.0"
		xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
		>

  <xsl:import href="alvisnlp-util.xslt"/>

  <xsl:param name="source"/>
  <xsl:param name="source-basename"/>
  <xsl:param name="id">patent-number</xsl:param>
  <xsl:param name="applicants">patent-assignees</xsl:param>
  <xsl:param name="inventors">inventors</xsl:param>
  <xsl:param name="epc">epc</xsl:param>
  <xsl:param name="date">priority-date</xsl:param>
  <xsl:param name="title">title</xsl:param>
  <xsl:param name="abstract">abstract</xsl:param>
  <xsl:param name="claims">claims</xsl:param>

  <xsl:template match="/document-list">
    <xsl:element name="alvisnlp-corpus">
      <xsl:apply-templates select="document"/>
    </xsl:element>
  </xsl:template>

  <xsl:template match="document">
    <xsl:element name="document">
      <xsl:attribute name="id">
	<xsl:choose>
	  <xsl:when test="@id">
	    <xsl:value-of select="@id"/>
	  </xsl:when>
	  <xsl:otherwise>
	    <xsl:value-of select="substring-before($source-basename, '.')"/>
	  </xsl:otherwise>
	</xsl:choose>
      </xsl:attribute>

      <xsl:for-each select="id">
	<xsl:call-template name="meta">
	  <xsl:with-param name="name" select="$id"/>
	  <xsl:with-param name="value" select="translate(@type, ' ', '')"/>
	</xsl:call-template>
      </xsl:for-each>

      <xsl:for-each select="field[@canonical-name = 'PA']/par">
	<xsl:call-template name="meta">
	  <xsl:with-param name="name" select="$applicants"/>
	  <xsl:with-param name="value" select="."/>
	</xsl:call-template>
      </xsl:for-each>

      <xsl:for-each select="field[@canonical-name = 'IN']/par">
	<xsl:call-template name="meta">
	  <xsl:with-param name="name" select="$inventors"/>
	  <xsl:with-param name="value" select="."/>
	</xsl:call-template>
      </xsl:for-each>

      <xsl:for-each select="field[@canonical-name = 'EPC']/par">
	<xsl:call-template name="meta">
	  <xsl:with-param name="name" select="$epc"/>
	  <xsl:with-param name="value" select="translate(.,'/',':')"/>
	</xsl:call-template>
      </xsl:for-each>

      <xsl:for-each select="field[@canonical-name = 'priority-date']/par">
	<xsl:call-template name="meta">
	  <xsl:with-param name="name" select="$date"/>
	  <xsl:with-param name="value" select="."/>
	</xsl:call-template>
      </xsl:for-each>

      <xsl:for-each select="field[@canonical-name = 'TI']">
	<xsl:if test="par">
	  <xsl:call-template name="section">
	    <xsl:with-param name="name" select="$title"/>
	    <xsl:with-param name="contents" select="string(par)"/>
	  </xsl:call-template>
	  <xsl:call-template name="meta">
	    <xsl:with-param name="name" select="$title"/>
	    <xsl:with-param name="value" select="string(par)"/>
	  </xsl:call-template>
	</xsl:if>
      </xsl:for-each>

      <xsl:for-each select="field[@canonical-name = 'AB']">
	<xsl:if test="par">
	  <xsl:call-template name="section">
	    <xsl:with-param name="name" select="$abstract"/>
	    <xsl:with-param name="contents" select="string(par)"/>
	  </xsl:call-template>
	</xsl:if>
      </xsl:for-each>

      <xsl:for-each select="field[@canonical-name = 'CLMS']/par">
	<xsl:call-template name="section">
	  <xsl:with-param name="name" select="$claims"/>
	  <xsl:with-param name="contents" select="."/>
	</xsl:call-template>
      </xsl:for-each>

    </xsl:element>
  </xsl:template>

</xsl:stylesheet>