# org.bibliome.alvisnlp.modules.Assert

## Synopsis

Tests an assertion on specified elements.

## Description

*org.bibliome.alvisnlp.modules.Assert* evaluates [target](#target) as a list of elements. Then, for each element, evaluates [assertion](#assertion) as a boolean. *org.bibliome.alvisnlp.modules.Assert* reports each element for which [assertion](#assertion) is `false`. *org.bibliome.alvisnlp.modules.Assert* aborts the processing of the corpus if [severe](#severe) is `true`.

## Parameters

<a name="assertion">

### assertion

Optional

Type: [Expression](../converter/alvisnlp.corpus.expressions.Expression)

Expression evaluated as a boolean with the target element as the context element. This parameter specifies the assertion to be checked on each target.

<a name="target">

### target

Optional

Type: [Expression](../converter/alvisnlp.corpus.expressions.Expression)

Expression evaluated as a list of elements with the corpus as the context element. This parameter specifies the elements on which the assertion is checked.

<a name="message">

### message

Optional

Type: [Expression](../converter/alvisnlp.corpus.expressions.Expression)

Customize the assertion failure message. This expression is evaluated as a string from the failed target.

<a name="outFile">

### outFile

Optional

Type: [TargetStream](../converter/org.bibliome.util.streams.TargetStream)

Path to the file where to record assertion failures. If not set then assertion failures are not recorded, and only displayed on the log.

<a name="stopAt">

### stopAt

Optional

Type: [Integer](../converter/java.lang.Integer)

Maximum number of assertion failures before *org.bibliome.alvisnlp.modules.Assert* will stop checking.

<a name="severe">

### severe

Default value: `true`

Type: [Boolean](../converter/java.lang.Boolean)

If `true` and there is at least one assertion failure, then *org.bibliome.alvisnlp.modules.Assert* will abort the processing of the corpus.

