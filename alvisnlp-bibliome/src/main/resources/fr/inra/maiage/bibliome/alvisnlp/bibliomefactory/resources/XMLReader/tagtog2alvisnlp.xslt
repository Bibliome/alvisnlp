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
		xmlns:xhtml="http://www.w3.org/1999/xhtml"
		extension-element-prefixes="a inline"
		>

  <xsl:param name="source-path"/>

  <xsl:template match="/">
    <xsl:apply-templates select="xhtml:html"/>
  </xsl:template>

  <xsl:template match="xhtml:html">
    <a:document xpath-id="@id">
      <a:feature key="data-origid" xpath-value="@data-origid"/>
      <xsl:apply-templates select=".//xhtml:pre"/>
    </a:document>
  </xsl:template>

  <xsl:template match="xhtml:pre">
    <a:section xpath-name="@id" xpath-contents="."/>
  </xsl:template>
</xsl:stylesheet>
