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

function usage() {
    if [ -n "$1" ]
    then
	echo "$1" >&2
    fi
    echo "Usage: $0 [-p DEFAULT_PARAM_VALUES_FILE] [-o DEFAULT_OPTIONS_FILE] [-i] INSTALL_DIRECTORY" >&2
    exit 1
}

while getopts ":p:o:i" opt
do
    case "$opt" in
	p)
	    if [ -n "$DEFAULT_PARAM_VALUES" ]
	    then
		usage "Duplicate option -$opt"
	    fi
	    if [ -n "$INTERACTIVE" ]
	    then
		usage "Conflicting options -$opt / -i"
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
	i)
	    if [ -n "$INTERACTIVE" ]
	    then
		usage "Duplicate option -$opt"
	    fi
	    if [ -n "$DEFAULT_PARAM_VALUES" ]
	    then
		usage "Conflicting options -$opt / -p"
	    fi
	    INTERACTIVE=yes
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

function search_executable() {
    name="$1"
    exclude="$2"
    result=$(which "$name")
    if [ -n "$result" ]
    then
	echo "$result"
	return
    fi
    if [ -n "$exclude" ]
    then
	result=$(locate --regex -b ^$name$ | grep -P -v "$exclude" | xargs -n 1 -I_FILE_ find '_FILE_' -type f -executable -maxdepth 0 2>/dev/null | awk '{ print length, $0 }' | sort -n | cut -d ' ' -f 2- | head -n 1)
    else
	result=$(locate --regex -b ^$name$ | xargs -n 1 -I_FILE_ find '_FILE_' -type f -executable -maxdepth 0 2>/dev/null | head -n 1)
    fi
    if [ -n "$result" ]
    then
	echo "$result"
	return
    fi
    echo ''
}

function search_directory() {
    name="$1"
    exclude="$2"
    if [ -n "$exclude" ]
    then
	locate --regex -b ^"$name"$ | grep -P -v "$exclude" | xargs -n 1 -I_FILE_ find '_FILE_' -type d -executable -maxdepth 0 2>/dev/null | head -n 1
    else
	locate --regex -b ^"$name"$ | xargs -n 1 -I_FILE_ find '_FILE_' -type d -executable -maxdepth 0 2>/dev/null | awk '{ print length, $0 }' | sort -n | cut -d ' ' -f 2- | head -n 1
    fi
}

function start_module() {
    full="$1"
    echo '  <module class="'"$full"'">' >>$DEFAULT_PARAM_VALUES
}

function end_module() {
    echo '  </module>' >>$DEFAULT_PARAM_VALUES
}

function param() {
    name="$1"
    value="$2"
    if [ -n "$value" ]
    then
	echo '    <'"$name"'>'"$value"'</'"$name"'>' >>$DEFAULT_PARAM_VALUES
    fi
}

