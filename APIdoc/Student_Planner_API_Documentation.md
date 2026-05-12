# Student Planner API Documentation

## Overview

This document provides comprehensive API documentation for the Student Planner application, generated from Javadoc and converted to Markdown format for easy reading and reference.

## Table of Contents

1. [Core MVC Framework](#core-mvc-framework)
2. [Application Package](#application-package)
3. [Class Index](#class-index)
4. [Package Index](#package-index)

---

## Core MVC Framework

The MVC framework provides the foundational architecture for the application.

### Model Interfaces

#### Model
The core model interface that defines the contract for all model components.

#### ModelListener
Interface for objects that want to receive notifications when model data changes.

#### ModelEvent
Encapsulates information about model changes, including the type of change and affected data.

### Abstract Classes

#### AbstractModel
Base implementation of the Model interface that provides:
- Listener management functionality
- Event firing capabilities
- Observer pattern implementation

Key methods:
- `addModelListener(ModelListener listener)` - Register a listener
- `removeModelListener(ModelListener listener)` - Unregister a listener
- `fireModelChanged(String eventType, Object data)` - Notify listeners of changes

#### AbstractView
Base implementation of the View interface that provides:
- Automatic model listener registration
- Model and controller reference management

#### AbstractController
Base implementation of the Controller interface that provides:
- Model and view reference management
- Basic controller functionality

### View and Controller Interfaces

#### View
Interface defining the contract for view components, including methods for:
- Model and controller access
- UI update notifications

#### Controller
Interface defining the contract for controller components, including methods for:
- Model and view access
- Event handling coordination

---

## Application Package

The `planner` package contains the main application components.

### Main Application

#### Main
The main entry point of the Student Planner application.

Key responsibilities:
- Application bootstrap and initialization
- MVC component wiring
- Application lifecycle management

### Domain Models

#### Student
Represents a student profile with academic information.

Key properties:
- `firstName`, `lastName` - Student name
- `email` - Contact email
- `studentId` - Unique identifier
- `academicYear` - Academic year enumeration
- `major` - Field of study

#### Course
Represents an academic course with instructor and credit information.

Key properties:
- `name` - Course name
- `code` - Unique course code
- `instructor` - Course instructor
- `credits` - Credit hours

#### Task
Represents a task with priority, due date, and color coding.

Key properties:
- `title` - Task title
- `description` - Detailed description
- `dueDate` - Due date and time
- `priority` - Priority level (LOW, MEDIUM, HIGH)
- `courseId` - Associated course identifier
- `accentColorHex` - Color hex code for visual identification

Key methods:
- `complete()` - Mark task as completed
- `incomplete()` - Mark task as incomplete
- `isCompleted()` - Check completion status

#### Event
Represents calendar events with time ranges.

Key properties:
- `title` - Event title
- `description` - Event description
- `startDateTime` - Event start time
- `endDateTime` - Event end time

#### PlannerModel
Central model that coordinates all domain entities.

Key responsibilities:
- Managing collections of students, courses, tasks, and events
- Providing query methods for data access
- Firing model change notifications

Key methods:
- `addCourse(Course course)` - Add a new course
- `addTask(Task task)` - Add a new task
- `getTasksForToday()` - Get tasks due today
- `getIncompleteTasks()` - Get incomplete tasks
- `completeTask(Task task)` - Mark task as complete

#### TaskPalette
Manages color palettes for task visualization.

Key functionality:
- Color canonicalization and validation
- Default color management
- Color consistency across views

### Service Layer

#### PlannerService
Encapsulates business logic and validation rules.

Key responsibilities:
- Data validation and business rule enforcement
- Complex operation coordination
- Error handling and user feedback

Key methods:
- `createStudentProfile(...)` - Create and validate student profile
- `addCourse(...)` - Add course with duplicate checking
- `addTask(...)` - Add task with validation
- `updateTask(...)` - Update existing task
- `removeTask(...)` - Remove task and associated data

### User Interface Layer

#### PlannerView
Main view component with tabbed interface.

Key features:
- Tab-based organization (Profile, Courses, Tasks, Calendar, Timer, Weekly)
- Automatic UI updates via model listener
- Form validation and user feedback
- Theme-aware visual design

#### PlannerController
Main controller coordinating user interactions.

Key responsibilities:
- Event handling and user input processing
- View-model coordination
- Business logic invocation
- Error handling and user notifications

#### CalendarView
Multi-view calendar component (Day/Week/Month).

Key features:
- 24-hour timeline rendering
- Event overlap detection
- Date navigation
- Task integration with color coding

#### TimerPanel
Study timer with preset functionality.

Key features:
- Countdown timer with visual feedback
- Preset buttons (30/60/90 minutes)
- Audio alerts on completion
- State management (idle/running/paused)

#### AppTheme
Centralized theme management for consistent visual design.

### Persistence Layer

#### PlannerRepository
File-based data storage with CSV format.

Key responsibilities:
- Profile-based data isolation
- CSV file reading and writing
- Data directory management
- Error handling and recovery

Key methods:
- `savePlannerData(PlannerModel model)` - Save all data
- `loadPlannerData(PlannerModel model)` - Load data into model
- `saveProfile(Student student)` - Save student profile
- `loadProfile(String profileName)` - Load specific profile

---

## Class Index

### Core MVC Classes
- `AbstractModel` - Base model with observer support
- `AbstractView` - Base view with automatic listener registration
- `AbstractController` - Base controller implementation
- `Model` - Model interface
- `View` - View interface
- `Controller` - Controller interface
- `ModelListener` - Observer interface
- `ModelEvent` - Event objects for model changes

### Domain Model Classes
- `Student` - Student profile entity
- `Course` - Course entity
- `Task` - Task entity with priority and color
- `Event` - Calendar event entity
- `PlannerModel` - Main model implementation
- `TaskPalette` - Color management system

### Service Classes
- `PlannerService` - Business logic and validation

### UI Classes
- `PlannerView` - Main tabbed interface
- `PlannerController` - Event handling coordination
- `CalendarView` - Multi-view calendar
- `TimerPanel` - Study timer component
- `AppTheme` - Theme management

### Persistence Classes
- `PlannerRepository` - File-based data storage

### Application Classes
- `Main` - Application entry point

---

## Package Index

### mvc Package
Contains the core MVC framework components that are reusable across different applications.

**Classes:**
- Abstract classes providing base functionality
- Interface definitions for MVC contracts
- Event and listener support classes

### planner Package
Contains the main application-specific components.

**Subpackages:**
- `planner.model` - Domain entities and main model
- `planner.service` - Business logic layer
- `planner.ui` - User interface components
- `planner.persistence` - Data storage layer
- `planner.ui.calendar` - Calendar-specific UI
- `planner.ui.timer` - Timer-specific UI

---

## Key Design Patterns

### Observer Pattern
- **Implementation**: ModelListener interface and AbstractModel
- **Purpose**: Automatic UI updates when model data changes
- **Usage**: All views register as listeners to model changes

### Repository Pattern
- **Implementation**: PlannerRepository class
- **Purpose**: Abstract data access layer
- **Usage**: All data operations go through repository

### Service Pattern
- **Implementation**: PlannerService class
- **Purpose**: Business logic encapsulation
- **Usage**: Controllers use service for all business operations

### MVC Pattern
- **Implementation**: Complete framework with abstract classes
- **Purpose**: Separation of concerns
- **Usage**: Throughout the application architecture

---

## Data Flow

```
User Action → View → Controller → Service → Model → Observer Notification → View Update
```

### Example: Adding a Task
1. User fills task form in PlannerView
2. PlannerController receives action and validates input
3. PlannerService validates business rules and creates Task
4. PlannerModel adds task and fires ModelEvent
5. Observer pattern notifies all registered views
6. UI components update automatically

---

## Error Handling

### Validation Layers
1. **UI Layer**: Basic format validation
2. **Service Layer**: Business rule validation
3. **Model Layer**: Data integrity validation

### Error Propagation
```
Service Exception → Controller → View → User (Dialog)
```

---

## Thread Safety

### EDT (Event Dispatch Thread)
- All UI updates occur on the EDT
- Timer operations use Swing Timer for thread safety
- Model events are fired from appropriate threads

### Data Synchronization
- Collections use defensive copying
- Model operations are atomic where possible
- Repository operations handle concurrent access

---

## Extension Points

### Adding New Entity Types
1. Create domain model class extending appropriate patterns
2. Add service methods in PlannerService
3. Update repository for persistence
4. Add UI components as needed

### Adding New Views
1. Extend AbstractView or implement View interface
2. Create corresponding controller
3. Register as model listener
4. Add to main application

### Changing Persistence
1. Implement new repository class
2. Maintain same interface as PlannerRepository
3. Update dependency injection in Main

---

*Generated from Javadoc API documentation*
*Generated with [Devin](https://cli.devin.ai/docs)*