<xsl:stylesheet
    version="1.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:a="xalan://fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.xml.XMLReader2"
    xmlns:inline="http://bibliome.jouy.inra.fr/alvisnlp/bibliome-module-factory/inline"
    xmlns:str="xalan://fr.inra.maiage.bibliome.util.xml.Functions"

    extension-element-prefixes="a inline"
    extension-element-prefixes="a inline str"
>

    <xsl:template match="/response">
        <xsl:apply-templates select="records" />
    </xsl:template>

    <xsl:template match="records">
        <a:document xpath-id="str:replace(Publisher/Journal/Volume/Issue/Article/ArticleInfo/ArticleDOI, '/', '_')">
            <a:feature
                name="doi"
                xpath-value="Publisher/Journal/Volume/Issue/Article/ArticleInfo/ArticleDOI" />
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
                xpath-value="Publisher/Journal/JournalInfo/JournalElectronicISSN" />
            <a:feature
                name="journal"
                xpath-value="Publisher/Journal/JournalInfo/JournalTitle" />
            <a:feature
                name="publisher"
                xpath-value="Publisher/PublisherInfo/PublisherName" />
            <a:feature
                name="year"
                xpath-value="Publisher/Journal/Volume/Issue/Article/ArticleInfo/ArticleHistory/OnlineDate/Year" />

            <a:feature
                name="copyright-statement"
                xpath-value="concat(Publisher/Journal/Volume/Issue/Article/ArticleInfo/ArticleCopyright/CopyrightHolderName, ' ', Publisher/Journal/Volume/Issue/Article/ArticleInfo/ArticleCopyright/CopyrightYear)" />

            <a:section
                name="article-title"
                xpath-contents="Publisher/Journal/Volume/Issue/Article/ArticleInfo/ArticleTitle" />

            <xsl:for-each
                select="Publisher/Journal/Volume/Issue/Article/ArticleHeader/AuthorGroup/Author"
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
                select="Publisher/Journal/Volume/Issue/Article/ArticleHeader/Abstract/AbstractSection"
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
                select="Publisher/Journal/Volume/Issue/Article/Body/Section1"
            >
                <a:section
                    xpath-name="Heading"
                    xpath-contents="Para"
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
                select="Publisher/Journal/Volume/Issue/Article/Body/Section1/Section2"
            >
                <a:section
                    xpath-name="Heading"
                    xpath-contents="Para"
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



        </a:document>
    </xsl:template>
</xsl:stylesheet>
