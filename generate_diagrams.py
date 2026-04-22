#!/usr/bin/env python3
"""
Auto-generated diagram generator for Student Planner project
Creates class diagrams and MVC architecture diagrams in PlantUML format
"""

import os
import re
import zlib
import base64
import urllib.request
import ssl
from pathlib import Path
from datetime import datetime

# Create SSL context that doesn't verify certificates (for macOS Python issues)
def get_ssl_context():
    """Get SSL context that works on macOS with Python"""
    context = ssl.create_default_context()
    context.check_hostname = False
    context.verify_mode = ssl.CERT_NONE
    return context

# Configuration
SRC_DIR = Path('./src/main/java')
OUTPUT_DIR = Path('./diagrams')

# PlantUML server URL (using official PlantUML server)
PLANTUML_SERVER = 'https://www.plantuml.com/plantuml/png'


def ensure_output_dir():
    """Ensure output directory exists"""
    OUTPUT_DIR.mkdir(exist_ok=True)


def encode_plantuml(text):
    """
    Encode PlantUML text for the PlantUML server.
    Uses the deflate + specific PlantUML encoding algorithm.
    """
    # Compress using zlib (deflate) with raw output (no header/footer)
    compressor = zlib.compressobj(9, zlib.DEFLATED, -15)
    compressed = compressor.compress(text.encode('utf-8'))
    compressed += compressor.flush()
    
    # PlantUML specific encoding
    return plantuml_encode(compressed)


def plantuml_encode(data):
    """
    PlantUML specific encoding using a custom base64-like character set.
    Based on the PlantUML encoding specification.
    """
    # PlantUML uses this character set (different from standard base64)
    plantuml_chars = '0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz-_' 
    
    # Convert bytes to the custom encoding
    result = ''
    i = 0
    
    while i < len(data):
        # Process 3 bytes at a time
        b1 = data[i]
        b2 = data[i + 1] if i + 1 < len(data) else 0
        b3 = data[i + 2] if i + 2 < len(data) else 0
        
        # Encode 3 bytes into 4 characters
        c1 = b1 >> 2
        c2 = ((b1 & 0x3) << 4) | (b2 >> 4)
        c3 = ((b2 & 0xF) << 2) | (b3 >> 6)
        c4 = b3 & 0x3F
        
        result += plantuml_chars[c1]
        result += plantuml_chars[c2]
        if i + 1 < len(data):
            result += plantuml_chars[c3]
        if i + 2 < len(data):
            result += plantuml_chars[c4]
        
        i += 3
    
    return result


def generate_png_from_puml(puml_file_path, output_png_path, verbose=False):
    """
    Generate PNG from PlantUML file using multiple methods.
    Tries: 1) PlantUML server encoding, 2) Text API, 3) Local Java if available
    """
    errors = []
    
    # First try: Use encoded URL approach
    try:
        with open(puml_file_path, 'r', encoding='utf-8') as f:
            puml_content = f.read()
        
        # Try the PlantUML encoding approach
        encoded = encode_plantuml(puml_content)
        url = f'{PLANTUML_SERVER}/{encoded}'
        
        headers = {
            'User-Agent': 'Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36',
            'Accept': 'image/png,image/*,*/*'
        }
        request = urllib.request.Request(url, headers=headers)
        
        with urllib.request.urlopen(request, timeout=30, context=get_ssl_context()) as response:
            if response.status == 200:
                png_data = response.read()
                if len(png_data) > 100:  # Ensure it's not an error page
                    with open(output_png_path, 'wb') as f:
                        f.write(png_data)
                    return True
                else:
                    errors.append(f"Server returned empty/invalid response ({len(png_data)} bytes)")
            else:
                errors.append(f"Server returned status {response.status}")
                
    except urllib.error.HTTPError as e:
        errors.append(f"HTTP Error {e.code}: {e.reason}")
    except urllib.error.URLError as e:
        errors.append(f"URL Error: {e.reason}")
    except Exception as e:
        errors.append(f"Server encoding failed: {str(e)}")
    
    # Second try: Use PlantUML text API
    try:
        if generate_png_via_text_api(puml_file_path, output_png_path):
            return True
    except Exception as e:
        errors.append(f"Text API failed: {str(e)}")
    
    # Third try: Local Java if available
    try:
        if generate_png_via_local_java(puml_file_path, output_png_path):
            return True
    except Exception as e:
        errors.append(f"Local Java failed: {str(e)}")
    
    if verbose:
        print(f"      ⚠️  All methods failed:")
        for err in errors:
            print(f"         • {err}")
    
    return False


