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
	
  <xsl:template match="/library">
package <xsl:value-of select="@package-name"/>;

@javax.annotation.Generated(value={"<xsl:value-of select="@generator"/>"}, date="<xsl:value-of select="@date"/>", comments="<xsl:value-of select="@generator-version"/>")
<xsl:if test="@generate-service = 'true'">
@org.bibliome.util.service.Service(<xsl:value-of select="@service-class"/>.class)
</xsl:if>
public final class <xsl:value-of select="@simple-name"/> extends <xsl:value-of select="@base-class"/> {
    public <xsl:value-of select="@simple-name"/>() {
        super();
    }

    @Override
    public String getName() { return "<xsl:value-of select="@name"/>"; }

    @Override
    public alvisnlp.documentation.Documentation getDocumentation() {
        return new alvisnlp.documentation.ResourceDocumentation("<xsl:value-of select="@resource-bundle-name"/>");
    }

    @Override
    public alvisnlp.corpus.expressions.Evaluator resolveExpression(alvisnlp.corpus.expressions.LibraryResolver resolver, java.util.List&lt;String> ftors, java.util.List&lt;alvisnlp.corpus.expressions.Expression> args) throws alvisnlp.corpus.expressions.ResolverException {
        if (!ftors.isEmpty()) {
	String firstFtor = ftors.get(0);
	int numFtors = ftors.size() - 1;
	int numArgs = args.size();
	switch (firstFtor) {
	    <xsl:for-each select="first-ftor">
	      case "<xsl:value-of select="@value"/>":
	    <xsl:for-each select="function">
	      if ((numFtors == <xsl:value-of select="count(ftor)"/>) &amp;&amp; (numArgs == <xsl:value-of select="count(arg)"/>))
		  return
		  <xsl:choose>
		    <xsl:when test="@custom-expression-implementation = 'true'">
		      <xsl:value-of select="@call-method"/>
		    </xsl:when>
		    <xsl:otherwise>
		  new <xsl:value-of select="concat('Evaluator__', translate(@call-method, '.', '_'), '__', @ord, '__', count(ftor), '__', count(arg))"/>
		    </xsl:otherwise>
		  </xsl:choose>
		  (
		  <xsl:if test="@needs-library-resolver = 'true'">resolver,</xsl:if>
		      <xsl:for-each select="ftor">
			<xsl:if test="position() != 1">, </xsl:if>ftors.get(<xsl:value-of select="position()"/>)
		      </xsl:for-each>
		      <xsl:for-each select="arg">
			<xsl:if test="count(../ftor) != 0 or position() != 1">, </xsl:if>args.get(<xsl:value-of select="position() - 1"/>)<xsl:if test="@is-expression != 'true'">.resolveExpressions(resolver)</xsl:if>
		      </xsl:for-each>
		  );
	    </xsl:for-each>
	    break;
	    </xsl:for-each>
	}
	}
	<xsl:choose>
	  <xsl:when test="@implements-resolve = 'true'">
	    return super.resolveExpression(resolver, ftors, args);
	  </xsl:when>
	  <xsl:otherwise>
	  	return cannotResolve(ftors, args);
	  </xsl:otherwise>
	</xsl:choose>
    }

    <xsl:for-each select="first-ftor/function[@custom-expression-implementation != 'true']">
    private <xsl:value-of select="@static"/> final class <xsl:value-of select="concat('Evaluator__', translate(@call-method, '.', '_'), '__', @ord, '__', count(ftor), '__', count(arg))"/> extends <xsl:value-of select="@base-class"/> {
      <xsl:for-each select="ftor">
	private final String <xsl:value-of select="concat('ftor', position())"/>;
      </xsl:for-each>
      <xsl:for-each select="arg">
	private final alvisnlp.corpus.expressions.Evaluator <xsl:value-of select="concat('arg', position())"/>;
      </xsl:for-each>

      private <xsl:value-of select="concat('Evaluator__', translate(@call-method, '.', '_'), '__', @ord, '__', count(ftor), '__', count(arg))"/>(
      <xsl:for-each select="ftor">
	<xsl:if test="position() != 1">, </xsl:if>String <xsl:value-of select="concat('ftor', position())"/>
      </xsl:for-each>
      <xsl:for-each select="arg">
	<xsl:if test="count(../ftor) != 0 or position() != 1">, </xsl:if>alvisnlp.corpus.expressions.Evaluator <xsl:value-of select="concat('arg', position())"/>
      </xsl:for-each>
      ) {
      <xsl:for-each select="ftor">
	this.<xsl:value-of select="concat('ftor', position())"/> = <xsl:value-of select="concat('ftor', position())"/>;
      </xsl:for-each>
      <xsl:for-each select="arg">
	this.<xsl:value-of select="concat('arg', position())"/> = <xsl:value-of select="concat('arg', position())"/>;
      </xsl:for-each>
      }

      @Override
      public <xsl:value-of select="concat(@return-type, ' ', @evaluation-method)"/>(alvisnlp.corpus.expressions.EvaluationContext ctx, alvisnlp.corpus.Element elt) {
         return <xsl:value-of select="@call-method"/>(
	 <xsl:if test="@needs-evaluation-context = 'true'">
	 ctx, elt
	 </xsl:if>
	 <xsl:for-each select="ftor">
	   <xsl:if test="../@needs-evaluation-context = 'true' or position() != 1">, </xsl:if>
	   <xsl:value-of select="concat('ftor', position())"/>
	 </xsl:for-each>
	 <xsl:for-each select="arg">
	   <xsl:if test="(../@needs-evaluation-context = 'true') or (position() > 1) or (count(../ftor) != 0)">, </xsl:if>
	   <xsl:value-of select="concat('arg', position())"/>
	   <xsl:if test="@evaluation-method != ''">.<xsl:value-of select="@evaluation-method"/>(ctx, elt)</xsl:if>
	 </xsl:for-each>
	 );
      }

      <xsl:if test="@evaluation-method = 'evaluateString'">
	@Override
	public void evaluateString(alvisnlp.corpus.expressions.EvaluationContext ctx, alvisnlp.corpus.Element elt, org.bibliome.util.StringCat strcat) {
	  strcat.append(evaluateString(ctx, elt));
	}
      </xsl:if>

	@Override
	public void collectUsedNames(alvisnlp.module.NameUsage nameUsage, String defaultType) throws alvisnlp.module.ModuleException {
	<xsl:for-each select="ftor">
	  <xsl:if test="@name-type">
	  nameUsage.addNames("<xsl:value-of select="@name-type"/>", <xsl:value-of select="concat('ftor', position())"/>);
	  </xsl:if>
	</xsl:for-each>
	
	<xsl:for-each select="arg">
	  if (<xsl:value-of select="concat('arg', position())"/> != null) {
	    <xsl:value-of select="concat('arg', position())"/>.collectUsedNames(nameUsage, defaultType);
	  }
	</xsl:for-each>
	}

    }
    </xsl:for-each>
}
  </xsl:template>
</xsl:stylesheet>