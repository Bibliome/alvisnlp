# org.bibliome.alvisnlp.modules.weka.WekaTrain

## Synopsis

Trains a Weka classifier where examples are elements.

## Description

*org.bibliome.alvisnlp.modules.weka.WekaTrain* builds a Weka training set where examples are elements, trains a classifier and writes it into [classifierFile](#classifierFile). The training set is specified by [examples](#examples). Example attributes are specified by [relationDefinition](#relationDefinition).

*org.bibliome.alvisnlp.modules.weka.WekaTrain* activates cross validation if one of the following parameters is set: [evaluationFile](#evaluationFile), [foldFeatureKey](#foldFeatureKey), [predictedClassFeatureKey](#predictedClassFeatureKey).

## Parameters

<a name="algorithm">

### algorithm

Optional

Type: [String](../converter/java.lang.String)

Classifier algorithm, this must be the canonical name of a class that extends Weka's [Classifier](http://weka.sourceforge.net/doc/weka/classifiers/Classifier.html).

<a name="classifierFile">

### classifierFile

Optional

Type: [File](../converter/java.io.File)

File where to write the trained classifier serialization.

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

<a name="arffFile">

### arffFile

Optional

Type: [TargetStream](../converter/org.bibliome.util.streams.TargetStream)

File where to write the training set in [ARFF](http://www.cs.waikato.ac.nz/ml/weka/arff.html) format.

<a name="classifierInfoFile">

### classifierInfoFile

Optional

Type: [TargetStream](../converter/org.bibliome.util.streams.TargetStream)

File where to write classifier information and statistics.

<a name="classifierOptions">

### classifierOptions

Optional

Type: [String[]](../converter/java.lang.String[])

Options to the classifier algorithm.

<a name="crossFolds">

### crossFolds

Optional

Type: [Integer](../converter/java.lang.Integer)

Number of segments for cross validation.

<a name="evaluationFile">

### evaluationFile

Optional

Type: [TargetStream](../converter/org.bibliome.util.streams.TargetStream)

File where to write evaluation results, if cross validation is activated.

<a name="foldFeatureKey">

### foldFeatureKey

Optional

Type: [String](../converter/java.lang.String)

Feature where to write the fold number in which the training element was in the test set if cross validation is activated.

<a name="predictedClassFeatureKey">

### predictedClassFeatureKey

Optional

Type: [String](../converter/java.lang.String)

Feature where to write the class prediction if cross validation is activated.

<a name="randomSeed">

### randomSeed

Default value: `1`

Type: [Long](../converter/java.lang.Long)

Random seed used by some algorithms and cross validation.

