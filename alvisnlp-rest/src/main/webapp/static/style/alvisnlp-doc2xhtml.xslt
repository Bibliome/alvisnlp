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


<xsl:stylesheet version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	>

	<xsl:output doctype-public="html" indent="yes" method="html" />
	
	<xsl:template match="/">
		<html>
			<head>
		        <link rel="stylesheet">
		        	<xsl:attribute name="href">
		        		<xsl:value-of select="concat(/*/@url-base, '/static/style/alvisnlp.css')"/>
		        	</xsl:attribute>
		        </link>
		        
				<xsl:apply-templates mode="head"/>
			</head>
			<body>
				<xsl:apply-templates mode="body"/>
			</body>
		</html>
	</xsl:template>

	<xsl:template match="alvisnlp-doc" mode="head">
		<title>
			<xsl:value-of select="@short-target"/>
		</title>
	</xsl:template>
	
	<xsl:template match="alvisnlp-supported-modules" mode="head">
		<title>Alvisnlp/ML Modules</title>
	</xsl:template>
	
	<xsl:template match="alvisnlp-supported-plans" mode="head">
		<title>Alvisnlp/ML Plans</title>
	</xsl:template>
	
	<xsl:template match="alvisnlp-supported-converters" mode="head">
		<title>Alvisnlp/ML Converters</title>
	</xsl:template>
	
	<xsl:template match="alvisnlp-supported-libraries" mode="head">
		<title>Alvisnlp/ML Libraries</title>
	</xsl:template>
	
	<xsl:template name="nav-bar">
		<xsl:param name="path"><xsl:text></xsl:text></xsl:param>
		<div id="navbar">
			<div id="nav-up">
				<a>
					<xsl:attribute name="href">
						<xsl:value-of select="concat(/*/@url-base, '/', $path)"/>
					</xsl:attribute>
					&lt;Back
				</a>
			</div>
		</div>
	</xsl:template>

	<xsl:template match="alvisnlp-doc" mode="body">
		<xsl:call-template name="nav-bar">
			<xsl:with-param name="path" select="concat('api/', @resource-list)"/>
		</xsl:call-template>
		<h1>
			<xsl:attribute name="class">
				<xsl:value-of select="@resource-type"/>
			</xsl:attribute>
			<xsl:value-of select="@short-target"/>
		</h1>
		<xsl:apply-templates select="synopsis"/>
		<xsl:apply-templates select="module-doc|converter-doc|library-doc|plan-doc"/>
	</xsl:template>

	<xsl:template match="alvisnlp-supported-modules" mode="body">
		<xsl:call-template name="nav-bar"/>
		<h1>Alvisnlp/ML Supported Modules</h1>
		<xsl:apply-templates select="module-item">
			<xsl:sort select="@target"/>
		</xsl:apply-templates>
	</xsl:template>

	<xsl:template match="alvisnlp-supported-plans" mode="body">
		<xsl:call-template name="nav-bar"/>
		<h1>Alvisnlp/ML Supported Plans</h1>
		<xsl:apply-templates select="plan-item">
			<xsl:sort select="@target"/>
		</xsl:apply-templates>
	</xsl:template>

	<xsl:template match="alvisnlp-supported-libraries" mode="body">
		<xsl:call-template name="nav-bar"/>
		<h1>Alvisnlp/ML Supported Libraries</h1>
		<xsl:apply-templates select="library-item">
			<xsl:sort select="@short-target"/>
		</xsl:apply-templates>
	</xsl:template>

	<xsl:template match="alvisnlp-supported-converters" mode="body">
		<xsl:call-template name="nav-bar"/>
		<h1>Alvisnlp/ML Supported Converters</h1>
		<xsl:apply-templates select="converter-item">
			<xsl:sort select="@short-target"/>
		</xsl:apply-templates>
	</xsl:template>

	<xsl:template match="module-item|converter-item|library-item|plan-item">
		<xsl:variable name="class">
			<xsl:value-of select="substring-before(name(), '-')"/>
		</xsl:variable>
		<xsl:variable name="list">
			<xsl:choose>
				<xsl:when test="name() = 'library-item'">libraries</xsl:when>
				<xsl:otherwise>
					<xsl:value-of select="concat($class, 's')"/>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<h2 class="item">
			<a>
				<xsl:attribute name="class">
					<xsl:value-of select="concat('item ', $class)"/>
				</xsl:attribute>
				<xsl:attribute name="href">
					<xsl:value-of select="concat(/*/@url-base, '/api/', $list, '/', @short-target)"/>
				</xsl:attribute>
				<xsl:value-of select="@short-target"/>
			</a>
		</h2>
		<xsl:if test="synopsis">
			<div class="li-synopsis">
				<xsl:apply-templates select="synopsis/*" />
			</div>
		</xsl:if>
	</xsl:template>

	<xsl:template match="synopsis">
		<div id="synopsis">
			<h2>Synopsis</h2>
			<xsl:apply-templates/>
		</div>
	</xsl:template>

	<xsl:template match="module-doc|plan-doc">
		<xsl:if test="/alvisnlp-doc/@beta = 'true'">
			<div id="beta">
				This module is experimental.
			</div>
		</xsl:if>
		<xsl:if test="/alvisnlp-doc/@use-instead != ''">
			<div id="use-instead">
				This module is obsolete, superceded by <xsl:value-of select="/alvisnlp-doc/@use-instead"/>
			</div>
		</xsl:if>
		<xsl:apply-templates select="description"/>
		<div id="parameters">
			<h2>Parameters</h2>
			<xsl:apply-templates select="param-doc[not(@default-value) and @mandatory = 'true']">
				<xsl:sort select="@name"/>
			</xsl:apply-templates>
			<xsl:apply-templates select="param-doc[not(@default-value) and @mandatory = 'false']">
				<xsl:sort select="@name"/>
			</xsl:apply-templates>
			<xsl:apply-templates select="param-doc[@default-value and @name != 'active' and @name != 'userFunctions']">
				<xsl:sort select="@name"/>
			</xsl:apply-templates>
		</div>
	</xsl:template>
	
	<xsl:template match="description">
		<div id="description">
			<h2>Description</h2>
			<xsl:apply-templates/>
		</div>
	</xsl:template>
	
	<xsl:template match="param-doc">
		<div class="param-doc">
			<xsl:attribute name="id">
				<xsl:value-of select="@name"/>
			</xsl:attribute>
			<h3 class="param">
				<xsl:value-of select="@name"/>
			</h3>
			<div>
				<xsl:choose>
					<xsl:when test="@default-value">
						<xsl:attribute name="class">param-level param-level-default-value</xsl:attribute>
						Default value: <xsl:value-of select="@default-value"/>
					</xsl:when>
					<xsl:when test="@mandatory = 'true'">
						<xsl:attribute name="class">param-level param-level-mandatory</xsl:attribute>
						Mandatory
					</xsl:when>
					<xsl:otherwise>
						<xsl:attribute name="class">param-level param-level-optional</xsl:attribute>
						Optional
					</xsl:otherwise>
				</xsl:choose>
			</div>
			<div class="param-type">
				Type:
				<a class="param-type">
					<xsl:attribute name="href">
						<xsl:value-of select="concat(/alvisnlp-doc/@url-base, 'api/converters/', @type)"/>
					</xsl:attribute>
					<xsl:value-of select="@short-type"/>
				</a>
			</div>
			<xsl:apply-templates/>
		</div>
	</xsl:template>
	
	<xsl:template match="converter-doc">
		<xsl:apply-templates select="string-conversion"/>
		<xsl:apply-templates select="xml-conversion"/>
	</xsl:template>
	
	<xsl:template match="string-conversion">
		<div id="#string-conversion">
			<h2>String conversion</h2>
			<xsl:apply-templates select="*"/>
		</div>
	</xsl:template>
	
	<xsl:template match="xml-conversion">
		<div id="#xml-conversion">
			<h2>XML conversion</h2>
			<xsl:apply-templates select="*"/>
		</div>
	</xsl:template>
	
	<xsl:template match="library-doc">
		<h2>Functions</h2>
		<xsl:apply-templates select="function-doc">
			<xsl:sort select="@first-ftor"/>
		</xsl:apply-templates>
	</xsl:template>
	
	<xsl:template match="function-doc">
		<div class="function">
			<h3>
				<xsl:value-of select="@first-ftor"/>
			</h3>
			<div class="function-synopsis">
				<xsl:value-of select="@synopsis"/>
			</div>
			<xsl:apply-templates select="*"/>
		</div>
	</xsl:template>
	
	<xsl:template match="this">
		<span>
			<xsl:attribute name="class">
				<xsl:value-of select="/alvisnlp-doc/@resource-type"/>
			</xsl:attribute>
			<xsl:value-of select="/alvisnlp-doc/@short-target"/>
		</span>
	</xsl:template>
	
	<xsl:template match="param">
		<xsl:choose>
			<xsl:when test="@module">
				<a class="param">
					<xsl:attribute name="href">
						<xsl:value-of select="concat(/alvisnlp-doc/@url-base, 'modules/', @module, '#', @name)"/>
					</xsl:attribute>
					<xsl:value-of select="concat(@module, '#', @name)"/>
				</a>
			</xsl:when>
			<xsl:otherwise>
				<a class="param">
					<xsl:attribute name="href">
						<xsl:value-of select="concat('#', @name)"/>
					</xsl:attribute>
					<xsl:value-of select="@name"/>
				</a>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template match="converter">
		<xsl:element name="a">
			<xsl:attribute name="href">
				<xsl:value-of select="concat(/alvisnlp-doc/@url-base, '/api/converters/', @name)"/>
			</xsl:attribute>
			<xsl:value-of select="@name"/>
		</xsl:element>
	</xsl:template>
	
	<xsl:template match="a">
		<xsl:copy>
			<xsl:copy-of select="@*"/>
			<xsl:attribute name="class">external</xsl:attribute>
			<xsl:apply-templates select="*|text()" />
		</xsl:copy>
	</xsl:template>
	
	<xsl:template match="*">
		<xsl:copy>
			<xsl:copy-of select="@*"/>
			<xsl:apply-templates select="*|text()" />
		</xsl:copy>
	</xsl:template>

</xsl:stylesheet>