def generate_png_via_text_api(puml_file_path, output_png_path):
    """
    Alternative method: Use PlantUML's text-based API.
    """
    with open(puml_file_path, 'r', encoding='utf-8') as f:
        puml_content = f.read()
    
    # Use PlantUML's form-based API
    url = 'https://www.plantuml.com/plantuml/png/'
    
    # Create multipart form data
    boundary = '----WebKitFormBoundary7MA4YWxkTrZu0gW'
    data = f'------WebKitFormBoundary7MA4YWxkTrZu0gW\r\n'
    data += 'Content-Disposition: form-data; name="text"\r\n\r\n'
    data += puml_content + '\r\n'
    data += '------WebKitFormBoundary7MA4YWxkTrZu0gW--\r\n'
    
    headers = {
        'User-Agent': 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36',
        'Content-Type': f'multipart/form-data; boundary={boundary}'
    }
    
    request = urllib.request.Request(url, data=data.encode('utf-8'), headers=headers, method='POST')
    
    with urllib.request.urlopen(request, timeout=30, context=get_ssl_context()) as response:
        if response.status == 200:
            png_data = response.read()
            with open(output_png_path, 'wb') as f:
                f.write(png_data)
            return True
    
    return False


def download_plantuml_jar():
    """
    Download PlantUML jar if not present.
    Returns path to jar or None if download fails.
    """
    jar_path = Path('./plantuml.jar')
    
    if jar_path.exists():
        return str(jar_path)
    
    try:
        print('      📥 Downloading PlantUML jar...')
        url = 'https://github.com/plantuml/plantuml/releases/download/v1.2023.13/plantuml-1.2023.13.jar'
        
        request = urllib.request.Request(url, headers={
            'User-Agent': 'Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36'
        })
        
        with urllib.request.urlopen(request, timeout=60, context=get_ssl_context()) as response:
            if response.status == 200:
                with open(jar_path, 'wb') as f:
                    f.write(response.read())
                print(f'      ✅ Downloaded to {jar_path}')
                return str(jar_path)
    except Exception as e:
        print(f'      ⚠️  Download failed: {e}')
    
    return None


def generate_png_via_local_java(puml_file_path, output_png_path):
    """
    Generate PNG using local PlantUML jar if available.
    Auto-downloads jar if not found.
    """
    import subprocess
    
    # Check if plantuml jar exists in common locations
    jar_locations = [
        './plantuml.jar',
        '../plantuml.jar',
        '/usr/local/bin/plantuml.jar',
        '/usr/share/plantuml/plantuml.jar',
        os.path.expanduser('~/plantuml.jar'),
    ]
    
    plantuml_jar = None
    for location in jar_locations:
        if os.path.exists(location):
            plantuml_jar = location
            break
    
    # Try to download if not found
    if not plantuml_jar:
        plantuml_jar = download_plantuml_jar()
    
    if not plantuml_jar:
        return False
    
    # Run PlantUML
    cmd = ['java', '-jar', plantuml_jar, '-tpng', '-o', str(output_png_path.parent), str(puml_file_path)]
    result = subprocess.run(cmd, capture_output=True, text=True, timeout=60)
    
    return result.returncode == 0 and os.path.exists(output_png_path)


