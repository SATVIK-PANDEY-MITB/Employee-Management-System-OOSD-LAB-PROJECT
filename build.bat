@echo off
REM Build and run the Employee Management System on Windows
setlocal enabledelayedexpansion

echo ==========================================
echo Employee Management System - Build Script
echo ==========================================

REM Check if Maven is installed
where mvn >nul 2>nul
if %ERRORLEVEL% NEQ 0 (
    echo Maven not found. Using local JavaFX jars to compile and run.
    set FX_BASE=%USERPROFILE%\.m2\repository\org\openjfx\javafx-base\19.0.2\javafx-base-19.0.2-win.jar
    set FX_CONTROLS=%USERPROFILE%\.m2\repository\org\openjfx\javafx-controls\19.0.2\javafx-controls-19.0.2-win.jar
    set FX_FXML=%USERPROFILE%\.m2\repository\org\openjfx\javafx-fxml\19.0.2\javafx-fxml-19.0.2-win.jar
    set FX_GRAPHICS=%USERPROFILE%\.m2\repository\org\openjfx\javafx-graphics\19.0.2\javafx-graphics-19.0.2-win.jar
    set H2_JAR=%USERPROFILE%\.m2\repository\com\h2database\h2\2.2.224\h2-2.2.224.jar
    set PDFBOX_JAR=%USERPROFILE%\.m2\repository\org\apache\pdfbox\pdfbox\2.0.31\pdfbox-2.0.31.jar
    set FONTBOX_JAR=%USERPROFILE%\.m2\repository\org\apache\pdfbox\fontbox\2.0.31\fontbox-2.0.31.jar
    set PDFBOX_IO_JAR=%USERPROFILE%\.m2\repository\org\apache\pdfbox\pdfbox-io\2.0.31\pdfbox-io-2.0.31.jar
    set COMMONS_LOGGING_JAR=%USERPROFILE%\.m2\repository\commons-logging\commons-logging\1.2\commons-logging-1.2.jar

    if not exist target\classes mkdir target\classes

    set FX_CP=!FX_BASE!;!FX_CONTROLS!;!FX_FXML!;!FX_GRAPHICS!;!H2_JAR!;!PDFBOX_JAR!;!FONTBOX_JAR!;!PDFBOX_IO_JAR!;!COMMONS_LOGGING_JAR!
    if exist target\sources.txt del target\sources.txt
    for /r src\main\java %%f in (*.java) do echo %%f>>target\sources.txt

    javac --module-path "!FX_BASE!;!FX_CONTROLS!;!FX_FXML!;!FX_GRAPHICS!" --add-modules javafx.controls,javafx.fxml -cp "!FX_CP!" -d target\classes @target\sources.txt
    if errorlevel 1 (
        echo ❌ Compilation failed.
        pause
        exit /b 1
    )

    java --module-path "!FX_BASE!;!FX_CONTROLS!;!FX_FXML!;!FX_GRAPHICS!" --add-modules javafx.controls,javafx.fxml -cp "target\classes;!FX_CP!" com.ems.EmployeeManagementApp
    exit /b 0
)

REM Check if Java is installed
where java >nul 2>nul
if %ERRORLEVEL% NEQ 0 (
    echo ❌ Java is not installed. Please install Java 11 or higher.
    echo    Download from: https://www.oracle.com/java/technologies/downloads/
    echo    Add Java bin folder to Windows PATH environment variable
    pause
    exit /b 1
)

REM Get Java version
for /f "tokens=3" %%g in ('java -version 2^>^&1 ^| findstr /i version') do (
    set JAVA_VERSION=%%g
)
echo ✓ Java version detected: %JAVA_VERSION%
echo.

REM Clean and build
echo Step 1: Cleaning previous builds...
call mvn clean

echo.
echo Step 2: Installing dependencies...
call mvn install

echo.
echo Step 3: Running the application...
call mvn javafx:run

echo.
echo ✓ Application completed successfully!
