#!/bin/env pwsh

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

$LIB_FILES = gci alvisnlp-core/target/lib/*.jar, alvisnlp-core/target/*.jar, alvisnlp-bibliome/target/lib/*.jar, alvisnlp-bibliome/target/*.jar

#$LIB_FILES | Format-Wide

if (-NOT $args[0]) {
       Write-Output "Missing install directory path"
       exit
}

if (-NOT (Test-Path $args[0] -pathType Container)) {
       Write-Output "Does not exist or is not a directory: ", $args[0]
       exit
}

$INSTALL_DIR = Resolve-Path $args[0]
$BIN_DIR = Write-Output $INSTALL_DIR/bin
$DOC_DIR = Write-Output $INSTALL_DIR/doc
$LIB_DIR = Write-Output $INSTALL_DIR/lib
$SHARE_DIR = Write-Output $INSTALL_DIR/share

New-Item $BIN_DIR, $DOC_DIR, $LIB_DIR, $SHARE_DIR -ItemType dir -Force

Copy-Item $LIB_FILES -Destination $LIB_DIR

If (Test-Path share/default-param-values.xml) {
       Copy-Item share/default-param-values.xml -Destination $SHARE_DIR
}
Else {
       Write-Output "No default parameter values file (though you should really consider it)"
}

Write-Output @"
#!/bin/env pwsh

`$JVMOPTS = `$args | Where { `$_ -like "-J*" } | ForEach { `$_.Substring(2) }
`$ANLPOPTS = `$args | Where { -NOT (`$_ -like "-J*") }
java `$JVMOPTS -cp "$LIB_DIR/*" fr.inra.maiage.bibliome.alvisnlp.core.app.cli.AlvisNLP `$args
"@ > $BIN_DIR/alvisnlp.ps1
