# Student Planner - Master Development Prompt

## Project Overview

**Project Name**: Student Planner  
**Architecture**: Modular MVC with Observer Pattern  
**Language**: Java 17+ with Swing UI  
**Build Tool**: Maven  
**Total Components**: 19 Java files across 6 architectural layers

## Module of Thought Development Order

Execute these phases sequentially. Each phase builds upon the previous.

### Phase 1: Reusable MVC Framework (Domain-Agnostic)
**Purpose**: Create infrastructure that can be reused for any MVC application

| Order | Prompt | Component | Files | Key Concepts |
|-------|--------|-----------|-------|--------------|
| 1 | [PROMPT-00](./prompts/PROMPT-00-ARCHITECTURE-OVERVIEW.md) | Architecture Mental Model | - | MVC pattern, observer pattern, data flow |
| 2 | [PROMPT-A](./prompts/PROMPT-A-CORE-MVC-CONTRACTS.md) | Core Interfaces | 3 files | Model, View, Controller interfaces |
| 3 | [PROMPT-B](./prompts/PROMPT-B-EVENT-INFRASTRUCTURE.md) | Event System | 2 files | ModelEvent, ModelListener (observer) |
| 4 | [PROMPT-C](./prompts/PROMPT-C-BASE-MVC-CLASSES.md) | Abstract Base Classes | 3 files | AbstractModel, AbstractView, AbstractController |

**Deliverable**: Reusable `mvc` package with 8 files  
**Dependencies**: None  
**Verification**: Framework contains no planner-specific code

---

### Phase 2: Domain Models (Business Entities)
**Purpose**: Define the core business entities for the student planner

| Order | Prompt | Component | Files | Key Concepts |
|-------|--------|-----------|-------|--------------|
| 5 | [PROMPT-D](./prompts/PROMPT-D-DOMAIN-MODELS.md) | Domain Entities | 4 files | Student, Course, Task, PlannerModel |
| 6 | [PROMPT-E](./prompts/PROMPT-E-SERVICE-LAYER.md) | Business Logic | 1 file | PlannerService with validation |

**Deliverable**: `planner.model` and `planner.service` packages  
**Dependencies**: Phase 1 (MVC Framework)  
**Verification**: Entities use UUID identity, service validates business rules

---

### Phase 3: Presentation Layer (User Interface)
**Purpose**: Create the Swing-based GUI that responds to model changes

| Order | Prompt | Component | Files | Key Concepts |
|-------|--------|-----------|-------|--------------|
| 7 | [PROMPT-F](./prompts/PROMPT-F-USER-INTERFACE.md) | Swing UI | 2 files | PlannerView, PlannerController |

**Deliverable**: `planner.ui` package with tabbed interface  
**Dependencies**: Phase 1 (Framework), Phase 2 (Domain)  
**Verification**: UI updates automatically when model changes (observer pattern works)

---

### Phase 4: Infrastructure (Persistence & Integration)
**Purpose**: Enable data storage and application startup

| Order | Prompt | Component | Files | Key Concepts |
|-------|--------|-----------|-------|--------------|
| 8 | [PROMPT-G](./prompts/PROMPT-G-PERSISTENCE-LAYER.md) | Data Storage | 1 file | PlannerRepository (CSV) |
| 9 | [PROMPT-H](./prompts/PROMPT-H-MAIN-APPLICATION.md) | Application Entry | 1 file | Main.java with dependency injection |

**Deliverable**: `planner.persistence` package and application bootstrap  
**Dependencies**: Phase 1-3  
**Verification**: Data persists across restarts, components wired correctly

---

### Phase 5: Documentation & Tooling
**Purpose**: Document the architecture and create development tools

| Order | Prompt | Component | Files | Key Concepts |
|-------|--------|-----------|-------|--------------|
| 10 | [PROMPT-I](./prompts/PROMPT-I-DOCUMENTATION.md) | Project Documentation | 3 files | README, ARCHITECTURE_SUMMARY, pom.xml |
| 11 | [PROMPT-J](./prompts/PROMPT-J-DIAGRAM-GENERATION.md) | Diagram Scripts | 2 files | generate_diagrams.py, generate_png.sh |