def generate_all_pngs(verbose=True):
    """
    Generate PNG files from all PlantUML files in the output directory.
    """
    puml_files = list(OUTPUT_DIR.glob('*.puml'))
    
    if not puml_files:
        print('   ⚠️  No .puml files found to convert')
        return
    
    print(f'🖼️  Generating PNG images from {len(puml_files)} PlantUML files...')
    print('   (Using PlantUML online server - requires internet connection)')
    print('   Tip: If this fails, install PlantUML locally: brew install plantuml')
    print()
    
    success_count = 0
    for puml_file in puml_files:
        png_file = puml_file.with_suffix('.png')
        print(f'   🎨 {puml_file.name} → {png_file.name}')
        
        if generate_png_from_puml(puml_file, png_file, verbose=verbose):
            # Get file size
            size = png_file.stat().st_size
            print(f'      ✅ Success ({size:,} bytes)')
            success_count += 1
        else:
            print(f'      ❌ Failed (see errors above)')
    
    print()
    print(f'📊 PNG Generation: {success_count}/{len(puml_files)} successful')
    
    if success_count == 0:
        print()
        print('💡 To generate PNGs locally:')
        print('   1. Install PlantUML: brew install plantuml')
        print('   2. Or use the VS Code PlantUML extension')
        print('   3. Or visit https://www.plantuml.com/plantuml/uml/ and paste the .puml content')


def parse_java_file(file_path):
    """Parse a Java file to extract class information"""
    with open(file_path, 'r', encoding='utf-8') as f:
        content = f.read()
    
    lines = content.split('\n')
    
    result = {
        'package': '',
        'className': '',
        'isInterface': False,
        'isAbstract': False,
        'classType': 'class',
        'extends': None,
        'implements': [],
        'methods': [],
        'fields': [],
        'imports': [],
        'filePath': str(file_path)
    }
    
    in_multiline_comment = False
    
    for line in lines:
        stripped = line.strip()
        
        # Skip empty lines
        if not stripped:
            continue
        
        # Track multi-line comments
        if stripped.startswith('/*'):
            in_multiline_comment = True
        if stripped.endswith('*/'):
            in_multiline_comment = False
            continue
        if in_multiline_comment:
            continue
        
        # Skip single-line comments and string literals containing 'package '
        if stripped.startswith('//') or stripped.startswith('*'):
            continue
        
        # Package declaration - must be at line start, not inside strings
        if stripped.startswith('package ') and stripped.endswith(';'):
            # Verify it's not inside a string literal (check for odd number of quotes before 'package')
            quote_count = line.count('"', 0, line.find('package '))
            if quote_count % 2 == 0:  # Even number of quotes means not inside a string
                result['package'] = stripped.replace('package ', '').replace(';', '')
        
        # Import statements
        if stripped.startswith('import '):
            result['imports'].append(stripped.replace('import ', '').replace(';', ''))
        
        # Class, interface, or enum declaration
        # Pattern: [public] [abstract] (class|interface|enum) Name [extends Parent] [implements Interface1, Interface2]
        class_pattern = r'^(public\s+)?(abstract\s+)?(class|interface|enum)\s+(\w+)(?:\s+extends\s+(\w+))?(?:\s+implements\s+([\w,\s]+))?'
        match = re.match(class_pattern, stripped)
        
        if match:
            result['isAbstract'] = match.group(2) is not None
            result['classType'] = match.group(3)
            result['isInterface'] = match.group(3) == 'interface'
            result['className'] = match.group(4)
            result['extends'] = match.group(5)
            
            if match.group(6):
                implements = match.group(6).split(',')
                result['implements'] = [i.strip() for i in implements if i.strip()]
        
        # Field declarations
        # Pattern: visibility [static] [final] Type name;
        field_pattern = r'^(private|protected|public)\s+(?:static\s+)?(?:final\s+)?(\w+(?:<[^>]+>)?)\s+(\w+)\s*;'
        field_match = re.match(field_pattern, stripped)
        if field_match and '(' not in stripped:
            result['fields'].append({
                'visibility': field_match.group(1),
                'type': field_match.group(2),
                'name': field_match.group(3)
            })
        
        # Method declarations (simplified)
        # Pattern: visibility [static] [abstract] ReturnType name(
        method_pattern = r'^(public|private|protected)\s+(?:static\s+)?(?:abstract\s+)?(\w+(?:<[^>]+>)?)\s+(\w+)\s*\('
        method_match = re.match(method_pattern, stripped)
        if method_match and not result['isInterface']:
            result['methods'].append({
                'visibility': method_match.group(1),
                'returnType': method_match.group(2),
                'name': method_match.group(3)
            })
    
    return result


