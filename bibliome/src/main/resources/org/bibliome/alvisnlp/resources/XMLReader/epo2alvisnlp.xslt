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

  <xsl:param name="patent-number">patent-number</xsl:param>
  <xsl:param name="ipc">ipc</xsl:param>
  <xsl:param name="title">title</xsl:param>
  <xsl:param name="applicants">applicants</xsl:param>
  <xsl:param name="inventors">inventors</xsl:param>

  <xsl:template match="/">
    <xsl:element name="alvisnlp-corpus">
      <xsl:apply-templates select="PATDOC/SDOBI"/>
    </xsl:element>
  </xsl:template>

  <xsl:template match="SDOBI">
    <xsl:variable name="pn" select="concat(B100/B190,B100/B110,B100/B130)"/>
    <xsl:element name="document">
      <xsl:attribute name="id">
	<xsl:value-of select="$pn"/>
      </xsl:attribute>

      <xsl:call-template name="meta">
	<xsl:with-param name="name" select="$patent-number"/>
	<xsl:with-param name="value" select="$pn"/>
      </xsl:call-template>

      <xsl:call-template name="meta">
	<xsl:with-param name="name" select="$ipc"/>
	<xsl:with-param name="value" select="translate(B500/B510/B511,' ','')"/>
      </xsl:call-template>

      <xsl:for-each select="B700">
	<xsl:for-each select="B710/B711/SNM">
	  <xsl:call-template name="meta">
	    <xsl:with-param name="name" select="$applicants"/>
	    <xsl:with-param name="value" select="."/>
	  </xsl:call-template>
	</xsl:for-each>
 	<xsl:for-each select="B720/B721/SNM">
	  <xsl:call-template name="meta">
	    <xsl:with-param name="name" select="$inventors"/>
	    <xsl:with-param name="value" select="."/>
	  </xsl:call-template>
	</xsl:for-each>
     </xsl:for-each>



      <xsl:for-each select="B500/B540/B542">
	<xsl:call-template name="section">
	  <xsl:with-param name="name" select="$title"/>
	  <xsl:with-param name="contents" select="."/>
	</xsl:call-template>
      </xsl:for-each>


    </xsl:element>
  </xsl:template>

</xsl:stylesheet>
