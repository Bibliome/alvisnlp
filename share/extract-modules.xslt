<xsl:stylesheet version = "1.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                >
  
  <xsl:output method="text" encoding="UTF-8"/>

  <xsl:param name="source"/>

  <xsl:template match="/">
    <xsl:for-each select="//*[@class]">
      <xsl:value-of select="$source"/>
      <xsl:text>	</xsl:text>
      <xsl:value-of select="@class"/>
      <xsl:text>
</xsl:text>
    </xsl:for-each>
  </xsl:template>
</xsl:stylesheet>
