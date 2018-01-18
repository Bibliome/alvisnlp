:: Copyright 2018 Institut National de la Recherche Agronomique
:: 
:: Licensed under the Apache License, Version 2.0 (the "License");
:: you may not use this file except in compliance with the License.
:: You may obtain a copy of the License at
:: 
::         http://www.apache.org/licenses/LICENSE-2.0
:: 
:: Unless required by applicable law or agreed to in writing, software
:: distributed under the License is distributed on an "AS IS" BASIS,
:: WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
:: See the License for the specific language governing permissions and
:: limitations under the License.

@echo off

if "%1"=="" (
    echo Install directory is mandatory
    exit /b
)

if not exist %1\nul (
    echo %1 does not exist or is not a directory
    exit /b
)

pushd %1
set INSTALL_DIR=%CD%
popd

mkdir %INSTALL_DIR%
mkdir %INSTALL_DIR%\bin
mkdir %INSTALL_DIR%\doc
mkdir %INSTALL_DIR%\lib
mkdir %INSTALL_DIR%\share

copy alvisnlp-core\target\lib\*.jar %INSTALL_DIR%\lib\
copy alvisnlp-core\target\*.jar %INSTALL_DIR%\lib\
copy alvisnlp-bibliome\target\lib\*.jar %INSTALL_DIR%\lib\
copy alvisnlp-bibliome\target\*.jar %INSTALL_DIR%\lib\

> %INSTALL_DIR%\bin\alvisnlp.bat (
echo @echo off
echo set JVMOPTS=
echo set ALVISOPTS=
echo :NextArg
echo if "%%1"=="" goto ArgsEnd
echo set arg=%%1
echo shift
echo if "%%arg:~0,2%"=="-J" (
echo.    set JVMOPTS=%%JVMOPTS%% %%arg:~2%%
echo.    goto NextArg
echo. ^)
echo set ALVISOPTS=%%ALVISOPTS%% %%arg%%
echo goto NextArg
echo :ArgsEnd
echo.
echo java %%JVMOPTS%% -cp "%INSTALL_DIR%\lib\*" fr.inra.maiage.bibliome.alvisnlp.core.app.cli.AlvisNLP %%ALVISOPTS%%
)
