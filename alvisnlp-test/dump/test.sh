run-alvisnlp -dumpModule export dump.bin dump.plan
check-file-sorted dump-features.txt
run-alvisnlp -resume dump.bin -alias file resume-features.txt write-features.plan
check-file-sorted resume-features.txt