def find_java_files(directory):
    """Recursively find all Java files"""
    java_files = []
    for root, dirs, files in os.walk(directory):
        for file in files:
            if file.endswith('.java'):
                java_files.append(Path(root) / file)
    return java_files


def generate_class_diagram(classes):
    """Generate PlantUML class diagram"""
    uml = '@startuml\n'
    uml += '!theme plain\n'
    uml += 'skinparam classAttributeIconSize 0\n\n'
    uml += 'title Student Planner - Class Diagram\n\n'
    
    # Group by package
    packages = {}
    for cls in classes:
        pkg = cls['package']
        if pkg not in packages:
            packages[pkg] = []
        packages[pkg].append(cls)
    
    # Generate classes by package
    for pkg, package_classes in packages.items():
        uml += f'package "{pkg}" {{\n\n'
        
        for cls in package_classes:
            if cls['isInterface']:
                type_decl = 'interface'
            elif cls['isAbstract']:
                type_decl = 'abstract class'
            else:
                type_decl = 'class'
            
            uml += f'  {type_decl} {cls["className"]}'
            
            if cls['extends']:
                uml += f' extends {cls["extends"]}'
            
            if cls['implements']:
                uml += f' implements {", ".join(cls["implements"])}'
            
            uml += ' {\n'
            
            # Fields
            for field in cls['fields']:
                visibility = '+' if field['visibility'] == 'public' else \
                           '#' if field['visibility'] == 'protected' else '-'
                uml += f'    {visibility} {field["name"]}: {field["type"]}\n'
            
            if cls['fields'] and cls['methods']:
                uml += '    ..\n'
            
            # Methods (limit to first 5 for readability)
            for method in cls['methods'][:5]:
                visibility = '+' if method['visibility'] == 'public' else \
                           '#' if method['visibility'] == 'protected' else '-'
                uml += f'    {visibility} {method["name"]}(): {method["returnType"]}\n'
            
            if len(cls['methods']) > 5:
                uml += '    ...\n'
            
            uml += '  }\n\n'
        
        uml += '}\n\n'
    
    # Add relationships
    uml += "\n' Relationships\n"
    for cls in classes:
        if cls['extends'] and cls['extends'] != 'Object':
            uml += f'{cls["extends"]} <|-- {cls["className"]}\n'
        
        for impl in cls['implements']:
            if impl not in ['Serializable', 'Comparable', 'Cloneable']:
                uml += f'{impl} <|.. {cls["className"]}\n'
    
    # MVC relationships
    uml += "\n' MVC Pattern Relationships\n"
    uml += 'Model ..> ModelEvent : uses\n'
    uml += 'AbstractModel ..> ModelListener : notifies\n'
    uml += 'AbstractView ..> ModelListener : implements\n'
    uml += 'PlannerService --> PlannerModel : uses\n'
    uml += 'PlannerController --> PlannerService : delegates\n'
    
    # Domain relationships
    uml += "\n' Domain Relationships\n"
    uml += 'PlannerModel "1" *-- "1" Student : manages\n'
    uml += 'PlannerModel "1" *-- "0..*" Course : manages\n'
    uml += 'PlannerModel "1" *-- "0..*" Task : manages\n'
    uml += 'Task --> Course : associated with\n'
    
    uml += '\n@enduml\n'
    return uml


