<xsl:stylesheet version="1.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:xlink="http://www.w3.org/1999/xlink"
		xmlns:mml="http://www.w3.org/1998/Math/MathML"
                xmlns:a="xalan://fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.xml.XMLReader2"
                xmlns:f="xalan://fr.inra.maiage.bibliome.util.xml.Functions"
                xmlns:inline="http://bibliome.jouy.inra.fr/alvisnlp/bibliome-module-factory/inline"
                extension-element-prefixes="a inline f"
		>

  <xsl:template match="/">
    <xsl:apply-templates select="BioSampleSet/BioSample"/>
  </xsl:template>

  <xsl:template match="BioSample">
    <a:document xpath-id="@accession">
      <xsl:apply-templates select="Description/Title" mode="section"/>
      <xsl:apply-templates select="Description/Comment/Paragraph" mode="section"/>
      <xsl:apply-templates select="Description/Organism"/>
      <xsl:apply-templates select="Attributes/Attribute" mode="attribute-section"/>
    </a:document>
  </xsl:template>

  <xsl:template match="*" mode="section">
    <a:section xpath-name="name()" xpath-contents="."/>
  </xsl:template>

  <xsl:template match="Organism">
    <xsl:variable name="name">
      <xsl:choose>
	<xsl:when test="../../Attributes/Attribute[@harmonized_name = 'strain']">
	  <xsl:value-of select="concat(OrganismName, ' ', ../../Attributes/Attribute[@harmonized_name = 'strain'])"/>
	</xsl:when>
	<xsl:otherwise>
	  <xsl:value-of select="OrganismName"/>
	</xsl:otherwise>
      </xsl:choose>
    </xsl:variable>
    <a:section name="Organism" xpath-contents="$name">
      <a:feature name="taxid" xpath-value="@taxonomy_id"/>
      <a:annotation start="0" end="string-length($name)" layers="taxa">
	<a:feature name="taxid" xpath-value="@taxonomy_id"/>
      </a:annotation>
    </a:section>
  </xsl:template>

  <xsl:template match="Attribute" mode="attribute-section">
    <xsl:variable name="lower">
      <xsl:value-of select="f:lower(.)"/>
    </xsl:variable>
    <xsl:if test="$lower != 'missing' and $lower != 'not applicable' and $lower != 'unknown' and $lower != 'not_applicable' and $lower != 'none' and $lower != 'na' and $lower != 'not collected' and $lower != 'not determined'">
      <a:section xpath-name="@harmonized_name" xpath-contents="."/>
    </xsl:if>
  </xsl:template>
</xsl:stylesheet>
