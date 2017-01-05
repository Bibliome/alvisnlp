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

  <xsl:template match="/document">
    <xsl:element name="alvisnlp-corpus">
      <xsl:element name="document">
	<xsl:attribute name="id">
	  <xsl:value-of select="id"/>
	</xsl:attribute>
	<xsl:call-template name="meta">
	  <xsl:with-param name="name" select="url"/>
	  <xsl:with-param name="value" select="normalize-space(url)"/>
	</xsl:call-template>

	<xsl:call-template name="meta">
	  <xsl:with-param name="name">dummy</xsl:with-param>
	  <xsl:with-param name="value">foo</xsl:with-param>
	</xsl:call-template>

	<xsl:apply-templates select="p"/>
      </xsl:element>
    </xsl:element>
  </xsl:template>

  <xsl:template match="p">
    <xsl:element name="section">
      <xsl:attribute name="name">section</xsl:attribute>
      <xsl:element name="contents">
	<xsl:value-of select="normalize-space(.)"/>
      </xsl:element>
    </xsl:element>
  </xsl:template>


</xsl:stylesheet>
