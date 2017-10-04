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
		extension-element-prefixes="a inline"
		>

  <xsl:template match="/">
    <xsl:apply-templates select="xml/records/record"/>
  </xsl:template>

  <xsl:template match="record">
    <a:document xpath-id="normalize-space(electronic-resource-num)">
      <xsl:if test="electronic-resource-num">
	<a:feature name="doi" xpath-value="normalize-space(electronic-resource-num)"/>
      </xsl:if>
      <xsl:for-each select="titles/title">
	<a:section name="title" xpath-contents="normalize-space(.)"/>
      </xsl:for-each>
      <xsl:for-each select="abstract">
	<a:section name="abstract" xpath-contents="normalize-space(.)"/>
      </xsl:for-each>
    </a:document>
  </xsl:template>
</xsl:stylesheet>
