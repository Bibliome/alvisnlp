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
@org.bibliome.util.service.Service(alvisnlp.converters.ParamConverterFactory.class)
public final class <xsl:value-of select="@name"/> extends org.bibliome.util.service.AbstractServiceFactory&lt;Class&lt;?>,alvisnlp.converters.ParamConverter> implements alvisnlp.converters.ParamConverterFactory {
    private final java.util.Collection&lt;Class&lt;?>> supportedConversions = new java.util.LinkedHashSet&lt;Class&lt;?>>();

    public <xsl:value-of select="@name"/>() {<xsl:for-each select="converter">
        supportedConversions.add(<xsl:value-of select="@target-type"/>);</xsl:for-each>
    }
    
    @Override
    public java.util.Collection&lt;Class&lt;?>> supportedServices() {
        return java.util.Collections.unmodifiableCollection(supportedConversions);
    }
    
    @Override
    public boolean isSupported(Class&lt;?> targetType) {
        return supportedConversions.contains(targetType);
    }
    
    @Override
    public alvisnlp.converters.ParamConverter getService(Class&lt;?> targetType) throws org.bibliome.util.service.UnsupportedServiceException {
	alvisnlp.converters.ParamConverter result = null;
        try {
<xsl:for-each select="converter">
            if (<xsl:value-of select="@target-type"/>.equals(targetType))
	        result = new <xsl:value-of select="@name"/>();
        	</xsl:for-each>
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
	if (result == null)
            throw new org.bibliome.util.service.UnsupportedServiceException(targetType.getName());
	result.setComponentConverterFactory(this);
	return result;
    }
}
	</xsl:template>
</xsl:stylesheet>
