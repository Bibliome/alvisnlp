run-alvisnlp train.plan
run-alvisnlp predict.plan
count-lines TP.txt 100
search-pattern TP.txt "^T\d+\t[^\t]+\tT\d+\t[^\t]+$"
count-lines FP.txt 1
count-lines FN.txt 1
#check-file TP.txt
#check-file FP.txt
#check-file FN.txt
