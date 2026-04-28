# Student Planner - Modular Architecture & Thought Process

## Overview

This document summarizes the modular MVC-based student planner application, demonstrating a structured approach to software design using the "module of thought" methodology.

## Architectural Philosophy

### Core Principles
1. **Separation of Concerns**: Each module has a single, well-defined responsibility
2. **Observer Pattern**: Automatic UI updates when model data changes
3. **Modularity**: Components can be developed, tested, and maintained independently
4. **Extensibility**: New features can be added without modifying existing code
5. **Testability**: Each layer can be unit tested in isolation

### Thought Process Flow

```
1. ARCHITECTURE DESIGN
   ↓
2. CORE MVC FRAMEWORK (Reusable)
   ↓
3. DOMAIN MODELS (Business Entities)
   ↓
4. SERVICE LAYER (Business Logic)
   ↓
5. USER INTERFACE (Presentation)
   ↓
6. PERSISTENCE LAYER (Data Storage)
   ↓
7. APPLICATION ENTRY POINT (Integration)
   ↓
8. DOCUMENTATION (Complete Package)
```

## Module Breakdown

### Phase 1: Core MVC Framework (Reusable Infrastructure)

**Thought**: Create framework components that know nothing about planners - pure MVC infrastructure.

#### Files Created:
- `mvc/Model.java` - Core model interface with notification contract
- `mvc/View.java` - Core view interface with model/controller references
- `mvc/Controller.java` - Core controller interface with model/view references
- `mvc/ModelEvent.java` - Event objects encapsulating model changes
- `mvc/ModelListener.java` - Observer interface for model change notifications
- `mvc/AbstractModel.java` - Base model with listener management
- `mvc/AbstractView.java` - Base view with automatic listener registration
- `mvc/AbstractController.java` - Base controller with reference management

**Key Design Decisions**:
- Framework is completely domain-agnostic
- Observer pattern built into core infrastructure
- Automatic listener registration in AbstractView
- Convenience methods for event firing in AbstractModel

### Phase 2: Domain Models (Business Entities)

**Thought**: Define the core business entities that represent the student planner domain.

#### Files Created:
- `planner/model/Student.java` - Student profile entity
- `planner/model/Course.java` - Course entity with academic details
- `planner/model/Task.java` - Task entity with priority and due date
- `planner/model/PlannerModel.java` - Main model implementing AbstractModel

**Key Design Decisions**:
- UUID-based entity identification
- Immutable IDs with mutable business data
- Proper equals/hashCode implementations
- Builder-style constructors with sensible defaults
- Priority enum for task categorization

### Phase 3: Service Layer (Business Logic)

**Thought**: Encapsulate business logic and validation rules separate from models.

#### Files Created:
- `planner/service/PlannerService.java` - Business logic and validation

**Key Design Decisions**:
- Centralized validation logic
- Comprehensive error handling
- Business rule enforcement (e.g., duplicate course codes)
- Query methods for common use cases (upcoming tasks, overdue tasks)
- Clear separation between validation and persistence

### Phase 4: User Interface (Presentation Layer)

**Thought**: Create a Swing-based UI that automatically responds to model changes.

#### Files Created:
- `planner/ui/PlannerView.java` - Main view with tabbed interface
- `planner/ui/PlannerController.java` - Event handling and coordination

**Key Design Decisions**:
- Tabbed interface for logical organization
- Automatic UI updates via ModelListener
- Form validation and user feedback
- Component reuse and consistent layout
- Event delegation to controller layer
- Theme-aware visual cues (task accents and timer state colors)

### Phase 5: Persistence Layer (Data Storage)

**Thought**: Implement file-based persistence that's simple and portable.

#### Files Created:
- `planner/persistence/PlannerRepository.java` - CSV-based data storage

**Key Design Decisions**:
- CSV format for human readability and portability
- Automatic directory creation
- Proper CSV escaping for special characters
- Graceful handling of missing files
- Atomic operations for data integrity

### Phase 6: Application Entry Point (Integration)

**Thought**: Wire all components together and handle application lifecycle.

#### Files Created:
- `planner/Main.java` - Application bootstrap and MVC wiring
- `pom.xml` - Maven build configuration

