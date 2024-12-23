#!/bin/bash

ALVISNLP=$1
ALVISNLPDOC2MD=alvisnlp-doc2md.xslt

function build() {
    $ALVISNLP $@ >.module-doc.xml
    xsltproc $ALVISNLPDOC2MD .module-doc.xml
    xsltproc ../share/check-doc.xslt .module-doc.xml
    rm -f .module-doc.xml
}

echo Creating reference directory tree
mkdir -p reference/module
mkdir -p reference/converter
mkdir -p reference/library

echo Clearing existing pages
rm -f reference/Converter-list.md
rm -f reference/Library-list.md
rm -f reference/Module-list.md
rm -f reference/converter/*.md
rm -f reference/library/*.md
rm -f reference/module/*.md

echo Building module list
build -supportedModulesXML >reference/Module-list.md

echo Building converter list
build -supportedConvertersXML >reference/Converter-list.md

echo Building library list
build -supportedLibrariesXML >reference/Library-list.md

for full in $($ALVISNLP -supportedModules);
do
    short=$(grep -o '[^\.]*$' <<<$full)
    echo Building page for module $short
    build -moduleDocXML $full >reference/module/$full.md
    cp reference/module/$full.md reference/module/$short.md
done

for full in $($ALVISNLP -supportedConverters);
do
    short=$(grep -o '[^\.]*$' <<<$full)
    echo Building page for converter $short
    build -converterDocXML $full >reference/converter/$full.md
    cp reference/converter/$full.md reference/converter/$short.md
done

for full in $($ALVISNLP -supportedLibraries);
do
    echo Building page for library $full
    build -libraryDocXML $full >reference/library/$full.md
done
