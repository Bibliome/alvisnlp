<xsl:stylesheet version = "1.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                >
  
  <xsl:output method="xml" indent="yes" encoding="UTF-8"/>

  <xsl:template match="/">
    <xsl:apply-templates select="*|@*|comment()|text()"/>
  </xsl:template>

  <xsl:template match="@*|comment()|text()">
    <xsl:copy/>
  </xsl:template>

  <xsl:template match="param[@name]">
    <param>
      <xsl:value-of select="@name"/>
    </param>
  </xsl:template>

  <xsl:template match="module[@name]">
    <module>
      <xsl:value-of select="@name"/>
    </module>
  </xsl:template>

  <xsl:template match="*">
    <xsl:copy>
      <xsl:apply-templates select="*|@*|comment()|text()"/>
    </xsl:copy>
  </xsl:template>
</xsl:stylesheet>
