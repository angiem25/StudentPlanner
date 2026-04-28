# Student Planner Development Prompts

This folder contains the modular development prompts used to create the Student Planner application.

## Module of Thought Development

Each prompt represents a self-contained development phase with:
- **Clear objectives** - What to build
- **Dependencies** - What must be completed first
- **Design rationale** - Why to build it this way
- **Verification criteria** - How to verify it's correct
- **Code examples** - What the result should look like

## Prompt Sequence

Execute prompts in order. Each builds upon the previous:

### Phase 1: MVC Framework (Reusable Infrastructure)

| Prompt | Title | Files Created | Purpose |
|--------|-------|---------------|---------|
| [PROMPT-00](PROMPT-00-ARCHITECTURE-OVERVIEW.md) | Architecture Overview | - | Establish mental model and design principles |
| [PROMPT-A](PROMPT-A-CORE-MVC-CONTRACTS.md) | Core MVC Contracts | 3 interfaces | Define minimal MVC interfaces |
| [PROMPT-B](PROMPT-B-EVENT-INFRASTRUCTURE.md) | Event Infrastructure | 2 classes | Implement observer pattern |
| [PROMPT-C](PROMPT-C-BASE-MVC-CLASSES.md) | Base MVC Classes | 3 abstract classes | Provide common functionality |

**Outcome**: Reusable MVC framework that knows nothing about planners

### Phase 2: Application Domain

| Prompt | Title | Files Created | Purpose |
|--------|-------|---------------|---------|
| [PROMPT-D](PROMPT-D-DOMAIN-MODELS.md) | Domain Models | 4 classes (Student, Course, Task, PlannerModel) | Business entities |
| [PROMPT-E](PROMPT-E-SERVICE-LAYER.md) | Service Layer | 1 class (PlannerService) | Business logic and validation |

**Outcome**: Complete domain model with business rules

### Phase 3: Presentation Layer

| Prompt | Title | Files Created | Purpose |
|--------|-------|---------------|---------|
| [PROMPT-F](PROMPT-F-USER-INTERFACE.md) | User Interface | 2 classes (PlannerView, PlannerController) | Swing GUI implementation |

**Outcome**: Working GUI with automatic model updates

### Phase 4: Infrastructure

| Prompt | Title | Files Created | Purpose |
|--------|-------|---------------|---------|
| [PROMPT-G](PROMPT-G-PERSISTENCE-LAYER.md) | Persistence Layer | 1 class (PlannerRepository) | File-based data storage |
| [PROMPT-H](PROMPT-H-MAIN-APPLICATION.md) | Main Application | 1 class (Main.java) | Application wiring and startup |

**Outcome**: Data persistence and application bootstrap

### Phase 5: Documentation & Tooling

| Prompt | Title | Files Created | Purpose |
|--------|-------|---------------|---------|
| [PROMPT-I](PROMPT-I-DOCUMENTATION.md) | Documentation | README.md, ARCHITECTURE_SUMMARY.md, pom.xml | Project documentation |
| [PROMPT-J](PROMPT-J-DIAGRAM-GENERATION.md) | Diagram Generation | generate_diagrams.py, generate_png.sh | Auto-generate diagrams |

**Outcome**: Complete project with documentation and tooling

### Phase 6: UI Enhancements (Optional Features)

| Order | Prompt | Title | Files | Purpose |
|-------|--------|-------|-------|---------|
| 12 | [PROMPT-K](PROMPT-K-CALENDAR-VIEW.md) | Calendar View | 1 file | Monthly calendar grid with tasks |
| 13 | [PROMPT-K1](PROMPT-K1-EVENT-ENTITY.md) | Event Entity | 1 file | Calendar Event domain model |
| 14 | [PROMPT-K2](PROMPT-K2-CALENDAR-VIEWS.md) | Calendar Day/Week/Month | 1 file | Multi-view calendar (Outlook-style) |
| 15 | [PROMPT-K3](PROMPT-K3-TIMER.md) | Study Timer | 1 file | Countdown timer with alerts |
| 16 | [PROMPT-K4](PROMPT-K4-TASK-COLOR-PALETTE.md) | Task Color Palette | 1 file | Color-coded tasks with persistence |
| 17 | [PROMPT-K5](PROMPT-K5-TIMER-PRESETS.md) | Timer Presets | 1 file | One-click timer durations |
| 18 | [PROMPT-K6](PROMPT-K6-DUE-DATE-REMINDER-POPUPS.md) | Due Date Reminder Popups | 1 file | Daily reminders from 7 days before due |

**Outcome**: Enhanced UI with calendar, events, timer, task colors, presets, and reminder popups

## Total Files Created

**Source Code**:
- 8 MVC framework files (interfaces + abstract classes)
- 5 domain model files (Student, Course, Task, Event, PlannerModel)
- 1 service layer file
- 4 UI layer files (view, controller, calendar, timer)
- 1 persistence layer file
- 1 main application file

**Total: 19 Java files**