if [ -n "$INTERACTIVE" ]
then
    echo Interactive mode for third-party tools
    DEFAULT_PARAM_VALUES=".default-param-values.xml"
    echo '<default-param-values>' >$DEFAULT_PARAM_VALUES

    echo
    echo Defaults for EnjuParser
    default=$(search_executable enju)
    read -e -p "Location of enju parser: " -i "$default" enju
    if [ -n "$enju" ]
    then
	enju=$(readlink -m "$enju")
	start_module fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.enju.EnjuParser
	param enjuExecutable "$enju"
	end_module
    fi

    echo
    echo Defaults for CCGParser and CCGPosTagger
    ccgdir=$(search_directory CCG)
    if [ -n "$ccgdir" ]
    then
	parser="$ccgdir/bin/parser"
	model="$ccgdir/models/parser"
	super="$ccgdir/models/super"
	pos="$ccgdir/bin/pos" 
	posmod="$ccgdir/models/pos"
    fi
    read -e -p "Location of CCG parser executable: " -i "$parser" parser
    if [ -n "$parser" ]
    then
	read -e -p "Location of CCG parser model: " -i "$model" model
	read -e -p "Location of CCG supers model: " -i "$super" super
	start_module fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.ccg.CCGParser
	param executable "$parser"
	param parserModel "$model"
	param superModel "$super"
	end_module
    fi
    read -e -p "Location of CCG POS-tagger executable: " -i "$pos" pos
    if [ -n "$pos" ]
    then
	read -e -p "Location of CCG POS model: " -i "$posmod" posmod
	start_module fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.ccg.CCGPosTagger
	param executable "$pos"
	param model "$posmod"
	end_module
    fi

    echo
    echo Defaults for GeniaTagger
    geniadir=$(search_directory geniatagger-3.0..)
    read -e -p "Location of Genia tagger directory: " -i "$geniadir" geniadir
    if [ -n "$geniadir" ]
    then
	start_module fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.geniatagger.GeniaTagger
	param geniaDir "$geniadir"
	end_module
    fi

    echo
    echo Defaults for StanfordNER
    ser=$(locate .crf.ser.gz | head -n 1)
    read -e -p "Location of Stanford NER classifier: " -i "$ser" ser
    if [ -n "$ser" ]
    then
	start_module fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.stanford.StanfordNER
	param classifierFile "$ser"
	end_module
    fi

    echo
    echo Defaults for TreeTagger
    tt=$(search_executable tree-tagger)
    read -e -p "Location of Tree tagger executable: " -i "$tt" tt
    if [ -n "$tt" ]
    then
	par=$(locate -b english*bin)
	read -e -p "Location of default PAR file: " -i "$par" par
	start_module fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.treetagger.TreeTagger
	param treeTaggerExecutable "$tt"
	param parFile "$par"
	if [[ "$par" == *utf* ]]
	then
	    param outputCharset UTF-8
	fi
	end_module
    fi

    echo
    echo Defaults for YateaExtractor and TomapTrain
    yatea=$(search_executable yatea)
    read -e -p "Location of YaTeA executable: " -i "$yatea" yatea
    if [ -n "$yatea" ]
    then
	read -e -p "Location of YaTeA .rc file: " -i "res://yatea.rc" rc
	resdir=$(search_directory YaTeA)
	read -e -p "Location of YaTeA config directory: " -i "$resdir/config" config
	read -e -p "Location of YaTeA locale directory: " -i "$resdir/locale" locale
	start_module fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.yatea.YateaExtractor
	param yateaExecutable "$yatea"
	param rcFile "$rc"
	param configDir "$config"
	param localeDir "$locale"
	end_module
	start_module fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.tomap.TomapTrain
	param yateaExecutable "$yatea"
	param rcFile "$rc"
	param configDir "$config"
	param localeDir "$locale"
	end_module
    fi

    echo
    echo Defaults for TEESTrain and TEESClassify
    tees=$(search_directory .*TEES.*)
    read -e -p "Location of TEES directory: " -i "$tees" tees
    if [ -n "$tees" ]
    then
	python2=$(search_executable python2)
	read -e -p "Location of Python 2 executable: " -i "$python2" python2
	start_module fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.tees.TEESTrain
	param python2Executable "$python2"
	param teesHome "$tees"
	end_module
	start_module fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.tees.TEESClassify
	param python2Executable "$python2"
	param teesHome "$tees"
	end_module
    fi

    echo
    echo Defaults for Chemspot
    chm=$(search_directory chemspot-2.0)
    read -e -p "Location of Chemspot 2 directory: " -i "$chm" chm
    if [ -n "$chm" ]
    then
	start_module fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.chemspot.Chemspot
	param chemspotDir "$chm"
	end_module
    fi

    echo
    echo Defaults for Word2Vec, ContesTrain and ContesPredict
    contes=$(search_directory CONTES)
    read -e -p "Location of CONTES directory: " -i "$contes" contes
    if [ -n "$contes" ]
    then
	python3=$(search_executable python3)
	read -e -p "Location of Python 3 executable: " -i "$python3" python3
	read -e -p "Number of threads to use in Word2Vec: " -i "2" workers
	start_module fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.contes.Word2Vec
	param python3Executable "$python3"
	param contesDir "$contes"
	param workers "$workers"
	end_module
	start_module fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.contes.ContesTrain
	param python3Executable "$python3"
	param contesDir "$contes"
	end_module
	start_module fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.contes.ContesPredict
	param python3Executable "$python3"
	param contesDir "$contes"
	end_module
    fi

    echo
    echo Defaults for Species
    species=$(search_directory species_tagger)
    read -e -p "Location of Species tagger directory: " -i "$species" species
    if [ -n "$species" ]
    then
	start_module fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.Species
	param speciesDir "$species"
	end_module
    fi

    echo
    echo Defaults for Ab3P
    ab=$(search_directory Ab3P)
    read -e -p "Location of AB3P directory: " -i "$ab" ab
    if [ -n "$ab" ]
    then
	start_module fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.ab3p.Ab3P
	param installDir "$ab"
	end_module
    fi

    echo
    echo Defaults for WapitiTrain WapitiLabel
    wap=$(search_executable wapiti)
    read -e -p "Location of Wapiti executable: " -i "$wap" wap
    if [ -n "$wap" ]
    then
	start_module fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.wapiti.WapitiTrain
	param wapitiExecutable "$wap"
	end_module
	start_module fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.wapiti.WapitiLabel
	param wapitiExecutable "$wap"
	end_module
    fi

    echo '</default-param-values>' >>$DEFAULT_PARAM_VALUES

    echo
    echo Done at last!
    echo
fi

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
mkdir -p "$LIB_DIR"
mkdir -p "$SHARE_DIR"

cp -f -u -r $LIB_FILES "$LIB_DIR"

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

cmd="java $JVMOPTS fr.inra.maiage.bibliome.alvisnlp.core.app.cli.AlvisNLP ""$OPTS ""$@"
#echo $CLASSPATH
#echo $cmd
$cmd
EOF

BIN_FILE="$BIN_DIR/alvisnlp"
sed -e "s,__INSTALL_DIR__,$INSTALL_DIR," <<<"$TEMPLATE" >"$BIN_FILE"
chmod +x "$BIN_FILE"