**Deliverable**: Complete documentation and auto-generated diagrams  
**Dependencies**: All previous phases  
**Verification**: README explains usage, diagrams generate successfully

---

### Phase 6: UI Enhancements (Optional Features)
**Purpose**: Add enhanced calendar and productivity features per SRS requirements

| Order | Prompt | Component | Files | Key Concepts |
|-------|--------|-----------|-------|--------------|
| 12 | [PROMPT-K](./prompts/PROMPT-K-CALENDAR-VIEW.md) | Calendar Foundation | 1 file | Monthly calendar grid with tasks |
| 13 | [PROMPT-K1](./prompts/PROMPT-K1-EVENT-ENTITY.md) | Event Entity | 1 file | Calendar Event domain model (start/end time) |
| 14 | [PROMPT-K2](./prompts/PROMPT-K2-CALENDAR-VIEWS.md) | Calendar Day/Week/Month | 1 file | Multi-view calendar with 24h timeline |
| 15 | [PROMPT-K3](./prompts/PROMPT-K3-TIMER.md) | Study Timer | 1 file | Countdown timer with alerts |

**Deliverable**: Calendar with Day/Week/Month views, Event entity, Study Timer  
**Dependencies**: Phase 5 (all core features complete)  
**Verification**: Calendar displays tasks/events correctly, timer works with alerts

---

## Complete Project Structure

```
windsurf-project/
│
├── PROMPT.md                          ← YOU ARE HERE (Master Guide)
├── README.md                          (User documentation)
├── ARCHITECTURE_SUMMARY.md            (Architectural overview)
├── pom.xml                           (Maven build configuration)
│
├── prompts/                          (Module of Thought Development)
│   ├── README.md                     (Prompt index)
│   ├── PROMPT-00-ARCHITECTURE-OVERVIEW.md
│   ├── PROMPT-A-CORE-MVC-CONTRACTS.md
│   ├── PROMPT-B-EVENT-INFRASTRUCTURE.md
│   ├── PROMPT-C-BASE-MVC-CLASSES.md
│   ├── PROMPT-D-DOMAIN-MODELS.md
│   ├── PROMPT-E-SERVICE-LAYER.md
│   ├── PROMPT-F-USER-INTERFACE.md
│   ├── PROMPT-G-PERSISTENCE-LAYER.md
│   ├── PROMPT-H-MAIN-APPLICATION.md
│   ├── PROMPT-I-DOCUMENTATION.md
│   ├── PROMPT-J-DIAGRAM-GENERATION.md
│   ├── PROMPT-K-CALENDAR-VIEW.md
│   ├── PROMPT-K1-EVENT-ENTITY.md
│   ├── PROMPT-K2-CALENDAR-VIEWS.md
│   └── PROMPT-K3-TIMER.md
│
├── src/main/java/
│   │
│   ├── mvc/                         (MVC Framework - Reusable)
│   │   ├── Model.java              (interface)
│   │   ├── View.java               (interface)
│   │   ├── Controller.java         (interface)
│   │   ├── ModelEvent.java         (event object)
│   │   ├── ModelListener.java      (observer interface)
│   │   ├── AbstractModel.java      (base implementation)
│   │   ├── AbstractView.java       (base implementation)
│   │   └── AbstractController.java (base implementation)
│   │
│   └── planner/
│       ├── Main.java               (Application entry point)
│       │
│       ├── model/                  (Domain Layer)
│       │   ├── Student.java        (Entity)
│       │   ├── Course.java         (Entity)
│       │   ├── Task.java           (Entity + Priority enum)
│       │   ├── Event.java          (Entity - calendar events)
│       │   └── PlannerModel.java   (Main model)
│       │
│       ├── service/                (Service Layer)
│       │   └── PlannerService.java (Business logic)
│       │
│       ├── ui/                     (Presentation Layer)
│       │   ├── PlannerView.java    (Swing GUI)
│       │   ├── PlannerController.java (Event handler)
│       │   ├── calendar/           (Calendar components)
│       │   │   └── CalendarView.java
│       │   └── timer/              (Timer components)
│       │       └── TimerPanel.java
│       │
│       └── persistence/            (Data Layer)
│           └── PlannerRepository.java (CSV storage)
│
├── diagrams/                        (Auto-generated)
│   ├── class-diagram.puml         (PlantUML class diagram)
│   ├── mvc-diagram.puml          (MVC architecture)
│   ├── sequence-diagram.puml     (Interaction flow)
│   ├── class-diagram.md          (Mermaid format)
│   └── README.md                 (Diagram viewing guide)
│
├── generate_diagrams.py           (Python diagram generator)
└── generate_png.sh               (Shell script for PNG generation)
```

