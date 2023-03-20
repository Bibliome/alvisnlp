<xsl:stylesheet version="1.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
		xmlns="http://maven.apache.org/POM/4.0.0"
		xmlns:mvn="http://maven.apache.org/POM/4.0.0"
		xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
		xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd"
		exclude-result-prefixes="mvn"
		>

  <xsl:param name="alvisnlp-version"/>
  <xsl:param name="bibliome-utils-version"/>

  <xsl:output method="xml" indent="yes"/>
  
  <xsl:template match="@*|text()|comment()" priority="0">
    <xsl:copy/>
  </xsl:template>

  <xsl:template match="mvn:dependency/mvn:version[../mvn:groupId = 'fr.jouy.inra.maiage.bibliome' and ../mvn:artifactId = 'bibliome-utils']">
    <version><xsl:value-of select="$bibliome-utils-version"/></version>
  </xsl:template>

  <xsl:template match="mvn:dependency/mvn:version[../mvn:groupId = 'fr.jouy.inra.maiage.bibliome' and ../mvn:artifactId = 'alvisnlp-core']">
    <version><xsl:value-of select="$alvisnlp-version"/></version>
  </xsl:template>

  <xsl:template match="mvn:project/mvn:version">
    <version><xsl:value-of select="$alvisnlp-version"/></version>
  </xsl:template>

  <xsl:template match="mvn:parent/mvn:version">
    <version><xsl:value-of select="$alvisnlp-version"/></version>
  </xsl:template>

  <xsl:template match="*">
    <xsl:copy>
      <xsl:apply-templates select="*|@*|text()|comment()"/>
    </xsl:copy>
  </xsl:template>
</xsl:stylesheet>
