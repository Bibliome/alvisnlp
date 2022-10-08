#!/bin/bash

xsltproc share/no-name.xslt "$1" >.doc.xml
mv -f .doc.xml "$1"

