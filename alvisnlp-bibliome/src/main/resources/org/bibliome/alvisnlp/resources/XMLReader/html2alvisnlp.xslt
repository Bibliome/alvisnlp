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

  <xsl:param name="source-path"/>

  <xsl:template match="/">
    <xsl:apply-templates select="HTML"/>
  </xsl:template>

  <xsl:template match="*">
    <a:document xpath-id="$source-path">
      <a:section name="html" xpath-contents=".">
	<xsl:for-each select="a:inline()">
	  <a:annotation start="@inline:start" end="@inline:end" layers="html">
	    <a:feature name="tag" xpath-value="name()"/>
	    <xsl:for-each select="@*[namespace-uri() != 'http://bibliome.jouy.inra.fr/alvisnlp/bibliome-module-factory/inline']">
	      <a:feature xpath-name="name()" xpath-value="."/>
	    </xsl:for-each>
	  </a:annotation>
	</xsl:for-each>
      </a:section>
    </a:document>
  </xsl:template>

</xsl:stylesheet>
