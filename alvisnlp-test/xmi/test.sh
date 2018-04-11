run-alvisnlp export.plan
check-file export
check-file-sorted export-features.txt
run-alvisnlp import.plan
check-file-sorted import-features.txt
run-alvisnlp import-dkpro.plan
check-file-sorted import-dkpro-features.txt
