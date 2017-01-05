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

  <xsl:param name="pmid">pmid</xsl:param>
  <xsl:param name="year">year</xsl:param>
  <xsl:param name="authors">authors</xsl:param>
  <xsl:param name="substance">substance</xsl:param>
  <xsl:param name="mesh">mesh</xsl:param>
  <xsl:param name="title">title</xsl:param>
  <xsl:param name="abstract">abstract</xsl:param>

  <xsl:template match="/PubmedArticleSet">
    <xsl:element name="alvisnlp-corpus">
      <xsl:apply-templates select="PubmedArticle/MedlineCitation"/>
    </xsl:element>
  </xsl:template>

  <xsl:template match="/MedlineCitationSet">
    <xsl:element name="alvisnlp-corpus">
      <xsl:apply-templates select="MedlineCitation"/>
    </xsl:element>
  </xsl:template>

  <xsl:template match="MedlineCitation">
    <xsl:variable name="id" select="PMID"/>
    <xsl:element name="document">
      <xsl:attribute name="id">
	<xsl:value-of select="$id"/>
      </xsl:attribute>
      <xsl:call-template name="meta">
	<xsl:with-param name="name" select="$pmid"/>
	<xsl:with-param name="value" select="$id"/>
      </xsl:call-template>

      <xsl:apply-templates select="Article/Journal/PubDate/Year"/>
      <xsl:apply-templates select="Article/AuthorList/Author"/>
      <xsl:apply-templates select="ChemicalList/Chemical/NameOfSubstance"/>
      <xsl:apply-templates select="MeshHeadingList/MeshHeading/DescriptorName[@MajorTopicYN = 'Y']"/>

      <xsl:call-template name="section">
	<xsl:with-param name="name" select="$title"/>
	<xsl:with-param name="contents" select="Article/ArticleTitle"/>
      </xsl:call-template>

      <xsl:call-template name="section">
	<xsl:with-param name="name" select="$abstract"/>
	<xsl:with-param name="contents" select="Article/Abstract/AbstractText"/>
      </xsl:call-template>
    </xsl:element>
  </xsl:template>

  <xsl:template match="Year">
    <xsl:call-template name="meta">
      <xsl:with-param name="name" select="$year"/>
      <xsl:with-param name="value" select="."/>
    </xsl:call-template>
  </xsl:template>

  <xsl:template match="Author">
    <xsl:call-template name="meta">
      <xsl:with-param name="name" select="$authors"/>
      <xsl:with-param name="value">
	<xsl:choose>
	  <xsl:when test="ForeName">
	    <xsl:value-of select="concat(LastName, ' ', ForeName)"/>
	  </xsl:when>
	  <xsl:when test="Initials">
	    <xsl:value-of select="concat(LastName, ' ', Initials)"/>
	  </xsl:when>
	  <xsl:otherwise>
	    <xsl:value-of select="ForeName"/>
	  </xsl:otherwise>
	</xsl:choose>
      </xsl:with-param>
    </xsl:call-template>
  </xsl:template>

  <xsl:template match="NameOfSubstance">
    <xsl:call-template name="meta">
      <xsl:with-param name="name" select="$substance"/>
      <xsl:with-param name="value" select="."/>
    </xsl:call-template>
  </xsl:template>

  <xsl:template match="DescriptorName">
    <xsl:call-template name="meta">
      <xsl:with-param name="name" select="$mesh"/>
      <xsl:with-param name="value" select="."/>
    </xsl:call-template>
  </xsl:template>

</xsl:stylesheet>
