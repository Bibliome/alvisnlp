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

	<xsl:output method="text" encoding="UTF-8"/>
	
	<xsl:template match="/factory">
package <xsl:value-of select="@package"/>;

@javax.annotation.Generated(value={"<xsl:value-of select="@generator"/>"}, date="<xsl:value-of select="@date"/>", comments="<xsl:value-of select="@generator-version"/>")
@org.bibliome.util.service.Service(<xsl:value-of select="@factoryInterface"/>.class)
public final class <xsl:value-of select="@name"/> extends org.bibliome.util.service.AbstractServiceFactory&lt;Class&lt;? extends alvisnlp.module.Module&lt;<xsl:value-of select="@dataClass"/>&gt;>,alvisnlp.module.Module&lt;<xsl:value-of select="@dataClass"/>&gt;> implements <xsl:value-of select="@factoryInterface"/> {
    public <xsl:value-of select="@name"/>() {<xsl:for-each select="module">
        addSupportedService(<xsl:value-of select="@full-name"/>.class);</xsl:for-each>
    }
    
    @Override
    public alvisnlp.module.Module&lt;<xsl:value-of select="@dataClass"/>&gt; getService(Class&lt;? extends alvisnlp.module.Module&lt;<xsl:value-of select="@dataClass"/>&gt;> moduleClass) throws org.bibliome.util.service.UnsupportedServiceException {
        try {<xsl:for-each select="module">
            if (<xsl:value-of select="@full-name"/>.class.equals(moduleClass))<xsl:choose>
                	<xsl:when test="count(accessor|funlib|timed-method) = 0">
                    return new <xsl:value-of select="@full-name"/>();</xsl:when>
                	<xsl:otherwise>
                    return new <xsl:value-of select="concat(../@package,'.',../@generated-prefix,@name)"/>();</xsl:otherwise>
                </xsl:choose>
        	</xsl:for-each>
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        throw new org.bibliome.util.service.UnsupportedServiceException(moduleClass.getName());
    }

    @Override
	public alvisnlp.module.Sequence&lt;<xsl:value-of select="@dataClass"/>&gt; newSequence() {
		return new <xsl:value-of select="@sequenceClass"/>();
	}
	
	@Override
	public String getShellModule() {
		<xsl:choose>
			<xsl:when test="@shellModule">return "<xsl:value-of select="@shellModule"/>";</xsl:when>
			<xsl:otherwise>return null;</xsl:otherwise>
		</xsl:choose>
	}
}
	</xsl:template>
</xsl:stylesheet>
