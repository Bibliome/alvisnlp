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
  
  <xsl:output method="xml" indent="yes" encoding="UTF-8" omit-xml-declaration="yes"/>

  <xsl:param name="tool-name"></xsl:param>
  <xsl:param name="tool-version">XXX Tool Version</xsl:param>
  <xsl:param name="tool-description">XXX Tool Description</xsl:param>
  <xsl:param name="command-scheme">python-script</xsl:param>
  <xsl:param name="output-alias">output</xsl:param>

  <xsl:template match="/alvisnlp-plan">
    <xsl:element name="tool">
      <xsl:attribute name="id">
	<xsl:value-of select="@id"/>
      </xsl:attribute>
      <xsl:attribute name="name">
	<xsl:choose>
	  <xsl:when test="$tool-name = ''">
	    <xsl:value-of select="concat('XXX Name of ', @id)"/>
	  </xsl:when>
	  <xsl:otherwise>
	    <xsl:value-of select="$tool-name"/>
	  </xsl:otherwise>
	</xsl:choose>
      </xsl:attribute>
      <xsl:attribute name="version">
 	<xsl:value-of select="$tool-version"/>
      </xsl:attribute>

      <xsl:element name="description">
 	<xsl:value-of select="$tool-description"/>
      </xsl:element>

      <xsl:element name="command">
	<xsl:choose>
	  <xsl:when test="$command-scheme = 'python-script'">
	    <xsl:call-template name="python-script-command"/>
	  </xsl:when>
	  <xsl:when test="$command-scheme = 'alvisnlp-command'">
	    <xsl:call-template name="alvisnlp-command"/>
	  </xsl:when>
	  <xsl:when test="$command-scheme = ''">
	    <xsl:call-template name="default-command"/>
	  </xsl:when>
	  <xsl:otherwise>
	    <xsl:message>Unknown command scheme: <xsl:value-of select="$command-scheme"/></xsl:message>
	    <xsl:call-template name="default-command"/>
	  </xsl:otherwise>
	</xsl:choose>
      </xsl:element>

      <xsl:element name="inputs">
	<xsl:for-each select="param[alias and @name != $output-alias]">
	  <xsl:element name="param">
	    <xsl:attribute name="format">txt</xsl:attribute>
	    <xsl:attribute name="name">
	      <xsl:value-of select="@name"/>
	    </xsl:attribute>
	    <xsl:if test="false">
	      <xsl:attribute name="value">XXX Value</xsl:attribute>
	    </xsl:if>
	    <xsl:attribute name="type">
	      <xsl:call-template name="type-map">
		<xsl:with-param name="alvisnlp-type" select="@type"/>
	      </xsl:call-template>
	    </xsl:attribute>
	    <xsl:attribute name="label">
	      <xsl:choose>
		<xsl:when test="@label != ''">
		  <xsl:value-of select="@label"/>
		</xsl:when>
		<xsl:otherwise>
		  <xsl:value-of select="@name"/>
		</xsl:otherwise>
	      </xsl:choose>
	    </xsl:attribute>
	    <xsl:attribute name="help">
	      <xsl:choose>
		<xsl:when test="@help != ''">
		  <xsl:value-of select="@help"/>
		</xsl:when>
		<xsl:otherwise>
		  <xsl:text>Parameter Help</xsl:text>
		</xsl:otherwise>
	      </xsl:choose>
	    </xsl:attribute>
	  </xsl:element>
	</xsl:for-each>
      </xsl:element>

      <xsl:element name="outputs">
	<xsl:for-each select="param[alias and @name = $output-alias]">
	  <xsl:variable name="type">
	    <xsl:call-template name="type-map">
	      <xsl:with-param name="alvisnlp-type" select="@type"/>
	    </xsl:call-template>
	  </xsl:variable>
	  <xsl:if test="$type != 'data'">
	    <xsl:message>WARNING output parameter '<xsl:value-of select="@name"/>' is not of type 'data'.</xsl:message>
	  </xsl:if>
	  <xsl:element name="data">
	    <xsl:attribute name="name">
	      <xsl:value-of select="@name"/>
	    </xsl:attribute>
	    <xsl:attribute name="format">XXX Output format</xsl:attribute>
	    <xsl:attribute name="help">
	      <xsl:choose>
		<xsl:when test="@help != ''">
		  <xsl:value-of select="@help"/>
		</xsl:when>
		<xsl:otherwise>
		  <xsl:text>XXX Parameter Help</xsl:text>
		</xsl:otherwise>
	      </xsl:choose>
	    </xsl:attribute>
	  </xsl:element>
	</xsl:for-each>
      </xsl:element>
    </xsl:element>
  </xsl:template>

  <xsl:template name="type-map">
    <xsl:param name="alvisnlp-type"/>
    <xsl:choose>
      <xsl:when test="$alvisnlp-type = 'java.lang.Integer'">
	<xsl:text>integer</xsl:text>
      </xsl:when>
      <xsl:when test="$alvisnlp-type = 'java.lang.Long'">
	<xsl:text>integer</xsl:text>
      </xsl:when>
      <xsl:when test="$alvisnlp-type = 'java.lang.Byte'">
	<xsl:text>integer</xsl:text>
      </xsl:when>
      <xsl:when test="$alvisnlp-type = 'java.lang.Float'">
	<xsl:text>float</xsl:text>
      </xsl:when>
      <xsl:when test="$alvisnlp-type = 'java.lang.Double'">
	<xsl:text>float</xsl:text>
      </xsl:when>
      <xsl:when test="$alvisnlp-type = 'java.lang.Boolean'">
	<xsl:text>boolean</xsl:text>
      </xsl:when>
      <xsl:when test="$alvisnlp-type = 'java.lang.String'">
	<xsl:text>text</xsl:text>
      </xsl:when>
      <xsl:when test="$alvisnlp-type = 'java.io.File'">
	<xsl:text>data</xsl:text>
      </xsl:when>
      <xsl:when test="$alvisnlp-type = 'org.bibliome.util.files.ExecutableFile'">
	<xsl:text>data</xsl:text>
      </xsl:when>
      <xsl:when test="$alvisnlp-type = 'org.bibliome.util.files.InputDirectory'">
	<xsl:text>data</xsl:text>
      </xsl:when>
      <xsl:when test="$alvisnlp-type = 'org.bibliome.util.files.InputFile'">
	<xsl:text>data</xsl:text>
      </xsl:when>
      <xsl:when test="$alvisnlp-type = 'org.bibliome.util.files.OutputDirectory'">
	<xsl:text>data</xsl:text>
      </xsl:when>
      <xsl:when test="$alvisnlp-type = 'org.bibliome.util.files.OutputFile'">
	<xsl:text>data</xsl:text>
      </xsl:when>
      <xsl:when test="$alvisnlp-type = 'org.bibliome.util.files.WorkingDirectory'">
	<xsl:text>data</xsl:text>
      </xsl:when>
      <xsl:when test="$alvisnlp-type = 'org.bibliome.util.streams.SourceStream'">
	<xsl:text>data</xsl:text>
      </xsl:when>
      <xsl:when test="$alvisnlp-type = 'org.bibliome.util.streams.TargetStream'">
	<xsl:text>data</xsl:text>
      </xsl:when>
      <xsl:otherwise>
	<xsl:value-of select="concat('XXX Galaxy type for ', $alvisnlp-type)"/>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>

  <xsl:template name="python-script-command">
    <xsl:for-each select="/alvisnlp-plan">
      <xsl:value-of select="concat(@id, '.py')"/>
      <xsl:for-each select="param[alias]">
	<xsl:value-of select="concat(' ', @name, ' ${', @name, '}')"/>
      </xsl:for-each>
    </xsl:for-each>
  </xsl:template>

  <xsl:template name="alvisnlp-command">
    <xsl:for-each select="/alvisnlp-plan">
      <xsl:text>alvisnlp -verbose -noColors</xsl:text>
      <xsl:for-each select="param[alias]">
	<xsl:value-of select="concat(' -alias ', @name, ' ${', @name, '}')"/>
      </xsl:for-each>
      <xsl:value-of select="concat(' ', @id, '.plan')"/>
    </xsl:for-each>
  </xsl:template>

  <xsl:template name="default-command">
    <xsl:text>XXX Command</xsl:text>
  </xsl:template>
</xsl:stylesheet>