def generate_mvc_diagram(classes):
    """Generate PlantUML MVC architecture diagram"""
    uml = '@startuml\n'
    uml += '!theme plain\n'
    uml += 'skinparam componentStyle rectangle\n'
    uml += 'title Student Planner - MVC Architecture\n\n'
    
    # MVC Framework Layer
    uml += 'package "MVC Framework" {\n'
    uml += '  [Model\\nInterface] as ModelInterface\n'
    uml += '  [View\\nInterface] as ViewInterface\n'
    uml += '  [Controller\\nInterface] as ControllerInterface\n'
    uml += '  [AbstractModel] as AbstractModel\n'
    uml += '  [AbstractView] as AbstractView\n'
    uml += '  [AbstractController] as AbstractController\n'
    uml += '  [ModelEvent] as ModelEvent\n'
    uml += '  [ModelListener] as ModelListener\n'
    uml += '}\n\n'
    
    # Domain Model Layer
    uml += 'package "Domain Model" {\n'
    uml += '  [PlannerModel] as PlannerModel\n'
    uml += '  [Student] as Student\n'
    uml += '  [Course] as Course\n'
    uml += '  [Task] as Task\n'
    uml += '}\n\n'
    
    # Service Layer
    uml += 'package "Service Layer" {\n'
    uml += '  [PlannerService] as PlannerService\n'
    uml += '}\n\n'
    
    # UI Layer
    uml += 'package "UI Layer (Swing)" {\n'
    uml += '  [PlannerView] as PlannerView\n'
    uml += '  [PlannerController] as PlannerController\n'
    uml += '}\n\n'
    
    # Persistence Layer
    uml += 'package "Persistence Layer" {\n'
    uml += '  [PlannerRepository] as PlannerRepository\n'
    uml += '  [CSV Files] as CSVFiles\n'
    uml += '}\n\n'
    
    # User Layer
    uml += 'actor User\n\n'
    
    # Relationships - Inheritance
    uml += "' Inheritance Relationships\n"
    uml += 'AbstractModel --|> ModelInterface\n'
    uml += 'AbstractView --|> ViewInterface\n'
    uml += 'AbstractController --|> ControllerInterface\n'
    uml += 'PlannerModel --|> AbstractModel\n'
    uml += 'PlannerView --|> AbstractView\n'
    uml += 'PlannerController --|> AbstractController\n'
    
    # Relationships - MVC Pattern
    uml += "\n' MVC Pattern Flow\n"
    uml += 'User --> PlannerView : interacts\n'
    uml += 'PlannerView --> PlannerController : delegates actions\n'
    uml += 'PlannerController --> PlannerService : validates & processes\n'
    uml += 'PlannerService --> PlannerModel : updates\n'
    uml += 'AbstractModel --> ModelListener : notifies changes\n'
    uml += 'AbstractView --> AbstractModel : displays data\n'
    
    # Relationships - Observer Pattern
    uml += "\n' Observer Pattern\n"
    uml += 'AbstractModel ..> ModelEvent : creates\n'
    uml += 'AbstractModel ..> ModelListener : notifies\n'
    uml += 'AbstractView ..> ModelListener : implements\n'
    
    # Relationships - Persistence
    uml += "\n' Persistence Flow\n"
    uml += 'PlannerRepository --> PlannerModel : loads/saves\n'
    uml += 'PlannerRepository --> CSVFiles : reads/writes\n'
    
    # Domain Relationships
    uml += "\n' Domain Relationships\n"
    uml += 'PlannerModel *--> Student : manages\n'
    uml += 'PlannerModel *--> Course : manages\n'
    uml += 'PlannerModel *--> Task : manages\n'
    uml += 'Task --> Course : associated with\n'
    
    uml += '\n@enduml\n'
    return uml