**Total Java Files**: 17  
**Total Prompt Files**: 11  
**Documentation Files**: 3  
**Scripts**: 2

---

## Architectural Layers

### Layer 1: MVC Framework (Domain-Agnostic)
```
┌─────────────────────────────────────────────┐
│           mvc Package                        │
│  ┌─────────┐ ┌─────────┐ ┌──────────────┐  │
│  │  Model  │ │   View  │ │  Controller  │  │  ← Interfaces
│  │(interface)│ │(interface)│ │ (interface)  │  │
│  └────┬────┘ └────┬────┘ └──────┬───────┘  │
│       │           │             │            │
│  ┌────▼───────────▼─────────────▼────────┐  │
│  │         Abstract Classes              │  │  ← Base implementations
│  │  AbstractModel, AbstractView,        │  │
│  │  AbstractController                   │  │
│  └────────────────────────────────────────┘  │
│       │                                      │
│  ┌────▼────────────────────────────────────┐ │
│  │      Observer Pattern                   │ │
│  │  ModelEvent ←────→ ModelListener       │ │
│  └─────────────────────────────────────────┘ │
└─────────────────────────────────────────────┘
```

**Characteristics**:
- Knows nothing about planners
- Reusable for any MVC application
- Implements observer pattern
- Provides base implementations

### Layer 2: Domain Model (Planner-Specific)
```
┌─────────────────────────────────────────────┐
│       planner.model Package                  │
│                                              │
│  ┌────────────────────────────────────┐   │
│  │    PlannerModel (extends             │   │
│  │     AbstractModel)                   │   │
│  │                                    │   │
│  │  ┌─────────┐ ┌─────────┐ ┌────────┐ │   │
│  │  │ Student │ │ Course  │ │  Task  │ │   │
│  │  │ Entity  │ │ Entity  │ │ Entity │ │   │
│  │  └─────────┘ └─────────┘ └────────┘ │   │
│  └────────────────────────────────────┘   │
│                                              │
└─────────────────────────────────────────────┘
```

**Characteristics**:
- Extends MVC framework
- UUID-based identity
- Business relationships
- Event notifications

### Layer 3: Service Layer (Business Logic)
```
┌─────────────────────────────────────────────┐
│       planner.service Package                │
│                                              │
│  ┌─────────────────────────────────────────┐  │
│  │      PlannerService                    │  │
│  │                                        │  │
│  │  ┌──────────────┐    ┌──────────────┐  │  │
│  │  │ Validation   │───▶│   CRUD       │  │  │
│  │  │   Rules      │    │ Operations   │  │  │
│  │  └──────────────┘    └──────────────┘  │  │
│  │                                        │  │
│  │  ┌──────────────┐    ┌──────────────┐  │  │
│  │  │   Queries    │    │  Business    │  │  │
│  │  │  (upcoming,  │    │   Logic      │  │  │
│  │  │  overdue)    │    │              │  │  │
│  │  └──────────────┘    └──────────────┘  │  │
│  └─────────────────────────────────────────┘  │
│                                              │
└─────────────────────────────────────────────┘
```

**Characteristics**:
- Stateless (only holds model reference)
- Validates business rules
- Coordinates complex operations
- Separates concerns from model

