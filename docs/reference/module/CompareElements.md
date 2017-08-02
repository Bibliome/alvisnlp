# org.bibliome.alvisnlp.modules.compare.CompareElements

## Synopsis

Compares two sets of elements.

## Description

*org.bibliome.alvisnlp.modules.compare.CompareElements* evaluates [predicted](#predicted) and [reference](#reference) as element lists and compares them according to [similarity](#similarity). Detailed comparison, recall, precision and F-Score are written in [outFile](#outFile).

## Parameters

<a name="face">

### face

Optional

Type: [Expression](../converter/alvisnlp.corpus.expressions.Expression)

Expression evaluated from a reference or predicted element as a string that will be written in [outFile](#outFile).

<a name="outFile">

### outFile

Optional

Type: [TargetStream](../converter/org.bibliome.util.streams.TargetStream)

File where to write results.

<a name="predicted">

### predicted

Optional

Type: [Expression](../converter/alvisnlp.corpus.expressions.Expression)

Set of predicted elements.

<a name="reference">

### reference

Optional

Type: [Expression](../converter/alvisnlp.corpus.expressions.Expression)

Set of reference elements.

<a name="sections">

### sections

Optional

Type: [Expression](../converter/alvisnlp.corpus.expressions.Expression)

Elements from which [predicted](#predicted) and [reference](#reference) are evaluated.

<a name="similarity">

### similarity

Optional

Type: [ElementSimilarity](../converter/org.bibliome.alvisnlp.modules.compare.ElementSimilarity)

Similarity function between two elements.

<a name="showFullMatches">

### showFullMatches

Default value: `true`

Type: [Boolean](../converter/java.lang.Boolean)

Either to write matches where the similarity equals 1 (true positives).

<a name="showPrecision">

### showPrecision

Default value: `true`

Type: [Boolean](../converter/java.lang.Boolean)

Either to write the precision.

<a name="showRecall">

### showRecall

Default value: `true`

Type: [Boolean](../converter/java.lang.Boolean)

Either to write the recall.

