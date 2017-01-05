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
		xmlns:a="xalan://org.bibliome.alvisnlp.modules.xml.XMLReader2"
		xmlns:i="http://bibliome.jouy.inra.fr/alvisnlp/bibliome-module-factory/inline"
		extension-element-prefixes="a"
                >

  <xsl:param name="gene-layer"/>

  <xsl:template match="/">
    <xsl:apply-templates select="document"/>
  </xsl:template>

  <xsl:template match="document">
    <a:document xpath-id="@pmid">
      <a:feature name="pmid" xpath-value="@pmid"/>
      <xsl:apply-templates select="title|abstract"/>
    </a:document>
  </xsl:template>

  <xsl:template match="title|abstract">
    <a:section xpath-name="name()" xpath-contents=".">
      <xsl:for-each select="a:inline()[name() = 'gene']">
	<a:annotation start="@i:start" end="@i:end" xpath-layers="$gene-layer">
	  <a:feature name="type" value="ne"/>
	  <a:feature name="ne-type" value="gene"/>
	</a:annotation>
      </xsl:for-each>
    </a:section>
  </xsl:template>
</xsl:stylesheet>
