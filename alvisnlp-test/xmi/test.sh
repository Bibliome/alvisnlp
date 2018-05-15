run-alvisnlp export.plan
check-file export-xmi
check-file export-dkpro
check-file export-features.txt
run-alvisnlp import-xmi.plan
check-file-sorted import-xmi-features.txt
run-alvisnlp import-dkpro.plan
check-file-sorted import-dkpro-features.txt
