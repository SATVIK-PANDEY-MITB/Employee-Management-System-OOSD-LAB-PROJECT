#!/bin/bash
# Build and run the Employee Management System

echo "=========================================="
echo "Employee Management System - Build Script"
echo "=========================================="

# Check if Maven is installed
if ! command -v mvn &> /dev/null; then
    echo "❌ Maven is not installed. Please install Maven first."
    echo "   Download from: https://maven.apache.org/download.cgi"
    exit 1
fi

# Check if Java is installed
if ! command -v java &> /dev/null; then
    echo "❌ Java is not installed. Please install Java 11 or higher."
    echo "   Download from: https://www.oracle.com/java/technologies/downloads/"
    exit 1
fi

# Check Java version
JAVA_VERSION=$(java -version 2>&1 | awk -F . '/version/ {print $2}')
if [ "$JAVA_VERSION" -lt 11 ]; then
    echo "❌ Java version must be 11 or higher. Current: $JAVA_VERSION"
    exit 1
fi

echo "✓ Java and Maven are properly installed"
echo ""

# Clean and build
echo "Step 1: Cleaning previous builds..."
mvn clean

echo ""
echo "Step 2: Installing dependencies..."
mvn install

echo ""
echo "Step 3: Running the application..."
mvn javafx:run

echo ""
echo "✓ Application completed successfully!"
