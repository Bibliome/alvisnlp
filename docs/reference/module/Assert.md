<h1 class="module">Assert</h1>

## Synopsis

Tests an assertion on specified elements.

## Description

*Assert* evaluates <a href="#target" class="param">target</a> as a list of elements. Then, for each element, evaluates <a href="#assertion" class="param">assertion</a> as a boolean. *Assert* reports each element for which <a href="#assertion" class="param">assertion</a> is `false`. *Assert* aborts the processing of the corpus if <a href="#severe" class="param">severe</a> is `true`.

## Parameters

<h3 name="assertion" class="param">assertion</h3>

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.Expression" class="converter">Expression</a>
</div>
Expression evaluated as a boolean with the target element as the context element. This parameter specifies the assertion to be checked on each target.

<h3 name="target" class="param">target</h3>

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.Expression" class="converter">Expression</a>
</div>
Expression evaluated as a list of elements with the corpus as the context element. This parameter specifies the elements on which the assertion is checked.

<h3 name="message" class="param">message</h3>

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.Expression" class="converter">Expression</a>
</div>
Customize the assertion failure message. This expression is evaluated as a string from the failed target.

<h3 name="outFile" class="param">outFile</h3>

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/fr.inra.maiage.bibliome.util.streams.TargetStream" class="converter">TargetStream</a>
</div>
Path to the file where to record assertion failures. If not set then assertion failures are not recorded, and only displayed on the log.

<h3 name="stopAt" class="param">stopAt</h3>

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/java.lang.Integer" class="converter">Integer</a>
</div>
Maximum number of assertion failures before *Assert* will stop checking.

<h3 name="severe" class="param">severe</h3>

<div class="param-level param-level-default-value">Default value: `true`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.Boolean" class="converter">Boolean</a>
</div>
If `true` and there is at least one assertion failure, then *Assert* will abort the processing of the corpus.

