@echo off
chcp 65001 >nul
cd /d "%~dp0"
title Mediciones - Listas, Pilas y Colas

rem --- Buscar Java (17 o superior) ---
set "JAVA="
for /d %%d in ("%USERPROFILE%\.vscode\extensions\redhat.java-*") do for /d %%j in ("%%d\jre\*") do if exist "%%j\bin\java.exe" set "JAVA=%%j\bin\java.exe"
if not defined JAVA if defined JAVA_HOME if exist "%JAVA_HOME%\bin\java.exe" set "JAVA=%JAVA_HOME%\bin\java.exe"
if not defined JAVA for /f "delims=" %%i in ('where java 2^>nul') do if not defined JAVA set "JAVA=%%i"
if not defined JAVA (
  echo No se encontro Java. Instala un JDK 17 o superior y vuelve a intentarlo.
  pause
  exit /b 1
)
echo Usando Java: "%JAVA%"
"%JAVA%" -version
echo.
echo Midiendo con n = 10, 100, 10 mil, 1 millon y 10 millones.
echo Tarda unos minutos. No cierres esta ventana.
echo.

"%JAVA%" -Xmx3g -XX:+UseParallelGC -Dstdout.encoding=UTF-8 -cp bin Main --out=results
if errorlevel 1 goto error

echo.
echo LISTO. Los resultados quedaron en la carpeta results.
echo Avisale a Claude en el chat para generar las graficas y el informe.
pause
exit /b 0

:error
echo.
echo La medicion fallo (mira el mensaje de arriba). Copia el error y pegalo en el chat.
pause
exit /b 1
