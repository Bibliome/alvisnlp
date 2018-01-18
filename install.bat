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
