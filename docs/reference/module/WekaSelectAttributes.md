# org.bibliome.alvisnlp.modules.weka.WekaSelectAttributes

## Synopsis

Searches for discrimminating attributes with Weka.

## Description

*org.bibliome.alvisnlp.modules.weka.WekaSelectAttributes* applies Weka's attribute selection on a training set sepcified by [examples](#examples) and writes the result in [evaluationFile](#evaluationFile).

## Parameters

<a name="evaluationFile">

### evaluationFile

Optional

Type: [TargetStream](../converter/org.bibliome.util.streams.TargetStream)

File where to write evaluation results.

<a name="evaluator">

### evaluator

Optional

Type: [String](../converter/java.lang.String)

Evaluation metrics, this should be the canonical name of a class that extends Weka's [ASEvaluation](http://weka.sourceforge.net/doc/weka/attributeSelection/ASEvaluation.html).

<a name="examples">

### examples

Optional

Type: [Expression](../converter/alvisnlp.corpus.expressions.Expression)

Training set examples. This expression is evaluated as a list of elements with the corpus as the context element.

<a name="relationDefinition">

### relationDefinition

Optional

Type: [RelationDefinition](../converter/org.bibliome.alvisnlp.modules.weka.RelationDefinition)

Specification of example attributes and class.

<a name="evaluatorOptions">

### evaluatorOptions

Optional

Type: [String[]](../converter/java.lang.String[])

Options to the evaluator.

<a name="search">

### search

Optional

Type: [String](../converter/java.lang.String)

Search heuristic, this should be the canonical name of a class that extends Weka's [ASSearch](http://weka.sourceforge.net/doc/weka/attributeSelection/ASSearch.html).

<a name="searchOptions">

### searchOptions

Optional

Type: [String[]](../converter/java.lang.String[])

Options to the search heuristic.

