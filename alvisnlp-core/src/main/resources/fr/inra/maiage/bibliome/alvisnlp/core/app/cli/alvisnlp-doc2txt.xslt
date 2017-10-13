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
	 >
  
  <xsl:output method="text" encoding="UTF-8"/>

	<xsl:param name="name" />
	<xsl:param name="synopsis" />
	<xsl:param name="description" />
	<xsl:param name="parameters" />
	<xsl:param name="string-conversion" />
	<xsl:param name="xml-conversion" />
	<xsl:param name="functions" />

  <xsl:template match="/">
  	<xsl:apply-templates select="//alvisnlp-doc"/>
  </xsl:template>

  <xsl:template match="alvisnlp-doc">
    <xsl:value-of select="$name"/>
    <xsl:text>
    </xsl:text>
    <xsl:value-of select="@short-target"/>
    <xsl:text>

</xsl:text>
    <xsl:apply-templates select="synopsis"/>
    <xsl:apply-templates select="module-doc|plan-doc|converter-doc|library-doc"/>
  </xsl:template>

  <xsl:template match="module-doc|plan-doc">
    <xsl:apply-templates select="description"/>
    <xsl:text>
</xsl:text>
    <xsl:value-of select="$parameters"/>
    <xsl:text>
</xsl:text>
    <xsl:apply-templates select="param-doc">
      <xsl:sort select="@name"/>
    </xsl:apply-templates>
  </xsl:template>

  <xsl:template match="converter-doc">
    <xsl:apply-templates select="string-conversion"/>
    <xsl:apply-templates select="xml-conversion"/>
  </xsl:template>

  <xsl:template match="library-doc">
    <xsl:text>
</xsl:text>    
    <xsl:value-of select="$functions"/>
    <xsl:text>
</xsl:text>
    <xsl:apply-templates select="function-doc">
      <xsl:sort select="@first-ftor"/>
    </xsl:apply-templates>
  </xsl:template>

  <xsl:template match="synopsis|description|string-conversion|xml-conversion">
    <xsl:variable name="title">
      <xsl:choose>
	<xsl:when test="name() = 'synopsis'"><xsl:value-of select="$synopsis"/></xsl:when>
	<xsl:when test="name() = 'description'"><xsl:value-of select="$description"/></xsl:when>
	<xsl:when test="name() = 'string-conversion'"><xsl:value-of select="$string-conversion"/></xsl:when>
	<xsl:when test="name() = 'xml-conversion'"><xsl:value-of select="$xml-conversion"/></xsl:when>
      </xsl:choose>
    </xsl:variable>
    <xsl:text>

</xsl:text>
    <xsl:value-of select="$title"/>
    <xsl:text>

</xsl:text>
    <xsl:apply-templates select="p"/>
  </xsl:template>

  <xsl:template match="function-doc">
    <xsl:text>
    </xsl:text>
    <xsl:value-of select="@synopsis"/>
    <xsl:text>
</xsl:text>
    <xsl:apply-templates select="p"/>    
  </xsl:template>

  <xsl:template match="param-doc">
    <xsl:text>
    </xsl:text>
    <xsl:value-of select="concat(@name, ' (', @short-type, ' ', @mandatory, ')')"/>
    <xsl:text>
</xsl:text>
    <xsl:apply-templates select="p"/>    
  </xsl:template>

  <xsl:template match="p">
    <xsl:text>    </xsl:text>
    <xsl:apply-templates select="*|text()"/>
    <xsl:text>
</xsl:text>
  </xsl:template>

  <xsl:template match="text()"><xsl:value-of select="."/></xsl:template>

  <xsl:template match="nl"><xsl:text>
</xsl:text></xsl:template>

  <xsl:template match="em"><xsl:value-of select="string(.)"/></xsl:template>

  <xsl:template match="strong">
    <xsl:value-of select="string(.)"/>
  </xsl:template>

  <xsl:template match="list">
    <xsl:apply-templates select="li"/>
  </xsl:template>

  <xsl:template match="enum">
    <xsl:apply-templates select="li"/>
  </xsl:template>

  <xsl:template match="li">
    <xsl:text>        * </xsl:text>
    <xsl:apply-templates select="*|text()"/>
  </xsl:template>

  <xsl:template match="a"><xsl:value-of select="."/></xsl:template>

  <xsl:template match="tag">
    <xsl:choose>
      <xsl:when test="(count(attr) = 0) and (count(text|tag) = 0)">
	<xsl:value-of select="concat('&lt;', @name, '/&gt;')"/>
      </xsl:when>
      <xsl:when test="count(attr) = 0">
	<xsl:value-of select="concat('&lt;', @name, '&gt;')"/>
	<xsl:apply-templates select="text|tag"/>
	<xsl:value-of select="concat('&lt;/', @name, '&gt;')"/>
      </xsl:when>
      <xsl:when test="count(text|tag) = 0">
	<xsl:value-of select="concat('&lt;', @name)"/><xsl:apply-templates select="attr"/>/&gt;
      </xsl:when>
      <xsl:otherwise>
	<xsl:value-of select="concat('&lt;', @name)"/><xsl:apply-templates select="attr"/>&gt;
	<xsl:apply-templates select="text|tag"/>
	<xsl:value-of select="concat('&lt;/', @name, '&gt;')"/>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>

  <xsl:template match="text"><xsl:value-of select="."/></xsl:template>

  <xsl:template match="attr"><xsl:value-of select="concat(' ', @name, '=')"/>"<xsl:value-of select="@value"/>"</xsl:template>

  <xsl:template match="this"><xsl:value-of select="//alvisnlp-doc/@short-target"/></xsl:template>

  <xsl:template match="param"><xsl:value-of select="@name"/></xsl:template>

  <xsl:template match="module"><xsl:value-of select="@name"/></xsl:template>

  <xsl:template match="converter">`<xsl:value-of select="@name"/>'</xsl:template>
</xsl:stylesheet>
