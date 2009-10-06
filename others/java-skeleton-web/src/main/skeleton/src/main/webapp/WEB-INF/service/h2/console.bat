@echo off

set ERROR_CODE=0

@setlocal enabledelayedexpansion

if not "%JAVA_HOME%" == "" goto OkJHome

echo.
echo ERROR: JAVA_HOME not found in your environment.
echo Please set the JAVA_HOME variable in your environment to match the
echo location of your Java installation
echo.
goto error

:OkJHome
if exist "%JAVA_HOME%\bin\java.exe" goto init

echo.
echo ERROR: JAVA_HOME is set to an invalid directory.
echo JAVA_HOME = %JAVA_HOME%
echo Please set the JAVA_HOME variable in your environment to match the
echo location of your Java installation
echo.
goto error

:init

set BATCH_HOME=%~dp0
set LOCALCLASSPATH_HOME=%BATCH_HOME%..\..\

pushd %BATCH_HOME%..\..\..
set WEBAPP=%CD%
popd

:setBatchHomeDone

set JAVA_EXE="%JAVA_HOME%\bin\java.exe"

set LOCALCLASSPATH=
for %%i in (%LOCALCLASSPATH_HOME%lib\h2*.jar) do set LOCALCLASSPATH=!LOCALCLASSPATH!;%%i

%JAVA_EXE% -classpath "%LOCALCLASSPATH%" org.h2.tools.Console %*
if ERRORLEVEL 1 goto error
goto end

:error
set ERROR_CODE=1

:end

@endlocal & set ERROR_CODE=%ERROR_CODE%

exit /B %ERROR_CODE%
