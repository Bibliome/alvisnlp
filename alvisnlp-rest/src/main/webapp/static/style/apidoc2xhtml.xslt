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
		        
		        <title>AlvisNLP/ML API Documentation</title>
			</head>
			<body>
				<div id="navbar">
					<div id="nav-up">
						<a>
							<xsl:attribute name="href">
								<xsl:value-of select="concat(alvisnlp-api/@url-base, '/')"/>
							</xsl:attribute>
							&lt;Back
						</a>
					</div>
				</div>
				<div id="api-toc">
					<xsl:apply-templates select="alvisnlp-api/api-section" mode="toc"/>
				</div>

				<xsl:apply-templates select="alvisnlp-api/api-section"/>
			</body>
		</html>
	</xsl:template>
	
	<xsl:template match="api-section" mode="toc">
		<div class="api-toc-section">
			<a>
				<xsl:attribute name="href">
					<xsl:value-of select="concat('#', @name)"/>
				</xsl:attribute>
				<xsl:value-of select="title"/>
			</a>
		</div>
		<xsl:apply-templates select="api-function" mode="toc"/>
	</xsl:template>
	
	<xsl:template match="api-function" mode="toc">
		<div class="api-toc-function">
			<a>
				<xsl:attribute name="href">
					<xsl:value-of select="concat('#', @name)"/>
				</xsl:attribute>
				<xsl:value-of select="title"/>
			</a>
		</div>
	</xsl:template>
	
	<xsl:template match="api-section">
		<h1 class="api-section">
			<xsl:attribute name="id">
				<xsl:value-of select="@name"/>
			</xsl:attribute>
			<xsl:value-of select="title"/>
		</h1>
		<xsl:apply-templates select="api-function"/>
	</xsl:template>
	
	<xsl:template match="api-function">
		<div class="api-fun">
			<xsl:apply-templates select="title"/>
			<xsl:apply-templates select="method"/>
			<xsl:apply-templates select="url"/>
			<xsl:apply-templates select="parameters"/>
			<xsl:apply-templates select="responses"/>
			<xsl:apply-templates select="description"/>
		</div>
	</xsl:template>

	<xsl:template match="title">
		<h2 class="api-title">
			<xsl:attribute name="id">
				<xsl:value-of select="../@name"/>
			</xsl:attribute>
			<xsl:value-of select="."/>
		</h2>
	</xsl:template>

	<xsl:template match="url">
		<div class="api-url">
			<xsl:value-of select="."/>
		</div>
	</xsl:template>

	<xsl:template match="method">
		<div>
			<xsl:attribute name="class">
				<xsl:value-of select="concat('api-method method-', .)"/>
			</xsl:attribute>
			<xsl:value-of select="."/>
		</div>
	</xsl:template>
	
	<xsl:template match="parameters">
		<div class="api-params-header">Parameters</div>
		<table class="api-params">
			<tbody class="api-params">
				<xsl:apply-templates select="param"/>
			</tbody>
		</table>
	</xsl:template>
	
	<xsl:template match="param">
		<tr class="api-param">
			<td class="api-param-name">
				<xsl:value-of select="@name"/>
			</td>
			<td class="api-param-descr">
				<xsl:apply-templates select="*" mode="descr"/>
			</td>
		</tr>
	</xsl:template>
	
	<xsl:template match="responses">
		<div class="api-responses-header">Responses</div>
		<table class="api-responses">
			<tbody class="api-responses">
				<xsl:apply-templates select="resp"/>
			</tbody>
		</table>
	</xsl:template>
	
	<xsl:template match="resp">
		<tr class="api-resp">
			<td class="api-resp-status">
				<xsl:attribute name="class">
					<xsl:value-of select="concat('api-resp-status status-', substring(@status, 1, 1), 'XX')"/>
				</xsl:attribute>
				<xsl:value-of select="@status"/>
			</td>
			<td class="api-resp-descr">
				<xsl:apply-templates select="*" mode="descr"/>
			</td>
		</tr>
	</xsl:template>
	
	<xsl:template match="description">
		<div class="api-description-header">Description</div>
		<div class="api-description">
			<xsl:apply-templates select="*" mode="descr"/>
		</div>
	</xsl:template>
	
	<xsl:template match="param" mode="descr">
		<code>
			<xsl:value-of select="."/>
		</code>
	</xsl:template>
	
	<xsl:template match="api-function" mode="descr">
		<xsl:variable name="name">
			<xsl:value-of select="."/>
		</xsl:variable>
		<code>
			<xsl:for-each select="//api-function[@name = $name]">
				<a>
					<xsl:attribute name="href">
						<xsl:value-of select="concat('#', $name)"/>
					</xsl:attribute>
					<xsl:value-of select="title"/>
				</a>
			</xsl:for-each>
		</code>
	</xsl:template>
	
	<xsl:template match="*|text()|@*" mode="descr">
		<xsl:copy>
			<xsl:apply-templates select="*|text()|@*" mode="descr"/>
		</xsl:copy>
	</xsl:template>
</xsl:stylesheet>