**Key Design Decisions**:
- Dependency injection in main method
- Automatic data loading on startup
- Graceful shutdown with data saving
- System look and feel for native appearance
- Executable JAR creation

## Data Flow Architecture

### User Interaction Flow
```
User Action → View → Controller → Service → Model → Observer Notification → View Update
```

### Example: Adding a Task
1. **View**: User fills task form and clicks "Add Task"
2. **Controller**: Receives action, validates input format
3. **Service**: Validates business rules, creates Task entity
4. **Model**: Adds task to collection, fires ModelEvent
5. **Observer**: View receives event, updates task list display

### Model Event Types
- `STUDENT_CHANGED` - Student profile updated
- `COURSE_ADDED/UPDATED/REMOVED` - Course operations
- `TASK_ADDED/UPDATED/REMOVED/COMPLETED/INCOMPLETED` - Task operations
- `PLANNER_CLEARED` - All data cleared

## Modular Benefits

### 1. Maintainability
- Each module has clear boundaries
- Changes in one module don't affect others
- Easy to locate and fix issues

### 2. Testability
- Models can be tested independently
- Service layer can be unit tested with mocks
- UI components can be tested with stub controllers

### 3. Extensibility
- New entity types can be added to domain model
- Additional views can be created for different use cases
- New persistence mechanisms can be implemented

### 4. Reusability
- MVC framework can be used for other applications
- Service layer can be exposed via different interfaces
- Persistence layer can be swapped for databases

## Error Handling Strategy

### Validation Layers
1. **UI Layer**: Basic format validation (dates, numbers)
2. **Service Layer**: Business rule validation (duplicates, ranges)
3. **Model Layer**: Data integrity validation (null checks, constraints)

### Error Propagation
```
Service Exception → Controller → View → User (Dialog)
```

## Future Enhancement Points

### Architectural Extensions
- **Repository Pattern**: Abstract persistence for database support
- **Command Pattern**: Undo/redo functionality
- **Factory Pattern**: Different view types (web, mobile)
- **Observer Extensions**: Real-time synchronization

### Feature Extensions
- **Palette Extension**: User-defined custom hex colors and saved favorites
- **Timer Session Analytics**: Track completed study sessions and streaks
- **Grade Module**: GPA calculation and tracking
- **Notification Module**: Deadline reminders
- **Export Module**: Data export in multiple formats

## Recent UI Enhancements

### Task Color Palette (K-4)
- Added `Task.accentColorHex` with default red (`#E53935`)
- Added model-level `TaskPalette` for canonical allowed values
- Persisted color into `tasks.csv` with backward-compatible loading
- Rendered accent color consistently in task list and calendar chips

### Timer Presets (K-5)
- Added one-click preset buttons in `TimerPanel`: 30, 60, 90 minutes
- Presets update input and display immediately while timer is idle
- Running-session guard prevents preset changes mid-countdown

### Due Date Reminder Popups (K-6)
- Added scheduled popup reminders in `PlannerView` for incomplete tasks
- Reminder cadence is daily, starting 7 days before due date
- Reminder keys are tracked by task+due+date to prevent duplicate popups
- Completed tasks are excluded from reminder checks automatically

## Design Patterns Used

1. **Model-View-Controller**: Overall architectural pattern
2. **Observer**: Model change notifications
3. **Abstract Factory**: MVC component creation
4. **Repository**: Data access abstraction
5. **Strategy**: Different persistence mechanisms
6. **Template Method**: Abstract base classes

## Lessons Learned

### What Worked Well
- Clear separation made development straightforward
- Observer pattern eliminated manual UI refresh calls
- Modular structure enabled parallel development
- CSV persistence made debugging easy

### Potential Improvements
- Consider dependency injection framework for larger applications
- Add comprehensive logging throughout layers
- Implement proper exception hierarchy
- Add configuration management for file locations

## Conclusion

This modular architecture demonstrates how the "module of thought" approach leads to maintainable, extensible software. By breaking the problem into logical layers and maintaining clear boundaries, we create a system that's both robust and flexible.

The MVC framework created here can serve as a foundation for future applications, while the specific planner implementation showcases how to apply these patterns to real-world problems.
