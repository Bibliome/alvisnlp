import alvisnlp
import sys


corpus = alvisnlp.Corpus.parse_json(sys.stdin)
corpus.write_json(sys.stdout)