### Layer 4: UI Layer (Presentation)
```
┌─────────────────────────────────────────────┐
│          planner.ui Package                  │
│                                              │
│  ┌─────────────────────────────────────────┐  │
│  │         PlannerView                     │  │
│  │   (extends AbstractView)                │  │
│  │                                     │  │
│  │   Tab 1: Student Profile              │  │
│  │   Tab 2: Course Management            │  │
│  │   Tab 3: Task Management                │  │
│  └─────────────────────────────────────────┘  │
│              │                               │
│              │ delegates                     │
│              ▼                               │
│  ┌─────────────────────────────────────────┐  │
│  │      PlannerController                  │  │
│  │   (extends AbstractController)          │  │
│  │                                    │  │
│  │   Handles user actions                  │  │
│  │   Delegates to Service                  │  │
│  └─────────────────────────────────────────┘  │
│                                              │
└─────────────────────────────────────────────┘
```

**Characteristics**:
- Extends MVC framework
- Swing-based implementation
- Automatic updates via observer
- Event delegation pattern

### Layer 5: Persistence Layer (Data)
```
┌─────────────────────────────────────────────┐
│       planner.persistence Package            │
│                                              │
│  ┌─────────────────────────────────────────┐  │
│  │        PlannerRepository                │  │
│  │                                    │  │
│  │  Load ──▶ PlannerModel                │  │
│  │                              │        │  │
│  │  Save ◀── PlannerModel                │  │
│  │                                    │  │
│  │  Files:                               │  │
│  │  • planner_data/student.csv          │  │
│  │  • planner_data/courses.csv          │  │
│  │  • planner_data/tasks.csv            │  │
│  └─────────────────────────────────────────┘  │
│                                              │
└─────────────────────────────────────────────┘
```

**Characteristics**:
- CSV format for portability
- Human-readable
- Simple implementation
- Automatic on shutdown

---

## Data Flow Architecture

### MVC Interaction Flow (Add Task Example)

```
User Action → View → Controller → Service → Model → Observer → View Update

1. User clicks "Add Task" button
   ↓
2. View.actionPerformed() captures input
   ↓
3. Controller.addTask() validates format
   ↓
4. Service.addTask() validates business rules
   ↓
5. Model.addTask() stores task, fires ModelEvent
   ↓
6. AbstractModel.notifyChanged() notifies listeners
   ↓
7. View.modelChanged() receives event
   ↓
8. View.refreshTaskList() updates JList
   ↓
9. User sees updated task list
```

### Event Types

| Event Name | Fired When | UI Update |
|------------|-----------|-----------|
| `STUDENT_CHANGED` | Student profile updated | Refresh student info label |
| `COURSE_ADDED` | New course created | Refresh course list |
| `COURSE_REMOVED` | Course deleted | Refresh course list, remove related tasks |
| `COURSE_UPDATED` | Course modified | Refresh course list |
| `TASK_ADDED` | New task created | Refresh task list |
| `TASK_REMOVED` | Task deleted | Refresh task list |
| `TASK_UPDATED` | Task modified | Refresh task list |
| `TASK_COMPLETED` | Task marked done | Refresh task list (show as complete) |
| `TASK_INCOMPLETED` | Task marked not done | Refresh task list |
| `PLANNER_CLEARED` | All data cleared | Clear all displays |

---

## Design Patterns Used

| Pattern | Implementation | Purpose |
|---------|---------------|---------|
| **MVC** | Model-View-Controller architecture | Separation of concerns |
| **Observer** | ModelListener / ModelEvent | Automatic UI updates |
| **Abstract Factory** | AbstractModel, AbstractView, AbstractController | Reusable base classes |
| **Repository** | PlannerRepository | Data access abstraction |
| **Template Method** | Abstract base classes | Common algorithm skeleton |
| **Dependency Injection** | Main.java constructor wiring | Loose coupling |
| **Factory** | UUID generation | Unique identity creation |

---

## Development Checklist

### Phase 1: MVC Framework
- [ ] PROMPT-00: Understand architecture mental model
- [ ] PROMPT-A: Create core interfaces (Model, View, Controller)
- [ ] PROMPT-B: Implement observer pattern (ModelEvent, ModelListener)
- [ ] PROMPT-C: Create abstract base classes

