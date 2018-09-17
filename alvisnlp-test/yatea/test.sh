run-alvisnlp extract.plan
check-file-sorted terms.txt
run-alvisnlp tomap-train.plan
run-alvisnlp tomap-predict.plan
check-file-sorted predictions.txt
