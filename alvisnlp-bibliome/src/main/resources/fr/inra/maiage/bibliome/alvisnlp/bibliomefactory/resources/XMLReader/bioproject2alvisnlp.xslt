<xsl:stylesheet version="1.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:xlink="http://www.w3.org/1999/xlink"
		xmlns:mml="http://www.w3.org/1998/Math/MathML"
                xmlns:a="xalan://fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.xml.XMLReader2"
                xmlns:inline="http://bibliome.jouy.inra.fr/alvisnlp/bibliome-module-factory/inline"
                extension-element-prefixes="a inline"
		>

  <xsl:template match="/">
    <xsl:apply-templates select="DocumentSummary/Project"/>
  </xsl:template>

  <xsl:template match="Project">
    <a:document xpath-id="ProjectID/ArchiveID/@accession">
      <a:feature name="archive" xpath-value="ProjectID/ArchiveID/@archive"/>
      <xsl:apply-templates select="ProjectDescr/Name" mode="section"/>
      <xsl:apply-templates select="ProjectDescr/Title" mode="section"/>
      <xsl:apply-templates select="ProjectDescr/Description" mode="section"/>
      <xsl:choose>
	<xsl:when test="ProjectType/ProjectTypeSubmission/Target/Organism">
	  <xsl:apply-templates select="ProjectType/ProjectTypeSubmission/Target/Organism"/>
	</xsl:when>
      </xsl:choose>
    </a:document>
  </xsl:template>

  <xsl:template match="*" mode="section">
    <a:section xpath-name="name()" xpath-contents="."/>
  </xsl:template>

  <xsl:template match="Organism">
    <xsl:variable name="name">
      <xsl:choose>
	<xsl:when test="Strain">
	  <xsl:value-of select="concat(OrganismName, ' ', Strain)"/>
	</xsl:when>
	<xsl:otherwise>
	  <xsl:value-of select="OrganismName"/>
	</xsl:otherwise>
      </xsl:choose>
    </xsl:variable>
    <a:section name="Organism" xpath-contents="$name">
      <a:feature name="taxid" xpath-value="@taxID"/>
      <a:annotation start="0" end="string-length($name)" layers="taxa">
	<a:feature name="taxid" xpath-value="@taxID"/>
      </a:annotation>
    </a:section>
  </xsl:template>
  
</xsl:stylesheet>
