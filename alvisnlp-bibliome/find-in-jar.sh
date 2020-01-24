#!/bin/bash

PAT="$1"

for j in target/lib/*.jar
do
    if jar -tf "$j" | grep -q "$PAT"
    then
	echo "### ""$j"
	jar -tf "$j" | grep --color "$PAT"
	echo
    fi
done
