<!--
Copyright 2016 Institut National de la Recherche Agronomique

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
		xmlns:bibliome="xalan://org.bibliome.util.xml.Functions"
		>
  
  <xsl:output method="text" encoding="UTF-8"/>

  <xsl:param name="name-section"/>
  <xsl:param name="synopsis"/>
  <xsl:param name="description"/>
  <xsl:param name="parameters"/>
  <xsl:param name="string-conversion"/>
  <xsl:param name="xml-conversion"/>
  <xsl:param name="functions"/>
  <xsl:param name="modules"/>
  <xsl:param name="converters"/>
  <xsl:param name="full-names"/>
  <xsl:param name="short-names"/>

  <xsl:param name="document-class"/>
  <xsl:param name="top-section">section</xsl:param>
  <xsl:param name="sub-section">subsection*</xsl:param>
  <xsl:param name="param-section">subsubsection*</xsl:param>
  <xsl:param name="function-section">subsection*</xsl:param>

  <xsl:template name="target">
    <xsl:value-of select="/alvisnlp-doc/@target"/>
  </xsl:template>

  <xsl:template name="short-target">
    <xsl:value-of select="/alvisnlp-doc/@short-target"/>
  </xsl:template>

  <xsl:template name="type">
    <xsl:choose>
      <xsl:when test="/alvisnlp-doc/module-doc">module</xsl:when>
      <xsl:when test="/alvisnlp-doc/converter-doc">converter</xsl:when>
      <xsl:when test="/alvisnlp-doc/library-doc">library</xsl:when>
    </xsl:choose>
  </xsl:template>

  <xsl:template match="alvisnlp-doc">
    <xsl:call-template name="doc-header">
      <xsl:with-param name="doc" select="."/>
    </xsl:call-template>
    <xsl:apply-templates select="synopsis"/>
    <xsl:apply-templates select="module-doc|converter-doc|library-doc"/>
    <xsl:call-template name="doc-footer">
      <xsl:with-param name="doc" select="."/>
    </xsl:call-template>
  </xsl:template>

  <xsl:template match="module-doc">
    <xsl:apply-templates select="description"/>
    <xsl:call-template name="section-header">
      <xsl:with-param name="name" select="$parameters"/>
      <xsl:with-param name="id">params</xsl:with-param>
    </xsl:call-template>
    <xsl:apply-templates select="param-doc">
      <xsl:sort select="@name"/>
    </xsl:apply-templates>
  </xsl:template>

  <xsl:template match="converter-doc">
    <xsl:apply-templates select="string-conversion"/>
    <xsl:apply-templates select="xml-conversion"/>
  </xsl:template>

  <xsl:template match="library-doc">
    <xsl:call-template name="section-header">
      <xsl:with-param name="name" select="$functions"/>
      <xsl:with-param name="id">functions</xsl:with-param>
    </xsl:call-template>
    <xsl:apply-templates select="function-doc">
      <xsl:sort select="@first-ftor"/>
    </xsl:apply-templates>
  </xsl:template>

  <xsl:template match="synopsis|description|string-conversion|xml-conversion">
    <xsl:call-template name="section-header">
      <xsl:with-param name="name">
	<xsl:choose>
	  <xsl:when test="name() = 'synopsis'"><xsl:value-of select="$synopsis"/></xsl:when>
	  <xsl:when test="name() = 'description'"><xsl:value-of select="$description"/></xsl:when>
	  <xsl:when test="name() = 'string-conversion'"><xsl:value-of select="$string-conversion"/></xsl:when>
	  <xsl:when test="name() = 'xml-conversion'"><xsl:value-of select="$xml-conversion"/></xsl:when>
	</xsl:choose>
      </xsl:with-param>
      <xsl:with-param name="id" select="name()"/>
    </xsl:call-template>
    <xsl:apply-templates select="*"/>
  </xsl:template>

  <xsl:template match="param-doc">
    <xsl:call-template name="param-header">
      <xsl:with-param name="param-doc" select="."/>
    </xsl:call-template>
    <xsl:apply-templates select="p"/>
  </xsl:template>

  <xsl:template match="function-doc">
    <xsl:call-template name="function-header">
      <xsl:with-param name="function-doc" select="."/>
    </xsl:call-template>
    <xsl:apply-templates select="p"/>
  </xsl:template>

  <xsl:template name="doc-header">
    <xsl:param name="doc"/> <!-- alvisnlp-doc node -->
    <xsl:if test="$document-class != ''">
      \documentclass[a4paper]{<xsl:value-of select="$document-class"/>}
      \usepackage{hyperref}
      \title{<xsl:value-of select="$doc/@target"/>}
      \author{<xsl:value-of select="$doc/@author"/>}
      \date{<xsl:value-of select="$doc/@date"/>}
      \begin{document}
      \maketitle
    </xsl:if>
    \<xsl:value-of select="$top-section"/>{<xsl:value-of select="$doc/@short-target"/>}
    \label{<xsl:call-template name="type"/>:<xsl:value-of select="$doc/@short-target"/>}
    \label{<xsl:call-template name="type"/>:<xsl:value-of select="$doc/@target"/>}
  </xsl:template>

  <xsl:template name="doc-footer">
    <xsl:param name="doc"/> <!-- alvisnlp-doc node -->
    <xsl:if test="$document-class != ''">
      \end{document}
    </xsl:if>
  </xsl:template>

  <xsl:template name="section-header">
    <xsl:param name="name"/>  <!-- name of the section header -->
    <xsl:param name="id"/>    <!-- id of the section header -->
    \<xsl:value-of select="$sub-section"/>{<xsl:value-of select="bibliome:capital($name)"/>}
    \label{<xsl:call-template name="type"/>:<xsl:call-template name="target"/>:<xsl:value-of select="$id"/>}
    \label{<xsl:call-template name="type"/>:<xsl:call-template name="short-target"/>:<xsl:value-of select="$id"/>}
  </xsl:template>

  <xsl:template name="param-header">
    <xsl:param name="param-doc"/>  <!-- param-doc node -->
    \<xsl:value-of select="$param-section"/>{<xsl:value-of select="$param-doc/@name"/>}
    \label{module:<xsl:call-template name="target"/>:param:<xsl:value-of select="$param-doc/@name"/>}
    \label{module:<xsl:call-template name="short-target"/>:param:<xsl:value-of select="$param-doc/@name"/>}
    \textbf{Type:} \hyperref[converter:<xsl:value-of select="bibliome:replace($param-doc/@short-type, '[]', 'Array')"/>]{<xsl:value-of select="bibliome:replace($param-doc/@short-type, '[]', '\[\]')"/>}\\
    \textbf{<xsl:value-of select="$param-doc/@mandatory"/>}

  </xsl:template>

  <xsl:template name="function-header">
    <xsl:param name="function-doc"/>
    \<xsl:value-of select="$function-section"/>{<xsl:value-of select="$function-doc/@first-ftor"/>}
    \label{lib:<xsl:call-template name="target"/>:fun:<xsl:value-of select="$function-doc/@first-ftor"/>}
    \label{lib:<xsl:call-template name="target"/>:fun:<xsl:value-of select="$function-doc/@first-ftor"/>}
    \textbf{Synopsis:} <xsl:value-of select="$function-doc/@synopsis"/>

  </xsl:template>

  <xsl:template match="p">
    <xsl:text>

    </xsl:text>
    <xsl:apply-templates select="*|text()"/>
    <xsl:text>

    </xsl:text>
  </xsl:template>

  <xsl:template match="text()">
    <xsl:value-of select="bibliome:escapeLatex(.)"/>
  </xsl:template>

  <xsl:template match="br">
    <xsl:text>\\