**Documentation & Configuration**:
- 17 prompt files (this documentation)
- 2 project documentation files (README, ARCHITECTURE_SUMMARY)
- 1 build configuration (pom.xml)
- 2 diagram generation scripts

## Development Flow

```
┌─────────────────────────────────────────────────────────────┐
│  PHASE 1: MVC Framework (Reusable)                             │
│  ┌─────────────────────────────────────────────────────────┐ │
│  │ PROMPT-00: Mental Model                                  │ │
│  │ PROMPT-A: Core Interfaces (Model, View, Controller)     │ │
│  │ PROMPT-B: Observer Pattern (ModelEvent, ModelListener)  │ │
│  │ PROMPT-C: Abstract Base Classes                         │ │
│  └─────────────────────────────────────────────────────────┘ │
└─────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────┐
│  PHASE 2: Domain Models (Planner-Specific)                    │
│  ┌─────────────────────────────────────────────────────────┐ │
│  │ PROMPT-D: Entities (Student, Course, Task)              │ │
│  │ PROMPT-E: Business Logic (PlannerService)             │ │
│  └─────────────────────────────────────────────────────────┘ │
└─────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────┐
│  PHASE 3: User Interface (Swing)                             │
│  ┌─────────────────────────────────────────────────────────┐ │
│  │ PROMPT-F: View and Controller (PlannerView, Controller)│ │
│  └─────────────────────────────────────────────────────────┘ │
└─────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────┐
│  PHASE 4: Infrastructure & Integration                       │
│  ┌─────────────────────────────────────────────────────────┐ │
│  │ PROMPT-G: Data Persistence (PlannerRepository)          │ │
│  │ PROMPT-H: Application Entry Point (Main)              │ │
│  └─────────────────────────────────────────────────────────┘ │
└─────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────┐
│  PHASE 5: Documentation & Tooling                            │
│  ┌─────────────────────────────────────────────────────────┐ │
│  │ PROMPT-I: Documentation                                  │ │
│  │ PROMPT-J: Diagram Generation Scripts                    │ │
│  └─────────────────────────────────────────────────────────┘ │
└─────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────┐
│  PHASE 6: UI Enhancements (Optional)                        │
│  ┌─────────────────────────────────────────────────────────┐ │
│  │ PROMPT-K: Calendar View                                  │ │
│  └─────────────────────────────────────────────────────────┘ │
└─────────────────────────────────────────────────────────────┘
```

## Key Architectural Decisions Documented

1. **MVC with Observer Pattern**: Automatic UI updates when model changes
2. **Layered Architecture**: Framework → Domain → Service → UI → Persistence
3. **Abstract Base Classes**: Common functionality in base classes, specifics in subclasses
4. **UUID Identity**: Immutable identifiers for entities
5. **CSV Persistence**: Human-readable, portable data storage
6. **Service Layer**: Business logic separated from models and controllers
7. **Dependency Injection**: Components wired together in Main.java

## How to Use These Prompts

### For Learning
1. Read prompts in sequence
2. Understand the design rationale before implementation
3. Review the verification criteria
4. Study the code examples

### For Development
1. Start with PROMPT-00 for architectural overview
2. Follow prompts sequentially
3. Complete verification before moving to next prompt
4. Use prompts as specification for each component

### For Teaching
1. Each prompt represents one lesson/module
2. Verification criteria serve as assessment rubrics
3. Code examples show expected outcomes
4. Dependencies show prerequisite knowledge

## Verification Protocol

Each prompt includes verification criteria. **DO NOT PROCEED** to next prompt until:
- All checkboxes in verification section are checked
- Code compiles without errors
- Design principles are understood

## Future Enhancement Prompts (Potential)

Additional prompts for extended functionality:

- ~~PROMPT-K: Calendar Integration~~ ✅ **COMPLETED**
- ~~PROMPT-K4: Task Color Palette~~ ✅ **COMPLETED**
- ~~PROMPT-K5: Timer Presets~~ ✅ **COMPLETED**
- ~~PROMPT-K6: Due Date Reminder Popups~~ ✅ **COMPLETED**
- PROMPT-L: Grade Tracking (GPA calculation)
- PROMPT-M: Notification System (deadline reminders)
- PROMPT-N: Export/Import Functionality (JSON/Excel)
- PROMPT-O: Cloud Synchronization (database)
- PROMPT-P: Mobile Application (React Native/Flutter)
- PROMPT-Q: Web UI (Spring Boot + Thymeleaf)

## Design Patterns Used

| Pattern | Where Used | Purpose |
|---------|-----------|---------|
| MVC | Overall architecture | Separation of concerns |
| Observer | Model → View updates | Automatic UI refresh |
| Abstract Factory | Base classes | Common functionality |
| Repository | Persistence layer | Data access abstraction |
| Template Method | Abstract classes | Algorithm skeletons |
| Dependency Injection | Main.java | Component wiring |

---

**Generated**: Part of Student Planner Project  
**Architecture**: Modular MVC with Observer Pattern  
**Language**: Java 17+ with Swing UI
