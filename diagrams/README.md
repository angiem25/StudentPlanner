# Diagram Generation Summary

Generated on: 2026-04-28 11:58:38
Total Java files scanned: 23
Total classes parsed: 20

## Files Generated

1. **class-diagram.puml** - PlantUML class diagram showing all classes and relationships
2. **mvc-diagram.puml** - PlantUML MVC architecture diagram showing layer interactions
3. **sequence-diagram.puml** - PlantUML sequence diagram showing MVC flow for "Add Task"
4. **class-diagram.md** - Mermaid class diagram (Markdown format for GitHub)

## Packages Found

- `mvc`
- `planner`
- `planner.model`
- `planner.persistence`
- `planner.service`
- `planner.ui`
- `planner.ui.calendar`
- `planner.ui.timer`

## Classes by Package

### mvc

- **ModelListener** (interface)
- **AbstractModel** (abstract class)
  - Implements: `Model`
- **AbstractController** (abstract class)
  - Implements: `Controller`
- **ModelEvent** (class)
- **AbstractView** (abstract class)
  - Implements: `View`, `ModelListener`
- **Controller** (interface)
- **View** (interface)
- **Model** (interface)

### planner

- **Main** (class)

### planner.model

- **PlannerModel** (class)
  - Extends: `AbstractModel`
- **Event** (class)
- **Course** (class)
- **Priority** (class)
- **Student** (class)

### planner.persistence

- **PlannerRepository** (class)

### planner.service

- **PlannerService** (class)

### planner.ui

- **PlannerView** (class)
  - Extends: `AbstractView`
- **PlannerController** (class)
  - Extends: `AbstractController`

### planner.ui.calendar

- **ViewType** (class)
  - Implements: `View`, `ModelListener`

### planner.ui.timer

- **TimerPanel** (class)
  - Extends: `JPanel`
  - Implements: `ActionListener`

## How to View the Diagrams

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
