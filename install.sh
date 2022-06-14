#!/bin/bash

# Copyright 2016-2022 Institut national de recherche pour l'agriculture, l'alimentation et l'environnement (INRAE)
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

function usage() {
    if [ -n "$1" ]
    then
	echo "$1" >&2
    fi
    echo "Usage: $0 [-p DEFAULT_PARAM_VALUES_FILE] [-o DEFAULT_OPTIONS_FILE] [-i] INSTALL_DIRECTORY" >&2
    exit 1
}

while getopts ":p:o:" opt
do
    case "$opt" in
	p)
	    if [ -n "$DEFAULT_PARAM_VALUES" ]
	    then
		usage "Duplicate option -$opt"
	    fi
	    DEFAULT_PARAM_VALUES="$OPTARG"
	    ;;
	o)
	    if [ -n "$DEFAULT_OPTIONS" ]
	    then
		usage "Duplicate option -$opt"
	    fi
	    DEFAULT_OPTIONS="$OPTARG"
	    ;;
	\?)
	    usage "Invalid option: -$OPTARG"
	    ;;
	:)
	    usage "Option -$OPTARG requires an argument"
	    ;;
    esac
done
shift $((OPTIND-1))

if [ -z "$DEFAULT_PARAM_VALUES" ]
then
    if [ -f "share/default-param-values.xml.$HOSTNAME" ]
    then
	DEFAULT_PARAM_VALUES="share/default-param-values.xml.$HOSTNAME"
    fi
fi

if [ -z "$DEFAULT_PARAM_VALUES" ]
then
    if [ -f "share/default-param-values.xml" ]
    then
	DEFAULT_PARAM_VALUES="share/default-param-values.xml"
    fi
fi

if [ -z "$DEFAULT_OPTIONS" ]
then
    if [ -f "share/default-options.txt.$HOSTNAME" ]
    then
	DEFAULT_OPTIONS="share/default-options.txt.$HOSTNAME"
    fi
fi

if [ -z "$DEFAULT_OPTIONS" ]
then
    if [ -f "share/default-options.txt" ]
    then
	DEFAULT_OPTIONS="share/default-options.txt"
    fi
fi

if [ "$#" -ne 1 ]; then
    usage "Missing install directory"
fi

if ! [ -d "$1" ]; then
    usage "$1 does not exist"
fi

LIB_FILES="alvisnlp-core/target/lib/*.jar alvisnlp-core/target/*.jar alvisnlp-bibliome/target/lib/*.jar alvisnlp-bibliome/target/*.jar"

INSTALL_DIR="$(readlink -m $1)"
echo "Install directory: $INSTALL_DIR"
if [ -n "$DEFAULT_PARAM_VALUES" ]
then
    echo "Default parameter values file: $DEFAULT_PARAM_VALUES"
else
    echo "Found no default parameter values file (it is highly recommended)"
fi
if [ -n "$DEFAULT_OPTIONS" ]
then
    echo "Default options file: $DEFAULT_OPTIONS"
else
    echo "Found no default options file (no worries)"
fi

BIN_DIR="$INSTALL_DIR/bin"
DOC_DIR="$INSTALL_DIR/doc"
LIB_DIR="$INSTALL_DIR/lib"
SHARE_DIR="$INSTALL_DIR/share"

mkdir -p "$INSTALL_DIR"
mkdir -p "$BIN_DIR"
mkdir -p "$DOC_DIR"
rm -f -r "$LIB_DIR"
mkdir -p "$LIB_DIR"
mkdir -p "$SHARE_DIR"

cp -f -u -r $LIB_FILES "$LIB_DIR"


function insert_param_value() {
    module="$1"
    param="$2"
    value="$3"

    if [ -n "$DEFAULT_PARAM_VALUES" ]
    then
	echo Inserting default param value: "$module"/"$param"="$value"
	xsltproc --stringparam module "$module" --stringparam param "$param" --stringparam value "$value" share/insert-default-param.xslt "$DEFAULT_PARAM_VALUES" >.default-param-values.xml.tmp
	mv .default-param-values.xml.tmp .default-param-values.xml
	DEFAULT_PARAM_VALUES=.default-param-values.xml
    fi
}

insert_param_value fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.python.PythonScript alvisnlpPythonDirectory "$SHARE_DIR"

if [ -n "$DEFAULT_PARAM_VALUES" ]
then
    cp -f -u "$DEFAULT_PARAM_VALUES" "$SHARE_DIR/default-param-values.xml"
fi

if [ -f "$DEFAULT_OPTIONS" ]
then
    cp -f -u "$DEFAULT_OPTIONS" "$SHARE_DIR/default-options.txt"
fi

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

java $JVMOPTS fr.inra.maiage.bibliome.alvisnlp.core.app.cli.AlvisNLP $OPTS "$@"
EOF

BIN_FILE="$BIN_DIR/alvisnlp"
sed -e "s,__INSTALL_DIR__,$INSTALL_DIR," <<<"$TEMPLATE" >"$BIN_FILE"
chmod +x "$BIN_FILE"