### Phase 2: Domain Models
- [ ] PROMPT-D: Create entities (Student, Course, Task)
- [ ] PROMPT-D: Create PlannerModel extending AbstractModel
- [ ] PROMPT-E: Implement PlannerService with validation

### Phase 3: User Interface
- [ ] PROMPT-F: Create PlannerView with tabbed interface
- [ ] PROMPT-F: Implement modelChanged() for automatic updates
- [ ] PROMPT-F: Create PlannerController for event handling

### Phase 4: Infrastructure
- [ ] PROMPT-G: Implement PlannerRepository with CSV storage
- [ ] PROMPT-G: Add shutdown hook for auto-save
- [ ] PROMPT-H: Create Main.java with component wiring
- [ ] PROMPT-H: Resolve circular reference (View ↔ Controller)

### Phase 5: Documentation
- [ ] PROMPT-I: Create comprehensive README
- [ ] PROMPT-I: Write ARCHITECTURE_SUMMARY
- [ ] PROMPT-I: Configure Maven build
- [ ] PROMPT-J: Create diagram generation scripts

### Phase 6: UI Enhancements (Optional)
- [ ] PROMPT-K: Create Calendar View (Month view with tasks)
- [ ] PROMPT-K1: Create Event Entity (calendar events with start/end time)
- [ ] PROMPT-K2: Add Day/Week/Month calendar views (Outlook-style 24h timeline)
- [ ] PROMPT-K3: Create Study Timer (countdown with alerts)

---

## Verification Protocol

### Before Each Phase
1. Review previous phase verification (all items checked)
2. Understand current phase design rationale
3. Review code examples

### During Each Phase
1. Implement components in order specified
2. Compile after each file (ensure no errors)
3. Run verification tests

### After Each Phase
1. Complete all verification checkboxes
2. Test integration with previous phases
3. Document any deviations from specification

### Final Verification
```bash
# Build project
mvn clean compile

# Run application
mvn exec:java -Dexec.mainClass="planner.Main"

# Or run JAR
mvn clean package
java -jar target/student-planner-1.0.0.jar
```

**Success Criteria**:
- [ ] Application launches without errors
- [ ] Can create student profile
- [ ] Can add courses
- [ ] Can add tasks with course associations
- [ ] Can mark tasks complete/incomplete
- [ ] Data persists after restart
- [ ] UI updates automatically when data changes
- [ ] All diagrams generate successfully
- [ ] Documentation is complete and accurate

---

## Quick Reference

### Building the Project
```bash
# Compile
mvn clean compile

# Run
mvn exec:java -Dexec.mainClass="planner.Main"

# Package as JAR
mvn clean package

# Run JAR
java -jar target/student-planner-1.0.0.jar
```

### Generating Diagrams
```bash
# Generate diagrams (text format)
python3 generate_diagrams.py

# Generate PNG images (requires PlantUML)
./generate_png.sh
```

### Project Stats
- **Lines of Code**: ~2000 (Java)
- **Architecture**: MVC with Observer
- **UI Framework**: Java Swing
- **Persistence**: CSV files
- **Dependencies**: 0 (pure Java standard library)

---

## Future Enhancement Prompts (Potential)

- **PROMPT-K**: Calendar Integration (due date visualization)
- **PROMPT-L**: Grade Tracking (GPA calculation)
- **PROMPT-M**: Notification System (deadline reminders)
- **PROMPT-N**: Export/Import (JSON/Excel support)
- **PROMPT-O**: Cloud Sync (database integration)
- **PROMPT-P**: Web UI (Spring Boot + Thymeleaf)
- **PROMPT-Q**: Mobile App (React Native or Flutter)

---

## Resources

- **Individual Prompts**: See `prompts/` folder
- **Architecture Details**: See `ARCHITECTURE_SUMMARY.md`
- **User Guide**: See `README.md`
- **Diagrams**: See `diagrams/` folder (auto-generated)
- **Build Config**: See `pom.xml`

---

**Last Updated**: 2026-04-21  
**Version**: 1.0  
**Maintained By**: Development Team  
**License**: Educational Example
