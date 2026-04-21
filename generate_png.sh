#!/bin/bash

# Shell script to generate PNG files from PlantUML diagrams
# This script requires Java and PlantUML jar file

echo "🖼️  PlantUML PNG Generator"
echo "=========================="

# Check if Java is installed
if ! command -v java &> /dev/null; then
    echo "❌ Error: Java is not installed or not in PATH"
    echo "   Please install Java 8 or higher"
    echo "   macOS: brew install openjdk"
    echo "   Ubuntu/Debian: sudo apt-get install default-jdk"
    echo "   Windows: Download from https://adoptium.net/"
    exit 1
fi

echo "✅ Java found: $(java -version 2>&1 | head -n 1)"

# Find PlantUML jar file
PLANTUML_JAR=""

# Check common locations
locations=(
    "./plantuml.jar"
    "../plantuml.jar"
    "./lib/plantuml.jar"
    "/usr/local/bin/plantuml.jar"
    "/usr/share/plantuml/plantuml.jar"
    "$HOME/plantuml.jar"
    "$HOME/Downloads/plantuml.jar"
    "/opt/plantuml/plantuml.jar"
)

for location in "${locations[@]}"; do
    if [ -f "$location" ]; then
        PLANTUML_JAR="$location"
        break
    fi
done

# If not found, download it
if [ -z "$PLANTUML_JAR" ]; then
    echo "📥 PlantUML jar not found. Downloading..."
    PLANTUML_JAR="./plantuml.jar"
    curl -L -o "$PLANTUML_JAR" "https://github.com/plantuml/plantuml/releases/download/v1.2023.13/plantuml-1.2023.13.jar"
    
    if [ ! -f "$PLANTUML_JAR" ]; then
        echo "❌ Failed to download PlantUML jar"
        exit 1
    fi
    echo "✅ Downloaded PlantUML to $PLANTUML_JAR"
else
    echo "✅ Found PlantUML jar: $PLANTUML_JAR"
fi

# Create output directory
mkdir -p diagrams

# Find all .puml files
echo ""
echo "🔍 Looking for PlantUML files..."
puml_files=(diagrams/*.puml)

if [ ${#puml_files[@]} -eq 0 ] || [ ! -f "${puml_files[0]}" ]; then
    echo "⚠️  No .puml files found in diagrams/ directory"
    echo "   Run 'python3 generate_diagrams.py' first to generate the .puml files"
    exit 1
fi

echo "📁 Found ${#puml_files[@]} PlantUML files"
echo ""

# Generate PNG for each file
success_count=0
for puml_file in "${puml_files[@]}"; do
    if [ -f "$puml_file" ]; then
        filename=$(basename "$puml_file")
        png_file="${puml_file%.puml}.png"
        
        echo "🎨 Processing: $filename"
        
        # Generate PNG using PlantUML
        java -jar "$PLANTUML_JAR" -tpng "$puml_file" -o diagrams/
        
        if [ -f "$png_file" ]; then
            size=$(du -h "$png_file" | cut -f1)
            echo "   ✅ Success: $png_file ($size)"
            ((success_count++))
        else
            echo "   ❌ Failed to generate PNG"
        fi
    fi
done

echo ""
echo "=========================="
echo "📊 Results: $success_count/${#puml_files[@]} PNG files generated"
echo "=========================="

if [ $success_count -eq ${#puml_files[@]} ]; then
    echo "🎉 All PNG files generated successfully!"
    echo ""
    echo "Generated files:"
    ls -lh diagrams/*.png
else
    echo "⚠️  Some PNG files could not be generated"
    echo "   Check the error messages above"
fi
