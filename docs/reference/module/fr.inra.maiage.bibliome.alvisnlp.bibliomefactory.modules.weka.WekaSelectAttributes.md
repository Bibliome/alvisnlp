<h1 class="module">WekaSelectAttributes</h1>

## Synopsis

Searches for discrimminating attributes with Weka.

## Description

*WekaSelectAttributes* applies Weka's attribute selection on a training set sepcified by <a href="#examples" class="param">examples</a> and writes the result in <a href="#evaluationFile" class="param">evaluationFile</a>.

## Parameters

<a name="evaluationFile">

### evaluationFile

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/fr.inra.maiage.bibliome.util.streams.TargetStream" class="converter">TargetStream</a>
</div>
File where to write evaluation results.

<a name="evaluator">

### evaluator

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Evaluation metrics, this should be the canonical name of a class that extends Weka's [ASEvaluation](http://weka.sourceforge.net/doc/weka/attributeSelection/ASEvaluation.html).

<a name="examples">

### examples

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.Expression" class="converter">Expression</a>
</div>
Training set examples. This expression is evaluated as a list of elements with the corpus as the context element.

<a name="relationDefinition">

### relationDefinition

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.weka.RelationDefinition" class="converter">RelationDefinition</a>
</div>
Specification of example attributes and class.

<a name="evaluatorOptions">

### evaluatorOptions

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String[]" class="converter">String[]</a>
</div>
Options to the evaluator.

<a name="search">

### search

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Search heuristic, this should be the canonical name of a class that extends Weka's [ASSearch](http://weka.sourceforge.net/doc/weka/attributeSelection/ASSearch.html).

<a name="searchOptions">

### searchOptions

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String[]" class="converter">String[]</a>
</div>
Options to the search heuristic.

