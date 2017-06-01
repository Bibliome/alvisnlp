#!/bin/bash

# Copyright 2016 Institut National de la Recherche Agronomique
# 
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
# 
#         http://www.apache.org/licenses/LICENSE-2.0
# 
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.

python $TEES_PRE_EXE --input  $TEES_CORPUS_IN --output $TEES_CORPUS_OUT --omitSteps $OMITSTEPS --debug true && python $TEES_CLASSIFY_EXE --input $TEES_CORPUS_OUT --output $OUTSTREAM  --workdir $WORKDIR --model $MODEL --omitSteps PREPROCESS --debug true