def generate_mermaid_diagram(classes):
    """Generate Mermaid class diagram"""
    mermaid = '```mermaid\n'
    mermaid += 'classDiagram\n'
    mermaid += '    title Student Planner - Class Diagram\n\n'
    
    # Group by package
    packages = {}
    for cls in classes:
        pkg = cls['package']
        if pkg not in packages:
            packages[pkg] = []
        packages[pkg].append(cls)
    
    # Generate classes
    for pkg, package_classes in packages.items():
        mermaid += f'    %% Package: {pkg}\n'
        
        for cls in package_classes:
            type_annotation = ''
            if cls['isInterface']:
                type_annotation = '<<interface>>'
            elif cls['isAbstract']:
                type_annotation = '<<abstract>>'
            
            mermaid += f'    class {cls["className"]}{type_annotation}\n'
            
            # Add members
            if cls['fields'] or cls['methods']:
                mermaid += f'    class {cls["className"]} {{\n'
                
                # Fields
                for field in cls['fields']:
                    visibility = '+' if field['visibility'] == 'public' else \
                               '#' if field['visibility'] == 'protected' else '-'
                    mermaid += f'        {visibility}{field["type"]} {field["name"]}\n'
                
                if cls['fields'] and cls['methods']:
                    mermaid += '        ..\n'
                
                # Methods (limit to first 3)
                for method in cls['methods'][:3]:
                    visibility = '+' if method['visibility'] == 'public' else \
                               '#' if method['visibility'] == 'protected' else '-'
                    mermaid += f'        {visibility}{method["name"]}() {method["returnType"]}\n'
                
                if len(cls['methods']) > 3:
                    mermaid += '        ...\n'
                
                mermaid += '    }\n'
        
        mermaid += '\n'
    
    # Inheritance relationships
    mermaid += '    %% Inheritance\n'
    for cls in classes:
        if cls['extends'] and cls['extends'] != 'Object':
            mermaid += f'    {cls["extends"]} <|-- {cls["className"]}\n'
        
        for impl in cls['implements']:
            if impl not in ['Serializable', 'Comparable', 'Cloneable']:
                mermaid += f'    {impl} <|.. {cls["className"]} : implements\n'
    
    # MVC relationships
    mermaid += '\n    %% MVC Relationships\n'
    mermaid += '    Model ..> ModelEvent : uses\n'
    mermaid += '    AbstractModel ..> ModelListener : notifies\n'
    mermaid += '    AbstractView ..> ModelListener : implements\n'
    mermaid += '    PlannerService --> PlannerModel : uses\n'
    mermaid += '    PlannerController --> PlannerService : delegates\n'
    mermaid += '    PlannerView --> PlannerController : delegates\n'
    
    mermaid += '\n    %% Domain Relationships\n'
    mermaid += '    PlannerModel "1" --> "1" Student : manages\n'
    mermaid += '    PlannerModel "1" --> "*" Course : manages\n'
    mermaid += '    PlannerModel "1" --> "*" Task : manages\n'
    mermaid += '    Task --> Course : associated\n'
    
    mermaid += '```\n'
    return mermaid


def generate_sequence_diagram():
    """Generate a sequence diagram showing typical MVC flow"""
    uml = '@startuml\n'
    uml += '!theme plain\n'
    uml += 'title Student Planner - MVC Interaction Flow (Add Task)\n\n'
    
    uml += 'actor User\n'
    uml += 'participant PlannerView\n'
    uml += 'participant PlannerController\n'
    uml += 'participant PlannerService\n'
    uml += 'participant PlannerModel\n'
    uml += 'participant AbstractModel\n'
    uml += 'participant ModelListener\n\n'
    
    uml += 'User -> PlannerView: Clicks "Add Task"\n'
    uml += 'PlannerView -> PlannerController: actionPerformed()\n'
    uml += 'PlannerController -> PlannerController: validateInput()\n'
    uml += 'PlannerController -> PlannerService: addTask()\n'
    uml += 'PlannerService -> PlannerService: validateBusinessRules()\n'
    uml += 'PlannerService -> PlannerModel: addTask()\n'
    uml += 'PlannerModel -> PlannerModel: store task\n'
    uml += 'PlannerModel -> AbstractModel: fireModelChanged("TASK_ADDED")\n'
    uml += 'AbstractModel -> ModelListener: notifyListeners()\n'
    uml += 'ModelListener -> PlannerView: modelChanged()\n'
    uml += 'PlannerView -> PlannerView: refreshTaskList()\n'
    uml += 'PlannerView --> User: Show updated list\n\n'
    
    uml += 'note right of PlannerService\n  Business logic validation:\n'
    uml += '  - Check course exists\n'
    uml += '  - Validate due date\n'
    uml += '  - Check for duplicates\n'
    uml += 'end note\n\n'
    
    uml += 'note right of AbstractModel\n  Observer Pattern:\n'
    uml += '  - All registered listeners\n'
    uml += '  - receive notification\n'
    uml += '  - and update accordingly\n'
    uml += 'end note\n\n'
    
    uml += '@enduml\n'
    return uml


