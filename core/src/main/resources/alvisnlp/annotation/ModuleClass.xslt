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
	
	<xsl:param name="full-name"/>

	<xsl:template match="/factory">
	  <xsl:apply-templates select="./module[@full-name = $full-name]"/>
	</xsl:template>
	
	<xsl:template match="module">
	  <xsl:variable name="class-name" select="concat(../@generated-prefix,@name)"/>
package <xsl:value-of select="../@package"/>;

@javax.annotation.Generated(value={"<xsl:value-of select="../@generator"/>"}, date="<xsl:value-of select="../@date"/>", comments="<xsl:value-of select="../@generator-version"/>")
public final class <xsl:value-of select="$class-name"/> extends <xsl:value-of select="@full-name"/> {
    public <xsl:value-of select="$class-name"/>() {
        super();
    }

    @Override
	public String getModuleClass() {
		return "<xsl:value-of select="$full-name"/>";
	}
    
    <xsl:for-each select="accessor">
    private <xsl:value-of select="concat(@type,' ',@java)"/> = <xsl:value-of select="@defaultValue"/>;
    
    @Override
    @alvisnlp.module.lib.Param(publicName="<xsl:value-of select="@public"/>", nameType="<xsl:value-of select="@name-type"/>", mandatory=<xsl:value-of select="@mandatory"/>, defaultDoc="<xsl:value-of select="@defaultDoc"/>")
    public <xsl:value-of select="concat(@type,' ',@getter)"/>() {
        return <xsl:value-of select="@java"/>;
    }
    
    @Override
    public void <xsl:value-of select="@setter"/>(<xsl:value-of select="concat(@type,' ',@java)"/>) {
        this.<xsl:value-of select="@java"/> = <xsl:value-of select="@java"/>;
    }
    </xsl:for-each>

    <xsl:for-each select="funlib">
      <xsl:variable name="internal-class" select="concat('InternalFunctionLibrary', position())"/>

      @Override
      <xsl:value-of select="concat(@access, ' ', @type, ' ', @factory, '(alvisnlp.node.evaluator.FunctionLibrary parent) { return new ', $internal-class, '(parent); }')"/>

      private static final class <xsl:value-of select="concat($internal-class, ' extends ', @type)"/> {
        private <xsl:value-of select="$internal-class"/>(alvisnlp.node.evaluator.FunctionLibrary parent) {
	  super();
	  setParent(parent);
	  <xsl:for-each select="fun">
	    registerFunction(<xsl:value-of select="@javaName"/>);
	  </xsl:for-each>
	}

      <xsl:for-each select="fun">
	<xsl:variable name="evaluator-class" select="concat(@upper-name, 'Evaluator')"/>
	<xsl:variable name="args">
	  <xsl:for-each select="arg">
	    <xsl:if test="position() != 1">,</xsl:if>
	    alvisnlp.node.evaluator.Evaluator <xsl:value-of select="@name"/>
	  </xsl:for-each>
	</xsl:variable>

	private final class <xsl:value-of select="$evaluator-class"/> extends <xsl:value-of select="@evaluator-super"/> {
	  <xsl:for-each select="arg">
	  private final alvisnlp.node.evaluator.Evaluator <xsl:value-of select="@name"/>;
	  </xsl:for-each>

	  private <xsl:value-of select="$evaluator-class"/>(<xsl:value-of select="$args"/>) {
	  <xsl:for-each select="arg">
	    this.<xsl:value-of select="@name"/> = <xsl:value-of select="@name"/>;
	  </xsl:for-each>
	  }

	  @Override
	  public <xsl:value-of select="concat(@return-type, ' ', @evaluator-method)"/>(alvisnlp.node.Node node) {
	    return <xsl:value-of select="@getter"/>(node
	    <xsl:for-each select="arg">, <xsl:value-of select="@name"/></xsl:for-each>
	    );
	  }
	}

	private final alvisnlp.node.evaluator.Function <xsl:value-of select="@javaName"/> = new alvisnlp.node.evaluator.AbstractFunction(&quot;<xsl:value-of select="@publicName"/>&quot;, alvisnlp.node.expression.Type.<xsl:value-of select="@type"/>, <xsl:value-of select="@minArgs"/>, <xsl:value-of select="@maxArgs"/>, <xsl:value-of select="@deterministic"/>) {
	  @Override
	  protected alvisnlp.node.evaluator.Evaluator doGetEvaluator(alvisnlp.node.evaluator.Evaluator[] args) {
	    return new <xsl:value-of select="$evaluator-class"/>(
	    <xsl:for-each select="arg">
	      <xsl:if test="position() != 1">,</xsl:if>
	      <xsl:choose>
		<xsl:when test="../@minArgs >= position()">args[<xsl:value-of select="position() - 1"/>]</xsl:when>
		<xsl:otherwise>args.length >= <xsl:value-of select="position()"/> ? args[<xsl:value-of select="position() - 1"/>] : null</xsl:otherwise>
	      </xsl:choose>
	    </xsl:for-each>
	    );
	  }
	};
      </xsl:for-each>
  }
  
    </xsl:for-each>
    
    <xsl:for-each select="timed-method">
      <xsl:value-of select="concat('@Override ', @scope, ' ', @return, ' ', @name)"/>(
      <xsl:for-each select="param">
	      <xsl:if test="position() != 1">,</xsl:if>
	      <xsl:value-of select="concat(., ' _', position())"/>
      </xsl:for-each>
      )
      <xsl:if test="throws">throws
      	<xsl:for-each select="throws">
	      <xsl:if test="position() != 1">,</xsl:if>
		<xsl:value-of select="."/>      	  
      	</xsl:for-each>
      </xsl:if>
      {
        org.bibliome.util.Timer&lt;alvisnlp.module.TimerCategory> timer = getTimer(_1,
        "<xsl:value-of select="@task"/>",
        <xsl:value-of select="concat('alvisnlp.module.TimerCategory.', @category)"/>,
        true
        );
        <xsl:if test="@return != 'void'"><xsl:value-of select="@return"/> result = </xsl:if> super.<xsl:value-of select="@name"/>(
        <xsl:for-each select="param">
	      <xsl:if test="position() != 1">,</xsl:if>
	      <xsl:value-of select="concat('_', position())"/>
        </xsl:for-each>
        );
        timer.stop();
        <xsl:if test="@return != 'void'">return result;</xsl:if>
      }
      
    </xsl:for-each>
}
	</xsl:template>
</xsl:stylesheet>
