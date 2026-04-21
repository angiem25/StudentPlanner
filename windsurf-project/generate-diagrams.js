#!/usr/bin/env node

/**
 * Auto-generated diagram generator for Student Planner project
 * Creates class diagrams and MVC architecture diagrams
 */

const fs = require('fs');
const path = require('path');

// Configuration
const SRC_DIR = './src/main/java';
const OUTPUT_DIR = './diagrams';

// Ensure output directory exists
if (!fs.existsSync(OUTPUT_DIR)) {
    fs.mkdirSync(OUTPUT_DIR, { recursive: true });
}

/**
 * Parses a Java file to extract class information
 */
function parseJavaFile(filePath) {
    const content = fs.readFileSync(filePath, 'utf8');
    const lines = content.split('\n');
    
    const result = {
        package: '',
        className: '',
        isInterface: false,
        isAbstract: false,
        extends: null,
        implements: [],
        methods: [],
        fields: [],
        imports: [],
        filePath: filePath
    };

    for (const line of lines) {
        const trimmed = line.trim();
        
        // Package declaration
        if (trimmed.startsWith('package ')) {
            result.package = trimmed.replace('package ', '').replace(';', '');
        }
        
        // Import statements
        if (trimmed.startsWith('import ')) {
            result.imports.push(trimmed.replace('import ', '').replace(';', ''));
        }
        
        // Class, interface, or enum declaration
        const classMatch = trimmed.match(/^(public\s+)?(abstract\s+)?(class|interface|enum)\s+(\w+)(?:\s+extends\s+(\w+))?(?:\s+implements\s+([\w,\s]+))?/);
        if (classMatch) {
            result.isAbstract = classMatch[2] !== undefined;
            result.classType = classMatch[3];
            result.isInterface = classMatch[3] === 'interface';
            result.className = classMatch[4];
            result.extends = classMatch[5] || null;
            if (classMatch[6]) {
                result.implements = classMatch[6].split(',').map(s => s.trim()).filter(s => s);
            }
        }
        
        // Field declarations (simple heuristic)
        const fieldMatch = trimmed.match(/^(private|protected|public)\s+(?:static\s+)?(?:final\s+)?(\w+(?:<[^>]+>)?)\s+(\w+)\s*;/);
        if (fieldMatch && !trimmed.includes('(')) {
            result.fields.push({
                visibility: fieldMatch[1],
                type: fieldMatch[2],
                name: fieldMatch[3]
            });
        }
        
        // Method declarations (simple heuristic)
        const methodMatch = trimmed.match(/^(public|private|protected)\s+(?:static\s+)?(?:abstract\s+)?(\w+(?:<[^>]+>)?)\s+(\w+)\s*\(/);
        if (methodMatch && !result.isInterface) {
            result.methods.push({
                visibility: methodMatch[1],
                returnType: methodMatch[2],
                name: methodMatch[3]
            });
        }
    }
    
    return result;
}

/**
 * Recursively finds all Java files
 */
function findJavaFiles(dir, files = []) {
    const items = fs.readdirSync(dir);
    
    for (const item of items) {
        const fullPath = path.join(dir, item);
        const stat = fs.statSync(fullPath);
        
        if (stat.isDirectory()) {
            findJavaFiles(fullPath, files);
        } else if (item.endsWith('.java')) {
            files.push(fullPath);
        }
    }
    
    return files;
}

/**
 * Generates PlantUML class diagram
 */
function generateClassDiagram(classes) {
    let uml = '@startuml\n';
    uml += '!theme plain\n';
    uml += 'skinparam classAttributeIconSize 0\n\n';
    uml += 'title Student Planner - Class Diagram\n\n';
    
    // Group by package
    const packages = {};
    for (const cls of classes) {
        if (!packages[cls.package]) {
            packages[cls.package] = [];
        }
        packages[cls.package].push(cls);
    }
    
    // Generate classes by package
    for (const [pkg, packageClasses] of Object.entries(packages)) {
        uml += `package \"${pkg}\" {\n`;
        
        for (const cls of packageClasses) {
            const type = cls.isInterface ? 'interface' : (cls.isAbstract ? 'abstract class' : 'class');
            uml += `  ${type} ${cls.className}`;
            
            if (cls.extends) {
                uml += ` extends ${cls.extends}`;
            }
            
            if (cls.implements.length > 0) {
                uml += ` implements ${cls.implements.join(', ')}`;
            }
            
            uml += ' {\n';
            
            // Fields
            for (const field of cls.fields) {
                const visibility = field.visibility === 'public' ? '+' : 
                                 field.visibility === 'protected' ? '#' : '-';
                uml += `    ${visibility} ${field.name}: ${field.type}\n`;
            }
            
            if (cls.fields.length > 0 && cls.methods.length > 0) {
                uml += '    ..\n';
            }
            
            // Methods
            for (const method of cls.methods) {
                const visibility = method.visibility === 'public' ? '+' : 
                                 method.visibility === 'protected' ? '#' : '-';
                uml += `    ${visibility} ${method.name}(): ${method.returnType}\n`;
            }
            
            uml += '  }\n\n';
        }
        
        uml += '}\n\n';
    }
    
    // Add relationships
    uml += '\n\' Relationships\n';
    for (const cls of classes) {
        if (cls.extends) {
            uml += `${cls.extends} <|-- ${cls.className}\n`;
        }
        for (const impl of cls.implements) {
            if (impl !== 'Serializable') { // Skip common interfaces
                uml += `${impl} <|.. ${cls.className}\n`;
            }
        }
    }
    
    // Add MVC relationships
    uml += '\n\' MVC Relationships\n';
    uml += 'Model ..> ModelEvent : uses\n';
    uml += 'Model ..> ModelListener : notifies\n';
    uml << 'View ..> Controller : delegates\n';
    uml += 'Controller ..> Model : updates\n';
    
    uml += '@enduml\n';
    return uml;
}

/**
 * Generates PlantUML MVC architecture diagram
 */
function generateMVCDiagram(classes) {
    let uml = '@startuml\n';
    uml += '!theme plain\n';
    uml += 'skinparam componentStyle rectangle\n';
    uml += 'title Student Planner - MVC Architecture\n\n';
    
    // MVC Framework Layer
    uml += 'package \"MVC Framework\" {\n';
    uml += '  [Model\\nInterface] as ModelInterface\n';
    uml += '  [View\\nInterface] as ViewInterface\n';
    uml += '  [Controller\\nInterface] as ControllerInterface\n';
    uml += '  [AbstractModel] as AbstractModel\n';
    uml += '  [AbstractView] as AbstractView\n';
    uml += '  [AbstractController] as AbstractController\n';
    uml += '  [ModelEvent] as ModelEvent\n';
    uml += '  [ModelListener] as ModelListener\n';
    uml += '}\n\n';
    
    // Domain Model Layer
    uml += 'package \"Domain Model\" {\n';
    uml += '  [PlannerModel] as PlannerModel\n';
    uml += '  [Student] as Student\n';
    uml += '  [Course] as Course\n';
    uml += '  [Task] as Task\n';
    uml += '}\n\n';
    
    // Service Layer
    uml += 'package \"Service Layer\" {\n';
    uml += '  [PlannerService] as PlannerService\n';
    uml += '}\n\n';
    
    // UI Layer
    uml += 'package \"UI Layer\" {\n';
    uml += '  [PlannerView] as PlannerView\n';
    uml += '  [PlannerController] as PlannerController\n';
    uml += '}\n\n';
    
    // Persistence Layer
    uml += 'package \"Persistence Layer\" {\n';
    uml += '  [PlannerRepository] as PlannerRepository\n';
    uml += '}\n\n';
    
    // Relationships
    uml += '\' Inheritance\n';
    uml += 'AbstractModel --|> ModelInterface\n';
    uml += 'AbstractView --|> ViewInterface\n';
    uml += 'AbstractController --|> ControllerInterface\n';
    uml += 'PlannerModel --|> AbstractModel\n';
    uml += 'PlannerView --|> AbstractView\n';
    uml += 'PlannerController --|> AbstractController\n';
    
    uml += '\' Relationships\n';
    uml += 'AbstractModel ..> ModelEvent : creates\n';
    uml += 'AbstractModel ..> ModelListener : notifies\n';
    uml += 'AbstractView ..> ModelListener : implements\n';
    uml += 'PlannerService --> PlannerModel : uses\n';
    uml += 'PlannerController --> PlannerService : delegates\n';
    uml += 'PlannerView --> PlannerController : delegates\n';
    uml += 'PlannerRepository --> PlannerModel : loads/saves\n';
    
    uml += '\' Domain Relationships\n';
    uml += 'PlannerModel *--> Student : manages\n';
    uml += 'PlannerModel *--> Course : manages\n';
    uml += 'PlannerModel *--> Task : manages\n';
    uml += 'Task --> Course : associated with\n';
    
    uml += '@enduml\n';
    return uml;
}

/**
 * Generates Mermaid class diagram (alternative format)
 */
function generateMermaidClassDiagram(classes) {
    let mermaid = '```mermaid\n';
    mermaid += 'classDiagram\n';
    mermaid += '    title Student Planner - Class Diagram\n\n';
    
    // Group by package
    const packages = {};
    for (const cls of classes) {
        if (!packages[cls.package]) {
            packages[cls.package] = [];
        }
        packages[cls.package].push(cls);
    }
    
    // Generate classes
    for (const [pkg, packageClasses] of Object.entries(packages)) {
        mermaid += `    %% Package: ${pkg}\n`;
        
        for (const cls of packageClasses) {
            const type = cls.isInterface ? '<<interface>>' : (cls.isAbstract ? '<<abstract>>' : '');
            mermaid += `    class ${cls.className}${type ? ' ' + type : ''} {\n`;
            
            for (const field of cls.fields) {
                const visibility = field.visibility === 'public' ? '+' : 
                                 field.visibility === 'protected' ? '#' : '-';
                mermaid += `        ${visibility}${field.type} ${field.name}\n`;
            }
            
            for (const method of cls.methods.slice(0, 3)) { // Limit methods for readability
                const visibility = method.visibility === 'public' ? '+' : 
                                 method.visibility === 'protected' ? '#' : '-';
                mermaid += `        ${visibility}${method.name}() ${method.returnType}\n`;
            }
            
            if (cls.methods.length > 3) {
                mermaid += `        ...\n`;
            }
            
            mermaid += '    }\n';
        }
        
        mermaid += '\n';
    }
    
    // Add relationships
    mermaid += '    %% Inheritance\n';
    for (const cls of classes) {
        if (cls.extends) {
            mermaid += `    ${cls.extends} <|-- ${cls.className}\n`;
        }
        for (const impl of cls.implements) {
            if (!['Serializable', 'Comparable', 'Cloneable'].includes(impl)) {
                mermaid += `    ${impl} <|.. ${cls.className} : implements\n`;
            }
        }
    }
    
    // MVC relationships
    mermaid += '\n    %% MVC Relationships\n';
    mermaid += '    Model ..> ModelEvent : uses\n';
    mermaid += '    AbstractModel ..> ModelListener : notifies\n';
    mermaid += '    AbstractView ..> ModelListener : implements\n';
    mermaid += '    PlannerService --> PlannerModel : uses\n';
    mermaid += '    PlannerController --> PlannerService : delegates\n';
    
    mermaid += '```\n';
    return mermaid;
}

/**
 * Main execution
 */
function main() {
    console.log('🔍 Scanning Java files...');
    const javaFiles = findJavaFiles(SRC_DIR);
    console.log(`📁 Found ${javaFiles.length} Java files`);
    
    console.log('🔍 Parsing classes...');
    const classes = javaFiles.map(file => parseJavaFile(file));
    console.log(`📊 Parsed ${classes.length} classes`);
    
    // Generate class diagram
    console.log('🎨 Generating class diagram (PlantUML)...');
    const classDiagram = generateClassDiagram(classes);
    fs.writeFileSync(path.join(OUTPUT_DIR, 'class-diagram.puml'), classDiagram);
    console.log('✅ Class diagram saved to: diagrams/class-diagram.puml');
    
    // Generate MVC diagram
    console.log('🎨 Generating MVC diagram (PlantUML)...');
    const mvcDiagram = generateMVCDiagram(classes);
    fs.writeFileSync(path.join(OUTPUT_DIR, 'mvc-diagram.puml'), mvcDiagram);
    console.log('✅ MVC diagram saved to: diagrams/mvc-diagram.puml');
    
    // Generate Mermaid diagram
    console.log('🎨 Generating class diagram (Mermaid)...');
    const mermaidDiagram = generateMermaidClassDiagram(classes);
    fs.writeFileSync(path.join(OUTPUT_DIR, 'class-diagram.md'), mermaidDiagram);
    console.log('✅ Mermaid diagram saved to: diagrams/class-diagram.md');
    
    // Generate summary
    const summary = `# Diagram Generation Summary

Generated on: ${new Date().toISOString()}
Total Java files scanned: ${javaFiles.length}
Total classes parsed: ${classes.length}

## Files Generated

1. **class-diagram.puml** - PlantUML class diagram
2. **mvc-diagram.puml** - PlantUML MVC architecture diagram  
3. **class-diagram.md** - Mermaid class diagram (Markdown format)

## Packages Found

${Object.keys(classes.reduce((acc, cls) => {
    acc[cls.package] = true;
    return acc;
}, {})).map(pkg => `- ${pkg}`).join('\n')}

## Usage

### Viewing PlantUML Diagrams
1. Use an online PlantUML editor: https://www.plantuml.com/plantuml/
2. Copy the contents of .puml files and paste into the editor
3. Or use local PlantUML installation

### Viewing Mermaid Diagrams
1. The .md file can be viewed in any Markdown viewer that supports Mermaid
2. GitHub automatically renders Mermaid diagrams
3. Use online Mermaid editor: https://mermaid.live/
`;
    
    fs.writeFileSync(path.join(OUTPUT_DIR, 'README.md'), summary);
    console.log('📝 Summary saved to: diagrams/README.md');
    
    console.log('\n🎉 Diagram generation complete!');
    console.log('📁 Output directory: ./diagrams/');
}

// Run if called directly
if (require.main === module) {
    try {
        main();
    } catch (error) {
        console.error('❌ Error generating diagrams:', error.message);
        process.exit(1);
    }
}

module.exports = { parseJavaFile, generateClassDiagram, generateMVCDiagram };
