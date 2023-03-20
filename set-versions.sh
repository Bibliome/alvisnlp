#!/usr/bin/env bash


ALVISNLP_VERSION=$1
BIBLIOME_UTILS_VERSION=$2

POM_FILES="pom.xml alvisnlp-core/pom.xml alvisnlp-bibliome/pom.xml"

for pomfile in $POM_FILES
do
    if xsltproc --stringparam alvisnlp-version $ALVISNLP_VERSION --stringparam bibliome-utils-version $BIBLIOME_UTILS_VERSION set-versions.xslt $pomfile >.pom.xml
    then
	mv .pom.xml $pomfile
    fi
done

echo $ALVISNLP_VERSION >docs/_includes/version
