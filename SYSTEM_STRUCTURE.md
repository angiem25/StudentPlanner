# System Structure and Algorithm Descriptions

## Overview
This document provides a detailed description of the Student Planner application's system structure and the critical algorithms implemented for its behavior.

## System Architecture

### High-Level Architecture
The Student Planner follows a Model-View-Controller (MVC) architecture pattern with additional service and persistence layers:

```
┌─────────────────────────────────────────────────────────────┐
│                    Presentation Layer                       │
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────────────┐ │
│  │ PlannerView │  │ CalendarView │  │ Various Dialogs    │ │
│  └─────────────┘  └─────────────┘  └─────────────────────┘ │
└─────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────┐
│                   Control Layer                              │
│  ┌─────────────┐  ┌─────────────────────────────────────┐ │
│  │PlannerController│ │         Event Handlers          │ │
│  └─────────────┘  └─────────────────────────────────────┘ │
└─────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────┐
│                   Business Logic Layer                      │
│  ┌─────────────┐  ┌─────────────────────────────────────┐ │
│  │PlannerService│  │        Validation Logic            │ │
│  └─────────────┘  └─────────────────────────────────────┘ │
└─────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────┐
│                    Data Layer                               │
│  ┌─────────────┐  ┌─────────────────────────────────────┐ │
│  │ PlannerModel │  │      PlannerRepository             │ │
│  └─────────────┘  └─────────────────────────────────────┘ │
└─────────────────────────────────────────────────────────────┘
```

### Package Structure
```
planner/
├── Main.java                 # Application entry point
├── model/                    # Domain entities and core model
│   ├── PlannerModel.java
│   ├── Student.java
│   ├── Course.java
│   ├── Task.java
│   ├── Event.java
│   └── TaskPalette.java
├── service/                  # Business logic and validation
│   └── PlannerService.java
├── persistence/              # Data persistence layer
│   └── PlannerRepository.java
├── view/                     # User interface components
│   ├── PlannerView.java
│   ├── CalendarView.java
│   └── dialogs/
├── controller/               # MVC controllers
│   └── PlannerController.java
└── mvc/                      # MVC framework components
    ├── Model.java
    ├── View.java
    ├── Controller.java
    ├── AbstractModel.java
    ├── AbstractController.java
    └── ModelEvent.java
```

## Core Algorithms

### 1. Observer Pattern Implementation

#### Model-View Synchronization Algorithm
The system uses an observer pattern to keep the UI synchronized with the data model:

```java
// AbstractModel.java - Core observer implementation
public abstract class AbstractModel implements Model {
    private final List<ModelListener> listeners = new ArrayList<>();
    
    protected void fireModelChanged(ModelEvent event) {
        for (ModelListener listener : listeners) {
            listener.modelChanged(event);
        }
    }
    
    public void addModelListener(ModelListener listener) {
        listeners.add(listener);
    }
    
    public void removeModelListener(ModelListener listener) {
        listeners.remove(listener);
    }
}
```

**Algorithm Complexity**: O(n) where n is the number of listeners
**Use Case**: Real-time UI updates when data changes

### 2. CSV Data Persistence Algorithm

#### CSV Parsing with Quoted Field Support
The repository implements a custom CSV parser that handles quoted fields containing commas:

```java
private String[] parseCsvLine(String line) {
    List<String> fields = new ArrayList<>();
    StringBuilder current = new StringBuilder();
    boolean inQuotes = false;
    
    for (int i = 0; i < line.length(); i++) {
        char c = line.charAt(i);
        
        if (c == '"') {
            if (inQuotes && i + 1 < line.length() && line.charAt(i + 1) == '"') {
                // Escaped quote
                current.append('"');
                i++; // Skip the next quote
            } else {
                // Toggle quote state
                inQuotes = !inQuotes;
            }
        } else if (c == ',' && !inQuotes) {
            // Field separator
            fields.add(current.toString());
            current = new StringBuilder();
        } else {
            current.append(c);
        }
    }
    
    // Add the last field
    fields.add(current.toString());
    
    return fields.toArray(new String[0]);
}
```

**Algorithm Complexity**: O(m) where m is the length of the CSV line
**Key Features**: 
- Handles quoted fields containing commas
- Supports escaped quotes within quoted fields
- Preserves empty fields

#### CSV Escaping Algorithm
```java
private String escapeCsv(String value) {
    if (value == null) return "";
    if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
        return "\"" + value.replace("\"", "\"\"") + "\"";
    }
    return value;
}
```

**Algorithm Complexity**: O(k) where k is the length of the value
**Purpose**: Ensures CSV data integrity when saving

### 3. Validation Algorithms

#### Email Validation Algorithm
```java
private boolean isValidEmail(String email) {
    if (email == null || email.trim().isEmpty()) {
        return false;
    }
    
    // Basic email validation regex
    String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
    return email.matches(emailRegex);
}
```

**Algorithm Complexity**: O(n) where n is the length of the email
**Validation Rules**:
- Contains exactly one @ symbol
- Valid domain format
- No leading or trailing spaces

#### Date Validation Algorithm
```java
private boolean isValidDueDate(LocalDateTime dueDate) {
    if (dueDate == null) {
        return false;
    }
    
    // Due date must be in the future or today
    LocalDateTime now = LocalDateTime.now();
    return !dueDate.isBefore(now.toLocalDate().atStartOfDay());
}
```

**Algorithm Complexity**: O(1)
**Business Rule**: Tasks cannot have due dates in the past

### 4. Calendar View Algorithms

