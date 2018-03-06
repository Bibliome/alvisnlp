<xsl:stylesheet version="1.0"
                missing="extension-element-prefixes=a inline"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:xlink="http://www.w3.org/1999/xlink"
		xmlns:mml="http://www.w3.org/1998/Math/MathML"
                xmlns:a="xalan://fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.xml.XMLReader2"
                xmlns:inline="http://bibliome.jouy.inra.fr/alvisnlp/bibliome-module-factory/inline"

		xmlns:els="http://www.elsevier.com/xml/svapi/article/dtd"
		xmlns:els2="http://www.elsevier.com/xml/ja/dtd"
		xmlns:bk="http://www.elsevier.com/xml/bk/dtd"
		xmlns:cals="http://www.elsevier.com/xml/common/cals/dtd"
		xmlns:ce="http://www.elsevier.com/xml/common/dtd"
		xmlns:ja="http://www.elsevier.com/xml/ja/dtd"
		xmlns:sa="http://www.elsevier.com/xml/common/struct-aff/dtd"
		xmlns:sb="http://www.elsevier.com/xml/common/struct-bib/dtd"
		xmlns:tb="http://www.elsevier.com/xml/common/table/dtd"
		xmlns:xocs="http://www.elsevier.com/xml/xocs/dtd"
		xmlns:dc="http://purl.org/dc/elements/1.1/"
		xmlns:dcterms="http://purl.org/dc/terms/"
		xmlns:prism="http://prismstandard.org/namespaces/basic/2.0/"
		xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
		xmlns:xoe="http://www.elsevier.com/xml/xoe/dtd"
>
  <xsl:template match="/">
    <a:document xpath-id="/els:full-text-retrieval-response/els:pubmed-id">
      <xsl:copy-of select="/els:full-text-retrieval-response/els:pubmed-id" />

      <a:feature name="issn" xpath-value="/els:full-text-retrieval-response/els:coredata/prism:issn"/>
      <xsl:copy-of select="/els:full-text-retrieval-response/els:coredata/prism:issn" />"

      <a:feature name="journal" xpath-value="/els:full-text-retrieval-response/els:coredata/prism:publicationName"/>
      <xsl:copy-of select="/els:full-text-retrieval-response/els:coredata/prism:publicationName" />

      <a:feature name="publisher" xpath-value="/els:full-text-retrieval-response/els:coredataprism:publisher"/>
      <xsl:copy-of select="/els:full-text-retrieval-response/els:coredata/prism:publicationName" />

      <a:feature name="year" xpath-value="/els:full-text-retrieval-response/els:originalText/xocs:doc/xocs:meta/xocs:year-nav"/>
      <xsl:copy-of select="/els:full-text-retrieval-response/els:originalText/xocs:doc/xocs:meta/xocs:year-nav" />

<a:section name="article-title" xpath-contents="/els:full-text-retrieval-response/els:coredata/dc:title"/>
      <xsl:copy-of select="/els:full-text-retrieval-response/els:coredata/dc:title" />


      <xsl:for-each select="//ce:affiliation">
        <xsl:variable name="aff">
          <xsl:value-of select="//ce:affiliation"/>
        </xsl:variable>
      </xsl:for-each>

      <xsl:for-each select="//ce:author-group/ce:author">

      <a:section name="author" xpath-contents="concat(ce:given-name, ' ', ce:surname)">
        <xsl:value-of select="concat(ce:given-name, ' ', ce:surname)" />

        <a:feature  name="given-name" xpath-value="ce:given-name"/>
        <xsl:copy-of select="ce:given-name"/>
        <a:feature name="surname" xpath-value="ce:surname"/>
        <xsl:copy-of select="ce:surname"/>

        <xsl:variable name="refid">
          <xsl:value-of select="//ce:cross-ref/@refid"/>
        </xsl:variable>

        <a:feature name="affiliation" xpath-value="//ce:affiliation/ce:textfn"/>
        <xsl:copy-of select="//ce:affiliation/ce:textfn"/>


        </a:section>
      </xsl:for-each>

      <a:feature name="ext-link" xpath-value="/els:full-text-retrieval-response/els:coredata/prism:url"/>
      <xsl:copy-of select="/els:full-text-retrieval-response/els:coredata/prism:url"/>

      <a:feature name="copyright-statement" xpath-value="/els:full-text-retrieval-response/els:coredat/prism:copyright" />
      <xsl:copy-of select="/els:full-text-retrieval-response/els:coredata/prism:copyright" />

      <xsl:apply-templates select="//xocs:doc/xocs:serial-item/els2:converted-article/els2:body" />
      <xsl:copy-of select="//xocs:doc/xocs:serial-item/els2:converted-article/els2:body" />


    </a:document>
  </xsl:template>

  <xsl:template match="//xocs:doc/xocs:serial-item/els2:converted-article/els2:body|//xocs:doc/xocs:serial-item/els2:converted-article/els2:body/ce:sections|//xocs:doc/xocs:serial-item/els2:converted-article/els2:body/ce:section/ce:section-title|//xocs:doc/xocs:serial-item/els2:converted-article/els2:body/ce:sections/ce:para">
    <a:feature name="fulltext" xpath-value="." >
      <xsl:for-each select="ce:s">
        <xsl:value-of select="." />
      </xsl:for-each>
    </a:feature>
  </xsl:template>


  <xsl:template match="els:objects">
    <a:document xpath-id="els:pubmed-id">
      <xsl:copy-of select="els:objects"/>
      <xsl:copy-of select="prism:issn" />
      <a:feature name="issn" xpath-value="prism:issn"/>
      <a:feature name="journal" xpath-value=""/>
      <a:feature name="abbrev" xpath-value="Article/Journal/ISOAbbreviation"/>
      <a:feature name="year" xpath-value="Article/Journal/JournalIssue/PubDate/Year"/>
<a:section name="title" xpath-contents="Article/ArticleTitle"/>
    </a:document>
  </xsl:template>
</xsl:stylesheet>
