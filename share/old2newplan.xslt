<!--
Copyright 2016 Institut National de la Recherche Agronomique

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
		xmlns:bibliome="xalan://org.bibliome.util.xml.Functions"
		>
  
  <xsl:output method="xml" indent="yes" encoding="UTF-8"/>

  <xsl:template match="*">
    <xsl:choose>
      <xsl:when test="name() = 'module' or name() = 'sequence'">
	<xsl:element name="{@id}">
	  <xsl:apply-templates select="@*[name() != 'id']|*|text()|comment()"/>
	</xsl:element>
      </xsl:when>
      <xsl:when test="name() = 'param' and name(..) = 'module'">
	<xsl:element name="{@name}">
	  <xsl:apply-templates select="@*[name() != 'name' and name() != 'value']"/>
	  <xsl:choose>
	    <xsl:when test="@value">
	      <xsl:value-of select="@value"/>
	    </xsl:when>
	    <xsl:otherwise>
	      <xsl:apply-templates select="*|text()|comment()"/>
	    </xsl:otherwise>
	  </xsl:choose>
	</xsl:element>
      </xsl:when>
      <xsl:otherwise>
	<xsl:copy>
	  <xsl:apply-templates select="@*|*|text()|comment()"/>
	</xsl:copy>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>

  <xsl:template match="@*|text()|comment()">
    <xsl:copy/>
  </xsl:template>
</xsl:stylesheet>
