<xsl:stylesheet version="1.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:xlink="http://www.w3.org/1999/xlink"
		xmlns:mml="http://www.w3.org/1998/Math/MathML"
                xmlns:a="xalan://org.bibliome.alvisnlp.modules.xml.XMLReader2"
                xmlns:inline="http://bibliome.jouy.inra.fr/alvisnlp/bibliome-module-factory/inline"
                extension-element-prefixes="a inline"
		>

  <xsl:template match="/">
    <xsl:apply-templates select="article"/>
  </xsl:template>

  <xsl:template match="article">
    <a:document xpath-id="front/article-meta/article-id[@pub-id-type = 'pmcid']">
      <a:feature name="article-type" xpath-value="@article-type"/>
      <xsl:apply-templates select="front/journal-meta"/>
      <xsl:apply-templates select="front/article-meta"/>
      <xsl:apply-templates select="body/sec|body[not(sec)]"/>
   </a:document>
  </xsl:template>

  <xsl:template match="journal-meta">
    <a:feature name="journal" xpath-value="journal-title"/>
    <a:feature name="issn" xpath-value="issn"/>
    <a:feature name="publisher" xpath-value="publisher/publisher-name"/>
  </xsl:template>

  <xsl:template match="article-meta">
    <xsl:apply-templates select="article-id"/>
    <xsl:apply-templates select="title-group/article-title"/>
    <xsl:apply-templates select="contrib-group/contrib[@contrib-type = 'author' or @contrib-type = 'presenting-author']"/>
    <xsl:apply-templates select="pub-date"/>
    <xsl:apply-templates select="ext-link"/>
    <xsl:apply-templates select="permissions"/>
    <xsl:apply-templates select="abstract/sec|abstract[not(sec)]"/>
  </xsl:template>

  <xsl:template match="article-id">
    <a:feature xpath-name="@pub-id-type" xpath-value="."/>
  </xsl:template>

  <xsl:template match="article-title">
    <a:section name="article-title" xpath-contents="."/>
  </xsl:template>

  <xsl:template match="contrib">
    <xsl:variable name="suffix">
      <xsl:choose>
	<xsl:when test="name/suffix">
	  <xsl:value-of select="concat(' ', name/suffix)"/>
	</xsl:when>
	<xsl:otherwise/>
      </xsl:choose>
    </xsl:variable>
    <xsl:variable name="aff-id">
      <xsl:value-of select="xref[@ref-type = 'aff']/@rid"/>
    </xsl:variable>
    <a:section name="author" xpath-contents="concat(name/given-names, ' ', name/surname, $suffix)">
      <a:feature name="given-names" xpath-value="name/given-names"/>
      <a:feature name="surname" xpath-value="name/surname"/>
      <a:feature name="suffix" xpath-value="name/suffix"/>
      <a:feature name="affiliation" xpath-value="../../aff[@id = $aff-id]/text()"/>
    </a:section>
  </xsl:template>

  <xsl:template match="pub-date">
    <a:feature name="year" xpath-value="year"/>
  </xsl:template>

  <xsl:template match="ext-link">
    <a:feature name="ext-link" xpath-value="@xlink:href"/>
  </xsl:template>

  <xsl:template match="permissions">
    <a:feature name="copyright-statement" xpath-value="copyright-statement"/>
    <a:feature name="license-link" xpath-value="license/@xlink:href"/>
    <a:feature name="license" xpath-value="license"/>
  </xsl:template>

  <xsl:template match="sec|abstract|body">
    <a:section xpath-name="name(..)" xpath-contents=".">
      <a:feature name="title" xpath-value="title"/>
      <xsl:for-each select="a:inline()">
	<a:annotation start="@inline:start" end="@inline:end" layers="markup">
          <a:feature name="tag" xpath-value="name()"/>
        </a:annotation>
      </xsl:for-each>
    </a:section>
  </xsl:template>
</xsl:stylesheet>
