<xsl:stylesheet
    version="1.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:a="xalan://fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.xml.XMLReader2"
    xmlns:inline="http://bibliome.jouy.inra.fr/alvisnlp/bibliome-module-factory/inline"

    extension-element-prefixes="a inline"
>

    <xsl:template match="/">
        <xsl:apply-templates select="response" />
    </xsl:template>

    <xsl:template match="">
        <a:document xpath-id="Article[@ID]">
            <a:feature
                name="doi"
                xpath-value="records/Publisher/Journal/Volume/Issue/Article/ArticleInfo/ArticleDOI" />
<!--            <a:feature
                name="pmid"
                xpath-value="els:pubmed-id" />
-->
<!--            <a:feature
                name="ext-link"
                xpath-value="els:coredata/prism:url" />
-->
            <a:feature
                name="issn"
                xpath-value="records/Publisher/Journal/JournalInfo/JournalElectronicISSN" />
            <a:feature
                name="journal"
                xpath-value="records/Publisher/Journal/JournalInfo/JournalTitle" />
            <a:feature
                name="publisher"
                xpath-value="records/Publisher/PublisherInfo/PublisherName" />
            <a:feature
                name="year"
                xpath-value="records/Publisher/Journal/Volume/Issue/Article/ArticleInfo/ArticleHistory/OnlineDate/Year" />

            <a:feature
                name="copyright-statement"
                xpath-value="concat(records/Publisher/Journal/Volume/Issue/Article/ArticleInfo/ArticleCopyright/CopyrightHolderName, ' ', records/Publisher/Journal/Volume/Issue/Article/ArticleInfo/ArticleCopyright/CopyrightYear)" />

            <a:section
                name="article-title"
                xpath-contents="records/Publisher/Journal/Volume/Issue/Article/ArticleInfo/ArticleTitle" />

            <xsl:for-each
                select="records/Publisher/Journal/Volume/Issue/Article/ArticleHeader/AuthorGroup/Author"
            >
<!--                <xsl:variable name="affiliation-text">
                    <xsl:value-of select="ce:textfn" />
                </xsl:variable>
-->
                    <a:section
                        name="author"
                        xpath-contents="concat(AuthorName/GivenName, ' ', AuthorName/FamilyName)" />

                    <a:feature
                        name="given-name"
                        xpath-value="AuthorName/GivenName" />
                    <a:feature
                        name="surname"
                        xpath-value="AuthorName/FamilyName" />

<!--                    <a:feature
                        name="affiliation"
                        xpath-value="$affiliation-text" />
-->            </xsl:for-each>

            <xsl:for-each
                select="records/Publisher/Journal/Volume/Issue/Article/ArticleHeader/Abstract/AbstractSection"
            >
                <a:section
                    name="abstract"
                    xpath-contents="."
                >
                    <xsl:for-each select="a:inline()">
                        <a:annotation
                            start="@inline:start"
                            end="@inline:end"
                            layer="formatting"
                        >
                            <a:feature
                                name="tag"
                                xpath-value="name()" />
                        </a:annotation>
                    </xsl:for-each>
                </a:section>
            </xsl:for-each>

            <xsl:for-each
                select="records/Publisher/Journal/Volume/Issue/Article/Body/Section1"
            >
                <a:section
                    xpath-name="Heading"
                    xpath-contents="Para"
                >
                    <xsl:for-each select="a:inline()">
                        <a:annotation
                            start="@inline:start"
                            end="@inline:end"
                            layer="formatting"
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
