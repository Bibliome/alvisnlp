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
                xmlns:a="xalan://org.bibliome.alvisnlp.modules.xml.XMLReader2"
                xmlns:inline="http://bibliome.jouy.inra.fr/alvisnlp/bibliome-module-factory/inline"
		xmlns:inra="http://record.prodinra.inra.fr"
                extension-element-prefixes="a inline"
		>

  <xsl:template match="/">
    <xsl:apply-templates select="Records/inra:*"/>
  </xsl:template>

  <xsl:template match="inra:*">
    <a:document xpath-id="identifier">
      <a:feature name="document-type" xpath-value="name()"/>
      <xsl:apply-templates select="year"/>
      <xsl:apply-templates select="creationDate"/>
      <xsl:apply-templates select="language"/>
      <xsl:apply-templates select="link"/>
      <xsl:apply-templates select="targetAudience"/>
      <xsl:apply-templates select="itemType"/>
      <xsl:apply-templates select="thematic"/>
      <xsl:apply-templates select="keywords/keyword"/>
      <xsl:apply-templates select="collection"/>
      <xsl:apply-templates select="articleInfos/peerReviewed"/>
      <xsl:apply-templates select="creator/author"/>
      <xsl:apply-templates select="title"/>
      <xsl:apply-templates select="abstract"/>
   </a:document>
  </xsl:template>

  <xsl:template match="*">
    <a:feature xpath-name="name()" xpath-value="."/>
  </xsl:template>

  <xsl:template match="collection">
    <a:feature name="collection" xpath-value="title"/>
    <a:feature name="issn" xpath-value="issn"/>
    <xsl:apply-templates select="issue/volume"/>
    <xsl:apply-templates select="issue/number"/>
  </xsl:template>

  <xsl:template match="thematic">
    <a:feature name="thematic" xpath-value="identifier"/>
    <a:feature name="thematic-name" xpath-value="name"/>
    <xsl:for-each select="inraClassification">
      <xsl:if test="inraClassificationIdentifier">
	<a:feature name="thematic-inra-id" xpath-value="inraClassificationIdentifier"/>
      </xsl:if>
      <xsl:if test="associatedTerm">
	<a:feature name="thematic-inra" xpath-value="associatedTerm"/>
      </xsl:if>
      <xsl:if test="usedTerm">
	<a:feature name="thematic-inra-used" xpath-value="usedTerm"/>
      </xsl:if>
      <xsl:if test="engTerm">
	<a:feature name="thematic-inra-english" xpath-value="engTerm"/>
      </xsl:if>
    </xsl:for-each>
  </xsl:template>

  <xsl:template match="title|abstract">
    <a:section xpath-name="name()" xpath-contents=".">
      <a:feature name="lang" xpath-value="@language"/>
    </a:section>
  </xsl:template>

  <xsl:template match="author">
    <a:section name="author" contents="">
      <xsl:apply-templates select="firstName"/>
      <xsl:apply-templates select="lastName"/>
      <xsl:apply-templates select="group"/>
      <xsl:if test="inraAffiliation">
	<a:feature name="inra" value="yes"/>
	<xsl:apply-templates select="inraAffiliation/unit"/>
      </xsl:if>
      <xsl:apply-templates select="externalAffiliation"/>
    </a:section>
  </xsl:template>

  <xsl:template match="unit">
    <a:feature name="unit" xpath-value="code"/>
    <a:feature name="unit-acronym" xpath-value="acronym"/>
    <a:feature name="department" xpath-value="department/acronym"/>
    <a:feature name="center" xpath-value="center/acronym"/>
  </xsl:template>

  <xsl:template match="externalAffiliation">
    <xsl:if test="identifier">
      <a:feature name="partner-id" xpath-value="identifier"/>
    </xsl:if>
    <xsl:choose>
      <xsl:when test="acronym">
	<a:feature name="partner" xpath-value="acronym"/>
      </xsl:when>
      <xsl:otherwise>
	<a:feature name="partner" xpath-value="name"/>
     </xsl:otherwise>
    </xsl:choose>
    <xsl:if test="country">
      <a:feature name="partner-country" xpath-value="country"/>
    </xsl:if>
  </xsl:template>
</xsl:stylesheet>