def main():
    """Main execution function"""
    print('🔍 Scanning Java files...')
    ensure_output_dir()
    
    java_files = find_java_files(SRC_DIR)
    print(f'📁 Found {len(java_files)} Java files')
    
    print('🔍 Parsing classes...')
    classes = []
    for java_file in java_files:
        try:
            cls = parse_java_file(java_file)
            if cls['className']:  # Only add if we found a class
                classes.append(cls)
        except Exception as e:
            print(f'⚠️  Error parsing {java_file}: {e}')
    
    print(f'📊 Parsed {len(classes)} classes')
    
    # Generate class diagram
    print('🎨 Generating class diagram (PlantUML)...')
    class_diagram = generate_class_diagram(classes)
    with open(OUTPUT_DIR / 'class-diagram.puml', 'w') as f:
        f.write(class_diagram)
    print('✅ Class diagram saved to: diagrams/class-diagram.puml')
    
    # Generate MVC diagram
    print('🎨 Generating MVC architecture diagram (PlantUML)...')
    mvc_diagram = generate_mvc_diagram(classes)
    with open(OUTPUT_DIR / 'mvc-diagram.puml', 'w') as f:
        f.write(mvc_diagram)
    print('✅ MVC diagram saved to: diagrams/mvc-diagram.puml')
    
    # Generate sequence diagram
    print('🎨 Generating sequence diagram (PlantUML)...')
    sequence_diagram = generate_sequence_diagram()
    with open(OUTPUT_DIR / 'sequence-diagram.puml', 'w') as f:
        f.write(sequence_diagram)
    print('✅ Sequence diagram saved to: diagrams/sequence-diagram.puml')
    
    # Generate Mermaid diagram
    print('🎨 Generating Mermaid class diagram...')
    mermaid_diagram = generate_mermaid_diagram(classes)
    with open(OUTPUT_DIR / 'class-diagram.md', 'w') as f:
        f.write(mermaid_diagram)
    print('✅ Mermaid diagram saved to: diagrams/class-diagram.md')
    
    # Generate summary README
    packages = sorted(set(cls['package'] for cls in classes))
    summary = f'''# Diagram Generation Summary

Generated on: {datetime.now().strftime('%Y-%m-%d %H:%M:%S')}
Total Java files scanned: {len(java_files)}
Total classes parsed: {len(classes)}

## Files Generated

1. **class-diagram.puml** - PlantUML class diagram showing all classes and relationships
2. **mvc-diagram.puml** - PlantUML MVC architecture diagram showing layer interactions
3. **sequence-diagram.puml** - PlantUML sequence diagram showing MVC flow for "Add Task"
4. **class-diagram.md** - Mermaid class diagram (Markdown format for GitHub)

## Packages Found

{chr(10).join(f'- `{pkg}`' for pkg in packages)}

## Classes by Package

'''
    
    for pkg in packages:
        package_classes = [cls for cls in classes if cls['package'] == pkg]
        summary += f'### {pkg}\n\n'
        for cls in package_classes:
            type_str = 'interface' if cls['isInterface'] else ('abstract class' if cls['isAbstract'] else 'class')
            summary += f'- **{cls["className"]}** ({type_str})\n'
            if cls['extends']:
                summary += f'  - Extends: `{cls["extends"]}`\n'
            if cls['implements']:
                summary += f'  - Implements: {", ".join(f"`{i}`" for i in cls["implements"])}\n'
        summary += '\n'
    
    summary += '''## How to View the Diagrams

### PlantUML Diagrams (.puml files)

**Option 1: Online Editor**
1. Go to https://www.plantuml.com/plantuml/
2. Copy the contents of any .puml file
3. Paste into the online editor
4. The diagram will render automatically

**Option 2: VS Code Extension**
1. Install the "PlantUML" extension
2. Open any .puml file in VS Code
3. Use Alt+D (Windows/Linux) or Option+D (Mac) to preview

**Option 3: Automatic PNG Generation**
The script now automatically generates PNG files from PlantUML diagrams using the online PlantUML server.

**Option 4: Local Installation (if you prefer)**
```bash
# Install PlantUML (requires Java)
# Download from https://plantuml.com/download

# Generate PNG
java -jar plantuml.jar diagrams/*.puml
```

### Mermaid Diagrams (.md files)

**GitHub/GitLab:**
- The .md files will render automatically on GitHub or GitLab
- Just open the file in the repository

**VS Code:**
1. Install the "Markdown Preview Mermaid Support" extension
2. Open the .md file
3. Use Ctrl+Shift+V to preview

**Online:**
- Go to https://mermaid.live/
- Paste the content from the .md file
- The diagram will render automatically

## Architecture Overview

### MVC Layers

1. **MVC Framework Layer** (mvc package)
   - Core interfaces and abstract classes
   - Observer pattern implementation
   - Reusable for any MVC application

2. **Domain Model Layer** (planner.model package)
   - Business entities (Student, Course, Task)
   - Main model (PlannerModel) extending AbstractModel
   - Data validation and relationships

3. **Service Layer** (planner.service package)
   - Business logic (PlannerService)
   - Validation rules
   - Complex operations

4. **UI Layer** (planner.ui package)
   - Swing-based interface (PlannerView)
   - Event handling (PlannerController)
   - Automatic updates via observer pattern

5. **Persistence Layer** (planner.persistence package)
   - Data storage (PlannerRepository)
   - CSV file format
   - Load/save operations

### Design Patterns Used

- **Model-View-Controller (MVC)**: Overall architecture
- **Observer Pattern**: Model change notifications
- **Abstract Factory**: Component creation
- **Repository Pattern**: Data access abstraction
- **Template Method**: Abstract base classes

## Regenerating Diagrams

To regenerate the diagrams after making code changes:

```bash
# Using Python
python3 generate_diagrams.py

# Or make the script executable and run directly
chmod +x generate_diagrams.py
./generate_diagrams.py
```

The script will automatically:
1. Scan all Java files in `src/main/java`
2. Parse class definitions, fields, and methods
3. Generate PlantUML and Mermaid diagrams
4. Generate PNG images using the PlantUML online server
5. Save all files to the `diagrams/` directory
'''
    
    with open(OUTPUT_DIR / 'README.md', 'w') as f:
        f.write(summary)
    print('📝 Summary saved to: diagrams/README.md')
    
    # Generate PNG images
    print()
    generate_all_pngs()
    
    print('\n' + '='*60)
    print('🎉 Diagram generation complete!')
    print(f'📁 Output directory: {OUTPUT_DIR}/')
    print('\n📊 Generated files:')
    print('   • class-diagram.puml    (PlantUML class diagram)')
    print('   • mvc-diagram.puml      (PlantUML MVC architecture)')
    print('   • sequence-diagram.puml (PlantUML sequence flow)')
    print('   • class-diagram.md      (Mermaid class diagram)')
    print('   • *.png                 (PNG images of all diagrams)')
    print('   • README.md             (Documentation)')
    print('='*60)


if __name__ == '__main__':
    try:
        main()
    except KeyboardInterrupt:
        print('\n\n⚠️  Operation cancelled by user')
    except Exception as e:
        print(f'\n❌ Error generating diagrams: {e}')
        import traceback
        traceback.print_exc()
        exit(1)
