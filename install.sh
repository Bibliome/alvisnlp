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

if [ "$#" -ne 1 ]; then
  echo "Usage: $0 INSTALL_DIRECTORY" >&2
  exit 1
fi

if ! [ -d "$1" ]; then
    echo "$1 does not exist"
    exit 1
fi

LIB_FILES="core/target/lib/*.jar core/target/*.jar bibliome/target/lib/*.jar bibliome/target/*.jar"

INSTALL_DIR="$(readlink -m $1)"
BIN_DIR="$INSTALL_DIR/bin"
DOC_DIR="$INSTALL_DIR/doc"
LIB_DIR="$INSTALL_DIR/lib"
SHARE_DIR="$INSTALL_DIR/share"

mkdir -p "$INSTALL_DIR"
mkdir -p "$BIN_DIR"
mkdir -p "$DOC_DIR"
mkdir -p "$LIB_DIR"
mkdir -p "$SHARE_DIR"

cp -f -u -r $LIB_FILES "$LIB_DIR"

./make-java-launcher.sh "$LIB_DIR" "$BIN_DIR"/alvisnlp alvisnlp.app.cli.AlvisNLP
./make-java-launcher.sh "$LIB_DIR" "$BIN_DIR"/alvisnlp-doc alvisnlp.app.cli.AlvisNLPDocumentation
