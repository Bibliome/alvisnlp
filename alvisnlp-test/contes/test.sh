run-alvisnlp word2vec.plan
#check-file vectors.json
#check-file vectors.txt

run-alvisnlp train.plan
run-alvisnlp predict.plan
check-file-sorted predictions.txt
