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

read -r -d '' TEMPLATE <<'EOF'

#!/bin/bash

# qsub options
#$ -S /bin/bash
#$ -V
#$ -cwd

ulimit -c 0

export CLASSPATH="__INSTALL_DIR__/lib/*:$CLASSPATH"

JVMOPTS=
OPTERR=0
while getopts J: o; do
    case "$o" in
	J) JVMOPTS="$JVMOPTS $OPTARG";;
	?) break;;
    esac
done
shift $(($OPTIND-1))

OPTS=""
if [ -f "__INSTALL_DIR__/share/default-param-values.xml" ]
then
    OPTS="$OPTS ""-defaultParamValuesFile __INSTALL_DIR__/share/default-param-values.xml"
fi
if [ -f "__INSTALL_DIR__/share/default-options.txt" ]
then
    OPTS="$OPTS "$(cat __INSTALL_DIR__/share/default-options.txt)
fi

cmd="java $JVMOPTS fr.inra.maiage.bibliome.alvisnlp.core.app.cli.AlvisNLP ""$OPTS ""$@"
#echo $CLASSPATH
#echo $cmd
$cmd
EOF

if [ $# -ne 1 ]; then
    echo "Usage: $0 INSTALL_DIR" >&2
    exit 1
fi

INSTALL_DIR=$1
shift

BINFILE="$INSTALL_DIR/bin/alvisnlp"

sed -e "s,__INSTALL_DIR__,$INSTALL_DIR," <<<"$TEMPLATE" >"$BINFILE"
chmod +x "$BINFILE"

