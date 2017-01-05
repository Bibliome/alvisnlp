<!--
Copyright 2016, 2017 Institut National de la Recherche Agronomique

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

        http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
-->


<xsl:stylesheet version = "1.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                >

  <xsl:import href="alvisnlp-util.xslt"/>

  <xsl:param name="pmid">pmid</xsl:param>
  <xsl:param name="abstract">abstract</xsl:param>
  <xsl:param name="type-feature">type</xsl:param>
  <xsl:param name="name-type">name</xsl:param>
  <xsl:param name="name-type-feature">name-type</xsl:param>
  <xsl:param name="newformer-type">none</xsl:param>
  <xsl:param name="name-layer">names</xsl:param>

  <xsl:template match="/">
    <xsl:element name="alvisnlp-corpus">
      <xsl:apply-templates select="document"/>
    </xsl:element>
  </xsl:template>

  <xsl:template match="document">
    <xsl:element name="document">
      <xsl:attribute name="id">
	<xsl:value-of select="pmid"/>
      </xsl:attribute>
      <xsl:call-template name="meta">
	<xsl:with-param name="name" select="$pmid"/>
	<xsl:with-param name="value" select="pmid"/>
      </xsl:call-template>

      
      <xsl:call-template name="rename-section">
	<xsl:with-param name="section-name" select="$abstract"/>
	<xsl:with-param name="node" select="text"/>
	<xsl:with-param name="newname-id" select="1"/>
	<xsl:with-param name="formername-id" select="1"/>
      </xsl:call-template>
    </xsl:element>
  </xsl:template>

  <xsl:template name="rename-section">
    <xsl:param name="section-name"/>
    <xsl:param name="node"/>
    <xsl:param name="newname-id"/>
    <xsl:param name="formername-id"/>

    <xsl:element name="section">
      <xsl:attribute name="name">
	<xsl:value-of select="$section-name"/>
      </xsl:attribute>
      <xsl:element name="contents">
	<xsl:for-each select="$node/descendant::text()">
	  <xsl:value-of select="."/>
	</xsl:for-each>
      </xsl:element>
      <xsl:call-template name="rename-annotations">
	<xsl:with-param name="node" select="$node"/>
	<xsl:with-param name="next-id" select="$newname-id"/>
	<xsl:with-param name="tag-name" select="'new-name'"/>
	<xsl:with-param name="feature-name" select="$type-feature"/>
	<xsl:with-param name="feature-value" select="$name-type"/>
	<xsl:with-param name="feature-name2" select="$name-type-feature"/>
	<xsl:with-param name="feature-value2" select="'new'"/>
      </xsl:call-template>
      <xsl:call-template name="rename-annotations">
	<xsl:with-param name="node" select="$node"/>
	<xsl:with-param name="next-id" select="$formername-id+count($node/descendant::new-name)"/>
	<xsl:with-param name="tag-name" select="'former-name'"/>
	<xsl:with-param name="feature-name" select="$type-feature"/>
	<xsl:with-param name="feature-value" select="$name-type"/>
	<xsl:with-param name="feature-name2" select="$name-type-feature"/>
	<xsl:with-param name="feature-value2" select="'former'"/>
      </xsl:call-template>
      <xsl:element name="layer">
	<xsl:attribute name="name">
	  <xsl:value-of select="$name-layer"/>
	</xsl:attribute>
	<xsl:attribute name="annotations">
	  <xsl:call-template name="enumerate">
	    <xsl:with-param name="min" select="$newname-id"/>
	    <xsl:with-param name="max" select="(count(text/descendant::new-name)+count(text/descendant::former-name)+1)"/>
	    <xsl:with-param name="sep" select="','"/>
	  </xsl:call-template>
	</xsl:attribute>
      </xsl:element>
    </xsl:element>
  </xsl:template>


  <!--
      Creates annotations from in-text annotations.
      Parameters:
          node : node containing the in-text annotation
	  next-id : identifier of the next annotation
	  tag-name : name of the in-text annotation tags
	  feature-name : name of a constant feature
	  feature-value : name of a constant feature
	  feature-name2 : name of another constant feature
	  feature-value2 : name of another constant feature
  -->
  <xsl:template name="rename-annotations">
    <xsl:param name="node"/>
    <xsl:param name="next-id"/>
    <xsl:param name="tag-name"/>
    <xsl:param name="feature-name"/>
    <xsl:param name="feature-value"/>
    <xsl:param name="feature-name2"/>
    <xsl:param name="feature-value2"/>
    <xsl:call-template name="rename-annotations-aux">
      <xsl:with-param name="nodes" select="$node/descendant::*|$node/descendant::text()"/>
      <xsl:with-param name="idx" select="1"/>
      <xsl:with-param name="pos" select="0"/>
      <xsl:with-param name="next-id" select="$next-id"/>
      <xsl:with-param name="tag-name" select="$tag-name"/>
      <xsl:with-param name="feature-name" select="$feature-name"/>
      <xsl:with-param name="feature-value" select="$feature-value"/>
      <xsl:with-param name="feature-name2" select="$feature-name2"/>
      <xsl:with-param name="feature-value2" select="$feature-value2"/>
    </xsl:call-template>
  </xsl:template>

  <!--
      Auxiliary template for annotations, do not use directly.
  -->
  <xsl:template name="rename-annotations-aux">
    <xsl:param name="nodes"/>
    <xsl:param name="idx"/>
    <xsl:param name="pos"/>
    <xsl:param name="next-id"/>
    <xsl:param name="tag-name"/>
    <xsl:param name="feature-name"/>
    <xsl:param name="feature-value"/>
    <xsl:param name="feature-name2"/>
    <xsl:param name="feature-value2"/>
    <xsl:if test="$idx &lt;= count($nodes)">
      <xsl:variable name="n" select="$nodes[$idx]"/>
      <xsl:choose>
	<xsl:when test="name($n) = $tag-name">
	  <xsl:element name="a">
	    <xsl:attribute name="id">
	      <xsl:value-of select="$next-id"/>
	    </xsl:attribute>
	    <xsl:attribute name="s">
	      <xsl:value-of select="$pos"/>
	    </xsl:attribute>
	    <xsl:attribute name="e">
	      <xsl:value-of select="$pos+string-length($n)"/>
	    </xsl:attribute>
	    <xsl:element name="f">
	      <xsl:attribute name="n">
		<xsl:value-of select="$feature-name"/>
	      </xsl:attribute>
	      <xsl:attribute name="v">
		<xsl:value-of select="$feature-value"/>
	      </xsl:attribute>
	    </xsl:element>
	    <xsl:element name="f">
	      <xsl:attribute name="n">
		<xsl:value-of select="$feature-name2"/>
	      </xsl:attribute>
	      <xsl:attribute name="v">
		<xsl:value-of select="$feature-value2"/>
	      </xsl:attribute>
	    </xsl:element>
	    <xsl:element name="f">
	      <xsl:attribute name="n">
		<xsl:value-of select="'rename-id'"/>
	      </xsl:attribute>
	      <xsl:attribute name="v">
	        <xsl:value-of select="$n/@id"/>
	      </xsl:attribute>
	    </xsl:element>
	  </xsl:element>
	  <xsl:call-template name="rename-annotations-aux">
	    <xsl:with-param name="nodes" select="$nodes"/>
	    <xsl:with-param name="idx" select="$idx+1"/>
	    <xsl:with-param name="pos" select="$pos"/>
	    <xsl:with-param name="next-id" select="$next-id+1"/>
	    <xsl:with-param name="tag-name" select="$tag-name"/>
	    <xsl:with-param name="feature-name" select="$feature-name"/>
	    <xsl:with-param name="feature-value" select="$feature-value"/>
	    <xsl:with-param name="feature-name2" select="$feature-name2"/>
	    <xsl:with-param name="feature-value2" select="$feature-value2"/>
	  </xsl:call-template>
	</xsl:when>
	<xsl:when test="name($n) = ''">
	  <xsl:call-template name="rename-annotations-aux">
	    <xsl:with-param name="nodes" select="$nodes"/>
	    <xsl:with-param name="idx" select="$idx+1"/>
	    <xsl:with-param name="pos" select="$pos+string-length($n)"/>
	    <xsl:with-param name="next-id" select="$next-id"/>
	    <xsl:with-param name="tag-name" select="$tag-name"/>
	    <xsl:with-param name="feature-name" select="$feature-name"/>
	    <xsl:with-param name="feature-value" select="$feature-value"/>
	    <xsl:with-param name="feature-name2" select="$feature-name2"/>
	    <xsl:with-param name="feature-value2" select="$feature-value2"/>
	  </xsl:call-template>
	</xsl:when>
	<xsl:when test="name($n) != $tag-name">
	  <xsl:call-template name="rename-annotations-aux">
	    <xsl:with-param name="nodes" select="$nodes"/>
	    <xsl:with-param name="idx" select="$idx+1"/>
	    <xsl:with-param name="pos" select="$pos"/>
	    <xsl:with-param name="next-id" select="$next-id"/>
	    <xsl:with-param name="tag-name" select="$tag-name"/>
	    <xsl:with-param name="feature-name" select="$feature-name"/>
	    <xsl:with-param name="feature-value" select="$feature-value"/>
	    <xsl:with-param name="feature-name2" select="$feature-name2"/>
	    <xsl:with-param name="feature-value2" select="$feature-value2"/>
	  </xsl:call-template>
	</xsl:when>
      </xsl:choose>
    </xsl:if>
  </xsl:template>

</xsl:stylesheet>