</xsl:text>
  </xsl:template>

  <xsl:template match="em">
    <xsl:text/>\emph{<xsl:value-of select="bibliome:escapeLatex(.)"/>}<xsl:text/>
  </xsl:template>

  <xsl:template match="strong">
    <xsl:text/>\textbf{<xsl:value-of select="."/>}<xsl:text/>
  </xsl:template>

  <xsl:template match="ul">
    \begin{itemize}
    <xsl:apply-templates select="li"/>
    \end{itemize}
  </xsl:template>

  <xsl:template match="ol">
    \begin{enumerate}
    <xsl:apply-templates select="li"/>
    \end{enumerate}
  </xsl:template>

  <xsl:template match="li">
    \item <xsl:apply-templates select="*|text()"/>
  </xsl:template>

  <xsl:template match="a">
    <xsl:text/>\href{<xsl:value-of select="@href"/>}{<xsl:value-of select="."/>}<xsl:text/>
  </xsl:template>

  <xsl:template match="xverb">
    \texttt{<xsl:apply-templates select="*|text()" mode="xverb"/>}
  </xsl:template>

  <xsl:template match="text()" mode="xverb">
    <span class="xverb">
      <xsl:value-of select="bibliome:escapeLatex(.)"/>
    </span>
  </xsl:template>

  <xsl:template match="*" mode="xverb">
    <span class="xverb">&lt;</span>
    <span class="xverbtag"><xsl:value-of select="name()"/></span>
    <xsl:for-each select="@*">
      <span class="xverbattr">~<xsl:value-of select="bibliome:escapeLatex(name())"/></span>
      <span class="xverb">=</span>
      <span class="xverbstr">"<xsl:value-of select="bibliome:escapeLatex(.)"/>"</span>
    </xsl:for-each>
    <xsl:choose>
      <xsl:when test=". = ''">
	<span class="xverb">/&gt;</span>
      </xsl:when>
      <xsl:otherwise>
	<span class="xverb">&gt;</span>
	<xsl:apply-templates select="*|text()" mode="xverb"/>
	<span class="xverb">&lt;/</span>
	<span class="xverbtag"><xsl:value-of select="name()"/></span>
	<span class="xverb">&gt;</span>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>

  <xsl:template match="param">
    <xsl:variable name="module">
      <xsl:choose>
	<xsl:when test="@module">
	  <xsl:value-of select="@module"/>
	</xsl:when>
	<xsl:otherwise>
	  <xsl:call-template name="target"/>
	</xsl:otherwise>
      </xsl:choose>
    </xsl:variable>
    <xsl:text/>\hyperref[<xsl:value-of select="concat('module:', $module, ':param:', @name)"/>]{<xsl:value-of select="@name"/>}<xsl:text/>
  </xsl:template>

  <xsl:template match="module">
    <xsl:text/>\hyperref[<xsl:value-of select="concat('module:', @name)"/>]{<xsl:value-of select="@name"/>}<xsl:text/>
  </xsl:template>

  <xsl:template match="converter">
    <xsl:variable name="name" select="bibliome:replace(@name, '[]', 'Array')"/>
    <xsl:text/>\hyperref[<xsl:value-of select="concat('converter:', bibliome:replace(@name, '[]', 'Array'))"/>]{<xsl:value-of select="bibliome:replace(@name, '[]', '\[\]')"/>}<xsl:text/>
  </xsl:template>
</xsl:stylesheet>
