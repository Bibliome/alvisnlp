<xsl:stylesheet version = "1.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:re="http://exslt.org/regular-expressions"
		extension-element-prefixes="re">
  
  <xsl:output method="text" encoding="UTF-8"/>

  <xsl:template match="/">
    <xsl:if test="/alvisnlp-doc/module-doc">
      <xsl:apply-templates select="//param"/> 
      <xsl:apply-templates select="//module"/> 
      <xsl:apply-templates select="/alvisnlp-doc/module-doc/param-doc"/>
    </xsl:if>
  </xsl:template>

  <xsl:template match="param">
    <xsl:if test="@name">
      <xsl:message>### Attribute 'name' for element 'param' is deprecated</xsl:message>
    </xsl:if>
    <xsl:variable name="name">
      <xsl:value-of select="concat(@name, .)"/>
    </xsl:variable>
    <xsl:if test="not(/alvisnlp-doc/module-doc/param-doc[@name = $name])">
      <xsl:message>### Parameter '<xsl:value-of select="$name"/>' referenced but not documented.</xsl:message>
    </xsl:if>
    <xsl:if test="/alvisnlp-doc/module-doc/param-doc[@name = $name and @deprecated = 'yes']">
      <xsl:message>### Reference to deprecated parameter '<xsl:value-of select="$name"/>'.</xsl:message>
    </xsl:if>
  </xsl:template>

  <xsl:template match="module">
    <xsl:if test="@name">
      <xsl:message>### Attribute 'name' for element 'param' is deprecated</xsl:message>
    </xsl:if>
  </xsl:template>

  <xsl:template match="param-doc">
    <xsl:choose>
      <xsl:when test="@deprecated = 'yes'">
	<xsl:if test="not(.//param)">
	  <xsl:message>### Documentation for deprecated param '<xsl:value-of select="@name"/>' does not reference an alternative parameter</xsl:message>
	</xsl:if>
      </xsl:when>
      <xsl:when test="@type = 'java.lang.String'">
	<xsl:variable name="len">
	  <xsl:value-of select="string-length(@name)"/>
	</xsl:variable>
	<xsl:if test="@name-type = 'feature' and not((@name = 'feature') or (@name = 'features') or (substring(@name, $len - 6) = 'Feature') or (substring(@name, $len - 7) = 'Features'))">
	  <xsl:message>### Parameter '<xsl:value-of select="@name"/>' specifies a feature but does not end with 'Feature'</xsl:message>
	</xsl:if>
	<xsl:if test="@name-type = 'relation' and not((@name = 'relation') or (@name = 'relations') or (substring(@name, $len - 7) = 'Relation') or (substring(@name, $len - 8) = 'Relations'))">
	  <xsl:message>### Parameter '<xsl:value-of select="@name"/>' specifies a relation but does not end with 'Relation'</xsl:message>
	</xsl:if>
	<xsl:if test="@name-type = 'argument' and not((@name = 'role') or (@name = 'roles') or (substring(@name, $len - 3) = 'Role') or (substring(@name, $len - 4) = 'Roles'))">
	  <xsl:message>### Parameter '<xsl:value-of select="@name"/>' specifies a role but does not end with 'Role'</xsl:message>
	</xsl:if>
	<xsl:if test="@name-type = 'section' and not((@name = 'section') or (substring(@name, $len - 6) = 'Section') or (substring(@name, $len - 7) = 'Sections'))">
	  <xsl:message>### Parameter '<xsl:value-of select="@name"/>' specifies a section but does not end with 'Section'</xsl:message>
	</xsl:if>
	<xsl:if test="@name-type = 'layer' and not((@name = 'layer') or (substring(@name, $len - 4) = 'Layer') or (substring(@name, $len - 5) = 'Layers'))">
	  <xsl:message>### Parameter '<xsl:value-of select="@name"/>' specifies a layer but does not end with 'Layer'</xsl:message>
	</xsl:if>
      </xsl:when>
    </xsl:choose>
  </xsl:template>
</xsl:stylesheet>
