# Student Planner - System Architecture & Algorithm Documentation

## **System Architecture Overview**

The Student Planner follows a **modular MVC (Model-View-Controller)** architecture with clear separation of concerns across six distinct layers:

### **1. Core MVC Framework (Infrastructure Layer)**
- **AbstractModel**: Base class implementing the Observer pattern with listener management
- **AbstractView**: Base view with automatic model listener registration
- **AbstractController**: Base controller with reference management
- **ModelEvent/ModelListener**: Event-driven communication system

### **2. Domain Model Layer**
- **Student**: Student profile with academic year enumeration and validation
- **Course**: Course entities with unique code validation
- **Task**: Task entities with priority enumeration, due dates, and color accents
- **Event**: Calendar events with time range management
- **PlannerModel**: Central model coordinating all domain entities

### **3. Service Layer (Business Logic)**
- **PlannerService**: Encapsulates validation rules, business logic, and data operations
- Handles complex operations like duplicate checking and data integrity

### **4. Persistence Layer**
- **PlannerRepository**: File-based CSV storage with profile management
- Supports multi-profile data isolation and automatic data directory creation

### **5. User Interface Layer**
- **PlannerView**: Main tabbed interface with automatic model updates
- **CalendarView**: Multi-view calendar (Day/Week/Month) with timeline rendering
- **TimerPanel**: Countdown timer with preset functionality and audio alerts

### **6. Application Entry Point**
- **Main**: Dependency injection and application lifecycle management

## **Critical Implemented Algorithms**

### **1. Observer Pattern Implementation**
```java
// Event-driven UI updates
fireModelChanged("TASK_ADDED", task);
// Automatic listener registration in AbstractView
// Real-time UI synchronization across all components
```

### **2. Profile-Based Data Isolation Algorithm**
The repository implements a sophisticated profile management system:
- **Profile Prefix Generation**: `studentId + "_" + firstName + "_" + lastName`
- **Data Isolation**: Separate CSV files per profile (`{prefix}_courses.csv`, `{prefix}_tasks.csv`)
- **Automatic Profile Loading**: Preference-based profile selection with fallback to first available
- **Data Migration**: Seamless switching between profiles with isolated data sets

### **3. Calendar Rendering Algorithm**
**CalendarView** implements complex calendar logic:
- **Month Grid Calculation**: 6x7 grid generation with proper date placement
- **Timeline Rendering**: 24-hour timeline with configurable hour height (50px)
- **Event Overlap Detection**: Time-based event positioning in day/week views
- **Date Navigation**: YearMonth-based navigation with boundary checking

### **4. Timer State Management Algorithm**
**TimerPanel** implements a robust countdown system:
- **State Machine**: Idle → Running → Paused → Complete transitions
- **Preset Management**: 30/60/90 minute presets with running-session guards
- **Audio Alert System**: WAV file playback on timer completion
- **Visual State Indication**: Color-coded display based on timer state

### **5. Task Color Palette System**
**TaskPalette** provides canonical color management:
- **Color Canonicalization**: Hex color validation and normalization
- **Default Color Fallback**: Red (`#E53935`) as default accent color
- **Consistent Rendering**: Color synchronization across task list and calendar views

### **6. Due Date Reminder Algorithm**
**PlannerView** implements intelligent reminder scheduling:
- **Daily Reminder Cadence**: Popups starting 7 days before due date
- **Duplicate Prevention**: Task+due+date key tracking to prevent spam
- **Completion Filtering**: Automatic exclusion of completed tasks
- **Dynamic Status Display**: "Due in X days", "Due tomorrow", "Due today"

### **7. Weekly Priority Algorithm**
Dedicated weekly tab with intelligent task filtering:
- **Date Range Calculation**: Current week boundaries (Sunday-Saturday)
- **Priority Filtering**: High-priority tasks due within the week
- **Checkbox State Management**: Direct completion toggling with model synchronization
- **Real-time Updates**: Automatic checklist refresh when tasks change elsewhere

