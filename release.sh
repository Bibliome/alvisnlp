#!/bin/bash -e




CHANGELOG=CHANGES.md
EDITOR=emacs




function hl {
    echo -e '\033[1;33m'"$@"'\033[0m'
}

function err {
    echo -e '\033[1;31m'"$@"'\033[0m' >&2
    return 1
}

function increment {
    local version="$1"
    local increment="$2"
    read -r major minor fix <<<$(echo "$version" | sed -e 's,\., ,g')
    case "$increment" in
	fix)
	    echo -n $major.$minor.$(($fix + 1))
	    ;;
	minor)
	    echo -n $major.$(($minor + 1)).0
	    ;;
	major)
	    echo -n $(($major + 1)).0.0
	    ;;
	*)
	    err Unknown increment "$increment", expected fix, minor or major
    esac
}


# parameter: increment
increment="$1"

# parameter: force
force="$2"
if [ -n "$force" ]
then
    if [ "$force" != "force" ]
    then
	hl Unknown option "$force"
	exit 1
    fi
fi

# read current version
current=$(git describe --abbrev=0)
hl Current: "$current"
if [ -z "$increment" ]
then
    exit 0
fi

# compute next versions
next=$(increment "$current" "$increment")
devel=$(increment "$next" major)-SNAPSHOT

hl Next: "$next"
hl Devel: "$devel"

# check modified files
if git status --porcelain | sed -e 's,^ ,,' | grep '^[MADRCU]'
then
    if [ -z "$force"]
    then
	hl Commit the above
	exit 1
    else
	hl Going through anyway
    fi
fi

# edit change log file
hl Edit $CHANGELOG
echo >>$CHANGELOG
case "$increment" in
    fix)
	echo "### $next"
	;;
    minor)
	echo "## $next"
	;;
    major)
	echo "# $next"
	;;
esac >>$CHANGELOG
git log --all --decorate --oneline --graph | sed "/$current/q"
$EDITOR $CHANGELOG

# build & install AlvisNLP
hl Build and install AlvisNLP
mvn clean install
./install.sh .test/alvisnlp

# documentation
DOC_VERSION_FILE=docs/_includes/version
hl Update documentation
echo -n "$next" >$DOC_VERSION_FILE
cd docs
./build-reference.sh ../.test/alvisnlp/bin/alvisnlp
cd ..

# poms
POM_FILES="pom.xml alvisnlp-core/pom.xml alvisnlp-bibliome/pom.xml"
hl Update pom.xml files
function update_poms {
    local version="$1"
    for pom in $POM_FILES
    do
	xsltproc --stringparam alvisnlp-version "$version" -o "$pom" update-pom-version.xslt "$pom"
    done
}
update_poms "$next"

# commit
COMMIT_FILES="$POM_FILES $CHANGELOG $DOC_VERSION_FILE docs/reference"
hl Commit changed files
git add $COMMIT_FILES
git commit -m "Updated pom.xml files and documentation for version $next"

# tag
hl Tag commit
git tag -a "$next" -m "Version $next"

# poms for devel
hl Update and commit pom.xml files for development
update_poms "$devel"
git add $POM_FILES
git commit -m "Updated pom.xml files and documentation for after version $next ($devel)"

# push
hl git push
hl git push origin "$next"
