run-alvisnlp extract.plan
check-file-sorted yatea/corpus/default/raw/termList.txt
run-alvisnlp tomap-train.plan
run-alvisnlp tomap-predict.plan
check-file-sorted predictions.txt
