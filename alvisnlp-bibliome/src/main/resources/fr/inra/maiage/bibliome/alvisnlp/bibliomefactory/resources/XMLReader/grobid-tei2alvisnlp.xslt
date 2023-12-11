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
		xmlns:a="xalan://fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.xml.XMLReader2"
		xmlns:inline="http://bibliome.jouy.inra.fr/alvisnlp/bibliome-module-factory/inline"
		xmlns:tei="http://www.tei-c.org/ns/1.0#"
		xpath-default-namespace="http://www.tei-c.org/ns/1.0"
		extension-element-prefixes="a inline"
		>

  <xsl:param name="source-path"/>

  <xsl:template match="/">
    <xsl:apply-templates select="TEI"/>
  </xsl:template>

  <xsl:template match="TEI">
    <a:document xpath-id="$source-path">
      <xsl:apply-templates select="tei:teiHeader/tei:fileDesc/tei:titleStmt/tei:title" mode="feature"/>
      <xsl:apply-templates select="tei:teiHeader/tei:fileDesc/tei:titleStmt/tei:funder/tei:orgName" mode="feature"/>
    </a:document>
  </xsl:template>

  <xsl:template match="*" mode="feature">
    <a:feature xpath-name="name()" xpath-value="."/>
  </xsl:template>

  <xsl:template match="*">
    <xsl:message>Unhandled: <xsl:value-of select="name()"/></xsl:message>
  </xsl:template>

</xsl:stylesheet>