### **8. CSV Persistence Algorithm**
**PlannerRepository** implements robust file-based storage:
- **CSV Escaping**: Proper handling of special characters in data fields
- **Atomic Operations**: File writing with error handling and rollback
- **Backward Compatibility**: Graceful handling of missing fields during data loading
- **Directory Management**: Automatic creation of data directories and profile subdirectories

## **Data Flow Architecture**

```
User Action → View → Controller → Service → Model → Observer Notification → View Update
```

**Example: Task Addition Flow:**
1. **View**: User fills task form and clicks "Add Task"
2. **Controller**: Receives action, validates input format
3. **Service**: Validates business rules, creates Task entity with canonical color
4. **Model**: Adds task to collection, fires ModelEvent
5. **Observer**: View receives event, updates task list display
6. **Persistence**: Repository saves data to profile-specific CSV file

## **Key Design Patterns**

1. **Observer Pattern**: Automatic UI updates via ModelListener interface
2. **Repository Pattern**: Abstracted data access layer
3. **Service Pattern**: Business logic encapsulation
4. **Factory Pattern**: View component creation
5. **Strategy Pattern**: Different calendar view types (Day/Week/Month)

## **Complex Algorithm Characteristics**

The system implements several sophisticated algorithms that are critical for its behavior:

- **Multi-threaded Timer**: Swing Timer integration with EDT-safe UI updates
- **Date Arithmetic**: Complex date calculations for calendar rendering and reminder scheduling
- **Color Management**: Hex color parsing and canonicalization
- **File I/O with Error Recovery**: Robust CSV parsing with graceful degradation
- **State Management**: Complex UI state synchronization across multiple tabs and components

## **Technology Stack**

### **Core Technologies**
- **Java 21** - Primary programming language
- **Java Swing** - GUI framework for the desktop application
- **Gradle** - Build tool and dependency management

### **Key Dependencies**
- **FlatLaf 3.4.1** - Modern Look and Feel for Swing applications
- **JUnit Jupiter 5.9.2** - Unit testing framework
- **Mockito 5.1.1** - Mocking framework for testing

### **Data Storage**
- **CSV files** - File-based persistence for student, course, and task data
- **Local file system** - Data stored in `planner_data` directory

## **Architecture Benefits**

### **1. Maintainability**
- Each module has clear boundaries
- Changes in one module don't affect others
- Easy to locate and fix issues

### **2. Testability**
- Models can be tested independently
- Service layer can be unit tested with mocks
- UI components can be tested with stub controllers

### **3. Extensibility**
- New entity types can be added to domain model
- Additional views can be created for different use cases
- New persistence mechanisms can be implemented

### **4. Reusability**
- MVC framework can be used for other applications
- Service layer can be exposed via different interfaces
- Persistence layer can be swapped for databases

## **Error Handling Strategy**

### **Validation Layers**
1. **UI Layer**: Basic format validation (dates, numbers)
2. **Service Layer**: Business rule validation (duplicates, ranges)
3. **Model Layer**: Data integrity validation (null checks, constraints)

### **Error Propagation**
```
Service Exception → Controller → View → User (Dialog)
```

## **Future Enhancement Points**

### **Architectural Extensions**
- **Repository Pattern**: Abstract persistence for database support
- **Command Pattern**: Undo/redo functionality
- **Factory Pattern**: Different view types (web, mobile)
- **Observer Extensions**: Real-time synchronization

### **Feature Extensions**
- **Palette Extension**: User-defined custom hex colors and saved favorites
- **Timer Session Analytics**: Track completed study sessions and streaks
- **Grade Module**: GPA calculation and tracking
- **Notification Module**: Deadline reminders
- **Export Module**: Data export in multiple formats

---

This architecture demonstrates enterprise-level software design with proper separation of concerns, comprehensive error handling, and extensible modular structure.

*Generated with [Devin](https://cli.devin.ai/docs)*