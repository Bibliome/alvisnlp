<xsl:stylesheet
    version="1.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:a="xalan://fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.xml.XMLReader2"
    xmlns:inline="http://bibliome.jouy.inra.fr/alvisnlp/bibliome-module-factory/inline"

    xmlns:xlink="http://www.w3.org/1999/xlink"
    xmlns:mml="http://www.w3.org/1998/Math/MathML"
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

    extension-element-prefixes="a inline"
>

    <xsl:template match="/">
        <xsl:apply-templates select="els:full-text-retrieval-response" />
    </xsl:template>

    <xsl:template match="els:full-text-retrieval-response">
        <a:document xpath-id="els:coredata/els:pii">
            <a:feature
                name="doi"
                xpath-value="els:coredata/prism:doi" />
            <a:feature
                name="pmid"
                xpath-value="els:pubmed-id" />

            <a:feature
                name="ext-link"
                xpath-value="els:coredata/prism:url" />

            <a:feature
                name="issn"
                xpath-value="els:coredata/prism:issn" />
            <a:feature
                name="journal"
                xpath-value="els:coredata/prism:publicationName" />
            <a:feature
                name="publisher"
                xpath-value="els:coredata/prism:publisher" />
            <a:feature
                name="year"
                xpath-value="els:originalText/xocs:doc/xocs:meta/xocs:year-nav" />

            <a:feature
                name="copyright-statement"
                xpath-value="els:coredata/prism:copyright" />

            <a:section
                name="article-title"
                xpath-contents="els:coredata/dc:title" />

            <xsl:for-each
                select="els:originalText/xocs:doc/xocs:serial-item/els2:*/els2:head/ce:author-group"
            >
                <xsl:variable name="affiliation">
                    <xsl:value-of select="ce:affiliation/ce:textfn" />
                </xsl:variable>
                <xsl:for-each select="ce:author">
                    <a:section
                        name="author"
                        xpath-contents="concat(ce:given-name, ' ', ce:surname)"
                    >

                        <a:feature
                            name="given-name"
                            xpath-value="ce:given-name" />
                        <a:feature
                            name="surname"
                            xpath-value="ce:surname" />

                        <a:feature
                            name="affiliation"
                            xpath-value="$affiliation" />
                    </a:section>
                </xsl:for-each>
            </xsl:for-each>

            <xsl:for-each
                select="els:originalText/xocs:doc/xocs:serial-item/els2:*/els2:head/ce:abstract/ce:abstract-sec"
            >
                <a:section
                    name="abstract"
                    xpath-contents="."
                >
                  <xsl:for-each select="a:inline()">
                        <a:annotation
                            start="@inline:start"
                            end="@inline:end"
                            layers="formatting"
                        >
                            <a:feature
                                name="tag"
                                xpath-value="name()" />
                        </a:annotation>
                    </xsl:for-each>
                </a:section>
            </xsl:for-each>

            <xsl:for-each
                select="els:originalText/xocs:doc/xocs:serial-item/els2:*/els2:body/ce:sections/ce:section"
            >
                <a:section
                    xpath-name="ce:section-title"
                    xpath-contents="."
                >
                    <a:feature
                        name="label"
                        xpath-value="ce:label" />
                    <xsl:for-each select="a:inline()">
                        <a:annotation
                            start="@inline:start"
                            end="@inline:end"
                            layers="formatting"
                        >
                            <a:feature
                                name="tag"
                                xpath-value="name()" />
                        </a:annotation>
			</xsl:for-each>
                </a:section>
            </xsl:for-each>
        </a:document>
    </xsl:template>
</xsl:stylesheet>
