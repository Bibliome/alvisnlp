<xsl:stylesheet version = "1.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:bibliome="xalan://org.bibliome.util.xml.Functions"
                >
  
  <xsl:output method="xml" indent="yes" encoding="UTF-8" omit-xml-declaration="yes"/>

  <xsl:param name="module"></xsl:param>
  <xsl:param name="param"></xsl:param>
  <xsl:param name="value"></xsl:param>

  <xsl:template match="/default-param-values">
    <default-param-values>
      <xsl:apply-templates select="module"/>
      <xsl:if test="not(module[@class = $module])">
	<module>
	  <xsl:attribute name="class"><xsl:value-of select="$module"/></xsl:attribute>
	  <xsl:element name="{$param}">
	    <xsl:value-of select="$value"/>
	  </xsl:element>
	</module>
      </xsl:if>
    </default-param-values>
  </xsl:template>

  <xsl:template match="module">
    <module>
      <xsl:attribute name="class"><xsl:value-of select="@class"/></xsl:attribute>
      <xsl:copy-of select="*"/>
      <xsl:if test="(@class = $module) and not(*[name() = $param])">
	<xsl:element name="{$param}">
	  <xsl:value-of select="$value"/>
	</xsl:element>
      </xsl:if>
    </module>
  </xsl:template>
</xsl:stylesheet>