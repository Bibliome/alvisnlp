<xsl:stylesheet version = "1.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                >
  
  <xsl:output method="text" encoding="UTF-8"/>

  <xsl:template match="/">
    <xsl:text>Plan file nesting:&#xA;</xsl:text>
    <xsl:apply-templates select="alvisnlp-plan-analysis/*" mode="plan-nesting"/>
    <xsl:text>&#xA;Resources:&#xA;</xsl:text>
    <xsl:apply-templates select="//resource"/>
  </xsl:template>

  <xsl:template match="*" mode="plan-nesting">
    <xsl:if test="@plan-source">
      <xsl:for-each select="ancestor-or-self::*[@plan-source]">
	<xsl:text>    </xsl:text>
      </xsl:for-each>
      <xsl:value-of select="concat(name(), ' : ', @plan-source, '&#xA;')"/>
    </xsl:if>
    <xsl:apply-templates select="*" mode="plan-nesting"/>
  </xsl:template>

  <xsl:template match="resource">
    <xsl:for-each select="ancestor::*[name() != 'alvisnlp-plan-analysis']">
      <xsl:if test="position() != 1">
	<xsl:text>.</xsl:text>
      </xsl:if>
      <xsl:value-of select="name()"/>
    </xsl:for-each>
    <xsl:value-of select="concat('&#x9;', @param, '&#x9;', @mode, '&#x9;', ., '&#x9;', (ancestor::*[@plan-source])[last()]/@plan-source, '&#xA;')"/>
  </xsl:template>
</xsl:stylesheet>
