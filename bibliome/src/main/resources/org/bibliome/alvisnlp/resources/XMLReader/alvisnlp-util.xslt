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

  <xsl:output method="xml" indent="yes"/>

  <!--
      This parameter is automatically set by XMLReader with the path to the file being transformed.
  -->
  <xsl:param name="source"/>

  <!--
      Creates a meta tag (for documents and sections).
      Parameters:
          name : name of the meta
	  value : value of the meta
  -->
  <xsl:template name="meta">
    <xsl:param name="name"/>
    <xsl:param name="value"/>
    <xsl:element name="feature">
      <xsl:attribute name="name">
	<xsl:value-of select="$name"/>
      </xsl:attribute>
      <xsl:attribute name="value">
	<xsl:value-of select="$value"/>
      </xsl:attribute>
    </xsl:element>
  </xsl:template>

  <!--
      Creates a section without annotations.
      Parameters:
          name : name of the section
	  contents : contents of the section
  -->
  <xsl:template name="section">
    <xsl:param name="name"/>
    <xsl:param name="contents"/>
    <xsl:if test="string-length($contents) > 0">
      <xsl:element name="section">
	<xsl:attribute name="name">
	  <xsl:value-of select="$name"/>
	</xsl:attribute>
	<xsl:element name="contents">
	  <xsl:value-of select="$contents"/>
	</xsl:element>
      </xsl:element>
    </xsl:if>
  </xsl:template>

  <!--
      Creates a character node with all integers between specified boundaries.
      Parameters:
          min : lower bound (inclusive)
	  max : higher bound (exclusive)
  -->
  <xsl:template name="enumerate">
    <xsl:param name="min"/>
    <xsl:param name="max"/>
    <xsl:param name="sep"/>
    <xsl:if test="$max > $min">
      <xsl:value-of select="$min"/>
      <xsl:call-template name="enumerate-aux">
	<xsl:with-param name="n" select="$min+1"/>
	<xsl:with-param name="max" select="$max"/>
	<xsl:with-param name="sep" select="$sep"/>
      </xsl:call-template>
    </xsl:if>
  </xsl:template>

  <!--
      Auxiliary template for enumerate, do not use directly.
  -->
  <xsl:template name="enumerate-aux">
    <xsl:param name="n"/>
    <xsl:param name="max"/>
    <xsl:param name="sep"/>
    <xsl:if test="$n &lt; $max">
      <xsl:value-of select="concat($sep, $n)"/>
      <xsl:call-template name="enumerate-aux">
	<xsl:with-param name="n" select="$n+1"/>
	<xsl:with-param name="max" select="$max"/>
	<xsl:with-param name="sep" select="$sep"/>
      </xsl:call-template>
    </xsl:if>
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
  <xsl:template name="annotations">
    <xsl:param name="node"/>
    <xsl:param name="next-id"/>
    <xsl:param name="tag-name"/>
    <xsl:param name="layers"/>
    <xsl:param name="feature-name"/>
    <xsl:param name="feature-value"/>
    <xsl:param name="feature-name2"/>
    <xsl:param name="feature-value2"/>
    <xsl:call-template name="annotations-aux">
      <xsl:with-param name="nodes" select="$node/descendant::*|$node/descendant::text()"/>
      <xsl:with-param name="idx" select="1"/>
      <xsl:with-param name="pos" select="0"/>
      <xsl:with-param name="next-id" select="$next-id"/>
      <xsl:with-param name="tag-name" select="$tag-name"/>
      <xsl:with-param name="layers" select="$layers"/>
      <xsl:with-param name="feature-name" select="$feature-name"/>
      <xsl:with-param name="feature-value" select="$feature-value"/>
      <xsl:with-param name="feature-name2" select="$feature-name2"/>
      <xsl:with-param name="feature-value2" select="$feature-value2"/>
    </xsl:call-template>
  </xsl:template>

  <!--
      Auxiliary template for annotations, do not use directly.
  -->
  <xsl:template name="annotations-aux">
    <xsl:param name="nodes"/>
    <xsl:param name="idx"/>
    <xsl:param name="pos"/>
    <xsl:param name="next-id"/>
    <xsl:param name="tag-name"/>
    <xsl:param name="layers"/>
    <xsl:param name="feature-name"/>
    <xsl:param name="feature-value"/>
    <xsl:param name="feature-name2"/>
    <xsl:param name="feature-value2"/>
    <xsl:if test="$idx &lt;= count($nodes)">
      <xsl:variable name="n" select="$nodes[$idx]"/>
      <xsl:choose>
	<xsl:when test="name($n) = $tag-name">
	  <xsl:element name="a">
	    <xsl:if test="$next-id">
	      <xsl:attribute name="id">
		<xsl:value-of select="$next-id"/>
	      </xsl:attribute>
	    </xsl:if>
	    <xsl:if test="$layers">
	      <xsl:attribute name="l">
		<xsl:value-of select="$layers"/>
	      </xsl:attribute>
	    </xsl:if>
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
	  </xsl:element>
	  <xsl:call-template name="annotations-aux">
	    <xsl:with-param name="nodes" select="$nodes"/>
	    <xsl:with-param name="idx" select="$idx+1"/>
	    <xsl:with-param name="pos" select="$pos"/>
	    <xsl:with-param name="next-id" select="$next-id+1"/>
	    <xsl:with-param name="tag-name" select="$tag-name"/>
	    <xsl:with-param name="layers" select="$layers"/>
	    <xsl:with-param name="feature-name" select="$feature-name"/>
	    <xsl:with-param name="feature-value" select="$feature-value"/>
	    <xsl:with-param name="feature-name2" select="$feature-name2"/>
	    <xsl:with-param name="feature-value2" select="$feature-value2"/>
	  </xsl:call-template>
	</xsl:when>
	<xsl:when test="name($n) = ''">
	  <xsl:call-template name="annotations-aux">
	    <xsl:with-param name="nodes" select="$nodes"/>
	    <xsl:with-param name="idx" select="$idx+1"/>
	    <xsl:with-param name="pos" select="$pos+string-length($n)"/>
	    <xsl:with-param name="next-id" select="$next-id"/>
	    <xsl:with-param name="tag-name" select="$tag-name"/>
	    <xsl:with-param name="layers" select="$layers"/>
	    <xsl:with-param name="feature-name" select="$feature-name"/>
	    <xsl:with-param name="feature-value" select="$feature-value"/>
	    <xsl:with-param name="feature-name2" select="$feature-name2"/>
	    <xsl:with-param name="feature-value2" select="$feature-value2"/>
	  </xsl:call-template>
	</xsl:when>
	<xsl:when test="name($n) != $tag-name">
	  <xsl:call-template name="annotations-aux">
	    <xsl:with-param name="nodes" select="$nodes"/>
	    <xsl:with-param name="idx" select="$idx+1"/>
	    <xsl:with-param name="pos" select="$pos"/>
	    <xsl:with-param name="next-id" select="$next-id"/>
	    <xsl:with-param name="tag-name" select="$tag-name"/>
	    <xsl:with-param name="layers" select="$layers"/>
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
