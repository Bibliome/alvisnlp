#!/bin/bash

ALVISNLP_SRC=$(readlink -f .)
NO_COMPILE=
WORKING_DIR=$(readlink -f _regression)
TESTS_DIR=

function log() {
    echo -e "$@" >&2
}

function warn() {
    log "\e[93m""$@" "\e[0m"
}

function emph() {
    log "\e[97m""$@" "\e[0m"
}

function ok() {
    log "\e[92m""$@" "\e[0m"
}

function fail() {
    log "\e[91m""$@" "\e[0m"
}

function usage() {
    cat <<EOF

Usage:

  $0 [OPTIONS] TESTSDIR

Synopsis:

  Perform ALvisNLP regression test runs. TESTSDIR is the base directory
containing tests.
  This script compiles the provided sources of AlvisNLP, then installs it in
the working directory. Then tests are searched in subdirectories and each test
is run.

Options:

  -h, --help                print help
  -a, --alvisnlp-sources    AlvisNLP sources directory (default: $ALVISNLP_SRC)
  -n, --no-compile          do not compile AlvisNLP
  -w, --working-directory   base working directory for test runs
                            (default: $WORKING_DIR)

Tests:

  The TESTSDIR directory must contain one sub-directory for each test. Each test
directory must contain a shell script file named \`test.sh'. This script is run
to perform the test with the \`errexit' option.
  This script exports two commodity functions:

    run-alvisnlp    runs AlvisNLP with the specified function. The input files
                    including the plan file are set to be searched in the test
                    directory, as well as the \`share' subdirectory of TESTSDIR.
                    The log file and the output files are set in this script
                    working directory.
                    Example: run-alvisnlp test.plan

    check-file      checks that a generated file is identical to a reference
                    file. The generated file must have been produced by a
                    AlvisNLP run. The reference file must be present in the test
                    directory. Both the generated and reference files must have
                    the same name.

EOF
}


while [ "$#" -ge 1 ]
do
    key="$1"
    shift
    case $key in
	-n|--no-compile)
	    NO_COMPILE="no-compile"
	    ;;
	-a|--alvisnlp-sources)
	    ALVISNLP_SRC=$(readlink -f "$1")
	    shift
	    ;;
	-w|--working-directory)
	    WORKING_DIRECTORY=$(readlink -f "$1")
	    shift
	    ;;
	-h|-?|--help)
	    usage
	    exit 0
	    ;;
	*)
	    export TESTS_DIR=$(readlink -f "$key")
	    ;;
    esac
done

if [ -z "$TESTS_DIR" ]
then
    usage
    exit 1
fi


mkdir -p "$WORKING_DIR"
log

if [ -z "$NO_COMPILE" ]
then
    log Compiling AlvisNLP from source: "$ALVISNLP_SRC"
    log Output redirected to: "$WORKING_DIR"/maven.out
    if (cd "$ALVISNLP_SRC" ; mvn clean install) &>"$WORKING_DIR"/maven.out;
    then
	ok Done
    else
	fail Compilation failed
	exit 1
    fi
else
    warn Skipping compilation, it is already done, right?
fi
log




export INSTALL_DIR="$WORKING_DIR"/alvisnlp
mkdir -p "$INSTALL_DIR"
log Installing AlvisNLP to: "$INSTALL_DIR"
log Output redirected to: "$WORKING_DIR"/install.out
if (cd "$ALVISNLP_SRC" ; ./install.sh "$INSTALL_DIR") &>"$WORKING_DIR"/install.out;
then
    ok Done
else
    fail Install failed
    exit 1
fi
log


function run-alvisnlp() {
    "$INSTALL_DIR"/bin/alvisnlp -verbose -log "$TEST_WD"/alvisnlp.log -inputDir "$TEST_DIR" -inputDir "$TESTS_DIR"/resources
}

function check-file() {
    file="$1"
    diff -q "$TEST_DIR"/"$file" "$TEST_WD"/"$file"
}

export -f run-alvisnlp
export -f check-file

TESTS=
FAILED=
for t in $(find "$TESTS_DIR" -type f -name test.sh);
do
    export TEST_DIR=$(dirname "$t")
    TEST_NAME=$(echo "$TEST_DIR" | sed -e "s,$TESTS_DIR/,,")
    TESTS="$TESTS $TEST_NAME"
    export TEST_WD="$WORKING_DIR"/"$TEST_NAME"
    mkdir -p "$TEST_WD"
    log Test: "$t"
    log Output redirected to: "$TEST_WD"/test.out
    if (cd "$TEST_WD" ; /bin/bash -o errexit "$t") &>"$TEST_WD"/test.out
    then
	ok Passed
    else
	fail Failed
	FAILED="$FAILED $TEST_NAME"
    fi
    log
done

if [ -z "$TESTS" ]
then
    fail Found no tests
    exit 1
fi

if [ -z "$FAIL" ]
then
    ok Congratulations: all tests passed
else
    fail The following tests failed: "$FAILED"
fi
