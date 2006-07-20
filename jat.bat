@echo off

REM The first argument is the config file to use, or use a default if not provided
set ANSR_CONFIG_FILE=cfg\dev.xml
IF NOT "%*"=="" set ANSR_CONFIG_FILE=%*

REM Find the path to java.  First look in JAT_JAVA, then JAVA_HOME, then in PATH
if defined JAT_JAVA if exist "%JAT_JAVA%\bin\java.exe" set _JAVA_CMD="%JAT_JAVA%\bin\java.exe"&& goto have_java 
if defined JAVA_HOME if exist "%JAVA_HOME%\bin\java.exe" set _JAVA_CMD="%JAVA_HOME%\bin\java.exe"&& goto have_java 
for %%d in (%PATH%) do if exist "%%d\java.exe" set _JAVA_CMD="%%d\java.exe"&& goto have_java
echo Cannot find java.exe in JAT_JAVA, JAVA_HOME, or path && goto error
:have_java

REM Determine where JAT classes are.  If not running from JAT root, then JAT_HOME must
REM be defined.
if defined JAT_HOME if exist "%JAT_HOME%\jat.bat" goto have_home
if exist ".\jat.bat" set JAT_HOME=.&& goto have_home 
:have_home

REM Setup the classpath - the JAT source tree and everything in external_jar
set JAT_CLASSPATH=%JAT_HOME%
set _JAT_LIBDIR=%JAT_HOME%\external_jar
REM The next statement is confusing but just combines names of all jars in 
REM \external_jar into %JAT_CLASSPATH%
for /R %_JAT_LIBDIR% %%X in (*.jar) do for /f "tokens=2 delims==" %%Y in ('set JAT_CLASSPATH') DO set JAT_CLASSPATH=%%Y;%%X

REM Now figure out the Java class to execute.  Defaults to CEVSim.
if "%*" == "" set _MAIN_CLASS=jat.sim.CEVSim&& goto have_class
if "%*" == "CEVSim" set _MAIN_CLASS=jat.sim.CEVSim&& goto have_class
if "%*" == "CEVLunarSim" set _MAIN_CLASS=jat.sim.CEVLunarSim&& goto have_class
REM If not one of the preconfigured options, assume a fully qualified class name has been
REM passed in
set _MAIN_CLASS=%*
:have_class

%_JAVA_CMD% -classpath %JAT_CLASSPATH% %_MAIN_CLASS%
if not errorlevel 1 goto done

:error
pause

:done
set _JAVA_CMD=
set _JAT_LIBDIR=
set _MAIN_CLASS=