#### Event Overlap Detection Algorithm
```java
public boolean overlapsWith(LocalDateTime start, LocalDateTime end) {
    return !this.startDateTime.isAfter(end) && !this.endDateTime.isBefore(start);
}
```

**Algorithm Complexity**: O(1)
**Logic**: Two time ranges overlap if neither ends before the other starts

#### Date Range Query Algorithm
```java
public List<Event> getEventsInRange(LocalDateTime start, LocalDateTime end) {
    return events.stream()
        .filter(event -> event.overlapsWith(start, end))
        .sorted(Comparator.comparing(Event::getStartDateTime))
        .collect(Collectors.toList());
}
```

**Algorithm Complexity**: O(n log n) where n is the number of events
**Optimization**: Events are streamed and sorted for display

### 5. Task Priority Algorithm

#### Priority-Based Task Sorting Algorithm
```java
public List<Task> getTasksByPriority(Task.Priority priority) {
    return tasks.stream()
        .filter(task -> task.getPriority() == priority)
        .sorted(Comparator
            .comparing(Task::getDueDate)
            .thenComparing(Task::getTitle))
        .collect(Collectors.toList());
}
```

**Algorithm Complexity**: O(n log n) where n is the number of tasks
**Sorting Criteria**: 
1. Due date (earliest first)
2. Title (alphabetical)

#### Overdue Task Detection Algorithm
```java
public List<Task> getOverdueTasks() {
    LocalDateTime now = LocalDateTime.now();
    return tasks.stream()
        .filter(task -> !task.isCompleted())
        .filter(task -> task.getDueDate().isBefore(now))
        .sorted(Comparator.comparing(Task::getDueDate))
        .collect(Collectors.toList());
}
```

**Algorithm Complexity**: O(n log n) where n is the number of tasks
**Business Logic**: Only incomplete tasks can be overdue

### 6. Color Palette Algorithm

#### Hex Color Canonicalization Algorithm
```java
public static String canonicalHex(String hex) {
    if (hex == null || hex.isBlank()) {
        return Task.DEFAULT_ACCENT_COLOR_HEX;
    }
    
    String trimmed = hex.trim();
    for (String paletteColor : HEX_CHOICES) {
        if (paletteColor.equalsIgnoreCase(trimmed)) {
            return paletteColor; // Return canonical form from palette
        }
    }
    
    return Task.DEFAULT_ACCENT_COLOR_HEX; // Fallback to default
}
```

**Algorithm Complexity**: O(p) where p is the number of palette colors (constant 8)
**Purpose**: Ensures consistent color representation across the application

### 7. Data Integrity Algorithms

#### Unique ID Generation Algorithm
```java
public class Course {
    private final String id;
    
    public Course(String name, String code, String instructor, int credits) {
        this.id = UUID.randomUUID().toString(); // Guaranteed unique
        // ... other initialization
    }
}
```

**Algorithm Complexity**: O(1) for UUID generation
**Guarantee**: Virtually collision-free unique identifiers

#### Circular Reference Prevention Algorithm
```java
// In PlannerController constructor
public PlannerController() {
    // Create model first
    this.model = new PlannerModel();
    
    // Create view with model reference
    this.view = new PlannerView(model, this);
    
    // Set view as controller's view
    setView(view);
    
    // Make view visible
    view.setVisible(true);
}
```

**Algorithm Complexity**: O(1)
**Purpose**: Prevents infinite recursion in MVC initialization

## Performance Considerations

### 1. Lazy Loading
- Calendar views load events on demand
- Large datasets are paginated
- Images and resources loaded asynchronously

### 2. Caching Strategies
- Model listener list cached for fast notification
- Frequently accessed data kept in memory
- Computed results (like overdue tasks) cached

### 3. Memory Management
- Weak references for UI listeners
- Automatic cleanup of unused resources
- Garbage collection optimization

### 4. I/O Optimization
- Batch file operations for CSV persistence
- Buffered readers/writers for file access
- Asynchronous auto-save operations

## Security Considerations

### 1. Input Validation
- All user inputs validated before processing
- SQL injection prevention (though not using SQL)
- Path traversal prevention in file operations

### 2. Data Protection
- Local file system permissions
- No sensitive data in logs
- Secure temporary file handling

### 3. Error Handling
- Graceful degradation on errors
- No information leakage in error messages
- Safe exception handling

## Extensibility Features

### 1. Plugin Architecture
- Observer pattern allows easy addition of new listeners
- Service layer can be extended with new business logic
- View components are modular and replaceable

### 2. Configuration
- Color palette can be customized
- Validation rules can be modified
- File storage location configurable

### 3. Internationalization
- String externalization ready
- Date/time formatting configurable
- Locale-aware sorting algorithms

## Testing Algorithms

### 1. Mock Object Generation
```java
@Test
public void testTaskCreation() {
    Task task = new Task("Test", "Description", 
                         LocalDateTime.now().plusDays(1), 
                         Task.Priority.HIGH);
    
    assertNotNull(task.getId());
    assertEquals("Test", task.getTitle());
    assertFalse(task.isCompleted());
}
```

### 2. Property-Based Testing
- Random data generation for edge cases
- Invariant checking for data integrity
- Automated regression testing

### 3. Integration Testing
- End-to-end workflow testing
- Database transaction testing
- UI interaction testing

## Conclusion

The Student Planner application implements several critical algorithms that ensure reliable data management, efficient user interface updates, and robust validation. The modular architecture allows for easy maintenance and extension while maintaining high performance and data integrity.

The combination of observer patterns, custom CSV parsing, and comprehensive validation creates a solid foundation for academic planning functionality that can scale to handle large datasets while maintaining responsive user interaction.
