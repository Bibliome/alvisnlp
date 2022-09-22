<h1 class="module">WekaTrain</h1>

## Synopsis

Trains a Weka classifier where examples are elements.

## Description

 *WekaTrain* builds a Weka training set where examples are elements, trains a classifier and writes it into <a href="#classifierFile" class="param">classifierFile</a> . The training set is specified by <a href="#examples" class="param">examples</a> . Example attributes are specified by <a href="#relationDefinition" class="param">relationDefinition</a> .

 *WekaTrain* activates cross validation if one of the following parameters is set: <a href="#evaluationFile" class="param">evaluationFile</a> , <a href="#foldFeatureKey" class="param">foldFeatureKey</a> , <a href="#predictedClassFeatureKey" class="param">predictedClassFeatureKey</a> .

## Snippet



```xml
<wekatrain class="WekaTrain>
    <algorithm></algorithm>
    <classifierFile></classifierFile>
    <examples></examples>
    <relationDefinition></relationDefinition>
</wekatrain>
```

## Mandatory parameters

<h3 id="algorithm" class="param">algorithm</h3>

<div class="param-level param-level-mandatory">Mandatory
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Classifier algorithm, this must be the canonical name of a class that extends Weka's [Classifier](http://weka.sourceforge.net/doc/weka/classifiers/Classifier.html) .

<h3 id="classifierFile" class="param">classifierFile</h3>

<div class="param-level param-level-mandatory">Mandatory
</div>
<div class="param-type">Type: <a href="../converter/java.io.File" class="converter">File</a>
</div>
File where to write the trained classifier serialization.

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

<h3 id="arffFile" class="param">arffFile</h3>

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/fr.inra.maiage.bibliome.util.streams.TargetStream" class="converter">TargetStream</a>
</div>
File where to write the training set in [ARFF](http://www.cs.waikato.ac.nz/ml/weka/arff.html) format.

<h3 id="classifierInfoFile" class="param">classifierInfoFile</h3>

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/fr.inra.maiage.bibliome.util.streams.TargetStream" class="converter">TargetStream</a>
</div>
File where to write classifier information and statistics.

<h3 id="classifierOptions" class="param">classifierOptions</h3>

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String%5B%5D" class="converter">String[]</a>
</div>
Options to the classifier algorithm.

<h3 id="crossFolds" class="param">crossFolds</h3>

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/java.lang.Integer" class="converter">Integer</a>
</div>
Number of segments for cross validation.

<h3 id="evaluationFile" class="param">evaluationFile</h3>

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/fr.inra.maiage.bibliome.util.streams.TargetStream" class="converter">TargetStream</a>
</div>
File where to write evaluation results.

<h3 id="foldFeatureKey" class="param">foldFeatureKey</h3>

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Feature where to write the fold number in which the training element was in the test set if cross validation is activated.

<h3 id="predictedClassFeatureKey" class="param">predictedClassFeatureKey</h3>

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/java.lang.String" class="converter">String</a>
</div>
Feature where to write the class prediction if cross validation is activated.

<h3 id="randomSeed" class="param">randomSeed</h3>

<div class="param-level param-level-default-value">Default value: `1`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.Long" class="converter">Long</a>
</div>
Random seed used by some algorithms and cross validation.

