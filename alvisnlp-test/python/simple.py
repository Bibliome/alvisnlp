import alvisnlp
import json
import sys


j_in = json.load(sys.stdin)
corpus = alvisnlp.Corpus.from_json(j_in)
j_out = corpus.to_json()
json.dump(j_out, sys.stdout)
