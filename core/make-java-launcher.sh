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

export CLASSPATH="__LIBSPATH__/*:$CLASSPATH"

JVMOPTS=
OPTERR=0
while getopts J: o; do
    case "$o" in
	J) JVMOPTS="$JVMOPTS $OPTARG";;
	?) break;;
    esac
done
shift $(($OPTIND-1))

cmd="java $JVMOPTS __MAINCLASS__ ""$@"
#echo $CLASSPATH
#echo $cmd
$cmd
EOF

if [ $# -ne 3 ]; then
    echo "Usage: $0 LIBSPATH BINFILE MAINCLASS" >&2
    exit 1
fi

LIBSPATH=$1
BINFILE=$2
MAINCLASS=$3

sed -e "s,__LIBSPATH__,$LIBSPATH," -e "s,__MAINCLASS__,$MAINCLASS," <<<"$TEMPLATE" >"$BINFILE"
chmod +x "$BINFILE"

