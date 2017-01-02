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

if [ "$#" -ne 1 ] || ! [ -d "$1" ]; then
  echo "Usage: $0 INSTALL_DIRECTORY" >&2
  exit 1
fi

if ! [ -d "$1" ]; then
  echo "$1 does not exist" >&2
  exit 1
fi

if ! [ -f "$1/bin/alvisnlp" ]; then
  echo "AlvisNLP/ML does not seem to be installed in $1" >&2
  exit 1
fi

LIB_FILES="target/*.jar target/lib/*.jar"

INSTALL_DIR="$(readlink -m $1)"
BIN_DIR="$INSTALL_DIR/bin"
DOC_DIR="$INSTALL_DIR/doc"
LIB_DIR="$INSTALL_DIR/lib"
SHARE_DIR="$INSTALL_DIR/share"

if [ "$INSTALL_DIR" != "$PWD" ];
then
    mkdir -p "$LIB_DIR"
    cp -n -r $LIB_FILES "$LIB_DIR"
fi
