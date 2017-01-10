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
		        <xsl:if test="not(alvisnlp-run/statuses/status[@finished = 'true'])">
					<meta http-equiv="refresh">
						<xsl:attribute name="content">
							<xsl:value-of select="concat('10; url=', @url-base, '/api/runs/', @id)"/>
						</xsl:attribute>
					</meta>
		        </xsl:if>
		        
				<title>
					<xsl:value-of select="concat('AlvisNLP/ML ', @plan, ' run: ', @id)"/>
				</title>
			</head>
			<body>
				<xsl:apply-templates/>
			</body>
		</html>
	</xsl:template>

	<xsl:template match="alvisnlp-run">
		<div class="run-prop">Identifier</div>
		<div class="run-value">
			<xsl:value-of select="@id"/>
		</div>
		
		<xsl:apply-templates select="statuses"/>
		<xsl:apply-templates select="." mode="plan"/>
		<xsl:apply-templates select="param-values"/>
		<xsl:apply-templates select="properties"/>
		<xsl:apply-templates select="dir" mode="top"/>
	</xsl:template>

	<xsl:template match="alvisnlp-run" mode="plan">
		<div class="run-prop">Plan</div>
		<div class="run-value">
			<a>
				<xsl:attribute name="href">
					<xsl:value-of select="concat(@url-base, '/api/plans/', @plan-name)" />
				</xsl:attribute>
				<xsl:value-of select="@plan-name" />
			</a>
		</div>
	</xsl:template>
	
	<xsl:template match="statuses">
		<div class="run-prop">Status</div>
		<div class="run-value">
			<table class="statuses">
				<tbody>
					<xsl:variable name="create">
						<xsl:value-of select="status[@status = 'created']/@timestamp"/>
					</xsl:variable>
					<xsl:for-each select="status">
						<tr>
							<xsl:attribute name="class">
								<xsl:choose>
									<xsl:when test="@finished = 'true'">
										<xsl:value-of select="concat('finished-status status-', @status)"/>
									</xsl:when>
									<xsl:otherwise>
										<xsl:value-of select="concat('status-', @status)"/>
									</xsl:otherwise>
								</xsl:choose>
							</xsl:attribute>
							<td class="status">
								<xsl:value-of select="@status"/>
							</td>
							<td class="status-timestamp">
								<xsl:value-of select="@timestamp - $create"/>ms
							</td>
						</tr>
					</xsl:for-each>
				</tbody>
			</table>
		</div>
	</xsl:template>

	<xsl:template match="param-values">
		<div class="run-prop">Parameters</div>
		<div class="run-value">
			<table class="run-params">
				<tbody>
					<xsl:apply-templates select="param-value"/>
				</tbody>
			</table>
		</div>
	</xsl:template>
	
	<xsl:template match="param-value">
		<tr class="run-param">
			<td class="run-param-name">
				<a>
					<xsl:attribute name="href">
						<xsl:value-of select="concat(/alvisnlp-run/@url-base, '/api/plans/', ../@plan-name, '#', @name)"/>
					</xsl:attribute>
					<xsl:value-of select="@name"/>
				</a>
			</td>
			<td class="run-param-method">
				<img class="param-method-icon">
					<xsl:attribute name="src">
						<xsl:value-of select="concat(/alvisnlp-run/@url-base, '/static/images/param-', @method, '.png')"/>
					</xsl:attribute>
					<xsl:attribute name="alt">
						<xsl:value-of select="@method"/>
					</xsl:attribute>
				</img>
			</td>
			<td class="run-param-value">
				<xsl:value-of select="."/>
			</td>
		</tr>
	</xsl:template>
	
	<xsl:template match="properties">
		<div class="run-prop">Properties</div>
		<div class="run-value">
			<table class="run-props">
				<tbody>
					<xsl:apply-templates select="*" mode="prop"/>
				</tbody>
			</table>
		</div>
	</xsl:template>
	
	<xsl:template match="*" mode="prop">
		<tr class="run-property">
			<td class="run-property-key">
				<xsl:value-of select="name()"/>
			</td>
			<td class="run-property-value">
				<xsl:value-of select="."/>
			</td>
		</tr>
	</xsl:template>

	<xsl:template match="dir" mode="top">
		<div class="run-prop">Output</div>
		<div class="run-value">
			<xsl:value-of select="@name"/>
		</div>
		<div class="run-prop"/>
		<div class="run-value">
			<ul class="dir-list dir-list-top">
				<xsl:apply-templates select="*" mode="list"/>
			</ul>
		</div>
	</xsl:template>
	
	<xsl:template match="dir" mode="list">
		<li class="file-name output-dir">
			<xsl:variable name="icon">
				<xsl:choose>
					<xsl:when test="@empty = 'true'">dir-empty.png</xsl:when>
					<xsl:otherwise>dir-full.png</xsl:otherwise>
				</xsl:choose>
			</xsl:variable>
			<img class="output-icon">
				<xsl:attribute name="src">
					<xsl:value-of select="concat(/alvisnlp-run/@url-base, '/static/images/', $icon)"/>
				</xsl:attribute>
			</img>
			<a>
				<xsl:attribute name="href">
					<xsl:value-of select="concat(/alvisnlp-run/@url-base, '/api/runs/', /alvisnlp-run/@id,'/output/', @path)"/>
				</xsl:attribute>
				<xsl:value-of select="@name"/>/
			</a>
		</li>
		<ul class="dir-list">
			<xsl:apply-templates select="*" mode="list"/>
		</ul>
	</xsl:template>

	<xsl:template match="file" mode="list">
		<li class="file-name output-file">
			<img class="output-icon">
				<xsl:attribute name="src">
					<xsl:value-of select="concat(/alvisnlp-run/@url-base, '/static/images/file.png')"/>
				</xsl:attribute>
			</img>
			<a>
				<xsl:attribute name="href">
					<xsl:value-of select="concat(/alvisnlp-run/@url-base, '/api/runs/', /alvisnlp-run/@id, '/output/', @path)"/>
				</xsl:attribute>
				<xsl:value-of select="@name"/>
			</a>
			<xsl:value-of select="concat('&#xA0;(', @human-size, ')')"/>
		</li>
	</xsl:template>
</xsl:stylesheet>
