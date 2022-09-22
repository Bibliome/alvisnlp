<h1 class="module">WekaSelectAttributes</h1>

## Synopsis

Searches for discrimminating attributes with Weka.

## Description

 *WekaSelectAttributes* applies Weka's attribute selection on a training set specified by <a href="#examples" class="param">examples</a> and writes the result in <a href="#evaluationFile" class="param">evaluationFile</a> .

## Snippet



```xml
<wekaselectattributes class="WekaSelectAttributes>
    <evaluationFile></evaluationFile>
    <evaluator></evaluator>
    <examples></examples>
    <relationDefinition></relationDefinition>
</wekaselectattributes>
```

## Mandatory parameters

<h3 id="evaluationFile" class="param">evaluationFile</h3>

<div class="param-level param-level-mandatory">Mandatory
</div>
<div class="param-type">Type: <a href="../converter/fr.inra.maiage.bibliome.util.streams.TargetStream" class="converter">TargetStream</a>
</div>
File where to write evaluation results.

<h3 id="evaluator" class="param">evaluator</h3>

<div class="param-level param-level-mandatory">Mandatory
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Evaluation metrics, this should be the canonical name of a class that extends Weka's [ASEvaluation](http://weka.sourceforge.net/doc/weka/attributeSelection/ASEvaluation.html) .

<h3 id="examples" class="param">examples</h3>

<div class="param-level param-level-mandatory">Mandatory
</div>
<div class="param-type">Type: <a href="../converter/fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.Expression" class="converter">Expression</a>
</div>
Training set examples. This expression is evaluated as a list of elements with the corpus as the context element.

<h3 id="relationDefinition" class="param">relationDefinition</h3>

<div class="param-level param-level-mandatory">Mandatory
</div>
<div class="param-type">Type: <a href="../converter/fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.weka.RelationDefinition" class="converter">RelationDefinition</a>
</div>
Specification of example attributes and class.

## Optional parameters

<h3 id="evaluatorOptions" class="param">evaluatorOptions</h3>

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String%5B%5D" class="converter">String[]</a>
</div>
Options to the evaluator.

<h3 id="search" class="param">search</h3>

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Search heuristic, this should be the canonical name of a class that extends Weka's [ASSearch](http://weka.sourceforge.net/doc/weka/attributeSelection/ASSearch.html) .

<h3 id="searchOptions" class="param">searchOptions</h3>

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String%5B%5D" class="converter">String[]</a>
</div>
Options to the search heuristic.

