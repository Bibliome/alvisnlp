import alvisnlp.data
import alvisnlp.serialization
import json
import sys


j_in = json.load(sys.stdin)
deserializer = alvisnlp.serialization.JsonDeserializer()
corpus = deserializer.deserialize_corpus(j_in)
serializer = alvisnlp.serialization.JsonSerializer()
j_out = serializer.serialize_corpus(corpus)
json.dump(j_out, sys.stdout)
